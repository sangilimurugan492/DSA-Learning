# Data Structure Theory: Heaps / Priority Queues

> **In-depth theory, diagrams, and implementation details for understanding heaps at a fundamental level.**

---

## 1. What is a Heap?

A heap is a **specialized binary tree** that satisfies the **heap property**. It's the most common implementation of a **Priority Queue** — a data structure where the element with the highest (or lowest) priority is always at the root.

```
Max-Heap (parent ≥ children):

            100
           /    \
         50      80
        /  \    /  \
      30   20  60   70

Every parent ≥ its children.
Root = MAXIMUM element (100).

Min-Heap (parent ≤ children):

            10
           /    \
         20      30
        /  \    /  \
      40   50  60   70

Every parent ≤ its children.
Root = MINIMUM element (10).
```

### Key Properties:
- **Complete binary tree**: All levels filled except possibly last (filled left-to-right)
- **Heap property**: Parent ≥ children (max-heap) or parent ≤ children (min-heap)
- **No ordering between siblings**: Only parent-child relationship is constrained
- **Array-based**: Heaps are stored in arrays (no pointers needed!)
- **NOT a BST**: No relationship between left and right subtrees

### Heap vs BST:

```
BST: In-order = sorted. Heap: No sorted traversal.

BST:        50           Heap:       100
           /  \                      /    \
         30    70                   50      80
        / \   / \                  /  \    /  \
      20  40 60 80               30   20  60  70

BST: left < node < right (searchable in O(log N))
Heap: parent ≥ children (only root is guaranteed max/min)

BST search: O(log N)    Heap search: O(N) (no ordering between siblings!)
BST insert: O(log N)    Heap insert: O(log N)
BST delete: O(log N)    Heap delete: O(log N) (only root, not arbitrary)
```

---

## 2. Array Representation of Heap

Since a heap is a **complete binary tree**, it can be stored in an array without pointers.

```
Heap:          Array Index:
    100         [0] = 100
   /    \
  50     80     [1] = 50    [2] = 80
 / \    / \
30 20  60 70    [3] = 30   [4] = 20   [5] = 60   [6] = 70

Array: [100, 50, 80, 30, 20, 60, 70]

Parent/Child Index Relationship:
  Parent of node at index i:     (i - 1) / 2
  Left child of node at index i:  2 * i + 1
  Right child of node at index i: 2 * i + 2

Example: Node at index 3 (value=30)
  Parent:  (3-1)/2 = 1 → index 1 (value=50) ✓
  Left:    2*3+1 = 7  → index 7 (no child)
  Right:   2*3+2 = 8  → index 8 (no child)
```

### Why Array Representation Works:

```
Complete binary tree → no "gaps" in array:

Tree:          Array:
    A            [A, B, C, D, E, F, G]
   / \
  B   C         No wasted space. Every index is used.
 / \ / \        Children of index i: 2i+1, 2i+2
D  E F G        Parent of index i: (i-1)/2

If tree is NOT complete → array wastes space (gaps for missing nodes)
Heaps are ALWAYS complete → array is always efficient
```

---

## 3. Heap Operations

### 3.1 Insert (Sift Up / Heapify Up)

Add new element at the **end** of the array, then **bubble up** until heap property is restored.

```
Max-Heap Insert: insert 90

BEFORE:
Array: [100, 50, 80, 30, 20, 60, 70]
Tree:       100
           /    \
         50      80
        /  \    /  \
      30   20  60   70

Step 1: Add 90 at end
Array: [100, 50, 80, 30, 20, 60, 70, 90]
Tree:       100
           /    \
         50      80
        /  \    /  \
      30   20  60   70
     /
   90                        ← New element at index 7

Step 2: Compare 90 with parent (index 3, value=30). 90 > 30 → SWAP
Array: [100, 50, 80, 90, 20, 60, 70, 30]
Tree:       100
           /    \
         50      80
        /  \    /  \
      90   20  60   70
     /
   30

Step 3: Compare 90 with parent (index 1, value=50). 90 > 50 → SWAP
Array: [100, 90, 80, 50, 20, 60, 70, 30]
Tree:       100
           /    \
         90      80
        /  \    /  \
      50   20  60   70
     /
   30

Step 4: Compare 90 with parent (index 0, value=100). 90 < 100 → STOP

Done! Heap property restored. Time: O(log N)
```

