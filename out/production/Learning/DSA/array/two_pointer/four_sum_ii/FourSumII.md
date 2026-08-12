# 4Sum II — Detailed Explanation

> **LeetCode #454** | [Problem Link](https://leetcode.com/problems/4sum-ii/)  
> **Topic:** HashMap  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given four arrays `nums1`, `nums2`, `nums3`, `nums4` (all length `n`), count tuples `(i, j, k, l)` such that `nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0`.

### Example

```
Input: nums1 = [1,2], nums2 = [-2,-1], nums3 = [-1,2], nums4 = [0,2]
Output: 2
```

---

## 🧩 Method 1: Brute Force

### Core Idea

Four nested loops — check all combinations.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N⁴) |
| **Space** | O(1) |

---

## 🧩 Method 2: HashMap (Optimal)

### Core Idea

Split into two groups:
1. Precompute all `nums1[i] + nums2[j]` sums → store frequencies in a HashMap.
2. For each `nums3[k] + nums4[l]`, look up `-(sum)` in the HashMap.

### Key Insight

> `a + b + c + d = 0` ⟺ `a + b = -(c + d)`. Precompute one side, look up the other.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — two pairs of nested loops |
| **Space** | O(N²) — HashMap stores up to N² sums |

---

## 🔑 Key Takeaways

1. **Split and conquer:** Break 4 arrays into 2+2, precompute one pair's sums.
2. **HashMap for complement lookup:** Store sums of first two arrays, look up negation for last two.
3. **Different from 4Sum:** Here we count tuples across 4 separate arrays (not one array), so no sorting/dedup needed.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| 4Sum II | [#454](https://leetcode.com/problems/4sum-ii/) | Medium |
| 4Sum | [#18](https://leetcode.com/problems/4sum/) | Medium |
| 3Sum | [#15](https://leetcode.com/problems/3sum/) | Medium |
