package BackTracking

/**
 * https://leetcode.com/problems/word-search/
 *
 * Given an m×n grid of characters and a string word, return true if word exists
 * in the grid. The word can be constructed from adjacent cells (horizontal/vertical).
 * Same cell may NOT be used more than once.
 *
 * Example: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]]
 *          word = "ABCCED" → true (A→B→C→C→E→D path exists)
 *          word = "SEE"    → true
 *          word = "ABCB"   → false (can't reuse B)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Grid backtracking — must-know pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is THE grid backtracking problem. Pattern:
 *   1. Find starting cell (matches word[0])
 *   2. DFS from that cell, marking visited (change to '#' temporarily)
 *   3. Try all 4 directions (up/down/left/right)
 *   4. If all chars matched → return true
 *   5. Backtrack: restore original character
 *
 * KEY TRICK: Mark visited by changing board[r][c] to '#' (non-letter).
 *            This avoids a separate visited array — O(1) space!
 *            Restore after backtracking.
 *
 * Connection to other problems:
 *   Word Search → Single word, grid DFS
 *   Word Search II → Multiple words, use Trie to optimize
 *   Number of Islands → Same grid DFS but without backtracking
 *   Pacific Atlantic → Same grid DFS from multiple sources
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Word Search ===")
    val board = arrayOf(
        charArrayOf('A', 'B', 'C', 'E'),
        charArrayOf('S', 'F', 'C', 'S'),
        charArrayOf('A', 'D', 'E', 'E')
    )
    println("ABCCED: ${exist(board, "ABCCED")}")  // true
    println("SEE:    ${exist(board, "SEE")}")     // true
    println("ABCB:   ${exist(board, "ABCB")}")    // false
}

/**
 * Grid Backtracking
 * Time Complexity: O(N × 4^L) — N cells, L = word length, 4 directions
 * Space Complexity: O(L) — recursion depth
 *
 * Trace for "SEE" on the board:
 * Start at (1,3)='S' → matches 'S'
 *   Try (0,3)='E' → matches 'E'
 *     Try (0,2)='C' → ≠ 'E' ✗
 *     Try (1,2)='C' → ≠ 'E' ✗
 *     Try (2,3)='E' → matches 'E' → ALL MATCHED → return true ✅
 */
fun exist(board: Array<CharArray>, word: String): Boolean {
    val rows = board.size
    val cols = board[0].size
    val directions = arrayOf(intArrayOf(0, 1), intArrayOf(0, -1), intArrayOf(1, 0), intArrayOf(-1, 0))

    fun dfs(r: Int, c: Int, index: Int): Boolean {
        // Base case: all characters matched
        if (index == word.length) return true

        // Out of bounds or character doesn't match
        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != word[index]) return false

        // Mark as visited (temporarily change to non-letter)
        val temp = board[r][c]
        board[r][c] = '#'

        // Try all 4 directions
        for (dir in directions) {
            val nr = r + dir[0]
            val nc = c + dir[1]
            if (dfs(nr, nc, index + 1)) {
                board[r][c] = temp  // Restore before returning
                return true
            }
        }

        // Backtrack: restore original character
        board[r][c] = temp
        return false
    }

    // Try starting from every cell
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (dfs(r, c, 0)) return true
        }
    }
    return false
}
