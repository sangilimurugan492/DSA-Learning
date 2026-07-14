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

---

## 🔗 Related Topics
- [Coroutines Deep Dive](Coroutines.md)
- [Collections](../intermediate/Collections.md)
