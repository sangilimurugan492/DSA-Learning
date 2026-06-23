package array.greedy

/**
 * https://leetcode.com/problems/insert-interval/
 *
 * You are given an array of non-overlapping intervals sorted by start time, and a new interval.
 * Insert the new interval and merge if necessary.
 *
 * Example 1:
 *
 * Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
 * Output: [[1,5],[6,9]]
 *
 * Example 2:
 *
 * Input: intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
 * Output: [[1,2],[3,10],[12,16]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)
 *
 * Key Insight: Three phases: (1) Add all intervals before newInterval,
 * (2) Merge all overlapping intervals with newInterval, (3) Add remaining intervals.
 */
fun main() {
    val result1 = insert(
        arrayOf(intArrayOf(1, 3), intArrayOf(6, 9)),
        intArrayOf(2, 5)
    )
    result1.forEach { println(it.toList()) }

    println("---")

    val result2 = insert(
        arrayOf(intArrayOf(1, 2), intArrayOf(3, 5), intArrayOf(6, 7), intArrayOf(8, 10), intArrayOf(12, 16)),
        intArrayOf(4, 8)
    )
    result2.forEach { println(it.toList()) }
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N) for result
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
