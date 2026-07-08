# Concurrency & Coroutines Scenarios

## Scenario 1: Race Condition in Counter

### Problem
You have a shared counter accessed by multiple coroutines. The final count is wrong.

```kotlin
// ❌ Bad — race condition
var counter = 0
runBlocking {
    repeat(1000) {
        launch { counter++ }
    }
}
println(counter)  // Not always 1000
```

### Solution: Use `Mutex` or `AtomicInteger`

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import java.util.concurrent.atomic.AtomicInteger

fun main() = runBlocking {
    // ✅ Solution 1: Mutex
    val mutex = Mutex()
    var counter1 = 0
    coroutineScope {
        repeat(1000) {
            launch {
                mutex.withLock { counter1++ }
            }
        }
    }
    println("Mutex counter: $counter1")  // Always 1000

    // ✅ Solution 2: AtomicInteger (lock-free)
    val counter2 = AtomicInteger(0)
    coroutineScope {
        repeat(1000) {
            launch { counter2.incrementAndGet() }
        }
    }
    println("Atomic counter: ${counter2.get()}")  // Always 1000

    // ✅ Solution 3: Single-threaded dispatcher
    val counter3 = AtomicInteger(0)
    val singleDispatcher = newSingleThreadContext("counter")
    coroutineScope {
        repeat(1000) {
            launch(singleDispatcher) { counter3.incrementAndGet() }
        }
    }
    println("Single-thread counter: ${counter3.get()}")
}
```

### Key Takeaway
- `counter++` is not atomic (read → increment → write)
- Use `Mutex.withLock` for complex critical sections
- Use `AtomicInteger` for simple counters (lock-free, faster)

---

## Scenario 2: Timeout Handling for Network Calls

### Problem
You need to call 3 APIs in parallel. If any takes > 5 seconds, use a fallback.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    data class UserProfile(val name: String, val email: String, val avatar: String)

    suspend fun fetchName(): String {
        delay(3000)
        return "Alice"
    }

    suspend fun fetchEmail(): String {
        delay(6000)  // Slow — will timeout
        return "alice@example.com"
    }

    suspend fun fetchAvatar(): String {
        delay(2000)
        return "https://example.com/avatar.jpg"
    }

    // ✅ Solution: withTimeoutOrNull for each call
    suspend fun <T> fetchWithTimeout(
        timeoutMs: Long,
        fallback: T,
        block: suspend () -> T
    ): T = withTimeoutOrNull(timeoutMs) { block() } ?: fallback

    val profile = coroutineScope {
        val name = async { fetchWithTimeout(5000, "Unknown") { fetchName() } }
        val email = async { fetchWithTimeout(5000, "no-email") { fetchEmail() } }
        val avatar = async { fetchWithTimeout(5000, "default.png") { fetchAvatar() } }

        UserProfile(name.await(), email.await(), avatar.await())
    }

    println(profile)
    // UserProfile(name=Alice, email=no-email, avatar=https://example.com/avatar.jpg)
}
```

### Key Takeaway
- `withTimeoutOrNull` returns `null` on timeout instead of throwing
- Use `async` for parallel execution
- Always provide fallback values for resilient UI

---

## Scenario 3: Producer-Consumer with Channel

### Problem
One coroutine produces data, another consumes it. Producer is faster than consumer.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    // ✅ Solution: Channel with buffering
    val channel = Channel<Int>(capacity = 10)  // Buffered channel

    // Producer
    val producer = launch {
        for (i in 1..20) {
            println("Producing: $i")
            channel.send(i)
            delay(100)  // Fast producer
        }
        channel.close()
    }

    // Consumer (slow)
    val consumer = launch {
        for (item in channel) {
            println("  Consuming: $item")
            delay(300)  // Slow consumer
        }
    }

    producer.join()
    consumer.join()
    println("Done")
}
```

### Key Takeaway
- `Channel(capacity)` buffers items between producer and consumer
- `channel.close()` signals end of stream
- Consumer automatically suspends when channel is empty
- Producer suspends when buffer is full (backpressure)

---

## Scenario 4: Cancellation Not Working

### Problem
A coroutine doesn't cancel because it's doing blocking I/O.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // ❌ Bad — blocking call ignores cancellation
    val job = launch {
        Thread.sleep(5000)  // Blocking — won't check for cancellation
        println("Done")
    }

    delay(1000)
    job.cancelAndJoin()
    println("Cancelled")  // But the coroutine is still sleeping!
}
```

### Solution: Use suspending functions

```kotlin
fun main() = runBlocking {
    // ✅ Solution 1: Use delay instead of Thread.sleep
    val job1 = launch {
        try {
            delay(5000)  // Suspending — checks cancellation
            println("Done")
        } catch (e: CancellationException) {
            println("Cancelled properly")
        }
    }
    delay(1000)
    job1.cancelAndJoin()

    // ✅ Solution 2: Check isActive in loops
    val job2 = launch {
        while (isActive) {
            // Do work chunk
            Thread.sleep(100)  // Small blocking chunks
        }
    }
    delay(1000)
    job2.cancelAndJoin()

    // ✅ Solution 3: Use ensureActive()
    val job3 = launch {
        while (true) {
            ensureActive()  // Throws CancellationException if cancelled
            // Do work
        }
    }
    delay(1000)
    job3.cancelAndJoin()

    println("All cancelled")
}
```

### Key Takeaway
- `Thread.sleep` blocks and ignores cancellation
- `delay` is suspending and cooperative
- Use `isActive` or `ensureActive()` in loops
- For blocking I/O, wrap in `withContext(Dispatchers.IO)` and check cancellation

---

## Scenario 5: Flow Collection with Lifecycle Awareness

### Problem
A Flow keeps emitting even when the UI is in the background, causing crashes.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // Simulate a Flow that emits every 500ms
    val dataFlow = flow {
        var i = 0
        while (true) {
            emit(i++)
            delay(500)
        }
    }

    // ✅ Solution: Use lifecycle-aware operators
    dataFlow
        .onEach { value -> println("Received: $value") }
        .launchIn(this)  // In Android, use repeatOnLifecycle(STARTED)

    delay(2000)
    // In Android: repeatOnLifecycle(Lifecycle.State.STARTED) { flow.collect { } }
    cancel()  // Stops collection
    println("Stopped collecting")
}
```

### Key Takeaway
- Flows are cold — they only emit when collected
- Use `repeatOnLifecycle(STARTED)` in Android to pause collection in background
- `Flow.onEach` + `launchIn` for manual scope control
- Always cancel flows when the UI is destroyed

---

## 🔗 Related Topics
- [Coroutines Deep Dive](../advanced/Coroutines.md)
- [Flows](../advanced/Flows.md)
