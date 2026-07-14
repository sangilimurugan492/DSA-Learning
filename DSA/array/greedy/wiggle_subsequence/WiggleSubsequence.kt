package array.greedy.wiggle_subsequence

/**
 * Wiggle Subsequence — LeetCode #376
 * https://leetcode.com/problems/wiggle-subsequence/
 *
 * Problem:
 * -------
 * A wiggle sequence alternates between increasing and decreasing.
 * Return the length of the longest wiggle subsequence.
 *
 * Example:  [1,7,4,9,2,5] → 6  (1<7>4<9>2<5)
 *           [1,17,5,10,13,15,10,5,16,8] → 7
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Amazon)
 *
 * Two approaches:
 * 1. DP: O(N²) — dp[i] = longest wiggle ending at i
 * 2. Greedy: O(N) — count peaks and valleys (direction changes)
 */

fun main() {
    println("=== Method 1: DP ===")
    println("wiggle([1,7,4,9,2,5]) = ${wiggleDP(intArrayOf(1, 7, 4, 9, 2, 5))}")
    println("wiggle([1,17,5,10,13,15,10,5,16,8]) = ${wiggleDP(intArrayOf(1, 17, 5, 10, 13, 15, 10, 5, 16, 8))}")

    println("\n=== Method 2: Greedy ===")
    println("wiggle([1,7,4,9,2,5]) = ${wiggleMaxLength(intArrayOf(1, 7, 4, 9, 2, 5))}")
    println("wiggle([1,17,5,10,13,15,10,5,16,8]) = ${wiggleMaxLength(intArrayOf(1, 17, 5, 10, 13, 15, 10, 5, 16, 8))}")
    println("wiggle([1,2,3,4,5]) = ${wiggleMaxLength(intArrayOf(1, 2, 3, 4, 5))}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: DP — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DP — up[i] = longest wiggle ending with UP at i. down[i] = longest ending with DOWN.
 *
 * Time Complexity:  O(N²).
 * Space Complexity: O(N).
 */
fun wiggleDP(nums: IntArray): Int {
    if (nums.size < 2) return nums.size
    val up = IntArray(nums.size) { 1 }
    val down = IntArray(nums.size) { 1 }

    for (i in 1 until nums.size) {
        for (j in 0 until i) {
            if (nums[i] > nums[j]) up[i] = maxOf(up[i], down[j] + 1)
            if (nums[i] < nums[j]) down[i] = maxOf(down[i], up[j] + 1)
        }
    }
    return maxOf(up.max(), down.max())
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: GREEDY — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY — Track up and down lengths. When increasing: up = down + 1. When decreasing: down = up + 1.
 *
 * Core Idea:
 *   - Count the number of direction changes (peaks and valleys).
 *   - Every time the diff changes sign (positive ↔ negative), we have a new wiggle.
 *
 * Key Insight:
 *   - We only care about direction changes, not the actual values.
 *   - Skip consecutive same-direction moves (they don't add to wiggle length).
 *
 * Time Complexity:  O(N).
 * Space Complexity: O(1).
 */
fun wiggleMaxLength(nums: IntArray): Int {
    if (nums.size < 2) return nums.size

    var up = 1   // Length of wiggle ending with UP
    var down = 1 // Length of wiggle ending with DOWN

    for (i in 1 until nums.size) {
        when {
            nums[i] > nums[i - 1] -> up = down + 1
            nums[i] < nums[i - 1] -> down = up + 1
        }
    }

    return maxOf(up, down)
}
