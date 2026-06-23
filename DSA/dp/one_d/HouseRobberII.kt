package dp.one_d

/**
 * https://leetcode.com/problems/house-robber-ii/
 *
 * You are a robber planning to rob houses along a street arranged in a CIRCLE.
 * Each house has money. You cannot rob two adjacent houses (alarm will trigger).
 * Since it's circular, the first and last houses are adjacent.
 * Return the maximum amount you can rob.
 *
 * Example 1: nums = [2,3,2] → Output: 3 (can't rob house 0 and 2, they're adjacent)
 * Example 2: nums = [1,2,3,1] → Output: 4 (rob house 1 and 3 = 2+1+1... wait, rob house 1=2 and house 3=1... actually rob house 0=1 and house 2=3)
 * Example 3: nums = [1,2,3] → Output: 3
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (House Robber circular variant — must-know twist)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is House Robber with a CIRCULAR constraint: house[0] and house[n-1]
 * are now adjacent. You can't rob both.
 *
 * KEY INSIGHT: Break the circle into two LINEAR sub-problems!
 *   Case 1: Rob houses [0..n-2] → exclude last house
 *   Case 2: Rob houses [1..n-1] → exclude first house
 *   Answer = max(Case 1, Case 2)
 *
 * WHY does this work? Since house[0] and house[n-1] can't both be robbed,
 * the optimal solution MUST exclude at least one of them. So:
 *   - If we exclude house[n-1], we solve House Robber I on [0..n-2]
 *   - If we exclude house[0], we solve House Robber I on [1..n-1]
 *   - The maximum of these two covers all possibilities!
 *
 * Edge case: If only 1 house, just rob it (can't split into two cases).
 *
 * Connection to other problems:
 *   House Robber I:  Linear street → simple DP
 *   House Robber II: Circular street → two linear sub-problems
 *   House Robber III: Tree structure → tree DP (different pattern)
 *
 * This "break circular into two linear" pattern appears in MANY circular problems!
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== House Robber II ===")
    println("Optimal [2,3,2]:    ${rob2(intArrayOf(2, 3, 2))}")
    println("Optimal [1,2,3,1]:  ${rob2(intArrayOf(1, 2, 3, 1))}")
    println("Optimal [1,2,3]:    ${rob2(intArrayOf(1, 2, 3))}")
    println("Optimal [1]:        ${rob2(intArrayOf(1))}")
    println("Optimal [5]:        ${rob2(intArrayOf(5))}")
}

/**
 * OPTIMAL — Two House Robber I calls
 * Time Complexity: O(N) — two passes of O(N)
 * Space Complexity: O(1) — space-optimized
 *
 * Break circular problem into two linear problems:
 *   max(robLinear[0..n-2], robLinear[1..n-1])
 *
 * Trace for [1,2,3,1]:
 * Case 1: [1,2,3] → robLinear = max(1+3, 2) = 4
 * Case 2: [2,3,1] → robLinear = max(2+1, 3) = 3
 * Answer: max(4, 3) = 4 ✅
 *
 * Trace for [2,3,2]:
 * Case 1: [2,3] → robLinear = max(2, 3) = 3
 * Case 2: [3,2] → robLinear = max(3, 2) = 3
 * Answer: max(3, 3) = 3 ✅ (can't rob house 0 and 2 since they're adjacent)
 */
fun rob2(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    if (nums.size == 1) return nums[0]

    // Case 1: Rob houses [0..n-2] (exclude last)
    // Case 2: Rob houses [1..n-1] (exclude first)
    return maxOf(robLinear(nums, 0, nums.size - 2), robLinear(nums, 1, nums.size - 1))
}

/**
 * Standard House Robber I on a subarray [start ..end]
 * Space-optimized: O(1) space
 */
private fun robLinear(nums: IntArray, start: Int, end: Int): Int {
    if (start > end) return 0
    var prev2 = 0
    var prev1 = 0

    for (i in start..end) {
        val curr = maxOf(prev1, prev2 + nums[i])
        prev2 = prev1
        prev1 = curr
    }
    return prev1
}
