# Flows

## 📖 Explanation

`Flow` is Kotlin's cold asynchronous stream — similar to `Observable` in RxJava. It emits multiple values sequentially over time.

### Cold vs Hot Streams
| Property  | `Flow` (Cold)              | `SharedFlow`/`StateFlow` (Hot) |
|-----------|---------------------------|--------------------------------|
| Activation| Per collector — starts on collect | Always active, independent of collectors |
| Values    | Fresh per collector        | Shared among all collectors     |
| Analogy   | A video on demand          | A live TV broadcast            |

### Creating Flows
```kotlin
// From a builder
flow {
    emit(1)
    emit(2)
    emit(3)
}

// From a range
(1..5).asFlow()

// From a function
flowOf("A", "B", "C")
```

### Collecting
```kotlin
flow.collect { value -> println(value) }
```

### Flow Operators
| Category   | Operators                                    |
|------------|----------------------------------------------|
| Transform  | `map`, `flatMapConcat`, `flatMapMerge`, `transform` |
| Filter     | `filter`, `filterIsInstance`, `take`, `drop` |
| Combine    | `zip`, `combine`, `flattenMerge`             |
| Terminal   | `collect`, `toList`, `first`, `single`, `reduce`, `fold` |
| Context    | `flowOn`, `flowWith`                         |
| Error      | `catch`, `retry`, `retryWhen`               |
| Buffer     | `buffer`, `conflate`, `collectLatest`       |

### `StateFlow`
A hot flow that holds a single updatable state. Great for UI state management.

### `SharedFlow`
A hot flow that broadcasts values to all collectors. Great for events.

---

## 🧪 Code Example

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // Basic flow
    println("=== Basic Flow ===")
    flow {
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }.collect { println("Received: $it") }

    // asFlow
    println("\n=== asFlow ===")
    (1..3).asFlow().collect { println("Item: $it") }

    // map and filter
    println("\n=== Map & Filter ===")
    (1..10).asFlow()
        .filter { it % 2 == 0 }
        .map { it * it }
        .collect { println("Even squared: $it") }

    // zip — combine two flows
    println("\n=== Zip ===")
    val flow1 = flowOf("A", "B", "C")
    val flow2 = flowOf(1, 2, 3)
    flow1.zip(flow2) { letter, num -> "$letter$num" }
        .collect { println("Zipped: $it") }

    // catch — handle errors
    println("\n=== Catch ===")
    flow {
        emit(1)
        emit(2)
        throw RuntimeException("Flow error!")
    }
    .catch { e -> println("Caught: $e"); emit(-1) }
    .collect { println("Value: $it") }

    // StateFlow — hot flow with state
    println("\n=== StateFlow ===")
    val stateFlow = MutableStateFlow(0)
    launch {
        stateFlow.collect { println("StateFlow collector: $it") }
    }
    delay(100)
    stateFlow.value = 1
    delay(100)
    stateFlow.value = 2
    delay(100)

    // SharedFlow — broadcast events
    println("\n=== SharedFlow ===")
    val sharedFlow = MutableSharedFlow<String>()
    launch {
        sharedFlow.collect { println("SharedFlow collector A: $it") }
    }
    launch {
        sharedFlow.collect { println("SharedFlow collector B: $it") }
    }
    delay(100)
    sharedFlow.emit("Hello")
    sharedFlow.emit("World")
    delay(100)

    // flatMapConcat
    println("\n=== flatMapConcat ===")
    flowOf(1, 2, 3)
        .flatMapConcat { flowOf(it, it * 10) }
        .collect { println("Flat: $it") }

    // flowOn — change dispatcher
    println("\n=== flowOn ===")
    flow {
        emit(1)
        println("Emitting on: ${Thread.currentThread().name}")
    }
    .flowOn(Dispatchers.IO)
    .collect { println("Collected: $it on ${Thread.currentThread().name}") }
}
```

### Output
```
=== Basic Flow ===
Received: 1
Received: 2
Received: 3

=== asFlow ===
Item: 1
Item: 2
Item: 3

=== Map & Filter ===
Even squared: 4
Even squared: 16
Even squared: 36
Even squared: 64
Even squared: 100

=== Zip ===
Zipped: A1
Zipped: B2
Zipped: C3

=== Catch ===
Value: 1
Value: 2
Caught: java.lang.RuntimeException: Flow error!
Value: -1

=== StateFlow ===
StateFlow collector: 0
StateFlow collector: 1
StateFlow collector: 2

