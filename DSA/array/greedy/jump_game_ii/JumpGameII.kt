package array.greedy.jump_game_ii

/**
 * Jump Game II — LeetCode #45
 * https://leetcode.com/problems/jump-game-ii/
 *
 * Problem:
 * -------
 * Return the minimum number of jumps to reach the last index.
 *
 * Example:  [2,3,1,1,4] → 2  (0→1→4)
 *           [2,3,0,1,4] → 2
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Two approaches:
 * 1. DP: O(N²) — dp[i] = min jumps to reach index i
 * 2. BFS-like Greedy: O(N) — track levels (jump ranges)
 */

fun main() {
    println("=== Method 1: DP ===")
    println("jump([2,3,1,1,4]) = ${jumpDP(intArrayOf(2, 3, 1, 1, 4))}")
    println("jump([2,3,0,1,4]) = ${jumpDP(intArrayOf(2, 3, 0, 1, 4))}")

    println("\n=== Method 2: BFS-like Greedy ===")
    println("jump([2,3,1,1,4]) = ${jumpGameII(intArrayOf(2, 3, 1, 1, 4))}")
    println("jump([2,3,0,1,4]) = ${jumpGameII(intArrayOf(2, 3, 0, 1, 4))}")
    println("jump([1,1,1,1]) = ${jumpGameII(intArrayOf(1, 1, 1, 1))}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: DP — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DP — dp[i] = min jumps to reach index i. For each i, check all j < i.
 *
 * Time Complexity:  O(N²).
 * Space Complexity: O(N).
 */
fun jumpDP(nums: IntArray): Int {
    val n = nums.size
    val dp = IntArray(n) { Int.MAX_VALUE }
    dp[0] = 0

    for (i in 1 until n) {
        for (j in 0 until i) {
            if (dp[j] != Int.MAX_VALUE && j + nums[j] >= i) {
                dp[i] = minOf(dp[i], dp[j] + 1)
            }
        }
    }
    return dp[n - 1]
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BFS-LIKE GREEDY — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BFS-LIKE GREEDY — Think of it as BFS levels. Each jump = one level.
 *
 * Core Idea:
 *   - Track currentEnd (end of current level) and farthest (farthest reachable in next level).
 *   - When i == currentEnd → jump to next level (jumps++, currentEnd = farthest).
 *
 * Key Insight:
 *   - Level 0: index 0 (can reach indices 1..2)
 *   - Level 1: indices 1..2 (can reach indices 2..4)
 *   - Level 2: reached the end!
 *
 * Time Complexity:  O(N).
 * Space Complexity: O(1).
 */
fun jumpGameII(nums: IntArray): Int {
    if (nums.size <= 1) return 0

    var jumps = 0
    var currentEnd = 0
    var farthest = 0

    for (i in 0 until nums.size - 1) {
        farthest = maxOf(farthest, i + nums[i])

        if (i == currentEnd) {
            jumps++
            currentEnd = farthest
            if (currentEnd >= nums.size - 1) break
        }
    }

    return jumps
}
