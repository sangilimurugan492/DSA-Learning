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
   - `when` is an expression (returns a value), supports ranges, types, and arbitrary conditions. No fall-through or `break` needed.

2. **Can `if` be used as an expression in Kotlin?**
   - Yes. `val result = if (cond) valueA else valueB`.

3. **What is the difference between `1..5` and `1 until 5`?**
   - `1..5` includes 5 (inclusive). `1 until 5` excludes 5 (exclusive end).

4. **What are labeled breaks in Kotlin?**
   - You can label a loop and use `break@label` to break out of outer loops.

5. **Can `when` be used without a subject?**
   - Yes. `when` can be used as a replacement for `if-else if` chains without a subject variable.

---

## 🔗 Related Topics
- [Variables & Data Types](VariablesAndDataTypes.md)
- [Functions](Functions.md)
