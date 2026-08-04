# State

## Q1: What is state in Compose?

State is any value that can change over time and affects the UI.

```kotlin
// Examples of state
var count by remember { mutableStateOf(0) }        // Counter
var name by remember { mutableStateOf("") }          // Text input
var isLoading by remember { mutableStateOf(false) }  // Loading flag
var items by remember { mutableStateOf(listOf<String>()) }  // List
var selectedTab by remember { mutableStateOf(0) }     // Tab index
```

### State triggers recomposition
```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }  // State

    Button(onClick = { count++ }) {  // State changes → recomposition
        Text("$count")  // UI reflects new state
    }
}
```

---

## Q2: What is the difference between remember and rememberSaveable?

| `remember` | `rememberSaveable` |
|------------|---------------------|
| Survives recomposition | Survives recomposition + config changes + process death |
| Lost on rotation | Saved on rotation |
| Lost on process death | Saved on process death |
| Any type | Must be saveable (primitives, Parcelable, custom Saver) |

```kotlin
// remember — lost on config change (rotation)
var count by remember { mutableStateOf(0) }

// rememberSaveable — survives rotation and process death
var name by rememberSaveable { mutableStateOf("") }
var count by rememberSaveable { mutableStateOf(0) }
var isChecked by rememberSaveable { mutableStateOf(false) }

// rememberSaveable with custom Saver
val savedList = rememberSaveable(
    saver = listSaver(
        save = { it.toTypedArray() },
        restore = { it.toList() },
    ),
) { mutableStateOf(listOf("Item 1", "Item 2")) }
```

---

## Q3: What is state hoisting?

State hoisting moves state from a child composable to its parent, making the child stateless.

```kotlin
// Stateless composable — no internal state, all passed in
@Composable
fun NameInput(name: String, onNameChange: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name") },
    )
}

// Stateful composable — holds state, passes to stateless
@Composable
fun NameForm() {
    var name by remember { mutableStateOf("") }
    NameInput(name = name, onNameChange = { name = it })
}
```

### State hoisting pattern
```
Stateful (parent)          Stateless (child)
┌──────────────┐          ┌──────────────┐
│ var name     │ ────→    │ name: String  │
│              │ ←────    │ onNameChange  │
└──────────────┘          └──────────────┘
```

### Benefits
- Stateless composables are reusable and testable
- Single source of truth
- Easier to reason about state

---

## Q4: What is `mutableStateListOf` and `mutableStateMapOf`?

```kotlin
// mutableStateListOf — observable list (triggers recomposition on add/remove)
val items = remember { mutableStateListOf<String>() }

items.add("New Item")      // Recomposition triggered
items.removeAt(0)          // Recomposition triggered
items[0] = "Updated"       // Recomposition triggered

// ❌ Bad — listOf doesn't trigger recomposition
val items = remember { mutableStateOf(listOf<String>()) }
items.value = items.value + "New"  // Must reassign entire list

// ✅ Good — mutableStateListOf
val items = remember { mutableStateListOf<String>() }
items.add("New")  // Auto-triggers recomposition

// mutableStateMapOf — observable map
val map = remember { mutableStateMapOf<String, Int>() }
map["key"] = 42  // Recomposition triggered
```

---

## Q5: How do you use `derivedStateOf`?

`derivedStateOf` creates a computed state that only recomposes when the derived value changes.

```kotlin
@Composable
fun TodoList() {
    val todos = remember { mutableStateListOf<Todo>() }
    val completedCount by remember {
        derivedStateOf { todos.count { it.isDone } }  // Only recomputes when count changes
    }

    Column {
        Text("Completed: $completedCount")
        // Only recomposes when completedCount actually changes
    }
}

// Scroll-based state
val listState = rememberLazyListState()
val showScrollToTop by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}

// Only recomposes when showScrollToTop changes (true ↔ false)
if (showScrollToTop) {
    FloatingActionButton(onClick = { /* scroll to top */ }) {
        Icon(Icons.Default.ArrowUpward, contentDescription = "Top")
    }
}
```

### When to use derivedStateOf?
- Computed value from one or more states
- Value changes infrequently compared to source state
- Avoid unnecessary recompositions

---

## Q6: What is `produceState`?

`produceState` converts non-Compose state (like Flow, LiveData) into Compose state.

