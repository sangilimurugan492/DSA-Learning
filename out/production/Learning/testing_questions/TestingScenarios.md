# Testing Scenarios

## Q1: How do you test a ViewModel with coroutines?

```kotlin
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var state by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun loadUser(id: String) {
        viewModelScope.launch {
            state = UiState.Loading
            try {
                val user = repository.getUser(id)
                state = UiState.Success(user)
            } catch (e: Exception) {
                state = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// Test
class UserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: UserRepository = mock()
    private lateinit var viewModel: UserViewModel

    @Before
    fun setUp() {
        viewModel = UserViewModel(repository)
    }

    @Test
    fun `load user success updates state to success`() = runTest {
        whenever(repository.getUser("123"))
            .thenReturn(User("Alice"))

        viewModel.loadUser("123")

        advanceUntilIdle()

        assertTrue(viewModel.state is UiState.Success)
        assertEquals("Alice", (viewModel.state as UiState.Success).user.name)
    }

    @Test
    fun `load user error updates state to error`() = runTest {
        whenever(repository.getUser("123"))
            .thenThrow(RuntimeException("Network error"))

        viewModel.loadUser("123")

        advanceUntilIdle()

        assertTrue(viewModel.state is UiState.Error)
        assertEquals("Network error", (viewModel.state as UiState.Error).message)
    }

    @Test
    fun `load user shows loading state first`() = runTest {
        val deferred = CompletableDeferred<User>()
        whenever(repository.getUser("123")).thenReturn(deferred.await())

        viewModel.loadUser("123")

        assertEquals(UiState.Loading, viewModel.state)

        deferred.complete(User("Alice"))
        advanceUntilIdle()

        assertTrue(viewModel.state is UiState.Success)
    }
}
```

---

## Q2: How do you test a Room DAO with Flow?

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<User?>

    @Insert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)
}

