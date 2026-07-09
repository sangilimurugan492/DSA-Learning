# State Management Comparison

## Q1: remember vs rememberSaveable vs ViewModel vs SavedStateHandle

| Feature | `remember` | `rememberSaveable` | `ViewModel` | `SavedStateHandle` |
|---------|-----------|-------------------|-------------|-------------------|
| Survives recomposition | ✅ | ✅ | ✅ | ✅ |
| Survives rotation | ❌ | ✅ | ✅ | ✅ |
| Survives process death | ❌ | ✅ | ❌ | ✅ |
| Holds business logic | ❌ | ❌ | ✅ | ✅ |
| Coroutine scope | ❌ | ❌ | ✅ (viewModelScope) | Via ViewModel |
| Scope | Composable | Composable | Screen | Screen |
| Size limit | None | Bundle size (~1MB) | None | Bundle size |

---

## Q2: When to use each mechanism?

```kotlin
// 1. remember — simple, ephemeral local state
@Composable
fun ExpandableCard() {
    var expanded by remember { mutableStateOf(false) }
    // Lost when composable leaves composition or rotation
}

// 2. rememberSaveable — simple state that survives rotation + process death
@Composable
fun TextField() {
    var text by rememberSaveable { mutableStateOf("") }
    // Survives rotation and process death
}

// 3. ViewModel — screen-level state with business logic
class UserViewModel : ViewModel() {
    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()
    // Survives rotation, NOT process death
    // Has viewModelScope for coroutines
}

// 4. SavedStateHandle — ViewModel state that survives process death
@HiltViewModel
class UserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var query by savedStateHandle.saveable { mutableStateOf("") }
    // Survives rotation AND process death
}

// 5. DataStore — app-level persistent state
class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    // Persists across app restarts
}
```

---

## Q3: Comparison diagram

```
Lifespan:    Short ──────────────────────────────────→ Forever

  remember      rememberSaveable     ViewModel      SavedStateHandle     DataStore
     │                  │                │                 │                 │
     ▼                  ▼                ▼                 ▼                 ▼
  Recomposition    Config change    Config change     Process death     App restart
  (only)           + Process death  (only)            + Config change   + Forever
```

```
Scope:    Narrow ──────────────────────────────────→ Wide

  remember      rememberSaveable     State Holder     ViewModel      SavedStateHandle     DataStore
     │                  │                │                │                 │                 │
     ▼                  ▼                ▼                ▼                 ▼                 ▼
  Single          Single            Multiple          Screen           Screen              App
  composable      composable        composables        (via nav)        (via nav)          (global)
```

---

## Q4: StateFlow vs SharedFlow vs Channel vs LiveData

| Feature | StateFlow | SharedFlow | Channel | LiveData |
|---------|-----------|------------|---------|----------|
| Initial value | ✅ | ❌ | ❌ | ✅ |
| Conflates | ✅ | Optional | ❌ | ✅ |
| Replay | 1 (current) | Configurable | ❌ | 1 (current) |
| Multiple collectors | ✅ | ✅ | ❌ (one) | ✅ |
| Lifecycle-aware | Via collectAsStateWithLifecycle | Via repeatOnLifecycle | Via LaunchedEffect | ✅ (built-in) |
| Use case | State | Events/Broadcast | One-time events | Legacy |

```kotlin
// StateFlow — for state
val count = MutableStateFlow(0)  // Always has value, conflates

// SharedFlow — for events (multiple collectors)
val events = MutableSharedFlow<Event>()  // No initial value, can replay

// Channel — for one-time events (single collector)
val events = Channel<Event>()  // Each event delivered to one collector

// LiveData — legacy, prefer StateFlow
val count = MutableLiveData(0)  // Lifecycle-aware but Kotlin-first prefer Flow
```

---

## Q5: mutableStateOf vs MutableStateFlow

| Feature | mutableStateOf | MutableStateFlow |
|---------|---------------|-----------------|
| Where | Composable | ViewModel |
| Compose integration | Native (remember) | collectAsStateWithLifecycle |
| Thread-safe | ✅ (snapshot) | ✅ (atomic) |
| Initial value | Required | Required |
| Conflation | ✅ | ✅ |
| Outside Compose | ❌ (needs Compose runtime) | ✅ |

```kotlin
// mutableStateOf — use in composables
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
}

// MutableStateFlow — use in ViewModel
class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()
}

// Bridge: MutableStateFlow → Compose State
@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel()) {
    val count by viewModel.count.collectAsStateWithLifecycle()
    Text("$count")
}
```

---

## Q6: remember vs derivedStateOf vs produceState

```kotlin
// remember — persist a value across recompositions
val formatter = remember { SimpleDateFormat("dd/MM/yyyy") }

// remember with key — recompute when key changes
val user = remember(userId) { fetchUser(userId) }

// derivedStateOf — compute from other state, only recompose when result changes
val isValid by remember {
    derivedStateOf {
        name.isNotBlank() && email.contains("@") && password.length >= 6
    }
}

// produceState — async load, convert non-Compose to State
val image by produceState<ImageState>(ImageState.Loading, url) {
    value = ImageState.Success(loadImage(url))
}
```

| API | When to use | Recomputes |
|-----|------------|-----------|
| `remember` | Persist value | Only when key changes |
| `derivedStateOf` | Computed from state | When result changes (not every source change) |
| `produceState` | Async data | When key changes |

---

## Q7: Quick reference decision matrix

```
NEED:                              USE:
────────────────────────────────── ──────────────────────────
Simple toggle in one composable    remember { mutableStateOf() }
Toggle survives rotation           rememberSaveable { mutableStateOf() }
List of items in composable        remember { mutableStateListOf() }
Computed value from state          remember { derivedStateOf { } }
Async loaded data                  produceState { }
Screen-level state                 ViewModel + StateFlow
State survives process death       SavedStateHandle in ViewModel
One-time event (navigate)          Channel in ViewModel
Broadcast event                    SharedFlow in ViewModel
User preferences                   DataStore
Large dataset                      Room + Flow
Form with validation               ViewModel + data class state
Scroll position                    rememberSaveable { LazyListState() }
Search with debounce               snapshotFlow + debounce in LaunchedEffect
```

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [ViewModel](ViewModel.md)
- [Best Practices](BestPractices.md)
