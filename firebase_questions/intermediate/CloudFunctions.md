# Cloud Functions for Firebase

## Q1: What are Cloud Functions?

Serverless functions that run in response to Firebase events. No server management needed.

### Function types
| Type | Trigger | Use Case |
|------|---------|----------|
| HTTP | HTTP request | Webhooks, API endpoints |
| Callable | Called from app | Authenticated actions |
| Firestore Trigger | Document write | Data validation, aggregation |
| Auth Trigger | User created/deleted | Welcome email, cleanup |
| Storage Trigger | File uploaded | Image resize, thumbnail |
| RTDB Trigger | Data write | Real-time processing |
| Pub/Sub | Scheduled/event | Cron jobs, batch processing |

---

## Q2: How do you write HTTP functions?

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// HTTP function
exports.helloWorld = functions.https.onRequest((req, res) => {
  res.status(200).send('Hello World');
});

// HTTP function with CORS
exports.api = functions.https.onRequest((req, res) => {
  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'GET, POST');
  if (req.method === 'OPTIONS') { res.status(204).send(''); return; }
  res.json({ data: req.body });
});

// Callable function (called from app)
exports.getUserData = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError('unauthenticated', 'Sign in required');
  const uid = context.auth.uid;
  const doc = await admin.firestore().collection('users').doc(uid).get();
  return { user: doc.data() };
});
```

### Calling from Android
```kotlin
val functions = Firebase.functions
val data = hashMapOf("userId" to userId)
functions.getHttpsCallable("getUserData").call(data)
    .addOnSuccessListener { result ->
        val user = result.data as Map<*, *>
    }
```

---

## Q3: How do you write Firestore trigger functions?

```javascript
// onCreate — new document
exports.onUserCreated = functions.firestore
  .document('users/{userId}')
  .onCreate(async (snap, context) => {
    const newUser = snap.data();
    await admin.firestore().collection('stats').doc('users')
      .update({ count: admin.firestore.FieldValue.increment(1) });
    // Send welcome email
    await sendWelcomeEmail(newUser.email);
  });

// onUpdate — document changed
exports.onOrderUpdated = functions.firestore
  .document('orders/{orderId}')
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    if (before.status !== after.status) {
      await admin.firestore().collection('notifications').add({
        userId: after.userId,
        message: `Order ${after.status}`,
        timestamp: admin.firestore.FieldValue.serverTimestamp()
      });
    }
  });

// onDelete — document deleted
exports.onPostDeleted = functions.firestore
  .document('posts/{postId}')
  .onDelete(async (snap, context) => {
    const postId = context.params.postId;
    // Delete all comments
    const comments = await admin.firestore().collection('comments')
      .where('postId', '==', postId).get();
    const batch = admin.firestore().batch();
    comments.docs.forEach(doc => batch.delete(doc.ref));
    await batch.commit();
  });
```

---

## Q4: How do you write Auth trigger functions?

```javascript
// User created
exports.onUserSignUp = functions.auth.user().onCreate(async (user) => {
  // Create user document in Firestore
  await admin.firestore().collection('users').doc(user.uid).set({
    email: user.email,
    displayName: user.displayName || '',
    photoURL: user.photoURL || '',
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    role: 'user'
  });
});

// User deleted
exports.onUserDelete = functions.auth.user().onDelete(async (user) => {
  // Clean up user data
  await admin.firestore().collection('users').doc(user.uid).delete();
  // Delete user's files from Storage
  await admin.storage().bucket().deleteFiles({
    prefix: `users/${user.uid}/`
  });
});
```

---

## Q5: How do you write Storage trigger functions?

```javascript
const sharp = require('sharp');

// Generate thumbnail on image upload
exports.generateThumbnail = functions.storage.object().onFinalize(async (object) => {
  const filePath = object.name;
  const contentType = object.contentType;

  if (!contentType.startsWith('image/')) return;
  if (filePath.includes('thumbnails/')) return; // Skip thumbnails

  const bucket = admin.storage().bucket(object.bucket);
  const fileName = filePath.split('/').pop();
  const tempFile = `/tmp/${fileName}`;

  // Download original
  await bucket.file(filePath).download({ destination: tempFile });

  // Resize
  const thumbPath = `/tmp/thumb_${fileName}`;
  await sharp(tempFile).resize(200, 200).toFile(thumbPath);

  // Upload thumbnail
  const thumbFilePath = `thumbnails/${fileName}`;
  await bucket.upload(thumbPath, { destination: thumbFilePath });

  // Save thumbnail URL to Firestore
  await admin.firestore().collection('images').doc(fileName).set({
    thumbnailPath: thumbFilePath,
    originalPath: filePath
  }, { merge: true });
});
```

---

## Q6: How do you handle cold starts?

```javascript
// ❌ Bad — initialization inside function (runs on every cold start)
exports.processData = functions.https.onCall((data, context) => {
  const admin = require('firebase-admin');  // Re-initialized every time
  admin.initializeApp();  // Error: already initialized
});

// ✅ Good — initialization at module level (runs once per instance)
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

exports.processData = functions.https.onCall(async (data, context) => {
  const result = await db.collection('data').get();
  return { data: result.docs.map(d => d.data()) };
});

// Use global scope for expensive operations
const heavyClient = createExpensiveClient();  // Runs once per instance

exports.processData = functions.https.onCall(async (data, context) => {
  return heavyClient.process(data);
});
```

### Cold start tips
- Initialize at module level, not inside function
- Set `minInstances` for critical functions
- Use `memory` option appropriately
- Keep dependencies minimal

```javascript
exports.criticalFunction = functions
  .runWith({ minInstances: 1, memory: '512MB' })
  .https.onCall((data, context) => { });
```

---

## Q7: How do you schedule functions?

```javascript
// Cron job — every 5 minutes
exports.cleanupOldSessions = functions.pubsub
  .schedule('every 5 minutes')
  .onRun(async (context) => {
    const cutoff = admin.firestore.Timestamp.fromMillis(Date.now() - 24 * 60 * 60 * 1000);
    const oldSessions = await admin.firestore().collection('sessions')
      .where('createdAt', '<', cutoff).get();
    const batch = admin.firestore().batch();
    oldSessions.docs.forEach(doc => batch.delete(doc.ref));
    await batch.commit();
  });

// Cron job — every day at midnight
exports.dailyReport = functions.pubsub
  .schedule('0 0 * * *')
  .timeZone('America/New_York')
  .onRun(async (context) => {
    // Generate daily report
    await generateReport();
  });
```

---

## 🔗 Related Topics
- [Firestore Basics](../beginner/FirestoreBasics.md)
- [FCM](FCM.md)
- [Scaling](../advanced/Scaling.md)
