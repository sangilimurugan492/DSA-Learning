package array.two_pointer

/**
 * https://leetcode.com/problems/container-with-most-water/
 *
 * Given n non-negative integers, find two lines that together with x-axis
 * form a container that holds the most water.
 *
 * Example: height = [1,8,6,2,5,4,8,3,7] → Output: 49
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 most asked)
 */

fun main() {
    println(maxAreaBruteForce(intArrayOf(1, 8, 6, 2, 5, 4, 8, 3, 7)))
    println("---")
    println(maxAreaTwoPointer(intArrayOf(1, 8, 6, 2, 5, 4, 8, 3, 7)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — check every pair
 * Space Complexity: O(1)
 *
 * For every pair of lines, calculate area = min(h[i], h[j]) * (j - i).
 */
fun maxAreaBruteForce(height: IntArray): Int {
    var maxArea = 0
    for (i in height.indices) {
        for (j in i + 1 until height.size) {
            val area = minOf(height[i], height[j]) * (j - i)
            maxArea = maxOf(maxArea, area)
        }
    }
    return maxArea
}

/**
 * OPTIMAL — Two Pointer
 * Time Complexity: O(N) — single pass
 * Space Complexity: O(1)
 *
 * Start from widest container. Move the shorter line inward — moving the
 * longer line can only decrease area (width shrinks, height ≤ shorter line).
 */
fun maxAreaTwoPointer(height: IntArray): Int {
    var left = 0
    var right = height.size - 1
    var maxArea = 0

    while (left < right) {
        val area = minOf(height[left], height[right]) * (right - left)
        maxArea = maxOf(maxArea, area)
        if (height[left] < height[right]) left++ else right--
    }
    return maxArea
}
