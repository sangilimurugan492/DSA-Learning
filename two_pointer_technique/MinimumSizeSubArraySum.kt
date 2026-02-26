package two_pointer_technique

import java.util.*

/**
 * https://leetcode.com/problems/minimum-size-subarray-sum/description/
 *
 * Given an array of positive integers nums and a positive integer target, return the minimal length of a subarray whose sum is greater than or equal to target. If there is no such subarray, return 0 instead.
 *
 * Example 1:
 *
 * Input: target = 7, nums = [2,3,1,2,4,3]
 * Output: 2
 * Explanation: The subarray [4,3] has the minimal length under the problem constraint.
 * Example 2:
 *
 * Input: target = 4, nums = [1,4,4]
 * Output: 1
 * Example 3:
 *
 * Input: target = 11, nums = [1,1,1,1,1,1,1,1]
 * Output: 0
 */
fun main() {
    println(minimumSizeSubArraySumBF(213, intArrayOf(12,28,83,4,25,26,25,2,25,25,25,12)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun minimumSizeSubArraySumBF(target: Int, nums: IntArray) : Int {
    var minLen = Int.MAX_VALUE
    val n = nums.size

    for (i in 0 until n) {
        var currentSum = 0
        for (j in i until n) {
            currentSum += nums[j]
            if (currentSum >= target) {
                minLen = minOf(minLen, j - i + 1)
                break // Found the shortest subarray starting at i
            }
        }
    }

    return if (minLen == Int.MAX_VALUE) 0 else minLen
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun minimumSizeSubArraySumOP(target: Int, nums: IntArray) : Int {
    var minLen = Int.MAX_VALUE
    var left = 0
    var currentSum = 0

    for (right in nums.indices) {
        currentSum += nums[right]

        // Try to shrink the window from the left as much as possible
        while (currentSum >= target) {
            minLen = minOf(minLen, right - left + 1)
            currentSum -= nums[left]
            left++
        }
    }

    return if (minLen == Int.MAX_VALUE) 0 else minLen
}