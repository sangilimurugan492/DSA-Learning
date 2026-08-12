# Find All Anagrams in a String — Detailed Explanation

> **LeetCode #438** | [Problem Link](https://leetcode.com/problems/find-all-anagrams-in-a-string/description/)  
> **Topic:** Sliding Window  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given strings `s` and `p`, return all start indices of `p`'s anagrams in `s`.

### Example

```
Input: s = "cbaebabacd", p = "abc"
Output: [0, 6]  ("cba" and "bac" are anagrams of "abc")
```

---

## 🧩 Method 1: Brute Force (Sort & Compare)

### Core Idea

For each substring of `s` with length `len(p)`, sort it and compare with sorted `p`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(M × N log N) — M substrings, each sorted |
| **Space** | O(N) |

---

## 🧩 Method 2: Sliding Window with Frequency Count (Optimal)

### Core Idea

Use two `IntArray(26)` frequency maps — one for `p` and one for the current window in `s`. Slide the window across `s`, updating counts in O(1) per step. If the frequency arrays match, the current window is an anagram.

### Walkthrough: `s = "cbaebabacd", p = "abc"`

```
Window size = 3
Window "cba": freq matches p → add index 0
Window "bae": freq doesn't match
...
Window "bac": freq matches p → add index 6

Result: [0, 6] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — sliding window with O(26) comparison per step |
| **Space** | O(1) — two fixed-size arrays of 26 |

---

## 🔑 Key Takeaways

1. **Frequency arrays instead of sorting:** Compare character counts in O(26) = O(1) per window.
2. **Slide efficiently:** Add new char on right, remove old char on left — O(1) update.
3. **Fixed window size:** Window size = `len(p)`.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Find All Anagrams in a String | [#438](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | Medium |
| Permutation in String | [#567](https://leetcode.com/problems/permutation-in-string/) | Medium |
| Minimum Window Substring | [#76](https://leetcode.com/problems/minimum-window-substring/) | Hard |
