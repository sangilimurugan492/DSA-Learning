package array.prefix_sum.subarray_sum_equals_k

/**
 * https://leetcode.com/problems/subarray-sum-equals-k/
 *
 * Given an integer array nums and integer k, return total number of subarrays
 * whose sum equals k.
 *
 * Example: nums = [1,1,1], k = 2 → Output: 2
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 most asked)
 */

fun main() {
    println(subarraySumBruteForce(intArrayOf(1, 1, 1), 2))
    println(subarraySumBruteForce(intArrayOf(1, 2, 3), 3))
    println("---")
    println(subarraySumPrefixHashMap(intArrayOf(1, 1, 1), 2))
    println(subarraySumPrefixHashMap(intArrayOf(1, 2, 3), 3))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — check every subarray
 * Space Complexity: O(1)
 *
 * For each starting index, accumulate sum and check if equals k.
 */
fun subarraySumBruteForce(nums: IntArray, k: Int): Int {
    var count = 0
    for (i in nums.indices) {
        var sum = 0
        for (j in i until nums.size) {
            sum += nums[j]
            if (sum == k) count++
        }
    }
    return count
}

/**
 * OPTIMAL — Prefix Sum + HashMap
 * Time Complexity: O(N) — single pass
 * Space Complexity: O(N) — HashMap
 *
 * Key insight: If prefix[j] - prefix[i] = k, then subarray [i+1..j] sums to k.
 * So for each j, count how many previous prefix sums equal prefix[j] - k.
 */
fun subarraySumPrefixHashMap(nums: IntArray, k: Int): Int {
    val prefixCount = hashMapOf(0 to 1)  // prefix 0 occurs once (empty subarray)
    var count = 0
    var prefixSum = 0

    for (num in nums) {
        prefixSum += num
        count += prefixCount.getOrDefault(prefixSum - k, 0)
        prefixCount[prefixSum] = prefixCount.getOrDefault(prefixSum, 0) + 1
    }
    return count
}
