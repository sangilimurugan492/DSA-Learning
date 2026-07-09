# Mockito

## Q1: What is Mockito and why use it?

Mockito is a mocking framework that lets you create test doubles (mocks, spies, stubs) to isolate the code under test.

### Why mock?
| Reason | Example |
|--------|---------|
| Isolate dependencies | Test ViewModel without real API |
| Control behavior | Make API return error |
| Speed | No network/database calls |
| Deterministic | Same result every time |

### Setup
```gradle
testImplementation 'org.mockito:mockito-core:5.10.0'
testImplementation 'org.mockito.kotlin:mockito-kotlin:5.2.1'
```

---

## Q2: How do you create a mock?

```kotlin
import org.mockito.kotlin.*

interface UserService {
    fun getUser(id: String): User
    fun saveUser(user: User): Boolean
}

class UserViewModelTest {

    // 1. Create mock
    private val mockService: UserService = mock()

    @Test
    fun `mock returns default value`() {
        // Mock returns null/0/false by default
        val user = mockService.getUser("123")
        assertNull(user)  // Returns null
    }

    @Test
    fun `stub method to return value`() {
        // Stub
        whenever(mockService.getUser("123"))
            .thenReturn(User("123", "Alice"))

        // Use
        val user = mockService.getUser("123")

        // Verify
        assertEquals("Alice", user.name)
    }
}
```

---

## Q3: How do you stub methods?

```kotlin
@Test
fun `stub with different return values`() {
    // Return different values for different calls
    whenever(mockService.getUser("1"))
        .thenReturn(User("1", "Alice"))
        .thenReturn(User("1", "Alice 2"))  // Second call

    assertEquals("Alice", mockService.getUser("1").name)
    assertEquals("Alice 2", mockService.getUser("1").name)
}

@Test
fun `stub to throw exception`() {
    whenever(mockService.getUser("999"))
        .thenThrow(RuntimeException("Not found"))

    assertThrows<RuntimeException> {
        mockService.getUser("999")
    }
}

@Test
fun `stub with any matcher`() {
    whenever(mockService.getUser(any()))
        .thenReturn(User("1", "Default"))

    assertEquals("Default", mockService.getUser("any-id").name)
}

@Test
fun `stub with argument matcher`() {
    whenever(mockService.getUser(eq("123")))
        .thenReturn(User("123", "Alice"))

    assertEquals("Alice", mockService.getUser("123").name)
    assertNull(mockService.getUser("456"))
}
```

### Argument matchers
| Matcher | Description |
|---------|-------------|
| `any()` | Any value |
| `eq(value)` | Equal to value |
| `isNull()` | Null value |
| `isNotNull()` | Non-null value |
| `argThat { }` | Custom predicate |
| `same(value)` | Same reference |

---

## Q4: How do you verify interactions?

```kotlin
@Test
fun `verify method was called`() {
    val viewModel = UserViewModel(mockService)

    viewModel.loadUser("123")

    // Verify method was called once
    verify(mockService).getUser("123")

    // Verify method was called with any argument
    verify(mockService).getUser(any())

    // Verify never called
    verify(mockService, never()).saveUser(any())

    // Verify called exactly N times
    verify(mockService, times(2)).getUser("123")

    // Verify called at least N times
    verify(mockService, atLeast(1)).getUser(any())

    // Verify called at most N times
    verify(mockService, atMost(3)).getUser(any())

    // Verify order of calls
    val inOrder = inOrder(mockService)
    inOrder.verify(mockService).getUser("123")
    inOrder.verify(mockService).saveUser(any())
}
```

### Verification modes
| Mode | Description |
|------|-------------|
| `times(n)` | Exactly n times |
| `never()` | Never called |
| `atLeast(n)` | At least n times |
| `atMost(n)` | At most n times |
| `only()` | Only this method called |
| `timeout(ms)` | Within timeout |

---

## Q5: How do you use ArgumentCaptor?

```kotlin
@Test
fun `capture argument passed to mock`() {
    val viewModel = UserViewModel(mockService)
    val captor = argumentCaptor<User>()

    viewModel.saveUser("Alice", "alice@test.com")

    // Capture the User object passed to saveUser
    verify(mockService).saveUser(captor.capture())

    val capturedUser = captor.firstValue
    assertEquals("Alice", capturedUser.name)
    assertEquals("alice@test.com", capturedUser.email)
}

@Test
fun `capture multiple arguments`() {
    val captor = argumentCaptor<User>()

    viewModel.saveUser("Alice", "alice@test.com")
    viewModel.saveUser("Bob", "bob@test.com")

    verify(mockService, times(2)).saveUser(captor.capture())

    assertEquals(2, captor.allValues.size)
    assertEquals("Alice", captor.allValues[0].name)
    assertEquals("Bob", captor.allValues[1].name)
}
```

### When to use ArgumentCaptor
- Verify complex object passed to mock
- Verify multiple calls with different arguments
- Assert on specific fields of captured object

---

## Q6: What is the difference between mock and spy?

