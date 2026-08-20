# Data Structure Theory: Queues

> **In-depth theory, diagrams, and implementation details for understanding queues at a fundamental level.**

---

## 1. What is a Queue?

A queue is a **FIFO (First-In-First-Out)** data structure. The first element added is the first one removed — like a line at a ticket counter.

```
   Enqueue (add)                              Dequeue (remove)
       ↓                                           ↑
     ┌────┬────┬────┬────┬────┐                  
     │ 10 │ 20 │ 30 │ 40 │ 50 │
     └────┴────┴────┴────┴────┘
     ↑                         ↑
    FRONT (dequeue here)      REAR (enqueue here)

Order added: 10 → 20 → 30 → 40 → 50
Order removed: 10 → 20 → 30 → 40 → 50 (SAME ORDER!)
```

### Key Properties:
- **FIFO**: First-In-First-Out ordering
- **Two access points**: Front (dequeue) and Rear (enqueue)
- **Restricted operations**: enqueue, dequeue, peek, isEmpty
- **No random access**: Can't access middle elements

---

## 2. Types of Queues

### 2.1 Simple Queue (Linear Queue)

```
Enqueue → [10, 20, 30, 40] → Dequeue
          ↑                ↑
        FRONT             REAR
```

### 2.2 Circular Queue (Ring Buffer)

```
When REAR reaches end, it wraps around to the beginning:

Array: [_ , 20, 30, 40, 10]
        ↑              ↑
       REAR           FRONT

Next enqueue goes to index 0 (wraps around!)
Solves the "empty slots at front" problem of linear queues.

     ┌──────────────────────────┐
     ↓                          │
  [0]  [1]  [2]  [3]  [4]
  10   20   30   40   50
  ↑                   ↑
 REAR               FRONT
```

### 2.3 Deque (Double-Ended Queue)

```
Insert/Delete from BOTH ends:

  ←──────────────────────────→
  [10, 20, 30, 40, 50]
  ↑                   ↑
 FRONT               REAR

Operations:
  - addFirst(x)  → insert at front
  - addLast(x)   → insert at rear
  - removeFirst() → remove from front
  - removeLast()  → remove from rear

Deque can be used as BOTH stack AND queue!
```

### 2.4 Priority Queue

```
Elements have priorities. Dequeue returns HIGHEST priority element.

Insert: (B,2), (A,1), (D,4), (C,3)

Internal (min-heap):
        (A,1)
       /      \
    (B,2)     (C,3)
    /
  (D,4)

Dequeue order by priority: A(1) → B(2) → C(3) → D(4)
NOT FIFO — priority determines order.
```

---

## 3. Operations and Time Complexity

| Operation | Array Queue | Linked List Queue | Circular Queue | Deque |
|-----------|-------------|------------------|----------------|-------|
| **enqueue** | O(1) | **O(1)** | **O(1)** | **O(1)** |
| **dequeue** | O(N)* | **O(1)** | **O(1)** | **O(1)** |
| **peek/front** | **O(1)** | **O(1)** | **O(1)** | **O(1)** |
| **isEmpty** | **O(1)** | **O(1)** | **O(1)** | **O(1)** |

*Array queue dequeue is O(N) if we shift elements, O(1) if we use front pointer.

### Visual: Enqueue Operation

```
BEFORE:                          enqueue(60):
FRONT → [10, 20, 30, 40, 50] ← REAR    FRONT → [10, 20, 30, 40, 50, 60] ← REAR
                                              ↑ new element at rear
```

### Visual: Dequeue Operation

```
BEFORE:                          dequeue() → returns 10:
FRONT → [10, 20, 30, 40, 50] ← REAR    FRONT → [20, 30, 40, 50] ← REAR
        ↑ removed                                ↑ new front
```

---

## 4. Implementation (Kotlin)

### Array-based Circular Queue:

```kotlin
class CircularQueue<T>(capacity: Int) {
    private val data = Array<Any?>(capacity) { null }
    private var front = 0
    private var rear = 0
    private var size = 0
    private val capacity = capacity

    fun enqueue(item: T): Boolean {
        if (isFull()) return false
        data[rear] = item
        rear = (rear + 1) % capacity  // Wrap around
        size++
        return true
    }

    fun dequeue(): T? {
        if (isEmpty()) return null
        @Suppress("UNCHECKED_CAST")
        val item = data[front] as T
        data[front] = null
        front = (front + 1) % capacity  // Wrap around
        size--
        return item
    }

    fun peek(): T? {
        if (isEmpty()) return null
        @Suppress("UNCHECKED_CAST")
        return data[front] as T
    }

    fun isEmpty(): Boolean = size == 0
    fun isFull(): Boolean = size == capacity
    fun size(): Int = size
}
```

