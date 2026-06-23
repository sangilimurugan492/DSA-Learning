package array.two_pointer

/**
 * https://leetcode.com/problems/max-consecutive-ones-iii/
 *
 * Given a binary array nums and an integer k, return the maximum number of
 * consecutive 1's in the array if you can flip at most k 0's.
 *
 * Example 1:
 *
 * Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
 * Output: 6
 * Explanation: [1,1,1,0,0,1,1,1,1,1,1] — flip last two 0s
 *
 * Example 2:
 *
 * Input: nums = [0,0,1,1,1,0,0], k = 0
 * Output: 3
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon — sliding window classic)
 *
 * Key Insight: Sliding window where we allow at most k zeros. When zeros exceed k,
 * shrink from left. Track max window size.
 */
fun main() {
    println(longestOnes(intArrayOf(1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0), 2))
    println(longestOnes(intArrayOf(0, 0, 1, 1, 1, 0, 0), 0))
    println(longestOnes(intArrayOf(1, 1, 1, 1, 1), 2))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: Sliding window with two pointers
 *
 * Expand right, count zeros. When zeros > k, shrink from left until zeros ≤ k.
 * Track max window size throughout.
 *
 * Trace for [1,1,1,0,0,0,1,1,1,1,0], k=2:
 * right=0..2: window=[1,1,1], zeros=0, maxLen=3
 * right=3..4: window=[1,1,1,0,0], zeros=2, maxLen=5
 * right=5: zeros=3 > k=2 → shrink left: left=1,2,3 → zeros=2, window=[1,0,0,0]
 *       still 3 zeros → shrink more: left=4 → zeros=2, window=[0,0]
 *       wait — let me redo. left moves past first 0: left=4, zeros becomes 2
 * right=5: zeros=3, shrink: move left past first 0 at index 3 → zeros=2, left=4
 * right=6..9: expand, window=[0,0,1,1,1,1], zeros=2, maxLen=6
 * right=10: zeros=3 > k → shrink: left=5 → zeros=2, maxLen=6
 * Result: 6 ✅
 */
fun longestOnes(nums: IntArray, k: Int): Int {
    var left = 0
    var zeros = 0
    var maxLen = 0

    for (right in nums.indices) {
        if (nums[right] == 0) zeros++

        while (zeros > k) {
            if (nums[left] == 0) zeros--
            left++
        }

        maxLen = maxOf(maxLen, right - left + 1)
    }

    return maxLen
}
