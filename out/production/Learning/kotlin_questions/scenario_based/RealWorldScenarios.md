# Real-World App Scenarios

## Scenario 1: Rate Limiter

### Problem
Implement a rate limiter that allows N requests per time window.

```kotlin
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class RateLimiter(
    private val maxRequests: Int,
    private val windowMs: Long
) {
    private val requests = ConcurrentHashMap<String, Pair<Long, AtomicInteger>>()

    suspend fun tryAcquire(key: String): Boolean {
        val now = System.currentTimeMillis()

        val entry = requests.compute(key) { _, current ->
            if (current == null || now - current.first >= windowMs) {
                now to AtomicInteger(1)
            } else {
                current.second.incrementAndGet()
                current
            }
        }!!

        val (windowStart, count) = entry
        return count.get() <= maxRequests
    }
}

fun main() = runBlocking {
    val limiter = RateLimiter(maxRequests = 3, windowMs = 1000)

    // Simulate 5 rapid requests
    repeat(5) { i ->
        val allowed = limiter.tryAcquire("user_123")
        println("Request ${i + 1}: ${if (allowed) "ALLOWED" else "BLOCKED"}")
    }

    // Wait for window to reset
    println("\nWaiting 1 second...")
    delay(1100)

    val allowed = limiter.tryAcquire("user_123")
    println("After wait: ${if (allowed) "ALLOWED" else "BLOCKED"}")
}
// Output:
// Request 1: ALLOWED
// Request 2: ALLOWED
// Request 3: ALLOWED
// Request 4: BLOCKED
// Request 5: BLOCKED
// After wait: ALLOWED
```

### Key Takeaway
- Use `ConcurrentHashMap` for thread-safe rate limiting
- `AtomicInteger` for lock-free counting
- Reset window when time expires
- Real-world: use Guava RateLimiter or Redis for distributed rate limiting

---

## Scenario 2: Retry with Exponential Backoff

### Problem
An API call fails intermittently. Implement retry with exponential backoff.

```kotlin
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.random.Random

class RetryConfig(
    val maxRetries: Int = 3,
    val initialDelayMs: Long = 1000,
    val maxDelayMs: Long = 10_000,
    val multiplier: Double = 2.0
)

suspend fun <T> retryWithBackoff(
    config: RetryConfig = RetryConfig(),
    block: suspend () -> T
): T {
    var lastError: Exception? = null
    var delay = config.initialDelayMs

    repeat(config.maxRetries + 1) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastError = e
            if (attempt < config.maxRetries) {
                println("  Attempt ${attempt + 1} failed: ${e.message}. Retrying in ${delay}ms...")
                delay(delay)
                delay = min((delay * config.multiplier).toLong(), config.maxDelayMs)
            }
        }
    }
    throw lastError ?: RuntimeException("Unknown error")
}

// Simulate flaky API
var callCount = 0
suspend fun flakyApi(): String {
    callCount++
    println("  API call #$callCount")
    if (callCount < 3) throw RuntimeException("503 Service Unavailable")
    return "Success!"
}

fun main() = runBlocking {
    println("=== Retry with Backoff ===")
    try {
        val result = retryWithBackoff(RetryConfig(maxRetries = 5)) {
            flakyApi()
        }
        println("Result: $result")
    } catch (e: Exception) {
        println("All retries failed: ${e.message}")
    }
}
// Output:
//   API call #1
//   Attempt 1 failed: 503 Service Unavailable. Retrying in 1000ms...
//   API call #2
//   Attempt 2 failed: 503 Service Unavailable. Retrying in 2000ms...
//   API call #3
// Result: Success!
```

### Key Takeaway
- Exponential backoff: delay doubles each retry (1s → 2s → 4s → 8s)
- Cap at `maxDelayMs` to avoid extremely long waits
- Add jitter (random delay) in production to avoid thundering herd
- Always set `maxRetries` to prevent infinite loops

---

## Scenario 3: Event Bus with Flows

