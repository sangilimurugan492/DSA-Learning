# Data Structure Theory: Stacks

> **In-depth theory, diagrams, and implementation details for understanding stacks at a fundamental level.**

---

## 1. What is a Stack?

A stack is a **LIFO (Last-In-First-Out)** data structure. The last element added is the first one removed — like a stack of plates.

```
      Push 30          Push 20          Push 10          Pop → 10
      ┌────┐           ┌────┐           ┌────┐           
      │ 30 │           │ 20 │           │ 10 │ ← TOP     │    │
      │    │           │ 30 │           │ 20 │           │ 20 │ ← TOP
      └────┘           └────┘           │ 30 │           │ 30 │
                                       └────┘           └────┘

Order added: 30 → 20 → 10
Order removed: 10 → 20 → 30 (REVERSED!)
```

### Key Properties:
- **LIFO**: Last-In-First-Out ordering
- **Single access point**: Only the top element is accessible
- **Restricted operations**: push, pop, peek, isEmpty
- **No random access**: Can't access middle elements directly

---

## 2. Operations and Time Complexity

| Operation | Time | Space | Description |
|-----------|------|-------|-------------|
| **push(x)** | O(1) | O(1) | Add element to top |
| **pop()** | O(1) | O(1) | Remove and return top element |
| **peek()/top()** | O(1) | O(1) | View top element without removing |
| **isEmpty()** | O(1) | O(1) | Check if stack is empty |
| **size()** | O(1) | O(1) | Get number of elements |
| **search(x)** | O(N) | O(1) | Find element (must pop into temp) |

### Visual: Push Operation

```
BEFORE:                push(40):
┌────┐                 ┌────┐
│ 30 │ ← TOP           │ 40 │ ← NEW TOP
├────┤                 ├────┤
│ 20 │                 │ 30 │
├────┤                 ├────┤
│ 10 │                 │ 20 │
└────┘                 ├────┤
                       │ 10 │
                       └────┘
```

### Visual: Pop Operation

```
BEFORE:                pop() → returns 40:
┌────┐                 ┌────┐
│ 40 │ ← TOP (removed)│ 30 │ ← NEW TOP
├────┤                 ├────┤
│ 30 │                 │ 20 │
├────┤                 ├────┤
│ 20 │                 │ 10 │
├────┤                 └────┘
│ 10 │
└────┘
```

---

## 3. Implementation (Kotlin)

### Array-based Stack (Dynamic Array):

```kotlin
class ArrayStack<T> {
    private val data = mutableListOf<T>()

    fun push(item: T) { data.add(item) }        // O(1) amortized
    fun pop(): T = data.removeAt(data.lastIndex) // O(1)
    fun peek(): T = data.last()                  // O(1)
    fun isEmpty(): Boolean = data.isEmpty()
    fun size(): Int = data.size
}
```

### Linked List-based Stack:

```kotlin
class LinkedStack<T> {
    private var head: ListNode<T>? = null
    private var size = 0

    fun push(item: T) {
        val newNode = ListNode(item)
        newNode.next = head
        head = newNode
        size++
    }

    fun pop(): T {
        require(!isEmpty()) { "Stack is empty" }
        val data = head!!.data
        head = head!!.next
        size--
        return data
    }

    fun peek(): T {
        require(!isEmpty()) { "Stack is empty" }
        return head!!.data
    }

    fun isEmpty(): Boolean = head == null
    fun size(): Int = size
}
```

### Comparison: Array vs Linked List Stack

| Aspect | Array Stack | Linked List Stack |
|--------|-------------|-------------------|
| Push | O(1) amortized | **O(1) always** |
| Pop | **O(1)** | O(1) |
| Memory | Contiguous, cache-friendly | Extra pointer per node |
| Resize | Occasional O(N) | Never resizes |
| Best for | General purpose, cache performance | Guaranteed O(1), no resize |

---

## 4. Key Stack Patterns (Interview Critical)

### 4.1 Matching / Validation (Parentheses)

```
Valid Parentheses: "{[()]()}"

Stack operations:
Char: ( → push → [(]
Char: [ → push → [(, []
Char: ( → push → [(, [, (]
Char: ) → pop ( → [(, []
Char: ] → pop [ → [(]
Char: ( → push → [(, (]
Char: ) → pop ( → [(]
Char: } → pop { → []  ← EMPTY = VALID!

Key: Every closing bracket must match the TOP of stack.
```

