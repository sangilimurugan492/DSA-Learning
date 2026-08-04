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

6. **What is the difference between `throw` and `try-catch` in Kotlin?**
   - `throw` is an **expression** in Kotlin — it returns `Nothing`. This means it can be used in any context: `val result = if (input != null) parse(input) else throw IllegalArgumentException("Input is null")`. The type of `result` is the common supertype of `parse(input)` and `Nothing`, which is the return type of `parse`. `try-catch` is also an expression: `val result = try { parseInt(str) } catch (e: NumberFormatException) { -1 }`. The result is the last expression in the `try` block (or `catch` block if an exception occurs). Both `throw` and `try` being expressions is a significant difference from Java, where they are statements.

7. **What is the `Nothing` type and how is it used?**
   - `Nothing` is a special type that has **no instances** — it represents a computation that never completes normally (always throws or runs forever). Functions that always throw return `Nothing`: `fun fail(message: String): Nothing = throw IllegalArgumentException(message)`. The compiler knows that after `fail()` is called, the code is unreachable. `Nothing` is the **bottom type** — it's a subtype of every other type. This allows `Nothing?` to be the type of `null` and enables `throw` to be used in any expression. Use cases: (1) `fail()` function for unreachable code. (2) `TODO()` function returns `Nothing`. (3) Infinite loops: `while (true) { }` returns `Nothing`. (4) `emptyList<T>()` returns `List<Nothing>` which is a subtype of `List<T>` for any `T`.

8. **What is the difference between `Exception` and `Error` in Kotlin?**
   - Both extend `Throwable`. **`Exception`** — represents recoverable conditions in application code (e.g., `IOException`, `NullPointerException`, `IllegalArgumentException`). You should catch and handle these. **`Error`** — represents unrecoverable system-level problems (e.g., `OutOfMemoryError`, `StackOverflowError`). You should NOT catch these — the JVM is in an unstable state. Kotlin follows the same hierarchy as Java: `Throwable` → `Exception`/`Error`. `RuntimeException` (unchecked) extends `Exception`. All exceptions in Kotlin are unchecked — there's no `throws` clause. Use `@Throws` annotation only for Java interop (so Java callers know to catch).

9. **How do you use `try` as an expression in Kotlin?**
   - `try` is an expression that returns a value — the last expression in the `try` or `catch` block: `val result = try { riskyOperation() } catch (e: Exception) { defaultValue }`. If `riskyOperation()` succeeds, `result` is its return value. If it throws, `result` is `defaultValue`. The `finally` block (if present) doesn't affect the result. Example: `val number = try { str.toInt() } catch (e: NumberFormatException) { 0 }`. This replaces Java's verbose try-catch-assign pattern. You can also use `try` with `when`: `val result = when { str.isNotEmpty() -> try { str.toInt() } catch (e: Exception) { 0 } else -> 0 }`. Always handle the exception in the `catch` block — return a meaningful default.

10. **What is the `finally` block and when is it executed?**
    - `finally` executes **always** — whether an exception is thrown or not, whether it's caught or not. Use it for cleanup: closing files, releasing resources, restoring state. Example: `try { resource.open() } catch (e: Exception) { handleError(e) } finally { resource.close() }`. The `finally` block runs after `try` (no exception), after `catch` (exception caught), or before the exception propagates (exception not caught). `finally` does NOT execute if `System.exit()` is called or the JVM crashes. If `finally` throws an exception, it replaces the original exception. In Kotlin, prefer `use { }` for `Closeable`/`AutoCloseable` resources — it automatically handles `try-finally`: `resource.use { it.read() }`. This is cleaner and less error-prone than manual `finally`.

