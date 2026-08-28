# Dart Basics

## 📖 Explanation

Dart is the programming language used by Flutter. It is a modern, object-oriented, statically typed language with sound null safety. Dart supports both JIT (for development) and AOT (for production) compilation.

### Variables
Dart has several variable declarations:
- **`var`** — Type inferred, mutable. Can be reassigned.
- **`final`** — Runtime constant. Assigned once, cannot be reassigned.
- **`const`** — Compile-time constant. Deeply immutable, value must be known at compile time.
- **`late`** — Declared now, initialized later. Must be assigned before first use.

```dart
var name = 'Alice';        // Type inferred (String)
String city = 'NYC';       // Explicit type
final age = 30;            // Runtime constant
const pi = 3.14;           // Compile-time constant
late String description;   // Assigned later
```

### Built-in Data Types
| Type | Description | Example |
|------|-------------|---------|
| `int` | 64-bit integer | `42` |
| `double` | 64-bit floating point | `9.99` |
| `bool` | Boolean | `true` |
| `String` | Text | `'Hello'` |
| `List<T>` | Ordered collection | `[1, 2]` |
| `Map<K,V>` | Key-value pairs | `{'a': 1}` |
| `Set<T>` | Unique elements | `{1, 2}` |
| `dynamic` | Any type (unsafe) | `'x'` |
| `Object` | Any non-null type | `'x'` |

### Type Conversion
Dart requires explicit type conversion — no implicit widening.
```dart
int.parse('42');           // String → int
double.parse('3.14');      // String → double
42.toString();             // int → String
'3.14'.toDouble();         // String → double
```

### Null Safety
Dart has sound null safety (Dart 2.12+). Types are non-nullable by default.

| Operator | Name | Behavior |
|----------|------|----------|
| `?` | Nullable type | `String?` can be null |
| `!` | Null assertion | Throws if null |
| `?.` | Null-aware access | Returns null if null |
| `??` | Null-coalescing | Default value if null |
| `??=` | Null-aware assign | Assign if null |
| `...?` | Null-aware spread | Spread if not null |

### Functions
Dart functions are first-class citizens — they can be assigned to variables, passed as arguments, and returned from functions.

```dart
// Basic function
int add(int a, int b) => a + b;

// Named parameters (curly braces)
void greet({String? name, int? age}) {}

// Required named parameter
void createUser({required String email}) {}

// Default values
void configure({int port = 8080}) {}

// Positional optional (square brackets)
void log(String message, [String? tag]) {}
```

### Classes and Constructors
Dart supports short-form constructors, named constructors, factory constructors, and mixins.

| Concept | Keyword | Purpose |
|---------|---------|---------|
| Class | `class` | Define a class |
| Abstract class | `abstract class` | Can't instantiate, can have abstract methods |
| Interface | `implements` | Must implement all members |
| Mixin | `with` | Reuse code across hierarchies |
| Extension | `extension` | Add methods to existing types |

**Abstract Class vs Interface vs Mixin — Real-time examples:**

