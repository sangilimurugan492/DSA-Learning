# State Management

## Q1: How do you use ViewModel with Compose?

```kotlin
class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() { _count.value++ }
    fun decrement() { _count.value-- }
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
- Survives configuration changes (rotation)
- Lifecycle-aware
- Holds and manages UI state
- Testable

---

## Q2: What is collectAsStateWithLifecycle?

```kotlin
// collectAsStateWithLifecycle — stops collecting when lifecycle is not active
val state by viewModel.state.collectAsStateWithLifecycle()

// collectAsState — collects regardless of lifecycle (older API)
val state by viewModel.state.collectAsState()

// collectAsStateWithLifecycle is preferred:
// - Saves battery (stops collection in background)
// - Prevents unnecessary recompositions
// - Lifecycle-aware
```

### Dependencies
```kotlin
// build.gradle
implementation "androidx.lifecycle:lifecycle-runtime-compose:2.7.0"
```

---

## Q3: How do you structure state with ViewModel?

```kotlin
// UI State — sealed class or data class
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()

    init { loadUser() }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = repository.getUser()
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// UI
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = state) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> Text("Hello, ${s.data.name}")
        is UiState.Error -> Text("Error: ${s.message}")
    }
}
```

---

## Q4: How do you use StateFlow vs SharedFlow?

```kotlin
// StateFlow — state holder (always has a value, conflation)
class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()
    // New collectors get current value immediately
}

// SharedFlow — events (no initial value, can replay)
class NavigationViewModel : ViewModel() {
    private val _events = MutableSharedFlow<NavigationEvent>()
    val events: SharedFlow<NavigationEvent> = _events.asSharedFlow()

    fun navigateTo(screen: String) {
        viewModelScope.launch {
            _events.emit(NavigationEvent.Navigate(screen))
        }
    }
}

// Consume SharedFlow in Compose
@Composable
fun NavScreen(viewModel: NavigationViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NavigationEvent.Navigate -> navController.navigate(event.screen)
            }
        }
    }
}
```

| StateFlow | SharedFlow |
|-----------|------------|
| Always has a value | No initial value |
| Conflates (keeps latest) | Can buffer/replay |
| For state | For events |
| `collectAsStateWithLifecycle` | `collect` in LaunchedEffect |

---

## Q5: How do you handle one-time events?

```kotlin
// ViewModel
class LoginViewModel : ViewModel() {
    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                repository.login(email, password)
                _events.send(LoginEvent.Success)
            } catch (e: Exception) {
                _events.send(LoginEvent.Error(e.message))
            }
        }
    }
}

sealed class LoginEvent {
    data object Success : LoginEvent()
    data class Error(val message: String) : LoginEvent()
}

// UI — consume with LaunchedEffect
@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.Success -> navController.navigate("home")
                is LoginEvent.Error -> showSnackbar(event.message)
            }
        }
    }
}
```

### Channel vs SharedFlow for events
| Channel | SharedFlow |
|---------|------------|
| Each event delivered once | Can replay or conflate |
| One collector | Multiple collectors |
| Better for one-time events | Better for shared events |

---

## Q6: How do you pass ViewModel with dependencies?

```kotlin
// ViewModel with constructor parameters
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    // ...
}

// 1. ViewModelProvider.Factory
class UserViewModelFactory(private val repo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}

@Composable
fun UserScreen() {
    val repo = UserRepository()
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repo))
    // ...
}

// 2. With Hilt (recommended)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() { /* ... */ }

@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    // Hilt provides the ViewModel with dependencies
}

// 3. With Navigation
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "user") {
        composable("user") { UserScreen() }  // hiltViewModel() scoped to route
    }
}
```

---

## Q7: How do you manage state for complex forms?

```kotlin
data class FormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
)

class FormViewModel : ViewModel() {
    private val _state = MutableStateFlow(FormState())
    val state: StateFlow<FormState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, nameError = null) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    fun validate(): Boolean {
        var isValid = true
        _state.update { state ->
            val nameError = if (state.name.isBlank()) "Required" else null
            val emailError = if (!state.email.contains("@")) "Invalid email" else null
            val passwordError = if (state.password.length < 6) "Min 6 chars" else null
            isValid = nameError == null && emailError == null && passwordError == null
            state.copy(nameError = nameError, emailError = emailError, passwordError = passwordError)
        }
        return isValid
    }

    fun submit() {
        if (!validate()) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                repository.submit(state.value)
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
```

---

## 🔗 Related Topics
- [State (Beginner)](../beginner/State.md)
- [Effects](Effects.md)
- [ViewModel (State Management)](../state_management/ViewModel.md)