### 3.2 Extract Max/Min (Sift Down / Heapify Down)

Remove the **root** (max/min), move last element to root, then **bubble down**.

```
Max-Heap Extract Max: remove 100

BEFORE:
Array: [100, 90, 80, 50, 20, 60, 70, 30]
Tree:       100          ← Remove this
           /    \
         90      80
        /  \    /  \
      50   20  60   70
     /
   30

Step 1: Save root (100), move last element (30) to root
Array: [30, 90, 80, 50, 20, 60, 70, _]
Tree:        30          ← Wrong! 30 < children
           /    \
         90      80
        /  \    /  \
      50   20  60   70

Step 2: Sift down. Compare 30 with children (90, 80). Larger = 90. 30 < 90 → SWAP with 90
Array: [90, 30, 80, 50, 20, 60, 70]
Tree:        90
           /    \
         30      80
        /  \    /  \
      50   20  60   70

Step 3: Compare 30 with children (50, 20). Larger = 50. 30 < 50 → SWAP with 50
Array: [90, 50, 80, 30, 20, 60, 70]
Tree:        90
           /    \
         50      80
        /  \    /  \
      30   20  60   70

Step 4: 30 has no children (index 3, left=7, right=8 — out of bounds). STOP.

Return 100. Heap property restored. Time: O(log N)
```

### 3.3 Heapify (Build Heap from Array)

Convert an arbitrary array into a heap in O(N) time.

```
Array: [3, 1, 6, 5, 2, 4]

Step 1: Start from last non-leaf parent (index (N/2)-1 = 2)
        Sift down index 2: [3, 1, 6, 5, 2, 4] → 6>4 OK → no change
Step 2: Sift down index 1: [3, 1, 6, 5, 2, 4] → 1<5, 1<2, swap with 5
                                  [3, 5, 6, 1, 2, 4]
Step 3: Sift down index 0: [3, 5, 6, 1, 2, 4] → 3<6, swap with 6
                                  [6, 5, 3, 1, 2, 4]
        Sift down 3: 3<4, swap with 4
                                  [6, 5, 4, 1, 2, 3]

Result (max-heap): [6, 5, 4, 1, 2, 3]

Time: O(N) — not O(N log N)! Lower nodes sift less.
```

---

## 4. Operations and Time Complexity

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| **insert** | **O(log N)** | O(1) | Sift up |
| **extractMax/Min** | **O(log N)** | O(1) | Sift down |
| **peek (get max/min)** | **O(1)** | O(1) | Just look at root |
| **heapify (build)** | **O(N)** | O(1) | Build from array |
| **search** | O(N) | O(1) | No ordering between siblings |
| **delete (arbitrary)** | O(N) | O(1) | Find (O(N)) + sift (O(log N)) |
| **merge two heaps** | O(N + M) | O(N+M) | Or O(log N log M) with meldable heap |

---

## 5. Implementation (Kotlin)

### Min-Heap (Array-based):

```kotlin
class MinHeap {
    private val heap = mutableListOf<Int>()

    fun insert(value: Int) {
        heap.add(value)
        siftUp(heap.size - 1)
    }

    private fun siftUp(index: Int) {
        var i = index
        while (i > 0) {
            val parent = (i - 1) / 2
            if (heap[i] < heap[parent]) {
                swap(i, parent)
                i = parent
            } else break
        }
    }

    fun extractMin(): Int? {
        if (heap.isEmpty()) return null
        val min = heap[0]
        val last = heap.removeAt(heap.size - 1)
        if (heap.isNotEmpty()) {
            heap[0] = last
            siftDown(0)
        }
        return min
    }

    private fun siftDown(index: Int) {
        var i = index
        while (true) {
            val left = 2 * i + 1
            val right = 2 * i + 2
            var smallest = i

            if (left < heap.size && heap[left] < heap[smallest]) smallest = left
            if (right < heap.size && heap[right] < heap[smallest]) smallest = right

            if (smallest != i) {
                swap(i, smallest)
                i = smallest
            } else break
        }
    }

    fun peek(): Int? = heap.firstOrNull()
    fun size(): Int = heap.size
    fun isEmpty(): Boolean = heap.isEmpty()

    private fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
    }
}
```

---

## 6. Key Heap Patterns (Interview Critical)

### 6.1 "Top K" Problems (Min-Heap of Size K)

