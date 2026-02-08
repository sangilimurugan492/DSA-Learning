package array_hashing_technique

import two_pointer_technique.twoSumLevelTwoWithSortedArrayBF
import two_pointer_technique.twoSumLevelTwoWithSortedArrayOP

/**
 * https://leetcode.com/problems/two-sum/description/
 */
fun main() {
    println("Brute Force Approach")
    val result = twoSumLevelOneWithoutSortedArrayBF(intArrayOf(2,5,4,7,6,11,15), 9)
    result.forEach {
        println(it)
    }

    println("Optimal Approach")
    val resultOP = twoSumLevelOneWithoutSortedArrayOP(intArrayOf(2,5,4,7,6,11,15), 9)
    resultOP.forEach {
        println(it)
    }
}

/**
 * Input: nums = [2,7,11,15], target = 9
 * Output: [0,1]
 * Input: nums = [3,2,4], target = 6
 * Output: [1,2]
 * Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].
 * Brute Force Technique Same as Two Sum Level I
 * Time Complexity : O(N^2) -> Space Complexity O(N)
 */
fun twoSumLevelOneWithoutSortedArrayBF(nums: IntArray, target: Int) : IntArray {
    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            if (target == (nums[i] + nums[j])) {
                return intArrayOf(i, j)
            }
        }
    }
    return intArrayOf()
}

/**
 * Input: nums = [2,7,11,15], target = 9
 * Output: [0,1]
 * Input: nums = [3,2,4], target = 6
 * Output: [1,2]
 * Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].
 * Brute Force Technique Same as Two Sum Level I
 * Time Complexity : O(N/2) i.e it will take half of the time in best case if we remove the constants So Time Complexity - O(N)
 * Space Complexity O(N)
 */
fun twoSumLevelOneWithoutSortedArrayOP(nums: IntArray, target: Int) : IntArray {
    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            if (target == (nums[i] + nums[j])) {
                return intArrayOf(i, j)
            }
        }
    }
    return intArrayOf()
}