package interview_problem.plus_one

/**
 * Plus One — LeetCode #66
 * https://leetcode.com/problems/plus-one/
 *
 * Problem:
 * -------
 * Given a large integer represented as an integer array digits, increment by one
 * and return the resulting array of digits.
 *
 * Example:  [1,2,3] → [1,2,4],  [3,9,9,9] → [4,0,0,0],  [9] → [1,0]
 *
 * FAANG Importance: ⭐⭐⭐⭐
 *
 * Two approaches:
 * 1. String Conversion: O(N) — convert to number, add 1, convert back
 * 2. Digit-by-Digit with Carry: O(N) — traverse from right, propagate carry
 */

fun main() {
    println("=== Method 1: String Conversion ===")
    println("plusOne([1,2,3]) = ${plusOneString(intArrayOf(1, 2, 3)).toList()}")
    println("plusOne([9,9,9]) = ${plusOneString(intArrayOf(9, 9, 9)).toList()}")

    println("\n=== Method 2: Digit-by-Digit with Carry ===")
    println("plusOne([1,2,3]) = ${plusOne(intArrayOf(1, 2, 3)).toList()}")
    println("plusOne([3,9,9,9]) = ${plusOne(intArrayOf(3, 9, 9, 9)).toList()}")
    println("plusOne([9,9,9]) = ${plusOne(intArrayOf(9, 9, 9)).toList()}")

    println("\n=== Step-by-step trace ===")
    plusOneTrace(intArrayOf(3, 9, 9, 9))
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: STRING CONVERSION — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * STRING CONVERSION — Convert to string, to BigInteger, add 1, convert back.
 *
 * Note: Fails for very large arrays (overflow with Long). Use BigInteger for safety.
 *
 * Time Complexity:  O(N).
 * Space Complexity: O(N).
 */
fun plusOneString(digits: IntArray): IntArray {
    val num = digits.joinToString("").toBigInteger() + 1.toBigInteger()
    return num.toString().map { it - '0' }.toIntArray()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: DIGIT-BY-DIGIT WITH CARRY — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DIGIT-BY-DIGIT — Traverse from least significant digit, add carry, propagate.
 *
 * Core Idea:
 *   - Start with carry = 1 (we're adding 1).
 *   - For each digit from right to left: sum = digit + carry, digit = sum % 10, carry = sum / 10.
 *   - If carry becomes 0, return early.
 *   - If carry remains after all digits, create new array with carry at front.
 *
 * Time Complexity:  O(N) — traverse once.
 * Space Complexity: O(N) worst case (all 9s → new array).
 */
fun plusOne(digits: IntArray): IntArray {
    var carry = 1

    for (i in digits.lastIndex downTo 0) {
        val sum = digits[i] + carry
        digits[i] = sum % 10
        carry = sum / 10
        if (carry == 0) return digits  // Early return — no further digits affected.
    }

    // Carry remains → need one extra digit at front (e.g., [9,9,9] → [1,0,0,0])
    val result = IntArray(digits.size + 1)
    result[0] = carry
    return result
}

/**
 * Digit-by-digit with step-by-step trace.
 */
fun plusOneTrace(digits: IntArray) {
    println("Input: ${digits.toList()}")
    var carry = 1
    val arr = digits.copyOf()

    for (i in arr.lastIndex downTo 0) {
        val sum = arr[i] + carry
        arr[i] = sum % 10
        carry = sum / 10
        println("  digit[$i]: ${digits[i]} + $carry = $sum → ${arr[i]}, carry=$carry")
        if (carry == 0) {
            println("  Early return: ${arr.toList()}")
            return
        }
    }
    val result = IntArray(arr.size + 1)
    result[0] = carry
    println("  Carry remains → new array: ${result.toList()}")
}
