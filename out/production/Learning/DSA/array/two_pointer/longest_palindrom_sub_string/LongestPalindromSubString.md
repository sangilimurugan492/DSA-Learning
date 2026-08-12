# Longest Palindromic Substring — Detailed Explanation

> **LeetCode #5** | [Problem Link](https://leetcode.com/problems/longest-palindromic-substring/description/)  
> **Topic:** Two Pointers / Expand Around Center  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given a string `s`, return the longest palindromic substring in `s`.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `"babad"` | `"bab"` (or `"aba"`) | Both are valid palindromes of length 3 |
| `"cbbd"` | `"bb"` | Even-length palindrome |

---

## 🧩 Method: Expand Around Center

### Core Idea

A palindrome mirrors around its **center**. For each index `i` in the string, try expanding outward from `i` as a center:
1. **Odd-length** palindrome: center is a single character `s[i]` (e.g., `"aba"`)
2. **Even-length** palindrome: center is between `s[i]` and `s[i+1]` (e.g., `"abba"`)

Track the longest palindrome found across all centers.

### Walkthrough: `"babad"`

```
i=0 ('b'): odd → "b" (len 1),  even → "" (len 0)     → max=1
i=1 ('a'): odd → "bab" (len 3), even → "" (len 0)    → max=3 ← longest
i=2 ('b'): odd → "aba" (len 3), even → "" (len 0)   → max=3 (tie)
i=3 ('a'): odd → "a" (len 1),   even → "" (len 0)    → max=3
i=4 ('d'): odd → "d" (len 1),   even → "" (len 0)    → max=3

Result: "bab" (or "aba") ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — N centers × O(N) expansion |
| **Space** | O(1) — only pointers |

---

## 🔑 Key Takeaways

1. **Two center types:** Always check both odd (single char) and even (between two chars) centers.
2. **Expand while matching:** Move `left--` and `right++` as long as `s[left] == s[right]`.
3. **Length formula:** When expansion stops, `left` and `right` have gone one step too far, so length = `right - left - 1`.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Palindromic Substring | [#5](https://leetcode.com/problems/longest-palindromic-substring/) | Medium |
| Palindromic Substrings | [#647](https://leetcode.com/problems/palindromic-substrings/) | Medium |
| Valid Palindrome | [#125](https://leetcode.com/problems/valid-palindrome/) | Easy |
