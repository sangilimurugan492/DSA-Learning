# Generics

## Q1: What are generics and why use them?

```java
// Without generics — type unsafe
List list = new ArrayList();
list.add("Hello");
String s = (String) list.get(0);  // Must cast
list.add(42);  // No error at compile time
// String s2 = (String) list.get(1);  // ClassCastException at runtime!

// With generics — type safe
List<String> genericList = new ArrayList<>();
genericList.add("Hello");
String s = genericList.get(0);  // No cast needed
// genericList.add(42);  // ❌ Compile error — type safety at compile time
```

### Benefits
1. **Type safety** — errors caught at compile time, not runtime
2. **No casting** — compiler knows the type
3. **Code reuse** — one implementation works for all types
4. **Self-documenting** — `List<User>` is clearer than `List`

---

## Q2: What are generic classes, methods, and interfaces?

```java
// Generic class
public class Box<T> {
    private T item;

    public void set(T item) { this.item = item; }
    public T get() { return item; }
}

Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String s = stringBox.get();

// Generic method — type parameter before return type
public <T> T getFirst(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
}

String first = getFirst(List.of("A", "B"));  // T inferred as String
Integer firstNum = getFirst(List.of(1, 2));  // T inferred as Integer

// Generic interface
public interface Repository<T, ID> {
    T findById(ID id);
    void save(T entity);
    List<T> findAll();
}

// Multiple type parameters
public class Pair<K, V> {
    private K key;
    private V value;
    public Pair(K key, V value) { this.key = key; this.value = value; }
    public K getKey() { return key; }
    public V getValue() { return value; }
}

Pair<String, Integer> pair = new Pair<>("age", 30);
```

---

## Q3: What are bounded type parameters?

```java
// Upper bound — T must be Number or subclass
public <T extends Number> double sum(List<T> numbers) {
    return numbers.stream().mapToDouble(Number::doubleValue).sum();
}
sum(List.of(1, 2, 3));       // Integer extends Number ✅
sum(List.of(1.0, 2.0));      // Double extends Number ✅
// sum(List.of("a", "b"));   // ❌ String doesn't extend Number

// Multiple bounds — T must extend both
public <T extends Number & Comparable<T>> T findMax(List<T> list) {
    return list.stream().max(Comparable::compareTo).orElse(null);
}

// Upper bound with class + interface
interface Serializable { }
public <T extends Number & Serializable> void process(T item) { }
```

| Bound | Syntax | Meaning |
|-------|--------|---------|
| Upper bound | `<T extends Number>` | T is Number or subclass |
| Multiple bounds | `<T extends A & B>` | T must extend A and implement B |
| Lower bound (wildcard) | `<? super Integer>` | Integer or superclass |

---

## Q4: What are wildcards (`?`, `extends`, `super`)?

```java
// Unbounded wildcard — any type
void printList(List<?> list) {
    list.forEach(System.out::println);  // Can read
    // list.add("item");  // ❌ Can't add — type unknown
}
printList(List.of("A", "B"));
printList(List.of(1, 2, 3));

// Upper bounded wildcard (PE — Producer Extends)
double sum(List<? extends Number> numbers) {
    // Can read as Number
    return numbers.stream().mapToDouble(Number::doubleValue).sum();
    // numbers.add(1);  // ❌ Can't write — don't know exact type
}
sum(List.of(1, 2, 3));      // List<Integer>
sum(List.of(1.0, 2.0));     // List<Double>

// Lower bounded wildcard (CS — Consumer Super)
void addNumbers(List<? super Integer> list) {
    list.add(1);  // ✅ Can write Integer (or subtype)
    list.add(2);
    // Number n = list.get(0);  // ❌ Can only read as Object
}
addNumbers(new ArrayList<Number>());    // ✅ Number is super of Integer
addNumbers(new ArrayList<Integer>());   // ✅ Integer is super of Integer
// addNumbers(new ArrayList<Double>()); // ❌ Double is not super of Integer
```

### PECS Rule
- **P**roducer **E**xtends — if you **read** from collection, use `<? extends T>`
- **C**onsumer **S**uper — if you **write** to collection, use `<? super T>`

```java
// Example: copy from producer to consumer
public <T> void copy(List<? extends T> src, List<? super T> dest) {
    for (T item : src) {  // Read from producer (extends)
        dest.add(item);   // Write to consumer (super)
    }
}
```

---

## Q5: What is type erasure?

```java
// At compile time:
List<String> strings = new ArrayList<>();
List<Integer> numbers = new ArrayList<>();

// After type erasure (bytecode):
List strings = new ArrayList();  // No type parameter
List numbers = new ArrayList();  // Same raw type

// Type erasure rules:
// 1. Replace type parameters with bound (or Object if unbounded)
// 2. Insert casts where needed
// 3. Generate bridge methods for polymorphism
```

```java
// Source code
public class Box<T> {
    private T item;
    public T get() { return item; }
}

// After erasure (what JVM sees)
public class Box {
    private Object item;
    public Object get() { return item; }
}

// Caller — compiler inserts cast
Box<String> box = new Box<>();
String s = box.get();
// Becomes: String s = (String) box.get();
```

### Consequences of Type Erasure
```java
// ❌ Can't overload with different generics
// void process(List<String> list) { }
// void process(List<Integer> list) { }  // Same erasure — compile error

// ❌ Can't create generic array
// List<String>[] array = new ArrayList<String>[10];  // Compile error

// ❌ Can't use primitives as type parameters
// List<int> nums;  // Use List<Integer> instead

// ❌ Can't use instanceof with generics
List<String> list = new ArrayList<>();
// if (list instanceof List<String>) { }  // Compile error
if (list instanceof List<?>) { }  // ✅ Raw or wildcard only

// ❌ Can't create instance of type parameter
public <T> void method() {
    // T item = new T();  // Compile error
}
```

---

## Q6: What are generic methods with type inference?

```java
// Type inference — compiler figures out T
public <T> T identity(T item) { return item; }

String s = identity("hello");        // T = String (explicit)
Integer i = identity(42);            // T = Integer (inferred)
var d = identity(3.14);              // T = Double (var + inference)

// Collections.emptyList() — generic factory method
List<String> empty = Collections.emptyList();  // T inferred from target

// Java 8+ improved type inference
void process(Map<String, List<Integer>> data) { }
// Java 7+ diamond operator
Map<String, List<Integer>> map = new HashMap<>();  // Inferred from target

// Generic constructor
public class Container<T> {
    public <U> Container(U source, Function<U, T> mapper) { }
}
// Type inferred from arguments
Container<String> c = new Container<>(42, Object::toString);
```

---

## 🔗 Related Topics
- [Collections](Collections.md)
- [OOP Concepts](OOPConcepts.md)
- [Lambda and Streams](LambdaAndStreams.md)
