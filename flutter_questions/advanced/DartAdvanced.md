# Dart Advanced

## 📖 Explanation

Advanced Dart concepts for senior-level Flutter interviews — covering generics, callable classes, metadata/annotations, zones, event loop internals, isolates in depth, streams, records/patterns, sealed classes, extension types, memory management, and FFI.

---

## Table of Contents

1. [Generics](#1-generics)
2. [Typedefs (Type Aliases)](#2-typedefs-type-aliases)
3. [Callable Classes](#3-callable-classes)
4. [Metadata & Annotations](#4-metadata--annotations)
5. [Event Loop Internals](#5-event-loop-internals)
6. [Zones](#6-zones)
7. [Completer](#7-completer)
8. [StreamController & Stream Transformers](#8-streamcontroller--stream-transformers)
9. [Isolates (In Depth)](#9-isolates-in-depth)
10. [Records & Patterns (Dart 3+)](#10-records--patterns-dart-3)
11. [Sealed Classes](#11-sealed-classes)
12. [Extension Types (Dart 3.3+)](#12-extension-types-dart-33)
13. [Memory Management & GC](#13-memory-management--gc)
14. [Dart FFI (Foreign Function Interface)](#14-dart-ffi-foreign-function-interface)
15. [Interview Questions](#-interview-questions)

---

## 1. Generics

Generics enable type-safe, reusable code. Dart uses **reified generics** — type information is available at runtime (unlike Java's type erasure).

### Generic Methods

```dart
// Generic method — T is inferred from arguments
T firstOrDefault<T>(List<T> items, T defaultValue) {
  if (items.isEmpty) return defaultValue;
  return items.first;
}

print(firstOrDefault<int>([1, 2, 3], 0));  // 1
print(firstOrDefault([], 'empty'));          // empty (T inferred as String)
```

### Generic Classes

```dart
class Stack<T> {
  final List<T> _items = [];

  void push(T item) => _items.add(item);
  T pop() => _items.removeLast();
  bool get isEmpty => _items.isEmpty;
  int get length => _items.length;
}

// Usage
final stack = Stack<String>();
stack.push('Hello');
stack.push('World');
print(stack.pop());  // World
```

### Generic Constraints (`extends`)

```dart
// Constrain T to be a num (int or double)
class NumericStore<T extends num> {
  final T value;
  NumericStore(this.value);

  double get asDouble => value.toDouble();
  NumericStore<T> operator +(T other) => NumericStore(value + other as T);
}

// Constrain T to implement a specific interface
abstract class Comparable<T> {
  int compareTo(T other);
}

class SortedList<T extends Comparable<T>> {
  final List<T> _items = [];

  void add(T item) {
    _items.add(item);
    _items.sort((a, b) => a.compareTo(b));
  }
}
```

### Generic typedefs

```dart
typedef Mapper<T, R> = R Function(T input);

String intToString(int n) => n.toString();
String doubleToString(double d) => d.toString();

void main() {
  Mapper<int, String> m1 = intToString;
  Mapper<double, String> m2 = doubleToString;
  print(m1(42));    // '42'
  print(m2(3.14));  // '3.14'
}
```

### Reified Generics

```dart
// Dart keeps type info at runtime (reified) — unlike Java (type erasure)
void checkType<T>(T value) {
  print('T is $T');           // ✅ Works — type info available at runtime
  print('Value type: ${value.runtimeType}');
}

checkType<int>(42);
// T is int
// Value type: int

// Compare with Java: Java erases T at runtime — can't do `new T()` or `T.class`
```

---

## 2. Typedefs (Type Aliases)

```dart
// Function type alias
typedef IntTransformer = int Function(int);
typedef Validator<T> = bool Function(T);

// Non-function type alias (Dart 2.13+)
typedef JSON = Map<String, dynamic>;
typedef StringList = List<String>;

// Usage
IntTransformer doubler = (int x) => x * 2;
Validator<String> emailValidator = (s) => s.contains('@');

JSON data = {'name': 'Alice', 'age': 30};
StringList names = ['Alice', 'Bob'];

print(doubler(5));              // 10
print(emailValidator('a@b.c')); // true
print(data['name']);            // Alice
```

---

## 3. Callable Classes

Any class that implements the `call()` method can be invoked like a function.

```dart
class Multiplier {
  final int factor;
  Multiplier(this.factor);

  // call() — makes the instance callable
  int call(int x) => x * factor;
}

void main() {
  final triple = Multiplier(3);
  // Invoked like a function — Dart calls triple.call(5)
  print(triple(5));   // 15
  print(triple(10));   // 30
}

// Real-world: Validator as callable class
class RangeValidator {
  final int min;
  final int max;
  RangeValidator(this.min, this.max);

  bool call(int value) => value >= min && value <= max;
}

void main() {
  final validator = RangeValidator(1, 100);
  if (validator(50)) print('Valid');    // Called like a function
  if (!validator(150)) print('Invalid');
}
```

---

## 4. Metadata & Annotations

Annotations start with `@` and can attach metadata to classes, methods, fields, and parameters.

```dart
// Built-in annotations
@override        // Marks method as overriding parent — compiler checks
@deprecated      // Marks as deprecated — IDE shows warning
@protected       // Marks as internal to package
@visibleForTesting  // Marks as visible only in tests
@nonVirtual      // Prevents overriding
@immutable       // Marks class as immutable (for linter)
@pragma('vm:entry-point')  // Prevents tree-shaking

// Custom annotation
class Todo {
  final String message;
  final String author;
  const Todo(this.message, {this.author = 'Unknown'});
}

class ApiClient {
  @Todo('Implement retry logic', author: 'Alice')
  Future<void> fetchData() async {
    // TODO: add retry
  }

  @deprecated('Use fetchData() instead')
  void oldFetchMethod() {}
}

// Reading annotations via reflection (requires dart:mirrors — not in Flutter)
// In Flutter, use build_runner + source_gen for annotation processing
```

---

## 5. Event Loop Internals

Dart is **single-threaded** with an event loop. Understanding the event loop is critical for async debugging.

### Event Loop Architecture

```
┌─────────────────────────────────────────────┐
│              Dart Isolate                     │
│  ┌─────────────────────────────────────────┐ │
│  │           Event Loop                     │ │
│  │                                          │ │
│  │  1. Microtask Queue (high priority)      │ │
│  │     ┌───┬───┬───┬───┐                   │ │
│  │     │M1 │M2 │M3 │...│  ← scheduleMicrotask│ │
│  │     └───┴───┴───┴───┘                   │ │
│  │                                          │ │
│  │  2. Event Queue (normal priority)        │ │
│  │     ┌───┬───┬───┬───┐                   │ │
│  │     │E1 │E2 │E3 │...│  ← Future, I/O,   │ │
│  │     └───┴───┴───┴───┘    Timer, UI      │ │
│  │                                          │ │
│  │  Loop:                                   │ │
│  │    1. Run ALL microtasks                 │ │
│  │    2. Run ONE event                      │ │
│  │    3. Repeat                             │ │
│  └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

### Microtask vs Event Queue

| Feature | Microtask Queue | Event Queue |
|---------|----------------|-------------|
| Priority | Higher (runs first) | Lower (runs after all microtasks) |
| Created by | `scheduleMicrotask()`, `Future.microtask()` | `Future()`, `Future.delayed()`, I/O, Timer |
| Use case | Short internal async work | User-visible async work, I/O |
| Starvation | Can starve event queue | Can't starve microtask queue |

```dart
void main() {
  print('1. Start');

  // Event queue — runs after all microtasks
  Future(() => print('4. Future (event queue)'));

  // Microtask queue — runs before event queue
  scheduleMicrotask(() => print('3. Microtask'));

  // Future.value — completes in microtask queue
  Future.value(42).then((v) => print('2. Future.value: $v'));

  print('5. End of main');
}

// Output:
// 1. Start
// 5. End of main
// 2. Future.value: 42
// 3. Microtask
// 4. Future (event queue)
```

### `Future` vs `Future.microtask` vs `Future.delayed`

```dart
// Future() — schedules on EVENT queue (lower priority)
Future(() => print('Event queue'));

// Future.microtask() — schedules on MICROTASK queue (higher priority)
Future.microtask(() => print('Microtask queue'));

// Future.delayed() — schedules on EVENT queue after delay
Future.delayed(Duration(seconds: 1), () => print('Delayed event'));

// Future.value() — completes synchronously, then .then() runs in microtask
Future.value(42).then((v) => print('Value: $v'));
```

---

## 6. Zones

Zones provide an execution context — they can intercept async errors, wrap callbacks, and track async operations.

```dart
import 'dart:async';

void main() {
  // Run code in a zone that catches all uncaught async errors
  runZonedGuarded(() {
    // This async error would normally be uncaught
    Future(() {
      throw Exception('Async error in zone!');
    });
  }, (error, stackTrace) {
    print('Zone caught error: $error');
    print('Stack: $stackTrace');
  });

  // Zone with custom print
  runZoned(
    () {
      print('This print is intercepted');
    },
    zoneSpecification: ZoneSpecification(
      print: (self, parent, zone, message) {
        parent.print(zone, '[INTERCEPTED] $message');
      },
    ),
  );
}

// Output:
// Zone caught error: Exception: Async error in zone!
// [INTERCEPTED] This print is intercepted
```

### Why Zones Matter in Flutter

- **Error boundaries:** `runZonedGuarded` catches async errors that `try/catch` can't (uncaught Future errors).
- **Flutter framework:** Flutter runs your app inside a zone to catch uncaught errors and report them to `FlutterError.onError`.
- **Testing:** `test()` and `group()` use zones to isolate test failures.

---

## 7. Completer

A `Completer<T>` lets you manually complete a `Future` — useful when bridging callback-based APIs to Future-based code.

```dart
import 'dart:async';

// Bridge a callback-based API to Future
Future<String> fetchFromCallbackApi() {
  final completer = Completer<String>();

  // Simulate a callback-based API
  someCallbackApi(
    onSuccess: (data) => completer.complete(data),
    onError: (error) => completer.completeError(error),
  );

  return completer.future;
}

void someCallbackApi({
  required void Function(String) onSuccess,
  required void Function(String) onError,
}) {
  Future.delayed(Duration(seconds: 1), () {
    onSuccess('Data from callback API');
  });
}

// Completer with timeout
Future<String> fetchWithTimeout() async {
  final completer = Completer<String>();

  // Auto-complete with error after timeout
  final timer = Timer(Duration(seconds: 5), () {
    if (!completer.isCompleted) {
      completer.completeError(TimeoutException('Request timed out'));
    }
  });

  // Simulate async work
  Future.delayed(Duration(seconds: 2), () {
    if (!completer.isCompleted) {
      completer.complete('Data received');
      timer.cancel();
    }
  });

  return completer.future;
}

void main() async {
  print(await fetchFromCallbackApi());  // Data from callback API
  print(await fetchWithTimeout());       // Data received
}
```

---

## 8. StreamController & Stream Transformers

### StreamController

A `StreamController` gives you manual control over a stream — you can add data, errors, and close it.

```dart
import 'dart:async';

// Single-subscription stream controller
final controller = StreamController<int>();

// Add a listener
controller.stream.listen(
  (data) => print('Received: $data'),
  onError: (error) => print('Error: $error'),
  onDone: () => print('Stream closed'),
);

// Emit data
controller.add(1);
controller.add(2);
controller.addError('Something went wrong');
controller.add(3);
controller.close();  // Triggers onDone

// Broadcast stream controller — multiple listeners
final broadcastController = StreamController<int>.broadcast();
broadcastController.stream.listen((d) => print('Listener 1: $d'));
broadcastController.stream.listen((d) => print('Listener 2: $d'));
broadcastController.add(42);  // Both listeners receive it
broadcastController.close();
```

### Single-Subscription vs Broadcast Stream

| Feature | Single-Subscription | Broadcast |
|---------|-------------------|-----------|
| Listeners | Only 1 | Multiple |
| Buffering | Buffers events if no listener | Drops events if no listener |
| Use case | File I/O, one-time data | Real-time updates, events |
| Controller | `StreamController<T>()` | `StreamController<T>.broadcast()` |

### Stream Transformers

```dart
// Transform a stream — map, filter, etc.
Stream<int> numbers() async* {
  for (int i = 1; i <= 10; i++) {
    await Future.delayed(Duration(milliseconds: 100));
    yield i;
  }
}

void main() async {
  // map — transform each value
  await for (final n in numbers().map((n) => n * 10)) {
    print(n);  // 10, 20, 30, ..., 100
  }

  // where — filter values
  await for (final n in numbers().where((n) => n.isEven)) {
    print(n);  // 2, 4, 6, 8, 10
  }

  // take — take first N values
  await for (final n in numbers().take(3)) {
    print(n);  // 1, 2, 3
  }

  // skip — skip first N values
  await for (final n in numbers().skip(7)) {
    print(n);  // 8, 9, 10
  }

  // Custom transformer
  final doubledEvens = numbers().transform(
    StreamTransformer<int, int>.fromHandlers(
      handleData: (int value, EventSink<int> sink) {
        if (value.isEven) {
          sink.add(value * 2);  // Only even numbers, doubled
        }
      },
      handleError: (error, stackTrace, sink) {
        sink.addError('Transformed error: $error');
      },
      handleDone: (EventSink<int> sink) {
        sink.add(-1);  // Add sentinel value before closing
        sink.close();
      },
    ),
  );

  await for (final n in doubledEvens) {
    print(n);  // 4, 8, 12, 16, 20, -1
  }
}
```

### Stream Methods Summary

| Method | Description |
|--------|-------------|
| `.map(f)` | Transform each value |
| `.where(f)` | Filter values |
| `.take(n)` | Take first N values |
| `.skip(n)` | Skip first N values |
| `.distinct()` | Remove consecutive duplicates |
| `.debounce(d)` | Drop values emitted too quickly |
| `.timeout(d)` | Error if no value within duration |
| `.toList()` | Collect all values into a List |
| `.fold(initial, f)` | Reduce to single value |
| `.first` / `.last` | Get first/last value (as Future) |
| `.isEmpty` | Whether stream has no values |
| `.length` | Number of values |

---

## 9. Isolates (In Depth)

Dart is single-threaded. Isolates provide **true parallelism** — each isolate has its own memory heap (no shared state).

### Why Isolates?

Dart runs on a single thread (event loop). Heavy CPU work (parsing, image processing, crypto) blocks the UI. Isolates solve this by running work on a separate thread with its own memory — no shared state means no locks, no race conditions.

### Three Ways to Use Isolates

| Method | Use Case | Two-way communication? | Since |
|--------|----------|:------------------------:|-------|
| `Isolate.run()` | One-shot heavy task, get result back | ❌ (returns Future) | Dart 2.19 |
| `compute()` | Flutter wrapper for `Isolate.run()` | ❌ (returns Future) | Flutter |
| `Isolate.spawn()` | Long-running isolate, continuous messaging | ✅ (SendPort/ReceivePort) | All |

### Method 1: `Isolate.run()` — One-shot Task

```dart
import 'dart:isolate';

// Top-level or static function (must be callable from isolate)
int heavyComputation(int n) {
  int sum = 0;
  for (int i = 0; i < n; i++) {
    sum += i;
  }
  return sum;
}

void main() async {
  // Runs heavyComputation on a separate isolate — UI stays smooth!
  int result = await Isolate.run(() => heavyComputation(100000000));
  print('Result: $result');  // 4999999950000000
}
```

### Method 2: `compute()` — Flutter Convenience

```dart
import 'package:flutter/foundation.dart';

// Same as Isolate.run() but works on all Flutter versions
Future<int> doHeavyWork() async {
  return compute(heavyComputation, 100000000);
}
```

### Method 3: `Isolate.spawn()` — Long-running with Two-way Communication

```dart
import 'dart:isolate';

void workerIsolate(SendPort mainSendPort) {
  // Create a receive port in the worker
  final workerReceivePort = ReceivePort();

  // Send our receive port to main so main can send messages to us
  mainSendPort.send(workerReceivePort.sendPort);

  // Listen for messages from main
  workerReceivePort.listen((message) {
    if (message == 'ping') {
      mainSendPort.send('pong');
    } else if (message == 'shutdown') {
      workerReceivePort.close();
      mainSendPort.send('shutdown_complete');
    } else {
      // Process data and send result back
      final result = (message as int) * 2;
      mainSendPort.send(result);
    }
  });
}

void main() async {
  final mainReceivePort = ReceivePort();
  final isolate = await Isolate.spawn(workerIsolate, mainReceivePort.sendPort);

  // Wait for worker to send its SendPort
  final workerSendPort = await mainReceivePort.first as SendPort;

  // Now create a new receive port for ongoing communication
  final ongoingPort = ReceivePort();
  workerSendPort.send('ping');
  // ... communicate as needed

  // Shutdown
  workerSendPort.send('shutdown');
  isolate.kill(priority: Isolate.immediate);
  mainReceivePort.close();
}
```

### Isolate Best Practices

- **Don't use isolates for I/O** (network, file, database) — Dart's async I/O is already non-blocking and doesn't block the event loop.
- **Use isolates for CPU-heavy work** — JSON parsing of large payloads, image processing, crypto, sorting large lists.
- **Data is copied** — Isolates don't share memory. Data passed between isolates is deep-copied (or transferred for `TransferableTypedData`).
- **Functions must be top-level or static** — Isolates can't access instance methods (no shared memory).

---

## 10. Records & Patterns (Dart 3+)

Records are anonymous aggregate types. Patterns enable destructuring and exhaustive switch expressions.

### Records

```dart
// Positional record — access via $1, $2, etc.
(String, int) user = ('Alice', 30);
print(user.$1);  // Alice
print(user.$2);  // 30

// Named record — access via field name
({String name, int age}) person = (name: 'Bob', age: 25);
print(person.name);  // Bob
print(person.age);   // 25

// Mixed record
(String, {int age}) mixed = ('Charlie', age: 40);
print(mixed.$1);     // Charlie
print(mixed.age);    // 40

// Record as return type — multiple return values
(String, bool) validate(String email) {
  return (email, email.contains('@'));
}

final (email, isValid) = validate('alice@example.com');
print('$email: $isValid');  // alice@example.com: true
```

### Patterns

```dart
// Destructuring records
final (name, age) = ('Alice', 30);
print('$name is $age');  // Alice is 30

// Destructuring lists
final [first, second, ...rest] = [1, 2, 3, 4, 5];
print(first);  // 1
print(second); // 2
print(rest);    // [3, 4, 5]

// Destructuring maps
final {'name': n, 'age': a} = {'name': 'Alice', 'age': 30};
print('$n, $a');  // Alice, 30

// Switch expression with patterns
String describe(Object obj) => switch (obj) {
  int i when i > 0 => 'Positive integer: $i',
  int i when i < 0 => 'Negative integer: $i',
  int i => 'Zero',
  String s when s.isEmpty => 'Empty string',
  String s => 'String: "$s"',
  List<int> list when list.isEmpty => 'Empty int list',
  List<int> list => 'Int list: $list',
  (String, int) record => 'Record: ${record.$1}, ${record.$2}',
  _ => 'Unknown: $obj',
};

print(describe(42));           // Positive integer: 42
print(describe(-5));            // Negative integer: -5
print(describe('hello'));       // String: "hello"
print(describe(('Alice', 30))); // Record: Alice, 30
```

### Exhaustive Switch with Sealed Classes

```dart
sealed class Result<T> {
  const Result();
}

class Success<T> extends Result<T> {
  final T data;
  const Success(this.data);
}

class Failure<T> extends Result<T> {
  final String error;
  const Failure(this.error);
}

class Loading<T> extends Result<T> {
  const Loading();
}

// Exhaustive — compiler enforces all cases
String handleResult<T>(Result<T> result) => switch (result) {
  Success(data: final d) => 'Success: $d',
  Failure(error: final e) => 'Error: $e',
  Loading() => 'Loading...',
};

// If a new subtype is added to Result, this switch will fail to compile
// until you handle the new case — that's exhaustive matching!
```

---

## 11. Sealed Classes

Sealed classes create **closed hierarchies** — all subtypes must be in the same library and are known at compile time, enabling exhaustive pattern matching.

| Feature | `sealed` | `abstract` | `abstract interface` |
|---------|----------|-----------|---------------------|
| Subtypes | Same library only | Anywhere | Anywhere |
| Exhaustive switch | ✅ Yes | ❌ No | ❌ No |
| Can be instantiated? | ❌ No | ❌ No | ❌ No |
| Use case | Fixed set of subtypes | Open hierarchies | Pure interfaces |

```dart
sealed class UiState {
  const UiState();
}

class Idle extends UiState {
  const Idle();
}

class Loading extends UiState {
  const Loading();
}

class Success<T> extends UiState {
  final T data;
  const Success(this.data);
}

class Error extends UiState {
  final String message;
  const Error(this.message);
}

// Exhaustive switch — compiler knows ALL subtypes
Widget build(UiState state) => switch (state) {
  Idle() => Text('Idle'),
  Loading() => CircularProgressIndicator(),
  Success(data: final d) => Text('Data: $d'),
  Error(message: final m) => Text('Error: $m'),
  // No default case needed — compiler knows all cases are covered
};
```

### When to Use Sealed vs Abstract

| Use `sealed` when... | Use `abstract` when... |
|---------------------|----------------------|
| Set of subtypes is fixed and known | Hierarchy is open for extension |
| You want exhaustive switch | You don't need exhaustive matching |
| Result types (Success/Failure/Loading) | Framework base classes |
| State machines | Plugin architectures |

---

## 12. Extension Types (Dart 3.3+)

Extension types provide zero-cost wrappers around existing types — like `typedef` but with methods and interfaces.

```dart
// Extension type — wraps an int with type-safe methods
extension type Temperature(int _celsius) {
  // Can add methods, getters, operators
  double get fahrenheit => _celsius * 9 / 5 + 32;

  Temperature operator +(Temperature other) => Temperature(_celsius + other._celsius);

  bool get isFreezing => _celsius <= 0;
  bool get isBoiling => _celsius >= 100;

  @override
  String toString() => '$_celsius°C (${fahrenheit.toStringAsFixed(1)}°F)';
}

void main() {
  final roomTemp = Temperature(22);
  print(roomTemp);              // 22°C (71.6°F)
  print(roomTemp.fahrenheit);   // 71.6
  print(roomTemp.isFreezing);   // false

  final boiling = Temperature(100);
  print(roomTemp + boiling);   // 122°C (251.6°F)
  print(boiling.isBoiling);    // true
}

// Extension type with representation method
extension type UserId(int value) {
  // The underlying int is NOT directly accessible unless you expose it
  // This provides type safety — can't mix UserId with regular int
}

extension type ProductId(int value) {}

void processUser(UserId id) { /* ... */ }
void processProduct(ProductId id) { /* ... */ }

void main() {
  final userId = UserId(42);
  final productId = ProductId(42);

  processUser(userId);      // ✅ OK
  // processUser(productId); // ❌ Compile error — type mismatch!
  // processUser(42);         // ❌ Compile error — int is not UserId
}
```

### Extension Type vs Extension vs Typedef

| Feature | Extension Type | Extension | Typedef |
|---------|---------------|-----------|--------|
| Creates new type? | ✅ Yes | ❌ No (adds methods) | ❌ No (alias) |
| Runtime cost? | Zero (compile-time) | Zero | Zero |
| Can have methods? | ✅ Yes | ✅ Yes | ❌ No |
| Type-safe from mixing? | ✅ Yes | ❌ No | ❌ No |
| Use case | Type-safe wrapper | Add methods to existing type | Simple alias |

---

## 13. Memory Management & GC

Dart uses a **generational garbage collector** — objects are divided into young and old generations.

### GC Architecture

```
┌──────────────────────────────────────────────┐
│              Dart Heap                         │
│                                               │
│  ┌─────────────────┐  ┌──────────────────┐   │
│  │  Young Generation │  │  Old Generation   │   │
│  │  (Nursery)        │  │  (Tenured)        │   │
│  │                   │  │                   │   │
│  │  - Short-lived     │  │  - Long-lived     │   │
│  │    objects          │  │    objects        │   │
│  │  - Frequent GC      │  │  - Infrequent GC  │   │
│  │  - Fast collection  │  │  - Mark-sweep     │   │
│  │  (scavenge)         │  │                   │   │
│  └─────────┬─────────┘  └──────────────────┘   │
│            │                                   │
│            │ Survives GC → promoted to Old     │
│            └───────────────────────────────►  │
└──────────────────────────────────────────────┘
```

### How It Works

1. **Young Generation (Nursery):** New objects are allocated here. GC runs frequently (scavenge) — copies live objects to a new space, dead objects are discarded. Very fast.
2. **Old Generation (Tenured):** Objects that survive multiple young-gen GCs are promoted here. GC runs less frequently (mark-sweep-compact). Slower but rare.
3. **Promotion:** If an object survives 2 young-gen GCs, it's promoted to old gen.

### Memory Best Practices

```dart
// ❌ BAD — creates many temporary objects in a loop
String buildString(int n) {
  String result = '';
  for (int i = 0; i < n; i++) {
    result += i.toString();  // Creates new String each iteration
  }
  return result;
}

// ✅ GOOD — uses StringBuffer (mutable, single allocation)
String buildStringGood(int n) {
  final buffer = StringBuffer();
  for (int i = 0; i < n; i++) {
    buffer.write(i.toString());
  }
  return buffer.toString();
}

// ❌ BAD — holds references in closures, preventing GC
class LeakyClass {
  final List<VoidCallback> _callbacks = [];

  void registerCallback(VoidCallback cb) {
    _callbacks.add(cb);  // Never removed — memory leak!
  }
}

// ✅ GOOD — provide a way to unregister
class CleanClass {
  final List<VoidCallback> _callbacks = [];

  void registerCallback(VoidCallback cb) {
    _callbacks.add(cb);
  }

  void unregisterCallback(VoidCallback cb) {
    _callbacks.remove(cb);
  }
}
```

### `weak` and `final` References (Dart 3.3+)

```dart
// WeakMap — keys are weakly held, can be GC'd
final cache = Expando<String>();  // Expando = weak map

class ExpensiveObject {
  String get data => 'expensive data';
}

void main() {
  final obj = ExpensiveObject();
  cache[obj] = 'cached value';

  print(cache[obj]);  // cached value

  // When obj is GC'd, the entry is automatically removed
  // No memory leak!
}
```

---

## 14. Dart FFI (Foreign Function Interface)

FFI allows Dart to call C/C++/Rust code directly — no method channels, no serialization overhead.

```dart
import 'dart:ffi';
import 'dart:io';
import 'package:ffi/ffi.dart';

// 1. Load the native library
final DynamicLibrary nativeLib = Platform.isAndroid
    ? DynamicLibrary.open('libnative.so')
    : DynamicLibrary.process();

// 2. Bind a C function
// C: int add(int a, int b);
typedef NativeAdd = Int32 Function(Int32 a, Int32 b);
typedef DartAdd = int Function(int a, int b);

final int Function(int, int) add = nativeLib
    .lookupFunction<NativeAdd, DartAdd>('add');

// 3. Call it — zero overhead, direct native call
void main() {
  print(add(3, 4));  // 7 — calls C function directly
}

// String passing (more complex — requires memory management)
// C: const char* greet(const char* name);
typedef NativeGreet = Pointer<Utf8> Function(Pointer<Utf8> name);
typedef DartGreet = Pointer<Utf8> Function(Pointer<Utf8> name);

final DartGreet greet = nativeLib
    .lookupFunction<NativeGreet, DartGreet>('greet');

void main() {
  final name = 'Alice'.toNativeUtf8();  // Allocate C string
  final result = greet(name);           // Call C function
  print(result.toDartString());         // Convert back to Dart string
  calloc.free(name);                    // Free allocated memory
  calloc.free(result);                  // Free result memory
}
```

### FFI vs Platform Channels

| Feature | FFI | Platform Channels |
|---------|-----|-------------------|
| Overhead | Near-zero | Serialization + async |
| Languages | C, C++, Rust, Go | Kotlin/Java, Swift/ObjC |
| Sync calls? | ✅ Yes | ❌ No (always async) |
| Memory management | Manual | Automatic (GC) |
| Use case | Computation-heavy, libraries | Platform APIs (camera, sensors) |

---

## ❓ Interview Questions

1. **What are generics in Dart and are they reified?**
   - Generics enable type-safe, reusable code. Dart uses **reified generics** — type information is preserved at runtime (unlike Java's type erasure). You can check `T` at runtime, use `is T`, and `runtimeType` returns the actual type. Generic constraints use `extends` (e.g., `class Store<T extends num>`). Generics work on methods, classes, and typedefs.

2. **What is the Dart event loop and how does it work?**
   - Dart is single-threaded with an event loop that processes two queues: **microtask queue** (high priority, runs all before any event) and **event queue** (normal priority, runs one per loop iteration). `scheduleMicrotask()` and `Future.microtask()` go to microtask queue; `Future()`, `Future.delayed()`, I/O, and Timer go to event queue. The loop: run ALL microtasks → run ONE event → repeat. Microtasks can starve the event queue.

3. **What are zones in Dart?**
   - Zones provide an execution context that can intercept async errors, wrap callbacks, and track async operations. `runZonedGuarded()` catches uncaught async errors (Futures without `.catchError`). Flutter runs your app inside a zone to catch uncaught errors and report them to `FlutterError.onError`. Zones can also intercept `print`, `Timer`, and `scheduleMicrotask` via `ZoneSpecification`.

4. **What is a Completer and when do you use it?**
   - A `Completer<T>` lets you manually complete a `Future` — useful when bridging callback-based APIs to Future-based code. You create a Completer, return `completer.future`, and call `completer.complete(value)` or `completer.completeError(error)` when the async operation finishes. Use cases: wrapping callback APIs, implementing timeouts, converting event-based APIs to Futures.

5. **What is the difference between single-subscription and broadcast streams?**
   - Single-subscription streams allow only ONE listener and buffer events if no listener is attached. Broadcast streams allow MULTIPLE listeners but drop events if no listener is attached. Use single-subscription for one-time data (file I/O, HTTP response) and broadcast for real-time events (WebSocket, sensor data, state changes). `StreamController<T>()` creates single-subscription; `StreamController<T>.broadcast()` creates broadcast.

6. **What are isolates and how do they differ from threads?**
   - Isolates provide true parallelism in Dart — each isolate has its own memory heap, event loop, and thread. Unlike threads (shared memory), isolates have NO shared state — they communicate only via message passing (copies of data). This eliminates locks, race conditions, and deadlocks. Use `Isolate.run()` for one-shot tasks, `compute()` (Flutter wrapper), or `Isolate.spawn()` for long-running workers with two-way communication. Don't use isolates for I/O — Dart's async I/O is already non-blocking.

7. **What are records and patterns in Dart 3?**
   - Records are anonymous aggregate types: positional `(String, int)` or named `({String name, int age})`. Accessed via `.$1`, `.$2` (positional) or `.name` (named). Destructuring: `final (name, age) = ('Alice', 30)`. Patterns enable switch expressions with exhaustive matching: `switch (obj) { int i when i > 0 => 'Positive', _ => 'Unknown' }`. Records replace boilerplate classes; patterns replace if-else chains.

8. **What are sealed classes and when do you use them?**
   - Sealed classes create closed hierarchies — all subtypes must be in the same library and are known at compile time. The compiler enforces exhaustive `switch` — if a new subtype is added, all switches must handle it or fail to compile. Use cases: result types (`Success`/`Failure`/`Loading`), UI state patterns, event types. Unlike `abstract`, sealed supports exhaustive pattern matching. Use `sealed` for fixed sets of subtypes; use `abstract` for open hierarchies.

9. **What are extension types in Dart 3.3+?**
   - Extension types provide zero-cost wrappers around existing types — like `typedef` but with methods and interfaces. They create a new type at compile time (no runtime cost) that prevents type mixing. For example, `extension type UserId(int value)` creates a distinct type that can't be confused with `ProductId` even though both wrap an int. Unlike extensions, extension types create a new type (type-safe from mixing). Use for type-safe IDs, units (Temperature, Distance), and domain-specific wrappers.

10. **How does Dart's garbage collector work?**
    - Dart uses a generational GC: objects start in the **young generation** (nursery) where GC runs frequently via scavenge (copying live objects, very fast). Objects surviving multiple young-gen GCs are promoted to the **old generation** where GC runs less frequently via mark-sweep. This is efficient because most objects die young. Best practices: use `StringBuffer` instead of string concatenation in loops, unregister callbacks to prevent leaks, use `Expando` for weak references.

11. **What is Dart FFI and how does it compare to platform channels?**
    - FFI (Foreign Function Interface) allows Dart to call C/C++/Rust code directly with near-zero overhead — no serialization, no async. You load a `DynamicLibrary`, look up functions with `lookupFunction<NativeType, DartType>()`, and call them synchronously. Compared to platform channels: FFI is synchronous and near-zero overhead but requires manual memory management; platform channels are async with serialization overhead but use GC and access platform APIs (camera, sensors). Use FFI for computation-heavy native libraries; use platform channels for platform API access.

---

## 🔗 Related Topics
- [Dart Basics](../beginner/DartBasics.md)
- [Flutter Internals](FlutterInternals.md)
- [Performance](Performance.md)
- [Platform Channels](PlatformChannels.md)
