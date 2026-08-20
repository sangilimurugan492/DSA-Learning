# Data Structure Theory: Hash Tables

> **In-depth theory, diagrams, and implementation details for understanding hash tables at a fundamental level.**

---

## 1. What is a Hash Table?

A hash table (hash map) is a data structure that stores **key-value pairs** and uses a **hash function** to compute an **index** (bucket) where the value is stored. This enables **O(1) average** insert, delete, and lookup.

```
Key: "apple"     Hash Function     Bucket Array
                  ─────────→       ┌──────┐
                                   │ [0]  │
                 ┌────────────────→│ [1]  │ → [("apple", 5)]
                 │                 │ [2]  │
  hash("apple") = 1               │ [3]  │ → [("banana", 3)]
                 │                 │ [4]  │
                 │                 │ [5]  │
                                   │ [6]  │ → [("cherry", 7)]
                                   │ [7]  │
                                   └──────┘

hash(key) = index in bucket array
```

### Key Properties:
- **Key-value pairs**: Each entry has a unique key mapped to a value
- **O(1) average operations**: Insert, delete, lookup all constant time (average)
- **O(N) worst case**: When all keys hash to same bucket (hash collision)
- **Unordered**: No inherent ordering of keys
- **Dynamic resizing**: Grows when load factor exceeds threshold

---

## 2. Hash Function

A hash function converts a key into an integer index.

### Properties of a Good Hash Function:
1. **Deterministic**: Same key → same hash every time
2. **Uniform distribution**: Spreads keys evenly across buckets
3. **Fast**: Computes quickly (O(1))
4. **Avalanche effect**: Small change in key → big change in hash

### Common Hash Functions:

```
1. Division Method:
   hash(key) = key % tableSize
   (tableSize should be prime for better distribution)

2. Multiplication Method:
   hash(key) = floor(tableSize × (key × A % 1))
   where A = (√5 - 1) / 2 ≈ 0.618 (golden ratio)

3. String Hash (djb2):
   hash = 5381
   for each char c in string:
       hash = hash × 33 + c
   return hash % tableSize

4. Polynomial Rolling Hash:
   hash("abc") = a×p² + b×p¹ + c×p⁰  (mod tableSize)
   where p = prime (e.g., 31)
```

### Example: String Hashing

```
Key: "cat"
Table size: 10

Simple hash: sum of char codes % 10
  'c' = 99, 'a' = 97, 't' = 116
  sum = 312
  312 % 10 = 2

→ "cat" goes to bucket [2]

Key: "dog"
  'd' = 100, 'o' = 111, 'g' = 103
  sum = 314
  314 % 10 = 4

→ "dog" goes to bucket [4]
```

---

## 3. Hash Collisions

A collision occurs when two different keys hash to the **same bucket**. This is inevitable (pigeonhole principle) — there are more possible keys than buckets.

### 3.1 Separate Chaining (Open Chaining)

Each bucket stores a **linked list** of all entries that hash to that bucket.

```
Bucket Array:
┌──────┐
│ [0]  │ → NULL
├──────┤
│ [1]  │ → [("apple",5)] → [("apricot",8)] → NULL
├──────┤
│ [2]  │ → [("cat",3)] → NULL
├──────┤
│ [3]  │ → NULL
├──────┤
│ [4]  │ → [("dog",4)] → [("duck",2)] → [("donkey",6)] → NULL
├──────┤
│ [5]  │ → NULL
└──────┘

"apple" and "apricot" both hash to bucket [1] → linked list.
"dog", "duck", "donkey" all hash to bucket [4] → linked list.

Lookup: hash key → go to bucket → traverse list → find key.
Worst case: all N keys in one bucket → O(N) lookup.
Average case: uniform distribution → O(1) lookup.
```

### 3.2 Open Addressing (Probing)

No linked lists. On collision, **probe** (search) for the next empty slot.

