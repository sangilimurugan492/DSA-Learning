# KMP Coroutines — Interview Questions

## 🔴 Q1: How do coroutines work in KMP?
**Answer:** `kotlinx.coroutines` is fully multiplatform. Same code runs on all platforms:

```kotlin
// commonMain
import kotlinx.coroutines.*

class DataRepository {
    suspend fun fetchData(): Data {
        delay(1000)  // Non-blocking delay
        return Data("loaded")
    }
    
    fun loadDataAsync(scope: CoroutineScope) {
        scope.launch {
            val data = fetchData()
            // Update state
        }
    }
}
```

Platform-specific dispatchers:
- `Dispatchers.Main` — UI thread (Android: Main Looper, iOS: MainQueue)
- `Dispatchers.IO` — I/O operations (Android: ThreadPool, iOS: global queue)
- `Dispatchers.Default` — CPU-intensive (all platforms)

---

## 🔴 Q2: How do you set up coroutines in KMP?
**Answer:**

```kotlin
// build.gradle.kts
val commonMain by getting {
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    }
}
val androidMain by getting {
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    }
}
```

```kotlin
// commonMain — shared ViewModel
abstract class ViewModel {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val viewModelScope get() = scope
    fun clear() { scope.cancel() }
}

// commonMain — usage
class UserViewModel(private val repo: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()
    
    fun load(id: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = UiState.Success(repo.getUser(id))
        }
    }
}
```

---

## 🔴 Q3: How do you use Flow in KMP?
**Answer:**

```kotlin
// commonMain
import kotlinx.coroutines.flow.*

class UserRepository(private val api: UserApi, private val db: AppDatabase) {
    // Cold flow — one consumer
    fun getUsers(): Flow<List<User>> = db.userQueries
        .selectAll()
        .asFlow()
        .map { it.executeAsList() }
    
    // Hot flow — multiple consumers
    private val _events = MutableSharedFlow<UserEvent>()
    val events: SharedFlow<UserEvent> = _events.asSharedFlow()
    
    suspend fun refreshUsers() {
        val users = api.fetchAllUsers()
        db.userQueries.transaction {
            users.forEach { db.userQueries.insert(it) }
        }
        _events.emit(UserEvent.Refreshed(users.size))
    }
}
```

---

## 🟡 Q4: How do you handle Dispatchers.Main on iOS?
**Answer:** `kotlinx-coroutines-core` provides `Dispatchers.Main` on all platforms. On iOS, it uses `dispatch_async(dispatch_get_main_queue())`:

```kotlin
// commonMain — works on both platforms
suspend fun loadData() {
    val data = withContext(Dispatchers.IO) {
        // Background work
        api.fetchData()
    }
    withContext(Dispatchers.Main) {
        // UI update
        _state.value = UiState.Success(data)
    }
}
```

No extra setup needed — `Dispatchers.Main` is available out of the box.

---

## 🟡 Q5: How do you call KMP coroutines from Swift?
**Answer:**

```kotlin
// commonMain
class UserViewModel {
    suspend fun loadUser(id: String): User {
        return repo.getUser(id)
    }
}
```

```swift
// Swift — use async/await (Kotlin 1.9+)
Task {
    let user = try await viewModel.loadUser(id: "123")
    print(user.name)
}

// Or use callback
func loadUser(id: String, completion: @escaping (User) -> Void) {
    Task {
        let user = try await viewModel.loadUser(id: id)
        completion(user)
    }
}
```

Kotlin `suspend` functions are exposed as Swift `async` functions.

---

## 🟡 Q6: How do you handle cancellation in KMP coroutines?
**Answer:**

```kotlin
// commonMain
class UserViewModel : ViewModel() {
    private var loadJob: Job? = null
    
    fun load(id: String) {
        loadJob?.cancel()  // Cancel previous
        loadJob = viewModelScope.launch {
            try {
                val user = repo.getUser(id)
                _state.value = UiState.Success(user)
            } catch (e: CancellationException) {
                // Handle cancellation
                throw e  // Re-throw to propagate
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
    
    fun cancelLoad() {
        loadJob?.cancel()
    }
}
```

---

## 🟡 Q7: How do you use StateFlow vs SharedFlow in KMP?
**Answer:**

| Aspect | StateFlow | SharedFlow |
|--------|-----------|------------|
| Initial value | Required | Not required |
| Conflation | Yes (latest only) | Configurable |
| Replay | 1 (latest) | Configurable (0+) |
| Use case | UI state | Events, one-time actions |

