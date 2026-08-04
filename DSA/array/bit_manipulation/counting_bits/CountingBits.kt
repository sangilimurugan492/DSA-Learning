package patterns.bit_manipulation.counting_bits

/**
 * https://leetcode.com/problems/counting-bits/
 * For each i from 0 to n, return array of count of 1 bits.
 * Example: n=2 → [0,1,1], n=5 → [0,1,1,2,1,2]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (DP + bit manipulation — must know)
 */

fun main() {
    println(countBits(2).toList())  // [0, 1, 1]
    println(countBits(5).toList())  // [0, 1, 1, 2, 1, 2]
}

/**
 * DP: O(N) time, O(N) space
 * ans[i] = ans[i >> 1] + (i & 1)
 * i >> 1 = i/2 (drop last bit), i & 1 = last bit (0 or 1)
 */
fun countBits(n: Int): IntArray {
    val ans = IntArray(n + 1)
    for (i in 1..n) {
        ans[i] = ans[i shr 1] + (i and 1)
    }
    return ans
}
