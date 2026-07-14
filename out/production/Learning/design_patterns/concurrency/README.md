# Concurrency Patterns

Concurrency patterns deal with **multi-threaded programming** — coordinating multiple threads, sharing data safely, and maximizing throughput while avoiding race conditions, deadlocks, and resource exhaustion.

---

## 1. Thread Pool

### Intent
Maintain a pool of worker threads that execute tasks from a queue. Reuse threads instead of creating/destroying them per task.

### Problem It Solves
- **Thread creation is expensive**: ~1-10 ms per thread. At 10,000 tasks/sec, creating a thread per task wastes 10-100 seconds of CPU per second.
- **Unbounded threads**: Creating a thread per task → 10,000 threads → OOM, context switching overhead, system freeze.
- **Thread pool solves both**: reuse threads (no creation overhead) and bound the count (no OOM).

### Structure
```kotlin
import java.util.concurrent.*

// Kotlin/Java: use ExecutorService
val threadPool: ExecutorService = Executors.newFixedThreadPool(8)

// Submit tasks
val futures = (1..100).map { i ->
    threadPool.submit Callable {
        println("Task $i running on ${Thread.currentThread().name}")
        "Result-$i"
    }
}

// Get results
futures.forEach { println(it.get()) }

// Shutdown
threadPool.shutdown()
```

### Pool Types

| Pool Type | Behavior | When to Use |
|---|---|---|
| **Fixed** | N threads, unbounded queue | Known parallelism, CPU-bound tasks |
| **Cached** | 0 to Integer.MAX threads, SynchronousQueue | Many short-lived tasks |
| **Scheduled** | N threads, delayed/periodic tasks | Cron jobs, timers |
| **Work-stealing** | N threads, each has a deque, steals from others | Fork-join, recursive tasks |

### Pool Sizing (The Critical Decision)
```
CPU-bound tasks:  threads = CPU cores (or cores + 1)
I/O-bound tasks:   threads = cores × (1 + wait_time / compute_time)
                   ≈ cores × 50 (for heavy I/O)
```

**Why?**
- CPU-bound: More threads than cores → context switching → slower.
- I/O-bound: Threads spend most time waiting (DB, network). More threads = more concurrent I/O = higher throughput.

### Rejection Policies (When Queue is Full)
```kotlin
// When the pool is saturated, what happens to new tasks?
val pool = ThreadPoolExecutor(
    8,                          // core pool size
    8,                          // max pool size
    60L, TimeUnit.SECONDS,      // keep-alive for idle threads
    LinkedBlockingQueue(100),   // bounded queue (100 tasks)
    ThreadPoolExecutor.CallerRunsPolicy()  // rejection policy
)

// Rejection policies:
// AbortPolicy (default) → throw RejectedExecutionException
// CallerRunsPolicy → run on the caller's thread (backpressure!)
// DiscardPolicy → silently drop
// DiscardOldestPolicy → drop oldest queued task, add new
```

### Key Insight
> **Thread pool is the most important concurrency pattern. It's behind every web server (Tomcat, Netty), every async framework (Kotlin coroutines, Java CompletableFuture), and every database connection pool. The critical configuration is pool size: too small → underutilized CPU; too large → context switching. For I/O-bound work, use more threads than cores. For CPU-bound, use exactly cores.**

---

## 2. Producer-Consumer

### Intent
Decouple producers (that generate data) from consumers (that process data) using a shared queue. Producers and consumers run at different speeds.

### Problem It Solves
- Producer generates 10,000 items/sec. Consumer processes 1,000 items/sec.
- Without a queue: producer overwhelms consumer → data loss or consumer crash.
- With a queue: queue buffers the excess. Consumer processes at its own pace.
- **Load leveling**: smooth out bursts.

### Structure
```kotlin
import java.util.concurrent.*

// Bounded queue: blocks when full (backpressure on producer)
val queue = ArrayBlockingQueue<String>(100)

// Producer
val producer = Thread {
    repeat(1000) { i ->
        queue.put("Item-$i")  // blocks if queue is full
        println("Produced: Item-$i")
    }
    queue.put("POISON")  // sentinel to signal end
}

// Consumer
val consumer = Thread {
    while (true) {
        val item = queue.take()  // blocks if queue is empty
        if (item == "POISON") break
        println("Consumed: $item")
        Thread.sleep(10)  // simulate slow processing
    }
}

producer.start()
consumer.start()
producer.join()
consumer.join()
```

