# State Hoisting

## Q1: What is state hoisting?

State hoisting is the pattern of moving state out of a composable to make it stateless and reusable.

```kotlin
// ❌ Stateful — state is inside, hard to test/reuse
@Composable
fun StatefulCounter() {
    var count by remember { mutableStateOf(0) }  // State inside
    Button(onClick = { count++ }) { Text("$count") }
}

// ✅ Stateless — state is hoisted to parent
@Composable
fun StatelessCounter(
    count: Int,              // State passed in
    onIncrement: () -> Unit, // Events passed out
) {
    Button(onClick = onIncrement) { Text("$count") }
}

// Parent owns the state
@Composable
fun CounterParent() {
    var count by remember { mutableStateOf(0) }
    StatelessCounter(count = count, onIncrement = { count++ })
}
```

### Benefits of state hoisting
- **Reusable** — same composable works with different state sources
- **Testable** — pass state directly, no setup needed
- **Predictable** — same input → same output
- **Composable** — can combine with other stateless composables

---

## Q2: What are the rules of state hoisting?

```
Rule 1: State goes down (parent → child)
Rule 2: Events go up (child → parent)
Rule 3: Parent owns the state
```

```kotlin
// Rule 1 & 2: State down, events up
@Composable
fun TextField(
    value: String,              // State: parent → child
    onValueChange: (String) -> Unit,  // Event: child → parent
)

// Rule 3: Parent owns state
@Composable
fun Parent() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,                    // Pass state down
        onValueChange = { text = it },   // Receive event up
    )
}
```

### Visual representation
```
┌─────────────────────────────┐
│  Parent (owns state)         │
│  var text by remember { }    │
│                              │
│  ┌───────────────────────┐  │
│  │  Child (stateless)     │  │
│  │  TextField(            │  │
│  │    value = text,  ←─── │──│── State goes DOWN
│  │    onValueChange = {   │  │
│  │      text = it  ────── │──│── Event goes UP
│  │    }                   │  │
│  │  )                     │  │
│  └───────────────────────┘  │
└─────────────────────────────┘
```

---

## Q3: How do you hoist different types of state?

```kotlin
// 1. Simple value
@Composable
fun NameInput(
    name: String,
    onNameChange: (String) -> Unit,
) {
    TextField(value = name, onValueChange = onNameChange)
}

// 2. Boolean toggle
@Composable
fun SwitchRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(checked = checked, onCheckedChange = onCheckedChange)
}

// 3. Selection
@Composable
fun RadioButtonRow(
    selected: Boolean,
    onSelect: () -> Unit,
) {
    RadioButton(selected = selected, onClick = onSelect)
}

// 4. List selection
@Composable
fun DropdownMenu(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
) {
    // ...
}

// 5. Complex state
@Composable
fun FormField(
    state: FormFieldState,
    onStateChange: (FormFieldState) -> Unit,
) {
    Column {
        TextField(value = state.value, onValueChange = { onStateChange(state.copy(value = it)) })
        if (state.error != null) Text(state.error, color = Color.Red)
    }
}
```

---

## Q4: How far should you hoist state?

```kotlin
// Hoist to the lowest common parent that needs the state

// Two siblings need the same state → hoist to parent
@Composable
fun Parent() {
    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {  // Sibling 1 reads state
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Tab 1") }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Tab 2") }
        }
        when (selectedTab) {  // Sibling 2 reads state
            0 -> Screen1()
            1 -> Screen2()
        }
    }
}

// Only one composable needs it → keep it local
@Composable
fun ExpandableCard() {
    var expanded by remember { mutableStateOf(false) }  // Only this composable needs it
    Column {
        Button(onClick = { expanded = !expanded }) { Text(if (expanded) "Collapse" else "Expand") }
        if (expanded) Text("Expanded content")
    }
}

// Screen-level state → ViewModel
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ViewModel owns state, survives config changes
}
```

### Hoisting levels
```
Local (remember)        → Only one composable needs it
Parent composable       → Multiple siblings need it
ViewModel               → Screen-level, business logic, survives rotation
SavedStateHandle        → Screen-level, survives process death
Repository/DataStore    → App-level, persistent
```

