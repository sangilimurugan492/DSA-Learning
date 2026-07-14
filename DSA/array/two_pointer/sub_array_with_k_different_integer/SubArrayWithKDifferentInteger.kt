package array.two_pointer.sub_array_with_k_different_integer

/**
 * https://leetcode.com/problems/subarrays-with-k-different-integers/description/
 *
 *Given an integer array nums and an integer k, return the number of good subarrays of nums.
 *
 * A good array is an array where the number of different integers in that array is exactly k.
 *
 * For example, [1,2,3,1,2] has 3 different integers: 1, 2, and 3.
 * A subarray is a contiguous part of an array.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [1,2,1,2,3], k = 2
 * Output: 7
 * Explanation: Subarrays formed with exactly 2 different integers: [1,2], [2,1], [1,2], [2,3], [1,2,1], [2,1,2], [1,2,1,2]
 * Example 2:
 *
 * Input: nums = [1,2,1,3,4], k = 3
 * Output: 3
 * Explanation: Subarrays formed with exactly 3 different integers: [1,2,1,3], [2,1,3], [1,3,4].
 *
 */

fun main() {
    subarraysWithKDistinctBF(intArrayOf(1,2,3,1,2), 2)
    subarraysWithKDistinctOP(intArrayOf(1,2,3,1,2), 2)
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(k)
 * Leet Code - Time Limit Exceeds
 */
fun subarraysWithKDistinctBF(nums: IntArray, k: Int): Int {

    var totalCount = 0
    for (i in nums.indices) {
        val distinctSet = mutableSetOf<Int>()
        for (j in i until nums.size) {
            distinctSet.add(nums[j])
            if (distinctSet.size == k) {
                totalCount++
            } else if (distinctSet.size > k) {
                break
            }
        }
    }
    return totalCount
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 * Leet Code - 7 MS
 */
fun subarraysWithKDistinctOP(nums: IntArray, k: Int): Int {
    return atMostK(nums, k) - atMostK(nums, k - 1)
}

private fun atMostK(nums: IntArray, k: Int): Int {
    var ans = 0
    val n = nums.size
    var freq = IntArray(n + 1)
    var count = 0
    var start = 0

    for ((end, num) in nums.withIndex()) {
        if (freq[num]++ == 0) {
            count++
        }

        while (count > k) {
            if (--freq[nums[start]] == 0) {
                count--
            }
            start++
        }

        ans += end - start + 1
    }

    return ans
}