# Firebase Integration

## 📖 Explanation

Firebase provides a backend-as-a-service for Flutter apps — authentication, real-time database, Firestore, cloud storage, push notifications, analytics, and crash reporting.

### Firebase Services for Flutter
| Service | Package | Use Case |
|---------|---------|----------|
| Authentication | `firebase_auth` | Email, Google, Apple, phone login |
| Cloud Firestore | `cloud_firestore` | NoSQL real-time database |
| Realtime Database | `firebase_database` | JSON real-time sync |
| Cloud Storage | `firebase_storage` | File uploads (images, videos) |
| Cloud Messaging | `firebase_messaging` | Push notifications |
| Analytics | `firebase_analytics` | User behavior tracking |
| Crashlytics | `firebase_crashlytics` | Crash reporting |
| Remote Config | `firebase_remote_config` | Feature flags, A/B testing |

### Firebase Initialization
```dart
// main.dart
await Firebase.initializeApp(
  options: DefaultFirebaseOptions.currentPlatform,
);
```

### Firestore Data Model
```
Collections (top-level)  →  Documents  →  Fields
users/                    →  user_123    →  {name, email, createdAt}
                          →  user_456    →  {name, email, createdAt}
chats/                    →  chat_001    →  {members, lastMessage}
  chat_001/messages/      →  msg_001     →  {text, senderId, timestamp}
```

### Firestore vs Realtime Database
| Feature | Firestore | Realtime DB |
|---------|-----------|-------------|
| Data model | Documents/Collections | JSON tree |
| Querying | Complex queries | Limited |
| Offline | Built-in cache | Manual |
| Scaling | Automatic | Sharding |
| Best for | Complex apps | Real-time sync |

### Firestore Operations
| Operation | Method |
|-----------|--------|
| Create | `collection.add()` or `doc.set()` |
| Read once | `doc.get()` or `collection.get()` |
| Read real-time | `doc.snapshots()` or `collection.snapshots()` |
| Update | `doc.update()` |
| Delete | `doc.delete()` |
| Query | `collection.where().orderBy().limit()` |

### Auth Providers
| Provider | Method |
|----------|--------|
| Email/Password | `createUserWithEmailAndPassword` |
| Google | `GoogleSignIn` + `signInWithCredential` |
| Apple | `SignInWithApple` |
| Phone | `verifyPhoneNumber` + `signInWithCredential` |
| Anonymous | `signInAnonymously` |

---

## 🧪 Code Example

