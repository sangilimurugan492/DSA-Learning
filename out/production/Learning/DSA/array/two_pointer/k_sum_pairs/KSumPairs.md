# K-Sum Pairs — Detailed Explanation

> **LeetCode #1679** | [Problem Link](https://leetcode.com/problems/k-sum-pairs/)  
> **Topic:** Two Pointers / Sorting  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array `nums` and integer `k`, return the maximum number of operations where each operation picks two numbers whose sum equals `k` and removes them.

### Example

```
Input: nums = [1,2,3,4], k = 5
Output: 2  ((1,4) and (2,3))
```

---

## 🧩 Method: Sort + Two Pointer

### Core Idea

Sort the array, then use two pointers from both ends:
- `sum == k` → count++, move both pointers
- `sum < k` → `left++` (need bigger)
- `sum > k` → `right--` (need smaller)

### Walkthrough: `[1,2,3,4], k=5`

```
Sorted: [1,2,3,4]
left=0, right=3: 1+4=5 == k → operations=1, left=1, right=2
left=1, right=2: 2+3=5 == k → operations=2, left=2, right=1
left >= right → stop

Result: 2 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Sort enables two-pointer:** After sorting, pair smallest with largest.
2. **Greedy pairing:** If sum < k, we need a bigger number (move left). If sum > k, we need smaller (move right).
3. **Alternative:** HashMap approach also works in O(N) time, O(N) space.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| K-Sum Pairs | [#1679](https://leetcode.com/problems/k-sum-pairs/) | Medium |
| Two Sum | [#1](https://leetcode.com/problems/two-sum/) | Easy |
| Two Sum II | [#167](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium |
