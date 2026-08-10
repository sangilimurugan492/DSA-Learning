package array.voting_floyd.missing_number_in_array

/**
 * https://leetcode.com/problems/missing-number/
 *
 * Given an array nums containing n distinct numbers in the range [0, n], return the only
 * number in the range that is missing from the array.
 *
 * Constraints:
 *   n == nums.length
 *   1 <= n <= 10^4
 *   0 <= nums[i] <= n
 *   All numbers in nums are unique.
 *
 * Example 1:
 *   Input:  nums = [3, 0, 1]
 *   Output: 2
 *   Explanation: n = 3, range is [0, 3]. 2 is missing.
 *
 * Example 2:
 *   Input:  nums = [0, 1]
 *   Output: 2
 *   Explanation: n = 2, range is [0, 2]. 2 is missing.
 *
 * Example 3:
 *   Input:  nums = [9, 6, 4, 2, 3, 5, 7, 0, 1]
 *   Output: 8
 *   Explanation: n = 9, range is [0, 9]. 8 is missing.
 */
fun main() {
    println(missingNumberBF(intArrayOf(3, 0, 1)))                    // 2
    println(missingNumberOP(intArrayOf(3, 0, 1)))                    // 2
    println(missingNumberOP(intArrayOf(0, 1)))                       // 2
    println(missingNumberOP(intArrayOf(9, 6, 4, 2, 3, 5, 7, 0, 1))) // 8
}

/**
 * Brute Force — Check Each Number in Range
 *
 * For each number from 0 to n, check if it exists in the array. If not, it's the
 * missing number.
 *
 * Time Complexity:  O(N²) — for each of N+1 numbers, scan the array
 * Space Complexity: O(1)
 */
fun missingNumberBF(nums: IntArray): Int {
    val n = nums.size
    for (i in 0..n) {
        var found = false
        for (num in nums) {
            if (num == i) {
                found = true
                break
            }
        }
        if (!found) return i
    }
    return -1
}

/**
 * Optimal — Gauss' Formula (Math)
 *
 * Key insight: The sum of numbers from 0 to n is n*(n+1)/2 (Gauss' formula).
 * If we subtract the actual sum of the array from this expected sum, the difference
 * is the missing number.
 *
 *   missing = expectedSum - actualSum
 *           = n*(n+1)/2 - sum(nums)
 *
 * Trace for nums = [3, 0, 1]:
 *
 *   n = 3
 *   expectedSum = 3 * 4 / 2 = 6
 *   actualSum = 3 + 0 + 1 = 4
 *   missing = 6 - 4 = 2 ✅
 *
 * Note: Use Long to avoid overflow for large n (up to 10^4, sum up to ~5*10^7, which
 * fits in Int, but Long is safer for general use).
 *
 * Time Complexity:  O(N) — single pass to compute sum
 * Space Complexity: O(1)
 */
fun missingNumberOP(nums: IntArray): Int {
    val n = nums.size

    // Expected sum of 0 + 1 + 2 + ... + n (Gauss' formula)
    val expectedSum = n.toLong() * (n + 1) / 2

    // Actual sum of array elements
    var actualSum = 0L
    for (num in nums) {
        actualSum += num
    }

    return (expectedSum - actualSum).toInt()
}
