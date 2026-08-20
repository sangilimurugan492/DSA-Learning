package array.binary_search.search_insert_position

/**
 * Search Insert Position — LeetCode #35
 * https://leetcode.com/problems/search-insert-position/
 *
 * Problem:
 * -------
 * Given a sorted array of distinct integers and a target value,
 * return the index of target if found, or the index where it would be inserted
 * to maintain sorted order. Must run in O(log N) time.
 *
 * Example:  nums=[1,3,5,6], target=5 → 2  (found at index 2)
 *           nums=[1,3,5,6], target=2 → 1  (would insert at index 1)
 *           nums=[1,3,5,6], target=7 → 4  (would insert at end)
 *           nums=[1,3,5,6], target=0 → 0  (would insert at start)
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Classic binary search — asked at Amazon, Apple)
 *
 * Two approaches:
 * 1. Linear Scan: O(N) — scan until target or insertion point found
 * 2. Binary Search: O(log N) — standard binary search, return left pointer
 */

fun main() {
    // ─────────────────────────────────────────────────────────────
    // HUGE 10-ELEMENT ARRAY for step-by-step walkthrough
    // Array: [1, 3, 5, 7, 9, 11, 13, 15, 17, 19]
    // Target: 11 → found at index 5
    // Target: 8  → would insert at index 4
    // ─────────────────────────────────────────────────────────────
    val hugeArray = intArrayOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19)

    println("=== Huge 10-Element Array Walkthrough ===")
    println("Array: ${hugeArray.toList()}\n")

    println("=== Method 1: Linear Scan ===")
    println("searchInsert(hugeArray, 11) = ${searchInsertLinear(hugeArray, 11)}")
    println("searchInsert(hugeArray, 8)  = ${searchInsertLinear(hugeArray, 8)}")

    println("\n=== Method 2: Binary Search (step-by-step) ===")
    println("searchInsert(hugeArray, 11) = ${searchInsertVerbose(hugeArray, 11)}")
    println()
    println("searchInsert(hugeArray, 8)  = ${searchInsertVerbose(hugeArray, 8)}")

    // ── Additional small test cases ──
    println("\n=== Additional Test Cases ===")
    println("searchInsert([1,3,5,6], 5) = ${searchInsert(intArrayOf(1, 3, 5, 6), 5)}")
    println("searchInsert([1,3,5,6], 2) = ${searchInsert(intArrayOf(1, 3, 5, 6), 2)}")
    println("searchInsert([1,3,5,6], 7) = ${searchInsert(intArrayOf(1, 3, 5, 6), 7)}")
    println("searchInsert([1,3,5,6], 0) = ${searchInsert(intArrayOf(1, 3, 5, 6), 0)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: LINEAR SCAN — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * LINEAR SCAN — Iterate until we find target or a value greater than it.
 *
 * Core Idea:
 *   - Walk through the array left to right.
 *   - If nums[i] >= target → return i (found or insertion point).
 *   - If we reach the end → return n (insert at end).
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — no extra space.
 */
fun searchInsertLinear(nums: IntArray, target: Int): Int {
    for (i in nums.indices) {
        if (nums[i] >= target) return i
    }
    return nums.size
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BINARY SEARCH — O(log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH — Standard binary search; when loop ends, left is the insertion point.
 *
 * Core Idea:
 *   - Use standard binary search with left and right pointers.
 *   - If nums[mid] == target → return mid (found).
 *   - If nums[mid] < target → search right half (left = mid + 1).
 *   - If nums[mid] > target → search left half (right = mid - 1).
 *   - When loop ends (left > right), left is the insertion index.
 *
 * Key Insight:
 *   - When the loop exits without finding target, `left` points to the first element
 *     greater than target — exactly where target should be inserted.
 *   - This works because left only moves past elements smaller than target,
 *     and right only moves past elements greater than target.
 *
 * Trace for [1,3,5,7,9,11,13,15,17,19], target=11:
 *   left=0, right=9, mid=4 → nums[4]=9 < 11 → left=5
 *   left=5, right=9, mid=7 → nums[7]=15 > 11 → right=6
 *   left=5, right=6, mid=5 → nums[5]=11 == 11 → return 5 ✅
 *
 * Trace for [1,3,5,7,9,11,13,15,17,19], target=8:
 *   left=0, right=9, mid=4 → nums[4]=9 > 8 → right=3
 *   left=0, right=3, mid=1 → nums[1]=3 < 8 → left=2
 *   left=2, right=3, mid=2 → nums[2]=5 < 8 → left=3
 *   left=3, right=3, mid=3 → nums[3]=7 < 8 → left=4
 *   left=4, right=3 → left > right → return left=4 ✅ (insert at index 4)
 *
 * Time Complexity:  O(log N) — binary search.
 * Space Complexity: O(1) — two pointers.
 */
fun searchInsert(nums: IntArray, target: Int): Int {
    var left = 0
    var right = nums.size - 1

    while (left <= right) {
        val mid = left + (right - left) / 2

        if (nums[mid] == target) return mid
        if (nums[mid] < target) {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return left
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2b: BINARY SEARCH WITH VERBOSE STEP-BY-STEP OUTPUT — O(log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH (VERBOSE) — Same logic as searchInsert but prints every step.
 *
 * ── Step-by-step explanation for the 10-element array [1,3,5,7,9,11,13,15,17,19] ──
 *
 * TARGET = 11 (found):
 *   STEP 1: left=0, right=9, mid=4 → nums[4]=9 < 11 → search right → left=5
 *   STEP 2: left=5, right=9, mid=7 → nums[7]=15 > 11 → search left → right=6
 *   STEP 3: left=5, right=6, mid=5 → nums[5]=11 == 11 → FOUND! return 5 ✅
 *
 * TARGET = 8 (not found, insert at 4):
 *   STEP 1: left=0, right=9, mid=4 → nums[4]=9 > 8 → search left → right=3
 *   STEP 2: left=0, right=3, mid=1 → nums[1]=3 < 8 → search right → left=2
 *   STEP 3: left=2, right=3, mid=2 → nums[2]=5 < 8 → search right → left=3
 *   STEP 4: left=3, right=3, mid=3 → nums[3]=7 < 8 → search right → left=4
 *   STEP 5: left=4, right=3 → left > right → NOT FOUND → return left=4 ✅
 *
 * Time Complexity:  O(log N) — binary search.
 * Space Complexity: O(1) — two pointers.
 */
fun searchInsertVerbose(nums: IntArray, target: Int): Int {
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

        if (nums[mid] < target) {
            println("         ${nums[mid]} < $target → search RIGHT → left = ${mid + 1}")
            left = mid + 1
        } else {
            println("         ${nums[mid]} > $target → search LEFT → right = ${mid - 1}")
            right = mid - 1
        }
        println()
        step++
    }

    println("  ──────────────────────────────────────────────")
    println("  left=$left > right=$right → NOT FOUND")
    println("  ✅ Insert position = left = $left")
    return left
}
