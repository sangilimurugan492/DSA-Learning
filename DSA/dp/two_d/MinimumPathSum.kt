package dp.two_d

/**
 * https://leetcode.com/problems/minimum-path-sum/
 *
 * Given a m×n grid filled with non-negative numbers, find a path from top-left
 * to bottom-right which minimizes the sum of all numbers along the path.
 * You can only move RIGHT or DOWN.
 *
 * Example 1: grid = [[1,3,1],[1,5,1],[4,2,1]] → Output: 7 (1→3→1→1→1)
 * Example 2: grid = [[1,2,3],[4,5,6]] → Output: 12 (1→2→3→6)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Unique Paths variant — minimization on grid)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is Unique Paths' "minimization" cousin.
 *
 * Unique Paths:     "How many WAYS to reach (m-1,n-1)?" → COUNT
 * Minimum Path Sum: "What's the MIN COST to reach (m-1,n-1)?" → MINIMIZE
 *
 * Key question: "How can I reach cell (r, c) with minimum cost?"
 *   → From ABOVE: (r-1, c) — moved DOWN, cost = dp[r-1][c] + grid[r][c]
 *   → From LEFT:  (r, c-1) — moved RIGHT, cost = dp[r][c-1] + grid[r][c]
 * So: dp[r][c] = min(dp[r-1][c], dp[r][c-1]) + grid[r][c]
 *
 * Base cases:
 *   - dp[0][0] = grid[0][0] (starting cell)
 *   - First row: dp[0][c] = dp[0][c-1] + grid[0][c] (can only come from left)
 *   - First col: dp[r][0] = dp[r-1][0] + grid[r][0] (can only come from above)
 *
 * Connection to other problems:
 *   - Unique Paths: same movement, but COUNT paths instead of MINIMIZE cost
 *   - Min Cost Climbing Stairs: 1D version of this problem!
 *   - Both are "reach the end with minimum cost" — just 1D vs 2D
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Minimum Path Sum ===")
    println("Brute Force [[1,3,1],[1,5,1],[4,2,1]]: ${minPathSumBruteForce(arrayOf(intArrayOf(1,3,1), intArrayOf(1,5,1), intArrayOf(4,2,1)))}")
    println("Memoization [[1,3,1],[1,5,1],[4,2,1]]: ${minPathSumMemo(arrayOf(intArrayOf(1,3,1), intArrayOf(1,5,1), intArrayOf(4,2,1)))}")
    println("Tabulation  [[1,3,1],[1,5,1],[4,2,1]]: ${minPathSumTabulation(arrayOf(intArrayOf(1,3,1), intArrayOf(1,5,1), intArrayOf(4,2,1)))}")
    println("Optimal     [[1,3,1],[1,5,1],[4,2,1]]: ${minPathSumOptimal(arrayOf(intArrayOf(1,3,1), intArrayOf(1,5,1), intArrayOf(4,2,1)))}")
    println("---")
    println("Optimal [[1,2,3],[4,5,6]]: ${minPathSumOptimal(arrayOf(intArrayOf(1,2,3), intArrayOf(4,5,6)))}")
}

/**
 * BRUTE FORCE — Recursion
 * Time Complexity: O(2^(m+n)) — at each cell, 2 choices (right or down)
 * Space Complexity: O(m+n) — recursion depth
 *
 * From each cell, try going right and going down. Take the minimum.
 *
 * Recursion tree for 3×3 grid (simplified):
 *              (0,0)
 *            /       \
 *         (1,0)     (0,1)
 *        /    \     /    \
 *     (2,0) (1,1) (1,1) (0,2)
 *      ...   ...   ...   ...
 *
 * (1,1) computed TWICE! Exponential overlap.
 */
fun minPathSumBruteForce(grid: Array<IntArray>): Int {
    return pathRec(grid, 0, 0)
}

