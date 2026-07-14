# Basics

## Q1: What is Jetpack Compose?

Jetpack Compose is Android's modern declarative UI toolkit for building native UI using Kotlin.

```kotlin
// Declarative — describe WHAT the UI looks like for a given state
@Composable
fun Greeting(name: String) {
    Text(text = "Hello, $name!")
}

// Imperative (old XML way) — describe HOW to update the UI
// val textView = findViewById<TextView>(R.id.text)
// textView.text = "Hello, $name"
```

### Key Differences from XML
| XML (Imperative) | Compose (Declarative) |
|-------------------|----------------------|
| `findViewById` + `setText` | `Text("Hello")` |
| XML layouts | `@Composable` functions |
| Manual sync state → UI | Auto recomposition |
| View tree | Slot table |
| `LayoutInflater` | Compiler plugin |

---

## Q2: What is a @Composable function?

A `@Composable` function describes a piece of UI. It can only be called from another `@Composable` function.

```kotlin
@Composable
fun ProfileCard(name: String, role: String) {
    Column {
        Text(text = name, style = MaterialTheme.typography.headlineMedium)
        Text(text = role, style = MaterialTheme.typography.bodyMedium)
    }
}

// Called from another composable
@Composable
fun App() {
    ProfileCard(name = "Alice", role = "Engineer")
}
```

### Rules
1. Must be annotated with `@Composable`
2. Can only be called from another `@Composable` function
3. Must not return a value (returns `Unit`)
4. Should be pure — same input → same output
5. Should have no side effects

```kotlin
// ❌ Bad — side effect in composable
@Composable
fun BadCounter() {
    var count = 0
    Button(onClick = { count++ }) {  // Lost on recomposition!
        Text("$count")
    }
}

// ✅ Good — remember state
@Composable
fun GoodCounter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("$count")
    }
}
```

---

## Q3: What is recomposition?

Recomposition is the process of re-executing composables when their inputs change, to update the UI.

```
Initial composition (first call):
  ProfileCard("Alice") → Text("Alice") added to slot table

State changes (name = "Bob"):
  Recomposition → ProfileCard("Bob") → Text("Bob") replaces old

If inputs don't change → no recomposition (skipped)
```

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    // Text recomposes when count changes
    Text("$count")

    // Button does NOT recompose (inputs unchanged)
    Button(onClick = { count++ }) {
        Text("Click")
    }
}
```

### When does recomposition happen?
- State read by a composable changes
- Input parameters change
- Parent recomposes and passes new values

### When is it skipped?
- Inputs are the same (`==` check)
- Composable is not in the affected subtree

---

## Q4: What is the difference between composition and recomposition?

| Composition | Recomposition |
|-------------|---------------|
| First execution | Subsequent executions |
| Builds slot table | Updates slot table |
| Creates initial UI | Updates changed parts |
| Happens once | Happens on state change |

```
App starts → Composition (build UI)
  ↓
State changes → Recomposition (update UI)
  ↓
State changes → Recomposition (update UI)
  ↓
App exits → Disposal (remove from tree)
```

---

## Q5: What are the phases of Compose?

```
1. Composition — Execute @Composable functions, build slot table
2. Layout — Measure and place composables
3. Drawing — Render to canvas
```

```kotlin
// Phase 1: Composition
@Composable
fun MyWidget(text: String) {
    Text(text)  // Added to slot table
}

// Phase 2: Layout
// Text measures itself → parent places it

// Phase 3: Drawing
// Text is drawn to canvas (Skia)

// State change triggers:
// Recomposition (Phase 1) → Relayout (Phase 2) → Redraw (Phase 3)
// Only affected phases run
```

### Optimizing by Phase
```kotlin
// ❌ Bad — reading state in lambda defers to layout phase
@Composable
fun BadScroll(scroll: ScrollState) {
    Box(Modifier.offset { IntOffset(0, scroll.value) })  // Reads in layout
}

// ✅ Good — read state in composition phase
@Composable
fun GoodScroll(scroll: ScrollState) {
    val offset = scroll.value  // Read in composition
    Box(Modifier.offset(y = offset.dp))
}
```

---

## Q6: What is `remember` and why is it needed?

`remember` stores a value across recompositions. Without it, values are lost on every recomposition.

```kotlin
@Composable
fun Counter() {
    // ❌ Without remember — count resets to 0 on every recomposition
    var count = 0
    Button(onClick = { count++ }) {
        Text("$count")  // Always shows 0
    }

    // ✅ With remember — count persists across recompositions
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("$count")  // Increments correctly
    }
}
```

### Types of remember
```kotlin
// remember — survives recomposition (not config changes)
val color = remember { Color.Red }

// rememberSaveable — survives recomposition + config changes + process death
var name by rememberSaveable { mutableStateOf("") }

// remember with calculation
val formatted = remember(key) { formatData(key) }  // Recalculates when key changes

// remember with keys
val derived = remember(key1, key2) { compute(key1, key2) }
```

---

## Q7: What is `mutableStateOf`?

`mutableStateOf` creates an observable state that triggers recomposition when changed.

```kotlin
// Different ways to declare state
val count = mutableStateOf(0)        // State<Int>
count.value = 1                       // Set value
count.value                           // Get value

// With by delegate (cleaner)
var count by mutableStateOf(0)        // Int
count = 1                             // Set value
count                                  // Get value

// With remember
var count by remember { mutableStateOf(0) }

// Different types
var name by remember { mutableStateOf("Alice") }
var items by remember { mutableStateOf(listOf<String>()) }
var isEnabled by remember { mutableStateOf(true) }
var user by remember { mutableStateOf(User("Alice")) }
```

### State Triggers Recomposition
```kotlin
@Composable
fun NameDisplay() {
    var name by remember { mutableStateOf("Alice") }

    Column {
        Text(name)  // Recomposes when name changes
        Button(onClick = { name = "Bob" }) {
            Text("Change Name")
        }
    }
}
```

---

## 🔗 Related Topics
- [Composables](Composables.md)
- [State](State.md)
- [Modifiers](Modifiers.md)
