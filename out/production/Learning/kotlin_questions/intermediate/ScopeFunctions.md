# Scope Functions

## 📖 Explanation

Kotlin provides five scope functions: `let`, `run`, `with`, `apply`, and `also`. They execute a block of code within the context of an object. The key differences are:

1. **Reference**: `this` (context object) vs `it` (argument)
2. **Return value**: Returns the object itself vs the lambda result

| Function | Object Reference | Return Value   | Typical Use                    |
|----------|-----------------|----------------|--------------------------------|
| `let`    | `it`            | Lambda result  | Null checks, transformations   |
| `run`    | `this`          | Lambda result  | Computation on object          |
| `with`   | `this`          | Lambda result  | Grouping calls on same object  |
| `apply`  | `this`          | Object itself  | Object configuration/setup     |
| `also`   | `it`            | Object itself  | Side effects, chaining         |

### `let`
```kotlin
val result = str?.let { it.uppercase() }
```

### `run`
```kotlin
val result = "Hello".run {
    length  // returns length
}
```

### `with`
```kotlin
val result = with(StringBuilder()) {
    append("A")
    append("B")
    toString()
}
```

### `apply`
```kotlin
val person = Person().apply {
    name = "Alice"
    age = 30
}
```

### `also`
```kotlin
val list = mutableListOf(1, 2, 3).also {
    println("Created: $it")
}
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // let — null check + transform
    val name: String? = "Kotlin"
    val upper = name?.let { it.uppercase() }
    println("let result: $upper")

    // run — compute on object
    val length = "Hello World".run {
        split(" ").size
    }
    println("run result (word count): $length")

    // with — group calls
    val result = with(StringBuilder()) {
        append("Kotlin ")
        append("is ")
        append("awesome!")
        toString()
    }
    println("with result: $result")

    // apply — configure object
    val config = ServerConfig().apply {
        host = "localhost"
        port = 8080
        timeout = 5000
    }
    println("apply result: $config")

    // also — side effect + chain
    val numbers = mutableListOf(1, 2, 3)
        .also { println("Before add: $it") }
        .apply { add(4) }
        .also { println("After add: $it") }
    println("Final list: $numbers")

    // Chaining scope functions
    val processed = "  Hello Kotlin  "
        .let { it.trim() }
        .also { println("Trimmed: '$it'") }
        .run { uppercase() }
        .also { println("Uppercased: $it") }
    println("Processed: '$processed'")
}

data class ServerConfig(
    var host: String = "",
    var port: Int = 0,
    var timeout: Int = 0
)
```

### Output
```
let result: KOTLIN
run result (word count): 2
with result: Kotlin is awesome!
apply result: ServerConfig(host=localhost, port=8080, timeout=5000)
Before add: [1, 2, 3]
After add: [1, 2, 3, 4]
Final list: [1, 2, 3, 4]
Trimmed: 'Hello Kotlin'
Uppercased: HELLO KOTLIN
Processed: 'HELLO KOTLIN'
```

---

## ❓ Interview Questions

1. **What are the five scope functions in Kotlin?**
   - `let`, `run`, `with`, `apply`, and `also`. They execute code in the context of an object.

2. **What is the difference between `apply` and `also`?**
   - `apply` uses `this` and returns the object. `also` uses `it` and returns the object. Use `apply` for configuration, `also` for side effects.

3. **When would you use `let`?**
   - For null checks (`obj?.let { ... }`) and to transform a value while keeping the scope limited.

4. **What is the difference between `run` and `with`?**
   - `run` is an extension function called on the object (`obj.run { }`). `with` takes the object as a parameter (`with(obj) { }`). Both use `this` and return the lambda result.

5. **Which scope function returns the object itself?**
   - `apply` and `also` return the context object. The others return the lambda result.

6. **What is the recommended convention for choosing between scope functions?**
   - Google/Kotlin conventions: (1) **`apply`** — for object configuration/initialization: `Button(context).apply { text = "Click"; setOnClickListener { } }`. (2) **`let`** — for null checks and transformations: `user?.let { println(it.name) }`. (3) **`run`** — for computations on an object: `val result = userRepository.run { fetchUser(id).toUIModel() }`. (4) **`with`** — for grouped calls on an object (not an extension): `with(textView) { text = "Hello"; textSize = 16f; visibility = VISIBLE }`. (5) **`also`** — for side effects (logging, debugging) without affecting the chain: `users.also { Log.d("TAG", "Users: ${it.size}") }.filter { it.active }`. Mnemonic: "also" = side effect, "apply" = configure, "let" = null-check, "run" = compute, "with" = group calls.