private fun pathRec(grid: Array<IntArray>, r: Int, c: Int): Int {
    val m = grid.size
    val n = grid[0].size
    if (r == m - 1 && c == n - 1) return grid[r][c]  // reached destination
    if (r >= m || c >= n) return Int.MAX_VALUE  // out of bounds
    return grid[r][c] + minOf(pathRec(grid, r + 1, c), pathRec(grid, r, c + 1))
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(m × n) — each cell computed once
 * Space Complexity: O(m × n) — memo + recursion stack
 *
 * Cache result for each cell (r, c).
 *
 * Trace for [[1,3,1],[1,5,1],[4,2,1]]:
 * f(0,0) = 1 + min(f(1,0), f(0,1))
 *   f(1,0) = 1 + min(f(2,0), f(1,1))
 *     f(2,0) = 4 + min(∞, f(2,1))
 *       f(2,1) = 2 + min(∞, f(2,2))
 *         f(2,2) = 1 (base case, cache!)
 *       f(2,1) = 2 + 1 = 3 (cache!)
 *     f(2,0) = 4 + 3 = 7 (cache!)
 *     f(1,1) = 5 + min(f(2,1), f(1,2))
 *       f(2,1) = 3 (cached!)
 *       f(1,2) = 1 + min(∞, f(1,2))... f(1,2) = 1 + f(2,2) = 1 + 1 = 2 (cache!)
 *     f(1,1) = 5 + min(3, 2) = 7 (cache!)
 *   f(1,0) = 1 + min(7, 7) = 8 (cache!)
 *   f(0,1) = 3 + min(f(1,1), f(0,2))
 *     f(1,1) = 7 (cached!)
 *     f(0,2) = 1 + f(1,2) = 1 + 2 = 3 (cache!)
 *   f(0,1) = 3 + min(7, 3) = 6 (cache!)
 * f(0,0) = 1 + min(8, 6) = 7 ✅
 */
fun minPathSumMemo(grid: Array<IntArray>): Int {
    val m = grid.size
    val n = grid[0].size
    val memo = Array(m) { IntArray(n) { -1 } }
    return pathMemo(grid, 0, 0, memo)
}

private fun pathMemo(grid: Array<IntArray>, r: Int, c: Int, memo: Array<IntArray>): Int {
    val m = grid.size
    val n = grid[0].size
    if (r == m - 1 && c == n - 1) return grid[r][c]
    if (r >= m || c >= n) return Int.MAX_VALUE
    if (memo[r][c] != -1) return memo[r][c]
    memo[r][c] = grid[r][c] + minOf(pathMemo(grid, r + 1, c, memo), pathMemo(grid, r, c + 1, memo))
    return memo[r][c]
}

/**
 * OPTIMAL-1 — Bottom-Up DP (Tabulation)
 * Time Complexity: O(m × n)
 * Space Complexity: O(m × n)
 *
 * dp[r][c] = minimum path sum to reach (r, c)
 * dp[0][0] = grid[0][0]
 * First row: dp[0][c] = dp[0][c-1] + grid[0][c]
 * First col: dp[r][0] = dp[r-1][0] + grid[r][0]
 * dp[r][c] = min(dp[r-1][c], dp[r][c-1]) + grid[r][c]
 *
 * Trace for [[1,3,1],[1,5,1],[4,2,1]]:
 *   1  4  5       ← row 0: 1, 1+3=4, 4+1=5
 *   2  7  6       ← row 1: 1+1=2, min(4,2)+5=7, min(7,5)+1=6
 *   6  8  7       ← row 2: 2+4=6, min(7,6)+2=8, min(6,8)+1=7
 *
 * dp[2][2] = 7 ✅
 */
fun minPathSumTabulation(grid: Array<IntArray>): Int {
    val m = grid.size
    val n = grid[0].size
    val dp = Array(m) { IntArray(n) }
    dp[0][0] = grid[0][0]

    // First row
    for (c in 1 until n) dp[0][c] = dp[0][c - 1] + grid[0][c]
    // First column
    for (r in 1 until m) dp[r][0] = dp[r - 1][0] + grid[r][0]
    // Fill rest
    for (r in 1 until m) {
        for (c in 1 until n) {
            dp[r][c] = minOf(dp[r - 1][c], dp[r][c - 1]) + grid[r][c]
        }
    }
    return dp[m - 1][n - 1]
}

/**
 * OPTIMAL-2 — Space-Optimized Bottom-Up DP
 * Time Complexity: O(m × n)
 * Space Complexity: O(n) ← only 1 row!
 *
 * Same as Unique Paths space optimization.
 * dp[r][c] only depends on the row above (dp[r-1][c]) and cell to the left (dp[r][c-1]).
 * We can use a single row and update in-place.
 *
 * Trace for [[1,3,1],[1,5,1],[4,2,1]]:
 * Row 0: [1, 4, 5]
 * Row 1: [1+1=2, min(4,2)+5=7, min(5,7)+1=6]  → [2, 7, 6]
 * Row 2: [2+4=6, min(7,6)+2=8, min(6,8)+1=7]  → [6, 8, 7]
 *
 * Result: 7 ✅
 */
fun minPathSumOptimal(grid: Array<IntArray>): Int {
    val m = grid.size
    val n = grid[0].size
    val dp = IntArray(n)

    // Initialize first row
    dp[0] = grid[0][0]
    for (c in 1 until n) dp[c] = dp[c - 1] + grid[0][c]

    // Process remaining rows
    for (r in 1 until m) {
        dp[0] = dp[0] + grid[r][0]  // first column: only from above
        for (c in 1 until n) {
            dp[c] = minOf(dp[c], dp[c - 1]) + grid[r][c]  // min(above, left) + current
        }
    }
    return dp[n - 1]
}
