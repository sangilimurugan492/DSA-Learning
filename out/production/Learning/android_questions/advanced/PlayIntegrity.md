# Play Integrity API

## Q1: What is the Play Integrity API?

Google's Play Integrity API helps verify that your app is genuine, unmodified, and running on a trusted Android device.

### What it verifies
| Check | Description |
|-------|-------------|
| App integrity | App is the official version from Google Play (not modified/repackaged) |
| Device integrity | Device is Android-certified (not rooted/emulator) |
| Account integrity | User's Google account is licensed (app licensing) |

### Replaces SafetyNet Attestation
```
SafetyNet Attestation (deprecated) → Play Integrity API (current)
```

### Use cases
- Anti-cheat in games
- Protect sensitive APIs (banking, payments)
- Prevent tampered apps from accessing your backend
- Verify app licensing

---

## Q2: How do you set up Play Integrity?

### Dependencies
```gradle
// build.gradle (app)
dependencies {
    implementation 'com.google.android.play:integrity:1.3.0'
}
```

### Google Cloud Console setup
1. Go to [Google Play Console](https://play.google.com/console)
2. Your app → Setup → App integrity
3. Enable Play Integrity API
4. Copy your **DECODING_KEY** (for client-side decryption)

### Request integrity verdict
```kotlin
class IntegrityManager(private val context: Context) {

    private val integrityManager: StandardIntegrityManager by lazy {
        StandardIntegrityManager(context)
    }

    private val nonceProvider: StandardIntegrityManager.StandardIntegrityTokenProvider by lazy {
        StandardIntegrityManager.StandardIntegrityTokenProvider(
            StandardIntegrityManager.StandardIntegrityTokenProvider.Builder(context)
                .setCloudProjectNumber(YOUR_CLOUD_PROJECT_NUMBER)
                .build()
        )
    }

    suspend fun requestIntegrityToken(nonce: String): String {
        // Prepare token request
        val tokenProvider = StandardIntegrityManager.StandardIntegrityTokenProvider
            .Builder(context)
            .setCloudProjectNumber(YOUR_CLOUD_PROJECT_NUMBER)
            .build()

        // Request token
        val token = tokenProvider.request(
            StandardIntegrityManager.StandardIntegrityTokenRequest.Builder()
                .setNonce(nonce)
                .build()
        ).token()

        return token
    }
}
```

---

## Q3: How do you request an integrity verdict?

```kotlin
class IntegrityChecker(private val context: Context) {

    private val integrityManager: StandardIntegrityManager =
        StandardIntegrityManager(context)

    suspend fun checkIntegrity(nonce: String): IntegrityResult {
        return try {
            // 1. Prepare token provider
            val tokenProvider = StandardIntegrityManager.StandardIntegrityTokenProvider
                .Builder(context)
                .setCloudProjectNumber(CLOUD_PROJECT_NUMBER)
                .build()

            // 2. Request integrity token
            val response = tokenProvider.request(
                StandardIntegrityManager.StandardIntegrityTokenRequest.Builder()
                    .setNonce(nonce)
                    .build()
            )

            // 3. Get token
            val token = response.token()

            // 4. Send token to your backend for verification
            val verdict = backendClient.verifyIntegrityToken(token)

            IntegrityResult.Success(verdict)
        } catch (e: Exception) {
            IntegrityResult.Error(e.message ?: "Integrity check failed")
        }
    }
}

sealed class IntegrityResult {
    data class Success(val verdict: IntegrityVerdict) : IntegrityResult()
    data class Error(val message: String) : IntegrityResult()
}
```

### Flow
```
App → Play Integrity API → Token → Your Backend → Google API → Verdict
```

---

## Q4: How do you verify the integrity token on the backend?

### Server-side verification (recommended)
```kotlin
// Backend (Kotlin server)
fun verifyIntegrityToken(token: String): IntegrityVerdict {
    val response = httpClient.post(
        "https://playintegrity.googleapis.com/v1/" +
        "projects/$PROJECT_NUMBER/" +
        "playIntegrity:decodeIntegrityToken"
    ) {
        header("Authorization", "Bearer $ACCESS_TOKEN")
        contentType(ContentType.Application.Json)
        body = """
            {
              "integrity_token": "$token"
            }
        """.trimIndent()
    }

    val decoded = response.body<DecodedIntegrityResponse>()

    return IntegrityVerdict(
        appIntegrity = decoded.appIntegrity,
        deviceIntegrity = decoded.deviceIntegrity,
        accountDetails = decoded.accountDetails
    )
}
```

### Decoded response structure
```json
{
  "requestPackageName": "com.example.app",
  "timestampMillis": "1700000000000",
  "appIntegrity": {
    "appRecognitionVerdict": "PLAY_RECOGNIZED",
    "packageName": "com.example.app",
    "certificateSha256": ["abc..."],
    "versionCode": "42"
  },
  "deviceIntegrity": {
    "deviceRecognitionVerdict": ["MEETS_DEVICE_INTEGRITY"]
  },
  "accountDetails": {
    "appLicensingVerdict": "LICENSED"
  }
}
```

---

## Q5: What are the integrity verdict values?

### App integrity
| Value | Meaning |
|-------|---------|
| `PLAY_RECOGNIZED` | ✅ Official app from Google Play |
| `UNRECOGNIZED_VERSION` | ⚠️ App not recognized (different version) |
| `REQUESTED_HASH_NOT_FOUND` | ❌ App hash not found |

### Device integrity
| Value | Meaning |
|-------|---------|
| `MEETS_DEVICE_INTEGRITY` | ✅ Certified Android device (not rooted) |
| `MEETS_BASIC_INTEGRITY` | ✅ Meets basic checks (may be older device) |
| `MEETS_STRONG_INTEGRITY` | ✅ Hardware-backed attestation |
| (empty) | ❌ Rooted/emulator/modified |

### Account integrity (licensing)
| Value | Meaning |
|-------|---------|
| `LICENSED` | ✅ User has valid license (purchased app) |
| `UNLICENSED` | ❌ User doesn't have license |
| `UNEVALUATED` | ⚠️ Not checked |

### Decision matrix
```
App: PLAY_RECOGNIZED + Device: MEETS_DEVICE_INTEGRITY → ✅ Allow
App: PLAY_RECOGNIZED + Device: (empty)               → ❌ Block (rooted)
App: UNRECOGNIZED_VERSION + Device: MEETS_DEVICE_INTEGRITY → ⚠️ Check version
App: PLAY_RECOGNIZED + Account: UNLICENSED           → ❌ Block (pirated)
```

---

## Q6: How do you handle integrity check failures?

```kotlin
class SecurityGate(private val integrityChecker: IntegrityChecker) {

    suspend fun verifyAccess(): AccessResult {
        return when (val result = integrityChecker.checkIntegrity(generateNonce())) {
            is IntegrityResult.Success -> {
                val verdict = result.verdict

                when {
                    // App is modified/repackaged
                    verdict.appIntegrity != "PLAY_RECOGNIZED" -> {
                        AccessResult.Denied("App integrity check failed")
                    }

                    // Device is rooted/emulator
                    verdict.deviceIntegrity.isEmpty() -> {
                        AccessResult.Denied("Device integrity check failed")
                    }

                    // App is pirated
                    verdict.accountDetails?.appLicensingVerdict == "UNLICENSED" -> {
                        AccessResult.Denied("App not licensed")
                    }

                    // All checks passed
                    else -> AccessResult.Allowed
                }
            }

            is IntegrityResult.Error -> {
                // Don't block on network errors — use fallback
                AccessResult.Retry(result.message)
            }
        }
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP)
    }
}

sealed class AccessResult {
    object Allowed : AccessResult()
    data class Denied(val reason: String) : AccessResult()
    data class Retry(val message: String) : AccessResult()
}
```

### Failure handling strategy
| Failure | Action |
|---------|--------|
| App integrity fail | Block access |
| Device integrity fail | Block or degrade features |
| Licensing fail | Block premium features |
| Network error | Retry with backoff |
| Rate limited | Cache last result temporarily |

---

## Q7: How do you use standard vs classic requests?

### Standard request (recommended)
```kotlin
// Standard — uses Google Play services
// No need for nonce management
val tokenProvider = StandardIntegrityManager.StandardIntegrityTokenProvider
    .Builder(context)
    .setCloudProjectNumber(CLOUD_PROJECT_NUMBER)
    .build()

val response = tokenProvider.request(
    StandardIntegrityManager.StandardIntegrityTokenRequest.Builder()
        .setNonce(nonce)
        .build()
)
```

### Classic request (legacy)
```kotlin
// Classic — uses app's signing key
val integrityManager = IntegrityManagerFactory.create(context)
val tokenRequest = IntegrityTokenRequest.builder()
    .setNonce(nonce)
    .build()

integrityManager.requestIntegrityToken(tokenRequest)
    .addOnSuccessListener { response ->
        val token = response.token()
        // Send to backend
    }
    .addOnFailureListener { error ->
        // Handle error
    }
```

### Standard vs Classic
| Feature | Standard | Classic |
|---------|----------|---------|
| Nonce | Required | Required |
| Cloud project | Required | Not required |
| Decoding | Server-side | Server-side |
| Status | ✅ Recommended | ⚠️ Legacy |
| Performance | Faster | Slower |

---

## Q8: How do you optimize Play Integrity calls?

### Problem
Play Integrity API has rate limits (~10,000 calls/day per app). Calling on every request is not feasible.

### Solution: Cache + lazy verification

```kotlin
class IntegrityCache(
    private val checker: IntegrityChecker,
    private val prefs: EncryptedSharedPreferences
) {
    companion object {
        private const val CACHE_KEY = "integrity_verdict"
        private const val CACHE_DURATION = 24 * 60 * 60 * 1000L  // 24 hours
    }

    suspend fun verifyIntegrity(forceRefresh: Boolean = false): Boolean {
        // Check cache first
        if (!forceRefresh) {
            val cached = getCachedVerdict()
            if (cached != null && !isExpired(cached.timestamp)) {
                return cached.passed
            }
        }

        // Call Play Integrity API
        return try {
            val result = checker.checkIntegrity(generateNonce())
            val passed = (result is IntegrityResult.Success && result.verdict.isValid())
            cacheVerdict(passed)
            passed
        } catch (e: Exception) {
            // On error, use cached result or allow
            getCachedVerdict()?.passed ?: true
        }
    }

    private fun getCachedVerdict(): CachedVerdict? {
        val json = prefs.getString(CACHE_KEY, null) ?: return null
        return Gson().fromJson(json, CachedVerdict::class.java)
    }

    private fun cacheVerdict(passed: Boolean) {
        val cached = CachedVerdict(passed, System.currentTimeMillis())
        prefs.edit().putString(CACHE_KEY, Gson().toJson(cached)).apply()
    }

    private fun isExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > CACHE_DURATION
    }
}

data class CachedVerdict(val passed: Boolean, val timestamp: Long)
```

### Optimization strategies
| Strategy | Description |
|----------|-------------|
| Cache verdict | Store result for 24h |
| Lazy verification | Only check on sensitive actions |
| Batch requests | Check once per session |
| Fallback on error | Don't block on API failure |
| Background check | Verify in background, not on critical path |

---

## Q9: How do you test Play Integrity?

### Testing in debug builds
```kotlin
class DebugIntegrityChecker : IntegrityChecker {
    // Bypass integrity check in debug
    override suspend fun checkIntegrity(nonce: String): IntegrityResult {
        return if (BuildConfig.DEBUG) {
            IntegrityResult.Success(IntegrityVerdict(
                appIntegrity = "PLAY_RECOGNIZED",
                deviceIntegrity = listOf("MEETS_DEVICE_INTEGRITY"),
                accountDetails = null
            ))
        } else {
            realChecker.checkIntegrity(nonce)
        }
    }
}
```

### Google Play Console testing
1. Play Console → Your app → Setup → App integrity
2. Test → Add testers by email
3. Testers get real integrity tokens
4. Use testing responses for different scenarios

### Test scenarios
| Scenario | How to test |
|----------|------------|
| Genuine app + device | Run on real device from Play Store |
| Modified app | Modify APK, sideload |
| Rooted device | Run on rooted device |
| Emulator | Run on Android emulator |
| Pirated app | Sideload without Play Store |

---

## Q10: What are Play Integrity best practices?

### Do's
- ✅ Verify token on **backend** (not client)
- ✅ Use **nonce** to prevent replay attacks
- ✅ Cache results to avoid rate limits
- ✅ Handle errors gracefully (don't block on network errors)
- ✅ Check only on **sensitive actions** (login, payment)
- ✅ Use standard API (not classic)

### Don'ts
- ❌ Don't verify token on client (can be bypassed)
- ❌ Don't call on every API request (rate limits)
- ❌ Don't block app on integrity failure (degrade instead)
- ❌ Don't reuse nonces (generate fresh each time)
- ❌ Don't rely solely on Play Integrity (use defense in depth)

### Nonce best practices
```kotlin
// ✅ Good — generate fresh nonce each time
fun generateNonce(): String {
    val bytes = ByteArray(16)
    SecureRandom().nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP)
}

// ❌ Bad — hardcoded nonce
val nonce = "fixed-nonce"  // Replay attack possible

// ✅ Good — tie nonce to request
val nonce = generateNonce()
// Send nonce + request to backend
// Backend verifies nonce matches in decoded token
```

### Defense in depth
```
Layer 1: Play Integrity (app + device verification)
Layer 2: SSL Pinning (network security)
Layer 3: API key protection (backend proxy)
Layer 4: R8/ProGuard (code obfuscation)
Layer 5: Root detection (device security)
```

---

## 🔗 Related Topics
- [Security](Security.md)
- [Testing](Testing.md)
- [CICD](CICD.md)
