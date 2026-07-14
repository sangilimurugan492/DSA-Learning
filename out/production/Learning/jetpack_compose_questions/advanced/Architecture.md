# Architecture

## Q1: What architecture patterns work with Compose?

```
1. MVI (Model-View-Intent) — Best fit for Compose
   Intent → ViewModel → State → UI

2. MVVM — Works well with StateFlow
   ViewModel → StateFlow → collectAsState → UI

3. MVI + MVVM hybrid
   Events → ViewModel → UiState → UI
```

### MVI Flow
```
User Action → Intent → ViewModel → New State → UI Rebuild

    ┌──────────┐     ┌──────────────┐     ┌──────────┐
    │  Intent   │ ──→ │  ViewModel   │ ──→ │  UiState  │
    │ (action)  │     │ (reduce)     │     │ (result)  │
    └──────────┘     └──────────────┘     └──────────┘
                                              ↓
                                         UI Rebuild
```

---

## Q2: How do you implement MVI with Compose?

```kotlin
// 1. State — single immutable state object
data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: TodoFilter = TodoFilter.All,
)

// 2. Intent — sealed class for all actions
sealed interface TodoIntent {
    data object Load : TodoIntent
    data class Add(val title: String) : TodoIntent
    data class Toggle(val id: String) : TodoIntent
    data class Delete(val id: String) : TodoIntent
    data class Filter(val filter: TodoFilter) : TodoIntent
}

// 3. ViewModel — processes intents, produces state
class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    private val _state = MutableStateFlow(TodoUiState())
    val state: StateFlow<TodoUiState> = _state.asStateFlow()

    fun onIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.Load -> loadTodos()
            is TodoIntent.Add -> addTodo(intent.title)
            is TodoIntent.Toggle -> toggleTodo(intent.id)
            is TodoIntent.Delete -> deleteTodo(intent.id)
            is TodoIntent.Filter -> applyFilter(intent.filter)
        }
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val todos = repository.getTodos()
                _state.update { it.copy(todos = todos, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun addTodo(title: String) {
        viewModelScope.launch {
            val todo = Todo(id = UUID.randomUUID().toString(), title = title)
            repository.addTodo(todo)
            _state.update { it.copy(todos = it.todos + todo) }
        }
    }
}

// 4. UI — stateless, sends intents
@Composable
fun TodoScreen(viewModel: TodoViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TodoContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun TodoContent(state: TodoUiState, onIntent: (TodoIntent) -> Unit) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { /* show add dialog */ }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }) { padding ->
        when {
            state.isLoading -> CircularProgressIndicator(Modifier.padding(padding))
            state.error != null -> Text("Error: ${state.error}")
            else -> LazyColumn(Modifier.padding(padding)) {
                items(state.todos, key = { it.id }) { todo ->
                    TodoItem(todo) { onIntent(TodoIntent.Toggle(todo.id)) }
                }
            }
        }
    }
}
```

---

## Q3: What is a state holder?

A state holder is a plain class that manages UI state for a composable. Used when state is too complex for `remember` but doesn't need a ViewModel.

```kotlin
// State holder class
class CounterState(initialValue: Int = 0) {
    var count by mutableStateOf(initialValue)
        private set

    fun increment() { count++ }
    fun decrement() { count-- }
    fun reset() { count = 0 }
}

// Use with remember
@Composable
fun Counter() {
    val state = remember { CounterState() }

    Column {
        Text("${state.count}")
        Button(onClick = state::increment) { Text("+") }
        Button(onClick = state::reset) { Text("Reset") }
    }
}

// State holder with SavedStateHandle
class FormState(savedStateHandle: SavedStateHandle) {
    var name by savedStateHandle.saveable { mutableStateOf("") }
    var email by savedStateHandle.saveable { mutableStateOf("") }

    val isValid: Boolean
        get() = name.isNotBlank() && email.contains("@")

    fun reset() {
        name = ""
        email = ""
    }
}
```

### When to use state holder vs ViewModel?
| State Holder | ViewModel |
|---------------|----------|
| UI-only state | Business logic |
| No lifecycle needed | Lifecycle-aware |
| Small scope | Screen/feature scope |
| `remember { }` | `viewModel()` |
| Form, toggle, scroll | Auth, cart, data |