11. **What is the difference between `runCatching` and `try-catch`?**
    - `runCatching { }` is a Kotlin stdlib function that wraps a block in try-catch and returns a `Result<T>`: `val result = runCatching { riskyOperation() }; result.onSuccess { println(it) }.onFailure { println("Error: $it") }`. `Result<T>` is either `Success(value)` or `Failure(exception)`. Benefits of `runCatching`: (1) Functional style — chain with `map`, `mapCatching`, `recover`, `getOrNull`, `getOrDefault`, `getOrThrow`. (2) No need for try-catch boilerplate. (3) Explicit error handling — the type system forces you to handle both success and failure. Use `runCatching` for functional-style error handling. Use `try-catch` when you need specific exception types or when the block has side effects. Note: `Result<T>` should not be used as a return type for public APIs (per Kotlin docs) — use sealed classes like `Result.Success/Error` instead.

12. **How do you handle exceptions in coroutines?**
     - Coroutine exceptions are handled differently: (1) **`try-catch` inside the coroutine** — catches exceptions within the coroutine body: `viewModelScope.launch { try { riskyCall() } catch (e: Exception) { } }`. (2) **`CoroutineExceptionHandler`** — a context element for uncaught exceptions in `launch` (NOT `async`): `val handler = CoroutineExceptionHandler { _, e -> log(e) }; scope.launch(handler) { }`. (3) **`async`/`await`** — exceptions in `async` are stored in `Deferred` and thrown on `await()`: wrap `await()` in try-catch. (4) **`SupervisorJob`** — children fail independently. (5) **CancellationException** — special: thrown when a coroutine is cancelled. Don't catch it (or re-throw) — let structured concurrency handle it. Never catch `CancellationException` and swallow it — this breaks cancellation.

13. **What is the difference between `runCatching` and `Result<T>`?**
    - `runCatching { block }` is a convenience function that executes the block and wraps the result in a `Result<T>` — either `Result.success(value)` or `Result.failure(exception)`. `Result<T>` provides functional operators: `onSuccess { }`, `onFailure { }`, `map { }`, `mapCatching { }`, `recover { }`, `getOrNull()`, `getOrDefault()`, `getOrThrow()`, `isSuccess`, `isFailure`. This enables functional error handling without try-catch: `runCatching { api.fetch() }.map { it.data }.getOrDefault(emptyList())`. Note: `Result<T>` is not recommended as a return type for public APIs — use sealed classes like `sealed class Outcome<out T, out E>` for better type safety and explicit error types.

14. **How do you create a custom exception hierarchy in Kotlin?**
    - Extend `Exception` or its subclasses: `open class AppException(msg: String, cause: Throwable? = null) : Exception(msg, cause)`. Create subtypes: `class NetworkException(msg: String) : AppException(msg)`, `class DatabaseException(msg: String) : AppException(msg)`, `class ValidationException(field: String, msg: String) : AppException("Validation failed for $field: $msg")`. Use sealed classes for exhaustive handling: `sealed class AppError : Exception() { data class Network(val e: IOException) : AppError(); data class Validation(val field: String) : AppError() }`. In `catch`, handle each type differently: `catch (e: NetworkException) { ... } catch (e: ValidationException) { ... }`. Always include a meaningful message and optional cause.

15. **What is the difference between `@Throws` annotation and Java `throws` clause?**
    - Kotlin doesn't have checked exceptions — no `throws` clause in function signatures. The `@Throws` annotation is purely for Java interop: it tells the Kotlin compiler to add a `throws` clause to the generated JVM bytecode so Java callers know to catch the exception. Example: `@Throws(IOException::class) fun readFile(path: String): String { ... }`. In Java, this becomes `String readFile(String path) throws IOException`. Without `@Throws`, Java callers wouldn't know the function throws `IOException`, and the compiler wouldn't force them to handle it. Use `@Throws` when: (1) Your Kotlin function throws a checked exception (from Java interop). (2) You want Java callers to handle specific exceptions. Never use `@Throws` for `RuntimeException` subclasses — they're unchecked in Java too.

---

## 🔗 Related Topics
- [Control Flow](ControlFlow.md)
- [Null Safety](NullSafety.md)
