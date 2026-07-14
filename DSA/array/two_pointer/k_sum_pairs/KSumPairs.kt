package array.two_pointer.k_sum_pairs

/**
 * https://leetcode.com/problems/k-sum-pairs/
 *
 * Given an array of integers nums and an integer k, return the maximum number of
 * operations you can perform where each operation picks two numbers whose sum equals k
 * and removes them from the array.
 *
 * Example 1:
 *
 * Input: nums = [1,2,3,4], k = 5
 * Output: 2
 * Explanation: (1,4) and (2,3) → 2 operations
 *
 * Example 2:
 *
 * Input: nums = [3,1,3,4,3], k = 6
 * Output: 1
 * Explanation: (3,3) → only one pair, remaining [1,4,3] can't form sum=6
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google)
 *
 * Key Insight: Sort + two pointers from both ends. If sum == k → count++, move both.
 * If sum < k → move left (increase). If sum > k → move right (decrease).
 */
fun main() {
    println(maxOperations(intArrayOf(1, 2, 3, 4), 5))
    println(maxOperations(intArrayOf(3, 1, 3, 4, 3), 6))
}

/**
 * Time Complexity O(N log N)
 * Space Complexity O(1)
 */
fun maxOperations(nums: IntArray, k: Int): Int {
    nums.sort()
    var left = 0
    var right = nums.size - 1
    var operations = 0

    while (left < right) {
        val sum = nums[left] + nums[right]
        when {
            sum == k -> {
                operations++
                left++
                right--
            }
            sum < k -> left++
            else -> right--
        }
    }

    return operations
}
