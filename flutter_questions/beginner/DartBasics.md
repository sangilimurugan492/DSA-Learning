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

## 🔗 Related Topics
- [Basics](Basics.md)
- [Widgets](Widgets.md)
- [State Management](StateManagement.md)