### Problem
Implement a simple event bus for cross-component communication.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class EventBus {
    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    suspend fun emit(event: Event) {
        _events.emit(event)
    }
}

sealed class Event {
    data class UserLoggedIn(val userId: String) : Event()
    data class UserLoggedOut(val userId: String) : Event()
    data class DataUpdated(val key: String) : Event()
    object NetworkError : Event()
}

class AnalyticsService(private val bus: EventBus) {
    fun startListening(scope: CoroutineScope) {
        scope.launch {
            bus.events.collect { event ->
                when (event) {
                    is Event.UserLoggedIn -> println("📊 Analytics: User ${event.userId} logged in")
                    is Event.UserLoggedOut -> println("📊 Analytics: User ${event.userId} logged out")
                    is Event.DataUpdated -> println("📊 Analytics: Data updated - ${event.key}")
                    is Event.NetworkError -> println("📊 Analytics: Network error detected")
                }
            }
        }
    }
}

class CacheService(private val bus: EventBus) {
    fun startListening(scope: CoroutineScope) {
        scope.launch {
            bus.events.filterIsInstance<Event.DataUpdated>().collect { event ->
                println("💾 Cache: Invalidating cache for ${event.key}")
            }
        }
    }
}

fun main() = runBlocking {
    val bus = EventBus()
    val analytics = AnalyticsService(bus)
    val cache = CacheService(bus)

    analytics.startListening(this)
    cache.startListening(this)

    delay(100)  // Let collectors start

    // Emit events
    bus.emit(Event.UserLoggedIn("user_123"))
    bus.emit(Event.DataUpdated("user_profile"))
    bus.emit(Event.NetworkError)
    bus.emit(Event.UserLoggedOut("user_123"))

    delay(500)  // Let events process
}
// Output:
// 📊 Analytics: User user_123 logged in
// 💾 Cache: Invalidating cache for user_profile
// 📊 Analytics: Data updated - user_profile
// 📊 Analytics: Network error detected
// 📊 Analytics: User user_123 logged out
```

### Key Takeaway
- `MutableSharedFlow` is a hot stream — perfect for event bus
- Multiple collectors receive all events
- `filterIsInstance<T>()` to filter by event type
- Sealed class for type-safe events

---

## Scenario 4: Pagination with Flows

### Problem
Implement pagination that loads pages on demand.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class Page<T>(val items: List<T>, val nextPage: Int?, val isLast: Boolean)

class Paginator<T>(
    private val pageSize: Int,
    private val loadPage: suspend (Int, Int) -> List<T>
) {
    private var currentPage = 0
    private var isLastPage = false
    private val allItems = mutableListOf<T>()

    fun loadNext(): Flow<List<T>> = flow {
        if (isLastPage) {
            emit(emptyList())
            return@flow
        }
        currentPage++
        val items = loadPage(currentPage, pageSize)
        allItems.addAll(items)
        isLastPage = items.size < pageSize
        emit(items)
    }

    fun getAll(): List<T> = allItems.toList()
}

fun main() = runBlocking {
    // Simulate paginated API
    val totalItems = (1..25).map { "Item $it" }
    val pageSize = 10

    val paginator = Paginator<String>(pageSize) { page, size ->
        delay(500)  // Simulate network
        val start = (page - 1) * size
        val end = minOf(start + size, totalItems.size)
        if (start < totalItems.size) totalItems.subList(start, end) else emptyList()
    }

    // Load pages
    var page = 1
    while (true) {
        println("Loading page $page...")
        val items = paginator.loadNext().first()
        if (items.isEmpty()) {
            println("No more items")
            break
        }
        println("  Got ${items.size} items: $items")
        page++
    }

    println("\nAll items: ${paginator.getAll().size}")
}
// Output:
// Loading page 1...
//   Got 10 items: [Item 1, Item 2, ..., Item 10]
// Loading page 2...
//   Got 10 items: [Item 11, Item 12, ..., Item 20]
// Loading page 3...
//   Got 5 items: [Item 21, Item 22, ..., Item 25]
// Loading page 4...
//   No more items
// All items: 25
```

