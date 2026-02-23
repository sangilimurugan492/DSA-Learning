package array_traversals

/**
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Given an integer array nums, find the subarray with the largest sum, and return its sum.
 *
 * Example 1:
 *
 * Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
 * Output: 6
 * Explanation: The subarray [4,-1,2,1] has the largest sum 6.
 * Example 2:
 *
 * Input: nums = [1]
 * Output: 1
 * Explanation: The subarray [1] has the largest sum 1.
 */
fun main() {
    println(maxSubArrayBF(intArrayOf(-2,1,-3,4,-1,2,1,-5,4)))

    println(maxSubArrayOP(intArrayOf(-2,1,-3,4,-1,2,1,-5,4)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun maxSubArrayBF(nums: IntArray): Int {
    var maxSum = Int.MIN_VALUE
    val n = nums.size
    var currentSum: Int
    for (i in 0 until n) {
        currentSum = 0
        for (j in i until n) {
            currentSum += nums[j]
            maxSum = maxSum.coerceAtLeast(currentSum)
        }
    }
    return maxSum
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun maxSubArrayOP(nums: IntArray): Int {

    var max = nums[0]
    var current = nums[0]

    for (i in 1 until nums.size) {
        if (current < 0) {
            current = 0
        }
        current += nums[i]
        if (current > max) {
            max = current
        }
    }
    return max
}