### Linked List Queue:

```kotlin
class LinkedQueue<T> {
    private var front: ListNode<T>? = null
    private var rear: ListNode<T>? = null
    private var size = 0

    fun enqueue(item: T) {
        val newNode = ListNode(item)
        if (rear == null) {
            front = newNode
            rear = newNode
        } else {
            rear!!.next = newNode
            rear = newNode
        }
        size++
    }

    fun dequeue(): T? {
        if (front == null) return null
        val data = front!!.data
        front = front!!.next
        if (front == null) rear = null
        size--
        return data
    }

    fun peek(): T? = front?.data
    fun isEmpty(): Boolean = front == null
    fun size(): Int = size
}
```

---

## 5. Queue vs Stack Comparison

| Aspect | Queue | Stack |
|--------|-------|-------|
| Order | FIFO (First-In-First-Out) | LIFO (Last-In-First-Out) |
| Insert | At rear (enqueue) | At top (push) |
| Remove | From front (dequeue) | From top (pop) |
| Access points | 2 (front, rear) | 1 (top) |
| Use case | BFS, scheduling | DFS, undo, matching |
| Real-world | Line at store | Stack of plates |

---

## 6. Key Queue Patterns

### 6.1 BFS (Breadth-First Search)

```
BFS explores nodes level by level using a queue:

Tree:       1
          /   \
         2     3
        / \   / \
       4   5 6   7

Queue: [1] → dequeue 1, enqueue 2,3 → [2,3]
       → dequeue 2, enqueue 4,5 → [3,4,5]
       → dequeue 3, enqueue 6,7 → [4,5,6,7]
       → dequeue 4 → [5,6,7]
       → ... continue until empty

Visit order: 1, 2, 3, 4, 5, 6, 7 (level by level)
```

### 6.2 Level-Order Traversal

```
Same as BFS — process tree level by level.
Queue stores (node, level) pairs.
```

### 6.3 Sliding Window Maximum (Monotonic Deque)

```
Array: [1, 3, -1, -3, 5, 3, 6, 7], k=3
Window positions and max:
[1, 3, -1] → 3
[3, -1, -3] → 3
[-1, -3, 5] → 5
[-3, 5, 3] → 5
[5, 3, 6] → 6
[3, 6, 7] → 7

Use DECREASING deque (front = max):
- Remove from back while back < current
- Add current to back
- Remove from front if out of window
- Front of deque = window max
```

---

## 7. Advantages and Disadvantages

### Advantages:
- **O(1) operations**: Enqueue, dequeue, peek all constant time
- **Natural ordering**: FIFO matches real-world scenarios
- **BFS foundation**: Essential for graph/tree level traversal
- **Scheduling**: Perfect for task scheduling, buffering

### Disadvantages:
- **No random access**: Can't access middle elements
- **Array waste**: Linear queue wastes space at front (solved by circular)
- **Not searchable efficiently**: O(N) to find an element

---

## 8. When to Use Queues

### Use Queues When:
- ✅ You need **FIFO** ordering
- ✅ **BFS** traversal (graphs, trees)
- ✅ **Task scheduling** (print queue, CPU scheduling)
- ✅ **Buffering** (message queues, IO buffers)
- ✅ **Level-order** tree traversal
- ✅ **Producer-Consumer** pattern
- ✅ **Sliding window** algorithms (with deque)

---

## 9. Real-World Applications

| Application | How Queue Is Used |
|-------------|-----------------|
| **Print queue** | First document submitted = first printed |
| **CPU scheduling** | Processes wait in ready queue |
| **BFS** | Level-by-level graph/tree traversal |
| **Message queues** | RabbitMQ, Kafka — producer/consumer |
| **Buffer** | Keyboard buffer, IO buffer |
| **Call center** | First caller = first served |
| **Web server** | Request queue for incoming HTTP requests |
| **OS scheduling** | Round-robin scheduling uses circular queue |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Stacks →](./03_Stacks.md)
- [Next: Hash Tables →](./05_Hash_Tables.md)
