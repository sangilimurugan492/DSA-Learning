# Unit Testing (JUnit)

## Q1: What is unit testing and why is it important?

Unit testing verifies individual units of code (functions, classes) in isolation.

| Benefit | Description |
|---------|-------------|
| Catch bugs early | Find issues before they reach production |
| Safe refactoring | Change code with confidence |
| Documentation | Tests show how code should behave |
| Design feedback | Hard-to-test code = bad design |
| Fast feedback | Seconds vs manual testing minutes |

### Test pyramid
```
        /\
       /UI\          ← Few, slow, expensive (Espresso)
      /------\
     /  Integration \  ← Medium (Room, Retrofit)
    /----------------\
   /     Unit Tests    \ ← Many, fast, cheap (JUnit)
  /--------------------\
```

---

## Q2: How do you set up JUnit in Android?

```gradle
// build.gradle (app)
dependencies {
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
    testImplementation 'com.google.truth:truth:1.1.5'
    testImplementation 'androidx.arch.core:core-testing:2.2.0'
}
```

### testImplementation vs androidTestImplementation
| Configuration | Location | Runs on | Speed |
|--------------|----------|---------|-------|
| `testImplementation` | `src/test/` | JVM | Fast |
| `androidTestImplementation` | `src/androidTest/` | Device/Emulator | Slow |

---

## Q3: How do you write a basic JUnit test?

```kotlin
// Production code
class Calculator {
    fun add(a: Int, b: Int): Int = a + b
    fun divide(a: Int, b: Int): Int {
        require(b != 0) { "Division by zero" }
        return a / b
    }
}

// Test code
class CalculatorTest {

    private val calculator = Calculator()

    @Test
    fun `add two positive numbers returns correct sum`() {
        // Given
        val a = 5
        val b = 3

        // When
        val result = calculator.add(a, b)

        // Then
        assertEquals(8, result)
    }

    @Test
    fun `divide by zero throws exception`() {
        // When & Then
        assertThrows<IllegalArgumentException> {
            calculator.divide(10, 0)
        }
    }
}
```

### AAA pattern
- **Arrange** — set up test data
- **Act** — call the method under test
- **Assert** — verify the result

---

## Q4: What are JUnit annotations?

```kotlin
class ExampleTest {

    @Before
    fun setUp() { /* Runs before each test */ }

    @After
    fun tearDown() { /* Runs after each test */ }

    @BeforeClass
    fun setUpClass() { /* Runs once before all tests */ }

    @AfterClass
    fun tearDownClass() { /* Runs once after all tests */ }

    @Test
    fun `basic test`() { }

    @Ignore("Not implemented yet")
    @Test
    fun `skipped test`() { }

    @Test(expected = IllegalArgumentException::class)
    fun `test expecting exception`() {
        throw IllegalArgumentException()
    }

    @Test(timeout = 1000L)
    fun `test with timeout`() { /* Must finish in 1s */ }
}
```

### Annotation summary
| Annotation | Purpose |
|-----------|---------|
| `@Test` | Marks a test method |
| `@Before` | Runs before each test |
| `@After` | Runs after each test |
| `@BeforeClass` | Runs once before all tests (static) |
| `@Ignore` | Skips a test |
| `@Test(timeout=)` | Fails if test takes too long |
| `@Test(expected=)` | Expects an exception |

---

## Q5: How do you use assertions?

```kotlin
import org.junit.Assert.*
import com.google.common.truth.Truth.assertThat

@Test
fun `junit assertions`() {
    assertEquals(5, calculator.add(2, 3))
    assertNotEquals(6, calculator.add(2, 3))
    assertTrue(result > 0)
    assertFalse(result < 0)
    assertNull(nullValue)
    assertNotNull(notNullValue)
    assertArrayEquals(intArrayOf(1, 2, 3), result)
    assertSame(obj1, obj2)  // Same reference
    assertNotSame(obj1, obj3)
}

@Test
fun `truth assertions (more readable)`() {
    assertThat(result).isEqualTo(5)
    assertThat(list).containsExactly("a", "b", "c")
    assertThat(list).contains("a")
    assertThat(list).hasSize(3)
    assertThat(string).startsWith("Hello")
    assertThat(string).isNotEmpty()
    assertThat(map).containsKey("key")
    assertThat(exception).hasMessageThat().isEqualTo("Error!")
}
```

### JUnit vs Truth
| Feature | JUnit | Truth |
|---------|-------|-------|
| Readability | Basic | Fluent, readable |
| Collections | Verbose | `containsExactly` |
| Error messages | Basic | Descriptive |
| Dependencies | Built-in | Extra dependency |