#### Linear Probing:
```
hash(key) = h. If bucket [h] is full, try [h+1], [h+2], [h+3]...

Insert "apple" (hash=1): bucket[1] empty → place at [1]
Insert "apricot" (hash=1): bucket[1] full → try [2] → empty → place at [2]
Insert "avocado" (hash=1): bucket[1] full → [2] full → try [3] → empty → place at [3]

Bucket: [_ , "apple", "apricot", "avocado", _, _, _, _]
         [0]  [1]       [2]        [3]      [4] [5] [6] [7]

Problem: PRIMARY CLUSTERING — long runs of filled slots build up.
```

#### Quadratic Probing:
```
Instead of h+1, h+2, h+3... try h+1², h+2², h+3²...

hash=1, collision → try 1+1²=2
hash=1, collision → try 1+2²=5
hash=1, collision → try 1+3²=10 → 10%8=2 (already tried? depends)

Reduces primary clustering but can cause SECONDARY CLUSTERING.
```

#### Double Hashing:
```
Use a SECOND hash function for probe sequence:

probe(i) = (hash1(key) + i × hash2(key)) % tableSize

hash1(key) = key % tableSize
hash2(key) = 1 + (key % (tableSize - 1))  // Never 0!

This ensures the probe sequence visits ALL buckets (no clustering).
```

### Comparison: Chaining vs Open Addressing

| Aspect | Separate Chaining | Open Addressing |
|--------|-------------------|-----------------|
| Memory | Extra (pointers per entry) | No extra (in-place) |
| Cache | Poor (linked list scattered) | Better (contiguous array) |
| Deletion | Easy (remove from list) | Hard (need tombstone markers) |
| Load factor | Can exceed 1 | Must be < 1 |
| Implementation | Simpler | More complex |
| Worst case | O(N) (all in one bucket) | O(N) (full table) |

---

## 4. Load Factor and Resizing

### Load Factor (α):

```
Load Factor = (number of entries) / (number of buckets)

α = n / m

  n = number of key-value pairs stored
  m = number of buckets (table size)

If α is high → more collisions → slower operations
If α is low → wasted space → faster operations

Typical threshold: α = 0.75 (resize when exceeded)

```

### Resizing (Rehashing):

```
When load factor exceeds threshold (e.g., 0.75):

BEFORE: 8 buckets, 6 entries → α = 6/8 = 0.75 → RESIZE!

Step 1: Create new array (2× size = 16 buckets)
Step 2: Rehash ALL existing entries into new array
        (hash function depends on table size, so indices change!)

Old bucket [1]: "apple" → new hash → bucket [3]
Old bucket [4]: "dog" → new hash → bucket [12]
...

Step 3: Replace old array with new

Cost: O(N) for rehashing, but amortized O(1) per insert.

Why rehash? Because hash = key % tableSize, and tableSize changed!
```

### Visual: Resizing

```
BEFORE (size=4, α=0.75):
[0] → NULL
[1] → [("apple",5)] → [("art",9)]
[2] → NULL
[3] → [("banana",3)]

AFTER RESIZE (size=8):
[0] → NULL
[1] → NULL
[2] → NULL
[3] → [("apple",5)]     ← rehashed: 5%8=5? No, depends on hash
[4] → NULL
[5] → [("art",9)]
[6] → NULL
[7] → [("banana",3)]

Each key is re-hashed with new table size.
```

---

## 5. Operations and Time Complexity

| Operation | Average | Worst Case | Notes |
|-----------|---------|------------|-------|
| **insert/put** | **O(1)** | O(N) | All keys collide |
| **search/get** | **O(1)** | O(N) | All keys in one bucket |
| **delete/remove** | **O(1)** | O(N) | Same as search + remove |
| **resize/rehash** | O(N) | O(N) | Amortized O(1) per insert |

> **Worst case O(N)** happens when all keys hash to the same bucket (terrible hash function or adversarial input). Good hash functions make this extremely unlikely.

---

## 6. Implementation (Kotlin)

### Hash Table with Separate Chaining:

