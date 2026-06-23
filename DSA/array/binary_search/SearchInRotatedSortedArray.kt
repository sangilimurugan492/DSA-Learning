package array.binary_search

/**
 * https://leetcode.com/problems/search-in-rotated-sorted-array/
 *
 * There is an integer array nums sorted in ascending order (with distinct values).
 * The array was rotated at an unknown pivot. Given the array after rotation and an
 * integer target, return the index of target if it exists, or -1.
 *
 * Example 1:
 *
 * Input: nums = [4,5,6,7,0,1,2], target = 0
 * Output: 4
 *
 * Example 2:
 *
 * Input: nums = [4,5,6,7,0,1,2], target = 3
 * Output: -1
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta, Apple — must-know binary search)
 *
 * Key Insight: In a rotated sorted array, one half is always sorted. Find which half is sorted,
 * check if target lies in that half. If yes, search there; otherwise search the other half.
 */
fun main() {
    println(searchRotated(intArrayOf(4, 5, 6, 7, 0, 1, 2), 0))
    println(searchRotated(intArrayOf(4, 5, 6, 7, 0, 1, 2), 3))
    println(searchRotated(intArrayOf(1), 0))
    println(searchRotated(intArrayOf(1, 3), 3))
}

/**
 * Time Complexity O(log N)
 * Space Complexity O(1)
 *
 * Approach: Modified binary search
 *
 * At any mid, one half is always sorted:
 * - If nums[left] <= nums[mid]: left half is sorted
 *   - If target in [nums[left], nums[mid]] → search left half
 *   - Else → search right half
 * - Else: right half is sorted
 *   - If target in [nums[mid], nums[right]] → search right half
 *   - Else → search left half
 *
 * Trace for nums=[4,5,6,7,0,1,2], target=0:
 * left=0, right=6, mid=3 → nums[3]=7
 * nums[0]=4 <= 7 → left half sorted
 * 0 not in [4,7] → search right: left=4
 * left=4, right=6, mid=5 → nums[5]=1
 * nums[4]=0 <= 1 → left half sorted
 * 0 in [0,1] → search left: right=4
 * left=4, right=4, mid=4 → nums[4]=0 == target → return 4 ✅
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
