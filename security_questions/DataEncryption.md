# Data Encryption

## Q1: What are symmetric and asymmetric encryption?

### Symmetric (same key for encrypt/decrypt)
```
Plaintext → [AES + Key] → Ciphertext → [AES + Key] → Plaintext
```

| Algorithm | Key Size | Use Case |
|-----------|----------|----------|
| AES-256-GCM | 256-bit | Data encryption (recommended) |
| AES-256-CBC | 256-bit | Legacy (no authentication) |
| ChaCha20 | 256-bit | Mobile (no hardware AES) |

### Asymmetric (public/private key pair)
```
Plaintext → [RSA + Public Key] → Ciphertext → [RSA + Private Key] → Plaintext
```

| Algorithm | Key Size | Use Case |
|-----------|----------|----------|
| RSA-2048 | 2048-bit | Signatures, key exchange |
| ECDSA-P256 | 256-bit | Signatures (smaller, faster) |
| ECDH | 256-bit | Key agreement |

### When to use which
| Scenario | Use |
|----------|-----|
| Encrypt user data | AES-256-GCM (symmetric) |
| Sign data | RSA or ECDSA (asymmetric) |
| Exchange AES key | RSA or ECDH (asymmetric) |
| Encrypt large data | AES (symmetric) |
| Verify app integrity | RSA/ECDSA signature |

---

## Q2: How do you encrypt data with AES-GCM?

```kotlin
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

class AesGcmCrypto {

    companion object {
        private const val AES_KEY_SIZE = 256  // bits
        private const val GCM_TAG_LENGTH = 128  // bits
        private const val IV_LENGTH = 12  // bytes
    }

    // Generate a random AES key
    fun generateKey(): ByteArray {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(AES_KEY_SIZE)
        return keyGenerator.generateKey().encoded
    }

    // Encrypt
    fun encrypt(plaintext: ByteArray, key: ByteArray): ByteArray {
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val params = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, params)

        val encrypted = cipher.doFinal(plaintext)
        // Prepend IV: [IV (12 bytes)] [Ciphertext + Auth Tag]
        return iv + encrypted
    }

    // Decrypt
    fun decrypt(ciphertext: ByteArray, key: ByteArray): ByteArray {
        val iv = ciphertext.copyOfRange(0, IV_LENGTH)
        val encrypted = ciphertext.copyOfRange(IV_LENGTH, ciphertext.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val params = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, params)

        return cipher.doFinal(encrypted)
    }
}
```

### Why GCM?
| Mode | Authenticated? | Parallel? | Recommended? |
|------|---------------|-----------|-------------|
| ECB | ❌ No | ✅ Yes | ❌ Never use |
| CBC | ❌ No | ❌ No | ⚠️ Legacy only |
| GCM | ✅ Yes | ✅ Yes | ✅ Recommended |
| CTR | ❌ No | ✅ Yes | ⚠️ With HMAC |

GCM provides **authenticated encryption** — detects tampering.

---

## Q3: How do you use RSA for encryption/signing?

```kotlin
import java.security.KeyPairGenerator
import java.security.Signature
import javax.crypto.Cipher

class RsaCrypto {

    companion object {
        private const val RSA_KEY_SIZE = 2048
    }

    // Generate RSA key pair
    fun generateKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(RSA_KEY_SIZE)
        return generator.generateKeyPair()
    }

    // Encrypt with public key
    fun encrypt(plaintext: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(plaintext)
    }

    // Decrypt with private key
    fun decrypt(ciphertext: ByteArray, privateKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(ciphertext)
    }

    // Sign with private key
    fun sign(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    // Verify with public key
    fun verify(data: ByteArray, signatureBytes: ByteArray, publicKey: PublicKey): Boolean {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureBytes)
    }
}
```

### RSA padding
| Padding | Secure? | Use Case |
|---------|---------|---------|
| PKCS1 | ⚠️ Vulnerable to padding oracle | Legacy |
| OAEP | ✅ Secure | Encryption |
| PSS | ✅ Secure | Signatures |

### RSA limitations
- Max plaintext size: `keySize/8 - padding` (e.g., 2048-bit RSA → ~190 bytes)
- Don't encrypt large data with RSA — use **hybrid encryption** instead

---

## Q4: What is hybrid encryption?

Use RSA to encrypt an AES key, then use AES to encrypt data.

```
1. Generate random AES key
2. Encrypt data with AES key
3. Encrypt AES key with RSA public key
4. Send: [encrypted AES key] + [encrypted data]

Receiver:
1. Decrypt AES key with RSA private key
2. Decrypt data with AES key
```

