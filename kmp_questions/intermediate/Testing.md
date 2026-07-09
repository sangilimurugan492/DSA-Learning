# KMP Testing — Interview Questions

## 🔴 Q1: How do you set up testing in KMP?
**Answer:**

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("io.mockk:mockk:1.13.8")
            }
        }
    }
}
```

Tests in `commonTest` run on **all platforms**. Platform-specific tests go in `androidUnitTest`, `iosTest`, etc.

---

## 🔴 Q2: What test source sets are available?
**Answer:**

| Source Set | Runs On | Purpose |
|-----------|---------|---------|
| `commonTest` | All platforms | Shared tests |
| `androidUnitTest` | JVM (Android) | Android-specific tests |
| `androidInstrumentedTest` | Android device | Instrumented tests |
| `iosTest` | iOS simulator | iOS-specific tests |
| `jvmTest` | JVM | JVM-specific tests |

```kotlin
// commonTest — runs everywhere
class CalculatorTest {
    @Test
    fun testAdd() {
        assertEquals(5, Calculator().add(2, 3))
    }
}
```

---

## 🔴 Q3: How do you test coroutines in KMP?
**Answer:**

```kotlin
// commonTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserViewModelTest {
    @Test
    fun `should load user successfully`() = runTest {
        // Arrange
        val repo = FakeUserRepository()
        val viewModel = UserViewModel(repo)
        
        // Act
        viewModel.load("1")
        
        // Assert
        assertEquals("John", viewModel.state.value.data?.name)
    }
    
    @Test
    fun `should handle loading state`() = runTest {
        val repo = FakeUserRepository(delayMs = 1000)
        val viewModel = UserViewModel(repo)
        
        viewModel.load("1")
        
        assertEquals(UiState.Loading, viewModel.state.value)
        // Advance time
        advanceTimeBy(1000)
        assertEquals(UiState.Success::class, viewModel.state.value::class)
    }
}

class FakeUserRepository(
    private val delayMs: Long = 0
) : UserRepository {
    override suspend fun getUser(id: String): User {
        delay(delayMs)
        return User(id, "John", "john@test.com")
    }
}
```

---

## 🟡 Q4: How do you mock dependencies in KMP?
**Answer:** Use **MockK** (Android/JVM) or **fakes** (common):

```kotlin
// commonTest — use fakes (works on all platforms)
class FakeUserRepository : UserRepository {
    var userToReturn: User? = null
    var error: Throwable? = null
    
    override suspend fun getUser(id: String): User {
        error?.let { throw it }
        return userToReturn ?: throw RuntimeException("Not configured")
    }
}

// androidUnitTest — use MockK
class UserViewModelTest {
    @Test
    fun test() = runTest {
        val repo = mockk<UserRepository>()
        coEvery { repo.getUser("1") } returns User("1", "John", "john@test.com")
        
        val vm = UserViewModel(repo)
        vm.load("1")
        
        coVerify { repo.getUser("1") }
    }
}
```

> **Note:** MockK doesn't work in `commonTest` (iOS). Use fakes for shared tests.

---

## 🟡 Q5: How do you test Flows in KMP?
**Answer:**

```kotlin
// commonTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test

