# Debugging Scenarios

## Scenario 1: Infinite Recomposition Loop

**Problem:** A composable recomposes infinitely, causing the app to freeze.

**Root Causes & Solutions:**

```kotlin
// ❌ Cause 1: State update during composition
@Composable
fun BadExample() {
    var count by remember { mutableStateOf(0) }
    count++  // State write during composition → recompose → write → loop!
    Text("$count")
}

// ✅ Fix: Move to event handler
@Composable
fun GoodExample() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) { Text("$count") }
}

// ❌ Cause 2: Creating new state in composition
@Composable
fun BadList(items: List<Item>) {
    val processedItems = items.map { it.copy(name = it.name.uppercase()) }  // New list each time
    LazyColumn { items(processedItems) { Text(it.name) } }
}

// ✅ Fix: Use remember
@Composable
fun GoodList(items: List<Item>) {
    val processedItems = remember(items) {
        items.map { it.copy(name = it.name.uppercase()) }
    }
    LazyColumn { items(processedItems) { Text(it.name) } }
}

// ❌ Cause 3: Unstable lambda parameter
@Composable
fun BadParent() {
    Child(onClick = { /* new lambda each recomposition */ })
}

// ✅ Fix: Stable lambda
@Composable
fun GoodParent() {
    val onClick = remember { { /* ... */ } }
    Child(onClick = onClick)
}
```

### How to debug:
1. Use Layout Inspector → "Show recomposition counts"
2. Add `Modifier.recomposeHighlighter()` to see what recomposes
3. Check Compose compiler reports for stability
4. Look for state writes in composition body

---

## Scenario 2: State Lost on Configuration Change

**Problem:** User input is lost when rotating the device.

**Solution:**
```kotlin
// ❌ Bad — state not saved
@Composable
fun BadScreen() {
    var text by remember { mutableStateOf("") }  // Lost on rotation
    TextField(value = text, onValueChange = { text = it })
}

// ✅ Good — use rememberSaveable
@Composable
fun GoodScreen() {
    var text by rememberSaveable { mutableStateOf("") }  // Survives rotation
    TextField(value = text, onValueChange = { text = it })
}

// ✅ For complex state — use ViewModel
class FormViewModel : ViewModel() {
    var name by mutableStateOf("")  // Survives rotation (ViewModel)
    var email by mutableStateOf("")
}

// ✅ For process death — use SavedStateHandle
class FormViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    var name by savedStateHandle.saveable { mutableStateOf("") }  // Survives process death
}
```

### State survival levels:
| Mechanism | Rotation | Process Death |
|-----------|----------|-------------|
| `remember` | ❌ | ❌ |
| `rememberSaveable` | ✅ | ✅ |
| `ViewModel` | ✅ | ❌ |
| `SavedStateHandle` | ✅ | ✅ |

---

## Scenario 3: Memory Leak from LaunchedEffect

**Problem:** A coroutine launched in `LaunchedEffect` keeps running after the composable is removed.

**Solution:**
```kotlin
// ❌ Bad — GlobalScope never cancels
@Composable
fun BadExample() {
    GlobalScope.launch {
        while (true) {
            delay(1000)
            // Runs forever, even after composable is removed!
        }
    }
}

// ✅ Good — LaunchedEffect auto-cancels
@Composable
fun GoodExample() {
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            // Cancelled when composable leaves composition
        }
    }
}

// ✅ Good — rememberCoroutineScope is lifecycle-aware
@Composable
fun GoodExample2() {
    val scope = rememberCoroutineScope()
    // scope is cancelled when composable leaves
}

// ❌ Bad — holding context reference
@Composable
fun BadContext() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val listener = SomeListener(context)  // Leaks context!
        register(listener)
        onDispose { unregister(listener) }  // Must unregister!
    }
}

// ✅ Good — cleanup in onDispose
@Composable
fun GoodContext() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val receiver = BroadcastReceiver { _, _ -> /* ... */ }
        context.registerReceiver(receiver, IntentFilter("ACTION"))
        onDispose { context.unregisterReceiver(receiver) }  // Cleanup!
    }
}
```

