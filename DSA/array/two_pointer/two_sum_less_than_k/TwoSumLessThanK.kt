package array.two_pointer.two_sum_less_than_k

/**
 * https://leetcode.com/problems/two-sum-less-than-k/
 *
 * Given an array nums and integer k, return the maximum sum of a pair such that
 * sum < k. If no such pair exists, return -1.
 *
 * Example 1:
 *
 * Input: nums = [34,23,1,24,75,33,54,8], k = 60
 * Output: 58
 * Explanation: 34 + 24 = 58, which is the max sum < 60
 *
 * Example 2:
 *
 * Input: nums = [10,20,30], k = 15
 * Output: -1
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google)
 *
 * Two approaches:
 * 1. Brute Force: Try every pair, track max sum < k
 * 2. Sort + Two Pointer: Pair smallest with largest
 */
fun main() {
    println("Brute Force:")
    println(twoSumLessThanKBF(intArrayOf(34, 23, 1, 24, 75, 33, 54, 8), 60))  // 58
    println(twoSumLessThanKBF(intArrayOf(10, 20, 30), 15))                     // -1
    println("Sort + Two Pointer:")
    println(twoSumLessThanK(intArrayOf(34, 23, 1, 24, 75, 33, 54, 8), 60))    // 58
    println(twoSumLessThanK(intArrayOf(10, 20, 30), 15))                       // -1
}

/**
 * Brute Force (Nested Loops): Try every pair (i, j) where i < j.
 * If nums[i] + nums[j] < k, track the maximum such sum.
 *
 * Step-by-step:
 * 1. Initialize maxSum = -1 (no valid pair found yet).
 * 2. For each i from 0 to n-1:
 *    a. For each j from i+1 to n-1:
 *       - Compute sum = nums[i] + nums[j].
 *       - If sum < k and sum > maxSum → update maxSum.
 * 3. Return maxSum.
 *
 * Walkthrough: nums = [34,23,1,24,75,33,54,8], k = 60
 *
 *   i=0(34): j=1(23)→57<60 maxSum=57, j=2(1)→35, j=3(24)→58<60 maxSum=58,
 *             j=4(75)→109, j=5(33)→57, j=6(54)→88, j=7(8)→42
 *   i=1(23): j=2(1)→24, j=3(24)→47, j=5(33)→56, j=7(8)→31 → no improvement
 *   ... (no pair beats 58)
 *
 * Result: 58 ✅
 *
 * Time Complexity:  O(N²) — nested loops checking all pairs
 * Space Complexity: O(1)
 */
fun twoSumLessThanKBF(nums: IntArray, k: Int): Int {
    var maxSum = -1

    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            val sum = nums[i] + nums[j]
            if (sum < k && sum > maxSum) {
                maxSum = sum
            }
        }
    }

    return maxSum
}

/**
 * Sort + Two Pointer (Optimal): Sort, then use two pointers from both ends.
 *
 * Step-by-step:
 * 1. Sort the array.
 * 2. Set left = 0, right = last index, maxSum = -1.
 * 3. While left < right:
 *    a. sum = nums[left] + nums[right]
 *    b. If sum < k → track max, left++ (try bigger)
 *    c. If sum >= k → right-- (try smaller)
 * 4. Return maxSum.
 *
 * Time Complexity:  O(N log N) — dominated by sorting
 * Space Complexity: O(1)
 */
fun twoSumLessThanK(nums: IntArray, k: Int): Int {
    nums.sort()
    var left = 0
    var right = nums.size - 1
    var maxSum = -1

    while (left < right) {
        val sum = nums[left] + nums[right]
        if (sum < k) {
            maxSum = maxOf(maxSum, sum)
            left++
        } else {
            right--
        }
    }

    return maxSum
}
