# KMP Expect/Actual — Interview Questions

## 🔴 Q1: What is the `expect`/`actual` mechanism?
**Answer:** `expect`/`actual` is KMP's way to declare platform-specific implementations:
- `expect` = a contract/declaration in `commonMain` (like an interface)
- `actual` = the implementation in each platform source set

```kotlin
// commonMain
expect class Random() {
    fun nextInt(): Int
    fun nextBoolean(): Boolean
}

// androidMain
actual class Random {
    actual fun nextInt(): Int = java.util.Random().nextInt()
    actual fun nextBoolean(): Boolean = java.util.Random().nextBoolean()
}

// iosMain
actual class Random {
    actual fun nextInt(): Int = kotlin.random.Random.nextInt()
    actual fun nextBoolean(): Boolean = kotlin.random.Random.nextBoolean()
}
```

---

## 🔴 Q2: What can be declared as `expect`?
**Answer:** Functions, properties, classes, interfaces, objects, annotations, and type aliases:

```kotlin
// commonMain
expect fun platformName(): String                    // Function
expect val osVersion: String                         // Property
expect class DateTimeFormatter(pattern: String) {    // Class
    fun format(timestamp: Long): String
}
expect interface Parcelable                          // Interface
expect object SystemInfo {                           // Object
    fun getDeviceModel(): String
}
expect annotation class Parcelize                    // Annotation
```

---

## 🔴 Q3: What are the rules for `expect`/`actual`?
**Answer:**

| Rule | Description |
|------|-------------|
| Same signature | `actual` must match `expect` signature exactly |
| Same name | `actual` must have the same name as `expect` |
| One-to-one | Each `expect` needs exactly one `actual` per target |
| No body in expect | `expect fun` has no body (just declaration) |
| Same visibility | `actual` visibility must match or be more permissive |
| Same supertypes | `actual class` must implement same interfaces as `expect class` |

---

## 🟡 Q4: Can `expect`/`actual` have different implementations per platform?
**Answer:** Yes! Each platform provides its own `actual`:

```kotlin
// commonMain
expect class DatabaseDriver {
    fun createDatabase(): Database
}

// androidMain
actual class DatabaseDriver {
    actual fun createDatabase(): Database {
        return Room.databaseBuilder(context, ...)
            .build()
    }
}

// iosMain
actual class DatabaseDriver {
    actual fun createDatabase(): Database {
        return SqlDriver("mydb.sqlite")
    }
}
```

---

## 🔴 Q5: What is the difference between `expect`/`actual` and interfaces?
**Answer:**

| Aspect | expect/actual | Interface + DI |
|--------|-------------|----------------|
| Declaration | Compile-time contract | Runtime polymorphism |
| Resolution | At compile time | At runtime |
| Boilerplate | Less (no DI needed) | More (need DI setup) |
| Testability | Harder to mock | Easier to mock |
| Flexibility | Fixed per platform | Can swap at runtime |

**Use `expect`/`actual` for:** Simple platform utilities (logging, device info)
**Use interfaces for:** Complex logic that needs testing/mocking

---

## 🟡 Q6: How do you handle `expect`/`actual` with different constructors?
**Answer:** `actual` class can have additional constructors, but must provide at least the `expect` constructor:

```kotlin
// commonMain
expect class HttpClient(config: HttpConfig)

// androidMain
actual class HttpClient actual constructor(config: HttpConfig) {
    // Can have additional constructors
    constructor() : this(HttpConfig.default())
}
```

---

## 🟡 Q7: Can `expect`/`actual` functions have default arguments?
**Answer:** Default arguments go in the `expect` declaration, not `actual`:

```kotlin
// commonMain
expect fun log(message: String, level: String = "INFO")

// androidMain — no default arg here
actual fun log(message: String, level: String) {
    Log.d(level, message)
}
```

---

## 🟡 Q8: How do you use `expect`/`actual` with generics?
**Answer:**

