package array_traversals

/**
 *
 *https://leetcode.com/problems/find-pivot-index/description/
 *
 * Given an array of integers nums, calculate the pivot index of this array.
 *
 * The pivot index is the index where the sum of all the numbers strictly to the left of the index is equal to the sum of all the numbers strictly to the index's right.
 *
 * If the index is on the left edge of the array, then the left sum is 0 because there are no elements to the left. This also applies to the right edge of the array.
 *
 * Return the leftmost pivot index. If no such index exists, return -1.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [1,7,3,6,5,6]
 * Output: 3
 * Explanation:
 * The pivot index is 3.
 * Left sum = nums[0] + nums[1] + nums[2] = 1 + 7 + 3 = 11
 * Right sum = nums[4] + nums[5] = 5 + 6 = 11
 * Example 2:
 *
 * Input: nums = [1,2,3]
 * Output: -1
 * Explanation:
 * There is no index that satisfies the conditions in the problem statement.
 */
fun main() {
//    println(findPivotIndexBF(intArrayOf(1,7,3,6,5,6)))
    println(findPivotIndexOP(intArrayOf(1,7,3,6,5,6)))
}

/**
 * Brute Force Method
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 * Time taken in leetcode : 117 ms
 */
fun findPivotIndexBF(nums: IntArray) : Int {

    val n = nums.size
    var pivotIndex = -1
    var sumLeft = 0
    var sumRight: Int
    for (i in nums.indices) {
        if (i != 0) {
            sumLeft += nums[i - 1]
        }
        sumRight = 0
        for(j in i+1 until n) {
            sumRight+=nums[j]
        }
        if (sumLeft == sumRight) {
            return i
        }
    }
    return pivotIndex
}

/**
 * Brute Force Method
 * Time Complexity O(N)
 * Space Complexity O(N)
 * Time taken in leetcode : 1 ms
 */
fun findPivotIndexOP(nums: IntArray) : Int {
    var sumLeft = 0
    var sumRight: Int
    var totalSum = 0

    for (i in nums.indices) {
        totalSum += nums[i]
    }

    for (i in nums.indices) {

        if (i != 0) {
            sumLeft += nums[i - 1]
        }
        sumRight = totalSum - (sumLeft + nums[i])
        if (sumLeft == sumRight) {
            return i
        }
    }

    return -1
}