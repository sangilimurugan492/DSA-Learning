package array.sliding_window.longest_sub_array_after_deleting_one

/**
 * https://leetcode.com/problems/longest-subarray-of-1s-after-deleting-one-element/
 * Given binary array, return longest subarray of 1s after deleting one element.
 * Example: [0,1,1,1,0,1,1,0,1] → 5 (delete the 0 at index 4)
 * FAANG Importance: ⭐⭐⭐⭐
 */

fun main() {
    println(longestSubarrayBruteForce(intArrayOf(0, 1, 1, 1, 0, 1, 1, 0, 1)))
    println(longestSubarrayBruteForce(intArrayOf(1, 1, 0, 1)))
    println("---")
    println(longestSubarraySlidingWindow(intArrayOf(0, 1, 1, 1, 0, 1, 1, 0, 1)))
    println(longestSubarraySlidingWindow(intArrayOf(1, 1, 0, 1)))
}

/**
 * BRUTE FORCE: O(N²) — try deleting each element, find longest run of 1s
 */
fun longestSubarrayBruteForce(nums: IntArray): Int {
    var maxLen = 0
    for (deleteIdx in nums.indices) {
        var len = 0
        var best = 0
        for (i in nums.indices) {
            if (i == deleteIdx) continue
            if (nums[i] == 1) { len++; best = maxOf(best, len) }
            else len = 0
        }
        maxLen = maxOf(maxLen, best)
    }
    return maxLen
}

/**
 * OPTIMAL: O(N) Sliding Window — at most 1 zero in window
 * Window size - 1 = answer (must delete one element)
 */
fun longestSubarraySlidingWindow(nums: IntArray): Int {
    var left = 0
    var zeroCount = 0
    var maxLen = 0

    for (right in nums.indices) {
        if (nums[right] == 0) zeroCount++

        while (zeroCount > 1) {
            if (nums[left] == 0) zeroCount--
            left++
        }
        maxLen = maxOf(maxLen, right - left)  // -1 because must delete one
    }
    return maxLen
}
