package array_traversals

/**
 * https://leetcode.com/problems/running-sum-of-1d-array/description/
 * Given an array nums. We define a running sum of an array as runningSum[i] = sum(nums[0]…nums[i]).
 *
 * Return the running sum of nums.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [1,2,3,4]
 * Output: [1,3,6,10]
 * Explanation: Running sum is obtained as follows: [1, 1+2, 1+2+3, 1+2+3+4].
 * Its Simple and traversal
 * Time Complexity O(N)
 * Space Complexity O(N)
 */
fun main() {

    runningSumOP(intArrayOf(1,2,3,4)).forEach {
        print("$it ")
    }
}


fun runningSumOP(nums : IntArray) : IntArray {
    var sum = 0
    for(i in nums.indices) {
        sum +=nums[i]
        nums[i] = sum
    }
    return  nums
}