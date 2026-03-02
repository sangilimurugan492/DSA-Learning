package sliding_window_technique

/**
 * https://leetcode.com/problems/longest-subarray-of-1s-after-deleting-one-element/
 *
 * Given a binary array nums, you should delete one element from it.
 *
 * Return the size of the longest non-empty subarray containing only 1's in the resulting array. Return 0 if there is no such subarray.
 *
 * Example 1:
 *
 * Input: nums = [1,1,0,1]
 * Output: 3
 * Explanation: After deleting the number in position 2, [1,1,1] contains 3 numbers with value of 1's.
 * Example 2:
 *
 * Input: nums = [0,1,1,1,0,1,1,0,1]
 * Output: 5
 * Explanation: After deleting the number in position 4, [0,1,1,1,1,1,0,1] longest subarray with value of 1's is [1,1,1,1,1].
 * Example 3:
 *
 * Input: nums = [1,1,1]
 * Output: 2
 * Explanation: You must delete one element.
 */
fun main() {
    println(longestSubArrayAfterDeletingOneBF(intArrayOf(0,1,1,1,0,1,1,0,1)))
    println(longestSubArrayAfterDeletingOneOP(intArrayOf(0,1,1,1,0,1,1,0,1)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun longestSubArrayAfterDeletingOneBF(nums: IntArray): Int {

    var maxLen = 0
    for (i in nums.indices) {
        var zeros = 0
        for (j in i until nums.size) {
            if (nums[j] == 0) zeros++
            if (zeros <= 1) {
                // Total elements minus the one we "delete"
                maxLen = maxOf(maxLen, (j - i + 1) - 1)
            } else {
                break
            }
        }
    }
    return maxLen

}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun longestSubArrayAfterDeletingOneOP(nums: IntArray): Int {

    var left = 0
    var zeroCount = 0
    var maxLen = 0

    for (right in nums.indices) {
        if (nums[right] == 0) {
            zeroCount++
        }

        // If we have more than one zero, shrink from left
        while (zeroCount > 1) {
            if (nums[left] == 0) {
                zeroCount--
            }
            left++
        }

        // Window size is (right - left + 1).
        // After deleting one element, length is (right - left).
        maxLen = maxOf(maxLen, right - left)
    }

    return maxLen

}