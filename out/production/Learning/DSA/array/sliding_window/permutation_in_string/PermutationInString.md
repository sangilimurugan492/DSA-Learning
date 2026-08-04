# PermutationInString — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/permutation-in-string/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/permutation-in-string/
 * Given strings s1 and s2, return true if s2 contains a permutation of s1.
 * Example: s1 = "ab", s2 = "eidbaooo" → true ("ba" at index 3)
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */
 * BRUTE FORCE: O(N × M log M) — check every substring of length M
 * Sort each substring and compare with sorted s1.
 */
 * OPTIMAL: O(N) Sliding Window with frequency match

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `PermutationInString.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `PermutationInString.kt` for details.

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
| PermutationInString | [https://leetcode.com/problems/permutation-in-string/](https://leetcode.com/problems/permutation-in-string/) | Medium |
