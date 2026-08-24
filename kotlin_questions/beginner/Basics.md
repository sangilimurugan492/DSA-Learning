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

6. **What is the difference between `fun main()` and `fun main(args: Array<String>)`?**
   - `fun main()` is the no-argument entry point — available since Kotlin 1.3, used when you don't need command-line arguments. `fun main(args: Array<String>)` is the traditional entry point that receives command-line arguments. Both are valid — the Kotlin compiler accepts either. Use `fun main()` for simple programs and scripts. Use `fun main(args: Array<String>)` when you need to read CLI arguments: `args[0]`, `args[1]`, etc. Behind the scenes, `fun main()` is expanded to `fun main(args: Array<String>)` with an empty array. In IntelliJ IDEA, you can configure run configurations to pass arguments to `args`.

7. **What is the difference between statements and expressions in Kotlin?**
   - In Kotlin, many constructs that are statements in Java are **expressions** (return a value). `if` is an expression: `val max = if (a > b) a else b`. `when` is an expression: `val result = when (x) { 1 -> "one"; else -> "other" }`. `try` is an expression: `val n = try { str.toInt() } catch (e: Exception) { 0 }`. This eliminates the need for Java's ternary operator (`? :`). However, `for`, `while`, and `do-while` are statements (return `Unit`). Assignments are also statements in Kotlin (not expressions like in Java) — `val x = (y = 5)` doesn't compile. This design reduces bugs from accidental assignment in conditions (`if (x = 5)` is a compile error, not a silent bug).

8. **What is the difference between `Unit` and `Nothing` in Kotlin?**
   - `Unit` is the equivalent of Java's `void` — it represents the return type of a function that returns no meaningful value. Unlike `void`, `Unit` is an actual type with a single instance (`Unit`). Functions without an explicit return type return `Unit`. `Nothing` is a special type that has no instances — a function returning `Nothing` never returns normally (it either throws an exception or runs forever). `Nothing` is the bottom of the type hierarchy — it's a subtype of every other type. Use cases for `Nothing`: `TODO()` function (throws `NotImplementedError`), `exitProcess()`, infinite loops. `Nothing?` is the type of `null`.

9. **What are top-level functions and properties in Kotlin?**
   - Kotlin allows functions and properties to be declared outside any class, directly in a file. These are called top-level declarations. Behind the scenes, the compiler generates a class (e.g., `FileNameKt`) containing these as static methods/fields. Top-level functions are useful for utility functions that don't belong to a specific class — like `println()`, `listOf()`, `measureTimeMillis()`. Example: `fun greet(name: String) = "Hello, $name"` can be called from anywhere with just `greet("Alice")`. You can control the generated class name with `@file:JvmName("Utils")` for better Java interop.

10. **What are Kotlin's key features that distinguish it from Java?**
    - (1) **Null safety** — nullable (`String?`) and non-nullable (`String`) types are distinguished at compile time. (2) **Extension functions** — add methods to existing classes without modification. (3) **Data classes** — auto-generate `equals`, `hashCode`, `toString`, `copy` with one keyword. (4) **Smart casts** — after `if (x is String)`, `x` is automatically cast. (5) **Coroutines** — lightweight threads for async programming. (6) **Scope functions** — `let`, `run`, `with`, `apply`, `also`. (7) **Default and named arguments** — reduces overloading. (8) **Sealed classes** — restricted hierarchies with exhaustive `when`. (9) **String templates** — `$variable` and `${expression}` in strings. (10) **No checked exceptions** — all exceptions are unchecked. (11) **Properties** — first-class, no need for explicit getters/setters.

11. **How does Kotlin compile and run?**
    - Kotlin source files (`.kt`) are compiled by the Kotlin compiler (`kotlinc`) into JVM bytecode (`.class` files), which run on the Java Virtual Machine (JVM). The compiler can also target JavaScript (Kotlin/JS) and native binaries (Kotlin/Native via LLVM). For Android, Kotlin compiles to JVM bytecode that runs on ART/Dalvik. Kotlin classes are compatible with Java classes — a Kotlin class can extend a Java class and vice versa. The Kotlin Standard Library (`kotlin-stdlib`) provides extensions and utilities. For coroutines, the `kotlinx-coroutines` library is needed. Kotlin's compiler is written in Kotlin itself (self-hosted).

12. **What is the Kotlin Standard Library and what does it provide?**
    - The Kotlin Standard Library (`kotlin-stdlib`) is the core library that provides essential functionality: (1) **Collection operations** — `map`, `filter`, `forEach`, `sortedBy`, `groupBy`, `flatMap`, etc. (2) **Scope functions** — `let`, `run`, `with`, `apply`, `also`. (3) **String utilities** — `trim()`, `split()`, `replace()`, `toIntOrNull()`, `padStart()`. (4) **IO utilities** — `File.readText()`, `File.writeText()`, `use { }` for auto-closing. (5) **Coroutines support** (via `kotlin-stdlib-jdk8` and `kotlinx-coroutines`). (6) **Reflection** (via `kotlin-reflect` — separate dependency). (7) **Extension functions** on standard Java types. (8) **Unsigned integer types** (`UInt`, `ULong`). The stdlib is automatically included in Kotlin projects. For Android, `kotlin-stdlib` is added via Gradle.

13. **What is a `companion object` in Kotlin?**
    - A `companion object` is a singleton object declared inside a class — it's Kotlin's replacement for Java's `static` members. Example: `class MyClass { companion object { const val MAX = 100; fun create() = MyClass() } }`. Access via `MyClass.MAX` or `MyClass.create()`. Unlike Java's `static`, a companion object is a real object — it can implement interfaces, be passed as a parameter, and have extension functions. Use `companion object` for factory methods, constants, and shared utilities. For Java interop, use `@JvmStatic` to make members true statics, and `const val` for compile-time constants. See [Variables & Data Types](VariablesAndDataTypes.md) for `const val` details.

14. **What is the `TODO()` function in Kotlin and what type does it return?**
    - `TODO()` is a built-in Kotlin function that throws `NotImplementedError` when called. It returns `Nothing` — the bottom type that is a subtype of everything. This means you can use it as a placeholder anywhere a value is expected: `fun calculateScore(): Int = TODO("Not yet implemented")` — the compiler accepts this because `Nothing` is a subtype of `Int`. It's useful during development for stubbing out functions you haven't implemented yet. Unlike Java's `// TODO` comments, `TODO()` fails fast at runtime if the code path is reached. There's also `TODO(reason: String)` for custom messages. The `Nothing` return type is key — it allows `TODO()` to be used in any context without type errors.

15. **What are Kotlin's platform targets (JVM, JS, Native) and how do they differ?**
    - Kotlin is a multiplatform language with three compilation targets: (1) **Kotlin/JVM** — compiles to JVM bytecode (`.class` files), runs on the JVM. Used for Android, server-side, and desktop apps. Full interop with Java libraries. Most mature target. (2) **Kotlin/JS** — compiles to JavaScript. Used for web frontend or Node.js backend. Can interop with JS libraries. (3) **Kotlin/Native** — compiles to native binaries via LLVM. Used for iOS, embedded systems, and native macOS/Linux/Windows apps. No JVM required. (4) **Kotlin Multiplatform (KMP)** — shares business logic across all targets while writing platform-specific UIs. Common code goes in `commonMain`, platform-specific code in `androidMain`, `iosMain`, etc. Not all Java libraries work on JS/Native — use Kotlin Multiplatform libraries instead.

---

## 🔗 Related Topics
- [Variables & Data Types](VariablesAndDataTypes.md)
- [Control Flow](ControlFlow.md)
