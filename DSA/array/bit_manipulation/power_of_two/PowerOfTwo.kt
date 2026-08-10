package array.bit_manipulation.power_of_two

/**
 * https://leetcode.com/problems/power-of-two/
 *
 * Given an integer n, return true if it is a power of two. Otherwise, return false.
 * An integer n is a power of two if there exists an integer x such that n == 2^x.
 *
 * Constraints:
 *   -2^31 <= n <= 2^31 - 1
 *
 * Example 1:
 *   Input:  n = 1
 *   Output: true  (2^0 = 1)
 *
 * Example 2:
 *   Input:  n = 16
 *   Output: true  (2^4 = 16)
 *
 * Example 3:
 *   Input:  n = 3
 *   Output: false
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Single set bit check — must know)
 */


fun main() {
    println(isPowerOfTwo(1))   // true
    println(isPowerOfTwo(16))  // true
    println(isPowerOfTwo(3))   // false
    println(isPowerOfTwo(0))   // false
    println(isPowerOfTwo(-16)) // false
}

/**
 * Bit trick: O(1) time, O(1) space
 * Power of 2 has exactly one '1' bit. n > 0 && (n & (n-1)) == 0.
 * n & (n-1) removes the lowest set bit. If result is 0 → only one bit was set.
 */
fun isPowerOfTwo(n: Int): Boolean {
    return n > 0 && (n and (n - 1)) == 0
}
