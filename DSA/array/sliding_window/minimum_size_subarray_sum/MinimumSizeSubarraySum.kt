package array.sliding_window.minimum_size_subarray_sum

/**
 * https://leetcode.com/problems/minimum-size-subarray-sum/
 *
 * Given an array of positive integers nums and a positive integer target, return the
 * **minimum length** of a contiguous subarray whose sum is greater than or equal to
 * target. If there is no such subarray, return 0.
 *
 * Constraints:
 *   1 <= target <= 10^9
 *   1 <= nums.length <= 10^5
 *   1 <= nums[i] <= 10^4
 *
 * Example 1:
 *   Input:  target = 7, nums = [2, 3, 1, 2, 4, 3]
 *   Output: 2
 *   Explanation: The subarray [4, 3] has the minimal length with sum >= 7.
 *
 * Example 2:
 *   Input:  target = 4, nums = [1, 4, 4]
 *   Output: 1
 *   Explanation: The subarray [4] has sum >= 4.
 *
 * Example 3:
 *   Input:  target = 11, nums = [1, 1, 1, 1, 1, 1, 1, 1]
 *   Output: 0
 *   Explanation: No subarray has sum >= 11.
 */
fun main() {
    println(minSubArrayLenBF(7, intArrayOf(2, 3, 1, 2, 4, 3))) // 2
    println(minSubArrayLenOP(7, intArrayOf(2, 3, 1, 2, 4, 3))) // 2
    println(minSubArrayLenOP(4, intArrayOf(1, 4, 4)))           // 1
    println(minSubArrayLenOP(11, intArrayOf(1, 1, 1, 1, 1, 1, 1, 1))) // 0
}

/**
 * Brute Force — Check All Subarrays
 *
 * For each starting index i, extend the subarray to the right, accumulating the sum.
 * If the sum >= target, update the minimum length.
 *
 * Time Complexity:  O(N²) — for each start, scan to the end
 * Space Complexity: O(1)
 */
fun minSubArrayLenBF(target: Int, nums: IntArray): Int {
    val n = nums.size
    var minLen = Int.MAX_VALUE

    for (i in 0 until n) {
        var sum = 0
        for (j in i until n) {
            sum += nums[j]
            if (sum >= target) {
                minLen = minOf(minLen, j - i + 1)
                break // No need to extend further; longer subarrays won't be shorter
            }
        }
    }

    return if (minLen == Int.MAX_VALUE) 0 else minLen
}

/**
 * Optimal — Sliding Window (Two Pointers)
 *
 * Key insight: Since all elements are positive, the sum is monotonically increasing as
 * we expand the window. This means we can use a sliding window:
 *   - Expand the right pointer to add elements until sum >= target.
 *   - Contract the left pointer to try to find a shorter valid window.
 *   - Track the minimum window length.
 *
 * Steps:
 * 1. Maintain a window [left, right] and a running sum.
 * 2. Expand right: add nums[right] to sum.
 * 3. While sum >= target: update minLen, then shrink from left (subtract nums[left], left++).
 * 4. Return minLen (or 0 if no valid window found).
 *
 * Trace for target = 7, nums = [2, 3, 1, 2, 4, 3]:
 *
 *   left=0, sum=0, minLen=∞
 *
 *   right=0: sum=2 (< 7)
 *   right=1: sum=5 (< 7)
 *   right=2: sum=6 (< 7)
 *   right=3: sum=8 (≥ 7) → minLen=4, shrink: sum=6, left=1
 *   right=4: sum=10 (≥ 7) → minLen=4, shrink: sum=7, left=2 → minLen=3, shrink: sum=6, left=3
 *   right=5: sum=9 (≥ 7) → minLen=3, shrink: sum=7, left=4 → minLen=2, shrink: sum=3, left=5
 *
 *   minLen = 2 ✅  (subarray [4, 3])
 *
 * Time Complexity:  O(N) — each element is visited at most twice (once by right, once by left)
 * Space Complexity: O(1) — only pointers and a sum variable
 */
fun minSubArrayLenOP(target: Int, nums: IntArray): Int {
    val n = nums.size
    var left = 0
    var sum = 0
    var minLen = Int.MAX_VALUE

    for (right in 0 until n) {
        sum += nums[right] // Expand window to the right

        // Contract window from the left while sum is still >= target
        while (sum >= target) {
            minLen = minOf(minLen, right - left + 1)
            sum -= nums[left]
            left++
        }
    }

    return if (minLen == Int.MAX_VALUE) 0 else minLen
}
