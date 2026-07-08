# Design Patterns

## Q1: What is the Singleton pattern and how to implement it?

```java
// 1. Eager initialization — simple, thread-safe
public class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();
    private EagerSingleton() {}
    public static EagerSingleton getInstance() { return INSTANCE; }
}

// 2. Lazy initialization — double-checked locking
public class LazySingleton {
    private static volatile LazySingleton instance;  // volatile required
    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {                    // Check 1 (no lock)
            synchronized (LazySingleton.class) {
                if (instance == null) {            // Check 2 (with lock)
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }
}

// 3. Bill Pugh — inner static helper (recommended)
public class BillPughSingleton {
    private BillPughSingleton() {}

    private static class Holder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return Holder.INSTANCE;  // Loaded on first call — lazy + thread-safe
    }
}

// 4. Enum singleton (best — handles serialization, reflection)
public enum EnumSingleton {
    INSTANCE;
    public void doWork() {}
}
```

| Implementation | Thread-safe | Lazy | Serialization-safe | Reflection-safe |
|----------------|-------------|------|--------------------|-----------------|
| Eager | ✅ | ❌ | ❌ | ❌ |
| Double-checked | ✅ | ✅ | ❌ | ❌ |
| Bill Pugh | ✅ | ✅ | ❌ | ❌ |
| Enum | ✅ | ✅ | ✅ | ✅ |

---

## Q2: What is the Builder pattern?

```java
// Builder — construct complex objects step by step
public class Pizza {
    private final String size;
    private final boolean cheese, pepperoni, mushrooms, onions;
    private final String sauce;

    private Pizza(Builder builder) {
        this.size = builder.size;
        this.cheese = builder.cheese;
        this.pepperoni = builder.pepperoni;
        this.mushrooms = builder.mushrooms;
        this.onions = builder.onions;
        this.sauce = builder.sauce;
    }

    public static class Builder {
        private final String size;  // Required
        private boolean cheese = false;      // Optional
        private boolean pepperoni = false;
        private boolean mushrooms = false;
        private boolean onions = false;
        private String sauce = "tomato";

        public Builder(String size) { this.size = size; }

        public Builder cheese(boolean val) { this.cheese = val; return this; }
        public Builder pepperoni(boolean val) { this.pepperoni = val; return this; }
        public Builder mushrooms(boolean val) { this.mushrooms = val; return this; }
        public Builder onions(boolean val) { this.onions = val; return this; }
        public Builder sauce(String val) { this.sauce = val; return this; }

        public Pizza build() { return new Pizza(this); }
    }
}

// Usage — readable, immutable, flexible
Pizza pizza = new Pizza.Builder("Large")
    .cheese(true)
    .mushrooms(true)
    .sauce("white")
    .build();
```

### Benefits
- Immutable object (thread-safe)
- Readable construction (named parameters)
- Handles many optional parameters (avoids telescoping constructors)
- Validates in `build()` method

---

## Q3: What is the Factory Method pattern?

```java
// Factory Method — create objects without specifying exact class
interface Payment {
    void pay(double amount);
}

class CreditCardPayment implements Payment {
    @Override
    public void pay(double amount) { System.out.println("Credit card: $" + amount); }
}

class PayPalPayment implements Payment {
    @Override
    public void pay(double amount) { System.out.println("PayPal: $" + amount); }
}

class CryptoPayment implements Payment {
    @Override
    public void pay(double amount) { System.out.println("Crypto: $" + amount); }
}

// Factory
class PaymentFactory {
    public static Payment create(PaymentType type) {
        return switch (type) {
            case CREDIT_CARD -> new CreditCardPayment();
            case PAYPAL -> new PayPalPayment();
            case CRYPTO -> new CryptoPayment();
        };
    }
}

enum PaymentType { CREDIT_CARD, PAYPAL, CRYPTO }

// Usage — client doesn't know about concrete classes
Payment payment = PaymentFactory.create(PaymentType.PAYPAL);
payment.pay(99.99);
```

---

## Q4: What is the Observer pattern?