### Multiple Producers and Consumers
```kotlin
val queue = ArrayBlockingQueue<Task>(1000)
val producerCount = 3
val consumerCount = 5

// 3 producers
val producers = (1..producerCount).map { id ->
    Thread {
        repeat(100) { i ->
            queue.put(Task("Producer-$id-Task-$i"))
        }
    }
}

// 5 consumers
val consumers = (1..consumerCount).map { id ->
    Thread {
        while (true) {
            val task = queue.take()
            if (task == POISON) break
            task.process()
        }
    }
}

producers.forEach { it.start() }
consumers.forEach { it.start() }
```

### Bounded vs Unbounded Queue

| Bounded | Unbounded |
|---|---|
| Blocks producer when full | Never blocks producer |
| Provides backpressure | Producer runs unbounded → OOM |
| Memory is predictable | Memory can explode |
| **Always prefer bounded** | Use only when memory is not a concern |

### Key Insight
> **Producer-Consumer is the pattern behind every message queue (Kafka, RabbitMQ, SQS), every event loop, and every pipeline. The queue is the buffer that decouples producer from consumer. The critical decision: bounded queue (backpressure, safe) vs unbounded (fast, dangerous). Always use bounded — backpressure is a feature, not a bug.**

---

## 3. Read-Write Lock

### Intent
Allow multiple concurrent readers but exclusive access for writers. Optimizes for read-heavy workloads.

### Problem It Solves
A regular lock (mutex) allows only one thread at a time — even for reads. But reads are safe to parallelize (no mutation). Read-write lock allows N concurrent readers OR 1 writer.

### Structure
```kotlin
import java.util.concurrent.locks.ReentrantReadWriteLock

class ThreadSafeCache<K, V> {
    private val cache = mutableMapOf<K, V>()
    private val lock = ReentrantReadWriteLock()

    fun get(key: K): V? {
        lock.readLock().lock()
        try {
            return cache[key]  // multiple threads can read simultaneously
        } finally {
            lock.readLock().unlock()
        }
    }

    fun put(key: K, value: V) {
        lock.writeLock().lock()
        try {
            cache[key] = value  // exclusive — no other reader or writer
        } finally {
            lock.writeLock().unlock()
        }
    }
}

// Usage:
val cache = ThreadSafeCache<String, String>()
// 10 threads reading concurrently — all proceed
// 1 thread writing — all readers and writers wait
```

### Read-Write Lock Semantics
```
State          | New Reader | New Writer
---------------|------------|----------
No locks       | ✅ Acquire | ✅ Acquire
Read lock held | ✅ Acquire | ⏳ Wait
Write lock held| ⏳ Wait    | ⏳ Wait
```

### Fair vs Unfair Lock
```kotlin
// Fair: writers get priority (prevents writer starvation)
val fairLock = ReentrantReadWriteLock(true)

// Unfair (default): readers can starve writers
val unfairLock = ReentrantReadWriteLock(false)
```

### When to Use
- Read-heavy workloads (90%+ reads).
- Data is read frequently but written rarely.
- You want to maximize read concurrency.

### When NOT to Use
- Write-heavy: the write lock blocks everything → worse than a regular lock.
- Short critical sections: the lock overhead exceeds the benefit.

### Key Insight
> **Read-Write Lock is the pattern behind `CopyOnWriteArrayList`, `ConcurrentHashMap` (segmented), and database MVCC. The key insight: reads are safe to parallelize, writes are not. If your read:write ratio is 100:1, a read-write lock gives 100x better read throughput than a regular mutex. But if it's 1:1, the lock overhead makes it slower.**

---

## 4. Future / Promise

### Intent
Represent the result of an asynchronous computation that may not be available yet. Allows the caller to continue working and check the result later.

### Problem It Solves
You start a long operation (API call, DB query, file read). You don't want to block the current thread waiting. A Future/Promise is a "placeholder" for the result — it will be filled when the operation completes.

