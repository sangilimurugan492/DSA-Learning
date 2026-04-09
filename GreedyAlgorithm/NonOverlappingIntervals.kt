package GreedyAlgorithm

/**
 * 435. Non-overlapping Intervals
 * https://leetcode.com/problems/non-overlapping-intervals/description/?envType=problem-list-v2&envId=greedy
 * Given an array of intervals intervals where intervals[i] = [starti, endi], return the minimum number of intervals you need to remove to make the rest of the intervals non-overlapping.
 *
 * Note that intervals which only touch at a point are non-overlapping. For example, [1, 2] and [2, 3] are non-overlapping.
 *
 * Example 1:
 *
 * Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
 * Output: 1
 * Explanation: [1,3] can be removed and the rest of the intervals are non-overlapping.
 * Example 2:
 *
 * Input: intervals = [[1,2],[1,2],[1,2]]
 * Output: 2
 * Explanation: You need to remove two [1,2] to make the rest of the intervals non-overlapping.
 * Example 3:
 *
 * Input: intervals = [[1,2],[2,3]]
 * Output: 0
 * Explanation: You don't need to remove any of the intervals since they're already non-overlapping.
 *
 */

fun main() {
    println(eraseOverlapIntervals(arrayOf(intArrayOf(1,2), intArrayOf(2,3), intArrayOf(3,4), intArrayOf(1,3))))
}

fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
    if (intervals.isEmpty()) return 0

    intervals.sortBy { it[1] }

    var removals = 0
    // Initialize lastEnd with the end time of the first interval
    var lastEnd = intervals[0][1]

    // 2. Iterate through the rest of the intervals
    for (i in 1 until intervals.size) {
        val currentStart = intervals[i][0]
        val currentEnd = intervals[i][1]

        if (currentStart < lastEnd) {
            // If the current interval starts before the last one ends,
            // it overlaps. We "remove" the current one.
            removals++
        } else {
            // No overlap: update lastEnd to the current interval's end
            lastEnd = currentEnd
        }
    }

    return removals
}