```dart
// ── Firebase initialization ──
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  // Crashlytics — catch all errors
  FlutterError.onError = FirebaseCrashlytics.instance.recordFlutterFatalError;

  runApp(const MyApp());
}

// ── Authentication ──
class AuthService {
  final _auth = FirebaseAuth.instance;

  Stream<User?> get authState => _auth.authStateChanges();
  User? get currentUser => _auth.currentUser;

  Future<User> signUp(String email, String password) async {
    final credential = await _auth.createUserWithEmailAndPassword(
      email: email, password: password,
    );
    // Create user document in Firestore
    await FirebaseFirestore.instance
        .collection('users')
        .doc(credential.user!.uid)
        .set({'email': email, 'createdAt': FieldValue.serverTimestamp()});
    return credential.user!;
  }

  Future<User> signIn(String email, String password) async {
    final credential = await _auth.signInWithEmailAndPassword(
      email: email, password: password,
    );
    return credential.user!;
  }

  Future<User> signInWithGoogle() async {
    final googleUser = await GoogleSignIn().signIn();
    final googleAuth = await googleUser!.authentication;
    final credential = GoogleAuthProvider.credential(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );
    return (await _auth.signInWithCredential(credential)).user!;
  }

  Future<void> signOut() async {
    await GoogleSignIn().signOut();
    await _auth.signOut();
  }
}

// ── Firestore CRUD ──
class FirestoreService {
  final _db = FirebaseFirestore.instance;

  // Create
  Future<void> addUser(UserModel user) async {
    await _db.collection('users').doc(user.id).set(user.toMap());
  }

  // Read once
  Future<UserModel?> getUser(String id) async {
    final doc = await _db.collection('users').doc(id).get();
    if (!doc.exists) return null;
    return UserModel.fromMap(doc.id, doc.data()!);
  }

  // Read real-time stream
  Stream<List<UserModel>> getUsersStream() {
    return _db.collection('users')
        .orderBy('name')
        .snapshots()
        .map((snapshot) => snapshot.docs
            .map((doc) => UserModel.fromMap(doc.id, doc.data()))
            .toList());
  }

  // Update
  Future<void> updateUser(String id, Map<String, dynamic> data) async {
    await _db.collection('users').doc(id).update(data);
  }

  // Delete
  Future<void> deleteUser(String id) async {
    await _db.collection('users').doc(id).delete();
  }

  // Query
  Future<List<UserModel>> searchUsers(String query) async {
    final snapshot = await _db.collection('users')
        .where('name', isGreaterThanOrEqualTo: query)
        .where('name', isLessThanOrEqualTo: '$query\uf8ff')
        .limit(20)
        .get();
    return snapshot.docs
        .map((doc) => UserModel.fromMap(doc.id, doc.data()))
        .toList();
  }
}

// ── StreamBuilder with Firestore ──
class UserListScreen extends StatelessWidget {
  const UserListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<QuerySnapshot>(
      stream: FirebaseFirestore.instance
          .collection('users')
          .orderBy('createdAt', descending: true)
          .snapshots(),
      builder: (context, snapshot) {
        if (snapshot.hasError) {
          return const Center(child: Text('Error loading users'));
        }
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        final users = snapshot.data!.docs;
        return ListView.builder(
          itemCount: users.length,
          itemBuilder: (context, index) {
            final user = users[index].data() as Map<String, dynamic>;
            return ListTile(title: Text(user['name'] ?? 'Unknown'));
          },
        );
      },
    );
  }
}

// ── Cloud Storage — upload image ──
class StorageService {
  final _storage = FirebaseStorage.instance;

  Future<String> uploadImage(File image, String userId) async {
    final ref = _storage.ref('users/$userId/avatar.jpg');
    final task = await ref.putFile(image);
    return await task.ref.getDownloadURL();  // Return download URL
  }
}

// ── Push Notifications ──
class NotificationService {
  Future<void> init() async {
    final messaging = FirebaseMessaging.instance;

    // Request permission (iOS)
    await messaging.requestPermission();

    // Get FCM token
    final token = await messaging.getToken();
    print('FCM Token: $token');

    // Foreground messages
    FirebaseMessaging.onMessage.listen((message) {
      print('Foreground: ${message.notification?.title}');
    });

    // Background/terminated
    FirebaseMessaging.onBackgroundMessage(_backgroundHandler);
  }
}

@pragma('vm:entry-point')
Future<void> _backgroundHandler(RemoteMessage message) async {
  print('Background: ${message.notification?.title}');
}
```

### Output
```
A Flutter app with Firebase integration:
- Auth: email/password and Google sign-in with Firestore user creation
- Firestore: CRUD operations + real-time streams + search queries
- StreamBuilder for reactive UI updates
- Cloud Storage: image upload with download URL
- FCM push notifications: foreground and background handlers
```

---

## ❓ Interview Questions

1. **How do you integrate Firebase in a Flutter app?**
   - Add Firebase to the project: use `flutterfire configure` CLI to auto-configure for all platforms. This generates `firebase_options.dart` with platform-specific config. In `main()`, call `await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform)` before `runApp()`. Add packages in `pubspec.yaml`: `firebase_core` (required), `firebase_auth`, `cloud_firestore`, `firebase_storage`, `firebase_messaging`, etc. For Crashlytics, set `FlutterError.onError = FirebaseCrashlytics.instance.recordFlutterFatalError`. Always initialize Firebase before any Firebase service usage.

2. **How do you implement authentication in Flutter with Firebase?**
   - Use `firebase_auth` package. Email/password: `_auth.createUserWithEmailAndPassword(email, password)` and `_auth.signInWithEmailAndPassword(email, password)`. Google: `GoogleSignIn().signIn()` → get `GoogleAuthAccount` → create `GoogleAuthProvider.credential(accessToken, idToken)` → `_auth.signInWithCredential(credential)`. Listen to auth state: `_auth.authStateChanges()` returns a `Stream<User?>` — use with `StreamBuilder` or Provider to rebuild UI on login/logout. Store additional user data in Firestore after sign-up. Always `signOut()` on logout. For phone auth, use `verifyPhoneNumber()` with code sent via SMS.

3. **How do you use Firestore in Flutter?**
   - Firestore is a NoSQL document database. Collections contain documents, which contain fields (and subcollections). Create: `db.collection('users').doc(id).set({'name': 'John'})`. Read once: `db.collection('users').doc(id).get()` → `doc.data()`. Read real-time: `db.collection('users').snapshots()` → `Stream<QuerySnapshot>`. Update: `db.collection('users').doc(id).update({'name': 'Jane'})`. Delete: `db.collection('users').doc(id).delete()`. Query: `db.collection('users').where('age', isGreaterThan: 18).orderBy('name').limit(10).get()`. Use `StreamBuilder` to reactively update UI on data changes. Use `FieldValue.serverTimestamp()` for server-side timestamps.

