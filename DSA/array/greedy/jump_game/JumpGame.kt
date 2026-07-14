package array.greedy.jump_game

/**
 * Jump Game — LeetCode #55
 * https://leetcode.com/problems/jump-game/
 *
 * Problem:
 * -------
 * You are at index 0. Each element = max jump length from that position.
 * Return true if you can reach the last index.
 *
 * Example:  [2,3,1,1,4] → true  (0→1→4)
 *           [3,2,1,0,4] → false (stuck at index 3)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Two approaches:
 * 1. DP: O(N²) — dp[i] = can reach index i?
 * 2. Greedy: O(N) — track farthest reachable index
 */

fun main() {
    println("=== Method 1: DP ===")
    println("canJump([2,3,1,1,4]) = ${canJumpDP(intArrayOf(2, 3, 1, 1, 4))}")
    println("canJump([3,2,1,0,4]) = ${canJumpDP(intArrayOf(3, 2, 1, 0, 4))}")

    println("\n=== Method 2: Greedy ===")
    println("canJump([2,3,1,1,4]) = ${canJump(intArrayOf(2, 3, 1, 1, 4))}")
    println("canJump([3,2,1,0,4]) = ${canJump(intArrayOf(3, 2, 1, 0, 4))}")
    println("canJump([0]) = ${canJump(intArrayOf(0))}")
    println("canJump([1,0,1,0]) = ${canJump(intArrayOf(1, 0, 1, 0))}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: DP — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DP — dp[i] = true if we can reach index i.
 * For each i, check all j < i: if dp[j] && j + nums[j] >= i → dp[i] = true.
 *
 * Core Idea:
 *   - dp[0] = true (we start here).
 *   - For each index i, check if any previous index j can jump to i.
 *   - Answer = dp[n-1].
 *
 * Time Complexity:  O(N²) — nested loops.
 * Space Complexity: O(N) — dp array.
 */
fun canJumpDP(nums: IntArray): Boolean {
    val n = nums.size
    val dp = BooleanArray(n)
    dp[0] = true

    for (i in 1 until n) {
        for (j in 0 until i) {
            if (dp[j] && j + nums[j] >= i) {
                dp[i] = true
                break
            }
        }
    }
    return dp[n - 1]
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: GREEDY — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY — Track the farthest index reachable so far.
 *
 * Core Idea:
 *   - Iterate through each index.
 *   - If current index > farthest → we can't reach here → return false.
 *   - Update farthest = max(farthest, i + nums[i]).
 *   - If farthest >= last index → return true.
 *
 * Key Insight:
 *   - We don't need to track the exact path — just whether we can reach each index.
 *   - "farthest" = the maximum index reachable from any position seen so far.
 *   - If we ever reach an index beyond farthest → it's unreachable.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — one variable.
 */
fun canJump(nums: IntArray): Boolean {
    var farthest = 0

    for (i in nums.indices) {
        if (i > farthest) return false  // Can't reach index i
        farthest = maxOf(farthest, i + nums[i])
        if (farthest >= nums.size - 1) return true  // Can reach the end
    }

    return true
}
