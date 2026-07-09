# Security Scenarios

## Scenario 1: Preventing Man-in-the-Middle Attacks

### Problem
Your banking app's API traffic can be intercepted via proxy (Charles/Burp). How do you prevent this?

### Solution: Multi-layer defense

```kotlin
// Layer 1: SSL Pinning (Network Security Config)
// res/xml/network_security_config.xml
// → See SSLPinning.md for full config

// Layer 2: OkHttp CertificatePinner
val client = OkHttpClient.Builder()
    .certificatePinner(
        CertificatePinner.Builder()
            .add("api.bank.com", "sha256/PRIMARY_PIN=")
            .add("api.bank.com", "sha256/BACKUP_PIN=")
            .build()
    )
    .build()

// Layer 3: Detect proxy
fun isProxyEnabled(): Boolean {
    val proxy = System.getProperty("http.proxyHost")
    return proxy != null
}

// Layer 4: Detect certificate from unexpected source
class SecurityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        response.handshake?.let { handshake ->
            // Verify certificate chain
            val peerCerts = handshake.peerCertificates
            if (peerCerts.isEmpty()) {
                throw IOException("No certificate presented")
            }
        }
        return response
    }
}
```

### Defense in depth
| Layer | What it prevents |
|------|-----------------|
| SSL Pinning | MITM with custom CA |
| Proxy detection | Casual proxy debugging |
| Certificate chain check | Self-signed certs |
| Cleartext traffic blocked | HTTP downgrade attacks |

---

## Scenario 2: Secure Token Storage

### Problem
Your app receives an auth token from the server. Where and how do you store it?

### Solution

```kotlin
class TokenManager(context: Context) {

    // Use EncryptedSharedPreferences for tokens
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .putLong("token_timestamp", System.currentTimeMillis())
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)

    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun isTokenExpired(): Boolean {
        val timestamp = prefs.getLong("token_timestamp", 0)
        val oneHour = 60 * 60 * 1000L
        return System.currentTimeMillis() - timestamp > oneHour
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}
```

### Token storage comparison
| Method | Security | Survives reinstall? | Best for |
|--------|---------|-------------------|---------|
| SharedPreferences | ❌ Plaintext | ❌ No | Nothing |
| EncryptedSharedPreferences | ✅ Encrypted | ❌ No | Auth tokens |
| Keystore + file | ✅ Hardware | ❌ No | Sensitive data |
| AccountManager | ✅ System | ❌ No | System integration |

### Token security checklist
- [ ] Never log tokens
- [ ] Store in EncryptedSharedPreferences (not plain prefs)
- [ ] Clear tokens on logout
- [ ] Use short-lived access tokens (15-60 min)
- [ ] Use refresh tokens for renewal
- [ ] Revoke tokens on server when user logs out
- [ ] Don't store tokens in intent extras

---

## Scenario 3: API Key Protection

### Problem
Your app uses a third-party API (Google Maps, Stripe) that requires an API key. How do you protect it?

### Solution: Backend proxy (best approach)

```
App ──► Your Backend ──► Third-party API
         (holds API key)
```

```kotlin
// App calls your backend, not the third-party API directly
interface ApiService {
    @POST("maps/geocode")
    suspend fun geocode(@Body request: GeocodeRequest): GeocodeResponse
}

// Backend (Node.js)
// app.post('/maps/geocode', async (req, res) => {
//   const result = await fetch('https://maps.googleapis.com/maps/api/geocode/json', {
//     headers: { Authorization: `Bearer ${process.env.GOOGLE_MAPS_API_KEY}` }
//   });
//   res.json(await result.json());
// });
```

### If you must store key on device
```kotlin
// 1. Use NDK (harder to extract from .so file)
// app/src/main/cpp/api_keys.cpp
extern "C" JNIEXPORT jstring JNICALL
Java_com_app_Keys_getMapsApiKey(JNIEnv* env, jobject) {
    return env->NewStringUTF("AIza...");
}

// 2. Restrict API key in Google Cloud Console
// - Restrict to Android app (package name + SHA-1)
// - Restrict to specific APIs
// - Restrict to specific IP addresses

// 3. Use certificate fingerprint restriction
// Google Console → Credentials → API Key → Android apps
// Add: com.example.app + SHA-1 fingerprint
```

### API key protection levels
| Method | Security | Effort |
|--------|---------|--------|
| Hardcoded in Kotlin | ❌ Low | Low |
| BuildConfig | ❌ Low | Low |
| NDK/C++ | ⚠️ Medium | Medium |
| API key restriction (console) | ✅ Medium | Low |
| Backend proxy | ✅ Highest | High |

---

## Scenario 4: Root Detection

### Problem
Your app should detect if it's running on a rooted device and warn the user.

### Solution

