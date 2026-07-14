# TDD (Test-Driven Development)

## Q1: What is TDD?

TDD is a development practice where you write tests **before** writing production code.

### Red-Green-Refactor cycle
```
    🔴 RED          🟢 GREEN         🔵 REFACTOR
   Write test  →  Make it pass  →  Improve code
  (fails)         (minimal code)    (keep tests green)
```

### TDD flow
1. **Red** — Write a failing test for the desired behavior
2. **Green** — Write the minimum code to make the test pass
3. **Refactor** — Improve code quality while keeping tests green
4. **Repeat**

### TDD benefits
| Benefit | Description |
|---------|-------------|
| Better design | Testable code = better architecture |
| Fewer bugs | Tests catch regressions |
| Living documentation | Tests show how code works |
| Confidence | Refactor without fear |
| Fast feedback | Know immediately if something breaks |

---

## Q2: How do you do TDD with a simple example?

### Feature: FizzBuzz

**Step 1: RED — Write first test**
```kotlin
class FizzBuzzTest {

    @Test
    fun `number 1 returns 1`() {
        val result = FizzBuzz().convert(1)
        assertEquals("1", result)
    }
}
// ❌ Won't compile — FizzBuzz doesn't exist
```

**Step 2: GREEN — Make it pass**
```kotlin
class FizzBuzz {
    fun convert(n: Int): String = "1"
}
// ✅ Test passes (hardcoded, but that's OK)
```

**Step 3: RED — Add next test**
```kotlin
@Test
fun `number 2 returns 2`() {
    val result = FizzBuzz().convert(2)
    assertEquals("2", result)
}
// ❌ Fails — returns "1" for all inputs
```

**Step 4: GREEN — Generalize**
```kotlin
class FizzBuzz {
    fun convert(n: Int): String = n.toString()
}
// ✅ Both tests pass
```

**Step 5: RED — Fizz**
```kotlin
@Test
fun `number 3 returns Fizz`() {
    assertEquals("Fizz", FizzBuzz().convert(3))
}
// ❌ Fails — returns "3"
```

**Step 6: GREEN — Implement Fizz**
```kotlin
class FizzBuzz {
    fun convert(n: Int): String = when {
        n % 3 == 0 -> "Fizz"
        else -> n.toString()
    }
}
// ✅ All tests pass
```

**Step 7: RED — Buzz**
```kotlin
@Test
fun `number 5 returns Buzz`() {
    assertEquals("Buzz", FizzBuzz().convert(5))
}
```

**Step 8: GREEN — Add Buzz**
```kotlin
class FizzBuzz {
    fun convert(n: Int): String = when {
        n % 3 == 0 -> "Fizz"
        n % 5 == 0 -> "Buzz"
        else -> n.toString()
    }
}
```

**Step 9: RED — FizzBuzz**
```kotlin
@Test
fun `number 15 returns FizzBuzz`() {
    assertEquals("FizzBuzz", FizzBuzz().convert(15))
}
```

**Step 10: GREEN — Final implementation**
```kotlin
class FizzBuzz {
    fun convert(n: Int): String = when {
        n % 15 == 0 -> "FizzBuzz"
        n % 3 == 0 -> "Fizz"
        n % 5 == 0 -> "Buzz"
        else -> n.toString()
    }
}
// ✅ All tests pass — done!
```

---

## Q3: What are the Three Laws of TDD?

| Law | Description |
|-----|-------------|
| 1 | Write no production code except to pass a failing test |
| 2 | Write only enough of a test to demonstrate failure |
| 3 | Write only enough production code to pass the test |

### Key principles
- **One test at a time** — don't write 5 tests then implement
- **Minimal code** — don't anticipate future requirements
- **Refactor after green** — not during red
- **Small steps** — tiny increments, not big leaps

---

## Q4: How do you TDD a ViewModel?

### Feature: Login ViewModel

**RED 1: Initial state is idle**
```kotlin
class LoginViewModelTest {

    @Test
    fun `initial state is idle`() {
        val viewModel = LoginViewModel(fakeRepo)
        assertEquals(LoginState.Idle, viewModel.state.value)
    }
}
```