```java
// Observer — publish-subscribe, one-to-many notification
interface Observer<T> {
    void update(T data);
}

class Subject<T> {
    private final List<Observer<T>> observers = new ArrayList<>();

    public void subscribe(Observer<T> observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer<T> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(T data) {
        observers.forEach(o -> o.update(data));
    }
}

// Usage
class NewsAgency extends Subject<String> {
    public void publishNews(String news) {
        System.out.println("Published: " + news);
        notifyObservers(news);  // Notify all subscribers
    }
}

class NewsChannel implements Observer<String> {
    private final String name;
    NewsChannel(String name) { this.name = name; }

    @Override
    public void update(String news) {
        System.out.println(name + " received: " + news);
    }
}

NewsAgency agency = new NewsAgency();
agency.subscribe(new NewsChannel("CNN"));
agency.subscribe(new NewsChannel("BBC"));
agency.publishNews("Breaking: Java 21 released!");
// CNN received: Breaking: Java 21 released!
// BBC received: Breaking: Java 21 released!
```

---

## Q5: What is the Strategy pattern?

```java
// Strategy — interchangeable algorithms behind common interface
interface SortingStrategy {
    <T extends Comparable<T>> void sort(List<T> list);
}

class QuickSort implements SortingStrategy {
    @Override
    public <T extends Comparable<T>> void sort(List<T> list) {
        list.sort(Comparator.naturalOrder());  // Simplified
        System.out.println("QuickSort applied");
    }
}

class MergeSort implements SortingStrategy {
    @Override
    public <T extends Comparable<T>> void sort(List<T> list) {
        list.sort(Comparator.naturalOrder());  // Simplified
        System.out.println("MergeSort applied");
    }
}

// Context — uses strategy, can swap at runtime
class Sorter<T extends Comparable<T>> {
    private SortingStrategy strategy;

    public Sorter(SortingStrategy strategy) { this.strategy = strategy; }

    public void setStrategy(SortingStrategy strategy) { this.strategy = strategy; }

    public void sort(List<T> list) { strategy.sort(list); }
}

// Usage — swap strategies at runtime
Sorter<Integer> sorter = new Sorter<>(new QuickSort());
sorter.sort(new ArrayList<>(List.of(3, 1, 2)));  // QuickSort applied

sorter.setStrategy(new MergeSort());
sorter.sort(new ArrayList<>(List.of(3, 1, 2)));  // MergeSort applied
```

---

## Q6: What is the Decorator pattern?

```java
// Decorator — add behavior without modifying original class
interface Coffee {
    double cost();
    String description();
}

class SimpleCoffee implements Coffee {
    @Override public double cost() { return 2.0; }
    @Override public String description() { return "Simple coffee"; }
}

// Abstract decorator
abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decorated;

    public CoffeeDecorator(Coffee coffee) { this.decorated = coffee; }
}

// Concrete decorators
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    @Override public double cost() { return decorated.cost() + 0.5; }
    @Override public String description() { return decorated.description() + ", Milk"; }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) { super(coffee); }
    @Override public double cost() { return decorated.cost() + 0.2; }
    @Override public String description() { return decorated.description() + ", Sugar"; }
}

// Usage — stack decorators
Coffee coffee = new SugarDecorator(
    new MilkDecorator(
        new SimpleCoffee()
    )
);
System.out.println(coffee.description());  // Simple coffee, Milk, Sugar
System.out.println(coffee.cost());          // 2.7
```

---

## Q7: What is the Template Method pattern?

```java
// Template Method — define algorithm skeleton, defer steps to subclasses
abstract class DataProcessor {
    // Template method — final, defines algorithm skeleton
    public final void process() {
        readData();
        validateData();
        transformData();
        saveData();
    }

    // Common steps
    private void validateData() { System.out.println("Validating..."); }
    private void saveData() { System.out.println("Saving to DB..."); }

    // Abstract steps — implemented by subclasses
    protected abstract void readData();
    protected abstract void transformData();
}

class CSVProcessor extends DataProcessor {
    @Override protected void readData() { System.out.println("Reading CSV file"); }
    @Override protected void transformData() { System.out.println("Parsing CSV rows"); }
}

class JSONProcessor extends DataProcessor {
    @Override protected void readData() { System.out.println("Reading JSON file"); }
    @Override protected void transformData() { System.out.println("Parsing JSON objects"); }
}

// Usage
DataProcessor processor = new CSVProcessor();
processor.process();
// Reading CSV file → Validating... → Parsing CSV rows → Saving to DB...
```

---

## 🔗 Related Topics
- [OOP Concepts](../intermediate/OOPConcepts.md)
- [OOP Scenarios](../scenario_based/OOPScenarios.md)
- [Design Patterns (separate folder)](../../design_patterns/README.md)
