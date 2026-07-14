# Creational Patterns

Creational patterns deal with **object creation**. They abstract the instantiation process, making a system independent of how its objects are created, composed, and represented.

---

## 1. Singleton

### Intent
Ensure a class has **only one instance** and provide a **global access point** to it.

### Problem It Solves
Some resources must be unique: a database connection pool, a configuration manager, a logger, a hardware access controller. If multiple instances exist, you get conflicts: duplicate connections, inconsistent config, race conditions.

### Structure
```kotlin
class DatabaseConnection private constructor() {
    companion object {
        private var instance: DatabaseConnection? = null

        fun getInstance(): DatabaseConnection {
            if (instance == null) {
                instance = DatabaseConnection()
            }
            return instance!!
        }
    }

    fun query(sql: String): Result { /* ... */ }
}
```

### Thread-Safe Singleton (Kotlin)
```kotlin
// Kotlin: use `object` for a simple singleton
object DatabaseConfig {
    val url = "jdbc:postgresql://localhost:5432/mydb"
    val maxConnections = 10
}

// Thread-safe lazy singleton (double-checked locking)
class Logger private constructor() {
    companion object {
        @Volatile
        private var instance: Logger? = null

        fun getInstance(): Logger {
            return instance ?: synchronized(this) {
                instance ?: Logger().also { instance = it }
            }
        }
    }

    fun log(message: String) { println("[LOG] $message") }
}
```

### When to Use
- Configuration manager (one set of settings).
- Connection pool (shared across the app).
- Logger (single output stream).
- Hardware access (one printer, one sensor).

### When NOT to Use
- **It's a global variable in disguise.** Singleton introduces hidden coupling — every class that uses it depends on a global state.
- **Hard to test.** You can't easily mock a singleton. Use DI instead.
- **Multi-threaded environments.** Naive singleton is not thread-safe. Even with locking, it's a bottleneck.
- **When you might need more than one.** Today you need one logger. Tomorrow you need separate loggers for errors and info. Singleton makes this change painful.

### The DI Alternative
```kotlin
// Instead of Singleton:
object AuthService { fun authenticate() { ... } }

// Use DI: create one instance, inject it everywhere
class AuthService { fun authenticate() { ... } }

// At the composition root:
val authService = AuthService()  // one instance
val userController = UserController(authService)
val orderController = OrderController(authService)
```
This gives you the "one instance" benefit without the global state. And it's testable.

### Key Insight
> **Singleton is the most overused and abused pattern. It feels convenient (global access!) but creates tight coupling, hidden dependencies, and testing nightmares. In modern code, prefer dependency injection. Use Singleton only for true singletons (hardware, JVM runtime) where multiple instances are physically impossible.**

---

## 2. Factory Method

### Intent
Define an interface for creating objects, but let subclasses decide **which class to instantiate**. The factory method defers instantiation to subclasses.

### Problem It Solves
You need to create objects, but the exact type isn't known until runtime. You want to decouple the **creation** from the **usage**.

### Structure
```kotlin
// Product interface
interface Transport {
    fun deliver(): String
}

// Concrete products
class Truck : Transport {
    override fun deliver() = "Delivering by land in a truck"
}

class Ship : Transport {
    override fun deliver() = "Delivering by sea in a ship"
}

// Creator with factory method
abstract class Logistics {
    // The factory method — subclasses override this
    abstract fun createTransport(): Transport

    fun planDelivery(): String {
        val transport = createTransport()
        return "Planning: ${transport.deliver()}"
    }
}

// Concrete creators
class RoadLogistics : Logistics() {
    override fun createTransport() = Truck()
}

class SeaLogistics : Logistics() {
    override fun createTransport() = Ship()
}

// Usage:
val logistics: Logistics = RoadLogistics()
println(logistics.planDelivery()) // "Planning: Delivering by land in a truck"
```

### When to Use
- You don't know the exact type at compile time.
- You want to delegate creation to subclasses.
- You want to centralize creation logic (validation, caching, logging).

### When NOT to Use
- When there's only one product type — a simple constructor is enough.
- When the type is always known at compile time — no need for indirection.

### Key Insight
> **Factory Method is about polymorphic creation. The creator doesn't know what it creates — the subclass decides. This is the Open/Closed Principle in action: add a new `AirLogistics` that creates `Airplane` without modifying existing code.**

---

## 3. Abstract Factory

### Intent
Provide an interface for creating **families of related objects** without specifying their concrete classes.

### Problem It Solves
You need to create a set of related objects that must work together. A "modern" UI needs modern buttons + modern checkboxes + modern dialogs. A "classic" UI needs classic versions of all. You can't mix a modern button with a classic dialog — they look inconsistent.

