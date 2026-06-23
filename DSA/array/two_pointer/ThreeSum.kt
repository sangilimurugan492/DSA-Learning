package array.two_pointer

/**
 * https://leetcode.com/problems/3sum/
 *
 * Given an integer array nums, return all unique triplets [nums[i], nums[j], nums[k]]
 * such that nums[i] + nums[j] + nums[k] == 0.
 *
 * Example: nums = [-1,0,1,2,-1,-4] → Output: [[-1,-1,2],[-1,0,1]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 5 most asked)
 */

fun main() {
    println(threeSumBruteForce(intArrayOf(-1, 0, 1, 2, -1, -4)))
    println("---")
    println(threeSumTwoPointer(intArrayOf(-1, 0, 1, 2, -1, -4)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N³) — three nested loops
 * Space Complexity: O(N) — HashSet to avoid duplicates
 *
 * Try every triplet combination. Use Set to avoid duplicates.
 */
fun threeSumBruteForce(nums: IntArray): List<List<Int>> {
    val result = mutableSetOf<List<Int>>()
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            for (k in j + 1 until nums.size) {
                if (nums[i] + nums[j] + nums[k] == 0) {
                    result.add(listOf(nums[i], nums[j], nums[k]).sorted())
                }
            }
        }
    }
    return result.toList()
}

/**
 * OPTIMAL — Sort + Two Pointer
 * Time Complexity: O(N²) — sort O(N log N) + two-pointer scan O(N²)
 * Space Complexity: O(1) — ignoring output
 *
 * Sort array. Fix first element, then use two-pointer for remaining two.
 * Skip duplicates at each level to avoid duplicate triplets.
 */
fun threeSumTwoPointer(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    nums.sort()

    for (i in nums.indices) {
        if (i > 0 && nums[i] == nums[i - 1]) continue  // skip duplicate first element

        var left = i + 1
        var right = nums.size - 1

        while (left < right) {
            val sum = nums[i] + nums[left] + nums[right]
            when {
                sum < 0 -> left++
                sum > 0 -> right--
                else -> {
                    result.add(listOf(nums[i], nums[left], nums[right]))
                    left++
                    right--
                    while (left < right && nums[left] == nums[left - 1]) left++      // skip duplicates
                    while (left < right && nums[right] == nums[right + 1]) right--   // skip duplicates
                }
            }
        }
    }
    return result
}
