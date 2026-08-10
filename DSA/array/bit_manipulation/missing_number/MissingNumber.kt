package array.bit_manipulation.missing_number

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
 *
 * Example 2:
 *   Input:  nums = [0, 1]
 *   Output: 2
 *
 * Example 3:
 *   Input:  nums = [9, 6, 4, 2, 3, 5, 7, 0, 1]
 *   Output: 8
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (XOR + Math — must know)
 */
fun main() {
    println(missingNumberXOR(intArrayOf(3, 0, 1)))                    // 2
    println(missingNumberMath(intArrayOf(0, 1)))                       // 2
    println(missingNumberMath(intArrayOf(9, 6, 4, 2, 3, 5, 7, 0, 1))) // 8
}

/**
 * XOR Approach: O(N) time, O(1) space
 *
 * Key insight: XOR all indices (0..n) and all array elements. Since x ^ x = 0 and
 * x ^ 0 = x, all paired numbers cancel out, leaving only the missing number.
 *
 * Trace for nums = [3, 0, 1], n = 3:
 *   result = 3 (start with n, since index n is never in the array)
 *   i=0: result = 3 ^ 0 ^ 3 = 0
 *   i=1: result = 0 ^ 1 ^ 0 = 1
 *   i=2: result = 1 ^ 2 ^ 1 = 2
 *   Result = 2 ✅
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(1)
 */
fun missingNumberXOR(nums: IntArray): Int {
    var result = nums.size  // Start with n (index n is not in array)
    for (i in nums.indices) {
        result = result xor i xor nums[i]
    }
    return result
}

/**
 * Math (Gauss' Formula): O(N) time, O(1) space
 *
 * Expected sum = n*(n+1)/2. Actual sum = sum(nums). Missing = expected - actual.
 *
 * Trace for nums = [3, 0, 1], n = 3:
 *   expected = 3*4/2 = 6
 *   actual = 3 + 0 + 1 = 4
 *   missing = 6 - 4 = 2 ✅
 *
 * Time Complexity:  O(N) — single pass to compute sum
 * Space Complexity: O(1)
 */
fun missingNumberMath(nums: IntArray): Int {
    val n = nums.size
    val expected = n * (n + 1) / 2
    val actual = nums.sum()
    return expected - actual
}

