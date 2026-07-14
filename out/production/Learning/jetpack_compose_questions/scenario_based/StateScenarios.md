# State Scenarios

## Scenario 1: Shopping Cart with Multiple Screens

**Problem:** Build a shopping cart where users browse products on one screen, add to cart, and see the cart on another screen. State must persist across navigation.

**Solution:**
```kotlin
// Cart state
data class CartItem(val product: Product, val quantity: Int)
data class CartState(val items: List<CartItem> = emptyList()) {
    val total: Double get() = items.sumOf { it.product.price * it.quantity }
    val itemCount: Int get() = items.sumOf { it.quantity }
}

// ViewModel
@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    fun addToCart(product: Product) {
        _state.update { state ->
            val existing = state.items.find { it.product.id == product.id }
            val items = if (existing != null) {
                state.items.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1)
                    else it
                }
            } else {
                state.items + CartItem(product, 1)
            }
            state.copy(items = items)
        }
    }

    fun removeFromCart(productId: String) {
        _state.update { it.copy(items = it.items.filterNot { item -> item.product.id == productId }) }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) { removeFromCart(productId); return }
        _state.update { state ->
            state.copy(items = state.items.map {
                if (it.product.id == productId) it.copy(quantity = quantity) else it
            })
        }
    }
}

// Product screen
@Composable
fun ProductScreen(viewModel: CartViewModel = hiltViewModel()) {
    val cart by viewModel.state.collectAsStateWithLifecycle()
    LazyColumn {
        items(products, key = { it.id }) { product ->
            ProductItem(
                product = product,
                inCart = cart.items.any { it.product.id == product.id },
                onAdd = { viewModel.addToCart(product) },
            )
        }
    }
}

// Cart screen
@Composable
fun CartScreen(viewModel: CartViewModel = hiltViewModel()) {
    val cart by viewModel.state.collectAsStateWithLifecycle()
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cart.items, key = { it.product.id }) { item ->
                CartRow(item, onRemove = { viewModel.removeFromCart(item.product.id) })
            }
        }
        Text("Total: $${cart.total}")
    }
}
```

---

## Scenario 2: Multi-Step Form with Validation

**Problem:** Build a 3-step registration form with validation. User can go back and forth. State must persist.

**Solution:**
```kotlin
data class RegistrationState(
    val currentStep: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val phoneError: String? = null,
    val isSubmitting: Boolean = false,
)

class RegistrationViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    fun onNameChange(name: String) = _state.update { it.copy(name = name, nameError = null) }
    fun onEmailChange(email: String) = _state.update { it.copy(email = email, emailError = null) }
    fun onPasswordChange(password: String) = _state.update { it.copy(password = password, passwordError = null) }
    fun onPhoneChange(phone: String) = _state.update { it.copy(phone = phone, phoneError = null) }

    fun next() {
        val current = _state.value
        val valid = when (current.currentStep) {
            0 -> validateStep1(current)
            1 -> validateStep2(current)
            else -> true
        }
        if (valid) _state.update { it.copy(currentStep = it.currentStep + 1) }
    }

    fun back() = _state.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) }

    private fun validateStep1(s: RegistrationState): Boolean {
        var valid = true
        _state.update {
            it.copy(
                nameError = if (it.name.isBlank()) { valid = false; "Required" } else null,
                emailError = if (!it.email.contains("@")) { valid = false; "Invalid email" } else null,
            )
        }
        return valid
    }

    private fun validateStep2(s: RegistrationState): Boolean {
        var valid = true
        _state.update {
            it.copy(
                passwordError = if (it.password.length < 6) { valid = false; "Min 6 chars" } else null,
                phoneError = if (it.phone.length < 10) { valid = false; "Invalid phone" } else null,
            )
        }
        return valid
    }
}

@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column {
        when (state.currentStep) {
            0 -> Step1(state, viewModel::onNameChange, viewModel::onEmailChange)
            1 -> Step2(state, viewModel::onPasswordChange, viewModel::onPhoneChange)
            2 -> Step3(state)
        }
        Row {
            if (state.currentStep > 0) TextButton(onClick = viewModel::back) { Text("Back") }
            Button(onClick = viewModel::next) { Text(if (state.currentStep < 2) "Next" else "Submit") }
        }
    }
}
```

---

## Scenario 3: Auth Flow with State Restoration

**Problem:** User is filling a login form, app gets killed. Restore form state after process death.

**Solution:**
```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Use SavedStateHandle for persistence across process death
    var email by savedStateHandle.saveable { mutableStateOf("") }
    var password by savedStateHandle.saveable { mutableStateOf("") }
    var rememberMe by savedStateHandle.saveable { mutableStateOf(false) }

    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun login() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = authRepository.login(email, password)
                if (rememberMe) authRepository.saveSession(user)
                _uiState.value = UiState.Success(user)
                _events.send(LoginEvent.NavigateToHome)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Login failed")
                _events.send(LoginEvent.ShowError(e.message ?: "Login failed"))
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> navController.navigate("home") { popUpTo("login") { inclusive = true } }
                is LoginEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Column {
        OutlinedTextField(value = viewModel.email, onValueChange = { viewModel.email = it }, label = { Text("Email") })
        OutlinedTextField(value = viewModel.password, onValueChange = { viewModel.password = it }, label = { Text("Password") })
        Switch(checked = viewModel.rememberMe, onCheckedChange = { viewModel.rememberMe = it })
        Button(onClick = viewModel::login, enabled = uiState !is UiState.Loading) {
            Text("Login")
        }
    }
}
```

---

## Scenario 4: Search with Debounce

**Problem:** Implement a search that only triggers API call after user stops typing for 300ms.

**Solution:**
```kotlin
class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    var query by mutableStateOf("")

    val results: StateFlow<SearchUiState> = flow {
        emit(SearchUiState.Idle)
        repository.search(query)
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .collect { q ->
                emit(SearchUiState.Loading)
                try {
                    val results = repository.search(q)
                    emit(SearchUiState.Success(results))
                } catch (e: Exception) {
                    emit(SearchUiState.Error(e.message))
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState.Idle)
}

// Alternative: snapshotFlow in composable
@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.length > 2 }
            .collect { q ->
                isLoading = true
                results = repository.search(q)
                isLoading = false
            }
    }

    Column {
        TextField(value = query, onValueChange = { query = it }, label = { Text("Search") })
        if (isLoading) CircularProgressIndicator()
        LazyColumn { items(results, key = { it.id }) { Text(it.name) } }
    }
}
```

---

## Scenario 5: Theme Persistence

**Problem:** User selects dark/light theme. Persist across app restarts.

**Solution:**
```kotlin
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = dataStore.data
        .map { it[THEME_KEY] ?: ThemeMode.SYSTEM.name }
        .map { ThemeMode.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            dataStore.edit { it[THEME_KEY] = mode.name }
        }
    }
}

@Composable
fun App(themeViewModel: ThemeViewModel = hiltViewModel()) {
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    val darkMode = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()) {
        AppContent()
    }
}
```

---

## 🔗 Related Topics
- [State Management](../intermediate/StateManagement.md)
- [Architecture](../advanced/Architecture.md)
- [State Management (Deep Dive)](../state_management/Fundamentals.md)
