# Delegated Properties

## 📖 Explanation

Delegated properties allow you to delegate the getter/setter of a property to another object. This enables lazy initialization, observable properties, and custom property behaviors.

### Syntax
```kotlin
val prop: Type by Delegate()
```

### Standard Delegates

#### `lazy`
Initializes the value only on first access. Thread-safe by default.

```kotlin
val expensive: Heavy by lazy { computeHeavy() }
```

#### `observable`
Notifies when a value changes. Initial value + lambda.

```kotlin
var count: Int by Delegates.observable(0) { prop, old, new ->
    println("$old -> $new")
}
```

#### `vetoable`
Can veto (reject) a change based on a condition.

```kotlin
var positive: Int by Delegates.vetoable(0) { _, _, new -> new >= 0 }
```

#### `notNull`
Throws if accessed before being set.

```kotlin
var late: String by Delegates.notNull()
```

### Custom Property Delegates
Implement `getValue` and (for `var`) `setValue` operator functions.

```kotlin
class Example {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = "..."
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) { ... }
}
```

### `map` Delegation
Store properties in a map — useful for JSON parsing, dynamic objects.

```kotlin
class User(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}
```

---

## 🧪 Code Example

```kotlin
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

fun main() {
    // lazy
    val config = Config()
    println("Before access")
    println("Database: ${config.databaseUrl}")  // Computed now
    println("Database: ${config.databaseUrl}")  // Reused (not recomputed)

    // observable
    var score: Int by Delegates.observable(0) { _, old, new ->
        println("Score changed: $old -> $new")
    }
    score = 10
    score = 20
    score = 30

    // vetoable
    var temperature: Int by Delegates.vetoable(20) { _, _, new -> new in -50..50 }
    temperature = 25   // ✅ accepted
    temperature = 100  // ❌ rejected
    println("Temperature: $temperature")  // 25

    // notNull
    var lateValue: String by Delegates.notNull()
    lateValue = "Initialized"
    println("Late value: $lateValue")

    // Custom delegate — logging
    var name: String by LoggerDelegate("Default")
    println("Name: $name")
    name = "Alice"
    println("Name: $name")

    // Map delegation
    val userMap = mapOf(
        "name" to "Bob",
        "age" to 30
    )
    val user = User(userMap)
    println("User: ${user.name}, ${user.age}")

    // Mutable map delegation
    val mutableMap = mutableMapOf("city" to "NYC")
    val person = MutablePerson(mutableMap)
    println("City: ${person.city}")
    person.city = "LA"
    println("City after change: ${person.city}")
    println("Map: $mutableMap")
}

// --- lazy ---
class Config {
    val databaseUrl: String by lazy {
        println("Computing database URL...")
        "jdbc:postgresql://localhost:5432/db"
    }
}

// --- Custom delegate ---
class LoggerDelegate(initial: String) {
    private var value = initial
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        println("GET ${property.name} = $value")
        return value
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("SET ${property.name} = $value")
        this.value = value
    }
}

// --- Map delegation ---
class User(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}

class MutablePerson(map: MutableMap<String, Any?>) {
    var city: String by map
}
```

### Output
```
Before access
Computing database URL...
Database: jdbc:postgresql://localhost:5432/db
Database: jdbc:postgresql://localhost:5432/db
Score changed: 0 -> 10
Score changed: 10 -> 20
Score changed: 20 -> 30
Temperature: 25
Late value: Initialized
GET name = Default
Name: Default
SET name = Alice
GET name = Alice
Name: Alice
User: Bob, 30
City: NYC
City after change: LA
Map: {city=LA}
```

---

## ❓ Interview Questions

1. **What are delegated properties in Kotlin?**
   - Properties whose getter/setter logic is delegated to another object via the `by` keyword. Enables `lazy`, `observable`, `vetoable`, and custom delegates.

2. **How does `lazy` work and is it thread-safe?**
   - `lazy` initializes the value only on first access. By default, it uses `LazyThreadSafetyMode.SYNCHRONIZED` (thread-safe). You can change to `PUBLICATION` or `NONE`.

3. **What is the difference between `observable` and `vetoable`?**
   - `observable` notifies after a change (cannot prevent it). `vetoable` can reject a change by returning `false` from the lambda.

