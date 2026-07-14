# Security & Encryption

## 📖 Explanation

Android security covers data storage, network communication, authentication, and code protection.

### Key Security Areas
| Area              | Technique                                    |
|-------------------|----------------------------------------------|
| Data at rest       | EncryptedSharedPreferences, SQLCipher       |
| Data in transit    | HTTPS, certificate pinning, TLS 1.2+        |
| Authentication     | Biometric API, Keystore, JWT                 |
| Code protection    | R8/ProGuard obfuscation, root detection      |
| API keys           | NDK, BuildConfig, backend proxy              |

### Android Keystore
Hardware-backed storage for cryptographic keys. Keys never leave the keystore.

```kotlin
val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
```

### EncryptedSharedPreferences
Encrypts SharedPreferences values using AES.

```kotlin
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val prefs = EncryptedSharedPreferences.create(
    context, "secret_prefs", masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

### Certificate Pinning
Prevents MITM attacks by pinning expected server certificates.

```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.example.com", "sha256/abc123...")
    .build()
```

### Biometric Authentication
```kotlin
val prompt = BiometricPrompt(activity, executor, callback)
prompt.authenticate(promptInfo)
```

### Common Vulnerabilities
| Vulnerability         | Prevention                          |
|-----------------------|-------------------------------------|
| Hardcoded secrets     | Use BuildConfig or backend proxy    |
| Insecure storage      | EncryptedSharedPreferences           |
| Exported components    | Set `exported=false`                |
| Intent injection      | Validate incoming intents           |
| WebView vulnerabilities | Disable JS, restrict file access  |
| Rooted devices        | SafetyNet, Play Integrity API       |

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.content.Context
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

// --- Encrypted SharedPreferences ---
class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}

// --- Keystore: Encrypt/Decrypt ---
class CryptoManager {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "my_key_alias"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun getOrCreateKey(): SecretKey {
        keyStore.getKey(KEY_ALIAS, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )

        return keyGenerator.generateKey()
    }

    fun encrypt(data: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(data)
        return encrypted to cipher.iv
    }

    fun decrypt(encrypted: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        return cipher.doFinal(encrypted)
    }
}

// --- Biometric Authentication ---
class BiometricActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric)

        val canAuthenticate = BiometricManager.from(this)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPrompt()
        }
    }

    private fun showBiometricPrompt() {
        val executor = mainExecutor

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setSubtitle("Use your fingerprint to unlock")
            .setNegativeButtonText("Cancel")
            .build()

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Unlock app
            }

            override fun onAuthenticationFailed() {
                // Show retry
            }
        }

        BiometricPrompt(this, executor, callback).authenticate(promptInfo)
    }
}

// --- Certificate Pinning with OkHttp ---
class SecureNetwork {

    fun createClient(): OkHttpClient {
        val pinner = CertificatePinner.Builder()
            .add("api.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            .add("api.example.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=")
            .build()

        return OkHttpClient.Builder()
            .certificatePinner(pinner)
            .build()
    }
}

// --- BuildConfig for API keys (not in source code) ---
// build.gradle:
// defaultConfig { buildConfigField("String", "API_KEY", "\"your-api-key\"") }
// Usage: BuildConfig.API_KEY
```

```groovy
// build.gradle dependencies
dependencies {
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.biometric:biometric:1.1.0")
}
```

---

## ❓ Interview Questions

1. **How do you securely store sensitive data on Android?**
   - Use `EncryptedSharedPreferences` for key-value pairs, SQLCipher for databases, and Android Keystore for cryptographic keys. Never store secrets in plain SharedPreferences or hardcoded in source.

2. **What is the Android Keystore and why is it important?**
   - Keystore is a hardware-backed container for cryptographic keys. Keys are generated and used inside the keystore — they never enter app memory. This prevents extraction even on rooted devices.

3. **What is certificate pinning and how does it work?**
   - Pinning associates a host with its expected certificate/public key. OkHttp's `CertificatePinner` rejects connections if the server's certificate doesn't match the pinned hash. Prevents MITM attacks even if a CA is compromised.

4. **How do you implement biometric authentication in Android?**
   - Use `BiometricPrompt` (Jetpack Biometric library). Check `BiometricManager.canAuthenticate()` first, then show `BiometricPrompt` with a `PromptInfo`. Handle success/failure in the callback. Never store the biometric data yourself.

5. **How do you protect API keys in an Android app?**
   - Use `BuildConfig` fields (not in source code), store in NDK/JNI (harder to reverse), proxy through your backend (best approach), and use ProGuard/R8 obfuscation. Never commit keys to version control.

---

## 🔗 Related Topics
- [Retrofit & Networking](../intermediate/Retrofit.md)
- [Performance Optimization](Performance.md)