// Test
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.userDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun `getAllUsers emits sorted list`() = runTest {
        dao.insert(User("2", "Bob", "bob@test.com"))
        dao.insert(User("1", "Alice", "alice@test.com"))

        val firstEmission = dao.getAllUsers().first()

        assertEquals(listOf("Alice", "Bob"), firstEmission.map { it.name })
    }

    @Test
    fun `getUserById returns null for non-existent`() = runTest {
        val user = dao.getUserById("999").first()
        assertNull(user)
    }

    @Test
    fun `insert and delete updates flow`() = runTest {
        val user = User("1", "Alice", "alice@test.com")

        dao.insert(user)
        assertEquals(1, dao.getAllUsers().first().size)

        dao.delete(user)
        assertEquals(0, dao.getAllUsers().first().size)
    }

    @Test
    fun `flow emits updates when data changes`() = runTest {
        val emissions = mutableListOf<List<User>>()
        val job = launch(UnconfinedTestDispatcher()) {
            dao.getAllUsers().collect { emissions.add(it) }
        }

        dao.insert(User("1", "Alice", "alice@test.com"))
        dao.insert(User("2", "Bob", "bob@test.com"))

        job.cancel()

        // First emission: empty
        assertEquals(0, emissions[0].size)
        // After first insert
        assertEquals(1, emissions[1].size)
        // After second insert
        assertEquals(2, emissions[2].size)
    }
}
```

---

## Q3: How do you test a Retrofit API with MockWebServer?

```kotlin
class UserApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: UserApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    @After
    fun tearDown() = mockWebServer.shutdown()

    @Test
    fun `fetchUser sends GET request with correct path`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"id":"123","name":"Alice","email":"alice@test.com"}""")
        )

        api.fetchUser("123")

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/users/123", request.path)
    }

    @Test
    fun `fetchUser parses response correctly`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"id":"123","name":"Alice","email":"alice@test.com"}""")
        )

        val user = api.fetchUser("123")

        assertEquals("123", user.id)
        assertEquals("Alice", user.name)
        assertEquals("alice@test.com", user.email)
    }

    @Test
    fun `createUser sends POST with JSON body`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody("""{"id":"1","name":"Alice","email":"alice@test.com"}""")
        )

        api.createUser(CreateUserRequest("Alice", "alice@test.com"))

        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        val body = request.body.readUtf8()
        assertTrue(body.contains("Alice"))
        assertTrue(body.contains("alice@test.com"))
    }

    @Test
    fun `404 response throws HttpException`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(404).setBody("""{"message":"Not found"}""")
        )

        assertThrows<HttpException> { api.fetchUser("999") }
    }

    @Test
    fun `server error throws HttpException`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500)
        )

        assertThrows<HttpException> { api.fetchUser("123") }
    }

    @Test
    fun `authorization header is sent`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"id":"123","name":"Alice"}""")
        )

        api.fetchUser("123")

        val request = mockWebServer.takeRequest()
        assertEquals("Bearer token123", request.getHeader("Authorization"))
    }
}
```

---

## Q4: How do you test a Repository with caching?

```kotlin
class UserRepository(
    private val api: UserApi,
    private val cache: UserCache,
    private val networkMonitor: NetworkMonitor
) {
    suspend fun getUser(id: String): Result<User> {
        // Check cache first
        cache.get(id)?.let { return Result.success(it) }

        // Check network
        if (!networkMonitor.isOnline()) {
            return Result.failure(IOException("No network"))
        }

        return try {
            val user = api.fetchUser(id)
            cache.put(id, user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Test
class UserRepositoryTest {

    private val api: UserApi = mock()
    private val cache: UserCache = mock()
    private val networkMonitor: NetworkMonitor = mock()
    private val repository = UserRepository(api, cache, networkMonitor)

    @Test
    fun `returns from cache without network call`() = runTest {
        whenever(cache.get("123")).thenReturn(User("Alice"))

        val result = repository.getUser("123")

        assertTrue(result.isSuccess)
        assertEquals("Alice", result.getOrNull()?.name)
        verify(api, never()).fetchUser(any())
    }

    @Test
    fun `fetches from network when cache is empty`() = runTest {
        whenever(cache.get("123")).thenReturn(null)
        whenever(networkMonitor.isOnline()).thenReturn(true)
        whenever(api.fetchUser("123")).thenReturn(User("Alice"))

        val result = repository.getUser("123")

        assertTrue(result.isSuccess)
        verify(api).fetchUser("123")
        verify(cache).put("123", User("Alice"))
    }

    @Test
    fun `returns error when offline and no cache`() = runTest {
        whenever(cache.get("123")).thenReturn(null)
        whenever(networkMonitor.isOnline()).thenReturn(false)

        val result = repository.getUser("123")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
        verify(api, never()).fetchUser(any())
    }

    @Test
    fun `network error returns failure`() = runTest {
        whenever(cache.get("123")).thenReturn(null)
        whenever(networkMonitor.isOnline()).thenReturn(true)
        whenever(api.fetchUser("123")).thenThrow(RuntimeException("Server error"))

        val result = repository.getUser("123")

        assertTrue(result.isFailure)
        verify(cache, never()).put(any(), any())
    }
}
```

---

## Q5: How do you test a Compose screen with ViewModel?

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel) {
    when (val state = viewModel.state) {
        is UiState.Idle -> Text("Click to load")
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> UserCard(state.user)
        is UiState.Error -> Text("Error: ${state.message}")
    }
}

// Test
class UserScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `idle state shows load prompt`() {
        val viewModel = UserViewModel(FakeRepository())
        composeRule.setContent { UserScreen(viewModel) }

        composeRule.onNodeWithText("Click to load").assertIsDisplayed()
    }

    @Test
    fun `loading state shows progress indicator`() {
        val viewModel = UserViewModel(FakeRepository()).apply {
            state = UiState.Loading
        }
        composeRule.setContent { UserScreen(viewModel) }

        composeRule.onNodeWithContentDescription("Loading").assertIsDisplayed()
    }

    @Test
    fun `success state shows user card`() {
        val viewModel = UserViewModel(FakeRepository()).apply {
            state = UiState.Success(User("Alice", "alice@test.com"))
        }
        composeRule.setContent { UserScreen(viewModel) }

        composeRule.onNodeWithText("Alice").assertIsDisplayed()
        composeRule.onNodeWithText("alice@test.com").assertIsDisplayed()
    }

    @Test
    fun `error state shows error message`() {
        val viewModel = UserViewModel(FakeRepository()).apply {
            state = UiState.Error("Network error")
        }
        composeRule.setContent { UserScreen(viewModel) }

        composeRule.onNodeWithText("Error: Network error").assertIsDisplayed()
    }
}
```

---

## Q6: How do you test WorkManager?

```gradle
testImplementation 'androidx.work:work-testing:2.9.0'
```

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val data = inputData.getString("user_id") ?: return Result.failure()
            syncUser(data)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }
}

// Test
class SyncWorkerTest {

    @get:Rule
    val workManagerTestRule = WorkManagerTestInitHelper.createTestRule()

    @Test
    fun `sync worker returns success`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setInputData(workDataOf("user_id" to "123"))
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `sync worker retries on failure`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setInputData(workDataOf("user_id" to "123"))
            .build()

        // Mock syncUser to throw
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `enqueue work request`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(workDataOf("user_id" to "123"))
            .build()

        WorkManager.getInstance(context).enqueue(request)

        val workInfo = WorkManager.getInstance(context)
            .getWorkInfoById(request.id)
            .get()

        assertNotNull(workInfo)
        assertNotEquals(WorkInfo.State.FAILED, workInfo.state)
    }
}
```

---

## Q7: How do you test LiveData transformations?

```kotlin
class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    val query = MutableLiveData("")

    val results: LiveData<List<Item>> = Transformations.switchMap(query) { q ->
        if (q.isBlank()) MutableLiveData(emptyList())
        else repository.search(q).asLiveData()
    }
}

// Test
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: SearchRepository = mock()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        viewModel = SearchViewModel(repository)
    }

    @Test
    fun `empty query returns empty results`() {
        val results = mutableListOf<List<Item>>()
        viewModel.results.observeForever { results.add(it) }

        viewModel.query.value = ""

        assertEquals(1, results.size)
        assertTrue(results[0].isEmpty())
    }

    @Test
    fun `non-empty query triggers search`() = runTest {
        whenever(repository.search("Alice"))
            .thenReturn(flowOf(listOf(Item("Alice"))))

        val results = mutableListOf<List<Item>>()
        viewModel.results.observeForever { results.add(it) }

        viewModel.query.value = "Alice"

        advanceUntilIdle()

        assertEquals(1, results.last().size)
        assertEquals("Alice", results.last()[0].name)
    }
}
```

---

## Q8: How do you test error handling in ViewModel?

```kotlin
class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    var state by mutableStateOf<UiState<Product>>(UiState.Idle)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadProduct(id: String) {
        viewModelScope.launch {
            state = UiState.Loading
            errorMessage = null
            try {
                val product = repository.getProduct(id)
                state = UiState.Success(product)
            } catch (e: IOException) {
                state = UiState.Error
                errorMessage = "Network error. Check your connection."
            } catch (e: HttpException) {
                state = UiState.Error
                errorMessage = when (e.code()) {
                    404 -> "Product not found"
                    500 -> "Server error"
                    else -> "Something went wrong"
                }
            } catch (e: Exception) {
                state = UiState.Error
                errorMessage = "Unexpected error"
            }
        }
    }
}

// Test
class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mock()
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setUp() {
        viewModel = ProductViewModel(repository)
    }

    @Test
    fun `io exception shows network error`() = runTest {
        whenever(repository.getProduct("123"))
            .thenThrow(IOException("No connection"))

        viewModel.loadProduct("123")
        advanceUntilIdle()

        assertEquals(UiState.Error, viewModel.state)
        assertEquals("Network error. Check your connection.", viewModel.errorMessage)
    }

    @Test
    fun `404 shows not found error`() = runTest {
        whenever(repository.getProduct("123"))
            .thenThrow(HttpException(404, "Not Found"))

        viewModel.loadProduct("123")
        advanceUntilIdle()

        assertEquals("Product not found", viewModel.errorMessage)
    }

    @Test
    fun `500 shows server error`() = runTest {
        whenever(repository.getProduct("123"))
            .thenThrow(HttpException(500, "Server Error"))

        viewModel.loadProduct("123")
        advanceUntilIdle()

        assertEquals("Server error", viewModel.errorMessage)
    }

    @Test
    fun `error message is cleared on retry`() = runTest {
        whenever(repository.getProduct("123"))
            .thenThrow(IOException("No connection"))

        viewModel.loadProduct("123")
        advanceUntilIdle()
        assertNotNull(viewModel.errorMessage)

        whenever(repository.getProduct("123"))
            .thenReturn(Product("123", "Widget"))
        viewModel.loadProduct("123")
        advanceUntilIdle()

        assertNull(viewModel.errorMessage)
        assertTrue(viewModel.state is UiState.Success)
    }
}
```

---

## Q9: How do you test a custom View?

```kotlin
class RatingBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var rating: Int = 0
        set(value) {
            field = value.coerceIn(0, 5)
            invalidate()
        }

    var onRatingChanged: ((Int) -> Unit)? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val newRating = (event.x / width * 5).toInt().coerceIn(0, 5)
            if (newRating != rating) {
                rating = newRating
                onRatingChanged?.invoke(newRating)
            }
        }
        return true
    }
}

// Test
@RunWith(AndroidJUnit4::class)
class RatingBarViewTest {

    @Test
    fun `rating is clamped to 0-5`() {
        val view = RatingBarView(ApplicationProvider.getApplicationContext())
        view.rating = 10
        assertEquals(5, view.rating)
        view.rating = -1
        assertEquals(0, view.rating)
    }

    @Test
    fun `onRatingChanged is called when rating changes`() {
        val view = RatingBarView(ApplicationProvider.getApplicationContext())
        var newRating = -1
        view.onRatingChanged = { newRating = it }

        view.rating = 3

        assertEquals(3, newRating)
    }

    @Test
    fun `touch updates rating`() {
        val view = RatingBarView(ApplicationProvider.getApplicationContext())
        view.layout(0, 0, 500, 100)

        val touchEvent = MotionEvent.obtain(
            0, 0, MotionEvent.ACTION_UP,
            250f, 50f, 0  // Middle of view → rating 2-3
        )

        view.onTouchEvent(touchEvent)

        assertTrue(view.rating in 2..3)
    }
}
```

---

## Q10: How do you test Navigation?

```kotlin
class NavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `start destination is login screen`() {
        composeRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun `login navigates to home`() {
        composeRule.onNodeWithTag("email_field").performTextInput("user@test.com")
        composeRule.onNodeWithTag("password_field").performTextInput("password")
        composeRule.onNodeWithText("Login").performClick()

        composeRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun `back from home returns to login`() {
        // Navigate to home first
        composeRule.onNodeWithText("Login").performClick()
        composeRule.onNodeWithText("Home").assertIsDisplayed()

        // Press back
        pressBack()

        composeRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun `deep link opens detail screen`() {
        val intent = Intent().apply {
            data = Uri.parse("myapp://detail/123")
        }
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.startActivity(intent)
        }

        composeRule.onNodeWithText("Item 123").assertIsDisplayed()
    }
}
```

---

## Q11: How do you test with Hilt/DI?

```kotlin
// Test module — replaces real dependencies
@Module
@TestInstallIn(
    component = SingletonComponent::class,
    replaces = [RepositoryModule::class]
)
object FakeRepositoryModule {
    @Provides
    @Singleton
    fun provideFakeRepository(): UserRepository = FakeUserRepository()
}

// Test
@HiltAndroidTest
class UserActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(UserActivity::class.java)

    @Inject
    lateinit var repository: UserRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun `displays user from repository`() {
        (repository as FakeUserRepository).userToReturn = User("Alice")

        onView(withId(R.id.userName))
            .check(matches(withText("Alice")))
    }
}
```

---

## Q12: How do you test a UseCase/Interactor?

```kotlin
class GetUserUseCase(
    private val repository: UserRepository,
    private val cache: UserCache
) {
    suspend operator fun invoke(id: String): Result<User> {
        return try {
            cache.get(id)?.let { return Result.success(it) }
            val user = repository.getUser(id)
            cache.put(id, user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Test
class GetUserUseCaseTest {

    private val repository: UserRepository = mock()
    private val cache: UserCache = mock()
    private val useCase = GetUserUseCase(repository, cache)

    @Test
    fun `returns cached user without repository call`() = runTest {
        whenever(cache.get("123")).thenReturn(User("Alice"))

        val result = useCase("123")

        assertTrue(result.isSuccess)
        verify(repository, never()).getUser(any())
    }

    @Test
    fun `fetches from repository when not cached`() = runTest {
        whenever(cache.get("123")).thenReturn(null)
        whenever(repository.getUser("123")).thenReturn(User("Alice"))

        val result = useCase("123")

        assertTrue(result.isSuccess)
        verify(cache).put("123", User("Alice"))
    }

    @Test
    fun `repository error returns failure`() = runTest {
        whenever(cache.get("123")).thenReturn(null)
        whenever(repository.getUser("123")).thenThrow(RuntimeException("Error"))

        val result = useCase("123")

        assertTrue(result.isFailure)
    }
}
```

---

## Q13: How do you test DataStore preferences?

```kotlin
class UserPreferences(private val dataStore: DataStore<Preferences>) {
    val themeFlow: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[THEME_KEY] ?: ThemeMode.SYSTEM.name)
    }

    suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { it[THEME_KEY] = theme.name }
    }

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }
}

// Test
class UserPreferencesTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferences: UserPreferences

    @Before
    fun setUp() {
        val tempFile = File.createTempFile("test", ".preferences_pb")
        dataStore = PreferenceDataStoreFactory.create { tempFile }
        preferences = UserPreferences(dataStore)
    }

    @Test
    fun `default theme is system`() = runTest {
        val theme = preferences.themeFlow.first()
        assertEquals(ThemeMode.SYSTEM, theme)
    }

    @Test
    fun `set theme updates flow`() = runTest {
        preferences.setTheme(ThemeMode.DARK)

        val theme = preferences.themeFlow.first()
        assertEquals(ThemeMode.DARK, theme)
    }

    @Test
    fun `theme change emits to observers`() = runTest {
        val themes = mutableListOf<ThemeMode>()
        val job = launch(UnconfinedTestDispatcher()) {
            preferences.themeFlow.collect { themes.add(it) }
        }

        preferences.setTheme(ThemeMode.LIGHT)
        preferences.setTheme(ThemeMode.DARK)

        job.cancel()

        assertEquals(
            listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK),
            themes
        )
    }
}
```

---

## Q14: How do you test a full login flow (integration)?

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginFlowIntegrationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun `valid login navigates to home screen`() {
        // Type email
        onView(withId(R.id.emailField))
            .perform(typeText("user@test.com"), closeSoftKeyboard())

        // Type password
        onView(withId(R.id.passwordField))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click login
        onView(withId(R.id.loginButton)).perform(click())

        // Verify home screen is shown
        onView(withId(R.id.welcomeText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `invalid email shows error`() {
        onView(withId(R.id.emailField))
            .perform(typeText("invalid"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        onView(withText("Invalid email"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `empty password shows error`() {
        onView(withId(R.id.emailField))
            .perform(typeText("user@test.com"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        onView(withText("Password required"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `wrong credentials shows error`() {
        onView(withId(R.id.emailField))
            .perform(typeText("wrong@test.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordField))
            .perform(typeText("wrong"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        onView(withText("Invalid credentials"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `login button is disabled with empty fields`() {
        onView(withId(R.id.loginButton))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.emailField))
            .perform(typeText("user@test.com"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.passwordField))
            .perform(typeText("password"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .check(matches(isEnabled()))
    }
}
```

---

## Q15: How do you test offline/online scenarios?

```kotlin
class SyncViewModel(
    private val repository: SyncRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    var state by mutableStateOf<SyncState>(SyncState.Idle)
        private set

    fun sync() {
        viewModelScope.launch {
            state = if (!networkMonitor.isOnline()) {
                SyncState.Offline("You are offline. Will sync when online.")
            } else {
                SyncState.Syncing
                try {
                    repository.syncAll()
                    SyncState.Success
                } catch (e: Exception) {
                    SyncState.Error(e.message ?: "Sync failed")
                }
            }
        }
    }
}

// Test
class SyncViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: SyncRepository = mock()
    private val networkMonitor: NetworkMonitor = mock()
    private lateinit var viewModel: SyncViewModel

    @Before
    fun setUp() {
        viewModel = SyncViewModel(repository, networkMonitor)
    }

    @Test
    fun `offline shows offline message`() = runTest {
        whenever(networkMonitor.isOnline()).thenReturn(false)

        viewModel.sync()
        advanceUntilIdle()

        assertTrue(viewModel.state is SyncState.Offline)
        verify(repository, never()).syncAll()
    }

    @Test
    fun `online syncs successfully`() = runTest {
        whenever(networkMonitor.isOnline()).thenReturn(true)
        whenever(repository.syncAll()).thenReturn(listOf(SyncResult("item1", true)))

        viewModel.sync()
        advanceUntilIdle()

        assertTrue(viewModel.state is SyncState.Success)
        verify(repository).syncAll()
    }

    @Test
    fun `sync error shows error message`() = runTest {
        whenever(networkMonitor.isOnline()).thenReturn(true)
        whenever(repository.syncAll()).thenThrow(RuntimeException("Server error"))

        viewModel.sync()
        advanceUntilIdle()

        assertTrue(viewModel.state is SyncState.Error)
        assertEquals("Server error", (viewModel.state as SyncState.Error).message)
    }

    @Test
    fun `retry after offline works when online`() = runTest {
        // First attempt — offline
        whenever(networkMonitor.isOnline()).thenReturn(false)
        viewModel.sync()
        advanceUntilIdle()
        assertTrue(viewModel.state is SyncState.Offline)

        // Network restored
        whenever(networkMonitor.isOnline()).thenReturn(true)
        whenever(repository.syncAll()).thenReturn(emptyList())

        viewModel.sync()
        advanceUntilIdle()

        assertTrue(viewModel.state is SyncState.Success)
    }
}
```

---

## 🔗 Related Topics
- [Unit Testing](UnitTesting.md)
- [Mockito](Mockito.md)
- [Espresso](Espresso.md)
- [Compose Testing](ComposeTesting.md)
- [TDD](TDD.md)
