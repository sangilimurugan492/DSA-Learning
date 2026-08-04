# Kotlin Basics & Hello World

## 📖 Explanation

Kotlin is a modern, statically typed programming language that runs on the JVM. It is fully interoperable with Java and is the official language for Android development. Kotlin is concise, safe, and expressive.

### Key Characteristics
- **Concise**: Reduces boilerplate code significantly.
- **Safe**: Null safety is built into the type system.
- **Interoperable**: Can use Java libraries and frameworks seamlessly.
- **Tool-friendly**: Excellent IDE support (IntelliJ IDEA, Android Studio).

### `main` Function
The `main` function is the entry point of a Kotlin program. Unlike Java, you don't need a class wrapper.

```kotlin
// Simple Hello World
fun main() {
    println("Hello, World!")
}
```

### `main` with Arguments
```kotlin
fun main(args: Array<String>) {
    println("Arguments received: ${args.toList()}")
}
```

### Semicolons
Semicolons are **optional** in Kotlin. Use them only when separating multiple statements on the same line.

```kotlin
fun main() {
    println("Statement 1")
    println("Statement 2"); println("Statement 3")
}
```

---

## 🧪 Code Example

```kotlin
// A simple program demonstrating basic Kotlin syntax
fun main() {
    // Variable declarations
    val name = "Kotlin"       // Immutable (read-only)
    var version = 2.0         // Mutable

    println("Welcome to $name $version!")

    // Conditional expression
    val isAwesome = true
    if (isAwesome) {
        println("Kotlin is awesome!")
    }

    // Function call
    val sum = add(3, 5)
    println("3 + 5 = $sum")
}

// Simple function with return type inference
fun add(a: Int, b: Int) = a + b
```

### Output
```
Welcome to Kotlin 2.0!
Kotlin is awesome!
3 + 5 = 8
```

---

## ❓ Interview Questions

1. **What is Kotlin and how does it differ from Java?**
   - Kotlin is concise, null-safe, and supports functional programming. It reduces boilerplate (no `new` keyword, no getters/setters needed for data classes) and has extension functions.

2. **Why is Kotlin called a statically typed language?**
   - Variable types are checked at compile time, catching type errors early.

3. **What is the entry point of a Kotlin program?**
   - The `main()` function — `fun main()` or `fun main(args: Array<String>)`.

4. **Can Kotlin code run without a class?**
   - Yes. Top-level functions like `main()` don't need a wrapping class. Behind the scenes, the compiler generates a class automatically.

5. **Is Kotlin fully interoperable with Java?**
   - Yes. Kotlin can call Java code and vice versa. You can mix both in the same project.

6. **What is the difference between `val`, `var`, and `const val`?**
   - `val` — read-only reference (immutable binding). The reference can't be reassigned, but if it points to a mutable object (like `MutableList`), the object's contents can change. `var` — mutable reference, can be reassigned. `const val` — compile-time constant. Must be top-level or in a `companion object`/`object`, must be a primitive or `String`, and the value must be known at compile time. `const val` is inlined by the compiler — no field lookup at runtime. Use `const val` for true constants (`const val MAX_SIZE = 100`), `val` for runtime-computed values, and `var` only when mutation is necessary.

7. **What is the difference between `==` and `===` in Kotlin?**
   - `==` checks **structural equality** — it calls `equals()`. For data classes, this compares all properties. For `String`, it compares content (unlike Java's `==` which compares references). `==` is null-safe: `a == b` compiles to `if (a === null) b === null else a.equals(b)`. `===` checks **referential equality** — same object in memory (Java's `==`). Example: `listOf(1) == listOf(1)` → true (structural), `listOf(1) === listOf(1)` → false (different instances). Always use `==` for value comparison. Use `===` only when you need to check if two references point to the exact same object.

8. **What is type inference in Kotlin?**
   - Kotlin's compiler automatically determines the type of a variable from its initializer: `val name = "Alice"` infers `String`, `val count = 42` infers `Int`. You don't need to specify the type explicitly in most cases. Use explicit types when: (1) The type isn't obvious from context. (2) You want a wider type than inferred (e.g., `val list: List<Animal> = listOf(Dog())`). (3) For public API documentation. (4) When the inferred type is too specific (e.g., `val map: Map<K, V> = hashMapOf()` instead of `HashMap`). For local variables, prefer type inference (cleaner). For public APIs, prefer explicit types (better documentation).

9. **What is the `Any` type in Kotlin?**
   - `Any` is the root of Kotlin's type hierarchy — all non-nullable types inherit from `Any` (like `Object` in Java). `Any?` is the root of all types including nullable. `Any` has three methods: `equals()`, `hashCode()`, and `toString()`. When Kotlin code uses `Any`, it compiles to `Object` on the JVM. Unlike Java's `Object`, `Any` does NOT have `wait()`, `notify()`, `getClass()` — those are extension functions or require casting. `Unit` is the type for functions that return no meaningful value (like `void`). `Nothing` is the bottom type — a function returning `Nothing` never returns (throws or loops forever).