```dart
// ─────────────────────────────────────────────────────────────
// 1. ABSTRACT CLASS — "Partial implementation, shared base"
//    Use when: Multiple classes share common behavior + you want
//    to provide some default implementation.
// ─────────────────────────────────────────────────────────────
abstract class PaymentProcessor {
  // Shared state (abstract classes CAN have fields)
  double amount;

  PaymentProcessor(this.amount);

  // Concrete method — shared by all subclasses
  void logTransaction() {
    print('Processing \$$amount via ${runtimeType}');
  }

  // Abstract method — subclasses MUST implement
  void processPayment();
}

class StripeProcessor extends PaymentProcessor {
  StripeProcessor(double amount) : super(amount);

  @override
  void processPayment() {
    logTransaction();
    print('Charging \$$amount via Stripe API');
  }
}

class PayPalProcessor extends PaymentProcessor {
  PayPalProcessor(double amount) : super(amount);

  @override
  void processPayment() {
    logTransaction();
    print('Charging \$$amount via PayPal API');
  }
}

// ─────────────────────────────────────────────────────────────
// 2. INTERFACE — "Contract only, no implementation"
//    Use when: You want to enforce that a class provides certain
//    methods, but you don't share any implementation.
//    In Dart, ANY class can be used as an interface via `implements`.
// ─────────────────────────────────────────────────────────────
abstract class Comparable {
  int compareTo(Comparable other);  // Contract only — no implementation
}

class Product implements Comparable {
  final String name;
  final double price;

  Product(this.name, this.price);

  @override
  int compareTo(Comparable other) {
    if (other is Product) {
      return price.compareTo(other.price);
    }
    return 0;
  }
}

// ─────────────────────────────────────────────────────────────
// 3. MIXIN — "Reusable behavior, no parent-child relationship"
//    Use when: You want to share behavior across UNRELATED classes
//    that don't share a common parent.
// ─────────────────────────────────────────────────────────────

// Mixin 1: Logging capability
mixin Loggable {
  void log(String message) {
    print('[${DateTime.now()}] $message');
  }
}

// Mixin 2: Validation capability
mixin Validatable {
  bool isValid();

  void validateOrThrow() {
    if (!isValid()) {
      throw ArgumentError('Validation failed for $runtimeType');
    }
  }
}

// Mixin 3: Serialization capability
mixin Serializable {
  Map<String, dynamic> toJson();

  String toJsonString() {
    return toJson().toString();
  }
}

// Mixin with constraint — can only be used on classes that extend Animal
mixin Swimmer on Animal {
  void swim() {
    print('$name is swimming 🏊');
  }
}

abstract class Animal {
  String get name;
}

// ─────────────────────────────────────────────────────────────
// APPLYING MIXINS — combine capabilities without inheritance
// ─────────────────────────────────────────────────────────────
class User with Loggable, Validatable, Serializable {
  final String email;
  final int age;

  User(this.email, this.age);

  @override
  bool isValid() => email.contains('@') && age >= 0;

  @override
  Map<String, dynamic> toJson() => {'email': email, 'age': age};

  void save() {
    validateOrThrow();  // From Validatable mixin
    log('Saving user $email');  // From Loggable mixin
    print('JSON: ${toJsonString()}');  // From Serializable mixin
  }
}

class Fish extends Animal with Swimmer {
  @override
  final String name;

  Fish(this.name);
}

// ─────────────────────────────────────────────────────────────
// REAL-TIME SCENARIO: E-commerce app
// ─────────────────────────────────────────────────────────────
void main() {
  // Abstract class in action
  final stripe = StripeProcessor(99.99);
  stripe.processPayment();
  // Processing $99.99 via StripeProcessor
  // Charging $99.99 via Stripe API

  // Interface in action
  final p1 = Product('Laptop', 999.0);
  final p2 = Product('Mouse', 25.0);
  print(p1.compareTo(p2) > 0 ? 'Laptop is pricier' : 'Mouse is pricier');
  // Laptop is pricier

  // Mixins in action — User gets logging + validation + serialization
  final user = User('alice@example.com', 30);
  user.save();
  // [2026-08-27 ...] Saving user alice@example.com
  // JSON: {email: alice@example.com, age: 30}

  // Mixin with constraint — Fish can swim because it extends Animal
  final nemo = Fish('Nemo');
  nemo.swim();
  // Nemo is swimming 🏊
}
```

**When to use what?**

| Concept | When to use | Can have state? | Can have implementation? | Multiple allowed? |
|--------|-------------|:---------------:|:------------------------:|:-----------------:|
| `abstract class` | Shared base with partial implementation | ✅ | ✅ (partial) | ❌ (single `extends`) |
| `interface` (implements) | Pure contract, enforce API | ❌ | ❌ (must reimplement all) | ✅ (multiple) |
| `mixin` (with) | Reusable behavior across unrelated classes | ✅ | ✅ | ✅ (multiple) |


### Async Programming
Dart uses `Future` and `Stream` for async operations, with `async`/`await` syntax.

