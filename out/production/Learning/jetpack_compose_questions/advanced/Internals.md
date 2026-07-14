# Internals

## Q1: What is the slot table?

The slot table is Compose's internal data structure that stores the UI tree, state, and position of composables.

```
Slot Table (linear array):
┌──────┬──────┬──────┬──────┬──────┬──────┐
│ Col  │ Text │ Text │ Btn  │ Text │ ...  │
│      │"Hi"  │"Bye" │      │"OK"  │      │
└──────┴──────┴──────┴──────┴──────┴──────┘

Each slot stores:
- Composable type
- Parameters
- State (remember values)
- Children positions
```

```kotlin
// When you write:
@Composable
fun MyScreen() {
    Column {
        Text("Hello")
        var count by remember { mutableStateOf(0) }
        Button(onClick = { count++ }) { Text("$count") }
    }
}

// Slot table stores:
// [Column] → [Text("Hello")] → [remember: 0] → [Button] → [Text("0")]
// On recomposition, Compose diffs old vs new and updates only changed slots
```

### Why slot table?
- **Efficient diffing** — compare old vs new, update only changes
- **State persistence** — `remember` values stored in slots
- **Tree structure** — parent-child relationships tracked
- **Reusability** — slots can be reused for items in lists

---

## Q2: What is the Composer?

The `Composer` is the engine that executes composables and manages the slot table.

```
@Composable fun MyScreen() {
    Text("Hello")
}

// What actually happens:
// 1. Composer.startNode() — begin tracking
// 2. Text() called → Composer records into slot table
// 3. Composer.endNode() — finish tracking
// 4. On recomposition, Composer diffs and updates
```

### Composer responsibilities
- **Record** composable calls into slot table
- **Diff** old vs new parameters
- **Skip** composables whose inputs haven't changed
- **Track** state reads (which composables read which state)
- **Schedule** recomposition when state changes

```kotlin
// Compiler transforms @Composable functions
// Before (what you write):
@Composable
fun Greeting(name: String) {
    Text("Hello, $name")
}

// After (what compiler generates, simplified):
fun Greeting(composer: Composer, name: String) {
    composer.startGroup(123)  // Unique key
    if (composer.changed(name)) {
        Text(composer, "Hello, $name")
    } else {
        composer.skipToGroupEnd()  // Skip — inputs unchanged
    }
    composer.endGroup()
}
```

---

## Q3: What is the snapshot system?

The snapshot system tracks state changes and triggers recomposition.

```
State change flow:
1. mutableStateOf value changes
2. Snapshot records the change
3. Snapshot applies → notifies listeners
4. Composer marks affected composables as invalid
5. Next frame → recompose only affected composables
```

```kotlin
// Snapshot system in action
val count = mutableStateOf(0)

// Reading state in a snapshot
Snapshot.withMutableSnapshot {
    count.value = 1
    count.value = 2
    // Only one recomposition triggered (conflated)
}

// Take a snapshot (read a consistent state)
val snapshot = Snapshot.takeSnapshot()
val value = snapshot.read { count.value }  // Read at snapshot point
snapshot.dispose()

// Apply a snapshot (atomic state update)
val mutableSnapshot = Snapshot.takeMutableSnapshot()
mutableSnapshot.read { count.value }  // Read
mutableSnapshot.write { count.value = 10 }  // Write
mutableSnapshot.apply()  // Commit changes → triggers recomposition
mutableSnapshot.dispose()
```

### Snapshot properties
- **Atomic** — all changes in a snapshot are applied together
- **Isolated** — each snapshot sees a consistent state
- **Reactive** — applying a snapshot notifies listeners
- **Conflated** — multiple changes → one recomposition

---

## Q4: What are the phases of recomposition?

```
1. State change → Snapshot applies
2. Composer marks affected composables as "invalid"
3. Scheduler schedules recomposition on next frame
4. Recomposition phase:
   a. Re-execute invalid composables
   b. Diff parameters (old vs new)
   c. Skip if inputs unchanged (skippable)
   d. Update slot table
5. Layout phase:
   a. Measure changed composables
   b. Place at new positions
6. Draw phase:
   a. Draw to canvas (Skia)
   b. Only redraw changed layers
```

### Recomposition types
| Type | Description |
|------|-------------|
| **Initial composition** | First execution, builds slot table |
| **Recomposition** | Re-execution on state change |
| **Smart recomposition** | Only affected composables re-execute |
| **Skippable** | Composable skipped if inputs unchanged |

