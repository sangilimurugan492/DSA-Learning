# Firebase App Check

## Q1: What is App Check?

App Check protects Firebase resources from abuse by verifying that requests come from your authentic app.

### How it works
1. App sends a token from a certified provider (Play Integrity, App Attest, reCAPTCHA)
2. Firebase verifies the token
3. Requests without valid tokens are rejected

### Supported providers
| Platform | Provider |
|----------|---------|
| Android | Play Integrity API |
| iOS | App Attest / DeviceCheck |
| Web | reCAPTCHA Enterprise / reCAPTCHA v3 |
| Custom | Custom provider |

---

## Q2: How do you set up App Check for Android?

```kotlin
// 1. Add dependency
// implementation 'com.google.firebase:firebase-appcheck-playintegrity'

// 2. Initialize App Check
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
    }
}

// 3. Enable in Firebase Console
// Console → Settings → App Check → Register app → Enable enforcement
```

### Debug mode
```kotlin
// Use debug provider for testing
if (BuildConfig.DEBUG) {
    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
        DebugAppCheckProviderFactory.getInstance()
    )
}
```

---

## Q3: How do you enforce App Check?

### Enforcement levels
| Level | Behavior |
|-------|---------|
| Off | No verification |
| Log only | Requests logged, not blocked |
| Enforce | Invalid requests blocked |

### Enforce per service
```
Firebase Console → App Check → Firestore → Enforce
Firebase Console → App Check → Realtime DB → Enforce
Firebase Console → App Check → Storage → Enforce
Firebase Console → App Check → Cloud Functions → Enforce
```

### Enforce in Cloud Functions
```javascript
exports.secureFunction = functions.https.onCall(async (data, context) => {
  // App Check token is verified automatically for callable functions
  if (!context.app) {
    throw new functions.https.HttpsError('failed-precondition', 'App Check token missing');
  }
  // Proceed with function
});
```

---

## Q4: How do you use App Check with custom tokens?

```kotlin
// For server-to-server or custom backends
val appCheck = FirebaseAppCheck.getInstance()
appCheck.getAppCheckToken()
    .addOnSuccessListener { token ->
        val tokenString = token.token
        // Send to backend
        api.callSecureEndpoint(tokenString)
    }

// Backend verification
// POST /api/secure
// Header: X-Firebase-AppCheck: <token>
```

```javascript
// Backend (Node.js)
const admin = require('firebase-admin');
admin.initializeApp();

async function verifyAppCheck(token) {
  try {
    const result = await admin.appCheck().verifyToken(token);
    return result;  // { appId: '...', token: '...' }
  } catch (error) {
    throw new Error('Invalid App Check token');
  }
}

app.post('/api/secure', async (req, res) => {
  const token = req.headers['x-firebase-appcheck'];
  if (!token) return res.status(401).send('Missing App Check token');
  try {
    await verifyAppCheck(token);
    // Process request
  } catch (error) {
    res.status(403).send('Invalid App Check token');
  }
});
```

---

## Q5: How do you debug App Check?

```kotlin
// 1. Use debug provider
FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
    DebugAppCheckProviderFactory.getInstance()
)

// 2. Get debug secret from logcat
// "Debug App Check token: <DEBUG_TOKEN>"

// 3. Register debug token in Firebase Console
// Console → App Check → Manage debug tokens → Add token

// 4. Test with debug token
// Requests will pass App Check verification
```

### Common issues
| Issue | Solution |
|-------|----------|
| Token not generated | Check provider setup |
| Token rejected | Check debug token registration |
| Enforcement too strict | Start with "Log only" |
| Emulator fails | Use debug provider |
| Production fails | Check Play Integrity API setup |

---

## Q6: How do you handle App Check token caching?

```kotlin
// App Check tokens are cached automatically
// Token TTL: ~1 hour
// Refresh: automatic before expiry

// Force token refresh
FirebaseAppCheck.getInstance().getAppCheckToken(true)
    .addOnSuccessListener { token ->
        // Fresh token
    }

// With limited-use tokens (more secure, more cost)
// Enable in Console → App Check → Settings → Limited-use tokens
```

### Token types
| Type | TTL | Cost | Use Case |
|------|-----|------|----------|
| Standard | ~1 hour | Free | General use |
| Limited-use | Single use | Billed | High-security operations |

---

## Q7: What are App Check best practices?

### Rollout strategy
1. **Phase 1**: Install provider, no enforcement (measure)
2. **Phase 2**: Log only (identify issues)
3. **Phase 3**: Enforce on non-critical services
4. **Phase 4**: Enforce on all services

### Best practices
- Start with "Log only" before enforcing
- Use debug provider for development
- Register debug tokens for CI/CD
- Monitor App Check metrics in console
- Handle token refresh gracefully
- Don't rely solely on App Check — use Security Rules too
- Use limited-use tokens for sensitive operations
- Keep Play Integrity / App Attest configuration updated

### What App Check does NOT protect against
- Authenticated but malicious users
- Compromised devices
- Server-side vulnerabilities
- It's a first layer of defense, not the only one

---

## 🔗 Related Topics
- [Security Rules](../intermediate/SecurityRules.md)
- [Authentication](../beginner/Authentication.md)
- [Cloud Functions](../intermediate/CloudFunctions.md)
