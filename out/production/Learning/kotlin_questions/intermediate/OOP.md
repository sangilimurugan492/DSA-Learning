# Object-Oriented Programming

## 📖 Explanation

Kotlin is fully object-oriented (and functional). It supports classes, inheritance, interfaces, abstract classes, and more — with less boilerplate than Java.

### Class & Constructor
```kotlin
class Person(val name: String, var age: Int)
```
- Primary constructor is in the class header.
- `val`/`var` in constructor params automatically creates properties.

### `init` Block
Runs during object initialization. Can have multiple `init` blocks.

```kotlin
class Person(val name: String) {
    init { println("Person $name created") }
}
```

### Inheritance
Classes are `final` by default. Use `open` to allow inheritance.

```kotlin
open class Animal(val name: String) {
    open fun sound() = "Some sound"
}
class Dog(name: String) : Animal(name) {
    override fun sound() = "Bark"
}
```

### Interface
```kotlin
interface Drawable {
    fun draw()              // abstract
    fun info() = "Drawable"  // default implementation
}
```

### Abstract Class
```kotlin
abstract class Shape {
    abstract fun area(): Double
    fun describe() = "I am a shape"
}
```

### `object` (Singleton)
```kotlin
object Database {
    fun connect() = println("Connected")
}
```

### Companion Object (Static-like)
```kotlin
class User(val name: String) {
    companion object {
        fun create(name: String) = User(name)
    }
}
```

### Enum Class
```kotlin
enum class Color(val hex: String) {
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF")
}
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // Class with primary constructor
    val person = Person("Alice", 30)
    println("${person.name}, ${person.age}")

    // Inheritance
    val dog = Dog("Buddy")
    println("${dog.name} says ${dog.sound()}")

    // Interface
    val circle = Circle(5.0)
    circle.draw()
    println("Area: ${circle.area()}")
    println("Info: ${circle.info()}")

    // Singleton
    Database.connect()

    // Companion object
    val user = User.create("Charlie")
    println("User: ${user.name}")

    // Enum
    val color = Color.RED
    println("Color: ${color.name}, Hex: ${color.hex}")
}

// --- Class ---
class Person(val name: String, var age: Int)

// --- Inheritance ---
open class Animal(val name: String) {
    open fun sound() = "Some sound"
}

class Dog(name: String) : Animal(name) {
    override fun sound() = "Bark"
}

// --- Interface ---
interface Drawable {
    fun draw()
    fun info() = "Drawable object"
}

// --- Abstract class + Interface ---
abstract class Shape {
    abstract fun area(): Double
}

class Circle(val radius: Double) : Shape(), Drawable {
    override fun area() = Math.PI * radius * radius
    override fun draw() = println("Drawing circle with radius $radius")
}

// --- Singleton ---
object Database {
    fun connect() = println("Database connected!")
}

// --- Companion object ---
class User(val name: String) {
    companion object {
        fun create(name: String) = User(name)
    }
}

// --- Enum ---
enum class Color(val hex: String) {
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF")
}
```

### Output
```
Alice, 30
Buddy says Bark
Drawing circle with radius 5.0
Area: 78.53981633974483
Info: Drawable object
Database connected!
User: Charlie
Color: RED, Hex: #FF0000
```

---

## ❓ Interview Questions

1. **Why are Kotlin classes `final` by default?**
   - To prevent unintended inheritance. Use `open` keyword to explicitly allow subclassing, promoting better design.

2. **What is the difference between primary and secondary constructors?**
   - Primary constructor is declared in the class header. Secondary constructors are declared inside the class body with `constructor` keyword and must delegate to the primary.

3. **What is a companion object and how does it differ from Java's `static`?**
   - A companion object is a singleton object inside a class. It can implement interfaces and be passed as a parameter. Java's `static` is just a method/field on the class.

4. **What is the difference between `abstract` and `open`?**
   - `abstract` forces subclasses to override (cannot be instantiated). `open` merely allows overriding but provides a default implementation.

5. **Can a Kotlin interface have default method implementations?**
   - Yes. Methods with bodies in interfaces provide default implementations. Classes can override them optionally.

6. **What is the difference between `open`, `abstract`, and `final` in Kotlin?**
   - `final` (default) — the class or member **cannot** be overridden or subclassed. All Kotlin classes/members are final by default (unlike Java). `open` — explicitly allows subclassing (`open class`) or overriding (`open fun`). The member has a default implementation but can be overridden. `abstract` — the member has **no** implementation and **must** be overridden. Only `abstract` classes can have `abstract` members. Example: `abstract class Shape { abstract fun area(): Double; open fun describe() = "Shape" } class Circle : Shape() { override fun area() = 3.14 * r * r; override fun describe() = "Circle" }`. You can override an `open` member but not a `final` one. You can prevent further overriding with `final override`. In Java, everything is open by default — Kotlin's final-by-default is a deliberate design choice to prevent accidental inheritance.

