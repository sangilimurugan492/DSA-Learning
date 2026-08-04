# Control Flow

## 📖 Explanation

Kotlin provides several control flow constructs that are more expressive than Java's equivalents.

### `if` Expression
In Kotlin, `if` is an **expression** — it returns a value.

```kotlin
val max = if (a > b) a else b
```

### `when` Expression
Replaces Java's `switch`. More powerful — supports ranges, types, and conditions.

```kotlin
when (x) {
    1 -> "One"
    2, 3 -> "Two or Three"
    in 4..10 -> "Between 4 and 10"
    else -> "Unknown"
}
```

### Loops
- `for` — Iterates over ranges, arrays, collections.
- `while` / `do-while` — Same as Java.

```kotlin
for (i in 1..5) print("$i ")        // 1 2 3 4 5
for (i in 1 until 5) print("$i ")   // 1 2 3 4
for (i in 5 downTo 1) print("$i ")  // 5 4 3 2 1
for (i in 1..10 step 2) print("$i ") // 1 3 5 7 9
```

### `break` and `continue`
Support labels for fine-grained control.

```kotlin
loop@ for (i in 1..3) {
    for (j in 1..3) {
        if (j == 2) break@loop
        println("i=$i, j=$j")
    }
}
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // if as expression
    val a = 10
    val b = 20
    val max = if (a > b) a else b
    println("Max: $max")

    // when expression
    val day = 3
    val dayName = when (day) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6, 7 -> "Weekend"
        else -> "Invalid"
    }
    println("Day: $dayName")

    // for loop with range
    print("Range: ")
    for (i in 1..5) print("$i ")
    println()

    // for loop with step
    print("Step: ")
    for (i in 0..10 step 2) print("$i ")
    println()

    // while loop
    var count = 3
    print("Countdown: ")
    while (count > 0) {
        print("$count ")
        count--
    }
    println()

    // iterate with index
    val fruits = listOf("Apple", "Banana", "Cherry")
    for ((index, fruit) in fruits.withIndex()) {
        println("$index: $fruit")
    }
}
```

### Output
```
Max: 20
Day: Wednesday
Range: 1 2 3 4 5 
Step: 0 2 4 6 8 10 
Countdown: 3 2 1 
0: Apple
1: Banana
2: Cherry
```

---

## ❓ Interview Questions

1. **How is Kotlin's `when` different from Java's `switch`?**
   - `when` is far more powerful than Java's `switch`: (1) It's an **expression** — returns a value: `val result = when(x) { 1 -> "one" else -> "other" }`. (2) Supports **multiple values** in one branch: `1, 2, 3 -> "low"`. (3) Supports **ranges**: `in 1..10 -> "small"`. (4) Supports **type checks**: `is String -> s.length`. (5) Supports **arbitrary conditions**: `x > 100 -> "big"`. (6) **No fall-through** — no need for `break`. (7) **Exhaustive** — when used as an expression with sealed classes, the compiler enforces all branches are covered. (8) Can be used **without a subject** — acts like an `if-else if` chain: `when { x > 0 -> "pos" x < 0 -> "neg" else -> "zero" }`. Java's `switch` only matches constants, requires `break`, and can't return values.

2. **Can `if` be used as an expression in Kotlin?**
   - Yes. In Kotlin, `if` is an expression that returns a value: `val max = if (a > b) a else b`. This replaces Java's ternary operator `? :` (Kotlin doesn't have a ternary operator). The `if` expression must have an `else` branch when used as an expression (otherwise the type is `Unit`). Example: `val status = if (score >= 60) "Pass" else "Fail"`. You can also use blocks: `val result = if (cond) { computeValue() } else { defaultValue }`. The last expression in each block is the return value. This makes code more concise and functional compared to Java's if-else statements.