```kotlin
class HashTable<K, V>(initialCapacity: Int = 16) {
    private data class Entry<K, V>(val key: K, var value: V, var next: Entry<K, V>? = null)

    private var buckets: Array<Entry<K, V>?> = arrayOfNulls(initialCapacity)
    private var size = 0
    private val loadFactorThreshold = 0.75

    fun put(key: K, value: V) {
        val index = hashIndex(key)
        var current = buckets[index]
        
        // Check if key already exists (update value)
        while (current != null) {
            if (current.key == key) {
                current.value = value
                return
            }
            current = current.next
        }
        
        // Insert new entry at head of chain
        val entry = Entry(key, value, buckets[index])
        buckets[index] = entry
        size++
        
        // Check if resize needed
        if (loadFactor() > loadFactorThreshold) {
            resize()
        }
    }

    fun get(key: K): V? {
        val index = hashIndex(key)
        var current = buckets[index]
        while (current != null) {
            if (current.key == key) return current.value
            current = current.next
        }
        return null
    }

    fun remove(key: K): V? {
        val index = hashIndex(key)
        var current = buckets[index]
        var prev: Entry<K, V>? = null
        
        while (current != null) {
            if (current.key == key) {
                if (prev == null) {
                    buckets[index] = current.next
                } else {
                    prev.next = current.next
                }
                size--
                return current.value
            }
            prev = current
            current = current.next
        }
        return null
    }

    private fun hashIndex(key: K): Int {
        val hashCode = key.hashCode()
        return (hashCode and 0x7FFFFFFF) % buckets.size  // Positive index
    }

    private fun loadFactor(): Double = size.toDouble() / buckets.size

    private fun resize() {
        val oldBuckets = buckets
        buckets = arrayOfNulls(oldBuckets.size * 2)
        size = 0  // Reset, put() will increment
        
        for (entry in oldBuckets) {
            var current = entry
            while (current != null) {
                put(current.key, current.value)
                current = current.next
            }
        }
    }

    fun size(): Int = size
    fun isEmpty(): Boolean = size == 0
}
```

---

## 7. Hash Table vs Other Data Structures

| Operation | Hash Table | Sorted Array | BST | Linked List |
|-----------|-----------|--------------|-----|-------------|
| Insert | **O(1)** avg | O(N) | O(log N) | **O(1)** |
| Search | **O(1)** avg | O(log N) | O(log N) | O(N) |
| Delete | **O(1)** avg | O(N) | O(log N) | O(N) |
| Ordered traversal | O(N log N)* | **O(N)** | **O(N)** | O(N) |
| Min/Max | O(N) | **O(1)** | O(log N) | O(N) |
| Range query | O(N) | **O(log N + k)** | O(log N + k) | O(N) |

*Need to sort keys first.

**Key insight:** Hash tables are the FASTEST for key-based lookup, but DON'T maintain order. If you need ordered keys, use a BST or sorted array.

---

## 8. When to Use Hash Tables

### Use Hash Tables When:
- ✅ You need **O(1) lookup** by key
- ✅ You need **fast insert/delete** by key
- ✅ You don't need **ordered** keys
- ✅ Implementing **caches** (LRU, LFU)
- ✅ **Counting frequencies** (histogram)
- ✅ **Deduplication** (HashSet)
- ✅ **Memoization** in DP (key = state, value = result)

### Don't Use Hash Tables When:
- ❌ You need **ordered** traversal (use BST)
- ❌ You need **range queries** (use BST)
- ❌ You need **sorted** min/max (use heap)
- ❌ Memory is extremely constrained (overhead for buckets)

---

## 9. Real-World Applications

| Application | How Hash Table Is Used |
|-------------|----------------------|
| **Database indexing** | Hash index for exact-match lookups |
| **Caches** | Key = URL/ID, Value = cached data |
| **Symbol table** | Compiler: variable name → memory address |
| **Spell checker** | HashSet of valid words, O(1) lookup |
| **Password storage** | Hash(password) → stored hash (with salt) |
| **Blockchain** | SHA-256 hash links blocks |
| **File checksums** | MD5/SHA hash verifies file integrity |
| **LRU Cache** | HashMap + Doubly Linked List for O(1) get/put |
| **Database JOIN** | Hash join: build hash table on smaller table |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Queues →](./04_Queues.md)
- [Next: Trees →](./06_Trees.md)
