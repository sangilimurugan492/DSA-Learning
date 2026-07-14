# Wiggle Subsequence — Detailed Explanation

> **LeetCode #376** | [Problem Link](https://leetcode.com/problems/wiggle-subsequence/)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Asked at Google, Amazon)  
> **Topic:** Greedy, DP

---

## 📋 Problem Statement

A wiggle sequence alternates between increasing and decreasing. Return the length of the longest wiggle subsequence.

### Examples

| nums | Output | Explanation |
|------|--------|-------------|
| `[1,7,4,9,2,5]` | 6 | 1<7>4<9>2<5 (entire sequence) |
| `[1,17,5,10,13,15,10,5,16,8]` | 7 | 1<17>5<13>5<16>8 |
| `[1,2,3,4,5]` | 2 | Only one direction → 2 (first and last) |

---

## 🧩 Method 1: DP — O(N²)

### Core Idea

`up[i]` = longest wiggle ending with UP at i. `down[i]` = longest ending with DOWN. For each i, check all j < i.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | up + down arrays |

---

## 🧩 Method 2: Greedy — O(N)

### Core Idea

Track `up` and `down` lengths. When increasing: `up = down + 1`. When decreasing: `down = up + 1`. Count direction changes.

### Key Insight

> We only care about direction changes, not actual values. Skip consecutive same-direction moves — they don't add to wiggle length.

### Dry Run — `[1,17,5,10,13,15,10,5,16,8]`

| i | nums[i] | Direction | up | down |
|:-:|:-------:|:---------:|:--:|:----:|
| 0 | 1 | — | 1 | 1 |
| 1 | 17 | up | 2 | 1 |
| 2 | 5 | down | 2 | 3 |
| 3 | 10 | up | 4 | 3 |
| 4 | 13 | up | 4 | 3 (same dir, skip) |
| 5 | 15 | up | 4 | 3 (same dir, skip) |
| 6 | 10 | down | 4 | 5 |
| 7 | 5 | down | 4 | 5 (same dir, skip) |
| 8 | 16 | up | 6 | 5 |
| 9 | 8 | down | 6 | 7 |

✅ **Result: max(6, 7) = 7**

### Code

```kotlin
fun wiggleMaxLength(nums: IntArray): Int {
    if (nums.size < 2) return nums.size
    var up = 1; var down = 1
    for (i in 1 until nums.size) {
        when {
            nums[i] > nums[i - 1] -> up = down + 1
            nums[i] < nums[i - 1] -> down = up + 1
        }
    }
    return maxOf(up, down)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Two variables |

---

## 📊 Comparison Table

| Aspect | DP | Greedy |
|--------|-----|--------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(1) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Count direction changes:** Every sign change (up↔down) = one more wiggle element.
2. **Skip same direction:** Consecutive increases/decreases don't add to wiggle length.
3. **up/down tracking:** `up = down + 1` when increasing, `down = up + 1` when decreasing.
4. **Pattern:** Greedy direction counting — extends to Stock Buy/Sell, Increasing Subsequence.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Wiggle Subsequence | [#376](https://leetcode.com/problems/wiggle-subsequence/) | Medium |
| Longest Increasing Subsequence | [#300](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium |
| Best Time to Buy/Sell Stock II | [#122](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) | Medium |
