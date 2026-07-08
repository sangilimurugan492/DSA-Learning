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

---

## 🔗 Related Topics
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
- [Room Database](../intermediate/RoomDatabase.md)
