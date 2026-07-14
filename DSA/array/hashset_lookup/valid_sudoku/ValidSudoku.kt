package array.hashset_lookup.valid_sudoku

/**
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
        charArrayOf('.','.','.','.','8','.','.','7','9')
    )
    println(isValidSudoku(board))
}

/**
 * Time Complexity O(N²) where N = 9
 * Space Complexity O(N²)
 */
fun isValidSudoku(board: Array<CharArray>): Boolean {
    val rows = Array(9) { mutableSetOf<Char>() }
    val cols = Array(9) { mutableSetOf<Char>() }
    val boxes = Array(9) { mutableSetOf<Char>() }

    for (i in 0 until 9) {
        for (j in 0 until 9) {
            val c = board[i][j]
            if (c == '.') continue

            val boxIndex = (i / 3) * 3 + (j / 3)

            if (c in rows[i] || c in cols[j] || c in boxes[boxIndex]) {
                return false
            }

            rows[i].add(c)
            cols[j].add(c)
            boxes[boxIndex].add(c)
        }
    }

    return true
}
