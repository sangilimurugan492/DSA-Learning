# LongestRepeatingCharacterReplacement — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/longest-repeating-character-replacement/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/longest-repeating-character-replacement/
 * Given string s and integer k, find longest substring after at most k replacements.
 * Example: s = "AABABBA", k = 1 → Output: 4 ("AABA" or "ABBA")
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */
 * BRUTE FORCE: O(N² × 26) — check every substring
 * For each substring, check if (length - maxFreq) <= k
 */

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `LongestRepeatingCharacterReplacement.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `LongestRepeatingCharacterReplacement.kt` for details.

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
| LongestRepeatingCharacterReplacement | [https://leetcode.com/problems/longest-repeating-character-replacement/](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium |
