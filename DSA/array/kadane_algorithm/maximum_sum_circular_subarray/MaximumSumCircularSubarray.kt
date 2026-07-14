package array.kadane_algorithm.maximum_sum_circular_subarray

/**
 * https://leetcode.com/problems/maximum-sum-circular-subarray/
 *
 * Given a circular integer array nums of length n, return the maximum possible sum of a
 * non-empty subarray of nums. Since it's circular, the subarray can wrap around the end.
 *
 * Example 1:
 *
 * Input: nums = [1,-2,3,-2]
 * Output: 3
 * Explanation: Subarray [3] has maximum sum 3.
 *
 * Example 2:
 *
 * Input: nums = [5,-3,5]
 * Output: 10
 * Explanation: Subarray [5,5] wraps around (5 + 5 = 10).
 *
 * Example 3:
 *
 * Input: nums = [-3,-2,-3]
 * Output: -2
 * Explanation: All negative → pick the largest single element.
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Meta)
 *
 * Key Insight: Maximum circular sum = max(maxNormalSum, totalSum - minSubarraySum)
 * The circular case is equivalent to: total sum minus the minimum subarray (the "gap" in the middle).
 * Edge case: If all numbers are negative, the answer is just the max element (don't use circular).
 */
fun main() {
    println(maxSubarraySumCircular(intArrayOf(1, -2, 3, -2)))
    println(maxSubarraySumCircular(intArrayOf(5, -3, 5)))
    println(maxSubarraySumCircular(intArrayOf(-3, -2, -3)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach:
 * Case 1: Max subarray is NON-CIRCULAR → use Kadane's algorithm
 * Case 2: Max subarray IS CIRCULAR → totalSum - minSubarraySum
 *   (The circular subarray wraps around, meaning the "gap" in the middle is the min subarray)
 *
 * Result = max(maxKadane, totalSum - minKadane)
 *
 * Special case: If all negative, maxKadane = max element, and totalSum - minKadane = 0
 * (empty subarray, which is not allowed). So return maxKadane.
 *
 * Trace for [5,-3,5]:
 * Kadane max: 5 (or 5+(-3)+5=7, or just 5) → maxKadane = 7
 * Kadane min: -3 → minKadane = -3
 * totalSum = 7, circular = 7 - (-3) = 10
 * Result = max(7, 10) = 10 ✅
 */
fun maxSubarraySumCircular(nums: IntArray): Int {
    var maxSoFar = nums[0]
    var currentMax = nums[0]
    var minSoFar = nums[0]
    var currentMin = nums[0]
    var totalSum = nums[0]

    for (i in 1 until nums.size) {
        val num = nums[i]
        totalSum += num

        // Kadane for max
        currentMax = maxOf(num, currentMax + num)
        maxSoFar = maxOf(maxSoFar, currentMax)

        // Kadane for min
        currentMin = minOf(num, currentMin + num)
        minSoFar = minOf(minSoFar, currentMin)
    }

    // If all negative, maxSoFar is the answer (don't use circular = empty subarray)
    return if (maxSoFar < 0) {
        maxSoFar
    } else {
        maxOf(maxSoFar, totalSum - minSoFar)
    }
}
