# Performance & Memory Scenarios

## Scenario 1: Sequence vs List for Large Data

### Problem
Processing 1 million items with `map` and `filter` is slow and uses too much memory.

```kotlin
import kotlin.system.measureTimeMillis

fun main() {
    val largeList = (1..1_000_000).toList()

    // ❌ Bad — creates intermediate lists at each step
    val listTime = measureTimeMillis {
        val result = largeList
            .map { it * 2 }
            .filter { it % 3 == 0 }
            .take(100)
            .toList()
        println("List result size: ${result.size}")
    }
    println("List time: ${listTime}ms")

    // ✅ Good — Sequence processes lazily, one item at a time
    val seqTime = measureTimeMillis {
        val result = largeList.asSequence()
            .map { it * 2 }
            .filter { it % 3 == 0 }
            .take(100)
            .toList()
        println("Sequence result size: ${result.size}")
    }
    println("Sequence time: ${seqTime}ms")
}
```

### Key Takeaway
- List operations create intermediate lists (3 lists for map → filter → take)
- `Sequence` processes items one at a time through the pipeline — no intermediate lists
- Use `asSequence()` for large collections (>1000 items) with multiple operations
- For small collections, List is faster (no sequence overhead)

---

## Scenario 2: Inline Functions for Lambda Performance

### Problem
Higher-order functions with lambdas create object allocations on every call.

```kotlin
import kotlin.system.measureNanoTime

// ❌ Without inline — creates lambda object each call
fun noInlineRepeat(times: Int, action: (Int) -> Unit) {
    for (i in 0 until times) action(i)
}

// ✅ With inline — lambda body is copied to call site, no allocation
inline fun inlineRepeat(times: Int, action: (Int) -> Unit) {
    for (i in 0 until times) action(i)
}

fun main() {
    val iterations = 1_000_000

    val noInlineTime = measureNanoTime {
        noInlineRepeat(iterations) { /* do nothing */ }
    }

    val inlineTime = measureNanoTime {
        inlineRepeat(iterations) { /* do nothing */ }
    }

    println("Without inline: ${noInlineTime / 1_000_000}ms")
    println("With inline: ${inlineTime / 1_000_000}ms")
    // Inline is significantly faster — no lambda object allocation
}
```

### Key Takeaway
- `inline` copies function body + lambda to call site — no lambda object
- Use for small, frequently-called higher-order functions
- Don't inline large functions (code bloat)
- Standard library functions like `forEach`, `map`, `let` are already inline

---

## Scenario 3: Memory Leak with Captured References

### Problem
A lambda captures a large object, preventing GC even after it's "done".

```kotlin
class BigData(val data: ByteArray = ByteArray(10_000_000))  // 10MB

class Cache {
    private var callback: (() -> Unit)? = null

    fun setCallback(cb: () -> Unit) {
        callback = cb
    }

    fun clear() {
        callback = null
    }
}

fun main() {
    val cache = Cache()

    // ❌ Bad — lambda captures bigData, keeping it alive
    var bigData: BigData? = BigData()
    cache.setCallback {
        println("Data size: ${bigData!!.data.size}")
    }
    bigData = null  // bigData object is still alive — captured by lambda!
    println("BigData still referenced by callback")

    // ✅ Solution: Use WeakReference or clear callback
    cache.clear()  // Now BigData can be GC'd

    // ✅ Better: Don't capture large objects in long-lived callbacks
    val cache2 = Cache()
    val bigData2 = BigData()
    val size = bigData2.data.size  // Extract what you need
    cache2.setCallback {
        println("Data size: $size")  // Only captures Int, not BigData
    }
    // bigData2 can be GC'd now
}
```

### Key Takeaway
- Lambdas capture variables by reference — keeps objects alive
- Extract only needed values before passing to long-lived callbacks
- Clear callbacks when done
- Use `WeakReference` for optional references

---

## Scenario 4: Array vs List Performance

### Problem
Choosing between `Array` and `List` for performance-critical code.

```kotlin
import kotlin.system.measureNanoTime

fun main() {
    val size = 1_000_000

    // Array — primitive storage, no boxing for IntArray
    val intArray = IntArray(size) { it }
    val array = Array(size) { it }

    // List — object-based
    val list = (1..size).toList()

    // ✅ IntArray is fastest for primitives (no boxing)
    val intArrayTime = measureNanoTime {
        var sum = 0
        for (i in 0 until intArray.size) {
            sum += intArray[i]
        }
    }

    val arrayTime = measureNanoTime {
        var sum = 0
        for (i in array.indices) {
            sum += array[i]
        }
    }

    val listTime = measureNanoTime {
        var sum = 0
        for (i in list.indices) {
            sum += list[i]
        }
    }

    println("IntArray: ${intArrayTime / 1_000_000}ms (no boxing)")
    println("Array<Int>: ${arrayTime / 1_000_000}ms")
    println("List<Int>: ${listTime / 1_000_000}ms")
    // IntArray is fastest — no Integer object overhead
}
```

### Key Takeaway
| Type         | Best For                          | Boxing |
|--------------|-----------------------------------|--------|
| `IntArray`   | Large primitive arrays            | No     |
| `Array<T>`   | Object arrays, fixed size         | Yes    |
| `List<T>`    | General use, flexible size        | Yes    |
| `MutableList`| When you need add/remove          | Yes    |

- Use `IntArray`, `LongArray`, etc. for primitives — no boxing
- Use `List` for general code — more flexible, readable
- Use `Array` only for fixed-size, performance-critical code

---

## Scenario 5: Lazy Initialization for Expensive Resources

### Problem
Creating all resources upfront slows app startup, even if some are never used.

```kotlin
import kotlin.system.measureTimeMillis

class ExpensiveResource(val name: String) {
    init {
        println("  Initializing $name...")
        Thread.sleep(500)  // Simulate expensive init
    }

    fun use() = println("  Using $name")
}

class App {
    // ❌ Bad — all created at construction
    val eagerDb = ExpensiveResource("Database")
    val eagerCache = ExpensiveResource("Cache")
    val eagerAnalytics = ExpensiveResource("Analytics")

    fun doWork() {
        eagerDb.use()
        // Cache and Analytics never used but still initialized
    }
}

class LazyApp {
    // ✅ Good — only initialized on first access
    val db by lazy { ExpensiveResource("Database") }
    val cache by lazy { ExpensiveResource("Cache") }
    val analytics by lazy { ExpensiveResource("Analytics") }

    fun doWork() {
        db.use()
        // Cache and Analytics never initialized — saved 1 second!
    }
}

fun main() {
    println("=== Eager (all initialized) ===")
    val eagerTime = measureTimeMillis {
        val app = App()
        app.doWork()
    }
    println("Eager total: ${eagerTime}ms\n")

    println("=== Lazy (only needed ones) ===")
    val lazyTime = measureTimeMillis {
        val app = LazyApp()
        app.doWork()
    }
    println("Lazy total: ${lazyTime}ms")
    // Eager: ~1500ms (3 resources x 500ms)
    // Lazy: ~500ms (only Database)
}
```

### Key Takeaway
- `lazy` defers initialization until first access
- Thread-safe by default (`LazyThreadSafetyMode.SYNCHRONIZED`)
- Use for resources that may never be needed
- Use `lazy(LazyThreadSafetyMode.NONE)` for single-threaded code (faster)

---

## 🔗 Related Topics
- [Inline Functions & Reified Types](../intermediate/InlineAndReified.md)
- [Collections](../intermediate/Collections.md)
- [Coroutines Deep Dive](../advanced/Coroutines.md)
