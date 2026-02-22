package array_hashing_technique

/**
 * https://leetcode.com/problems/check-if-n-and-its-double-exist/description/
 * Given an array arr of integers, check if there exist two indices i and j such that :
 *
 * i != j
 * 0 <= i, j < arr.length
 * arr[i] == 2 * arr[j]
 *
 *
 * Example 1:
 *
 * Input: arr = [10,2,5,3]
 * Output: true
 * Explanation: For i = 0 and j = 2, arr[i] == 10 == 2 * 5 == 2 * arr[j]
 * Example 2:
 *
 * -20,8,-6,-14,0,-19,14,4
 *
 * Input: arr = [3,1,7,11]
 * Output: false
 * Explanation: There is no i and j that satisfy the conditions.
 */

fun main() {
//    println(checkIFNDoubleExistsBF(intArrayOf(-20,8,-6,-14,0,-19,14,4)))
    println(checkIFNDoubleExistsOP(intArrayOf(7,1,14,11)))
}


fun checkIFNDoubleExistsBF(nums: IntArray) : Boolean {
    for (i in nums.indices) {
        for (j in nums.indices) {
            if (i != j && nums[i] == 2 * nums[j]) {
                return true
            }
        }
    }
    return false
}

fun checkIFNDoubleExistsOP(nums: IntArray) : Boolean {
    val map = mutableSetOf<Int>()
    for (i in nums) {
        if (map.contains(2 * i) || (i % 2 == 0 && map.contains(i / 2))) {
            return true
        }
        map.add(i)
    }
    return false
}
