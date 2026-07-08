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

---

## 🔗 Related Topics
- [Variables & Data Types](VariablesAndDataTypes.md)
- [Control Flow](ControlFlow.md)
