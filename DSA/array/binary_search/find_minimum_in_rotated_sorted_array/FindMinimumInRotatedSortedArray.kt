package array.binary_search.find_minimum_in_rotated_sorted_array

/**
 * Find Minimum in Rotated Sorted Array — LeetCode #153
 * https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/
 *
 * Problem:
 * -------
 * A sorted array is rotated at an unknown pivot. Find the minimum element in O(log N) time.
 *
 * Example:  [3,4,5,1,2] → 1
 *           [4,5,6,7,0,1,2] → 0
 *           [11,13,15,17] → 11 (not rotated)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Two approaches:
 * 1. Linear Scan: O(N) — scan entire array for minimum
 * 2. Binary Search: O(log N) — compare mid with right to find unsorted half
 */

fun main() {
    println("=== Method 1: Linear Scan ===")
    println("findMin([3,4,5,1,2]) = ${findMinLinear(intArrayOf(3, 4, 5, 1, 2))}")
    println("findMin([4,5,6,7,0,1,2]) = ${findMinLinear(intArrayOf(4, 5, 6, 7, 0, 1, 2))}")
    println("findMin([11,13,15,17]) = ${findMinLinear(intArrayOf(11, 13, 15, 17))}")

    println("\n=== Method 2: Binary Search ===")
    println("findMin([3,4,5,1,2]) = ${findMin(intArrayOf(3, 4, 5, 1, 2))}")
    println("findMin([4,5,6,7,0,1,2]) = ${findMin(intArrayOf(4, 5, 6, 7, 0, 1, 2))}")
    println("findMin([11,13,15,17]) = ${findMin(intArrayOf(11, 13, 15, 17))}")
    println("findMin([2,1]) = ${findMin(intArrayOf(2, 1))}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: LINEAR SCAN — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * LINEAR SCAN — Scan entire array, track minimum.
 *
 * Core Idea:
 *   - Iterate through every element.
 *   - Keep track of the smallest value seen.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — one variable.
 */
fun findMinLinear(nums: IntArray): Int {
    var min = nums[0]
    for (num in nums) {
        if (num < min) min = num
    }
    return min
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BINARY SEARCH — O(log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH — Compare mid with right to determine which half is unsorted.
 *
 * Core Idea:
 *   - In a rotated sorted array, the minimum is in the unsorted half.
 *   - If nums[mid] > nums[right] → left half is sorted → minimum is in right half.
 *   - If nums[mid] <= nums[right] → right half is sorted → minimum is in left half (including mid).
 *
 * Key Insight:
 *   - The minimum element is the only element smaller than its left neighbor.
 *   - By comparing mid with right, we know which side the rotation point (minimum) is on.
 *
 * Trace for [4,5,6,7,0,1,2]:
 *   left=0, right=6, mid=3 → nums[3]=7 > nums[6]=2 → left=4
 *   left=4, right=6, mid=5 → nums[5]=1 <= nums[6]=2 → right=5
 *   left=4, right=5, mid=4 → nums[4]=0 <= nums[5]=1 → right=4
 *   left=4, right=4 → return nums[4]=0 ✅
 *
 * Time Complexity:  O(log N) — binary search.
 * Space Complexity: O(1) — two pointers.
 */
fun findMin(nums: IntArray): Int {
    var left = 0
    var right = nums.size - 1

    while (left < right) {
        val mid = left + (right - left) / 2

        if (nums[mid] > nums[right]) {
            // Left half is sorted, minimum is in right half
            left = mid + 1
        } else {
            // Right half is sorted, minimum is at mid or in left half
            right = mid
        }
    }

    return nums[left]
}
