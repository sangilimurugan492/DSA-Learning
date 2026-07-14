# GameOfLife — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/game-of-life/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/game-of-life/
 *
 * Given the current state of an m x n board of cells (1 = live, 0 = dead),
 * compute the next state simultaneously:
 * - Live cell with <2 live neighbors dies (underpopulation)
 * - Live cell with 2-3 live neighbors lives
 * - Live cell with >3 live neighbors dies (overpopulation)
 * - Dead cell with exactly 3 live neighbors becomes live (reproduction)
 *
 * Must be done in-place.
 *
 * Example:
 *
 * Input: board = [[0,1,0],[0,0,1],[1,1,1],[0,0,0]]
 * Output: [[0,0,0],[1,0,1],[0,1,1],[0,1,0]]

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `GameOfLife.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `GameOfLife.kt` for details.

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
| GameOfLife | [https://leetcode.com/problems/game-of-life/](https://leetcode.com/problems/game-of-life/) | Medium |
