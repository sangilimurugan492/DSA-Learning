# SavedStateHandle

## Q1: What is SavedStateHandle?

`SavedStateHandle` is a key-value map that persists data across configuration changes AND process death. It's available in ViewModel.

```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // Simple read/write
    val userId: String? = savedStateHandle.get<String>("userId")

    fun setUserId(id: String) {
        savedStateHandle["userId"] = id
    }
}
```

### What survives?
| Mechanism | Rotation | Process Death |
|-----------|----------|-------------|
| `remember` | ❌ | ❌ |
| `rememberSaveable` | ✅ | ✅ |
| `ViewModel` | ✅ | ❌ |
| `SavedStateHandle` | ✅ | ✅ |
| `DataStore` | ✅ | ✅ |

---

## Q2: How do you use SavedStateHandle with Compose State?

```kotlin
@HiltViewModel
class FormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // Use saveable delegate — creates mutableStateOf backed by SavedStateHandle
    var name by savedStateHandle.saveable { mutableStateOf("") }
    var email by savedStateHandle.saveable { mutableStateOf("") }
    var selectedTab by savedStateHandle.saveable { mutableStateOf(0) }

    // With custom key
    var darkMode by savedStateHandle.saveable(key = "theme_mode") { mutableStateOf(false) }

    fun reset() {
        name = ""
        email = ""
        selectedTab = 0
    }
}

@Composable
fun FormScreen(viewModel: FormViewModel = hiltViewModel()) {
    Column {
        TextField(value = viewModel.name, onValueChange = { viewModel.name = it })
        TextField(value = viewModel.email, onValueChange = { viewModel.email = it })
        Text("Tab: ${viewModel.selectedTab}")
    }
}
```

### How saveable delegate works
```
First access → savedStateHandle has no value → use initializer
  ↓
State changes → savedStateHandle updated
  ↓
Process death → SavedStateHandle saved to Bundle
  ↓
App restarts → SavedStateHandle restored → state available
```

---

## Q3: How do you use SavedStateHandle with StateFlow?

```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: SearchRepository,
) : ViewModel() {
    // Get StateFlow from SavedStateHandle
    val query: StateFlow<String> = savedStateHandle
        .getStateFlow("query", "")

    // Update value
    fun setQuery(newQuery: String) {
        savedStateHandle["query"] = newQuery
    }

    // Reactive search based on saved query
    val results: StateFlow<List<Item>> = query
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList())
            else flow { emit(repository.search(q)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()

    Column {
        TextField(value = query, onValueChange = viewModel::setQuery)
        LazyColumn { items(results) { Text(it.name) } }
    }
}
```

---

## Q4: How do you handle Navigation arguments with SavedStateHandle?

```kotlin
// Navigation passes arguments via SavedStateHandle
NavHost(navController, startDestination = "detail/{itemId}") {
    composable("detail/{itemId}") { entry ->
        DetailScreen()
    }
}

// In ViewModel — read navigation argument
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ItemRepository,
) : ViewModel() {
    // Read navigation argument
    val itemId: String = savedStateHandle.get<String>("itemId") ?: ""

    // Or as StateFlow
    val itemIdFlow: StateFlow<String> = savedStateHandle
        .getStateFlow("itemId", "")

    private val _state = MutableStateFlow<UiState<Item>>(UiState.Loading)
    val state: StateFlow<UiState<Item>> = _state.asStateFlow()

    init { loadItem() }

    fun loadItem() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val item = repository.getItem(itemId)
                _state.value = UiState.Success(item)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
}
```

---

## Q5: How do you pass results between screens with SavedStateHandle?

```kotlin
// Screen A — receives result
@Composable
fun ListScreen(navController: NavController) {
    // Read result from previous backstack entry
    val result = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("result")

    LaunchedEffect(result) {
        result?.let {
            // Handle result
            println("Got result: $it")
            // Clear after handling
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("result")
        }
    }

    LazyColumn {
        items(items) { item ->
            Text(
                item.name,
                modifier = Modifier.clickable {
                    navController.navigate("detail/${item.id}")
                },
            )
        }
    }
}

// Screen B — sends result back
@Composable
fun DetailScreen(
    itemId: String,
    navController: NavController,
) {
    Button(
        onClick = {
            // Set result on previous entry's SavedStateHandle
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("result", "Selected: $itemId")
            navController.popBackStack()
        },
    ) {
        Text("Select")
    }
}
```

---

## Q6: How do you save complex objects with SavedStateHandle?

```kotlin
// SavedStateHandle only supports Parcelable, Serializable, and primitives
// For complex objects, convert to Parcelable or use custom serialization

// 1. Make data class Parcelable
@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
) : Parcelable

@HiltViewModel
class UserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // Store Parcelable
    var user by savedStateHandle.saveable { mutableStateOf<User?>(null) }

    fun setUser(u: User) { user = u }
}

// 2. Use Gson/Moshi for serialization
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val gson: Gson,
) : ViewModel() {
    var settings: Settings
        get() = gson.fromJson(savedStateHandle.get<String>("settings"), Settings::class.java)
        set(value) { savedStateHandle["settings"] = gson.toJson(value) }
}

// 3. Store only IDs, fetch data from repository
@HiltViewModel
class CartViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CartRepository,
) : ViewModel() {
    // Save only IDs — not full objects
    val productIds: StateFlow<List<String>> = savedStateHandle
        .getStateFlow("productIds", emptyList())

    // Fetch full objects from repository
    val products: StateFlow<List<Product>> = productIds
        .map { ids -> repository.getProducts(ids) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addProduct(id: String) {
        val current = productIds.value.toMutableList()
        if (id !in current) current.add(id)
        savedStateHandle["productIds"] = current
    }
}
```

---

## Q7: When to use SavedStateHandle vs rememberSaveable vs DataStore?

```kotlin
// 1. rememberSaveable — composable-level state
@Composable
fun FormField() {
    var text by rememberSaveable { mutableStateOf("") }
    // Good for: single field, scroll position, toggle
}

// 2. SavedStateHandle — ViewModel-level state
@HiltViewModel
class FormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var name by savedStateHandle.saveable { mutableStateOf("") }
    // Good for: screen state with business logic, navigation args
}

// 3. DataStore — app-level persistent state
class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    val theme = dataStore.data.map { it[THEME_KEY] ?: "system" }
    // Good for: user preferences, settings that persist forever
}
```

### Decision guide
```
Is it a simple UI state (scroll, toggle)?
  → rememberSaveable

Is it screen-level state with business logic?
  → SavedStateHandle in ViewModel

Does it need to persist across app restarts (user preferences)?
  → DataStore

Is it large data (list of items)?
  → Store IDs in SavedStateHandle, fetch from Room/Repository
```

### What can be saved in SavedStateHandle?
| Type | Supported |
|------|-----------|
| Primitives (Int, String, Boolean, etc.) | ✅ |
| Parcelable | ✅ |
| Serializable | ✅ (not recommended) |
| Custom objects | ❌ (use Parcelable or serialize) |
| Lists of supported types | ✅ |

---

## 🔗 Related Topics
- [ViewModel](ViewModel.md)
- [Flow](Flow.md)
- [Comparison](Comparison.md)
