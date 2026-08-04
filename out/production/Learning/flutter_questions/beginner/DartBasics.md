# Dart Basics

## Q1: What are variables and data types in Dart?

```dart
// Variables
var name = 'Alice';        // Type inferred (String)
String city = 'NYC';       // Explicit type
final age = 30;            // Runtime constant
const pi = 3.14;           // Compile-time constant
late String description;   // Assigned later

// Built-in types
int count = 42;            // 64-bit integer
double price = 9.99;      // 64-bit float
bool isActive = true;      // Boolean
String message = 'Hello';  // String
List<int> nums = [1, 2];  // List (growable)
Map<String, int> map = {}; // Map
Set<String> set = {};      // Set
dynamic anything = 'x';   // Any type (unsafe)
Object obj = 'x';          // Any non-null type

// Type conversion
int.parse('42');           // String → int
double.parse('3.14');      // String → double
42.toString();             // int → String
'3.14'.toDouble();         // String → double

// Null-aware operators
String? name;              // Nullable type
int length = name?.length ?? 0;  // Null-aware access + default
name ??= 'default';        // Assign if null
```

---

## Q2: How does null safety work in Dart?

```dart
// Sound null safety (Dart 2.12+)
// Types are non-nullable by default

String name = 'Alice';     // Non-nullable — can't be null
String? nickname;          // Nullable — can be null

// Null assertion (!) — throws if null
int len = nickname!.length;  // ❌ if nickname is null

// Null-aware access (?.) — returns null if null
int? len2 = nickname?.length;  // Safe — null if nickname is null

// Null-coalescing (??) — default value
String display = nickname ?? 'Anonymous';

// Null-aware assignment (??=)
nickname ??= 'Bob';  // Assign only if null

// Null-aware spread (...)
List<int>? nums;
var all = [1, 2, ...?nums];  // [1, 2] — no error if nums is null

// Late initialization
late String config;
void init() {
  config = loadConfig();  // Must be assigned before use
}
```

| Operator | Name | Behavior |
|----------|------|----------|
| `?` | Nullable type | `String?` can be null |
| `!` | Null assertion | Throws if null |
| `?.` | Null-aware access | Returns null if null |
| `??` | Null-coalescing | Default value if null |
| `??=` | Null-aware assign | Assign if null |
| `...?` | Null-aware spread | Spread if not null |

---

## Q3: What are functions in Dart?

```dart
// Basic function
int add(int a, int b) {
  return a + b;
}

// Arrow function (single expression)
int multiply(int a, int b) => a * b;

// Named parameters (curly braces)
void greet({String? name, int? age}) {
  print('Hello $name, age $age');
}
greet(name: 'Alice', age: 30);  // Named args

// Required named parameter
void createUser({required String email, String? name}) {
  // email is required, name is optional
}
createUser(email: 'a@b.com', name: 'Alice');

// Default values
void configure({int port = 8080, String host = 'localhost'}) {}
configure();  // Uses defaults

// Positional optional (square brackets)
void log(String message, [String? tag]) {
  print('[$tag] $message');
}
log('Hello');           // tag is null
log('Hello', 'INFO');   // tag is 'INFO'

// Function as first-class citizen
void process(String input, String Function(String) transformer) {
  print(transformer(input));
}
process('hello', (s) => s.toUpperCase());  // HELLO

// Typedef
typedef Transformer = String Function(String);
void apply(String input, Transformer fn) {}
```

---

## Q4: What are classes and constructors in Dart?

```dart
class User {
  // Fields
  final String name;
  final int age;
  String? email;

  // Constructor (short form)
  User(this.name, this.age);

  // Named constructor
  User.guest() : name = 'Guest', age = 0;

  // Named constructor with redirecting
  User.admin(String name) : this(name, 99);

  // Factory constructor
  factory User.fromJson(Map<String, dynamic> json) {
    return User(json['name'] as String, json['age'] as int);
  }

  // Getters
  bool get isAdult => age >= 18;

  // Methods
  String greet() => 'Hi, I am $name';

  // toString
  @override
  String toString() => 'User($name, $age)';
}

// Usage
final user = User('Alice', 30);
final guest = User.guest();
final admin = User.admin('Bob');
final fromJson = User.fromJson({'name': 'Charlie', 'age': 25});

print(user.isAdult);  // true
print(user.greet());  // Hi, I am Alice
```

