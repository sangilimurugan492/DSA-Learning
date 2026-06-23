package array.greedy

/**
 * https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 *
 * There are spherical balloons taped onto a wall represented as intervals [xstart, xend].
 * An arrow shot at x bursts all balloons where xstart ≤ x ≤ xend.
 * Find the minimum number of arrows needed to burst all balloons.
 *
 * Example 1:
 *
 * Input: points = [[10,16],[2,8],[1,6],[7,12]]
 * Output: 2
 * Explanation: Arrow at x=6 bursts [2,8] and [1,6]. Arrow at x=11 bursts [10,16] and [7,12].
 *
 * Example 2:
 *
 * Input: points = [[1,2],[3,4],[5,6],[7,8]]
 * Output: 4
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Meta — same pattern as Non-overlapping Intervals)
 *
 * Key Insight: Sort by END. Shoot arrow at the end of the first balloon.
 * Any balloon that starts before this point is also burst. When a balloon starts
 * after the arrow position, we need a new arrow.
 */
fun main() {
    println(findMinArrowShots(arrayOf(intArrayOf(10, 16), intArrayOf(2, 8), intArrayOf(1, 6), intArrayOf(7, 12))))
    println(findMinArrowShots(arrayOf(intArrayOf(1, 2), intArrayOf(3, 4), intArrayOf(5, 6), intArrayOf(7, 8))))
    println(findMinArrowShots(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(4, 5))))
}

/**
 * Time Complexity O(N log N)
 * Space Complexity O(1)
 */
fun findMinArrowShots(points: Array<IntArray>): Int {
    if (points.isEmpty()) return 0

    points.sortBy { it[1] }  // Sort by end

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
