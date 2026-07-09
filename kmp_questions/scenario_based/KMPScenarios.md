# KMP Scenario-Based Questions — Interview Questions

## 🔴 Q1: You need to share networking logic between Android and iOS. How do you architect this?
**Answer:**

```kotlin
// commonMain — shared API client
class UserApi(private val client: HttpClient) {
    suspend fun getUser(id: String): User = client.get("/users/$id").body()
    suspend fun getUsers(): List<User> = client.get("/users").body()
}

// commonMain — repository with caching
class UserRepository(
    private val api: UserApi,
    private val cache: KeyValueStorage
) {
    suspend fun getUser(id: String): User {
        cache.getString("user_$id")?.let { return Json.decodeFromString(it) }
        val user = api.getUser(id)
        cache.putString("user_$id", Json.encodeToString(user))
        return user
    }
}

// commonMain — ViewModel
class UserViewModel(private val repo: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()
    
    fun load(id: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                _state.value = UiState.Success(repo.getUser(id))
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
}
```

Platform-specific: `HttpClient` engine (OkHttp/Darwin), `KeyValueStorage` (SharedPreferences/NSUserDefaults).

---

## 🔴 Q2: Your KMP module crashes on iOS with a memory leak. How do you debug?
**Answer:**

1. **Check coroutine scopes** — ensure `scope.cancel()` is called in Swift `deinit`
2. **Use Xcode Instruments** — Leaks template to identify retained objects
3. **Check strong references** — ensure no retain cycles between Swift/Kotlin

```kotlin
// commonMain
class DataManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    fun start() { scope.launch { observe() } }
    
    // MUST call from Swift
    fun destroy() { scope.cancel() }
}
```

```swift
class MyVC: UIViewController {
    let manager = DataManager()
    
    deinit {
        manager.destroy()  // Critical!
    }
}
```

4. **Check Flow collectors** — cancel Flow collection when view disappears
5. **Use `@SharedImmutable`** (old memory model) or verify new memory model is enabled

---

## 🔴 Q3: You need to share a database between Android and iOS. How do you design this?
**Answer:**

```kotlin
// commonMain — schema
object DatabaseSchema : SqlDriver.Schema {
    override val version = 1
    override fun create(db: SqlDriver) { /* CREATE TABLE statements */ }
    override fun migrate(db: SqlDriver, old: Int, new: Int) { /* migrations */ }
}

// commonMain — factory interface
interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// commonMain — repository
class UserRepository(private val db: AppDatabase) {
    fun observeUsers(): Flow<List<User>> =
        db.userQueries.selectAll().asFlow().map { it.executeAsList() }
    
    fun saveUser(user: User) = db.userQueries.insert(user.id, user.name, user.email)
}

// androidMain
class AndroidDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver() = AndroidSqliteDriver(DatabaseSchema, context, "app.db")
}

// iosMain
class IosDriverFactory : DatabaseDriverFactory {
    override fun createDriver() = NativeSqliteDriver(DatabaseSchema, "app.db")
}
```

---

## 🟡 Q4: Your iOS build takes 20 minutes. How do you optimize?
**Answer:**

1. **Enable caching:**
```properties
kotlin.native.cacheKind=static
kotlin.native.incrementalCompilation=true
org.gradle.caching=true
org.gradle.parallel=true
```

2. **Cache `~/.konan` in CI**
3. **Reduce targets** — only build `iosSimulatorArm64` for testing, not all 3
4. **Use framework caching** — cache the built `.framework`
5. **Minimize dependencies** in `iosMain`
6. **Use `--no-daemon` only if needed** — daemon is faster for repeated builds
7. **Profile build** with `--scan` to identify bottlenecks

---

## 🟡 Q5: You need to implement offline-first sync. How do you architect this in KMP?
**Answer:**

