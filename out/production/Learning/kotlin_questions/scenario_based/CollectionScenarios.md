# Collections & Data Processing Scenarios

## Scenario 1: Grouping and Aggregating Data

### Problem
You have a list of transactions and need to compute total amount per user, sorted by amount descending.

```kotlin
data class Transaction(val userId: String, val amount: Double, val category: String)

fun main() {
    val transactions = listOf(
        Transaction("u1", 50.0, "food"),
        Transaction("u2", 200.0, "electronics"),
        Transaction("u1", 30.0, "food"),
        Transaction("u3", 150.0, "books"),
        Transaction("u2", 80.0, "food"),
        Transaction("u1", 100.0, "electronics"),
        Transaction("u3", 25.0, "food")
    )

    // ✅ Solution: groupBy + sumOf + sortedByDescending
    val totalsByUser = transactions
        .groupBy { it.userId }
        .mapValues { (_, txns) -> txns.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
        .toMap()

    println("Total by user: $totalsByUser")
    // {u2=280.0, u1=180.0, u3=175.0}

    // ✅ Bonus: Total by category per user
    val byUserAndCategory = transactions
        .groupBy { it.userId to it.category }
        .mapValues { (_, txns) -> txns.sumOf { it.amount } }

    println("\nBy user and category:")
    byUserAndCategory.forEach { (key, total) ->
        println("  ${key.first} / ${key.second}: $total")
    }
    // u1 / food: 80.0
    // u2 / electronics: 200.0
    // u1 / electronics: 100.0
    // u3 / books: 150.0
    // u2 / food: 80.0
    // u3 / food: 25.0
}
```

### Key Takeaway
- `groupBy` creates `Map<K, List<V>>`
- `mapValues` transforms values while keeping keys
- `toList().sortedByDescending().toMap()` for sorted maps
- Pair as composite key for multi-level grouping

---

## Scenario 2: Finding Duplicates

### Problem
Find duplicate elements in a list and their count.

```kotlin
fun main() {
    val items = listOf("apple", "banana", "apple", "cherry", "banana", "apple", "date")

    // ✅ Solution 1: groupBy + filter
    val duplicates1 = items
        .groupBy { it }
        .filter { (_, list) -> list.size > 1 }
        .mapValues { it.value.size }

    println("Duplicates: $duplicates1")  // {apple=3, banana=2}

    // ✅ Solution 2: groupingBy + eachCount (more efficient)
    val duplicates2 = items
        .groupingBy { it }
        .eachCount()
        .filter { it.value > 1 }

    println("Duplicates: $duplicates2")  // {apple=3, banana=2}

    // ✅ Solution 3: Find just the duplicate values (no count)
    val dupValues = items.groupingBy { it }.eachCount()
        .filter { it.value > 1 }.keys

    println("Duplicate values: $dupValues")  // [apple, banana]

    // ✅ Solution 4: Remove duplicates keeping first occurrence
    val unique = items.toMutableList()
    val seen = mutableSetOf<String>()
    val iterator = unique.iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        if (!seen.add(item)) iterator.remove()
    }
    println("Unique: $unique")  // [apple, banana, cherry, date]

    // ✅ Solution 5: Simple distinct
    println("Distinct: ${items.distinct()}")  // [apple, banana, cherry, date]
}
```

### Key Takeaway
- `groupingBy { }.eachCount()` is the most efficient for counting
- `filter { it.value > 1 }` to find duplicates
- `distinct()` removes duplicates keeping first occurrence

---

## Scenario 3: Chunking and Batching

### Problem
Process a large list in batches of 100 items (e.g., for API pagination).

```kotlin
fun main() {
    val allItems = (1..250).toList()

    // ✅ Solution 1: chunked
    val batches = allItems.chunked(100)
    println("Number of batches: ${batches.size}")  // 3

    batches.forEachIndexed { index, batch ->
        println("Batch ${index + 1}: ${batch.size} items (${batch.first()}-${batch.last()})")
    }
    // Batch 1: 100 items (1-100)
    // Batch 2: 100 items (101-200)
    // Batch 3: 50 items (201-250)

    // ✅ Solution 2: windowed (sliding window)
    val numbers = listOf(1, 2, 3, 4, 5)
    val windows = numbers.windowed(3, step = 1)
    println("Windows: $windows")  // [[1,2,3], [2,3,4], [3,4,5]]

    // ✅ Solution 3: windowed with partial windows
    val partialWindows = numbers.windowed(3, step = 1, partialWindows = true)
    println("Partial: $partialWindows")  // [[1,2,3], [2,3,4], [3,4,5], [4,5], [5]]

    // ✅ Solution 4: Process batches with index
    allItems.chunked(100).forEachIndexed { batchIndex, batch ->
        // api.sendBatch(batchIndex, batch)
        println("Sending batch $batchIndex with ${batch.size} items")
    }
}
```

