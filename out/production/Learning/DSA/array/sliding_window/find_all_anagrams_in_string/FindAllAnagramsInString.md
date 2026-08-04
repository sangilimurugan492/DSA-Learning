# FindAllAnagramsInString — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/find-all-anagrams-in-a-string/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/find-all-anagrams-in-a-string/
 * Given strings s and p, return start indices of p's anagrams in s.
 * Example: s = "cbaebabacd", p = "abc" → [0,6]
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */
 * BRUTE FORCE: O(N × M log M) — check every substring of length M
 * Sort each substring and compare with sorted p.
 */

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `FindAllAnagramsInString.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `FindAllAnagramsInString.kt` for details.

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
| FindAllAnagramsInString | [https://leetcode.com/problems/find-all-anagrams-in-a-string/](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | Medium |
