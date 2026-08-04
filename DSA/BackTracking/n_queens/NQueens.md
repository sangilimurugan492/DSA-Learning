# N-Queens — Detailed Explanation

> **LeetCode #51** | [Problem Link](https://leetcode.com/problems/n-queens/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic backtracking — must know)
> **Topic:** Backtracking, Recursion, Matrix

---

## 📋 Problem Statement

Place N queens on an N×N chessboard so that no two queens attack each other. Return all distinct solutions.

### Example — N=4

```
Solution 1:        Solution 2:
. Q . .            . . Q .
. . . Q            Q . . .
Q . . .            . . . Q
. . Q .            . Q . .
```

---

## 🧩 Method 1: Backtracking with Sets — O(N!)

### Core Idea

Place queens row by row. For each row, try every column. Skip columns/diagonals that are under attack. Use sets to track attacked positions.

### Key Insight

> Queens attack in 3 ways: same column, same main diagonal (row - col = constant), same anti-diagonal (row + col = constant). Track these with 3 sets. Place one queen per row — this guarantees no row conflicts.

### Dry Run — N=4

```
backtrack(0):
  col=0: place Q at (0,0), cols={0}, diag1={0}, diag2={0}
    backtrack(1):
      col=0: in cols → skip
      col=1: (1-1=0) in diag1 → skip
      col=2: place Q at (1,2), cols={0,2}, diag1={0,-1}, diag2={0,3}
        backtrack(2):
          col=0: in cols → skip
          col=1: (2-1=1) not in diag1, (2+1=3) in diag2 → skip
          col=2: in cols → skip
          col=3: (2-3=-1) in diag1 → skip
          → no valid column, backtrack
      col=3: place Q at (1,3), cols={0,3}, diag1={0,-2}, diag2={0,4}
        backtrack(2):
          col=1: (2-1=1) not in sets, (2+1=3) not in sets → place Q at (2,1)
            backtrack(3):
              col=2: (3-2=1) not in sets, (3+2=5) not in sets → place Q at (3,2)
                backtrack(4): row==n → solution found! ✅
```

### Code

```kotlin
fun solveNQueens(n: Int): List<List<String>> {
    val results = mutableListOf<List<String>>()
    val cols = mutableSetOf<Int>()
    val diag1 = mutableSetOf<Int>()  // row - col
    val diag2 = mutableSetOf<Int>()  // row + col
    val queens = IntArray(n)  // queens[row] = col

    fun backtrack(row: Int) {
        if (row == n) {
            val solution = (0 until n).map { r ->
                ".".repeat(queens[r]) + "Q" + ".".repeat(n - queens[r] - 1)
            }
            results.add(solution)
            return
        }
        for (col in 0 until n) {
            if (col in cols || (row - col) in diag1 || (row + col) in diag2) continue
            queens[row] = col
            cols.add(col); diag1.add(row - col); diag2.add(row + col)
            backtrack(row + 1)
            cols.remove(col); diag1.remove(row - col); diag2.remove(row + col)
        }
    }

    backtrack(0)
    return results
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N!) | First row: N choices, second: ≤ N-2, etc. |
| **Space** | O(N²) | Board + sets + recursion |

---

## 📊 Key Patterns

| Concept | Implementation |
|---------|---------------|
| Column attack | `cols` set |
| Main diagonal | `row - col` (constant per diagonal) |
| Anti-diagonal | `row + col` (constant per diagonal) |
| One queen per row | Guarantees no row conflict |
| Backtrack | Add to sets → recurse → remove from sets |

> **Interview Tip:** The diagonal trick is the key insight: `row - col` is constant along main diagonals, `row + col` is constant along anti-diagonals. This avoids O(N) diagonal checks. Place queens row by row (one per row) — this eliminates row conflicts by construction. N-Queens II (LeetCode #52) asks for the count only — same logic, just count instead of collecting solutions.
