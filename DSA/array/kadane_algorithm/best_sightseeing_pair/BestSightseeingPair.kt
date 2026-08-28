package array.kadane_algorithm.best_sightseeing_pair

/**
 * https://leetcode.com/problems/best-sightseeing-pair/
 *
 * You are given an integer array values where values[i] represents the score of the i-th sightseeing spot.
 * The score of a pair (i < j) is: values[i] + values[j] + i - j
 * (i.e., the sum of the two spot values minus the distance between them).
 *
 * Return the maximum score of any pair.
 *
 * Example 1:
 * Input: values = [8,1,5,2,6] → Output: 11
 *   Pair (0,2): 8 + 5 + 0 - 2 = 11
 *
 * Example 2:
 * Input: values = [1,2] → Output: 2
 *   Pair (0,1): 1 + 2 + 0 - 1 = 2
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Kadane-style DP — tests ability to decompose a formula)
 */

fun main() {
    println(bestSightseeingPairBruteForce(intArrayOf(8, 1, 5, 2, 6)))
    println(bestSightseeingPairBruteForce(intArrayOf(1, 2)))
    println("---")
    println(bestSightseeingPairOptimal(intArrayOf(8, 1, 5, 2, 6)))
    println(bestSightseeingPairOptimal(intArrayOf(1, 2)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — try every pair (i, j)
 * Space Complexity: O(1)
 *
 * For each pair i < j, compute values[i] + values[j] + i - j and track the max.
 *
 * Trace for [8,1,5,2,6]:
 * (0,1): 8+1+0-1=8
 * (0,2): 8+5+0-2=11 ← max!
 * (0,3): 8+2+0-3=7
 * (0,4): 8+6+0-4=10
 * (1,2): 1+5+1-2=5
 * (2,3): 5+2+2-3=6
 * (3,4): 2+6+3-4=7
 * Result: 11 ✅
 */
fun bestSightseeingPairBruteForce(values: IntArray): Int {
    var maxScore = Int.MIN_VALUE
    for (i in values.indices) {
        for (j in i + 1 until values.size) {
            val score = values[i] + values[j] + i - j
            maxScore = maxOf(maxScore, score)
        }
    }
    return maxScore
}

/**
 * OPTIMAL — Kadane-style single pass
 * Time Complexity: O(N) — single pass
 * Space Complexity: O(1)
 *
 * KEY INSIGHT — Decompose the score formula:
 *   score(i, j) = values[i] + values[j] + i - j
 *               = (values[i] + i) + (values[j] - j)
 *
 * As we iterate j from left to right:
 *   - We need the best (values[i] + i) for all i < j seen so far.
 *   - At each j, the best score = maxSoFar + (values[j] - j)
 *     where maxSoFar = max(values[i] + i) for i in [0, j-1].
 *   - Then update maxSoFar with (values[j] + j) for future j's.
 *
 * Trace for [8,1,5,2,6]:
 * j=0: maxSoFar = 8+0 = 8  (no pair yet)
 * j=1: score = 8 + (1-1) = 8,  maxScore=8,  maxSoFar = max(8, 1+1=2) = 8
 * j=2: score = 8 + (5-2) = 11, maxScore=11, maxSoFar = max(8, 5+2=7) = 8
 * j=3: score = 8 + (2-3) = 7,  maxScore=11, maxSoFar = max(8, 2+3=5) = 8
 * j=4: score = 8 + (6-4) = 10, maxScore=11, maxSoFar = max(8, 6+4=10) = 10
 * Result: 11 ✅
 */
fun bestSightseeingPairOptimal(values: IntArray): Int {
    var maxSoFar = values[0] + 0  // best (values[i] + i) seen so far
    var maxScore = Int.MIN_VALUE

    for (j in 1 until values.size) {
        // Best pair ending at j: maxSoFar + (values[j] - j)
        maxScore = maxOf(maxScore, maxSoFar + values[j] - j)
        // Update maxSoFar to include current index for future j's
        maxSoFar = maxOf(maxSoFar, values[j] + j)
    }

    return maxScore
}