```kotlin
// MOCK — replaces all methods with stubs
@Test
fun `mock does not call real methods`() {
    val mockList: MutableList<String> = mock()

    mockList.add("item")  // Does nothing (stubbed)

    assertTrue(mockList.isEmpty())  // true — real size() not called
    verify(mockList).add("item")    // Can verify
}

// SPY — wraps real object, calls real methods
@Test
fun `spy calls real methods`() {
    val spyList: MutableList<String> = spy(arrayListOf())

    spyList.add("item")  // Calls real add()

    assertEquals(1, spyList.size)  // Real size() called
    verify(spyList).add("item")
}

// Stub a spy method
@Test
fun `stub spy method`() {
    val spyList: MutableList<String> = spy(arrayListOf("a", "b", "c"))

    // Stub size() to return 100
    doReturn(100).whenever(spyList).size

    assertEquals(100, spyList.size)  // Stubbed
    assertEquals(3, spyList)  // Real data still there
}
```

### mock vs spy
| Feature | mock() | spy() |
|---------|--------|-------|
| Real methods called | ❌ No | ✅ Yes |
| Default return | null/0/false | Real value |
| Use case | Replace dependency | Wrap real object |
| Stub syntax | `whenever().thenReturn()` | `doReturn().whenever()` |

---

## Q7: How do you mock suspend functions?

```kotlin
interface UserApi {
    suspend fun fetchUser(id: String): User
    suspend fun updateUser(user: User): Boolean
}

class UserRepositoryTest {

    private val api: UserApi = mock()
    private val repository = UserRepository(api)

    @Test
    fun `mock suspend function`() = runTest {
        whenever(api.fetchUser("123"))
            .thenReturn(User("123", "Alice"))

        val result = repository.getUser("123")

        assertEquals("Alice", result.name)
    }

    @Test
    fun `mock suspend function throws`() = runTest {
        whenever(api.fetchUser("999"))
            .thenThrow(RuntimeException("Not found"))

        assertThrows<RuntimeException> {
            repository.getUser("999")
        }
    }

    @Test
    fun `verify suspend function called`() = runTest {
        repository.getUser("123")

        verify(api).fetchUser("123")
    }
}
```

---

## Q8: How do you use MockWebServer?

```gradle
testImplementation 'com.squareup.okhttp3:mockwebserver:4.12.0'
```

```kotlin
class UserApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: UserApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(UserApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `fetch user returns parsed user`() = runTest {
        // Enqueue mock response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"id": "123", "name": "Alice"}""")
        )

        // Call API
        val user = api.fetchUser("123")

        // Assert
        assertEquals("Alice", user.name)

        // Verify request
        val request = mockWebServer.takeRequest()
        assertEquals("/users/123", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `api returns error`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(404)
        )

        assertThrows<HttpException> {
            api.fetchUser("999")
        }
    }
}
```

---

## Q9: How do you mock static methods?

```kotlin
import org.mockito.MockedStatic

class DateUtilsTest {

    @Test
    fun `mock static method`() {
        // Mock static method
        `when`<Long>(System::class.java).useStaticMethod {
            System.currentTimeMillis()
        }.thenReturn(1700000000000L)

        assertEquals(1700000000000L, System.currentTimeMillis())
    }

    // Alternative: mockStatic
    @Test
    fun `mock static with mockStatic`() {
        mockStatic(TextUtils::class.java).use { mocked ->
            mocked.`when`<Boolean> { TextUtils.isEmpty("") }.thenReturn(false)

            assertFalse(TextUtils.isEmpty(""))
        }
    }
}
```

### When to mock static
- `TextUtils.isEmpty()`
- `System.currentTimeMillis()`
- `Uri.parse()`
- `Log.d()` (use `Log` wrapper instead)

---

## Q10: How do you mock final classes?

```kotlin
// Mockito 5+ mocks final classes by default

// For older versions, create mockito-extensions
// src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker
// content: mock-maker-inline

// Now you can mock final classes
final class FinalUser {
    fun getName(): String = "Real"
}

@Test
fun `mock final class`() {
    val mock: FinalUser = mock()
    whenever(mock.getName()).thenReturn("Mocked")

    assertEquals("Mocked", mock.getName())
}
```

---

## Q11: How do you use @Mock annotation?

```kotlin
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class UserViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var userService: UserService

    @Mock
    lateinit var analytics: Analytics

    @InjectMocks
    lateinit var viewModel: UserViewModel

    @Test
    fun `annotations create mocks automatically`() {
        whenever(userService.getUser("123"))
            .thenReturn(User("123", "Alice"))

        viewModel.loadUser("123")

        verify(userService).getUser("123")
        verify(analytics).trackEvent("user_loaded")
    }
}
```

### Annotation summary
| Annotation | Purpose |
|-----------|---------|
| `@Mock` | Creates a mock |
| `@InjectMocks` | Creates object with mocks injected |
| `@Spy` | Creates a spy |
| `@Captor` | Creates an ArgumentCaptor |

---

