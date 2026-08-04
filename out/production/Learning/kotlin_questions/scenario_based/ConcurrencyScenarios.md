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

## Scenario 6: Structured Concurrency — Child Coroutine Exception

### Problem
A parent coroutine launches multiple child coroutines to fetch data in parallel. When one child fails, the others keep running — wasting resources and causing inconsistent state.

```kotlin
// ❌ Bad — GlobalScope, no parent-child relationship
fun fetchAllData() {
    GlobalScope.launch {
        val data1 = async { api.fetchData1() }  // fails
        val data2 = async { api.fetchData2() }  // keeps running
        val data3 = async { api.fetchData3() }  // keeps running

        try {
            println("${data1.await()} ${data2.await()} ${data3.await()}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            // ❌ data2 and data3 still running — leaked coroutines
        }
    }
}
```

### Solution: Structured concurrency with supervisorScope and exception handling

```kotlin
// ✅ Good — structured concurrency with proper cancellation
suspend fun fetchAllData(): Result<CombinedData> = coroutineScope {
    try {
        // ✅ coroutineScope cancels all children if any child fails
        val data1 = async { api.fetchData1() }
        val data2 = async { api.fetchData2() }
        val data3 = async { api.fetchData3() }

        Result.success(CombinedData(data1.await(), data2.await(), data3.await()))
    } catch (e: Exception) {
        // ✅ All children automatically cancelled by coroutineScope
        Result.failure(e)
    }
}

// ✅ Use supervisorScope when you want children to fail independently
suspend fun fetchAllDataIndependent(): List<Result<Data>> = supervisorScope {
    val results = listOf(
        async { runCatching { api.fetchData1() } },
        async { runCatching { api.fetchData2() } },
        async { runCatching { api.fetchData3() } }
    )
    results.map { it.await() }
    // ✅ If fetchData1 fails, fetchData2 and fetchData3 still complete
}
```

### Key Takeaway
- `coroutineScope` cancels all children when any child fails — fail-fast
- `supervisorScope` lets children fail independently — good for parallel fetches
- `async` propagates exceptions to the parent on `await()`
- Never use `GlobalScope` — it breaks structured concurrency
- `Job` hierarchy ensures no leaked coroutines when parent is cancelled

---

## Scenario 7: Coroutine Leak When ViewModel Is Cleared

### Problem
A ViewModel launches a network request in `viewModelScope`. When the user navigates away and the ViewModel is cleared, the coroutine keeps running and causes a memory leak.

```kotlin
// ❌ Bad — coroutine outlives ViewModel
class BadViewModel : ViewModel() {
    fun loadData() {
        // ❌ GlobalScope — not cancelled when ViewModel is cleared
        GlobalScope.launch {
            val data = api.fetchLargeData()  // keeps running after ViewModel cleared
            // ❌ References Activity context via callback → memory leak
            _uiState.value = data  // Updates a destroyed ViewModel
        }
    }
}
```

### Solution: viewModelScope + proper cancellation

```kotlin
// ✅ Good — viewModelScope auto-cancels on onCleared()
class GoodViewModel(private val api: Api) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = api.fetchLargeData()
                _uiState.value = UiState.Success(data)
            } catch (e: CancellationException) {
                // ✅ Must rethrow — coroutine was cancelled
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message)
            }
        }
    }

    // ✅ For long-running work that should survive config change
    private var fetchJob: Job? = null

    fun loadDataWithCancel() {
        fetchJob?.cancel()  // ✅ Cancel previous request
        fetchJob = viewModelScope.launch {
            val data = api.fetchLargeData()
            _uiState.value = UiState.Success(data)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // viewModelScope is auto-cancelled here
        // No need to manually cancel
    }
}
```

### Key Takeaway
- Always use `viewModelScope` — it's automatically cancelled in `onCleared()`
- Never use `GlobalScope` in a ViewModel — coroutines outlive the ViewModel
- Catch `CancellationException` separately and rethrow it
- Cancel previous jobs before starting new ones to avoid race conditions
- `StateFlow` doesn't leak — it's not tied to any lifecycle owner

---

## Scenario 8: Flow Backpressure — Producer Faster Than Consumer

### Problem
A Flow emits values at a high rate (e.g., sensor data every 10ms), but the consumer processes each value slowly (e.g., 100ms per operation). Values pile up, causing memory pressure and outdated data processing.

```kotlin
// ❌ Bad — no backpressure handling
fun sensorFlow(): Flow<Float> = flow {
    while (true) {
        emit(sensor.read())  // Emits every 10ms
        delay(10)
    }
}

// Consumer takes 100ms per item
// ❌ Buffer grows unbounded → OOM
sensorFlow().collect { value ->
    processValue(value)  // Takes 100ms — 10x slower than producer
}
```

### Solution: Buffer, conflate, or sample the Flow

```kotlin
// ✅ Fix 1: Buffer — run producer and consumer in parallel
sensorFlow()
    .buffer(100)  // Buffer up to 100 values
    .collect { value ->
        processValue(value)  // Producer continues emitting while consumer processes
    }

// ✅ Fix 2: Conflate — only process the latest value (drop intermediate)
sensorFlow()
    .conflate()  // Keep only the most recent value
    .collect { value ->
        processValue(value)  // Skips outdated values
    }

// ✅ Fix 3: Sample — process at fixed intervals
sensorFlow()
    .sample(50)  // Take latest value every 50ms
    .collect { value ->
        processValue(value)
    }

// ✅ Fix 4: Debounce — only process after emissions settle
sensorFlow()
    .debounce(50)  // Wait 50ms of silence before processing
    .collect { value ->
        processValue(value)
    }

// ✅ Fix 5: Flow with capacity — bounded channel
fun sensorFlow(): Flow<Float> = channelFlow {
    while (true) {
        send(sensor.read())
        delay(10)
    }
}.buffer(Channel.BUFFERED)  // Bounded buffer with default capacity
```

### Key Takeaway
- Flows are cold — backpressure is natural for suspending collectors, but only if the producer also suspends
- `buffer()` runs producer and consumer concurrently with a buffer
- `conflate()` keeps only the latest value — perfect for UI state updates
- `sample(period)` picks the latest at fixed intervals — good for sensor data
- `debounce()` waits for silence — good for search inputs
- Use `channelFlow` with bounded capacity for hot-stream-like behavior

---

## 🔗 Related Topics
- [Coroutines Deep Dive](../advanced/Coroutines.md)
- [Flows](../advanced/Flows.md)
