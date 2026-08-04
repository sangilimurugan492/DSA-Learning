package patterns.bit_manipulation.reverse_bits

/**
 * https://leetcode.com/problems/reverse-bits/
 * Reverse the 32 bits of an unsigned integer.
 * Example: n=43261596 (10100101000011000010100100) → 964176192
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
