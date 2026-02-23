package two_pointer_technique

/**
 * https://leetcode.com/problems/trapping-rain-water/description/
 *
 * Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.
 *
 * Example 1:
 *
 * Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
 * Output: 6
 * Explanation: The above elevation map (black section) is represented by array [0,1,0,2,1,0,1,3,2,1,2,1]. In this case, 6 units of rain water (blue section) are being trapped.
 * Example 2:
 *
 * Input: height = [4,2,0,3,2,5]
 * Output: 9
 */

fun main() {
    println(trappingRainWaterIBF(intArrayOf(4,2,0,3,2,5)))

    println(trappingRainWaterIOP(intArrayOf(4,2,0,3,2,5)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun trappingRainWaterIBF(heights: IntArray): Int {
    var totalWater = 0
    var left: Int
    var right: Int
    var maxLeft: Int
    var maxRight: Int
    var currentWater: Int

    for (i in heights.indices) {
        left = i
        right = i
        maxRight = 0
        maxLeft = 0

        while(left >= 0) {
            maxLeft = maxLeft.coerceAtLeast(heights[left])
            left--
        }

        while (right < heights.size) {
            maxRight = maxRight.coerceAtLeast(heights[right])
            right ++
        }
        currentWater = maxLeft.coerceAtMost(maxRight) - heights[i]
        if (currentWater >= 0) {
            totalWater += currentWater
        }
    }

    return totalWater
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun trappingRainWaterIOP(heights: IntArray): Int {
    var totalWater = 0
    var left = 0
    var right: Int = heights.size - 1
    var maxLeft = 0
    var maxRight = 0

    while (left < right) {
        if (heights[left] <= heights[right]) {
            if (heights[left] >= maxLeft) {
                maxLeft = heights[left]
            } else {
                totalWater += maxLeft - heights[left]

            }
            left++
        } else {
            if (heights[right] >= maxRight) {
                maxRight = heights[right]
            } else {
                totalWater += maxRight - heights[right]
            }
            right--
        }
    }
    return totalWater
}

