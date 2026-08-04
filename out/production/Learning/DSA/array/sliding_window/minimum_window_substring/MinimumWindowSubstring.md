# MinimumWindowSubstring — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/minimum-window-substring/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/minimum-window-substring/
 * Given strings s and t, return minimum window substring of s containing all chars of t.
 * Example: s = "ADOBECODEBANC", t = "ABC" → "BANC"
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE hardest sliding window)
 */
 * BRUTE FORCE: O(N² × M) — check every substring
 * Time: O(N²) substrings × O(M) to check if contains all of t
 * Space: O(N) for substring copies
 */

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `MinimumWindowSubstring.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `MinimumWindowSubstring.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. See the `.kt` file for full implementation and inline comments.
2. Refer to the LeetCode problem for detailed examples.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| MinimumWindowSubstring | [https://leetcode.com/problems/minimum-window-substring/](https://leetcode.com/problems/minimum-window-substring/) | Medium |
