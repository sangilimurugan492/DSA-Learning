package array.binary_search

/**
 * https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/
 *
 * Suppose an array sorted in ascending order is rotated at an unknown pivot.
 * Find the minimum element. You must write an algorithm that runs in O(log n) time.
 *
 * Example 1:
 *
 * Input: nums = [3,4,5,1,2]
 * Output: 1
 *
 * Example 2:
 *
 * Input: nums = [4,5,6,7,0,1,2]
 * Output: 0
 *
 * Example 3:
 *
 * Input: nums = [11,13,15,17]
 * Output: 11
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Key Insight: In a rotated sorted array, the minimum is in the unsorted half.
 * If nums[mid] > nums[right], the left half is sorted → minimum is in right half.
 * If nums[mid] <= nums[right], the right half is sorted → minimum is in left half (including mid).
 */
fun main() {

    println("Minimum Value")
    println(findMin(intArrayOf(3, 4, 5, 1, 2)))
    println(findMin(intArrayOf(3, 4, 5, 1, 2)))
    println(findMin(intArrayOf(4, 5, 6, 7, 0, 1, 2)))
    println(findMin(intArrayOf(11, 13, 15, 17)))
    println(findMin(intArrayOf(2, 1)))

    println("\nMax Value")

    println(findMax(intArrayOf(3, 4, 5, 1, 2)))
    println(findMax(intArrayOf(3, 4, 5, 1, 2)))
    println(findMax(intArrayOf(4, 5, 6, 7, 0, 1, 2)))
    println(findMax(intArrayOf(11, 13, 15, 17)))
    println(findMax(intArrayOf(2, 1)))
}

/**
 * Time Complexity O(log N)
 * Space Complexity O(1)
 *
 * Approach: Binary search comparing mid with right
 *
 * If nums[mid] > nums[right] → minimum is to the right of mid (left half is sorted)
 * If nums[mid] <= nums[right] → minimum is at mid or to the left (right half is sorted)
 *
 * Trace for [4,5,6,7,0,1,2]:
 * left=0, right=6, mid=3 → nums[3]=7 > nums[6]=2 → left=4
 * left=4, right=6, mid=5 → nums[5]=1 <= nums[6]=2 → right=5
 * left=4, right=5, mid=4 → nums[4]=0 <= nums[5]=1 → right=4
 * left=4, right=4 → return nums[4]=0 ✅
 */
fun findMin(nums: IntArray): Int {
    var left = 0
    var right = nums.size - 1

    while (left < right) {
        val mid = left + (right - left) / 2

        if (nums[mid] > nums[right]) {
            left = mid + 1  // Min is in right half
        } else {
            right = mid     // Min is at mid or left half
        }
    }

    return nums[left]
}

fun findMax(nums: IntArray): Int {
    var left = 0
    var right = nums.size - 1

    while (left < right) {
        val mid = left + (right - left) / 2

        if (nums[mid] < nums[right]) {
            left = mid + 1  // Min is in right half
        } else {
            right = mid     // Min is at mid or left half
        }
    }

    return nums[left]
}
