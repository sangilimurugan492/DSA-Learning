package stack.daily_temperatures

/**
 * Daily Temperatures — LeetCode #739
 * https://leetcode.com/problems/daily-temperatures/
 *
 * Problem:
 * -------
 * Given array of temperatures, return array showing days to wait for warmer temp.
 *
 * Example:  [73,74,75,71,69,72,76,73] → [1,1,4,2,1,1,0,0]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (#1 Monotonic Stack problem)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — for each day, scan forward for warmer day
 * 2. Monotonic Stack: O(N) — stack stores indices waiting for warmer day
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    println("dailyTemperatures([73,74,75,71,69,72,76,73]) = ${dailyTemperaturesBruteForce(intArrayOf(73, 74, 75, 71, 69, 72, 76, 73)).toList()}")

    println("\n=== Method 2: Monotonic Stack ===")
    println("dailyTemperatures([73,74,75,71,69,72,76,73]) = ${dailyTemperaturesMonotonicStack(intArrayOf(73, 74, 75, 71, 69, 72, 76, 73)).toList()}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each day, scan forward for warmer day.
 *
 * Time Complexity:  O(N²).
 * Space Complexity: O(N) — result.
 */
fun dailyTemperaturesBruteForce(temperatures: IntArray): IntArray {
    val result = IntArray(temperatures.size)
    for (i in temperatures.indices) {
        for (j in i + 1 until temperatures.size) {
            if (temperatures[j] > temperatures[i]) {
                result[i] = j - i
                break
            }
        }
    }
    return result
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MONOTONIC STACK — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MONOTONIC DECREASING STACK — Stack stores indices of days waiting for warmer temp.
 * When we find a warmer day, pop all colder days and calculate their answer.
 *
 * Core Idea:
 *   - Stack maintains indices in decreasing temperature order.
 *   - When current temp > stack top's temp → pop, result = current - popped index.
 *   - Push current index.
 *
 * Key Insight:
 *   - We only need to find the NEXT warmer day. The stack remembers days still waiting.
 *
 * Time Complexity:  O(N) — each element pushed/popped once.
 * Space Complexity: O(N) — stack.
 */
fun dailyTemperaturesMonotonicStack(temperatures: IntArray): IntArray {
    val result = IntArray(temperatures.size)
    val stack = ArrayDeque<Int>()  // stores indices, decreasing temperatures

    for (i in temperatures.indices) {
        while (stack.isNotEmpty() && temperatures[i] > temperatures[stack.last()]) {
            val prevIdx = stack.removeLast()
            result[prevIdx] = i - prevIdx
        }
        stack.addLast(i)
    }
    return result
}
