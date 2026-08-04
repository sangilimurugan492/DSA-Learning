# Largest Rectangle in Histogram — Detailed Explanation

> **LeetCode #84** | [Problem Link](https://leetcode.com/problems/largest-rectangle-in-histogram/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 3 hardest stack problem)  
> **Topic:** Monotonic Stack, Array

---

## 📋 Problem Statement

Given an array of heights representing a histogram's bar heights, find the area of the largest rectangle that can be formed within the histogram.

### Examples

| heights | Output | Explanation |
|---------|--------|-------------|
| `[2,1,5,6,2,3]` | 10 | Rectangle [5,6] has area 2×5=10 |
| `[2,4]` | 4 | Rectangle [2,4] has area 2×2=4 |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

For each bar, expand right and track min height. Area = minHeight × width.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(1) | — |

---

## 🧩 Method 2: Monotonic Stack — O(N)

### Core Idea

Stack stores indices in increasing height order. When a smaller height is found, pop and calculate area. Add sentinel 0 at end to flush remaining bars.

### Key Insight

> For each bar, the max rectangle using its height extends from the **previous smaller bar** to the **next smaller bar**. The stack helps find these boundaries efficiently.

### Dry Run — `heights = [2,1,5,6,2,3]` (with sentinel 0)

| i | height | Stack (before) | Action | Area | maxArea |
|:-:|:------:|:--------------:|:------:|:----:|:-------:|
| 0 | 2 | [] | push 0 | — | 0 |
| 1 | 1 | [0] | 1<2 → pop 0, h=2, w=1 | 2 | 2 |
| 1 | 1 | [] | push 1 | — | 2 |
| 2 | 5 | [1] | push 2 | — | 2 |
| 3 | 6 | [1,2] | push 3 | — | 2 |
| 4 | 2 | [1,2,3] | 2<6 → pop 3, h=6, w=1 | 6 | 6 |
| 4 | 2 | [1,2] | 2<5 → pop 2, h=5, w=2 | 10 | 10 |
| 4 | 2 | [1] | push 4 | — | 10 |
| 5 | 3 | [1,4] | push 5 | — | 10 |
| 6 | 0 | [1,4,5] | 0<3 → pop 5, h=3, w=1 | 3 | 10 |
| 6 | 0 | [1,4] | 0<2 → pop 4, h=2, w=4 | 8 | 10 |
| 6 | 0 | [1] | 0<1 → pop 1, h=1, w=6 | 6 | 10 |

✅ **Result: 10**

### Code

```kotlin
fun largestRectangleAreaStack(heights: IntArray): Int {
    val stack = ArrayDeque<Int>()
    var maxArea = 0
    val extended = heights + intArrayOf(0)

    for (i in extended.indices) {
        while (stack.isNotEmpty() && extended[i] < extended[stack.last()]) {
            val height = extended[stack.removeLast()]
            val width = if (stack.isEmpty()) i else i - stack.last() - 1
            maxArea = maxOf(maxArea, height * width)
        }
        stack.addLast(i)
    }
    return maxArea
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
| **Space** | O(1) | O(N) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Monotonic increasing stack:** Stores indices in increasing height order. When a smaller height arrives, pop and calculate.
2. **Width calculation:** `width = i - stack.last() - 1` (distance from previous smaller bar to current). If stack empty, `width = i` (extends to start).
3. **Sentinel 0:** Adding a 0 at the end forces all remaining bars to be processed.
4. **Pattern:** Monotonic stack — extends to Trapping Rain Water, Daily Temperatures, Next Greater Element.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Largest Rectangle in Histogram | [#84](https://leetcode.com/problems/largest-rectangle-in-histogram/) | Hard |
| Trapping Rain Water | [#42](https://leetcode.com/problems/trapping-rain-water/) | Hard |
| Daily Temperatures | [#739](https://leetcode.com/problems/daily-temperatures/) | Medium |
| Maximal Rectangle | [#85](https://leetcode.com/problems/maximal-rectangle/) | Hard |