4. **How do you create a custom property delegate?**
   - Implement `operator fun getValue(thisRef, property)` and optionally `operator fun setValue(thisRef, property, value)`. The `by` keyword connects the delegate to the property.

5. **What is map delegation and when is it useful?**
   - Delegating properties to a `Map` or `MutableMap` so property access reads/writes map entries. Useful for dynamic data like JSON parsing, configuration, or ORM entities.

6. **What are the `ReadWriteProperty` and `ReadOnlyProperty` interfaces and how do you use them?**
    - Kotlin provides standard interfaces for custom delegates: `ReadOnlyProperty<in R, out T>` — for `val` properties: `interface ReadOnlyProperty<in R, out T> { operator fun getValue(thisRef: R, property: KProperty<*>): T }`. `ReadWriteProperty<in R, T>` — for `var` properties: `interface ReadWriteProperty<in R, T> : ReadOnlyProperty<R, T> { operator fun setValue(thisRef: R, property: KProperty<*>, value: T) }`. `R` is the receiver type (the class the property belongs to), `T` is the property type. Usage: `class TrimmedString : ReadWriteProperty<Any?, String> { private var value: String = ""; override fun getValue(thisRef, property) = value; override fun setValue(thisRef, property, value) { this.value = value.trim() } }`. Usage: `var name: String by TrimmedString()`. Using these interfaces provides type safety and makes your delegates interoperable with Kotlin's delegation framework. You can also create delegates as top-level functions returning `ReadOnlyProperty`/`ReadWriteProperty`.

7. **How do you create a custom property delegate for logging or validation?**
    - **Logging delegate**: `class LoggingVar<T>(initial: T) : ReadWriteProperty<Any?, T> { private var value = initial; override fun getValue(thisRef, property) = value; override fun setValue(thisRef, property, value) { println("${property.name} changed from ${this.value} to $value"); this.value = value } }`. Usage: `var count: Int by LoggingVar(0)` — every change is logged. **Validation delegate**: `class ValidatedString(private val rule: (String) -> Boolean) : ReadWriteProperty<Any?, String> { private var value = ""; override fun getValue(thisRef, property) = value; override fun setValue(thisRef, property, value) { require(rule(value)) { "Invalid value for ${property.name}: $value" }; this.value = value } }`. Usage: `var email: String by ValidatedString { it.contains("@") }` — throws on invalid input. **Caching delegate**: `class Cached<T>(private val loader: () -> T) : ReadOnlyProperty<Any?, T> { private var cached: T? = null; override fun getValue(thisRef, property): T { if (cached == null) cached = loader(); return cached!! } }`. Custom delegates encapsulate cross-cutting concerns — validation, logging, caching — in reusable property logic.

8. **What is the difference between `lazy` and `lateinit var`?**
    - **`lazy`** — for `val` only. Initializes on first access. Thread-safe by default (`LazyThreadSafetyMode.SYNCHRONIZED`). The initializer lambda runs once. Cannot be reinitialized. `val db: Database by lazy { Database.connect() }`. **`lateinit var`** — for `var` only. No custom initializer — you set the value later. Not thread-safe. Accessing before initialization throws `UninitializedPropertyAccessException`. Must be non-nullable and non-primitive. `lateinit var service: UserService`. **Key differences**: (1) `lazy` is for `val`, `lateinit` for `var`. (2) `lazy` has a lambda initializer, `lateinit` doesn't. (3) `lazy` is thread-safe, `lateinit` is not. (4) `lazy` works with any type, `lateinit` only with non-nullable reference types (no primitives, no `String?`). (5) `lazy` initializes on first access, `lateinit` requires manual initialization. (6) `lateinit` can be checked with `::service.isInitialized`. Use `lazy` for expensive initialization that may never be needed. Use `lateinit` for DI (Hilt `@Inject`) where the value is set after construction.