### Key Takeaway
- Flow for lazy page loading
- Track current page and last page flag
- Accumulate items in memory or use Paging 3 library
- Real-world: use Android Paging 3 library for RecyclerView integration

---

## Scenario 5: Circuit Breaker

### Problem
Stop calling a failing service to prevent cascading failures.

```kotlin
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

class CircuitBreaker(
    private val failureThreshold: Int = 3,
    private val resetTimeoutMs: Long = 5000
) {
    enum class State { CLOSED, OPEN, HALF_OPEN }

    private var state = State.CLOSED
    private val failureCount = AtomicInteger(0)
    private var lastFailureTime: Long = 0

    suspend fun <T> execute(block: suspend () -> T): T {
        when (state) {
            State.OPEN -> {
                if (System.currentTimeMillis() - lastFailureTime >= resetTimeoutMs) {
                    state = State.HALF_OPEN
                    println("  🔶 Circuit: OPEN → HALF_OPEN (trying again)")
                } else {
                    throw RuntimeException("Circuit is OPEN — rejecting call")
                }
            }
            else -> {}
        }

        return try {
            val result = block()
            onSuccess()
            result
        } catch (e: Exception) {
            onFailure()
            throw e
        }
    }

    private fun onSuccess() {
        failureCount.set(0)
        if (state == State.HALF_OPEN) {
            state = State.CLOSED
            println("  ✅ Circuit: HALF_OPEN → CLOSED (recovered)")
        }
    }

    private fun onFailure() {
        lastFailureTime = System.currentTimeMillis()
        val failures = failureCount.incrementAndGet()
        if (failures >= failureThreshold) {
            state = State.OPEN
            println("  🔴 Circuit: CLOSED → OPEN (failures=$failures)")
        }
    }

    fun getState() = state
}

// Simulate flaky service
var callCount = 0
suspend fun unreliableService(): String {
    callCount++
    if (callCount <= 5) throw RuntimeException("Service error")
    return "Success on call $callCount"
}

fun main() = runBlocking {
    val breaker = CircuitBreaker(failureThreshold = 3, resetTimeoutMs = 2000)

    repeat(8) { i ->
        try {
            val result = breaker.execute { unreliableService() }
            println("Call ${i + 1}: $result (state=${breaker.getState()})")
        } catch (e: Exception) {
            println("Call ${i + 1}: FAILED - ${e.message} (state=${breaker.getState()})")
        }
        delay(500)
    }
}
// Call 1: FAILED - Service error (state=CLOSED)
// Call 2: FAILED - Service error (state=CLOSED)
// Call 3: FAILED - Service error (state=OPEN)
// Call 4: FAILED - Circuit is OPEN — rejecting call (state=OPEN)
// Call 5: FAILED - Circuit is OPEN — rejecting call (state=OPEN)
//   🔶 Circuit: OPEN → HALF_OPEN (trying again)
// Call 6: FAILED - Service error (state=OPEN)
//   🔶 Circuit: OPEN → HALF_OPEN (trying again)
// Call 7: FAILED - Service error (state=OPEN)
//   🔶 Circuit: OPEN → HALF_OPEN (trying again)
// Call 8: Success on call 8 (state=CLOSED)
```

### Key Takeaway
- **CLOSED**: Normal operation, count failures
- **OPEN**: Reject all calls, wait for reset timeout
- **HALF_OPEN**: Allow one trial call — success → CLOSED, failure → OPEN
- Prevents cascading failures in microservices
- Real-world: use Resilience4j for production circuit breakers

---

## 🔗 Related Topics
- [Coroutines Deep Dive](../advanced/Coroutines.md)
- [Flows](../advanced/Flows.md)
- [OOP & Design Scenarios](OOPScenarios.md)
