package DSA.interview_problem

/**
 * LeetCode #66 - Plus One
 * https://leetcode.com/problems/plus-one/
 *
 * You are given a large integer represented as an integer array digits,
 * where each digits[i] is the ith digit of the integer.
 * The digits are ordered from most significant to least significant
 * in left-to-right order. The large integer does not contain any leading 0's.
 *
 * Increment the large integer by one and return the resulting array of digits.
 *
 * Example:
 *   [3,9,9,9] => [4,0,0,0]
 *   [9]       => [1,0]
 *   [1,2,3]   => [1,2,4]
 *
 * Approach: Traditional digit-by-digit addition with carry propagation.
 * No string conversion used. Handles arbitrary large array sizes.
 *
 * Time Complexity: O(n) where n is the number of digits
 * Space Complexity: O(n) in worst case (when all digits are 9, we need a new array)
 */
fun plusOne(digits: IntArray): IntArray {
    var carry = 1 // We are adding 1, so start with carry = 1

    // Traverse from least significant digit to most significant
    for (i in digits.lastIndex downTo 0) {
        val sum = digits[i] + carry
        digits[i] = sum % 10
        carry = sum / 10

        // If no carry, we can return early — no further digits are affected
        if (carry == 0) return digits
    }

    // If we still have a carry after processing all digits (e.g., [9,9,9] => [1,0,0,0])
    // We need a new array with one extra position
    val result = IntArray(digits.size + 1)
    result[0] = carry // carry will be 1 here
    // Remaining positions are already 0 by default in IntArray
    return result
}

fun main() {
    // Test cases
    println(plusOne(intArrayOf(3, 9, 9, 9)).toList())   // [4, 0, 0, 0]
//    println(plusOne(intArrayOf(9)).toList())             // [1, 0]
//    println(plusOne(intArrayOf(1, 2, 3)).toList())       // [1, 2, 4]
    println(plusOne(intArrayOf(9, 9, 9)).toList())       // [1, 0, 0, 0]
//    println(plusOne(intArrayOf(9, 9, 9, 9)).toList())   // [9, 0, 0, 0]
//    println(plusOne(intArrayOf(0)).toList())              // [1]
}
