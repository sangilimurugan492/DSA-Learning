# Data Structure Theory: Linked Lists

> **In-depth theory, diagrams, and implementation details for understanding linked lists at a fundamental level.**

---

## 1. What is a Linked List?

A linked list is a **linear data structure** where elements (nodes) are connected via **pointers/references**. Unlike arrays, nodes are **not stored in contiguous memory** — each node contains data and a reference to the next node.

```
Singly Linked List:

    HEAD                                              TAIL
     ↓                                                 ↓
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ data: 10 │───→│ data: 20 │───→│ data: 30 │───→│ data: 40 │───→ NULL
│ next: ───┘    │ next: ───┘    │ next: ───┘    │ next: ───┘
└──────────┘    └──────────┘    └──────────┘    └──────────┘
  Node 1          Node 2          Node 3          Node 4

Memory Layout (NOT contiguous):
0x2A0: Node1     0x5F8: Node2     0x1B4: Node3     0x7C2: Node4
```

### Key Properties:
- **Non-contiguous memory**: Nodes scattered in heap
- **Dynamic size**: Grow/shrink at runtime
- **Pointer-based**: Each node has data + pointer(s)
- **Sequential access only**: No random access (must traverse from head)
- **No memory waste**: Only allocate what you need

---

## 2. Node Structure

### Singly Linked List Node:

```
┌──────────┐
│  data    │   ← The actual value stored
├──────────┤
│  next    │   ← Pointer to the next node (or NULL)
└──────────┘
```

```kotlin
class ListNode<T>(var data: T) {
    var next: ListNode<T>? = null
}
```

### Doubly Linked List Node:

```
┌──────────┐
│  prev    │   ← Pointer to previous node (or NULL)
├──────────┤
│  data    │   ← The actual value stored
├──────────┤
│  next    │   ← Pointer to next node (or NULL)
└──────────┘
```

```kotlin
class DoublyListNode<T>(var data: T) {
    var prev: DoublyListNode<T>? = null
    var next: DoublyListNode<T>? = null
}
```

---

## 3. Types of Linked Lists

### 3.1 Singly Linked List (SLL)

```
HEAD → [10|•] → [20|•] → [30|•] → [40|•] → NULL

Traversal: Only forward (HEAD → TAIL)
```

### 3.2 Doubly Linked List (DLL)

```
NULL ← [•|10|•] ←→ [•|20|•] ←→ [•|30|•] ←→ [•|40|•] → NULL
       HEAD                                        TAIL

Traversal: Both forward and backward
```

### 3.3 Circular Linked List (CLL)

```
    ┌──────────────────────────────────┐
    ↓                                  │
   [10|•] → [20|•] → [30|•] → [40|•]──┘
    ↑                                  
   HEAD                                

Last node's next points back to HEAD (not NULL)
```

### 3.4 Circular Doubly Linked List

```
    ┌─────────────────────────────────────────────────────┐
    ↓                                                     │
   [•|10|•] ←→ [•|20|•] ←→ [•|30|•] ←→ [•|40|•]         │
    ↑                                                     │
    └─────────────────────────────────────────────────────┘

    HEAD → Tail.next = Head, Head.prev = Tail
    Both directions form a circle
```

---

## 4. Operations and Time Complexity

### Summary Table

| Operation | Singly LL | Doubly LL | Notes |
|-----------|-----------|-----------|-------|
| Access by index | **O(N)** | **O(N)** | Must traverse from head |
| Search by value | **O(N)** | **O(N)** | Linear scan |
| Insert at head | **O(1)** | **O(1)** | Just update head pointer |
| Insert at tail | O(N)* / **O(1)**† | **O(1)** | *No tail ptr, †with tail ptr |
| Insert at middle | **O(1)**‡ | **O(1)**‡ | ‡If node reference known |
| Delete at head | **O(1)** | **O(1)** | Update head pointer |
| Delete at tail | O(N)* / **O(1)**† | **O(1)** | DLL: use prev pointer |
| Delete by value | **O(N)** | **O(N)** | Must find node first |
| Detect cycle | **O(N)** | **O(N)** | Floyd's algorithm |

