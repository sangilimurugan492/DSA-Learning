package array.linear_scan.height_checker

/**
 * https://leetcode.com/problems/height-checker/description/
 *
 * A school is trying to take an annual photo of all the students. The students are asked to
 * stand in a single file line in non-decreasing order by height. Let this ordering be represented
 * by the integer array expected where expected[i] is the expected height of the ith student in line.
 *
 * You are given an integer array heights representing the current order that the students are
 * standing in. Each heights[i] is the height of the ith student in line (0-indexed).
 *
 * Return the number of indices where heights[i] != expected[i].
 *
 * Constraints:
 *   1 <= heights.length <= 100
 *   1 <= heights[i] <= 100
 *
 * Example 1:
 *   Input: heights = [1,1,4,2,1,3]
 *   Output: 3
 *   Explanation:
 *     heights:  [1,1,4,2,1,3]
 *     expected: [1,1,1,2,3,4]
 *     Indices 2, 4, and 5 do not match.
 *
 * Example 2:
 *   Input: heights = [5,1,2,3,4]
 *   Output: 5
 *   Explanation:
 *     heights:  [5,1,2,3,4]
 *     expected: [1,2,3,4,5]
 *     All indices do not match.
 */
fun main() {
    println(heightCheckerBF(intArrayOf(1, 1, 4, 2, 1, 3))) // 3
    println(heightCheckerOP(intArrayOf(1, 1, 4, 2, 1, 3))) // 3
    println(heightCheckerOP(intArrayOf(5, 1, 2, 3, 4)))     // 5
}

/**
 * Brute Force — Sort and Compare
 *
 * 1. Create a sorted copy of `heights` → this is the `expected` array.
 * 2. Compare element-by-element; count mismatches.
 *
 * Time Complexity:  O(N log N)  — dominated by sorting
 * Space Complexity: O(N)        — for the sorted copy
 */
fun heightCheckerBF(heights: IntArray): Int {
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
 * Optimal — Counting Sort (no explicit sort needed)
 *
 * Key insight: heights are in the range [1, 100], so we can use a frequency array
 * of size 101 to count occurrences of each height. We then walk through the original
 * `heights` array and use a pointer (`currentHeight`) to track what the next expected
 * height should be in sorted order, comparing on the fly without building the sorted array.
 *
 * Steps:
 * 1. Build a frequency array `counts` where counts[h] = number of students with height h.
 * 2. Iterate through each height in the original array:
 *    a. Advance `currentHeight` until we find a height that still has a remaining count > 0.
 *       (This simulates reading the sorted array left-to-right.)
 *    b. If the actual height at this position differs from `currentHeight`, it's a mismatch.
 *    c. Decrement counts[currentHeight] (we've "placed" one student of this height).
 * 3. Return the total mismatch count.
 *
 * Time Complexity:  O(N + K)  where K = 100 (max height) → effectively O(N)
 * Space Complexity: O(K)     where K = 100 → effectively O(1)
 */
fun heightCheckerOP(heights: IntArray): Int {
    // Frequency array: index = height, value = count of students with that height.
    // Size 101 because heights range from 1 to 100 (index 0 is unused).
    val counts = IntArray(101)
    for (h in heights) {
        counts[h]++
    }

    var mismatchCount = 0
    var currentHeight = 0  // pointer into the "sorted" sequence

    for (h in heights) {
        // Skip heights that have been fully consumed (count == 0).
        while (counts[currentHeight] == 0) {
            currentHeight++
        }

        // currentHeight is now the expected height at this position in sorted order.
        if (h != currentHeight) {
            mismatchCount++
        }

        // Consume one occurrence of currentHeight.
        counts[currentHeight]--
    }

    return mismatchCount
}