9. **How does `observable` differ from `vetoable` and when do you use each?**
    - **`observable`** — notifies you AFTER a change is made. You can observe but NOT prevent it: `var count: Int by observable(0) { _, old, new -> println("Changed: $old → $new") }`. The lambda receives property, old value, and new value. The change has already happened. Use for logging, triggering side effects, updating UI. **`vetoable`** — can REJECT a change by returning `false`: `var age: Int by vetoable(0) { _, old, new -> if (new < 0) { println("Rejecting negative age"); false } else true }`. The lambda runs BEFORE the change — returning `false` prevents it. Use for validation — ensuring only valid values are stored. Both are in `kotlin.properties.Delegates`. Use `observable` for "notify after change" (logging, side effects). Use `vetoable` for "validate before change" (constraints, invariants). Example: `var password by vetoable("") { _, _, new -> new.length >= 8 }` — rejects passwords shorter than 8 characters.

10. **How do you delegate properties to a Map and what are the use cases?**
    - **Read-only map delegation**: `class Config(map: Map<String, Any?>) { val host: String by map; val port: Int by map; val debug: Boolean by map }`. The property name must match the map key: `map["host"]`, `map["port"]`. Usage: `val config = Config(mapOf("host" to "localhost", "port" to 8080, "debug" to true)); config.host` → "localhost". **Mutable map delegation**: `class MutableConfig(map: MutableMap<String, Any?>) { var host: String by map; var port: Int by map }`. Setting a property writes to the map: `config.host = "newhost"` → `map["host"] = "newhost"`. Use cases: (1) **JSON parsing** — map JSON fields to typed properties: `class User(map: Map<String, Any?>) { val name: String by map; val age: Int by map }`. (2) **Configuration** — load config from a map/properties file. (3) **ORM entities** — map database columns to properties. (4) **Dynamic objects** — when the schema is not known at compile time. **Caveat**: type casting happens at runtime — `map["port"] as Int` throws `ClassCastException` if the value isn't an `Int`. Not type-safe at compile time.

11. **How do you use delegation with composition and what is class delegation?**
    - **Class delegation** (`by` on a class) — delegates interface implementation to another object: `interface CanFly { fun fly() }; class Bird : CanFly { override fun fly() = println("Flying") }; class Penguin(bird: CanFly) : CanFly by bird`. The `Penguin` class delegates `fly()` to `bird`. Unlike inheritance, delegation is composition — `Penguin` IS-NOT-A `Bird`, it HAS-A `CanFly`. Benefits: (1) **Favor composition over inheritance** — more flexible than inheritance. (2) **Multiple interface delegation** — `class SuperBird(a: CanFly, b: CanSwim) : CanFly by a, CanSwim by b`. (3) **Override specific methods** — `class SmartBird(b: CanFly) : CanFly by b { override fun fly() { println("Smart flying"); b.fly() } }`. (4) **Testability** — easily mock the delegate. Use class delegation when: you want to reuse implementation without inheritance, you need multiple "is-a" relationships, or you want to decorate/extend behavior. Kotlin's `by` keyword makes delegation zero-boilerplate — the compiler generates forwarding methods.

12. **What are advanced delegation patterns and best practices?**
    - **Advanced patterns**: (1) **Conditional delegation** — `val data: String by if (BuildConfig.DEBUG) LoggingDelegate("") else SimpleDelegate("")`. (2) **Delegation with state** — `class Counter { var count = 0; val next: Int by observable(count) { _, _, _ -> count++ } }`. (3) **Property delegation with coroutines** — `val data: String by asyncLazy { fetchData() }` (custom delegate that suspends). (4) **Delegation to SharedPreferences** — `var theme: String by SharedPreferencesDelegate(prefs, "theme", "light")`. (5) **Delegation to Flow/StateFlow** — bridge reactive and property worlds. **Best practices**: (1) Use `lazy` for expensive initialization — not for simple values. (2) Use `lateinit` for DI — check `isInitialized` before access. (3) Keep custom delegates simple — one responsibility. (4) Cache delegate instances — `companion object { val loggingDelegate = LoggingDelegate() }` — avoid creating new delegate objects per property. (5) Use `ReadOnlyProperty`/`ReadWriteProperty` interfaces for type safety. (6) Test delegates independently — they're just classes/functions. (7) Document delegate behavior — especially side effects, thread safety, and lifecycle. (8) Avoid delegation for trivial properties — it adds complexity.

---

## 🔗 Related Topics
- [Generics](Generics.md)
- [OOP](../intermediate/OOP.md)
