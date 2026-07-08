# Lambda and Streams

## Q1: What is a functional interface?

```java
// Functional interface — exactly one abstract method
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);  // Single abstract method

    // Default and static methods don't count
    default int doubleIt(int a) { return a * 2; }
    static Calculator add() { return (a, b) -> a + b; }
}

// Lambda can be used wherever functional interface is expected
Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;
Calculator subtract = (a, b) -> a - b;

add.calculate(5, 3);       // 8
multiply.calculate(5, 3);  // 15
```

### Built-in Functional Interfaces (java.util.function)
```java
// Function<T, R> — takes T, returns R
Function<String, Integer> length = String::length;
Function<String, String> upper = s -> s.toUpperCase();

// Predicate<T> — takes T, returns boolean
Predicate<String> isEmpty = String::isEmpty;
Predicate<Integer> isPositive = n -> n > 0;

// Consumer<T> — takes T, returns nothing
Consumer<String> printer = System.out::println;
Consumer<List<Integer>> sorter = List::sort;

// Supplier<T> — takes nothing, returns T
Supplier<String> factory = () -> new String("default");
Supplier<List<Integer>> listFactory = ArrayList::new;

// BiFunction<T, U, R> — takes two args
BiFunction<String, Integer, String> repeat = String::repeat;

// Primitive specializations
IntFunction<String> intToStr = String::valueOf;
ToIntFunction<String> strToInt = Integer::parseInt;
IntPredicate isEven = n -> n % 2 == 0;
IntConsumer printer = System.out::println;
IntSupplier random = () -> ThreadLocalRandom.current().nextInt();
```

---

## Q2: What is a lambda expression?

```java
// Anonymous function — concise way to implement functional interface

// Full syntax: (parameters) -> { body }
Comparator<String> byLength = (s1, s2) -> { return s1.length() - s2.length(); };

// Single expression — no braces, no return
Comparator<String> byLength2 = (s1, s2) -> s1.length() - s2.length();

// No parameters
Runnable task = () -> System.out.println("Running");

// Single parameter — no parentheses needed
Consumer<String> print = s -> System.out.println(s);

// Multiple parameters
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
```

### Method References (shorthand for lambdas)
```java
// Static method reference
Function<String, Integer> parser = Integer::parseInt;
// Equivalent: s -> Integer.parseInt(s)

// Instance method on object
String str = "hello";
Supplier<Integer> len = str::length;
// Equivalent: () -> str.length()

// Instance method on parameter (unbound)
Function<String, String> upper = String::toUpperCase;
// Equivalent: s -> s.toUpperCase()

// Constructor reference
Supplier<ArrayList<String>> factory = ArrayList::new;
// Equivalent: () -> new ArrayList<>()

Function<Integer, ArrayList<String>> sizedFactory = ArrayList::new;
// Equivalent: size -> new ArrayList<>(size)
```

---

## Q3: What is the Stream API?

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Pipeline: source → intermediate ops → terminal op
int sumOfEvenSquares = numbers.stream()
    .filter(n -> n % 2 == 0)      // Intermediate: 2, 4, 6, 8, 10
    .mapToInt(n -> n * n)         // Intermediate: 4, 16, 36, 64, 100
    .sum();                        // Terminal: 220

// Stream doesn't modify the source
System.out.println(numbers);  // [1, 2, 3, ..., 10] — unchanged
```

### Stream Characteristics
- **Lazy** — intermediate ops don't execute until terminal op
- **Single-use** — can't reuse a stream after terminal op
- **Non-interfering** — doesn't modify source
- **Internal iteration** — framework controls iteration, not you

```java
// Lazy evaluation — filter doesn't run until findFirst
Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n > 5;
    });
// Nothing printed yet — lazy!

stream.findFirst();  // Now filter runs — stops after first match
// Prints: "Filtering: 1" "Filtering: 2" ... "Filtering: 6"
```

---

## Q4: What are intermediate and terminal operations?

### Intermediate Operations (return Stream — lazy)
```java
// filter — keep matching elements
Stream<Integer> evens = numbers.stream().filter(n -> n % 2 == 0);

// map — transform each element
Stream<String> strings = numbers.stream().map(Object::toString);

// flatMap — flatten nested streams
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
List<Integer> flat = nested.stream()
    .flatMap(List::stream)  // [1, 2, 3, 4]
    .collect(Collectors.toList());

// distinct — remove duplicates
Stream<Integer> unique = List.of(1, 1, 2, 2, 3).stream().distinct();  // [1, 2, 3]

// sorted — sort elements
Stream<Integer> sorted = List.of(3, 1, 2).stream().sorted();  // [1, 2, 3]
Stream<Integer> revSorted = List.of(3, 1, 2).stream().sorted(Comparator.reverseOrder());

// limit / skip — pagination
List<Integer> page = numbers.stream()
    .skip(10)   // Skip first 10
    .limit(5)   // Take next 5
    .collect(Collectors.toList());

// peek — debug (side effect, mainly for debugging)
numbers.stream()
    .peek(n -> System.out.println("Before filter: " + n))
    .filter(n -> n > 5)
    .peek(n -> System.out.println("After filter: " + n))
    .count();
