# Longest Substring Without Repeating Characters — Detailed Explanation

> **LeetCode #3** | [Problem Link](https://leetcode.com/problems/longest-substring-without-repeating-characters/description/)  
> **Topic:** Sliding Window / Two Pointers  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given a string `s`, find the length of the **longest substring** without repeating characters.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `"abcabcbb"` | `3` | `"abc"` |
| `"bbbbb"` | `1` | `"b"` |
| `"pwwkew"` | `3` | `"wke"` |

---

## 🧩 Method 1: Brute Force

### Core Idea

For each starting index, expand right until a duplicate is found. Track the max length.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Sliding Window (Optimal)

### Core Idea

Use two pointers (`left`, `right`) and a HashSet to maintain a window of unique characters:
1. Expand `right` and add `s[right]` to the set.
2. If `s[right]` is already in the set (duplicate), shrink `left` until the duplicate is removed.
3. Track the max window size.

### Walkthrough: `"abcabcbb"`

```
right=0 ('a'): set={a}         window="a"      maxLen=1
right=1 ('b'): set={a,b}       window="ab"     maxLen=2
right=2 ('c'): set={a,b,c}     window="abc"    maxLen=3
right=3 ('a'): 'a' is duplicate → shrink left: remove 'a', left=1
               set={b,c,a}      window="bca"    maxLen=3
right=4 ('b'): 'b' is duplicate → shrink left: remove 'b', left=2
               set={c,a,b}      window="cab"    maxLen=3
...continues similarly

Result: 3 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — each char added/removed at most once |
| **Space** | O(min(N, charset)) |

---

## 🔑 Key Takeaways

1. **Sliding window with HashSet:** The set tracks characters in the current window.
2. **Shrink on duplicate:** When a duplicate is found, move `left` forward until it's resolved.
3. **Each char processed at most twice:** Once when added, once when removed → O(N) total.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Substring Without Repeating Characters | [#3](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium |
| Longest Repeating Character Replacement | [#424](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium |
| Minimum Window Substring | [#76](https://leetcode.com/problems/minimum-window-substring/) | Hard |
