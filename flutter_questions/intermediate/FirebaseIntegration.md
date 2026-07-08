# Firebase Integration

## Q1: How do you set up Firebase in Flutter?

```dart
// 1. Install CLI tools
// flutterfire configure  → generates firebase_options.dart

// 2. Initialize in main()
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(const MyApp());
}

// pubspec.yaml dependencies:
// firebase_core: ^2.24.0
// firebase_auth: ^4.15.0
// cloud_firestore: ^4.13.0
// firebase_storage: ^11.5.0
// firebase_messaging: ^14.7.0
```

---

## Q2: How do you implement Firebase Authentication?

```dart
import 'package:firebase_auth/firebase_auth.dart';

class AuthService {
  final _auth = FirebaseAuth.instance;

  // Email/Password sign up
  Future<User?> signUp(String email, String password) async {
    final credential = await _auth.createUserWithEmailAndPassword(
      email: email,
      password: password,
    );
    return credential.user;
  }

  // Sign in
  Future<User?> signIn(String email, String password) async {
    final credential = await _auth.signInWithEmailAndPassword(
      email: email,
      password: password,
    );
    return credential.user;
  }

  // Sign out
  Future<void> signOut() => _auth.signOut();

  // Auth state stream
  Stream<User?> get authState => _auth.authStateChanges();

  // Google Sign-In
  Future<User?> signInWithGoogle() async {
    final googleUser = await GoogleSignIn().signIn();
    final googleAuth = await googleUser!.authentication;
    final credential = GoogleAuthProvider.credential(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );
    return (await _auth.signInWithCredential(credential)).user;
  }

  // Password reset
  Future<void> resetPassword(String email) {
    return _auth.sendPasswordResetEmail(email: email);
  }

  // Phone auth
  Future<void> verifyPhone(String phone) async {
    await _auth.verifyPhoneNumber(
      phoneNumber: phone,
      verificationCompleted: (credential) async {
        await _auth.signInWithCredential(credential);
      },
      verificationFailed: (e) => print('Failed: $e'),
      codeSent: (verificationId, resendToken) {
        // Store verificationId, show OTP input
      },
      codeAutoRetrievalTimeout: (verificationId) {},
    );
  }
}

// Usage in widget
StreamBuilder<User?>(
  stream: AuthService().authState,
  builder: (context, snapshot) {
    if (snapshot.hasData) {
      return const HomeScreen();
    }
    return const LoginScreen();
  },
)
```

---

## Q3: How do you use Cloud Firestore?

```dart
import 'package:cloud_firestore/cloud_firestore.dart';

class FirestoreService {
  final _db = FirebaseFirestore.instance;

  // Create document
  Future<void> addUser(User user) async {
    await _db.collection('users').doc(user.id).set(user.toJson());
  }

  // Read single document
  Future<User?> getUser(String id) async {
    final doc = await _db.collection('users').doc(id).get();
    if (doc.exists) {
      return User.fromJson(doc.data()!);
    }
    return null;
  }

  // Read collection
  Future<List<User>> getUsers() async {
    final snapshot = await _db.collection('users').get();
    return snapshot.docs.map((d) => User.fromJson(d.data())).toList();
  }

  // Update document
  Future<void> updateUser(String id, Map<String, dynamic> data) async {
    await _db.collection('users').doc(id).update(data);
  }

  // Delete document
  Future<void> deleteUser(String id) async {
    await _db.collection('users').doc(id).delete();
  }

  // Query with filters
  Future<List<User>> getActiveUsers() async {
    final snapshot = await _db
        .collection('users')
        .where('isActive', isEqualTo: true)
        .where('age', isGreaterThanOrEqualTo: 18)
        .orderBy('name')
        .limit(20)
        .get();
    return snapshot.docs.map((d) => User.fromJson(d.data())).toList();
  }

  // Real-time stream
  Stream<List<User>> watchUsers() {
    return _db.collection('users').snapshots().map((snapshot) {
      return snapshot.docs.map((d) => User.fromJson(d.data())).toList();
    });
  }

  // Batch write
  Future<void> batchWrite(List<User> users) async {
    final batch = _db.batch();
    for (final user in users) {
      final ref = _db.collection('users').doc(user.id);
      batch.set(ref, user.toJson());
    }
    await batch.commit();
  }

  // Transaction
  Future<void> transferCredits(String fromId, String toId, int amount) async {
    await _db.runTransaction((txn) async {
      final fromRef = _db.collection('users').doc(fromId);
      final toRef = _db.collection('users').doc(toId);

      final fromDoc = await txn.get(fromRef);
      final toDoc = await txn.get(toRef);

      final fromCredits = fromDoc.data()!['credits'] as int;
      final toCredits = toDoc.data()!['credits'] as int;

      if (fromCredits < amount) throw Exception('Insufficient credits');

      txn.update(fromRef, {'credits': fromCredits - amount});
      txn.update(toRef, {'credits': toCredits + amount});
    });
  }
}
```

---

## Q4: How do you use Firebase Storage?

