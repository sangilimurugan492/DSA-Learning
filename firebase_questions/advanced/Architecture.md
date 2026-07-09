# Firebase Architecture

## Q1: How do you model data in Firestore?

Firestore is NoSQL — data modeling is different from SQL. You model for queries, not for normalization.

### Key principles
1. **Model for your queries** — design data structure around how you'll read it
2. **Denormalize** — duplicate data for fast reads
3. **Use subcollections** — for one-to-many relationships
4. **Use references** — for many-to-many relationships
5. **Keep documents small** — < 1MB, avoid arrays that grow unbounded

### Example: Social media post
```
// ✅ Good — denormalized for fast reads
posts/{postId}
  ├── authorId: "user_123"
  ├── authorName: "Alice"        // denormalized
  ├── authorPhotoUrl: "https://..."  // denormalized
  ├── title: "My first post"
  ├── content: "Hello world"
  ├── likeCount: 42               // counter, not array
  ├── commentCount: 5             // counter
  ├── createdAt: timestamp
  └── tags: ["kotlin", "android"]

posts/{postId}/comments/{commentId}
  ├── authorId: "user_456"
  ├── authorName: "Bob"
  ├── text: "Great post!"
  └── createdAt: timestamp

// ❌ Bad — embedded array that grows unbounded
posts/{postId}
  ├── comments: [
  │     { authorId: "1", text: "..." },
  │     { authorId: "2", text: "..." },
  │     ... // 10,000 comments → document too large
  │   ]
```

---

## Q2: How do you handle fan-out (denormalization)?

Fan-out = writing the same data to multiple locations for fast reads.

```kotlin
// When a user creates a post, fan-out to:
// 1. posts/{postId} — the post itself
// 2. users/{userId}/posts/{postId} — user's post list
// 3. feeds/{followerId}/posts/{postId} — each follower's feed

val batch = db.batch()

// 1. Post document
val postRef = db.collection("posts").document(postId)
batch.set(postRef, post)

// 2. User's post list
val userPostRef = db.collection("users").document(userId)
    .collection("posts").document(postId)
batch.set(userPostRef, post)

// 3. Fan-out to followers' feeds
for (followerId in followerIds) {
    val feedRef = db.collection("feeds").document(followerId)
        .collection("posts").document(postId)
    batch.set(feedRef, post)
}

batch.commit()
```

### When to use fan-out
| Scenario | Fan-out? | Why |
|----------|---------|-----|
| Social feed | ✅ | Can't query all followers' posts efficiently |
| User profile | ❌ | Single document is enough |
| Comments | ❌ | Subcollection is enough |
| Like counts | ❌ | Use counter, not fan-out |

---

## Q3: How do you handle counters?

Firestore doesn't support atomic increments efficiently at scale. Use distributed counters.

```kotlin
// Simple counter (low traffic)
db.collection("posts").document(postId)
    .update("likeCount", FieldValue.increment(1))

// Distributed counter (high traffic)
// Split counter across N shards
fun incrementCounter(postId: String) {
    val shardId = Random.nextInt(10)  // 10 shards
    db.collection("posts").document(postId)
        .collection("likes_shards").document("shard_$shardId")
        .update("count", FieldValue.increment(1))
}

// Read total count
suspend fun getTotalLikes(postId: String): Long {
    val shards = db.collection("posts").document(postId)
        .collection("likes_shards").get().await()
    return shards.sumOf { it.getLong("count") ?: 0 }
}
```

### When to use sharded counters
| Traffic | Approach |
|---------|----------|
| < 1/sec | `FieldValue.increment()` |
| 1-10/sec | Single counter with transaction |
| > 10/sec | Sharded counter (10-100 shards) |
| > 1000/sec | Cloud Function aggregator |

---

## Q4: How do you model many-to-many relationships?

```kotlin
// Approach 1: Junction collection
// users/{userId}/groups/{groupId} → { joinedAt: timestamp }
// groups/{groupId}/members/{userId} → { role: "admin" }

// Query: Get user's groups
db.collection("users").document(userId).collection("groups").get()

// Query: Get group's members
db.collection("groups").document(groupId).collection("members").get()

// Approach 2: Array contains
// groups/{groupId} → { memberIds: ["user_1", "user_2", "user_3"] }

// Query: Get groups for a user
db.collection("groups").whereArrayContains("memberIds", userId).get()

// Limitation: max 40,000 elements in array
// Use junction collection for large sets
```

---

## Q5: How do you handle pagination?

```kotlin
// Cursor-based pagination
var lastVisible: DocumentSnapshot? = null

suspend fun loadFirstPage(): List<Post> {
    val query = db.collection("posts")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(20)
    val snapshot = query.get().await()
    lastVisible = snapshot.documents.lastOrNull()
    return snapshot.documents.map { it.toObject(Post::class.java) }
}

suspend fun loadNextPage(): List<Post> {
    if (lastVisible == null) return emptyList()
    val query = db.collection("posts")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .startAfter(lastVisible!!)
        .limit(20)
    val snapshot = query.get().await()
    lastVisible = snapshot.documents.lastOrNull()
    return snapshot.documents.map { it.toObject(Post::class.java) }
}
```

### Pagination tips
- Use `startAfter` (not `offset`) — offset reads all skipped docs
- Order by a unique field to avoid skipping items
- Handle empty results (end of list)
- Cache the last document per filter/sort combination

---

## Q6: How do you handle data aggregation?

```javascript
// Cloud Function: aggregate on write
exports.onLikeAdded = functions.firestore
  .document('posts/{postId}/likes/{likeId}')
  .onCreate(async (snap, context) => {
    const postId = context.params.postId;
    await admin.firestore().collection('posts').doc(postId)
      .update({ likeCount: admin.firestore.FieldValue.increment(1) });
  });

exports.onLikeRemoved = functions.firestore
  .document('posts/{postId}/likes/{likeId}')
  .onDelete(async (snap, context) => {
    const postId = context.params.postId;
    await admin.firestore().collection('posts').doc(postId)
      .update({ likeCount: admin.firestore.FieldValue.increment(-1) });
  });

// Scheduled aggregation (for complex stats)
exports.dailyAggregation = functions.pubsub
  .schedule('every 24 hours')
  .onRun(async (context) => {
    const posts = await admin.firestore().collection('posts').get();
    const batch = admin.firestore().batch();
    for (const post of posts.docs) {
      const likes = await post.ref.collection('likes').get();
      batch.update(post.ref, { likeCount: likes.size });
    }
    await batch.commit();
  });
```

---

## Q7: How do you choose between Firestore and Realtime DB?

| Criteria | Choose Firestore | Choose Realtime DB |
|----------|----------------|-------------------|
| Complex queries | ✅ | ❌ |
| Real-time sync | ✅ | ✅ |
| Offline support | ✅ | ✅ |
| Large datasets | ✅ (auto-scale) | ❌ (manual shard) |
| Simple key-value | ❌ (overkill) | ✅ |
| Presence detection | ❌ | ✅ (onDisconnect) |
| Price (high reads) | ❌ (expensive) | ✅ (flat rate) |
| Price (high writes) | ✅ | ❌ (bandwidth) |

### Hybrid approach
Use both:
- **Firestore** for structured data (users, posts, orders)
- **Realtime DB** for presence (online status) and simple real-time features (typing indicators)

---

## 🔗 Related Topics
- [Firestore Basics](../beginner/FirestoreBasics.md)
- [Performance](Performance.md)
- [Cost Optimization](CostOptimization.md)
