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

6. **What is the difference between symmetric and asymmetric encryption and when do you use each?**
   - **Symmetric encryption** (AES) uses the same key for encryption and decryption. Fast, efficient for large data. Use case: encrypting local database, SharedPreferences, file content. Key must be stored securely in the Android Keystore. **Asymmetric encryption** (RSA, ECDSA) uses a public key to encrypt and a private key to decrypt. Slower but solves the key distribution problem. Use case: TLS/HTTPS (server has private key, client has public key), digital signatures, key exchange. In Android: use AES (via Keystore) for local data encryption, rely on TLS for network encryption. For hybrid encryption: use RSA to encrypt an AES key, then use AES for the actual data. Android Keystore supports both RSA and AES key generation and storage in hardware-backed storage.

7. **What is SQL injection and how do you prevent it in Android?**
   - SQL injection occurs when user input is concatenated directly into SQL queries, allowing attackers to execute arbitrary SQL. Prevention in Android: (1) **Room** — uses parameterized queries by default: `@Query("SELECT * FROM users WHERE id = :id")` — Room escapes the `:id` parameter, preventing injection. (2) **SQLiteOpenHelper** — use `selectionArgs` instead of string concatenation: `db.query("users", null, "id = ?", arrayOf(userId), null, null, null)`. Never do `"id = " + userId`. (3) **ContentProvider** — use `selectionArgs` in `query()`. (4) **Validate input** — sanitize and validate all user input before using in queries. Room is the best defense — it makes parameterized queries the default. If using raw SQLite, always use `?` placeholders with `selectionArgs`. Also avoid `execSQL()` with user input — use `compileStatement()` with bindings.

8. **What is SSL Pinning and what are the different types?**
   - SSL Pinning prevents Man-in-the-Middle (MITM) attacks by verifying that the server's certificate matches a known, pre-pinned value. Three types: (1) **Certificate Pinning** — pin the entire X.509 certificate. Breaks when the certificate is renewed (every 1-2 years). Not recommended. (2) **Public Key Pinning** — pin the certificate's public key (SPKI). More flexible — survives certificate renewal as long as the key pair stays the same. (3) **Hash Pinning** — pin the SHA-256 hash of the public key (SPKI hash). Most common — `sha256/Base64EncodedHash=`. Use OkHttp's `CertificatePinner`: `CertificatePinner.Builder().add("api.example.com", "sha256/abc123=").build()`. To get the hash: `echo | openssl s_client -connect api.example.com:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64`. Always pin with a backup key for key rotation.

9. **What is Play Integrity API and how does it replace SafetyNet?**
   - **Play Integrity API** (replaces deprecated SafetyNet Attestation API) verifies that your app is genuine (not tampered), running on a genuine Android device (not emulator/rooted), and from a trusted source (Google Play). Steps: (1) Call `IntegrityManager.requestIntegrityToken(IntegrityTokenRequest)`. (2) Send the token to your server. (3) Server decrypts and verifies the token with Google Play. (4) Check the verdict: `appIntegrity` (PLAY_INTEGRITY/UNRECOGNIZED), `deviceIntegrity` (MEETS_DEVICE_INTEGRITY/MEETS_STRONG_INTEGRITY), `accountDetails` (LICENSED). Use cases: anti-cheat in games, protecting premium content, preventing API abuse. Unlike SafetyNet, Play Integrity is free and more efficient. Protects against: modified apps, rooted devices, emulators, and unauthorized distribution. Configure in Google Play Console.

10. **How do you securely implement user authentication in Android?**
    - (1) **Biometric authentication** — use `BiometricPrompt` for fingerprint/face unlock. Check `BiometricManager.canAuthenticate(BIOMETRIC_STRONG)` first. Never store biometric data — the system handles it. (2) **Token-based auth** — after login, store the JWT/access token in `EncryptedSharedPreferences`. Use an `Authenticator` to auto-refresh expired tokens. (3) **Session management** — use short-lived access tokens (15-30 min) and long-lived refresh tokens (days). Store refresh token in `EncryptedSharedPreferences`. (4) **Credential Manager** (new API) — integrates with password managers, passkeys, and Google Sign-In. (5) **Auto-fill** — integrate with Android AutoFill framework. (6) **Lock screen** — use `KeyguardManager` to require device unlock before showing sensitive data. (7) **Re-authentication** — require biometric before sensitive actions (payments, deleting data). Never hardcode credentials or store passwords in plaintext.

11. **How do you secure data in transit (network security)?**
    - (1) **HTTPS only** — use `android:usesCleartextTraffic="false"` in the manifest (Android 9+ defaults to false). (2) **Network Security Config** — define a `network_security_config.xml` that specifies trusted CAs, domain-specific rules, and certificate pinning. (3) **Certificate pinning** — pin server public keys with OkHttp `CertificatePinner`. (4) **TLS 1.2+** — enforce minimum TLS version: `<ssl-config><tls-version>TLSv1.2</tls-version></ssl-config>`. (5) **Certificate transparency** — verify certificates against CT logs. (6) **API key rotation** — rotate API keys regularly. (7) **Request signing** — sign each request with an HMAC using a secret key stored in the Keystore. (8) **Certificate Transparency** — verify the server's certificate is logged in a CT log. Always test with tools like Burp Suite to verify MITM protection works.

12. **What are common Android security vulnerabilities and how do you prevent them?**
    - (1) **Insecure data storage** — plaintext SharedPreferences/SQLite. Fix: `EncryptedSharedPreferences`, SQLCipher, Keystore. (2) **Insecure communication** — HTTP, no certificate pinning. Fix: HTTPS + pinning + Network Security Config. (3) **Insecure components** — exported activities/services without permission checks. Fix: set `android:exported="false"`, validate intents, use custom permissions. (4) **Intent injection** — attackers craft malicious intents. Fix: validate all incoming intent data, use `PendingIntent` with `FLAG_IMMUTABLE`. (5) **WebView vulnerabilities** — JavaScript enabled, file access. Fix: disable JavaScript if not needed, restrict file access, use `Safe Browsing`. (6) **Hardcoded secrets** — API keys in source. Fix: `BuildConfig`, NDK, backend proxy. (7) **Root detection bypass** — use Play Integrity API, multiple root detection methods. (8) **Tapjacking** — overlay attacks. Fix: `android:filterTouchesWhenObscured="true"`. (9) **Deep link hijacking** — fix: use App Links with `autoVerify="true"`. Use OWASP MASVS checklist for comprehensive security audits.

---

## 🔗 Related Topics
- [Retrofit & Networking](../intermediate/Retrofit.md)
- [Performance Optimization](Performance.md)
