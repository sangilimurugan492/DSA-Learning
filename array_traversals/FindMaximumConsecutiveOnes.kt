package array_traversals

/**
 * consecutive 1's in the array.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [1,1,0,1,1,1]
 * Output: 3
 * Explanation: The first two digits or the last three digits are consecutive 1s. The maximum number of consecutive 1s is 3.
 * Example 2:
 *
 * Input: nums = [1,0,1,1,0,1]
 * Output: 2
 *
 * Time Complexity O(N)
 * Space COmplexity O(N)
 * Its simple traverval and change the count based on max value
 * https://leetcode.com/problems/max-consecutive-ones/
 */

fun main() {
    findMaximumOnes(intArrayOf(1,1,0,1,1,1))
}

fun findMaximumOnes(nums : IntArray) {

    var maxCount = 0
    var count = 0

    for(i in nums.indices) {
        if (nums[i] == 1) {
            count++
        } else {
            count = 0
        }
        if (count > maxCount) {
            maxCount = count
        }
    }
    println(maxCount)
}