---

## Q5: What are mixins in Dart?

```dart
// Mixin — reusable code shared across class hierarchies
mixin Logger {
  void log(String message) {
    print('[$runtimeType] $message');
  }
}

mixin Validator {
  bool isValid(String input) => input.isNotEmpty;
}

// Apply mixins with 'with' keyword
class UserService with Logger, Validator {
  void createUser(String name) {
    if (!isValid(name)) {
      log('Invalid name');
      return;
    }
    log('Creating user: $name');
  }
}

// Mixin with constraint (on)
mixin OnString on String {
  String shout() => toUpperCase();
}
// Only String subclasses can use this mixin

// Usage
final service = UserService();
service.createUser('Alice');  // [UserService] Creating user: Alice
service.createUser('');        // [UserService] Invalid name
```

| Concept | Keyword | Purpose |
|---------|---------|---------|
| Class | `class` | Define a class |
| Abstract class | `abstract class` | Can't instantiate, can have abstract methods |
| Interface | `implements` | Must implement all members |
| Mixin | `with` | Reuse code across hierarchies |
| Extension | `extension` | Add methods to existing types |

---

## Q6: What are extensions in Dart?

```dart
// Extension — add methods to existing types
extension StringExtensions on String {
  bool get isEmail => contains('@') && contains('.');
  String capitalize() =>
      isEmpty ? this : '${this[0].toUpperCase()}${substring(1)}';
  String reverse() => split('').reversed.join();
}

// Usage
print('alice@test.com'.isEmail);    // true
print('hello'.capitalize());         // Hello
print('flutter'.reverse());          // rettulf

// Extension on List
extension ListExtensions<T> on List<T> {
  T? get firstOrNull => isEmpty ? null : first;
  List<T> shuffled() => [...this]..shuffle();
}

// Extension on int
extension IntExtensions on int {
  bool get isEven => this % 2 == 0;
  Duration get seconds => Duration(seconds: this);
}

// Usage
print([1, 2, 3].firstOrNull);  // 1
print(5.seconds);               // 0:00:05.000000
```

---

## Q7: What are futures, async, and await in Dart?

```dart
// Future — represents a value that will be available later
Future<String> fetchUser() {
  return Future.delayed(const Duration(seconds: 1), () => 'Alice');
}

// async/await — read async code like sync
Future<void> main() async {
  print('Start');
  final user = await fetchUser();  // Waits for result
  print('User: $user');            // Prints after 1 second
  print('End');
}
// Output: Start → (1s) → User: Alice → End

// Error handling
Future<void> loadData() async {
  try {
    final data = await fetchData();
    print(data);
  } catch (e) {
    print('Error: $e');
  } finally {
    print('Done');
  }
}

// Future methods
Future<List<String>> fetchAll() async {
  final results = await Future.wait([
    fetchUser(),
    fetchPosts(),
    fetchComments(),
  ]);
  return results;
}

// then/catchError (alternative to async/await)
fetchUser()
    .then((user) => print(user))
    .catchError((e) => print('Error: $e'));

// Stream — async sequence of values
Stream<int> countDown(int from) async* {
  for (int i = from; i >= 0; i--) {
    await Future.delayed(const Duration(seconds: 1));
    yield i;
  }
}

// Listen to stream
countDown(3).listen((value) {
  print(value);  // 3, 2, 1, 0
});
```

| Concept | Description |
|---------|-------------|
| `Future<T>` | Value available later (single value) |
| `async` | Marks function as asynchronous |
| `await` | Pauses until Future completes |
| `Stream<T>` | Async sequence of values (multiple) |
| `async*` | Async generator (yields values) |
| `yield` | Emits a value in a stream |

---

## Q8: What are isolates and how do they work?

```dart
// Dart is single-threaded — isolates provide true parallelism
// Each isolate has its own memory heap (no shared state)

// Isolate.run (Dart 2.19+) — simplest way
final result = await Isolate.run(() {
  return heavyComputation();  // Runs in separate isolate
});

// compute() — Flutter wrapper for Isolate.run
final result = await compute(_processData, largeList);

List<Result> _processData(List<Data> data) {
  return data.map((e) => complexCalculation(e)).toList();
}

// Isolate.spawn — for long-running isolates with communication
Future<void> startWorker() async {
  final receivePort = ReceivePort();
  await Isolate.spawn(_workerEntry, receivePort.sendPort);

  receivePort.listen((message) {
    print('From worker: $message');
  });
}

void _workerEntry(SendPort sendPort) {
  // Runs in separate isolate
  final result = heavyComputation();
  sendPort.send(result);
}

// Two-way communication
Future<void> twoWayCommunication() async {
  final receivePort = ReceivePort();
  final isolate = await Isolate.spawn(_entry, receivePort.sendPort);

  final sendPort = await receivePort.first as SendPort;
  sendPort.send('Do work');
}
```

