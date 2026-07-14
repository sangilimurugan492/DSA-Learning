# Architecture & State Management Scenarios

## Scenario 1: God Activity — Business Logic in UI Layer

### Problem
An Activity contains all logic: API calls, database access, validation, and UI updates. It's 2000 lines and unmaintainable.

```kotlin
// ❌ Bad — Activity does everything
class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // ❌ Network call in Activity
        val retrofit = Retrofit.Builder().baseUrl("https://api.com").build()
        val api = retrofit.create(ApiService::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            val users = withContext(Dispatchers.IO) { api.getUsers() }
            // ❌ DB access in Activity
            val db = Room.databaseBuilder(this@UserActivity, AppDb::class.java, "db").build()
            db.userDao().insertAll(users)

            // ❌ Validation in Activity
            if (users.isNotEmpty() && users[0].email.contains("@")) {
                textView.text = users[0].name
            }
        }
    }
}
```

### Solution: MVVM with Clean Architecture layers

```kotlin
// ✅ Layer 1: UI — Activity only observes state
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showUsers(state.data)
                        is UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }
}

// ✅ Layer 2: ViewModel — orchestrates state, no UI references
class UserViewModel(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<User>>> = _uiState

    init { loadUsers() }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getUsersUseCase())
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// ✅ Layer 3: Use Case — business logic
class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> {
        val users = repository.getUsersFromNetwork()
        return users.filter { it.email.contains("@") }  // Validation in domain layer
    }
}

// ✅ Layer 4: Repository — data source coordination
class UserRepository(
    private val api: ApiService,
    private val dao: UserDao
) {
    suspend fun getUsersFromNetwork(): List<User> {
        val remote = api.getUsers()
        dao.insertAll(remote)  // Cache locally
        return remote
    }
}
```

### Key Takeaway
- **UI Layer** (Activity/Fragment): Only renders state, delegates actions to ViewModel
- **Presentation Layer** (ViewModel): Manages UI state, survives rotation, no Android UI imports
- **Domain Layer** (Use Cases): Contains business rules — testable and reusable
- **Data Layer** (Repository): Coordinates network + DB, abstracts data sources
- Each layer has a single responsibility — testable independently

---

## Scenario 2: State Loss on Configuration Change

### Problem
The user fills out a form, rotates the screen, and all input is lost. The ViewModel has the data but the UI doesn't restore it.

```kotlin
// ❌ Bad — UI state in Activity, lost on rotation
class FormActivity : AppCompatActivity() {
    var name = ""
    var email = ""
    var selectedCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ❌ All fields reset to empty on rotation
        nameEditText.setText(name)
        emailEditText.setText(email)
    }
}
```

### Solution: ViewModel + SavedStateHandle

```kotlin
// ✅ Good — ViewModel holds transient state, SavedStateHandle for process death
class FormViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ✅ UI state in ViewModel — survives rotation
    val name: StateFlow<String> = savedStateHandle
        .getStateFlow("name", "")

    val email: StateFlow<String> = savedStateHandle
        .getStateFlow("email", "")

    val selectedCategory: StateFlow<String> = savedStateHandle
        .getStateFlow("category", "general")

    fun onNameChanged(value: String) {
        savedStateHandle["name"] = value
    }

    fun onEmailChanged(value: String) {
        savedStateHandle["email"] = value
    }

    fun onCategoryChanged(value: String) {
        savedStateHandle["category"] = value
    }

    fun submit(): Boolean {
        val nameVal = name.value
        val emailVal = email.value
        if (nameVal.isBlank() || !emailVal.contains("@")) return false
        // Submit logic...
        return true
    }
}

class FormActivity : AppCompatActivity() {
    private val viewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        // ✅ Restore state from ViewModel — survives rotation AND process death
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.name.collect { nameEditText.setText(it) }
                }
                launch {
                    viewModel.email.collect { emailEditText.setText(it) }
                }
                launch {
                    viewModel.selectedCategory.collect { categorySpinner.setSelection(getCategoryIndex(it)) }
                }
            }
        }

        nameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text.toString())
        }
        emailEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailChanged(text.toString())
        }
    }
}
```

### Key Takeaway
- ViewModel survives **configuration changes** (rotation) but NOT process death
- `SavedStateHandle` survives **process death** — persists across app kill
- Use `savedStateHandle.getStateFlow()` for reactive state that auto-saves
- UI reads from ViewModel on recreate — state flows restore automatically
- Only save user input and navigation state, not cached API data

---

## Scenario 3: Shared State Between Multiple ViewModels

### Problem
Two ViewModels need the same data (e.g., user session). Duplicating API calls or using a singleton Activity creates tight coupling.

```kotlin
// ❌ Bad — each ViewModel fetches its own copy
class ProfileViewModel : ViewModel() {
    fun loadUser() { api.getUser() }  // ❌ Fetches user
}
class SettingsViewModel : ViewModel() {
    fun loadUser() { api.getUser() }  // ❌ Fetches user AGAIN
}
```

### Solution: Shared repository as single source of truth