### Visual: Insert at Head (O(1))

```
BEFORE:
HEAD → [10|•] → [20|•] → [30|•] → NULL

Insert 5 at head:
Step 1: Create new node
        [5|•]
Step 2: New node's next = old head
        [5|•] → [10|•] → [20|•] → [30|•] → NULL
Step 3: Update head
HEAD → [5|•] → [10|•] → [20|•] → [30|•] → NULL

Cost: 2 pointer operations → O(1)
```

### Visual: Insert at Tail (O(1) with tail pointer)

```
BEFORE:
HEAD → [10|•] → [20|•] → [30|•] → NULL
                                   ↑
                                  TAIL

Insert 40 at tail:
Step 1: Create new node [40|•]
Step 2: Tail.next = new node
        [10|•] → [20|•] → [30|•] → [40|•] → NULL
Step 3: Update tail to new node
        [10|•] → [20|•] → [30|•] → [40|•] → NULL
                                      ↑
                                     TAIL

Cost: 2 pointer operations → O(1) (with tail pointer)
```

### Visual: Delete from Middle (O(N) to find + O(1) to delete)

```
BEFORE:
HEAD → [10|•] → [20|•] → [30|•] → [40|•] → NULL

Delete node with value 30:
Step 1: Traverse to find node BEFORE 30 (i.e., node 20)
Step 2: Bypass node 30
        [10|•] → [20|•]  →  [40|•] → NULL
                      ↑          ↑
                    prev        next
        Set prev.next = next (skip node 30)
Step 3: Node 30 is garbage collected

Result: [10|•] → [20|•] → [40|•] → NULL

Cost: O(N) to find + O(1) to delete
```

### Visual: DLL Delete (O(1) if node reference known)

```
BEFORE:
NULL ← [•|10|•] ←→ [•|20|•] ←→ [•|30|•] ←→ [•|40|•] → NULL

Delete node 30 (we have direct reference):
Step 1: node30.prev.next = node30.next
        [•|20|•].next → [•|40|•]
Step 2: node30.next.prev = node30.prev
        [•|40|•].prev → [•|20|•]

Result: NULL ← [•|10|•] ←→ [•|20|•] ←→ [•|40|•] → NULL

Cost: 2 pointer operations → O(1) (no traversal needed!)
```

---

## 5. Implementation (Kotlin)

### 5.1 Singly Linked List

```kotlin
class SinglyLinkedList<T> {
    private var head: ListNode<T>? = null
    private var tail: ListNode<T>? = null
    private var size = 0

    // O(1) - Insert at head
    fun insertAtHead(data: T) {
        val newNode = ListNode(data)
        newNode.next = head
        head = newNode
        if (tail == null) tail = head
        size++
    }

    // O(1) - Insert at tail (with tail pointer)
    fun insertAtTail(data: T) {
        val newNode = ListNode(data)
        if (tail == null) {
            head = newNode
            tail = newNode
        } else {
            tail!!.next = newNode
            tail = newNode
        }
        size++
    }

    // O(N) - Insert at specific index
    fun insertAt(index: Int, data: T) {
        require(index in 0..size) { "Index out of bounds" }
        if (index == 0) { insertAtHead(data); return }
        if (index == size) { insertAtTail(data); return }

        val newNode = ListNode(data)
        var current = head
        for (i in 0 until index - 1) {
            current = current!!.next
        }
        newNode.next = current!!.next
        current.next = newNode
        size++
    }

    // O(1) - Delete at head
    fun deleteAtHead(): T? {
        if (head == null) return null
        val data = head!!.data
        head = head!!.next
        if (head == null) tail = null
        size--
        return data
    }

    // O(N) - Delete at tail (SLL can't do O(1) - must find prev)
    fun deleteAtTail(): T? {
        if (head == null) return null
        if (head!!.next == null) {
            val data = head!!.data
            head = null
            tail = null
            size--
            return data
        }
        var current = head
        while (current!!.next != tail) {
            current = current.next
        }
        val data = tail!!.data
        current.next = null
        tail = current
        size--
        return data
    }

    // O(N) - Search
    fun search(data: T): Int {
        var current = head
        var index = 0
        while (current != null) {
            if (current.data == data) return index
            current = current.next
            index++
        }
        return -1
    }

    // O(N) - Reverse (iterative)
    fun reverse() {
        var prev: ListNode<T>? = null
        var current = head
        tail = head
        while (current != null) {
            val next = current.next
            current.next = prev
            prev = current
            current = next
        }
        head = prev
    }

    fun size(): Int = size
    fun isEmpty(): Boolean = size == 0
}
```

