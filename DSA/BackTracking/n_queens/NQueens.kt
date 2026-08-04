package patterns.backtracking.n_queens

/**
 * https://leetcode.com/problems/n-queens/
 * Place N queens on an N×N board so no two attack each other.
 * Return all distinct solutions.
 * Example: n=4 → 2 solutions
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic backtracking — must know)
 */

fun main() {
    val solutions = solveNQueens(4)
    println("Solutions for N=4: ${solutions.size}")  // 2
    solutions.forEach { board -> board.forEach { println(it) }; println() }
}

/**
 * Backtracking: O(N!) time, O(N²) space
 * Place queens row by row. For each row, try every column.
 * Use sets to track attacked columns and diagonals.
 */
fun solveNQueens(n: Int): List<List<String>> {
    val results = mutableListOf<List<String>>()
    val cols = mutableSetOf<Int>()           // Attacked columns
    val diag1 = mutableSetOf<Int>()          // row - col (main diagonal)
    val diag2 = mutableSetOf<Int>()          // row + col (anti-diagonal)
    val board = CharArray(n * n) { '.' }.also { } // Not used directly

    val queens = IntArray(n)  // queens[row] = col

    fun backtrack(row: Int) {
        if (row == n) {
            // Build solution
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
