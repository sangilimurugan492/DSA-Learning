package dp.two_d.unique_paths

/**
 * Unique Paths — LeetCode #62
 * https://leetcode.com/problems/unique-paths/
 *
 * Problem:
 * -------
 * A robot is at top-left corner of an m×n grid. It can only move right or down.
 * How many possible unique paths to reach the bottom-right corner?
 *
 * Example:  m=3, n=7  →  28
 *           m=3, n=2  →  3
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic 2D DP + Combinatorics)
 *
 * Recurrence: dp[i][j] = dp[i-1][j] + dp[i][j-1]
 * Base case: dp[0][j] = 1, dp[i][0] = 1 (first row/col = 1 path)
 *
 * Two approaches:
 * 1. 2D DP: O(M × N) time, O(M × N) space
 * 2. Math (Combinatorics): O(M) time, O(1) space — C(m+n-2, m-1)
 */

fun main() {
    val m = 3
    val n = 7

    println("=== Method 1: 2D DP ===")
    println("uniquePaths($m, $n) = ${uniquePathsDP(m, n)}")

    println("\n=== Method 2: Math (Combinatorics) ===")
    println("uniquePaths($m, $n) = ${uniquePathsMath(m, n)}")

    println("\n=== Step-by-step trace ===")
    uniquePathsTrace(m, n)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: 2D DP — O(M × N) time and space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * 2D DP — dp[i][j] = number of paths to cell (i, j).
 *
 * Core Idea:
 *   - To reach (i, j), you came from above (i-1, j) or left (i, j-1).
 *   - dp[i][j] = dp[i-1][j] + dp[i][j-1].
 *   - First row and first col = 1 (only one way — all right or all down).
 *
 * Key Insight:
 *   - Same as grid DP: sum paths from top and left neighbors.
 *
 * Time Complexity:  O(M × N) — fill 2D table.
 * Space Complexity: O(M × N) — 2D dp array.
 */
fun uniquePathsDP(m: Int, n: Int): Int {
    val dp = Array(m) { IntArray(n) }

    // First row and first column = 1.
    for (i in 0 until m) dp[i][0] = 1
    for (j in 0 until n) dp[0][j] = 1

    for (i in 1 until m) {
        for (j in 1 until n) {
            dp[i][j] = dp[i - 1][j] + dp[i][j - 1]
        }
    }
    return dp[m - 1][n - 1]
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MATH (Combinatorics) — O(M) time, O(1) space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MATH — The robot makes (m-1) down moves and (n-1) right moves = (m+n-2) total moves.
 * Choose (m-1) of them to be down: C(m+n-2, m-1).
 *
 * Core Idea:
 *   - Total moves = (m-1) down + (n-1) right = m+n-2.
 *   - Number of paths = C(m+n-2, m-1) = (m+n-2)! / ((m-1)! × (n-1)!).
 *
 * Key Insight:
 *   - This is a combinatorics problem — choose which moves are "down" (or "right").
 *
 * Time Complexity:  O(min(M,N)) — compute combination.
 * Space Complexity: O(1) — no extra space.
 */
fun uniquePathsMath(m: Int, n: Int): Int {
    val total = m + n - 2
    val choose = minOf(m - 1, n - 1)  // Choose smaller for efficiency.
    var result = 1L
    for (i in 0 until choose) {
        result = result * (total - i) / (i + 1)
    }
    return result.toInt()
}

/**
 * 2D DP with step-by-step trace.
 */
fun uniquePathsTrace(m: Int, n: Int) {
    val dp = Array(m) { IntArray(n) }
    for (i in 0 until m) dp[i][0] = 1
    for (j in 0 until n) dp[0][j] = 1
    println("Grid: $m × $n")

    for (i in 1 until m) {
        for (j in 1 until n) {
            dp[i][j] = dp[i - 1][j] + dp[i][j - 1]
            println("  [$i][$j] = ${dp[i-1][j]} (top) + ${dp[i][j-1]} (left) = ${dp[i][j]}")
        }
    }
    println("  Result: ${dp[m - 1][n - 1]}")
}
