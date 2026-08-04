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

6. **What is the performance impact of reflection and how do you minimize it?**
    - Reflection is significantly slower than direct calls — 10-100x for method invocation, 5-20x for field access. The JVM can't optimize reflective calls as well as direct calls. Minimize impact: (1) **Cache reflective objects** — `KClass`, `KFunction`, `KProperty` are expensive to look up. Store them in a `companion object` or `val`. (2) **Use `kotlin.reflect` sparingly** — it requires the `kotlin-reflect` library (adds ~2MB to APK). (3) **Prefer code generation** — Moshi's codegen, Kotlinx Serialization's compiler plugin generate code at compile time instead of using reflection. (4) **Use `@JvmStatic` and direct calls** where possible. (5) **Lazy initialization** — `lazy { MyClass::class }` to defer reflection cost. (6) **Avoid reflection in hot paths** — don't use it in loops or frequently-called methods. (7) **Use `KClass.simpleName` instead of `javaClass.simpleName`** — sometimes faster. (8) **Benchmark** — use JMH to measure reflective overhead. For Android, prefer annotation processing (KSP/KAPT) over runtime reflection. Reflection is acceptable for frameworks (DI, serialization) but avoid it in performance-critical code.

7. **How do you use reflection to instantiate objects and call methods dynamically?**
    - **Instantiate**: `val klass = MyClass::class; val instance = klass.createInstance()` (requires primary constructor with no required params) or `klass.constructors.first().call(arg1, arg2)`. For data classes: `val constructor = klass.primaryConstructor; val instance = constructor?.callBy(mapOf(param to value))`. **Call methods**: `val method = klass.functions.find { it.name == "myMethod" }; method?.call(instance, arg1)`. For member functions: `klass.memberFunctions.find { it.name == "process" }?.call(instance, data)`. **Access properties**: `val prop = klass.memberProperties.find { it.name == "name" } as KMutableProperty1<MyClass, String>; prop.set(instance, "New")`. Use cases: (1) DI frameworks — instantiate classes by scanning annotations. (2) JSON serialization — read property names and values. (3) ORM — map columns to properties. (4) Testing — invoke private methods (not recommended). Always handle `IllegalArgumentException` — wrong argument types cause runtime errors. Prefer `callBy(map)` over `call(args)` for named/optional parameters.

8. **What are Kotlin property references and how are they different from Java field references?**
    - Kotlin property references (`::property`) create a `KProperty<T>` that provides typed access to the property's getter (and setter for `var`). Unlike Java field references (which just access the field), Kotlin property references encapsulate the getter/setter. Types: (1) `KProperty0<T>` — no receiver (top-level or local). (2) `KProperty1<R, T>` — one receiver (member property): `User::name`. (3) `KProperty2<D, I, T>` — extension property. Usage: `val nameProp = User::name; val name = nameProp.get(user)`. For `var`: `val ageProp = User::age as KMutableProperty1<User, Int>; ageProp.set(user, 31)`. Property references are used in: (1) Data binding — `binding.setVariable(BR.user, user)`. (2) Testing — `assertEquals("Alice", User::name.get(user))`. (3) Functional programming — `list.map(User::name)` is a property reference used as a function. (4) Reflection — inspect properties. Property references are compile-time safe — the compiler verifies the property exists.

9. **How do you create and process custom annotations in Kotlin?**
    - **Create**: `@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION) @Retention(AnnotationRetention.RUNTIME) annotation class Route(val path: String)`. Targets: CLASS, FUNCTION, PROPERTY, FIELD, CONSTRUCTOR, etc. Retention: SOURCE (compile-time only), BINARY (in class files but not runtime), RUNTIME (accessible via reflection). **Process at runtime**: `val route = MyClass::class.annotations.filterIsInstance<Route>().firstOrNull(); println(route?.path)`. **Process at compile time** (KSP/KAPT): create a `SymbolProcessor` that scans annotated elements and generates code. Use cases: (1) **Retrofit** — `@GET("users") suspend fun getUsers()`. (2) **Room** — `@Entity data class User`. (3) **Hilt** — `@Inject constructor()`. (4) **Custom validation** — `@MaxLength(50) val name: String`. (5) **Serialization** — `@SerialName("user_name") val userName: String`. Best practice: prefer compile-time processing (KSP) over runtime reflection — faster and type-safe. Use `@Retention(RUNTIME)` only when you need runtime access.