```

### Terminal Operations (produce result — trigger execution)
```java
// collect — gather into collection
List<Integer> list = numbers.stream().collect(Collectors.toList());
Set<Integer> set = numbers.stream().collect(Collectors.toSet());
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
Map<Integer, String> map = people.stream()
    .collect(Collectors.toMap(Person::getId, Person::getName));

// reduce — combine elements
int sum = numbers.stream().reduce(0, Integer::sum);
Optional<Integer> product = numbers.stream().reduce((a, b) -> a * b);

// count
long count = numbers.stream().filter(n -> n > 5).count();

// findFirst / findAny
Optional<Integer> first = numbers.stream().filter(n -> n > 5).findFirst();

// anyMatch / allMatch / noneMatch
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
boolean allEven = numbers.stream().allMatch(n -> n % 2 == 0);
boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);

// forEach — side effect
numbers.stream().forEach(System.out::println);

// min / max
Optional<Integer> min = numbers.stream().min(Comparator.naturalOrder());
Optional<Integer> max = numbers.stream().max(Comparator.naturalOrder());

// toArray
Integer[] arr = numbers.stream().toArray(Integer[]::new);
```

---

## Q5: How do you use Collectors?

```java
List<Person> people = List.of(
    new Person("Alice", 30, "NYC"),
    new Person("Bob", 25, "LA"),
    new Person("Charlie", 35, "NYC"),
    new Person("Diana", 28, "LA")
);

// Grouping
Map<String, List<Person>> byCity = people.stream()
    .collect(Collectors.groupingBy(Person::getCity));
// {NYC=[Alice, Charlie], LA=[Bob, Diana]}

// Grouping with downstream collector
Map<String, Long> countByCity = people.stream()
    .collect(Collectors.groupingBy(Person::getCity, Collectors.counting()));
// {NYC=2, LA=2}

Map<String, Double> avgAgeByCity = people.stream()
    .collect(Collectors.groupingBy(Person::getCity,
        Collectors.averagingInt(Person::getAge)));
// {NYC=32.5, LA=26.5}

// Partitioning (two groups)
Map<Boolean, List<Person>> byAge = people.stream()
    .collect(Collectors.partitioningBy(p -> p.getAge() >= 30));
// {true=[Alice, Charlie], false=[Bob, Diana]}

// Joining
String names = people.stream()
    .map(Person::getName)
    .collect(Collectors.joining(", ", "[", "]"));
// [Alice, Bob, Charlie, Diana]

// Summarizing
IntSummaryStatistics stats = people.stream()
    .collect(Collectors.summarizingInt(Person::getAge));
// stats.getCount()=4, stats.getAverage()=29.5, stats.getMax()=35

// Mapping + reducing
int totalAge = people.stream()
    .collect(Collectors.reducing(0, Person::getAge, Integer::sum));

// toMap (handle duplicates)
Map<String, Integer> nameToAge = people.stream()
    .collect(Collectors.toMap(Person::getName, Person::getAge,
        (existing, replacement) -> existing));  // Keep first on conflict
```

---

## Q6: What are parallel streams?

```java
// Sequential stream
long sum1 = numbers.stream().mapToLong(n -> n * n).sum();

// Parallel stream — uses ForkJoinPool.commonPool()
long sum2 = numbers.parallelStream().mapToLong(n -> n * n).sum();

// Or convert sequential to parallel
long sum3 = numbers.stream().parallel().mapToLong(n -> n * n).sum();
```

### When to use parallel streams
```java
// ✅ Good for:
// - Large datasets (>10,000 elements)
// - CPU-intensive operations
// - Stateless, non-interfering operations
// - Associative reduce operations

// ❌ Bad for:
// - Small datasets (overhead > benefit)
// - I/O operations (blocking)
// - Ordered operations (limit, skip)
// - Shared mutable state
// - Operations with side effects

// ⚠️ Avoid shared mutable state
List<Integer> results = new ArrayList<>();  // ❌ Not thread-safe
numbers.parallelStream().forEach(results::add);  // Race condition!

// ✅ Use collect instead
List<Integer> safe = numbers.parallelStream()
    .collect(Collectors.toList());  // Thread-safe collection
```

---

## Q7: What is `Optional` and how does it prevent NPE?

```java
// Optional — container that may or may not contain a value
Optional<String> opt = Optional.of("hello");       // Non-null value
Optional<String> empty = Optional.empty();           // Empty
Optional<String> nullable = Optional.ofNullable(getName());  // May be null

// Check if present
if (opt.isPresent()) {
    System.out.println(opt.get());
}

// ifPresent — consume if exists
opt.ifPresent(System.out::println);

// orElse — default value
String value = opt.orElse("default");
String computed = opt.orElseGet(() -> expensiveDefault());  // Lazy default

// orElseThrow — exception if empty
String required = opt.orElseThrow(() -> new RuntimeException("No value"));

// map — transform if present
Optional<Integer> length = opt.map(String::length);

// filter — keep if matches
Optional<String> filtered = opt.filter(s -> s.length() > 3);

// flatMap — chain optionals
Optional<String> upper = opt.flatMap(s -> Optional.of(s.toUpperCase()));

// Chaining — no NPE risk
String result = Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("Unknown city");
// If user is null → "Unknown city" (no NPE)
```

---

## 🔗 Related Topics
- [Collections](Collections.md)
- [Generics](Generics.md)
- [Concurrency](Concurrency.md)