### 5.2 Doubly Linked List

```kotlin
class DoublyLinkedList<T> {
    private var head: DoublyListNode<T>? = null
    private var tail: DoublyListNode<T>? = null
    private var size = 0

    fun insertAtHead(data: T) {
        val newNode = DoublyListNode(data)
        newNode.next = head
        head?.prev = newNode
        head = newNode
        if (tail == null) tail = newNode
        size++
    }

    fun insertAtTail(data: T) {
        val newNode = DoublyListNode(data)
        newNode.prev = tail
        tail?.next = newNode
        tail = newNode
        if (head == null) head = newNode
        size++
    }

    // O(1) - Delete given node reference (DLL advantage!)
    fun deleteNode(node: DoublyListNode<T>) {
        node.prev?.next = node.next
        node.next?.prev = node.prev
        if (node == head) head = node.next
        if (node == tail) tail = node.prev
        size--
    }

    // O(N) - Delete by value
    fun deleteByValue(data: T): Boolean {
        var current = head
        while (current != null) {
            if (current.data == data) {
                deleteNode(current)
                return true
            }
            current = current.next
        }
        return false
    }
}
```

---

## 6. Key Algorithms

### 6.1 Floyd's Cycle Detection (Tortoise & Hare)

```
Detect if linked list has a cycle:

    ┌─────────────────────┐
    ↓                     │
   [1] → [2] → [3] → [4] ─┘
         ↑     
       slow (1x)
       fast (2x)

Slow moves 1 step, Fast moves 2 steps.
If they meet → cycle exists.
If fast reaches NULL → no cycle.

Why it works: In a cycle, fast gains 1 step per iteration on slow.
They MUST meet eventually (gap closes by 1 each step).
```

```kotlin
fun hasCycle(head: ListNode<Int>?): Boolean {
    var slow = head
    var fast = head
    while (fast != null && fast.next != null) {
        slow = slow!!.next
        fast = fast.next!!.next
        if (slow === fast) return true
    }
    return false
}
```

### 6.2 Find Middle Node

```
Same tortoise & hare approach:
- Fast reaches end → slow is at middle

[1] → [2] → [3] → [4] → [5] → NULL
            ↑
          middle (slow)

For even length: slow ends at first middle
[1] → [2] → [3] → [4] → NULL
      ↑
    middle (slow)
```

### 6.3 Reverse a Linked List (Iterative)

```
BEFORE: 1 → 2 → 3 → 4 → NULL

Step by step:
prev=NULL  cur=1    1 → 2 → 3 → 4 → NULL
           ↓
prev=NULL  cur=NULL  NULL ← 1    2 → 3 → 4 → NULL
                      ↑ prev   ↑ cur

prev=1     cur=2     NULL ← 1 ← 2    3 → 4 → NULL
                      ↑ prev   ↑ cur

prev=2     cur=3     NULL ← 1 ← 2 ← 3    4 → NULL
                               ↑ prev  ↑ cur

prev=3     cur=4     NULL ← 1 ← 2 ← 3 ← 4    NULL
                                    ↑ prev  ↑ cur

AFTER:  NULL ← 1 ← 2 ← 3 ← 4
                          ↑ HEAD
```

