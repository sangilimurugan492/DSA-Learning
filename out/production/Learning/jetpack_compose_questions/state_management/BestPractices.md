# State Management Best Practices

## 1. Hoist State Appropriately

```kotlin
// ✅ Good — hoist to lowest common parent
@Composable
fun Parent() {
    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) { /* tabs */ }
        when (selectedTab) {
            0 -> Screen1()
            1 -> Screen2()
        }
    }
}

// ✅ Good — keep local state local
@Composable
fun ExpandableCard() {
    var expanded by remember { mutableStateOf(false) }  // Only this card needs it
    // ...
}

// ✅ Good — screen-level state in ViewModel
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ...
}
```

---

## 2. Use Immutable State

```kotlin
// ✅ Good — immutable data class with val
data class UserState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

// ❌ Bad — mutable fields
data class UserState(
    var name: String = "",  // Mutable!
    var isLoading: Boolean = false,
)

// ✅ Good — use copy() for updates
_state.update { it.copy(name = newName, error = null) }

// ❌ Bad — mutating state
_state.value.name = newName  // Don't mutate!
```

---

## 3. Use Sealed Classes for UI State

```kotlin
// ✅ Good — sealed class for distinct states
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

@Composable
fun UserScreen(state: UiState<User>) {
    when (state) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> UserContent(state.data)
        is UiState.Error -> ErrorContent(state.message)
    }
    // Compiler enforces exhaustive when — no missing cases
}
```

---

## 4. Separate State from Events

```kotlin
class LoginViewModel : ViewModel() {
    // State — persists, displayed in UI
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // Events — one-time, trigger actions
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val user = repo.login()
                _events.send(LoginEvent.NavigateToHome)  // One-time event
            } catch (e: Exception) {
                _events.send(LoginEvent.ShowError(e.message))  // One-time event
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
```

### Rules:
- **State** → `StateFlow` → `collectAsStateWithLifecycle`
- **Events** → `Channel` → `collect` in `LaunchedEffect`
- Never use `StateFlow` for one-time events (they get conflated)
- Never use `Channel` for state (it gets consumed)

---

## 5. Use collectAsStateWithLifecycle

```kotlin
// ✅ Good — lifecycle-aware collection
val state by viewModel.state.collectAsStateWithLifecycle()

// ❌ Bad — collects even when backgrounded
val state by viewModel.state.collectAsState()

// ✅ Good — for events, use repeatOnLifecycle
LaunchedEffect(Unit) {
    lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.events.collect { /* ... */ }
    }
}
```

---

## 6. Use Single Source of Truth

```kotlin
// ✅ Good — ViewModel is the single source of truth
class TodoViewModel : ViewModel() {
    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()

    // All state changes go through ViewModel methods
    fun addTodo(title: String) { /* ... */ }
    fun toggleTodo(id: String) { /* ... */ }
    fun deleteTodo(id: String) { /* ... */ }
}

// ✅ Good — UI is stateless, sends events to ViewModel
@Composable
fun TodoScreen(viewModel: TodoViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TodoContent(state = state, onAdd = viewModel::addTodo, onToggle = viewModel::toggleTodo)
}
```

---

## 7. Avoid Anti-Patterns

```kotlin
// ❌ Anti-pattern: State write in composition
@Composable
fun BadExample() {
    var count by remember { mutableStateOf(0) }
    count++  // Write during composition → infinite loop!
}

// ❌ Anti-pattern: Creating new objects in composition
@Composable
fun BadColors() {
    Text("Hello", color = Color(0xFF0000FF))  // New Color each time
}

// ✅ Fix: Use remember or define outside
val BlueColor = Color(0xFF0000FF)

@Composable
fun GoodColors() {
    Text("Hello", color = BlueColor)
}

// ❌ Anti-pattern: Passing ViewModel to child composables
@Composable
fun Child(viewModel: MyViewModel) {  // Tight coupling!
    // ...
}

// ✅ Fix: Pass state and events (state hoisting)
@Composable
fun Child(state: ChildState, onAction: (ChildAction) -> Unit) {
    // ...
}

// ❌ Anti-pattern: Using LiveData with Compose
val name by viewModel.name.observeAsState()  // Legacy

// ✅ Fix: Use StateFlow
val name by viewModel.name.collectAsStateWithLifecycle()
```

---

## 8. Handle State Restoration Correctly

```kotlin
// ✅ Simple UI state → rememberSaveable
@Composable
fun FormField() {
    var text by rememberSaveable { mutableStateOf("") }
}

// ✅ Screen state → SavedStateHandle in ViewModel
@HiltViewModel
class FormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var name by savedStateHandle.saveable { mutableStateOf("") }
    var email by savedStateHandle.saveable { mutableStateOf("") }
}

// ✅ App preferences → DataStore
class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    val theme = dataStore.data.map { it[THEME_KEY] ?: "system" }
}
```

---

## 9. Optimize Recomposition

```kotlin
// ✅ Use stable types
@Immutable
data class UserList(val users: List<User>)

// ✅ Use keys in lists
LazyColumn {
    items(users, key = { it.id }) { user -> UserCard(user) }
}

// ✅ Defer state reads
Box(Modifier.offset { IntOffset(0, scrollState.value) })  // Layout phase, not composition

// ✅ Use derivedStateOf
val showButton by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}

// ✅ Remember lambdas
val onClick = remember { { id -> handleClick(id) } }
```

---

## 10. Checklist

```
✅ State is hoisted to the appropriate level
✅ State is immutable (data class with val)
✅ UiState is sealed class or data class
✅ State and events are separated (StateFlow vs Channel)
✅ collectAsStateWithLifecycle for StateFlow
✅ repeatOnLifecycle for SharedFlow/Channel
✅ ViewModel is single source of truth
✅ Composables are stateless (receive state, emit events)
✅ rememberSaveable for state that survives rotation
✅ SavedStateHandle for state that survives process death
✅ Stable types for skippable composables
✅ Keys in LazyColumn
✅ No state writes in composition
✅ No object creation in composition
✅ DerivedStateOf for computed state
✅ Deferred state reads for performance
```

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [State Hoisting](StateHoisting.md)
- [Comparison](Comparison.md)