### Structure
```kotlin
import java.util.concurrent.*

// Submit a task, get a Future immediately
val executor = Executors.newCachedThreadPool()

val future: Future<String> = executor.submit {
    Thread.sleep(2000)  // simulate slow operation
    "Result from async computation"
}

// Do other work while the task runs...
println("Doing other work...")

// Get the result (blocks until ready)
val result = future.get()  // blocks for 2 seconds
println(result)  // "Result from async computation"

// With timeout
val result2 = future.get(5, TimeUnit.SECONDS)  // throws TimeoutException if not done
```

### CompletableFuture (Composable Futures)
```kotlin
// Java/Kotlin: CompletableFuture for chaining async operations
val future = CompletableFuture
    .supplyAsync {
        // Step 1: fetch user from DB (async)
        fetchUserFromDB("alice")
    }
    .thenApply { user ->
        // Step 2: transform (same thread or pool)
        user.toDTO()
    }
    .thenCompose { dto ->
        // Step 3: fetch orders (async, returns another future)
        fetchOrdersAsync(dto.id)
    }
    .thenAccept { orders ->
        // Step 4: consume result
        println("Orders: $orders")
    }
    .exceptionally { error ->
        // Error handling
        println("Error: ${error.message}")
        null
    }

// Non-blocking: the main thread continues
println("This prints immediately")
future.join()  // wait for completion if needed
```

### Kotlin Coroutines (Modern Async)
```kotlin
import kotlinx.coroutines.*

// Kotlin coroutines: structured concurrency
suspend fun fetchUserData(userId: String): UserData {
    val user = async { fetchUser(userId) }      // start async
    val prefs = async { fetchPreferences(userId) }  // start async (parallel!)
    return UserData(user.await(), prefs.await())  // wait for both
}

// Launch multiple coroutines
runBlocking {
    val data = fetchUserData("alice")  // suspends, doesn't block
    println(data)
}
```

### Future vs Callback

| Future/Promise | Callback |
|---|---|
| Result is a value you can hold | Result is passed to a function |
| Composable (chain, combine) | Hard to compose (callback hell) |
| Error handling built-in | Manual error handling |
| Can timeout, cancel | Hard to cancel |

### Key Insight
> **Future/Promise is the pattern behind every async API: JavaScript Promise, Java CompletableFuture, Kotlin Deferred, Scala Future, Python asyncio. The key insight: it turns async operations into values you can compose. `future.thenApply().thenCompose()` is the async equivalent of method chaining. Kotlin coroutines make this even better — async code looks synchronous, but doesn't block threads.**

---

## 5. Monitor

### Intent
An object whose methods are guarded by a mutex. Only one thread can execute any method of the monitor at a time. Provides condition variables for waiting/signaling.

### Problem It Solves
Multiple threads access shared state. You need mutual exclusion (only one at a time) and coordination (wait until a condition is met).

### Structure
```kotlin
// Kotlin: `synchronized` block + `wait/notify`
class BoundedBuffer<T>(private val capacity: Int) {
    private val buffer = ArrayDeque<T>()
    private val lock = Any()

    fun put(item: T) {
        synchronized(lock) {
            while (buffer.size >= capacity) {
                lock.wait()  // release lock, wait for space
            }
            buffer.addLast(item)
            lock.notifyAll()  // wake up consumers
        }
    }

    fun take(): T {
        synchronized(lock) {
            while (buffer.isEmpty()) {
                lock.wait()  // release lock, wait for data
            }
            val item = buffer.removeFirst()
            lock.notifyAll()  // wake up producers
        }
        return item
    }
}
```

### Condition Variables
```kotlin
// More flexible: multiple conditions on one lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition

class BoundedBuffer<T>(private val capacity: Int) {
    private val buffer = ArrayDeque<T>()
    private val lock = ReentrantLock()
    private val notFull: Condition = lock.newCondition()
    private val notEmpty: Condition = lock.newCondition()

    fun put(item: T) {
        lock.lock()
        try {
            while (buffer.size >= capacity) {
                notFull.await()  // wait specifically for "not full"
            }
            buffer.addLast(item)
            notEmpty.signal()  // signal specifically "not empty"
        } finally {
            lock.unlock()
        }
    }

    fun take(): T {
        lock.lock()
        try {
            while (buffer.isEmpty()) {
                notEmpty.await()
            }
            val item = buffer.removeFirst()
            notFull.signal()
            return item
        } finally {
            lock.unlock()
        }
    }
}
```

