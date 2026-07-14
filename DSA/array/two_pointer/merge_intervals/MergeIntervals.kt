package array.two_pointer.merge_intervals

/**
 * Merge Intervals — LeetCode #56
 * https://leetcode.com/problems/merge-intervals/
 *
 * Problem:
 * -------
 * Given an array of intervals where intervals[i] = [start, end], merge all
 * overlapping intervals, and return the merged result.
 *
 * Example:  [[1,3],[2,6],[8,10],[15,18]]  →  [[1,6],[8,10],[15,18]]
 *           [[1,4],[4,5]]  →  [[1,5]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic interval problem)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — compare all pairs
 * 2. Sort + Merge: O(N log N) — sort by start, merge sequentially
 */

fun main() {
    val intervals = arrayOf(intArrayOf(1, 3), intArrayOf(2, 6), intArrayOf(8, 10), intArrayOf(15, 18))

    println("=== Method 1: Brute Force ===")
    println("merge(${intervals.toList()}) = ${mergeBruteForce(intervals).toList()}")

    println("\n=== Method 2: Sort + Merge ===")
    println("merge(${intervals.toList()}) = ${mergeOptimal(intervals).toList()}")

    println("\n=== Step-by-step trace ===")
    mergeTrace(intervals)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Compare all pairs of intervals. If they overlap, merge them.
 * Repeat until no more merges possible.
 *
 * Core Idea:
 *   - For each pair of intervals, check if they overlap.
 *   - If overlap, merge and restart.
 *
 * Time Complexity:  O(N²) — compare all pairs.
 * Space Complexity: O(N) — result list.
 */
fun mergeBruteForce(intervals: Array<IntArray>): Array<IntArray> {
    val sorted = intervals.sortedBy { it[0] }.toMutableList()
    var changed = true

    while (changed) {
        changed = false
        val merged = mutableListOf<IntArray>()
        var i = 0
        while (i < sorted.size) {
            if (i + 1 < sorted.size && sorted[i][1] >= sorted[i + 1][0]) {
                // Overlap — merge.
                merged.add(intArrayOf(sorted[i][0], maxOf(sorted[i][1], sorted[i + 1][1])))
                i += 2
                changed = true
            } else {
                merged.add(sorted[i])
                i++
            }
        }
        sorted.clear()
        sorted.addAll(merged)
    }
    return sorted.toTypedArray()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SORT + MERGE (OPTIMAL) — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SORT + MERGE — Sort by start. Iterate and merge overlapping intervals.
 *
 * Core Idea:
 *   - Sort intervals by start time.
 *   - For each interval, if it overlaps with the last merged interval → merge.
 *   - Otherwise → add as new interval.
 *
 * Key Insight:
 *   - After sorting, overlapping intervals are adjacent.
 *   - Only need to compare with the last merged interval.
 *
 * Time Complexity:  O(N log N) — sorting dominates.
 * Space Complexity: O(N) — result list.
 */
fun mergeOptimal(intervals: Array<IntArray>): Array<IntArray> {
    if (intervals.isEmpty()) return arrayOf()
    val sorted = intervals.sortedBy { it[0] }
    val result = mutableListOf<IntArray>(sorted[0])

    for (i in 1 until sorted.size) {
        val last = result.last()
        val curr = sorted[i]
        if (curr[0] <= last[1]) {
            // Overlap — extend the last interval.
            last[1] = maxOf(last[1], curr[1])
        } else {
            // No overlap — add new interval.
            result.add(curr)
        }
    }
    return result.toTypedArray()
}

/**
 * Sort + merge with step-by-step trace.
 */
fun mergeTrace(intervals: Array<IntArray>) {
    val sorted = intervals.sortedBy { it[0] }
    println("Sorted: ${sorted.toList()}")
    val result = mutableListOf<IntArray>(sorted[0].copyOf())
    println("  Start: result=${result.toList()}")

    for (i in 1 until sorted.size) {
        val last = result.last()
        val curr = sorted[i]
        if (curr[0] <= last[1]) {
            println("  ${curr.toList()} overlaps with ${last.toList()} → merge → [${last[0]}, ${maxOf(last[1], curr[1])}]")
            last[1] = maxOf(last[1], curr[1])
        } else {
            println("  ${curr.toList()} no overlap → add new")
            result.add(curr.copyOf())
        }
    }
    println("  Result: ${result.toList()}")
}
