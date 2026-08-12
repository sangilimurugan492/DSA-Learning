# Minimum Pair Removal to Sort Array I — Detailed Explanation

> **LeetCode** | [Problem Link](https://leetcode.com/problems/minimum-pair-removal-to-sort-array-i/)  
> **Topic:** Binary Search / LIS Pattern  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array `nums`, return the minimum number of elements to remove so that the remaining array is **non-decreasing** (sorted).

### Key Insight

> This is equivalent to: **Answer = n − length of Longest Non-Decreasing Subsequence (LNDS)**

Elements not in the LNDS are the ones to remove.

### Example

```
Input:  [5, 2, 3, 1]
LNDS:   [2, 3] (length 2)
Answer: 4 - 2 = 2
```

---

## 🧩 Method 1: Brute Force (DP)

### Core Idea

Standard LIS DP: `dp[i]` = length of longest non-decreasing subsequence ending at `i`. For each `i`, check all `j < i` where `nums[j] <= nums[i]` and extend.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Binary Search (Patience Sorting)

### Core Idea

Maintain a `tails` list where `tails[i]` = smallest tail of all LNDS of length `i+1`. For each number, binary search to find where it belongs:
- If it extends the longest subsequence → append.
- Otherwise → replace the existing tail (keeps future extensions possible).

### Walkthrough: `[5, 2, 3, 1]`

```
num=5: tails=[] → append → tails=[5]
num=2: 2 < 5 → replace → tails=[2]
num=3: 3 > 2 → append → tails=[2, 3]
num=1: 1 < 2 → replace → tails=[1, 3]

LNDS length = 2, Answer = 4 - 2 = 2 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) |
| **Space** | O(N) |

---

## 🔑 Key Takeaways

1. **Reduce to LIS:** "Min removals to sort" = "n − longest sorted subsequence".
2. **Patience sorting:** Binary search on `tails` gives O(N log N) instead of O(N²).
3. **Non-decreasing vs strictly increasing:** Use `<=` (not `<`) for non-decreasing.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Increasing Subsequence | [#300](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium |
| Minimum Pair Removal to Sort Array I | [Link](https://leetcode.com/problems/minimum-pair-removal-to-sort-array-i/) | Easy |
