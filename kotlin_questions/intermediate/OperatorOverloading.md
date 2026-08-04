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

6. **What operators can be overloaded in Kotlin and what are their corresponding function names?**
   - Unary: `+a` → `unaryPlus`, `-a` → `unaryMinus`, `!a` → `not`, `++a`/`a++` → `inc`, `--a`/`a--` → `dec`. Binary arithmetic: `a + b` → `plus`, `a - b` → `minus`, `a * b` → `times`, `a / b` → `div`, `a % b` → `rem`, `a..b` → `rangeTo`. Compound: `a += b` → `plusAssign` (or `plus` if `plusAssign` not defined). Comparison: `a > b` → `compareTo` (also `<`, `<=`, `>=`). Equality: `a == b` → `equals` (no `operator` keyword needed, just override). Indexed: `a[i]` → `get`, `a[i] = v` → `set`. Invoke: `a()` → `invoke`. In: `a in b` → `contains` (on `b`). Range: `a in b..c` → `rangeTo` + `contains`. Convention: use `compareTo` for ordering, `equals` for equality, `contains` for membership. Not all operators are overloadable — `&&`, `||`, `?:`, `===`, `!==` cannot be overloaded.

7. **How do you implement `compareTo` for comparison operators and what's the convention?**
   - Implement `operator fun compareTo(other: T): Int` — returns negative if `this < other`, zero if equal, positive if `this > other`. This enables `<`, `>`, `<=`, `>=`. Convention: use `compareTo` from `Comparable` interface (which already has `operator fun compareTo`). Example: `data class Version(val major: Int, val minor: Int) : Comparable<Version> { override fun compareTo(other: Version): Int = compareValuesBy(this, other, { it.major }, { it.minor }) }`. Now `Version(1, 0) < Version(2, 0)` works. `compareValuesBy` is a utility that compares by multiple properties in order. For custom ordering, use `Comparator`: `val byAge = compareBy<Person> { it.age }`. Note: `compareTo` should be consistent with `equals` — `a.compareTo(b) == 0` should imply `a == b`. If not, document the inconsistency. Never use `compareTo` for equality checks — use `equals` instead.

8. **What is the `in` operator and how do you implement `contains`?**
   - `a in b` is syntactic sugar for `b.contains(a)`. Implement `operator fun contains(element: T): Boolean` on the container class. Example: `class IntRange(val start: Int, val end: Int) { operator fun contains(value: Int): Boolean = value in start..end }`. Usage: `5 in IntRange(1, 10)` → true. The `in` operator also works with ranges: `if (x in 1..10)`. For custom collections, implement `contains` to support `in`. The `!in` operator (`a !in b`) is `!b.contains(a)`. Use `in` for readability — `if (name in namesList)` reads better than `if (namesList.contains(name))`. For ranges, Kotlin's `IntRange` already implements `contains`. You can also implement `contains` on enums or sealed classes: `operator fun Color.contains(c: Color) = c in setOf(RED, GREEN, BLUE)`.

9. **How does the `invoke` operator work and when is it useful?**
   - `invoke` allows an object to be called like a function: `obj(args)`. Implement `operator fun invoke(params): ReturnType`. Example: `class Multiplier(val factor: Int) { operator fun invoke(x: Int): Int = x * factor }`. Usage: `val triple = Multiplier(3); triple(5)` → 15. Use cases: (1) **Strategy/Command pattern** — encapsulate behavior in an object: `class ClickHandler(val action: () -> Unit) { operator fun invoke() = action() }`. (2) **DSL builders** — `class Html { operator fun invoke(block: Html.() -> Unit) = block() }`. (3) **Function-like wrappers** — `class Validator<T>(val predicate: (T) -> Boolean) { operator fun invoke(value: T) = predicate(value) }`. (4) **Configurable operations** — `class ApiClient(val baseUrl: String) { operator fun invoke(path: String) = "$baseUrl$path" }`. (5) **Kotlinx HTML** — `div { }` uses `invoke`. `invoke` makes objects feel like functions, creating natural DSLs and fluent APIs. It can have multiple overloads with different parameters.

10. **What is the difference between `plus` and `plusAssign` and when do you use each?**
    - `plus` (`a + b`) returns a **new** object — it doesn't modify either operand: `operator fun plus(other: T): T`. Example: `val c = a + b` — `a` and `b` are unchanged, `c` is a new object. `plusAssign` (`a += b`) **modifies** the left operand in place: `operator fun plusAssign(other: T): Unit`. Example: `a += b` — `a` is modified. Important: you cannot define BOTH `plus` and `plusAssign` for the same types — the compiler can't decide which to use for `+=`. Convention: (1) For **immutable** types (like `String`, `Int`, data classes), implement `plus` only — `+=` will use `plus` and reassign. (2) For **mutable** collections (like `MutableList`), implement `plusAssign` for in-place modification: `list += item`. (3) For **builder** patterns, use `plusAssign`: `builder += element`. If both are defined, use `a = a + b` explicitly for `plus` or `a.plusAssign(b)` for `plusAssign`.

11. **How do you overload destructuring with `componentN` operators?**
    - `componentN` operators enable destructuring declarations: `val (a, b, c) = obj` calls `obj.component1()`, `obj.component2()`, `obj.component3()`. Data classes auto-generate these. For regular classes, implement manually: `class Point(val x: Int, val y: Int) { operator fun component1() = x; operator fun component2() = y }`. Usage: `val (x, y) = point`. Use cases: (1) **Multiple return values**: `class Result(val data: String, val error: Exception?); val (data, error) = fetch()`. (2) **Map entries**: `for ((key, value) in map) { }`. (3) **Loop with index**: `for ((index, value) in list.withIndex()) { }`. (4) **Pair/Triple**: `val (first, second) = pair`. You can skip components with `_`: `val (name, _, email) = user`. Only define `componentN` for logically destructurable types — don't force it on objects that don't naturally decompose.

12. **What are best practices and pitfalls for operator overloading in Kotlin?**
    - Best practices: (1) **Keep the semantic meaning** — `+` should mean addition/concatenation, not something surprising. `a + b` should be intuitive. (2) **Return consistent types** — `Matrix + Matrix → Matrix`, not `String`. (3) **Make operators pure** — `plus` should return a new value, not modify `this`. Use `plusAssign` for mutation. (4) **Document non-obvious behavior** — if `+` is expensive, note it. (5) **Prefer extension functions** — don't pollute the class with operators. (6) **Be consistent with `equals`/`hashCode`/`compareTo`** — if `a == b` then `a.compareTo(b) == 0`. Pitfalls: (1) **Overloading too many operators** — makes the class confusing. Only overload operators whose meaning is clear. (2) **Surprising semantics** — `Money + Money` is fine, but `User + User` is unclear. (3) **Side effects in operators** — `+` shouldn't log or trigger network calls. (4) **Performance** — overloaded operators may create many temporary objects (e.g., `a + b + c + d` creates 3 intermediates). Use `plusAssign` or `buildString` for performance. (5) **Not all operators are intuitive** — `!` (not) on a custom class may confuse readers. Only overload when the meaning is universally clear.

---

## 🔗 Related Topics
- [Extensions](Extensions.md)
- [OOP](OOP.md)
