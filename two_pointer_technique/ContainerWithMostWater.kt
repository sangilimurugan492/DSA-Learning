package two_pointer_technique

/**
 * https://leetcode.com/problems/container-with-most-water/description/
 *
 * You are given an integer array height of length n. There are n vertical lines drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]).
 *
 * Find two lines that together with the x-axis form a container, such that the container contains the most water.
 *
 * Return the maximum amount of water a container can store.
 *
 * Input: height = [1,8,6,2,5,4,8,3,7]
 * Output: 49
 * Explanation: The above vertical lines are represented by array [1,8,6,2,5,4,8,3,7]. In this case, the max area of water (blue section) the container can contain is 49.
 * Example 2:
 *
 * Input: height = [1,1]
 * Output: 1
 */
fun main() {
    println(containerWithMostWaterBF(intArrayOf(1,8,6,2,5,4,8,3,7)))
    println(containerWithMostWaterOP(intArrayOf(1,8,6,2,5,4,8,3,7)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun containerWithMostWaterBF(heights: IntArray): Int {
    var maxArea = 0
    var minHeight: Int
    var width: Int

    for (i in heights.indices) {
        for (j in i+1 until heights.size) {
            minHeight = heights[i].coerceAtMost(heights[j])
            width = j - i
            maxArea = maxArea.coerceAtLeast((minHeight * width))
        }
    }
    return maxArea
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun containerWithMostWaterOP(heights: IntArray): Int {
    var maxArea = 0
    var minHeight: Int
    var width: Int
    var left = 0
    var right = heights.size - 1

    while (left < right) {
        minHeight = heights[left].coerceAtMost(heights[right])
        width = right - left
        maxArea = maxArea.coerceAtLeast((minHeight * width))
        if (heights[left] <= heights[right]) {
            left++
        } else {
            right--
        }
    }

    return maxArea
}