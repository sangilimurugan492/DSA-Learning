# Best Sightseeing Pair — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/best-sightseeing-pair/  
> **Topic:** Array / Kadane-style DP

---

## 📋 Problem Statement

You are given an integer array `values` where `values[i]` represents the score of the i-th sightseeing spot. The score of a pair `(i, j)` where `i < j` is:

```
score(i, j) = values[i] + values[j] + i - j
```

Return the **maximum score** of any pair.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[8,1,5,2,6]` | `11` | Pair (0,2): 8 + 5 + 0 - 2 = 11 |
| `[1,2]` | `2` | Pair (0,1): 1 + 2 + 0 - 1 = 2 |

---

## 🧩 Method 1: Brute Force

### Core Idea

Try every pair `(i, j)` with `i < j`, compute the score, and track the maximum.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(1) |

---

## 🧩 Method 2: Optimal — Kadane-style Single Pass

### Core Idea

**Key Insight — Decompose the score formula:**

```
score(i, j) = values[i] + values[j] + i - j
            = (values[i] + i) + (values[j] - j)
```

As we iterate `j` from left to right:
- We maintain `maxSoFar` = the best `(values[i] + i)` for all `i < j` seen so far.
- At each `j`, the best pair score = `maxSoFar + (values[j] - j)`.
- Then update `maxSoFar` with `(values[j] + j)` for future indices.

This is Kadane-style: at each step, we either extend (use the previous best `values[i] + i`) or implicitly "start fresh" (the current index becomes the new best `i` for future pairs).

### Trace for `[8,1,5,2,6]`

| j | values[j] | values[j]-j | maxSoFar | score = maxSoFar + (values[j]-j) | maxScore | Update maxSoFar = max(old, values[j]+j) |
|---|-----------|-------------|----------|----------------------------------|----------|----------------------------------------|
| 0 | 8         | 8           | 8        | — (no pair yet)                   | —        | 8                                      |
| 1 | 1         | 0           | 8        | 8 + 0 = 8                        | 8        | max(8, 2) = 8                          |
| 2 | 5         | 3           | 8        | 8 + 3 = 11                       | 11       | max(8, 7) = 8                          |
| 3 | 2         | -1          | 8        | 8 + (-1) = 7                     | 11       | max(8, 5) = 8                          |
| 4 | 6         | 2           | 8        | 8 + 2 = 10                       | 11       | max(8, 10) = 10                        |

**Result: 11** ✅

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Formula decomposition** is the key trick — split `values[i] + values[j] + i - j` into `(values[i] + i)` and `(values[j] - j)` so each part depends on only one index.
2. **Kadane-style thinking**: maintain a running maximum of the "left part" as you scan right, just like Kadane's maintains a running subarray sum.
3. The "distance penalty" (`-j`) is naturally handled by subtracting `j` at each step — no need for a separate distance variable.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Best Sightseeing Pair | [https://leetcode.com/problems/best-sightseeing-pair/](https://leetcode.com/problems/best-sightseeing-pair/) | Medium |
| Maximum Subarray | [https://leetcode.com/problems/maximum-subarray/](https://leetcode.com/problems/maximum-subarray/) | Medium |
| Maximum Sum Circular Subarray | [https://leetcode.com/problems/maximum-sum-circular-subarray/](https://leetcode.com/problems/maximum-sum-circular-subarray/) | Medium |
| Maximum Product Subarray | [https://leetcode.com/problems/maximum-product-subarray/](https://leetcode.com/problems/maximum-product-subarray/) | Medium |
