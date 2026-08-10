package array.bit_manipulation.sum_of_two_integers

/**
 * https://leetcode.com/problems/sum-of-two-integers/
 *
 * Given two integers a and b, return the sum of the two integers without using the
 * operators + and -.
 *
 * Constraints:
 *   -1000 <= a, b <= 1000
 *
 * Example 1:
 *   Input:  a = 1, b = 2
 *   Output: 3
 *
 * Example 2:
 *   Input:  a = 2, b = 3
 *   Output: 5
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Bit manipulation — carry propagation)
 */


fun main() {
    println(getSum(1, 2))   // 3
    println(getSum(2, 3))   // 5
    println(getSum(-1, 1))  // 0
    println(getSum(-14, 16)) // 2
}

/**
 * Bit manipulation: O(1) time (max 32 iterations), O(1) space
 * Sum = XOR (carry-less add) + AND shifted left (carry).
 * Repeat until no carry.
 */
fun getSum(a: Int, b: Int): Int {
    var x = a
    var y = b
    while (y != 0) {
        val carry = (x and y) shl 1  // Carry bits, shifted left
        x = x xor y                    // Sum without carry
        y = carry                      // Repeat with carry
    }
    return x
}
