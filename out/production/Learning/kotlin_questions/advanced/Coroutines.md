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

6. **What is a `CoroutineExceptionHandler` and how do you use it?**
   - `CoroutineExceptionHandler` is a coroutine context element that handles uncaught exceptions in coroutines. It's the last-resort handler — similar to a global `Thread.uncaughtExceptionHandler`. Usage: `val handler = CoroutineExceptionHandler { _, exception -> Log.e("TAG", "Caught: $exception") }; viewModelScope.launch(handler) { throw RuntimeException("Oops") }`. Key points: (1) It only works for `launch` — NOT `async` (async exceptions are stored in `Deferred` and thrown on `await()`). (2) It only catches exceptions that are NOT caught by the coroutine itself — if you have try/catch in the coroutine, the handler is never called. (3) It only works for uncaught exceptions in root coroutines (coroutines without a parent). (4) You can also use `supervisorScope` or `SupervisorJob` to prevent child failure from cancelling siblings. (5) In structured concurrency, the handler is typically set on the root coroutine. (6) For ViewModel, use `viewModelScope` with a custom exception handler or catch in the coroutine body.

7. **What is the difference between `Dispatchers.IO`, `Dispatchers.Default`, and `Dispatchers.Main`?**
   - `Dispatchers.Main` — runs on the Android main/UI thread. Use for UI updates, lifecycle operations, and lightweight work. All Android UI operations must be on this thread. Backed by a single thread. `Dispatchers.IO` — optimized for blocking I/O operations (file, network, database). Backed by a pool of 64 threads (configurable via `kotlinx.coroutines.io.parallelism`). Use for Retrofit calls, file reads/writes, blocking SDK calls. Room with `suspend` automatically uses IO. Never do CPU-intensive work here — use `Default` instead. `Dispatchers.Default` — optimized for CPU-intensive work (parsing, sorting, calculations, image processing). Backed by a pool of threads equal to the CPU core count (`Runtime.getRuntime().availableProcessors()`). Use for heavy computation, JSON parsing, sorting large lists. `Dispatchers.Unconfined` — runs on the calling thread, then resumes wherever it was suspended. Rarely used — not recommended for production. Rule: UI → Main, I/O → IO, CPU → Default. Switch with `withContext(Dispatchers.IO) { ... }`.

8. **How does coroutine cancellation work and what is cooperative cancellation?**
   - Coroutines support **cooperative cancellation** — a coroutine is only cancelled if it checks for cancellation. When you call `job.cancel()`, the coroutine's `Job` is marked as cancelled, but the coroutine continues running until it hits a **cancellation point** — a suspending function like `delay()`, `await()`, `withContext()`. If the coroutine does CPU-intensive work without any suspend calls, it won't respond to cancellation. To make coroutines cooperative: (1) Use `ensureActive()` or `isActive` to check cancellation: `if (!isActive) return`. (2) Use `yield()` periodically to allow cancellation. (3) Use suspending functions that check cancellation (`delay`, `withContext`). (4) Close resources in `finally` blocks or use `use { }`. (5) For `InputStream`/`OutputStream`, use `withContext(NonCancellable) { stream.close() }` in `finally` to ensure cleanup even when cancelled. Cancellation is **structured** — cancelling a parent cancels all children. `cancelAndJoin()` cancels and waits for completion.

9. **What is the difference between `withContext` and `async`/`await`?**
   - `withContext(dispatcher) { ... }` — switches the coroutine to a different dispatcher, executes the block, and returns the result. It's sequential — the calling coroutine suspends until the block completes. Use for switching threads (e.g., IO to Main): `val data = withContext(Dispatchers.IO) { fetchData() }; updateUI(data)`. `async { ... }` — starts a **concurrent** coroutine that returns `Deferred<T>`. The work starts immediately (or when `start()` is called). `await()` suspends until the result is ready. Use for parallel execution: `val deferred1 = async { fetchUser() }; val deferred2 = async { fetchPosts() }; val user = deferred1.await(); val posts = deferred2.await()`. Key differences: (1) `withContext` is sequential, `async` is concurrent. (2) `withContext` returns the result directly, `async` returns a `Deferred` you must `await`. (3) `withContext` is for thread switching, `async` is for parallelism. (4) `withContext` always waits for completion, `async` starts work immediately. Use `withContext` for "do this on another thread and wait", `async` for "start this and continue while it runs."

