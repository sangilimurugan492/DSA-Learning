# Collections Framework

## Q1: What is the Collections Framework hierarchy?

```
Collection (interface)
├── List (interface)          — Ordered, allows duplicates
│   ├── ArrayList             — Resizable array, fast random access
│   ├── LinkedList            — Doubly-linked list, fast insert/delete
│   ├── Vector                — Synchronized ArrayList (legacy)
│   │   └── Stack             — LIFO stack (legacy)
│   └── CopyOnWriteArrayList  — Thread-safe, copy on write
│
├── Set (interface)           — No duplicates
│   ├── HashSet               — Hash table, O(1) lookup
│   ├── LinkedHashSet         — HashSet with insertion order
│   └── TreeSet               — Red-Black tree, sorted
│
└── Queue (interface)         — FIFO
    ├── PriorityQueue         — Min-heap, sorted by priority
    ├── ArrayDeque             — Resizable array deque
    └── LinkedList             — Also a Deque

Map (interface) — Key-value pairs (not a Collection)
├── HashMap                   — Hash table, O(1), no order
├── LinkedHashMap             — HashMap with insertion order
├── TreeMap                    — Red-Black tree, sorted by key
├── Hashtable                  — Synchronized (legacy)
└── ConcurrentHashMap          — Thread-safe, segment locking
```

---

## Q2: ArrayList vs LinkedList

| Feature | ArrayList | LinkedList |
|---------|-----------|------------|
| Internal | Dynamic array | Doubly-linked list |
| `get(i)` | O(1) | O(n) |
| `add(e)` (end) | O(1) amortized | O(1) |
| `add(0, e)` (start) | O(n) — shift all | O(1) |
| `remove(i)` | O(n) — shift | O(n) to find, O(1) to remove |
| Memory | Less (contiguous) | More (node pointers) |
| Iteration | Fast (cache-friendly) | Slower (pointer chasing) |
| Implements | List, RandomAccess | List, Deque |

```java
// ArrayList — use for random access, iteration
List<String> list = new ArrayList<>();
list.add("A"); list.add("B"); list.add("C");
list.get(1);  // O(1) — "B"

// LinkedList — use for frequent insert/delete at ends
Deque<String> deque = new LinkedList<>();
deque.addFirst("A");
deque.addLast("B");
deque.removeFirst();  // O(1)
```

---

## Q3: HashMap vs TreeMap vs LinkedHashMap

| Feature | HashMap | LinkedHashMap | TreeMap |
|---------|---------|----------------|---------|
| Order | No order | Insertion order | Sorted by key |
| `get/put` | O(1) avg | O(1) avg | O(log n) |
| Null keys | One null key | One null key | No null keys |
| Internal | Array + buckets | HashMap + DLL | Red-Black tree |
| Implements | Map | Map | NavigableMap |

```java
// HashMap — fast, no order
Map<String, Integer> hashMap = new HashMap<>();
hashMap.put("banana", 2);
hashMap.put("apple", 5);
hashMap.put("cherry", 3);
// Iteration order: unpredictable

// LinkedHashMap — insertion order
Map<String, Integer> linkedMap = new LinkedHashMap<>();
linkedMap.put("banana", 2);
linkedMap.put("apple", 5);
linkedMap.put("cherry", 3);
// Order: banana, apple, cherry (insertion order)

// Access-order LinkedHashMap (LRU cache)
Map<String, Integer> lru = new LinkedHashMap<>(16, 0.75f, true);
lru.put("a", 1); lru.put("b", 2); lru.get("a");
// Order: b, a (accessed moves to end)

// TreeMap — sorted by key
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.put("banana", 2);
treeMap.put("apple", 5);
treeMap.put("cherry", 3);
// Order: apple, banana, cherry (sorted)
```

---

## Q4: How does HashMap work internally?

```java
// Internal structure: array of buckets (Node[])
// Each bucket: linked list or red-black tree (Java 8+)

// put("key", value):
// 1. hash = key.hashCode()
// 2. index = hash & (capacity - 1)  — find bucket
// 3. If bucket empty → add node
// 4. If key exists → update value
// 5. If bucket has > 8 nodes → convert to Red-Black tree

// Default: capacity=16, loadFactor=0.75
// When size > capacity * loadFactor → resize (double capacity)
```

