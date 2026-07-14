package backtracking.word_search

/**
 * Word Search — LeetCode #79
 * https://leetcode.com/problems/word-search/
 *
 * Problem:
 * -------
 * Given an m×n grid of characters and a word, return true if the word exists in the grid.
 * The word can be constructed from adjacent cells (horizontally/vertically).
 * A cell may not be used more than once.
 *
 * Example:  board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]]
 *           word = "ABCCED"  →  true
 *           word = "SEE"  →  true
 *           word = "ABCB"  →  false
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Backtracking on Grid)
 *
 * Two approaches:
 * 1. Brute Force DFS: O(N × M × 4^L) — try from each cell, no visited optimization
 * 2. Backtracking with Visited: O(N × M × 4^L) — mark visited, prune early
 */

fun main() {
    val board = arrayOf(
        charArrayOf('A', 'B', 'C', 'E'),
        charArrayOf('S', 'F', 'C', 'S'),
        charArrayOf('A', 'D', 'E', 'E')
    )
    val word = "ABCCED"

    println("=== Method 1: Brute Force DFS ===")
    println("exist(\"$word\") = ${existBruteForce(board, word)}")

    println("\n=== Method 2: Backtracking with Visited ===")
    println("exist(\"$word\") = ${existBacktrack(board, word)}")

    println("\n=== Step-by-step trace ===")
    existTrace(board, word)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE DFS — O(N × M × 4^L)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Try starting from each cell. DFS in 4 directions. Use a separate visited set.
 *
 * Core Idea:
 *   - For each cell matching word[0], start DFS.
 *   - Track visited cells in a HashSet.
 *   - No early pruning beyond character matching.
 *
 * Time Complexity:  O(N × M × 4^L) — L = word length, 4 directions per step.
 * Space Complexity: O(L) — recursion depth + visited set.
 */
fun existBruteForce(board: Array<CharArray>, word: String): Boolean {
    val rows = board.size
    val cols = board[0].size

    fun dfs(r: Int, c: Int, index: Int, visited: MutableSet<Pair<Int, Int>>): Boolean {
        if (index == word.length) return true
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false
        if (board[r][c] != word[index]) return false
        if (Pair(r, c) in visited) return false

        visited.add(Pair(r, c))
        val found = dfs(r + 1, c, index + 1, visited) ||
                    dfs(r - 1, c, index + 1, visited) ||
                    dfs(r, c + 1, index + 1, visited) ||
                    dfs(r, c - 1, index + 1, visited)
        visited.remove(Pair(r, c))
        return found
    }

    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (dfs(i, j, 0, mutableSetOf())) return true
        }
    }
    return false
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BACKTRACKING WITH IN-PLACE MARKING — O(N × M × 4^L)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BACKTRACKING — Mark visited cells in-place (temporarily change char). Restore after.
 *
 * Core Idea:
 *   - For each cell matching word[0], start DFS.
 *   - Mark visited by temporarily changing board[r][c] to '#'.
 *   - Restore after backtracking — no extra visited set needed.
 *
 * Key Insight:
 *   - In-place marking avoids HashSet overhead — O(1) space for visited tracking.
 *   - Restore the original char after exploring all 4 directions.
 *
 * Time Complexity:  O(N × M × 4^L) — L = word length.
 * Space Complexity: O(L) — recursion depth only (no visited set).
 */
fun existBacktrack(board: Array<CharArray>, word: String): Boolean {
    val rows = board.size
    val cols = board[0].size

    fun backtrack(r: Int, c: Int, index: Int): Boolean {
        if (index == word.length) return true
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false
        if (board[r][c] != word[index]) return false

        val temp = board[r][c]
        board[r][c] = '#'  // Mark visited.

        val found = backtrack(r + 1, c, index + 1) ||
                    backtrack(r - 1, c, index + 1) ||
                    backtrack(r, c + 1, index + 1) ||
                    backtrack(r, c - 1, index + 1)

        board[r][c] = temp  // Restore.
        return found
    }

    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (backtrack(i, j, 0)) return true
        }
    }
    return false
}

/**
 * Backtracking with step-by-step trace.
 */
fun existTrace(board: Array<CharArray>, word: String) {
    val rows = board.size
    val cols = board[0].size
    println("Word: \"$word\"")

    fun backtrack(r: Int, c: Int, index: Int, path: String): Boolean {
        if (index == word.length) {
            println("  ✅ Found: $path")
            return true
        }
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false
        if (board[r][c] != word[index]) return false

        val temp = board[r][c]
        board[r][c] = '#'
        val indent = "  ".repeat(index)
        println("${indent}[$r,$c]='${temp}' matches word[$index]='${word[index]}' → path=\"$path$temp\"")

        val found = backtrack(r + 1, c, index + 1, path + temp) ||
                    backtrack(r - 1, c, index + 1, path + temp) ||
                    backtrack(r, c + 1, index + 1, path + temp) ||
                    backtrack(r, c - 1, index + 1, path + temp)

        board[r][c] = temp
        return found
    }

    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (board[i][j] == word[0]) {
                println("Starting at [$i,$j]: '${board[i][j]}'")
                if (backtrack(i, j, 0, "")) {
                    println("  Result: true")
                    return
                }
            }
        }
    }
    println("  Result: false")
}