4. **What is the difference between Firestore and Realtime Database?**
   - **Firestore**: document/ collection model, complex queries (where, orderBy, limit), built-in offline cache, automatic scaling, hierarchical data. Best for most apps. **Realtime Database**: JSON tree model, limited queries, manual offline support, requires sharding for scale, simpler real-time sync. Best for simple real-time features (chat, presence). Firestore is the recommended default — it's newer, more scalable, and has better querying. Use Realtime Database only when you need very fast real-time syncing of simple JSON data (e.g., live game state, presence detection).

5. **How do you handle real-time updates with Firestore?**
   - Use `snapshots()` which returns a `Stream`. For a single document: `db.collection('users').doc(id).snapshots()` → `Stream<DocumentSnapshot>`. For a collection: `db.collection('users').snapshots()` → `Stream<QuerySnapshot>`. Use with `StreamBuilder` in the widget tree — Flutter automatically rebuilds when data changes, and cancels the stream subscription when the widget is disposed. For filtered real-time: `db.collection('users').where('status', isEqualTo: 'active').snapshots()`. The stream stays open and emits new snapshots whenever data changes in Firestore — no need for polling.

6. **How do you upload files to Firebase Storage?**
   - Use `firebase_storage` package. Create a reference: `final ref = FirebaseStorage.instance.ref('users/$userId/avatar.jpg')`. Upload: `final task = await ref.putFile(imageFile)`. Get download URL: `final url = await task.ref.getDownloadURL()`. Track progress: `ref.putFile(file).snapshotEvents.listen((task) { print('${task.bytesTransferred / task.totalBytes * 100}%'); })`. Upload from bytes: `ref.putData(uint8List)`. For large files, use `UploadTask` with pause/resume: `task.pause()`, `task.resume()`, `task.cancel()`. Store the download URL in Firestore for later retrieval.

7. **How do you implement push notifications with FCM?**
   - Use `firebase_messaging` package. Request permission: `messaging.requestPermission()`. Get token: `messaging.getToken()` — send this token to your server to send targeted notifications. Foreground messages: `FirebaseMessaging.onMessage.listen((message) { ... })` — show in-app notification. Background: `FirebaseMessaging.onBackgroundMessage(_handler)` — annotate with `@pragma('vm:entry-point')`. Terminated: `messaging.getInitialMessage()` — check if app was opened from notification. Use `flutter_local_notifications` to display notifications when in foreground. Send from server using the FCM HTTP API with the device token.

8. **How do you structure Firestore data for a chat app?**
   - Top-level `chats` collection: each chat document has `members` (array of user IDs), `lastMessage`, `lastMessageTime`, `unreadCount`. Subcollection `chats/{chatId}/messages`: each message document has `senderId`, `text`, `timestamp`, `type` (text/image). Query user's chats: `db.collection('chats').where('members', arrayContains: userId).orderBy('lastMessageTime', descending: true)`. Real-time messages: `db.collection('chats/$chatId/messages').orderBy('timestamp', descending: true).limit(50).snapshots()`. Use batch writes for sending a message (update message + update chat's lastMessage). Use `FieldValue.arrayUnion()` to add members.

9. **How do you handle offline support with Firebase?**
   - Firestore has built-in offline persistence — enable with `FirebaseFirestore.instance.settings = Settings(persistenceEnabled: true)`. Reads work offline from cache. Writes are queued and synced when online. Check if data is from cache: `snapshot.metadata.isFromCache`. For auth, use `flutter_secure_storage` to persist the user session. For file storage, cache downloaded files locally. For push notifications, queue notification sends server-side. Use `connectivity_plus` to detect online/offline status and show appropriate UI. Firestore offline support is automatic — no extra code needed for basic CRUD operations.

10. **How do you use Firebase Remote Config for feature flags?**
    - Use `firebase_remote_config` package. Initialize: `final remoteConfig = FirebaseRemoteConfig.instance`. Set defaults: `remoteConfig.setDefaults({'new_feature_enabled': false})`. Fetch: `await remoteConfig.fetchAndActivate()`. Read: `remoteConfig.getBool('new_feature_enabled')`. Set values in Firebase Console — changes propagate to all devices. Use for: feature flags (enable/disable features per platform), A/B testing (different values for different user groups), emergency switches (disable a broken feature), phased rollouts. Set `minimumFetchInterval` to control how often the app fetches new config (default 12 hours). Use `StreamBuilder` with `remoteConfig.onConfigChanged` for real-time updates.

---

## 🔗 Related Topics
- [HTTP Networking](HTTPNetworking.md)
- [State Management Advanced](StateManagementAdvanced.md)
- [Architecture Patterns](../advanced/ArchitecturePatterns.md)
