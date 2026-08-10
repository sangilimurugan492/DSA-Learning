package array.bit_manipulation.counting_bits

/**
 * https://leetcode.com/problems/counting-bits/
 *
 * Given an integer n, return an array ans of length n + 1 such that for each i (0 <= i <= n),
 * ans[i] is the number of 1's in the binary representation of i.
 *
 * Constraints:
 *   0 <= n <= 10^5
 *
 * Example 1:
 *   Input:  n = 2
 *   Output: [0, 1, 1]
 *   Explanation: 0 → 0, 1 → 1, 2 → 10
 *
 * Example 2:
 *   Input:  n = 5
 *   Output: [0, 1, 1, 2, 1, 2]
 *   Explanation: 0→0, 1→1, 2→10, 3→11, 4→100, 5→101
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (DP + bit manipulation — must know)
 */
fun main() {
    println(countBits(2).toList())  // [0, 1, 1]
    println(countBits(5).toList())  // [0, 1, 1, 2, 1, 2]
}

/**
 * DP + Bit Manipulation: O(N) time, O(N) space
 *
 * Key insight: ans[i] = ans[i >> 1] + (i & 1)
 *   - i >> 1 = i / 2 (drops the last bit)
 *   - i & 1 = last bit (0 or 1)
 *
 * The number of 1-bits in i equals the number of 1-bits in i/2 (which we already computed)
 * plus the last bit of i (0 or 1).
 *
 * Trace for n = 5:
 *   ans[0] = 0 (base case)
 *   ans[1] = ans[0] + (1 & 1) = 0 + 1 = 1
 *   ans[2] = ans[1] + (2 & 1) = 1 + 0 = 1
 *   ans[3] = ans[1] + (3 & 1) = 1 + 1 = 2
 *   ans[4] = ans[2] + (4 & 1) = 1 + 0 = 1
 *   ans[5] = ans[2] + (5 & 1) = 1 + 1 = 2
 *   Result = [0, 1, 1, 2, 1, 2] ✅
 */
fun countBits(n: Int): IntArray {
    val ans = IntArray(n + 1)
    for (i in 1..n) {
        ans[i] = ans[i shr 1] + (i and 1)
    }
    return ans
}