### wait/notify Rules
1. **Always call `wait()` inside a `while` loop** — not `if`. The condition may be false when you wake up (spurious wakeup or another thread took the item).
2. **Always call `wait/notify` inside `synchronized`** — otherwise `IllegalMonitorStateException`.
3. **Use `notifyAll()` over `notify()`** — `notify()` wakes one random thread, which may not be the right one.

### Key Insight
> **Monitor is the pattern behind every `synchronized` method, every `wait/notify`, and every Java object (every object has a monitor). The key insight: the monitor combines mutual exclusion (only one thread inside) with condition variables (wait for a condition). The `while` loop around `wait()` is critical — `if` is a bug that causes lost wakeups.**

---

## 6. Barrier

### Intent
Allow a set of threads to wait for each other at a common barrier point. All threads must arrive before any can proceed.

### Problem It Solves
You have N threads doing parallel work. They must all finish phase 1 before any starts phase 2. The barrier synchronizes them.

### Structure
```kotlin
import java.util.concurrent.CyclicBarrier

// 3 worker threads must all reach the barrier before proceeding
val barrier = CyclicBarrier(3) {
    println("🚧 All threads reached the barrier. Proceeding!")
}

val workers = (1..3).map { id ->
    Thread {
        println("Thread-$id: Working on phase 1...")
        Thread.sleep((1000..3000).random().toLong())
        println("Thread-$id: Reached barrier, waiting...")
        barrier.await()  // blocks until all 3 arrive
        println("Thread-$id: Starting phase 2!")
    }
}

workers.forEach { it.start() }
// Thread-1: Working on phase 1...
// Thread-2: Working on phase 1...
// Thread-3: Working on phase 1...
// Thread-2: Reached barrier, waiting...
// Thread-1: Reached barrier, waiting...
// Thread-3: Reached barrier, waiting...
// 🚧 All threads reached the barrier. Proceeding!
// Thread-3: Starting phase 2!
// Thread-1: Starting phase 2!
// Thread-2: Starting phase 2!
```

### CyclicBarrier vs CountDownLatch

| CyclicBarrier | CountDownLatch |
|---|---|
| Reusable (reset after use) | One-time use |
| All threads wait for each other | One or more threads wait for N events |
| Symmetric (all are equal) | Asymmetric (waiters vs counters) |
| Can have a barrier action | No action |

```kotlin
// CountDownLatch: main thread waits for N workers
val latch = CountDownLatch(3)

(1..3).map { i ->
    Thread {
        println("Worker-$i: Working...")
        Thread.sleep(1000)
        latch.countDown()  // signal completion
    }.also { it.start() }
}

println("Main: Waiting for all workers...")
latch.await()  // blocks until count reaches 0
println("Main: All workers done!")
```

### When to Use
- **Barrier**: Parallel algorithms where phases must sync (map-reduce, parallel sorting).
- **CountDownLatch**: One-time initialization (wait for N services to start).
- **Phaser**: Dynamic barrier (threads can register/deregister dynamically).

### Key Insight
> **Barrier is the pattern behind parallel algorithms: fork-join, map-reduce, parallel streams. The key insight: it's a synchronization point where threads rendezvous. Without a barrier, fast threads would start phase 2 while slow threads are still in phase 1 → data corruption. The barrier ensures all-for-one, one-for-all coordination.**

---

## 7. Immutable Object

### Intent
An object whose state cannot be changed after construction. Immutable objects are inherently thread-safe.

### Problem It Solves
Shared mutable state is the root of all concurrency evil. Locks are error-prone (deadlocks, race conditions, performance). Immutable objects eliminate the problem entirely — if nothing changes, there's nothing to synchronize.

### Structure
```kotlin
// Kotlin: data class with `val` properties = immutable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val roles: List<String>  // List is read-only in Kotlin
)

// To "modify" an immutable object, create a copy
val user = User("1", "Alice", "alice@example.com", listOf("admin"))
val updatedUser = user.copy(name = "Alice Smith")  // new object, original unchanged

// Both objects exist:
println(user.name)         // "Alice" (unchanged)
println(updatedUser.name)  // "Alice Smith"
```

