# OOP & Design Scenarios

## Scenario 1: Sealed Class for State Management

### Problem
You need to represent different UI states (loading, success, error) in a type-safe way.

```kotlin
// ✅ Solution: Sealed class hierarchy
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val code: Int? = null) : UiState<Nothing>()
}

class UserViewModel {
    private var _state: UiState<List<String>> = UiState.Loading
    val state get() = _state

    fun loadUsers() {
        _state = UiState.Loading
        try {
            val users = listOf("Alice", "Bob", "Charlie")
            _state = UiState.Success(users)
        } catch (e: Exception) {
            _state = UiState.Error(e.message ?: "Unknown error")
        }
    }
}

fun renderState(state: UiState<List<String>>) {
    // ✅ Compiler enforces exhaustive when — all branches covered
    when (state) {
        is UiState.Loading -> println("⏳ Loading...")
        is UiState.Success -> println("✅ Users: ${state.data}")
        is UiState.Error -> println("❌ Error: ${state.message}")
    }
}

fun main() {
    val vm = UserViewModel()

    vm.loadUsers()
    renderState(vm.state)  // ✅ Users: [Alice, Bob, Charlie]

    // Simulate error
    vm._state = UiState.Error("Network timeout", 503)
    renderState(vm.state)  // ❌ Error: Network timeout
}
```

### Key Takeaway
- Sealed classes restrict subtypes to the same file — exhaustive `when`
- Compiler warns if a branch is missing
- Generic `UiState<T>` works for any data type
- `object` for singletons (Loading), `data class` for data-carrying states

---

## Scenario 2: Delegation Over Inheritance

