# Max Consecutive Ones III — Detailed Explanation

> **LeetCode #1004** | [Problem Link](https://leetcode.com/problems/max-consecutive-ones-iii/)  
> **Topic:** Sliding Window  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given a binary array `nums` and integer `k`, return the maximum number of consecutive `1`s if you can flip at most `k` zeros.

### Example

```
Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
Output: 6  (flip the two zeros at indices 3,4 → six 1s)
```

---

## 🧩 Method: Sliding Window

### Core Idea

Maintain a window `[left, right]`. Count zeros in the window. If zeros > k, shrink from left until zeros <= k. Track max window size.

### Key Insight

> Flipping a zero is equivalent to "allowing" it in the window. The window is valid as long as it contains at most `k` zeros.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Same pattern as Longest Repeating Character Replacement:** `window_size - count_of_target <= k`.
2. **Count zeros, not ones:** The constraint is on zeros (flips), so track zero count.
3. **Sliding window:** Expand right, shrink left when zeros exceed k.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Max Consecutive Ones III | [#1004](https://leetcode.com/problems/max-consecutive-ones-iii/) | Medium |
| Longest Repeating Character Replacement | [#424](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium |
| Max Consecutive Ones | [#485](https://leetcode.com/problems/max-consecutive-ones/) | Easy |
