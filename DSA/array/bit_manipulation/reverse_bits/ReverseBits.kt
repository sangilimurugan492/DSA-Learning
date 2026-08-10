package array.bit_manipulation.reverse_bits

/**
 * https://leetcode.com/problems/reverse-bits/
 *
 * Reverse the 32 bits of a given unsigned integer.
 *
 * Note: In Kotlin/Java, integers are 32-bit signed. Treat the input as unsigned
 * by using unsigned right shift (ushr).
 *
 * Constraints:
 *   The input must be a binary string of length 32.
 *
 * Example 1:
 *   Input:  n = 43261596 (binary: 00000010100101000001111010011100)
 *   Output: 964176192   (binary: 00111001011110000010100101000000)
 *
 * Example 2:
 *   Input:  n = 0
 *   Output: 0
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Bit reversal — must know)
 */


fun main() {
    println(reverseBits(43261596))  // 964176192
    println(reverseBits(0))         // 0
}

/**
 * Bit-by-bit reversal: O(1) time (32 iterations), O(1) space
 * For each of 32 bits: shift result left, add LSB of n, shift n right.
 */
fun reverseBits(n: Int): Int {
    var result = 0
    var num = n
    for (i in 0 until 32) {
        result = result shl 1          // Make room for next bit
        result = result or (num and 1)  // Add LSB of n
        num = num ushr 1                // Next bit of n
    }
    return result
}
