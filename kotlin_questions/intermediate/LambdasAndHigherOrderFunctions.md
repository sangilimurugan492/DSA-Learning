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

---

## 🔗 Related Topics
- [Functions](../beginner/Functions.md)
- [Scope Functions](ScopeFunctions.md)
