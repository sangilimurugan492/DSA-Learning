package array.greedy.minimum_arrows_to_burst_balloons

/**
 * Minimum Number of Arrows to Burst Balloons — LeetCode #452
 * https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 *
 * Problem:
 * -------
 * Balloons are taped on a wall as intervals [xstart, xend]. An arrow at x bursts
 * all balloons where xstart ≤ x ≤ xend. Find the minimum number of arrows.
 *
 * Example:  [[10,16],[2,8],[1,6],[7,12]] → 2
 *           [[1,2],[3,4],[5,6],[7,8]] → 4
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Meta — same pattern as Non-overlapping Intervals)
 *
 * Two approaches:
 * 1. Sort by Start: O(N log N) — merge overlapping, count groups
 * 2. Sort by End: O(N log N) — shoot at end of first, skip all that start before
 */

fun main() {
    val points = arrayOf(intArrayOf(10, 16), intArrayOf(2, 8), intArrayOf(1, 6), intArrayOf(7, 12))

    println("=== Method 1: Sort by Start ===")
    println("findMinArrowShots(${points.map { it.toList() }}) = ${findMinArrowShotsByStart(points.copyOf())}")

    println("\n=== Method 2: Sort by End ===")
    println("findMinArrowShots(${points.map { it.toList() }}) = ${findMinArrowShots(points.copyOf())}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: SORT BY START — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SORT BY START — Merge overlapping intervals. Count merged groups = arrows.
 *
 * Time Complexity:  O(N log N).
 * Space Complexity: O(1).
 */
fun findMinArrowShotsByStart(points: Array<IntArray>): Int {
    if (points.isEmpty()) return 0
    points.sortBy { it[0] }

    var arrows = 1
    var end = points[0][1]

    for (i in 1 until points.size) {
        if (points[i][0] > end) {
            arrows++
            end = points[i][1]
        } else {
            end = minOf(end, points[i][1])  // Narrow the overlap range
        }
    }
    return arrows
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SORT BY END — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SORT BY END — Shoot arrow at end of first balloon. Skip all that start before it.
 *
 * Core Idea:
 *   - Sort by end time.
 *   - Shoot arrow at the end of the first balloon.
 *   - Any balloon that starts before this arrow position is also burst.
 *   - When a balloon starts after the arrow → need a new arrow.
 *
 * Key Insight:
 *   - Same as Non-overlapping Intervals but with "touching = overlap" (≤ vs <).
 *
 * Time Complexity:  O(N log N).
 * Space Complexity: O(1).
 */
fun findMinArrowShots(points: Array<IntArray>): Int {
    if (points.isEmpty()) return 0
    points.sortBy { it[1] }

    var arrows = 1
    var arrowPos = points[0][1]

    for (i in 1 until points.size) {
        if (points[i][0] > arrowPos) {
            arrows++
            arrowPos = points[i][1]
        }
    }
    return arrows
}
