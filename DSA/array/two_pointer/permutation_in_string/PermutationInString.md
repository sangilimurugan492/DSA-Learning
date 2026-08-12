# Permutation in String — Detailed Explanation

> **LeetCode #567** | [Problem Link](https://leetcode.com/problems/permutation-in-string/)  
> **Topic:** Sliding Window  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given two strings `s1` and `s2`, return `true` if `s2` contains a permutation of `s1` (i.e., a substring that is an anagram of `s1`).

### Example

```
Input: s1 = "ab", s2 = "eidbaooo"
Output: true  ("ba" is a permutation of "ab")
```

---

## 🧩 Method: Sliding Window with Frequency Count

### Core Idea

Use two `IntArray(26)` frequency maps — one for `s1` and one for the current window in `s2` (window size = `len(s1)`). Slide the window across `s2`, updating counts in O(1) per step. If the frequency arrays match, a permutation exists.

### Walkthrough: `s1 = "ab", s2 = "eidbaooo"`

```
Window size = 2
Window "ei": freq doesn't match s1
Window "id": freq doesn't match
Window "db": freq doesn't match
Window "ba": freq matches s1 {a:1, b:1} → true ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — sliding window with O(26) comparison per step |
| **Space** | O(1) — two fixed-size arrays of 26 |

---

## 🔑 Key Takeaways

1. **Fixed window size:** Window size = `len(s1)`.
2. **Frequency arrays:** Compare character counts in O(26) = O(1) per window.
3. **Same pattern as Find All Anagrams:** The only difference is returning boolean vs indices.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Permutation in String | [#567](https://leetcode.com/problems/permutation-in-string/) | Medium |
| Find All Anagrams in a String | [#438](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | Medium |
| Minimum Window Substring | [#76](https://leetcode.com/problems/minimum-window-substring/) | Hard |