### Why Immutable Objects Are Thread-Safe
```
Thread A reads user.name → "Alice"
Thread B reads user.name → "Alice"
Thread C "modifies" → creates new copy with name = "Bob"
Thread A still reads the original → "Alice" (safe!)
Thread B still reads the original → "Alice" (safe!)
Thread D reads the new copy → "Bob" (safe!)

No locks needed. No race conditions. No deadlocks.
```

### Mutable vs Immutable

| Mutable | Immutable |
|---|---|
| Modify in place | Create a copy with changes |
| Needs locks for thread safety | Inherently thread-safe |
| Less memory (one object) | More memory (copies) |
| Harder to reason about | Easier to reason about |
| Can lead to bugs | Predictable |

### When to Use
- Shared state across threads.
- You want thread safety without locks.
- Functional programming style.
- Caching (immutable cache values can't be corrupted).

### Key Insight
> **Immutability is the most powerful concurrency pattern. If objects can't change, there's nothing to synchronize. This is why functional programming (Haskell, Erlang) is naturally concurrent — everything is immutable. In Kotlin, use `val` over `var`, `List` over `MutableList`, and `data class` with `copy()`. The trade-off: more allocations (GC pressure). But modern GCs make this cheap, and the safety is worth it.**

---

## 8. Actor Model

### Intent
Treat each concurrent entity as an "actor" — an independent object with its own state, that communicates exclusively through asynchronous message passing. No shared state.

### Problem It Solves
Shared mutable state + locks = deadlocks, race conditions, complexity. The actor model eliminates shared state entirely. Each actor has private state. Actors communicate by sending messages.

### Structure
```kotlin
// Kotlin: using kotlinx.coroutines channels (simplified actor)
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

sealed class CounterMsg
object Increment : CounterMsg
object Decrement : CounterMsg
class GetCount(val reply: Channel<Int>) : CounterMsg

fun counterActor() = actor<CounterMsg> {
    var count = 0  // private state — no other thread can access this
    for (msg in channel) {
        when (msg) {
            is Increment -> count++
            is Decrement -> count--
            is GetCount -> msg.reply.send(count)
        }
    }
}

// Usage:
runBlocking {
    val counter = counterActor()
    counter.send(Increment)
    counter.send(Increment)
    counter.send(Decrement)

    val reply = Channel<Int>()
    counter.send(GetCount(reply))
    println("Count: ${reply.receive()}")  // 1
}
```

### Actor Model Principles
1. **No shared state**: Each actor has private state. No other actor can access it.
2. **Message passing**: Actors communicate only by sending messages. Messages are immutable.
3. **Sequential processing**: An actor processes one message at a time. No concurrency within an actor.
4. **Location transparency**: An actor can be local or remote — the message-passing API is the same.

### Actor Model vs Shared Memory

| Shared Memory (Locks) | Actor Model |
|---|---|
| Shared mutable state | Private state per actor |
| Locks for synchronization | Message passing |
| Deadlocks possible | No deadlocks (no locks) |
| Harder to scale | Naturally distributed |
| Java, C++, Python | Erlang, Akka, Elixir |

### When to Use
- Highly concurrent systems with complex state.
- Distributed systems (actors can be on different machines).
- Fault-tolerant systems (Erlang's "let it crash" philosophy).
- When locks become too complex to manage.

### Key Insight
> **The actor model is the pattern behind Erlang (WhatsApp, RabbitMQ), Akka (Scala/Java), and Kotlin's actor coroutines. The key insight: instead of sharing state and locking, give each actor private state and communicate via messages. This eliminates deadlocks and race conditions entirely. The trade-off: message passing has overhead, and the async style is harder to debug. But for highly concurrent, fault-tolerant systems, actors are the gold standard.**

---

## Summary: When to Use Which Concurrency Pattern

| Pattern | When to Use | Key Benefit |
|---|---|---|
| **Thread Pool** | Execute many tasks | Reuse threads, bound resources |
| **Producer-Consumer** | Decouple producer from consumer | Buffer, load level |
| **Read-Write Lock** | Read-heavy shared data | Parallel reads, exclusive writes |
| **Future/Promise** | Async computation | Non-blocking, composable |
| **Monitor** | Shared mutable state | Mutual exclusion + conditions |
| **Barrier** | Sync parallel phases | All-for-one coordination |
| **Immutable Object** | Shared state across threads | No locks needed |
| **Actor Model** | Complex concurrent state | No shared state, message passing |