### Structure
```kotlin
// Abstract products
interface Button { fun render(): String }
interface Checkbox { fun render(): String }
interface TextField { fun render(): String }

// Modern family
class ModernButton : Button { override fun render() = "[Modern Button]" }
class ModernCheckbox : Checkbox { override fun render() = "[Modern Checkbox]" }
class ModernTextField : TextField { override fun render() = "[Modern TextField]" }

// Classic family
class ClassicButton : Button { override fun render() = "[Classic Button]" }
class ClassicCheckbox : Checkbox { override fun render() = "[Classic Checkbox]" }
class ClassicTextField : TextField { override fun render() = "[Classic TextField]" }

// Abstract factory
interface UIFactory {
    fun createButton(): Button
    fun createCheckbox(): Checkbox
    fun createTextField(): TextField
}

// Concrete factories
class ModernUIFactory : UIFactory {
    override fun createButton() = ModernButton()
    override fun createCheckbox() = ModernCheckbox()
    override fun createTextField() = ModernTextField()
}

class ClassicUIFactory : UIFactory {
    override fun createButton() = ClassicButton()
    override fun createCheckbox() = ClassicCheckbox()
    override fun createTextField() = ClassicTextField()
}

// Usage:
class Application(private val factory: UIFactory) {
    fun renderUI(): String {
        val button = factory.createButton()
        val checkbox = factory.createCheckbox()
        val textField = factory.createTextField()
        return "${button.render()} ${checkbox.render()} ${textField.render()}"
    }
}

val app = Application(ModernUIFactory())
println(app.renderUI()) // "[Modern Button] [Modern Checkbox] [Modern TextField]"
```

### Factory Method vs Abstract Factory

| Factory Method | Abstract Factory |
|---|---|
| Creates **one** product | Creates a **family** of related products |
| Subclass decides which to create | Factory itself decides all products |
| Inheritance-based | Composition-based |
| Simpler | More complex |

### When to Use
- You need to create families of related objects (UI themes, DB drivers for different SQL dialects).
- The family must be consistent — no mixing modern buttons with classic dialogs.

### Key Insight
> **Abstract Factory is Factory Method for families. It ensures you get a consistent set of objects. The key is: the factory knows how to create a complete family, and you can swap the entire family by swapping the factory.**

---

## 4. Builder

### Intent
Separate the construction of a complex object from its representation. Allow step-by-step construction of an object with many optional parameters.

### Problem It Solves
When an object has many parameters (especially optional ones), constructors become unmanageable:

```kotlin
// BAD: Telescoping constructor problem
class Pizza(
    size: String,
    cheese: Boolean = false,
    pepperoni: Boolean = false,
    mushrooms: Boolean = false,
    onions: Boolean = false,
    peppers: Boolean = false,
    olives: Boolean = false,
    extraSauce: Boolean = false,
    stuffedCrust: Boolean = false
)

// Which boolean is which? Unreadable.
val pizza = Pizza("large", true, false, true, false, false, true, false, true)
```

### Structure
```kotlin
class Pizza private constructor(
    val size: String,
    val cheese: Boolean,
    val pepperoni: Boolean,
    val mushrooms: Boolean,
    val olives: Boolean,
    val stuffedCrust: Boolean
) {
    // Builder class
    class Builder(private val size: String) {
        private var cheese = false
        private var pepperoni = false
        private var mushrooms = false
        private var olives = false
        private var stuffedCrust = false

        fun addCheese() = apply { cheese = true }
        fun addPepperoni() = apply { pepperoni = true }
        fun addMushrooms() = apply { mushrooms = true }
        fun addOlives() = apply { olives = true }
        fun stuffedCrust() = apply { stuffedCrust = true }

        fun build(): Pizza {
            // Validate invariants here
            require(size.isNotBlank()) { "Size is required" }
            return Pizza(size, cheese, pepperoni, mushrooms, olives, stuffedCrust)
        }
    }
}

// Usage: readable and fluent
val pizza = Pizza.Builder("large")
    .addCheese()
    .addPepperoni()
    .addMushrooms()
    .stuffedCrust()
    .build()
```

### Kotlin DSL Alternative
```kotlin
// Kotlin makes this even cleaner with DSL
class Pizza(val size: String) {
    val toppings = mutableListOf<String>()
    var stuffedCrust = false

    fun topping(name: String) { toppings.add(name) }
    fun stuffedCrust(enabled: Boolean = true) { stuffedCrust = enabled }
}

fun pizza(size: String, init: Pizza.() -> Unit): Pizza {
    return Pizza(size).apply(init)
}

// Usage:
val myPizza = pizza("large") {
    topping("cheese")
    topping("pepperoni")
    topping("mushrooms")
    stuffedCrust()
}
```

### When to Use
- Object has many parameters (more than 3-4).
- Many parameters are optional.
- Object should be immutable after creation.
- Construction has validation/invariants.

### When NOT to Use
- Object has few parameters — a data class with defaults is simpler.
- Object is mutable and will change after creation.