class UserRepositoryTest {
    @Test
    fun `should emit users`() = runTest {
        val repo = UserRepository(FakeApi(), FakeDb())
        
        repo.getUsers().test {
            assertEquals(emptyList<User>(), awaitItem())
            repo.refreshUsers()
            assertEquals(listOf(User("1", "John", "john@test.com")), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `should get first emission`() = runTest {
        val repo = UserRepository(FakeApi(), FakeDb())
        val users = repo.getUsers().first()
        assertEquals(emptyList(), users)
    }
}
```

Use **Turbine** (`app.cash.turbine`) for Flow testing.

---

## 🟡 Q6: How do you test networking in KMP?
**Answer:**

```kotlin
// commonTest
import io.ktor.client.engine.mock.*
import io.ktor.client.*
import io.ktor.http.*

class UserApiTest {
    private val mockEngine = MockEngine { request ->
        when (request.url.encodedPath) {
            "/users/1" -> respond(
                content = """{"id":"1","name":"John","email":"john@test.com"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
            "/users" -> respond(
                content = """[{"id":"1","name":"John","email":"john@test.com"}]""",
                status = HttpStatusCode.OK
            )
            else -> respond("Not found", HttpStatusCode.NotFound)
        }
    }
    
    private val client = HttpClient(mockEngine) {
        install(ContentNegotiation) { json() }
    }
    
    @Test
    fun `should fetch user`() = runTest {
        val api = UserApi(client)
        val user = api.getUser("1")
        assertEquals("John", user.name)
    }
}
```

---

## 🟡 Q7: How do you test database code in KMP?
**Answer:**

```kotlin
// commonTest
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcSqliteDriver

class UserRepositoryTest {
    private lateinit var driver: SqlDriver
    private lateinit var db: AppDatabase
    private lateinit var repo: UserRepository
    
    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver("jdbc:sqlite::memory:")
        AppDatabase.Schema.create(driver)
        db = AppDatabase(driver)
        repo = UserRepository(db)
    }
    
    @Test
    fun `should save and retrieve user`() {
        val user = User("1", "John", "john@test.com")
        repo.save(user)
        
        val retrieved = repo.getById("1")
        assertEquals("John", retrieved?.name)
    }
    
    @AfterTest
    fun teardown() { driver.close() }
}
```

---

## 🟡 Q8: How do you test ViewModels in KMP?
**Answer:**

```kotlin
// commonTest
class UserViewModelTest {
    @Test
    fun `should update state on success`() = runTest {
        val repo = FakeUserRepository().apply {
            userToReturn = User("1", "John", "john@test.com")
        }
        val viewModel = UserViewModel(repo)
        
        viewModel.load("1")
        
        assertTrue(viewModel.state.value is UiState.Success)
        assertEquals("John", (viewModel.state.value as UiState.Success).data.name)
    }
    
    @Test
    fun `should show error on failure`() = runTest {
        val repo = FakeUserRepository().apply {
            error = RuntimeException("Network error")
        }
        val viewModel = UserViewModel(repo)
        
        viewModel.load("1")
        
        assertTrue(viewModel.state.value is UiState.Error)
        assertEquals("Network error", (viewModel.state.value as UiState.Error).message)
    }
}
```

---

## 🟡 Q9: How do you run tests on iOS?
**Answer:**

```kotlin
// iosTest — runs on iOS simulator
class PlatformTest {
    @Test
    fun `should return iOS platform name`() {
        assertEquals("iOS", Platform().name)
    }
}
```

Run with: `./gradlew :shared:iosSimulatorArm64Test`

Or use `iosX64Test` for Intel simulators.

---

## 🟡 Q10: How do you set up test fixtures in KMP?
**Answer:**

```kotlin
// commonTest
object TestData {
    val user = User("1", "John Doe", "john@test.com")
    val users = listOf(
        User("1", "John", "john@test.com"),
        User("2", "Jane", "jane@test.com"),
        User("3", "Bob", "bob@test.com")
    )
    val emptyUser = User("", "", "")
}

// Usage
class UserViewModelTest {
    @Test
    fun `should display user`() = runTest {
        val repo = FakeUserRepository().apply { userToReturn = TestData.user }
        val vm = UserViewModel(repo)
        
        vm.load("1")
        
        assertEquals(TestData.user.name, (vm.state.value as UiState.Success).data.name)
    }
}
```

---

## 📌 Key Takeaways
- `commonTest` = shared tests running on all platforms
- Use **fakes** for `commonTest`, **MockK** for `androidUnitTest`
- `runTest` for coroutine testing
- **Turbine** for Flow testing
- `MockEngine` for Ktor HTTP testing
- In-memory SQLite (`JdbcSqliteDriver`) for database tests

---

[← Coroutines](Coroutines.md) | [Back to README](../README.md) | [Next: Advanced →](../advanced/ComposeMultiplatform.md)
