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

5. **What is the difference between a function parameter and a function argument?**
   - A **parameter** is the variable declared in the function signature: `fun greet(name: String)` — `name` is the parameter. An **argument** is the actual value passed when calling the function: `greet("Alice")` — `"Alice"` is the argument. Parameters define what the function expects (type, name, default). Arguments are what the caller provides. This distinction matters when discussing default parameters (parameters have defaults) and named arguments (arguments can be named to match parameters). In Kotlin, you can also have `vararg` parameters that accept multiple arguments, and named arguments that allow passing arguments in any order by matching parameter names.

6. **What is the difference between `infix` functions and regular functions?**
   - `infix` functions allow calling a function **without the dot and parentheses**: `a to b` instead of `a.to(b)`. Requirements: (1) Must be a member function or extension function. (2) Must have exactly one parameter. (3) Must be marked `infix`. Example: `infix fun String.times(n: Int): String = this.repeat(n)` — call as `"ab" times 3`. Built-in infix functions: `to` (`1 to "one"`), `step` (`1..10 step 2`), `until` (`1 until 5`). Use infix for: (1) DSL-like syntax. (2) Mathematical operations. (3) Readable domain-specific code. Don't overuse — regular function calls are clearer for complex logic.

7. **What is the difference between `return`, `return@label`, and `return@function`?**
   - `return` — returns from the **enclosing function**. In a lambda inside a non-inline function, it returns from the enclosing function. `return@label` — returns from the **labeled lambda** (local return). Example: `list.forEach { if (it == 0) return@forEach; println(it) }` — skips the current iteration, continues to next (like `continue`). `return@functionName` — same as `return@label` but uses the function name as the implicit label. In `inline` functions, `return` inside a lambda does a **non-local return** — exits the enclosing function. This is only possible with `inline` functions. Use `return@forEach` to skip (like `continue`). Use `return` to exit the function entirely.

8. **What are function types in Kotlin?**
   - Function types describe the signature of a function. Syntax: `(ParamType1, ParamType2) -> ReturnType`. Examples: `(Int, Int) -> Int` (takes two Ints, returns Int), `() -> Unit` (no params, no return), `(String) -> Boolean` (takes String, returns Boolean). You can store functions in variables: `val checker: (String) -> Boolean = { it.isNotEmpty() }`. Function types can have named parameters for documentation: `(name: String, age: Int) -> String`. Nullable return: `(Int) -> Int?`. Nullable function: `((Int) -> Int)?`. You can use type aliases: `typealias Predicate<T> = (T) -> Boolean`. Function types are implemented as interfaces (`Function1<P1, R>`, `Function2<P1, P2, R>`, etc.) — each lambda is an object implementing these interfaces.

9. **What is the difference between a function type and a lambda in Kotlin?**
   - Think of it like **interface vs object** or **blueprint vs instance**:
   
   **Function type** = the *type* (the blueprint/signal). It describes what inputs a function takes and what it returns, but doesn't contain any actual logic. Syntax: `(InputTypes) -> ReturnType`.
   - `(Int, Int) -> Int` means "a function that takes two Ints and returns an Int."
   - `(String) -> Boolean` means "a function that takes a String and returns a Boolean."
   - `() -> Unit` means "a function that takes nothing and returns nothing."
   
   You can use function types as variable types, parameter types, or return types — just like `String` or `Int`:
   ```kotlin
   // Using a function type as a parameter type
   fun calculate(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
       return operation(a, b)  // Call the function that was passed in
   }
   ```
   
   **Lambda** = the *value* (the actual implementation). It's a concrete block of code that you can assign to a variable or pass around. Syntax: `{ parameters -> body }`.
   ```kotlin
   // A lambda that adds two numbers
   { a: Int, b: Int -> a + b }
   
   // Assign a lambda to a variable with an explicit function type
   val add: (Int, Int) -> Int = { a, b -> a + b }
   val isLong: (String) -> Boolean = { s -> s.length > 10 }
   
   // Call it
   println(add(3, 5))  // Output: 8
   ```
   
   **The relationship**: A function type is the *type*, a lambda is a *value* of that type. Just like `String` is a type and `"hello"` is a value of that type, `(Int, Int) -> Int` is a type and `{ a, b -> a + b }` is a value of that type.
   
   ```kotlin
   // Analogy:
   val name: String = "Alice"           // String = type, "Alice" = value
   val add: (Int, Int) -> Int = { a, b -> a + b }  // (Int,Int)->Int = type, { a,b->a+b } = value
   ```
   
   **Other ways to provide a value for a function type** — lambdas aren't the only option:
   ```kotlin
   // 1. Lambda
   val double: (Int) -> Int = { x -> x * 2 }
   
   // 2. Method reference (::functionName)
   val toIntFn: (String) -> Int = String::toInt
   
   // 3. Named function reference (::ClassName.functionName)
   val lengthFn: (String) -> Int = String::length
   
   // 4. Anonymous function
   val triple: (Int) -> Int = fun(x: Int): Int { return x * 3 }
   ```
   
   **At runtime**: Each lambda compiles to an object implementing a generated interface (`Function0`, `Function1`, `Function2`, etc. — the number matches the parameter count). So `{ a, b -> a + b }` becomes an object of type `Function2<Int, Int, Int>` at runtime. This is why lambdas have a slight allocation overhead — they're real objects on the heap. Using `inline` functions eliminates this overhead by copying the lambda body directly at the call site.

