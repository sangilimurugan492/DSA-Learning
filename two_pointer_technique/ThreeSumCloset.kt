package two_pointer_technique

import kotlin.math.abs

/**
 * https://leetcode.com/problems/3sum-closest/description/
 *Given an integer array nums of length n and an integer target, find three integers at distinct indices in nums such that the sum is closest to target.
 *
 * Return the sum of the three integers.
 *
 * You may assume that each input would have exactly one solution.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [-1,2,1,-4], target = 1
 * Output: 2
 * Explanation: The sum that is closest to the target is 2. (-1 + 2 + 1 = 2).
 * Example 2:
 *
 * Input: nums = [0,0,0], target = 1
 * Output: 0
 * Explanation: The sum that is closest to the target is 0. (0 + 0 + 0 = 0).
 */
fun main() {
    println(threeSumClosetBF(intArrayOf(-1,2,1,-4), 1))
    println(threeSumClosetOP(intArrayOf(-1,2,1,-4), 1))
}

/**
 * Time Complexity O(N^3)
 * Space Complexity O(N)
 */
fun threeSumClosetBF(nums: IntArray, target : Int): Int {

    var sum = nums[0] + nums[1] + nums[2]
    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            for (k in j + 1 until nums.size) {
                val currentSum = nums[i] + nums[j] + nums[k]
                // If this new sum is closer to the target, update closestSum
                if (Math.abs(target - currentSum) < Math.abs(target - sum)) {
                    sum = currentSum
                }
            }
        }
    }
    return sum
}

/**
 * Time Complexity O(n^2)
 * Space Complexity O(1) or O(log n)
 */
fun threeSumClosetOP(nums: IntArray,target : Int): Int {
    nums.sort()
    var closestSum = nums[0] + nums[1] + nums[2]

    for (i in 0 until nums.size - 2) {
        var left = i + 1
        var right = nums.size - 1

        while (left < right) {
            val currentSum = nums[i] + nums[left] + nums[right]

            // If we found the exact target, return immediately
            if (currentSum == target) return currentSum

            // Update closestSum if the current one is better
            if (abs(target - currentSum) < abs(target - closestSum)) {
                closestSum = currentSum
            }

            // Move pointers based on comparison with target
            if (currentSum < target) {
                left++
            } else {
                right--
            }
        }
    }

    return closestSum
}