```dart
import 'package:firebase_storage/firebase_storage.dart';

class StorageService {
  final _storage = FirebaseStorage.instance;

  // Upload file
  Future<String> uploadImage(File file, String path) async {
    final ref = _storage.ref().child(path);
    final task = ref.putFile(file);

    // Track progress
    task.snapshotEvents.listen((event) {
      final progress = event.bytesTransferred / event.totalBytes * 100;
      print('Upload: ${progress.toStringAsFixed(0)}%');
    });

    final snapshot = await task;
    return snapshot.ref.getDownloadURL();  // Return URL
  }

  // Download file
  Future<File> downloadFile(String path, String localPath) async {
    final ref = _storage.ref().child(path);
    final file = File(localPath);
    await ref.writeToFile(file);
    return file;
  }

  // Delete file
  Future<void> deleteFile(String path) async {
    await _storage.ref().child(path).delete();
  }

  // List files
  Future<List<String>> listFiles(String folder) async {
    final result = await _storage.ref(folder).listAll();
    return result.items.map((ref) => ref.fullPath).toList();
  }
}
```

---

## Q5: How do you implement Push Notifications?

```dart
import 'package:firebase_messaging/firebase_messaging.dart';

// Background handler (must be top-level function)
@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  print('Background message: ${message.messageId}');
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
  runApp(const MyApp());
}

class NotificationService {
  Future<void> init() async {
    // Request permission
    await FirebaseMessaging.instance.requestPermission(
      alert: true, badge: true, sound: true,
    );

    // Get FCM token
    final token = await FirebaseMessaging.instance.getToken();
    print('FCM Token: $token');
    // Send this token to your server

    // Foreground messages
    FirebaseMessaging.onMessage.listen((message) {
      print('Foreground: ${message.notification?.title}');
      // Show local notification
    });

    // Background/notification tap
    FirebaseMessaging.onMessageOpenedApp.listen((message) {
      // Navigate to specific screen
      final screen = message.data['screen'];
      if (screen != null) Navigator.pushNamed(context, screen);
    });

    // Terminated state tap
    final initialMessage = await FirebaseMessaging.instance.getInitialMessage();
    if (initialMessage != null) {
      // App opened from notification
    }
  }

  // Subscribe to topic
  Future<void> subscribeToTopic(String topic) {
    return FirebaseMessaging.instance.subscribeToTopic(topic);
  }
}
```

---

## Q6: How do you use Firebase Remote Config?

```dart
import 'package:firebase_remote_config/firebase_remote_config.dart';

class RemoteConfigService {
  final _remoteConfig = FirebaseRemoteConfig.instance;

  Future<void> init() async {
    await _remoteConfig.setConfigSettings(RemoteConfigSettings(
      fetchTimeout: const Duration(seconds: 10),
      minimumFetchInterval: const Duration(hours: 1),
    ));

    await _remoteConfig.setDefaults({
      'maintenance_mode': false,
      'max_items_per_page': 20,
      'feature_flag_new_ui': false,
    });

    await _remoteConfig.fetchAndActivate();
  }

  bool get maintenanceMode => _remoteConfig.getBool('maintenance_mode');
  int get maxItems => _remoteConfig.getInt('max_items_per_page');
  bool get newUiEnabled => _remoteConfig.getBool('feature_flag_new_ui');
}

// Usage
if (remoteConfig.maintenanceMode) {
  return const MaintenanceScreen();
}
```

---

## Q7: How do you structure Firestore for a chat app?

```dart
// Firestore structure for chat
// chats/{chatId}/messages/{messageId}

// Chat document
// chats/chat_123
{
  'participants': ['user_1', 'user_2'],
  'lastMessage': 'Hello!',
  'lastMessageTime': FieldValue.serverTimestamp(),
  'unreadCount': {'user_1': 0, 'user_2': 1},
}

// Message document
// chats/chat_123/messages/msg_456
{
  'senderId': 'user_1',
  'text': 'Hello!',
  'timestamp': FieldValue.serverTimestamp(),
  'type': 'text',  // text, image, file
}

// Service
class ChatService {
  final _db = FirebaseFirestore.instance;

  // Send message
  Future<void> sendMessage(String chatId, String senderId, String text) async {
    final batch = _db.batch();

    // Add message
    final msgRef = _db.collection('chats').doc(chatId).collection('messages').doc();
    batch.set(msgRef, {
      'senderId': senderId,
      'text': text,
      'timestamp': FieldValue.serverTimestamp(),
      'type': 'text',
    });

    // Update chat metadata
    final chatRef = _db.collection('chats').doc(chatId);
    batch.update(chatRef, {
      'lastMessage': text,
      'lastMessageTime': FieldValue.serverTimestamp(),
    });

    await batch.commit();
  }

  // Stream messages (real-time)
  Stream<List<Message>> watchMessages(String chatId) {
    return _db
        .collection('chats')
        .doc(chatId)
        .collection('messages')
        .orderBy('timestamp', descending: true)
        .limit(50)
        .snapshots()
        .map((s) => s.docs.map((d) => Message.fromJson(d.data())).toList());
  }

  // Get user's chats
  Stream<List<Chat>> watchUserChats(String userId) {
    return _db
        .collection('chats')
        .where('participants', arrayContains: userId)
        .orderBy('lastMessageTime', descending: true)
        .snapshots()
        .map((s) => s.docs.map((d) => Chat.fromJson(d.data())).toList());
  }
}
```

---

## 🔗 Related Topics
- [HTTP & Networking](HTTPNetworking.md)
- [State Management Advanced](StateManagementAdvanced.md)
- [Testing](Testing.md)
