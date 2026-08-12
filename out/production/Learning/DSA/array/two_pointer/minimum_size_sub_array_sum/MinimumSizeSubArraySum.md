# Minimum Size Subarray Sum — Detailed Explanation

> **LeetCode #209** | [Problem Link](https://leetcode.com/problems/minimum-size-subarray-sum/)  
> **Topic:** Sliding Window  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array of positive integers `nums` and a positive integer `target`, return the minimal length of a subarray whose sum ≥ `target`. If none exists, return 0.

### Example

```
Input: target = 7, nums = [2,3,1,2,4,3]
Output: 2  (subarray [4,3] sums to 7)
```

---

## 🧩 Method: Sliding Window

### Core Idea

Expand `right` to grow the window sum. When `sum >= target`, shrink from `left` to find the minimum length. Track the minimum.

### Walkthrough: `target = 7, nums = [2,3,1,2,4,3]`

```
right=0: sum=2 < 7
right=1: sum=5 < 7
right=2: sum=6 < 7
right=3: sum=8 >= 7 → minLen=4, shrink left: sum=6 < 7
right=4: sum=10 >= 7 → minLen=2 ([4,3]... actually [4] at this point), shrink left
right=5: sum=7 >= 7 → minLen=2 ([4,3])

Result: 2 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — each element visited at most twice |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Variable-size sliding window:** Expand right to increase sum, shrink left to decrease.
2. **Only works for positive numbers:** The approach relies on the fact that expanding increases sum and shrinking decreases it.
3. **Track minimum:** Update `minLen` whenever `sum >= target`.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Minimum Size Subarray Sum | [#209](https://leetcode.com/problems/minimum-size-subarray-sum/) | Medium |
| Maximum Average Subarray I | [#643](https://leetcode.com/problems/maximum-average-subarray-i/) | Easy |
| Maximum Length of Repeated Subarray | [#718](https://leetcode.com/problems/maximum-length-of-repeated-subarray/) | Medium |
