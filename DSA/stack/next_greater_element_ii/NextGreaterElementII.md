# Next Greater Element II — Detailed Explanation

> **LeetCode #503** | [Problem Link](https://leetcode.com/problems/next-greater-element-ii/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐  
> **Topic:** Monotonic Stack, Circular Array

---

## 📋 Problem Statement

Given a **circular** array, find the next greater element for every element. If no greater element exists, use -1.

### Examples

| nums | Output | Explanation |
|------|--------|-------------|
| `[1,2,1]` | `[2,-1,2]` | Last 1 wraps around to find 2 |
| `[5,4,3,2,1]` | `[-1,5,5,5,5]` | All wrap to find 5 |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

For each element, scan the circular array (using modulo) for the next greater.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | Result array |

---

## 🧩 Method 2: Monotonic Stack — O(N)

### Core Idea

Iterate 2×N using modulo. Stack stores indices in decreasing order. When a larger element arrives, pop and set result.

### Key Insight

> Circular array = traverse the array **twice**. Monotonic decreasing stack: when a larger element arrives, it's the next greater for all smaller elements in the stack.

### Dry Run — `nums = [1,2,1]`

| i | idx | nums[idx] | Stack (before) | Action | Result |
|:-:|:---:|:---------:|:--------------:|:------:|:------:|
| 0 | 0 | 1 | [] | push 0 | [-1,-1,-1] |
| 1 | 1 | 2 | [0] | 2>1 → pop 0, result[0]=2 | [2,-1,-1] |
| 1 | 1 | 2 | [] | push 1 | [2,-1,-1] |
| 2 | 2 | 1 | [1] | push 2 | [2,-1,-1] |
| 3 | 0 | 1 | [1,2] | 1 not > 1 | [2,-1,-1] |
| 4 | 1 | 2 | [1,2] | 2>1 → pop 2, result[2]=2 | [2,-1,2] |
| 5 | 2 | 1 | [1] | 1 not > 2 | [2,-1,2] |

✅ **Result: [2,-1,2]**

### Code

```kotlin
fun nextGreaterElementsCircular(nums: IntArray): IntArray {
    val n = nums.size
    val result = IntArray(n) { -1 }
    val stack = ArrayDeque<Int>()

    for (i in 0 until 2 * n) {
        val idx = i % n
        while (stack.isNotEmpty() && nums[idx] > nums[stack.last()]) {
            result[stack.removeLast()] = nums[idx]
        }
        if (i < n) stack.addLast(idx)
    }
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each element pushed/popped once |
| **Space** | O(N) | Stack + result |

---

## 📊 Comparison Table

| Aspect | Brute Force | Monotonic Stack |
|--------|-------------|-----------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(N) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Circular = traverse twice:** Use `i % n` to simulate circular traversal.
2. **Only push in first pass:** `if (i < n)` prevents duplicate indices in stack.
3. **Monotonic decreasing stack:** When a larger element arrives, it resolves all smaller pending elements.
4. **Pattern:** Monotonic stack — extends to Daily Temperatures, Largest Rectangle in Histogram.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Next Greater Element II | [#503](https://leetcode.com/problems/next-greater-element-ii/) | Medium |
| Next Greater Element I | [#496](https://leetcode.com/problems/next-greater-element-i/) | Easy |
| Daily Temperatures | [#739](https://leetcode.com/problems/daily-temperatures/) | Medium |
| Largest Rectangle in Histogram | [#84](https://leetcode.com/problems/largest-rectangle-in-histogram/) | Hard |
