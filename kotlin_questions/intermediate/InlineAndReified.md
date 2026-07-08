# Inline Functions & Reified Types

## 📖 Explanation

### `inline` Functions
When a function is marked `inline`, the compiler copies the function body (and any lambda parameters) directly to each call site. This eliminates the overhead of creating function objects and lambda allocations.

```kotlin
inline fun measureTime(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}
```

### Why Use `inline`?
1. **Performance**: No lambda object allocation — the lambda body is inlined.
2. **Non-local returns**: Lambdas passed to inline functions can use `return` to return from the calling function.
3. **Reified type parameters**: Only works with `inline` functions.

### `noinline`
Prevents a specific lambda from being inlined.

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    inlined()
    notInlined()
}
```

### `crossinline`
Allows the lambda to be inlined but prevents non-local returns. Used when the lambda is called from another context (e.g., a nested object).

```kotlin
inline fun runInThread(crossinline block: () -> Unit) {
    Thread { block() }.start()
}
```

### `reified` Type Parameters
Normally, generic type parameters are erased at runtime (type erasure). With `inline` + `reified`, the type parameter is available at runtime.

```kotlin
inline fun <reified T> isOfType(value: Any): Boolean = value is T
```

### Common Use Cases for `reified`
- Type checks: `value is T`
- Type casts: `value as T`
- Getting class: `T::class.java`
- Filtering by type: `filterIsInstance<T>()`
- Starting Android activities: `startActivity<MainActivity>()`

### `inline` Properties
Properties can also be inlined (getter/setter).

```kotlin
inline val <T> T.identity: T get() = this
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // inline function — no lambda allocation overhead
    val time = measureTime {
        repeat(1_000_000) { it * 2 }
    }
    println("Time: ${time}ms")

    // Non-local return — only works with inline functions
    val numbers = listOf(1, 2, 3, 4, 5)
    val firstEven = findFirstEven(numbers)
    println("First even: $firstEven")

    // reified — type check at runtime
    println("\n42 is Int: ${isOfType<Int>(42)}")
    println("42 is String: ${isOfType<String>(42)}")
    println("'hello' is String: ${isOfType<String>("hello")}")

    // reified — get class
    println("Class of Int: ${classOf<Int>()}")
    println("Class of String: ${classOf<String>()}")

    // reified — filterIsInstance
    val mixed: List<Any> = listOf(1, "two", 3, "four", 5)
    val strings = mixed.filterByType<String>()
    val ints = mixed.filterByType<Int>()
    println("\nStrings: $strings")
    println("Ints: $ints")

    // reified — parse JSON-like
    val json = """{"name":"Alice","age":30}"""
    val name = extractField<String>(json, "name")
    val age = extractField<Int>(json, "age")
    println("\nName: $name, Age: $age")

    // noinline
    withNoinline({ println("Inlined") }, { println("Not inlined") })

    // crossinline
    runInThread {
        println("Running in thread: ${Thread.currentThread().name}")
    }
    Thread.sleep(100)
}

// --- inline function ---
inline fun measureTime(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}

// --- Non-local return ---
inline fun findFirstEven(numbers: List<Int>): Int? {
    numbers.forEach {
        if (it % 2 == 0) return it  // non-local return from findFirstEven
    }
    return null
}

// --- reified type check ---
inline fun <reified T> isOfType(value: Any): Boolean = value is T

// --- reified class ---
inline fun <reified T> classOf(): Class<T> = T::class.java

// --- reified filter ---
inline fun <reified T> List<Any>.filterByType(): List<T> =
    filter { it is T }.map { it as T }

// --- reified JSON extraction (simplified) ---
inline fun <reified T> extractField(json: String, field: String): T {
    val pattern = """"$field"\s*:\s*"?([^,}]+)"?""".toRegex()
    val match = pattern.find(json)
    val value = match?.groupValues?.get(1)?.trim() ?: ""
    return when (T::class) {
        Int::class -> value.toInt() as T
        String::class -> value as T
        else -> throw IllegalArgumentException("Unsupported type")
    }
}

// --- noinline ---
inline fun withNoinline(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    inlined()
    notInlined()
}

// --- crossinline ---
inline fun runInThread(crossinline block: () -> Unit) {
    Thread { block() }.start()
}
```

### Output
```
Time: 15ms
First even: 2

42 is Int: true
42 is String: false
'hello' is String: true

Class of Int: int
Class of String: class java.lang.String

Strings: [two, four]
Ints: [1, 3, 5]

Name: Alice, Age: 30
Inlined
Not inlined
Running in thread: Thread-0
```

---

## ❓ Interview Questions

1. **What does `inline` do and when should you use it?**
   - `inline` copies the function body and lambda parameters to the call site, eliminating lambda object allocation. Use for performance-critical higher-order functions. Don't inline large functions (code bloat).

2. **What is a non-local return and why does it only work with inline functions?**
   - A `return` inside a lambda that returns from the enclosing function. It works with inline functions because the lambda body is copied into the calling function, so `return` naturally exits the caller.

3. **What is `reified` and why does it require `inline`?**
   - `reified` makes a generic type parameter available at runtime (normally erased). It requires `inline` because the compiler copies the function body and replaces `T` with the actual type at each call site.

4. **What is the difference between `noinline` and `crossinline`?**
   - `noinline` prevents a lambda from being inlined (stays as a function object). `crossinline` inlines the lambda but prevents non-local returns (used when the lambda is called from a different execution context).

5. **What are the downsides of marking everything `inline`?**
   - Code bloat (function body is copied everywhere), increased compilation time, and larger bytecode. Only inline small, performance-critical higher-order functions.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
- [Generics](../advanced/Generics.md)
