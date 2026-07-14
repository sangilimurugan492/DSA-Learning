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

---

## 🔗 Related Topics
- [Architecture Patterns](../intermediate/ArchitecturePatterns.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
