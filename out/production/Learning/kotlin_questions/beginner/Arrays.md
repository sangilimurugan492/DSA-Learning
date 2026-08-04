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

6. **What is the difference between `arrayOf` and `intArrayOf`?**
   - `arrayOf(1, 2, 3)` creates `Array<Int>` — each element is a boxed `Integer` object (object on the heap). `intArrayOf(1, 2, 3)` creates `IntArray` — backed by a primitive `int[]` (no boxing). `IntArray` is more memory-efficient and faster for primitive types. Kotlin provides typed array factories for all primitives: `intArrayOf`, `longArrayOf`, `doubleArrayOf`, `floatArrayOf`, `booleanArrayOf`, `byteArrayOf`, `shortArrayOf`, `charArrayOf`. Use these instead of `arrayOf` for primitive types to avoid boxing overhead. `arrayOf` is for object types (`String`, custom classes).

7. **How do you iterate over an array with index in Kotlin?**
   - Three approaches: (1) `for (i in array.indices)` — iterates over indices: `array[i]`. (2) `for ((index, value) in array.withIndex())` — destructured index and value. (3) `array.forEachIndexed { index, value -> }` — functional style. The `indices` property returns `0..array.lastIndex` (an `IntRange`). `withIndex()` returns `Iterable<IndexedValue<T>>` — each element wraps index and value. For most cases, `forEachIndexed` is the cleanest. Use `indices` when you only need the index. Use `withIndex()` when you need both but prefer a for loop.

8. **What is the difference between `Array<Int>` and `List<Int>`?**
   - `Array<Int>` — fixed size, mutable elements (can change `array[0]` but can't add/remove elements). Backed by `Integer[]` (boxed primitives). Created with `arrayOf()`. `List<Int>` — read-only interface (can't modify elements through `List`). Backed by `java.util.Arrays$ArrayList` (fixed-size) when created with `listOf()`. Can be converted to `MutableList` for add/remove. Arrays are lower-level and fixed-size. Lists are higher-level, support dynamic sizing (via `MutableList`), and have rich extension functions (`map`, `filter`, `sortedBy`). Prefer `List` for most use cases — arrays are for performance-critical code or interop with Java APIs that require arrays.

9. **How do you convert between arrays and lists in Kotlin?**
   - **Array to List**: `array.toList()` — creates a new read-only `List`. `array.toMutableList()` — creates a new `MutableList`. `array.asList()` — returns a **view** backed by the array (changes to the array are reflected, but you can't add/remove). **List to Array**: `list.toTypedArray()` — creates a new `Array<T>`. `list.toIntArray()` (for `List<Int>`) — creates `IntArray` (unboxed). Note: `arrayOf(1, 2, 3).toList()` creates a copy — modifications to the array don't affect the list. `asList()` creates a view — modifications to the array DO affect the list. Use `toList()` for a safe copy, `asList()` when you want a live view.

10. **How do you sort an array in Kotlin?**
    - `array.sorted()` — returns a new sorted `List` (doesn't modify the original). `array.sortedArray()` — returns a new sorted `Array`. `array.sort()` — sorts the array **in place** (modifies the original, only for `IntArray`/`Array<T>` which are mutable). `array.sortDescending()` — in place, descending. `array.sortedBy { selector }` — sort by a property. `array.sortedWith(comparator)` — custom comparator. `array.reverse()` — reverses in place. For primitive arrays (`IntArray`), `sort()` uses dual-pivot quicksort (O(n log n)). For `Array<T>`, it uses `Arrays.sort()` (TimSort). Always use `sortedArray()` if you need to preserve the original.

11. **What array operations does Kotlin provide?**
    - (1) **Transform**: `map { }`, `flatMap { }`, `mapIndexed { }`. (2) **Filter**: `filter { }`, `filterNot { }`, `filterIndexed { }`. (3) **Find**: `find { }`, `firstOrNull { }`, `lastOrNull { }`. (4) **Aggregate**: `sum()`, `average()`, `maxOrNull()`, `minOrNull()`, `count { }`, `fold(initial) { }`, `reduce { }`. (5) **Check**: `any { }`, `all { }`, `none { }`, `contains(element)`. (6) **Sort**: `sorted()`, `sortedBy { }`, `sortedDescending()`, `sort()`. (7) **Combine**: `zip(otherArray)`, `plus(otherArray)`. (8) **Convert**: `toList()`, `toSet()`, `toMutableList()`, `joinToString()`. (9) **Chunk**: `chunked(size)`, `windowed(size)`. All these are extension functions from the standard library.

12. **What is the performance difference between `IntArray` and `Array<Int>`?**
     - `IntArray` — backed by primitive `int[]`. Each element is a 4-byte primitive. No object allocation per element. Faster access, less memory, no GC pressure. `Array<Int>` — backed by `Integer[]`. Each element is a boxed `Integer` object (16+ bytes on 64-bit JVM). Each access requires unboxing. Creates GC pressure for large arrays. For 1000 elements: `IntArray` = ~4KB, `Array<Int>` = ~16KB+ (object headers + references). Always use `IntArray`, `LongArray`, `DoubleArray`, etc. for primitive data. Use `Array<T>` only for object types (`String`, custom classes) or when the API requires `Array<T>`. The performance difference is significant for large arrays (>1000 elements) and in performance-critical code (games, image processing, sorting).

13. **How do you use `vararg` with arrays in Kotlin?**
    - `vararg` allows a function to accept a variable number of arguments. Inside the function, `vararg` is treated as an `Array`. Use the spread operator `*` to pass an array to a `vararg` parameter: `val arr = arrayOf(1, 2, 3); listOf(*arr)`. You can combine spread with individual elements: `listOf(0, *arr, 4)`. Only one `vararg` parameter per function, and it must be the last (unless using named arguments). Example: `fun sum(vararg numbers: Int): Int = numbers.sum()`. Call: `sum(1, 2, 3)` or `sum(*intArrayOf(1, 2, 3))`.

14. **How do you create and use multi-dimensional arrays in Kotlin?**
    - Two approaches: (1) `Array(rows) { IntArray(cols) }` — creates an array of `IntArray`s, each initialized to 0. (2) `Array(rows) { Array(cols) { 0 } }` — for object types. Access with `grid[row][col]`. For 3D: `Array(x) { Array(y) { IntArray(z) } }`. Initialize with a lambda: `val grid = Array(3) { i -> IntArray(3) { j -> i * 3 + j } }`. For jagged arrays (rows of different lengths): `Array(3) { row -> IntArray(row + 1) }`. Use `contentDeepEquals()` to compare multi-dimensional arrays: `array1 contentDeepEquals array2`.

15. **What is the `arrayOfNulls` function and when do you use it?**
    - `arrayOfNulls<T>(size)` creates an array of the specified size filled with `null` values. The type is `Array<T?>`. It's useful when you need to create an array with a fixed size and fill it later: `val array = arrayOfNulls<String>(5); array[0] = "Hello"`. This is more efficient than creating an empty `MutableList` and adding elements one by one if you know the final size. For primitive arrays, use `IntArray(size)` (fills with 0), `BooleanArray(size)` (fills with `false`), etc. `arrayOfNulls` is only for object types since primitives can't be null.

---

## 🔗 Related Topics
- [Collections](../intermediate/Collections.md)
- [Control Flow](ControlFlow.md)
