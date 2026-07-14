# Operator Overloading

## 📖 Explanation

Kotlin allows you to provide custom implementations for predefined operators (like `+`, `-`, `*`, `[]`, `==`, etc.) by implementing specific member functions or extension functions with the `operator` keyword.

### How It Works
Each operator maps to a specific function name. You implement that function with the `operator` modifier.

| Operator | Function          | Example         |
|----------|-------------------|-----------------|
| `+`      | `plus`            | `a + b`         |
| `-`      | `minus`           | `a - b`         |
| `*`      | `times`           | `a * b`         |
| `/`      | `div`             | `a / b`         |
| `%`      | `rem`             | `a % b`         |
| `+=`     | `plusAssign`      | `a += b`        |
| `==`     | `equals`          | `a == b`        |
| `>`      | `compareTo`       | `a > b`         |
| `[]`     | `get` / `set`     | `a[i]`          |
| `in`     | `contains`        | `x in a`        |
| `..`     | `rangeTo`         | `a..b`          |
| `()`     | `invoke`          | `a()`           |
| `unary -`| `unaryMinus`      | `-a`            |
| `++`     | `inc`             | `a++`           |

### Rules
- Must use the `operator` keyword.
- Can be member functions or extension functions.
- Cannot define entirely new operators — only override predefined ones.
- `==` calls `equals()` — don't use `operator` with it; just override `equals`.

### `invoke` Operator
Allows objects to be called like functions.

```kotlin
class Multiplier(val factor: Int) {
    operator fun invoke(x: Int) = x * factor
}
val triple = Multiplier(3)
triple(5)  // 15
```

### `compareTo` for Ordering
Implement `compareTo` to enable `<`, `>`, `<=`, `>=`.

```kotlin
operator fun compareTo(other: T): Int
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // Vector with arithmetic operators
    val v1 = Vector(3.0, 4.0)
    val v2 = Vector(1.0, 2.0)

    println("v1 = $v1")
    println("v2 = $v2")
    println("v1 + v2 = ${v1 + v2}")
    println("v1 - v2 = ${v1 - v2}")
    println("v1 * 2 = ${v1 * 2.0}")
    println("-v1 = ${-v1}")
    println("|v1| = ${v1.magnitude()}")

    // Index access
    println("v1[0] = ${v1[0]}")
    v1[0] = 10.0
    println("v1 after set = $v1")

    // compareTo
    val a = Vector(1.0, 0.0)
    val b = Vector(3.0, 4.0)
    println("\na < b (magnitude): ${a < b}")
    println("a > b: ${a > b}")

    // invoke operator
    val multiplier = Multiplier(3)
    println("\nmultiplier(5) = ${multiplier(5)}")
    println("multiplier(10) = ${multiplier(10)}")

    // contains operator
    val range = 1..10
    println("\n5 in 1..10: ${5 in range}")
    println("15 in 1..10: ${15 in range}")

    // Custom range
    val customRange = 'a'..'e'
    println("'c' in 'a'..'e': ${'c' in customRange}")

    // String repetition via times
    println("\n'Ab' * 3 = ${"Ab" * 3}")

    // Matrix with get/set
    val matrix = Matrix(2, 2)
    matrix[0, 0] = 1
    matrix[0, 1] = 2
    matrix[1, 0] = 3
    matrix[1, 1] = 4
    println("\nMatrix:")
    println(matrix)
    println("matrix[1,1] = ${matrix[1, 1]}")
}

// --- Vector with operators ---
data class Vector(var x: Double, var y: Double) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)
    operator fun unaryMinus() = Vector(-x, -y)

    operator fun get(index: Int): Double = when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid index: $index")
    }

    operator fun set(index: Int, value: Double) {
        when (index) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException("Invalid index: $index")
        }
    }

    operator fun compareTo(other: Vector): Int =
        magnitude().compareTo(other.magnitude())

    fun magnitude(): Double = Math.sqrt(x * x + y * y)
}

// --- invoke operator ---
class Multiplier(val factor: Int) {
    operator fun invoke(x: Int): Int = x * factor
}

// --- times for String (extension) ---
operator fun String.times(n: Int): String = repeat(n)

// --- Matrix with multi-dimensional get/set ---
class Matrix(val rows: Int, val cols: Int) {
    private val data = Array(rows) { IntArray(cols) }

    operator fun get(row: Int, col: Int): Int = data[row][col]

    operator fun set(row: Int, col: Int, value: Int) {
        data[row][col] = value
    }

    override fun toString(): String = data.joinToString("\n") { row ->
        row.joinToString(" ", "[", "]")
    }
}
```

### Output
```
v1 = Vector(x=3.0, y=4.0)
v2 = Vector(x=1.0, y=2.0)
v1 + v2 = Vector(x=4.0, y=6.0)
v1 - v2 = Vector(x=2.0, y=2.0)
v1 * 2 = Vector(x=6.0, y=8.0)
-v1 = Vector(x=-3.0, y=-4.0)
|v1| = 5.0
v1[0] = 3.0
v1 after set = Vector(x=10.0, y=4.0)

a < b (magnitude): true
a > b: false

multiplier(5) = 15
multiplier(10) = 30

5 in 1..10: true
15 in 1..10: false
'c' in 'a'..'e': true

'Ab' * 3 = AbAbAb

Matrix:
[1 2]
[3 4]
matrix[1,1] = 4
```

---

## ❓ Interview Questions

1. **What is operator overloading in Kotlin?**
   - Providing custom implementations for predefined operators (`+`, `-`, `[]`, etc.) by implementing corresponding functions (like `plus`, `minus`, `get`) with the `operator` keyword.

2. **Can you create entirely new operators in Kotlin?**
   - No. You can only override predefined operators that map to specific function names. You cannot invent new operator symbols.

3. **What is the `invoke` operator and when is it useful?**
   - `invoke` allows an object to be called like a function: `obj(args)`. Useful for DSLs, function-like objects, and strategy patterns.

4. **How does `==` work with operator overloading?**
   - `==` calls `equals()`. You don't use the `operator` keyword — just override `equals`. `===` checks referential equality and cannot be overloaded.

5. **How do you implement indexed access (`[]`) for a class?**
   - Implement `operator fun get(index: Int)` for reading and `operator fun set(index: Int, value: T)` for writing. Supports multiple indices: `get(row: Int, col: Int)`.

---

## 🔗 Related Topics
- [Extensions](Extensions.md)
- [OOP](OOP.md)
