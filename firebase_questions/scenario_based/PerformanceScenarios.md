# Firebase Performance Scenarios

## Scenario 1: Slow Query Optimization

### Problem
A query to fetch user's orders is taking 3+ seconds. The query:
```kotlin
db.collection("orders")
    .whereEqualTo("userId", userId)
    .whereGreaterThanOrEqualTo("createdAt", monthStart)
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .get()
```

### Diagnosis
1. **Missing composite index** — Firestore returns an error with a link to create it
2. **Large result set** — returning all orders for the month without limit
3. **N+1 reads** — each order fetches product details separately

### Solution

```kotlin
// 1. Create composite index (Firestore Console)
// Index: userId (ASC) + createdAt (DESC)

// 2. Add limit + pagination
db.collection("orders")
    .whereEqualTo("userId", userId)
    .whereGreaterThanOrEqualTo("createdAt", monthStart)
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .limit(20)
    .get()

// 3. Denormalize product info into order items
// orders/{orderId} → items: [{ productId, productName, price, ... }]
// Avoids N+1 reads for product details

// 4. Use cursor pagination
db.collection("orders")
    .whereEqualTo("userId", userId)
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .startAfter(lastVisibleDoc)
    .limit(20)
    .get()
```

### Result
- Query time: 3s → 200ms
- Reads: 100+ → 20 per page
- No N+1 reads (denormalized product info)

---

## Scenario 2: High Read Costs

### Problem
Firebase bill shows $500/month for reads. Investigation reveals:
- 2M reads/day from a real-time listener on `posts` collection
- Listener has no filter — receives ALL posts
- 10K active users × 200 reads per update = 2M reads

### Solution

```kotlin
// ❌ Before — listening to ALL posts
db.collection("posts").addSnapshotListener { /* ... */ }

// ✅ After — filter + limit
db.collection("posts")
    .whereEqualTo("status", "published")
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .limit(20)
    .addSnapshotListener { /* ... */ }

// ✅ Use one-time reads for non-real-time data
db.collection("posts")
    .whereEqualTo("status", "published")
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .limit(20)
    .get()  // 1 read, no listener

// ✅ Cache results locally
db.collection("posts").document(postId).get(Source.CACHE)
```

### Cost reduction
| Strategy | Reads/day | Monthly Cost |
|----------|-----------|-------------|
| Before (no filter) | 2M | $500 |
| After (filter + limit 20) | 200K | $50 |
| After + cache | 50K | $12.50 |
| After + one-time reads | 20K | $5 |

---

## Scenario 3: Cloud Function Cold Starts

### Problem
A callable Cloud Function takes 5-8 seconds on first call (cold start). Users see a loading spinner for too long.

### Solution

```javascript
// 1. Module-level initialization
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();

// 2. Min instances for critical function
exports.getUserProfile = functions
  .runWith({ minInstances: 1, memory: '256MB' })
  .https.onCall(async (data, context) => {
    return await db.collection('users').doc(context.auth.uid).get();
  });

// 3. Keep dependencies minimal
// ❌ Remove unused heavy dependencies
// ✅ Use lightweight alternatives

// 4. Use Gen 2 for concurrency
exports.processData = functions
  .runWith({ concurrency: 80 })
  .https.onCall((data, context) => { });
```

### Result
| Metric | Before | After |
|--------|--------|-------|
| Cold start | 5-8s | 0s (min instances) |
| Warm start | 200ms | 150ms |
| Monthly cost | $0 | +$10 (min instance) |

---

## Scenario 4: Real-Time Listener Optimization

### Problem
A chat app has 500 concurrent users. Each user has 3 active listeners (chats list, messages, typing). That's 1,500 listeners. Reads are 500K/day.

### Solution

```kotlin
// 1. Detach listeners on background
override fun onPause() {
    super.onPause()
    chatsListener?.remove()
    messagesListener?.remove()
}

override fun onResume() {
    super.onResume()
    // Re-attach
}

// 2. Use one listener for chat list
db.collection("userChats").document(userId).collection("chats")
    .orderBy("lastMessageTime", Query.Direction.DESCENDING)
    .limit(20)
    .addSnapshotListener { /* ... */ }

// 3. Only listen to active chat's messages
// Don't listen to ALL chats' messages — only the open one
db.collection("chats").document(activeChatId).collection("messages")
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .limit(50)
    .addSnapshotListener { /* ... */ }

// 4. Use Realtime DB for typing indicators (ephemeral)
// Don't store typing in Firestore — it's temporary
val typingRef = Firebase.database.reference
    .child("typing").child(chatId).child(userId)
typingRef.setValue(true)
typingRef.onDisconnect().removeValue()
```

### Result
- Listeners per user: 3 → 2 (detach on background)
- Reads: 500K/day → 100K/day
- Monthly cost: $150 → $30

---

## Scenario 5: Large Collection Pagination

### Problem
Need to paginate through 10,000+ documents. Using `offset()` is expensive — Firestore reads all skipped documents.

### Solution

```kotlin
// ❌ Bad — offset reads all skipped docs
db.collection("products")
    .orderBy("name")
    .offset(9000)  // Reads 9000 docs!
    .limit(100)
    .get()

// ✅ Good — cursor-based pagination
var lastVisible: DocumentSnapshot? = null

suspend fun loadPage(): List<Product> {
    var query = db.collection("products")
        .orderBy("name")
        .limit(100)

    if (lastVisible != null) {
        query = query.startAfter(lastVisible!!)
    }

    val snapshot = query.get().await()
    lastVisible = snapshot.documents.lastOrNull()
    return snapshot.documents.map { it.toObject(Product::class.java) }
}

// ✅ For deep pagination — use a different strategy
// Store a "page" field and query by page number
db.collection("products")
    .whereEqualTo("page", 90)
    .get()
```

### Cost comparison
| Approach | Reads for page 90 |
|----------|-------------------|
| `offset(9000)` | 9,100 |
| `startAfter` | 100 |
| Page field | 100 |

---

## Scenario 6: Image Upload Performance

### Problem
Users upload 5MB+ images. Upload takes 10+ seconds and sometimes fails on slow networks.

### Solution

```kotlin
// 1. Compress before upload
val compressed = compressImage(originalUri, quality = 70, maxWidth = 1080)
// 5MB → 500KB

// 2. Use resumable upload (automatic with putFile)
val uploadTask = storageRef.putFile(compressed)

// 3. Show progress
uploadTask.addOnProgressListener { task ->
    val percent = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
    updateProgressBar(percent)
}

// 4. Generate thumbnail in Cloud Function
// (Don't make client wait for thumbnail generation)

// 5. Use chunked upload for very large files
// (Storage SDK handles this automatically)

// 6. Upload to a temp path, then move
val tempRef = storageRef.child("temp/${UUID.randomUUID()}.jpg")
tempRef.putFile(compressed).continueWithTask { task ->
    if (!task.isSuccessful) task.exception?.let { throw it }
    // Move to permanent location
    val permRef = storageRef.child("images/${userId}/profile.jpg")
    tempRef.metadata.flatMap { metadata ->
        // Copy then delete temp
        permRef.putFile(tempRef).continueWithTask {
            tempRef.delete()
            permRef.downloadUrl
        }
    }
}
```

### Result
| Metric | Before | After |
|--------|--------|-------|
| Image size | 5MB | 500KB |
| Upload time | 10s | 1.5s |
| Failure rate | 15% | 2% |
| Bandwidth | 5MB | 500KB |

---

## 🔗 Related Topics
- [Performance](../advanced/Performance.md)
- [Cost Optimization](../advanced/CostOptimization.md)
- [Architecture](../advanced/Architecture.md)
