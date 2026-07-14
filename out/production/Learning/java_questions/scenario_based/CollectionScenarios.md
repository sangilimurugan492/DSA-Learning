# Collection Scenarios

## Scenario 1: Choosing the Right Collection

### Problem
You need to store 1 million user records and frequently look up by email. Which collection should you use?

```java
// ❌ Bad — List lookup is O(n)
List<User> users = new ArrayList<>();
User found = null;
for (User u : users) {
    if (u.getEmail().equals(targetEmail)) {
        found = u;
        break;  // Still O(n) worst case
    }
}

// ❌ Bad — TreeSet requires Comparable, O(log n) lookup
Set<User> userSet = new TreeSet<>(Comparator.comparing(User::getEmail));
// Can't look up by email efficiently — need to iterate
```

### Solution: HashMap for O(1) lookup

```java
// ✅ Good — HashMap with email as key
Map<String, User> usersByEmail = new HashMap<>();
usersByEmail.put(user.getEmail(), user);

User found = usersByEmail.get(targetEmail);  // O(1) average

// ✅ If insertion order matters — LinkedHashMap
Map<String, User> ordered = new LinkedHashMap<>();

// ✅ If sorted by key — TreeMap
Map<String, User> sorted = new TreeMap<>();

// ✅ If thread-safe — ConcurrentHashMap
Map<String, User> concurrent = new ConcurrentHashMap<>();
```

### Decision Matrix
| Need | Use | Lookup |
|------|-----|--------|
| Fast lookup by key | `HashMap` | O(1) |
| Lookup + insertion order | `LinkedHashMap` | O(1) |
| Lookup + sorted keys | `TreeMap` | O(log n) |
| Thread-safe lookup | `ConcurrentHashMap` | O(1) |
| Ordered, indexed access | `ArrayList` | O(1) by index |
| Frequent insert at front | `LinkedList` | O(1) |
| Unique elements, no order | `HashSet` | O(1) |
| Unique + sorted | `TreeSet` | O(log n) |

### Key Takeaway
- For key-based lookup, always use `Map` — never `List`
- `HashMap` is O(1) average — 1 million records, instant lookup
- Choose `LinkedHashMap` for insertion order, `TreeMap` for sorted
- `ConcurrentHashMap` for multi-threaded access
- Pre-size the map: `new HashMap<>(1_300_000)` (capacity / 0.75) to avoid resizing

---

## Scenario 2: Removing Elements During Iteration

### Problem
Removing elements from a list while iterating causes `ConcurrentModificationException` or skips elements.

```java
// ❌ Bad — ConcurrentModificationException
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5));
for (Integer n : nums) {
    if (n % 2 == 0) {
        nums.remove(n);  // ❌ ConcurrentModificationException
    }
}

// ❌ Bad — skips elements (index shifts)
for (int i = 0; i < nums.size(); i++) {
    if (nums.get(i) % 2 == 0) {
        nums.remove(i);  // Removes, shifts left, skips next element
    }
}
```

### Solution: Iterator.remove() or removeIf

```java
// ✅ Good 1: Iterator.remove()
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5));
Iterator<Integer> it = nums.iterator();
while (it.hasNext()) {
    if (it.next() % 2 == 0) {
        it.remove();  // ✅ Safe — uses iterator's remove
    }
}
// [1, 3, 5]

// ✅ Good 2: Collection.removeIf() (Java 8+)
nums.removeIf(n -> n % 2 == 0);  // ✅ Clean, one line

// ✅ Good 3: Stream filter (creates new list)
List<Integer> filtered = nums.stream()
    .filter(n -> n % 2 != 0)
    .collect(Collectors.toList());

// ✅ Good 4: CopyOf + clear + addAll (for immutable source)
List<Integer> copy = new ArrayList<>(nums);
copy.removeIf(n -> n % 2 == 0);
```

### Key Takeaway
- Never modify a collection while iterating with for-each (throws CME)
- `Iterator.remove()` is the only safe way to remove during iteration
- `removeIf(predicate)` is the cleanest approach (Java 8+)
- Stream `filter()` creates a new collection — doesn't modify source
- For `CopyOnWriteArrayList`, for-each is safe (iterates a snapshot)

---

## Scenario 3: HashMap with Custom Keys

### Problem
Using a custom object as a HashMap key produces incorrect results — `get()` returns null even though the key exists.

```java
// ❌ Bad — no equals/hashCode override
class UserKey {
    private String email;

    public UserKey(String email) { this.email = email; }
    // No equals(), no hashCode() — uses Object's (reference equality)
}

Map<UserKey, String> map = new HashMap<>();
map.put(new UserKey("alice@test.com"), "Admin");

// Returns null — different object, different hashCode
String role = map.get(new UserKey("alice@test.com"));
```

### Solution: Override equals() and hashCode()

```java
// ✅ Good — proper equals() and hashCode()
public class UserKey {
    private final String email;

    public UserKey(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserKey userKey = (UserKey) o;
        return Objects.equals(email, userKey.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

// Now it works
Map<UserKey, String> map = new HashMap<>();
map.put(new UserKey("alice@test.com"), "Admin");
map.get(new UserKey("alice@test.com"));  // "Admin" ✅

// ✅ Even better — use record (auto-generates equals/hashCode)
public record UserKey(String email) {}

// ✅ Or use String as key directly (simplest)
Map<String, String> map = new HashMap<>();
map.put("alice@test.com", "Admin");
```

