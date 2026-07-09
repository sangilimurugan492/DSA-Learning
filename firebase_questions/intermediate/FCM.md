# Firebase Cloud Messaging (FCM)

## Q1: What is FCM?

Cross-platform messaging solution for sending notifications and data messages.

### Message types
| Type | Description | Use Case |
|------|-------------|----------|
| Notification | System displays automatically | Push notifications |
| Data | App handles in code | Custom data, background sync |
| Combined | Both notification + data | Notification with payload |

---

## Q2: How do you receive push notifications on Android?

```kotlin
// Extend FirebaseMessagingService
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Handle notification when app is in foreground
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Notification message
        remoteMessage.notification?.let { notification ->
            showNotification(notification.title, notification.body)
        }

        // Data message
        remoteMessage.data.isNotEmpty().let {
            val orderId = remoteMessage.data["orderId"]
            val action = remoteMessage.data["action"]
            handleDataMessage(orderId, action)
        }
    }

    // Handle token refresh
    override fun onNewToken(token: String) {
        // Send token to server
        Firebase.firestore.collection("users")
            .document(userId).update("fcmToken", token)
    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(1, notification)
    }
}
```

### Manifest
```xml
<service android:name=".MyFirebaseMessagingService"
         android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

---

## Q3: How do you send messages from the server?

```javascript
const admin = require('firebase-admin');
admin.initializeApp();

// Send to a specific device
const message = {
  token: 'device-token-here',
  notification: {
    title: 'Order Shipped',
    body: 'Your order #12345 has been shipped!'
  },
  data: {
    orderId: '12345',
    action: 'ORDER_SHIPPED'
  },
  android: {
    priority: 'high',
    notification: {
      channelId: 'orders',
      icon: 'ic_notification',
      color: '#FF6B35'
    }
  }
};
admin.messaging().send(message);

// Send to a topic
admin.messaging().send({
  topic: 'promotions',
  notification: { title: 'Sale!', body: '50% off everything' }
});

// Send to multiple devices
admin.messaging().sendMulticast({
  tokens: ['token1', 'token2', 'token3'],
  notification: { title: 'New message', body: 'You have a new message' }
});

// Send with conditions (topic1 AND topic2)
admin.messaging().send({
  condition: "'sports' in topics && 'news' in topics",
  notification: { title: 'Breaking News', body: 'Sports update' }
});
```

---

## Q4: How do you manage device tokens?

```kotlin
// Get current token
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        // Save to Firestore
        saveTokenToServer(token)
    }
}

// Subscribe to topic
FirebaseMessaging.getInstance().subscribeToTopic("promotions")
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            println("Subscribed to promotions")
        }
    }

// Unsubscribe from topic
FirebaseMessaging.getInstance().unsubscribeFromTopic("promotions")

// Delete token (on sign out)
FirebaseMessaging.getInstance().deleteToken()
```

### Token lifecycle
1. App installs → FCM generates token
2. `onNewToken()` called → save to server
3. Token may rotate → `onNewToken()` called again
4. User signs out → delete token from server
5. User signs in → get new token, save to server

---

## Q5: How do you handle notification channels (Android 8+)?

```kotlin
// Create notification channel (call once on app start)
fun createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "orders",
            "Order Updates",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications about your orders"
            enableVibration(true)
            enableLights(true)
            lightColor = Color.GREEN
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

// Send with channel from server
// android: { notification: { channelId: 'orders' } }
```

---

## Q6: How do you handle background vs foreground?

```kotlin
// Foreground — onMessageReceived is called
// You must display the notification yourself

// Background — system tray displays notification automatically
// onMessageReceived is NOT called for notification-only messages
// onMessageReceived IS called for data messages

// To always handle in onMessageReceived, send data-only messages:
// { data: { title: 'Hello', body: 'World', click_action: 'OPEN_CHAT' } }
```

### Handling notification tap
```kotlin
// In Activity
val intent = intent
if (intent.extras != null) {
    val orderId = intent.getStringExtra("orderId")
    val action = intent.getStringExtra("action")
    // Navigate to order detail
}
```

---

## Q7: How do you send targeted notifications?

```javascript
// Send to users with specific subscription
admin.messaging().send({
  topic: 'premium_users',
  notification: { title: 'Premium Feature', body: 'New feature available!' }
});

// Send to specific user (via token stored in Firestore)
async function sendToUser(userId, title, body) {
  const userDoc = await admin.firestore().collection('users').doc(userId).get();
  const token = userDoc.data().fcmToken;
  if (!token) return;

  await admin.messaging().send({
    token: token,
    notification: { title, body },
    data: { userId }
  });
}

// Send to multiple users
async function sendToUsers(userIds, title, body) {
  const tokens = await Promise.all(
    userIds.map(id =>
      admin.firestore().collection('users').doc(id).get()
        .then(doc => doc.data()?.fcmToken)
    )
  );
  const validTokens = tokens.filter(Boolean);

  await admin.messaging().sendEachForMulticast({
    tokens: validTokens,
    notification: { title, body }
  });
}
```

---

## 🔗 Related Topics
- [Cloud Functions](CloudFunctions.md)
- [Authentication](../beginner/Authentication.md)
- [Analytics](Analytics.md)
