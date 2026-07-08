# OOP Concepts

## Q1: What is the difference between abstract class and interface?

| Abstract Class | Interface |
|---------------|-----------|
| `abstract class` keyword | `interface` keyword |
| Can have both abstract and concrete methods | All methods abstract (pre-Java 8) |
| Can have constructors | No constructors |
| Can have instance fields (any access) | Fields are `public static final` by default |
| Single inheritance (one parent) | Multiple implementation (many interfaces) |
| `extends` | `implements` |
| Used for "is-a" with shared code | Used for "can-do" contract |

```java
// Abstract class — shared implementation
abstract class Vehicle {
    protected String name;

    public Vehicle(String name) {  // Constructor
        this.name = name;
    }

    abstract void start();  // Must be implemented by subclass

    public void stop() {   // Concrete method — shared
        System.out.println(name + " stopped");
    }
}

// Interface — contract
interface Drivable {
    void drive();  // Abstract by default
}

interface Maintainable {
    void service();
}

// Class extends one abstract class, implements multiple interfaces
class Car extends Vehicle implements Drivable, Maintainable {
    public Car(String name) { super(name); }

    @Override
    void start() { System.out.println("Engine started"); }

    @Override
    public void drive() { System.out.println("Driving"); }

    @Override
    public void service() { System.out.println("Servicing"); }
}
```

---

## Q2: What changed for interfaces in Java 8 and 9?

```java
public interface Calculator {
    // Abstract method — must implement
    int calculate(int a, int b);

    // Java 8: default method — has implementation, can override
    default int doubleIt(int a) {
        return a * 2;
    }

    // Java 8: static method — called on interface
    static Calculator add() {
        return (a, b) -> a + b;
    }

    // Java 9: private method — shared by default methods
    private int validate(int value) {
        return value < 0 ? 0 : value;
    }

    // Java 9: private static method
    private static void log(String msg) {
        System.out.println(msg);
    }
}
```

### Diamond Problem Resolution
```java
interface A {
    default void hello() { System.out.println("A"); }
}
interface B {
    default void hello() { System.out.println("B"); }
}
// Must override to resolve ambiguity
class C implements A, B {
    @Override
    public void hello() {
        A.super.hello();  // Choose which to call
    }
}
```

---

## Q3: What is IS-A vs HAS-A relationship?

```java
// IS-A (Inheritance) — "Dog IS-A Animal"
class Animal { }
class Dog extends Animal { }  // Dog IS-A Animal

// HAS-A (Composition) — "Car HAS-A Engine"
class Engine {
    void start() { }
}
class Car {
    private Engine engine;  // Composition — Car HAS-A Engine

    public Car() {
        this.engine = new Engine();
    }
}
```

| IS-A (Inheritance) | HAS-A (Composition) |
|--------------------|--------------------|
| `extends` | Field reference |
| Tight coupling | Loose coupling |
| Parent changes affect child | Independent |
| Static (compile-time) | Dynamic (runtime) |
| "is-a" relationship | "has-a" relationship |

> **Best Practice:** Favor composition over inheritance. Inheritance is for true "is-a" relationships.

---

## Q4: What is runtime polymorphism (dynamic dispatch)?

```java
abstract class Shape {
    abstract double area();
}

class Circle extends Shape {
    private double radius;
    Circle(double r) { this.radius = r; }

    @Override
    double area() { return Math.PI * radius * radius; }
}

class Rectangle extends Shape {
    private double width, height;
    Rectangle(double w, double h) { this.width = w; this.height = h; }

    @Override
    double area() { return width * height; }
}

// Runtime polymorphism — JVM decides which area() to call
Shape s1 = new Circle(5);
Shape s2 = new Rectangle(4, 6);

System.out.println(s1.area());  // 78.54 — Circle.area() at runtime
System.out.println(s2.area());  // 24.0  — Rectangle.area() at runtime

// Polymorphic collection
List<Shape> shapes = List.of(s1, s2, new Circle(3));
double totalArea = shapes.stream()
    .mapToDouble(Shape::area)  // Each calls its own area()
    .sum();
```

---

## Q5: What is the `instanceof` operator and pattern matching?

```java
// Traditional instanceof
Object obj = "Hello";
if (obj instanceof String) {
    String s = (String) obj;  // Must cast
    System.out.println(s.length());
}

// Java 16+ — Pattern matching for instanceof
if (obj instanceof String s) {  // No cast needed
    System.out.println(s.length());
}

// With conditions
if (obj instanceof String s && s.length() > 3) {
    System.out.println("Long string: " + s);
}

// Java 21+ — Pattern matching with switch
String result = switch (obj) {
    case Integer i when i > 0 -> "Positive integer: " + i;
    case String s when s.length() > 5 -> "Long string: " + s;
    case null -> "Null";
    default -> "Unknown: " + obj;
};
```

---

## Q6: What is the Object class and its methods?

```java
public class Person {
    private String name;
    private int age;

    // 1. equals() — define logical equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Same reference
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    // 2. hashCode() — must be consistent with equals
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    // 3. toString() — human-readable representation
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    // 4. clone() — shallow copy (implement Cloneable)
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // Shallow copy
    }
}
```

### equals() and hashCode() Contract
- If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true
- If `a.equals(b)` is false, `hashCode()` may or may not be equal
- Consistent: multiple calls return same value (if fields unchanged)
- Reflexive: `a.equals(a)` is true
- Symmetric: if `a.equals(b)`, then `b.equals(a)`
- Transitive: if `a.equals(b)` and `b.equals(c)`, then `a.equals(c)`

---

## Q7: What are sealed classes (Java 17+)?

```java
// Sealed class — restricts which classes can extend it
public sealed class Shape
    permits Circle, Square, Triangle {}

final class Circle extends Shape { }      // Must be final, sealed, or non-sealed
final class Square extends Shape { }
non-sealed class Triangle extends Shape { }  // Open for extension

// Sealed interface
public sealed interface Result
    permits Success, Error {}

record Success(String value) implements Result {}
record Error(String message) implements Result {}

// Exhaustive switch — no default needed
String handle(Result result) {
    return switch (result) {
        case Success s -> "OK: " + s.value();
        case Error e -> "Error: " + e.message();
        // No default — compiler knows all permitted subtypes
    };
}
```

---

## 🔗 Related Topics
- [OOP Basics](../beginner/OOPBasics.md)
- [Design Patterns](../advanced/DesignPatterns.md)
- [OOP Scenarios](../scenario_based/OOPScenarios.md)
