# KMP Common Code — Interview Questions

## 🔴 Q1: What can and cannot go in `commonMain`?
**Answer:**

**Can:**
- Pure Kotlin code (no platform APIs)
- KMP-compatible libraries (`kotlinx.coroutines`, `kotlinx.serialization`, etc.)
- `expect` declarations
- Interfaces, data classes, sealed classes, enums
- Kotlin standard library (common subset)

**Cannot:**
- Java/JVM-specific APIs (`java.util.Date`, `java.io.File`)
- Android-specific APIs (`android.content.Context`, `android.util.Log`)
- iOS-specific APIs (`Foundation.NSDate`, `UIKit.UIView`)
- Any platform-specific library

---

## 🔴 Q2: How do you share data models in KMP?
**Answer:**

```kotlin
// commonMain
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)

@Serializable
enum class UserRole { ADMIN, USER, GUEST }

@Serializable
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}
```

Both Android and iOS use the same model classes.

---

## 🔴 Q3: How do you share business logic in KMP?
**Answer:**

```kotlin
// commonMain
class UserRepository(
    private val api: UserApi,
    private val storage: KeyValueStorage
) {
    suspend fun getUser(id: String): User {
        // Try cache first
        storage.getString("user_$id")?.let { cached ->
            return Json.decodeFromString<User>(cached)
        }
        
        // Fetch from API
        val user = api.fetchUser(id)
        storage.putString("user_$id", Json.encodeToString(user))
        return user
    }
}

interface UserApi {
    suspend fun fetchUser(id: String): User
}

interface KeyValueStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}
```

Platform-specific implementations of `UserApi` and `KeyValueStorage` are injected.

---

## 🟡 Q4: How do you handle logging in common code?
**Answer:** Use `expect`/`actual` or a common interface:

```kotlin
// commonMain
interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

expect fun getLogger(): Logger

// androidMain
actual fun getLogger(): Logger = AndroidLogger()

class AndroidLogger : Logger {
    override fun d(tag: String, message: String) = Log.d(tag, message)
    override fun e(tag: String, message: String, throwable: Throwable?) =
        Log.e(tag, message, throwable)
}

// iosMain
actual fun getLogger(): Logger = IosLogger()

class IosLogger : Logger {
    override fun d(tag: String, message: String) = println("[$tag] $message")
    override fun e(tag: String, message: String, throwable: Throwable?) =
        println("[$tag] ERROR: $message")
}
```

---

## 🟡 Q5: How do you handle date/time in common code?
**Answer:** Use `kotlinx-datetime` library:

```kotlin
// commonMain
import kotlinx.datetime.*

fun formatTimestamp(epoch: Long): String {
    val instant = Instant.fromEpochMilliseconds(epoch)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.date} ${localDateTime.time}"
}

fun daysBetween(start: Instant, end: Instant): Int {
    val startLocal = start.toLocalDateTime(TimeZone.UTC).date
    val endLocal = end.toLocalDateTime(TimeZone.UTC).date
    return startLocal.daysUntil(endLocal)
}
```

---

## 🔴 Q6: How do you share platform-specific implementations?
**Answer:** Three approaches:

**1. expect/actual:**
```kotlin
// commonMain
expect fun getPlatformName(): String
// androidMain
actual fun getPlatformName() = "Android"
// iosMain
actual fun getPlatformName() = "iOS"
```

**2. Interface injection:**
```kotlin
// commonMain
interface Platform {
    val name: String
    fun showToast(message: String)
}
// Inject from platform side
```

**3. Sealed class / enum:**
```kotlin
enum class Platform { ANDROID, IOS }
```

---

## 🟡 Q7: How do you handle file I/O in common code?
**Answer:** Use `expect`/`actual` or a multiplatform library:

```kotlin
// commonMain
expect class File(path: String) {
    fun readText(): String
    fun writeText(content: String)
    fun exists(): Boolean
}

// Or use an interface
interface FileSystem {
    fun read(path: String): String
    fun write(path: String, content: String)
}
```

Or use `okio` (multiplatform) for file operations.

---

## 🟡 Q8: How do you share constants in KMP?
**Answer:**

```kotlin
// commonMain
object AppConfig {
    const val BASE_URL = "https://api.example.com"
    const val TIMEOUT_SECONDS = 30
    const val MAX_RETRIES = 3
    
    val SUPPORTED_LOCALES = listOf("en", "es", "fr", "de")
}
```

---

## 🟡 Q9: How do you handle network connectivity checks in common code?
**Answer:**

```kotlin
// commonMain
interface ConnectivityObserver {
    val isOnline: StateFlow<Boolean>
    fun startObserving()
    fun stopObserving()
}

// androidMain
class AndroidConnectivityObserver(
    private val context: Context
) : ConnectivityObserver {
    override val isOnline = MutableStateFlow(false)
    
    override fun startObserving() {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        // Register NetworkCallback
    }
}

// iosMain
class IosConnectivityObserver : ConnectivityObserver {
    override val isOnline = MutableStateFlow(false)
    
    override fun startObserving() {
        // Use NWPathMonitor
    }
}
```

---

## 🟡 Q10: How do you share validation logic in KMP?
**Answer:**

```kotlin
// commonMain
object Validator {
    fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regex.matches(email)
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
               password.any { it.isUpperCase() } &&
               password.any { it.isDigit() }
    }
    
    fun isValidPhone(phone: String): Boolean {
        val regex = Regex("^\\+?[1-9]\\d{1,14}$")
        return regex.matches(phone)
    }
}
```

---

## 🟡 Q11: How do you share preferences/settings in KMP?
**Answer:** Use `expect`/`actual` or a multiplatform library like `multiplatform-settings`:

```kotlin
// commonMain
expect class SettingsFactory {
    fun create(): Settings
}

interface Settings {
    fun getString(key: String, default: String = ""): String
    fun putString(key: String, value: String)
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putBoolean(key: String, value: Boolean)
}

// androidMain — uses SharedPreferences
actual class SettingsFactory {
    actual fun create(): Settings = SharedPreferencesSettings(context)
}

// iosMain — uses NSUserDefaults
actual class SettingsFactory {
    actual fun create(): Settings = NSUserDefaultsSettings()
}
```

---

## 🟡 Q12: How do you share error handling in KMP?
**Answer:**

```kotlin
// commonMain
sealed class AppError : Throwable() {
    data class NetworkError(val code: Int, val message: String) : AppError()
    data class DatabaseError(val message: String) : AppError()
    data class ValidationError(val field: String, val reason: String) : AppError()
    data class UnknownError(val message: String) : AppError()
}

class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: AppError) : Result<Nothing>()
}

suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: AppError) {
        Result.Failure(e)
    } catch (e: Exception) {
        Result.Failure(AppError.UnknownError(e.message ?: "Unknown error"))
    }
}
```

---

## 📌 Key Takeaways
- `commonMain` = pure Kotlin + KMP libraries only
- Share data models with `@Serializable` data classes
- Use interfaces + DI for platform-specific implementations
- `kotlinx-datetime` for date/time, `kotlinx-serialization` for JSON
- Share validation, business logic, error handling in common code

---

[← Project Setup](ProjectSetup.md) | [Back to README](../README.md) | [Next: Expect/Actual →](ExpectActual.md)
