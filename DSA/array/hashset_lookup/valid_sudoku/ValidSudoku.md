# ValidSudoku — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/valid-sudoku/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/valid-sudoku/
 *
 * Determine if a 9 x 9 Sudoku board is valid. Only the filled cells need to be validated
 * according to the following rules:
 * - Each row must contain the digits 1-9 without repetition
 * - Each column must contain the digits 1-9 without repetition
 * - Each of the nine 3 x 3 sub-boxes must contain the digits 1-9 without repetition
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Amazon, Apple)
 *
 * Key Insight: Use HashSets for each row, column, and 3x3 box.
 * Box index = (row / 3) * 3 + (col / 3) — maps each cell to its 3x3 box.
 */
 * Time Complexity O(N²) where N = 9
 * Space Complexity O(N²)

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `ValidSudoku.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `ValidSudoku.kt` for details.

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
| ValidSudoku | [https://leetcode.com/problems/valid-sudoku/](https://leetcode.com/problems/valid-sudoku/) | Medium |