### Key Insight
> **Builder solves the telescoping constructor problem and makes object construction readable. It also centralizes validation — the `build()` method is the single point where invariants are checked. The trade-off: more code. Use it when the complexity of construction justifies it.**

---

## 5. Prototype

### Intent
Create new objects by **cloning an existing object** (the prototype) instead of creating from scratch.

### Problem It Solves
- Creating a new object is expensive (DB query, network call, complex computation).
- You already have a fully configured object and want a copy with minor changes.
- You don't know the concrete type — you just want a copy of "this."

### Structure
```kotlin
abstract class Shape : Cloneable {
    var x: Int = 0
    var y: Int = 0
    var color: String = ""

    abstract fun clone(): Shape

    public override fun clone(): Shape {
        return super.clone() as Shape
    }
}

class Circle : Shape() {
    var radius: Int = 0

    override fun clone(): Circle {
        return Circle().also {
            it.x = this.x
            it.y = this.y
            it.color = this.color
            it.radius = this.radius
        }
    }
}

// Usage:
val original = Circle().apply {
    x = 10; y = 20; color = "red"; radius = 5
}

val clone = original.clone()
clone.x = 100  // clone is independent

println(original.x)  // 10 (unchanged)
println(clone.x)     // 100
```

### Shallow vs Deep Copy
```kotlin
class Address(var city: String)
class Person(var name: String, var address: Address) : Cloneable {
    // Shallow copy: address is shared between original and clone
    fun shallowCopy(): Person {
        return Person(name, address)  // same Address object!
    }

    // Deep copy: address is also cloned
    fun deepCopy(): Person {
        return Person(name, Address(address.city))  // new Address object
    }
}

val original = Person("Alice", Address("NYC"))
val shallow = original.shallowCopy()
val deep = original.deepCopy()

original.address.city = "LA"

println(shallow.address.city) // "LA" — affected! (shared reference)
println(deep.address.city)    // "NYC" — unaffected (independent copy)
```

### When to Use
- Object creation is expensive (complex initialization).
- You need many similar objects that differ slightly.
- You want to avoid subclass hierarchies for object creation.

### Key Insight
> **Prototype is about cloning, not creating. It's useful when construction is expensive but copying is cheap. The critical gotcha: shallow vs deep copy. If your object contains references, you must decide: share the reference (shallow) or copy it (deep). Most bugs in prototype come from getting this wrong.**

---

## 6. Object Pool

### Intent
Reuse objects from a pool instead of creating and destroying them repeatedly.

### Problem It Solves
Some objects are expensive to create and destroy: database connections, thread objects, network sockets. If you create a new connection per request and destroy it after, you waste 20-50 ms per request on setup/teardown.

### Structure
```kotlin
class DatabaseConnection {
    fun query(sql: String): String { return "Result of: $sql" }
    fun close() { /* release resources */ }
}

class ConnectionPool(private val maxSize: Int = 10) {
    private val available = mutableListOf<DatabaseConnection>()
    private val inUse = mutableSetOf<DatabaseConnection>()

    @Synchronized
    fun acquire(): DatabaseConnection {
        if (available.isNotEmpty()) {
            val conn = available.removeAt(0)
            inUse.add(conn)
            return conn
        }
        if (inUse.size < maxSize) {
            val conn = DatabaseConnection()
            inUse.add(conn)
            return conn
        }
        // Pool exhausted — wait or throw
        throw IllegalStateException("Connection pool exhausted")
    }

    @Synchronized
    fun release(conn: DatabaseConnection) {
        inUse.remove(conn)
        available.add(conn)  // return to pool, don't destroy
    }
}

// Usage:
val pool = ConnectionPool(maxSize = 5)
val conn = pool.acquire()
try {
    val result = conn.query("SELECT * FROM users")
} finally {
    pool.release(conn)  // return to pool, not destroyed
}
```

### When to Use
- Object creation/destruction is expensive (DB connections, threads, sockets).
- You need a bounded number of objects (limit resource usage).
- Objects are used briefly and returned.

### When NOT to Use
- Objects are cheap to create (plain data objects).
- Objects are held for a long time (pool doesn't help).

### Key Insight
> **Object Pool is the pattern behind connection pools (HikariCP), thread pools (ExecutorService), and socket pools. It trades memory (keeping objects alive) for performance (avoiding recreation). The critical configuration is pool size: too small → contention; too large → resource waste.**

---

## Summary: When to Use Which Creational Pattern

| Pattern | When to Use | Key Benefit |
|---|---|---|
| **Singleton** | One instance needed globally | Single instance |
| **Factory Method** | Don't know type at compile time | Decoupled creation |
| **Abstract Factory** | Family of related objects | Consistent families |
| **Builder** | Many optional parameters | Readable construction |
| **Prototype** | Expensive creation, need copies | Clone instead of construct |
| **Object Pool** | Expensive create/destroy cycle | Reuse objects |
