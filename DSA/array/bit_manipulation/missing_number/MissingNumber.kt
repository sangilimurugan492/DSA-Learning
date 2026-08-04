package patterns.bit_manipulation.missing_number

/**
 * https://leetcode.com/problems/missing-number/
 * Array contains n distinct numbers from 0 to n. Find the missing one.
 * Example: [3,0,1] → 2,  [0,1] → 2,  [9,6,4,2,3,5,7,0,1] → 8
 * FAANG Importance: ⭐⭐⭐⭐⭐ (XOR + Math — must know)
 */

fun main() {
    println(missingNumberXOR(intArrayOf(3, 0, 1)))  // 2
    println(missingNumberMath(intArrayOf(0, 1)))     // 2
    println(missingNumberMath(intArrayOf(9, 6, 4, 2, 3, 5, 7, 0, 1)))  // 8
}

/**
 * XOR: O(N) time, O(1) space
 * XOR all indices (0..n) and all array elements. Duplicates cancel.
 */
fun missingNumberXOR(nums: IntArray): Int {
    var result = nums.size  // Start with n (index n is not in array)
    for (i in nums.indices) {
        result = result xor i xor nums[i]
    }
    return result
}

/**
 * Math (Gauss formula): O(N) time, O(1) space
 * Expected sum = n*(n+1)/2. Actual sum = sum(nums). Missing = expected - actual.
 */
fun missingNumberMath(nums: IntArray): Int {
    val n = nums.size
    val expected = n * (n + 1) / 2
    val actual = nums.sum()
    return expected - actual
}
