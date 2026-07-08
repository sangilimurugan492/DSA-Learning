# Null Safety & Edge Case Scenarios

## Scenario 1: Chained Null Checks Gone Wrong

### Problem
You have deeply nested data and need to access a value 3 levels deep. Using `?.` everywhere returns `null` but you don't know which level was null.

```kotlin
data class Company(val name: String, val ceo: CEO?)
data class CEO(val name: String, val assistant: Assistant?)
data class Assistant(val name: String, val email: String?)

fun getAssistantEmail(company: Company?): String {
    // ❌ Bad — can't tell which level was null
    return company?.ceo?.assistant?.email ?: "No email"
}
```

### Solution: Sealed Result or Step-by-Step Validation

```kotlin
sealed class EmailResult {
    data class Found(val email: String) : EmailResult()
    object NoCompany : EmailResult()
    object NoCEO : EmailResult()
    object NoAssistant : EmailResult()
    object NoEmail : EmailResult()
}

fun getAssistantEmail(company: Company?): EmailResult {
    // ✅ Solution: Step-by-step with clear error at each level
    if (company == null) return EmailResult.NoCompany
    val ceo = company.ceo ?: return EmailResult.NoCEO
    val assistant = ceo.assistant ?: return EmailResult.NoAssistant
    val email = assistant.email ?: return EmailResult.NoEmail
    return EmailResult.Found(email)
}

fun main() {
    val company = Company("Acme", CEO("Bob", Assistant("Alice", "alice@acme.com"))
    )
    when (val result = getAssistantEmail(company)) {
        is EmailResult.Found -> println("Email: ${result.email}")
        EmailResult.NoCompany -> println("No company provided")
        EmailResult.NoCEO -> println("Company has no CEO")
        EmailResult.NoAssistant -> println("CEO has no assistant")
        EmailResult.NoEmail -> println("Assistant has no email")
    }
}
```

### Key Takeaway
- `?.` chains hide which level failed
- Use sealed classes for granular error reporting
- Step-by-step validation gives clear feedback

---

## Scenario 2: Null in Collections

### Problem
You have a list that may contain nulls. You need to filter, transform, and process.

```kotlin
fun main() {
    val names: List<String?> = listOf("Alice", null, "Bob", null, "Charlie", null)

    // ❌ Bad — NPE risk
    // names.map { it.uppercase() }  // Crashes on null

    // ✅ Solution 1: filterNotNull
    val upper1 = names.filterNotNull().map { it.uppercase() }
    println(upper1)  // [ALICE, BOB, CHARLIE]

    // ✅ Solution 2: mapNotNull (filter + transform in one)
    val upper2 = names.mapNotNull { it?.uppercase() }
    println(upper2)  // [ALICE, BOB, CHARLIE]

    // ✅ Solution 3: Keep nulls with default
    val withDefault = names.map { it?.uppercase() ?: "UNKNOWN" }
    println(withDefault)  // [ALICE, UNKNOWN, BOB, UNKNOWN, CHARLIE, UNKNOWN]

    // ✅ Solution 4: Partition nulls and non-nulls
    val (valid, nulls) = names.partition { it != null }
    println("Valid: ${valid.map { it!!.uppercase() }}")  // [ALICE, BOB, CHARLIE]
    println("Nulls: ${nulls.size}")  // 3
}
```

### Key Takeaway
- `filterNotNull()` removes nulls before processing
- `mapNotNull` combines filter + map in one pass
- `partition` splits into two lists based on predicate

---

## Scenario 3: Lateinit vs Lazy vs Nullable

### Problem
You need a property that's initialized after construction but before first use. Which approach?

```kotlin
class Service {
    // ✅ lateinit — for var, set later, non-null
    lateinit var database: Database

    // ✅ lazy — for val, computed once on first access
    val config: Config by lazy {
        loadConfig()  // Only called when first accessed
    }

    // ✅ nullable — when null is a valid state
    var currentUser: User? = null

    fun init(db: Database) {
        database = db  // Must set before use
    }

    fun doWork() {
        // Check lateinit
        if (::database.isInitialized) {
            database.query("SELECT * FROM users")
        }

        // Lazy is thread-safe by default
        println(config.timeout)  // Loads on first access

        // Nullable — check with let
        currentUser?.let { user ->
            println("Logged in as ${user.name}")
        }
    }
}

data class Database(val name: String) {
    fun query(sql: String) = println("Query: $sql")
}
data class Config(val timeout: Int)
data class User(val name: String)

fun loadConfig() = Config(30)

fun main() {
    val service = Service()
    service.init(Database("MyDB"))
    service.doWork()
    service.currentUser = User("Alice")
    service.doWork()
}
```

### Key Takeaway
| Approach   | Use When                              | Thread-safe |
|------------|---------------------------------------|-------------|
| `lateinit` | `var`, set before use, non-null      | No          |
| `lazy`     | `val`, expensive init, one-time      | Yes         |
| `nullable` | Null is a valid state                 | N/A         |

---

## Scenario 4: Smart Cast Not Working

### Problem
Smart cast doesn't work on a `var` property, causing compilation errors.

```kotlin
class Processor {
    var data: String? = null

    fun process() {
        // ❌ Won't compile — smart cast doesn't work on var
        // if (data != null) {
        //     println(data.length)  // Error: data may have changed
        // }

        // ✅ Solution 1: Local val
        val localData = data
        if (localData != null) {
            println(localData.length)  // Smart cast works on val
        }

        // ✅ Solution 2: Safe call
        println(data?.length)

        // ✅ Solution 3: let
        data?.let { d ->
            println(d.length)  // d is non-null
        }

        // ✅ Solution 4: requireNotNull (throws if null)
        try {
            val required = requireNotNull(data) { "Data must not be null" }
            println(required.length)
        } catch (e: IllegalArgumentException) {
            println("Error: ${e.message}")
        }
    }
}

fun main() {
    Processor().apply {
        data = "Hello"
        process()
    }
}
```

### Key Takeaway
- Smart cast doesn't work on `var` (could change between check and use)
- Copy to a local `val` for smart cast
- `?.let` gives a non-null parameter inside the lambda
- `requireNotNull` throws if null (for preconditions)

---

## Scenario 5: Null Safety with Java Interop

### Problem
Calling Java code that returns null, but Kotlin thinks it's non-null.

```kotlin
// Java class
// public class JavaUtils {
//     public static String getName() { return null; }  // Can return null!
// }

fun main() {
    // ❌ Bad — Kotlin thinks it's non-null, crashes at runtime
    // val name: String = JavaUtils.getName()
    // println(name.length)  // NPE!

    // ✅ Solution 1: Treat as nullable
    val name: String? = JavaUtils.getName()
    println(name?.length)  // null

    // ✅ Solution 2: Use @Nullable annotation in Java
    // Java: @Nullable public static String getName() { ... }

    // ✅ Solution 3: Defensive wrapper
    fun safeGetName(): String = JavaUtils.getName() ?: "default"
    println(safeGetName())  // "default"
}
```

### Key Takeaway
- Java doesn't have null safety — treat all Java return values as nullable
- Use `?` type for Java interop results
- Add `@Nullable`/`@NonNull` annotations in Java for better Kotlin interop
- Wrap Java calls in defensive Kotlin functions

---

## 🔗 Related Topics
- [Null Safety](../beginner/NullSafety.md)
- [Type Checks & Smart Casts](../beginner/TypeChecksAndSmartCasts.md)
