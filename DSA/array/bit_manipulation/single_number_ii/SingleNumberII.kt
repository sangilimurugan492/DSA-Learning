package array.bit_manipulation.single_number_ii

/**
 * https://leetcode.com/problems/single-number-ii/
 *
 * Given an integer array nums where every element appears three times except for one,
 * which appears exactly once. Find the single element and return it.
 * Must run in O(n) time and O(1) space.
 *
 * Example 1:
 *
 * Input: nums = [2,2,3,2]
 * Output: 3
 *
 * Example 2:
 *
 * Input: nums = [0,1,0,1,0,1,99]
 * Output: 99
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Key Insight: Count bits at each position. If a bit position has count % 3 == 1,
 * that bit belongs to the single number. Use two variables (ones, twos) to track
 * bits that have appeared 1 time and 2 times. When a bit appears 3 times, reset both.
 */
fun main() {
    println(singleNumberII(intArrayOf(2, 2, 3, 2)))
    println(singleNumberII(intArrayOf(0, 1, 0, 1, 0, 1, 99)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: Bit manipulation with two counters
 *
 * ones = bits that have appeared 1 time (mod 3)
 * twos = bits that have appeared 2 times (mod 3)
 *
 * When a bit appears 3 times: both ones and twos get reset for that bit.
 *
 * Update rules:
 * ones = (ones ^ num) & ~twos  → add to ones if not in twos
 * twos = (twos ^ num) & ~ones  → add to twos if not in ones (after update)
 *
 * Trace for [2,2,3,2]:
 * num=2: ones=2, twos=0
 * num=2: ones=0, twos=2
 * num=3: ones=3, twos=2
 * num=2: ones=0, twos=0 → wait, let me recalculate
 *
 * Actually: ones tracks bits seen once, twos tracks bits seen twice.
 * After seeing 3 of the same number, both reset → the single number remains in ones.
 */
fun singleNumberII(nums: IntArray): Int {
    var ones = 0
    var twos = 0

    for (num in nums) {
        ones = (ones xor num) and twos.inv()
        twos = (twos xor num) and ones.inv()
    }

    return ones
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Alternative: Count bits at each position
 * For each of the 32 bits, count how many numbers have that bit set.
 * If count % 3 != 0, that bit belongs to the answer.
 */
fun singleNumberIIBitCount(nums: IntArray): Int {
    var result = 0

    for (i in 0 until 32) {
        var bitSum = 0
        for (num in nums) {
            if ((num shr i) and 1 == 1) {
                bitSum++
            }
        }
        if (bitSum % 3 != 0) {
            result = result or (1 shl i)
        }
    }

    return result
}
