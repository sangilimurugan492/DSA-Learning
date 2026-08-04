# BackspaceStringCompare — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/backspace-string-compare/description/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/backspace-string-compare/description/
 * Given two strings s and t, return true if they are equal when both are typed into empty text editors. '#' means a backspace character.
 *
 * Note that after backspacing an empty text, the text will continue empty.
 *
 * Example 1:
 *
 * Input: s = "ab#c", t = "ad#c"
 * Output: true
 * Explanation: Both s and t become "ac".
 * Example 2:
 *
 * Input: s = "ab##", t = "c#d#"
 * Output: true
 * Explanation: Both s and t become "".

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `BackspaceStringCompare.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `BackspaceStringCompare.kt` for details.

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
| BackspaceStringCompare | [https://leetcode.com/problems/backspace-string-compare/description/](https://leetcode.com/problems/backspace-string-compare/description/) | Medium |