---

## Q5: How do you handle stateful vs stateless composables?

```kotlin
// Stateless composable — pure, reusable
@Composable
fun StatelessSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Slider(value = value, onValueChange = onValueChange)
}

// Stateful wrapper — provides state for convenience
@Composable
fun StatefulSlider(
    initialValue: Float = 0f,
    onValueChange: (Float) -> Unit,
) {
    var value by remember { mutableStateOf(initialValue) }
    StatelessSlider(value = value) { newValue ->
        value = newValue
        onValueChange(newValue)
    }
}

// Usage: use stateless when you need control, stateful for convenience
@Composable
fun Screen() {
    var volume by remember { mutableStateOf(0.5f) }

    // Stateless — full control
    StatelessSlider(value = volume, onValueChange = { volume = it })

    // Stateful — quick setup
    StatefulSlider(initialValue = 0.5f) { newVolume -> /* ... */ }
}
```

### Pattern: stateless + stateful wrapper
```kotlin
// Stateless core (testable, reusable)
@Composable
fun CounterCore(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row {
        Button(onClick = onDecrement) { Text("-") }
        Text("$count")
        Button(onClick = onIncrement) { Text("+") }
    }
}

// Stateful wrapper (convenient)
@Composable
fun Counter(initial: Int = 0) {
    var count by remember { mutableStateOf(initial) }
    CounterCore(
        count = count,
        onIncrement = { count++ },
        onDecrement = { count-- },
    )
}
```

---

## Q6: How does state hoisting enable unidirectional data flow?

```
Unidirectional Data Flow (UDF):

    ┌──────────┐  State (down)   ┌──────────┐
    │ ViewModel │ ──────────────→ │   UI     │
    │           │                  │(Composable)│
    │           │ ←────────────── │           │
    └──────────┘  Events (up)     └──────────┘
```

```kotlin
// ViewModel (state owner)
class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() { _count.value++ }
    fun decrement() { _count.value-- }
}

// UI (stateless, displays state, sends events)
@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel()) {
    val count by viewModel.count.collectAsStateWithLifecycle()

    CounterCore(
        count = count,           // State down
        onIncrement = viewModel::increment,  // Event up
        onDecrement = viewModel::decrement,
    )
}
```

### UDF benefits
- **Single source of truth** — ViewModel owns state
- **Predictable** — state changes only through events
- **Testable** — test ViewModel and UI separately
- **Debuggable** — trace events → state changes → UI

---

## Q7: What are common state hoisting mistakes?

```kotlin
// ❌ Mistake 1: Not hoisting when siblings need state
@Composable
fun BadParent() {
    Child1()  // Each has own state
    Child2()  // Can't share
}

@Composable
fun Child1() {
    var selected by remember { mutableStateOf(0) }  // Local state
}

// ✅ Fix: Hoist to parent
@Composable
fun GoodParent() {
    var selected by remember { mutableStateOf(0) }
    Child1(selected) { selected = it }
    Child2(selected)
}

// ❌ Mistake 2: Passing state that child doesn't need
@Composable
fun BadParent() {
    var user by remember { mutableStateOf<User?>(null) }
    // Passing entire user when child only needs name
    NameDisplay(user)
}

@Composable
fun NameDisplay(user: User?) {
    Text(user?.name ?: "")
}

// ✅ Fix: Pass only what's needed
@Composable
fun GoodParent() {
    var user by remember { mutableStateOf<User?>(null) }
    NameDisplay(user?.name)
}

@Composable
fun NameDisplay(name: String?) {
    Text(name ?: "")
}

// ❌ Mistake 3: Creating new lambdas each recomposition
@Composable
fun BadParent() {
    Child(onClick = { doSomething() })  // New lambda each time
}

// ✅ Fix: Remember lambda or pass reference
@Composable
fun GoodParent() {
    val onClick = remember { { doSomething() } }
    Child(onClick = onClick)
}
```

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [ViewModel](ViewModel.md)
- [Best Practices](BestPractices.md)
