package patterns.prefix_sum.longest_subarray_with_sum_k

/**
 * https://www.geeksforgeeks.org/problems/longest-sub-array-with-sum-k/0809
 * Find the length of the longest subarray with sum equal to K.
 * Example: nums = [1,2,3,1,1,1,1], K = 3 → Output: 3 ([1,1,1] or [1,2])
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Prefix sum + HashMap pattern — must know)
 */

fun main() {
    println(longestSubarrayWithSumK(intArrayOf(1, 2, 3, 1, 1, 1, 1), 3))  // 3
    println(longestSubarrayWithSumK(intArrayOf(-1, 1, 1), 1))              // 3
    println(longestSubarrayWithSumKPositive(intArrayOf(1, 2, 3, 1, 1, 1, 1), 3))  // 3
}

/**
 * APPROACH 1: Prefix Sum + HashMap — O(N) time, O(N) space
 * Works for positive AND negative numbers.
 * Store first occurrence of each prefix sum. If (prefixSum - K) seen, we found a subarray.
 */
fun longestSubarrayWithSumK(nums: IntArray, k: Int): Int {
    val prefixSumIndex = HashMap<Int, Int>()  // prefixSum → first index
    prefixSumIndex[0] = -1  // Empty prefix at index -1
    var prefixSum = 0
    var maxLen = 0

    for (i in nums.indices) {
        prefixSum += nums[i]
        // If (prefixSum - k) exists, subarray from that index+1 to i has sum k
        if (prefixSum - k in prefixSumIndex) {
            maxLen = maxOf(maxLen, i - prefixSumIndex[prefixSum - k]!!)
        }
        // Only store FIRST occurrence (for longest subarray)
        if (prefixSum !in prefixSumIndex) {
            prefixSumIndex[prefixSum] = i
        }
    }
    return maxLen
}

/**
 * APPROACH 2: Sliding Window — O(N) time, O(1) space
 * Only works for NON-NEGATIVE numbers.
 * Expand right, shrink left when sum > k.
 */
fun longestSubarrayWithSumKPositive(nums: IntArray, k: Int): Int {
    var left = 0
    var sum = 0
    var maxLen = 0

    for (right in nums.indices) {
        sum += nums[right]
        while (sum > k && left <= right) {
            sum -= nums[left]
            left++
        }
        if (sum == k) {
            maxLen = maxOf(maxLen, right - left + 1)
        }
    }
    return maxLen
}
