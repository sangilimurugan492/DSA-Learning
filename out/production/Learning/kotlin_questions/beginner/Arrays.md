# Arrays

## 📖 Explanation

Arrays in Kotlin are represented by the `Array<T>` class. Unlike Java, Kotlin arrays are invariant — `Array<Int>` is not a subtype of `Array<Any>`.

### Creating Arrays
```kotlin
// Using arrayOf
val nums = arrayOf(1, 2, 3)
val mixed = arrayOf(1, "two", 3.0)  // Array<Any>

// Using Array constructor with init lambda
val squares = Array(5) { i -> i * i }  // [0, 1, 4, 9, 16]

// Primitive arrays (avoid boxing overhead)
val ints = intArrayOf(1, 2, 3)
val doubles = doubleArrayOf(1.0, 2.0)
val booleans = booleanArrayOf(true, false)
```

### Primitive Arrays vs `Array<T>`
| Type            | Class           | Description                        |
|-----------------|-----------------|------------------------------------|
| `IntArray`      | Primitive int[] | No boxing overhead                 |
| `Array<Int>`    | `Integer[]`     | Each element is boxed              |

### Accessing & Modifying
```kotlin
val arr = arrayOf("A", "B", "C")
arr[0]          // "A" — get
arr[1] = "X"   // set
arr.size        // 3
```

### Useful Array Functions
| Function         | Description                          |
|------------------|--------------------------------------|
| `size`           | Number of elements                   |
| `get(i)` / `set(i, v)` | Access by index              |
| `indices`        | Range of valid indices (0..size-1)   |
| `withIndex()`    | Returns (index, value) pairs         |
| `toList()`       | Convert to List                      |
| `sorted()`       | Returns sorted copy                  |
| `reversed()`     | Returns reversed copy                |
| `contentToString()` | String representation             |
| `contentEquals()` | Structural equality check           |

### 2D Arrays
```kotlin
val matrix = Array(3) { IntArray(3) }  // 3x3 matrix of zeros
matrix[0][1] = 5
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // arrayOf
    val fruits = arrayOf("Apple", "Banana", "Cherry")
    println("Fruits: ${fruits.contentToString()}")

    // Array constructor with lambda
    val squares = Array(5) { it * it }
    println("Squares: ${squares.contentToString()}")

    // Primitive arrays
    val ints = intArrayOf(10, 20, 30, 40, 50)
    println("Ints: ${ints.contentToString()}")
    println("Sum: ${ints.sum()}")
    println("Max: ${ints.maxOrNull()}")
    println("Average: ${ints.average()}")

    // Accessing elements
    println("First: ${ints[0]}, Last: ${ints[ints.size - 1]}")
    println("First (func): ${ints.first()}, Last (func): ${ints.last()}")

    // Iterate with index
    println("\nWith index:")
    for ((index, value) in fruits.withIndex()) {
        println("  [$index] = $value")
    }

    // indices
    println("\nUsing indices:")
    for (i in ints.indices) {
        print("${ints[i]} ")
    }
    println()

    // 2D array
    val matrix = Array(3) { IntArray(3) { it } }
    matrix[0][0] = 100
    println("\nMatrix:")
    for (row in matrix) {
        println("  ${row.contentToString()}")
    }

    // Transform
    val doubled = ints.map { it * 2 }
    println("\nDoubled: $doubled")

    // Filter
    val evens = ints.filter { it % 20 == 0 }
    println("Evens: $evens")

    // Sort
    val unsorted = intArrayOf(5, 2, 8, 1, 9)
    println("Sorted: ${unsorted.sorted().toIntArray().contentToString()}")

    // Convert to List
    val list = fruits.toList()
    println("As List: $list")

    // contentEquals
    val a1 = intArrayOf(1, 2, 3)
    val a2 = intArrayOf(1, 2, 3)
    println("a1 == a2 (ref): ${a1 == a2}")               // false
    println("a1 contentEquals a2: ${a1.contentEquals(a2)}") // true
}
```

### Output
```
Fruits: [Apple, Banana, Cherry]
Squares: [0, 1, 4, 9, 16]
Ints: [10, 20, 30, 40, 50]
Sum: 150
Max: 50
Average: 30.0
First: 10, Last: 50
First (func): 10, Last (func): 50

With index:
  [0] = Apple
  [1] = Banana
  [2] = Cherry

Using indices:
10 20 30 40 50 

Matrix:
  [100, 1, 2]
  [0, 1, 2]
  [0, 1, 2]

Doubled: [20, 40, 60, 80, 100]
Evens: [20, 40]
Sorted: [1, 2, 5, 8, 9]
As List: [Apple, Banana, Cherry]
a1 == a2 (ref): false
a1 contentEquals a2: true
```

---

## ❓ Interview Questions

1. **What is the difference between `IntArray` and `Array<Int>`?**
   - `IntArray` is backed by a primitive `int[]` — no boxing overhead. `Array<Int>` uses `Integer[]` — each element is boxed, adding memory overhead.

2. **Why are Kotlin arrays invariant?**
   - `Array<Int>` is not a subtype of `Array<Any>` to prevent type-unsafe operations (e.g., putting a `String` into an `Int` array). This is safer than Java's covariant arrays.

3. **How do you create an array with a custom init function?**
   - `Array(5) { i -> i * i }` — the lambda receives the index and returns the element value.

4. **How do you compare two arrays for equality in Kotlin?**
   - Use `contentEquals()` or `contentDeepEquals()` for nested arrays. The `==` operator checks referential equality, not structural.

5. **How do you create a 2D array in Kotlin?**
   - `Array(rows) { IntArray(cols) }` creates a 2D array. Each row is a separate `IntArray`.

---

## 🔗 Related Topics
- [Collections](../intermediate/Collections.md)
- [Control Flow](ControlFlow.md)
