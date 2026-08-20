# Data Structure Theory: Arrays

> **In-depth theory, diagrams, and implementation details for understanding arrays at a fundamental level.**

---

## 1. What is an Array?

An array is a **contiguous block of memory** that stores a fixed number of elements of the **same data type**. Each element is accessed by its **index** (zero-based).

```
Memory Layout (Contiguous):

Index:    0      1      2      3      4      5
        ┌──────┬──────┬──────┬──────┬──────┬──────┐
Value:  │  10  │  20  │  30  │  40  │  50  │  60  │
        └──────┴──────┴──────┴──────┴──────┴──────┘
Address: 0x100  0x104  0x108  0x10C  0x110  0x114

         ↑
      base address
      (array[0] = 0x100)

Element size = 4 bytes (for Int)
Address of array[i] = base_address + (i × element_size)
```

### Key Properties:
- **Contiguous memory**: All elements are stored side-by-side
- **Fixed size** (in most languages): Size determined at creation
- **Homogeneous**: All elements are the same type
- **Random access**: O(1) access to any element via index
- **Zero-indexed**: First element is at index 0

---

## 2. How Arrays Work Internally

### Memory Address Calculation

```
array[i] address = base_address + (i × sizeof(element_type))

Example: int array[6] at base 0x100 (int = 4 bytes)

array[0] → 0x100 + (0 × 4) = 0x100
array[1] → 0x100 + (1 × 4) = 0x104
array[2] → 0x100 + (2 × 4) = 0x108
array[3] → 0x100 + (3 × 4) = 0x10C
array[4] → 0x100 + (4 × 4) = 0x110
array[5] → 0x100 + (5 × 4) = 0x114
```

This is why array access is **O(1)** — the CPU directly computes the memory address with simple arithmetic.

### Static vs Dynamic Arrays

```
STATIC ARRAY (C/C++/Java primitive):
┌──────────────────────────┐
│ Fixed size at creation    │
│ Cannot grow/shrink        │
│ Allocated on stack/heap   │
│ int arr[10];              │
└──────────────────────────┘

DYNAMIC ARRAY (ArrayList, vector, Kotlin MutableList):
┌──────────────────────────────────────┐
│ Grows automatically when full         │
│ Backed by a fixed array internally    │
│ When full: create 2x array, copy over │
│ Amortized O(1) append                 │
└──────────────────────────────────────┘

Dynamic Array Growth:
Capacity: 4 → 8 → 16 → 32 → 64 ...

Step 1: [10, 20, 30, 40]  capacity=4, size=4 (FULL!)
Step 2: Allocate new array capacity=8
Step 3: Copy elements: [10, 20, 30, 40, _, _, _, _]
Step 4: Add new element: [10, 20, 30, 40, 50, _, _, _]
```

### Amortized Analysis of Dynamic Arrays

```
Operation   | Cost  | Notes
------------|-------|------
Insert (normal)  | O(1) | Just add at end
Insert (resize)   | O(N) | Allocate + copy N elements
Resize frequency  | Rare | Every N/2 inserts

Amortized cost = (N × O(1) + 1 × O(N)) / (N+1) ≈ O(1)

When we double, the resize cost is spread across many inserts.
Each element is copied at most log(n) times total.
```

---

## 3. Types of Arrays

### 3.1 One-Dimensional (1D) Array

```
[10, 20, 30, 40, 50]
 0    1    2    3    4
```

### 3.2 Two-Dimensional (2D) Array (Matrix)

```
Row\Memory layout (Row-Major Order):

Logical view:              Memory (Row-Major):
┌────┬────┬────┐          [1][2][3][4][5][6]
│ 1  │ 2  │ 3  │           ↑           ↑
├────┼────┼────┤          Row 0      Row 1
│ 4  │ 5  │ 6  │
└────┴────┴────┘
  [0]  [1]  [2]

matrix[i][j] address = base + (i × numColumns + j) × elementSize
```

### 3.3 Multi-Dimensional Array (3D)

```
3D Array: [2][3][2]

Layer 0:          Layer 1:
┌────┬────┐       ┌────┬────┐
│ a  │ b  │       │ g  │ h  │
├────┼────┤       ├────┼────┤
│ c  │ d  │       │ i  │ j  │
├────┼────┤       ├────┼────┤
│ e  │ f  │       │ k  │ l  │
└────┴────┘       └────┴────┘

Memory: [a][b][c][d][e][f][g][h][i][j][k][l]
```

### 3.4 Jagged Array (Ragged Array)