**GREEN 1**
```kotlin
class LoginViewModel(private val repo: UserRepository) : ViewModel() {
    var state by mutableStateOf<LoginState>(LoginState.Idle)
        private set
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
```

**RED 2: Login with valid credentials → Success**
```kotlin
@Test
fun `login with valid credentials returns success`() = runTest {
    whenever(repo.login("user@test.com", "pass"))
        .thenReturn(User("Alice"))

    viewModel.login("user@test.com", "pass")

    assertEquals(LoginState.Success(User("Alice")), viewModel.state.value)
}
```

**GREEN 2**
```kotlin
fun login(email: String, password: String) {
    viewModelScope.launch {
        state = LoginState.Loading
        val user = repo.login(email, password)
        state = LoginState.Success(user)
    }
}
```

**RED 3: Login with invalid credentials → Error**
```kotlin
@Test
fun `login with invalid credentials returns error`() = runTest {
    whenever(repo.login("wrong", "wrong"))
        .thenThrow(RuntimeException("Invalid"))

    viewModel.login("wrong", "wrong")

    assertTrue(viewModel.state.value is LoginState.Error)
    assertEquals("Invalid", (viewModel.state.value as LoginState.Error).message)
}
```

**GREEN 3**
```kotlin
fun login(email: String, password: String) {
    viewModelScope.launch {
        state = LoginState.Loading
        try {
            val user = repo.login(email, password)
            state = LoginState.Success(user)
        } catch (e: Exception) {
            state = LoginState.Error(e.message ?: "Unknown error")
        }
    }
}
```

**REFACTOR**
```kotlin
fun login(email: String, password: String) {
    viewModelScope.launch {
        state = LoginState.Loading
        state = try {
            LoginState.Success(repo.login(email, password))
        } catch (e: Exception) {
            LoginState.Error(e.message ?: "Unknown error")
        }
    }
}
```

---

## Q5: How do you TDD a Repository?

### Feature: UserRepository with caching

**RED 1: Fetch from network on first call**
```kotlin
class UserRepositoryTest {

    private val api: UserApi = mock()
    private val cache: UserCache = mock()
    private val repository = UserRepository(api, cache)

    @Test
    fun `first call fetches from network`() = runTest {
        whenever(api.fetchUser("123")).thenReturn(User("Alice"))
        whenever(cache.get("123")).thenReturn(null)

        val result = repository.getUser("123")

        assertEquals("Alice", result.name)
        verify(api).fetchUser("123")
    }
}
```

**GREEN 1**
```kotlin
class UserRepository(
    private val api: UserApi,
    private val cache: UserCache
) {
    suspend fun getUser(id: String): User {
        return api.fetchUser(id)
    }
}
```

**RED 2: Return from cache on second call**
```kotlin
@Test
fun `second call returns from cache`() = runTest {
    val user = User("Alice")
    whenever(cache.get("123")).thenReturn(user)

    val result = repository.getUser("123")

    assertEquals("Alice", result.name)
    verify(api, never()).fetchUser(any())
}
```

**GREEN 2**
```kotlin
suspend fun getUser(id: String): User {
    cache.get(id)?.let { return it }
    return api.fetchUser(id)
}
```

**RED 3: Save to cache after network call**
```kotlin
@Test
fun `network result is saved to cache`() = runTest {
    whenever(api.fetchUser("123")).thenReturn(User("Alice"))
    whenever(cache.get("123")).thenReturn(null)

    repository.getUser("123")

    verify(cache).put("123", User("Alice"))
}
```

**GREEN 3**
```kotlin
suspend fun getUser(id: String): User {
    cache.get(id)?.let { return it }
    val user = api.fetchUser(id)
    cache.put(id, user)
    return user
}
```

---

## Q6: What are test doubles?

| Type | Description | Example |
|------|-------------|---------|
| **Dummy** | Passed but never used | Null parameter |
| **Stub** | Returns canned data | `when(api.get()).thenReturn(user)` |
| **Mock** | Verifies interactions | `verify(api).get("123")` |
| **Spy** | Wraps real object | `spy(realObject)` |
| **Fake** | Working implementation | In-memory database |