### Key Takeaway
- `chunked(n)` splits into fixed-size lists
- `windowed(size, step)` creates sliding windows
- `partialWindows = true` includes partial windows at the end
- Use `chunked` for pagination, `windowed` for moving averages

---

## Scenario 4: Flattening Nested Collections

### Problem
You have a list of departments, each with employees. Get a flat list of all employee names.

```kotlin
data class Department(val name: String, val employees: List<Employee>)
data class Employee(val name: String, val salary: Double)

fun main() {
    val departments = listOf(
        Department("Engineering", listOf(
            Employee("Alice", 90000.0),
            Employee("Bob", 85000.0)
        )),
        Department("Sales", listOf(
            Employee("Charlie", 70000.0)
        )),
        Department("HR", listOf(
            Employee("Diana", 75000.0),
            Employee("Eve", 72000.0)
        ))
    )

    // ✅ Solution 1: flatMap
    val allEmployees = departments.flatMap { it.employees }
    println("All employees: ${allEmployees.map { it.name }}")
    // [Alice, Bob, Charlie, Diana, Eve]

    // ✅ Solution 2: flatten (for List<List<T>>)
    val nestedLists = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
    println("Flattened: ${nestedLists.flatten()}")  // [1, 2, 3, 4, 5]

    // ✅ Solution 3: flatMap with filter — high earners
    val highEarners = departments
        .flatMap { it.employees }
        .filter { it.salary > 75000 }
        .sortedByDescending { it.salary }

    println("\nHigh earners:")
    highEarners.forEach { println("  ${it.name}: $${it.salary}") }
    // Alice: $90000.0
    // Bob: $85000.0
    // Diana: $75000.0

    // ✅ Solution 4: Associate by name
    val byName = departments
        .flatMap { it.employees }
        .associateBy { it.name }

    println("\nLookup Alice: ${byName["Alice"]}")  // Employee(name=Alice, salary=90000.0)
}
```

### Key Takeaway
- `flatMap` transforms each element to a collection and flattens
- `flatten` just flattens `List<List<T>>` without transformation
- `associateBy` creates a lookup map by key

---

## Scenario 5: Building a Pipeline

### Problem
Process a log file: parse, filter errors, group by error code, count, and format.

```kotlin
data class LogEntry(
    val timestamp: String,
    val level: String,
    val message: String
)

fun main() {
    val logs = listOf(
        LogEntry("2024-01-01 10:00", "INFO", "App started"),
        LogEntry("2024-01-01 10:01", "ERROR", "DB connection failed [ERR_001]"),
        LogEntry("2024-01-01 10:02", "WARN", "Slow query"),
        LogEntry("2024-01-01 10:03", "ERROR", "Auth failed [ERR_002]"),
        LogEntry("2024-01-01 10:04", "ERROR", "DB timeout [ERR_001]"),
        LogEntry("2024-01-01 10:05", "INFO", "User logged in"),
        LogEntry("2024-01-01 10:06", "ERROR", "Memory leak [ERR_003]"),
        LogEntry("2024-01-01 10:07", "ERROR", "DB retry [ERR_001]")
    )

    // ✅ Solution: Functional pipeline
    val errorSummary = logs
        .filter { it.level == "ERROR" }
        .mapNotNull { entry ->
            val code = Regex("\\[ERR_\\d+\\]").find(entry.message)?.value
            if (code != null) entry to code else null
        }
        .groupBy({ it.second }, { it.first })
        .mapValues { (_, entries) ->
            mapOf(
                "count" to entries.size,
                "firstOccurrence" to entries.first().timestamp,
                "lastOccurrence" to entries.last().timestamp
            )
        }
        .toList()
        .sortedByDescending { (_, info) -> info["count"] as Int }
        .toMap()

    println("Error Summary:")
    errorSummary.forEach { (code, info) ->
        println("  $code: ${info["count"]} times (${info["firstOccurrence"]} → ${info["lastOccurrence"]})")
    }
    // [ERR_001]: 3 times (2024-01-01 10:01 → 2024-01-01 10:07)
    // [ERR_002]: 1 times (2024-01-01 10:03 → 2024-01-01 10:03)
    // [ERR_003]: 1 times (2024-01-01 10:06 → 2024-01-01 10:06)
}
```

### Key Takeaway
- Chain `filter → map → groupBy → mapValues → sortedBy` for data pipelines
- `mapNotNull` filters and transforms in one step
- `Regex` for pattern extraction
- Each step is a pure function — easy to test and debug

---

## 🔗 Related Topics
- [Collections](../intermediate/Collections.md)
- [Lambdas & Higher-Order Functions](../intermediate/LambdasAndHigherOrderFunctions.md)
