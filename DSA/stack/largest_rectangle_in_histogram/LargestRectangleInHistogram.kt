package stack.largest_rectangle_in_histogram

/**
 * Largest Rectangle in Histogram — LeetCode #84
 * https://leetcode.com/problems/largest-rectangle-in-histogram/
 *
 * Problem:
 * -------
 * Given an array of heights representing a histogram's bar heights, find the area
 * of the largest rectangle that can be formed within the histogram.
 *
 * Example:  heights = [2,1,5,6,2,3]  →  10  (rectangle [5,6] has area 2×5=10)
 *           heights = [2,4]  →  4
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 3 hardest stack problem)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — for each bar, expand left and right
 * 2. Monotonic Stack: O(N) — stack stores indices in increasing height order
 */

fun main() {
    val heights = intArrayOf(2, 1, 5, 6, 2, 3)

    println("=== Method 1: Brute Force ===")
    println("largestRectangleArea(${heights.toList()}) = ${largestRectangleAreaBruteForce(heights)}")

    println("\n=== Method 2: Monotonic Stack ===")
    println("largestRectangleArea(${heights.toList()}) = ${largestRectangleAreaStack(heights)}")

    println("\n=== Step-by-step trace ===")
    largestRectangleAreaTrace(heights)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each bar, expand right and track min height. Area = minHeight × width.
 *
 * Time Complexity:  O(N²) — nested loops.
 * Space Complexity: O(1).
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

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MONOTONIC STACK — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MONOTONIC STACK — Stack stores indices in increasing height order.
 *
 * Core Idea:
 *   - When a smaller height is found, pop from stack and calculate area.
 *   - Area = height[popped] × width.
 *   - Width = current_index - stack.top - 1 (or current_index if stack empty).
 *   - Add sentinel 0 at end to flush remaining bars.
 *
 * Key Insight:
 *   - For each bar, the max rectangle using its height extends from the previous
 *     smaller bar to the next smaller bar.
 *   - The stack helps find these boundaries efficiently.
 *
 * Time Complexity:  O(N) — each element pushed/popped once.
 * Space Complexity: O(N) — stack.
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

/**
 * Monotonic stack with step-by-step trace.
 */
fun largestRectangleAreaTrace(heights: IntArray) {
    println("Input: ${heights.toList()}")
    val stack = ArrayDeque<Int>()
    var maxArea = 0
    val extended = heights + intArrayOf(0)

    for (i in extended.indices) {
        println("  i=$i, height=${extended[i]}, stack=${stack.toList()}")
        while (stack.isNotEmpty() && extended[i] < extended[stack.last()]) {
            val height = extended[stack.removeLast()]
            val width = if (stack.isEmpty()) i else i - stack.last() - 1
            val area = height * width
            println("    Pop index=${stack.toList().lastOrNull() ?: "empty"}, height=$height, width=$width, area=$area")
            maxArea = maxOf(maxArea, area)
        }
        stack.addLast(i)
    }
    println("  Result: $maxArea")
}
