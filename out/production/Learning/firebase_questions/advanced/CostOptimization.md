# Firebase Cost Optimization

## Q1: How does Firestore pricing work?

### Pricing model ( Blaze plan )
| Operation | Cost |
|-----------|------|
| Document read | $0.036 / 100K |
| Document write | $0.108 / 100K |
| Document delete | $0.012 / 100K |
| Storage | $0.108 / GB / month |
| Network egress | $0.12 / GB |

### Free tier ( Spark plan )
- 50K reads/day
- 20K writes/day
- 20K deletes/day
- 1 GB storage

### Cost calculation example
```
10K users × 10 reads/session × 5 sessions/day = 500K reads/day
500K × 30 days = 15M reads/month
15M / 100K × $0.036 = $5.40/month (reads only)
```

---

## Q2: How do you reduce read costs?

```kotlin
// 1. Denormalize — avoid N+1 reads
// ❌ 1 query + 10 author reads = 11 reads
// ✅ Denormalize author name → 1 read

// 2. Use batch reads
val refs = userIds.map { db.collection("users").document(it) }
db.getAll(refs).await()  // 1 read for up to 10 docs

// 3. Use cache
db.collection("users").document(userId).get(Source.CACHE)
// 0 server reads if cached

// 4. Limit query results
db.collection("posts").limit(20).get()  // max 20 reads

// 5. Use count() instead of get() (1 read instead of N)
db.collection("posts").whereEqualTo("authorId", userId).count().get()

// 6. Use aggregation queries (sum, average, count)
db.collection("orders").whereEqualTo("status", "completed")
    .aggregate(AggregateField.sum("total")).get()
// 1 read instead of N reads + client-side sum
```

### Read cost reduction strategies
| Strategy | Savings |
|----------|---------|
| Denormalize | 80-90% |
| Batch reads | 90% |
| Cache | 50-80% |
| `limit()` | Proportional |
| `count()` | 99%+ |
| Aggregation | 99%+ |

---

## Q3: How do you reduce write costs?

```kotlin
// 1. Batch writes (1 operation for up to 500 writes)
val batch = db.batch()
items.forEach { batch.set(db.collection("items").document(it.id), it) }
batch.commit()  // 1 billed operation

// 2. Use set with merge (avoid read-before-write)
db.collection("users").document(userId)
    .set(mapOf("lastSeen" to timestamp), SetOptions.merge())
// 1 write instead of 1 read + 1 write

// 3. Avoid unnecessary updates
// ❌ Bad — writes even if nothing changed
db.collection("users").document(userId).set(user)
// ✅ Good — only write if changed
if (user != cachedUser) {
    db.collection("users").document(userId).set(user)
}

// 4. Use Cloud Functions for server-side aggregation
// Instead of client writing to multiple docs, use a function
```

---

## Q4: How do you reduce Cloud Functions costs?

```javascript
// 1. Right-size memory (lower = cheaper)
// 128MB for simple functions, 256MB+ for processing

// 2. Set min instances only for critical functions
exports.critical = functions
  .runWith({ minInstances: 1 })  // ~$10/month per instance
  .https.onCall((data, context) => { });

// 3. Use max instances to cap costs
exports.api = functions
  .runWith({ maxInstances: 10 })
  .https.onCall((data, context) => { });

// 4. Avoid unnecessary triggers
// ❌ Bad — triggers on every write
exports.onEveryWrite = functions.firestore
  .document('logs/{logId}').onWrite(async (change, context) => { });

// ✅ Good — trigger only on create
exports.onCreate = functions.firestore
  .document('logs/{logId}').onCreate(async (snap, context) => { });

// 5. Keep functions fast (billed per 100ms)
// 6. Use concurrency (Gen 2 functions)
```

### Function cost comparison
| Config | Monthly Cost |
|--------|-------------|
| 128MB, 1s, 1M invocations | ~$18 |
| 256MB, 1s, 1M invocations | ~$36 |
| 1GB, 1s, 1M invocations | ~$145 |
| 128MB, 1s, 1M invocations, min 1 instance | ~$48 |

---

## Q5: How do you reduce Storage costs?

```kotlin
// 1. Compress images before upload
val compressed = compressImage(original, quality = 70, maxWidth = 1080)
storageRef.putFile(compressed)

// 2. Use lifecycle rules to delete old files
// Firebase Console → Storage → Lifecycle
// Delete files older than 90 days
// Move to coldline storage after 30 days

// 3. Use thumbnails instead of full images
// Generate thumbnail in Cloud Function
// Store thumbnail in /thumbnails/ path

// 4. Clean up orphaned files
// Cloud Function: on document delete → delete associated file
exports.onPostDeleted = functions.firestore
  .document('posts/{postId}').onDelete(async (snap, context) => {
    const postId = context.params.postId;
    await admin.storage().bucket().deleteFiles({ prefix: `posts/${postId}/` });
  });
```

---

## Q6: How do you monitor and set budgets?

### Firebase Console → Usage and Billing
- Monitor reads, writes, deletes, storage, function invocations
- Set per-day alerts
- Set monthly budget caps

### Key metrics to track
| Metric | Alert Threshold |
|--------|----------------|
| Reads/day | > 100K |
| Writes/day | > 50K |
| Function invocations/day | > 10K |
| Storage | > 5GB |
| Egress | > 1GB/day |

### Cost monitoring script
```javascript
// Cloud Function: daily cost report
exports.dailyCostReport = functions.pubsub
  .schedule('0 9 * * *')
  .onRun(async (context) => {
    const yesterday = new Date(Date.now() - 24 * 60 * 60 * 1000);
    const stats = await admin.firestore().collection('usage_stats')
      .where('date', '>=', yesterday).get();

    let totalReads = 0, totalWrites = 0;
    stats.docs.forEach(doc => {
      totalReads += doc.data().reads || 0;
      totalWrites += doc.data().writes || 0;
    });

    const estimatedCost = (totalReads * 0.036 + totalWrites * 0.108) / 100000;

    await sendSlackAlert(`Daily Firebase cost: $${estimatedCost.toFixed(2)}`);
  });
```

---

## Q7: What are common cost traps?

| Trap | Impact | Solution |
|------|--------|----------|
| Real-time listeners on large collections | High reads | Filter + limit |
| N+1 query patterns | N reads per query | Denormalize |
| Writing on every app open | High writes | Batch + throttle |
| Storing large files uncompressed | High storage | Compress first |
| Cloud Functions with high memory | High cost | Right-size memory |
| No `maxInstances` | Unlimited cost | Set cap |
| Listening to all documents | High reads | Use `where` + `limit` |
| Not using cache | Unnecessary reads | Enable persistence |
| `offset` pagination | Reads skipped docs | Use `startAfter` |
| Counting docs with `get().size` | N reads | Use `count()` |

### Monthly cost checklist
- [ ] Review reads/writes in console
- [ ] Check for N+1 patterns
- [ ] Verify listeners have filters + limits
- [ ] Review function memory settings
- [ ] Check storage for orphaned files
- [ ] Set budget alerts
- [ ] Review free tier usage

---

## 🔗 Related Topics
- [Performance](Performance.md)
- [Architecture](Architecture.md)
- [Scaling](Scaling.md)
