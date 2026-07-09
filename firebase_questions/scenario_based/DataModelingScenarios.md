# Firebase Data Modeling Scenarios

## Scenario 1: Chat App

### Problem
Design a Firestore data model for a chat app with:
- Multiple chat rooms
- Real-time messages
- Read receipts
- Typing indicators
- Last message preview

### Solution

```
chats/{chatId}
  ├── type: "direct" | "group"
  ├── lastMessage: "Hello!"
  ├── lastMessageSender: "Alice"
  ├── lastMessageTime: timestamp
  ├── participantIds: ["user_1", "user_2"]
  └── participants: {
        "user_1": { name: "Alice", photoUrl: "..." },
        "user_2": { name: "Bob", photoUrl: "..." }
      }

chats/{chatId}/messages/{messageId}
  ├── senderId: "user_1"
  ├── text: "Hello!"
  ├── timestamp: timestamp
  └── readBy: ["user_1", "user_2"]

userChats/{userId}/chats/{chatId}
  ├── lastMessage: "Hello!"
  ├── lastMessageTime: timestamp
  └── unreadCount: 3
```

### Key decisions
- **Denormalize** last message into chat doc (avoid querying messages for preview)
- **Subcollection** for messages (unbounded growth)
- **userChats** collection for fast "my chats" query
- **Realtime DB** for typing indicators (ephemeral, doesn't need persistence)

```kotlin
// Query: Get user's chats ordered by last message
db.collection("userChats").document(userId).collection("chats")
    .orderBy("lastMessageTime", Query.Direction.DESCENDING)
    .limit(50)

// Query: Listen to messages in a chat
db.collection("chats").document(chatId).collection("messages")
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .limit(50)
    .addSnapshotListener { /* ... */ }
```

---

## Scenario 2: Social Feed

### Problem
Design a data model for a social feed where:
- Users follow other users
- Feed shows posts from followed users
- Posts have likes, comments, and shares
- Feed must load in < 500ms

### Solution

```
posts/{postId}
  ├── authorId: "user_1"
  ├── authorName: "Alice"        // denormalized
  ├── authorPhotoUrl: "..."      // denormalized
  ├── content: "Hello world"
  ├── imageUrl: "..."
  ├── likeCount: 42              // counter
  ├── commentCount: 5            // counter
  ├── shareCount: 3              // counter
  ├── createdAt: timestamp
  └── tags: ["kotlin", "android"]

posts/{postId}/comments/{commentId}
  ├── authorId: "user_2"
  ├── authorName: "Bob"
  ├── text: "Great post!"
  └── createdAt: timestamp

posts/{postId}/likes/{userId}
  └── createdAt: timestamp

feeds/{userId}/posts/{postId}    // fan-out
  ├── authorId: "user_1"
  ├── content: "Hello world"
  ├── createdAt: timestamp
  └── // denormalized post data

followers/{userId}/following/{followedId}
  └── since: timestamp
```

### Key decisions
- **Fan-out** to feeds collection for O(1) feed reads
- **Counters** for like/comment/share counts (not arrays)
- **Subcollections** for comments and likes (unbounded)
- **Denormalize** author info into post (avoid N+1 reads)

```kotlin
// Query: Get feed (1 read, fast)
db.collection("feeds").document(userId).collection("posts")
    .orderBy("createdAt", Query.Direction.DESCENDING)
    .limit(20)

// Fan-out on post create (Cloud Function)
exports.onPostCreated = functions.firestore
  .document('posts/{postId}').onCreate(async (snap, context) => {
    const post = snap.data();
    const followers = await admin.firestore()
      .collection('followers').doc(post.authorId).collection('following').get();

    const batch = admin.firestore().batch();
    followers.docs.forEach(follower => {
      batch.set(
        admin.firestore().collection('feeds').doc(follower.id)
          .collection('posts').doc(context.params.postId),
        post
      );
    });
    await batch.commit();
  });
```

---

## Scenario 3: E-Commerce

### Problem
Design a data model for an e-commerce app with:
- Products with categories and variants
- Shopping cart
- Orders with multiple items
- Inventory tracking

### Solution

```
products/{productId}
  ├── name: "Wireless Headphones"
  ├── description: "..."
  ├── price: 99.99
  ├── categoryId: "cat_1"
  ├── categoryName: "Electronics"    // denormalized
  ├── imageUrl: "..."
  ├── stock: 150
  ├── rating: 4.5
  ├── reviewCount: 234
  ├── variants: [
        { name: "Color", value: "Black", stock: 75 },
        { name: "Color", value: "White", stock: 75 }
      ]
  └── tags: ["audio", "wireless"]

categories/{categoryId}
  ├── name: "Electronics"
  ├── productCount: 150
  └── imageUrl: "..."

carts/{userId}
  ├── items: [
        { productId: "p1", quantity: 2, price: 99.99 },
        { productId: "p2", quantity: 1, price: 49.99 }
      ]
  ├── total: 249.97
  └── updatedAt: timestamp

orders/{orderId}
  ├── userId: "user_1"
  ├── items: [
        { productId: "p1", name: "Headphones", quantity: 2, price: 99.99 },
        { productId: "p2", name: "Case", quantity: 1, price: 49.99 }
      ]
  ├── total: 249.97
  ├── status: "pending" | "paid" | "shipped" | "delivered"
  ├── shippingAddress: { ... }
  ├── paymentMethod: "card"
  ├── createdAt: timestamp
  └── updatedAt: timestamp
```

### Key decisions
- **Denormalize** product name/price into order (order is immutable snapshot)
- **Cart** is a single document per user (small, bounded)
- **Stock** in product doc (use transaction for decrement)
- **Order items** are embedded array (bounded, immutable)

```kotlin
// Checkout with transaction (atomic stock check + order)
db.runTransaction { transaction ->
    val productRef = db.collection("products").document(productId)
    val product = transaction.get(productRef)
    val stock = product.getLong("stock") ?: 0

    if (stock < quantity) {
        throw Exception("Insufficient stock")
    }

    // Decrement stock
    transaction.update(productRef, "stock", stock - quantity)

    // Create order
    val orderRef = db.collection("orders").document()
    transaction.set(orderRef, order)

    null
}
```

---

## Scenario 4: Leaderboard

### Problem
Design a leaderboard for a game with:
- 1M+ players
- Top 100 players displayed
- Real-time updates
- Player rank lookup

### Solution

```
leaderboard/{userId}
  ├── score: 15000
  ├── playerName: "Alice"
  ├── rank: 1
  └── updatedAt: timestamp
```

### Key decisions
- **Single collection** for leaderboard
- **Sharded** if write rate > 1/sec per player
- **Cloud Function** to update rank periodically

```kotlin
// Query: Top 100 players
db.collection("leaderboard")
    .orderBy("score", Query.Direction.DESCENDING)
    .limit(100)
    .addSnapshotListener { /* real-time top 100 */ }

// Query: Get player's rank
// Option 1: Store rank in document (updated by Cloud Function)
db.collection("leaderboard").document(userId).get()

// Option 2: Count players with higher score
db.collection("leaderboard")
    .whereGreaterThan("score", playerScore)
    .count().get()  // 1 read, returns count
// rank = count + 1
```

### For very large leaderboards (1M+)
- Use **bucketing**: group scores into ranges (0-999, 1000-1999, etc.)
- Use **Redis** for real-time leaderboard (sync from Firestore)
- Use **Cloud Function** to update ranks every 5 minutes

---

## 🔗 Related Topics
- [Architecture](../advanced/Architecture.md)
- [Performance](../advanced/Performance.md)
- [Security Rules](../intermediate/SecurityRules.md)
