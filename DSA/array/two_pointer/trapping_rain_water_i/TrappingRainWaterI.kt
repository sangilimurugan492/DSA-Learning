package array.two_pointer.trapping_rain_water_i

/**
 * https://leetcode.com/problems/trapping-rain-water/
 *
 * Given n non-negative integers representing an elevation map, compute how much
 * water it can trap after raining.
 *
 * Example: height = [0,1,0,2,1,0,1,3,2,1,2,1] → Output: 6
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 3 most asked Hard problem)
 */

fun main() {
    println(trapBruteForce(intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)))
    println("---")
    println(trapDP(intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)))
    println("---")
    println(trapTwoPointer(intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — for each bar, scan left and right for max
 * Space Complexity: O(1)
 *
 * For each index i, water = min(maxLeft, maxRight) - height[i].
 * Scan left/right for each position to find max.
 */
fun trapBruteForce(height: IntArray): Int {
    var totalWater = 0
    for (i in height.indices) {
        var maxLeft = 0
        var maxRight = 0
        for (l in 0..i) maxLeft = maxOf(maxLeft, height[l])
        for (r in i until height.size) maxRight = maxOf(maxRight, height[r])
        totalWater += minOf(maxLeft, maxRight) - height[i]
    }
    return totalWater
}

/**
 * BETTER — Dynamic Programming (precompute maxLeft/maxRight)
 * Time Complexity: O(N) — three passes
 * Space Complexity: O(N) — two arrays
 *
 * Precompute maxLeft[] and maxRight[] arrays, then calculate water.
 */
fun trapDP(height: IntArray): Int {
    if (height.isEmpty()) return 0
    val n = height.size
    val maxLeft = IntArray(n)
    val maxRight = IntArray(n)

    maxLeft[0] = height[0]
    for (i in 1 until n) maxLeft[i] = maxOf(maxLeft[i - 1], height[i])

    maxRight[n - 1] = height[n - 1]
    for (i in n - 2 downTo 0) maxRight[i] = maxOf(maxRight[i + 1], height[i])

    var totalWater = 0
    for (i in height.indices) totalWater += minOf(maxLeft[i], maxRight[i]) - height[i]
    return totalWater
}

/**
 * OPTIMAL — Two Pointer
 * Time Complexity: O(N) — single pass
 * Space Complexity: O(1)
 *
 * Track maxLeft and maxRight as running variables. Move the pointer with
 * smaller max — that side is the bottleneck, so we can calculate water.
 */
fun trapTwoPointer(height: IntArray): Int {
    var left = 0
    var right = height.size - 1
    var maxLeft = height[left]
    var maxRight = height[right]
    var totalWater = 0

    while (left < right) {
        if (maxLeft < maxRight) {
            left++
            maxLeft = maxOf(maxLeft, height[left])
            totalWater += maxLeft - height[left]
        } else {
            right--
            maxRight = maxOf(maxRight, height[right])
            totalWater += maxRight - height[right]
        }
    }
    return totalWater
}