---

## Q6: How do you write parameterized tests?

```kotlin
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CalculatorParameterizedTest(
    private val a: Int,
    private val b: Int,
    private val expected: Int
) {
    private val calculator = Calculator()

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Int>> = listOf(
            arrayOf(1, 2, 3),
            arrayOf(5, 5, 10),
            arrayOf(-1, 1, 0),
            arrayOf(0, 0, 0),
            arrayOf(100, 200, 300)
        )
    }

    @Test
    fun `add returns correct sum`() {
        assertEquals(expected, calculator.add(a, b))
    }
}
```

### JUnit5 parameterized (if using JUnit5)
```kotlin
@ParameterizedTest
@CsvSource("1, 2, 3", "5, 5, 10", "-1, 1, 0")
fun `add with multiple inputs`(a: Int, b: Int, expected: Int) {
    assertEquals(expected, calculator.add(a, b))
}
```

---

## Q7: How do you test coroutines?

```gradle
testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
```

```kotlin
class UserRepositoryTest {

    @Test
    fun `fetch user returns user`() = runTest {
        // Given
        val repository = UserRepository(api)

        // When
        val result = repository.fetchUser("123")

        // Then
        assertEquals("Alice", result.name)
    }

    @Test
    fun `fetch user with delay`() = runTest {
        // runTest skips delays automatically
        val result = repository.fetchUserWithDelay("123")
        assertEquals("Alice", result.name)
        // Test completes instantly even if fetchUserWithDelay has delay(1000)
    }

    @Test
    fun `test with Dispatchers`() = runTest {
        // Replace Main dispatcher
        Dispatchers.setMain(StandardTestDispatcher())

        val viewModel = MyViewModel()
        viewModel.loadData()

        // Advance virtual time
        advanceUntilIdle()

        assertEquals("Loaded", viewModel.state.value)
    }
}
```

### Key coroutine test APIs
| API | Purpose |
|-----|---------|
| `runTest` | Run suspend test, skip delays |
| `Dispatchers.setMain()` | Replace Main dispatcher |
| `advanceUntilIdle()` | Execute pending coroutines |
| `advanceTimeBy()` | Advance virtual time |
| `UnconfinedTestDispatcher` | Execute immediately |

---

## Q8: How do you test LiveData?

```gradle
testImplementation 'androidx.arch.core:core-testing:2.2.0'
```

```kotlin
@get:Rule
val instantTaskExecutorRule = InstantTaskExecutorRule()

class UserViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val viewModel = UserViewModel(fakeRepository)

    @Test
    fun `loading state is emitted`() {
        // Given
        val states = mutableListOf<UiState>()
        viewModel.uiState.observeForever { states.add(it) }

        // When
        viewModel.loadUser()

        // Then
        assertEquals(listOf(UiState.Loading, UiState.Success(user)), states)
    }

    @After
    fun tearDown() {
        viewModel.uiState.removeObserver { }
    }
}
```

### InstantTaskExecutorRule
- Executes LiveData tasks synchronously
- Required for testing LiveData in unit tests
- Without it: `IllegalStateException: Cannot invoke observeForever on a background thread`

---

## Q9: How do you test StateFlow?

```kotlin
class UserViewModelTest {

    @Test
    fun `state flow emits loading then success`() = runTest {
        // Given
        val viewModel = UserViewModel(fakeRepository)

        // When
        viewModel.loadUser()

        // Then
        assertEquals(UiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        assertEquals(UiState.Success(user), viewModel.uiState.value)
    }

    @Test
    fun `test flow emissions`() = runTest {
        val flow = flowOf(1, 2, 3)

        val results = flow.toList()

        assertEquals(listOf(1, 2, 3), results)
    }

    @Test
    fun `test flow with Turbine`() = runTest {
        val flow = flow {
            emit(1)
            delay(100)
            emit(2)
        }

        flow.test {
            assertEquals(1, awaitItem())
            assertEquals(2, awaitItem())
            awaitComplete()
        }
    }
}
```

### Turbine library
```gradle
testImplementation 'app.cash.turbine:turbine:1.0.0'
```

---

## Q10: How do you test exceptions?

