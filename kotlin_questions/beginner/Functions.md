# Functions

## 📖 Explanation

Functions are first-class citizens in Kotlin. They can be assigned to variables, passed as arguments, and returned from other functions.

### Function Declaration
```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}
```

### Single-Expression Function
When a function returns a single expression, curly braces and `return` can be omitted.
```kotlin
fun add(a: Int, b: Int) = a + b
```

### Default Arguments
```kotlin
fun greet(name: String = "World") = "Hello, $name!"
```

### Named Arguments
```kotlin
fun power(base: Int, exponent: Int) = base.toDouble().pow(exponent).toInt()
power(exponent = 3, base = 2)  // Order doesn't matter
```

### Vararg (Variable Number of Arguments)
```kotlin
fun sum(vararg numbers: Int): Int = numbers.sum()
sum(1, 2, 3, 4)  // Returns 10
```

### Local Functions
Functions can be defined inside other functions.
```kotlin
fun outer() {
    fun inner() { println("Inside inner") }
    inner()
}
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // Basic function call
    println("add(3, 5) = ${add(3, 5)}")

    // Single-expression function
    println("multiply(4, 6) = ${multiply(4, 6)}")

    // Default argument
    println(greet())
    println(greet("Kotlin"))

    // Named arguments
    println("power(2, 3) = ${power(base = 2, exponent = 3)}")

    // Vararg
    println("sum(1,2,3,4,5) = ${sum(1, 2, 3, 4, 5)}")

    // Spread operator
    val nums = intArrayOf(10, 20, 30)
    println("sum(*nums) = ${sum(*nums)}")

    // Local function
    processOrder("ORD-001")
}

fun add(a: Int, b: Int): Int {
    return a + b
}

fun multiply(a: Int, b: Int) = a * b

fun greet(name: String = "World") = "Hello, $name!"

fun power(base: Int, exponent: Int): Int {
    var result = 1
    repeat(exponent) { result *= base }
    return result
}

fun sum(vararg numbers: Int): Int = numbers.sum()

fun processOrder(orderId: String) {
    fun validate() = println("Validating order $orderId...")
    fun save() = println("Saving order $orderId...")
    validate()
    save()
    println("Order $orderId processed!")
}
```

### Output
```
add(3, 5) = 8
multiply(4, 6) = 24
Hello, World!
Hello, Kotlin!
power(2, 3) = 8
sum(1,2,3,4,5) = 15
sum(*nums) = 60
Validating order ORD-001...
Saving order ORD-001...
Order ORD-001 processed!
```

---

## ❓ Interview Questions

1. **What is a single-expression function in Kotlin?**
   - A single-expression function has a body that is a single expression. It uses `=` instead of curly braces and omits the `return` keyword: `fun square(x: Int) = x * x`. The return type is inferred from the expression. Benefits: (1) More concise — no braces or `return`. (2) Return type can be omitted (inferred). (3) Clearer intent — the function is a pure mapping. Use for simple transformations, calculations, and delegations. For complex logic with multiple statements, use the regular block body with `{ }` and `return`. Example: `fun isEven(n: Int) = n % 2 == 0` — the return type `Boolean` is inferred. Single-expression functions are common in Kotlin standard library: `fun String.isBlank() = this.trim().isEmpty()`.

2. **What are default and named arguments? How do they reduce boilerplate?**
   - **Default arguments** provide fallback values for parameters — if the caller doesn't provide a value, the default is used: `fun greet(name: String = "World") = "Hello, $name!"`. Calling `greet()` returns "Hello, World!". This eliminates the need for overloaded methods (Java requires separate methods for each combination). **Named arguments** allow calling parameters by name in any order: `fun power(base: Int, exponent: Int)` can be called as `power(exponent = 3, base = 2)`. Benefits: (1) No overloads needed — one function with defaults replaces 3-4 overloads. (2) Self-documenting calls — `copy(name = "Alice", age = 30)` is clearer than `copy("Alice", 30)`. (3) Flexible API — callers only specify what they need. Note: when mixing positional and named args, positional must come first.

3. **What is `vararg` and how do you pass an array to it?**
   - `vararg` allows a function to accept a **variable number of arguments** of the same type. Inside the function, the parameter is accessed as an array: `fun sum(vararg numbers: Int): Int = numbers.sum()`. Call with individual values: `sum(1, 2, 3, 4)`. To pass an existing array, use the **spread operator** `*`: `val arr = intArrayOf(1, 2, 3); sum(*arr)`. The spread operator unpacks the array into individual arguments. You can also combine: `sum(0, *arr, 5)`. Only one `vararg` parameter per function, and it must be the last parameter (unless using named arguments for subsequent params). `vararg` is implemented as an array under the hood — `numbers` is `IntArray`. Common in standard library: `listOf()`, `setOf()`, `arrayOf()` all use `vararg`.