```kotlin
// commonMain
class SyncManager(
    private val api: UserApi,
    private val db: AppDatabase,
    private val connectivity: ConnectivityObserver
) {
    fun observeUsers(): Flow<List<User>> =
        db.userQueries.selectAll().asFlow().map { it.executeAsList() }
    
    suspend fun sync() {
        if (!connectivity.isOnline.value) return
        
        // Push pending changes
        val pending = db.pendingQueries.selectAll().executeAsList()
        pending.forEach { change ->
            try {
                api.pushChange(change)
                db.pendingQueries.markSynced(change.id)
            } catch (e: Exception) { /* retry later */ }
        }
        
        // Pull remote changes
        val remoteUsers = api.getUsers()
        db.transaction {
            remoteUsers.forEach { db.userQueries.insert(it) }
        }
    }
}
```

---

## 🟡 Q6: How do you handle different API responses on Android vs iOS?
**Answer:** Don't — the API should be the same. But if platform-specific behavior is needed:

```kotlin
// commonMain — same API for all platforms
class UserApi(private val client: HttpClient) {
    suspend fun getUser(id: String): User = client.get("/users/$id").body()
}

// If headers differ per platform
expect fun defaultHeaders(): Map<String, String>

// androidMain
actual fun defaultHeaders() = mapOf("X-Platform" to "Android", "X-Device" to Build.MODEL)

// iosMain
actual fun defaultHeaders() = mapOf("X-Platform" to "iOS", "X-Device" to UIDevice.currentDevice.model)
```

---

## 🟡 Q7: You need to share validation logic. How do you implement this?
**Answer:**

```kotlin
// commonMain
object Validator {
    fun validateEmail(email: String): ValidationResult {
        return if (Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email))
            ValidationResult.Valid
        else ValidationResult.Invalid("Invalid email format")
    }
    
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.length < 8 -> ValidationResult.Invalid("Too short")
            !password.any { it.isUpperCase() } -> ValidationResult.Invalid("Need uppercase")
            !password.any { it.isDigit() } -> ValidationResult.Invalid("Need digit")
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}
```

Both platforms use the same validation — no duplication.

---

## 🟡 Q8: Your KMP library needs to support both KMP and Android-only consumers. How?
**Answer:**

```kotlin
// build.gradle.kts
kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
}

// Android-only consumers can use the .aar
// KMP consumers use the common metadata
// Both get the same API
```

Publish both `release` variant for Android and common metadata for KMP. Gradle resolves the correct artifact automatically.

---

## 🟡 Q9: How do you handle push notifications in KMP?
**Answer:**

```kotlin
// commonMain — shared logic
class NotificationManager(
    private val api: NotificationApi,
    private val storage: KeyValueStorage
) {
    suspend fun registerToken(token: String) {
        api.registerDevice(token, storage.getString("user_id"))
    }
    
    fun handleNotification(data: Map<String, String>) {
        // Process notification payload
    }
}

// androidMain — Firebase Cloud Messaging
class AndroidNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        NotificationManager(api, storage).handleNotification(message.data)
    }
}

// iosMain — APNs
class IosNotificationService: UNUserNotificationCenterDelegateProtocol {
    func didReceiveNotification(response: UNNotificationResponse) {
        NotificationManager(api, storage).handleNotification(response.notification.request.content.userInfo)
    }
}
```

---

## 🟡 Q10: You need to share analytics tracking. How do you architect this?
**Answer:**

```kotlin
// commonMain
interface AnalyticsTracker {
    fun trackEvent(name: String, params: Map<String, Any>)
    fun trackScreen(screenName: String)
    fun setUserProperty(key: String, value: String)
}

// commonMain — shared tracking logic
class AnalyticsManager(private val tracker: AnalyticsTracker) {
    fun trackUserAction(action: String, userId: String) {
        tracker.trackEvent("user_action", mapOf(
            "action" to action,
            "user_id" to userId,
            "timestamp" to currentTimeMillis()
        ))
    }
}

// androidMain — Firebase Analytics
class FirebaseAnalyticsTracker(context: Context) : AnalyticsTracker {
    private val firebase = FirebaseAnalytics.getInstance(context)
    override fun trackEvent(name: String, params: Map<String, Any>) {
        firebase.logEvent(name, params.toBundle())
    }
}

// iosMain — Firebase or Mixpanel
class IosAnalyticsTracker : AnalyticsTracker {
    override fun trackEvent(name: String, params: Map<String, Any>) {
        Analytics.logEvent(name, parameters: params)
    }
}
```