| Feature | Main Isolate | Worker Isolate |
|---------|-------------|----------------|
| Memory | Shared | Separate heap |
| UI | ✅ Runs UI | ❌ No UI |
| Concurrency | Single-threaded | True parallelism |
| Communication | Direct | Message passing (SendPort) |
| Use case | UI, event handling | Heavy computation |

> **Rule:** Use isolates for CPU-heavy work (parsing large JSON, image processing, crypto). Don't use isolates for I/O (network, file) — Dart's async I/O is already non-blocking.

---

## Q9: What are records and patterns in Dart 3?

```dart
// Records — anonymous aggregate types (Dart 3+)
// (String, int) — positional fields
// ({String name, int age}) — named fields

// Positional record
(String, int) user = ('Alice', 30);
print(user.$1);  // Alice
print(user.$2);  // 30

// Named record
({String name, int age}) person = (name: 'Bob', age: 25);
print(person.name);  // Bob
print(person.age);   // 25

// Destructuring
final (name, age) = ('Alice', 30);
print(name);  // Alice

final (name: n, age: a) = (name: 'Bob', age: 25);
print(n);  // Bob

// Patterns — switch expressions and destructuring
String describe(Object obj) => switch (obj) {
  int i when i > 0 => 'Positive integer: $i',
  String s => 'String: $s',
  List<int> l => 'Int list: $l',
  (String, int) r => 'Record: ${r.$1}, ${r.$2}',
  null => 'Null',
  _ => 'Unknown',
};

// Pattern matching with if-case
final result = switch (statusCode) {
  200 || 201 => 'Success',
  404 => 'Not found',
  >= 500 => 'Server error',
  _ => 'Unknown: $statusCode',
};

// Map entry destructuring
for (final MapEntry(:key, :value) in map.entries) {
  print('$key: $value');
}
```

> **Key:** Records and patterns (Dart 3+) make Dart more expressive — replacing boilerplate classes with lightweight records, and replacing if-else chains with pattern matching.

---

## Q10: What are sealed classes and when do you use them?

```dart
// Sealed class — closed hierarchy, all subtypes known at compile time
// Used for exhaustive pattern matching

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

// Exhaustive switch — compiler warns if a case is missing
String handleResult(Result<int> result) => switch (result) {
  Success(:final data) => 'Got: $data',
  Failure(:final error) => 'Error: $error',
  Loading() => 'Loading...',
};

// State pattern for BLoC/state management
sealed class UiState<T> {
  const UiState();
}

class Initial<T> extends UiState<T> {
  const Initial();
}

class LoadingState<T> extends UiState<T> {
  const LoadingState();
}

class SuccessState<T> extends UiState<T> {
  final T data;
  const SuccessState(this.data);
}

class ErrorState<T> extends UiState<T> {
  final String message;
  const ErrorState(this.message);
}

// Usage
Widget build(BuildContext context) {
  return switch (state) {
    Initial() => const SizedBox.shrink(),
    LoadingState() => const CircularProgressIndicator(),
    SuccessState(:final data) => ContentWidget(data: data),
    ErrorState(:final message) => ErrorWidget(message: message),
  };
}
```

| Feature | `sealed` | `abstract` | `abstract interface` |
|---------|----------|-----------|---------------------|
| Subtypes | Same library only | Anywhere | Anywhere |
| Exhaustive switch | ✅ Yes | ❌ No | ❌ No |
| Implement | `extends` | `extends`/`implements` | `implements` |
| Use case | Closed hierarchies | Open hierarchies | Pure interfaces |

> **Key:** Use `sealed` when you have a fixed set of subtypes (states, results, events). The compiler enforces exhaustive `switch` — if you add a new subtype, all switches must handle it.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Widgets](Widgets.md)
- [State Management](StateManagement.md)
