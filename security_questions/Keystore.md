# Android Keystore

## Q1: What is the Android Keystore?

A secure hardware-backed container for cryptographic keys. Keys are stored in hardware (TEE/StrongBox) and never leave it.

### Key properties
| Property | Description |
|----------|-------------|
| Hardware-backed | Keys in TEE or StrongBox (not extractable) |
| Key non-exportable | Private keys never leave Keystore |
| User authentication | Require biometric/PIN to use key |
| Key attestation | Verify key is hardware-backed |
| Alias-based | Each key has a unique alias |

### Keystore vs plain storage
| Approach | Key Extractable? | Hardware-backed? |
|----------|-----------------|-----------------|
| SharedPreferences | ✅ Yes (plaintext) | ❌ No |
| EncryptedSharedPreferences | ❌ No (encrypted) | ❌ No (software) |
| Android Keystore | ❌ No | ✅ Yes (TEE/StrongBox) |

---

## Q2: How do you generate a key in the Keystore?

```kotlin
fun generateSecretKey(alias: String): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        "AndroidKeyStore"
    )

    val spec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .setUserAuthenticationRequired(false)  // Set true for biometric-gated
        .setRandomizedEncryptionRequired(true)
        .build()

    keyGenerator.init(spec)
    return keyGenerator.generateKey()
}

fun getKey(alias: String): SecretKey? {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    return (keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry)?.secretKey
}
```

### Key generation options
| Option | Description |
|--------|-------------|
| `PURPOSE_ENCRYPT` | Key can encrypt |
| `PURPOSE_SIGN` | Key can sign data |
| `setUserAuthenticationRequired` | Require biometric to use key |
| `setUserAuthenticationValidityDurationSeconds` | Key valid for N seconds after auth |
| `setKeySize(256)` | AES-256 |
| `setBlockModes(GCM)` | Authenticated encryption |
| `setInvalidatedByBiometricEnrollment` | Key invalidates if biometrics change |

---

## Q3: How do you encrypt and decrypt with Keystore?

```kotlin
class KeystoreCrypto {

    companion object {
        private const val ALIAS = "my_secret_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val GCM_IV_LENGTH = 12  // bytes
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        keyStore.getKey(ALIAS, null)?.let { return it as SecretKey }

        return generateKey()
    }

    private fun generateKey(): SecretKey {
        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )
        generator.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    fun encrypt(plaintext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext)
        // Prepend IV to ciphertext
        return iv + encrypted
    }

    fun decrypt(ciphertext: ByteArray): ByteArray {
        val iv = ciphertext.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = ciphertext.copyOfRange(GCM_IV_LENGTH, ciphertext.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val params = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), params)
        return cipher.doFinal(encrypted)
    }
}
```

### Usage
```kotlin
val crypto = KeystoreCrypto()
val encrypted = crypto.encrypt("Sensitive data".toByteArray())
val decrypted = String(crypto.decrypt(encrypted))
// "Sensitive data"
```

---

## Q4: How do you use EncryptedSharedPreferences?

```gradle
// build.gradle
dependencies {
    implementation "androidx.security:security-crypto:1.1.0-alpha06"
}
```

```kotlin
class SecurePrefs(context: Context) {

    private val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_prefs",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}
```

### When to use what
| Storage | Use Case | Security Level |
|---------|----------|---------------|
| SharedPreferences | Non-sensitive data | ❌ Plaintext |
| EncryptedSharedPreferences | Tokens, small secrets | ✅ Encrypted (software) |
| Keystore + Cipher | Large data, custom crypto | ✅ Hardware-backed |
| Keystore + Biometric | Sensitive operations | ✅ Hardware + biometric |

---

## Q5: How do you use BiometricPrompt with Keystore?

```kotlin
class BiometricCrypto(activity: FragmentActivity) {

    companion object {
        private const val ALIAS = "biometric_key"
    }

    private lateinit var cipher: Cipher

    fun encryptWithBiometric(data: ByteArray) {
        val key = getOrCreateKey()
        cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val cryptoObject = BiometricPrompt.CryptoObject(cipher)
                    val encrypted = cipher.doFinal(data)
                    // Save encrypted data
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // Handle error
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Encrypt Data")
            .setSubtitle("Authenticate to encrypt sensitive data")
            .setNegativeButtonText("Cancel")
            .build()

        prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        keyStore.getKey(ALIAS, null)?.let { return it as SecretKey }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        generator.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(true)  // ← Require biometric
                .setInvalidatedByBiometricEnrollment(true)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }
}
```

### BiometricPrompt requirements
| Requirement | Description |
|-------------|-------------|
| `setUserAuthenticationRequired(true)` | Key can only be used after biometric auth |
| `setInvalidatedByBiometricEnrollment(true)` | Key invalidated if new fingerprint added |
| `CryptoObject` | Links cipher to biometric auth |
| `FragmentActivity` | Required for BiometricPrompt |

---

## Q6: How do you generate and use an asymmetric key pair?

```kotlin
class AsymmetricCrypto {

    companion object {
        private const val ALIAS = "signing_key"
    }

    // Generate RSA key pair in Keystore
    fun generateKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
        )

        val spec = KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            .setKeySize(2048)
            .build()

        generator.initialize(spec)
        return generator.generateKeyPair()
    }

    // Sign data with private key
    fun sign(data: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val privateKey = keyStore.getKey(ALIAS, null) as PrivateKey

        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    // Verify signature with public key
    fun verify(data: ByteArray, signatureBytes: ByteArray): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val publicKey = keyStore.getCertificate(ALIAS).publicKey

        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureBytes)
    }
}
```

### Symmetric vs Asymmetric
| Feature | AES (Symmetric) | RSA (Asymmetric) |
|---------|----------------|------------------|
| Key type | Same key for encrypt/decrypt | Public + private key pair |
| Speed | Fast | Slower |
| Use case | Encrypt data | Sign/verify, key exchange |
| Key size | 256-bit | 2048-bit |
| Best for | Large data | Small data, signatures |

---

## Q7: What are Keystore best practices?

### Do's
- ✅ Use `AndroidKeyStore` provider (hardware-backed)
- ✅ Set `setUserAuthenticationRequired(true)` for sensitive keys
- ✅ Use GCM mode (authenticated encryption)
- ✅ Set `setInvalidatedByBiometricEnrollment(true)`
- ✅ Use `setRandomizedEncryptionRequired(true)`
- ✅ Delete keys when user logs out

### Don'ts
- ❌ Don't store keys in SharedPreferences
- ❌ Don't hardcode keys in code
- ❌ Don't use ECB mode (insecure)
- ❌ Don't reuse IVs with same key
- ❌ Don't keep keys longer than needed

### Key management
```kotlin
// Delete a key
fun deleteKey(alias: String) {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    keyStore.deleteEntry(alias)
}

// Check if key exists
fun keyExists(alias: String): Boolean {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    return keyStore.containsAlias(alias)
}

// List all keys
fun listKeys(): List<String> {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    return keyStore.aliases().toList()
}
```

### On user logout
```kotlin
fun onLogout() {
    // Delete all sensitive keys
    deleteKey("auth_token_key")
    deleteKey("biometric_key")
    // Clear encrypted prefs
    securePrefs.clear()
}
```

---

## 🔗 Related Topics
- [SSL Pinning](SSLPinning.md)
- [Data Encryption](DataEncryption.md)
- [Security Scenarios](SecurityScenarios.md)
