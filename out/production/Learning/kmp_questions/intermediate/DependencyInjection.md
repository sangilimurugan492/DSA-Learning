# KMP Dependency Injection — Interview Questions

## 🔴 Q1: What DI frameworks work with KMP?
**Answer:**

| Framework | Type | KMP Support | Notes |
|-----------|------|-------------|-------|
| **Koin** | Runtime | ✅ Full | Most popular, DSL-based |
| **Kotlin Inject** | Compile-time | ✅ Full | Annotation processor, type-safe |
| **Kodein** | Runtime | ✅ Full | DSL-based, multiplatform |
| **Dagger/Hilt** | Compile-time | ❌ Android only | Not KMP-compatible |
| **Manual DI** | None | ✅ Always | No library, constructor injection |

---

## 🔴 Q2: How do you set up Koin in KMP?
**Answer:**

```kotlin
// build.gradle.kts
val commonMain by getting {
    dependencies {
        implementation("io.insert-koin:koin-core:3.5.3")
    }
}
val androidMain by getting {
    dependencies { implementation("io.insert-koin:koin-android:3.5.3") }
}

// commonMain — shared module
val sharedModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single { GetUserUseCase(get()) }
    factory { UserViewModel(get()) }
}

// androidMain
fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(sharedModule, androidModule)
    }
}

val androidModule = module {
    single<UserApi> { RetrofitUserApi(get()) }
    single<DatabaseDriverFactory> { AndroidDriverFactory(get()) }
    single { context }
}

// iosMain
fun initKoin() {
    startKoin {
        modules(sharedModule, iosModule)
    }
}

val iosModule = module {
    single<UserApi> { KtorUserApi() }
    single<DatabaseDriverFactory> { IosDriverFactory() }
}
```

---

## 🔴 Q3: How do you use Koin with ViewModels in KMP?
**Answer:**

```kotlin
// commonMain
val sharedModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    viewModel { UserViewModel(get()) }
}

// androidMain — use koin-androidx-viewmodel
fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(sharedModule)
    }
}

// In Android Activity/Fragment
val viewModel: UserViewModel by viewModel()

// iosMain — resolve manually
fun getUserViewModel(): UserViewModel = KoinPlatformToolsKt.getKoin().get<UserViewModel>()
```

```swift
// Swift
let viewModel = KoinPlatformToolsKt.getKoin().get { (module) in
    module.userViewModel
}
```

---

## 🟡 Q4: How do you use Kotlin Inject in KMP?
**Answer:**

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("me.tatarka.inject:kotlin-inject-runtime:0.6.3")
            }
        }
    }
}

// commonMain
@Me.tatarka.inject.annotations.Inject
class UserViewModel(
    private val getUserUseCase: GetUserUseCase
)

@Me.tatarka.inject.annotations.Inject
class GetUserUseCase(
    private val repo: UserRepository
)

@Me.tatarka.inject.annotations.Inject
class UserRepositoryImpl(
    private val api: UserApi,
    private val cache: UserCache
) : UserRepository

// Generate component
@Me.tatarka.inject.annotations.Component
abstract class AppComponent {
    abstract val userViewModel: UserViewModel
    
    @Me.tatarka.inject.annotations.Provides
    protected fun userApi(): UserApi = KtorUserApi()
}
```

---

## 🟡 Q5: How do you handle platform-specific dependencies in Koin?
**Answer:**

```kotlin
// commonMain — define interface
interface PlatformService {
    fun getDeviceId(): String
    fun getOSVersion(): String
}

// commonMain — shared module
val sharedModule = module {
    single<PlatformService> { getPlatformService() }
}

// androidMain
fun getPlatformService(): PlatformService = AndroidPlatformService(context)

// iosMain
fun getPlatformService(): PlatformService = IosPlatformService()
```

Or use `expect`/`actual`:
```kotlin
// commonMain
expect fun platformModule(): Module

// androidMain
actual fun platformModule() = module {
    single<PlatformService> { AndroidPlatformService(androidContext) }
}

// iosMain
actual fun platformModule() = module {
    single<PlatformService> { IosPlatformService() }
}
```

---

## 🟡 Q6: How do you handle scopes in Koin KMP?
**Answer:**

```kotlin
// commonMain
val sharedModule = module {
    scope<UserSession> {
        scoped { UserSessionData() }
        scoped { SessionRepository(get()) }
    }
    
    scope<FeatureScope> {
        scoped { FeatureRepository(get()) }
    }
}

// Usage
val session = getKoin().createScope<UserSession>()
val repo = session.get<SessionRepository>()
session.close()  // Clean up
```

---

## 🟡 Q7: How do you test DI in KMP?
**Answer:**

```kotlin
// commonTest
class UserViewModelTest {
    @AfterTest
    fun tearDown() = stopKoin()
    
    @Test
    fun `should load user`() = runTest {
        startKoin {
            modules(module {
                single<UserRepository> { FakeUserRepository() }
                single { GetUserUseCase(get()) }
                single { UserViewModel(get()) }
            }
        }
        
        val viewModel = KoinPlatformToolsKt.getKoin().get<UserViewModel>()
        viewModel.loadUser("1")
        
        assertEquals("John", viewModel.uiState.value.data?.name)
    }
}

class FakeUserRepository : UserRepository {
    override suspend fun getUser(id: String) = User(id, "John", "john@test.com")
}
```

---

## 🟡 Q8: How do you do manual DI in KMP?
**Answer:**

```kotlin
// commonMain
class ServiceLocator(
    private val platformDependencies: PlatformDependencies
) {
    val httpClient: HttpClient by lazy { createHttpClient() }
    val database: AppDatabase by lazy { 
        AppDatabase(platformDependencies.createDriver()) 
    }
    val userRepository: UserRepository by lazy { 
        UserRepositoryImpl(httpClient, database) 
    }
    val getUserUseCase: GetUserUseCase by lazy { 
        GetUserUseCase(userRepository) 
    }
    
    fun createUserViewModel() = UserViewModel(getUserUseCase)
}

// androidMain
val locator = ServiceLocator(AndroidDependencies(context))

// iosMain
val locator = ServiceLocator(IosDependencies())
```

---

## 🟡 Q9: How do you initialize Koin on iOS?
**Answer:**

```kotlin
// commonMain
fun initKoin(modules: List<Module>) {
    startKoin {
        modules(modules)
    }
}

// iosMain
fun initKoinIos() {
    initKoin(listOf(sharedModule, iosModule))
}
```

```swift
// Swift
KoinInitKt.initKoinIos()
let viewModel = KoinPlatformToolsKt.getKoin().get { (module) in
    module.userViewModel
}
```

---

## 🟡 Q10: How do you handle DI for different build variants?
**Answer:**

```kotlin
// commonMain
val sharedModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}

// androidMain — debug
val debugModule = module {
    single<HttpClient> { createMockHttpClient() }
}

// androidMain — release
val releaseModule = module {
    single<HttpClient> { createRealHttpClient() }
}

// App
fun initKoin(context: Context) {
    startKoin {
        modules(sharedModule + if (BuildConfig.DEBUG) debugModule else releaseModule)
    }
}
```

---

## 📌 Key Takeaways
- **Koin** = most popular runtime DI for KMP
- **Kotlin Inject** = compile-time DI, type-safe
- Share DI modules in `commonMain`, platform-specific in `androidMain`/`iosMain`
- Use `expect`/`actual` for platform-specific module creation
- Manual DI is viable for small projects

---

[← Database](Database.md) | [Back to README](../README.md) | [Next: Coroutines →](Coroutines.md)