| Concept | Description |
|---------|-------------|
| `Future<T>` | Value available later (single value) |
| `async` | Marks function as asynchronous |
| `await` | Pauses until Future completes |
| `Stream<T>` | Async sequence of values (multiple) |
| `async*` | Async generator (yields values) |
| `yield` | Emits a value in a stream |

**`async`/`await` vs `async*`/`yield`:**

| Feature | `async` + `await` | `async*` + `yield` |
|---------|--------------------|--------------------|
| Returns | `Future<T>` (single value) | `Stream<T>` (multiple values) |
| Keyword | `async` | `async*` (note the `*`) |
| Emits via | `return` | `yield` |
| Consumer | `await future` | `await for (var x in stream)` |
| Use case | One-shot async result | Async sequence / events |

```dart
// async + await → single value (Future)
Future<int> fetchCount() async {
  await Future.delayed(Duration(seconds: 1));
  return 42;
}

// async* + yield → multiple values (Stream)
Stream<int> countDown(int from) async* {
  for (int i = from; i >= 1; i--) {
    await Future.delayed(Duration(seconds: 1));
    yield i;  // Emits each value to the stream
  }
}

void main() async {
  // Consuming a Future
  int count = await fetchCount();
  print('Count: $count');  // Count: 42

  // Consuming a Stream with await for
  await for (int n in countDown(3)) {
    print('Tick: $n');  // Tick: 3 → Tick: 2 → Tick: 1
  }
}
```

### Isolates

Dart is single-threaded. Isolates provide true parallelism — each isolate has its own memory heap (no shared state).

| Feature | Main Isolate | Worker Isolate |
|---------|-------------|----------------|
| Memory | Shared | Separate heap |
| UI | ✅ Runs UI | ❌ No UI |
| Concurrency | Single-threaded | True parallelism |
| Communication | Direct | Message passing (SendPort) |

**Why Isolates?** Dart runs on a single thread (event loop). Heavy CPU work (parsing, image processing, crypto) blocks the UI. Isolates solve this by running work on a separate thread with its own memory — no shared state means no locks, no race conditions.

**Three ways to use isolates:**

| Method | Use Case | Two-way communication? |
|--------|----------|------------------------|
| `Isolate.run()` | One-shot heavy task, get result back | ❌ (returns Future) |
| `compute()` | Flutter wrapper for `Isolate.run()` | ❌ (returns Future) |
| `Isolate.spawn()` | Long-running isolate, continuous messaging | ✅ (SendPort/ReceivePort) |

```dart
import 'dart:isolate';

// 1. Isolate.run() — simplest: run a function in a separate isolate, get result back
int heavyComputation(int n) {
  // Simulate CPU-heavy work (e.g., prime counting, image processing)
  int sum = 0;
  for (int i = 0; i < n; i++) {
    sum += i;
  }
  return sum;
}

void main() async {
  // Runs heavyComputation on a separate isolate — UI stays smooth!
  int result = await Isolate.run(() => heavyComputation(100000000));
  print('Result: $result');  // Result: 4999999950000000

  // 2. Isolate.spawn() — long-running isolate with two-way communication
  final receivePort = ReceivePort();
  final isolate = await Isolate.spawn(
    (SendPort sendPort) {
      // This runs in the worker isolate
      sendPort.send('Worker ready!');
      sendPort.send('Processing data...');
      sendPort.send('Done!');
    },
    receivePort.sendPort,
  );

  // Listen for messages from the worker isolate
  await for (final msg in receivePort) {
    print('From worker: $msg');
    if (msg == 'Done!') {
      receivePort.close();
      isolate.kill();
      break;
    }
  }
}
```

**Key points:**
- **No shared memory:** Isolates can't access each other's variables. They communicate only by passing messages (copies of data).
- **`Isolate.run()`** is best for one-shot tasks — it spawns an isolate, runs your function, returns the result, and kills the isolate automatically.
- **`compute()`** is Flutter's convenience wrapper — same as `Isolate.run()` but works on all Flutter versions.
- **`Isolate.spawn()`** is for long-running workers that need continuous two-way communication via `SendPort`/`ReceivePort`.
- **Don't use isolates for I/O** (network, file, database) — Dart's async I/O is already non-blocking and doesn't block the event loop.


