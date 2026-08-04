# Lambdas & Higher-Order Functions

## 📖 Explanation

Kotlin has first-class support for functional programming. Functions can be treated as values — passed, returned, and stored.

### Lambda Expression
An anonymous function defined with curly braces.

```kotlin
val square: (Int) -> Int = { x: Int -> x * x }
val result = square(5)  // 25
```

### `it` Parameter
If a lambda has a single parameter, it can be omitted and accessed via `it`.

```kotlin
val double: (Int) -> Int = { it * 2 }
```

### Higher-Order Functions
Functions that take other functions as parameters or return functions.

```kotlin
fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int = op(a, b)
operate(3, 4) { x, y -> x + y }  // 7
```

### Trailing Lambda
If the last parameter is a function, the lambda can be placed outside parentheses.

```kotlin
list.filter { it > 0 }
```

### Function Types
```kotlin
val sum: (Int, Int) -> Int = { a, b -> a + b }
val print: (String) -> Unit = { println(it) }
```

### `typealias` for Function Types
```kotlin
typealias MathOp = (Int, Int) -> Int
val add: MathOp = { a, b -> a + b }
```

### Returning a Function
```kotlin
fun multiplier(factor: Int): (Int) -> Int = { it * factor }
val triple = multiplier(3)
triple(5)  // 15
```

### `inline` Functions
Higher-order functions can be marked `inline` to eliminate lambda overhead by inlining the bytecode at the call site.

---

## 🧪 Code Example

```kotlin
fun main() {
    // Lambda with explicit types
    val square: (Int) -> Int = { x -> x * x }
    println("Square of 5: ${square(5)}")

    // Lambda with `it`
    val double = { x: Int -> x * 2 }
    println("Double of 7: ${double(7)}")

    // Higher-order function
    val result = operate(10, 3) { a, b -> a - b }
    println("10 - 3 = $result")

    // Using with collections
    val numbers = listOf(1, 2, 3, 4, 5)
    val evens = numbers.filter { it % 2 == 0 }
    val doubled = numbers.map { it * 2 }
    val sum = numbers.reduce { acc, n -> acc + n }
    println("Evens: $evens")
    println("Doubled: $doubled")
    println("Sum: $sum")

    // Returning a function (closure)
    val triple = multiplier(3)
    val quadruple = multiplier(4)
    println("Triple 5: ${triple(5)}")
    println("Quadruple 5: ${quadruple(5)}")

    // Function composition
    val addOne = { x: Int -> x + 1 }
    val timesTwo = { x: Int -> x * 2 }
    val composed = compose(timesTwo, addOne)  // (x + 1) * 2
    println("compose(addOne, timesTwo)(3) = ${composed(3)}")

    // typealias
    val add: MathOp = { a, b -> a + b }
    println("typealias add(2, 3) = ${add(2, 3)}")

    // forEach with index
    numbers.forEachIndexed { index, value ->
        println("  [$index] = $value")
    }
}

fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int = op(a, b)

fun multiplier(factor: Int): (Int) -> Int = { it * factor }

fun <T, U, R> compose(f: (U) -> R, g: (T) -> U): (T) -> R = { x -> f(g(x)) }

typealias MathOp = (Int, Int) -> Int
```

### Output
```
Square of 5: 25
Double of 7: 14
10 - 3 = 7
Evens: [2, 4]
Doubled: [2, 4, 6, 8, 10]
Sum: 15
Triple 5: 15
Quadruple 5: 20
compose(addOne, timesTwo)(3) = 8
typealias add(2, 3) = 5
  [0] = 1
  [1] = 2
  [2] = 3
  [3] = 4
  [4] = 5
```

---

## ❓ Interview Questions

1. **What is a lambda expression in Kotlin?**
   - An anonymous function enclosed in curly braces: `{ x: Int -> x * 2 }`. It can be stored in a variable, passed, or returned.

2. **What is `it` in Kotlin lambdas?**
   - Implicit name for a single parameter in a lambda. If the lambda has one parameter, you can omit the declaration and use `it`.

3. **What is a higher-order function?**
   - A function that takes one or more functions as parameters, or returns a function. E.g., `map`, `filter`, `operate`.

4. **What is a trailing lambda and why is it useful?**
   - If the last parameter is a function type, the lambda can be placed outside the parentheses. Improves readability: `list.filter { it > 0 }`.

5. **What does `inline` do and when should you use it?**
   - `inline` copies the function body and lambda to the call site, avoiding the overhead of creating function objects. Use for performance-critical higher-order functions.

6. **What are function types in Kotlin and how do you declare them?**
   - Function types describe the signature of a function. Syntax: `(parameters) -> ReturnType`. Examples: (1) `() -> Unit` — no params, returns Unit. (2) `(Int, String) -> Boolean` — takes Int and String, returns Boolean. (3) `(String) -> Int` — takes String, returns Int. (4) `((Int) -> Int) -> Int` — higher-order: takes a function, returns Int. Function types with receiver: `String.() -> Int` — equivalent to `(String) -> Int` but `this` refers to the String. Use `typealias` for readability: `typealias ClickHandler = (View) -> Unit`. Instantiate with lambdas: `val handler: (Int) -> Int = { it * 2 }`. Or method references: `val handler = String::length`. Function types are used as parameter types, return types, and property types. They're the foundation of Kotlin's functional programming support.

