# KMP Architecture — Interview Questions

## 🔴 Q1: What architecture patterns work well with KMP?
**Answer:** Common patterns: MVVM, MVI, Clean Architecture. The key is sharing ViewModels and business logic in `commonMain`.

```
┌─────────────────────────────────────────┐
│              commonMain                  │
│  ┌─────────┐  ┌──────────┐  ┌────────┐ │
│  │  Model  │  │ UseCase  │  │  Repo  │ │
│  └─────────┘  └──────────┘  └────────┘ │
│  ┌─────────────────────────────────────┐│
│  │           ViewModel (shared)        ││
│  └─────────────────────────────────────┘│
├──────────────┬──────────────────────────┤
│  androidMain │         iosMain           │
│  ┌────────┐  │     ┌────────┐           │
│  │  View  │  │     │  View  │           │
│  │(XML/   │  │     │(SwiftUI│           │
│  │Compose)│  │     │/UIKit) │           │
│  └────────┘  │     └────────┘           │
└──────────────┴──────────────────────────┘
```

---

## 🔴 Q2: How do you implement shared ViewModels in KMP?
**Answer:**

```kotlin
// commonMain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUser(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = repository.getUser(id)
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

---

## 🔴 Q3: How do you implement Clean Architecture in KMP?
**Answer:**

```kotlin
// commonMain — Domain layer (pure Kotlin)
// Entity
data class User(val id: String, val name: String, val email: String)

// Repository interface
interface UserRepository {
    suspend fun getUser(id: String): User
    suspend fun saveUser(user: User)
}

// Use Case
class GetUserUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(id: String): User = repo.getUser(id)
}

// commonMain — Data layer (interfaces)
interface UserApi {
    suspend fun fetchUser(id: String): UserDto
}
interface UserCache {
    fun get(id: String): User?
    fun put(user: User)
}

// commonMain — Repository implementation
class UserRepositoryImpl(
    private val api: UserApi,
    private val cache: UserCache
) : UserRepository {
    override suspend fun getUser(id: String): User {
        cache.get(id)?.let { return it }
        val dto = api.fetchUser(id)
        val user = dto.toDomain()
        cache.put(user)
        return user
    }
}
```

Platform-specific: `UserApi` and `UserCache` implementations in `androidMain`/`iosMain`.

---

## 🟡 Q4: How do you handle dependency injection in KMP?
**Answer:** Use KMP-compatible DI frameworks:

**Koin (most popular):**
```kotlin
// commonMain
val sharedModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single { GetUserUseCase(get()) }
    factory { UserViewModel(get()) }
}

// androidMain
fun initKoin(androidContext: Context) {
    startKoin {
        modules(sharedModule + androidModule)
    }
}

val androidModule = module {
    single<UserApi> { RetrofitUserApi(get()) }
    single<UserCache> { RoomUserCache(get()) }
    single { androidContext }
}
```

**Kotlin Inject (compile-time):**
```kotlin
// commonMain
@Inject
class UserViewModel(
    private val getUserUseCase: GetUserUseCase
)
```

---

## 🟡 Q5: How do you share navigation logic in KMP?
**Answer:** Navigation is typically platform-specific. Share the navigation **state**:

```kotlin
// commonMain
class NavigationManager {
    private val _screen = MutableStateFlow<Screen>(Screen.Home)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _screen.value = screen
    }
}

sealed class Screen {
    object Home : Screen()
    data class Profile(val userId: String) : Screen()
    object Settings : Screen()
}
```

With Compose Multiplatform, use Voyager or Navigation Compose for shared navigation.

---

## 🟡 Q6: How do you handle state management in KMP?
**Answer:** Use `StateFlow`/`SharedFlow` from kotlinx.coroutines:

```kotlin
// commonMain
class TodoViewModel(private val repo: TodoRepository) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()
    
    private val _events = MutableSharedFlow<TodoEvent>()
    val events: SharedFlow<TodoEvent> = _events.asSharedFlow()
    
    fun addTodo(title: String) {
        viewModelScope.launch {
            repo.addTodo(Todo(title = title))
            _todos.value = repo.getAllTodos()
            _events.emit(TodoEvent.Added)
        }
    }
}
```

---

## 🟡 Q7: How do you structure a KMP project for scalability?
**Answer:**

```
:shared
├── commonMain/
│   ├── domain/          ← Entities, UseCases, Repository interfaces
│   ├── data/            ← Repository implementations, DTOs
│   ├── presentation/    ← ViewModels, UiState
│   └── infrastructure/   ← Platform interfaces
├── androidMain/
│   └── infrastructure/   ← Android implementations
└── iosMain/
    └── infrastructure/   ← iOS implementations

