# Daily Temperatures — Detailed Explanation

> **LeetCode #739** | [Problem Link](https://leetcode.com/problems/daily-temperatures/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (#1 Monotonic Stack problem)  
> **Topic:** Monotonic Stack, Array

---

## 📋 Problem Statement

Given array of temperatures, return array showing days to wait for warmer temp.

### Example

`[73,74,75,71,69,72,76,73]` → `[1,1,4,2,1,1,0,0]`

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

For each day, scan forward for warmer day.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | Result |

---

## 🧩 Method 2: Monotonic Stack — O(N)

### Core Idea

Stack stores indices of days waiting for warmer temp. When we find a warmer day, pop all colder days and calculate their answer.

### Key Insight

> Stack maintains indices in decreasing temperature order. When current temp > stack top's temp → we found the answer for the popped index. Each element is pushed/popped at most once → O(N).

### Dry Run — `[73,74,75,71,69,72,76,73]`

| i | temp[i] | Stack (before) | Action | Stack (after) | Result |
|:-:|:-------:|:--------------:|:------:|:-------------:|:------:|
| 0 | 73 | [] | push 0 | [0] | [0,0,0,0,0,0,0,0] |
| 1 | 74 | [0] | 74>73 → pop 0, result[0]=1. push 1 | [1] | [1,0,0,0,0,0,0,0] |
| 2 | 75 | [1] | 75>74 → pop 1, result[1]=1. push 2 | [2] | [1,1,0,0,0,0,0,0] |
| 3 | 71 | [2] | 71<75 → push 3 | [2,3] | [1,1,0,0,0,0,0,0] |
| 4 | 69 | [2,3] | 69<71 → push 4 | [2,3,4] | [1,1,0,0,0,0,0,0] |
| 5 | 72 | [2,3,4] | 72>69 → pop 4, r[4]=1. 72>71 → pop 3, r[3]=2. push 5 | [2,5] | [1,1,0,2,1,0,0,0] |
| 6 | 76 | [2,5] | 76>72 → pop 5, r[5]=1. 76>75 → pop 2, r[2]=4. push 6 | [6] | [1,1,4,2,1,1,0,0] |
| 7 | 73 | [6] | 73<76 → push 7 | [6,7] | [1,1,4,2,1,1,0,0] |

✅ **Result: [1,1,4,2,1,1,0,0]**

### Code

```kotlin
fun dailyTemperaturesMonotonicStack(temperatures: IntArray): IntArray {
    val result = IntArray(temperatures.size)
    val stack = ArrayDeque<Int>()
    for (i in temperatures.indices) {
        while (stack.isNotEmpty() && temperatures[i] > temperatures[stack.last()]) {
            val prevIdx = stack.removeLast()
            result[prevIdx] = i - prevIdx
        }
        stack.addLast(i)
    }
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each element pushed/popped once |
| **Space** | O(N) | Stack |

---

## 📊 Comparison Table

| Aspect | Brute Force | Monotonic Stack |
|--------|-------------|-----------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(N) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Monotonic decreasing stack:** Stores indices waiting for a warmer day.
2. **Pop on warmer:** When current > stack top → answer found for popped index.
3. **O(N) guarantee:** Each element pushed and popped at most once.
4. **Pattern:** Next greater element — extends to Next Greater Element I/II, Online Stock Span.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Daily Temperatures | [#739](https://leetcode.com/problems/daily-temperatures/) | Medium |
| Next Greater Element I | [#496](https://leetcode.com/problems/next-greater-element-i/) | Easy |
| Online Stock Span | [#901](https://leetcode.com/problems/online-stock-span/) | Medium |
| Car Fleet | [#853](https://leetcode.com/problems/car-fleet/) | Medium |
