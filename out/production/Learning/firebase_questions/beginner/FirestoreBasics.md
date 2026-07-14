# Firestore Basics

## Q1: What is Cloud Firestore?

Firestore is a NoSQL, document-oriented database. Data is stored in **documents**, which are organized into **collections**.

```
Collection (users)
  └── Document (user_123)
        ├── name: "Alice"
        ├── email: "alice@example.com"
        ├── age: 30
        └── Subcollection (orders)
              └── Document (order_456)
                    ├── total: $50
                    └── status: "shipped"
```

### Key characteristics
- **NoSQL** — no schema, flexible document structure
- **Real-time** — listeners receive updates instantly
- **Offline** — local persistence works offline and syncs when online
- **Scalable** — automatically scales with usage
- **Hierarchical** — collections → documents → subcollections

---

## Q2: How do you perform CRUD operations in Firestore?

```kotlin
val db = Firebase.firestore

// CREATE — add a document with auto-ID
val user = hashMapOf("name" to "Alice", "email" to "alice@example.com")
db.collection("users").add(user)

// CREATE — add a document with specific ID
db.collection("users").document("user_123").set(user)

// READ — get a single document
db.collection("users").document("user_123").get()
    .addOnSuccessListener { doc ->
        if (doc.exists()) {
            val name = doc.getString("name")
        }
    }

// READ — get multiple documents
db.collection("users").whereEqualTo("name", "Alice").get()
    .addOnSuccessListener { querySnapshot ->
        for (doc in querySnapshot) {
            println("${doc.id} => ${doc.data}")
        }
    }

// UPDATE — update specific fields
db.collection("users").document("user_123")
    .update("name" to "Bob", "age" to 25)

// UPDATE — set with merge (creates if doesn't exist)
db.collection("users").document("user_123")
    .set(mapOf("name" to "Bob"), SetOptions.merge())

// DELETE — delete a document
db.collection("users").document("user_123").delete()

// DELETE — delete a field
db.collection("users").document("user_123")
    .update("age", FieldValue.delete())
```

---

## Q3: What are the different query types in Firestore?

```kotlin
// Where clauses
db.collection("users").whereEqualTo("status", "active")
db.collection("users").whereGreaterThan("age", 18)
db.collection("users").whereLessThan("age", 65)
db.collection("users").whereArrayContains("tags", "kotlin")
db.collection("users").whereIn("city", listOf("NYC", "SF", "LA"))
db.collection("users").whereArrayContainsAny("tags", listOf("kotlin", "java"))

// Ordering and limiting
db.collection("users").orderBy("name").limit(10)
db.collection("users").orderBy("age", Query.Direction.DESCENDING).limit(5)

// Pagination
db.collection("users").orderBy("name").startAfter(lastVisibleDoc).limit(10)

// Range queries
db.collection("users")
    .whereGreaterThanOrEqualTo("age", 18)
    .whereLessThanOrEqualTo("age", 65)

// Compound queries
db.collection("users")
    .whereEqualTo("status", "active")
    .whereGreaterThan("age", 18)
    .orderBy("age")
    .limit(10)
```

### Query limitations
| Limitation | Description |
|-----------|-------------|
| No `!=` (use `not-in` or client-side filter) | Inequality not supported on all fields |
| Range filter on one field only | Can't range filter on 2 fields |
| `OR` not supported natively | Use `whereIn` or multiple queries |
| Max 10 `whereIn` values | Use batch reads for more |
| Requires composite index | For multi-field queries |

---

## Q4: How do you listen to real-time updates?

```kotlin
// Listen to a single document
db.collection("users").document("user_123")
    .addSnapshotListener { snapshot, error ->
        if (error != null) return@addSnapshotListener
        if (snapshot != null && snapshot.exists()) {
            val name = snapshot.getString("name")
        }
    }

// Listen to a query
db.collection("users").whereEqualTo("status", "active")
    .addSnapshotListener { value, error ->
        if (error != null) return@addSnapshotListener
        for (dc in value!!.documentChanges) {
            when (dc.type) {
                DocumentChange.Type.ADDED -> println("New: ${dc.document.data}")
                DocumentChange.Type.MODIFIED -> println("Modified: ${dc.document.data}")
                DocumentChange.Type.REMOVED -> println("Removed: ${dc.document.data}")
            }
        }
    }

// Listen with metadata changes
db.collection("users").document("user_123")
    .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
        // Includes metadata-only changes (e.g., pending writes)
        val isFromCache = snapshot?.metadata?.isFromCache
    }

// Detach listener (important for lifecycle management)
val registration = db.collection("users").document("user_123")
    .addSnapshotListener { /* ... */ }
registration.remove()  // Stop listening
```