4. **Can Kotlin functions be nested?**
   - Yes. Kotlin supports **local functions** — functions defined inside other functions. They can access variables from the enclosing function (closures). Example:
     ```kotlin
     fun processOrder(orderId: String) {
         fun validate() = println("Validating $orderId")
         fun save() = println("Saving $orderId")
         validate()
         save()
     }
     ```
     Use cases: (1) Extracting helper logic that's only used in one function — avoids polluting class scope. (2) Encapsulation — the inner function is invisible outside. (3) Closures — inner functions can capture and modify outer variables. Local functions can themselves contain local functions (nesting). They can be recursive. They can't be `private`/`public` (visibility is implicitly local). Use when a helper is only needed in one place — if it's needed in multiple functions, make it a member or top-level function.

5. **What is the difference between `inline` functions and regular functions?**
   - `inline` functions have their bytecode **copied to the call site** instead of being called via a function call. This eliminates the overhead of lambda object allocation and function call overhead. Example: `inline fun measure(block: () -> Unit) { block() }` — the lambda body is inlined at the call site, no lambda object is created. Benefits: (1) No lambda allocation — significant for hot paths. (2) Non-local returns — lambdas in inline functions can use `return` to exit the enclosing function. (3) Reified type parameters — `inline fun <reified T> typeOf() = T::class.java`. Drawbacks: (1) Larger bytecode — each call site gets a copy. (2) Can't be recursive (infinite inlining). Use `inline` for higher-order functions called frequently (like `forEach`, `let`, `run`). Don't inline large functions. Use `noinline` to prevent specific lambda parameters from being inlined.

6. **What are extension functions in Kotlin?**
   - Extension functions allow you to add methods to existing classes **without modifying them** or inheriting from them. Example: `fun String.isEmail(): Boolean = this.contains("@")`. Now `"test@test.com".isEmail()` works. They're resolved **statically** — the compiler replaces the call with a static function call. They don't actually modify the class — it's syntactic sugar. Use cases: (1) Adding utility methods to third-party classes. (2) Android View extensions (`View.visible()`, `View.gone()`). (3) Improving readability. Restrictions: (1) Can't access private members. (2) Can be shadowed by member functions with the same signature. (3) Are resolved at compile time (no polymorphism). Extension properties also exist: `val String.firstChar get() = this[0]`.

7. **What are higher-order functions in Kotlin?**
   - Higher-order functions are functions that **take other functions as parameters** or **return functions**. Example: `fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int = op(a, b)`. Call with a lambda: `operate(3, 5) { x, y -> x + y }`. This is the foundation of functional programming in Kotlin. Common examples: `list.map { it * 2 }`, `list.filter { it > 0 }`, `list.forEach { println(it) }`. The function type `(Int, Int) -> Int` means "a function that takes two Ints and returns an Int". Higher-order functions enable: (1) Strategy pattern without classes. (2) Callbacks. (3) Collection transformations. (4) DSL building. Use `inline` for performance with higher-order functions to avoid lambda allocation.

8. **What are function types in Kotlin?**
   - Function types describe the signature of a function. Syntax: `(ParamType1, ParamType2) -> ReturnType`. Examples: `(Int, Int) -> Int` (takes two Ints, returns Int), `() -> Unit` (no params, no return), `(String) -> Boolean` (takes String, returns Boolean). You can store functions in variables: `val checker: (String) -> Boolean = { it.isNotEmpty() }`. Function types can have named parameters for documentation: `(name: String, age: Int) -> String`. Nullable return: `(Int) -> Int?`. Nullable function: `((Int) -> Int)?`. You can use type aliases: `typealias Predicate<T> = (T) -> Boolean`. Function types are implemented as interfaces (`Function1<P1, R>`, `Function2<P1, P2, R>`, etc.) — each lambda is an object implementing these interfaces.

9. **What is the difference between `return`, `return@label`, and `return@function`?**
   - `return` — returns from the **enclosing function** (not just the lambda). In a regular function, it exits the function. In a lambda inside a non-inline function, it returns from the enclosing function. `return@label` — returns from the **labeled lambda** (local return). Example: `list.forEach { if (it == 0) return@forEach; println(it) }` — skips the current iteration, continues to next. `return@functionName` — same as `return@label` but uses the function name as the implicit label. In `inline` functions, `return` inside a lambda does a **non-local return** — exits the enclosing function. This is only possible with `inline` functions. Use `return@forEach` to skip (like `continue`). Use `return` to exit the function entirely.

