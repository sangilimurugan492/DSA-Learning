package array.complement_search.two_sum_ii

/**
 * https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/
 *
 * Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order,
 * find two numbers such that they add up to a specific target number.
 * Return the indices (1-indexed) of the two numbers.
 *
 * Example 1:
 *
 * Input: numbers = [2,7,11,15], target = 9
 * Output: [1,2]
 * Explanation: 2 + 7 = 9
 *
 * Example 2:
 *
 * Input: numbers = [2,3,4], target = 6
 * Output: [1,3]
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Key Insight: Since array is sorted, use two pointers from both ends.
 * If sum < target → move left (increase sum). If sum > target → move right (decrease sum).
 * This avoids the O(N) space of HashMap approach.
 */
fun main() {
    println(twoSumII(intArrayOf(2, 7, 11, 15), 9).toList())
    println(twoSumII(intArrayOf(2, 3, 4), 6).toList())
    println(twoSumII(intArrayOf(-1, 0), -1).toList())

    println(twoSumIIBF(intArrayOf(2, 7, 11, 15), 9).toList())
    println(twoSumIIBF(intArrayOf(2, 3, 4), 6).toList())
    println(twoSumIIBF(intArrayOf(-1, 0), -1).toList())
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun twoSumIIBF(numbers: IntArray, target: Int): IntArray {
    for(i in numbers.indices) {
        for(j in i+1 until numbers.size) {
            if(numbers[i] + numbers[j] == target) return intArrayOf(i + 1, j + 1)
        }
    }

    return intArrayOf(-1, -1)
}


/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun twoSumII(numbers: IntArray, target: Int): IntArray {
    var left = 0
    var right = numbers.size - 1

    while (left < right) {
        val sum = numbers[left] + numbers[right]
        when {
            sum == target -> return intArrayOf(left + 1, right + 1)
            sum < target -> left++
            else -> right--
        }
    }

    return intArrayOf(-1, -1)
}