10. **What is the difference between extension functions and member functions?**
    - **Member functions** are defined inside the class — they can access private members, are resolved at runtime (polymorphic), and can be overridden in subclasses. **Extension functions** are defined outside the class — they can only access the public API, are resolved statically at compile time (no polymorphism), and can't be overridden. If a class has both a member and an extension with the same signature, the **member always wins**. Extension functions don't modify the original class — they're syntactic sugar compiled to static functions. Use member functions for core class behavior. Use extension functions for utilities and adding methods to third-party classes (like `String`, `List`) without modifying them.

11. **What is the difference between `inline` and regular functions in Kotlin?**
    - `inline` functions have their bytecode **copied to the call site** instead of being called via a function call. This eliminates lambda object allocation and function call overhead. Example: `inline fun measure(block: () -> Unit) { block() }` — the lambda body is inlined at the call site, no lambda object is created. Benefits: (1) No lambda allocation — significant for hot paths. (2) Non-local returns — lambdas in inline functions can use `return` to exit the enclosing function. (3) Reified type parameters — `inline fun <reified T> typeOf() = T::class.java`. Drawbacks: (1) Larger bytecode — each call site gets a copy. (2) Can't be recursive (infinite inlining). Use `inline` for higher-order functions called frequently (like `forEach`, `let`, `run`). Don't inline large functions. Use `noinline` to prevent specific lambda parameters from being inlined.

12. **What is the difference between `@JvmOverloads` and `@JvmName` annotations?**
     - `@JvmOverloads` generates overloaded methods for functions with default parameters, so Java callers can use them without passing all arguments: `@JvmOverloads fun greet(name: String, greeting: String = "Hello")` generates `greet(String)`, `greet(String, String)`. Without it, Java callers must pass all arguments. `@JvmName` changes the JVM name of a function or property — useful when the Kotlin name conflicts or isn't valid in Java: `@JvmName("customName") fun String.myFunc() = ...`. Common use: `@JvmName` for extension functions on the same type with same name but different generic types — `@JvmName("filterStrings") fun <T> List<T>.filter()` vs `@JvmName("filterInts") fun List<Int>.filter()`. Both annotations improve Java interop.

13. **What are default arguments and how do they reduce overloading?**
    - Kotlin supports default arguments — a parameter can have a default value: `fun greet(name: String, greeting: String = "Hello", punct: String = "!")`. Call with: `greet("Alice")`, `greet("Alice", "Hi")`, `greet("Alice", punct = "?")`, or `greet("Alice", punct = "!", greeting = "Hi")`. This replaces Java's method overloading where you'd need multiple methods: `greet(name)`, `greet(name, greeting)`, `greet(name, greeting, punct)`. Named arguments allow skipping parameters — you can provide `punct` without `greeting` by using `greet("Alice", punct = "?")`. The compiler generates a synthetic method with a bitmask for Java interop. Use `@JvmOverloads` to generate the overloaded methods for Java callers.

14. **What is the difference between `tailrec` and regular recursion in Kotlin?**
    - Regular recursion adds a stack frame for each call — deep recursion causes `StackOverflowError`. `tailrec` (tail-recursive) functions have the recursive call as the **last operation** — the compiler optimizes this to a `while` loop with no additional stack frames. Mark with `tailrec`: `tailrec fun factorial(n: Long, acc: Long = 1): Long = if (n <= 1) acc else factorial(n - 1, acc * n)`. Requirements: (1) The recursive call must be the last operation. (2) Must be marked `tailrec`. (3) The compiler verifies and warns if not in tail position. Benefits: no `StackOverflowError`, same performance as a loop. If recursion isn't in tail position, refactor using an accumulator parameter.

15. **What is the difference between `reified` type parameters and regular generics in Kotlin?**
    - Regular generic type parameters are erased at runtime (type erasure) — you can't check `if (item is T)` or access `T::class.java`. `reified` makes the type parameter available at runtime, but it requires the function to be `inline`: `inline fun <reified T> typeOf() = T::class.java`. The compiler copies the function body to each call site and replaces `T` with the actual type. Use cases: (1) Type checking: `inline fun <reified T> List<*>.filterByType() = filter { it is T }`. (2) Starting Activities: `inline fun <reified T : Activity> Context.startActivity() = startActivity(Intent(this, T::class.java))`. (3) JSON parsing: `inline fun <reified T> Gson.fromJson(json: String) = fromJson(json, T::class.java)`. Reified eliminates the need to pass `Class<T>` parameters — the compiler infers the type.

---

## 🔗 Related Topics
- [Control Flow](ControlFlow.md)
- [Null Safety](NullSafety.md)
