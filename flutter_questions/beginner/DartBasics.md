# Dart Basics

## 📖 Explanation

Dart is the programming language used by Flutter. It is a modern, object-oriented, statically typed language with sound null safety. Dart supports both JIT (for development — hot reload) and AOT (for production — native performance) compilation.

---

## Table of Contents

1. [Variables & Keywords](#1-variables--keywords)
2. [Built-in Data Types](#2-built-in-data-types)
3. [Type System: var vs dynamic vs Object](#3-type-system-var-vs-dynamic-vs-object)
4. [Type Conversion](#4-type-conversion)
5. [Operators](#5-operators)
6. [Control Flow](#6-control-flow)
7. [Collections (List, Map, Set)](#7-collections-list-map-set)
8. [Collection Operators (spread, collection-if, collection-for)](#8-collection-operators)
9. [String Manipulation](#9-string-manipulation)
10. [Null Safety](#10-null-safety)
11. [Functions](#11-functions)
12. [Classes & Constructors](#12-classes--constructors)
13. [Getters & Setters](#13-getters--setters)
14. [Static Members](#14-static-members)
15. [Enums](#15-enums)
16. [Error Handling](#16-error-handling)
17. [Cascade Notation](#17-cascade-notation)
18. [Async Programming Basics](#18-async-programming-basics)
19. [Interview Questions](#-interview-questions)

---

## 1. Variables & Keywords

Dart has several variable declarations:

| Keyword | Mutability | When is type known | Use case |
|---------|-----------|-------------------|----------|
| `var` | Mutable | Inferred at compile time | General purpose |
| `final` | Immutable (single assignment) | Runtime | Value computed once at runtime |
| `const` | Immutable (deeply) | Compile time | Value known at compile time |
| `late` | Mutable | First use | Initialized later (lazy) |
| `dynamic` | Mutable | Runtime (disables type checking) | Interop, JSON parsing |

```dart
var name = 'Alice';        // Type inferred (String)
String city = 'NYC';       // Explicit type
final age = 30;            // Runtime constant — set once
const pi = 3.14;           // Compile-time constant — deeply immutable
late String description;   // Assigned later, before first use
```

### `final` vs `const` — Key Difference

```dart
// final — runtime constant (value can be computed at runtime)
final DateTime now = DateTime.now();  // ✅ OK — computed at runtime

// const — compile-time constant (value must be known at compile time)
// const DateTime now2 = DateTime.now();  // ❌ Error — not known at compile time

// const is DEEPLY immutable — the entire object graph is immutable
const List<int> a = [1, 2, 3];       // ✅ const list
final List<int> b = [1, 2, 3];       // ✅ mutable list (final reference, mutable content)
// b[0] = 99;  // ✅ OK — final allows content mutation
// a[0] = 99;  // ❌ Error — const list is deeply immutable

// const constructor — makes a class instance compile-time constant
const Point p = Point(3, 4);  // Only works if Point has a const constructor
```

### `late` — Deferred Initialization

```dart
class Config {
  // late — initialized on first access (lazy)
  late final String apiKey = _loadApiKey();  // _loadApiKey() runs on first access

  // late without initializer — must assign before use
  late String databasePath;

  void init(String path) {
    databasePath = path;  // Must assign before first read
  }

  String _loadApiKey() {
    print('Loading API key...');  // Only runs once, on first access
    return 'secret-key-123';
  }
}
```

---

## 2. Built-in Data Types

| Type | Description | Example | Nullable? |
|------|-------------|---------|-----------|
| `int` | 64-bit integer (platform-dependent) | `42` | `int?` |
| `double` | 64-bit floating point (IEEE 754) | `9.99` | `double?` |
| `num` | Supertype of `int` and `double` | `42` or `3.14` | `num?` |
| `bool` | Boolean (`true` / `false`) | `true` | `bool?` |
| `String` | UTF-16 text (immutable) | `'Hello'` | `String?` |
| `List<T>` | Ordered collection (growable or fixed) | `[1, 2]` | `List<T>?` |
| `Map<K,V>` | Key-value pairs | `{'a': 1}` | `Map<K,V>?` |
| `Set<T>` | Unique unordered elements | `{1, 2}` | `Set<T>?` |
| `Runes` | Unicode code points | `Runes('\u{1F600}')` | — |
| `Symbol` | Compile-time symbol | `#mySymbol` | — |
| `dynamic` | Any type (disables type checking) | `'x'` | — |
| `Object` | Root of all non-null types | `'x'` | `Object?` |
| `Null` | The null type | `null` | — |

```dart
int count = 42;
double price = 9.99;
num anyNumber = 5;       // num accepts int or double
anyNumber = 3.14;        // ✅ Also valid

bool isActive = true;
String name = 'Alice';

// Numbers — int vs double
int a = 10;
double b = a.toDouble();  // Explicit conversion
int c = 3.7.toInt();      // Truncates → 3
int d = 3.7.round();     // Rounds → 4

// BigInt — arbitrary precision integers
BigInt huge = BigInt.parse('92233720368547758081234567890');
```

---

## 3. Type System: var vs dynamic vs Object

| Declaration | Type checking | Can reassign to different type? | When to use |
|------------|:------------:|:-------------------------------:|-------------|
| `var x = 5` | ✅ Compile-time | ❌ (type locked to `int`) | General purpose |
| `Object x = 5` | ✅ Compile-time | ✅ (any non-null type) | Storing any value, type-safe |
| `dynamic x = 5` | ❌ Runtime only | ✅ (any type) | JSON parsing, interop (avoid in app code) |

```dart
var x = 5;
// x = 'hello';  // ❌ Compile error — type locked to int

Object y = 5;
y = 'hello';     // ✅ OK — Object accepts any non-null type
// y.length;     // ❌ Compile error — Object has no .length

dynamic z = 5;
z = 'hello';     // ✅ OK — dynamic accepts anything
z.length;        // ✅ Compiles — but checked at runtime (risky!)
z.unknownMethod(); // ✅ Compiles — ❌ NoSuchMethodError at runtime!
```

> **Rule of thumb:** Prefer `var` → `Object` → `dynamic` (in that order). Use `dynamic` only when you must (JSON decoding, platform interop).

---

## 4. Type Conversion

Dart requires **explicit** type conversion — no implicit widening/narrowing.

```dart
// String → number
int.parse('42');              // 42
double.parse('3.14');         // 3.14
int.tryParse('abc');          // null (safe parse — no exception)
double.tryParse('abc');       // null

// Number → String
42.toString();                // '42'
3.14.toStringAsFixed(1);      // '3.1' (1 decimal place)
3.14159.toStringAsPrecision(2); // '3.1' (2 significant digits)

// int ↔ double
int a = 42;
double d = a.toDouble();      // 42.0
int back = d.toInt();         // 42 (truncates)
d.round();                    // 42 (rounds)
d.ceil();                     // 42 (ceiling)
d.floor();                    // 42 (floor)

// String ↔ List (code units)
'Hello'.codeUnits;            // [72, 101, 108, 108, 111]
String.fromCharCodes([72, 105]); // 'Hi'
```

---

## 5. Operators

### Arithmetic Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `+` | Addition | `3 + 2` → `5` |
| `-` | Subtraction | `3 - 2` → `1` |
| `*` | Multiplication | `3 * 2` → `6` |
| `/` | Division (always returns `double`) | `7 / 2` → `3.5` |
| `~/` | Integer division (truncates) | `7 ~/ 2` → `3` |
| `%` | Modulo (remainder) | `7 % 2` → `1` |
| `-` (unary) | Negation | `-a` |
| `++` | Increment | `a++` (post), `++a` (pre) |
| `--` | Decrement | `a--` (post), `--a` (pre) |

```dart
int a = 7, b = 2;
print(a / b);   // 3.5 (double!)
print(a ~/ b);  // 3   (integer division)
print(a % b);   // 1   (remainder)
```

### Relational / Comparison Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `==` | Equal (value equality) | `a == b` |
| `!=` | Not equal | `a != b` |
| `>` | Greater than | `a > b` |
| `<` | Less than | `a < b` |
| `>=` | Greater or equal | `a >= b` |
| `<=` | Less or equal | `a <= b` |

### Logical Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `&&` | Logical AND (short-circuit) | `a > 0 && b > 0` |
| `\|\|` | Logical OR (short-circuit) | `a > 0 \|\| b > 0` |
| `!` | Logical NOT | `!isActive` |

### Bitwise Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `&` | Bitwise AND | `a & b` |
| `\|` | Bitwise OR | `a \| b` |
| `^` | Bitwise XOR | `a ^ b` |
| `~` | Bitwise NOT | `~a` |
| `<<` | Left shift | `a << 2` |
| `>>` | Right shift | `a >> 2` |
| `>>>` | Unsigned right shift | `a >>> 2` |

### Assignment Operators

```dart
var x = 10;
x += 5;   // x = x + 5  → 15
x -= 3;   // x = x - 3  → 12
x *= 2;   // x = x * 2  → 24
x ~/= 5;  // x = x ~/ 5 → 4
x %= 3;   // x = x % 3  → 1
```

### Type Test Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `is` | True if object is of type | `x is int` |
| `is!` | True if object is NOT of type | `x is! String` |
| `as` | Type cast (throws if wrong) | `(obj as String)` |

```dart
Object value = 'Hello';
if (value is String) {
  print(value.length);  // ✅ Dart smart-casts — no cast needed
}

// as — explicit cast (throws if wrong type)
String str = value as String;
```

### Conditional (Ternary) Operator

```dart
int age = 20;
String status = age >= 18 ? 'Adult' : 'Minor';
```

---

## 6. Control Flow

### if-else

```dart
int score = 85;

if (score >= 90) {
  print('A');
} else if (score >= 80) {
  print('B');
} else if (score >= 70) {
  print('C');
} else {
  print('F');
}
```

### switch (statement)

```dart
String day = 'Mon';

switch (day) {
  case 'Mon':
  case 'Tue':               // Fall-through (empty cases)
  case 'Wed':
    print('Weekday');
    break;
  case 'Sat':
  case 'Sun':
    print('Weekend');
    break;
  default:
    print('Unknown');
}
```

### switch (expression — Dart 3+)

```dart
String day = 'Sat';
String type = switch (day) {
  'Mon' || 'Tue' || 'Wed' || 'Thu' || 'Fri' => 'Weekday',
  'Sat' || 'Sun' => 'Weekend',
  _ => 'Unknown',
};
```

### for loop

```dart
// Traditional for loop
for (int i = 0; i < 5; i++) {
  print('Index: $i');
}

// for-in loop (collections)
List<int> nums = [1, 2, 3];
for (int n in nums) {
  print(n);
}

// for-in with index (using entries)
for (final entry in nums.asMap().entries) {
  print('Index ${entry.key}: ${entry.value}');
}
```

### while & do-while

```dart
// while — checks condition first
int i = 0;
while (i < 3) {
  print('While: $i');
  i++;
}

// do-while — executes body at least once
int j = 0;
do {
  print('Do: $j');
  j++;
} while (j < 3);
```

### break & continue

```dart
// break — exits the loop entirely
for (int i = 0; i < 10; i++) {
  if (i == 5) break;       // Stops at i=5
  print(i);
}

// continue — skips to next iteration
for (int i = 0; i < 5; i++) {
  if (i == 2) continue;    // Skips i=2
  print(i);
}

// Labeled break — break out of nested loops
outer:
for (int i = 0; i < 3; i++) {
  for (int j = 0; j < 3; j++) {
    if (i == 1 && j == 1) break outer;  // Exits both loops
    print('i=$i, j=$j');
  }
}
```

### assert

```dart
// assert — only runs in debug mode (ignored in production)
void withdraw(double amount) {
  assert(amount > 0, 'Amount must be positive');
  print('Withdrawing $amount');
}
```

---

## 7. Collections (List, Map, Set)

### List

```dart
// Growable list (default)
List<int> nums = [1, 2, 3];
nums.add(4);                    // [1, 2, 3, 4]
nums.addAll([5, 6]);           // [1, 2, 3, 4, 5, 6]
nums.insert(0, 0);             // [0, 1, 2, 3, 4, 5, 6]
nums.removeAt(0);              // [1, 2, 3, 4, 5, 6]
nums.remove(3);                // [1, 2, 4, 5, 6] — removes first match
nums.length;                   // 5
nums.contains(2);              // true
nums.indexOf(4);              // 2
nums.sublist(1, 3);           // [2, 4] — from index 1 to 3 (exclusive)
nums.reversed.toList();       // [6, 5, 4, 2, 1]

// Fixed-length list (cannot add/remove, but can modify elements)
List<int> fixed = List<int>.filled(5, 0);  // [0, 0, 0, 0, 0]
fixed[0] = 42;                 // ✅ OK
// fixed.add(1);               // ❌ Error — fixed length

// Common methods
[3, 1, 2].sort();             // [1, 2, 3] — in-place sort
[1, 2, 3].map((e) => e * 2);  // [2, 4, 6] — transform
[1, 2, 3, 4].where((e) => e.isEven).toList();  // [2, 4] — filter
[1, 2, 3].fold(0, (a, b) => a + b);  // 6 — reduce
[1, 2, 3].reduce((a, b) => a + b);   // 6 — reduce (no initial)
[1, 2, 3].any((e) => e > 2);  // true — any match
[1, 2, 3].every((e) => e > 0); // true — all match
[1, 2, 3].forEach(print);     // 1, 2, 3
```

### Map

```dart
// Literal
Map<String, int> ages = {
  'Alice': 30,
  'Bob': 25,
};

// Constructor
Map<String, String> capitals = Map();
capitals['India'] = 'Delhi';
capitals['Japan'] = 'Tokyo';

// Common methods
ages['Alice'];                // 30
ages.containsKey('Alice');    // true
ages.containsValue(30);      // true
ages.keys;                    // ['Alice', 'Bob']
ages.values;                  // [30, 25]
ages.length;                  // 2
ages['Charlie'] = 35;         // Add/update
ages.remove('Bob');          // Remove
ages.forEach((k, v) => print('$k: $v'));
ages.update('Alice', (v) => v + 1);  // Update value
ages.putIfAbsent('Dave', () => 40);  // Add if missing

// Map.fromEntries — create from key-value pairs
Map<String, int> doubled = Map.fromEntries(
  ages.entries.map((e) => MapEntry(e.key, e.value * 2)),
);
```

### Set

```dart
// Literal
Set<int> unique = {1, 2, 3, 3};  // {1, 2, 3} — duplicates removed

// Constructor
Set<String> names = Set();
names.add('Alice');
names.add('Bob');
names.add('Alice');           // Ignored — already exists

// Set operations
Set<int> a = {1, 2, 3};
Set<int> b = {3, 4, 5};
a.union(b);                   // {1, 2, 3, 4, 5}
a.intersection(b);            // {3}
a.difference(b);              // {1, 2}
a.contains(2);                // true
```

---

## 8. Collection Operators

### Spread Operator (`...`)

```dart
List<int> a = [1, 2];
List<int> b = [0, ...a, 3];   // [0, 1, 2, 3]

// Null-aware spread (...?) — spreads only if not null
List<int>? maybeNull;
List<int> c = [0, ...?maybeNull, 3];  // [0, 3] — null is skipped
```

### Collection-If

```dart
bool isEven = true;
List<int> nums = [
  1,
  if (isEven) 2,    // Included only if condition is true
  3,
  if (!isEven) 4,
];
// [1, 2, 3]
```

### Collection-For

```dart
List<int> input = [1, 2, 3];
List<int> doubled = [
  for (int n in input) n * 2,   // [2, 4, 6]
];

// Combined: collection-if + collection-for
List<int> evens = [
  for (int i = 0; i < 10; i++)
    if (i.isEven) i,
];
// [0, 2, 4, 6, 8]
```

---

## 9. String Manipulation

```dart
// String interpolation
String name = 'Alice';
int age = 30;
print('Hello, $name! You are $age years old.');
print('Length: ${name.length}');  // ${} for expressions

// Multiline strings
String multiline = '''
Line 1
Line 2
Line 3
''';

// Raw strings (no escape processing)
String raw = r'C:\Users\Alice\nNewFolder';  // \n is literal, not newline
String dollar = r'Price: $5.99';            // $ is literal

// Common methods
'Hello World'.toLowerCase();        // 'hello world'
'Hello World'.toUpperCase();        // 'HELLO WORLD'
'  hello  '.trim();                // 'hello'
'  hello  '.trimLeft();             // 'hello  '
'  hello  '.trimRight();            // '  hello'
'Hello'.split('');                  // ['H', 'e', 'l', 'l', 'o']
'a,b,c'.split(',');                 // ['a', 'b', 'c']
['a', 'b', 'c'].join('-');         // 'a-b-c'
'Hello'.substring(1, 3);           // 'el' (start inclusive, end exclusive)
'Hello'.replaceAll('l', 'L');      // 'HeLLo'
'Hello'.contains('ell');           // true
'Hello'.startsWith('He');         // true
'Hello'.endsWith('lo');          // true
'Hello'.padLeft(10, '*');         // '*****Hello'
'Hello'.padRight(10, '*');        // 'Hello*****'
'5'.padLeft(3, '0');              // '005'
'Hello'.codeUnits;                // [72, 101, 108, 108, 111]

// String comparison
'abc'.compareTo('abd');           // -1 (abc < abd)
'abc'.compareTo('abc');           // 0 (equal)
'abd'.compareTo('abc');           // 1 (abd > abc)

// String * (repeat)
'ab' * 3;                          // 'ababab'
```

---

## 10. Null Safety

Dart has **sound null safety** (Dart 2.12+). Types are non-nullable by default.

| Operator | Name | Behavior | Example |
|----------|------|----------|---------|
| `?` | Nullable type | Can hold null | `String? name` |
| `!` | Null assertion | Throws if null | `name!.length` |
| `?.` | Null-aware access | Returns null if null | `name?.length` |
| `??` | Null-coalescing | Default value if null | `name ?? 'Unknown'` |
| `??=` | Null-aware assign | Assign if null | `name ??= 'Default'` |
| `...?` | Null-aware spread | Spread if not null | `[...?list]` |

```dart
// Non-nullable — CANNOT be null
String name = 'Alice';
// name = null;  // ❌ Compile error

// Nullable — CAN be null
String? nickname;
print(nickname);  // null

// ? — null-aware access (returns null instead of throwing)
int? length = nickname?.length;  // null (no crash)
print(length);  // null

// ?? — null-coalescing (provide default)
String display = nickname ?? 'Anonymous';  // 'Anonymous'

// ??= — assign if null
nickname ??= 'Default';  // nickname is now 'Default'

// ! — null assertion (I KNOW it's not null — throws if wrong)
String forced = nickname!;  // OK if not null, throws if null

// Safe navigation chain
String? city = user?.address?.city;  // null at any point → null
```

### `late` and Null Safety

```dart
class Service {
  // late — tells compiler "I'll initialize this before use"
  late final Database db;

  void init() {
    db = Database.connect();  // Must assign before first read
  }

  // late with initializer — lazy evaluation
  late final String config = _loadConfig();  // Runs on first access

  String _loadConfig() => 'loaded';
}
```

---

## 11. Functions

Dart functions are **first-class citizens** — they can be assigned to variables, passed as arguments, and returned from functions.

### Function Syntax

```dart
// Basic function
int add(int a, int b) => a + b;       // Arrow function (expression body)

// Block body
int add2(int a, int b) {
  return a + b;
}

// Optional positional parameters (square brackets)
void log(String message, [String? tag]) {
  print('[${tag ?? 'INFO'}] $message');
}
log('Hello');              // [INFO] Hello
log('Hello', 'DEBUG');    // [DEBUG] Hello

// Named parameters (curly braces) — optional by default
void greet({String? name, int? age}) {
  print('Hi $name, age $age');
}
greet(name: 'Alice', age: 30);

// Required named parameters
void createUser({required String email}) {
  print('Creating user: $email');
}
createUser(email: 'alice@example.com');

// Default values
void configure({int port = 8080, String host = 'localhost'}) {
  print('$host:$port');
}
configure();                    // localhost:8080
configure(port: 3000);         // localhost:3000
```

### Anonymous Functions (Closures / Lambdas)

```dart
// Anonymous function assigned to variable
var multiply = (int a, int b) => a * b;
print(multiply(3, 4));  // 12

// Passed as argument
[1, 2, 3].map((e) => e * 2).toList();  // [2, 4, 6]
[1, 2, 3].forEach((e) => print(e));

// Closure — captures variables from enclosing scope
Function makeAdder(int n) {
  return (int x) => x + n;  // Captures n
}
var add5 = makeAdder(5);
print(add5(3));  // 8
```

### typedef (Function Type Alias)

```dart
// Define a function type
typedef IntOperation = int Function(int, int);

// Use it as a type annotation
int calculate(IntOperation op, int a, int b) => op(a, b);

int add(int a, int b) => a + b;
int subtract(int a, int b) => a - b;

print(calculate(add, 5, 3));       // 8
print(calculate(subtract, 5, 3));  // 2
```

### Recursive Functions

```dart
int factorial(int n) {
  if (n <= 1) return 1;
  return n * factorial(n - 1);
}
print(factorial(5));  // 120
```

---

## 12. Classes & Constructors

Dart supports short-form constructors, named constructors, redirecting constructors, factory constructors, and mixins.

### Basic Class

```dart
class User {
  // Fields
  final String name;
  final int age;

  // Short-form constructor — auto-assigns parameters to fields
  User(this.name, this.age);

  // Named constructor
  User.guest() : name = 'Guest', age = 0;

  // Redirecting constructor — delegates to another constructor
  User.admin(String name) : this(name, 99);

  // Factory constructor — can return existing instance or subtype
  factory User.fromJson(Map<String, dynamic> json) {
    return User(json['name'] as String, json['age'] as int);
  }

  // Method
  String greet() => 'Hi, I am $name';

  // Getter
  bool get isAdult => age >= 18;

  @override
  String toString() => 'User($name, $age)';
}
```

### Constructor with initializer list

```dart
class Rectangle {
  final double width;
  final double height;
  final double area;

  // Initializer list runs BEFORE constructor body
  Rectangle(this.width, this.height) : area = width * height;

  // Named constructor with validation in initializer
  Rectangle.square(double side) : width = side, height = side, area = side * side;
}
```

### Inheritance

```dart
class Animal {
  String name;
  Animal(this.name);

  void eat() => print('$name is eating');
}

class Dog extends Animal {
  Dog(String name) : super(name);

  @override
  void eat() {
    super.eat();  // Call parent method
    print('$name is eating dog food');
  }

  void bark() => print('$name says Woof!');
}
```

### Abstract Class vs Interface vs Mixin

| Concept | Keyword | Purpose | Can have state? | Can have implementation? | Multiple allowed? |
|--------|---------|---------|:---------------:|:------------------------:|:-----------------:|
| `abstract class` | `extends` | Shared base with partial implementation | ✅ | ✅ (partial) | ❌ (single) |
| `interface` | `implements` | Pure contract, enforce API | ❌ | ❌ (must reimplement all) | ✅ (multiple) |
| `mixin` | `with` | Reusable behavior across unrelated classes | ✅ | ✅ | ✅ (multiple) |

```dart
// ─────────────────────────────────────────────────────────────
// 1. ABSTRACT CLASS — "Partial implementation, shared base"
// ─────────────────────────────────────────────────────────────
abstract class PaymentProcessor {
  double amount;
  PaymentProcessor(this.amount);

  void logTransaction() => print('Processing \$$amount via $runtimeType');
  void processPayment();  // Abstract — subclasses MUST implement
}

class StripeProcessor extends PaymentProcessor {
  StripeProcessor(double amount) : super(amount);
  @override
  void processPayment() {
    logTransaction();
    print('Charging \$$amount via Stripe API');
  }
}

// ─────────────────────────────────────────────────────────────
// 2. INTERFACE — "Contract only, no implementation"
//    In Dart, ANY class can be used as an interface via `implements`.
// ─────────────────────────────────────────────────────────────
abstract class Comparable {
  int compareTo(Comparable other);
}

class Product implements Comparable {
  final String name;
  final double price;
  Product(this.name, this.price);

  @override
  int compareTo(Comparable other) {
    if (other is Product) return price.compareTo(other.price);
    return 0;
  }
}

// ─────────────────────────────────────────────────────────────
// 3. MIXIN — "Reusable behavior, no parent-child relationship"
// ─────────────────────────────────────────────────────────────
mixin Loggable {
  void log(String message) => print('[${DateTime.now()}] $message');
}

mixin Validatable {
  bool isValid();
  void validateOrThrow() {
    if (!isValid()) throw ArgumentError('Validation failed for $runtimeType');
  }
}

// Mixin with constraint — can only be used on Animal subclasses
mixin Swimmer on Animal {
  void swim() => print('$name is swimming 🏊');
}

class User with Loggable, Validatable {
  final String email;
  User(this.email);

  @override
  bool isValid() => email.contains('@');

  void save() {
    validateOrThrow();
    log('Saving user $email');
  }
}
```

### Extensions

```dart
// Add methods to existing types without modifying the original class
extension StringExtensions on String {
  bool get isEmail => contains('@') && contains('.');
  String capitalize() => isEmpty ? this : '${this[0].toUpperCase()}${substring(1)}';
  String reverse() => split('').reversed.join();
}

extension ListExtensions<T> on List<T> {
  T? firstWhereOrNull(bool Function(T) test) {
    for (final item in this) {
      if (test(item)) return item;
    }
    return null;
  }
}

// Usage
print('alice@example.com'.isEmail);    // true
print('hello'.capitalize());            // Hello
print('hello'.reverse());               // olleh
```

---

## 13. Getters & Setters

```dart
class Temperature {
  double _celsius;  // Private field (underscore = library-private)

  Temperature(this._celsius);

  // Getter — computed property
  double get celsius => _celsius;
  double get fahrenheit => _celsius * 9 / 5 + 32;

  // Setter — validation on assignment
  set celsius(double value) {
    if (value < -273.15) throw ArgumentError('Below absolute zero!');
    _celsius = value;
  }

  set fahrenheit(double value) {
    celsius = (value - 32) * 5 / 9;  // Reuses celsius setter
  }
}

void main() {
  final temp = Temperature(25);
  print(temp.fahrenheit);  // 77.0 (getter)
  temp.fahrenheit = 100;   // Setter
  print(temp.celsius);      // 37.78
}
```

---

## 14. Static Members

```dart
class MathUtils {
  // Static field — shared across all instances
  static const double pi = 3.14159;
  static int instanceCount = 0;

  // Static method — no instance needed
  static double circleArea(double radius) => pi * radius * radius;

  // Static getter
  static double get piValue => pi;

  MathUtils() {
    instanceCount++;
  }
}

// Usage — no instance needed
print(MathUtils.pi);                  // 3.14159
print(MathUtils.circleArea(5));       // 78.54
print(MathUtils.piValue);             // 3.14159
```

---

## 15. Enums

### Basic Enum

```dart
enum Color { red, green, blue }

void main() {
  Color c = Color.red;
  print(c);                    // Color.red
  print(c.name);              // 'red'
  print(c.index);             // 0
  print(Color.values);        // [Color.red, Color.green, Color.blue]
}
```

### Enhanced Enum (Dart 2.17+)

```dart
enum HttpStatus {
  ok(200, 'Success'),
  notFound(404, 'Not Found'),
  serverError(500, 'Internal Server Error');

  final int code;
  final String message;
  const HttpStatus(this.code, this.message);

  bool get isError => code >= 400;

  static HttpStatus fromCode(int code) {
    return HttpStatus.values.firstWhere(
      (e) => e.code == code,
      orElse: () => HttpStatus.serverError,
    );
  }
}

void main() {
  final status = HttpStatus.notFound;
  print(status.code);       // 404
  print(status.message);    // Not Found
  print(status.isError);    // true
  print(HttpStatus.fromCode(200));  // HttpStatus.ok
}
```

---

## 16. Error Handling

```dart
// try / catch / finally
void riskyOperation() {
  try {
    final result = int.parse('abc');  // Throws FormatException
    print(result);
  } on FormatException catch (e) {
    // Catch specific exception type
    print('Format error: $e');
  } on IntegerDivisionByZeroException {
    // Catch without variable if not needed
    print('Division by zero!');
  } catch (e, stackTrace) {
    // Catch any exception + stack trace
    print('Unexpected error: $e');
    print('Stack: $stackTrace');
  } finally {
    // Always runs — cleanup
    print('Cleanup');
  }
}

// Custom exceptions
class InvalidAgeException implements Exception {
  final int age;
  InvalidAgeException(this.age);

  @override
  String toString() => 'InvalidAgeException: age $age is not valid';
}

void verifyAge(int age) {
  if (age < 0 || age > 150) {
    throw InvalidAgeException(age);
  }
}

// rethrow — re-throw the caught exception
void process() {
  try {
    verifyAge(-5);
  } on InvalidAgeException {
    print('Logging invalid age...');
    rethrow;  // Re-throw to let caller handle it
  }
}
```

### Error vs Exception

| Feature | `Error` | `Exception` |
|---------|---------|------------|
| When | Programming mistake | Runtime condition |
| Should catch? | ❌ No (fix the bug) | ✅ Yes |
| Examples | `StateError`, `RangeError` | `FormatException`, `TimeoutException` |
| Base class | `Error` | `Exception` |

---

## 17. Cascade Notation

The cascade operator (`..`) allows chaining multiple operations on the same object.

```dart
class StringBuilder {
  final StringBuffer _buffer = StringBuffer();
  StringBuilder append(String s) { _buffer.write(s); return this; }
  String build() => _buffer.toString();
}

// Without cascade — verbose
var sb = StringBuilder();
sb.append('Hello');
sb.append(' ');
sb.append('World');

// With cascade (..) — fluent, returns same object
var result = StringBuilder()
  ..append('Hello')
  ..append(' ')
  ..append('World');
print(result.build());  // Hello World

// Cascade on built-in types
var list = <int>[]
  ..add(1)
  ..add(2)
  ..add(3)
  ..sort();
print(list);  // [1, 2, 3]

// Nested cascade
var paint = Paint()
  ..color = Color.red
  ..strokeWidth = 2.0
  ..style = PaintingStyle.stroke;
```

---

## 18. Async Programming Basics

Dart uses `Future` and `Stream` for async operations, with `async`/`await` syntax.

| Concept | Description |
|---------|-------------|
| `Future<T>` | Value available later (single value) |
| `async` | Marks function as asynchronous |
| `await` | Pauses until Future completes |
| `Stream<T>` | Async sequence of values (multiple) |
| `async*` | Async generator (yields values) |
| `yield` | Emits a value in a stream |

### async / await

```dart
Future<int> fetchCount() async {
  await Future.delayed(Duration(seconds: 1));
  return 42;
}

void main() async {
  int count = await fetchCount();
  print('Count: $count');  // Count: 42
}
```

### Error handling with async

```dart
Future<String> fetchData() async {
  try {
    final response = await http.get('https://api.example.com/data');
    return response.body;
  } on HttpException catch (e) {
    print('Network error: $e');
    rethrow;
  } finally {
    print('Request completed');
  }
}
```

### Future methods

```dart
// Future.wait — run multiple futures in parallel
final results = await Future.wait([
  fetchUser(),
  fetchPosts(),
  fetchSettings(),
]);

// Future.any — returns first to complete
final fastest = await Future.any([
  fetchFromCache(),
  fetchFromNetwork(),
]);

// Future.delayed — delayed execution
await Future.delayed(Duration(seconds: 2));

// Future.value / Future.error
Future.value(42);
Future.error('Something went wrong');
```

### Stream basics

```dart
// async* + yield → multiple values (Stream)
Stream<int> countDown(int from) async* {
  for (int i = from; i >= 1; i--) {
    await Future.delayed(Duration(seconds: 1));
    yield i;
  }
}

void main() async {
  await for (int n in countDown(3)) {
    print('Tick: $n');  // Tick: 3 → Tick: 2 → Tick: 1
  }
}
```

> **Advanced async topics** (Completer, StreamController, Zones, Isolates) are covered in [DartAdvanced.md](../advanced/DartAdvanced.md).

---

## 🧪 Code Example

```dart
void main() {
  // Variables and data types
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

---

## ❓ Interview Questions

1. **What are variables and data types in Dart?**
   - Dart has `var` (type inferred, mutable), `final` (runtime constant), `const` (compile-time constant), and `late` (declared now, initialized later). Built-in types include `int` (64-bit), `double` (64-bit float), `bool`, `String`, `List`, `Map`, `Set`, `dynamic` (any type, unsafe), and `Object` (any non-null type). Type conversion is explicit — `int.parse('42')`, `42.toString()`, `'3.14'.toDouble()`.

2. **What is the difference between `final` and `const`?**
   - `final` is a runtime constant — assigned once, value can be computed at runtime (`final now = DateTime.now()`). `const` is a compile-time constant — value must be known at compile time, and it's **deeply immutable** (the entire object graph is immutable). `const` can be used for default constructor arguments, but `final` cannot. `const` variables are canonicalized — identical `const` objects share the same instance.

3. **How does null safety work in Dart?**
   - Dart has sound null safety (Dart 2.12+) — types are non-nullable by default. `String` can't be null; `String?` can be null. Operators: `!` (null assertion, throws if null), `?.` (null-aware access, returns null), `??` (null-coalescing, default value), `??=` (assign if null), `...?` (null-aware spread). `late` allows deferring initialization — must be assigned before first use.

4. **What are functions in Dart?**
   - Functions are first-class citizens — can be assigned to variables, passed as arguments, returned from functions. Dart supports: arrow functions (`=>`), named parameters (`{}`), required named parameters (`required`), default values (`= 8080`), positional optional (`[]`), and typedefs (`typedef Transformer = String Function(String)`). Anonymous functions (closures) capture variables from enclosing scope.

5. **What are classes and constructors in Dart?**
   - Dart supports short-form constructors (`User(this.name, this.age)`), named constructors (`User.guest()`), redirecting constructors (`User.admin(String name) : this(name, 99)`), and factory constructors (`factory User.fromJson(...)`). Getters use `get` keyword. Dart has no interfaces keyword — any class can be used as an interface via `implements`. Mixins use `with` keyword for code reuse across hierarchies.

6. **What are mixins in Dart and how do they differ from classes?**
   - Mixins are reusable code blocks shared across class hierarchies using the `with` keyword. Unlike classes, mixins can't be instantiated — they're meant to be mixed in. Mixins can have constraints using `on` keyword (e.g., `mixin Swimmer on Animal`). Use mixins for cross-cutting concerns like logging, validation. Multiple mixins can be applied: `class Service with Logger, Validator`.

7. **What are extensions in Dart?**
   - Extensions add methods to existing types without modifying the original class. `extension StringExtensions on String { bool get isEmail => contains('@'); }`. Extensions can be generic: `extension ListExtensions<T> on List<T>`. They're useful for adding utility methods to framework types. Extensions are resolved at compile time — they don't modify the type system.

8. **What are Futures, async, and await in Dart?**
   - `Future<T>` represents a value available later (single value). `async` marks a function as asynchronous. `await` pauses execution until the Future completes. Error handling uses try/catch/finally. `Future.wait()` runs multiple futures in parallel. `Stream<T>` represents an async sequence of values (multiple). `async*` marks an async generator that yields values.

9. **What is the cascade operator (`..`) in Dart?**
   - The cascade operator allows chaining multiple operations on the same object without repeating the variable name. `list..add(1)..add(2)..sort()` is equivalent to calling `list.add(1); list.add(2); list.sort();`. It returns the original object (not the method return value), enabling fluent APIs. Useful for configuring objects like `Paint()..color = Colors.red..strokeWidth = 2.0`.

10. **What is the difference between `is` and `as` in Dart?**
    - `is` checks if an object is of a given type and returns a boolean — `if (x is String) print(x.length)`. After an `is` check, Dart smart-casts the type within that block. `as` is a type cast that throws if the object is not of the target type — `String s = obj as String`. Use `is` for safe type checking, `as` only when you're certain of the type.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Widgets](Widgets.md)
- [State Management](StateManagement.md)
- [Dart Advanced](../advanced/DartAdvanced.md)
