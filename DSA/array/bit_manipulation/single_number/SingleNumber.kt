package array.bit_manipulation.single_number

/**
 * https://leetcode.com/problems/single-number/
 * Given array where every element appears twice except one, find the single one.
 * Example: [4,1,2,1,2] → 4
 * FAANG Importance: ⭐⭐⭐⭐
 */

fun main() {
    println(singleNumberBruteForce(intArrayOf(4, 1, 2, 1, 2)))
    println("---")
    println(singleNumberHashSet(intArrayOf(4, 1, 2, 1, 2)))
    println("---")
    println(singleNumberXOR(intArrayOf(4, 1, 2, 1, 2)))
}

/** BRUTE FORCE: O(N²) — for each element, count occurrences */
fun singleNumberBruteForce(nums: IntArray): Int {
    for (num in nums) {
        var count = 0
        for (n in nums) if (n == num) count++
        if (count == 1) return num
    }
    return -1
}

/** BETTER: O(N) HashSet — add on first seen, remove on second, last one remaining */
fun singleNumberHashSet(nums: IntArray): Int {
    val seen = mutableSetOf<Int>()
    for (num in nums) {
        if (num in seen) seen.remove(num) else seen.add(num)
    }
    return seen.first()
}

/** OPTIMAL: O(N) O(1) XOR — a^a=0, a^0=a. Pairs cancel out. */
fun singleNumberXOR(nums: IntArray): Int {
    var result = 0
    for (num in nums) result = result xor num
    return result
}