```
Problem: Find K largest elements in array [3, 1, 4, 1, 5, 9, 2, 6], K=3

Min-heap of size K=3:
Insert 3: [3]
Insert 1: [1, 3]
Insert 4: [1, 3, 4]
Insert 1: heap full, 1 > min(1)? No, skip
Insert 5: heap full, 5 > min(1)? Yes, remove 1, insert 5 → [3, 4, 5]
Insert 9: 9 > min(3)? Yes, remove 3, insert 9 → [4, 5, 9]
Insert 2: 2 > min(4)? No, skip
Insert 6: 6 > min(4)? Yes, remove 4, insert 6 → [5, 6, 9]

Result: [5, 6, 9] — the K=3 largest elements

Time: O(N log K) — much better than O(N log N) sort!
```

### 6.2 "Kth Largest/Smallest" (Min-Heap or QuickSelect)

```
Problem: Find 3rd largest in [3, 1, 4, 1, 5, 9, 2, 6]

Option 1: Min-heap of size K=3
→ After processing all, root = 3rd largest = 5

Option 2: Max-heap, extract K times
→ Extract max: 9, Extract max: 6, Extract max: 5 → Answer = 5

Option 3: QuickSelect (O(N) average, no heap needed)
```

### 6.3 "Merge K Sorted Lists" (K-way Merge)

```
Problem: Merge K sorted linked lists into one sorted list.

Min-heap approach:
1. Push first element of each list into min-heap
2. Pop minimum → add to result
3. Push next element from same list
4. Repeat until heap empty

Time: O(N log K) where N = total elements, K = number of lists
```

### 6.4 "Find Median from Data Stream" (Two Heaps)

```
Problem: Continuously find median of streaming numbers.

Two heaps:
- Max-heap: stores smaller half (root = largest of small half)
- Min-heap: stores larger half (root = smallest of large half)

Balance: max-heap.size = min-heap.size OR max-heap.size = min-heap.size + 1

Insert 5:  max-heap: [5]         min-heap: []
Insert 2:  max-heap: [2]         min-heap: [5]
Insert 8:  max-heap: [5, 2]      min-heap: [8]
Insert 1:  max-heap: [2, 1]      min-heap: [5, 8]
Insert 9:  max-heap: [5, 2, 1]   min-heap: [8, 9]

Median:
  - If sizes equal: (max-heap.root + min-heap.root) / 2
  - If max-heap larger: max-heap.root

For [5,2,8,1,9]: sorted=[1,2,5,8,9], median=5 (max-heap.root) ✓

Insert: O(log N), Find median: O(1)
```

---

## 7. Advantages and Disadvantages

### Advantages:
- **O(1) peek**: Always know max/min instantly
- **O(log N) insert/extract**: Efficient for priority-based operations
- **O(N) build**: Heapify an entire array in linear time
- **Array-based**: No pointers, cache-friendly
- **Space efficient**: O(N) with no overhead

### Disadvantages:
- **No random access**: Can't efficiently find arbitrary element (O(N))
- **No sorted order**: In-order traversal doesn't give sorted data
- **Only root is accessible**: Only max/min, not 2nd or 3rd
- **Not good for search**: O(N) to find arbitrary element

---

## 8. When to Use Heaps

### Use Heaps When:
- ✅ You need **repeated min/max** access
- ✅ **Top K** elements from a stream
- ✅ **Kth largest/smallest** element
- ✅ **Merge K sorted** sources
- ✅ **Running median** (two heaps)
- ✅ **Priority scheduling** (task scheduler, Dijkstra)
- ✅ **Event-driven simulation** (process by priority/time)

---

## 9. Real-World Applications

| Application | How Heap Is Used |
|-------------|-----------------|
| **Priority queue** | OS task scheduling (highest priority first) |
| **Dijkstra's algorithm** | Min-heap for shortest path (next closest vertex) |
| **Prim's MST** | Min-heap for cheapest edge |
| **Heap sort** | Heapify + extract all → O(N log N) sort |
| **Top K problems** | Min-heap of size K |
| **Median stream** | Two heaps (max + min) |
| **Task scheduler** | Max-heap by priority |
| **Huffman coding** | Min-heap for frequency-based tree building |
| **A* search** | Priority queue for optimal path finding |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Graphs →](./07_Graphs.md)
- [Next: Tries →](./09_Tries.md)
