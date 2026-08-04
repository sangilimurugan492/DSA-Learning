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

6. **How does `reified` enable runtime type checks that aren't possible with regular generics?**
   - Regular generics suffer from **type erasure** — `T` is erased at runtime, so you can't do `if (value is T)` or `T::class.java`. `reified` makes `T` available at runtime by replacing it with the actual type at each call site. Example: `inline fun <reified T> List<*>.filterByType(): List<T> = filter { it is T }`. Without `reified`, this would be impossible — `is T` is a compile error for regular generics. Other use cases: (1) `inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, T::class.java)`. (2) `inline fun <reified T> fragment(): T = fragmentManager.findFragmentByTag(T::class.java.name) as T`. (3) `inline fun <reified T> Activity.extra(key: String): T = intent.getParcelableExtra(key)`. (4) Starting activities: `inline fun <reified T : Activity> Context.startActivity() = startActivity(Intent(this, T::class.java))`. `reified` eliminates the need to pass `Class<T>` parameters.

7. **What is the difference between `noinline` and `crossinline`?**
   - **`noinline`** — prevents a specific lambda parameter from being inlined. The lambda remains a function object at runtime. Use when you need to store the lambda, pass it to a non-inline function, or return it. Example: `inline fun foo(block1: () -> Unit, noinline block2: () -> Unit) { storedLambda = block2; block1() }`. `block1` is inlined (no allocation), `block2` is a real function object. **`crossinline`** — the lambda IS inlined, but non-local returns are forbidden. Use when the lambda is called from a different execution context — inside another lambda or from another thread. Example: `inline fun runOnMainThread(crossinline block: () -> Unit) { handler.post { block() } }`. Without `crossinline`, a `return` inside `block` would try to return from `runOnMainThread` — but since `block` runs inside `handler.post`'s lambda (at a later time), the non-local return doesn't make sense. `crossinline` prevents this by making `return` a compile error inside the lambda. Summary: `noinline` = don't inline the lambda; `crossinline` = inline but no non-local returns.

8. **How does inlining affect lambda allocation and performance?**
   - Without `inline`: each lambda creates a function object (anonymous class instance) on the heap. For hot paths (tight loops, frequent callbacks), this causes GC pressure. With `inline`: the lambda body is **copied directly** into the calling function — no function object is created. The bytecode is larger but execution is faster (no object allocation, no virtual dispatch). Benchmark: `list.forEach { process(it) }` — without inline, each `forEach` call creates a lambda object; with inline (which `forEach` is), zero allocations. Performance gains: (1) No lambda object allocation. (2) No virtual method dispatch. (3) JIT can optimize the inlined code better. (4) Enables non-local returns and reified types. Downsides: (1) Code bloat — if the inline function is large and called from many places, the bytecode grows significantly. (2) Slower compilation. (3) Cannot be called from Java (reified). Only inline small, frequently-called higher-order functions.

9. **Can inline functions be `public`/`private`/`internal`? How does visibility affect inlining?**
    - Inline functions can have any visibility (`public`, `private`, `internal`, `protected`). However: (1) **`public` inline functions** can only access `public` members of the class — because the inlined code runs at the call site, which may be in a different module. You cannot access `private` or `internal` members from a `public` inline function. (2) **`private` inline functions** can access `private` members — because they can only be called from within the same file. (3) **`internal` inline functions** can access `internal` members — same module. (4) **`protected` inline functions** — same as `public` for access purposes (can't access `private`). If you try to access a `private` member from a `public` inline function, you get a compile error: "Cannot access 'x': it is private in 'MyClass'". Workaround: mark the member `internal` or make the function non-inline. This is a common gotcha when refactoring to inline.

10. **How do you use `reified` to start an Activity without passing `Class<T>`?**
    - Without `reified`: `fun <T : Activity> startActivity(context: Context, clazz: Class<T>) { context.startActivity(Intent(context, clazz)) }` — caller must pass `MainActivity::class.java`. With `reified`: `inline fun <reified T : Activity> Context.startActivity() { startActivity(Intent(this, T::class.java)) }` — caller just writes `startActivity<MainActivity>()`. The compiler replaces `T` with `MainActivity` at the call site. Other reified use cases: (1) `inline fun <reified T : Parcelable> Intent.extra(key: String): T? = getParcelableExtra(key)`. (2) `inline fun <reified T : ViewModel> Fragment.viewModel(): Lazy<T> = viewModels { defaultViewModelProviderFactory }`. (3) `inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, object : TypeToken<T>() {}.type)`. Reified makes generic APIs much cleaner by eliminating explicit `Class<T>` parameters.

11. **What are `public inline` function restrictions and the `@PublishedApi` annotation?**
    - `public` inline functions **cannot access `private` or `internal` members** because the inlined code is copied to call sites in other modules. This is a significant restriction. The `@PublishedApi` annotation solves this: it makes an `internal` member visible to inline functions as if it were `public`, while keeping it `internal` to the module. Example: `@PublishedApi internal var counter = 0; inline fun increment() { counter++ }`. Without `@PublishedApi`, the `inline fun` can't access `counter`. With it, the compiler allows the access because it treats the member as `public` for inlining purposes. However, `counter` remains `internal` — it can't be accessed from outside the module directly. Use `@PublishedApi` when: (1) You need an inline function to access internal state. (2) You want to hide implementation details from the public API while still using them in inline functions. The annotation doesn't change the actual visibility — it's a compiler hint for inline function access.

12. **What is the performance difference between inline and non-inline higher-order functions?**
    - Non-inline: (1) Each lambda parameter creates a `Function0`/`Function1` object on the heap. (2) The function is called via virtual dispatch. (3) For a loop like `list.forEach { }`, each iteration calls through the lambda object. Inline: (1) Zero lambda object allocation — the body is copied to the call site. (2) No virtual dispatch — direct code execution. (3) The compiler can optimize the inlined code with the surrounding code. Real-world impact: for `list.filter { it > 0 }.map { it * 2 }.sortedBy { it }`, with inline functions, there are zero lambda allocations — only the intermediate lists are allocated. Without inline, each operation creates 1 lambda object + the intermediate list. For 1000-element lists with 3 operations, that's 3 lambda objects (minor) but for tight loops called millions of times, the difference is significant. Kotlin's standard library marks ALL higher-order functions as `inline` for this reason. Always use `inline` for your own higher-order utility functions.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
- [Generics](../advanced/Generics.md)