---

## 🟡 Q11: How do you handle deep links in KMP?
**Answer:**

```kotlin
// commonMain — shared routing logic
class DeepLinkHandler {
    fun parse(uri: String): DeepLink {
        return when {
            uri.startsWith("myapp://user/") -> DeepLink.UserProfile(uri.substringAfterLast("/"))
            uri.startsWith("myapp://settings") -> DeepLink.Settings
            else -> DeepLink.Unknown
        }
    }
}

sealed class DeepLink {
    data class UserProfile(val userId: String) : DeepLink()
    object Settings : DeepLink()
    object Unknown : DeepLink()
}

// androidMain — Intent handling
// iosMain — URL scheme handling
```

---

## 🟡 Q12: Your team is new to KMP. How do you convince them to adopt it?
**Answer:**

1. **Start with a POC** — share one small module (e.g., validation logic)
2. **Show code reuse** — measure lines of code saved
3. **No disruption to Android** — `androidMain` works exactly as before
4. **Gradual adoption** — no big-bang migration
5. **Shared tests** — write once, test on all platforms
6. **Future-proof** — Compose Multiplatform for shared UI later
7. **Industry adoption** — JetBrains, Netflix, Philips, McDonald's use KMP

---

## 🟡 Q13: How do you handle secure storage (tokens, keys) in KMP?
**Answer:**

```kotlin
// commonMain
interface SecureStorage {
    fun save(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
}

// androidMain — EncryptedSharedPreferences
class AndroidSecureStorage(context: Context) : SecureStorage {
    private val prefs = EncryptedSharedPreferences.create(
        context, "secure", MasterKey(context)
    )
    override fun save(key: String, value: String) = prefs.edit().putString(key, value).apply()
    override fun get(key: String): String? = prefs.getString(key, null)
}

// iosMain — Keychain
class IosSecureStorage : SecureStorage {
    override fun save(key: String, value: String) {
        val data = value.encodeToNSData()
        SecItemAdd([
            kSecClass: kSecClassGenericPassword,
            kSecAttrAccount: key,
            kSecValueData: data
        ] as CFDictionary, null)
    }
}
```

---

## 🟡 Q14: How do you handle localization/i18n in KMP?
**Answer:**

```kotlin
// commonMain — shared string resources
object Strings {
    fun get(key: String, locale: String): String = when (locale) {
        "en" -> englishStrings[key] ?: key
        "es" -> spanishStrings[key] ?: key
        else -> englishStrings[key] ?: key
    }
}

// Or use Compose Multiplatform resources
@Composable
fun Greeting() {
    Text(stringResource(Res.string.welcome_message))
}
```

For non-Compose projects, use `multiplatform-settings` to store locale and a shared string map.

---

## 🟡 Q15: How do you handle feature flags in KMP?
**Answer:**

```kotlin
// commonMain
class FeatureFlagManager(
    private val remoteConfig: RemoteConfigProvider,
    private val localCache: KeyValueStorage
) {
    fun isEnabled(flag: FeatureFlag): Boolean {
        return localCache.getBoolean(flag.key, flag.defaultValue)
    }
    
    suspend fun refresh() {
        val flags = remoteConfig.fetchFlags()
        flags.forEach { (key, value) ->
            localCache.putBoolean(key, value)
        }
    }
}

enum class FeatureFlag(val key: String, val defaultValue: Boolean) {
    DARK_MODE("dark_mode", false),
    NEW_DASHBOARD("new_dashboard", false),
    ANALYTICS_V2("analytics_v2", true)
}
```

---

## 📌 Key Takeaways
- Share business logic, use interfaces for platform-specific code
- Always cancel coroutine scopes in iOS `deinit`
- Offline-first: local DB + sync manager
- Secure storage: EncryptedSharedPreferences (Android) / Keychain (iOS)
- Shared validation, analytics, feature flags, deep links
- Gradual adoption minimizes risk

---

[← Advanced](../advanced/Migration.md) | [Back to README](../README.md)