7. **How does Kotlin handle multiple inheritance with interfaces and default implementations?**
   - Kotlin (like Java 8+) allows implementing multiple interfaces with default method implementations. If two interfaces have the same method signature with default implementations, the implementing class **must** override it to resolve the ambiguity: `interface A { fun foo() = "A" }; interface B { fun foo() = "B" }; class C : A, B { override fun foo() = super<A>.foo() }`. Use `super<A>.foo()` to call a specific interface's implementation. Classes can only extend **one** class (single inheritance for state) but implement **multiple** interfaces. Interfaces cannot have constructors or state (backing fields) — only abstract properties and default method bodies. If interfaces have conflicting property names, the class must override the property. Diamond problem is resolved by requiring explicit override in the implementing class.

8. **What are `init` blocks and how do they relate to constructors?**
   - `init` blocks execute during class initialization, after the primary constructor's property declarations but before secondary constructor bodies. Multiple `init` blocks execute in order of appearance. Example: `class User(name: String) { val name = name.uppercase(); init { println("Init 1: $name") }; init { println("Init 2") } }`. Execution order: (1) Primary constructor parameters are available. (2) Property initializers and `init` blocks execute **top-to-bottom** in the order they appear. (3) Secondary constructor body executes (if called). Use `init` blocks for validation: `init { require(age >= 0) { "Age must be positive" } }`. Don't put heavy initialization in `init` — use `lazy` or factory functions. If using DI (Hilt), `init` blocks run before `@Inject` field injection completes — don't access injected fields in `init`.

9. **What are object expressions (anonymous objects) and when do you use them?**
   - Object expressions create anonymous objects that implement an interface or extend a class on the fly: `val listener = object : OnClickListener { override fun onClick(v: View) { } }`. Use cases: (1) **One-time implementations** — when you need a single instance of an interface and don't want to create a named class. (2) **Multiple interfaces** — `object : A, B { }` can implement multiple interfaces (unlike Java anonymous classes). (3) **Accessing enclosing variables** — object expressions can capture and modify variables from the enclosing scope (closures). (4) **Method override only** — unlike lambdas (which implement functional interfaces), object expressions can override multiple methods. Difference from `object` declarations: object expressions are created each time (not singletons). In Kotlin, prefer lambdas for single-method interfaces (SAM conversion) and object expressions for multi-method interfaces. Anonymous objects are `private` when used as local variables.

10. **What is the difference between `==` and `===` in Kotlin?**
    - `==` checks **structural equality** — calls `equals()` method. For data classes, `equals()` compares all properties. For regular classes, `equals()` defaults to referential equality unless overridden. `==` is null-safe: `a == b` is compiled to `if (a === null) b === null else a.equals(b)`. `===` checks **referential equality** — same object in memory. `!==` is the negation. Example: `val a = listOf(1); val b = listOf(1); a == b` → true (structural), `a === b` → false (different instances). For data classes: `data class User(val name: String); val u1 = User("A"); val u2 = User("A"); u1 == u2` → true. For strings: `"a" == "a"` → true (structural), `"a" === "a"` → usually true (string pooling). Always use `==` for value comparison. Use `===` only when you specifically need to check if two references point to the same object.

11. **How do enums work in Kotlin and what features do they have?**
    - Kotlin enums are more powerful than Java enums. Features: (1) **Properties and methods** — `enum class Color(val rgb: Int) { RED(0xFF0000), GREEN(0x00FF00); fun isPrimary() = this == RED || this == GREEN }`. (2) **Implement interfaces** — `enum class State : Runnable { ACTIVE { override fun run() { } }; override fun run() { } }`. (3) **Each constant can have its own body** — override methods per constant. (4) **`values()` and `valueOf()`** — get all constants or by name. (5) **`enumValueOf<T>()` and `enumValues<T>()`** — reified generic versions. (6) **Range and iteration** — `for (c in Color.RED..Color.BLUE)`. (7) **Sealed class alternative** — enums for fixed values, sealed classes for fixed types with varying data. (8) **`name` and `ordinal`** properties. Enums are implicitly `final` and cannot be subclassed. Each constant is a singleton instance. Use `when` with enums — the compiler warns if not exhaustive.

12. **What are inline classes (value classes) and how do they differ from data classes?**
    - Value classes (`@JvmInline value class`) wrap a single value with **zero runtime overhead** — the wrapper is erased at compile time. Use for type safety: `@JvmInline value class UserId(val value: Long)` — `UserId` and `OrderId` are distinct types even though both wrap `Long`. Data classes: (1) Can have multiple properties. (2) Generate `equals`, `hashCode`, `toString`, `copy`, `componentN`. (3) Have runtime overhead — actual objects in memory. (4) Used for data transfer. Value classes: (1) Only one property. (2) No generated methods (must implement manually). (3) Zero overhead at runtime (erased to the underlying type). (4) Used for type-safe IDs, units (Distance, Weight), and avoiding primitive obsession. Boxing occurs when value classes are used in generics or as nullable types. Use value classes for type safety with primitives, data classes for structured data.

---

## 🔗 Related Topics
- [Data Classes & Sealed Classes](DataAndSealedClasses.md)
- [Collections](Collections.md)
