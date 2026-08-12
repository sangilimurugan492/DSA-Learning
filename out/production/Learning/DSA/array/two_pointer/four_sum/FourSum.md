# 4Sum — Detailed Explanation

> **LeetCode #18** | [Problem Link](https://leetcode.com/problems/4sum/description/)  
> **Topic:** Two Pointers / Sorting  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array `nums` and a `target`, return all **unique quadruplets** that sum to `target`.

### Example

```
Input: nums = [1,0,-1,0,-2,2], target = 0
Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
```

---

## 🧩 Method 1: Brute Force

### Core Idea

Four nested loops checking all quadruplets. Use a Set to avoid duplicates.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N⁴) |
| **Space** | O(N) |

---

## 🧩 Method 2: Sort + Two Pointer (Optimal)

### Core Idea

Sort the array. Fix the first two elements with nested loops, then use two pointers for the remaining two. Skip duplicates at every level.

### Key Insight

> After fixing two elements, the problem reduces to "find two numbers that sum to a target" — classic two-pointer on sorted array.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N³) — two outer loops + two-pointer scan |
| **Space** | O(1) — ignoring output |

### Overflow Note

Use `Long` for the sum to prevent integer overflow with large values.

---

## 🔑 Key Takeaways

1. **Fix two, two-pointer the rest:** Extends 3Sum pattern by one more nested loop.
2. **Skip duplicates at all levels:** Essential for unique quadruplets.
3. **Use Long for sum:** Prevents overflow with large numbers.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| 3Sum | [#15](https://leetcode.com/problems/3sum/) | Medium |
| 4Sum | [#18](https://leetcode.com/problems/4sum/) | Medium |
| 4Sum II | [#454](https://leetcode.com/problems/4sum-ii/) | Medium |
