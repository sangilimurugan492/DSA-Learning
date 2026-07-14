package array.prefix_sum.contiguous_array

/**
 * https://leetcode.com/problems/contiguous-array/
 *
 * Given a binary array nums, return the maximum length of a contiguous subarray
 * with an equal number of 0 and 1.
 *
 * Example 1:
 *
 * Input: nums = [0,1]
 * Output: 2
 * Explanation: [0,1] has equal 0s and 1s.
 *
 * Example 2:
 *
 * Input: nums = [0,1,0]
 * Output: 2
 * Explanation: [0,1] or [1,0] both have equal 0s and 1s.
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)
 *
 * Key Insight: Replace 0 with -1. Then the problem becomes: find the longest subarray
 * with sum = 0. Use prefix sum + HashMap. If the same prefix sum appears at two indices,
 * the subarray between them sums to 0.
 */
fun main() {
    println(findMaxLength(intArrayOf(0, 1)))
    println(findMaxLength(intArrayOf(0, 1, 0)))
    println(findMaxLength(intArrayOf(0, 1, 1, 1, 1, 1, 0, 0, 0)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N)
 *
 * Approach: Replace 0 with -1, then find longest subarray with sum 0.
 * Use HashMap to store first occurrence of each prefix sum.
 * If same prefix sum appears again, subarray between them sums to 0.
 *
 * Trace for [0,1,0] → treat as [-1,1,-1]:
 * prefix=0: map={0:-1}
 * i=0: prefix=-1, not in map → map={0:-1, -1:0}
 * i=1: prefix=0, in map at -1 → length=1-(-1)=2, maxLen=2
 * i=2: prefix=-1, in map at 0 → length=2-0=2, maxLen=2
 * Result: 2 ✅
 */
fun findMaxLength(nums: IntArray): Int {
    val map = hashMapOf<Int, Int>(0 to -1)  // prefix sum → first index
    var maxLen = 0
    var prefixSum = 0

    for (i in nums.indices) {
        prefixSum += if (nums[i] == 0) -1 else 1

        if (prefixSum in map) {
            maxLen = maxOf(maxLen, i - map[prefixSum]!!)
        } else {
            map[prefixSum] = i
        }
    }

    return maxLen
}