```
Rows have different lengths:

Row 0: [1, 2, 3, 4]
Row 1: [5, 6]
Row 2: [7, 8, 9]

Not all rows same length → not a true matrix
```

---

## 4. Operations and Time Complexity

### Summary Table

| Operation | Time Complexity | Space | Notes |
|-----------|----------------|-------|-------|
| Access by index | **O(1)** | O(1) | Direct address calculation |
| Search (unsorted) | **O(N)** | O(1) | Linear scan |
| Search (sorted) | **O(log N)** | O(1) | Binary search |
| Insert at end | **O(1)** amortized | O(1) | Dynamic array |
| Insert at beginning | **O(N)** | O(1) | Shift all elements right |
| Insert at middle | **O(N)** | O(1) | Shift elements right |
| Delete from end | **O(1)** | O(1) | Just decrement size |
| Delete from beginning | **O(N)** | O(1) | Shift all elements left |
| Delete from middle | **O(N)** | O(1) | Shift elements left |
| Update by index | **O(1)** | O(1) | Direct address |
| Get length | **O(1)** | O(1) | Stored as metadata |

### Visual: Insert at Beginning (O(N))

```
BEFORE: [10, 20, 30, 40, _]  size=4

Insert 99 at index 0:
Step 1: Shift all right
        [_, 10, 20, 30, 40]  (every element moved!)

Step 2: Insert at 0
        [99, 10, 20, 30, 40]  size=5

Cost: N shifts → O(N)
```

### Visual: Insert at End (O(1) amortized)

```
BEFORE: [10, 20, 30, _, _]  size=3, capacity=5

Insert 40 at end:
        [10, 20, 30, 40, _]  size=4

No shifting needed → O(1)

If full → resize (O(N)), but rare → amortized O(1)
```

### Visual: Delete from Middle (O(N))

```
BEFORE: [10, 20, 30, 40, 50]  size=5

Delete index 2 (value 30):
Step 1: Remove 30
        [10, 20, __, 40, 50]
Step 2: Shift left to fill gap
        [10, 20, 40, 50, _]  size=4

Cost: N-i shifts → O(N)
```

---

## 5. Implementation (Kotlin)

### Static-style Array (FixedSizeArray):

```kotlin
class FixedSizeArray<T>(val capacity: Int) {
    private val data = Array<Any?>(capacity) { null }
    private var size = 0

    fun get(index: Int): T {
        checkIndex(index)
        @Suppress("UNCHECKED_CAST")
        return data[index] as T
    }

    fun set(index: Int, value: T) {
        checkIndex(index)
        data[index] = value
    }

    fun append(value: T) {
        require(size < capacity) { "Array is full" }
        data[size++] = value
    }

    fun insertAt(index: Int, value: T) {
        require(size < capacity) { "Array is full" }
        require(index in 0..size) { "Index out of bounds" }
        // Shift elements right
        for (i in size downTo index + 1) {
            data[i] = data[i - 1]
        }
        data[index] = value
        size++
    }

    fun deleteAt(index: Int): T {
        checkIndex(index)
        @Suppress("UNCHECKED_CAST")
        val removed = data[index] as T
        // Shift elements left
        for (i in index until size - 1) {
            data[i] = data[i + 1]
        }
        data[--size] = null
        return removed
    }

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    private fun checkIndex(index: Int) {
        require(index in 0 until size) { 
            "Index $index out of bounds for size $size" 
        }
    }
}
```

### Dynamic Array (like ArrayList):

```kotlin
class DynamicArray<T> {
    private var data: Array<Any?> = Array(4) { null }
    private var size = 0

    fun get(index: Int): T {
        checkIndex(index)
        @Suppress("UNCHECKED_CAST")
        return data[index] as T
    }

    fun add(value: T) {
        if (size == data.size) {
            resize()  // Double the capacity
        }
        data[size++] = value
    }

    private fun resize() {
        val newData = Array<Any?>(data.size * 2) { null }
        for (i in 0 until size) {
            newData[i] = data[i]
        }
        data = newData
    }

    fun removeAt(index: Int): T {
        checkIndex(index)
        @Suppress("UNCHECKED_CAST")
        val removed = data[index] as T
        for (i in index until size - 1) {
            data[i] = data[i + 1]
        }
        data[--size] = null
        
        // Shrink if too empty (optional)
        if (size > 0 && size == data.size / 4) {
            shrink()
        }
        return removed
    }

    private fun shrink() {
        val newData = Array<Any?>(data.size / 2) { null }
        for (i in 0 until size) {
            newData[i] = data[i]
        }
        data = newData
    }

    fun size(): Int = size
    fun capacity(): Int = data.size

    private fun checkIndex(index: Int) {
        require(index in 0 until size) { 
            "Index $index out of bounds for size $size" 
        }
    }
}
```