```kotlin
class RootDetector {

    fun isDeviceRooted(): Boolean {
        return checkRootFiles() || checkSuBinary() || checkRootApps()
    }

    // 1. Check for common root files
    private fun checkRootFiles(): Boolean {
        val paths = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        return paths.any { File(it).exists() }
    }

    // 2. Try to execute 'su'
    private fun checkSuBinary(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.readLine() != null
        } catch (e: Exception) {
            false
        }
    }

    // 3. Check for root apps
    private fun checkRootApps(): Boolean {
        val rootApps = listOf(
            "com.topjohnwu.magisk",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.kingouser.com",
            "com.kingroot.kinguser"
        )
        return rootApps.any { isPackageInstalled(it) }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
```

### Important caveats
| Issue | Description |
|-------|-------------|
| Magisk Hide | Hides root from detection |
| False positives | Some legitimate apps have `su` |
| Cat-and-mouse | Root tools evolve to bypass detection |
| User experience | Don't block — warn instead |

### Recommended approach
```kotlin
if (RootDetector().isDeviceRooted()) {
    // Show warning, don't block
    showSecurityWarning(
        "Your device appears to be rooted. " +
        "This may put your data at risk."
    )
    // Optionally: disable sensitive features
    // disableBiometricLogin()
    // requireAdditionalAuth()
}
```

---

## Scenario 5: Tamper Detection

### Problem
An attacker modifies your APK (injects malware, removes security checks). How do you detect this?

### Solution: APK signature verification

```kotlin
class TamperDetector(private val context: Context) {

    // 1. Verify app signature
    fun isAppTampered(): Boolean {
        val expectedSignature = "expected_sha256_signature_hash"
        val actualSignature = getAppSignature()
        return actualSignature != expectedSignature
    }

    private fun getAppSignature(): String? {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            signatures?.firstOrNull()?.let { signature ->
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signature.toByteArray())
                return digest.joinToString("") { "%02x".format(it) }
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    // 2. Verify installer source
    fun isInstalledFromPlayStore(): Boolean {
        val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstallerPackageName(context.packageName)
        }
        return installer == "com.android.vending"  // Google Play Store
    }

    // 3. Verify debug build
    fun isDebugBuild(): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}
```

### Tamper detection checklist
| Check | What it detects |
|-------|----------------|
| Signature hash | Repackaged APK with different signing key |
| Installer source | Sideloaded modified APK |
| Debug flag | Debug build in production |
| Package name | Cloned app with different package name |

### Response to tampering
```kotlin
if (tamperDetector.isAppTampered()) {
    // Option 1: Crash silently
    System.exit(0)

    // Option 2: Report to server
    api.reportTamperAttempt(deviceId)

    // Option 3: Degrade functionality
    disableSensitiveFeatures()

    // Option 4: Show warning
    showSecurityWarning("App integrity check failed")
}
```

---

## Scenario 6: Secure Deep Link / Intent Handling

### Problem
Your app receives data via deep links or intents. Malicious apps can send fake intents.

### Solution

```kotlin
// 1. Verify the sender
fun handleIntent(intent: Intent) {
    // Check if intent came from trusted source
    val callingPackage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        referrer?.host
    } else {
        intent.getStringExtra(Intent.EXTRA_REFERRER_NAME)
    }

    if (callingPackage != "com.trusted.app") {
        Log.w("Security", "Untrusted intent source: $callingPackage")
        return
    }

    // 2. Validate deep link data
    val data = intent.data
    if (data?.scheme != "myscheme" || data.host != "myhost") {
        return
    }

    // 3. Sanitize input
    val token = data.getQueryParameter("token")
    if (token.isNullOrBlank() || token.length > 256) {
        return  // Reject invalid input
    }

    // 4. Don't execute actions from unverified intents
    if (intent.action == "com.app.PAYMENT") {
        // Require additional verification
        requireUserConfirmation()
    }
}

// 2. Export only necessary components
// AndroidManifest.xml
<activity
    android:name=".PaymentActivity"
    android:exported="false">  <!-- Not accessible from other apps -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="myscheme" android:host="payment" />
    </intent-filter>
</activity>

// 3. Use PendingIntent securely
val pendingIntent = PendingIntent.getActivity(
    context,
    requestCode,
    intent,
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT  // FLAG_IMMUTABLE required on API 31+
)
```

### Intent security rules
| Rule | Why |
|------|-----|
| `android:exported="false"` | Don't expose components unless needed |
| `FLAG_IMMUTABLE` on PendingIntent | Prevent modification by other apps |
| Validate `referrer` | Verify who sent the intent |
| Sanitize all input | Prevent injection attacks |
| Don't trust deep link data | Can be spoofed by any app |

---

## 🔗 Related Topics
- [SSL Pinning](SSLPinning.md)
- [Keystore](Keystore.md)
- [Data Encryption](DataEncryption.md)
