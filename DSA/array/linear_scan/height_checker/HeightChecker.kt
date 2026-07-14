package array.linear_scan.height_checker

/**
 * https://leetcode.com/problems/height-checker/description/
 *
 * A school is trying to take an annual photo of all the students. The students are asked to stand in a single file line in non-decreasing order by height. Let this ordering be represented by the integer array expected where expected[i] is the expected height of the ith student in line.
 *
 * You are given an integer array heights representing the current order that the students are standing in. Each heights[i] is the height of the ith student in line (0-indexed).
 *
 * Return the number of indices where heights[i] != expected[i].
 *
 *
 *
 * Example 1:
 *
 * Input: heights = [1,1,4,2,1,3]
 * Output: 3
 * Explanation:
 * heights:  [1,1,4,2,1,3]
 * expected: [1,1,1,2,3,4]
 * Indices 2, 4, and 5 do not match.
 * Example 2:
 *
 * Input: heights = [5,1,2,3,4]
 * Output: 5
 * Explanation:
 * heights:  [5,1,2,3,4]
 * expected: [1,2,3,4,5]
 * All indices do not match.
 */
fun main() {
    println(heightCheckerBF(intArrayOf(1,1,4,2,1,3)))
    println(heightCheckerOP(intArrayOf(1,1,4,2,1,3)))
}

/**
 * TIme Complexity O(N Log N)
 * Space Complexity O(N)
 */

fun heightCheckerBF(heights: IntArray) : Int {
    val expected = heights.copyOf()
    expected.sort()

    var count = 0
    for (i in heights.indices) {
        if (heights[i] != expected[i]) {
            count++
        }
    }
    return count
}

/**
 * Time Complexity O(N) -> O(n + k) -> K relatively small so we ignore k
 * Space Complexity O(N)
 */

fun heightCheckerOP(heights: IntArray): Int {
    val counts = IntArray(101)
    for (h in heights) {
        counts[h]++
    }

    var mismatchCount = 0
    var currentHeight = 0

    for (h in heights) {
        while (counts[currentHeight] == 0) {
            currentHeight++
        }

        if (h != currentHeight) {
            mismatchCount++
        }

        counts[currentHeight]--
    }

    return mismatchCount
}