3. **What is the difference between `1..5` and `1 until 5`?**
   - `1..5` creates a range that **includes** 5 (inclusive on both ends): 1, 2, 3, 4, 5. `1 until 5` creates a range that **excludes** 5 (inclusive start, exclusive end): 1, 2, 3, 4. `until` is syntactic sugar for `1..4` but more readable. Use `..` when you want both endpoints included (e.g., iterating over array indices `0..array.lastIndex`). Use `until` when you want to exclude the end (e.g., iterating over array size `0 until array.size`). Other range operators: `5 downTo 1` (reverse: 5, 4, 3, 2, 1), `1..10 step 2` (step: 1, 3, 5, 7, 9). Ranges implement `ClosedRange<T>` and `Iterable<T>`.

4. **What are labeled breaks in Kotlin?**
   - Kotlin supports labels for fine-grained loop control. A label is defined with `@` suffix: `loop@ for (i in 1..3) { for (j in 1..3) { if (j == 2) break@loop } }`. `break@loop` breaks the **outer** loop, not just the inner one. Similarly, `continue@loop` continues the outer loop. Without labels, `break` only exits the innermost loop. Labels are useful for nested loops where you need to control the outer loop from the inner one. Any expression can be labeled: `return@forEach` returns from a lambda. Labels are also used implicitly with function literals: `forEach` creates an implicit label, so `return@forEach` continues to the next iteration.

5. **Can `when` be used without a subject?**
   - Yes. `when` can be used without a subject variable — it acts as a replacement for `if-else if` chains. Each branch is a boolean condition: `when { x > 100 -> "big" x > 50 -> "medium" x > 0 -> "small" else -> "zero or negative" }`. The first matching branch wins (top to bottom). This is cleaner than chained `if-else if` and allows mixing different condition types. Use subject `when` when all conditions check the same variable (`when(x) { ... }`). Use subjectless `when` when conditions are unrelated. The subjectless form is also an expression and can return values.

6. **What is the difference between `for` loop and `forEach` in Kotlin?**
   - `for (item in collection)` is a traditional loop — it uses an iterator and supports `break`/`continue`. `collection.forEach { }` is a higher-order function — it takes a lambda and calls it for each element. Key differences: (1) `forEach` is a lambda — you can't use `break`/`continue` (use `return@forEach` to skip, or `return` to exit the enclosing function). (2) `for` supports `break` and `continue` natively. (3) `forEach` is more functional and composable with other collection operations. (4) `forEach` has slightly more overhead (lambda allocation) but is negligible in most cases. Use `for` when you need `break`/`continue`. Use `forEach` for functional-style chains: `list.filter { }.map { }.forEach { }`.

7. **What are ranges in Kotlin and how do they work?**
   - Ranges represent a sequence of values between a start and end. Created with `..` (inclusive): `1..5` = 1, 2, 3, 4, 5. Types: `IntRange`, `LongRange`, `CharRange` (`'a'..'z'`). Ranges implement `ClosedRange<T>` (provides `start`, `endInclusive`, `contains()`) and `Iterable<T>` (can be used in `for` loops). Modifiers: `until` (exclusive end), `downTo` (reverse), `step` (increment). Check membership: `5 in 1..10` → `true`. Ranges are lightweight — they don't allocate a list, just store start, end, and step. Custom ranges: implement `ClosedRange<T>` for custom types with `Comparable`. Ranges are used in `when` branches: `in 1..10 -> "small"`.

8. **What is the difference between `while` and `do-while` in Kotlin?**
   - `while (condition) { }` checks the condition **before** each iteration — the body may execute zero times. `do { } while (condition)` checks the condition **after** each iteration — the body executes at least once. Use `while` when the condition should be checked first (most common). Use `do-while` when the body must execute at least once (e.g., reading input until valid, menu display). Both are statements (not expressions) — they don't return values. Kotlin's `while` and `do-while` work identically to Java's. Prefer `for` or `forEach` for collection iteration — `while` is better for condition-based loops (e.g., polling, game loops).

9. **What is `repeat` in Kotlin?**
   - `repeat(n) { }` is a built-in function that executes a lambda `n` times. It's cleaner than `for (i in 1..n)`. Example: `repeat(3) { println("Hello") }` prints "Hello" 3 times. The lambda receives the current index (0-based): `repeat(3) { index -> println("Iteration $index") }`. `repeat` is an inline function — no lambda allocation overhead. It's equivalent to `for (i in 0 until n) { }` but more concise. Use `repeat` for simple count-based loops where you don't need the index, or where the index is optional.

