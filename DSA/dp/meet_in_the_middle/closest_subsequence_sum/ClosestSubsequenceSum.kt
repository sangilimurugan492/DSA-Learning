package dp.meet_in_the_middle.closest_subsequence_sum

/**
 * Closest Subsequence Sum — LeetCode #1755
 * https://leetcode.com/problems/closest-subsequence-sum/
 *
 * Problem:
 * -------
 * Choose a subsequence of nums whose sum is closest to `goal`.
 * Return the minimum possible |sum - goal|.
 *
 * Example:  nums = [5,-7,3,5], goal = 6   → 0  (subsequence [5, -7, 3, 5] sums to 6)
 *           nums = [7,-9,15,-2], goal = -5 → 1  (subsequence [-9, 3]... best is diff 1)
 *           nums = [1,2,3], goal = -7     → 7  (empty subsequence sums to 0)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Canonical Meet in the Middle problem)
 *
 * Key Insight:
 * ------------
 * n ≤ 40 → 2^40 subsets is too many, but 2^20 ≈ 10^6 is fine.
 * Split nums into two halves. Every subset sum = (left-half sum) + (right-half sum).
 * Enumerate all subset sums of each half, sort the right side, and for each left
 * sum binary-search the right side for the value closest to (goal - leftSum).
 *
 * Two approaches:
 * 1. Brute force: O(2^N) — infeasible for N = 40
 * 2. Meet in the middle: O(2^(N/2) · N)
 */

fun main() {
    val tests = listOf(
        intArrayOf(5, -7, 3, 5) to 6,      // 0
        intArrayOf(7, -9, 15, -2) to -5,   // 1
        intArrayOf(1, 2, 3) to -7           // 7
    )

    for ((nums, goal) in tests) {
        println("nums = ${nums.toList()}, goal = $goal")
        println("  Brute force          : ${minAbsDifferenceBrute(nums, goal)}")
        println("  Meet in the middle   : ${minAbsDifference(nums, goal)}")
        println()
    }

    println("=== Step-by-step trace (nums = [5,-7,3,5], goal = 6) ===")
    minAbsDifferenceTrace(intArrayOf(5, -7, 3, 5), 6)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: Brute Force — enumerate all 2^N subset sums
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Brute force — try every subset via bitmask.
 *
 * Core Idea:
 *   - For each of the 2^N masks, compute the subset sum and track the minimum
 *     |sum - goal|.
 *
 * Key Insight:
 *   - Fine for n ≤ ~25; 2^40 ≈ 10^12 is impossible.
 *
 * Time Complexity:  O(2^N × N)
 * Space Complexity: O(1)
 */
fun minAbsDifferenceBrute(nums: IntArray, goal: Int): Int {
    val n = nums.size
    var best = Int.MAX_VALUE.toLong()

    for (mask in 0 until (1 shl n)) {
        var sum = 0L
        for (i in 0 until n) {
            if (mask and (1 shl i) != 0) sum += nums[i]
        }
        best = minOf(best, kotlin.math.abs(sum - goal))
    }
    return best.toInt()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: Meet in the Middle + Binary Search — optimal
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Meet in the middle — split, enumerate, sort, binary search.
 *
 * Core Idea:
 *   - leftSums  = all subset sums of nums[0 .. n/2)
 *   - rightSums = all subset sums of nums[n/2 .. n)
 *   - Sort rightSums. For each ls in leftSums, we want rs closest to (goal - ls).
 *     Binary search for the insertion point and check BOTH neighbors
 *     (floor and ceiling) since the exact target may not exist.
 *
 * Key Insight:
 *   - Every subset of nums = (subset of left half) ∪ (subset of right half),
 *     so every achievable sum is ls + rs for some pair.
 *
 * Time Complexity:  O(2^(N/2) × N) — enumeration, sort, and binary search.
 * Space Complexity: O(2^(N/2)) — one half's sums.
 */
fun minAbsDifference(nums: IntArray, goal: Int): Int {
    val n = nums.size
    val half = n / 2

    val leftSums = subsetSums(nums, 0, half)
    val rightSums = subsetSums(nums, half, n).sorted()

    var best = Long.MAX_VALUE

    for (ls in leftSums) {
        val need = goal.toLong() - ls  // want rs as close to `need` as possible

        val idx = rightSums.binarySearch(need).let {
            if (it >= 0) it else -(it + 1)  // insertion point
        }

        // Check the ceiling (idx) and floor (idx - 1) candidates.
        if (idx < rightSums.size) {
            best = minOf(best, kotlin.math.abs(ls + rightSums[idx] - goal))
        }
        if (idx > 0) {
            best = minOf(best, kotlin.math.abs(ls + rightSums[idx - 1] - goal))
        }
    }
    return best.toInt()
}

/**
 * Enumerate all 2^(to - from) subset sums of nums[from, to).
 * Uses an incremental build: start with [0], then for each element
 * append (existing + element) to the list — O(2^k) total.
 */
private fun subsetSums(nums: IntArray, from: Int, to: Int): List<Long> {
    val sums = ArrayList<Long>(1 shl (to - from))
    sums.add(0L)
    for (i in from until to) {
        val v = nums[i].toLong()
        val cur = sums.size
        for (j in 0 until cur) sums.add(sums[j] + v)
    }
    return sums
}

/**
 * Meet in the middle with step-by-step trace.
 */
fun minAbsDifferenceTrace(nums: IntArray, goal: Int) {
    val half = nums.size / 2
    val leftSums = subsetSums(nums, 0, half)
    val rightSums = subsetSums(nums, half, nums.size).sorted()

    println("Input: ${nums.toList()}, goal = $goal")
    println("  leftSums  = $leftSums")
    println("  rightSums (sorted) = $rightSums")

    var best = Long.MAX_VALUE
    for (ls in leftSums) {
        val need = goal.toLong() - ls
        val idx = rightSums.binarySearch(need).let { if (it >= 0) it else -(it + 1) }
        var localBest = Long.MAX_VALUE
        if (idx < rightSums.size) {
            localBest = minOf(localBest, kotlin.math.abs(ls + rightSums[idx] - goal))
        }
        if (idx > 0) {
            localBest = minOf(localBest, kotlin.math.abs(ls + rightSums[idx - 1] - goal))
        }
        println("  ls=$ls → need=$need → best pair diff=$localBest")
        best = minOf(best, localBest)
    }
    println("  Result: $best")
}
