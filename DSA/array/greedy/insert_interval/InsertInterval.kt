package array.greedy.insert_interval

/**
 * Insert Interval — LeetCode #57
 * https://leetcode.com/problems/insert-interval/
 *
 * Problem:
 * -------
 * Given an array of non-overlapping intervals sorted by start time, and a new interval,
 * insert the new interval and merge if necessary.
 *
 * Example:  intervals = [[1,3],[6,9]], newInterval = [2,5] → [[1,5],[6,9]]
 *           intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8] → [[1,2],[3,10],[12,16]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — add newInterval, then merge all overlapping pairs
 * 2. Three-Phase: O(N) — add before, merge overlapping, add after
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    insertBruteForce(
        arrayOf(intArrayOf(1, 3), intArrayOf(6, 9)),
        intArrayOf(2, 5)
    ).forEach { println(it.toList()) }

    println("\n=== Method 2: Three-Phase ===")
    insert(
        arrayOf(intArrayOf(1, 2), intArrayOf(3, 5), intArrayOf(6, 7), intArrayOf(8, 10), intArrayOf(12, 16)),
        intArrayOf(4, 8)
    ).forEach { println(it.toList()) }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Add newInterval to the list, then repeatedly merge overlapping pairs.
 *
 * Time Complexity:  O(N²) — repeated merging passes.
 * Space Complexity: O(N).
 */
fun insertBruteForce(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
    val all = (intervals.toList() + newInterval).sortedBy { it[0] }.toMutableList()
    val result = mutableListOf(all[0])

    for (i in 1 until all.size) {
        val last = result.last()
        if (all[i][0] <= last[1]) {
            last[1] = maxOf(last[1], all[i][1])
        } else {
            result.add(all[i])
        }
    }
    return result.toTypedArray()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: THREE-PHASE — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * THREE-PHASE — (1) Add all before newInterval, (2) Merge overlapping, (3) Add remaining.
 *
 * Core Idea:
 *   - Phase 1: Add intervals that end before newInterval starts (no overlap).
 *   - Phase 2: Merge all intervals that overlap with newInterval.
 *   - Phase 3: Add remaining intervals.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(N) — result.
 */
fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
    val result = mutableListOf<IntArray>()
    var i = 0
    val n = intervals.size

    // Phase 1: Add all intervals that end before newInterval starts
    while (i < n && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i])
        i++
    }

    // Phase 2: Merge all overlapping intervals with newInterval
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = minOf(newInterval[0], intervals[i][0])
        newInterval[1] = maxOf(newInterval[1], intervals[i][1])
        i++
    }
    result.add(newInterval)

    // Phase 3: Add remaining intervals
    while (i < n) {
        result.add(intervals[i])
        i++
    }

    return result.toTypedArray()
}
