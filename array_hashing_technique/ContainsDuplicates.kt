package array_hashing_technique

/**
 * https://leetcode.com/problems/contains-duplicate/description/4
 * Given an integer array nums, return true if any value appears at least twice in the array, and return false if every element is distinct.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [1,2,3,1]
 *
 * Output: true
 *
 * Explanation:
 *
 * The element 1 occurs at the indices 0 and 3.
 *
 * Example 2:
 *
 * Input: nums = [1,2,3,4]
 *
 * Output: false
 *
 * Explanation:
 *
 * All elements are distinct.
 *
 * Example 3:
 *
 * Input: nums = [1,1,1,3,3,4,3,2,4,2]
 *
 * Output: true
 */

fun main() {
    println(containsDuplicateBF(intArrayOf(1,1,1,3,3,4,3,2,4,2)))
    println(containsDuplicateOP(intArrayOf(1,1,1,3,3,4,3,2,4,2)))
}

/**
 * Time Complexity: $O(n^2)$ — For every element, we scan the rest of the array.
 * Space Complexity: $O(1)$ — No extra memory used.
 * Leet Code Running time : 50 ms
 */
fun containsDuplicateBF(nums: IntArray): Boolean {
    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            if (nums[i] == nums[j]) {
                return true
            }
        }
    }
    return false
}

/**
 * Time Complexity: $O(N)$ — For every element, we scan the rest of the array.
 * Space Complexity: $O(N)$ — Extra memory used.
 * Leet Code Running time : 11 ms
 */
fun containsDuplicateOP(nums: IntArray): Boolean {
    val set = HashSet<Int>(nums.size * 2)
    for (n in nums) {
        if (!set.add(n)) {
            return true
        }
    }
    return false
}