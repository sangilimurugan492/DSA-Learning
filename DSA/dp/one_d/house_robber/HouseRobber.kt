package dp.one_d.house_robber

/**
 * House Robber — LeetCode #198
 * https://leetcode.com/problems/house-robber/
 *
 * Problem:
 * -------
 * You are a robber planning to rob houses along a street. Each house has money.
 * You cannot rob two adjacent houses (alarm will trigger).
 * Return the maximum amount you can rob.
 *
 * Example 1:  nums = [1, 2, 3, 1]    →  4  (rob house 0 + house 3 = 1+3)
 * Example 2:  nums = [2, 7, 9, 3, 1] →  12 (rob house 0 + house 2 + house 4 = 2+9+1)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic DP — decision at each step)
 *
 * Recurrence: dp[i] = max(dp[i-1], dp[i-2] + nums[i])
 *             "skip house i"  vs  "rob house i"
 */

fun main() {
    println("=== Method 1: Brute Force (Recursion) ===")
    println("rob([1,2,3,1]) = ${robBruteForce(intArrayOf(1, 2, 3, 1))}")
    println("rob([2,7,9,3,1]) = ${robBruteForce(intArrayOf(2, 7, 9, 3, 1))}")

    println("\n=== Method 2: Space-Optimized DP ===")
    println("rob([1,2,3,1]) = ${robOptimal(intArrayOf(1, 2, 3, 1))}")
    println("rob([2,7,9,3,1]) = ${robOptimal(intArrayOf(2, 7, 9, 3, 1))}")
    println("rob([2,1,1,2]) = ${robOptimal(intArrayOf(2, 1, 1, 2))}")

    println("\n=== Step-by-step trace ===")
    robTrace(intArrayOf(1, 2, 3, 1))
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Recursion (try all possibilities)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — At each house, two choices: rob or skip.
 *
 * Core Idea:
 *   - rob(i): Rob house i → get nums[i] + rob(i+2) (skip next house).
 *   - skip(i): Skip house i → get rob(i+1).
 *   - Return max(rob, skip).
 *
 * Problem: Overlapping subproblems → exponential time.
 *
 * Time Complexity:  O(2^N) — two choices at each house.
 * Space Complexity: O(N) — recursion stack.
 */
fun robBruteForce(nums: IntArray): Int {
    return robFrom(nums, 0)
}

private fun robFrom(nums: IntArray, i: Int): Int {
    if (i >= nums.size) return 0
    // Choice 1: Rob house i, skip i+1.
    val rob = nums[i] + robFrom(nums, i + 2)
    // Choice 2: Skip house i.
    val skip = robFrom(nums, i + 1)
    return maxOf(rob, skip)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SPACE-OPTIMIZED DP (O(1) space)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SPACE-OPTIMIZED DP — dp[i] only depends on dp[i+1] and dp[i+2].
 *
 * Core Idea:
 *   - dp[i] = max money from house i to end.
 *   - dp[i] = max(nums[i] + dp[i+2], dp[i+1])  →  rob vs skip.
 *   - Only need two variables: next1 (dp[i+1]) and next2 (dp[i+2]).
 *
 * Key Insight:
 *   - If you rob house i, you CANNOT rob house i-1, so add dp[i-2].
 *   - If you skip house i, you get dp[i-1].
 *   - Take the max of these two choices.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — two variables.
 */
fun robOptimal(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    var next2 = 0  // dp[i+2] — best from two houses ahead.
    var next1 = 0  // dp[i+1] — best from next house.

    for (i in nums.lastIndex downTo 0) {
        // Rob house i (nums[i] + next2) vs skip house i (next1).
        val curr = maxOf(nums[i] + next2, next1)
        next2 = next1
        next1 = curr
    }

    return next1
}

/**
 * Space-optimized DP with step-by-step trace for learning/debugging.
 */
fun robTrace(nums: IntArray) {
    println("Input: ${nums.toList()}")
    if (nums.isEmpty()) {
        println("  Result: 0")
        return
    }
    var next2 = 0
    var next1 = 0

    for (i in nums.lastIndex downTo 0) {
        val rob = nums[i] + next2
        val skip = next1
        val curr = maxOf(rob, skip)
        val choice = if (rob >= skip) "ROB" else "SKIP"
        println("  i=$i | nums[$i]=${nums[i]} | rob=$rob | skip=$skip | curr=$curr ($choice)")
        next2 = next1
        next1 = curr
    }
    println("  Result: $next1")
}
