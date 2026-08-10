package array.linear_scan.plus_one

/**
 * https://leetcode.com/problems/plus-one/
 *
 * You are given a large integer represented as an integer array digits, where each
 * digits[i] is the ith digit of the integer. The digits are ordered from most significant
 * to least significant in left-to-right order. The large integer does not contain any
 * leading 0's.
 *
 * Increment the large integer by one and return the resulting array of digits.
 *
 * Constraints:
 *   1 <= digits.length <= 100
 *   0 <= digits[i] <= 9
 *   digits does not contain any leading 0's.
 *
 * Example 1:
 *   Input:  digits = [1, 2, 3]
 *   Output: [1, 2, 4]
 *   Explanation: 123 + 1 = 124
 *
 * Example 2:
 *   Input:  digits = [4, 3, 2, 1]
 *   Output: [4, 3, 2, 2]
 *   Explanation: 4321 + 1 = 4322
 *
 * Example 3:
 *   Input:  digits = [9]
 *   Output: [1, 0]
 *   Explanation: 9 + 1 = 10
 *
 * FAANG Importance: ⭐⭐⭐⭐
 */
fun main() {
    println(plusOneString(intArrayOf(1, 2, 3)).toList())  // [1, 2, 4]
    println(plusOne(intArrayOf(1, 2, 3)).toList())        // [1, 2, 4]
    println(plusOne(intArrayOf(3, 9, 9, 9)).toList())    // [4, 0, 0, 0]
    println(plusOne(intArrayOf(9, 9, 9)).toList())        // [1, 0, 0, 0]
    println(plusOne(intArrayOf(9)).toList())              // [1, 0]
}

/**
 * Method 1: String Conversion — O(N)
 *
 * Convert array → string → BigInteger → add 1 → convert back to array.
 * Works for arbitrarily large inputs but uses extra space for string conversion.
 *
 * Time Complexity:  O(N) — conversion + addition
 * Space Complexity: O(N) — string + result array
 */
fun plusOneString(digits: IntArray): IntArray {
    val num = digits.joinToString("").toBigInteger() + 1.toBigInteger()
    return num.toString().map { it - '0' }.toIntArray()
}

/**
 * Method 2: Digit-by-Digit with Carry (Optimal) — O(N)
 *
 * Core Idea:
 *   - Start with carry = 1 (we're adding 1).
 *   - For each digit from right to left: sum = digit + carry,
 *     digit = sum % 10, carry = sum / 10.
 *   - If carry becomes 0, return early (no further digits affected).
 *   - If carry remains after all digits, create new array with carry at front.
 *
 * Trace for [3, 9, 9, 9]:
 *
 *   carry = 1
 *   i=3: sum = 9 + 1 = 10 → digits[3] = 0, carry = 1
 *   i=2: sum = 9 + 1 = 10 → digits[2] = 0, carry = 1
 *   i=1: sum = 9 + 1 = 10 → digits[1] = 0, carry = 1
 *   i=0: sum = 3 + 1 = 4  → digits[0] = 4, carry = 0 → early return!
 *
 *   Result = [4, 0, 0, 0] ✅
 *
 * Trace for [9, 9, 9]:
 *
 *   carry = 1
 *   i=2: sum = 9 + 1 = 10 → digits[2] = 0, carry = 1
 *   i=1: sum = 9 + 1 = 10 → digits[1] = 0, carry = 1
 *   i=0: sum = 9 + 1 = 10 → digits[0] = 0, carry = 1
 *   carry remains → new array: [1, 0, 0, 0] ✅
 *
 * Time Complexity:  O(N) — traverse once from right to left
 * Space Complexity: O(N) worst case (all 9s → new array of size N+1), O(1) best case
 */
fun plusOne(digits: IntArray): IntArray {
    var carry = 1

    for (i in digits.lastIndex downTo 0) {
        val sum = digits[i] + carry
        digits[i] = sum % 10
        carry = sum / 10
        if (carry == 0) return digits // Early return — no further digits affected
    }

    // Carry remains → need one extra digit at front (e.g., [9,9,9] → [1,0,0,0])
    val result = IntArray(digits.size + 1)
    result[0] = carry
    // result[1..n] are already 0 (IntArray initializes to 0)
    return result
}
