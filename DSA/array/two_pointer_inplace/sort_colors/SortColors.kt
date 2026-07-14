package array.two_pointer_inplace.sort_colors

/**
 * Sort Colors (Dutch National Flag) — LeetCode #75
 * https://leetcode.com/problems/sort-colors/
 *
 * Problem:
 * -------
 * Given an array with values 0 (red), 1 (white), 2 (blue), sort in-place so that
 * same colors are adjacent, in order 0, 1, 2. Must not use library sort.
 *
 * Example:  [2,0,2,1,1,0]  →  [0,0,1,1,2,2]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic three-pointer problem)
 *
 * Two approaches:
 * 1. Counting Sort: O(N) — count 0s, 1s, 2s, then overwrite
 * 2. Dutch National Flag: O(N) — three pointers, one pass
 */

fun main() {
    val nums = intArrayOf(2, 0, 2, 1, 1, 0)

    println("=== Method 1: Counting Sort ===")
    val copy1 = nums.copyOf()
    sortColorsCounting(copy1)
    println("Result: ${copy1.toList()}")

    println("\n=== Method 2: Dutch National Flag ===")
    val copy2 = nums.copyOf()
    sortColorsDutchFlag(copy2)
    println("Result: ${copy2.toList()}")

    println("\n=== Step-by-step trace ===")
    sortColorsTrace(nums.copyOf())
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: COUNTING SORT — O(N), two passes
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * COUNTING SORT — Count 0s, 1s, 2s. Overwrite array with correct counts.
 *
 * Core Idea:
 *   - Count occurrences of each color.
 *   - Overwrite: fill 0s, then 1s, then 2s.
 *
 * Time Complexity:  O(N) — two passes.
 * Space Complexity: O(1) — only 3 counters.
 */
fun sortColorsCounting(nums: IntArray) {
    var zeros = 0
    var ones = 0
    var twos = 0

    for (num in nums) {
        when (num) {
            0 -> zeros++
            1 -> ones++
            2 -> twos++
        }
    }

    var i = 0
    repeat(zeros) { nums[i++] = 0 }
    repeat(ones) { nums[i++] = 1 }
    repeat(twos) { nums[i++] = 2 }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: DUTCH NATIONAL FLAG — O(N), one pass
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DUTCH NATIONAL FLAG — Three pointers: low, mid, high.
 *
 * Core Idea:
 *   - low: boundary for 0s (everything before low is 0).
 *   - high: boundary for 2s (everything after high is 2).
 *   - mid: current element being examined.
 *   - nums[mid] == 0 → swap with low, advance both low and mid.
 *   - nums[mid] == 1 → just advance mid (1s are in the middle).
 *   - nums[mid] == 2 → swap with high, only advance high (swapped element needs checking).
 *
 * Key Insight:
 *   - [0..low) = all 0s, [low..mid) = all 1s, [mid to high] = unknown, (high..n) = all 2s.
 *   - When swapping 0 to low, the swapped-in element is 1 (since low..mid is all 1s) → advance mid.
 *   - When swapping 2 to high, the swapped-in element is unknown → don't advance mid.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — in-place.
 */
fun sortColorsDutchFlag(nums: IntArray) {
    var low = 0
    var mid = 0
    var high = nums.lastIndex

    while (mid <= high) {
        when (nums[mid]) {
            0 -> {
                swap(nums, low, mid)
                low++
                mid++
            }
            1 -> {
                mid++
            }
            2 -> {
                swap(nums, mid, high)
                high--  // Don't advance mid — swapped element is unknown.
            }
        }
    }
}

private fun swap(nums: IntArray, i: Int, j: Int) {
    val temp = nums[i]
    nums[i] = nums[j]
    nums[j] = temp
}

/**
 * Dutch National Flag with step-by-step trace.
 */
fun sortColorsTrace(nums: IntArray) {
    println("Input: ${nums.toList()}")
    var low = 0
    var mid = 0
    var high = nums.lastIndex

    while (mid <= high) {
        print("  low=$low, mid=$mid, high=$high, nums=${nums.toList()}")
        when (nums[mid]) {
            0 -> {
                println(" → nums[$mid]=0 → swap($low,$mid), low++, mid++")
                swap(nums, low, mid)
                low++
                mid++
            }
            1 -> {
                println(" → nums[$mid]=1 → mid++")
                mid++
            }
            2 -> {
                println(" → nums[$mid]=2 → swap($mid,$high), high--")
                swap(nums, mid, high)
                high--
            }
        }
    }
    println("  Result: ${nums.toList()}")
}
