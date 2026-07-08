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

---

## 🔗 Related Topics
- [Data Classes & Sealed Classes](DataAndSealedClasses.md)
- [Collections](Collections.md)