### Listener best practices
- Always `remove()` listeners when the screen is destroyed
- Use `LifecycleOwner` to auto-detach
- Handle errors in the listener callback
- Use `DocumentChange` for granular UI updates

---

## Q5: What is the difference between `set()` and `update()`?

```kotlin
// set() — overwrites the entire document
db.collection("users").document("123").set(
    mapOf("name" to "Alice")  // All other fields are DELETED
)

// set() with merge — only updates specified fields
db.collection("users").document("123").set(
    mapOf("name" to "Alice"), SetOptions.merge()  // Only "name" is updated
)

// update() — only updates specified fields (fails if doc doesn't exist)
db.collection("users").document("123").update(
    mapOf("name" to "Alice")  // Only "name" is updated, other fields preserved
)
```

| Operation | Overwrites? | Creates if missing? | Fails if missing? |
|-----------|------------|-------------------|------------------|
| `set(data)` | ✅ Entire doc | ✅ | ❌ |
| `set(data, merge())` | ❌ Only specified | ✅ | ❌ |
| `update(fields)` | ❌ Only specified | ❌ | ✅ |

---

## Q6: What are batched writes and transactions?

```kotlin
// Batch — multiple writes atomically (no read needed)
val batch = db.batch()
batch.set(db.collection("users").document("123"), mapOf("name" to "Alice"))
batch.set(db.collection("users").document("456"), mapOf("name" to "Bob"))
batch.delete(db.collection("users").document("789"))
batch.commit().addOnSuccessListener { /* all succeeded */ }

// Transaction — read + write atomically (need to read before write)
db.runTransaction { transaction ->
    val sfDoc = transaction.get(db.collection("cities").document("SF"))
    val newPop = sfDoc.getDouble("population")!! + 1
    transaction.update(db.collection("cities").document("SF"), "population", newPop)
    null  // return value
}

// Transaction with return value
db.runTransaction { transaction ->
    val doc = transaction.get(db.collection("counters").document("views"))
    val count = doc.getLong("count") ?: 0
    transaction.update(db.collection("counters").document("views"), "count", count + 1)
    count + 1  // return new count
}.addOnSuccessListener { newCount -> println("Views: $newCount") }
```

### Batch vs Transaction
| Feature | Batch | Transaction |
|---------|-------|------------|
| Read before write | ❌ | ✅ |
| Atomic | ✅ | ✅ |
| Max operations | 500 | 500 |
| Use case | Multi-write without read | Read-modify-write |
| Retries | ❌ | ✅ (automatic) |

---

## Q7: How does offline persistence work?

```kotlin
// Enable offline persistence (enabled by default on mobile)
val settings = firestoreSettings {
    setPersistenceEnabled(true)
    setCacheSizeBytes(FirestoreSettings.CACHE_SIZE_UNLIMITED)
}
db.firestoreSettings = settings

// Offline writes are queued and synced when online
db.collection("users").document("123").set(mapOf("name" to "Alice"))
// If offline → write is queued → synced when connection returns

// Check if data is from cache
db.collection("users").document("123").get(Source.CACHE)
    .addOnSuccessListener { doc ->
        // Only reads from cache, never server
    }

// Listen to server vs cache
db.collection("users").document("123")
    .addSnapshotListener { snapshot, _ ->
        val isFromCache = snapshot?.metadata?.isFromCache ?: false
        if (isFromCache) {
            // Data from local cache
        } else {
            // Data from server
        }
    }
```

### Offline behavior
- **Reads** — served from cache if available, then synced with server
- **Writes** — queued locally, sent to server when online
- **Listeners** — receive cache updates immediately, then server updates
- **Conflicts** — last write wins (no automatic merge)

---

## 🔗 Related Topics
- [Authentication](Authentication.md)
- [Security Rules](../intermediate/SecurityRules.md)
- [Performance](../advanced/Performance.md)