```kotlin
// commonMain
expect class MultiplatformList<T> {
    fun add(item: T)
    fun get(index: Int): T
    val size: Int
}

// androidMain
actual class MultiplatformList<T> {
    private val list = mutableListOf<T>()
    actual fun add(item: T) = list.add(item)
    actual fun get(index: Int): T = list[index]
    actual val size: Int get() = list.size
}
```

---

## 🟡 Q9: What happens if an `actual` is missing?
**Answer:** Compilation error. Every `expect` declaration must have a matching `actual` in every target source set. If you target Android + iOS, you need `actual` in both `androidMain` and `iosMain`.

---

## 🔴 Q10: How do you use `expect`/`actual` for annotations?
**Answer:** Common pattern for `@Parcelize` on Android:

```kotlin
// commonMain
expect annotation class Parcelize()

// androidMain
actual typealias Parcelize = android.os.Parcelable.Parcelize

// iosMain — no-op
actual annotation class Parcelize
```

Then in common code:
```kotlin
@Parcelize
data class User(val id: String, val name: String)
```

---

## 🟡 Q11: What is `typealias` in `actual`?
**Answer:** `actual typealias` maps an `expect` type to an existing platform type:

```kotlin
// commonMain
expect class UUID

// androidMain
actual typealias UUID = java.util.UUID

// iosMain
actual typealias UUID = platform.Foundation.NSUUID
```

This avoids wrapping — the common `UUID` IS the platform `UUID`.

---

## 🟡 Q12: Can you have `expect`/`actual` in intermediate source sets?
**Answer:** Yes! You can put `actual` in intermediate source sets like `iosMain` or `nativeMain`:

```kotlin
// commonMain
expect fun currentTimeMillis(): Long

// nativeMain (shared by all native targets)
actual fun currentTimeMillis(): Long = clock_gettime_ms()

// androidMain (separate implementation)
actual fun currentTimeMillis(): Long = System.currentTimeMillis()
```

---

## 🟡 Q13: How do you test `expect`/`actual` code?
**Answer:** Two approaches:

**1. Test the common interface:**
```kotlin
// commonTest
class CalculatorTest {
    @Test
    fun testAdd() {
        val calc = Calculator()
        assertEquals(5, calc.add(2, 3))
    }
}
```

**2. Mock the platform dependency:**
```kotlin
// commonTest
class RepositoryTest {
    @Test
    fun testGetUser() = runTest {
        val mockApi = mockk<UserApi>()
        coEvery { mockApi.fetchUser("1") } returns User("1", "John")
        val repo = UserRepository(mockApi)
        assertEquals("John", repo.getUser("1").name)
    }
}
```

---

## 🟢 Q14: What is the `@ReplacementFunction` annotation?
**Answer:** Used to provide a default implementation for `expect` functions that can be overridden:

```kotlin
// commonMain
@ReplacementFunction
expect fun getDeviceId(): String {
    return "unknown"
}
```

This allows platforms to optionally override with their implementation.

---

## 🟡 Q15: How do you handle `expect`/`actual` with companion objects?
**Answer:**

```kotlin
// commonMain
expect class DateUtils {
    companion object {
        fun now(): Long
        fun format(timestamp: Long): String
    }
}

// androidMain
actual class DateUtils {
    actual companion object {
        actual fun now(): Long = System.currentTimeMillis()
        actual fun format(timestamp: Long): String =
            SimpleDateFormat("dd/MM/yyyy").format(Date(timestamp))
    }
}
```

---

## 📌 Key Takeaways
- `expect` = contract in `commonMain`, `actual` = implementation per platform
- Can be used for functions, properties, classes, interfaces, objects, annotations
- `actual typealias` maps to existing platform types (no wrapper)
- Every `expect` needs an `actual` in every target
- Use interfaces + DI for testable code, `expect`/`actual` for utilities

---

[← Common Code](CommonCode.md) | [Back to README](../README.md) | [Next: Platform Specific →](PlatformSpecific.md)