### Records and Patterns (Dart 3+)
Records are anonymous aggregate types. Patterns enable destructuring and exhaustive switch expressions.

```dart
// Positional record
(String, int) user = ('Alice', 30);
print(user.$1);  // Alice

// Named record
({String name, int age}) person = (name: 'Bob', age: 25);

// Destructuring
final (name, age) = ('Alice', 30);

// Switch expression
String describe(Object obj) => switch (obj) {
  int i when i > 0 => 'Positive: $i',
  String s => 'String: $s',
  _ => 'Unknown',
};
```

### Sealed Classes
Sealed classes create closed hierarchies — all subtypes are known at compile time, enabling exhaustive pattern matching.

| Feature | `sealed` | `abstract` | `abstract interface` |
|---------|----------|-----------|---------------------|
| Subtypes | Same library only | Anywhere | Anywhere |
| Exhaustive switch | ✅ Yes | ❌ No | ❌ No |
| Use case | Closed hierarchies | Open hierarchies | Pure interfaces |

---

## 🧪 Code Example

```dart
// Variables and data types
void main() {
  var name = 'Alice';
  String city = 'NYC';
  final age = 30;
  const pi = 3.14;

  print('Name: $name, City: $city, Age: $age, Pi: $pi');

  // Built-in types
  int count = 42;
  double price = 9.99;
  bool isActive = true;
  List<int> nums = [1, 2, 3];
  Map<String, int> map = {'a': 1, 'b': 2};

  print('Count: $count, Price: $price, Active: $isActive');
  print('Nums: $nums, Map: $map');

  // Type conversion
  int parsed = int.parse('42');
  String str = 42.toString();
  print('Parsed: $parsed, String: $str');

  // Null safety
  String? nickname;
  int len = nickname?.length ?? 0;
  print('Length: $len');

  // Functions
  int add(int a, int b) => a + b;
  print('3 + 5 = ${add(3, 5)}');

  // Class
  final user = User('Alice', 30);
  print(user.greet());
  print('Is adult: ${user.isAdult}');
}

class User {
  final String name;
  final int age;
  User(this.name, this.age);

  bool get isAdult => age >= 18;
  String greet() => 'Hi, I am $name';

  @override
  String toString() => 'User($name, $age)';
}
```

### Output
```
Name: Alice, City: NYC, Age: 30, Pi: 3.14
Count: 42, Price: 9.99, Active: true
Nums: [1, 2, 3], Map: {a: 1, b: 2}
Parsed: 42, String: 42
Length: 0
3 + 5 = 8
Hi, I am Alice
Is adult: true
```

### Async Generator (`async*` + `yield`) Example

```dart
// async* generator — emits a sequence of values to a Stream
Stream<String> emitMessages(List<String> messages) async* {
  for (final msg in messages) {
    await Future.delayed(Duration(milliseconds: 100));
    yield msg;  // Emits one value at a time
  }
}

// yield* — delegates to another stream (flattens all values)
Stream<int> nestedCount() async* {
  yield* Stream.fromIterable([1, 2, 3]);  // Emits 1, 2, 3
  yield* Stream.fromIterable([4, 5]);     // Emits 4, 5
}

void main() async {
  // Consume stream with await for
  await for (final msg in emitMessages(['Hello', 'World', 'Dart'])) {
    print('Received: $msg');
  }

  // Consume nested stream
  await for (final n in nestedCount()) {
    print('Number: $n');
  }
}
```

### Output
```
Received: Hello
Received: World
Received: Dart
Number: 1
Number: 2
Number: 3
Number: 4
Number: 5
```


---

## ❓ Interview Questions