```java
// Java 8+ bucket structure
class Node<K, V> {
    final int hash;
    final K key;
    V value;
    Node<K, V> next;
}

// When bucket size > TREEIFY_THRESHOLD (8):
// Linked list → Red-Black tree (O(n) → O(log n) lookup)
// When bucket size < UNTREEIFY_THRESHOLD (6):
// Red-Black tree → Linked list
```

### Key Points
- `hashCode()` determines the bucket
- `equals()` checks if key exists in the bucket
- Bad `hashCode()` (all same) → all in one bucket → O(n) lookup
- Java 8 converts long chains to trees for O(log n) worst case
- Resize doubles capacity and rehashes all entries

---

## Q5: What is the difference between fail-fast and fail-safe iterators?

```java
// Fail-fast — throws ConcurrentModificationException
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
Iterator<String> it = list.iterator();
list.add("D");  // Modify during iteration
it.next();  // ❌ ConcurrentModificationException

// Fail-safe — works on a copy, no exception
CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>(List.of("A", "B", "C"));
Iterator<String> cowIt = cowList.iterator();
cowList.add("D");
cowIt.next();  // ✅ "A" — sees the snapshot, no exception

// Safe removal during iteration
Iterator<String> safeIt = list.iterator();
while (safeIt.hasNext()) {
    if (safeIt.next().equals("B")) {
        safeIt.remove();  // ✅ Use iterator's remove method
    }
}
```

| Fail-Fast | Fail-Safe |
|-----------|-----------|
| Throws `ConcurrentModificationException` | No exception |
| Iterates original collection | Iterates copy/snapshot |
| Doesn't reflect modifications | May not see latest changes |
| Examples: ArrayList, HashMap | Examples: CopyOnWriteArrayList, ConcurrentHashMap |

---

## Q6: How do you sort collections?

```java
// 1. Collections.sort() — natural ordering
List<Integer> nums = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9));
Collections.sort(nums);  // [1, 1, 3, 4, 5, 9]

// 2. List.sort() with Comparator
List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob"));
names.sort(Comparator.naturalOrder());  // [Alice, Bob, Charlie]
names.sort(Comparator.reverseOrder());  // [Charlie, Bob, Alice]
names.sort(Comparator.comparing(String::length));  // [Bob, Alice, Charlie]

// 3. Custom Comparator
List<Person> people = new ArrayList<>(List.of(
    new Person("Alice", 30),
    new Person("Bob", 25),
    new Person("Charlie", 35)
));
people.sort(Comparator.comparing(Person::getAge));  // By age ascending
people.sort(Comparator.comparing(Person::getAge).reversed());  // By age descending
people.sort(Comparator.comparing(Person::getName)
    .thenComparing(Person::getAge));  // Name, then age

// 4. Stream sorted()
List<Person> sorted = people.stream()
    .sorted(Comparator.comparing(Person::getAge))
    .collect(Collectors.toList());

// 5. TreeSet — auto-sorted on insertion
Set<Integer> sortedSet = new TreeSet<>(List.of(3, 1, 4, 1, 5));
// [1, 3, 4, 5] — sorted, no duplicates
```

---

## Q7: What are concurrent collections?

```java
// ConcurrentHashMap — thread-safe HashMap
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);  // Thread-safe, no synchronization needed
map.computeIfAbsent("key2", k -> 0);  // Atomic operation

// CopyOnWriteArrayList — thread-safe, copy on write
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.add("item");  // Creates a new copy — expensive for writes

// BlockingQueue — producer-consumer pattern
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);
// Producer
queue.put(task);  // Blocks if full
// Consumer
Task task = queue.take();  // Blocks if empty

// ConcurrentLinkedQueue — non-blocking, lock-free
ConcurrentLinkedQueue<String> clQueue = new ConcurrentLinkedQueue<>();
clQueue.offer("item");  // Non-blocking
```

| Collection | Thread Safety | Performance | Use Case |
|-----------|--------------|-------------|----------|
| `HashMap` | ❌ No | Fastest | Single-thread |
| `Hashtable` | ✅ Full lock | Slow (legacy) | Don't use |
| `ConcurrentHashMap` | ✅ Segment lock | Fast | Multi-threaded map |
| `synchronizedList` | ✅ Full lock | Moderate | Low contention |
| `CopyOnWriteArrayList` | ✅ Copy on write | Fast read, slow write | Read-heavy |

---

## 🔗 Related Topics
- [Arrays](../beginner/Arrays.md)
- [Generics](Generics.md)
- [Lambda and Streams](LambdaAndStreams.md)
- [Collection Scenarios](../scenario_based/CollectionScenarios.md)
