# Sudoku Solver — Detailed Explanation

> **LeetCode #37** | [Problem Link](https://leetcode.com/problems/sudoku-solver/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic backtracking — must know)
> **Topic:** Backtracking, Matrix, Recursion

---

## 📋 Problem Statement

Solve a 9×9 Sudoku puzzle by filling empty cells (marked with `'.'`). Each row, column, and 3×3 box must contain digits 1-9 exactly once. Modify the board in-place.

### Example

```
Input (partial):          Output (solved):
5 3 . . 7 . . . .         5 3 4 6 7 8 9 1 2
6 . . 1 9 5 . . .         6 7 2 1 9 5 3 4 8
. 9 8 . . . . 6 .         1 9 8 3 4 2 5 6 7
8 . . . 6 . . . 3         8 5 9 7 6 1 4 2 3
4 . . 8 . 3 . . 1         4 2 6 8 5 3 7 9 1
7 . . . 2 . . . 6         7 1 3 9 2 4 8 5 6
. 6 . . . . 2 8 .         9 6 1 5 3 7 2 8 4
. . . 4 1 9 . . 5         2 8 7 4 1 9 6 3 5
. . . . 8 . . 7 9         3 4 5 2 8 6 1 7 9
```

---

## 🧩 Method 1: Backtracking — O(9^N)

### Core Idea

Scan the board for an empty cell. Try digits 1-9. If valid, place it and recurse. If recursion fails, backtrack (reset to `'.'`). If no digit works, return false to trigger backtracking in the caller.

### Key Insight

> The 3×3 box check uses `(row / 3) * 3` and `(col / 3) * 3` to find the top-left corner of the box. This integer division trick maps any cell to its box origin.

### Dry Run (simplified)

```
solveSudoku():
  Find first empty cell at (0,2)
  Try '1': isValid? row 0 has no '1', col 2 has no '1', box has no '1' → valid
    Place '1', recurse...
    Find next empty cell at (0,3)
    Try '1': row 0 already has '1' → invalid
    Try '2': ... valid → place, recurse...
    ... (deep recursion)
    If all fail → backtrack: reset (0,2) to '.'
  Try '2': ...
  Try '4': valid → place, recurse → eventually solves ✅
```

### Code

```kotlin
fun solveSudoku(board: Array<CharArray>): Boolean {
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            if (board[row][col] == '.') {
                for (num in '1'..'9') {
                    if (isValid(board, row, col, num)) {
                        board[row][col] = num
                        if (solveSudoku(board)) return true
                        board[row][col] = '.'  // Backtrack
                    }
                }
                return false  // No valid number → trigger backtrack
            }
        }
    }
    return true  // All cells filled → solved
}

private fun isValid(board: Array<CharArray>, row: Int, col: Int, num: Char): Boolean {
    for (c in 0 until 9) if (board[row][c] == num) return false  // Row
    for (r in 0 until 9) if (board[r][col] == num) return false  // Column
    val boxRow = (row / 3) * 3
    val boxCol = (col / 3) * 3
    for (r in boxRow until boxRow + 3)
        for (c in boxCol until boxCol + 3)
            if (board[r][c] == num) return false  // Box
    return true
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(9^N) | N = empty cells, 9 choices each |
| **Space** | O(N) | Recursion stack |

---

## 🧩 Method 2: Optimized with Sets — O(9^N)

### Core Idea

Use sets to track used numbers in each row, column, and box. O(1) validation instead of O(9).

### Code

```kotlin
fun solveSudokuOptimized(board: Array<CharArray>): Boolean {
    val rows = Array(9) { mutableSetOf<Char>() }
    val cols = Array(9) { mutableSetOf<Char>() }
    val boxes = Array(9) { mutableSetOf<Char>() }

    // Initialize sets with existing numbers
    for (r in 0 until 9) {
        for (c in 0 until 9) {
            if (board[r][c] != '.') {
                val num = board[r][c]
                rows[r].add(num)
                cols[c].add(num)
                boxes[(r / 3) * 3 + c / 3].add(num)
            }
        }
    }

    fun solve(): Boolean {
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (board[r][c] == '.') {
                    val boxIdx = (r / 3) * 3 + c / 3
                    for (num in '1'..'9') {
                        if (num !in rows[r] && num !in cols[c] && num !in boxes[boxIdx]) {
                            board[r][c] = num
                            rows[r].add(num); cols[c].add(num); boxes[boxIdx].add(num)
                            if (solve()) return true
                            board[r][c] = '.'
                            rows[r].remove(num); cols[c].remove(num); boxes[boxIdx].remove(num)
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    return solve()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(9^N) | Same worst case, faster validation |
| **Space** | O(N) | Recursion + sets |

---

## 📊 Method Comparison

| Method | Time | Space | Validation | When to Use |
|--------|------|-------|-----------|-------------|
| Linear scan | O(9^N) | O(N) | O(9) per check | Simpler code |
| Sets | O(9^N) | O(N) | O(1) per check | Faster in practice |

> **Interview Tip:** The key pattern: find empty cell → try all digits → validate → recurse → backtrack. The box index trick `(row/3)*3 + col/3` maps any cell to its 3×3 box index (0-8). This is the same backtracking pattern as N-Queens — try, validate, recurse, undo. The puzzle is guaranteed to have a unique solution.
