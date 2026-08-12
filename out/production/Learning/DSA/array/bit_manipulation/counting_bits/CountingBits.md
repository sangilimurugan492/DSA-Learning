# Counting Bits — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/counting-bits/  
> **Topic:** DP, Bit Manipulation  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an integer `n`, return an array `ans` of length `n + 1` such that for each `i`
(`0 <= i <= n`), `ans[i]` is the number of `1`'s in the binary representation of `i`.

### Constraints

- `0 <= n <= 10^5`

### Examples

**Example 1:**

```
Input:  n = 2
Output: [0, 1, 1]

0 → 0   → 0 ones
1 → 1   → 1 one
2 → 10  → 1 one
```

**Example 2:**

```
Input:  n = 5
Output: [0, 1, 1, 2, 1, 2]

0 → 0     → 0
1 → 1     → 1
2 → 10    → 1
3 → 11    → 2
4 → 100   → 1
5 → 101   → 2
```

---

## 🧩 Method: DP + Bit Manipulation

### Core Idea

The key recurrence is: **`ans[i] = ans[i >> 1] + (i & 1)`**

- `i >> 1` = `i / 2` (drops the last bit)
- `i & 1` = last bit (0 or 1)

The number of 1-bits in `i` equals the number of 1-bits in `i/2` (already computed) plus
the last bit of `i`. This is because shifting right by 1 removes the LSB, and we add it back.

### Step-by-step Walkthrough (n = 5)

| i | Binary | i >> 1 | ans[i >> 1] | i & 1 | ans[i] |
|---|--------|--------|-------------|-------|--------|
| 0 | 0      | —      | —           | —     | 0 (base) |
| 1 | 1      | 0      | 0           | 1     | 0 + 1 = 1 |
| 2 | 10     | 1      | 1           | 0     | 1 + 0 = 1 |
| 3 | 11     | 1      | 1           | 1     | 1 + 1 = 2 |
| 4 | 100    | 2      | 1           | 0     | 1 + 0 = 1 |
| 5 | 101    | 2      | 1           | 1     | 1 + 1 = 2 |

**Result = [0, 1, 1, 2, 1, 2]** ✅

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass from 1 to n |
| **Space** | O(N) — for the result array |

---

## 🔑 Key Takeaways

1. **DP with bit manipulation**: `ans[i] = ans[i >> 1] + (i & 1)` — reuse previously computed
   results.
2. Shifting right by 1 is equivalent to integer division by 2 — it drops the LSB.
3. `i & 1` extracts the LSB (0 for even, 1 for odd).
4. This is O(N) — far better than computing popcount independently for each number.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Counting Bits | [link](https://leetcode.com/problems/counting-bits/) | Easy |
| Number of 1 Bits | [link](https://leetcode.com/problems/number-of-1-bits/) | Easy |
| Missing Number | [link](https://leetcode.com/problems/missing-number/) | Easy |
