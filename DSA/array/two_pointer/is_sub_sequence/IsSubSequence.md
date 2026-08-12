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

## 🧩 Method 1: Brute Force (Nested Loops)

### Core Idea

For each character in `s`, scan through `t` (starting from where we left off) looking for a match. If every character of `s` is found in order within `t`, it's a subsequence.

### Step-by-Step

1. Start with `searchPos = 0` (where to start searching in `t`).
2. For each character `s[i]`:
   - Scan `t` from `searchPos` forward until we find `s[i]`.
   - If found at index `j` → update `searchPos = j + 1` (next search starts after this match).
   - If not found → return `false`.
3. If all characters found → return `true`.

### Walkthrough: `s = "abc", t = "ahbgdc"`

```
i=0, s[0]='a': scan t from 0 → t[0]='a' match! searchPos=1
i=1, s[1]='b': scan t from 1 → t[1]='h' no, t[2]='b' match! searchPos=3
i=2, s[2]='c': scan t from 3 → t[3]='g' no, t[4]='d' no, t[5]='c' match! searchPos=6
All matched → true ✅
```

### Walkthrough: `s = "axc", t = "ahbgdc"`

```
i=0, s[0]='a': scan t from 0 → t[0]='a' match! searchPos=1
i=1, s[1]='x': scan t from 1 → 'h','b','g','d','c' → 'x' NOT found
Return false ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(M × N) — for each char in s, scan through t |
| **Space** | O(1) |

---

## 🧩 Method 2: Two Pointer (Optimal)

### Core Idea

Single pass through `t`. Use one pointer for `s` (`sIndex`) and one for `t` (`tIndex`). Advance `sIndex` only when characters match. If `sIndex` reaches the end of `s`, all matched.

### Walkthrough: `s = "abc", t = "ahbgdc"`

```
t[0]='a' == s[0]='a' → sIndex=1
t[1]='h' != s[1]='b' → skip
t[2]='b' == s[1]='b' → sIndex=2
t[3]='g' != s[2]='c' → skip
t[4]='d' != s[2]='c' → skip
t[5]='c' == s[2]='c' → sIndex=3
sIndex=3 == s.length → true ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass through t |
| **Space** | O(1) |

---

## 🧩 Method 3: Recursive

### Core Idea

At each step, either match (advance both) or skip (advance only `t`). Base cases: `i == s.length` → true, `j == t.length` → false.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) — recursion stack |

---

## 📊 Comparison

| Method | Time | Space |
|--------|------|-------|
| Brute Force | O(M × N) | O(1) |
| Two Pointer | O(N) | O(1) |
| Recursive | O(N) | O(N) |

---

## 🔑 Key Takeaways

1. **Brute force rescan:** For each char in `s`, scan `t` from the last match position — intuitive but O(M×N).
2. **Two pointer is optimal:** Single pass through `t`, advancing `s` pointer only on matches — O(N).
3. **Greedy matching:** Match each character of `s` as early as possible — always optimal.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Is Subsequence | [#392](https://leetcode.com/problems/is-subsequence/) | Easy |
| Number of Matching Subsequences | [#792](https://leetcode.com/problems/number-of-matching-subsequences/) | Medium |
| Shortest Way to Form String | [#1055](https://leetcode.com/problems/shortest-way-to-form-string/) | Medium |
