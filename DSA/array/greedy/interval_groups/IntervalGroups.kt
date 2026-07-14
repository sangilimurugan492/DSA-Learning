package array.greedy.interval_groups

/**
 * Divide Intervals Into Minimum Number of Groups — LeetCode #2406
 * https://leetcode.com/problems/divide-intervals-into-minimum-number-of-groups/
 *
 * Problem:
 * -------
 * Given intervals, divide them into minimum number of groups such that no two intervals
 * in the same group overlap.
 *
 * Example:  [[5,10],[6,8],[1,5],[2,3],[1,10]] → 3
 *
 * FAANG Importance: ⭐⭐⭐⭐
 *
 * Two approaches:
 * 1. Sweep Line with HashMap: O(N + maxTime) — count starts/ends, sweep
 * 2. Min-Heap: O(N log N) — sort by start, reuse rooms that ended
 */

fun main() {
    val intervals = arrayOf(intArrayOf(5, 10), intArrayOf(6, 8), intArrayOf(1, 5), intArrayOf(2, 3), intArrayOf(1, 10))

    println("=== Method 1: Sweep Line (Counting) ===")
    println("minGroups(${intervals.map { it.toList() }}) = ${minGroups(intervals)}")

    println("\n=== Method 2: Min-Heap ===")
    println("minGroups(${intervals.map { it.toList() }}) = ${minGroupsHeap(intervals)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: SWEEP LINE (COUNTING) — O(N + maxTime)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SWEEP LINE — Count starts and ends at each time point. Sweep through,
 * track running overlap. Max overlap = min groups.
 *
 * Core Idea:
 *   - Same as Meeting Rooms II sweep line, but using counting arrays.
 *   - At each time: overlap += starts[t], overlap -= ends[t].
 *   - Max overlap across all times = minimum groups needed.
 *
 * Time Complexity:  O(N + maxTime) — counting + sweep.
 * Space Complexity: O(maxTime) — counting arrays.
 */
fun minGroups(intervals: Array<IntArray>): Int {
    val n = intervals.size
    val starts = IntArray(1_000_001)
    val ends = IntArray(1_000_001)
    var maxEnd = 0

    for (interval in intervals) {
        starts[interval[0]]++
        ends[interval[1]]++
        if (interval[1] > maxEnd) maxEnd = interval[1]
    }

    var maxOverlap = 0
    var overlap = 0

    for (i in 1..maxEnd) {
        if (starts[i] > 0) {
            overlap += starts[i]
            if (overlap > maxOverlap) maxOverlap = overlap
        }
        overlap -= ends[i]
    }
    return maxOverlap
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MIN-HEAP — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MIN-HEAP — Sort by start. Min-heap tracks end times. If earliest ended ≤ current start,
 * reuse that group. Heap size = min groups.
 *
 * Time Complexity:  O(N log N).
 * Space Complexity: O(N).
 */
fun minGroupsHeap(intervals: Array<IntArray>): Int {
    intervals.sortBy { it[0] }
    val minHeap = java.util.PriorityQueue<Int>()

    for (interval in intervals) {
        if (minHeap.isNotEmpty() && minHeap.peek() < interval[0]) {
            minHeap.poll()  // Reuse group.
        }
        minHeap.offer(interval[1])
    }
    return minHeap.size
}
