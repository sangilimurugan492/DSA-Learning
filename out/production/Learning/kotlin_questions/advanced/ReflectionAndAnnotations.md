# Reflection & Annotations

## 📖 Explanation

### Reflection
Reflection allows inspecting and modifying code structures (classes, functions, properties) at runtime. Kotlin provides two reflection APIs:

1. **Kotlin Reflection** (`kotlin.reflect`) — `KClass`, `KFunction`, `KProperty`
2. **Java Reflection** (`java.lang.reflect`) — `Class`, `Method`, `Field`

### Class References
```kotlin
val kClass = String::class          // KClass<String>
val javaClass = String::class.java  // Class<String>
val fromInstance = "hello"::class   // KClass out of instance
```

### Function References
```kotlin
val isEven: (Int) -> Boolean = { it % 2 == 0 }
val isEvenRef: (Int) -> Boolean = ::isEvenFun  // function reference
```

### Property References
```kotlin
val prop = Person::name  // KProperty1<Person, String>
val name = prop.get(person)
```

### Constructor References
```kotlin
val factory = ::Person  // (String, Int) -> Person
val person = factory("Alice", 30)
```

### Inspecting Classes
```kotlin
val kClass = Person::class
kClass.members          // All members
kClass.constructors     // All constructors
kClass.properties       // All properties
kClass.isData           // true for data class
kClass.isSealed         // true for sealed class
```

### Annotations
Annotations are metadata added to code. Kotlin provides built-in annotations and allows custom ones.

#### Built-in Annotations
| Annotation       | Description                                    |
|------------------|------------------------------------------------|
| `@Deprecated`    | Marks code as deprecated with a message        |
| `@JvmStatic`     | Generates true static method for Java interop  |
| `@JvmOverloads`  | Generates overloaded methods for default args  |
| `@JvmField`      | Exposes property as public field (no accessor) |
| `@Throws`        | Declares checked exceptions for Java           |
| `@Transient`     | Excludes field from serialization              |
| `@Volatile`      | Marks field as volatile for thread safety      |
| `@DslMarker`     | Restricts DSL receiver access                 |
| `@JvmInline`     | Marks a value class                           |

#### Custom Annotations
```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cacheable(val key: String)
```

### Annotation Targets
| Target              | Applies to                          |
|---------------------|-------------------------------------|
| `CLASS`             | Classes                             |
| `FUNCTION`          | Functions                           |
| `PROPERTY`          | Properties                          |
| `FIELD`             | Backing field                       |
| `VALUE_PARAMETER`   | Parameter values                    |
| `CONSTRUCTOR`       | Constructors                        |
| `PROPERTY_GETTER`   | Property getter                     |
| `PROPERTY_SETTER`   | Property setter                     |

### Retention
| Retention           | When accessible                     |
|---------------------|-------------------------------------|
| `SOURCE`            | Source code only (discarded at compile) |
| `BINARY`            | In bytecode, not visible at runtime  |
| `RUNTIME`           | Visible at runtime via reflection    |

---

## 🧪 Code Example