### When to use each
```kotlin
// DUMMY — just to satisfy constructor
val dummyLogger: Logger = mock()

// STUB — return predefined data
val stubApi: UserApi = mock {
    on { fetchUser("123") } doReturn User("Alice")
}

// MOCK — verify method was called
verify(repository).saveUser(any())

// SPY — wrap real object, verify real behavior
val spyList = spy(mutableListOf("a", "b"))

// FAKE — real but simplified implementation
class FakeUserCache : UserCache {
    private val map = mutableMapOf<String, User>()
    override fun get(id: String) = map[id]
    override fun put(id: String, user: User) { map[id] = user }
}
```

---

## Q7: How do you TDD a Compose screen?

### Feature: Counter screen

**RED 1: Shows initial count**
```kotlin
class CounterScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `shows initial count of zero`() {
        composeRule.setContent { CounterScreen() }
        composeRule.onNodeWithText("0").assertIsDisplayed()
    }
}
```

**GREEN 1**
```kotlin
@Composable
fun CounterScreen() {
    Text("0")
}
```

**RED 2: Increment button exists**
```kotlin
@Test
fun `has increment button`() {
    composeRule.setContent { CounterScreen() }
    composeRule.onNodeWithText("+").assertIsDisplayed()
}
```

**GREEN 2**
```kotlin
@Composable
fun CounterScreen() {
    Column {
        Text("0")
        Button(onClick = {}) { Text("+") }
    }
}
```

**RED 3: Clicking + increments count**
```kotlin
@Test
fun `clicking plus increments count`() {
    composeRule.setContent { CounterScreen() }
    composeRule.onNodeWithText("+").performClick()
    composeRule.onNodeWithText("1").assertIsDisplayed()
}
```

**GREEN 3**
```kotlin
@Composable
fun CounterScreen() {
    var count by remember { mutableIntStateOf(0) }
    Column {
        Text(count.toString())
        Button(onClick = { count++ }) { Text("+") }
    }
}
```

**RED 4: Decrement button**
```kotlin
@Test
fun `clicking minus decrements count`() {
    composeRule.setContent { CounterScreen() }
    composeRule.onNodeWithText("+").performClick()  // 1
    composeRule.onNodeWithText("-").performClick()  // 0
    composeRule.onNodeWithText("0").assertIsDisplayed()
}
```

**GREEN 4 — Final**
```kotlin
@Composable
fun CounterScreen() {
    var count by remember { mutableIntStateOf(0) }
    Column {
        Text(count.toString())
        Row {
            Button(onClick = { count-- }) { Text("-") }
            Button(onClick = { count++ }) { Text("+") }
        }
    }
}
```

---

## Q8: What is the difference between TDD and test-after?

| Aspect | TDD (Test-First) | Test-After |
|--------|-----------------|------------|
| When tests written | Before code | After code |
| Design impact | High — shapes design | Low |
| Coverage | High | Variable |
| Motivation | Design + correctness | Correctness only |
| Refactoring confidence | High | Medium |
| Test quality | Behavior-focused | Implementation-focused |

### TDD forces better design
- Hard to test → bad design → refactor
- Dependencies become explicit
- Classes stay small and focused
- Side effects are minimized

---

## Q9: How do you TDD edge cases?

### Feature: String validator

**Start with happy path**
```kotlin
@Test
fun `valid email returns true`() {
    assertTrue(EmailValidator.isValid("user@test.com"))
}
```

**Then edge cases**
```kotlin
@Test
fun `empty string returns false`() {
    assertFalse(EmailValidator.isValid(""))
}

@Test
fun `null returns false`() {
    assertFalse(EmailValidator.isValid(null))
}

@Test
fun `no at symbol returns false`() {
    assertFalse(EmailValidator.isValid("usertest.com"))
}

@Test
fun `multiple at symbols returns false`() {
    assertFalse(EmailValidator.isValid("user@@test.com"))
}

@Test
fun `no domain returns false`() {
    assertFalse(EmailValidator.isValid("user@"))
}

@Test
fun `no local part returns false`() {
    assertFalse(EmailValidator.isValid("@test.com"))
}
```