## Q12: How do you mock Kotlin classes with Mockito-Kotlin?

```kotlin
import org.mockito.kotlin.*

// mockito-kotlin provides Kotlin-friendly DSL

@Test
fun `kotlin-friendly mocking`() {
    val mockService: UserService = mock {
        on { getUser("123") } doReturn User("123", "Alice")
        on { getUser("999") } doThrow RuntimeException("Not found")
    }

    assertEquals("Alice", mockService.getUser("123").name)
    assertThrows<RuntimeException> { mockService.getUser("999") }
}

// Stub with relaxed mock (returns default values without stubbing)
@Test
fun `relaxed mock returns defaults`() {
    val mockService: UserService = mock(relaxed = true)

    // No stubbing needed — returns default User
    val user = mockService.getUser("123")
    // user is a default User object
}

// Verify with Kotlin DSL
@Test
fun `verify with kotlin DSL`() {
    viewModel.saveUser("Alice", "alice@test.com")

    verify(mockService).saveUser(
        argThat {
            this.name == "Alice" && this.email == "alice@test.com"
        }
    )
}
```

---

## Q13: How do you test with in-memory Room database?

```kotlin
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert and read user`() = runTest {
        val user = User(id = "1", name = "Alice", email = "alice@test.com")

        userDao.insert(user)

        val loaded = userDao.getUser("1")
        assertEquals("Alice", loaded?.name)
    }

    @Test
    fun `delete user removes from db`() = runTest {
        userDao.insert(User("1", "Alice", "alice@test.com"))

        userDao.delete("1")

        assertNull(userDao.getUser("1"))
    }
}
```

### In-memory DB
- Lives in memory (no disk)
- Destroyed when test ends
- Fast — no real I/O
- Tests real SQL queries

---

## Q14: How do you mock a Repository?

```kotlin
interface UserRepository {
    suspend fun getUser(id: String): User
    suspend fun saveUser(user: User): Result<Unit>
}

class UserViewModelTest {

    private val repository: UserRepository = mock()
    private lateinit var viewModel: UserViewModel

    @Before
    fun setUp() {
        viewModel = UserViewModel(repository)
    }

    @Test
    fun `load user success updates state`() = runTest {
        whenever(repository.getUser("123"))
            .thenReturn(User("123", "Alice"))

        viewModel.loadUser("123")

        assertEquals(UiState.Success(User("123", "Alice")), viewModel.state.value)
    }

    @Test
    fun `load user error shows error state`() = runTest {
        whenever(repository.getUser("123"))
            .thenThrow(RuntimeException("Network error"))

        viewModel.loadUser("123")

        assertTrue(viewModel.state.value is UiState.Error)
    }

    @Test
    fun `save user calls repository`() = runTest {
        whenever(repository.saveUser(any()))
            .thenReturn(Result.success(Unit))

        viewModel.saveUser("Alice", "alice@test.com")

        verify(repository).saveUser(
            argThat { name == "Alice" && email == "alice@test.com" }
        )
    }
}
```

---

## Q15: What are common Mockito pitfalls?

### 1. Stubbing but not verifying
```kotlin
// ❌ Bad — stubbing without verifying behavior
@Test
fun `bad test`() {
    whenever(service.getUser("123")).thenReturn(user)
    // No assertion or verify — test always passes
}

// ✅ Good — verify behavior
@Test
fun `good test`() {
    whenever(service.getUser("123")).thenReturn(user)
    val result = viewModel.loadUser("123")
    assertEquals(user, result)
    verify(service).getUser("123")
}
```

### 2. Over-mocking
```kotlin
// ❌ Bad — mocking everything, testing nothing
@Test
fun `over mocked`() {
    val mockA: A = mock()
    val mockB: B = mock()
    val mockC: C = mock()
    // If everything is mocked, you're testing Mockito, not your code
}

// ✅ Good — mock only external dependencies
@Test
fun `mock only api`() {
    val mockApi: UserApi = mock()
    val repository = UserRepository(mockApi)  // Real repository
    // Test real repository logic with mocked API
}
```

### 3. Using `when` instead of `whenever`
```kotlin
// ❌ Bad — `when` is a Kotlin keyword
`when`(mock.method()).thenReturn(value)

// ✅ Good — use `whenever`
whenever(mock.method()).thenReturn(value)
```

### 4. Not resetting mocks
```kotlin
@After
fun tearDown() {
    // Reset mocks between tests (or create new ones)
    Mockito.reset(mockService)
}
```

### Pitfalls summary
| Pitfall | Solution |
|---------|---------|
| Stubbing without verifying | Always assert or verify |
| Over-mocking | Mock only external deps |
| `when` keyword conflict | Use `whenever` |
| Mock state leaking | Reset in `@After` or create new |
| Mocking value classes | Use interface instead |
| Mocking Kotlin final classes | Use `mock-maker-inline` |

---

## 🔗 Related Topics
- [Unit Testing](UnitTesting.md)
- [Espresso](Espresso.md)
- [Testing Scenarios](TestingScenarios.md)
