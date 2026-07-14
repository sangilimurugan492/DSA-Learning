package array.sliding_window.maximum_average_sub_array

/**
 * https://leetcode.com/problems/maximum-average-subarray-i/
 * Given integer array nums and integer k, find max average of any contiguous subarray of length k.
 * Example: nums = [1,12,-5,-6,50,3], k = 4 → Output: 12.75 (avg of [12,-5,-6,50])
 * FAANG Importance: ⭐⭐⭐ (Classic fixed-window warm-up)
 */

fun main() {
    println(findMaxAverageBruteForce(intArrayOf(1, 12, -5, -6, 50, 3), 4))
    println(findMaxAverageBruteForce(intArrayOf(5), 1))
    println("---")
    println(findMaxAverageSlidingWindow(intArrayOf(1, 12, -5, -6, 50, 3), 4))
    println(findMaxAverageSlidingWindow(intArrayOf(5), 1))
}

/**
 * BRUTE FORCE: O(N × K) — calculate sum for every window of size k
 */
fun findMaxAverageBruteForce(nums: IntArray, k: Int): Double {
    var maxAvg = Double.NEGATIVE_INFINITY
    for (i in 0..nums.size - k) {
        var sum = 0
        for (j in i until i + k) sum += nums[j]
        maxAvg = maxOf(maxAvg, sum.toDouble() / k)
    }
    return maxAvg
}

/**
 * OPTIMAL: O(N) Fixed Sliding Window
 * Maintain window sum. Add new element, remove old element.
 */
fun findMaxAverageSlidingWindow(nums: IntArray, k: Int): Double {
    var sum = 0.0
    for (i in 0 until k) sum += nums[i]
    var maxAvg = sum / k

    for (i in k until nums.size) {
        sum += nums[i] - nums[i - k]
        maxAvg = maxOf(maxAvg, sum / k)
    }
    return maxAvg
}
