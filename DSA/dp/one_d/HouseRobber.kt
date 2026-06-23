package dp.one_d

/**
 * https://leetcode.com/problems/house-robber/
 *
 * You are a robber planning to rob houses along a street. Each house has money.
 * You cannot rob two adjacent houses (alarm will trigger).
 * Return the maximum amount you can rob.
 *
 * Example 1: nums = [1,2,3,1] → Output: 4 (rob house 0 + house 3 = 1+3)
 * Example 2: nums = [2,7,9,3,1] → Output: 12 (rob house 0 + house 2 + house 4 = 2+9+1)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic DP — decision at each step)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * At each house, you have TWO choices:
 *   1. ROB it → you get money[i] + best from houses BEFORE the previous (i-2)
 *   2. SKIP it → you get the best from all houses up to the previous one (i-1)
 *
 * Recurrence: dp[i] = max(dp[i-1], dp[i-2] + nums[i])
 *             "skip"  vs  "rob this house"
 *
 * WHY dp[i-2] when robbing? Because if you rob house i, you CANNOT rob house i-1.
 * So the best you can add is dp[i-2] (best from houses 0..i-2).
 *
 * This is similar to Climbing Stairs but with a MAX instead of SUM,
 * because we're OPTIMIZING (max money) not COUNTING (number of ways).
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== House Robber ===")
    println("Brute Force [1,2,3,1]:     ${robBruteForce(intArrayOf(1, 2, 3, 1))}")
    println("Memoization [1,2,3,1]:     ${robMemo(intArrayOf(1, 2, 3, 1))}")
    println("Tabulation  [1,2,3,1]:     ${robTabulation(intArrayOf(1, 2, 3, 1))}")
    println("Optimal     [1,2,3,1]:     ${robOptimal(intArrayOf(1, 2, 3, 1))}")
    println("---")
    println("Optimal [2,7,9,3,1]:       ${robOptimal(intArrayOf(2, 7, 9, 3, 1))}")
    println("Optimal [2,1,1,2]:         ${robOptimal(intArrayOf(2, 1, 1, 2))}")
}

/**
 * BRUTE FORCE — Recursion (try all possibilities)
 * Time Complexity: O(2^N) — at each house, 2 choices (rob/skip)
 * Space Complexity: O(N) — recursion stack
 *
 * For each house i, we either rob it (skip i-1) or skip it.
 *
 * Recursion tree for [1,2,3,1]:
 *                    rob(0)
 *                  /        \
 *            rob(0+2)       rob(1)
 *           /       \       /      \
 *       rob(2)    rob(3)  rob(3)   rob(end)
 *       /    \      |       |
 *   rob(end) rob(3) 1     1
 *              |
 *              1
 *
 * Overlapping subproblems everywhere! Same index computed multiple times.
 */
fun robBruteForce(nums: IntArray): Int {
    return robFrom(nums, 0)
}

private fun robFrom(nums: IntArray, i: Int): Int {
    if (i >= nums.size) return 0
    // Choice 1: Rob house i, skip i+1
    val rob = nums[i] + robFrom(nums, i + 2)
    // Choice 2: Skip house i
    val skip = robFrom(nums, i + 1)
    return maxOf(rob, skip)
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N) — each index computed once
 * Space Complexity: O(N) — memo + recursion stack
 *
 * Cache the result for each starting index.
 * Same logic as brute force, but never recompute.
 *
 * Trace for [1,2,3,1]:
 * robFrom(0):
 *   rob = 1 + robFrom(2)
 *          robFrom(2):
 *            rob = 3 + robFrom(4) = 3 + 0 = 3  (cache[2]=3)
 *            skip = robFrom(3) = 1 + robFrom(5) = 1  (cache[3]=1)
 *            max(3, 1) = 3  → cache[2] = 3
 *   rob = 1 + 3 = 4
 *   skip = robFrom(1):
 *          robFrom(1):
 *            rob = 2 + robFrom(3) = 2 + 1 = 3  (cache[3] already known!)
 *            skip = robFrom(2) = 3  (cache[2] already known!)
 *            max(3, 3) = 3  → cache[1] = 3
 *   skip = 3
 * Result: max(4, 3) = 4 ✅
 */
fun robMemo(nums: IntArray): Int {
    val memo = IntArray(nums.size) { -1 }
    return robMemoFrom(nums, 0, memo)
}

private fun robMemoFrom(nums: IntArray, i: Int, memo: IntArray): Int {
    if (i >= nums.size) return 0
    if (memo[i] != -1) return memo[i]
    val rob = nums[i] + robMemoFrom(nums, i + 2, memo)
    val skip = robMemoFrom(nums, i + 1, memo)
    memo[i] = maxOf(rob, skip)
    return memo[i]
}

/**
 * OPTIMAL-1 — Bottom-Up DP (Tabulation)
 * Time Complexity: O(N)
 * Space Complexity: O(N)
 *
 * Build from the end backwards. dp[i] = max money from house i to end.
 *
 * Trace for [1,2,3,1]:
 * dp[3] = max(dp[4]+1, dp[3]) = max(0+1, 0) = 1
 * dp[2] = max(dp[4]+3, dp[3]) = max(0+3, 1) = 3
 * dp[1] = max(dp[3]+2, dp[2]) = max(1+2, 3) = 3
 * dp[0] = max(dp[2]+1, dp[1]) = max(3+1, 3) = 4 ✅
 */
fun robTabulation(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    if (nums.size == 1) return nums[0]
    val n = nums.size
    val dp = IntArray(n + 2)  // dp[n] = 0, dp[n+1] = 0 (base cases)

    for (i in n - 1 downTo 0) {
        dp[i] = maxOf(nums[i] + dp[i + 2], dp[i + 1])
    }
    return dp[0]
}

/**
 * OPTIMAL-2 — Space-Optimized Bottom-Up DP
 * Time Complexity: O(N)
 * Space Complexity: O(1)
 *
 * dp[i] only depends on dp[i+1] and dp[i+2].
 * Just track two variables!
 *
 * Trace for [1,2,3,1]:
 * next2=0, next1=0
 * i=3: curr = max(1+0, 0) = 1, next2=0→0, next1=0→1  → next2=0, next1=1
 * i=2: curr = max(3+0, 1) = 3, next2=0→1, next1=1→3  → next2=1, next1=3
 * i=1: curr = max(2+1, 3) = 3, next2=1→3, next1=3→3  → next2=3, next1=3
 * i=0: curr = max(1+3, 3) = 4, next2=3→3, next1=3→4  → next2=3, next1=4
 * Result: 4 ✅
 */
fun robOptimal(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    var next2 = 0  // dp[i+2]
    var next1 = 0  // dp[i+1]

    for (i in nums.lastIndex downTo 0) {
        val curr = maxOf(nums[i] + next2, next1)
        next2 = next1
        next1 = curr
    }
    return next1
}
