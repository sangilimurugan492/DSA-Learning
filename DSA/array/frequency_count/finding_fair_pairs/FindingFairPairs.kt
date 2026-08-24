package array.frequency_count.finding_fair_pairs

/**
 * Count the Number of Fair Pairs — LeetCode #2563
 * https://leetcode.com/problems/count-the-number-of-fair-pairs/
 *
 * Problem:
 * -------
 * Given a 0-indexed integer array nums and two integers lower and upper,
 * return the number of fair pairs (i, j) where:
 *   0 <= i < j < n  AND  lower <= nums[i] + nums[j] <= upper
 *
 * Example:  nums = [0,1,7,4,4,5], lower = 3, upper = 6
 *           Output: 6
 *           Fair pairs: (0,3),(0,4),(0,5),(1,3),(1,4),(1,5)
 *
 * FAANG Importance: ⭐⭐⭐⭐
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — check all pairs
 * 2. Sort + Two Pointers: O(N log N) — count pairs with sum ≤ upper minus sum < lower
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    println("Fair pairs: ${findingFairPairsBF(intArrayOf(0, 1, 7, 4, 4, 5), 3, 6)}")

    println("\n=== Method 2: Sort + Two Pointers ===")
    println("Fair pairs: ${findingFairPairsOP(intArrayOf(0, 1, 7, 4, 4, 5), 3, 6)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Check every pair (i, j) where i < j.
 *
 * Time Complexity:  O(N²) — nested loops over all pairs.
 * Space Complexity: O(1) — only a counter.
 */
fun findingFairPairsBF(nums: IntArray, lower: Int, upper: Int): Long {
    var count = 0L
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            val sum = nums[i] + nums[j]
            if (sum in lower..upper) {
                count++
            }
        }
    }
    return count
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SORT + TWO POINTERS — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SORT + TWO POINTERS — Sort the array, then count pairs with sum ≤ upper
 * and subtract pairs with sum < lower (i.e., sum ≤ lower - 1).
 *
 * Core Idea:
 *   - Sorting is safe because we only need the COUNT of valid pairs,
 *     not the original indices. Any pair (i, j) in sorted order still
 *     represents a unique pair from the original array.
 *   - countLessEqual(target) uses two pointers:
 *       left = 0, right = n-1
 *       If nums[left] + nums[right] <= target → all pairs (left, left+1..right)
 *         are valid → add (right - left), move left++.
 *       Else → move right-- (sum too large).
 *   - Answer = countLessEqual(upper) - countLessEqual(lower - 1)
 *
 * Time Complexity:  O(N log N) — sort dominates; two-pointer passes are O(N).
 * Space Complexity: O(1) — in-place sort (or O(N) depending on sort impl).
 */
fun findingFairPairsOP(nums: IntArray, lower: Int, upper: Int): Long {
    nums.sort()
    return countLessEqual(nums, upper) - countLessEqual(nums, lower - 1)
}

/**
 * Counts pairs (i, j) with i < j and nums[i] + nums[j] <= target.
 * Assumes nums is already sorted.
 */
private fun countLessEqual(nums: IntArray, target: Int): Long {
    var count = 0L
    var left = 0
    var right = nums.size - 1

    while (left < right) {
        val sum = nums[left] + nums[right]
        if (sum <= target) {
            // All pairs (left, left+1), (left, left+2), ..., (left, right) are valid
            count += (right - left)
            left++
        } else {
            right--
        }
    }
    return count
}