7. **How do scope functions interact with nullable types?**
   - `let` is the most useful for nullable types: `user?.let { greet(it) }` — the lambda only executes if `user` is non-null, and inside the lambda `it` is smart-cast to non-null. Without `let`, you'd need an explicit `if (user != null)`. `also` and `apply` also work with nullable receivers: `user?.apply { name = "New" }` — only configures if non-null. `run` with nullable: `user?.run { println(name) }` — same pattern. `with` does NOT work well with nullables because it takes the object as a parameter, not as a receiver extension. Best practice: use `let` for null-safe transformations, `apply` for null-safe configuration. Avoid chaining multiple `?.let` — it creates a "pyramid of doom." Use `?:` (elvis) or early returns instead.

8. **What happens when you chain multiple scope functions?**
   - Scope functions can be chained for fluent APIs. Example: `User().apply { name = "Alice" }.also { println("Created: $it") }.let { it.toDTO() }`. The key is understanding what each function returns: `apply`/`also` return the object (so you can chain), `let`/`run`/`with` return the lambda result (which may be a different type). Chaining order matters: `list.filter { it.active }.also { log(it) }.map { it.name }` — `also` receives the filtered list and passes it through unchanged. Be careful not to over-chain — it can reduce readability. Use scope functions to improve clarity, not to show off. If a chain is more than 3-4 functions, extract intermediate variables or use a separate function.

9. **What are scope function anti-patterns to avoid?**
   - (1) **Nesting scope functions** — `user?.let { it.apply { ... }.also { ... } }` — hard to read. Extract to a function instead. (2) **Using `apply` where `let` is better** — `user.apply { println(name) }` modifies nothing, use `also` for side effects or `let` for transformations. (3) **Shadowing `this` or `it`** — `with(obj1) { with(obj2) { /* which `this`? */ } }` — ambiguous. Use named parameters or `also`/`let` to use `it` instead. (4) **Using scope functions for simple assignments** — `val x = with(y) { z }` when `val x = y.z` suffices. (5) **Excessive chaining** — 5+ chained scope functions are unreadable. Break into steps. (6) **Using `with` on nullable** — `with(nullableObj) { ... }` doesn't handle null. Use `?.let` instead. (7) **Ignoring return values** — `user.let { saveUser(it) }` discards the return value of `saveUser`. Use `also` if you want to return the user.

10. **How do scope functions work under the hood?**
    - All scope functions are `inline` functions, meaning the compiler copies the lambda body to the call site — no function object allocation. `let` is essentially `inline fun <T, R> T.let(block: (T) -> R): R = block(this)`. `apply` is `inline fun <T> T.apply(block: T.() -> Unit): T { block(); return this }`. `also` is `inline fun <T> T.also(block: (T) -> Unit): T { block(this); return this }`. `run` is `inline fun <T, R> T.run(block: T.() -> R): R = block()`. `with` is `inline fun <T, R> with(receiver: T, block: T.() -> R): R = receiver.block()`. The `T.() -> R` is a lambda with receiver — `this` refers to `T`. The `(T) -> R` is a regular function type — the parameter is `it`. Because they're inline, there's zero runtime overhead — they're purely a convenience for code organization.

11. **What is the difference between `also` and `let` in terms of the lambda receiver?**
    - `also` uses `it` (the object is passed as an argument): `user.also { println(it.name) }`. The lambda is `(T) -> Unit`. `also` returns the object itself — it's for side effects. `let` also uses `it`: `user.let { it.name }`. The lambda is `(T) -> R`. `let` returns the lambda result — it's for transformation. The key difference is the return value: `also` always returns the original object (chainable), `let` returns whatever the lambda returns (transformative). Use `also` when you want to inspect/log the object without changing the chain: `users.filter { it.active }.also { log("Active: ${it.size}") }.map { it.name }`. Use `let` when you want to transform: `user?.let { it.toDTO() }`.

12. **Can you use scope functions with Kotlin DSLs? Give an example.**
    - Yes! `run` and `with` (which use lambda with receiver) are fundamental to DSL building. Example — a HTML builder DSL: `fun html(init: HTML.() -> Unit): HTML = HTML().apply(init)`. The `apply(init)` passes the receiver as `this` inside the init block. Another example — Gradle build scripts use `with`/`apply` extensively: `dependencies { implementation("...") }` — `dependencies` is a `DependencyHandler.() -> Unit` lambda. The scope function provides the receiver context. For testing DSLs: `assertThat(result) { isEqualTo(expected) }` — the lambda receives `result` as `this`. DSLs rely on the receiver pattern that `apply` and `run` use. The `@DslMarker` annotation prevents scope ambiguity in nested DSL blocks.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
- [Extensions](Extensions.md)