```kotlin
import kotlin.reflect.*
import kotlin.reflect.full.*

fun main() {
    // --- Class reflection ---
    val kClass = Person::class
    println("=== Class Reflection ===")
    println("Simple name: ${kClass.simpleName}")
    println("Is data: ${kClass.isData}")
    println("Is sealed: ${kClass.isSealed}")
    println("Is abstract: ${kClass.isAbstract}")

    // Constructors
    println("\nConstructors:")
    kClass.constructors.forEach { println("  $it") }

    // Properties
    println("\nProperties:")
    kClass.memberProperties.forEach { prop ->
        println("  ${prop.name}: ${prop.returnType}")
    }

    // Functions
    println("\nFunctions:")
    kClass.memberFunctions.forEach { func ->
        println("  ${func.name}(${func.parameters.drop(1).joinToString { it.toString() }})")
    }

    // --- Create instance via reflection ---
    println("\n=== Instance Creation ===")
    val constructor = Person::class.primaryConstructor
    val person = constructor?.call("Alice", 30)
    println("Created: $person")

    // --- Property access via reflection ---
    println("\n=== Property Access ===")
    val nameProp = Person::class.memberProperties.find { it.name == "name" }
    val nameValue = nameProp?.getter?.call(person)
    println("Name via reflection: $nameValue")

    // --- Annotations ---
    println("\n=== Annotations ===")
    val serviceClass = ApiService::class
    val classAnnotation = serviceClass.annotations.find { it is Cacheable }
    println("Class annotation: $classAnnotation")

    // Find annotated functions
    println("\nCached methods:")
    serviceClass.memberFunctions.forEach { func ->
        val cacheable = func.annotations.find { it is Cacheable }
        if (cacheable != null) {
            println("  ${func.name} -> ${(cacheable as Cacheable).key}")
        }
    }

    // --- Function reference ---
    println("\n=== Function References ===")
    val isEvenRef = ::isEven
    println("isEven(4): ${isEvenRef(4)}")
    println("isEven(5): ${isEvenRef(5)}")

    // --- Constructor reference ---
    println("\n=== Constructor Reference ===")
    val factory: (String, Int) -> Person = ::Person
    val created = factory("Bob", 25)
    println("Factory created: $created")

    // --- @JvmOverloads demo ---
    println("\n=== Default Args ===")
    greet("Alice")
    greet("Bob", "Hi")
    greet("Charlie", "Hey", 3)

    // --- @Deprecated ---
    println("\n=== Deprecated ===")
    @Suppress("DEPRECATION")
    oldFunction()
}

// --- Data class for reflection ---
data class Person(val name: String, val age: Int) {
    fun greet() = "Hello, I'm $name"
    fun isAdult() = age >= 18
}

// --- Custom annotation ---
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cacheable(val key: String)

// --- Annotated class ---
@Cacheable("api_service")
class ApiService {
    @Cacheable("get_user")
    fun getUser(id: Long): String = "User($id)"

    @Cacheable("list_users")
    fun listUsers(): String = "All users"

    fun deleteUser(id: Long) = "Deleted($id)"  // Not cached
}

// --- Function for reference ---
fun isEven(n: Int): Boolean = n % 2 == 0

// --- @JvmOverloads ---
@JvmOverloads
fun greet(name: String, greeting: String = "Hello", times: Int = 1) {
    repeat(times) { println("$greeting, $name!") }
}

// --- @Deprecated ---
@Deprecated("Use newFunction() instead", ReplaceWith("newFunction()"))
fun oldFunction() = println("Old function called")

fun newFunction() = println("New function called")
```

### Output
```
=== Class Reflection ===
Simple name: Person
Is data: true
Is sealed: false
Is abstract: false

Constructors:
  fun <init>(kotlin.String, kotlin.Int): Person

Properties:
  name: kotlin.String
  age: kotlin.Int

Functions:
  greet()
  isAdult()
  equals(kotlin.Any?): kotlin.Boolean
  hashCode(): kotlin.Int
  toString(): kotlin.String
  component1(): kotlin.String
  component2(): kotlin.Int
  copy(kotlin.String, kotlin.Int): Person

=== Instance Creation ===
Created: Person(name=Alice, age=30)

=== Property Access ===
Name via reflection: Alice

=== Annotations ===
Class annotation: @Cacheable(key=api_service)

Cached methods:
  getUser -> get_user
  listUsers -> list_users

=== Function References ===
isEven(4): true
isEven(5): false

=== Constructor Reference ===
Factory created: Person(name=Bob, age=25)

=== Default Args ===
Hello, Alice!
Hi, Bob!
Hey, Charlie!
Hey, Charlie!
Hey, Charlie!

=== Deprecated ===
Old function called
```

---

## ❓ Interview Questions

1. **What is reflection in Kotlin and when would you use it?**
   - Reflection allows inspecting classes, functions, and properties at runtime. Use for frameworks (serialization, DI, ORM), testing, and code generation. Avoid in performance-critical code.

2. **What is the difference between `::class` and `::class.java`?**
   - `::class` returns a `KClass` (Kotlin reflection). `::class.java` returns a `Class` (Java reflection). Kotlin reflection is richer — knows about properties, nullable types, etc.

3. **What are function and property references?**
   - `::functionName` creates a reference to a function that can be passed as a lambda. `ClassName::propertyName` creates a property reference (`KProperty`) for getting/setting.

4. **How do you create a custom annotation in Kotlin?**
   - Declare with `annotation class` keyword. Specify `@Target` (where it applies) and `@Retention` (source, binary, or runtime). E.g., `@Retention(RUNTIME) annotation class MyAnno(val key: String)`.

5. **What is the difference between `@JvmStatic`, `@JvmOverloads`, and `@JvmField`?**
   - `@JvmStatic` makes a companion object member a true static for Java. `@JvmOverloads` generates overloaded methods for default arguments (Java doesn't have defaults). `@JvmField` exposes a property as a public field without getters/setters.

---

## 🔗 Related Topics
- [Generics](Generics.md)
- [Inline Functions & Reified Types](../intermediate/InlineAndReified.md)
