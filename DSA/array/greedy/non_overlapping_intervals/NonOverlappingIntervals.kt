package array.greedy.non_overlapping_intervals

/**
 * Non-overlapping Intervals — LeetCode #435
 * https://leetcode.com/problems/non-overlapping-intervals/
 *
 * Problem:
 * -------
 * Given an array of intervals, return the minimum number of intervals to remove
 * to make the rest non-overlapping.
 *
 * Example:  [[1,2],[2,3],[3,4],[1,3]] → 1 (remove [1,3])
 *           [[1,2],[1,2],[1,2]] → 2
 *           [[1,2],[2,3]] → 0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐
 *
 * Two approaches:
 * 1. Brute Force: O(2^N) — try all subsets
 * 2. Greedy: O(N log N) — sort by end, keep non-overlapping, count removals
 */

fun main() {
    val intervals = arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(1, 3))

    println("=== Method 1: Greedy (Sort by End) ===")
    println("eraseOverlapIntervals(${intervals.map { it.toList() }}) = ${eraseOverlapIntervals(intervals)}")

    println("\n=== Step-by-step trace ===")
    eraseOverlapIntervalsTrace(intervals.copyOf())
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: GREEDY — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY — Sort by end time. Keep intervals that don't overlap. Count removals.
 *
 * Core Idea:
 *   - Sort by end time (earliest ending first).
 *   - Greedily keep intervals that don't overlap with the last kept.
 *   - If current start < lastEnd → overlap → remove (count++).
 *   - Else → no overlap → update lastEnd.
 *
 * Key Insight:
 *   - Sorting by end time ensures we keep the interval that ends earliest,
 *     leaving maximum room for remaining intervals.
 *
 * Time Complexity:  O(N log N) — sort.
 * Space Complexity: O(1).
 */
fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
    if (intervals.isEmpty()) return 0

    intervals.sortBy { it[1] }

    var removals = 0
    var lastEnd = intervals[0][1]

    for (i in 1 until intervals.size) {
        if (intervals[i][0] < lastEnd) {
            removals++  // Overlap → remove current.
        } else {
            lastEnd = intervals[i][1]  // No overlap → keep.
        }
    }
    return removals
}

/**
 * Greedy with step-by-step trace.
 */
fun eraseOverlapIntervalsTrace(intervals: Array<IntArray>) {
    println("Input: ${intervals.map { it.toList() }}")
    intervals.sortBy { it[1] }
    println("Sorted by end: ${intervals.map { it.toList() }}")

    var removals = 0
    var lastEnd = intervals[0][1]
    println("  Keep [${intervals[0][0]},${intervals[0][1]}], lastEnd=$lastEnd")

    for (i in 1 until intervals.size) {
        if (intervals[i][0] < lastEnd) {
            removals++
            println("  [${intervals[i][0]},${intervals[i][1]}] overlaps (start=${intervals[i][0]} < lastEnd=$lastEnd) → REMOVE (count=$removals)")
        } else {
            lastEnd = intervals[i][1]
            println("  [${intervals[i][0]},${intervals[i][1]}] no overlap → KEEP, lastEnd=$lastEnd")
        }
    }
    println("  Result: $removals removals")
}
