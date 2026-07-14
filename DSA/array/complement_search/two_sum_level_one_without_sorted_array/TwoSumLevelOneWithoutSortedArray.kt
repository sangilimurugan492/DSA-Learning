package array.complement_search.two_sum_level_one_without_sorted_array

/**
 * https://leetcode.com/problems/two-sum/
 *
 * Given an array of integers nums and an integer target, return indices of the two
 * numbers that add up to target. Each input has exactly one solution.
 *
 * Example 1:
 * Input: nums = [2,7,11,15], target = 9 → Output: [0,1]
 * Example 2:
 * Input: nums = [3,2,4], target = 6 → Output: [1,2]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE most asked FAANG question)
 */

fun main() {
    println(twoSumBruteForce(intArrayOf(2, 7, 11, 15), 9).toList())
    println(twoSumBruteForce(intArrayOf(3, 2, 4), 6).toList())
    println("---")
    println(twoSumHashMap(intArrayOf(2, 7, 11, 15), 9).toList())
    println(twoSumHashMap(intArrayOf(3, 2, 4), 6).toList())
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — check every pair
 * Space Complexity: O(1)
 *
 * Try every combination of two elements.
 * Simple but too slow for large inputs (N > 10⁴).
 */
fun twoSumBruteForce(nums: IntArray, target: Int): IntArray {
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            if (nums[i] + nums[j] == target) {
                return intArrayOf(i, j)
            }
        }
    }
    return intArrayOf()
}

/**
 * OPTIMAL — HashMap (One Pass)
 * Time Complexity: O(N) — single pass with O(1) lookup
 * Space Complexity: O(N) — HashMap storage
 *
 * For each number, check if (target - num) already seen.
 * If yes → found the pair. If no → store current number.
 *
 * Trace for [2,7,11,15], target=9:
 * i=0: num=2, complement=7, seen={} → not found, store 2→0
 * i=1: num=7, complement=2, seen={2:0} → FOUND! return [0,1]
 */
fun twoSumHashMap(nums: IntArray, target: Int): IntArray {
    val seen = hashMapOf<Int, Int>()  // value → index
    for (i in nums.indices) {
        val complement = target - nums[i]
        if (complement in seen) {
            return intArrayOf(seen[complement]!!, i)
        }
        seen[nums[i]] = i
    }
    return intArrayOf()
}
