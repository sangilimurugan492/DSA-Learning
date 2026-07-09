# Side Effects

## Q1: What is a side effect in Compose?

A side effect is any operation that affects state outside the composable's scope (API calls, database, analytics, timers).

```kotlin
// ❌ Bad — side effect directly in composable (runs on every recomposition)
@Composable
fun BadExample(userId: String) {
    val user = fetchUser(userId)  // Runs every recomposition!
    Text(user.name)
}

// ✅ Good — use LaunchedEffect (runs once, cancels on key change)
@Composable
fun GoodExample(userId: String) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        user = fetchUser(userId)  // Runs once per userId
    }

    if (user != null) Text(user!!.name) else CircularProgressIndicator()
}
```

---

## Q2: What is LaunchedEffect?

`LaunchedEffect` runs a coroutine when the composable enters composition and cancels it when the key changes or the composable leaves.

```kotlin
@Composable
fun UserScreen(userId: String) {
    var user by remember { mutableStateOf<User?>(null) }

    // Runs when userId changes, cancels previous coroutine
    LaunchedEffect(userId) {
        user = repository.fetchUser(userId)
    }

    // Multiple keys — restarts when either changes
    LaunchedEffect(userId, refreshKey) {
        user = repository.fetchUser(userId)
    }

    // No key — runs once on first composition
    LaunchedEffect(Unit) {
        // One-time setup
    }

    user?.let { Text(it.name) } ?: CircularProgressIndicator()
}
```

### LaunchedEffect lifecycle
```
Composable enters composition → LaunchedEffect launches coroutine
  ↓
Key changes → Cancel old coroutine, launch new one
  ↓
Composable leaves composition → Cancel coroutine
```

---

## Q3: What is rememberCoroutineScope?

`rememberCoroutineScope` gives a `CoroutineScope` that can be used to launch coroutines from event handlers (not during composition).

```kotlin
@Composable
fun LoginScreen() {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // Use scope in event handlers (onClick, etc.)
    Button(
        onClick = {
            scope.launch {
                isLoading = true
                try {
                    val user = repository.login(email, password)
                    // Handle success
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    isLoading = false
                }
            }
        },
        enabled = !isLoading,
    ) {
        if (isLoading) CircularProgressIndicator() else Text("Login")
    }
}
```

### LaunchedEffect vs rememberCoroutineScope
| LaunchedEffect | rememberCoroutineScope |
|----------------|----------------------|
| Runs on composition | Runs on event (onClick) |
| Auto-cancels on leave | Must manage manually |
| Key-based restart | Launch from anywhere |
| Use for setup/loading | Use for user actions |

---

## Q4: What is DisposableEffect?

`DisposableEffect` runs cleanup when the composable leaves composition or the key changes.

```kotlin
@Composable
fun LocationTracker() {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startLocationTracking()
                Lifecycle.Event.ON_STOP -> stopLocationTracking()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        // Cleanup — called when composable leaves or key changes
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

// Register a broadcast receiver
@Composable
fun BatteryWatcher() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Handle battery change
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}
```

---

## Q5: What is SideEffect?

`SideEffect` runs after every successful recomposition. Used to sync Compose state with non-Compose objects.

```kotlin
@Composable
fun AnalyticsTracker(screenName: String) {
    // Runs after every recomposition
    SideEffect {
        analytics.setCurrentScreen(screenName)  // Sync to analytics
    }
}

// Common use: sync state to View system
@Composable
fun SyncToView(view: View, color: Color) {
    SideEffect {
        view.setBackgroundColor(color.toArgb())  // Sync Compose → View
    }
}
```

### When to use SideEffect?
- Sync Compose state to non-Compose objects
- Analytics logging
- Not for heavy operations (runs every recomposition)

---

## Q6: What is rememberUpdatedState?

`rememberUpdatedState` captures the latest value without restarting a `LaunchedEffect`.

```kotlin
@Composable
fun Timer(onTimeout: () -> Unit) {
    // Capture latest onTimeout without restarting the timer
    val currentOnTimeout by rememberUpdatedState(onTimeout)

    // LaunchedEffect runs once — doesn't restart when onTimeout changes
    LaunchedEffect(Unit) {
        delay(5000)
        currentOnTimeout()  // Calls the LATEST onTimeout
    }
}

// Without rememberUpdatedState:
@Composable
fun BadTimer(onTimeout: () -> Unit) {
    // Restarts timer every time onTimeout changes (bad!)
    LaunchedEffect(onTimeout) {
        delay(5000)
        onTimeout()
    }
}
```

### Use case: Long-running effect with changing callbacks
```kotlin
@Composable
fun AutoRefresh(data: Data, onRefresh: () -> Unit) {
    val currentOnRefresh by rememberUpdatedState(onRefresh)

    LaunchedEffect(Unit) {  // Runs once
        while (true) {
            delay(5000)
            currentOnRefresh()  // Always calls latest onRefresh
        }
    }
}
```

---

## Q7: What is snapshotFlow?

`snapshotFlow` converts Compose `State` into a `Flow` that emits when the state changes.

```kotlin
@Composable
fun ScrollExample() {
    val listState = rememberLazyListState()

    // Convert Compose state to Flow
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .filter { it > 5 }
            .collect {
                analytics.log("Scrolled past 5 items")
            }
    }

    // Debounce scroll events
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .debounce(100)
            .collect { offset ->
                // Handle scroll position
            }
    }

    LazyColumn(state = listState) {
        items(100) { Text("Item $it") }
    }
}
```

### Side Effects Summary
| Effect | When | Use Case |
|--------|------|----------|
| `LaunchedEffect` | On composition / key change | API calls, timers |
| `rememberCoroutineScope` | On event (onClick) | User-triggered async |
| `DisposableEffect` | On composition / key change | Register/unregister listeners |
| `SideEffect` | After every recomposition | Sync to non-Compose |
| `rememberUpdatedState` | Every recomposition | Capture latest value |
| `snapshotFlow` | State changes | Convert State → Flow |
| `produceState` | On composition | Convert non-Compose → State |

---

## 🔗 Related Topics
- [State](State.md)
- [Basics](Basics.md)
- [Effects (Intermediate)](../intermediate/Effects.md)
