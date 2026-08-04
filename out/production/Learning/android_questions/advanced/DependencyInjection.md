# Dependency Injection (Dagger/Hilt)

## 📖 Explanation

Dependency Injection (DI) provides objects their dependencies instead of them creating their own. This improves testability, modularity, and decoupling.

### Hilt (Recommended)
Hilt is built on top of Dagger, providing a simpler API with less boilerplate. It's Google's recommended DI framework for Android.

### Key Hilt Annotations
| Annotation          | Description                                          |
|---------------------|------------------------------------------------------|
| `@HiltAndroidApp`    | Application class — triggers Hilt code generation   |
| `@AndroidEntryPoint` | Marks Activity/Fragment/View/Service for injection  |
| `@Inject`            | Marks a constructor or field for injection           |
| `@Module`            | Marks a class that provides dependencies             |
| `@InstallIn`         | Specifies the Hilt component scope                   |
| `@Provides`          | Provides a dependency inside a module                |
| `@Binds`             | Binds an interface to an implementation              |
| `@Singleton`         | Scope — one instance for the app lifetime            |
| `@ViewModelScoped`   | Scope — one instance per ViewModel                   |

### Hilt Components (Scopes)
| Component          | Scope            | Lifetime                    |
|--------------------|------------------|------------------------------|
| SingletonComponent | `@Singleton`     | App lifetime                 |
| ActivityComponent  | `@ActivityScoped`| Activity lifetime            |
| FragmentComponent  | `@FragmentScoped`| Fragment lifetime            |
| ViewModelComponent | `@ViewModelScoped`| ViewModel lifetime          |
| ViewComponent      | `@ViewScoped`    | View lifetime                |

### Constructor Injection (Preferred)
```kotlin
class UserRepository @Inject constructor(
    private val api: ApiService,
    private val dao: UserDao
)
```

### Module Injection (for things you can't construct)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService = Retrofit.Builder()
        .baseUrl("https://api.example.com/")
        .build()
        .create(ApiService::class.java)
}
```

### `@Binds` (Interface to Implementation)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

// --- Application ---
@HiltAndroidApp
class App : Application()

// --- Interfaces ---
interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun getUser(id: Long): User?
}

// --- Implementation with constructor injection ---
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: UserDao
) : UserRepository {
    override suspend fun getUsers(): List<User> {
        // Try cache first, then network
        val cached = dao.getAllUsers()
        return if (cached.isNotEmpty()) cached else api.getUsers()
    }

    override suspend fun getUser(id: Long): User? {
        return dao.getUserById(id) ?: api.getUserById(id)
    }
}

// --- Modules ---
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}

// --- ViewModel with Hilt ---
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = repository.getUsers()
        }
    }
}

// --- Activity with field injection ---
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Field injection (for things that can't use constructor)
    @Inject
    lateinit var userRepository: UserRepository

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.loadUsers()
    }
}

// --- Fragment with injection ---
@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserViewModel by hiltNavGraphViewModels(R.id.userFragment)

    // Inject directly
    @Inject
    lateinit var userRepository: UserRepository
}
```

```groovy
// build.gradle (project)
plugins {
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

// build.gradle (app)
plugins {
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
}
```

---

## ❓ Interview Questions

1. **What is Dependency Injection and why is it useful?**
   - DI provides objects their dependencies instead of them creating their own. Benefits: testability (swap implementations), decoupling (no `new` in business logic), and single responsibility.

2. **What is the difference between Dagger and Hilt?**
   - Hilt is built on Dagger with Android-specific simplifications. No custom Component boilerplate, predefined components (Singleton, Activity, Fragment, ViewModel), and `@AndroidEntryPoint` replaces manual component injection.

3. **What is the difference between `@Provides` and `@Binds`?**
   - `@Provides` is for objects you construct manually (Retrofit, Room). `@Binds` is for mapping an interface to an implementation — no object creation, just type binding. `@Binds` is more efficient (less generated code).

4. **What are Hilt components and scopes?**
   - Components define the lifecycle of injected objects. Scopes tie to components: `@Singleton` (app), `@ActivityScoped` (activity), `@ViewModelScoped` (ViewModel). A scoped dependency lives as long as its component.

5. **How do you inject a ViewModel with Hilt?**
   - Annotate the ViewModel with `@HiltViewModel` and `@Inject constructor`. Use `by viewModels()` in Activity or `hiltNavGraphViewModels()` in Fragment. Hilt generates the ViewModelProvider.Factory automatically.

6. **What is the difference between constructor injection and field injection?**
   - **Constructor injection** — dependencies are passed through the constructor: `class Repository @Inject constructor(api: ApiService, dao: UserDao)`. This is the **preferred** approach because: (1) Dependencies are immutable (`val`). (2) The class is testable — just pass mocks to the constructor. (3) Clear dependencies — you can see everything the class needs. (4) Works with `final` properties. **Field injection** — dependencies are injected into fields: `@Inject lateinit var api: ApiService`. Used when constructor injection isn't possible: (1) Activities/Fragments — system creates them, you can't control the constructor. (2) Objects with no constructor. (3) When you need a dependency after construction. Field injection requires `@AndroidEntryPoint` and `@Inject`. Drawbacks: mutable, not testable without a DI framework, hidden dependencies. Always prefer constructor injection when possible.

