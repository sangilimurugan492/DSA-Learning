# Min Stack — Detailed Explanation

> **LeetCode #155** | [Problem Link](https://leetcode.com/problems/min-stack/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 10 most asked design)  
> **Topic:** Stack, Design

---

## 📋 Problem Statement

Design a stack that supports `push`, `pop`, `top`, and `getMin` — all in **O(1)** time.

### Example

```
push(-2), push(0), push(-3) → getMin() = -3
pop() → top() = 0, getMin() = -2
```

---

## 🧩 Method 1: Brute Force — O(N) getMin

### Core Idea

`getMin()` scans the entire stack to find the minimum.

### Complexity

| Operation | Time | Space |
|-----------|------|-------|
| push | O(1) | O(N) |
| pop | O(1) | — |
| top | O(1) | — |
| getMin | **O(N)** | — |

---

## 🧩 Method 2: Auxiliary Stack — O(1) getMin

### Core Idea

Store pairs of `(value, currentMin)` in the stack. Each entry remembers the minimum at the time it was pushed.

### Key Insight

> When pushing, compare new value with current min (top's min). Store `min(new value, current min)` alongside the value. This way, every stack level knows the minimum up to that point.

### Dry Run

| Operation | Stack (value, min) | getMin |
|-----------|---------------------|--------|
| push(-2) | [(-2, -2)] | -2 |
| push(0) | [(-2,-2), (0,-2)] | -2 |
| push(-3) | [(-2,-2), (0,-2), (-3,-3)] | -3 |
| pop() | [(-2,-2), (0,-2)] | -2 |
| top() | [(-2,-2), (0,-2)] → 0 | — |

### Code

```kotlin
class MinStack {
    private val stack = ArrayDeque<Pair<Int, Int>>()  // (value, minAtThisPoint)

    fun push(value: Int) {
        val min = if (stack.isEmpty()) value else minOf(value, stack.last().second)
        stack.addLast(Pair(value, min))
    }

    fun pop() = stack.removeLast()
    fun top(): Int = stack.last().first
    fun getMin(): Int = stack.last().second
}
```

### Complexity

| Operation | Time | Space |
|-----------|------|-------|
| push | O(1) | O(N) |
| pop | O(1) | — |
| top | O(1) | — |
| getMin | **O(1)** | — |

---

## 📊 Comparison Table

| Aspect | Brute Force | Auxiliary Stack |
|--------|-------------|-----------------|
| **getMin** | O(N) | O(1) |
| **Space** | O(N) | O(N) (2× per entry) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Store min at each level:** Each stack entry remembers the minimum up to that point.
2. **O(1) getMin:** Just read the min from the top entry — no scanning needed.
3. **Trade-off:** 2× space for O(1) getMin. Worth it.
4. **Alternative:** Two stacks — one for values, one for mins. Only push to min stack when new min is found.
5. **Pattern:** Auxiliary data structure — extends to Max Stack, LFU Cache.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Min Stack | [#155](https://leetcode.com/problems/min-stack/) | Medium |
| Max Stack | [#716](https://leetcode.com/problems/max-stack/) | Hard |
| Implement Stack using Queues | [#225](https://leetcode.com/problems/implement-stack-using-queues/) | Easy |
| LRU Cache | [#146](https://leetcode.com/problems/lru-cache/) | Medium |