```kotlin
// Convert Flow to State
@Composable
fun UserScreen(viewModel: UserViewModel) {
    val user by produceState<User?>(initialValue = null) {
        viewModel.userFlow.collect { value = it }
    }

    if (user != null) Text(user!!.name) else CircularProgressIndicator()
}

// Load data with produceState
@Composable
fun ImageScreen(url: String) {
    val image by produceState<ImageState>(initialValue = ImageState.Loading) {
        value = ImageState.Loading
        try {
            val bitmap = loadImage(url)
            value = ImageState.Success(bitmap)
        } catch (e: Exception) {
            value = ImageState.Error(e)
        }
    }

    when (image) {
        is ImageState.Loading -> CircularProgressIndicator()
        is ImageState.Success -> Image(bitmap = (image as ImageState.Success).bitmap)
        is ImageState.Error -> Text("Error")
    }
}
```

---

## Q7: How do you share state between composables?

```kotlin
// 1. Pass state down + callbacks up (hoisting)
@Composable
fun Parent() {
    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Tab 1") }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Tab 2") }
        }
        when (selectedTab) {
            0 -> Screen1()
            1 -> Screen2()
        }
    }
}

// 2. CompositionLocal — for widely shared state
val LocalTheme = staticCompositionLocalOf { Theme.Light }

@Composable
fun App() {
    CompositionLocalProvider(LocalTheme provides Theme.Dark) {
        Child()  // Can read LocalTheme.current
    }
}

@Composable
fun Child() {
    val theme = LocalTheme.current  // Read shared state
}

// 3. ViewModel — for screen-level state
@Composable
fun CartScreen(viewModel: CartViewModel = viewModel()) {
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    CartContent(items = cart.items, onRemove = viewModel::removeItem)
}
```

---

## Q8: What is state hoisting and why is it important?

```kotlin
// State hoisting — move state up to parent, pass events down
// Pattern: stateless composable receives state + callback

// ❌ Without hoisting — composable manages its own state (hard to test/reuse)
@Composable
fun NameInputBad() {
    var name by remember { mutableStateOf("") }  // State inside
    TextField(value = name, onValueChange = { name = it })
}

// ✅ With hoisting — state is passed in, events passed out
@Composable
fun NameInput(
    name: String,              // State from parent
    onNameChange: (String) -> Unit,  // Event to parent
) {
    TextField(value = name, onValueChange = onNameChange)
}

// Parent owns the state
@Composable
fun FormScreen() {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    Column {
        NameInput(name = name, onNameChange = { name = it })
        EmailInput(email = email, onEmailChange = { email = it })
        Button(onClick = { /* submit(name, email) */ }) {
            Text("Submit")
        }
    }
}
```

### Benefits of state hoisting
| Benefit | Description |
|---------|-------------|
| Reusable | Same composable works with different state sources |
| Testable | Pass state directly, no internal state to mock |
| Single source of truth | Parent owns state, no duplication |
| Decoupled | UI logic separate from business logic |

> **Rule:** A composable should be stateless whenever possible. State should be hoisted to the lowest common ancestor that needs it. This is the core principle of unidirectional data flow in Compose.

---

## Q9: What is `snapshotFlow` and how do you use it?

```kotlin
// snapshotFlow — convert Compose State into a Flow
// Opposite of produceState — State → Flow

@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<Item>()) }

    // Convert state changes to Flow — debounce + filter
    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(300)  // Wait 300ms after user stops typing
            .filter { it.length >= 2 }
            .distinctUntilChanged()
            .flatMapLatest { searchApi.search(it) }
            .collect { results = it }
    }

    Column {
        TextField(value = query, onValueChange = { query = it })
        LazyColumn { items(results) { Text(it.name) } }
    }
}

// Another use — react to scroll position
@Composable
fun LazyColumnWithFab() {
    val listState = rememberLazyListState()
    val showFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    // Or use snapshotFlow for more complex logic
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
        .filter { it.first > 5 }
        .collect { /* track scroll depth analytics */ }
    }

    Box {
        LazyColumn(state = listState) { /* items */ }
        if (showFab) {
            FloatingActionButton(onClick = { /* scroll to top */ }) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Top")
            }
        }
    }
}
```

> **Key:** `snapshotFlow` converts Compose `State` reads into a `Flow` — enabling operators like `debounce`, `filter`, `distinctUntilChanged`. Use it when you need Flow operators on state changes (search-as-you-type, analytics, complex state-derived effects).

---

## Q10: How do you save and restore UI state with `Saveable`?

