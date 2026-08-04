# Testing (Unit, UI, Integration)

## 📖 Explanation

Testing ensures code correctness and prevents regressions. Android testing follows a pyramid: many unit tests, fewer integration tests, fewest UI tests.

### Testing Pyramid
```
        /\
       /UI\          ← Few, slow, brittle
      /------\
     /Integration\   ← Medium
    /--------------\
   /  Unit Tests    \  ← Many, fast, reliable
  /-------------------\
```

### Test Types
| Type             | Framework        | Tests                          |
|------------------|------------------|--------------------------------|
| Unit Test         | JUnit + Mockito  | Pure logic, ViewModels, Repos  |
| Instrumented Test | AndroidX Test    | Room, Context, ContentProvider  |
| UI Test           | Espresso/Compose | User flows, interactions        |

### JUnit Annotations
| Annotation       | Description                          |
|------------------|--------------------------------------|
| `@Test`           | Marks a test method                 |
| `@Before`         | Runs before each test               |
| `@After`          | Runs after each test                |
| `@BeforeClass`    | Runs once before all tests          |
| `@Ignore`         | Skips a test                        |
| `@RunWith`        | Custom test runner                  |

### Mockito
Mock dependencies to isolate the unit under test.

```kotlin
val mockRepo = mock(UserRepository::class.java)
`when`(mockRepo.getUsers()).thenReturn(listOf(User(1, "Alice")))
```

### Coroutines Testing
```kotlin
@Test
fun test() = runTest {
    val result = repository.getUsers()
    assertEquals(1, result.size)
}
```

### Espresso (UI Testing)
```kotlin
onView(withId(R.id.loginButton)).perform(click())
onView(withId(R.id.welcomeText)).check(matches(withText("Welcome")))
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.*

// --- Unit Test: ViewModel ---
class UserViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()  // For LiveData

    private lateinit var viewModel: UserViewModel
    private val repository: UserRepository = mock()

    @Before
    fun setup() {
        viewModel = UserViewModel(repository)
    }

    @Test
    fun `load users success updates state`() = runTest {
        // Arrange
        val users = listOf(User(1, "Alice", "alice@test.com"))
        whenever(repository.getUsers()).thenReturn(users)

        // Act
        viewModel.loadUsers()

        // Assert
        assertEquals(users, viewModel.uiState.value)
        verify(repository).getUsers()
    }

    @Test
    fun `load users error shows error state`() = runTest {
        // Arrange
        whenever(repository.getUsers()).thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.loadUsers()

        // Assert
        assertTrue(viewModel.uiState.value is NetworkResult.Error)
    }

    @Test
    fun `add user calls repository`() = runTest {
        viewModel.addUser("Bob", "bob@test.com")
        verify(repository).addUser("Bob", "bob@test.com")
    }
}

// --- Unit Test: Repository ---
class UserRepositoryTest {

    private lateinit var repository: UserRepository
    private val api: ApiService = mock()
    private val dao: UserDao = mock()

    @Before
    fun setup() {
        repository = UserRepository(api, dao)
    }

    @Test
    fun `get users returns from cache when available`() = runTest {
        val cachedUsers = listOf(User(1, "Alice", "alice@test.com"))
        whenever(dao.getAllUsers()).thenReturn(cachedUsers)

        val result = repository.getUsers()

        assertEquals(cachedUsers, result)
        verify(api, never()).getUsers()  // API not called
    }

    @Test
    fun `get users fetches from API when cache empty`() = runTest {
        val apiUsers = listOf(User(1, "Bob", "bob@test.com"))
        whenever(dao.getAllUsers()).thenReturn(emptyList())
        whenever(api.getUsers()).thenReturn(apiUsers)

        val result = repository.getUsers()

        assertEquals(apiUsers, result)
        verify(api).getUsers()
    }
}

// --- Instrumented Test: Room Database ---
// androidTest
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetUser() = runTest {
        val user = User(name = "Alice", email = "alice@test.com", age = 30)
        val id = dao.insert(user)

        val retrieved = dao.getUserById(id)
        assertNotNull(retrieved)
        assertEquals("Alice", retrieved?.name)
    }

    @Test
    fun deleteAllUsers() = runTest {
        dao.insert(User(name = "Alice", email = "a@t.com", age = 30))
        dao.insert(User(name = "Bob", email = "b@t.com", age = 25))

        dao.deleteAll()

        assertEquals(0, dao.count())
    }
}

// --- UI Test: Espresso ---
// androidTest
class LoginFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun loginWithValidCredentials_showsWelcome() {
        // Type email
        onView(withId(R.id.emailInput))
            .perform(typeText("user@test.com"))
        // Type password
        onView(withId(R.id.passwordInput))
            .perform(typeText("password123"))
        // Close keyboard
        closeSoftKeyboard()
        // Click login
        onView(withId(R.id.loginButton))
            .perform(click())
        // Verify welcome text
        onView(withId(R.id.welcomeText))
            .check(matches(withText("Welcome")))
    }

    @Test
    fun loginWithEmptyFields_showsError() {
        onView(withId(R.id.loginButton)).perform(click())
        onView(withText("Please fill all fields"))
            .check(matches(isDisplayed()))
    }
}

// --- Compose UI Test ---
class ComposeLoginTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginWithValidCredentials_navigatesToHome() {
        composeRule.onNodeWithTag("EmailField")
            .performTextInput("user@test.com")
        composeRule.onNodeWithTag("PasswordField")
            .performTextInput("password123")
        composeRule.onNodeWithTag("LoginButton")
            .performClick()
        composeRule.onNodeWithTag("WelcomeText")
            .assertExists()
    }
}
```

