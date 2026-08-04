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

6. **What is the difference between `listOf`, `mutableListOf`, `arrayListOf`, and `buildList`?**
   - `listOf(1, 2, 3)` — creates a read-only `List<T>`. The underlying implementation is `Arrays$ArrayList` (a fixed-size adapter). You cannot add/remove elements through the `List` interface, but the list is not truly immutable — casting to `MutableList` may work (but is unsafe). `mutableListOf(1, 2, 3)` — creates a `MutableList<T>` backed by `ArrayList`. Supports add/remove/modify. `arrayListOf()` — explicitly creates an `ArrayList` (Java's `ArrayList`). Same as `mutableListOf` but explicitly typed. `buildList { add(1); add(2) }` — creates a read-only list using a builder lambda. The lambda receives a `MutableList` for building, and returns a read-only `List`. This is the preferred way to construct complex lists immutably. Use `listOf` for simple lists, `buildList` for conditional construction, and `mutableListOf` when you need to modify the list later.

7. **What are `associate`, `groupBy`, and `associateBy` and how do they differ?**
   - `associate` — transforms each element to a `Pair<K, V>` and creates a `Map<K, V>`: `list.associate { it.id to it.name }`. If keys duplicate, the last one wins. `associateBy` — creates a map keyed by a selector function: `list.associateBy { it.id }` → `Map<Int, User>`. More concise than `associate` when the value is the element itself. `associateBy(keySelector, valueTransform)` — both key and value transformed: `list.associateBy({ it.id }, { it.name })`. `groupBy` — creates `Map<K, List<V>>` — groups elements by key, preserving all values per key: `list.groupBy { it.category }` → `Map<Category, List<Item>>`. Unlike `associateBy` (which keeps the last value per key), `groupBy` keeps ALL values. Use `associate`/`associateBy` for 1:1 mapping (key → value), `groupBy` for 1:N mapping (key → list of values). For counting per group: `groupingBy { it.category }.eachCount()`.

8. **What is the difference between `flatMap`, `flatten`, and `flatMap` with `mapNotNull`?**
   - `flatten` — takes a list of lists and flattens into a single list: `listOf(listOf(1, 2), listOf(3, 4)).flatten()` → `[1, 2, 3, 4]`. No transformation, just flattening. `flatMap` — transforms each element to an iterable and then flattens: `list.flatMap { it.children }` — equivalent to `list.map { it.children }.flatten()` but more efficient (single pass). Use `flatMap` when each element produces multiple results. `mapNotNull` + `flatten` — if some elements should be skipped: `list.mapNotNull { it?.children }.flatten()`. Use `flatMap` when every element maps to a collection. Use `mapNotNull` when some elements might be null and should be skipped. `flatMap` is one of the most powerful collection operations — it's the monadic `bind` operation. Example: parsing words from lines: `lines.flatMap { it.split(" ") }`.

9. **What are `windowed`, `chunked`, and `zip` and when do you use them?**
    - `chunked(size)` — splits a collection into chunks of the given size: `list.chunked(2)` → `[[1, 2], [3, 4], [5]]`. Use for pagination, batch processing. `windowed(size, step, partialWindows)` — creates sliding windows: `list.windowed(3)` → `[[1, 2, 3], [2, 3, 4], [3, 4, 5]]`. Use for moving averages, N-grams, or comparing adjacent elements. `windowed(2)` with step 1 gives pairs of consecutive elements. `zip` — combines two collections element-by-element into Pairs: `names.zip(ages)` → `[(Alice, 30), (Bob, 25)]`. Stops at the shorter collection. Use `zipWithNext()` for consecutive pairs: `list.zipWithNext()` → `[(1, 2), (2, 3), (3, 4)]`. Use cases: `chunked` for batching, `windowed` for sliding analysis, `zip` for combining parallel arrays, `zipWithNext` for comparing neighbors.

10. **What is the difference between `first`, `firstOrNull`, `firstOrElse`, and `getOrElse`?**
    - `first()` — returns the first element, throws `NoSuchElementException` if empty. Use when you're certain the collection is non-empty. `firstOrNull()` — returns the first element or `null` if empty or no match. Safe version of `first()`. `first { predicate }` — returns the first matching element, throws if none. `firstOrNull { predicate }` — returns first match or null. `firstOrElse(default)` — returns first or the default value. `getOrElse(index) { default }` — for index-based access: `list.getOrElse(5) { -1 }`. Use `first()` when empty is an error condition. Use `firstOrNull()` when empty is a valid case. Use `getOrElse` for safe index access. Kotlin also has `single()` (exactly one element, throws otherwise) and `singleOrNull()`. For collections, prefer `firstOrNull` + `?:` (elvis) over `firstOrNull` + `if` for providing defaults: `list.firstOrNull() ?: defaultValue`.

11. **What are sequences and how do they differ from collections?**
    - Sequences (`Sequence<T>`) are lazily evaluated — elements are processed one at a time through the chain, like Java Streams. Collections are eagerly evaluated — each operation creates a new collection. Example: `list.map { it * 2 }.filter { it > 5 }.first()` — with a List, `map` processes ALL elements, then `filter` processes ALL, then `first` takes the first. With `list.asSequence().map { it * 2 }.filter { it > 5 }.first()` — it processes elements one by one until the first match is found, then stops. Use sequences for: (1) Large collections — avoids intermediate collections. (2) Early termination (find first, take 10). (3) Infinite sequences: `generateSequence(1) { it + 1 }`. Convert to sequence with `asSequence()`, back to list with `toList()`. Sequences are not always faster — for small collections, the overhead of sequence creation outweighs the benefit. Rule of thumb: use sequences when you have 1000+ elements or when the chain has 3+ operations with early termination.

12. **How do you sort and compare collections in Kotlin?**
    - (1) `sorted()` — natural order (Comparable). `sortedDescending()`. (2) `sortedBy { selector }` — sort by a property: `users.sortedBy { it.name }`. (3) `sortedByDescending { it.age }`. (4) `sortedWith(comparator)` — custom comparator: `sortedWith(compareBy({ it.lastName }, { it.firstName }))`. (5) `compareBy` — chain multiple criteria: `compareBy<User> { it.age }.thenBy { it.name }`. (6) `sortedWith(compareByDescending<User> { it.age })`. (7) In-place sorting: `mutableList.sortBy { it.name }` (modifies the list). (8) `distinctBy { it.id }` — remove duplicates by key. (9) `maxByOrNull { it.age }` / `minByOrNull { it.age }` — find max/min. (10) Natural order requires `Comparable`: `data class User(val name: String) : Comparable<User> { override fun compareTo(other: User) = name.compareTo(other.name) }`. Use `compareBy` for multi-criteria sorting. Always prefer `sortedBy` over manual comparators when possible.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
- [OOP](OOP.md)
