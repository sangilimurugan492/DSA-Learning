package array_traversals

/**
 * https://leetcode.com/problems/find-the-duplicate-number/description/
 * Given an array of integers nums containing n + 1 integers where each integer is in the range [1, n] inclusive.
 *
 * There is only one repeated number in nums, return this repeated number.
 *
 * You must solve the problem without modifying the array nums and using only constant extra space.
 *
 * Example 1:
 *
 * Input: nums = [1,3,4,2,2]
 * Output: 2
 * Example 2:
 *
 * Input: nums = [3,1,3,4,2]
 * Output: 3
 */

fun main() {
    println(findDuplicateNumberBF(intArrayOf(1,3,4,2,2)))
    println(findDuplicateNumberOP(intArrayOf(3,1,3,4,2)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun findDuplicateNumberBF(nums: IntArray): Int {

    val n = nums.size
    for (i in 0 until n) {
        for (j in i + 1 until n) {
            if (nums[i] == nums[j]) {
                return nums[i]
            }
        }
    }
    return -1 // Should not happen per constraints
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun findDuplicateNumberOP(nums: IntArray): Int {
    var slow = nums[0]
    var fast = nums[0]

    do {
        slow = nums[slow]
        fast = nums[nums[fast]]
    } while (slow != fast)

    slow = nums[0]
    while (slow != fast) {
        slow = nums[slow]
        fast = nums[fast]
    }

    return fast
}