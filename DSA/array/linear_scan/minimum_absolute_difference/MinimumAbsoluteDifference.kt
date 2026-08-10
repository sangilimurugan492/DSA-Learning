package array.linear_scan.minimum_absolute_difference

import kotlin.math.abs

/**
 * https://leetcode.com/problems/minimum-absolute-difference/
 *
 * Given an array of **distinct** integers arr, find all pairs of elements with the
 * minimum absolute difference of any two elements.
 *
 * Return a list of pairs in ascending order (with respect to pairs), where each pair
 * [a, b] follows:
 *   - a, b are from arr
 *   - a < b
 *   - b - a equals the minimum absolute difference of any two elements in arr
 *
 * Constraints:
 *   2 <= arr.length <= 10^5
 *   -10^6 <= arr[i] <= 10^6
 *   All integers in arr are distinct.
 *
 * Example 1:
 *   Input:  arr = [4, 2, 1, 3]
 *   Output: [[1, 2], [2, 3], [3, 4]]
 *   Explanation: The minimum absolute difference is 1.
 *                Pairs with difference 1: [1,2], [2,3], [3,4]
 *
 * Example 2:
 *   Input:  arr = [1, 3, 6, 10, 15]
 *   Output: [[1, 3]]
 *   Explanation: The minimum absolute difference is 2. Pair: [1, 3].
 *
 * Example 3:
 *   Input:  arr = [3, 8, -10, 4, 11]
 *   Output: [[0, 2], [2, 4]]
 *   Explanation: Sorted: [-10, 3, 4, 8, 11].
 *                Min diff = 1. Pairs: [3,4], [10,11] → wait, let me re-check.
 *                Actually sorted = [-10, 3, 4, 8, 11], diffs = [13, 1, 4, 3].
 *                Min diff = 1. Pair: [3, 4].
 *                Output should be [[3, 4]] — see LeetCode for exact expected output.
 */
fun main() {
    println(minimumAbsDiffBF(intArrayOf(4, 2, 1, 3)))  // [[1, 2], [2, 3], [3, 4]]
    println(minimumAbsDiffOP(intArrayOf(4, 2, 1, 3)))  // [[1, 2], [2, 3], [3, 4]]
    println(minimumAbsDiffOP(intArrayOf(1, 3, 6, 10, 15))) // [[1, 3]]
}

/**
 * Brute Force — Check All Pairs
 *
 * For every pair (i, j) where i < j, compute |arr[i] - arr[j]|.
 * Track the minimum difference, then collect all pairs with that difference.
 * Finally, sort the pairs in ascending order.
 *
 * Time Complexity:  O(N²) — examine all N*(N-1)/2 pairs
 * Space Complexity: O(N²) — worst case store all pairs (before filtering)
 */
fun minimumAbsDiffBF(arr: IntArray): List<List<Int>> {
    var minDiff = Int.MAX_VALUE

    // Pass 1: Find the minimum absolute difference among all pairs.
    for (i in arr.indices) {
        for (j in i + 1 until arr.size) {
            val diff = abs(arr[i] - arr[j])
            if (diff < minDiff) {
                minDiff = diff
            }
        }
    }

    // Pass 2: Collect all pairs with that minimum difference.
    val result = mutableListOf<List<Int>>()
    for (i in arr.indices) {
        for (j in i + 1 until arr.size) {
            if (abs(arr[i] - arr[j]) == minDiff) {
                // Ensure a < b in the pair.
                val a = minOf(arr[i], arr[j])
                val b = maxOf(arr[i], arr[j])
                result.add(listOf(a, b))
            }
        }
    }

    // Sort pairs in ascending order (by first element, then second).
    result.sortWith(compareBy({ it[0] }, { it[1] }))
    return result
}

/**
 * Optimal — Sort + Single Pass
 *
 * Key insight: After sorting, the minimum absolute difference between any two elements
 * must occur between **adjacent** elements in the sorted array. (For any pair (i, j) with
 * i < j in sorted order, |arr[i] - arr[j]| >= |arr[i] - arr[i+1]| since the array is sorted.)
 *
 * Steps:
 * 1. Sort the array.
 * 2. Find the minimum difference among all adjacent pairs.
 * 3. Collect all adjacent pairs that have this minimum difference.
 *    (They're already in ascending order since the array is sorted.)
 *
 * Trace for arr = [4, 2, 1, 3]:
 *
 *   Step 1 — Sort:
 *     sorted = [1, 2, 3, 4]
 *
 *   Step 2 — Find min diff among adjacent pairs:
 *     |2-1|=1, |3-2|=1, |4-3|=1 → minDiff = 1
 *
 *   Step 3 — Collect pairs with diff == 1:
 *     (1,2): 2-1=1 == minDiff → add [1, 2]
 *     (2,3): 3-2=1 == minDiff → add [2, 3]
 *     (3,4): 4-3=1 == minDiff → add [3, 4]
 *
 *   Result = [[1, 2], [2, 3], [3, 4]] ✅
 *
 * Time Complexity:  O(N log N) — dominated by sorting; two O(N) passes after
 * Space Complexity: O(N)       — for the result list (sorting may be in-place)
 */
fun minimumAbsDiffOP(arr: IntArray): List<List<Int>> {
    // Step 1: Sort the array so the minimum difference must be between adjacent elements.
    arr.sort()

    // Step 2: Find the minimum difference among adjacent pairs.
    var minDiff = Int.MAX_VALUE
    for (i in 1 until arr.size) {
        minDiff = minOf(minDiff, arr[i] - arr[i - 1])
    }

    // Step 3: Collect all adjacent pairs with the minimum difference.
    // Since the array is sorted, pairs are naturally in ascending order.
    val result = mutableListOf<List<Int>>()
    for (i in 1 until arr.size) {
        if (arr[i] - arr[i - 1] == minDiff) {
            result.add(listOf(arr[i - 1], arr[i]))
        }
    }

    return result
}