```groovy
// build.gradle dependencies
dependencies {
    // Unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Instrumented tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.room:room-testing:2.6.1")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
}
```

---

## ❓ Interview Questions

1. **What is the testing pyramid and why is it important?**
   - Many fast unit tests at the base, fewer integration tests in the middle, fewest UI tests at the top. Unit tests are fast and reliable; UI tests are slow and brittle. The pyramid maximizes coverage while minimizing flakiness.

2. **How do you test coroutines in Android?**
   - Use `runTest` from `kotlinx-coroutines-test`. It creates a virtual time controller — delays are skipped. Use `TestDispatcher` to control execution order. For LiveData, add `InstantTaskExecutorRule`.

3. **What is the difference between `mock` and `spy` in Mockito?**
   - `mock` creates a dummy object — all methods return defaults unless stubbed. `spy` wraps a real object — real methods execute unless stubbed. Use `mock` for dependencies, `spy` for partial mocking.

4. **What is Espresso and how does it work?**
   - Espresso is a UI testing framework. It finds views by matchers (`onView(withId(...))`), performs actions (`perform(click())`), and checks results (`check(matches(...))`). It auto-syncs with the main thread (idle resources).

5. **How do you test Room database?**
   - Use an in-memory database (`Room.inMemoryDatabaseBuilder`) in instrumented tests. It's created in memory (fast, no persistence) and destroyed after tests. Test DAO methods directly with `runTest`.

