# Effects

## Q1: What are all the side effect APIs in Compose?

| Effect | When | Use Case |
|--------|------|----------|
| `LaunchedEffect` | On composition / key change | API calls, timers |
| `rememberCoroutineScope` | On event (onClick) | User-triggered async |
| `DisposableEffect` | On composition / key change | Register/unregister |
| `SideEffect` | After every recomposition | Sync to non-Compose |
| `rememberUpdatedState` | Every recomposition | Capture latest value |
| `snapshotFlow` | State changes | Convert State → Flow |
| `produceState` | On composition | Convert non-Compose → State |
| `derivedStateOf` | State changes | Computed state |

---

## Q2: How do you use produceState for async data?

```kotlin
@Composable
fun UserAvatar(userId: String) {
    val imageState by produceState<ImageState>(initialValue = ImageState.Loading, userId) {
        value = ImageState.Loading
        try {
            val bitmap = loadImage(userId)
            value = ImageState.Success(bitmap)
        } catch (e: Exception) {
            value = ImageState.Error(e)
        }
    }

    when (imageState) {
        is ImageState.Loading -> CircularProgressIndicator()
        is ImageState.Success -> Image(bitmap = (imageState as ImageState.Success).bitmap)
        is ImageState.Error -> Text("Error")
    }
}

// produceState with Flow
@Composable
fun LocationFlow() {
    val location by produceState<Location?>(initialValue = null) {
        locationFlow.collect { value = it }
    }
    location?.let { Text("${it.lat}, ${it.lng}") }
}
```

---

## Q3: How do you use derivedStateOf correctly?

```kotlin
// ✅ Good — derivedStateOf wraps a computation
@Composable
fun TodoList() {
    val todos = remember { mutableStateListOf<Todo>() }
    val completedCount by remember {
        derivedStateOf { todos.count { it.isDone } }
    }
    Text("Completed: $completedCount")
}

// ✅ Good — derivedStateOf for scroll state
val listState = rememberLazyListState()
val showButton by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}

// ❌ Bad — unnecessary derivedStateOf for simple state read
val count by remember { mutableStateOf(0) }
val doubled by remember { derivedStateOf { count * 2 } }  // Overkill
// ✅ Just compute directly
val doubled = count * 2

// ❌ Bad — derivedStateOf without remember
val completed = derivedStateOf { todos.count { it.isDone } }  // New instance each recomposition!
// ✅ Always remember it
val completed by remember { derivedStateOf { todos.count { it.isDone } } }
```

### When to use derivedStateOf?
- Value changes less frequently than source state
- Computation is non-trivial
- Multiple state reads → one derived value

---

## Q4: How do you use snapshotFlow with operators?

```kotlin
@Composable
fun ScrollHandler(listState: LazyListState) {
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it > 5 }
            .distinctUntilChanged()
            .filter { it }
            .collect { analytics.log("Scrolled past 5 items") }
    }

    // Debounce scroll
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .debounce(200)
            .collect { offset ->
                // Handle after scrolling stops
            }
    }

    // Combine multiple states
    LaunchedEffect(Unit) {
        snapshotFlow { count1.value to count2.value }
            .collect { (a, b) ->
                if (a + b > 100) { /* threshold reached */ }
            }
    }
}
```

---

## Q5: How do you handle lifecycle-aware effects?

```kotlin
// Lifecycle-aware LaunchedEffect
@Composable
fun LifecycleAwareEffect(key: Any?, block: suspend CoroutineScope.() -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}

// Usage — only collects when STARTED
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    LifecycleAwareEffect(Unit) {
        viewModel.userFlow.collect { user ->
            // Only collects when lifecycle is STARTED
        }
    }
}

// DisposableEffect with lifecycle
@Composable
fun LocationEffect(onLocation: (Location) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startLocation(onLocation)
                Lifecycle.Event.ON_STOP -> stopLocation()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
```

---

## Q6: How do you cancel and restart effects?

```kotlin
// Key-based cancellation
@Composable
fun UserScreen(userId: String) {
    var user by remember { mutableStateOf<User?>(null) }

    // Cancels and restarts when userId changes
    LaunchedEffect(userId) {
        user = repository.fetchUser(userId)
    }
}

// Multiple keys
LaunchedEffect(userId, refreshKey) {
    // Restarts when either changes
    user = repository.fetchUser(userId)
}

// Manual cancellation with rememberCoroutineScope
@Composable
fun SearchScreen() {
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    fun search(query: String) {
        searchJob?.cancel()  // Cancel previous
        searchJob = scope.launch {
            delay(300)  // Debounce
            val results = repository.search(query)
            // Update state
        }
    }
}

// rememberUpdatedState — don't restart, use latest
@Composable
fun Timer(onTick: (Int) -> Unit) {
    val currentOnTick by rememberUpdatedState(onTick)

    LaunchedEffect(Unit) {  // Runs once, doesn't restart
        var seconds = 0
        while (true) {
            delay(1000)
            seconds++
            currentOnTick(seconds)  // Always calls latest onTick
        }
    }
}
```

---

## Q7: How do you test side effects?

```kotlin
// Test LaunchedEffect with compose rule
@RunWith(AndroidJUnit4::class)
class EffectTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `LaunchedEffect loads user`() {
        composeRule.setContent {
            UserScreen(userId = "42")
        }

        // Wait for effect to run
        composeRule.waitForIdle()
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithText("Alice").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Alice").assertExists()
    }

    @Test
    fun `DisposableEffect cleans up`() {
        var disposed = false

        composeRule.setContent {
            DisposableEffect(Unit) {
                onDispose { disposed = true }
            }
        }

        composeRule.disposeContent()
        assertTrue(disposed)
    }
}
```

### Effects Best Practices
```
✅ Use LaunchedEffect for one-time async work
✅ Use rememberCoroutineScope for event-triggered work
✅ Use DisposableEffect for cleanup (listeners, receivers)
✅ Use rememberUpdatedState to avoid restarting effects
✅ Use derivedStateOf for computed state
✅ Use snapshotFlow to convert State → Flow
✅ Always pass keys to LaunchedEffect for cancellation
✅ Don't do heavy work in SideEffect (runs every recomposition)
```

---

## 🔗 Related Topics
- [Side Effects (Beginner)](../beginner/SideEffects.md)
- [State Management](StateManagement.md)
- [Performance](../advanced/Performance.md)
