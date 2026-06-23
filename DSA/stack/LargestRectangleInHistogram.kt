package stack

/**
 * https://leetcode.com/problems/largest-rectangle-in-histogram/
 * Given array of heights, find area of largest rectangle in histogram.
 * Example: [2,1,5,6,2,3] → 10 (rectangle [5,6] has area 2×5=10)
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 3 hardest stack problem)
 */

fun main() {
    println(largestRectangleAreaBruteForce(intArrayOf(2, 1, 5, 6, 2, 3)))
    println(largestRectangleAreaBruteForce(intArrayOf(2, 4)))
    println("---")
    println(largestRectangleAreaStack(intArrayOf(2, 1, 5, 6, 2, 3)))
    println(largestRectangleAreaStack(intArrayOf(2, 4)))
}

/**
 * BRUTE FORCE: O(N²) — for each bar, expand left and right to find max width
 */
fun largestRectangleAreaBruteForce(heights: IntArray): Int {
    var maxArea = 0
    for (i in heights.indices) {
        var minHeight = heights[i]
        for (j in i until heights.size) {
            minHeight = minOf(minHeight, heights[j])
            maxArea = maxOf(maxArea, minHeight * (j - i + 1))
        }
    }
    return maxArea
}

/**
 * OPTIMAL: O(N) Monotonic Stack
 * Stack stores indices in increasing height order.
 * When smaller height found, pop and calculate area: height[popped] × width.
 * Width = current_index - stack.top - 1 (or current_index if stack empty)
 *
 * Add sentinel 0 at end to flush remaining bars.
 */
fun largestRectangleAreaStack(heights: IntArray): Int {
    val stack = ArrayDeque<Int>()
    var maxArea = 0
    val extended = heights + intArrayOf(0)  // sentinel to flush stack

    for (i in extended.indices) {
        while (stack.isNotEmpty() && extended[i] < extended[stack.last()]) {
            val height = extended[stack.removeLast()]
            val width = if (stack.isEmpty()) i else i - stack.last() - 1
            maxArea = maxOf(maxArea, height * width)
        }
        stack.addLast(i)
    }
    return maxArea
}