10. **What are Kotlin's key features that distinguish it from Java?**
    - (1) **Null safety** — nullable (`String?`) and non-nullable (`String`) types are distinguished at compile time. (2) **Extension functions** — add methods to existing classes without modification. (3) **Data classes** — auto-generate `equals`, `hashCode`, `toString`, `copy` with one keyword. (4) **Smart casts** — after `if (x is String)`, `x` is automatically cast. (5) **Coroutines** — lightweight threads for async programming. (6) **Scope functions** — `let`, `run`, `with`, `apply`, `also`. (7) **Default and named arguments** — reduces overloading. (8) **Sealed classes** — restricted hierarchies with exhaustive `when`. (9) **String templates** — `$variable` and `${expression}` in strings. (10) **No checked exceptions** — all exceptions are unchecked. (11) **Properties** — first-class, no need for explicit getters/setters.

11. **How does Kotlin compile and run?**
    - Kotlin source files (`.kt`) are compiled by the Kotlin compiler (`kotlinc`) into JVM bytecode (`.class` files), which run on the Java Virtual Machine (JVM). The compiler can also target JavaScript (Kotlin/JS) and native binaries (Kotlin/Native via LLVM). For Android, Kotlin compiles to JVM bytecode that runs on ART/Dalvik. Kotlin classes are compatible with Java classes — a Kotlin class can extend a Java class and vice versa. The Kotlin Standard Library (`kotlin-stdlib`) provides extensions and utilities. For coroutines, the `kotlinx-coroutines` library is needed. Kotlin's compiler is written in Kotlin itself (self-hosted).

12. **What is the Kotlin Standard Library and what does it provide?**
    - The Kotlin Standard Library (`kotlin-stdlib`) is the core library that provides essential functionality: (1) **Collection operations** — `map`, `filter`, `forEach`, `sortedBy`, `groupBy`, `flatMap`, etc. (2) **Scope functions** — `let`, `run`, `with`, `apply`, `also`. (3) **String utilities** — `trim()`, `split()`, `replace()`, `toIntOrNull()`, `padStart()`. (4) **IO utilities** — `File.readText()`, `File.writeText()`, `use { }` for auto-closing. (5) **Coroutines support** (via `kotlin-stdlib-jdk8` and `kotlinx-coroutines`). (6) **Reflection** (via `kotlin-reflect` — separate dependency). (7) **Extension functions** on standard Java types. (8) **Unsigned integer types** (`UInt`, `ULong`). The stdlib is automatically included in Kotlin projects. For Android, `kotlin-stdlib` is added via Gradle.

13. **What are top-level functions and properties in Kotlin?**
    - Kotlin allows functions and properties to be declared outside any class, directly in a file. These are called top-level declarations. Behind the scenes, the compiler generates a class (e.g., `FileNameKt`) containing these as static methods/fields. Top-level functions are useful for utility functions that don't belong to a specific class — like `println()`, `listOf()`, `measureTimeMillis()`. Example: `fun greet(name: String) = "Hello, $name"` can be called from anywhere with just `greet("Alice")`. You can control the generated class name with `@file:JvmName("Utils")` for better Java interop.

14. **What is the difference between `Unit` and `Nothing` in Kotlin?**
    - `Unit` is the equivalent of Java's `void` — it represents the return type of a function that returns no meaningful value. Unlike `void`, `Unit` is an actual type with a single instance (`Unit`). Functions without an explicit return type return `Unit`. `Nothing` is a special type that has no instances — a function returning `Nothing` never returns normally (it either throws an exception or runs forever). `Nothing` is the bottom of the type hierarchy — it's a subtype of every other type. Use cases for `Nothing`: `TODO()` function (throws `NotImplementedError`), `exitProcess()`, infinite loops. `Nothing?` is the type of `null`.

15. **What is the difference between `const val` and `val` in a `companion object`?**
    - `const val` is a compile-time constant — the value must be known at compile time (primitives or `String`), and it's inlined at every usage site. It's stored in the static initializer of the class. `val` in a companion object is a runtime constant — it can be computed at runtime, but the reference can't be changed. It's stored as a field on the companion object. Key differences: (1) `const val` must be a primitive or `String`; `val` can be any type. (2) `const val` is inlined — no field access overhead; `val` requires field access. (3) `const val` is accessible from Java as `ClassName.CONSTANT`; `val` requires `Companion` access unless `@JvmField` is used. Use `const val` for true constants like `const val MAX_RETRIES = 3`.

---

## 🔗 Related Topics
- [Variables & Data Types](VariablesAndDataTypes.md)
- [Control Flow](ControlFlow.md)
