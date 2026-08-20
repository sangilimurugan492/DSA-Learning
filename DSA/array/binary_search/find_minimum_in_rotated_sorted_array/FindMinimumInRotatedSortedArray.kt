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
    // ─────────────────────────────────────────────────────────────
    // HUGE 10-ELEMENT ARRAY for step-by-step walkthrough
    // Original sorted: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    // Rotated at pivot 5 → [6, 7, 8, 9, 10, 1, 2, 3, 4, 5]
    // Minimum element = 1 (at index 5)
    // ─────────────────────────────────────────────────────────────
    val hugeArray = intArrayOf(6, 7, 8, 9, 10, 1, 2, 3, 4, 5)

    println("=== Huge 10-Element Array Walkthrough ===")
    println("Array: ${hugeArray.toList()}")
    println("Expected minimum: 1 (at index 5)\n")

    println("=== Method 1: Linear Scan ===")
    println("findMin(hugeArray) = ${findMinLinear(hugeArray)}")

    println("\n=== Method 2: Binary Search (step-by-step) ===")
    println("findMin(hugeArray) = ${findMinVerbose(hugeArray)}")

    // ── Additional small test cases ──
    println("\n=== Additional Test Cases ===")
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

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2b: BINARY SEARCH WITH VERBOSE STEP-BY-STEP OUTPUT — O(log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH (VERBOSE) — Same logic as findMin but prints every step.
 *
 * This function is identical to findMin() but adds println statements at each
 * iteration so you can see exactly how the binary search narrows down the range.
 *
 * ── Step-by-step explanation for the 10-element array [6,7,8,9,10,1,2,3,4,5] ──
 *
 * STEP 1:  left=0, right=9
 *          mid = 0 + (9-0)/2 = 4 → nums[4] = 10
 *          nums[mid]=10 > nums[right]=5 → left half [6,7,8,9,10] is sorted
 *          → minimum must be in right half → left = mid+1 = 5
 *
 * STEP 2:  left=5, right=9
 *          mid = 5 + (9-5)/2 = 7 → nums[7] = 3
 *          nums[mid]=3 <= nums[right]=5 → right half [3,4,5] is sorted
 *          → minimum is at mid or in left half → right = mid = 7
 *
 * STEP 3:  left=5, right=7
 *          mid = 5 + (7-5)/2 = 6 → nums[6] = 2
 *          nums[mid]=2 <= nums[right]=3 → right half [2,3] is sorted
 *          → minimum is at mid or in left half → right = mid = 6
 *
 * STEP 4:  left=5, right=6
 *          mid = 5 + (6-5)/2 = 5 → nums[5] = 1
 *          nums[mid]=1 <= nums[right]=2 → right half [1,2] is sorted
 *          → minimum is at mid or in left half → right = mid = 5
 *
 * STEP 5:  left=5, right=5 → left == right → loop ends
 *          return nums[5] = 1 ✅
 *
 * Only 4 iterations for a 10-element array — that's O(log N) in action!
 *
 * Time Complexity:  O(log N) — binary search.
 * Space Complexity: O(1) — two pointers.
 */
fun findMinVerbose(nums: IntArray): Int {
    var left = 0
    var right = nums.size - 1
    var step = 1

    println("  Array: ${nums.toList()}")
    println("  ──────────────────────────────────────────────")

    while (left < right) {
        val mid = left + (right - left) / 2

        println("  STEP $step: left=$left, right=$right, mid=$mid")
        println("         nums[mid]=${nums[mid]}, nums[right]=${nums[right]}")

        if (nums[mid] > nums[right]) {
            // Left half is sorted, minimum is in right half
            println("         ${nums[mid]} > ${nums[right]} → left half sorted → min in RIGHT half")
            println("         → left = mid + 1 = ${mid + 1}")
            left = mid + 1
        } else {
            // Right half is sorted, minimum is at mid or in left half
            println("         ${nums[mid]} <= ${nums[right]} → right half sorted → min at mid or LEFT half")
            println("         → right = mid = $mid")
            right = mid
        }
        println()
        step++
    }

    println("  ──────────────────────────────────────────────")
    println("  left == right == $left → loop ends")
    println("  ✅ Minimum = nums[$left] = ${nums[left]}")
    return nums[left]
}

