# Collections

## 📖 Explanation

Kotlin collections are built on top of Java collections but provide a rich set of extension functions. Collections are split into **read-only** and **mutable** variants.

### Collection Types

| Type            | Read-Only         | Mutable                  |
|-----------------|-------------------|--------------------------|
| List            | `List<T>`         | `MutableList<T>`         |
| Set             | `Set<T>`          | `MutableSet<T>`          |
| Map             | `Map<K, V>`       | `MutableMap<K, V>`       |

### Creating Collections
```kotlin
val list = listOf(1, 2, 3)              // Read-only
val mutableList = mutableListOf(1, 2, 3) // Mutable
val set = setOf("A", "B", "C")
val map = mapOf(1 to "One", 2 to "Two")
```

### Key Operations
| Category    | Functions                                      |
|-------------|------------------------------------------------|
| Transform   | `map`, `flatMap`, `groupBy`, `chunked`         |
| Filter      | `filter`, `filterNot`, `filterNotNull`         |
| Aggregate   | `sum`, `count`, `maxOrNull`, `minOrNull`, `average` |
| Find        | `find`, `findLast`, `first`, `last`            |
| Sort        | `sorted`, `sortedBy`, `sortedDescending`       |
| Partition   | `partition`, `zip`, `unzip`                    |
| Fold/Reduce | `fold`, `reduce`, `reduceRight`                |
| Check       | `any`, `all`, `none`, `contains`               |
| Join        | `joinToString`, `joinTo`                       |

---

## 🧪 Code Example

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // map — transform each element
    val squared = numbers.map { it * it }
    println("Squared: $squared")

    // filter — keep matching elements
    val evens = numbers.filter { it % 2 == 0 }
    println("Evens: $evens")

    // groupBy — group by a key
    val grouped = numbers.groupBy { if (it % 2 == 0) "even" else "odd" }
    println("Grouped: $grouped")

    // flatMap — flatten nested collections
    val nested = listOf(listOf(1, 2), listOf(3, 4))
    val flat = nested.flatMap { it }
    println("Flat: $flat")

    // Aggregate
    println("Sum: ${numbers.sum()}")
    println("Count: ${numbers.count()}")
    println("Max: ${numbers.maxOrNull()}")
    println("Average: ${numbers.average()}")

    // find
    val firstEven = numbers.find { it % 2 == 0 }
    println("First even: $firstEven")

    // partition — split into two lists
    val (even, odd) = numbers.partition { it % 2 == 0 }
    println("Even: $even, Odd: $odd")

    // fold
    val product = numbers.fold(1) { acc, n -> acc * n }
    println("Product (fold): $product")

    // sorted
    val unsorted = listOf(5, 2, 8, 1, 9)
    println("Sorted: ${unsorted.sorted()}")
    println("Sorted desc: ${unsorted.sortedDescending()}")

    // any / all / none
    println("Any > 5: ${numbers.any { it > 5 }}")
    println("All positive: ${numbers.all { it > 0 }}")
    println("None negative: ${numbers.none { it < 0 }}")

    // joinToString
    val joined = numbers.joinToString(", ", "(", ")")
    println("Joined: $joined")

    // Map operations
    val names = listOf("Alice", "Bob", "Charlie")
    val nameLengths = names.associateWith { it.length }
    println("Name lengths: $nameLengths")

    // chunked
    val chunks = numbers.chunked(3)
    println("Chunked: $chunks")

    // zip
    val zipped = names.zip(names.map { it.length })
    println("Zipped: $zipped")
}
```

### Output
```
Squared: [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
Evens: [2, 4, 6, 8, 10]
Grouped: {odd=[1, 3, 5, 7, 9], even=[2, 4, 6, 8, 10]}
Flat: [1, 2, 3, 4]
Sum: 55
Count: 10
Max: 10
Average: 5.5
First even: 2
Even: [2, 4, 6, 8, 10], Odd: [1, 3, 5, 7, 9]
Product (fold): 3628800
Sorted: [1, 2, 5, 8, 9]
Sorted desc: [9, 8, 5, 2, 1]
Any > 5: true
All positive: true
None negative: true
Joined: (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
Name lengths: {Alice=5, Bob=3, Charlie=7}
Chunked: [[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]
Zipped: [(Alice, 5), (Bob, 3), (Charlie, 7)]
```

---

## ❓ Interview Questions

1. **What is the difference between `List` and `MutableList` in Kotlin?**
   - `List` is read-only (cannot add/remove elements). `MutableList` allows modifications. Read-only interfaces don't guarantee immutability — they just prevent modification through that reference.

2. **What is the difference between `map` and `flatMap`?**
   - `map` transforms each element 1:1. `flatMap` transforms each element to a collection and then flattens the result into a single list.

3. **What is the difference between `fold` and `reduce`?**
   - `reduce` uses the first element as the initial accumulator. `fold` takes an explicit initial value, allowing different types and empty collections.

4. **What does `partition` do?**
   - Splits a collection into a `Pair` of two lists: one matching the predicate and one not.

5. **Are Kotlin's read-only collections truly immutable?**
   - No. They are read-only interfaces. The underlying implementation may be mutable. For true immutability, use libraries like Kotlinx Collections Immutable.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
- [OOP](OOP.md)
