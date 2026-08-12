# Longest Repeating Character Replacement — Detailed Explanation

> **LeetCode #424** | [Problem Link](https://leetcode.com/problems/longest-repeating-character-replacement/)  
> **Topic:** Sliding Window  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given a string `s` and integer `k`, find the length of the longest substring where you can replace at most `k` characters to make all characters the same.

### Example

```
Input: s = "ABAB", k = 2
Output: 4  (replace 2 chars → all same)
```

---

## 🧩 Method: Sliding Window

### Core Idea

Maintain a window `[left, right]`. Track the frequency of the most common character in the window. If `window_size - maxFreq > k`, the window is invalid → shrink from left.

### Key Insight

> `window_size - maxFreq` = number of characters that need to be replaced. If this exceeds `k`, shrink the window.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(26) = O(1) — frequency array |

---

## 🔑 Key Takeaways

1. **maxFreq optimization:** We don't need to recompute maxFreq when shrinking — a higher maxFreq from a previous window is still valid.
2. **Window validity:** `right - left + 1 - maxFreq <= k` means the window is valid.
3. **Sliding window with condition:** Expand right, shrink left when invalid.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Repeating Character Replacement | [#424](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium |
| Max Consecutive Ones III | [#1004](https://leetcode.com/problems/max-consecutive-ones-iii/) | Medium |
| Longest Substring Without Repeating Characters | [#3](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium |
