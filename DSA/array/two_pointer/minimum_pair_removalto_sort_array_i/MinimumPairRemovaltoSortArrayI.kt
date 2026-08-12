package array.two_pointer.minimum_pair_removalto_sort_array_i

/**
 * https://leetcode.com/problems/minimum-pair-removal-to-sort-array-i/
 *
 * Given an array nums, return the minimum number of elements to remove so that
 * the remaining array is non-decreasing (sorted).
 *
 * Key Insight: This is equivalent to finding the Longest Non-Decreasing Subsequence (LNDS)
 * and subtracting its length from n. Elements not in the LNDS are the ones to remove.
 *
 * Answer = n - length_of_LNDS
 *
 * Two approaches:
 * 1. Brute Force (DP): O(N²) — standard LIS DP
 * 2. Optimal (Binary Search): O(N log N) — patience sorting with binary search
 */
fun main() {
    println(minPairRemovalBF(intArrayOf(5, 2, 3, 1)))  // 2
    println(minPairRemovalOP(intArrayOf(5, 2, 3, 1)))  // 2
}

/**
 * Brute Force (DP): Find LNDS using standard DP.
 * dp[i] = length of longest non-decreasing subsequence ending at i.
 *
 * Answer = n - max(dp[i])
 *
 * Time Complexity:  O(N²)
 * Space Complexity: O(N)
 */
fun minPairRemovalBF(nums: IntArray): Int {
    val n = nums.size
    if (n <= 1) return 0

    val dp = IntArray(n) { 1 }
    var maxLNDS = 1

    for (i in 1 until n) {
        for (j in 0 until i) {
            // If non-decreasing condition met, extend the subsequence
            if (nums[j] <= nums[i]) {
                dp[i] = maxOf(dp[i], dp[j] + 1)
            }
        }
        maxLNDS = maxOf(maxLNDS, dp[i])
    }

    // Total elements minus the ones we kept in the LNDS
    return n - maxLNDS
}

/**
 * Optimal (Binary Search — Patience Sorting):
 * Maintain a `tails` list where tails[i] = smallest tail of all LNDS of length i+1.
 * For each num, binary search to find where it should go.
 *
 * Answer = n - tails.size
 *
 * Time Complexity:  O(N log N)
 * Space Complexity: O(N)
 */
fun minPairRemovalOP(nums: IntArray): Int {
    val tails = mutableListOf<Int>()

    for (num in nums) {
        // Binary search: find the first element in tails strictly greater than num
        var left = 0
        var right = tails.size
        while (left < right) {
            val mid = left + (right - left) / 2
            if (tails[mid] <= num) {
                left = mid + 1
            } else {
                right = mid
            }
        }

        if (left == tails.size) {
            // num extends the longest subsequence
            tails.add(num)
        } else {
            // Replace — we found a smaller tail for this length
            tails[left] = num
        }
    }

    return nums.size - tails.size
}
