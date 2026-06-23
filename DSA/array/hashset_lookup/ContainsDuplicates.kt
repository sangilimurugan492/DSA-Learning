package array.hashset_lookup

/**
 * https://leetcode.com/problems/contains-duplicate/
 *
 * Given an integer array nums, return true if any value appears at least twice.
 *
 * Example 1:
 * Input: nums = [1,2,3,1] → Output: true
 * Example 2:
 * Input: nums = [1,2,3,4] → Output: false
 *
 * FAANG Importance: ⭐⭐⭐ (Easy warm-up, but testsHashSet knowledge)
 */

fun main() {
    println(containsDuplicateBruteForce(intArrayOf(1, 2, 3, 1)))
    println(containsDuplicateBruteForce(intArrayOf(1, 2, 3, 4)))
    println("---")
    println(containsDuplicateSorted(intArrayOf(1, 2, 3, 1)))
    println(containsDuplicateSorted(intArrayOf(1, 2, 3, 4)))
    println("---")
    println(containsDuplicateHashSet(intArrayOf(1, 2, 3, 1)))
    println(containsDuplicateHashSet(intArrayOf(1, 2, 3, 4)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — check every pair
 * Space Complexity: O(1)
 *
 * For each element, check if it appears again later.
 * Simple but too slow for large inputs.
 */
fun containsDuplicateBruteForce(nums: IntArray): Boolean {
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            if (nums[i] == nums[j]) return true
        }
    }
    return false
}

/**
 * BETTER — Sort first
 * Time Complexity: O(N log N) — sort + single pass
 * Space Complexity: O(1) or O(N) depending on sort
 *
 * After sorting, duplicates are adjacent.
 */
fun containsDuplicateSorted(nums: IntArray): Boolean {
    nums.sort()
    for (i in 1 until nums.size) {
        if (nums[i] == nums[i - 1]) return true
    }
    return false
}

/**
 * OPTIMAL — HashSet
 * Time Complexity: O(N) — single pass with O(1) lookup
 * Space Complexity: O(N) — HashSet storage
 *
 * Add each element to HashSet. If already exists → duplicate found.
 */
fun containsDuplicateHashSet(nums: IntArray): Boolean {
    val seen = mutableSetOf<Int>()
    for (num in nums) {
        if (num in seen) return true
        seen.add(num)
    }
    return false
}
