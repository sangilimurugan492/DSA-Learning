# Is Subsequence — Detailed Explanation

> **LeetCode #392** | [Problem Link](https://leetcode.com/problems/is-subsequence/description)  
> **Topic:** Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given two strings `s` and `t`, return `true` if `s` is a **subsequence** of `t`. A subsequence is formed by deleting some characters of `t` without changing the order of the remaining characters.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `s = "abc"`, `t = "ahbgdc"` | `true` | a→a, b→b, c→c (skip h,g,d) |
| `s = "axc"`, `t = "ahbgdc"` | `false` | No 'x' in t |

---

## 🧩 Method 1: Two Pointer (Optimal)

### Core Idea

Use two pointers — `sIndex` for `s` and `tIndex` for `t`. Iterate through `t`:
- If `s[sIndex] == t[tIndex]`, advance `sIndex` (found a match).
- Always advance `tIndex`.

If `sIndex` reaches the end of `s`, all characters were matched in order → `true`.

### Walkthrough: `s = "abc", t = "ahbgdc"`

```
tIndex=0: t[0]='a' == s[0]='a' → sIndex=1
tIndex=1: t[1]='h' != s[1]='b' → skip
tIndex=2: t[2]='b' == s[1]='b' → sIndex=2
tIndex=3: t[3]='g' != s[2]='c' → skip
tIndex=4: t[4]='d' != s[2]='c' → skip
tIndex=5: t[5]='c' == s[2]='c' → sIndex=3

sIndex=3 == s.length → true ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass through t |
| **Space** | O(1) |

---

## 🧩 Method 2: Recursive

### Core Idea

At each step, either match (advance both) or skip (advance only `t`). Base cases: `i == s.length` → true, `j == t.length` → false.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) — recursion stack |

---

## 🔑 Key Takeaways

1. **Two pointers, one pass:** The s pointer only advances on matches — simple and efficient.
2. **Greedy matching:** Match each character of `s` as early as possible in `t` — this is always optimal.
3. **Follow-up:** If there are many `s` queries on the same `t`, precompute a HashMap of character positions for binary search.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Is Subsequence | [#392](https://leetcode.com/problems/is-subsequence/) | Easy |
| Number of Matching Subsequences | [#792](https://leetcode.com/problems/number-of-matching-subsequences/) | Medium |
| Shortest Way to Form String | [#1055](https://leetcode.com/problems/shortest-way-to-form-string/) | Medium |
