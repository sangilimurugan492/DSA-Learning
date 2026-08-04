package patterns.bit_manipulation.number_of_1_bits

/**
 * https://leetcode.com/problems/number-of-1-bits/
 * Count the number of '1' bits in an unsigned integer (Hamming weight).
 * Example: n = 11 (1011) → 3
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Brian Kernighan's algorithm — must know)
 */

fun main() {
    println(hammingWeight(11))   // 3 (1011)
    println(hammingWeight(128))  // 1 (10000000)
    println(hammingWeightK(11))  // 3
}

/**
 * Brian Kernighan's Algorithm: O(k) time, O(1) space
 * k = number of 1 bits. n & (n-1) removes the lowest set bit.
 */
fun hammingWeight(n: Int): Int {
    var count = 0
    var num = n
    while (num != 0) {
        num = num and (num - 1)  // Removes lowest set bit
        count++
    }
    return count
}

/**
 * Bit-by-bit check: O(32) time, O(1) space
 * Check each of 32 bits.
 */
fun hammingWeightK(n: Int): Int {
    var count = 0
    var num = n
    for (i in 0 until 32) {
        count += num and 1
        num = num ushr 1  // Unsigned right shift
    }
    return count
}