### equals/hashCode Contract
- If `a.equals(b)`, then `a.hashCode() == b.hashCode()` (must be true)
- If `!a.equals(b)`, hashCodes may or may not be equal
- Consistent: same object always returns same hashCode
- Use `Objects.hash()` for multi-field hashCode
- Use `Objects.equals()` for null-safe field comparison

### Key Takeaway
- Default `equals()`/`hashCode()` use reference identity — wrong for HashMap keys
- Always override both when using objects as Map keys or in Set
- `record` types auto-generate correct `equals()`/`hashCode()`
- If possible, use a simple key (String, Integer) instead of a custom object
- IDE can generate `equals()`/`hashCode()` — always review them

---

## Scenario 4: Grouping and Aggregating Data

### Problem
Given a list of employees, group them by department and calculate the average salary per department.

```java
// ❌ Bad — manual grouping with loops
List<Employee> employees = getEmployees();
Map<String, List<Employee>> byDept = new HashMap<>();
for (Employee e : employees) {
    if (!byDept.containsKey(e.getDepartment())) {
        byDept.put(e.getDepartment(), new ArrayList<>());
    }
    byDept.get(e.getDepartment()).add(e);
}

Map<String, Double> avgSalary = new HashMap<>();
for (Map.Entry<String, List<Employee>> entry : byDept.entrySet()) {
    double sum = 0;
    for (Employee e : entry.getValue()) {
        sum += e.getSalary();
    }
    avgSalary.put(entry.getKey(), sum / entry.getValue().size());
}
```

### Solution: Stream API Collectors

```java
// ✅ Good — groupingBy with downstream collector
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.averagingDouble(Employee::getSalary)
    ));

// ✅ More examples
// Count by department
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

// Sum salary by department
Map<String, Double> totalSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.summingDouble(Employee::getSalary)
    ));

// List of names by department
Map<String, List<String>> namesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));

// Highest paid employee per department
Map<String, Optional<Employee>> highestByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
    ));

// Partition by salary threshold
Map<Boolean, List<Employee>> bySalary = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getSalary() > 50000));

// Multi-level grouping
Map<String, Map<String, List<Employee>>> byDeptAndCity = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.groupingBy(Employee::getCity)
    ));

// Statistics summary
Map<String, DoubleSummaryStatistics> statsByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.summarizingDouble(Employee::getSalary)
    ));
// stats.getCount(), stats.getAverage(), stats.getMax(), stats.getMin(), stats.getSum()
```

### Key Takeaway
- `Collectors.groupingBy()` replaces manual grouping loops
- Downstream collectors: `counting`, `summingDouble`, `averagingDouble`, `maxBy`
- `mapping()` transforms elements before collecting
- `partitioningBy()` splits into two groups (true/false)
- Multi-level grouping: nest `groupingBy` inside `groupingBy`
- `summarizingDouble` gives count, sum, min, avg, max in one pass

---

## Scenario 5: LRU Cache with LinkedHashMap

### Problem
Implement a cache that holds the last N accessed items. When full, evict the least recently used (LRU) entry.

```java
// ❌ Bad — manual LRU with LinkedList + HashMap
class BadLRUCache<K, V> {
    private final int capacity;
    private final Map<K, V> cache = new HashMap<>();
    private final LinkedList<K> accessOrder = new LinkedList<>();

    public V get(K key) {
        if (!cache.containsKey(key)) return null;
        accessOrder.remove(key);  // O(n) — slow!
        accessOrder.addFirst(key);
        return cache.get(key);
    }

    public void put(K key, V value) {
        if (cache.size() >= capacity) {
            K lru = accessOrder.removeLast();
            cache.remove(lru);
        }
        cache.put(key, value);
        accessOrder.addFirst(key);
    }
}
```

### Solution: LinkedHashMap with access order

```java
// ✅ Good — LinkedHashMap with access-order + removeEldestEntry
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);  // true = access-order (not insertion-order)
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;  // Auto-remove oldest when over capacity
    }
}

// Usage
LRUCache<String, String> cache = new LRUCache<>(3);
cache.put("a", "1");
cache.put("b", "2");
cache.put("c", "3");
cache.get("a");  // Access "a" — moves to most-recent
cache.put("d", "4");  // "b" is LRU → evicted
// Cache: {c=3, a=1, d=4}

// ✅ Thread-safe version
public class ConcurrentLRUCache<K, V> {
    private final LinkedHashMap<K, V> cache;

    public ConcurrentLRUCache(int capacity) {
        this.cache = Collections.synchronizedMap(
            new LinkedHashMap<>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > capacity;
                }
            }
        );
    }

    public synchronized V get(K key) { return cache.get(key); }
    public synchronized void put(K key, V value) { cache.put(key, value); }
}
```

### Key Takeaway
- `LinkedHashMap(capacity, loadFactor, accessOrder=true)` enables LRU behavior
- Override `removeEldestEntry()` to auto-evict when capacity exceeded
- `accessOrder=true` reorders on `get()` — most recently used moves to end
- `accessOrder=false` (default) only reorders on insertion
- For thread safety, wrap with `Collections.synchronizedMap` or use `ConcurrentHashMap` with manual LRU

---

## 🔗 Related Topics
- [Collections](../intermediate/Collections.md)
- [Lambda and Streams](../intermediate/LambdaAndStreams.md)
- [Concurrency Scenarios](ConcurrencyScenarios.md)