### Edge case categories
| Category | Example |
|----------|---------|
| Empty/null | `""`, `null` |
| Boundary | `0`, `-1`, `Int.MAX_VALUE` |
| Invalid format | `"abc"` for number |
| Missing parts | `"user@"` |
| Extra parts | `"user@@test.com"` |
| Unicode | `"用户@测试.com"` |

---

## Q10: How do you TDD error handling?

### Feature: Network request with error handling

**RED 1: Success case**
```kotlin
@Test
fun `fetch user success returns user`() = runTest {
    whenever(api.fetchUser("123")).thenReturn(User("Alice"))
    val result = repository.fetchUser("123")
    assertTrue(result.isSuccess)
    assertEquals("Alice", result.getOrNull()?.name)
}
```

**RED 2: Network error**
```kotlin
@Test
fun `network error returns failure`() = runTest {
    whenever(api.fetchUser("123"))
        .thenThrow(IOException("No network"))
    val result = repository.fetchUser("123")
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is IOException)
}
```

**RED 3: Not found error**
```kotlin
@Test
fun `404 returns user not found error`() = runTest {
    whenever(api.fetchUser("999"))
        .thenThrow(HttpException(404, "Not Found"))
    val result = repository.fetchUser("999")
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is UserNotFoundError)
}
```

**GREEN — Implement**
```kotlin
suspend fun fetchUser(id: String): Result<User> {
    return try {
        Result.success(api.fetchUser(id))
    } catch (e: HttpException) {
        if (e.code == 404) Result.failure(UserNotFoundError(id))
        else Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

---

## Q11: How do you TDD with parameterized tests?

### Feature: Password validator

**Write parameterized test**
```kotlin
@RunWith(Parameterized::class)
class PasswordValidatorTest(
    private val password: String,
    private val expected: Boolean
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf("Abc123!@", true),      // Valid
            arrayOf("abc123!@", false),     // No uppercase
            arrayOf("ABC123!@", false),     // No lowercase
            arrayOf("Abcdefg!", false),     // No number
            arrayOf("Abc12345", false),     // No special char
            arrayOf("A1!a", false),          // Too short
            arrayOf("", false),             // Empty
            arrayOf("Abcdefgh123!@#", true) // Long valid
        )
    }

    @Test
    fun `validate password`() {
        assertEquals(expected, PasswordValidator.isValid(password))
    }
}
```

**GREEN — Implement**
```kotlin
object PasswordValidator {
    fun isValid(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { !it.isLetterOrDigit() }) return false
        return true
    }
}
```

---

## Q12: How do you TDD a Room DAO?

**RED 1: Insert and read**
```kotlin
@Test
fun `insert user and read by id`() = runTest {
    val user = User("1", "Alice", "alice@test.com")
    dao.insert(user)
    val loaded = dao.getUser("1")
    assertEquals("Alice", loaded?.name)
}
```

**RED 2: Update user**
```kotlin
@Test
fun `update user changes name`() = runTest {
    dao.insert(User("1", "Alice", "alice@test.com"))
    dao.update(User("1", "Alice2", "alice@test.com"))
    assertEquals("Alice2", dao.getUser("1")?.name)
}
```

**RED 3: Delete user**
```kotlin
@Test
fun `delete user removes from db`() = runTest {
    dao.insert(User("1", "Alice", "alice@test.com"))
    dao.delete("1")
    assertNull(dao.getUser("1"))
}
```

**RED 4: Get all users sorted by name**
```kotlin
@Test
fun `get all users sorted by name`() = runTest {
    dao.insert(User("2", "Bob", "bob@test.com"))
    dao.insert(User("1", "Alice", "alice@test.com"))
    dao.insert(User("3", "Charlie", "charlie@test.com"))

    val users = dao.getAllUsers().first()
    assertEquals(listOf("Alice", "Bob", "Charlie"), users.map { it.name })
}
```

**GREEN — Implement DAO**
```kotlin
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUser(id: String): User?

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>
}
```

---

## Q13: How do you TDD a Retrofit API?

**RED 1: GET request**
```kotlin
@Test
fun `fetch user returns parsed user`() = runTest {
    mockWebServer.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setBody("""{"id":"123","name":"Alice"}""")
    )

    val user = api.fetchUser("123")

    assertEquals("Alice", user.name)
    val request = mockWebServer.takeRequest()
    assertEquals("/users/123", request.path)
    assertEquals("GET", request.method)
}
```

**RED 2: POST request**
```kotlin
@Test
fun `create user sends POST with body`() = runTest {
    mockWebServer.enqueue(
        MockResponse()
            .setResponseCode(201)
            .setBody("""{"id":"1","name":"Alice"}""")
    )

    val user = api.createUser(CreateUserRequest("Alice", "alice@test.com"))

    assertEquals("Alice", user.name)
    val request = mockWebServer.takeRequest()
    assertEquals("POST", request.method)
    assertTrue(request.body.readUtf8().contains("Alice"))
}
```

**RED 3: Error response**
```kotlin
@Test
fun `404 throws HttpException`() = runTest {
    mockWebServer.enqueue(
        MockResponse().setResponseCode(404)
    )

    assertThrows<HttpException> {
        api.fetchUser("999")
    }
}
```

**GREEN — Define interface**
```kotlin
interface UserApi {
    @GET("users/{id}")
    suspend fun fetchUser(@Path("id") id: String): User

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): User
}
```

---

## Q14: What are TDD anti-patterns?

### 1. Writing all tests first
```kotlin
// ❌ Bad — writing 10 tests then implementing
@Test fun `test 1`() {}
@Test fun `test 2`() {}
@Test fun `test 3`() {}
// ... then implement all at once