### Problem
You need a class that behaves like a List but adds custom behavior. Inheritance is limited (can't extend `List`).

```kotlin
// ✅ Solution: Delegation with `by`
class ObservableList<T>(
    private val inner: MutableList<T> = mutableListOf()
) : MutableList<T> by inner {

    var onAdd: ((T) -> Unit)? = null
    var onRemove: ((T) -> Unit)? = null

    override fun add(element: T): Boolean {
        onAdd?.invoke(element)
        return inner.add(element)
    }

    override fun remove(element: T): Boolean {
        onRemove?.invoke(element)
        return inner.remove(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach { onAdd?.invoke(it) }
        return inner.addAll(elements)
    }
}

fun main() {
    val list = ObservableList<String>()

    list.onAdd = { item -> println("➕ Added: $item") }
    list.onRemove = { item -> println("➖ Removed: $item") }

    list.add("Apple")     // ➕ Added: Apple
    list.add("Banana")    // ➕ Added: Banana
    list.addAll(listOf("Cherry", "Date"))  // ➕ Added: Cherry, ➕ Added: Date

    println("List: $list")  // [Apple, Banana, Cherry, Date]

    list.remove("Banana")  // ➖ Removed: Banana
    println("List: $list")  // [Apple, Cherry, Date]
}
```

### Key Takeaway
- `by` delegates interface implementation to another object
- Override only the methods you need to customize
- Prefer delegation over inheritance (composition over inheritance)
- All other `MutableList` methods work automatically via `inner`

---

## Scenario 3: Builder Pattern with DSL

### Problem
Build a complex object (e.g., HTML document) with a clean, readable syntax.

```kotlin
// ✅ Solution: DSL with lambda receivers
class HtmlBuilder {
    private val elements = mutableListOf<String>()

    fun body(block: BodyBuilder.() -> Unit) {
        val body = BodyBuilder().apply(block)
        elements.add("<body>${body.build()}</body>")
    }

    fun head(block: HeadBuilder.() -> Unit) {
        val head = HeadBuilder().apply(block)
        elements.add("<head>${head.build()}</head>")
    }

    fun build(): String = "<html>${elements.joinToString("")}</html>"
}

class BodyBuilder {
    private val children = mutableListOf<String>()

    fun p(text: String) { children.add("<p>$text</p>") }
    fun h1(text: String) { children.add("<h1>$text</h1>") }
    fun ul(block: UlBuilder.() -> Unit) {
        children.add("<ul>${UlBuilder().apply(block).build()}</ul>")
    }

    fun build() = children.joinToString("")
}

class HeadBuilder {
    private val children = mutableListOf<String>()
    fun title(text: String) { children.add("<title>$text</title>") }
    fun build() = children.joinToString("")
}

class UlBuilder {
    private val items = mutableListOf<String>()
    fun li(text: String) { items.add("<li>$text</li>") }
    fun build() = items.joinToString("")
}

fun html(block: HtmlBuilder.() -> Unit): String =
    HtmlBuilder().apply(block).build()

fun main() {
    // ✅ Clean DSL usage
    val document = html {
        head {
            title("My Page")
        }
        body {
            h1("Welcome")
            p("This is a paragraph.")
            ul {
                li("Item 1")
                li("Item 2")
                li("Item 3")
            }
        }
    }

    println(document)
    // <html><head><title>My Page</title></head><body><h1>Welcome</h1><p>This is a paragraph.</p><ul><li>Item 1</li><li>Item 2</li><li>Item 3</li></ul></body></html>
}
```

### Key Takeaway
- Lambda with receiver (`T.() -> Unit`) enables DSL syntax
- Each builder manages its own children
- `apply(block)` runs the lambda in the builder's context
- DSLs make complex object construction readable and type-safe

---

## Scenario 4: Strategy Pattern with Function Types

### Problem
You need different discount strategies without creating a class hierarchy.

```kotlin
data class Order(val items: List<Double>, val customerType: String)

// ✅ Solution: Strategy as function type
class DiscountCalculator {
    private val strategies = mutableMapOf<String, (Order) -> Double>()

    fun registerStrategy(type: String, strategy: (Order) -> Double) {
        strategies[type] = strategy
    }

    fun calculate(order: Order): Double {
        val strategy = strategies[order.customerType]
            ?: { o: Order -> o.items.sum() }  // Default: no discount
        return strategy(order)
    }
}

fun main() {
    val calculator = DiscountCalculator()

    // Register strategies as lambdas
    calculator.registerStrategy("regular") { order ->
        order.items.sum()  // No discount
    }

    calculator.registerStrategy("premium") { order ->
        order.items.sum() * 0.9  // 10% off
    }

    calculator.registerStrategy("vip") { order ->
        val total = order.items.sum()
        if (total > 100) total * 0.8 else total * 0.85  // 15-20% off
    }

    val orders = listOf(
        Order(listOf(50.0, 30.0), "regular"),
        Order(listOf(50.0, 30.0), "premium"),
        Order(listOf(50.0, 60.0), "vip"),
        Order(listOf(20.0), "vip"),
        Order(listOf(100.0), "unknown")
    )

    orders.forEach { order ->
        val total = calculator.calculate(order)
        println("${order.customerType}: items=${order.items.sum()} → total=$total")
    }
    // regular: items=80.0 → total=80.0
    // premium: items=80.0 → total=72.0
    // vip: items=110.0 → total=88.0
    // vip: items=20.0 → total=17.0
    // unknown: items=100.0 → total=100.0
}
```

### Key Takeaway
- Function types `(T) -> R` replace strategy interfaces
- Register strategies in a map for dynamic dispatch
- No boilerplate classes — just lambdas
- Default strategy as fallback

---

## Scenario 5: Singleton with Parameter

### Problem
You need a singleton that takes a constructor parameter. `object` doesn't support parameters.

```kotlin
// ✅ Solution 1: Companion object with lazy init
class Database private constructor(private val name: String) {
    companion object {
        @Volatile
        private var instance: Database? = null

        fun getInstance(name: String): Database {
            return instance ?: synchronized(this) {
                instance ?: Database(name).also { instance = it }
            }
        }
    }

    fun query(sql: String) = println("[$name] Query: $sql")
}

// ✅ Solution 2: Dependency Injection (preferred in real apps)
class ConfigManager private constructor(private val config: Map<String, String>) {
    companion object {
        fun create(env: String): ConfigManager {
            val config = when (env) {
                "dev" -> mapOf("url" to "http://localhost", "debug" to "true")
                "prod" -> mapOf("url" to "https://api.example.com", "debug" to "false")
                else -> throw IllegalArgumentException("Unknown env: $env")
            }
            return ConfigManager(config)
        }
    }

    fun get(key: String): String? = config[key]
}

fun main() {
    // Singleton with parameter
    val db1 = Database.getInstance("MyDB")
    val db2 = Database.getInstance("OtherDB")  // Returns same instance
    println(db1 === db2)  // true — same instance
    db1.query("SELECT * FROM users")

    // Config manager
    val config = ConfigManager.create("dev")
    println("URL: ${config.get("url")}")    // http://localhost
    println("Debug: ${config.get("debug")}")  // true
}
```

### Key Takeaway
- `object` can't take constructor parameters
- Use double-checked locking with `@Volatile` + `synchronized` for thread safety
- In real apps, prefer DI (Hilt/Koin) over manual singletons
- `also { instance = it }` assigns after construction

---

## 🔗 Related Topics
- [OOP](../intermediate/OOP.md)
- [Data Classes & Sealed Classes](../intermediate/DataAndSealedClasses.md)
- [DSL Building](../advanced/DSL.md)
