package array.two_pointer_inplace.sorted_squares

import kotlin.math.pow

/**
 * https://leetcode.com/problems/squares-of-a-sorted-array/description/
 * Given an integer array nums sorted in non-decreasing order, return an array of the squares of each number sorted in non-decreasing order.
 *
 * Example 1:
 *
 * Input: nums = [-4,-1,0,3,10]
 * Output: [0,1,9,16,100]
 * Explanation: After squaring, the array becomes [16,1,0,9,100].
 * After sorting, it becomes [0,1,9,16,100].
 * Example 2:
 *
 * Input: nums = [-7,-3,2,3,11]
 * Output: [4,9,9,49,121]
 */
fun main() {
    sortedSquaresBF(intArrayOf(-4,-1,0,3,10)).forEach {
        print("$it ")
    }

    println()
    sortedSquaresOP(intArrayOf(-4,-1,0,3,10)).forEach {
        print("$it ")
    }
}

/**
 * Time Complexity O(N Log N) because of sorting
 * Space Complexity O(1)
 */
fun sortedSquaresBF(nums: IntArray) : IntArray {
    for (i in nums.indices) {
        nums[i] = nums[i].toDouble().pow(2.0).toInt()
    }
    nums.sort()
    return  nums
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N) Using extra array
 */
fun sortedSquaresOP(nums: IntArray) : IntArray {
    val result = IntArray(nums.size)
    var left = 0
    var right = nums.size - 1
    var i = nums.size - 1
    while (i >= 0) {
        val leftSquare = nums[left] * nums[left]
        val rightSquare = nums[right] * nums[right]
        if (leftSquare < rightSquare) {
            right--
            result[i] = rightSquare
        } else {
            left++
            result[i] = leftSquare
        }
        i--
    }
    return result
}