6. **What is TDD (Test-Driven Development) and how do you apply it in Android?**
   - TDD is the Red-Green-Refactor cycle: (1) **Red** — write a failing test first. (2) **Green** — write the minimum code to pass the test. (3) **Refactor** — improve the code while keeping tests green. In Android: (1) Write a ViewModel test that defines the expected behavior: `@Test fun 'load users returns success'() { ... }`. (2) Run it — it fails (ViewModel doesn't exist yet). (3) Implement the ViewModel to pass the test. (4) Refactor — extract Repository, add error handling, keep tests passing. Benefits: (1) Tests are written, not added later. (2) Code is designed for testability from the start. (3) Regression safety net. (4) Better API design — you think about usage before implementation. Apply TDD to ViewModels, Use Cases, and Repositories (unit tests). For UI, use BDD or integration tests. TDD works best with clean architecture — each layer is independently testable.

7. **How do you test coroutines and Flow in Android?**
   - **Coroutines**: Use `runTest` from `kotlinx-coroutines-test`. It creates a virtual time controller — `delay()` is skipped. Use `TestDispatcher` (e.g., `StandardTestDispatcher` or `UnconfinedTestDispatcher`) to control execution. For `viewModelScope`, use `MainDispatcherRule` with `@get:Rule`: `val mainDispatcherRule = MainDispatcherRule()`. This replaces `Dispatchers.Main` with a test dispatcher. **Flow**: (1) `flow.first()` — collect the first emission. (2) `flow.toList(mutableList)` — collect all emissions into a list (for finite flows). (3) `Turbine` library — test hot flows (StateFlow, SharedFlow): `flow.test { assertEquals(expected, awaitItem()); awaitComplete() }`. (4) For Room Flow queries, use in-memory database + `first()`. (5) For infinite flows, use `Job` to cancel after first item. Always use `runTest` — never `runBlocking` in tests (it blocks the real thread). For `StateFlow`, check `.value` directly — no need to collect.

8. **What is the difference between `mock`, `spy`, and `fake` in testing?**
   - **Mock** — creates a dummy object that returns default values. You stub specific methods: `whenever(repo.getUsers()).thenReturn(list)`. Unstubbed methods return defaults (null, 0, false). Use for dependencies where you want full control. **Spy** — wraps a real object. Real methods execute unless stubbed: `val spy = spy(RealRepository()); whenever(spy.getUsers()).thenReturn(emptyList())`. Use for partial mocking — test some methods while stubbing others. **Fake** — a real implementation that works but is simplified for testing: `class FakeUserRepository : UserRepository { val users = mutableListOf<User>(); override suspend fun getUsers() = users }`. Fakes are stateful and more realistic than mocks. Use fakes when mocks become too verbose or when you need stateful behavior. Best practice: prefer fakes for repositories (they're reusable and testable), mocks for simple dependencies. Avoid spies — they indicate tight coupling.

9. **How do you test Compose UI?**
   - Use `createAndroidComposeRule<MainActivity>()` or `createComposeRule()` (no Activity needed). Key APIs: (1) `composeRule.onNodeWithText("Login").performClick()` — find and interact. (2) `composeRule.onNodeWithTag("EmailField").performTextInput("test@email.com")` — text input. (3) `composeRule.onNodeWithText("Welcome").assertIsDisplayed()` — assertions. (4) `composeRule.onAllNodesWithText("Item")[0]` — find multiple. (5) `composeRule.waitForIdle()` — wait for recomposition. (6) `composeRule.onNode(hasTestTag("Button") and hasText("Save"))` — matchers. Testing strategies: (1) Use `testTag` instead of text for stable selectors. (2) Test state changes, not implementation details. (3) Test user flows (type → click → verify). (4) For ViewModel, pass a fake/mock. (5) For animations, use `mainClock` to control time. Compose testing is faster than Espresso — no Activity launch needed for pure Compose tests. Always set `testTag` modifiers in production code for testability.

10. **What is dependency injection testing and how do you use fake modules?**
    - For unit tests: pass fakes/mocks directly to the constructor — no DI needed. For instrumented/UI tests: use Hilt testing. (1) Create a fake module: `@Module @TestInstallIn(component = SingletonComponent::class, replaces = [RepositoryModule::class]) object FakeRepositoryModule { @Provides @Singleton fun provideFakeRepo(): UserRepository = FakeUserRepository() }`. (2) Use `@HiltAndroidTest` and `@UninstallModules(RepositoryModule::class)` to replace specific modules. (3) Use `@HiltAndroidRule` to set up the test component: `@get:Rule val hiltRule = HiltAndroidRule(this)`. (4) Inject the fake: `@Inject lateinit var fakeRepo: UserRepository`. (5) For ViewModels in tests, use `@HiltViewModel` with injected fakes. Benefits: test with real DI graph, swap specific dependencies with fakes. Best practice: create reusable fakes in a `test` or `androidTest` source set. Use `@UninstallModules` for targeted replacement.

11. **How do you write integration tests for Room + Retrofit + Repository?**
    - (1) **Room integration test** — use `Room.inMemoryDatabaseBuilder()` with a real SQLite. Test DAO queries, migrations, and relations. (2) **Retrofit integration test** — use `MockWebServer` to simulate API responses. Test serialization, error handling, and interceptors. (3) **Repository integration test** — combine Room (in-memory) + Retrofit (MockWebServer) + Repository. Test the full flow: cache miss → network call → save to Room → return data. (4) **End-to-end test** — launch the Activity with Espresso/Compose, inject fake dependencies, verify the UI shows data from Room. Use `runTest` for coroutines. Test scenarios: online (API returns data), offline (API fails, Room has cache), first launch (empty cache, API call), conflict (API and cache differ). Use `@get:Rule val hiltRule = HiltAndroidRule(this)` for DI. Integration tests catch issues that unit tests miss — serialization bugs, Room query errors, caching logic.

12. **What are best practices for Android testing?**
    - (1) **Follow the testing pyramid** — 70% unit tests, 20% integration tests, 10% UI tests. (2) **Test behavior, not implementation** — don't test private methods. Test what the class does, not how. (3) **Use fakes over mocks** — fakes are reusable and realistic. Create a `FakeRepository` that implements the interface. (4) **Name tests clearly** — `'when login with valid credentials then navigate to home'` or `'loadUsers_success_updatesState'`. (5) **Arrange-Act-Assert** — structure tests in three sections. (6) **One assertion per test** (mostly) — each test should verify one behavior. (7) **Test edge cases** — empty lists, null values, network errors, concurrent access. (8) **Use `runTest`** for coroutines — never `runBlocking`. (9) **Use `MainDispatcherRule`** for ViewModel tests that use `viewModelScope`. (10) **Keep tests fast** — unit tests should run in <100ms. (11) **Use `@Before`/`@After`** for setup/teardown. (12) **Don't test the framework** — don't test if `LiveData` notifies observers. Test your logic.

---

## 🔗 Related Topics
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
- [Room Database](../intermediate/RoomDatabase.md)
