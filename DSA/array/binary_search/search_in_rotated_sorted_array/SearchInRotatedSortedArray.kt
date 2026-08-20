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
    // ─────────────────────────────────────────────────────────────
    // HUGE 10-ELEMENT ARRAY for step-by-step walkthrough
    // Original sorted: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    // Rotated at pivot 6 → [7, 8, 9, 10, 1, 2, 3, 4, 5, 6]
    // Target: 3 → found at index 6
    // Target: 5 → found at index 8
    // Target: 11 → not found (-1)
    // ─────────────────────────────────────────────────────────────
    val hugeArray = intArrayOf(7, 8, 9, 10, 1, 2, 3, 4, 5, 6)

    println("=== Huge 10-Element Array Walkthrough ===")
    println("Array: ${hugeArray.toList()}")
    println("Original sorted: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]")
    println("Rotated at pivot 6\n")

    println("=== Method 1: Linear Scan ===")
    println("search(hugeArray, 3) = ${searchLinear(hugeArray, 3)}")
    println("search(hugeArray, 11) = ${searchLinear(hugeArray, 11)}")

    println("\n=== Method 2: Binary Search (step-by-step) ===")
    println("search(hugeArray, 3) = ${searchRotatedVerbose(hugeArray, 3)}")
    println()
    println("search(hugeArray, 11) = ${searchRotatedVerbose(hugeArray, 11)}")

    // ── Additional small test cases ──
    println("\n=== Additional Test Cases ===")
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
// METHOD 2b: BINARY SEARCH WITH VERBOSE STEP-BY-STEP OUTPUT — O(log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH (VERBOSE) — Same logic as searchRotated but prints every step.
 *
 * ── Step-by-step explanation for the 10-element array [7,8,9,10,1,2,3,4,5,6] ──
 *
 * TARGET = 3 (found at index 6):
 *   STEP 1: left=0, right=9, mid=4 → nums[4]=1
 *           nums[0]=7 <= nums[4]=1? No → right half sorted
 *           3 in (1,6]? Yes → search right → left=5
 *   STEP 2: left=5, right=9, mid=7 → nums[7]=4
 *           nums[5]=2 <= nums[7]=4? Yes → left half sorted
 *           3 in [2,4)? Yes → search left → right=6
 *   STEP 3: left=5, right=6, mid=5 → nums[5]=2
 *           nums[5]=2 <= nums[5]=2? Yes → left half sorted
 *           3 in [2,2)? No → search right → left=6
 *   STEP 4: left=6, right=6, mid=6 → nums[6]=3 == 3 → FOUND! return 6 ✅
 *
 * TARGET = 11 (not found):
 *   STEP 1: left=0, right=9, mid=4 → nums[4]=1
 *           nums[0]=7 <= 1? No → right half sorted
 *           11 in (1,6]? No → search left → right=3
 *   STEP 2: left=0, right=3, mid=1 → nums[1]=8
 *           nums[0]=7 <= 8? Yes → left half sorted
 *           11 in [7,8)? No → search right → left=2
 *   STEP 3: left=2, right=3, mid=2 → nums[2]=9
 *           nums[2]=9 <= 9? Yes → left half sorted
 *           11 in [9,9)? No → search right → left=3
 *   STEP 4: left=3, right=3, mid=3 → nums[3]=10
 *           nums[3]=10 <= 10? Yes → left half sorted
 *           11 in [10,10)? No → search right → left=4
 *   STEP 5: left=4, right=3 → left > right → NOT FOUND → return -1 ✅
 *
 * Time Complexity:  O(log N) — binary search.
 * Space Complexity: O(1) — two pointers.
 */
fun searchRotatedVerbose(nums: IntArray, target: Int): Int {
    var left = 0
    var right = nums.size - 1
    var step = 1

    println("  Array: ${nums.toList()}")
    println("  Target: $target")
    println("  ──────────────────────────────────────────────")

    while (left <= right) {
        val mid = left + (right - left) / 2

        println("  STEP $step: left=$left, right=$right, mid=$mid → nums[$mid]=${nums[mid]}")

        if (nums[mid] == target) {
            println("         ${nums[mid]} == $target → FOUND! return $mid ✅")
            return mid
        }

        if (nums[left] <= nums[mid]) {
            // Left half is sorted
            println("         nums[$left]=${nums[left]} <= nums[$mid]=${nums[mid]} → LEFT half is sorted")
            if (target >= nums[left] && target < nums[mid]) {
                println("         $target in [${nums[left]}, ${nums[mid]})? Yes → search LEFT → right = ${mid - 1}")
                right = mid - 1
            } else {
                println("         $target in [${nums[left]}, ${nums[mid]})? No → search RIGHT → left = ${mid + 1}")
                left = mid + 1
            }
        } else {
            // Right half is sorted
            println("         nums[$left]=${nums[left]} > nums[$mid]=${nums[mid]} → RIGHT half is sorted")
            if (target > nums[mid] && target <= nums[right]) {
                println("         $target in (${nums[mid]}, ${nums[right]}]? Yes → search RIGHT → left = ${mid + 1}")
                left = mid + 1
            } else {
                println("         $target in (${nums[mid]}, ${nums[right]}]? No → search LEFT → right = ${mid - 1}")
                right = mid - 1
            }
        }
        println()
        step++
    }

    println("  ──────────────────────────────────────────────")
    println("  left=$left > right=$right → NOT FOUND")
    println("  ✅ Return -1")
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
