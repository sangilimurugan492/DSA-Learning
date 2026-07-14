package array.binary_search.search_in_rotated_sorted_array

/**
 * Search in Rotated Sorted Array — LeetCode #33
 * https://leetcode.com/problems/search-in-rotated-sorted-array/
 *
 * Problem:
 * -------
 * A sorted array (distinct values) is rotated at an unknown pivot.
 * Given target, return its index, or -1. Must run in O(log N) time.
 *
 * Example:  nums=[4,5,6,7,0,1,2], target=0 → 4
 *           nums=[4,5,6,7,0,1,2], target=3 → -1
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta, Apple — must-know)
 *
 * Two approaches:
 * 1. Linear Scan: O(N) — scan entire array for target
 * 2. Binary Search: O(log N) — find sorted half, check if target is in it
 */

fun main() {
    println("=== Method 1: Linear Scan ===")
    println("search([4,5,6,7,0,1,2], 0) = ${searchLinear(intArrayOf(4, 5, 6, 7, 0, 1, 2), 0)}")
    println("search([4,5,6,7,0,1,2], 3) = ${searchLinear(intArrayOf(4, 5, 6, 7, 0, 1, 2), 3)}")

    println("\n=== Method 2: Binary Search ===")
    println("search([4,5,6,7,0,1,2], 0) = ${searchRotated(intArrayOf(4, 5, 6, 7, 0, 1, 2), 0)}")
    println("search([4,5,6,7,0,1,2], 3) = ${searchRotated(intArrayOf(4, 5, 6, 7, 0, 1, 2), 3)}")
    println("search([1], 0) = ${searchRotated(intArrayOf(1), 0)}")
    println("search([1,3], 3) = ${searchRotated(intArrayOf(1, 3), 3)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: LINEAR SCAN — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * LINEAR SCAN — Iterate through array, return index when target found.
 *
 * Core Idea:
 *   - Check each element one by one.
 *   - Return index if found, -1 otherwise.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — no extra space.
 */
fun searchLinear(nums: IntArray, target: Int): Int {
    for (i in nums.indices) {
        if (nums[i] == target) return i
    }
    return -1
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BINARY SEARCH — O(log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH — At any mid, one half is always sorted. Find which, check target.
 *
 * Core Idea:
 *   - At any mid point, one half (left or right) is always sorted.
 *   - If left half is sorted (nums[left] <= nums[mid]):
 *     - If target in [nums[left], nums[mid]) → search left half.
 *     - Else → search right half.
 *   - If right half is sorted:
 *     - If target in (nums[mid], nums[right]] → search right half.
 *     - Else → search left half.
 *
 * Key Insight:
 *   - In a rotated sorted array, one half is ALWAYS sorted.
 *   - By identifying the sorted half, we can check if target lies in it.
 *   - This eliminates half the search space each iteration → O(log N).
 *
 * Trace for nums=[4,5,6,7,0,1,2], target=0:
 *   left=0, right=6, mid=3 → nums[3]=7
 *   nums[0]=4 <= 7 → left half sorted
 *   0 not in [4,7) → search right: left=4
 *   left=4, right=6, mid=5 → nums[5]=1
 *   nums[4]=0 <= 1 → left half sorted
 *   0 in [0,1) → search left: right=4
 *   left=4, right=4, mid=4 → nums[4]=0 == target → return 4 ✅
 *
 * Time Complexity:  O(log N) — binary search.
 * Space Complexity: O(1) — two pointers.
 */
fun searchRotated(nums: IntArray, target: Int): Int {
    var left = 0
    var right = nums.size - 1

    while (left <= right) {
        val mid = left + (right - left) / 2

        if (nums[mid] == target) return mid

        // Left half is sorted
        if (nums[left] <= nums[mid]) {
            if (target >= nums[left] && target < nums[mid]) {
                right = mid - 1  // Target in left half
            } else {
                left = mid + 1   // Target in right half
            }
        }
        // Right half is sorted
        else {
            if (target > nums[mid] && target <= nums[right]) {
                left = mid + 1   // Target in right half
            } else {
                right = mid - 1  // Target in left half
            }
        }
    }

    return -1
}
