package array.voting_floyd

/**
 * https://leetcode.com/problems/find-the-duplicate-number/
 *
 * Given an array of n+1 integers where each is in [1, n], find the duplicate.
 * Must not modify the array, O(1) extra space.
 *
 * Example: nums = [1,3,4,2,2] → Output: 2
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(findDuplicateBruteForce(intArrayOf(1, 3, 4, 2, 2)))
    println("---")
    println(findDuplicateSort(intArrayOf(1, 3, 4, 2, 2)))
    println("---")
    println(findDuplicateFloyd(intArrayOf(1, 3, 4, 2, 2)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — compare every pair
 * Space Complexity: O(1)
 */
fun findDuplicateBruteForce(nums: IntArray): Int {
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            if (nums[i] == nums[j]) return nums[i]
        }
    }
    return -1
}

/**
 * BETTER — Sort
 * Time Complexity: O(N log N)
 * Space Complexity: O(1) or O(N)
 * Note: Modifies array (not allowed by problem constraints)
 */
fun findDuplicateSort(nums: IntArray): Int {
    nums.sort()
    for (i in 1 until nums.size) {
        if (nums[i] == nums[i - 1]) return nums[i]
    }
    return -1
}

/**
 * OPTIMAL — Floyd's Cycle Detection
 * Time Complexity: O(N)
 * Space Complexity: O(1)
 *
 * Treat array as linked list: index → value → index...
 * Duplicate creates a cycle. Phase 1: find meeting point.
 * Phase 2: find cycle entrance (the duplicate).
 */
fun findDuplicateFloyd(nums: IntArray): Int {
    var slow = nums[0]
    var fast = nums[0]

    // Phase 1: Find meeting point
    do {
        slow = nums[slow]
        fast = nums[nums[fast]]
    } while (slow != fast)

    // Phase 2: Find cycle entrance
    slow = nums[0]
    while (slow != fast) {
        slow = nums[slow]
        fast = nums[fast]
    }
    return slow
}