1. **What are variables and data types in Dart?**
   - Dart has `var` (type inferred, mutable), `final` (runtime constant), `const` (compile-time constant), and `late` (declared now, initialized later). Built-in types include `int` (64-bit), `double` (64-bit float), `bool`, `String`, `List`, `Map`, `Set`, `dynamic` (any type, unsafe), and `Object` (any non-null type). Type conversion is explicit — `int.parse('42')`, `42.toString()`, `'3.14'.toDouble()`.

2. **How does null safety work in Dart?**
   - Dart has sound null safety (Dart 2.12+) — types are non-nullable by default. `String` can't be null; `String?` can be null. Operators: `!` (null assertion, throws if null), `?.` (null-aware access, returns null), `??` (null-coalescing, default value), `??=` (assign if null), `...?` (null-aware spread). `late` allows deferring initialization — must be assigned before first use.

3. **What are functions in Dart?**
   - Functions are first-class citizens — can be assigned to variables, passed as arguments, returned from functions. Dart supports: arrow functions (`=>`), named parameters (`{}`), required named parameters (`required`), default values (`= 8080`), positional optional (`[]`), and typedefs (`typedef Transformer = String Function(String)`).

4. **What are classes and constructors in Dart?**
   - Dart supports short-form constructors (`User(this.name, this.age)`), named constructors (`User.guest()`), redirecting constructors (`User.admin(String name) : this(name, 99)`), and factory constructors (`factory User.fromJson(...)`). Getters use `get` keyword. Dart has no interfaces keyword — any class can be used as an interface via `implements`. Mixins use `with` keyword for code reuse across hierarchies.

5. **What are mixins in Dart and how do they differ from classes?**
   - Mixins are reusable code blocks shared across class hierarchies using the `with` keyword. Unlike classes, mixins can't be instantiated — they're meant to be mixed in. Mixins can have constraints using `on` keyword (e.g., `mixin OnString on String`). Use mixins for cross-cutting concerns like logging, validation. Multiple mixins can be applied: `class Service with Logger, Validator`.

6. **What are extensions in Dart?**
   - Extensions add methods to existing types without modifying the original class. `extension StringExtensions on String { bool get isEmail => contains('@'); }`. Extensions can be generic: `extension ListExtensions<T> on List<T>`. They're useful for adding utility methods to framework types. Extensions are resolved at compile time — they don't modify the type system.

7. **What are Futures, async, and await in Dart?**
   - `Future<T>` represents a value available later (single value). `async` marks a function as asynchronous. `await` pauses execution until the Future completes. Error handling uses try/catch/finally. `Future.wait()` runs multiple futures in parallel. `Stream<T>` represents an async sequence of values (multiple). `async*` marks an async generator that yields values. Alternative to async/await: `.then().catchError()` chaining.

8. **What are isolates and how do they work?**
   - Dart is single-threaded. Isolates provide true parallelism — each isolate has its own memory heap (no shared state). `Isolate.run()` (Dart 2.19+) is the simplest way. `compute()` is Flutter's wrapper. `Isolate.spawn()` creates long-running isolates with two-way communication via `SendPort`/`ReceivePort`. Use isolates for CPU-heavy work (parsing, image processing). Don't use for I/O — Dart's async I/O is already non-blocking.

9. **What are records and patterns in Dart 3?**
   - Records are anonymous aggregate types: positional `(String, int)` or named `({String name, int age})`. Accessed via `.$1`, `.$2` (positional) or `.name` (named). Destructuring: `final (name, age) = ('Alice', 30)`. Patterns enable switch expressions with exhaustive matching: `switch (obj) { int i when i > 0 => 'Positive', _ => 'Unknown' }`. Records replace boilerplate classes; patterns replace if-else chains.

10. **What are sealed classes and when do you use them?**
    - Sealed classes create closed hierarchies — all subtypes must be in the same library and are known at compile time. The compiler enforces exhaustive `switch` — if a new subtype is added, all switches must handle it. Use cases: result types (`Success`/`Failure`/`Loading`), UI state patterns, event types. Unlike `abstract`, sealed supports exhaustive pattern matching. Use `sealed` for fixed sets of subtypes; use `abstract` for open hierarchies.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Widgets](Widgets.md)
- [State Management](StateManagement.md)
