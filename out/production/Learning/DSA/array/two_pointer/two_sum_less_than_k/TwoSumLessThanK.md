# Two Sum Less Than K — Detailed Explanation

> **LeetCode #1099** | [Problem Link](https://leetcode.com/problems/two-sum-less-than-k/)  
> **Topic:** Two Pointers / Sorting  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array `nums` and integer `k`, return the **maximum sum** of a pair such that `sum < k`. If no such pair exists, return `-1`.

### Example

```
Input: nums = [34,23,1,24,75,33,54,8], k = 60
Output: 58  (34 + 24 = 58, the max sum < 60)
```

---

## 🧩 Method: Sort + Two Pointer

### Core Idea

Sort the array, then use two pointers from both ends:
- `sum < k` → track max, `left++` (try bigger)
- `sum >= k` → `right--` (try smaller)

### Walkthrough: `nums = [34,23,1,24,75,33,54,8], k = 60`

```
Sorted: [1,8,23,24,33,34,54,75]
left=0, right=7: 1+75=76 >= 60 → right--
left=0, right=6: 1+54=55 < 60 → maxSum=55, left++
left=1, right=6: 8+54=62 >= 60 → right--
left=1, right=5: 8+34=42 < 60 → maxSum=55, left++
...
left=4, right=5: 33+34=67 >= 60 → right--
left=4, right=4 → stop

Result: 58 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Sort enables two-pointer:** After sorting, pair smallest with largest.
2. **Track max under k:** When `sum < k`, update `maxSum` and try a bigger pair (`left++`).
3. **When sum ≥ k:** Move `right--` to try a smaller sum.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Two Sum Less Than K | [#1099](https://leetcode.com/problems/two-sum-less-than-k/) | Easy |
| Two Sum II | [#167](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium |
| 3Sum Closest | [#16](https://leetcode.com/problems/3sum-closest/) | Medium |