10. **What is the difference between `break` and `continue` in Kotlin?**
    - `break` exits the loop entirely — no more iterations. `continue` skips the rest of the current iteration and moves to the next one. Both work in `for` and `while` loops. Both support labels: `break@outerLoop` exits the labeled outer loop, `continue@outerLoop` skips to the next iteration of the labeled outer loop. Neither works inside `forEach` (it's a lambda, not a loop) — use `return@forEach` to skip (equivalent to `continue`) or `return` to exit the enclosing function. In nested loops, `break`/`continue` affect only the innermost loop unless a label is specified.

11. **What is the `in` operator in Kotlin?**
    - `in` has two uses: (1) **Range/collection membership check** — `x in 1..10` checks if `x` is in the range. `item in list` checks if `item` is in the collection (calls `contains()`). (2) **For-loop iteration** — `for (item in list)` iterates over the collection. The negation is `!in` — `x !in 1..10` checks if `x` is NOT in the range. `in` works with any type that implements `Comparable` for ranges, and any `Collection` for membership. You can make custom classes work with `in` by implementing `contains(value: T): Boolean` or `ClosedRange<T>`.

12. **What is exhaustive `when` and how does it work with sealed classes?**
    - When `when` is used as an expression (not a statement) with a sealed class or enum, the compiler enforces that **all branches are covered** — if you miss a case, it won't compile. This is called exhaustive `when`. Example:
      ```kotlin
      sealed class Result
      data class Success(val data: String) : Result()
      data class Error(val message: String) : Result()
      
      val text = when (result) {
          is Success -> result.data
          is Error -> result.message
          // No else needed — all cases covered
      }
      ```
      If you add a new subclass to `Result` (e.g., `Loading`), the compiler will flag all `when` expressions that don't handle it. This prevents bugs from missing cases. If `when` is used as a statement (not expression), it's not exhaustive — always use it as an expression for sealed classes.

13. **What is the difference between `if` as a statement and `if` as an expression?**
    - In Kotlin, `if` can be used as both. As a **statement**: `if (x > 0) { println("positive") }` — no return value, executes a block. As an **expression**: `val result = if (x > 0) "positive" else "negative"` — returns the value of the matched branch. Unlike Java, Kotlin doesn't have a ternary operator (`? :`) because `if` as an expression replaces it. `if` as an expression requires an `else` branch when used for assignment. The last expression in each branch is the return value. Example: `val max = if (a > b) a else b`. This is cleaner than Java's `int max = (a > b) ? a : b;`.

14. **How do you use labels in Kotlin for control flow?**
    - Labels mark a position in code with `labelName@`. Use with `break`, `continue`, and `return` for fine-grained control. Example: `outer@ for (i in 1..3) { for (j in 1..3) { if (j == 2) continue@outer; println("$i, $j") } }`. `break@outer` exits the labeled loop. `continue@outer` skips to the next iteration of the labeled loop. `return@label` returns from a labeled lambda: `list.forEach label@ { if (it == 0) return@label; println(it) }`. Implicit labels use the function name: `return@forEach` is equivalent. Labels are essential for non-local returns from inline function lambdas and for nested loop control.

15. **What is the difference between `when` with subject and `when` without subject?**
    - **With subject**: `when (x) { 1 -> "one"; 2 -> "two"; else -> "many" }` — each branch is compared against `x`. Supports: exact values (`1 ->`), ranges (`in 1..10 ->`), types (`is String ->`), and function calls. **Without subject**: `when { x > 100 -> "big"; x > 50 -> "medium"; else -> "small" }` — each branch is a boolean condition. The subjectless form is like `if-else if` but cleaner. Use subject `when` when all branches check the same variable. Use subjectless `when` when conditions are different variables or complex expressions. Both forms can be expressions (return values) or statements. Subjectless `when` is more flexible but less readable when all conditions check the same value.

---

## 🔗 Related Topics
- [Variables & Data Types](VariablesAndDataTypes.md)
- [Functions](Functions.md)