:core-network          ← Shared networking module
:core-database         ← Shared database module
:core-ui               ← Shared UI components (Compose MP)
```

---

## 🟡 Q8: How do you handle error handling across platforms?
**Answer:**

```kotlin
// commonMain
sealed class AppError : Throwable() {
    data class Network(val code: Int) : AppError()
    data class Database(val message: String) : AppError()
    data class Validation(val field: String) : AppError()
    data class Unknown(val message: String) : AppError()
}

class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: AppError) : Result<Nothing>()
}

// ViewModel handles errors
fun load() {
    viewModelScope.launch {
        when (val result = getUserUseCase(id)) {
            is Result.Success -> _state.value = UiState.Success(result.data)
            is Result.Failure -> _state.value = UiState.Error(result.error.toUserMessage())
        }
    }
}
```

---

## 🟡 Q9: How do you share ViewModels between Android and iOS?
**Answer:**

```kotlin
// commonMain
abstract class ViewModel {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val viewModelScope get() = scope
    fun clear() { scope.cancel() }
}

// androidMain — extend androidx.lifecycle.ViewModel
actual class ViewModel actual constructor() : ViewModel()

// iosMain — KMP ViewModel used directly
// Swift creates and holds reference
```

```swift
// Swift
class IOSViewModel: ObservableObject {
    let viewModel: UserViewModel
    
    init() {
        viewModel = UserViewModel(repository: ...)
    }
    
    deinit {
        viewModel.clear()
    }
}
```

---

## 🟡 Q10: How do you handle offline-first architecture in KMP?
**Answer:**

```kotlin
// commonMain
class OfflineFirstRepository<T>(
    private val local: LocalDataSource<T>,
    private val remote: RemoteDataSource<T>
) {
    suspend fun get(id: String): T {
        // Return local first
        local.get(id)?.let { return it }
        // Fetch from remote
        val data = remote.fetch(id)
        local.save(data)
        return data
    }
    
    suspend fun sync() {
        val pending = local.getPending()
        pending.forEach { remote.push(it) }
        local.markSynced(pending)
    }
}
```

---

## 🟡 Q11: How do you handle background tasks in KMP?
**Answer:** Platform-specific background task scheduling:

```kotlin
// commonMain
interface BackgroundTaskScheduler {
    fun schedule(task: BackgroundTask)
    fun cancel(taskId: String)
}

// androidMain — WorkManager
// iosMain — BGTaskScheduler
```

---

## 🟡 Q12: How do you handle configuration changes in KMP?
**Answer:** ViewModels survive config changes on Android. On iOS, the ViewModel is held by the view:

```kotlin
// commonMain
class MyViewModel : ViewModel() {
    val state: StateFlow<MyState> = ...
}

// androidMain — ViewModel survives config changes automatically
// iosMain — Store in a container that persists across view recreations
```

---

## 🟡 Q13: How do you handle dependency inversion in KMP?
**Answer:**

```kotlin
// commonMain — Define abstractions
interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// commonMain — Use abstraction
class Database(factory: DatabaseDriverFactory) {
    private val driver = factory.createDriver()
}

// androidMain — Provide implementation
class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver =
        AndroidSqliteDriver("app.db", context, Database.Schema)
}

// iosMain — Provide implementation
class IosDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver =
        NativeSqliteDriver(Database.Schema, "app.db")
}
```

---

## 🟡 Q14: How do you handle module communication in KMP?
**Answer:** Use shared events or a message bus:

```kotlin
// commonMain
class EventBus {
    private val _events = MutableSharedFlow<AppEvent>()
    val events = _events.asSharedFlow()
    
    suspend fun emit(event: AppEvent) { _events.emit(event) }
}

sealed class AppEvent {
    data class UserLoggedIn(val userId: String) : AppEvent()
    object UserLoggedOut : AppEvent()
}
```

---

## 🟡 Q15: How do you handle testing in KMP architecture?
**Answer:**

```kotlin
// commonTest
class GetUserUseCaseTest {
    @Test
    fun `should return user from repository`() = runTest {
        val mockRepo = mockk<UserRepository>()
        coEvery { mockRepo.getUser("1") } returns User("1", "John", "john@test.com")
        
        val useCase = GetUserUseCase(mockRepo)
        val result = useCase("1")
        
        assertEquals("John", result.name)
    }
}
```

Tests in `commonTest` run on all platforms.

---

## 📌 Key Takeaways
- Share ViewModels, UseCases, Repositories in `commonMain`
- Use `StateFlow`/`SharedFlow` for state management
- Koin or Kotlin Inject for DI
- Clean Architecture: domain (pure) → data (interfaces) → platform (implementations)
- Tests in `commonTest` run on all platforms

---

[← Beginner](../beginner/PlatformSpecific.md) | [Back to README](../README.md) | [Next: Networking →](Networking.md)