```kotlin
// rememberSaveable — saves to Bundle, survives process death
@Composable
fun FormScreen() {
    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf(0) }
    var isChecked by rememberSaveable { mutableStateOf(false) }
}

// Save lists
@Composable
fun ListScreen() {
    val items = rememberSaveable(
        saver = listSaver(
            save = { it.toTypedArray() },
            restore = { it.toList() },
        ),
    ) { mutableStateOf(listOf("A", "B", "C")) }
}

// Save custom objects with Saver
data class User(val name: String, val age: Int)

val UserSaver = run {
    mapSaver(
        save = { mapOf("name" to it.name, "age" to it.age) },
        restore = { User(it["name"] as String, it["age"] as Int) },
    )
}

@Composable
fun UserScreen() {
    var user by rememberSaveable(stateSaver = UserSaver) {
        mutableStateOf(User("Alice", 30))
    }
}

// Save scroll position
@Composable
fun ScrollScreen() {
    val scrollState = rememberScrollState()
    // ScrollState is already Saveable — position survives rotation
    Column(Modifier.verticalScroll(scrollState)) {
        repeat(100) { Text("Item $it") }
    }
}

// Save LazyListState
@Composable
fun LazyListScreen() {
    var listState by rememberSaveable(saver = LazyListState.Saver) {
        mutableStateOf(LazyListState(0, 0))
    }
    LazyColumn(state = listState) { /* items */ }
}
```

| What to Save | How |
|-------------|-----|
| Primitives | `rememberSaveable { mutableStateOf(value) }` |
| String, Int, Boolean | Auto-saved |
| List | `listSaver` |
| Custom object | `mapSaver` or custom `Saver` |
| Scroll position | `rememberScrollState()` (auto-saved) |
| Parcelable | Auto-saved |

> **Rule:** Use `rememberSaveable` for any state the user would expect to survive — form inputs, scroll position, selected items. Use `remember` for transient UI state (animation progress, hover state).

---

## Q11: What is `snapshotFlow` and how do you convert Compose state to Flow?

```kotlin
// snapshotFlow — convert Compose State<T> into a cold Flow<T>
// Emits when the state value changes, deduplicates consecutive values

@Composable
fun ScrollToTopEffect(listState: LazyListState) {
    // Convert scroll state to Flow — only emits when at top
    LaunchedEffect(Unit) {
        snapshotFlow { listState.firstVisibleItemIndex == 0 }
            .distinctUntilChanged()
            .collect { isAtTop ->
                if (isAtTop) showTopBar() else hideTopBar()
            }
    }
}

// Debounce scroll events
@Composable
fun SearchOnScrollStop(listState: LazyListState) {
    LaunchedEffect(Unit) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .debounce(500)  // Wait 500ms after scroll stops
            .distinctUntilChanged()
            .collect { index ->
                analytics.trackScrollPosition(index)
            }
    }
}

// Combine multiple states
@Composable
fun FormValidation(
    email: String,
    password: String,
) {
    LaunchedEffect(Unit) {
        snapshotFlow {
            email.isValidEmail() && password.length >= 8
        }
            .distinctUntilChanged()
            .collect { isValid ->
                if (isValid) enableSubmitButton()
            }
    }
}
```

> **Key:** `snapshotFlow` bridges Compose's snapshot system with Kotlin Flows. It only emits when the snapshot read inside the lambda changes. Use it for debouncing, throttling, or combining multiple state reads with Flow operators.

---

## Q12: What is `mutableStateListOf` and `mutableStateMapOf`?

```kotlin
// mutableStateListOf — observable list that triggers recomposition on change
@Composable
fun TodoList() {
    val todos = remember { mutableStateListOf<Todo>() }

    Column {
        // Adding/removing items triggers recomposition automatically
        todos.forEach { todo ->
            TodoItem(todo)
        }

        Button(onClick = { todos.add(Todo("New task")) }) {
            Text("Add")
        }
    }
}

// mutableStateMapOf — observable map
@Composable
fun CartScreen() {
    val cart = remember { mutableStateMapOf<String, Int>() }

    Column {
        cart.forEach { (productId, quantity) ->
            Text("$productId: $quantity")
        }

        Button(onClick = { cart["item1"] = cart.getOrPut("item1") { 0 } + 1 }) {
            Text("Add to cart")
        }
    }
}
```

| Collection | Regular | Observable |
|------------|--------|-----------|
| List | `mutableListOf()` | `mutableStateListOf()` |
| Map | `mutableMapOf()` | `mutableStateMapOf()` |
| Rebuilds on change | ❌ | ✅ |
| Use case | Non-UI data | UI state |

> **Important:** `mutableStateListOf` and `mutableStateMapOf` trigger recomposition on structural changes (add, remove, set). For large lists, prefer using a `mutableStateOf(listOf(...))` and replacing the entire list — it's more efficient because Compose can diff the old and new lists.

---

## Q13: What is `rememberCoroutineScope` and how does it differ from `LaunchedEffect`?