=== SharedFlow ===
SharedFlow collector A: Hello
SharedFlow collector B: Hello
SharedFlow collector A: World
SharedFlow collector B: World

=== flatMapConcat ===
Flat: 1
Flat: 10
Flat: 2
Flat: 20
Flat: 3
Flat: 30

=== flowOn ===
Emitting on: DefaultDispatcher-worker-1
Collected: 1 on main
```

---

## ❓ Interview Questions

1. **What is a `Flow` and how does it differ from a `Sequence`?**
   - `Flow` is asynchronous and supports suspension. `Sequence` is synchronous. Flow can use `delay`, switch dispatchers, and handle async errors.

2. **What is the difference between cold and hot flows?**
   - Cold flows (`Flow`) start emitting per collector. Hot flows (`SharedFlow`, `StateFlow`) are always active and broadcast to all collectors regardless.

3. **What is the difference between `StateFlow` and `SharedFlow`?**
   - `StateFlow` holds a single state value (like a `BehaviorSubject`). `SharedFlow` is a general-purpose broadcast (like a `PublishSubject`). `StateFlow` always has a value and conflates.

4. **What does `flowOn` do?**
   - Changes the dispatcher for the upstream flow operations (everything before `flowOn`). The collector still runs on the original context.

5. **How do you handle exceptions in a Flow?**
   - Use the `catch` operator for upstream errors. Use `retry`/`retryWhen` for retrying. Terminal operators like `collect` can also use `try/catch`.

6. **What is the difference between `collect`, `toList`, and `single` as terminal operators?**
   - `collect` — the primary terminal operator. Collects emissions one by one, executing the provided lambda for each: `flow.collect { value -> println(value) }`. Suspends until the flow completes. (2) `toList()` — collects all emissions into a `List` and returns it. Suspends until the flow completes: `val list = flow.toList()`. Useful for testing and one-shot flows. (3) `single()` — expects exactly one emission. Throws `NoSuchElementException` if zero, `IllegalArgumentException` if more than one. Use when the flow should emit exactly once: `val user = repository.getUser(id).single()`. (4) `first()` — returns the first emission and cancels the flow. Use for one-shot reads. (5) `firstOrNull()` — returns the first emission or null. (6) `fold(initial) { acc, value -> }` — reduces emissions to a single value. (7) `count()` — counts emissions. All terminal operators are suspending — they drive the flow. Without a terminal operator, the flow does nothing (cold flows are lazy).

7. **What is `flowOn` and how does it differ from `launchOn`/`withContext`?**
   - `flowOn(dispatcher)` changes the dispatcher for the **upstream** operators (everything before `flowOn`). The collector always runs on the calling context. Example: `flow.map { heavyTransform(it) }.flowOn(Dispatchers.Default).filter { it > 0 }.collect { updateUI(it) }` — `map` runs on `Default`, `filter` runs on the calling dispatcher, `collect` runs on the calling dispatcher. Key points: (1) `flowOn` only affects upstream — not downstream. (2) Multiple `flowOn` calls create buffer boundaries. (3) `flowOn` uses channel-based buffering to switch threads. (4) Unlike `withContext`, which wraps a suspend block, `flowOn` is an operator that changes the context for the flow pipeline. (5) For Room flows, Room automatically handles the IO dispatcher — you don't need `flowOn(Dispatchers.IO)`. (6) Use `flowOn` when the flow's producer does heavy work. Use `withContext` in the collector if the collector needs a specific thread.

8. **What are `StateFlow`, `SharedFlow`, and their use cases in Android?**
   - **StateFlow** — a hot flow that always has a value and conflates (only latest matters). Like `BehaviorSubject` in RxJava. Use for **UI state** — data that represents the current screen state. Pattern: `private val _state = MutableStateFlow(UiState()); val state = _state.asStateFlow()`. Update with `_state.value = newState`. Collectors get the current value immediately and all subsequent updates. StateFlow is perfect for MVVM — the ViewModel exposes `StateFlow<UiState>` and the UI collects it. **SharedFlow** — a hot flow for broadcasting events. Like `PublishSubject`. Use for **one-time events** — navigation, snackbar messages, toasts. Pattern: `private val _events = MutableSharedFlow<UiEvent>(); val events = _events.asSharedFlow()`. Emit with `_events.tryEmit(ShowSnackbar("Saved!"))`. `replay = 0` (default) means new collectors don't receive past events — perfect for navigation. Unlike StateFlow, SharedFlow doesn't require an initial value and doesn't conflate. Use StateFlow for state, SharedFlow for events.

9. **What is `combine`, `zip`, and `flatMapLatest` and when do you use each?**
   - `combine(flow1, flow2) { a, b -> Result(a, b) }` — combines the **latest** values from both flows. Whenever either flow emits, the result is recomputed with the latest from both. Use for combining multiple data sources: `combine(userFlow, settingsFlow) { user, settings -> UserWithSettings(user, settings) }`. Initially waits for both to emit. `zip(flow1, flow2) { a, b -> Pair(a, b) }` — pairs emissions one-to-one. Flow1's first emission is paired with Flow2's first, second with second, etc. Slower flow controls the pace. Use for pairing related streams: `zip(clicksFlow, positionsFlow) { click, pos -> ... }`. `flatMapLatest(transform) { result -> Flow }` — transforms each emission to a new flow and collects only the **latest** inner flow, cancelling the previous. Use for search-as-you-type: `queryFlow.flatMapLatest { query -> repository.search(query) }` — when a new query arrives, the previous search is cancelled. `switchMap` is the LiveData equivalent. Use `combine` for merging state, `zip` for pairing, `flatMapLatest` for switching.

10. **How do you test Flows in Kotlin?**
    - Several approaches: (1) `first()` — get the first emission: `assertEquals(expected, flow.first())`. (2) `toList()` — collect all emissions into a list: `assertEquals(listOf(1, 2, 3), flow.toList())`. (3) **Turbine** library — test hot flows: `flow.test { assertEquals(1, awaitItem()); assertEquals(2, awaitItem()); awaitComplete() }`. Turbine handles timing, emissions, and errors. (4) For `StateFlow`, check `.value` directly — no collection needed: `assertEquals(expected, stateFlow.value)`. (5) For `SharedFlow`, use Turbine with `test { }`. (6) Use `runTest` for virtual time control. (7) For Room Flow queries, use in-memory database + `first()`. (8) Test error cases: `assertThrows<Exception> { flow.collect { } }`. (9) For flows with delays, use `runTest` — `advanceTimeBy(1000)` to skip delays. Always use Turine for complex flow testing — it handles edge cases that `toList()` misses (like intermediate states). For Android, use `MainDispatcherRule` to control dispatchers in ViewModel tests.

11. **What is `callbackFlow` and `channelFlow` and when do you use them?**
    - `callbackFlow { }` — converts callback-based APIs to Flow. Use when integrating with callback-based libraries. Example: `fun locationUpdates(): Flow<Location> = callbackFlow { val callback = object : LocationCallback { override fun onLocation(location: Location) { trySend(location) } }; locationManager.requestLocationUpdates(callback); awaitClose { locationManager.removeUpdates(callback) } }`. Key points: (1) Use `trySend()` to emit — it's thread-safe and non-suspending. (2) `awaitClose { }` is required — it runs when the flow is cancelled, allowing cleanup. (3) The flow is cold — the callback is registered when collected and unregistered when cancelled. (4) Use `awaitClose` for cleanup — removing listeners, closing resources. `channelFlow { }` — similar but for more complex concurrent scenarios. Supports sending from multiple coroutines. Use when you need to emit from different contexts. Both produce a cold flow. Always handle cancellation in `awaitClose` — otherwise the callback leaks.

12. **What is Flow conflation, buffering, and backpressure?**
    - **Conflation**: When the collector is slower than the producer, intermediate values are dropped — only the latest is kept. `StateFlow` conflates by default. Use `.conflate()` operator to enable conflation. **Buffering**: Use `.buffer(capacity)` to add a buffer between producer and collector. The producer can continue emitting while the collector processes. Capacity options: `Channel.BUFFERED` (default 64), `Channel.UNLIMITED`, `Channel.CONFLATED`, `Channel.RENDEZVOUS` (0). **Backpressure**: When the producer emits faster than the collector can process. Handling strategies: (1) **Conflate** — drop intermediate values, keep latest. Good for UI updates (only latest matters). (2) **Buffer** — queue emissions. Good when all values matter but processing is slow. (3) **`collectLatest`** — cancel the previous collection when a new value arrives. Good for search queries. (4) **`flatMapLatest`** — cancel previous inner flow. Good for switch-based operations. (5) **Slow down the producer** — use `delay()` or rate limiting. Choose based on whether all values matter (buffer) or only the latest (conflate/collectLatest).

---

## 🔗 Related Topics
- [Coroutines Deep Dive](Coroutines.md)
- [Collections](../intermediate/Collections.md)