```kotlin
// ✅ Good — SharedRepository holds state, ViewModels observe
@Singleton
class SessionRepository @Inject constructor(
    private val api: ApiService,
    private val dao: UserDao
) {
    // Shared StateFlow — all ViewModels see the same data
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun loadUser() {
        if (_currentUser.value == null) {
            _currentUser.value = try {
                api.getUser()
            } catch (e: Exception) {
                dao.getUser()  // Fallback to cache
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        dao.clearUser()
    }
}

// ✅ ProfileViewModel and SettingsViewModel share the same data
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionRepo: SessionRepository
) : ViewModel() {
    val user: StateFlow<User?> = sessionRepo.currentUser  // Same instance
    fun refresh() { viewModelScope.launch { sessionRepo.loadUser() } }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionRepo: SessionRepository
) : ViewModel() {
    val user: StateFlow<User?> = sessionRepo.currentUser  // Same instance
    fun logout() = sessionRepo.logout()
}
```

### Key Takeaway
- Shared data belongs in a **shared Repository**, not individual ViewModels
- `StateFlow` in the repository acts as a shared broadcast — all collectors get updates
- `@Singleton` + Hilt ensures a single repository instance across the app
- ViewModels become thin — they delegate to repositories and transform state
- Avoid sharing ViewModels directly — it creates hidden dependencies

---

## Scenario 4: One-Time Events vs. State

### Problem
A ViewModel shows a Snackbar after a successful save. But `StateFlow` replays the last value, so the Snackbar shows again on every rotation.

```kotlin
// ❌ Bad — StateFlow replays event on rotation
class SaveViewModel : ViewModel() {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun save() {
        viewModelScope.launch {
            repo.save()
            _message.value = "Saved successfully!"  // ❌ Shows again on rotation
        }
    }
}
```

### Solution: Channel for one-time events

```kotlin
// ✅ Good — Channel for one-time events, StateFlow for state
class SaveViewModel(private val repo: Repository) : ViewModel() {

    // ✅ State — persists across rotation (form data, loading state)
    private val _uiState = MutableStateFlow(SaveUiState())
    val uiState: StateFlow<SaveUiState> = _uiState

    // ✅ Events — consumed once, not replayed (snackbar, navigation)
    private val _events = Channel<SaveEvent>(Channel.BUFFERED)
    val events: Flow<SaveEvent> = _events.receiveAsFlow()

    fun save() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                repo.save()
                _uiState.update { it.copy(isSaving = false) }
                _events.send(SaveEvent.ShowSnackbar("Saved successfully!"))
                _events.send(SaveEvent.NavigateTo(R.id.homeFragment))
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
                _events.send(SaveEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }
}

sealed class SaveEvent {
    data class ShowSnackbar(val message: String) : SaveEvent()
    data class NavigateTo(val destination: Int) : SaveEvent()
}

data class SaveUiState(
    val isSaving: Boolean = false,
    val error: String? = null
)

// Activity — collect events separately
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
            viewModel.uiState.collect { state ->
                progressBar.visibility = if (state.isSaving) View.VISIBLE else View.GONE
            }
        }
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    is SaveEvent.ShowSnackbar -> showSnackbar(event.message)
                    is SaveEvent.NavigateTo -> findNavController().navigate(event.destination)
                }
            }
        }
    }
}
```

### Key Takeaway
- **State** (UI data, loading flags) → `StateFlow` — replays last value, persists across rotation
- **Events** (snackbar, navigation, toast) → `Channel.receiveAsFlow()` — consumed once
- Mixing them causes bugs: Snackbar replaying, navigation happening twice
- `Channel.BUFFERED` ensures events aren't dropped if collector is slow
- Collect both in separate `launch` blocks within `repeatOnLifecycle`

---

## Scenario 5: Dependency Injection Without Framework

### Problem
A project doesn't use Hilt/Dagger. Manual dependency creation creates hardcoded dependencies and makes testing impossible.

```kotlin
// ❌ Bad — hardcoded dependencies everywhere
class UserViewModel : ViewModel() {
    private val repo = UserRepository(  // ❌ Hardcoded
        Retrofit.Builder().baseUrl("https://api.com").build().create(ApiService::class.java),
        Room.databaseBuilder(context, AppDb::class.java, "db").build().userDao()
    )
}
```

### Solution: Manual DI with ServiceLocator pattern

```kotlin
// ✅ Good — ServiceLocator as a lightweight DI container
object ServiceLocator {
    private lateinit var appContext: Context

    // Lazy singletons — created on first access
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(appContext, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val userDao by lazy { database.userDao() }

    val userRepository by lazy { UserRepository(api, userDao) }

    val getUserUseCase by lazy { GetUsersUseCase(userRepository) }

    fun init(context: Context) {
        appContext = context.applicationContext
    }
}

// Initialize in Application class
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}

// ✅ ViewModelFactory creates ViewModels with dependencies
class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(ServiceLocator.getUserUseCase) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(ServiceLocator.userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

// Activity — get ViewModel with dependencies
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels { ViewModelFactory() }

    // ✅ Testing: swap ServiceLocator with test doubles
    // ServiceLocator.api = FakeApiService()
}

// ✅ Even better — use Hilt for production apps
// @HiltViewModel
// class UserViewModel @Inject constructor(
//     private val getUsersUseCase: GetUsersUseCase
// ) : ViewModel()
```

### Key Takeaway
- `ServiceLocator` is a simple DI pattern — no framework needed
- `by lazy` ensures singletons are created on first use, not at startup
- Initialize in `Application.onCreate()` with app context
- `ViewModelProvider.Factory` injects dependencies into ViewModels
- For production apps, prefer **Hilt** — it generates boilerplate and handles scoping
- ServiceLocator makes testing easy — swap dependencies before tests

---

## 🔗 Related Topics
- [Architecture Patterns](../intermediate/ArchitecturePatterns.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
- [Dependency Injection](../advanced/DependencyInjection.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