10. **What is the difference between `::class`, `::class.java`, and `::class.kotlin`?**
    - `::class` — returns `KClass<T>` (Kotlin reflection). Available on any type or expression: `String::class` or `"hello"::class`. Provides Kotlin-specific info: properties, functions, constructors, companion object, type parameters, nullability. Requires the `kotlin-reflect` library. `::class.java` — returns `Class<T>` (Java reflection). Available on any `KClass`. Provides Java-specific info: methods, fields, annotations, modifiers. No additional dependency needed. `::class.kotlin` — converts `Class<T>` back to `KClass<T>`. Use when you have a Java `Class` and need Kotlin reflection. Example: `val kClass: KClass<String> = String::class; val jClass: Class<String> = String::class.java; val backToKClass: KClass<String> = jClass.kotlin`. Prefer `::class` (Kotlin reflection) for Kotlin code — it's richer and knows about Kotlin features. Use `::class.java` when interfacing with Java libraries that require `Class<T>`.

11. **How does Kotlin reflection differ from Java reflection?**
    - **Kotlin reflection** (`kotlin.reflect`): (1) Knows about Kotlin features — nullable types, extension functions, data class properties, companion objects, sealed classes. (2) `KProperty` knows if a property is `val` or `var`, nullable, late-initialized. (3) `KFunction` knows about default parameters, suspend functions. (4) `KClass` knows about companion objects, object declarations. (5) Requires `kotlin-reflect` library (2MB+). **Java reflection** (`java.lang.reflect`): (1) Doesn't know Kotlin features — sees everything as Java. (2) Properties appear as getter/setter methods, not properties. (3) No concept of nullable types — all types are platform types. (4) No concept of `suspend` functions — they appear as regular methods with `Continuation` parameter. (5) Built into JDK — no dependency. Use Kotlin reflection when you need Kotlin-specific info (nullable types, properties, suspend functions). Use Java reflection when you need raw Java interop or want to avoid the `kotlin-reflect` dependency. Kotlin reflection is a wrapper over Java reflection — it adds Kotlin semantics on top.

12. **What are the best practices for using annotations and reflection in Kotlin?**
    - (1) **Prefer compile-time processing (KSP/KAPT) over runtime reflection** — faster, type-safe, no runtime overhead. (2) **Use `@Retention(RUNTIME)` only when necessary** — SOURCE/BINARY are lighter. (3) **Cache reflective lookups** — `KClass`, `KFunction` lookups are expensive. Store in `companion object`. (4) **Avoid reflection in performance-critical code** — use code generation instead. (5) **Use annotation targets** — `@Target` restricts where annotations can be used, preventing misuse. (6) **Document annotations** — explain what they do and how they're processed. (7) **Prefer Moshi codegen over Gson reflection** — Moshi's `@JsonClass(generateAdapter = true)` generates type-safe adapters at compile time. (8) **Use Kotlinx Serialization over reflection-based parsers** — compile-time safe, multiplatform. (9) **Minimize `kotlin-reflect` dependency** — it adds 2MB. Use Java reflection if you don't need Kotlin features. (10) **Test annotation processing** — verify that annotations are correctly processed at compile time and runtime. (11) **Use `@Repeatable` for multiple annotations of the same type** on a single target. (12) **Security** — don't use reflection on untrusted input (deserialization vulnerabilities).

---

## 🔗 Related Topics
- [Generics](Generics.md)
- [Inline Functions & Reified Types](../intermediate/InlineAndReified.md)