10. **How do you handle timeouts in coroutines?**
    - Use `withTimeout(duration) { ... }` — throws `TimeoutCancellationException` (a subclass of `CancellationException`) if the block doesn't complete in time. Example: `try { withTimeout(5000) { fetchData() } } catch (e: TimeoutCancellationException) { showTimeoutError() }`. For a nullable result instead of an exception, use `withTimeoutOrNull(duration) { ... }` — returns `null` on timeout: `val data = withTimeoutOrNull(5000) { fetchData() } ?: cachedData`. Best practices: (1) Use `withTimeout` for network calls, database queries, and any operation that might hang. (2) Always handle the timeout — either with try/catch or `withTimeoutOrNull`. (3) The timeout cancels the coroutine — resources are cleaned up via `finally` blocks. (4) For Retrofit, set both OkHttp timeout (for network-level timeout) and `withTimeout` (for coroutine-level timeout). (5) `withTimeout` is a suspending function — it works with structured concurrency and respects cancellation.

11. **What is a `CoroutineScope` and why is it important for structured concurrency?**
    - A `CoroutineScope` is a context that manages coroutines and ensures they don't leak. Every coroutine must be launched in a scope. The scope defines: (1) The `Job` hierarchy — parent-child relationships. (2) The `CoroutineDispatcher` — which thread the coroutine runs on. (3) The lifecycle — when the scope is cancelled, all coroutines in it are cancelled. Built-in scopes: `viewModelScope` (tied to ViewModel lifecycle), `lifecycleScope` (tied to Activity/Fragment lifecycle), `GlobalScope` (never cancelled — avoid). Structured concurrency means: (1) Coroutines are launched in a scope, not "fire-and-forget". (2) When a scope is cancelled, all child coroutines are cancelled. (3) A parent waits for all children to complete before completing. (4) If a child fails, the parent (and siblings) are notified. This prevents leaks — you can't have orphaned coroutines running after the scope is destroyed. Never use `GlobalScope` — it has no lifecycle and causes leaks.

12. **What are common coroutine pitfalls and how to avoid them?**
    - (1) **Using `GlobalScope`** — causes leaks. Fix: use `viewModelScope` or `lifecycleScope`. (2) **Not handling exceptions** — uncaught exceptions crash the app. Fix: try/catch or `CoroutineExceptionHandler`. (3) **Blocking the main thread** — calling `Thread.sleep()` or blocking I/O on `Dispatchers.Main`. Fix: use `withContext(Dispatchers.IO)`. (4) **Not using `withContext` for thread switching** — calling suspend functions that don't switch dispatchers. Fix: explicitly switch with `withContext`. (5) **Catching `CancellationException`** — swallowing cancellation prevents cleanup. Fix: only catch specific exceptions, re-throw `CancellationException`. (6) **Using `runBlocking` in production** — blocks the thread. Fix: use `runTest` in tests, proper scopes in production. (7) **Not cancelling coroutines** — long-running coroutines outlive their scope. Fix: structured concurrency with proper scopes. (8) **Using `async` without `await`** — fire-and-forget `async` that swallows exceptions. Fix: use `launch` for fire-and-forget, `async` only when you need the result. (9) **Nested `withContext` calls** — unnecessary dispatcher switches. Fix: batch work in a single `withContext` block.

---

## 🔗 Related Topics
- [Flows](Flows.md)
- [Lambdas & Higher-Order Functions](../intermediate/LambdasAndHigherOrderFunctions.md)