7. **What is `it` and when should you use it vs named parameters?**
   - `it` is the implicit name for a single-parameter lambda. Instead of `{ x -> x * 2 }`, you write `{ it * 2 }`. Use `it` when: (1) The lambda has exactly one parameter. (2) The meaning is obvious from context (`list.map { it.uppercase() }`). (3) The lambda is short (1-2 expressions). Use named parameters when: (1) The lambda has multiple parameters: `{ index, value -> }`. (2) The parameter name adds clarity: `{ user -> user.name.uppercase() }`. (3) The lambda is long or nested. (4) You need to use `it` for an inner lambda (avoid shadowing). Anti-pattern: nested lambdas both using `it`: `list.map { it.filter { it > 0 } }` — which `it` is which? Use named parameters for the outer: `list.map { item -> item.filter { it > 0 } }`. Rule: `it` for short, single-level lambdas; named parameters for clarity or nested lambdas.

8. **What are trailing lambda syntax and its rules?**
    - If the **last parameter** of a function is a function type, the lambda can be placed **outside** the parentheses: `list.fold(0) { acc, n -> acc + n }` instead of `list.fold(0, { acc, n -> acc + n })`. If the function has **only one** parameter and it's a function type, you can omit parentheses entirely: `run { println("Hi") }`. Rules: (1) Only the last parameter can be a trailing lambda. (2) If there are other parameters, they go in parentheses. (3) Multiple trailing lambdas are not supported (Kotlin 1.x) — use named arguments for additional lambdas: `func({ }) { }` or `func(other = { }) { }`. (4) Trailing lambdas improve readability significantly for DSLs, collection operations, and higher-order functions. This is why Kotlin code reads naturally: `users.filter { it.active }.map { it.name }.sortedBy { it }`.

9. **What is the difference between `->` in lambdas and `when`/`if`?**
    - In **lambdas**, `->` separates parameters from the body: `{ x: Int, y: Int -> x + y }`. Everything before `->` is the parameter list, everything after is the body. In **`when` expressions**, `->` separates the condition from the result: `when (x) { 1 -> "one"; 2 -> "two"; else -> "other" }`. In **function types**, `->` separates parameters from the return type: `(Int, String) -> Boolean`. In **destructuring in lambdas**, `->` separates the destructured params from the body: `map.forEach { (key, value) -> println("$key = $value") }`. The `->` is a general-purpose separator in Kotlin — its meaning depends on context. In all cases, the left side defines the "input" and the right side defines the "output" or "body."

10. **What are closures and how do they capture variables in Kotlin?**
    - A closure is a lambda that captures (accesses) variables from its enclosing scope. Kotlin lambdas can capture both `val` and `var`: `var count = 0; val increment = { count++ }; increment(); increment(); println(count)` → 2. Captured `var` variables are wrapped in a `Ref` object so the lambda can modify them. Key behaviors: (1) **Val capture** — the value is captured by reference (for objects) or by value (for primitives). (2) **Var capture** — the variable is wrapped in a `Ref<Int>` object, and the lambda holds a reference to it. (3) **Late binding** — the lambda sees the latest value of the captured variable at call time, not at capture time. (4) **Object capture** — captures the reference, so mutations to the object are visible. (5) **Memory** — captured variables keep the enclosing scope alive (potential leak in long-lived lambdas). Closures are essential for callbacks, event handlers, and functional programming patterns. Be careful with `var` capture in loops — all lambdas may see the last value.

11. **What are `inline`, `noinline`, and `crossinline` and when do you use each?**
    - **`inline`** — the function and its lambda parameters are inlined (copied to call site). Eliminates function object allocation and enables non-local returns. Use for performance-critical higher-order functions (`forEach`, `let`, `run`). **`noinline`** — prevents a specific lambda parameter from being inlined. The lambda stays as a function object. Use when the lambda is stored or passed to another function (can't be inlined): `inline fun foo(block1: () -> Unit, noinline block2: () -> Unit) { storedLambda = block2; block1() }`. **`crossinline`** — inlines the lambda but disables non-local returns. Use when the lambda is called from a different execution context (e.g., inside another lambda or a different thread): `inline fun runOnUiThread(crossinline block: () -> Unit) { handler.post { block() } }`. Without `crossinline`, the non-local return inside `block` would be ambiguous (it's called from `handler.post`'s lambda). Use `inline` by default, `noinline` for stored lambdas, `crossinline` for lambdas called from other lambdas/threads.

12. **What is the `suspend` modifier and how does it relate to higher-order functions?**
    - `suspend` marks a function or lambda as suspendable — it can pause and resume execution (coroutine). `suspend` functions can only be called from coroutines or other `suspend` functions. Suspend lambdas have type `suspend () -> T` — they can call `delay()`, other suspend functions, etc. Example: `suspend fun <T> withContext(dispatcher: CoroutineDispatcher, block: suspend () -> T): T`. The `block` parameter is a suspend lambda. Key points: (1) `suspend` lambdas are like regular lambdas but can suspend. (2) You cannot pass a regular lambda where a suspend lambda is expected (and vice versa). (3) `inline` functions with suspend lambdas work naturally — the inlining preserves the suspend context. (4) `suspend` functions are colored — they can only be called in suspend contexts. This is known as the "colored function problem." (5) `runBlocking` bridges regular and suspend worlds — but never use it in production Android code. Use `viewModelScope.launch` or `lifecycleScope.launch` to start coroutines.

---

## 🔗 Related Topics
- [Functions](../beginner/Functions.md)
- [Scope Functions](ScopeFunctions.md)