```kotlin
fun hybridEncrypt(data: ByteArray, publicKey: PublicKey): HybridEncrypted {
    // 1. Generate random AES key
    val aesKey = AesGcmCrypto().generateKey()

    // 2. Encrypt data with AES
    val encryptedData = AesGcmCrypto().encrypt(data, aesKey)

    // 3. Encrypt AES key with RSA
    val encryptedKey = RsaCrypto().encrypt(aesKey, publicKey)

    return HybridEncrypted(encryptedKey, encryptedData)
}

fun hybridDecrypt(encrypted: HybridEncrypted, privateKey: PrivateKey): ByteArray {
    // 1. Decrypt AES key with RSA
    val aesKey = RsaCrypto().decrypt(encrypted.encryptedKey, privateKey)

    // 2. Decrypt data with AES
    return AesGcmCrypto().decrypt(encrypted.encryptedData, aesKey)
}
```

### Why hybrid?
| Approach | Pros | Cons |
|----------|------|------|
| RSA only | Simple | Max ~190 bytes |
| AES only | Fast, any size | Key exchange problem |
| Hybrid | Fast + secure key exchange | More complex |

---

## Q5: How do you store secrets securely?

### ❌ Bad approaches
```kotlin
// 1. Hardcoded in code
val API_KEY = "sk_live_abc123"  // Extractable from APK

// 2. In BuildConfig
BuildConfig.API_KEY  // Extractable from APK

// 3. In strings.xml
<string name="api_key">sk_live_abc123</string>  // Extractable from APK

// 4. In SharedPreferences (plaintext)
prefs.edit().putString("token", token).apply()  // Readable by root
```

### ✅ Good approaches

```kotlin
// 1. EncryptedSharedPreferences (for tokens)
val prefs = EncryptedSharedPreferences.create(...)
prefs.edit().putString("auth_token", token).apply()

// 2. Keystore + AES (for sensitive data)
val encrypted = keystoreCrypto.encrypt(data.toByteArray())
// Store encrypted bytes in file/prefs

// 3. NDK/C++ (for API keys — harder to extract)
// Store key in C++ file, access via JNI
// app/src/main/cpp/secrets.cpp
extern "C" JNIEXPORT jstring JNICALL
Java_com_app_Secrets_getApiKey(JNIEnv* env, jobject) {
    return env->NewStringUTF("sk_live_abc123");
}

// 4. Backend proxy (best for API keys)
// App → Your backend → Third-party API
// API key never on device
```

### Secret storage comparison
| Method | Security | Effort | Best For |
|--------|---------|--------|---------|
| Hardcoded | ❌ Low | Low | Nothing |
| BuildConfig | ❌ Low | Low | Nothing |
| EncryptedSharedPreferences | ✅ Medium | Low | Auth tokens |
| Keystore | ✅ High | Medium | User data |
| NDK/C++ | ✅ Medium-High | High | API keys |
| Backend proxy | ✅ Highest | High | API keys |

---

## Q6: How do you use R8/ProGuard for code obfuscation?

```gradle
// build.gradle (app)
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### ProGuard rules
```proguard
# proguard-rules.pro

# Keep model classes (needed for Gson/Moshi)
-keep class com.example.app.model.** { *; }

# Keep Retrofit service interfaces
-keepattributes Signature, Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep Room entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep enum values (for serialization)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
```

### What R8 does
| Feature | Description |
|---------|-------------|
| Obfuscation | Rename classes/methods to short names |
| Optimization | Remove unused code, inline methods |
| Shrinking | Remove unused classes/fields |
| Logging removal | Strip `Log.d()` calls in release |

### What R8 does NOT do
- ❌ Doesn't encrypt strings (use DexGuard for that)
- ❌ Doesn't prevent decompilation entirely
- ❌ Doesn't hide native code (.so files)

---

## Q7: How do you hash data securely?

```kotlin
import java.security.MessageDigest

// SHA-256 hashing (for integrity, not passwords)
fun sha256(data: ByteArray): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(data)
}

fun sha256Hex(data: ByteArray): String {
    return sha256(data).joinToString("") { "%02x".format(it) }
}

// HMAC (for API request signing)
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun hmacSha256(data: ByteArray, key: ByteArray): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    val keySpec = SecretKeySpec(key, "HmacSHA256")
    mac.init(keySpec)
    return mac.doFinal(data)
}

// PBKDF2 (for password hashing)
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

fun hashPassword(password: String, salt: ByteArray): ByteArray {
    val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    return factory.generateSecret(spec).encoded
}
```

### Hashing use cases
| Algorithm | Use Case | Secure? |
|-----------|----------|---------|
| SHA-256 | Data integrity, checksums | ✅ Yes |
| HMAC-SHA256 | API request signing | ✅ Yes |
| PBKDF2 | Password hashing | ✅ Yes (with salt) |
| MD5 | Legacy | ❌ No (collisions) |
| SHA-1 | Legacy | ❌ No (collisions) |

### Password hashing rules
- ✅ Always use a **salt** (random per user)
- ✅ Use **slow** algorithm (PBKDF2, bcrypt, Argon2)
- ✅ Use **high iteration count** (10,000+)
- ❌ Never use plain SHA-256 for passwords (too fast → brute-forceable)

---

## 🔗 Related Topics
- [SSL Pinning](SSLPinning.md)
- [Keystore](Keystore.md)
- [Security Scenarios](SecurityScenarios.md)