---

## Q4: How do you structure a Compose project?

```
app/src/main/java/com/example/app/
├── core/
│   ├── designsystem/        # Theme, colors, typography
│   ├── components/          # Shared composables
│   └── util/                # Extensions, helpers
├── data/
│   ├── remote/              # API, DTOs
│   ├── local/               # Database, DataStore
│   └── repository/          # Repository implementations
├── domain/
│   ├── model/               # Domain models
│   └── repository/          # Repository interfaces
├── feature/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   ├── HomeUiState.kt
│   │   └── HomeIntent.kt
│   ├── detail/
│   │   ├── DetailScreen.kt
│   │   └── DetailViewModel.kt
│   └── settings/
│       └── ...
├── navigation/
│   └── AppNavigation.kt
└── MainActivity.kt
```

### Principles
1. **Feature-first** — group by feature, not by layer
2. **Unidirectional data flow** — state flows down, events flow up
3. **Stateless UI** — composables receive state, emit events
4. **Single source of truth** — ViewModel holds state
5. **Immutable state** — data classes with `val` fields

---

## Q5: How do you handle one-time events in MVI?

```kotlin
// ViewModel
class LoginViewModel : ViewModel() {
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Submit -> login(intent.email, intent.password)
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.login(email, password)
                _events.send(LoginEvent.NavigateToHome)
            } catch (e: Exception) {
                _events.send(LoginEvent.ShowError(e.message ?: "Error"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}

// UI — consume events
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

    LoginContent(state = state, onIntent = viewModel::onIntent)
}
```

---

## Q6: How do you handle state restoration?

```kotlin
// ViewModel with SavedStateHandle
class SearchViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    var query by savedStateHandle.saveable { mutableStateOf("") }
    var filters by savedStateHandle.saveable { mutableStateOf(setOf<String>()) }

    val results: StateFlow<List<Item>> = flow {
        emit(repository.search(query, filters))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

// rememberSaveable for simple state
@Composable
fun FormScreen() {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
}

// Custom Saver for complex objects
val userSaver = run {
    mapSaver(
        save = { mapOf("name" to it.name, "age" to it.age) },
        restore = { User(it["name"] as String, it["age"] as Int) },
    )
}

var user by rememberSaveable(stateSaver = userSaver) {
    mutableStateOf(User("Alice", 30))
}
```

---

## Q7: How do you test architecture in Compose?

```kotlin
// 1. Test ViewModel
class TodoViewModelTest {
    @Test
    fun `load todos updates state`() = runTest {
        val repository = FakeTodoRepository(listOf(Todo("1", "Test")))
        val viewModel = TodoViewModel(repository)

        viewModel.onIntent(TodoIntent.Load)

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(1, viewModel.state.value.todos.size)
    }
}

// 2. Test composable
class TodoScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `displays todos`() {
        val state = TodoUiState(todos = listOf(Todo("1", "Test")))
        composeRule.setContent {
            TodoContent(state = state, onIntent = {})
        }
        composeRule.onNodeWithText("Test").assertExists()
    }

    @Test
    fun `click toggle sends intent`() {
        var clickedIntent: TodoIntent? = null
        composeRule.setContent {
            TodoContent(
                state = TodoUiState(todos = listOf(Todo("1", "Test"))),
                onIntent = { clickedIntent = it },
            )
        }
        composeRule.onNodeWithText("Test").performClick()
        assertTrue(clickedIntent is TodoIntent.Toggle)
    }
}
```

### Architecture Best Practices
```
✅ Unidirectional data flow (state down, events up)
✅ Single immutable UiState per screen
✅ Sealed class for intents/events
✅ Stateless composables (testable, reusable)
✅ ViewModel for screen-level state
✅ State holder for UI-only state
✅ SavedStateHandle for process death
✅ Feature-first package structure
✅ Repository pattern for data
✅ Test ViewModel and composables separately
```

---

## 🔗 Related Topics
- [State Management](../intermediate/StateManagement.md)
- [Performance](Performance.md)
- [Testing](Testing.md)
