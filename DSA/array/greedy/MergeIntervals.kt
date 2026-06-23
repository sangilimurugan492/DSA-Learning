package array.greedy

/**
 * https://leetcode.com/problems/merge-intervals/
 *
 * Given an array of intervals, merge all overlapping intervals.
 *
 * Example: [[1,3],[2,6],[8,10],[15,18]] → [[1,6],[8,10],[15,18]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 5 most asked)
 */

fun main() {
    val intervals = arrayOf(intArrayOf(1, 3), intArrayOf(2, 6), intArrayOf(8, 10), intArrayOf(15, 18))
    mergeBruteForce(intervals).forEach { println(it.toList()) }
    println("---")
    mergeOptimal(intervals).forEach { println(it.toList()) }
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — compare every pair of intervals
 * Space Complexity: O(N)
 *
 * For each interval, check if it overlaps with any other and merge.
 * Repeat until no more merges possible.
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
        sorted.clear()
        sorted.addAll(merged)
    }
    return sorted.toTypedArray()
}

/**
 * OPTIMAL — Sort + Single Pass
 * Time Complexity: O(N log N) — sort + one pass
 * Space Complexity: O(N) — output
 *
 * Sort by start time. If current start ≤ previous end → merge.
 * Otherwise, start a new interval.
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