```kotlin
class UserServiceTest {

    @Test
    fun `login with invalid credentials throws AuthException`() {
        assertThrows<AuthException> {
            userService.login("wrong", "wrong")
        }
    }

    @Test
    fun `login exception has correct message`() {
        val exception = assertThrows<AuthException> {
            userService.login("wrong", "wrong")
        }
        assertEquals("Invalid credentials", exception.message)
    }

    @Test
    fun `login with empty email throws exception`() {
        val exception = assertThrows<IllegalArgumentException> {
            userService.login("", "password")
        }
        assertTrue(exception.message!!.contains("email"))
    }

    // JUnit 4 style
    @Test(expected = AuthException::class)
    fun `login fails with wrong password`() {
        userService.login("user@test.com", "wrong")
    }
}
```

---

## Q11: How do you use test rules?

```kotlin
class MyTest {

    // 1. InstantTaskExecutorRule — for LiveData
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // 2. MainDispatcherRule — for coroutines
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 3. Custom rule
    class MainDispatcherRule(
        private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }
        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}
```

### Common test rules
| Rule | Purpose |
|------|---------|
| `InstantTaskExecutorRule` | Sync LiveData |
| `MainDispatcherRule` | Replace Main dispatcher |
| `RuleChain` | Order multiple rules |
| `Timeout` | Global timeout for all tests |

---

## Q12: How do you measure test coverage?

```gradle
// build.gradle (project)
plugins {
    id 'org.jacoco' version '0.8.11'
}

// build.gradle (app)
android {
    buildTypes {
        debug {
            testCoverageEnabled true
        }
    }
}
```

### Run coverage
```bash
./gradlew createDebugCoverageReport
# Report: app/build/reports/coverage/debug/index.html
```

### Coverage types
| Type | What it measures |
|------|-----------------|
| Line coverage | Lines executed |
| Branch coverage | Branches taken |
| Method coverage | Methods called |
| Class coverage | Classes instantiated |

### Good coverage targets
| Layer | Target |
|-------|--------|
| ViewModels | 80-90% |
| Repositories | 70-80% |
| Utilities | 90-100% |
| UI/Activities | 30-50% (use Espresso) |

---

## Q13: How do you name tests?

### Naming conventions
```kotlin
// 1. Backtick naming (Kotlin — most readable)
@Test
fun `add two positive numbers returns correct sum`() { }

// 2. should_ExpectedBehavior_When_State
@Test
fun `should return 8 when adding 5 and 3`() { }

// 3. MethodName_State_Expected
@Test
fun `add_5and3_returns8`() { }

// 4. Given_When_Then
@Test
fun `given_two_numbers_when_added_then_sum_returned`() { }
```

### Best practices
- ✅ Describe behavior, not implementation
- ✅ Use backtick names in Kotlin
- ✅ Include scenario and expected result
- ❌ Don't use `test1`, `test2`
- ❌ Don't test method names

---

## Q14: How do you test sealed classes?

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: String) : UiState()
    data class Error(val message: String) : UiState()
}

class UiStateTest {

    @Test
    fun `loading state is object`() {
        val state = UiState.Loading
        assertTrue(state is UiState.Loading)
    }

    @Test
    fun `success state contains data`() {
        val state = UiState.Success("Hello")
        assertEquals("Hello", state.data)
    }

    @Test
    fun `error state contains message`() {
        val state = UiState.Error("Network error")
        assertEquals("Network error", state.message)
    }

    @Test
    fun `when loading then show loading UI`() {
        val state: UiState = UiState.Loading
        val uiText = when (state) {
            is UiState.Loading -> "Loading..."
            is UiState.Success -> state.data
            is UiState.Error -> state.message
        }
        assertEquals("Loading...", uiText)
    }
}
```

---

## Q15: What makes a good unit test?

### FIRST principles
| Principle | Description |
|-----------|-------------|
| **F**ast | Runs in milliseconds |
| **I**solated | No dependency on other tests |
| **R**epeatable | Same result every time |
| **S**elf-validating | Pass/fail automatically |
| **T**imely | Written before/with production code |

### Good test checklist
- [ ] Tests one behavior per test
- [ ] Uses AAA pattern (Arrange-Act-Assert)
- [ ] Has descriptive name
- [ ] No conditional logic (no if/else)
- [ ] No random data (or use fixed seed)
- [ ] No I/O (file, network, database)
- [ ] No shared state between tests
- [ ] Runs in any order
- [ ] Fails for the right reason

### Code smells in tests
| Smell | Problem |
|-------|---------|
| Multiple asserts | Testing too many things |
| Sleep/wait | Flaky test |
| Random data | Non-deterministic |
| Test order dependency | Hidden coupling |
| Large setup | Class doing too much |

---

## 🔗 Related Topics
- [Mockito](Mockito.md)
- [Espresso](Espresso.md)
- [TDD](TDD.md)
