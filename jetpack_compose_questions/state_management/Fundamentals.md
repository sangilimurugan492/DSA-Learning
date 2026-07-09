# State Management Fundamentals

## Q1: What is state in Compose?

State is any value that can change over time and triggers UI updates.

```kotlin
// State — a value that Compose observes
val count = mutableStateOf(0)  // State<Int>

// Reading state in composable → Compose tracks the read
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }  // Create state
    Text("$count")  // Read state → Compose knows to recompose when count changes
    Button(onClick = { count++ }) { Text("Increment") }  // Write state → triggers recomposition
}
```

### State types
```kotlin
// mutableStateOf — single value
val text = mutableStateOf("Hello")

// mutableStateListOf — observable list
val items = mutableStateListOf<Item>()

// mutableStateMapOf — observable map
val map = mutableStateMapOf<String, Item>()

// derivedStateOf — computed from other state
val isValid = derivedStateOf { name.value.isNotBlank() && email.value.contains("@") }
```

---

## Q2: How does mutableStateOf work internally?

```kotlin
// mutableStateOf creates a SnapshotState — observed by the snapshot system

val count = mutableStateOf(0)

// When you read count.value:
// 1. Compose records which composable read this state
// 2. Associates composable with this state object

// When you write count.value:
// 1. Snapshot system records the change
// 2. Notifies all composables that read this state
// 3. Schedules recomposition for next frame

// State read tracking
@Composable
fun Example() {
    val count by remember { mutableStateOf(0) }
    // Compose tracks: "Example() read count"
    Text("$count")
    // If count changes → Example() recomposes
}

// Only composables that READ the state recompose
@Composable
fun Parent() {
    val count by remember { mutableStateOf(0) }
    Child1(count)  // Reads count → recomposes
    Child2()  // Doesn't read count → doesn't recompose
}
```

---

## Q3: What is remember and why is it needed?

```kotlin
// Without remember — state is lost on recomposition
@Composable
fun BadCounter() {
    var count = mutableStateOf(0)  // New instance every recomposition!
    // count is always 0 because it's recreated
    Button(onClick = { count.value++ }) { Text("${count.value}") }
}

// With remember — state persists across recompositions
@Composable
fun GoodCounter() {
    var count by remember { mutableStateOf(0) }  // Stored in slot table
    Button(onClick = { count++ }) { Text("$count") }
}

// remember with key — recomputes when key changes
@Composable
fun UserCard(userId: String) {
    val user = remember(userId) { fetchUser(userId) }  // Refetch when userId changes
    Text(user.name)
}

// rememberSaveable — survives configuration changes (rotation)
@Composable
fun FormField() {
    var text by rememberSaveable { mutableStateOf("") }
    TextField(value = text, onValueChange = { text = it })
}
```

### remember lifecycle
```
First composition → remember stores value in slot table
  ↓
Recomposition → remember reads from slot table (same value)
  ↓
Composable leaves composition → slot table entry removed → value lost
  ↓
(rememberSaveable) Configuration change → value saved to Bundle → restored
```

---

## Q4: What is the snapshot system?

The snapshot system is Compose's mechanism for tracking state changes and triggering recomposition.

```kotlin
// Snapshots provide a consistent view of state
val state1 = mutableStateOf(0)
val state2 = mutableStateOf("")

// Take a snapshot — read state at a point in time
val snapshot = Snapshot.takeSnapshot()
val value1 = snapshot.read { state1.value }  // Read at snapshot point
val value2 = snapshot.read { state2.value }
snapshot.dispose()

// Mutable snapshot — batch writes atomically
Snapshot.withMutableSnapshot {
    state1.value = 1
    state2.value = "hello"
    // Both changes applied together → one recomposition
}

// Nested snapshots
Snapshot.withMutableSnapshot {
    state1.value = 1
    Snapshot.withMutableSnapshot {
        state2.value = "hello"
    }
    // Both applied together
}
```

### Snapshot flow
```
1. State write → Snapshot records change
2. Snapshot.apply() → Notifies listeners
3. Composer receives notification → Marks composables as invalid
4. Next frame → Recompose only invalid composables
5. During recomposition → Read state → Track new reads
```

---

## Q5: What is state read tracking?

Compose only recomposes composables that actually READ the changed state.

```kotlin
@Composable
fun Parent() {
    var count by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("Alice") }

    Column {
        // Reads count → recomposes when count changes
        CountDisplay(count)

        // Reads name → recomposes when name changes
        NameDisplay(name)

        // Doesn't read any state → never recomposes
        StaticText()

        // Reads count → recomposes when count changes (not name)
        CountButton(count) { count++ }
    }
}

@Composable
fun CountDisplay(count: Int) { Text("Count: $count") }

@Composable
fun NameDisplay(name: String) { Text("Name: $name") }

@Composable
fun StaticText() { Text("I never change") }

@Composable
fun CountButton(count: Int, onClick: () -> Unit) {
    Button(onClick = onClick) { Text("Count: $count") }
}
```

### Key insight: Only composables that READ state recompose
```
count changes → CountDisplay + CountButton recompose
name changes → NameDisplay recomposes
Neither changes → StaticText never recomposes
```

---

## Q6: What are the different state APIs?

```kotlin
// 1. mutableStateOf — basic state
val state = remember { mutableStateOf(0) }
state.value = 5
// or with by delegate
var count by remember { mutableStateOf(0) }
count = 5

// 2. mutableStateListOf — observable list
val items = remember { mutableStateListOf<Item>() }
items.add(Item())  // Triggers recomposition
items.removeAt(0)  // Triggers recomposition

// 3. mutableStateMapOf — observable map
val map = remember { mutableStateMapOf<String, Item>() }
map["key"] = Item()  // Triggers recomposition

// 4. derivedStateOf — computed state
val isValid by remember {
    derivedStateOf {
        name.isNotBlank() && email.contains("@") && password.length >= 6
    }
}

// 5. produceState — async state
val image by produceState<ImageState>(ImageState.Loading, url) {
    value = ImageState.Success(loadImage(url))
}

// 6. snapshotFlow — State → Flow
LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
        .collect { /* ... */ }
}
```

---

## Q7: How do you choose the right state API?

```
Decision tree:

1. Is it simple local state?
   → remember { mutableStateOf() }

2. Does it need to survive rotation?
   → rememberSaveable { mutableStateOf() }

3. Is it a list/map?
   → mutableStateListOf() / mutableStateMapOf()

4. Is it computed from other state?
   → derivedStateOf { }

5. Is it async loaded?
   → produceState { }

6. Does it need to survive process death?
   → SavedStateHandle in ViewModel

7. Is it screen-level with business logic?
   → ViewModel + StateFlow

8. Is it app-level persistent?
   → DataStore / Room
```

### State API comparison
| API | Scope | Survives Rotation | Survives Process Death | Use Case |
|-----|-------|-----------------|----------------------|----------|
| `remember` | Composable | ❌ | ❌ | Simple local state |
| `rememberSaveable` | Composable | ✅ | ✅ | Form fields, scroll |
| `mutableStateListOf` | Composable | ❌ | ❌ | Observable list |
| `derivedStateOf` | Composable | N/A | N/A | Computed state |
| `ViewModel` | Screen | ✅ | ❌ | Business logic |
| `SavedStateHandle` | Screen | ✅ | ✅ | Critical state |
| `DataStore` | App | ✅ | ✅ | User preferences |

---

## 🔗 Related Topics
- [State Hoisting](StateHoisting.md)
- [ViewModel](ViewModel.md)
- [Comparison](Comparison.md)
