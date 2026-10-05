package array.matrix.game_of_life

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

    println("=== Brute Force (Extra Space) ===")
    val board1 = board.map { it.copyOf() }.toTypedArray()
    gameOfLifeBruteForce(board1)
    board1.forEach { println(it.toList()) }

    println("=== Optimal (In-Place) ===")
    val board2 = board.map { it.copyOf() }.toTypedArray()
    gameOfLife(board2)
    board2.forEach { println(it.toList()) }
}

// ──────────────────────────────────────────────────────────────
// Method 1: Brute Force — Copy the board, read from copy, write to original
// ──────────────────────────────────────────────────────────────

/**
 * Brute Force: Make a full copy of the board.
 * Read live neighbor counts from the copy (original state),
 * write the next state directly to the original board.
 *
 * Time Complexity: O(M × N) — each cell visited once, 8 neighbors checked
 * Space Complexity: O(M × N) — full copy of the board
 */
fun gameOfLifeBruteForce(board: Array<IntArray>) {
    val m = board.size
    val n = board[0].size

    // Make a copy of the original board
    val copy = board.map { it.copyOf() }.toTypedArray()

    val dirs = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)

    for (i in 0 until m) {
        for (j in 0 until n) {
            // Count live neighbors from the COPY (original state)
            var liveNeighbors = 0
            for ((di, dj) in dirs) {
                val ni = i + di
                val nj = j + dj
                if (ni in 0 until m && nj in 0 until n && copy[ni][nj] == 1) {
                    liveNeighbors++
                }
            }

            // Apply rules and write to the ORIGINAL board
            when {
                // Live cell with <2 or >3 neighbors dies
                copy[i][j] == 1 && (liveNeighbors !in 2..3) -> board[i][j] = 0
                // Dead cell with exactly 3 neighbors becomes live
                copy[i][j] == 0 && liveNeighbors == 3 -> board[i][j] = 1
                // Otherwise, state stays the same (already in board from copy)
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// Method 2: Optimal — In-Place with Intermediate States
// ──────────────────────────────────────────────────────────────

/**
 * Optimal: Use intermediate values to encode transitions in-place:
 *   0 → 1 = 2 (dead to live)
 *   1 → 0 = 3 (live to dead)
 * Original live = 1 or 3 (was live, now dead)
 * Original dead = 0 or 2 (was dead, now live)
 *
 * Time Complexity: O(M × N) — two passes over the board
 * Space Complexity: O(1) — no extra board, only a few variables
 */
fun gameOfLife(board: Array<IntArray>) {
    val m = board.size
    val n = board[0].size

    // Directions for 8 neighbors
    val dirs = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)

    // First pass: mark transitions with intermediate values
    for (i in 0 until m) {
        for (j in 0 until n) {
            val liveNeighbors = countNeighbors(board, i, j, dirs)
            when {
                board[i][j] == 1 && (liveNeighbors !in 2..3) ->
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

fun countNeighbors(board: Array<IntArray>, i : Int, j : Int, dirs : Array<Pair<Int, Int>>): Int {
    var liveNeighbors = 0
    for ((di, dj) in dirs) {
        val ni = i + di
        val nj = j + dj
        if (ni in 0 until board.size && nj in 0 until board[0].size) {
            // Original live = 1 or 3 (was live, now dead)
            if (board[ni][nj] == 1 || board[ni][nj] == 3) liveNeighbors++
        }
    }
    return liveNeighbors
}