// ✅ Good — one test at a time
@Test fun `test 1`() {}
// Implement
// Then write test 2
```

### 2. Testing implementation, not behavior
```kotlin
// ❌ Bad — testing private methods
@Test
fun `validate uses regex`() {
    val validator = Validator()
    // Testing internal implementation
}

// ✅ Good — testing public behavior
@Test
fun `valid email returns true`() {
    assertTrue(Validator.isValid("user@test.com"))
}
```

### 3. Writing too much code in green phase
```kotlin
// ❌ Bad — implementing features not tested yet
fun login(email: String, password: String) {
    // Don't add validation, logging, analytics
    // that aren't tested yet
}

// ✅ Good — minimal code to pass test
fun login(email: String, password: String) {
    // Just enough to make test pass
}
```

### 4. Skipping refactor step
```kotlin
// ❌ Bad — moving to next test without refactoring
// Code gets messy, duplicated

// ✅ Good — refactor while green
// Extract methods, remove duplication, improve naming
```

### Anti-patterns summary
| Anti-pattern | Problem |
|-------------|---------|
| Big batch tests | Not really TDD |
| Testing privates | Brittle tests |
| Over-engineering in green | YAGNI violation |
| Skipping refactor | Code rot |
| Testing framework features | Not testing your code |
| 0% → 100% in one step | Too much at once |

---

## Q15: When should you NOT use TDD?

### When TDD is great
- ✅ Business logic / domain rules
- ✅ Algorithms
- ✅ Parsers / validators
- ✅ State machines
- ✅ API clients
- ✅ ViewModels

### When TDD is hard/not worth it
| Scenario | Why | Alternative |
|----------|-----|-------------|
| UI layout | Visual, not logical | Test after |
| One-off scripts | Throwaway code | No tests |
| Prototypes | Changing rapidly | Test after stable |
| Third-party integration | External dependency | Integration tests |
| Performance tuning | Not behavior | Benchmark tests |
| Exploratory coding | Don't know solution yet | Test after |

### Pragmatic TDD
```
Pure TDD:     Every line of code is test-first
Pragmatic TDD: TDD for logic, test-after for UI
Common:        TDD for new features, test-after for bug fixes
```

### Bug fix TDD
```kotlin
// 1. Write test that reproduces the bug (RED)
@Test
fun `empty list does not crash`() {
    assertDoesNotThrow {
        calculator.average(emptyList())
    }
}

// 2. Fix the bug (GREEN)
fun average(numbers: List<Int>): Double {
    if (numbers.isEmpty()) return 0.0  // Fix
    return numbers.average()
}

// 3. Refactor if needed
```

---

## 🔗 Related Topics
- [Unit Testing](UnitTesting.md)
- [Mockito](Mockito.md)
- [Testing Scenarios](TestingScenarios.md)
