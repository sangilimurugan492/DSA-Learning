# Flow in Compose

## Q1: How do you collect Flow in Compose?

```kotlin
// collectAsStateWithLifecycle — recommended (lifecycle-aware)
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Text(state.name)
}

// collectAsState — older API (not lifecycle-aware)
@Composable
fun OldWay(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
}

// collectAsStateWithLifecycle — stops collecting when STOPPED
// collectAsState — collects even when backgrounded (wastes resources)
```

### Dependencies
```kotlin
implementation "androidx.lifecycle:lifecycle-runtime-compose:2.7.0"
```

---

## Q2: What is StateFlow and how do you use it?

```kotlin
class CounterViewModel : ViewModel() {
    // MutableStateFlow — private, writable
    private val _count = MutableStateFlow(0)
    // StateFlow — public, read-only
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() { _count.value++ }
}

// Properties of StateFlow:
// 1. Always has a value (initial value required)
// 2. Conflates — only latest value matters
// 3. New collectors get current value immediately
// 4. Two values are compared with equals to decide emission

// Multiple StateFlows
class FormViewModel : ViewModel() {
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // Combine multiple StateFlows
    val isValid: StateFlow<Boolean> = combine(_name, _email) { name, email ->
        name.isNotBlank() && email.contains("@")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}
```

---

## Q3: What is SharedFlow and when to use it?

```kotlin
class NavigationViewModel : ViewModel() {
    // SharedFlow — for events (no initial value, can replay)
    private val _events = MutableSharedFlow<NavEvent>(
        replay = 0,  // Don't replay old events to new collectors
        extraBufferCapacity = 10,  // Buffer events
    )
    val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    fun navigate(route: String) {
        viewModelScope.launch {
            _events.emit(NavEvent.Navigate(route))  // Suspend
        }
    }

    fun tryEmit(route: String) {
        _events.tryEmit(NavEvent.Navigate(route))  // Non-suspend
    }
}

// Consume SharedFlow in Compose
@Composable
fun NavScreen(viewModel: NavigationViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NavEvent.Navigate -> navController.navigate(event.route)
            }
        }
    }
}
```

### StateFlow vs SharedFlow
| StateFlow | SharedFlow |
|-----------|------------|
| Always has a value | No initial value |
| Conflates (keeps latest) | Can buffer/replay |
| For state | For events |
| `collectAsStateWithLifecycle` | `collect` in LaunchedEffect |
| New collector gets current | New collector gets replay (if configured) |

---

## Q4: How do you use Channel for one-time events?

```kotlin
class SnackbarViewModel : ViewModel() {
    // Channel — each event delivered to exactly one collector
    private val _events = Channel<SnackbarEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()  // Convert to Flow

    fun showMessage(message: String) {
        viewModelScope.launch {
            _events.send(SnackbarEvent(message))
        }
    }
}

sealed interface SnackbarEvent {
    data class Show(val message: String) : SnackbarEvent
    data class ShowAction(val message: String, val action: String) : SnackbarEvent
}

// Consume in Compose
@Composable
fun MyScreen(viewModel: SnackbarViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SnackbarEvent.Show -> snackbarHostState.showSnackbar(event.message)
                is SnackbarEvent.ShowAction -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        // Handle action
                    }
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        // Content
    }
}
```

### Channel vs SharedFlow for events
| Channel | SharedFlow |
|---------|------------|
| Each event delivered once | Can be delivered to multiple |
| No replay | Can replay |
| Better for one-time events | Better for broadcast events |
| `receiveAsFlow()` | `asSharedFlow()` |
| Navigate, show snackbar | Multiple collectors need same event |

---

## Q5: How do you combine multiple Flows?

```kotlin
class DashboardViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())

    // Combine 2 flows
    val userWithOrders = combine(_user, _orders) { user, orders ->
        UserWithOrders(user, orders)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserWithOrders(null, emptyList()))

    // Combine 3+ flows
    val dashboardState = combine(_user, _orders, _notifications) { user, orders, notifications ->
        DashboardState(user, orders, notifications)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    // Map a flow
    val orderCount = _orders.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filter a flow
    val activeOrders = _orders.map { orders -> orders.filter { it.isActive } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
```

### SharingStarted options
| Option | Description |
|--------|-------------|
| `WhileSubscribed(timeout)` | Starts when first collector, stops after timeout with no collectors |
| `Lazily` | Starts when first collector, never stops |
| `Eagerly` | Starts immediately, never stops |

---

## Q6: How do you use Flow operators in Compose?

```kotlin
class SearchViewModel(private val repo: SearchRepository) : ViewModel() {
    var query by mutableStateOf("")

    // Debounce search
    val results: StateFlow<SearchState> = flow {
        emit(SearchState.Loading)
    }.flatMapLatest { _ ->
        flow {
            delay(300)  // Debounce
            try {
                emit(SearchState.Success(repo.search(query)))
            } catch (e: Exception) {
                emit(SearchState.Error(e.message ?: "Error"))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchState.Idle)

    // Using snapshotFlow in composable
    @Composable
    fun SearchScreen() {
        var query by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            snapshotFlow { query }
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.length > 2 }
                .flatMapLatest { repo.search(it) }
                .collect { results ->
                    // Update UI
                }
        }
    }
}

// Flow operators commonly used:
// .map { }         — transform values
// .filter { }      — keep matching values
// .debounce(ms)    — wait for quiet period
// .distinctUntilChanged() — skip duplicates
// .flatMapLatest { } — switch to new flow, cancel old
// .combine(flow2) { a, b -> } — merge two flows
// .onEach { }      — side effect
// .catch { }       — handle errors
// .stateIn()       — convert to StateFlow
```

---

## Q7: How do you handle Flow lifecycle in Compose?

```kotlin
// collectAsStateWithLifecycle — stops collecting when not at least STARTED
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle(
        minValue = Lifecycle.State.STARTED,  // Collect when STARTED or above
    )
    // Stops collecting when STOPPED → saves battery
}

// repeatOnLifecycle — for manual collection
@Composable
fun LocationScreen(viewModel: LocationViewModel = viewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.locationFlow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.locationFlow.collect { location ->
                // Only collects when STARTED
            }
        }
    }
}

// Flow with lifecycle states
@Composable
fun MyScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Collect only when RESUMED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            someFlow.collect { /* ... */ }
        }
    }
}
```

### Lifecycle states for collection
| State | When |
|-------|------|
| `RESUMED` | App is in foreground, visible |
| `STARTED` | App is visible (may be partially covered) |
| `CREATED` | App is in background |
| `DESTROYED` | App is being destroyed |

### Best practice
```
✅ Use collectAsStateWithLifecycle for StateFlow
✅ Use repeatOnLifecycle(STARTED) for SharedFlow/Channel
✅ Use WhileSubscribed(5000) for stateIn
✅ Don't use collectAsState (not lifecycle-aware)
✅ Don't collect in composition without LaunchedEffect
```

---

## 🔗 Related Topics
- [ViewModel](ViewModel.md)
- [Fundamentals](Fundamentals.md)
- [SavedStateHandle](SavedStateHandle.md)
