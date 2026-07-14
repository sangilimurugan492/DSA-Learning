# Exception Handling

## 📖 Explanation

Kotlin exception handling is similar to Java but with key differences: all exceptions are **unchecked** — there is no `throws` clause or checked exceptions.

### try-catch-finally
```kotlin
try {
    riskyOperation()
} catch (e: IOException) {
    println("IO error: ${e.message}")
} finally {
    cleanup()
}
```

### `try` is an Expression
```kotlin
val result = try { parseInt(input) } catch (e: NumberFormatException) { 0 }
```

### Throwing Exceptions
```kotlin
throw IllegalArgumentException("Invalid input")
```

### Custom Exceptions
```kotlin
class ValidationException(message: String) : Exception(message)
```

### `Nothing` Type
`throw` returns `Nothing` — a type that has no instances. It signals that execution never continues.

```kotlin
fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}
```

### `TODO()` Function
Returns `Nothing`. Useful for marking unimplemented code.

```kotlin
fun notDoneYet(): String = TODO("Implement later")
```

### Checked vs Unchecked
- Kotlin has **no checked exceptions**. All exceptions are unchecked.
- The `@Throws` annotation is for Java interop (so Java callers know to catch).

---

## 🧪 Code Example

```kotlin
fun main() {
    // Basic try-catch
    try {
        val result = divide(10, 0)
        println("Result: $result")
    } catch (e: ArithmeticException) {
        println("Caught: ${e.message}")
    } finally {
        println("Finally block executed")
    }

    // try as expression
    val num = try { "abc".toInt() } catch (e: NumberFormatException) { -1 }
    println("Parsed: $num")

    // Multiple catch blocks
    try {
        riskyOperation("error")
    } catch (e: IllegalArgumentException) {
        println("Illegal arg: ${e.message}")
    } catch (e: RuntimeException) {
        println("Runtime: ${e.message}")
    }

    // Custom exception
    try {
        validateAge(-5)
    } catch (e: ValidationException) {
        println("Validation failed: ${e.message}")
    }

    // Nothing type
    val name: String = getNameOrDefault(null)
    println("Name: $name")

    // Nested try-catch
    try {
        val data = loadConfig("missing.json")
    } catch (e: ConfigException) {
        println("Config error: ${e.message}")
        try {
            recover()
        } catch (e: Exception) {
            println("Recovery failed: ${e.message}")
        }
    }
}

fun divide(a: Int, b: Int): Int {
    if (b == 0) throw ArithmeticException("Division by zero")
    return a / b
}

fun riskyOperation(input: String): String {
    if (input == "error") throw IllegalArgumentException("Bad input: $input")
    return "OK"
}

class ValidationException(message: String) : Exception(message)

fun validateAge(age: Int) {
    if (age < 0) throw ValidationException("Age cannot be negative: $age")
    println("Age $age is valid")
}

// Nothing return type
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

fun getNameOrDefault(name: String?): String {
    if (name == null) fail("Name is required")  // returns Nothing
    return name
}

class ConfigException(message: String) : Exception(message)

fun loadConfig(path: String): String {
    throw ConfigException("File not found: $path")
}

fun recover() {
    println("Attempting recovery...")
}
```

### Output
```
Caught: Division by zero
Finally block executed
Parsed: -1
Illegal arg: Bad input: error
Validation failed: Age cannot be negative: -5
Name: null
Config error: File not found: missing.json
Recovery failed: Attempting recovery...
```

---

## ❓ Interview Questions

1. **Does Kotlin have checked exceptions?**
   - No. All exceptions in Kotlin are unchecked. There is no `throws` clause. The `@Throws` annotation exists only for Java interop.

2. **What is the `Nothing` type in Kotlin?**
   - `Nothing` is a type with no instances. Functions that never return (throw an exception or run forever) return `Nothing`. It's the bottom type in Kotlin's type hierarchy.

3. **Can `try` be used as an expression?**
   - Yes. `val result = try { parse() } catch (e: Exception) { defaultValue }`. The result is the last expression in the `try` or `catch` block.

4. **What is the difference between `Nothing` and `Unit`?**
   - `Unit` means the function completes and returns no meaningful value (like `void`). `Nothing` means the function never returns normally (throws or loops forever).

5. **How do you create a custom exception in Kotlin?**
   - Extend `Exception` or any of its subclasses: `class MyException(msg: String) : Exception(msg)`. Custom exceptions work the same as Java exceptions.

---

## 🔗 Related Topics
- [Control Flow](ControlFlow.md)
- [Null Safety](NullSafety.md)
