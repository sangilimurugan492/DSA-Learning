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

---

## 🔗 Related Topics
- [Generics](Generics.md)
- [OOP](../intermediate/OOP.md)