```kotlin
// StateFlow — for UI state
private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
val uiState = _uiState.asStateFlow()

// SharedFlow — for events
private val _navigation = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
val navigation = _navigation.asSharedFlow()

// Usage
fun save() {
    viewModelScope.launch {
        _uiState.value = UiState.Loading
        repo.save(data)
        _uiState.value = UiState.Success
        _navigation.emit(NavigationEvent.Back)
    }
}
```

---

## 🟡 Q8: How do you handle errors in KMP coroutines?
**Answer:**

```kotlin
// commonMain
class UserViewModel(private val repo: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()
    
    fun load(id: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val user = repo.getUser(id)
                _state.value = UiState.Success(user)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                _state.value = UiState.Error("Network error: ${e.code}")
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Using CoroutineExceptionHandler
    private val handler = CoroutineExceptionHandler { _, throwable ->
        _state.value = UiState.Error(throwable.message ?: "Error")
    }
    
    fun loadSafe(id: String) {
        viewModelScope.launch(handler) {
            val user = repo.getUser(id)
            _state.value = UiState.Success(user)
        }
    }
}
```

---

## 🟡 Q9: How do you test coroutines in KMP?
**Answer:**

```kotlin
// commonTest
class UserViewModelTest {
    @Test
    fun `should load user`() = runTest {
        val repo = mockk<UserRepository>()
        coEvery { repo.getUser("1") } returns User("1", "John", "john@test.com")
        
        val viewModel = UserViewModel(repo)
        viewModel.load("1")
        
        assertEquals(UiState.Success(User("1", "John", "john@test.com")), viewModel.state.value)
    }
    
    @Test
    fun `should handle error`() = runTest {
        val repo = mockk<UserRepository>()
        coEvery { repo.getUser("1") } throws RuntimeException("Not found")
        
        val viewModel = UserViewModel(repo)
        viewModel.load("1")
        
        assertTrue(viewModel.state.value is UiState.Error)
    }
}
```

Use `kotlinx-coroutines-test` for `runTest`, `advanceTimeBy`, etc.

---

## 🟡 Q10: How do you handle structured concurrency in KMP?
**Answer:**

```kotlin
// commonMain
class SyncManager(
    private val userRepo: UserRepository,
    private val postRepo: PostRepository
) {
    suspend fun syncAll(): SyncResult = coroutineScope {
        // Parallel execution
        val usersDeferred = async { userRepo.sync() }
        val postsDeferred = async { postRepo.sync() }
        
        // Wait for all
        SyncResult(
            users = usersDeferred.await(),
            posts = postsDeferred.await()
        )
    }
    
    // SupervisorScope — one failure doesn't cancel others
    suspend fun loadAll(): AllData = supervisorScope {
        val users = async { userRepo.getAll() }
        val posts = async { postRepo.getAll() }
        AllData(users.await(), posts.await())
    }
}
```

---

## 🟡 Q11: How do you handle Flows with platform-specific sources?
**Answer:**

```kotlin
// commonMain
interface LocationProvider {
    fun locationFlow(): Flow<Location>
}

// androidMain
class AndroidLocationProvider(private val client: FusedLocationProviderClient) : LocationProvider {
    override fun locationFlow(): Flow<Location> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation.toLocation())
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }
}

// iosMain
class IosLocationProvider : LocationProvider {
    override fun locationFlow(): Flow<Location> = callbackFlow {
        val manager = CLLocationManager()
        manager.delegate = object : CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                trySend(manager.location!!.toLocation())
            }
        }
        manager.startUpdatingLocation()
        awaitClose { manager.stopUpdatingLocation() }
    }
}
```

---

## 🟡 Q12: How do you handle coroutine scopes on iOS?
**Answer:**

```kotlin
// commonMain
class IOSViewModel {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    fun start() {
        scope.launch {
            // Work
        }
    }
    
    fun destroy() {
        scope.cancel()  // Must call from Swift deinit
    }
}
```

```swift
// Swift
class MyViewController: UIViewController {
    let viewModel = IOSViewModel()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.start()
    }
    
    deinit {
        viewModel.destroy()  // Critical — prevent leaks
    }
}
```

---

## 📌 Key Takeaways
- `kotlinx.coroutines` works identically across all platforms
- `Dispatchers.Main` available on iOS without extra setup
- Kotlin `suspend` → Swift `async` (Kotlin 1.9+)
- `StateFlow` for UI state, `SharedFlow` for events
- Always cancel scopes on iOS in `deinit` to prevent leaks
- `runTest` for testing coroutines in `commonTest`

---

[← DI](DependencyInjection.md) | [Back to README](../README.md) | [Next: Testing →](Testing.md)
