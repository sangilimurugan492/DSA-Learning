# Firebase Scaling

## Q1: What are Firestore scaling limits?

| Limit | Value |
|-------|-------|
| Document size | 1 MB |
| Write rate per doc | 1/sec |
| Read rate per doc | Unlimited |
| Field transforms per doc | 5 |
| Max array size | 40,000 elements |
| Max document depth | 20 levels |
| Max collection depth | 100 subcollections |
| Max query results | 1,000 (default) |
| Max `whereIn` values | 10 |
| Max `whereArrayContainsAny` values | 10 |
| Max batch size | 500 operations |
| Max concurrent connections | 1M per database |

---

## Q2: How do you handle hot documents?

A "hot document" is one that receives > 1 write/sec. Firestore limits writes to 1/sec per document.

### Problem: Counter with high traffic
```kotlin
// ❌ Bad — single counter, 1 write/sec limit
db.collection("posts").document(postId)
    .update("likeCount", FieldValue.increment(1))
// Fails if > 1 like/sec
```

### Solution: Sharded counter
```kotlin
// Split counter across N shards
const NUM_SHARDS = 10

fun incrementLike(postId: String) {
    val shardId = Random.nextInt(NUM_SHARDS)
    db.collection("posts").document(postId)
        .collection("likes_shards").document("shard_$shardId")
        .update("count", FieldValue.increment(1))
}

suspend fun getTotalLikes(postId: String): Long {
    val shards = db.collection("posts").document(postId)
        .collection("likes_shards").get().await()
    return shards.documents.sumOf { it.getLong("count") ?: 0 }
}
```

### Other hot document solutions
| Problem | Solution |
|---------|----------|
| Counter | Sharded counter |
| Queue | Distribute across N documents |
| Leaderboard | Bucket by score range |
| Real-time updates | Multiple listeners on subsets |

---

## Q3: How do you handle Cloud Function cold starts?

```javascript
// Cold start = new instance initialization (2-10s)
// Warm start = reuse existing instance (< 100ms)

// 1. Min instances (always warm)
exports.critical = functions
  .runWith({ minInstances: 1 })
  .https.onCall((data, context) => { });
// Cost: ~$10/month per instance

// 2. Module-level initialization
const admin = require('firebase-admin');
admin.initializeApp();  // Runs once per instance
const db = admin.firestore();

// 3. Keep dependencies minimal
// ❌ require('heavy-lib') inside function → slow cold start
// ✅ require('light-lib') at module level → fast cold start

// 4. Use Gen 2 functions (Cloud Run) for better concurrency
// Gen 2 handles multiple concurrent requests per instance

// 5. Right-size memory
// 128MB → slower CPU but cheaper
// 256MB+ → faster CPU, better for CPU-heavy tasks
```

### Cold start optimization
| Strategy | Cold Start | Cost |
|-----------|-----------|------|
| Default | 2-10s | $0 |
| `minInstances: 1` | 0s | ~$10/mo |
| Gen 2 + concurrency | 2-5s (shared) | Lower per-request |
| Minimal deps | 1-3s | $0 |
| Module-level init | 1-2s | $0 |

---

## Q4: How do you scale Realtime DB?

Realtime DB requires manual sharding for large datasets.

```kotlin
// Single database limit: ~200K concurrent connections
// Solution: Multiple databases

// Firebase Console → Realtime Database → Create multiple databases
// db1: users/shard1
// db2: users/shard2

// Route writes to correct shard
fun getDbForUser(userId: String): FirebaseDatabase {
    val shardIndex = userId.hashCode() % NUM_SHARDS
    return Firebase.database.getInstance("https://myapp-$shardIndex.firebaseio.com")
}
```

### Sharding strategies
| Strategy | Use Case |
|----------|---------|
| Hash by user ID | User-specific data |
| Hash by tenant ID | Multi-tenant apps |
| Geographic | Location-based data |
| Time-based | Time-series data |

---

## Q5: How do you handle connection management?

```kotlin
// Firestore: 1M concurrent connections per database
// Realtime DB: 200K concurrent connections per database

// 1. Detach listeners when not needed
override fun onPause() {
    super.onPause()
    registration?.remove()
}

override fun onResume() {
    super.onResume()
    registration = db.collection("messages")
        .addSnapshotListener { /* ... */ }
}

// 2. Use one-time reads for non-real-time data
db.collection("products").get()  // 1 connection, then closes

// 3. Batch listeners
// ❌ Bad — 5 separate listeners
db.collection("a").addSnapshotListener { }
db.collection("b").addSnapshotListener { }
db.collection("c").addSnapshotListener { }

// ✅ Good — 1 listener with compound query
db.collection("dashboard")
    .addSnapshotListener { /* all dashboard data */ }
```

---

## Q6: How do you handle rate limiting?

```javascript
// Cloud Function: rate limit writes
exports.rateLimitedWrite = functions.https.onCall(async (data, context) => {
  const uid = context.auth.uid;
  const now = Date.now();

  // Check last write time
  const userRef = admin.firestore().collection('users').doc(uid);
  const userDoc = await userRef.get();
  const lastWrite = userDoc.data()?.lastWriteTime || 0;

  if (now - lastWrite < 1000) {  // 1 sec cooldown
    throw new functions.https.HttpsError('resource-exhausted', 'Rate limit exceeded');
  }

  await userRef.update({ lastWriteTime: now });
  // Process write
});

// Firestore rules: limit writes per user
match /posts/{postId} {
  allow create: if request.auth != null &&
    get(/databases/$(database)/documents/users/$(request.auth.uid)).data.postsToday < 10;
}
```

---

## Q7: How do you scale Cloud Functions?

```javascript
// 1. Set max instances (cap cost)
exports.api = functions
  .runWith({ maxInstances: 100 })
  .https.onCall((data, context) => { });

// 2. Use concurrency (Gen 2)
exports.api = functions
  .runWith({ concurrency: 80 })  // 80 concurrent requests per instance
  .https.onCall((data, context) => { });

// 3. Use idempotent functions (safe retries)
exports.processOrder = functions.firestore
  .document('orders/{orderId}')
  .onCreate(async (snap, context) => {
    const orderId = context.params.orderId;
    // Check if already processed (idempotent)
    const existing = await admin.firestore()
      .collection('processed_orders').doc(orderId).get();
    if (existing.exists) return;

    // Process
    await processOrder(snap.data());
    await admin.firestore().collection('processed_orders').doc(orderId).set({ done: true });
  });

// 4. Use task queues for background processing
exports.processQueue = functions.tasks
  .taskQueue()
  .onDispatch(async (data, context) => {
    // Process task
  });

// 5. Use Pub/Sub for fan-out
exports.fanOut = functions.pubsub
  .topic('process-items')
  .onPublish(async (message) => {
    // Process each item
  });
```

### Scaling checklist
- [ ] Set `maxInstances` on all functions
- [ ] Use `minInstances` only for critical functions
- [ ] Make functions idempotent (safe retries)
- [ ] Use task queues for heavy processing
- [ ] Batch Firestore operations
- [ ] Right-size memory
- [ ] Monitor cold start rate
- [ ] Use Gen 2 for concurrency

---

## 🔗 Related Topics
- [Architecture](Architecture.md)
- [Cost Optimization](CostOptimization.md)
- [Cloud Functions](../intermediate/CloudFunctions.md)
