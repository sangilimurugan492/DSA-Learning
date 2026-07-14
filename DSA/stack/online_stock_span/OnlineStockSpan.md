# Online Stock Span — Detailed Explanation

> **LeetCode #901** | [Problem Link](https://leetcode.com/problems/online-stock-span/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Monotonic Stack application)  
> **Topic:** Monotonic Stack, Design

---

## 📋 Problem Statement

Design StockSpanner: for each price, return span of consecutive days where price was ≤ current day's price (including current day).

### Example

`[100,80,60,70,60,75,85]` → `[1,1,1,2,1,4,6]`

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

For each day, look back until price > current.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Look back for each day |
| **Space** | O(N) | Result |

---

## 🧩 Method 2: Monotonic Stack — O(N) amortized

### Core Idea

Stack stores (price, span). When new price comes, pop all smaller prices and accumulate their spans.

### Key Insight

> We don't need to re-examine popped prices — their spans are absorbed into the current span. Each element is pushed/popped at most once → O(N) amortized.

### Dry Run — `[100,80,60,70,60,75,85]`

| Price | Stack (before) | Action | Span | Stack (after) |
|:-----:|:--------------:|:------:|:----:|:-------------:|
| 100 | [] | push (100,1) | 1 | [(100,1)] |
| 80 | [(100,1)] | 80<100 → push (80,1) | 1 | [(100,1),(80,1)] |
| 60 | [(100,1),(80,1)] | 60<80 → push (60,1) | 1 | [(100,1),(80,1),(60,1)] |
| 70 | [...(60,1)] | 70>60 → pop(60,1), span=2. push (70,2) | 2 | [(100,1),(80,1),(70,2)] |
| 60 | [...(70,2)] | 60<70 → push (60,1) | 1 | [(100,1),(80,1),(70,2),(60,1)] |
| 75 | [...(60,1)] | 75>60 → pop(60,1), span=2. 75>70 → pop(70,2), span=4. push (75,4) | 4 | [(100,1),(80,1),(75,4)] |
| 85 | [...(75,4)] | 85>75 → pop(75,4), span=5. 85>80 → pop(80,1), span=6. push (85,6) | 6 | [(100,1),(85,6)] |

✅ **Result: [1,1,1,2,1,4,6]**

### Code

```kotlin
class StockSpanner {
    private val stack = ArrayDeque<Pair<Int, Int>>()  // (price, span)
    fun next(price: Int): Int {
        var span = 1
        while (stack.isNotEmpty() && stack.last().first <= price) {
            span += stack.removeLast().second
        }
        stack.addLast(Pair(price, span))
        return span
    }
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) amortized | Each element pushed/popped once |
| **Space** | O(N) | Stack |

---

## 📊 Comparison Table

| Aspect | Brute Force | Monotonic Stack |
|--------|-------------|-----------------|
| **Time** | O(N²) | O(N) amortized |
| **Space** | O(N) | O(N) |
| **Online?** | No | Yes ✅ |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Stack stores (price, span):** Span = number of consecutive days with price ≤ current.
2. **Pop and accumulate:** When new price ≥ stack top → pop, add their span to current.
3. **Absorbed spans:** Popped prices are never re-examined — their spans are absorbed.
4. **Pattern:** Monotonic stack with accumulated values — extends to Daily Temperatures, Next Greater Element.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Online Stock Span | [#901](https://leetcode.com/problems/online-stock-span/) | Medium |
| Daily Temperatures | [#739](https://leetcode.com/problems/daily-temperatures/) | Medium |
| Next Greater Element I | [#496](https://leetcode.com/problems/next-greater-element-i/) | Easy |
| Car Fleet | [#853](https://leetcode.com/problems/car-fleet/) | Medium |
