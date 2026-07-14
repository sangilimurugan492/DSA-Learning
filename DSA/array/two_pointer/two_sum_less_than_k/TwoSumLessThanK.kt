package array.two_pointer.two_sum_less_than_k

/**
 * https://leetcode.com/problems/two-sum-less-than-k/
 *
 * Given an array nums and integer k, return the maximum sum of a pair such that
 * sum < k. If no such pair exists, return -1.
 *
 * Example 1:
 *
 * Input: nums = [34,23,1,24,75,33,54,8], k = 60
 * Output: 58
 * Explanation: 34 + 24 = 58, which is the max sum < 60
 *
 * Example 2:
 *
 * Input: nums = [10,20,30], k = 15
 * Output: -1
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google)
 *
 * Key Insight: Sort + two pointers. If sum < k, track max and move left (try bigger).
 * If sum >= k, move right (try smaller).
 */
fun main() {
    println(twoSumLessThanK(intArrayOf(34, 23, 1, 24, 75, 33, 54, 8), 60))
    println(twoSumLessThanK(intArrayOf(10, 20, 30), 15))
}

/**
 * Time Complexity O(N log N)
 * Space Complexity O(1)
 */
fun twoSumLessThanK(nums: IntArray, k: Int): Int {
    nums.sort()
    var left = 0
    var right = nums.size - 1
    var maxSum = -1

    while (left < right) {
        val sum = nums[left] + nums[right]
        if (sum < k) {
            maxSum = maxOf(maxSum, sum)
            left++
        } else {
            right--
        }
    }

    return maxSum
}
