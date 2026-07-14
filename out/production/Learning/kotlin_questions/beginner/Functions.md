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
   - A function whose body is a single expression. Uses `=` instead of curly braces: `fun square(x: Int) = x * x`.

2. **What are default and named arguments? How do they reduce boilerplate?**
   - Default arguments provide fallback values, eliminating the need for overloaded methods. Named arguments allow calling parameters in any order, improving readability.

3. **What is `vararg` and how do you pass an array to it?**
   - `vararg` allows a variable number of arguments. Use the spread operator `*` to pass an array: `sum(*array)`.

4. **Can Kotlin functions be nested?**
   - Yes. Local functions can be declared inside other functions and can access outer function's variables.

5. **What is the difference between `inline` functions and regular functions?**
   - `inline` functions have their bytecode copied to the call site, reducing overhead of lambda allocations. Useful for higher-order functions.

---

## 🔗 Related Topics
- [Control Flow](ControlFlow.md)
- [Null Safety](NullSafety.md)