### 4.2 Monotonic Stack (Next Greater Element)

```
Problem: For each element, find next greater element to its right.

Array: [2, 1, 2, 4, 3]
Answer: [4, 2, 4, -1, -1]

Stack maintains DECREASING sequence:

i=0, val=2: stack empty → push 2     stack: [2]
i=1, val=1: 1 < 2 → push 1          stack: [2, 1]
i=2, val=2: 2 > 1 → pop 1 (NGE=2)   stack: [2]
             2 >= 2 → pop 2 (NGE=2)  stack: []
             push 2                  stack: [2]
i=3, val=4: 4 > 2 → pop 2 (NGE=4)   stack: []
             push 4                  stack: [4]
i=4, val=3: 3 < 4 → push 3          stack: [4, 3]

Remaining in stack → no NGE → -1
```

### 4.3 Largest Rectangle in Histogram

```
Use monotonic stack to find max rectangular area:

Heights: [2, 1, 5, 6, 2, 3]

For each bar, find:
  - Left boundary: first smaller bar to the left
  - Right boundary: first smaller bar to the right
  - Area = height × (right - left - 1)

Max area = 10 (5×2, bars at index 2-3)
```

### 4.4 Expression Evaluation (RPN)

```
Evaluate: ["2", "1", "+", "3", "*"] → ((2+1)*3) = 9

Stack: []
"2"  → push 2     [2]
"1"  → push 1     [2, 1]
"+"  → pop 1, pop 2, push 2+1=3   [3]
"3"  → push 3     [3, 3]
"*"  → pop 3, pop 3, push 3*3=9   [9]

Result = 9
```

### 4.5 Call Stack / Recursion

```
Recursion implicitly uses a stack:

factorial(4):
  → factorial(3):           Stack: [fact(4)]
    → factorial(2):         Stack: [fact(4), fact(3)]
      → factorial(1):       Stack: [fact(4), fact(3), fact(2)]
        → factorial(0)=1    Stack: [fact(4), fact(3), fact(2), fact(1)]
      ← return 1*1=1        Stack: [fact(4), fact(3), fact(2)]
    ← return 2*1=2          Stack: [fact(4), fact(3)]
  ← return 3*2=6            Stack: [fact(4)]
← return 4*6=24             Stack: []

Stack overflow = too deep recursion (stack full)
```

---

## 5. Advantages and Disadvantages

### Advantages:
- **O(1) operations**: Push, pop, peek are all constant time
- **Simple**: Very easy to implement and understand
- **Memory efficient**: No pointers needed (array-based)
- **Natural for recursion**: Mirrors function call behavior
- **Undo/Redo**: Natural fit for tracking history

### Disadvantages:
- **No random access**: Can only access top element
- **Restricted**: Can't iterate without destroying (pop all)
- **Size limit** (array-based): May need resizing
- **Not searchable efficiently**: O(N) to find an element

---

## 6. When to Use Stacks

### Use Stacks When:
- ✅ You need **LIFO** ordering
- ✅ **Matching/nesting** problems (parentheses, HTML tags)
- ✅ **Next greater/smaller** element (monotonic stack)
- ✅ **Undo/Redo** functionality
- ✅ **Expression evaluation** (postfix, infix → postfix)
- ✅ **Backtracking** (DFS uses stack implicitly)
- ✅ **Call stack** simulation
- ✅ **Browser back/forward** navigation

---

## 7. Real-World Applications

| Application | How Stack Is Used |
|-------------|-----------------|
| **Undo/Redo** | Each action pushed; undo = pop |
| **Browser back** | Push URLs; back = pop |
| **Code editor** | Bracket matching, auto-indent |
| **Compiler** | Syntax checking, expression parsing |
| **OS** | Function call stack, stack frames |
| **DFS traversal** | Stack of nodes to visit |
| **Text editor** | Undo stack of text changes |
| **Call center** | Most recent caller served first |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Linked Lists →](./02_Linked_Lists.md)
- [Next: Queues →](./04_Queues.md)