7. **What are Hilt qualifiers and when do you use them?**
   - Qualifiers resolve ambiguity when Hilt can provide multiple instances of the same type. Example: if you have two `String` dependencies (API URL and database name), Hilt can't distinguish them. Create qualifiers: `@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ApiUrl; @Qualifier annotation class DatabaseName`. Provide: `@Provides @ApiUrl fun provideApiUrl() = "https://api.example.com/"`. Inject: `class Repository @Inject constructor(@ApiUrl val url: String)`. Hilt has built-in qualifiers: `@ApplicationContext` and `@ActivityContext`. Use qualifiers when: (1) Same type with different configurations (e.g., two `OkHttpClient` instances — one for auth, one for general). (2) Same interface with different implementations. Qualifiers make dependencies explicit and type-safe.

8. **How do you test classes that use Hilt DI?**
   - Three testing approaches: (1) **Unit tests without Hilt** — for ViewModels and Repositories, just pass mock dependencies to the constructor. No Hilt needed. `class UserViewModelTest { val repo = mock<UserRepository>(); val vm = UserViewModel(repo) }`. This is the simplest and fastest. (2) **Hilt Android testing** — use `@HiltAndroidTest` and `@UninstallModules` to replace modules in instrumented tests. Create a `FakeModule` with `@TestInstallIn` that provides fake implementations. (3) **HiltTestRule** — `@get:Rule val hiltRule = HiltAndroidRule(this)`. Inject real or fake dependencies. For UI tests with Espresso/Compose, use `@HiltAndroidTest` and launch the Activity with `launchActivity<MainActivity>()`. Best practice: keep business logic testable without Hilt (constructor injection). Use Hilt testing only for integration/UI tests.

9. **What is the Hilt component hierarchy and how do scopes work?**
   - Hilt components form a hierarchy: `SingletonComponent` → `ActivityRetainedComponent` → `ActivityComponent` → `FragmentComponent` → `ViewComponent` → `ServiceComponent`. Each component has a scope: `@Singleton` (app), `@ActivityRetainedScoped` (survives config changes), `@ActivityScoped`, `@FragmentScoped`, `@ViewScoped`, `@ServiceScoped`. A scoped binding means one instance per component instance. Child components can access parent-scoped dependencies (e.g., Fragment can access `@Singleton` and `@ActivityScoped` dependencies). But parent cannot access child-scoped dependencies. `@ViewModelScoped` is special — it's tied to the ViewModel lifecycle, not the component hierarchy. Use `@Singleton` for app-wide singletons (API, database). Use `@ActivityScoped` for per-activity instances (e.g., per-activity ViewModel factory). Don't over-scope — `@Singleton` everything leads to memory pressure.

10. **How do you inject dependencies into WorkManager Workers with Hilt?**
    - Use `@HiltWorker` annotation and `@AssistedInject` (not `@Inject`). Workers require `Context` and `WorkerParameters` which are provided by the system via assisted injection. Steps: (1) `@HiltWorker class MyWorker @AssistedInject constructor(@Assisted context: Context, @Assisted params: WorkerParameters, private val repo: Repository) : CoroutineWorker(context, params)`. (2) Add `implementation("androidx.hilt:hilt-work:1.2.0")` and `kapt("androidx.hilt:hilt-compiler:1.2.0")`. (3) In `Application.onCreate()`: `WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(HiltWorkerFactory.getInstance()).build())`. Or with `Configuration.Provider`: `override val workManagerConfiguration: Configuration get() = Configuration.Builder().setWorkerFactory(workerFactory).build()`. (4) Hilt injects the `HiltWorkerFactory` automatically.

11. **What is the difference between `@Singleton`, `@ViewModelScoped`, and `@ActivityScoped`?**
    - `@Singleton` — one instance for the entire app lifetime. Use for shared resources: API client, Room database, SharedPreferences. The instance is created once and reused everywhere. Pros: efficient, shared state. Cons: lives forever, memory pressure if overused. `@ActivityScoped` — one instance per Activity. A new instance is created for each Activity and destroyed when the Activity is destroyed. Use for per-activity state like navigation or per-activity adapters. Survives configuration changes? No — Activity is destroyed on rotation, so `@ActivityScoped` instances are recreated. Use `@ActivityRetainedScoped` to survive config changes. `@ViewModelScoped` — one instance per ViewModel. The dependency lives as long as the ViewModel. Use for dependencies that should be shared across screens within the same ViewModel but not app-wide. Scoping helps control memory and lifecycle — match the scope to the dependency's actual usage.

12. **How do you migrate from Dagger to Hilt?**
    - Migration steps: (1) Add Hilt plugins and dependencies to `build.gradle`. (2) Annotate Application class with `@HiltAndroidApp`. (3) Annotate Activities, Fragments, Services, Views with `@AndroidEntryPoint`. (4) Replace custom `Component` classes with Hilt's predefined components. (5) Replace `@Component` modules with `@Module @InstallIn(SingletonComponent::class)`. (6) Replace `@ContributesAndroidInjector` with `@AndroidEntryPoint` (no module needed). (7) Replace `AndroidInjection.inject(this)` with `@AndroidEntryPoint` (automatic). (8) Replace custom `ViewModelProvider.Factory` with `@HiltViewModel`. (9) Use `@ApplicationContext` instead of `@ApplicationContext Context`. (10) Remove `DaggerApplication`, `HasAndroidInjector`, `DispatchingAndroidInjector`. (11) Test that everything compiles and works. Benefits: less boilerplate, predefined components, better tooling support. Hilt is fully interoperable with existing Dagger code during migration.

---

## 🔗 Related Topics
- [Architecture Patterns](../intermediate/ArchitecturePatterns.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
