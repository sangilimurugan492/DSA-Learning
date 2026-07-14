# Realtime Database

## Q1: What is Firebase Realtime Database?

A NoSQL cloud database that syncs data in real-time across all connected devices.

```json
{
  "users": {
    "user_123": {
      "name": "Alice",
      "email": "alice@example.com",
      "age": 30
    }
  },
  "messages": {
    "msg_456": {
      "text": "Hello",
      "sender": "user_123",
      "timestamp": 1690000000
    }
  }
}
```

### Firestore vs Realtime Database
| Feature | Firestore | Realtime DB |
|---------|-----------|-------------|
| Data model | Documents & Collections | JSON Tree |
| Real-time | ✅ | ✅ |
| Offline | ✅ | ✅ |
| Query | Complex, indexed | Limited, deep filtering |
| Scaling | Automatic | Manual sharding |
| Max write | 1 MB | 256 MB (single write) |
| Price | Per read/write | Per data stored + bandwidth |

---

## Q2: How do you perform CRUD in Realtime DB?

```kotlin
val db = Firebase.database.reference

// CREATE — set value (overwrites)
db.child("users").child("123").setValue(mapOf("name" to "Alice", "age" to 30))

// CREATE — push (auto-generated ID)
val key = db.child("messages").push().key
db.child("messages").child(key!!).setValue(mapOf("text" to "Hello"))

// READ — one-time
db.child("users").child("123").get().addOnSuccessListener { snapshot ->
    val name = snapshot.child("name").value
}

// READ — real-time listener
db.child("users").child("123").addValueEventListener(object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        val name = snapshot.child("name").value
    }
    override fun onCancelled(error: DatabaseError) { }
})

// UPDATE — specific fields
db.child("users").child("123").updateChildren(mapOf("name" to "Bob", "age" to 25))

// DELETE
db.child("users").child("123").removeValue()
```

---

## Q3: How do you query Realtime DB?

```kotlin
// Order by child
db.child("users").orderByChild("age").limitToFirst(10)

// Filter
db.child("users").orderByChild("age").equalTo(30)
db.child("users").orderByChild("age").startAt(18).endAt(65)
db.child("users").orderByChild("name").limitToLast(5)

// Order by key
db.child("users").orderByKey().limitToFirst(10)

// Order by value
db.child("scores").orderByValue().limitToLast(10)

// Pagination
db.child("users").orderByKey().startAfter(lastKey).limitToFirst(10)
```

### Query limitations
- Can only order by ONE field
- No `OR` queries
- No `!=` operator
- Deep filtering is limited
- No compound queries

---

## Q4: How do you handle real-time listeners?

```kotlin
// ValueEventListener — gets entire data at path
db.child("users").child("123").addValueEventListener(object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) { }
    override fun onCancelled(error: DatabaseError) { }
})

// ChildEventListener — gets granular changes
db.child("messages").addChildEventListener(object : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildKey: String?) { }
    override fun onChildChanged(snapshot: DataSnapshot, previousChildKey: String?) { }
    override fun onChildRemoved(snapshot: DataSnapshot) { }
    override fun onChildMoved(snapshot: DataSnapshot, previousChildKey: String?) { }
    override fun onCancelled(error: DatabaseError) { }
})

// Remove listener (important!)
db.child("users").child("123").removeEventListener(valueEventListener)

// One-time read
db.child("users").child("123").addListenerForSingleValueEvent(object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) { }
    override fun onCancelled(error: DatabaseError) { }
})
```

---

## Q5: How do you do atomic writes?

```kotlin
// Multi-path update — atomic
val updates = mapOf(
    "users/123/name" to "Alice",
    "users/123/lastSeen" to ServerValue.TIMESTAMP,
    "userChats/123/chat_456" to true,
    "chats/456/lastMessage" to "Hello"
)
db.updateChildren(updates)  // All succeed or all fail

// Transaction — for counters
db.child("posts/123/likes").runTransaction(object : Transaction.Handler {
    override fun doTransaction(currentData: MutableData): Transaction.Result {
        val likes = currentData.getValue(Int::class.java) ?: 0
        currentData.value = likes + 1
        return Transaction.success(currentData)
    }
    override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) { }
})
```

---

## Q6: How does offline persistence work?

```kotlin
// Enable offline persistence
Firebase.database.setPersistenceEnabled(true)

// Keep specific data synced (disk persistence)
db.child("users").child("123").keepSynced(true)

// Disable disk persistence for a path
db.child("users").child("123").keepSynced(false)

// Server timestamps
db.child("messages").child("123").child("timestamp").setValue(ServerValue.TIMESTAMP)
```

---

## Q7: How do you structure data for Realtime DB?

```json
// ❌ Bad — nested, hard to query
{
  "chats": {
    "chat_1": {
      "messages": {
        "msg_1": { "text": "Hi", "sender": "user_1" },
        "msg_2": { "text": "Hello", "sender": "user_2" }
      }
    }
  }
}

// ✅ Good — flat, queryable
{
  "chats": {
    "chat_1": { "lastMessage": "Hello", "lastSender": "user_2" }
  },
  "messages": {
    "chat_1": {
      "msg_1": { "text": "Hi", "sender": "user_1" },
      "msg_2": { "text": "Hello", "sender": "user_2" }
    }
  }
}
```

### Best practices
- **Flatten data** — avoid nesting > 3 levels
- **Denormalize** — duplicate data for fast reads
- **Index fields** — add `.indexOn` in rules
- **Use push keys** — for ordered lists
- **Separate lists** — don't embed collections in documents

---

## 🔗 Related Topics
- [Firestore Basics](FirestoreBasics.md)
- [Security Rules](../intermediate/SecurityRules.md)
- [Architecture](../advanced/Architecture.md)
