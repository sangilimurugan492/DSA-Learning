package dp.subsequence.partition_equal_subset_sum

/**
 * Partition Equal Subset Sum — LeetCode #416
 * https://leetcode.com/problems/partition-equal-subset-sum/
 *
 * Problem:
 * -------
 * Given a non-empty array, determine if it can be partitioned into two subsets
 * with equal sum.
 *
 * Example:  [1,5,11,5]  →  true  ([1,5,5] and [11], both sum to 11)
 *           [1,2,3,5]  →  false (total=11, can't split evenly)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic 0/1 Knapsack DP)
 *
 * Key Insight: If total sum is odd → false. If even → can we find a subset
 * that sums to total/2? This is the 0/1 Knapsack problem!
 *
 * Recurrence: dp[j] = dp[j] || dp[j - nums[i]]
 *   (can we make sum j, either without nums[i] or with nums[i])
 *
 * Two approaches:
 * 1. 2D DP: O(N × target) time, O(N × target) space
 * 2. 1D DP (Space-Optimized): O(N × target) time, O(target) space
 */

fun main() {
    val nums = intArrayOf(1, 5, 11, 5)

    println("=== Method 1: 2D DP ===")
    println("canPartition(${nums.toList()}) = ${canPartition2D(nums)}")

    println("\n=== Method 2: 1D DP (Space-Optimized) ===")
    println("canPartition(${nums.toList()}) = ${canPartition1D(nums)}")

    println("\n=== Step-by-step trace ===")
    canPartitionTrace(nums)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: 2D DP — O(N × target) time and space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * 2D DP — dp[i][j] = true if subset of first i elements can sum to j.
 *
 * Core Idea:
 *   - This is 0/1 Knapsack: for each element, include it or not.
 *   - dp[i][j] = dp[i-1][j] (exclude nums[i]) || dp[i-1][j-nums[i]] (include nums[i]).
 *
 * Key Insight:
 *   - If total sum is odd → impossible.
 *   - If even → find subset summing to total/2 (0/1 Knapsack).
 *
 * Time Complexity:  O(N × target) — target = total/2.
 * Space Complexity: O(N × target) — 2D dp array.
 */
fun canPartition2D(nums: IntArray): Boolean {
    val total = nums.sum()
    if (total % 2 != 0) return false
    val target = total / 2

    val dp = Array(nums.size + 1) { BooleanArray(target + 1) }
    for (i in 0..nums.size) dp[i][0] = true  // Sum 0 is always possible.

    for (i in 1..nums.size) {
        for (j in 1..target) {
            dp[i][j] = dp[i - 1][j]  // Exclude nums[i-1].
            if (j >= nums[i - 1]) {
                dp[i][j] = dp[i][j] || dp[i - 1][j - nums[i - 1]]  // Include nums[i-1].
            }
        }
    }
    return dp[nums.size][target]
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: 1D DP (Space-Optimized) — O(target) space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * 1D DP — dp[j] = true if sum j is achievable. Process right-to-left to avoid reuse.
 *
 * Core Idea:
 *   - Same as 2D but only keep one row. Process j from right to left.
 *   - Right-to-left ensures each element is used at most once (0/1 Knapsack).
 *
 * Key Insight:
 *   - dp[j] = dp[j] || dp[j - nums[i]] — can we make sum j with or without nums[i]?
 *   - Process j from target down to nums[i] to avoid using the same element twice.
 *
 * Time Complexity:  O(N × target) — same as 2D.
 * Space Complexity: O(target) — 1D dp array.
 */
fun canPartition1D(nums: IntArray): Boolean {
    val total = nums.sum()
    if (total % 2 != 0) return false
    val target = total / 2

    val dp = BooleanArray(target + 1)
    dp[0] = true  // Sum 0 is always possible.

    for (num in nums) {
        for (j in target downTo num) {
            dp[j] = dp[j] || dp[j - num]
        }
    }
    return dp[target]
}

/**
 * 1D DP with step-by-step trace.
 */
fun canPartitionTrace(nums: IntArray) {
    val total = nums.sum()
    println("Input: ${nums.toList()}, total=$total")
    if (total % 2 != 0) {
        println("  Total is odd → false")
        return
    }
    val target = total / 2
    println("  Target: $target")

    val dp = BooleanArray(target + 1)
    dp[0] = true

    for (num in nums) {
        print("  num=$num: dp before=${dp.toList().mapIndexed { i, b -> if (b) i.toString() else "-" }}")
        for (j in target downTo num) {
            dp[j] = dp[j] || dp[j - num]
        }
        println(" → dp after=${dp.toList().mapIndexed { i, b -> if (b) i.toString() else "-" }}")
    }
    println("  Result: ${dp[target]}")
}
