# Coroutines Deep Dive

## 📖 Explanation

Coroutines are Kotlin's solution for **asynchronous, non-blocking programming**. They are lightweight threads — you can launch thousands of coroutines on a single thread.

### Key Concepts

| Concept          | Description                                              |
|------------------|----------------------------------------------------------|
| Coroutine        | A suspendable computation unit                          |
| `suspend` fun    | A function that can suspend and resume                  |
| CoroutineScope   | Manages coroutine lifetimes                              |
| Dispatcher       | Determines which thread(s) the coroutine runs on        |
| Job              | Represents a cancellable unit of work                   |
| Deferred         | A `Job` that produces a result (like a Future/Promise)  |

### Dispatchers
| Dispatcher        | Use Case                                    |
|-------------------|---------------------------------------------|
| `Dispatchers.Main`| UI thread (Android)                         |
| `Dispatchers.IO`  | Blocking I/O (network, file, database)     |
| `Dispatchers.Default` | CPU-intensive work                     |
| `Dispatchers.Unconfined` | Runs on current thread, then resumes wherever it left off |

### Coroutine Builders
- `launch` — Fire-and-forget, returns a `Job`.
- `async` — Returns a `Deferred<T>`, use `await()` for the result.
- `runBlocking` — Blocks the current thread (mainly for testing).

### `suspend` Functions
Can be called only from a coroutine or another suspend function.

```kotlin
suspend fun fetchData(): String {
    delay(1000)
    return "Data"
}
```

### Structured Concurrency
Coroutines are launched within a `CoroutineScope`. When the scope is cancelled, all child coroutines are cancelled too.

### Exception Handling
- `CoroutineExceptionHandler` — Top-level uncaught exception handler.
- `SupervisorJob` — Children fail independently.
- `try/catch` inside coroutines works for `async`/`launch`.

---

## 🧪 Code Example

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // launch — fire and forget
    launch {
        delay(500)
        println("Task 1 done")
    }

    // async — returns a result
    val deferred = async {
        delay(300)
        "Async result"
    }
    println("Awaiting: ${deferred.await()}")

    // Concurrent execution
    val time = measureTimeMillis {
        val result1 = async { fetchData("API-1", 1000) }
        val result2 = async { fetchData("API-2", 1500) }
        println("Both done: ${result1.await()}, ${result2.await()}")
    }
    println("Concurrent time: ${time}ms")

    // withContext — switch dispatcher
    val data = withContext(Dispatchers.Default) {
        // CPU-intensive work
        (1..1_000_000).sum()
    }
    println("Sum: $data")

    // SupervisorJob — children fail independently
    val supervisor = CoroutineScope(SupervisorJob())
    supervisor.launch {
        delay(100)
        println("Supervisor child 1 done")
    }
    supervisor.launch {
        throw RuntimeException("Child 2 failed")
    }
    supervisor.launch {
        delay(200)
        println("Supervisor child 3 done")
    }
    delay(500)

    // Cancellation
    val job = launch {
        repeat(10) { i ->
            delay(200)
            println("Working $i")
        }
    }
    delay(500)
    job.cancelAndJoin()
    println("Job cancelled")

    // Exception handling
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught: $exception")
    }
    launch(handler) {
        throw RuntimeException("Oops!")
    }
    delay(100)
}

suspend fun fetchData(source: String, delayMs: Long): String {
    delay(delayMs)
    return "$source:OK"
}

fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}
```

### Output
```
Awaiting: Async result
Both done: API-1:OK, API-2:OK
Concurrent time: 1501ms
Sum: 1784293664
Supervisor child 1 done
Supervisor child 3 done
Working 0
Working 1
Job cancelled
Caught: java.lang.RuntimeException: Oops!
Task 1 done
```

---

## ❓ Interview Questions

1. **What is a coroutine and how does it differ from a thread?**
   - Coroutines are lightweight, suspendable computations. They don't block threads — they suspend and resume. You can have thousands of coroutines on a single thread. Threads are heavier OS-level constructs.

2. **What is the difference between `launch` and `async`?**
   - `launch` returns a `Job` (fire-and-forget). `async` returns a `Deferred<T>` — call `await()` to get the result. Use `async` when you need a return value.

3. **What is structured concurrency?**
   - Coroutines are launched within a scope. Parent-child relationships ensure that when a parent is cancelled, all children are cancelled. No orphan coroutines.

4. **What is `withContext` used for?**
   - It switches the coroutine to a different dispatcher and returns a result. E.g., `withContext(Dispatchers.IO) { readFile() }`.

5. **What is the difference between `SupervisorJob` and `Job`?**
   - With a regular `Job`, if one child fails, all siblings are cancelled. With `SupervisorJob`, children fail independently — one failure doesn't affect others.

---

## 🔗 Related Topics
- [Flows](Flows.md)
- [Lambdas & Higher-Order Functions](../intermediate/LambdasAndHigherOrderFunctions.md)
