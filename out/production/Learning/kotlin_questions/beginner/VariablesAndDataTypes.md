# Variables & Data Types

## 📖 Explanation

Kotlin has two keywords for declaring variables:
- **`val`** — Read-only (immutable reference). Cannot be reassigned after initialization. Equivalent to Java's `final`.
- **`var`** — Mutable. Can be reassigned.

### Type Inference
Kotlin infers the type from the assigned value. You can also declare types explicitly.

```kotlin
val name = "Alice"        // Inferred as String
var age = 30              // Inferred as Int
val pi: Double = 3.14     // Explicit type
```

### Primitive Data Types
Kotlin does not have primitives in the language — everything is an object. However, the compiler optimizes to primitives where possible.

| Type      | Description                  | Example                  |
|-----------|------------------------------|--------------------------|
| `Byte`    | 8-bit integer                | `127`                    |
| `Short`   | 16-bit integer               | `32767`                  |
| `Int`     | 32-bit integer               | `2147483647`             |
| `Long`    | 64-bit integer               | `9223372036854775807L`   |
| `Float`   | 32-bit floating point        | `3.14f`                  |
| `Double`  | 64-bit floating point         | `3.14159265359`          |
| `Char`    | Single character             | `'A'`                    |
| `Boolean` | True or false                | `true`                   |
| `String`  | Text                         | `"Hello"`                |

### Type Conversion
Kotlin requires **explicit** conversions — no implicit widening like Java.

```kotlin
val i: Int = 10
val l: Long = i.toLong()      // Explicit conversion required
val d: Double = l.toDouble()
```

### `const` vs `val`
- `const val` — Compile-time constant. Must be top-level or in an object. Only `String` or primitive types.
- `val` — Runtime constant. Can be assigned at runtime.

---

## 🧪 Code Example

```kotlin
fun main() {
    // val (immutable)
    val language = "Kotlin"
    // language = "Java"  // ❌ Compilation error

    // var (mutable)
    var version = 1.0
    version = 2.0           // ✅ Allowed
    println("$language $version")

    // All basic types
    val byteVal: Byte = 1
    val intVal: Int = 100
    val longVal: Long = 9999999999L
    val floatVal: Float = 3.14f
    val doubleVal: Double = 3.14159265359
    val charVal: Char = 'K'
    val boolVal: Boolean = true
    val stringVal: String = "Kotlin"

    println("Byte=$byteVal, Int=$intVal, Long=$longVal")
    println("Float=$floatVal, Double=$doubleVal")
    println("Char=$charVal, Boolean=$boolVal, String=$stringVal")

    // Type conversion
    val num = 42
    val numAsLong = num.toLong()
    val numAsString = num.toString()
    println("Converted: Long=$numAsLong, String=$numAsString")

    // String to number
    val parsed = "123".toInt()
    println("Parsed: $parsed")
}
```

### Output
```
Kotlin 2.0
Byte=1, Int=100, Long=9999999999
Float=3.14, Double=3.14159265359
Char=K, Boolean=true, String=Kotlin
Converted: Long=42, String=42
Parsed: 123
```

---

## ❓ Interview Questions

