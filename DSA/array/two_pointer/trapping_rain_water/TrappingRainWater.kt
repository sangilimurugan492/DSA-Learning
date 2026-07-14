package array.two_pointer.trapping_rain_water

/**
 * Trapping Rain Water — LeetCode #42
 * https://leetcode.com/problems/trapping-rain-water/
 *
 * Problem:
 * -------
 * Given n non-negative integers representing an elevation map, compute how much
 * water can be trapped after raining.
 *
 * Example 1:  height = [0,1,0,2,1,0,1,3,2,1,2,1]  →  6
 * Example 2:  height = [4,2,0,3,2,5]               →  9
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 3 most asked Hard problem)
 *
 * Key Insight: Water at index i = min(maxLeft[i], maxRight[i]) - height[i]
 */

fun main() {
    val test1 = intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)
    val test2 = intArrayOf(4, 2, 0, 3, 2, 5)

    println("=== Method 1: Brute Force ===")
    println("trap([0,1,0,2,1,0,1,3,2,1,2,1]) = ${trapBruteForce(test1)}")
    println("trap([4,2,0,3,2,5]) = ${trapBruteForce(test2)}")

    println("\n=== Method 2: DP (Precompute maxLeft/maxRight) ===")
    println("trap([0,1,0,2,1,0,1,3,2,1,2,1]) = ${trapDP(test1)}")
    println("trap([4,2,0,3,2,5]) = ${trapDP(test2)}")

    println("\n=== Method 3: Two Pointer (Optimal) ===")
    println("trap([0,1,0,2,1,0,1,3,2,1,2,1]) = ${trapTwoPointer(test1)}")
    println("trap([4,2,0,3,2,5]) = ${trapTwoPointer(test2)}")

    println("\n=== Step-by-step trace (Two Pointer) ===")
    trapTrace(test1)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Scan Left/Right for Each Bar
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each bar, find maxLeft and maxRight by scanning.
 *
 * Core Idea:
 *   - Water trapped at index i = min(maxLeft, maxRight) - height[i]
 *   - maxLeft = tallest bar to the left of i (including i)
 *   - maxRight = tallest bar to the right of i (including i)
 *
 * Time Complexity:  O(N²) — for each index, scan left and right.
 * Space Complexity: O(1).
 */
fun trapBruteForce(height: IntArray): Int {
    var totalWater = 0

    for (i in height.indices) {
        // Find max height to the left (including current).
        var maxLeft = 0
        for (l in 0..i) maxLeft = maxOf(maxLeft, height[l])

        // Find max height to the right (including current).
        var maxRight = 0
        for (r in i until height.size) maxRight = maxOf(maxRight, height[r])

        // Water at this position = min(maxLeft, maxRight) - height[i].
        totalWater += minOf(maxLeft, maxRight) - height[i]
    }

    return totalWater
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: DP — Precompute maxLeft and maxRight Arrays
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DP — Precompute maxLeft[] and maxRight[] in O(N), then calculate water.
 *
 * Core Idea:
 *   - Instead of scanning for each index, precompute the max arrays.
 *   - maxLeft[i] = max(maxLeft[i-1], height[i]) — left to right.
 *   - maxRight[i] = max(maxRight[i+1], height[i]) — right to left.
 *   - Water at i = min(maxLeft[i], maxRight[i]) - height[i].
 *
 * Time Complexity:  O(N) — three passes.
 * Space Complexity: O(N) — two arrays.
 */
fun trapDP(height: IntArray): Int {
    if (height.isEmpty()) return 0
    val n = height.size

    // Precompute maxLeft: tallest bar from left up to index i.
    val maxLeft = IntArray(n)
    maxLeft[0] = height[0]
    for (i in 1 until n) maxLeft[i] = maxOf(maxLeft[i - 1], height[i])

    // Precompute maxRight: tallest bar from right up to index i.
    val maxRight = IntArray(n)
    maxRight[n - 1] = height[n - 1]
    for (i in n - 2 downTo 0) maxRight[i] = maxOf(maxRight[i + 1], height[i])

    // Calculate total water.
    var totalWater = 0
    for (i in height.indices) {
        totalWater += minOf(maxLeft[i], maxRight[i]) - height[i]
    }
    return totalWater
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 3: TWO POINTER (OPTIMAL)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * TWO POINTER — Track maxLeft and maxRight as running variables.
 *
 * Core Idea:
 *   - Use two pointers from both ends.
 *   - Track maxLeft and maxRight as we move inward.
 *   - Move the pointer with the SMALLER max — that side is the bottleneck.
 *   - Water at the moved pointer = maxLeft/maxRight - height[pointer].
 *
 * Why move the smaller max:
 *   - If maxLeft < maxRight, the water at `left` is determined by maxLeft
 *     (since maxRight is already taller, min(maxLeft, maxRight) = maxLeft).
 *   - So we can safely calculate water at `left` and move it inward.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — only variables.
 */
fun trapTwoPointer(height: IntArray): Int {
    if (height.isEmpty()) return 0

    var left = 0
    var right = height.size - 1
    var maxLeft = height[left]
    var maxRight = height[right]
    var totalWater = 0

    while (left < right) {
        if (maxLeft < maxRight) {
            // Left side is the bottleneck — process left pointer.
            left++
            maxLeft = maxOf(maxLeft, height[left])
            totalWater += maxLeft - height[left]  // maxLeft >= height[left], so ≥ 0
        } else {
            // Right side is the bottleneck — process right pointer.
            right--
            maxRight = maxOf(maxRight, height[right])
            totalWater += maxRight - height[right]
        }
    }

    return totalWater
}

/**
 * Two-pointer with step-by-step trace for learning/debugging.
 */
fun trapTrace(height: IntArray) {
    println("Input: ${height.toList()}")
    var left = 0
    var right = height.size - 1
    var maxLeft = height[left]
    var maxRight = height[right]
    var totalWater = 0

    while (left < right) {
        if (maxLeft < maxRight) {
            left++
            maxLeft = maxOf(maxLeft, height[left])
            val water = maxLeft - height[left]
            totalWater += water
            println("  left=$left (${height[left]}) | maxLeft=$maxLeft maxRight=$maxRight | water=$water | total=$totalWater")
        } else {
            right--
            maxRight = maxOf(maxRight, height[right])
            val water = maxRight - height[right]
            totalWater += water
            println("  right=$right (${height[right]}) | maxLeft=$maxLeft maxRight=$maxRight | water=$water | total=$totalWater")
        }
    }
    println("  Result: $totalWater")
}