---

## Q5: What is positional memoization?

Positional memoization is how `remember` works — values are stored by their position in the slot table, not by key.

```kotlin
@Composable
fun MyScreen() {
    // Position 1 in slot table
    val color = remember { Color.Red }  // Stored at position 1

    // Position 2 in slot table
    val size = remember { 100.dp }  // Stored at position 2

    // On recomposition, remember reads from same position
    // No key needed — position is the key
}

// Problem: conditional composition shifts positions
@Composable
fun BadRemember(condition: Boolean) {
    if (condition) {
        val a = remember { computeA() }  // Position 1
    }
    val b = remember { computeB() }  // Position 1 or 2 depending on condition!
    // b might get a's value if condition changes
}

// Fix: use key to ensure stable position
@Composable
fun GoodRemember(condition: Boolean) {
    if (condition) {
        key("a") {
            val a = remember { computeA() }
        }
    }
    key("b") {
        val b = remember { computeB() }
    }
}
```

---

## Q6: How does the Compose compiler plugin work?

```kotlin
// What you write:
@Composable
fun Profile(name: String, age: Int) {
    Column {
        Text(name)
        Text("$age")
    }
}

// What the compiler generates (simplified):
fun Profile(
    composer: Composer,
    name: String,
    age: Int,
) {
    composer.startRestartGroup(123456)  // Unique key

    // Check if inputs changed
    val changed = composer.changed(name) or composer.changed(age)

    if (changed || !composer.skipping) {
        Column(composer) {
            Text(composer, name)
            Text(composer, "$age")
        }
    } else {
        composer.skipToGroupEnd()  // Skip — inputs unchanged
    }

    composer.endRestartGroup()
}

// remember transformation:
@Composable
fun Example() {
    val x = remember { 42 }
}
// Becomes:
fun Example(composer: Composer) {
    val x = composer.cache(123) { 42 }  // Read from slot or compute
}
```

### Compiler transforms
1. **Add Composer parameter** — every `@Composable` gets `composer: Composer`
2. **Add group markers** — `startGroup`/`endGroup` for tracking
3. **Track changes** — `composer.changed()` for each parameter
4. **Cache remember** — `composer.cache()` for `remember` calls
5. **Add keys** — unique integer keys for each composable call site

---

## Q7: How does Compose handle concurrency?

```
Compose threading model:
- Composition runs on main thread (single-threaded)
- State reads tracked on main thread
- State writes can be from any thread (snapshot system)
- Recomposition scheduled on main thread
```

```kotlin
// ❌ Bad — modifying state from background thread
LaunchedEffect(Unit) {
    withContext(Dispatchers.IO) {
        val data = fetchData()
        count.value = data  // State write from IO thread
        // Works (snapshot system is thread-safe)
        // But recomposition still happens on main thread
    }
}

// ✅ Good — state writes are thread-safe via snapshots
// But reads must be on main thread (during composition)

// Snapshot.withMutableSnapshot for atomic updates
fun updateFromBackground() {
    Snapshot.withMutableSnapshot {
        state1.value = newValue1
        state2.value = newValue2
        // Both applied atomically → one recomposition
    }
}

// Snapshot.takeSnapshot for reading consistent state
fun readConsistentState(): Pair<String, Int> {
    return Snapshot.takeSnapshot { 
        state1.value to state2.value 
    }
}
```

### Thread safety guarantees
| Operation | Thread-safe? |
|-----------|-------------|
| State read in composition | Main thread only |
| State write from any thread | ✅ (snapshot system) |
| `Snapshot.withMutableSnapshot` | ✅ (atomic) |
| `remember` | Main thread only |
| `LaunchedEffect` | Main thread (coroutine) |
| `mutableStateOf` | ✅ (thread-safe) |

### Key Takeaways
```
1. Slot table — stores UI tree + state (linear array)
2. Composer — executes composables, diffs, skips unchanged
3. Snapshot system — tracks state changes, triggers recomposition
4. Positional memoization — remember stores by position
5. Compiler plugin — transforms @Composable to include Composer
6. Single-threaded composition — all on main thread
7. Thread-safe state writes — via snapshot system
```

---

## 🔗 Related Topics
- [Performance](Performance.md)
- [Basics](../beginner/Basics.md)
- [Effects](../intermediate/Effects.md)