```kotlin
// rememberCoroutineScope — get a coroutine scope tied to the composition
// Survives recomposition but cancels when composable leaves

@Composable
fun MyScreen() {
    val scope = rememberCoroutineScope()

    Button(onClick = {
        // Launch from event handler (not composition)
        scope.launch {
            delay(1000)
            // Do async work
        }
    }) {
        Text("Start")
    }
}

// LaunchedEffect — auto-launches and cancels with key
@Composable
fun MyScreen(userId: String) {
    LaunchedEffect(userId) {
        // Auto-launches when userId changes
        // Auto-cancels when userId changes or composable leaves
        val user = api.fetchUser(userId)
        // Update state
    }
}
```

| Feature | `LaunchedEffect` | `rememberCoroutineScope` |
|---------|-----------------|------------------------|
| Launches | Automatically (on composition) | Manually (in event handlers) |
| Cancel trigger | Key change or leave composition | Composable leaves composition |
| Use case | Side effects tied to composition | User-triggered async work |
| Survives recomposition | Yes (same key) | Yes |
| Key-based restart | ✅ Yes | ❌ No |

> **Best Practice:** Use `LaunchedEffect` for side effects that should run on composition (fetching data, subscribing). Use `rememberCoroutineScope` for user-triggered actions (button click → API call). Never launch coroutines directly in composition — always use these APIs.

---

## Q14: How do you test state in Compose?

```kotlin
// Testing state — use ComposeTestRule
class CounterTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `counter increments on button click`() {
        composeRule.setContent {
            CounterScreen()
        }

        // Initial state
        composeRule.onNodeWithText("Count: 0").assertIsDisplayed()

        // Click button
        composeRule.onNodeWithText("Increment").performClick()

        // Verify state changed
        composeRule.onNodeWithText("Count: 1").assertIsDisplayed()
    }
}

// Testing ViewModel state
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val data = repository.getData()
            _state.value = UiState(data = data, isLoading = false)
        }
    }
}

class ViewModelTest {
    @Test
    fun `loadData updates state`() = runTest {
        val repository = mockk<Repository>()
        every { repository.getData() } returns listOf("Item 1", "Item 2")
        val viewModel = MyViewModel(repository)

        viewModel.loadData()

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(listOf("Item 1", "Item 2"), viewModel.state.value.data)
    }
}

// Testing state restoration
@Test
fun `state survives configuration change`() {
    composeRule.setContent {
        CounterScreen()
    }

    composeRule.onNodeWithText("Increment").performClick()
    composeRule.onNodeWithText("Increment").performClick()

    // Simulate recreation
    composeRule.setContent {
        CounterScreen()
    }

    // State should be restored
    composeRule.onNodeWithText("Count: 2").assertIsDisplayed()
}
```

> **Key:** Test state in isolation by testing ViewModels directly (unit tests). For UI tests, use `ComposeTestRule` to interact with composables and assert state-driven UI. Always test `rememberSaveable` state restoration by recreating the composition.

---

## Q15: What is `snapshotWithMutableRead` and how does Compose's snapshot system work?

```kotlin
// Compose's snapshot system — the core of how state changes trigger recomposition

// 1. State reads are tracked
@Composable
fun TrackedReads() {
    val name by remember { mutableStateOf("Alice") }
    // Compose records that this composable read `name`
    Text(name)  // If name changes, only this composable recomposes
}

// 2. State writes are tracked
fun updateState() {
    val state = mutableStateOf(0)
    state.value = 1  // Triggers snapshot — all readers of `state` are marked invalid
}

// 3. Snapshot — a point-in-time view of all state
val snapshot = Snapshot.takeMutableSnapshot()
snapshot.enter {
    // All state reads/writes in this block are tracked
    // Changes are applied atomically when the block exits
}

// 4. Apply changes
snapshot.apply()

// Global snapshot — runs on every frame
// Compose batches state changes and applies them together for efficiency

// Manual snapshot (advanced)
Snapshot.withMutableSnapshot {
    // Multiple state changes in one atomic update
    nameState.value = "Bob"
    ageState.value = 30
    // Both changes trigger ONE recomposition, not two
}
```

### How recomposition works:
```
State change → Snapshot marks readers as invalid
    → Next frame: Compose recomposes invalid composables
    → Only composables that READ the changed state recompose
    → Children that don't read the state are skipped
```

> **Deep Dive:** Compose's snapshot system is like a version control system for state. Every `mutableStateOf` creates a snapshot-aware value. When you read state in a composable, Compose tracks the read. When you write, it marks all readers as invalid. On the next frame, only invalid composables recompose. This is why Compose is so efficient — it's surgical about what recomposes.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Side Effects](SideEffects.md)
- [State Hoisting](../state_management/StateHoisting.md)