10. **What are tail-recursive functions in Kotlin?**
    - A tail-recursive function is one where the recursive call is the **last operation** in the function. Kotlin optimizes this to a loop (no stack overflow). Mark with `tailrec`: `tailrec fun factorial(n: Long, acc: Long = 1): Long = if (n <= 1) acc else factorial(n - 1, acc * n)`. The compiler transforms this into a `while` loop — no stack frames are added. Requirements: (1) The recursive call must be the last operation (no computation after it). (2) Must be marked `tailrec`. (3) The compiler verifies tail position — if not, it warns. Benefits: (1) No `StackOverflowError` for deep recursion. (2) Same performance as a loop. Use cases: tree traversal, factorial, Fibonacci, list processing. If the recursion isn't in tail position, refactor to use an accumulator parameter.

11. **What is the difference between `infix` functions and regular functions?**
    - `infix` functions allow calling a function **without the dot and parentheses**: `a shl b` instead of `a.shl(b)`. Requirements: (1) Must be a member function or extension function. (2) Must have exactly one parameter. (3) Must be marked `infix`. Example: `infix fun Int.shl(bitCount: Int): Int = this shl bitCount` — wait, `shl` is already infix. Better example: `infix fun String.times(n: Int): String = this.repeat(n)` — call as `"ab" times 3`. Built-in infix functions: `to` (`1 to "one"`), `in` (`x in list`), `step` (`1..10 step 2`), `until` (`1 until 5`). Use infix for: (1) DSL-like syntax. (2) Mathematical operations. (3) Readable domain-specific code. Don't overuse — regular function calls are clearer for complex logic.

12. **What are reified type parameters and how do they work?**
     - `reified` allows you to access the actual type argument at runtime inside an `inline` function. Normally, generics are erased at runtime (type erasure) — you can't check `if (item is T)`. With `reified`: `inline fun <reified T> typeOf() = T::class.java`. Now `typeOf<String>()` returns `String::class.java`. Requirements: (1) Function must be `inline`. (2) Type parameter must be marked `reified`. Use cases: (1) Type checking: `inline fun <reified T> Any.isInstanceOf() = this is T`. (2) Starting Activities: `inline fun <reified T : Activity> Context.startActivity() = startActivity(Intent(this, T::class.java))`. (3) Gson/Moshi parsing: `inline fun <reified T> fromJson(json: String) = gson.fromJson(json, T::class.java)`. Reified types eliminate the need to pass `Class<T>` parameters.

13. **What are default arguments and how do they reduce overloading?**
    - Kotlin supports default arguments — a parameter can have a default value: `fun greet(name: String, greeting: String = "Hello", punct: String = "!")`. Call with: `greet("Alice")`, `greet("Alice", "Hi")`, `greet("Alice", punct = "?")`, or `greet("Alice", punct = "!", greeting = "Hi")`. This replaces Java's method overloading where you'd need multiple methods: `greet(name)`, `greet(name, greeting)`, `greet(name, greeting, punct)`. Named arguments allow skipping parameters — you can provide `punct` without `greeting` by using `greet("Alice", punct = "?")`. The compiler generates a synthetic method with a bitmask for Java interop. Use `@JvmOverloads` to generate the overloaded methods for Java callers.

14. **What is the difference between `crossinline` and `noinline`?**
    - Both modify lambda parameters in `inline` functions. `noinline` — prevents the lambda from being inlined; it's treated as a regular function object. Use when you need to store the lambda or pass it to a non-inline function: `inline fun foo(noinline f: () -> Unit) { storedLambda = f }`. `crossinline` — the lambda IS inlined but cannot use non-local returns (`return`). Use when the lambda is called from another lambda or nested context where non-local returns don't make sense: `inline fun forEach(crossinline action: (T) -> Unit) { thread { action(item) } }`. Without `crossinline`, the compiler would allow `return` inside `action`, which is unsafe since it runs in a different thread. Use `noinline` to prevent inlining entirely. Use `crossinline` to allow inlining but prevent non-local returns.

15. **What are `vararg` parameters and how do you use the spread operator?**
    - `vararg` allows a function to accept a variable number of arguments: `fun sum(vararg numbers: Int): Int = numbers.sum()`. Inside the function, `numbers` is an `Array<Int>`. Call: `sum(1, 2, 3)` or `sum()`. Only one `vararg` per function, typically the last parameter. Use named arguments for parameters after `vararg`: `fun format(vararg items: String, separator: String)`. The **spread operator** `*` passes an array to a `vararg` parameter: `val arr = arrayOf(1, 2, 3); sum(*arr)`. Combine spread with individual values: `sum(0, *arr, 4)`. For primitive arrays: `val ints = intArrayOf(1, 2, 3); sum(*ints.toTypedArray())`. `vararg` is implemented as an array on the JVM.

---

## 🔗 Related Topics
- [Control Flow](ControlFlow.md)
- [Null Safety](NullSafety.md)