### 6.4 Merge Two Sorted Lists

```
List1: 1 → 3 → 5 → NULL
List2: 2 → 4 → 6 → NULL

Compare heads, pick smaller:
1 < 2 → pick 1
3 > 2 → pick 2
3 < 4 → pick 3
5 > 4 → pick 4
5 < 6 → pick 5
(append remaining) → pick 6

Result: 1 → 2 → 3 → 4 → 5 → 6 → NULL
```

---

## 7. Memory Representation

### How Nodes Are Allocated:

```
Heap Memory (each node allocated separately):

Address  │ Node
─────────┼──────────────────────
0x1000   │ [data=10, next=0x2A00]
0x2A00   │ [data=20, next=0x1B40]
0x1B40   │ [data=30, next=0x7C20]
0x7C20   │ [data=40, next=NULL]

HEAD = 0x1000

Note: Addresses are NOT sequential!
Each `new` allocates wherever the heap has space.

Memory overhead per node:
- Data: sizeof(T)
- Pointer: 8 bytes (64-bit) or 4 bytes (32-bit)
- DLL: 2 × pointer overhead
```

### Comparison: Array vs Linked List Memory:

```
Array (6 ints):          Linked List (6 nodes):
                         (each node = int + pointer)
┌────┬────┬────┐         
│ 10 │ 20 │ 30 │         [10|•] → [20|•] → [30|•] → ...
└────┴────┴────┘
12 bytes total            6 × (4 + 8) = 72 bytes total
(3 × 4 bytes)             (3 × 12 bytes per node)

Array: 12 bytes           LL: 72 bytes (6x more memory!)
```

---

## 8. Advantages and Disadvantages

### Advantages:

| Advantage | Explanation |
|-----------|-------------|
| **Dynamic size** | Grow/shrink at runtime, no resizing |
| **O(1) insert/delete at ends** | Just update pointers |
| **No memory waste** | Allocate exactly what you need |
| **No shifting** | Insert/delete don't require moving other elements |
| **Efficient for unknown sizes** | No need to pre-allocate |

### Disadvantages:

| Disadvantage | Explanation |
|--------------|-------------|
| **No random access** | O(N) to access element at index i |
| **Extra memory** | Pointer overhead per node |
| **Poor cache locality** | Nodes scattered in memory → cache misses |
| **No binary search** | Can't do O(log N) search (no random access) |
| **Reverse traversal** | SLL can't go backward (DLL can, but uses more memory) |
| **Fragmentation** | Many small allocations can fragment heap |

---

## 9. When to Use Linked Lists

### Use Linked Lists When:
- ✅ Frequent **insertion/deletion at beginning** or end
- ✅ Size is **unknown** or changes frequently
- ✅ You need to **insert/delete in the middle** (if you have node references)
- ✅ You don't need **random access**
- ✅ Implementing **other data structures** (stacks, queues, hash table chaining)

### Don't Use Linked Lists When:
- ❌ You need **random access** by index
- ❌ You need **binary search**
- ❌ **Memory is tight** (pointer overhead is significant)
- ❌ **Cache performance** is critical
- ❌ You mostly **read/scan sequentially** (arrays are faster due to cache)

---

## 10. Real-World Applications

| Application | How Linked Lists Are Used |
|-------------|--------------------------|
| **LRU Cache** | Doubly LL for O(1) eviction; move accessed node to front |
| **Music playlist** | DLL for next/previous song navigation |
| **Browser history** | DLL for back/forward navigation |
| **Undo/Redo** | DLL of actions; traverse back (undo) or forward (redo) |
| **Hash table chaining** | SLL for collision resolution (separate chaining) |
| **Memory management** | OS free list of memory blocks (linked list) |
| **Polynomial representation** | Each term is a node (coefficient + exponent) |
| **Implementing stacks/queues** | LL-backed stack/queue with O(1) push/pop |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Arrays →](./01_Arrays.md)
- [Next: Stacks →](./03_Stacks.md)