---

## 6. Array Traversal Patterns

### 6.1 Linear Scan (Left to Right)

```kotlin
for (i in array.indices) {
    // Process array[i]
}
```

```
→ → → → →
[10, 20, 30, 40, 50]
```

### 6.2 Reverse Scan (Right to Left)

```kotlin
for (i in array.indices.reversed()) {
    // Process array[i]
}
```

```
[10, 20, 30, 40, 50]
             ← ← ← ← ←
```

### 6.3 Two Pointer (Converging)

```kotlin
var left = 0
var right = array.size - 1
while (left < right) {
    // Process array[left] and array[right]
    left++
    right--
}
```

```
left              right
 ↓                 ↓
[10, 20, 30, 40, 50]
  →              ←
```

### 6.4 Sliding Window

```kotlin
var left = 0
for (right in array.indices) {
    // Expand window by including array[right]
    while (windowInvalid()) {
        // Shrink window from left
        left++
    }
    // Process valid window [left..right]
}
```

```
Window: [left ===== right]
[10, 20, 30, 40, 50, 60, 70]
         └──window──┘
```

---

## 7. Advantages and Disadvantages

### Advantages:

| Advantage | Explanation |
|-----------|-------------|
| **O(1) random access** | Directly compute address from index |
| **Cache-friendly** | Contiguous memory → excellent spatial locality |
| **Memory efficient** | No overhead for pointers (unlike linked lists) |
| **Simple** | Easy to understand and implement |
| **Predictable** | Fixed size means predictable memory usage |

### Disadvantages:

| Disadvantage | Explanation |
|--------------|-------------|
| **Fixed size** (static) | Can't grow/shrink after creation |
| **O(N) insert/delete** | Must shift elements |
| **Memory waste** | May allocate more than needed |
| **Contiguous requirement** | Large arrays may fail to allocate |
| **No gap tolerance** | No "holes" allowed — must be compact |

### Cache Locality (Why Arrays Are Fast):

```
CPU Cache Line (64 bytes = 16 ints):

When CPU reads array[0], it loads array[0..15] into cache.
Accessing array[1], array[2], ... is a CACHE HIT (super fast).

Linked List: nodes scattered in memory → CACHE MISS on every access.

This is why arrays are faster than linked lists even for sequential access.
```

---

## 8. When to Use Arrays

### Use Arrays When:
- ✅ You need **random access** by index
- ✅ Size is **known and fixed** (or mostly fixed)
- ✅ You need **cache-efficient** sequential access
- ✅ Memory is **constrained** (no pointer overhead)
- ✅ You do lots of **searching** (binary search needs sorted array)

### Don't Use Arrays When:
- ❌ You need frequent **insertion/deletion in the middle**
- ❌ Size is **unpredictable** and varies wildly
- ❌ You need to store **different data types** (use objects)
- ❌ You need to **insert at front frequently** (use linked list or deque)

---

## 9. Common Array Interview Patterns

| Pattern | Description | Example Problem |
|---------|-------------|-----------------|
| **Two Pointer** | Two indices moving towards each other | Two Sum (sorted), 3Sum |
| **Sliding Window** | Subarray with dynamic boundaries | Longest substring without repeating |
| **Prefix Sum** | Precompute cumulative sums | Range sum query, Subarray sum = K |
| **Kadane's Algorithm** | Max/min subarray | Maximum subarray sum |
| **In-place modification** | Modify without extra space | Remove duplicates, rotate array |
| **Binary Search** | O(log N) search on sorted array | Search in rotated sorted array |
| **Dutch National Flag** | 3-way partitioning | Sort colors (0,1,2) |

---

## 10. Array vs Linked List — Comparison

| Aspect | Array | Linked List |
|--------|-------|-------------|
| Memory | Contiguous | Scattered (nodes + pointers) |
| Access by index | **O(1)** | O(N) |
| Insert at end | O(1) amortized | **O(1)** (if tail pointer) |
| Insert at beginning | O(N) | **O(1)** |
| Insert at middle | O(N) | **O(1)** (if node known) |
| Memory overhead | None (just data) | Extra pointer per node |
| Cache performance | **Excellent** | Poor |
| Search (unsorted) | O(N) | O(N) |
| Search (sorted) | **O(log N)** | O(N) |
| Resizing | O(N) (copy) | **O(1)** (just add node) |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Next: Linked Lists →](./02_Linked_Lists.md)
