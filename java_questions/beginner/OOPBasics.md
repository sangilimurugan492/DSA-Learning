# OOP Basics

## Q1: What are the four pillars of OOP?

| Pillar | Description | Example |
|--------|-------------|---------|
| **Encapsulation** | Hide internal state, expose via methods | `private` fields + `getters/setters` |
| **Inheritance** | Child class inherits from parent | `class Dog extends Animal` |
| **Polymorphism** | One interface, many implementations | Overriding, overloading |
| **Abstraction** | Hide complexity, show essentials | `abstract class`, `interface` |

```java
// Encapsulation
public class BankAccount {
    private double balance;  // Hidden

    public double getBalance() { return balance; }  // Controlled access
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }
}

// Inheritance
class Animal {
    void eat() { System.out.println("Eating"); }
}
class Dog extends Animal {
    void bark() { System.out.println("Barking"); }
}

// Polymorphism
Animal a = new Dog();  // Upcasting
a.eat();               // Calls Animal.eat() (or overridden)

// Abstraction
interface Vehicle {
    void start();  // No implementation — abstract
}
```

---

## Q2: What is a class vs an object?

```java
// Class — blueprint/template
public class Car {
    String model;
    int year;

    // Constructor
    public Car(String model, int year) {
        this.model = model;  // 'this' refers to current object
        this.year = year;
    }

    void drive() {
        System.out.println(model + " is driving");
    }
}

// Object — instance of a class
Car myCar = new Car("Tesla", 2024);  // Creates object in heap
Car yourCar = new Car("BMW", 2023);  // Different object

myCar.drive();   // "Tesla is driving"
yourCar.drive();  // "BMW is driving"
```

| Class | Object |
|-------|--------|
| Blueprint/template | Instance of a class |
| Logical entity | Physical entity (in memory) |
| Created once | Can create many |
| No memory allocated | Memory allocated on heap |
| Declared with `class` keyword | Created with `new` keyword |

---

## Q3: What are constructors and their types?

```java
public class Person {
    private String name;
    private int age;

    // 1. No-arg (default) constructor
    public Person() {
        this("Unknown", 0);  // Calls another constructor
    }

    // 2. Parameterized constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 3. Copy constructor (convention, not built-in)
    public Person(Person other) {
        this(other.name, other.age);
    }
}

// Usage
Person p1 = new Person("Alice", 30);
Person p2 = new Person();          // No-arg
Person p3 = new Person(p1);        // Copy
```

### Constructor Rules
- Name must match class name
- No return type (not even `void`)
- Can be overloaded
- If no constructor defined, compiler adds default no-arg constructor
- If any constructor defined, no default is added
- First statement can be `this()` or `super()` (not both)

---

## Q4: What is `this` and `super`?

```java
public class Animal {
    String name;

    public Animal(String name) {
        this.name = name;  // 'this' — current object reference
    }

    public void eat() {
        System.out.println(name + " is eating");
    }
}

public class Dog extends Animal {
    String breed;

    public Dog(String name, String breed) {
        super(name);           // Call parent constructor — must be first
        this.breed = breed;    // 'this' — current object
    }

    public void display() {
        super.eat();           // Call parent method
        System.out.println(this.name + " is a " + this.breed);
    }
}
```

| `this` | `super` |
|--------|---------|
| Reference to current object | Reference to parent object |
| `this.field` — current class field | `super.field` — parent class field |
| `this()` — call another constructor | `super()` — call parent constructor |
| Must be first statement if `this()` | Must be first statement if `super()` |
| Can't be used in static context | Can't be used in static context |

---

## Q5: What is the difference between method overloading and overriding?

```java
// Overloading (compile-time polymorphism) — same name, different params
class Calculator {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
    int add(int a, int b, int c) { return a + b + c; }
}

// Overriding (runtime polymorphism) — same signature in child class
class Animal {
    void sound() { System.out.println("Some sound"); }
}
class Dog extends Animal {
    @Override
    void sound() { System.out.println("Bark"); }  // Overrides parent
}
```

| Overloading | Overriding |
|-------------|-----------|
| Same class | Parent-child (inheritance) |
| Same name, different parameters | Same name, same parameters |
| Compile-time (static binding) | Runtime (dynamic binding) |
| Return type can differ | Return type must be same or covariant |
| Access modifier can differ | Can't reduce visibility (public → private ❌) |
| Can overload static methods | Can't override static methods (can hide) |

### Overriding Rules
```java
class Parent {
    public void method() { }           // public
    protected void method2() { }       // protected
}

class Child extends Parent {
    @Override
    public void method() { }           // ✅ Same or wider access
    // private void method2() { }      // ❌ Can't reduce visibility
    public void method2() { }          // ✅ Wider access (protected → public)
}
```

---

## Q6: What are access modifiers in Java?

```java
public class Example {
    public int publicVar;      // Everywhere
    protected int protectedVar; // Same package + subclasses
    int defaultVar;             // Same package only (package-private)
    private int privateVar;     // Same class only
}
```

| Modifier | Class | Package | Subclass | World |
|----------|-------|---------|----------|-------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| *default* (package-private) | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

```java
// Top-level class can only be public or package-private
public class PublicClass { }    // Accessible everywhere
class PackageClass { }          // Accessible within package only

// Nested class can be private
public class Outer {
    private class Inner { }      // Only accessible within Outer
}
```

---

## Q7: What is `static` in Java?

```java
public class Counter {
    // Static variable — shared across all instances
    private static int count = 0;

    // Instance variable — per object
    private int id;

    public Counter() {
        count++;           // Increment shared counter
        this.id = count;   // Assign unique ID
    }

    // Static method — called on class, not object
    public static int getCount() {
        return count;
        // Can't access 'id' or 'this' — no instance context
    }

    // Static initializer — runs once when class is loaded
    static {
        System.out.println("Counter class loaded");
    }
}

// Usage
new Counter();
new Counter();
System.out.println(Counter.getCount());  // 2 — called on class, not object
```

| Static | Instance (non-static) |
|--------|----------------------|
| Belongs to class | Belongs to object |
| Shared across all instances | Per-object copy |
| Accessed via `ClassName.member` | Accessed via `object.member` |
| Can't access instance members directly | Can access both |
| Stored in method area | Stored in heap (with object) |

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [OOP Concepts](../intermediate/OOPConcepts.md)
- [OOP Scenarios](../scenario_based/OOPScenarios.md)
