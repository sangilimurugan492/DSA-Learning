package array.matrix

/**
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
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)
 *
 * Key Insight: Use intermediate states to encode transitions in-place:
 * 0 → 1 = 2 (dead to live), 1 → 0 = 3 (live to dead)
 * This lets us read the original state while writing the new state.
 */
fun main() {
    val board = arrayOf(
        intArrayOf(0, 1, 0),
        intArrayOf(0, 0, 1),
        intArrayOf(1, 1, 1),
        intArrayOf(0, 0, 0)
    )
    gameOfLife(board)
    board.forEach { println(it.toList()) }
}

/**
 * Time Complexity O(M × N)
 * Space Complexity O(1)
 */
fun gameOfLife(board: Array<IntArray>) {
    val m = board.size
    val n = board[0].size

    // Directions for 8 neighbors
    val dirs = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)

    // First pass: mark transitions with intermediate values
    for (i in 0 until m) {
        for (j in 0 until n) {
            var liveNeighbors = 0
            for ((di, dj) in dirs) {
                val ni = i + di
                val nj = j + dj
                if (ni in 0 until m && nj in 0 until n) {
                    // Original live = 1 or 3 (was live, now dead)
                    if (board[ni][nj] == 1 || board[ni][nj] == 3) liveNeighbors++
                }
            }

            when {
                board[i][j] == 1 && (liveNeighbors < 2 || liveNeighbors > 3) ->
                    board[i][j] = 3  // Live → Dead
                board[i][j] == 0 && liveNeighbors == 3 ->
                    board[i][j] = 2  // Dead → Live
            }
        }
    }

    // Second pass: convert intermediate values to final
    for (i in 0 until m) {
        for (j in 0 until n) {
            when (board[i][j]) {
                2 -> board[i][j] = 1
                3 -> board[i][j] = 0
            }
        }
    }
}
