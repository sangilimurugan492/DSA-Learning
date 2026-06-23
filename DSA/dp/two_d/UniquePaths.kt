package dp.two_d

/**
 * https://leetcode.com/problems/unique-paths/
 *
 * A robot is at the top-left corner of an m×n grid and wants to reach
 * the bottom-right corner. It can only move RIGHT or DOWN.
 * How many unique paths are there?
 *
 * Example 1: m=3, n=7 → Output: 28
 * Example 2: m=3, n=2 → Output: 3 (R→D→D, D→R→D, D→D→R)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic 2D grid DP — foundation for all grid problems)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Key question: "How can I reach cell (r, c)?"
 *   → I could have come from ABOVE: (r-1, c) — moved DOWN
 *   → I could have come from LEFT:  (r, c-1) — moved RIGHT
 * So: paths(r,c) = paths(r-1,c) + paths(r,c-1)
 *
 * WHY only these two? Because the robot can ONLY move right or down.
 * It cannot come from above-right or below-left — those moves don't exist.
 *
 * Base cases:
 *   - First row: only 1 way (all RIGHT moves)
 *   - First column: only 1 way (all DOWN moves)
 *
 * This is a 2D version of Climbing Stairs!
 *   Climbing Stairs: 1D, 2 choices → Fibonacci
 *   Unique Paths:    2D, 2 choices → Pascal's Triangle
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Unique Paths ===")
    println("Brute Force (3,7): ${uniquePathsBruteForce(3, 7)}")
    println("Memoization (3,7):  ${uniquePathsMemo(3, 7)}")
    println("Tabulation  (3,7):  ${uniquePathsTabulation(3, 7)}")
    println("Optimal     (3,7):  ${uniquePathsOptimal(3, 7)}")
    println("---")
    println("Optimal (3,2):      ${uniquePathsOptimal(3, 2)}")
}

/**
 * BRUTE FORCE — Recursion
 * Time Complexity: O(2^(m+n)) — exponential, each cell spawns 2 calls
 * Space Complexity: O(m+n) — recursion depth
 *
 * From each cell, try going right and going down.
 *
 * Recursion tree for 3×3 grid (simplified):
 *              (0,0)
 *            /       \
 *         (1,0)     (0,1)
 *        /    \     /    \
 *     (2,0) (1,1) (1,1) (0,2)
 *      |    / \    / \     |
 *    (2,1)(2,1)(1,2)(2,1)(1,2)
 *     ...  (1,1) computed MULTIPLE times!
 *
 * Massive overlap — same cells recomputed over and over.
 */
fun uniquePathsBruteForce(m: Int, n: Int): Int {
    return pathFrom(0, 0, m - 1, n - 1)
}

private fun pathFrom(r: Int, c: Int, destR: Int, destC: Int): Int {
    if (r > destR || c > destC) return 0
    if (r == destR && c == destC) return 1
    return pathFrom(r + 1, c, destR, destC) + pathFrom(r, c + 1, destR, destC)
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(m × n) — each cell computed once
 * Space Complexity: O(m × n) — memo + recursion stack
 *
 * Cache result for each cell (r, c).
 *
 * Trace for 3×3 grid:
 * (0,0) → (1,0) + (0,1)
 *   (1,0) → (2,0) + (1,1)
 *     (2,0) → only right moves: (2,1)→(2,2)=1  → (2,0)=1
 *     (1,1) → (2,1) + (1,2)
 *       (2,1) → (2,2)=1  → (2,1)=1
 *       (1,2) → (2,2)=1  → (1,2)=1
 *     (1,1) = 1+1 = 2
 *   (1,0) = 1+2 = 3
 *   (0,1) → (1,1) + (0,2)
 *     (1,1) = 2 (cached!) ← no recomputation!
 *     (0,2) → (1,2) + (0,3)invalid → (1,2)=1 (cached!) → (0,2)=1
 *   (0,1) = 2+1 = 3
 * (0,0) = 3+3 = 6 ✅
 */
fun uniquePathsMemo(m: Int, n: Int): Int {
    val memo = Array(m) { IntArray(n) { -1 } }
    return pathMemo(0, 0, m - 1, n - 1, memo)
}

private fun pathMemo(r: Int, c: Int, destR: Int, destC: Int, memo: Array<IntArray>): Int {
    if (r > destR || c > destC) return 0
    if (r == destR && c == destC) return 1
    if (memo[r][c] != -1) return memo[r][c]
    memo[r][c] = pathMemo(r + 1, c, destR, destC, memo) + pathMemo(r, c + 1, destR, destC, memo)
    return memo[r][c]
}

/**
 * OPTIMAL-1 — Bottom-Up DP (Tabulation)
 * Time Complexity: O(m × n)
 * Space Complexity: O(m × n)
 *
 * Fill the grid from top-left to bottom-right.
 * First row = 1, first column = 1.
 * dp[r][c] = dp[r-1][c] + dp[r][c-1]
 *
 * Trace for 3×3 grid:
 *   1  1  1
 *   1  2  3
 *   1  3  6
 *
 * dp[2][2] = dp[1][2] + dp[2][1] = 3 + 3 = 6 ✅
 */
fun uniquePathsTabulation(m: Int, n: Int): Int {
    val dp = Array(m) { IntArray(n) { 1 } }  // first row & col = 1

    for (r in 1 until m) {
        for (c in 1 until n) {
            dp[r][c] = dp[r - 1][c] + dp[r][c - 1]
        }
    }
    return dp[m - 1][n - 1]
}

/**
 * OPTIMAL-2 — Space-Optimized Bottom-Up DP
 * Time Complexity: O(m × n)
 * Space Complexity: O(n) ← only 1 row instead of full grid!
 *
 * Key insight: dp[r][c] only depends on the row above (dp[r-1][c])
 * and the cell to the left (dp[r][c-1], which is already in current row).
 * So we only need ONE row and update it in-place!
 *
 * Trace for 3×3 grid:
 * Row 0: [1, 1, 1]
 * Row 1: [1, 1+1=2, 1+2=3]  → [1, 2, 3]
 * Row 2: [1, 1+2=3, 2+3=5]  → wait, that's wrong...
 *         Actually: Row 2: [1, 1+2=3, 3+3=6]  → [1, 3, 6]
 *         dp[c] = dp[c] (from above) + dp[c-1] (from left)
 *
 * Result: 6 ✅
 *
 * MATH ALTERNATIVE: C(m+n-2, m-1) = C(m+n-2, n-1)
 * This is combinatorics: choose which m-1 steps are "down" out of m+n-2 total steps.
 */
fun uniquePathsOptimal(m: Int, n: Int): Int {
    val dp = IntArray(n) { 1 }  // first row: all 1s

    for (r in 1 until m) {
        for (c in 1 until n) {
            dp[c] = dp[c] + dp[c - 1]  // above + left
        }
    }
    return dp[n - 1]
}