---

## Scenario 4: ViewModel Not Sharing State

**Problem:** Two composables in the same screen have different ViewModel instances.

**Solution:**
```kotlin
// ❌ Bad — each composable gets its own ViewModel
@Composable
fun BadScreen() {
    Header()  // Creates ViewModel A
    Content()  // Creates ViewModel B (different instance!)
}

@Composable
fun Header() {
    val viewModel: MyViewModel = viewModel()  // Instance A
}

@Composable
fun Content() {
    val viewModel: MyViewModel = viewModel()  // Instance B
}

// ✅ Good — hoist ViewModel to parent
@Composable
fun GoodScreen() {
    val viewModel: MyViewModel = viewModel()  // Single instance
    Header(viewModel)
    Content(viewModel)
}

@Composable
fun Header(viewModel: MyViewModel) { /* ... */ }

@Composable
fun Content(viewModel: MyViewModel) { /* ... */ }

// ✅ Good — with Navigation, scope to route
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") {
            // Same ViewModel instance for all children in this composable
            val viewModel: HomeViewModel = hiltViewModel()
            HomeHeader(viewModel)
            HomeContent(viewModel)
        }
    }
}
```

---

## Scenario 5: Composition Local Not Updating

**Problem:** A `CompositionLocal` value changes but child composables don't recompose.

**Solution:**
```kotlin
// ❌ Bad — staticCompositionLocalOf doesn't trigger recomposition
val LocalTheme = staticCompositionLocalOf { Theme.Light }

@Composable
fun App() {
    var theme by remember { mutableStateOf(Theme.Light) }
    // Changing theme won't update children reading LocalTheme
    CompositionLocalProvider(LocalTheme provides theme) {
        Child()  // Won't recompose when theme changes!
    }
}

// ✅ Fix: Use compositionLocalOf for dynamic values
val LocalTheme = compositionLocalOf { Theme.Light }

@Composable
fun App() {
    var theme by remember { mutableStateOf(Theme.Light) }
    CompositionLocalProvider(LocalTheme provides theme) {
        Child()  // Will recompose when theme changes ✅
    }
}

// Rule of thumb:
// staticCompositionLocalOf → values that NEVER change (e.g., app config)
// compositionLocalOf → values that CAN change (e.g., theme, locale)
```

---

## Scenario 6: LazyColumn Items Jumping

**Problem:** Items in LazyColumn jump or animate incorrectly when items are added/removed.

**Solution:**
```kotlin
// ❌ Bad — no key, Compose can't track items
LazyColumn {
    items(items) { item ->
        ItemRow(item)
    }
}

// ✅ Good — use key for stable identity
LazyColumn {
    items(items, key = { it.id }) { item ->
        ItemRow(item)
    }
}

// ✅ Good — use contentType for mixed lists
LazyColumn {
    items(items, key = { it.id }, contentType = { "item" }) { item ->
        ItemRow(item)
    }
    item(contentType = "header") { Header() }
    item(contentType = "footer") { Footer() }
}

// ✅ Good — animate item placement
LazyColumn {
    items(items, key = { it.id }) { item ->
        ItemRow(
            item,
            modifier = Modifier.animateItem(
                placementSpec = spring(stiffness = Spring.StiffnessLow),
            ),
        )
    }
}

// ✅ Good — avoid 0.dp height items (causes measurement issues)
LazyColumn {
    items(items, key = { it.id }) { item ->
        if (item.isVisible) {
            ItemRow(item, modifier = Modifier.heightIn(min = 1.dp))  // Min height
        }
    }
}
```

---

## 🔗 Related Topics
- [Performance](../advanced/Performance.md)
- [Effects](../intermediate/Effects.md)
- [Internals](../advanced/Internals.md)
