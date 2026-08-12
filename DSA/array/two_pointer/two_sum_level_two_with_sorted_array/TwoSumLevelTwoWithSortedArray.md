# Two Sum II — Input Array Is Sorted

> **LeetCode #167** | [Problem Link](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/description/)  
> **Topic:** Two Pointers  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given a **1-indexed** sorted array `numbers` and a `target`, return the 1-indexed indices of the two numbers that add up to `target`. Exactly one solution exists.

### Example

```
Input:  numbers = [2,7,11,15], target = 9
Output: [1,2]  (2 + 7 = 9)
```

---

## 🧩 Method 1: Brute Force

### Core Idea

Try every pair `(i, j)` and check if their sum equals `target`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(1) |

---

## 🧩 Method 2: Two Pointer (Optimal)

### Core Idea

Since the array is **sorted**, use two pointers from both ends:
- `sum < target` → `left++` (need a bigger sum)
- `sum > target` → `right--` (need a smaller sum)
- `sum == target` → return `[left+1, right+1]` (1-indexed)

### Walkthrough: `[2, 7, 11, 15], target = 9`

```
left=0, right=3: 2 + 15 = 17 > 9 → right--
left=0, right=2: 2 + 11 = 13 > 9 → right--
left=0, right=1: 2 + 7 = 9 == 9 → return [1, 2] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Sorted array → two pointers:** This is the classic pattern for sorted two-sum.
2. **1-indexed:** Remember to add 1 to the returned indices.
3. **Why move pointers:** Moving left increases sum; moving right decreases sum — both are safe because the array is sorted.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Two Sum II | [#167](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium |
| Two Sum | [#1](https://leetcode.com/problems/two-sum/) | Easy |
| 3Sum | [#15](https://leetcode.com/problems/3sum/) | Medium |
