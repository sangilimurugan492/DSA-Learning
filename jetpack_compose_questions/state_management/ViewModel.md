# ViewModel in Compose

## Q1: How do you use ViewModel with Compose?

```kotlin
class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() { _count.value++ }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel()) {
    val count by viewModel.count.collectAsStateWithLifecycle()

    Column {
        Text("$count")
        Button(onClick = viewModel::increment) { Text("+") }
    }
}
```

### Why ViewModel?
- **Survives configuration changes** — rotation doesn't destroy it
- **Lifecycle-aware** — cleared when associated scope is destroyed
- **Holds UI state** — single source of truth for screen
- **Testable** — test business logic without UI
- **Coroutine scope** — `viewModelScope` for async operations

---

## Q2: How do you scope ViewModel?

```kotlin
// 1. Activity-scoped — shared across all composables in Activity
@Composable
fun Screen1(viewModel: MyViewModel = viewModel()) { /* same instance */ }
@Composable
fun Screen2(viewModel: MyViewModel = viewModel()) { /* same instance */ }

// 2. Navigation route-scoped — one instance per route
NavHost(navController, startDestination = "home") {
    composable("home") {
        val viewModel: HomeViewModel = viewModel()  // Scoped to "home" route
    }
    composable("detail") {
        val viewModel: DetailViewModel = viewModel()  // Scoped to "detail" route
    }
}

// 3. Navigation graph-scoped — shared across routes in a graph
NavHost(navController, startDestination = "checkout") {
    navigation(startDestination = "cart", route = "checkout") {
        composable("cart") { entry ->
            val viewModel: CheckoutViewModel = viewModel(entry.destination.parent!!)
            // Shared across cart, shipping, payment
        }
        composable("shipping") { entry ->
            val viewModel: CheckoutViewModel = viewModel(entry.destination.parent!!)
        }
    }
}

// 4. With Hilt — automatic scoping
@HiltViewModel
class MyViewModel @Inject constructor() : ViewModel()

@Composable
fun Screen(viewModel: MyViewModel = hiltViewModel()) { /* ... */ }
```

---

## Q3: How do you structure UiState in ViewModel?

```kotlin
// Single UiState — recommended for most screens
data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
)

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init { loadUser() }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = repository.getUser()
                _uiState.update { it.copy(isLoading = false, user = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

// Sealed class UiState — for distinct states
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class UserViewModel2(private val repo: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState<User>>(UiState.Loading)
    val state: StateFlow<UiState<User>> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                _state.value = UiState.Success(repo.getUser())
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
}
```

### Single data class vs sealed class
| Single data class | Sealed class |
|-------------------|-------------|
| Multiple fields can be set | One state at a time |
| Good for forms, complex UI | Good for loading/success/error |
| Easier to update partial state | Clearer state transitions |
| More flexible | More type-safe |

---

## Q4: How do you handle events from ViewModel?

```kotlin
class LoginViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // One-time events using Channel
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val user = repo.login(email, password)
                _events.send(LoginEvent.NavigateToHome(user))
            } catch (e: Exception) {
                _events.send(LoginEvent.ShowError(e.message ?: "Error"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

sealed interface LoginEvent {
    data class NavigateToHome(val user: User) : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}

// Consume events in Compose
@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> navController.navigate("home")
                is LoginEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LoginContent(state = state, onLogin = viewModel::login)
}
```

### State vs Events
| State (StateFlow) | Events (Channel) |
|-------------------|------------------|
| Persists until changed | Consumed once |
| New collectors get current | New collectors get nothing |
| For UI state | For one-time actions |
| Navigation state, form data | Show snackbar, navigate |

---

## Q5: How do you inject dependencies into ViewModel?

```kotlin
// 1. Manual factory
class MyViewModel(private val repo: MyRepository) : ViewModel()

class MyViewModelFactory(private val repo: MyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}

@Composable
fun MyScreen() {
    val repo = MyRepository()
    val viewModel: MyViewModel = viewModel(factory = MyViewModelFactory(repo))
}

// 2. Hilt (recommended)
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()

@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) { /* ... */ }

// 3. With Navigation + Hilt
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()  // Scoped to route
            HomeScreen(viewModel)
        }
    }
}
```

---

## Q6: How do you test ViewModel?

```kotlin
class CounterViewModelTest {
    @Test
    fun `initial count is zero`() {
        val viewModel = CounterViewModel()
        assertEquals(0, viewModel.count.value)
    }

    @Test
    fun `increment increases count`() {
        val viewModel = CounterViewModel()
        viewModel.increment()
        assertEquals(1, viewModel.count.value)
    }
}

// With dependencies and coroutines
class UserViewModelTest {
    @Test
    fun `load user updates state to success`() = runTest {
        val fakeRepo = FakeUserRepository(User("Alice"))
        val viewModel = UserViewModel(fakeRepo)

        viewModel.loadUser()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("Alice", (state as UiState.Success).data.name)
    }

    @Test
    fun `load user handles error`() = runTest {
        val fakeRepo = FakeUserRepository(throw Exception("Network error"))
        val viewModel = UserViewModel(fakeRepo)

        viewModel.loadUser()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals("Network error", (state as UiState.Error).message)
    }
}

// Fake repository
class FakeUserRepository(private val user: User? = null, private val error: Exception? = null) : UserRepository {
    override suspend fun getUser(): User {
        error?.let { throw it }
        return user ?: throw IllegalStateException("No user")
    }
}
```

---

## Q7: How do you handle ViewModel lifecycle?

```
ViewModel lifecycle:

Activity created → ViewModel created
  ↓
Configuration change (rotation) → ViewModel survives
  ↓
Activity finished → ViewModel.onCleared() → ViewModel destroyed

With Navigation:
Composable enters composition → ViewModel created (if new route)
  ↓
Composable leaves composition (back press) → ViewModel.onCleared()
  ↓
Process death → ViewModel destroyed → SavedStateHandle survives
```

```kotlin
class MyViewModel : ViewModel() {
    init {
        // Called when ViewModel is created
    }

    override fun onCleared() {
        super.onCleared()
        // Called when ViewModel is destroyed
        // Clean up resources, cancel coroutines
        // viewModelScope is automatically cancelled
    }
}

// ViewModel with SavedStateHandle for process death
@HiltViewModel
class MyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // Survives process death
    var query by savedStateHandle.saveable { mutableStateOf("") }

    // Read from SavedStateHandle
    val userId: String? = savedStateHandle.get<String>("userId")
}
```

### ViewModel lifecycle with Compose
| Event | ViewModel |
|-------|-----------|
| First composition | Created (if not exists) |
| Recomposition | Unchanged |
| Configuration change | Survives |
| Composable leaves composition | May survive (if Activity alive) |
| Activity finished | `onCleared()` called |
| Process death | Destroyed, SavedStateHandle survives |

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [Flow](Flow.md)
- [SavedStateHandle](SavedStateHandle.md)
