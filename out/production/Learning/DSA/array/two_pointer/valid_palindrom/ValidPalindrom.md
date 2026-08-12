# Valid Palindrome — Detailed Explanation

> **LeetCode #125** | [Problem Link](https://leetcode.com/problems/valid-palindrome/)  
> **Topic:** Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given a string `s`, return `true` if it is a palindrome, considering only **alphanumeric** characters and ignoring case.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `"A man, a plan, a canal: Panama"` | `true` | Cleaned: `"amanaplanacanalpanama"` — reads same forwards/backwards |
| `"race a car"` | `false` | Cleaned: `"raceacar"` — not a palindrome |

---

## 🧩 Method 1: Brute Force (Clean + Reverse)

### Core Idea

Clean the string (lowercase + alphanumeric only), reverse it, and compare.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) — cleaned string + reversed copy |

---

## 🧩 Method 2: Two Pointer (Optimal)

### Core Idea

Use two pointers from both ends. Skip non-alphanumeric characters. Compare the lowercase versions of the characters at each pointer.

### Walkthrough: `"A man, a plan, a canal: Panama"`

```
left=0 ('a'), right=20 ('a') → match → left++, right--
left=1 (skip ' '), right=19 (skip ':') → left=2 ('m'), right=18 ('m') → match
... continues until left >= right

Result: true ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — no extra string |

---

## 🔑 Key Takeaways

1. **Skip non-alphanumeric:** Use `isLetterOrDigit()` to skip spaces, punctuation, etc.
2. **Case-insensitive:** Compare using `lowercaseChar()`.
3. **Two pointers save space:** No need to build a cleaned string — compare in-place.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Valid Palindrome | [#125](https://leetcode.com/problems/valid-palindrome/) | Easy |
| Valid Palindrome II | [#680](https://leetcode.com/problems/valid-palindrome-ii/) | Easy |
| Longest Palindromic Substring | [#5](https://leetcode.com/problems/longest-palindromic-substring/) | Medium |
