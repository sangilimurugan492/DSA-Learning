package array.two_pointer.two_sum_level_two_with_sorted_array

/**
 * https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/description/
 *
 * Given a 1-indexed sorted array `numbers` and a `target`, return the 1-indexed
 * indices of the two numbers that add up to target.
 *
 * Example: numbers = [2,7,11,15], target = 9 → [1,2]
 *
 * Key Idea: Since the array is sorted, use two pointers from both ends.
 * If sum < target → move left (increase sum). If sum > target → move right (decrease sum).
 *
 * Time Complexity:  O(N)
 * Space Complexity: O(1)
 */
fun main() {
    println("Brute Force Approach")
    twoSumLevelTwoWithSortedArrayBF(intArrayOf(2, 7, 11, 15), 9).forEach { println(it) }
    println("Optimal Approach (Two Pointer)")
    twoSumLevelTwoWithSortedArrayOP(intArrayOf(2, 7, 11, 15), 9).forEach { println(it) }
}

/**
 * Brute Force: Try every pair.
 *
 * Time Complexity:  O(N²)
 * Space Complexity: O(1)
 */
fun twoSumLevelTwoWithSortedArrayBF(numbers: IntArray, target: Int): IntArray {
    for (i in numbers.indices) {
        for (j in i + 1 until numbers.size) {
            if (target == numbers[i] + numbers[j]) {
                return intArrayOf(i + 1, j + 1) // 1-indexed
            }
        }
    }
    return intArrayOf()
}

/**
 * Optimal (Two Pointer): Since the array is sorted, start from both ends.
 * - sum < target → left++ (need a bigger sum)
 * - sum > target → right-- (need a smaller sum)
 * - sum == target → return [left+1, right+1] (1-indexed)
 *
 * Time Complexity:  O(N)
 * Space Complexity: O(1)
 */
fun twoSumLevelTwoWithSortedArrayOP(numbers: IntArray, target: Int): IntArray {
    var left = 0
    var right = numbers.size - 1

    while (left < right) {
        val sum = numbers[left] + numbers[right]
        when {
            sum > target -> right--
            sum < target -> left++
            else -> return intArrayOf(left + 1, right + 1) // 1-indexed
        }
    }

    return intArrayOf()
}
