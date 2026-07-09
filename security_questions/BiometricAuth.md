# Biometric Authentication

## Q1: What is Biometric Authentication in Android?

Biometric authentication uses fingerprint, face, or iris recognition to verify user identity.

### Setup
```gradle
dependencies {
    implementation 'androidx.biometric:biometric:1.1.0'
}
```

### Biometric types
| Type | API | Description |
|------|-----|-------------|
| Fingerprint | API 23+ | Most common |
| Face | API 29+ | Pixel, Samsung |
| Iris | API 29+ | Samsung only |
| Class 3 | All | Strong biometric (can unlock keystore) |
| Class 2 | All | Weak biometric (app unlock only) |
| Class 1 | All | Very weak (not recommended) |

### BiometricManager
```kotlin
val biometricManager = BiometricManager.from(context)
when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
    BiometricManager.BIOMETRIC_SUCCESS -> // Can authenticate
    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> // No biometric hardware
    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> // Hardware unavailable
    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> // No biometric enrolled
    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> // Security update needed
}
```

---

## Q2: How do you show biometric prompt?

```kotlin
class BiometricAuth(private val activity: FragmentActivity) {

    fun authenticate(
        title: String = "Authenticate",
        subtitle: String = "Use your fingerprint to continue",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                // Called on each failed attempt (fingerprint not recognized)
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Cancel")  // For BIOMETRIC only
            // .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        prompt.authenticate(info)
    }
}

// Usage
biometricAuth.authenticate(
    title = "Unlock App",
    onSuccess = { /* Navigate to home */ },
    onError = { error -> showToast(error) }
)
```

---

## Q3: How do you use biometric with cryptographic operations?

```kotlin
class BiometricCrypto(private val context: Context) {

    // Create crypto key tied to biometric
    fun createSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val spec = KeyGenParameterSpec.Builder(
            "biometric_key",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)  // Require biometric
            .setInvalidatedByBiometricEnrollment(true)  // Invalidate on new fingerprint
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    // Encrypt with biometric
    fun encryptWithBiometric(
        data: String,
        activity: FragmentActivity,
        onResult: (String) -> Unit
    ) {
        val cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
            KeyProperties.BLOCK_MODE_CBC + "/" +
            KeyProperties.ENCRYPTION_PADDING_PKCS7
        )

        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val cryptoObject = BiometricPrompt.CryptoObject(cipher)

        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val encryptedCipher = result.cryptoObject?.cipher!!
                    val encrypted = encryptedCipher.doFinal(data.toByteArray())
                    onResult(Base64.encodeToString(encrypted, Base64.DEFAULT))
                }
            })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Encrypt Data")
            .setNegativeButtonText("Cancel")
            .build()

        prompt.authenticate(info, cryptoObject)
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey("biometric_key", null) as SecretKey
    }
}
```

---

## Q4: How do you handle fallback authentication?

```kotlin
class BiometricAuth(private val activity: FragmentActivity) {

    fun authenticateWithFallback(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val biometricManager = BiometricManager.from(activity)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt(onSuccess, onError)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showEnrollBiometricDialog()
            }
            else -> {
                showPinFallback(onSuccess)
            }
        }
    }

    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            showPinFallback(onSuccess)  // User chose PIN
                        }
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            onError("Authentication canceled")
                        }
                        else -> onError(errString.toString())
                    }
                }
            })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setSubtitle("Use biometric or PIN")
            .setNegativeButtonText("Use PIN")  // Fallback to PIN
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
            .build()

        prompt.authenticate(info)
    }

    private fun showEnrollBiometricDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Set Up Biometric")
            .setMessage("No biometric enrolled. Set up fingerprint?")
            .setPositiveButton("Set Up") { _, _ ->
                val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG)
                }
                activity.startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPinFallback(onSuccess: () -> Unit) {
        // Show PIN screen
        // onSuccess() when PIN verified
    }
}
```

---

## Q5: How do you check biometric availability?

```kotlin
class BiometricChecker(context: Context) {

    private val biometricManager = BiometricManager.from(context)

    fun canAuthenticate(): Boolean {
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun getBiometricStatus(): BiometricStatus {
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NotEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricStatus.SecurityUpdateRequired
            else -> BiometricStatus.Unknown
        }
    }
}

sealed class BiometricStatus {
    object Available : BiometricStatus()
    object NoHardware : BiometricStatus()
    object HardwareUnavailable : BiometricStatus()
    object NotEnrolled : BiometricStatus()
    object SecurityUpdateRequired : BiometricStatus()
    object Unknown : BiometricStatus()
}
```

---

## Q6: How do you use biometric in Compose?

