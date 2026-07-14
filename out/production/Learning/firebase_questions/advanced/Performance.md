# Firebase Performance

## Q1: How do you optimize Firestore reads?

Every document read costs money and latency. Minimize reads.

### Strategies

```kotlin
// ❌ Bad — N+1 reads (1 query + N reads)
val posts = db.collection("posts").get().await()
for (post in posts) {
    val author = db.collection("users").document(post.getString("authorId")).get().await()
    // 1 + N reads
}

// ✅ Good — Denormalize author name into post
// posts/{postId} → { authorName: "Alice", authorPhotoUrl: "..." }
// 1 read for all posts

// ✅ Good — Batch read
val authorIds = posts.map { it.getString("authorId") }.distinct()
val userDocs = authorIds.chunked(10).map { chunk ->
    db.collection("users").whereIn("uid", chunk).get().await()
}
// 1 + (N/10) reads

// ✅ Good — Use `__name__` for batch reads by ID
val refs = postIds.map { db.collection("posts").document(it) }
db.getAll(refs).await()  // 1 read for up to 10 docs
```

### Read optimization checklist
- [ ] Denormalize frequently-accessed related data
- [ ] Use `whereIn` for batch reads (max 10)
- [ ] Use `getAll()` for batch reads by ID
- [ ] Cache results locally (offline persistence)
- [ ] Use `limit()` to reduce result size
- [ ] Use cursor pagination instead of offset

---

## Q2: How do you optimize Firestore writes?

```kotlin
// ❌ Bad — individual writes
for (item in items) {
    db.collection("items").document(item.id).set(item)  // N writes
}

// ✅ Good — batch write
val batch = db.batch()
for (item in items) {
    batch.set(db.collection("items").document(item.id), item)
}
batch.commit()  // 1 write (up to 500 operations)

// ✅ Good — use set with merge for partial updates
db.collection("users").document(userId)
    .set(mapOf("lastSeen" to timestamp), SetOptions.merge())
// 1 write instead of read + update
```

---

## Q3: How do you use indexes effectively?

```kotlin
// Single-field indexes — automatic
db.collection("posts").whereEqualTo("status", "published")
db.collection("posts").whereGreaterThan("createdAt", timestamp)

// Composite indexes — manual in Firebase Console
db.collection("posts")
    .whereEqualTo("status", "published")
    .whereGreaterThan("createdAt", timestamp)
    .orderBy("createdAt")
// Requires composite index on (status, createdAt)

// Check if index is needed — Firestore returns an error with a link to create it
```

### Index best practices
| Index Type | Created | Cost |
|-----------|---------|------|
| Single-field | Automatic | Free |
| Composite | Manual | Billed |
| Array-contains | Automatic | Free |

### When NOT to create indexes
- One-time queries
- Queries that scan < 100 docs
- Queries that can be split into 2 simpler queries

---

## Q4: How do you handle offline persistence efficiently?

```kotlin
// Configure cache size
val settings = firestoreSettings {
    setPersistenceEnabled(true)
    setCacheSizeBytes(50L * 1024 * 1024)  // 50MB
}
db.firestoreSettings = settings

// Use Source.CACHE for cache-only reads
db.collection("posts").document(postId)
    .get(Source.CACHE)
    .addOnSuccessListener { doc ->
        // Only from cache — no server read
    }

// Use Source.SERVER for server-only reads
db.collection("posts").document(postId)
    .get(Source.SERVER)

// Default: try cache, then server
db.collection("posts").document(postId).get()
```

### Cache strategy
| Data Type | Source | Why |
|-----------|--------|-----|
| User profile | CACHE | Rarely changes |
| Product list | SERVER | Changes frequently |
| Real-time data | LISTENER | Always fresh |
| Settings | CACHE | Rarely changes |
| Search results | SERVER | Dynamic |

---

## Q5: How do you optimize Cloud Functions performance?

```javascript
// ✅ Initialize at module level (runs once per instance)
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();

// ✅ Use connection pooling
const axios = require('axios');
const httpClient = axios.create({ keepAlive: true });

// ✅ Set appropriate memory and timeout
exports.processData = functions
  .runWith({ memory: '1GB', timeoutSeconds: 60 })
  .https.onCall(async (data, context) => {
    // Process data
  });

// ✅ Use min instances for latency-sensitive functions
exports.criticalFunction = functions
  .runWith({ minInstances: 1 })
  .https.onCall((data, context) => { });

// ✅ Batch operations
const batch = db.batch();
for (const item of items) {
  batch.set(db.collection('items').doc(item.id), item);
}
await batch.commit();  // 1 write instead of N
```

### Performance tips
| Tip | Impact |
|-----|--------|
| Module-level init | Eliminates cold start overhead |
| `minInstances` | Eliminates cold starts |
| Right memory size | More memory = faster CPU |
| Batch writes | Fewer operations |
| Connection pooling | Faster HTTP calls |
| Keep deps minimal | Faster cold starts |

---

## Q6: How do you monitor Firebase performance?

```kotlin
// Add Performance Monitoring (automatic for HTTP requests)
// Manual traces for custom operations
val trace = Firebase.performance.newTrace("load_feed")
trace.start()
// ... load feed data ...
trace.stop()

// Add metrics to trace
trace.putMetric("items_loaded", itemCount)
trace.putAttribute("source", "cache")

// Custom screen tracing
val screenTrace = Firebase.performance.newTrace("product_detail_screen")
screenTrace.start()
// ... screen loads ...
screenTrace.stop()
```

### Key metrics to monitor
| Metric | Target |
|--------|--------|
| Firestore read latency | < 200ms |
| Cloud Function cold start | < 3s |
| Cloud Function execution | < 1s |
| FCM delivery rate | > 95% |
| App start time | < 2s |

---

## Q7: How do you reduce real-time listener costs?

```kotlin
// ❌ Bad — listening to entire collection
db.collection("messages").addSnapshotListener { /* all messages */ }

// ✅ Good — listen with filter + limit
db.collection("messages")
    .whereEqualTo("chatId", chatId)
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .limit(50)
    .addSnapshotListener { /* last 50 messages */ }

// ✅ Good — detach when not needed
val registration = db.collection("messages")
    .addSnapshotListener { /* ... */ }
// When screen is destroyed:
registration.remove()

// ✅ Good — use one-time reads for non-real-time data
db.collection("products").document(productId).get()
// Instead of addSnapshotListener
```

### Listener cost optimization
| Strategy | Savings |
|----------|---------|
| Filter + limit | 80-95% fewer reads |
| Detach on background | 50% fewer reads |
| One-time reads | 100% fewer reads |
| Cache + listener | 60% fewer reads |

---

## 🔗 Related Topics
- [Architecture](Architecture.md)
- [Cost Optimization](CostOptimization.md)
- [Firestore Basics](../beginner/FirestoreBasics.md)
