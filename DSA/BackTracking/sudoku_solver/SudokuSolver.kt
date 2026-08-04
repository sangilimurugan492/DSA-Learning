package patterns.backtracking.sudoku_solver

/**
 * https://leetcode.com/problems/sudoku-solver/
 * Solve a 9×9 Sudoku puzzle by filling empty cells ('.').
 * Rules: each row, column, and 3×3 box must contain digits 1-9 exactly once.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic backtracking — must know)
 */

fun main() {
    val board = arrayOf(
        charArrayOf('5','3','.','.','7','.','.','.','.'),
        charArrayOf('6','.','.','1','9','5','.','.','.'),
        charArrayOf('.','9','8','.','.','.','.','6','.'),
        charArrayOf('8','.','.','.','6','.','.','.','3'),
        charArrayOf('4','.','.','8','.','3','.','.','1'),
        charArrayOf('7','.','.','.','2','.','.','.','6'),
        charArrayOf('.','6','.','.','.','.','2','8','.'),
        charArrayOf('.','.','.','4','1','9','.','.','5'),
        charArrayOf('.','.','.','.','8','.','.','7','9'),
    )
    solveSudoku(board)
    board.forEach { println(it.joinToString(" ")) }
}

/**
 * Backtracking: O(9^(N)) where N = empty cells, O(N) space
 * Find empty cell, try digits 1-9, validate, recurse.
 */
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
                return false  // No valid number → backtrack
            }
        }
    }
    return true  // All cells filled → solved
}

/**
 * Check if placing num at (row, col) is valid.
 */
private fun isValid(board: Array<CharArray>, row: Int, col: Int, num: Char): Boolean {
    // Check row
    for (c in 0 until 9) if (board[row][c] == num) return false
    // Check column
    for (r in 0 until 9) if (board[r][col] == num) return false
    // Check 3×3 box
    val boxRow = (row / 3) * 3
    val boxCol = (col / 3) * 3
    for (r in boxRow until boxRow + 3) {
        for (c in boxCol until boxCol + 3) {
            if (board[r][c] == num) return false
        }
    }
    return true
}