```kotlin
@Composable
fun BiometricButton(
    activity: FragmentActivity,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    val context = LocalContext.current
    val biometricManager = remember { BiometricManager.from(context) }

    val canAuthenticate = remember {
        biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    if (canAuthenticate) {
        Button(
            onClick = {
                val executor = ContextCompat.getMainExecutor(context)
                val prompt = BiometricPrompt(activity, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            onAuthSuccess()
                        }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            onAuthError(errString.toString())
                        }
                    })

                val info = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Authenticate")
                    .setSubtitle("Use your fingerprint")
                    .setNegativeButtonText("Cancel")
                    .build()

                prompt.authenticate(info)
            }
        ) {
            Text("Unlock with Biometric")
        }
    } else {
        Text("Biometric not available")
    }
}
```

---

## Q7: What are biometric authenticator classes?

| Class | Description | Can Unlock Keystore | Use Case |
|-------|-------------|-------------------|----------|
| Class 3 (BIOMETRIC_STRONG) | Strong biometric | ✅ Yes | Payments, sensitive data |
| Class 2 (BIOMETRIC_WEAK) | Weak biometric | ❌ No | App unlock |
| DEVICE_CREDENTIAL | PIN/Pattern/Password | ✅ Yes | Fallback |

### Using multiple authenticators
```kotlin
val info = BiometricPrompt.PromptInfo.Builder()
    .setTitle("Authenticate")
    .setAllowedAuthenticators(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )
    // No negative button when DEVICE_CREDENTIAL is used
    .build()
```

---

## Q8: How do you handle biometric lifecycle?

```kotlin
class BiometricViewModel : ViewModel() {

    private var isAuthenticating = false

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isAuthenticating) return  // Prevent multiple prompts

        isAuthenticating = true

        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    isAuthenticating = false
                    onSuccess()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    isAuthenticating = false
                    onError(errString.toString())
                }
                override fun onAuthenticationFailed() {
                    // Don't dismiss — user can try again
                }
            })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setNegativeButtonText("Cancel")
            .build()

        prompt.authenticate(info)
    }
}
```

### Lifecycle considerations
| Issue | Solution |
|-------|---------|
| Prompt on background | Check activity is resumed |
| Multiple prompts | Track `isAuthenticating` flag |
| Config change | Use `FragmentActivity` (survives rotation) |
| App backgrounded | Cancel prompt in `onPause` |

---

## Q9: How do you test biometric authentication?

```kotlin
// Use BiometricPrompt with fake callback for testing
class FakeBiometricAuth : BiometricAuthInterface {
    var shouldSucceed = true

    override fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (shouldSucceed) onSuccess()
        else onError("Authentication failed")
    }
}

// Test ViewModel
class LoginViewModelTest {

    private val biometricAuth = FakeBiometricAuth()
    private val viewModel = LoginViewModel(biometricAuth)

    @Test
    fun `biometric success navigates to home`() {
        biometricAuth.shouldSucceed = true

        viewModel.authenticateWithBiometric()

        assertEquals(NavigationState.Home, viewModel.navigationState.value)
    }

    @Test
    fun `biometric failure shows error`() {
        biometricAuth.shouldSucceed = false

        viewModel.authenticateWithBiometric()

        assertEquals("Authentication failed", viewModel.errorMessage.value)
    }
}
```

### Testing on emulator
```bash
# ADB command to simulate fingerprint
adb -e emu finger touch 1  # Simulate touch
adb -e emu finger touch 2  # Simulate fail
```

---

## Q10: What are biometric best practices?

### Do's
- ✅ Check `canAuthenticate()` before showing prompt
- ✅ Provide fallback (PIN/password)
- ✅ Use `BIOMETRIC_STRONG` for sensitive operations
- ✅ Tie crypto keys to biometric for payments
- ✅ Handle all error codes
- ✅ Cancel prompt on app background

### Don'ts
- ❌ Don't auto-show prompt on app launch (let user choose)
- ❌ Don't use `BIOMETRIC_WEAK` for payments
- ❌ Don't store biometric data yourself
- ❌ Don't block app if biometric unavailable (use fallback)
- ❌ Don't show multiple prompts simultaneously

### Security levels
| Operation | Min Authenticator | Crypto |
|-----------|------------------|--------|
| App unlock | BIOMETRIC_WEAK | Optional |
| View sensitive data | BIOMETRIC_STRONG | Optional |
| Payment | BIOMETRIC_STRONG | ✅ Required |
| Key decryption | BIOMETRIC_STRONG | ✅ Required |

---

## 🔗 Related Topics
- [Keystore](Keystore.md)
- [Data Encryption](DataEncryption.md)
- [Security Scenarios](SecurityScenarios.md)
