package array.greedy.merge_intervals

/**
 * Merge Intervals — LeetCode #56
 * https://leetcode.com/problems/merge-intervals/
 *
 * Problem:
 * -------
 * Given an array of intervals, merge all overlapping intervals.
 *
 * Example:  [[1,3],[2,6],[8,10],[15,18]] → [[1,6],[8,10],[15,18]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 5 most asked)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — compare every pair, merge until no changes
 * 2. Sort + Single Pass: O(N log N) — sort by start, merge in one pass
 */

fun main() {
    val intervals = arrayOf(intArrayOf(1, 3), intArrayOf(2, 6), intArrayOf(8, 10), intArrayOf(15, 18))

    println("=== Method 1: Brute Force ===")
    mergeBruteForce(intervals.copyOf()).forEach { println(it.toList()) }

    println("\n=== Method 2: Sort + Single Pass ===")
    mergeOptimal(intervals.copyOf()).forEach { println(it.toList()) }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Compare every pair of intervals and merge until no more merges possible.
 *
 * Time Complexity:  O(N²) — repeated passes.
 * Space Complexity: O(N).
 */
fun mergeBruteForce(intervals: Array<IntArray>): Array<IntArray> {
    if (intervals.isEmpty()) return intervals
    val sorted = intervals.sortedBy { it[0] }.toMutableList()
    var changed = true
    while (changed) {
        changed = false
        val merged = mutableListOf(sorted[0])
        for (i in 1 until sorted.size) {
            val last = merged.last()
            val curr = sorted[i]
            if (curr[0] <= last[1]) {
                last[1] = maxOf(last[1], curr[1])
                changed = true
            } else {
                merged.add(curr)
            }
        }
        sorted.clear(); sorted.addAll(merged)
    }
    return sorted.toTypedArray()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SORT + SINGLE PASS — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SORT + SINGLE PASS — Sort by start. If current start ≤ previous end → merge.
 *
 * Core Idea:
 *   - Sort intervals by start time.
 *   - For each interval, if it overlaps with the last merged → merge.
 *   - Otherwise, start a new interval.
 *
 * Time Complexity:  O(N log N) — sort + one pass.
 * Space Complexity: O(N) — output.
 */
fun mergeOptimal(intervals: Array<IntArray>): Array<IntArray> {
    if (intervals.isEmpty()) return intervals
    intervals.sortBy { it[0] }
    val result = mutableListOf(intervals[0])

    for (i in 1 until intervals.size) {
        val last = result.last()
        if (intervals[i][0] <= last[1]) {
            last[1] = maxOf(last[1], intervals[i][1])
        } else {
            result.add(intervals[i])
        }
    }
    return result.toTypedArray()
}
