# KMP Migration — Interview Questions

## 🔴 Q1: How do you migrate an Android app to KMP?
**Answer:** Gradual migration approach:

**Step 1: Convert module to KMP**
```kotlin
// Before: build.gradle.kts (Android library)
plugins { kotlin("android") }

// After: KMP module
plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
```

**Step 2: Move pure Kotlin code to `commonMain`**
- Data classes, models, utilities → `commonMain`
- Android-specific code stays in `androidMain`

**Step 3: Add iOS target and implement `expect`/`actual`**

---

## 🔴 Q2: How do you handle Java/Android imports during migration?
**Answer:**

```kotlin
// ❌ commonMain — won't compile (Java import)
import java.util.Date
class Event(val date: Date)

// ✅ Use kotlinx-datetime
import kotlinx.datetime.Instant
class Event(val timestamp: Instant)

// ✅ Or use expect/actual
// commonMain
expect class DateWrapper()
// androidMain
actual class DateWrapper(actual val date: java.util.Date = java.util.Date())
```

Common replacements:

| Java/Android | KMP Alternative |
|-------------|-----------------|
| `java.util.Date` | `kotlinx.datetime.Instant` |
| `java.io.File` | `okio.Path` / `expect`/`actual` |
| `java.util.logging` | Custom `Logger` interface |
| `android.util.Log` | `expect`/`actual` logger |
| `android.content.Context` | Interface abstraction |

---

## 🟡 Q3: How do you migrate from Retrofit to Ktor?
**Answer:**

```kotlin
// Before: Retrofit (Android-only)
interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User
}

val api = Retrofit.Builder()
    .baseUrl("https://api.example.com")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()
    .create(UserApi::class.java)

// After: Ktor (multiplatform)
class UserApi(private val client: HttpClient) {
    suspend fun getUser(id: String): User =
        client.get("users/$id").body()
}

val client = HttpClient {
    install(ContentNegotiation) { json() }
}
```

---

## 🟡 Q4: How do you migrate from Room to SQLDelight?
**Answer:**

```kotlin
// Before: Room
@Entity
data class User(@PrimaryKey val id: String, val name: String)

@Dao
interface UserDao {
    @Query("SELECT * FROM User") fun getAll(): Flow<List<User>>
    @Insert suspend fun insert(user: User)
}

// After: SQLDelight
-- User.sq
CREATE TABLE User (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL);
selectAll: SELECT * FROM User;
insert: INSERT INTO User(id, name) VALUES (?, ?);
```

```kotlin
class UserRepository(private val db: AppDatabase) {
    fun getAll(): Flow<List<User>> = db.userQueries.selectAll().asFlow().map { it.executeAsList() }
    fun insert(user: User) = db.userQueries.insert(user.id, user.name)
}
```

---

## 🟡 Q5: How do you migrate from Dagger/Hilt to Koin?
**Answer:**

```kotlin
// Before: Hilt
@HiltAndroidApp
class App : Application()

@Module @InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideRepo(): UserRepository = UserRepositoryImpl()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel()

// After: Koin
val sharedModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    viewModel { UserViewModel(get()) }
}

fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(sharedModule)
    }
}
```

---

## 🟡 Q6: How do you incrementally adopt KMP?
**Answer:**

1. **Start small:** Move one module (e.g., `data` or `domain`) to KMP
2. **Keep Android working:** `androidMain` has same code as before
3. **Add iOS target:** Implement `expect`/`actual` for platform-specific code
4. **Gradually expand:** Move more modules to KMP
5. **Add iOS app:** Create `iosApp` target, consume shared module

```
Phase 1: shared/ (data models only)
Phase 2: shared/ + networking (Ktor)
Phase 3: shared/ + database (SQLDelight)
Phase 4: shared/ + ViewModels
Phase 5: Compose Multiplatform (optional)
```

---

## 🟡 Q7: How do you handle SharedPreferences migration?
**Answer:**

```kotlin
// Before: Android-only
val prefs = context.getSharedPreferences("app", MODE_PRIVATE)
prefs.edit().putString("user_id", "123").apply()

// After: multiplatform-settings
// commonMain
val settings: Settings = Settings()
settings.putString("user_id", "123")

// androidMain — uses SharedPreferences under the hood
// iosMain — uses NSUserDefaults
```

---

## 🟡 Q8: How do you migrate from Gson/Moshi to kotlinx.serialization?
**Answer:**

```kotlin
// Before: Gson
data class User(val id: String, val name: String)
val user = Gson().fromJson(json, User::class.java)

// After: kotlinx.serialization
@Serializable
data class User(val id: String, val name: String)
val user = Json.decodeFromString<User>(json)
```

Key changes:
- Add `@Serializable` annotation
- Use `Json.decodeFromString<T>()` / `Json.encodeToString()`
- No reflection needed (compile-time generated)
- Works on all platforms

---

## 🟡 Q9: How do you handle platform-specific code during migration?
**Answer:**

```kotlin
// Step 1: Identify platform-specific code
// Search for: import android.*, import java.*

// Step 2: Create interface in commonMain
interface SystemInfo {
    fun getDeviceId(): String
    fun getAppVersion(): String
}

// Step 3: Implement per platform
// androidMain
class AndroidSystemInfo(private val context: Context) : SystemInfo {
    override fun getDeviceId() = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    override fun getAppVersion() = context.packageManager.getPackageInfo(context.packageName, 0).versionName
}

// iosMain
class IosSystemInfo : SystemInfo {
    override fun getDeviceId() = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: ""
    override fun getAppVersion() = Bundle.main.objectForInfoDictionaryKey("CFBundleShortVersionString") as String
}
```

---

## 🟡 Q10: How do you test migration didn't break anything?
**Answer:**

```kotlin
// commonTest — shared tests verify common code
class UserRepositoryTest {
    @Test
    fun `should return cached user`() = runTest {
        val repo = UserRepository(FakeApi(), FakeCache())
        val user = repo.getUser("1")
        assertEquals("John", user.name)
    }
}

// androidUnitTest — verify Android-specific code
class AndroidSystemInfoTest {
    @Test
    fun `should return device id`() {
        val info = AndroidSystemInfo(context)
        assertTrue(info.getDeviceId().isNotEmpty())
    }
}

// Run all: ./gradlew allTests
```

---

## 📌 Key Takeaways
- Migrate gradually: one module at a time
- Replace Java/Android APIs with KMP alternatives
- Retrofit → Ktor, Room → SQLDelight, Hilt → Koin
- Gson/Moshi → kotlinx.serialization
- Use interfaces + DI for platform-specific code
- Run `allTests` to verify migration

---

[← Library Development](LibraryDevelopment.md) | [Back to README](../README.md) | [Next: Scenarios →](../scenario_based/KMPScenarios.md)