1. **What is the difference between `val` and `var`?**
   - `val` is read-only — the reference cannot be reassigned after initialization (similar to Java's `final`). `var` is mutable — the reference can be reassigned. Use `val` by default for safety and predictability; only use `var` when mutation is truly needed. This is a key Kotlin best practice that reduces bugs from accidental reassignment. Note: `val` means the *reference* is immutable, not the object it points to — a `val` pointing to a `MutableList` can still have elements added/removed.

2. **Is `val` truly immutable?**
   - No, `val` only makes the *reference* immutable (cannot reassign the variable). If it points to a mutable object (e.g., `MutableList`, `MutableMap`), the object's contents can still change. Example: `val list = mutableListOf(1, 2); list.add(3)` — this compiles fine. For true immutability, use read-only collection types (`List`, `Map`) or libraries like Kotlinx Collections Immutable (`persistentListOf`). The distinction is: `val` = immutable binding, not immutable data. This is similar to Java's `final` — `final List<Integer> list` doesn't prevent `list.add()`.

3. **What is the difference between `const val` and `val`?**
   - `const val` is a **compile-time constant** — the value is inlined at compile time. Restrictions: must be top-level or in an `object`/`companion object`, must be a primitive type (`Int`, `Long`, `Double`, etc.) or `String`, and the value must be known at compile time. `val` is a **runtime constant** — the value is assigned at runtime (e.g., from a function call, database, or calculation). `const val` has better performance (no field lookup at runtime) and can be used in annotations. Use `const val` for true constants like `const val MAX_RETRIES = 3`. Use `val` for values computed at runtime like `val currentTime = System.currentTimeMillis()`.

4. **Does Kotlin have primitive types?**
   - At the language level, **everything is an object** — `Int`, `Long`, `Double`, `Boolean`, etc. are classes with methods (e.g., `42.toString()`). However, the Kotlin compiler optimizes to JVM primitives where possible: `Int` → `int`, `Double` → `double`, `Boolean` → `boolean`. This optimization happens when the type is non-nullable and not used in generics. When a primitive type is boxed (e.g., `Int?` or `List<Int>`), it uses the Java wrapper classes (`java.lang.Integer`). This is why `Int?` has more overhead than `Int`. The type `Any` is the root of the Kotlin type hierarchy (like `Object` in Java).

5. **Why does Kotlin require explicit type conversions?**
   - To prevent silent data loss and make conversions intentional. In Java, implicit widening conversions (e.g., `int` to `long`) can hide bugs. Kotlin forces you to write `val l: Long = i.toLong()` instead of `val l: Long = i`. This makes the conversion explicit and visible. Kotlin provides conversion functions for all types: `toInt()`, `toLong()`, `toDouble()`, `toFloat()`, `toChar()`, `toString()`, `toByte()`, `toShort()`. Note: even `Int` to `Long` requires explicit conversion, unlike Java. This is a deliberate design choice for safety — every conversion is a conscious decision by the developer.

6. **What is the difference between `Any` and `Unit` in Kotlin?**
   - `Any` is the root of the Kotlin type hierarchy — all non-nullable types inherit from it (like `Object` in Java). `Any?` is the root of all types including nullable. `Unit` is a type with exactly one instance (`Unit`) — it's used as the return type of functions that don't return anything meaningful (like `void` in Java). The difference: `Unit` is a type with a value, `void` is not. This allows functions to return `Unit` generically (e.g., functional interfaces). `Nothing` is a type that has no instances — it represents a function that never returns (infinite loop, always throws exception).

7. **What is type inference in Kotlin and when should you use explicit types?**
   - Type inference means the compiler determines the type from the assigned value: `val name = "Alice"` infers `String`. Use explicit types when: (1) The type isn't obvious from context (e.g., `val result: Result<User> = api.fetch()`). (2) You want a wider type than inferred (e.g., `val list: List<Animal> = listOf(Dog())`). (3) For public API documentation in libraries. (4) When the inferred type is too specific (e.g., `val map = hashMapOf(...)` infers `HashMap` but you want `Map`). For local variables, prefer inference (cleaner code). For public APIs, prefer explicit types (better documentation).

8. **What are unsigned integer types in Kotlin and when would you use them?**
   - Kotlin provides unsigned types: `UByte`, `UShort`, `UInt`, `ULong` (experimental, marked with `@ExperimentalUnsignedTypes`). They represent non-negative integers: `UInt` range is 0 to 4,294,967,295 (double the positive range of `Int`). Use cases: (1) Bit manipulation where sign doesn't matter. (2) Interop with C/C++ unsigned types. (3) When you need the full positive range of a 32-bit integer. They're still experimental — use with caution. Operations: `val a: UInt = 42u; val b = a + 1u`. The `u` suffix denotes unsigned literals. Unsigned types don't have the same operator support as signed types.

9. **What is the difference between `==` and `===` in Kotlin?**
   - `==` checks **structural equality** — calls `equals()` method. For data classes, this compares all properties. For `String`, it compares content (unlike Java's `==` which compares references). `===` checks **referential equality** — same object in memory (like Java's `==`). Example: `listOf(1) == listOf(1)` is `true` (structural), but `listOf(1) === listOf(1)` is `false` (different instances). For data classes, `==` is equivalent to `equals()` which compares all properties. Always use `==` for value comparison — Kotlin fixed Java's biggest gotcha where `==` compared references instead of values.

10. **What is `lateinit` and how does it differ from regular initialization?**
    - `lateinit` allows deferring initialization of a non-nullable `var` property. It's a promise that the property will be initialized before first use. Use cases: (1) Dependency injection (Hilt/Dagger injects after construction). (2) Android View bindings (initialized in `onCreate`). (3) Test setup. Restrictions: must be `var` (not `val`), must be non-nullable, must be an object type (not primitives like `Int`). Accessing before initialization throws `UninitializedPropertyAccessException`. Check with `::property.isInitialized`. Unlike `lazy`, `lateinit` doesn't compute the value — it's set externally. Use `lateinit` when you can't provide a default value and initialization happens in a lifecycle callback.

11. **What is `lazy` initialization and how does it work?**
    - `lazy` is a delegate that initializes a `val` only on first access — thread-safe by default. Example: `val database by lazy { Room.databaseBuilder(context).build() }`. The lambda runs only once, on first access, and the result is cached. Benefits: (1) Deferred initialization — saves startup time. (2) Thread-safe by default (`LazyThreadSafetyMode.SYNCHRONIZED`). (3) Can specify `LazyThreadSafetyMode.NONE` for single-threaded contexts. Use `lazy` for expensive objects that may not always be needed. Unlike `lateinit`, `lazy` is for `val` (immutable) and computes the value itself. The value is computed once and cached — subsequent accesses return the cached value.

12. **What is the difference between `String` and `StringBuilder` in Kotlin?**
     - `String` is immutable — every modification creates a new `String` object. `StringBuilder` is mutable — modifications happen in-place. Use `StringBuilder` for heavy string concatenation (loops, building large strings) to avoid creating many intermediate `String` objects. Kotlin's `String` has extension functions like `plus()`, `replace()`, `split()` that return new strings. For simple concatenation, Kotlin's string templates (`"Hello, $name!"`) are efficient enough. For loops building strings, use `buildString { append("...") }` which uses `StringBuilder` internally. `StringBuilder` is from `kotlin.text` and is an alias for `java.lang.StringBuilder`.

13. **What are `const` constants and where can they be declared?**
    - `const val` declares a compile-time constant. Requirements: (1) Must be a primitive type (`Int`, `Long`, `Double`, `Float`, `Boolean`, `Byte`, `Short`, `Char`) or `String`. (2) Must be known at compile time. (3) Can only be declared at the top level, in an `object`, or in a `companion object`. (4) Cannot be a local variable. `const val` values are inlined at every usage — no runtime field access. This makes them slightly faster and usable in annotations. Example: `companion object { const val MAX_RETRIES = 3; const val BASE_URL = "https://api.example.com" }`. From Java, access as `MyClass.MAX_RETRIES` (static field). Use `const val` for true constants; use `val` for runtime-computed values.

14. **What is the difference between `Int` and `Int?` in terms of memory?**
    - `Int` (non-nullable) is compiled to a JVM primitive `int` when possible — 4 bytes, no object overhead. `Int?` (nullable) requires boxing — it's compiled to `java.lang.Integer` (an object), which takes 16+ bytes (object header + int value + padding) on 64-bit JVMs. This difference matters in: (1) Collections — `List<Int>` boxes each element (use `IntArray` instead). (2) Generic functions — `fun <T> process(t: T)` boxes primitives. (3) Fields — `var x: Int = 0` is efficient; `var x: Int? = null` has boxing overhead. The Kotlin compiler optimizes non-nullable primitives to JVM primitives wherever possible, but nullable primitives and generic contexts force boxing.

15. **What is the `typealias` keyword and when do you use it?**
    - `typealias` creates an alternative name (alias) for an existing type. It does NOT create a new type — both names are interchangeable. Use cases: (1) **Simplifying complex generic types**: `typealias UserMap = Map<String, List<User>>`. (2) **Function type aliases**: `typealias ClickHandler = (View) -> Boolean`. (3) **Intent documentation**: `typealias UserId = String` makes it clear a `String` represents a user ID. (4) **Platform-specific aliases**: `typealias Date = java.util.Date`. Unlike `type` in Swift, Kotlin's `typealias` doesn't provide type safety — `UserId` and `String` are the same type, so you can pass a `String` where a `UserId` is expected. Use `value class` (inline class) if you need type safety: `@JvmInline value class UserId(val value: String)`.

---

## 🔗 Related Topics
- [Basics & Hello World](Basics.md)
- [Control Flow](ControlFlow.md)
