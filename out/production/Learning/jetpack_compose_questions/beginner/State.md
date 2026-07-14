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

## 🔗 Related Topics
- [Basics](Basics.md)
- [Side Effects](SideEffects.md)
- [State Hoisting](../state_management/StateHoisting.md)
