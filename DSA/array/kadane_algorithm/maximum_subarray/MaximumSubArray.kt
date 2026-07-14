package array.kadane_algorithm.maximum_subarray

/**
 * Maximum Subarray — LeetCode #53
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Problem:
 * -------
 * Given an integer array nums, find the contiguous subarray (containing at least
 * one element) which has the largest sum. Return its sum.
 *
 * Example 1:  nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]  →  6  (subarray [4, -1, 2, 1])
 * Example 2:  nums = [5, 4, -1, 7, 8]                  →  23 (entire array)
 * Example 3:  nums = [1]                               →  1
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Kadane's — asked everywhere)
 */

fun main() {
    val test1 = intArrayOf(-2, 1, -3, 4, -1, 2, 1, -5, 4)
    val test2 = intArrayOf(5, 4, -1, 7, 8)
    val test3 = intArrayOf(1)

    println("=== Method 1: Brute Force ===")
    println("maxSubArray([-2,1,-3,4,-1,2,1,-5,4]) = ${maxSubArrayBruteForce(test1)}")
    println("maxSubArray([5,4,-1,7,8]) = ${maxSubArrayBruteForce(test2)}")
    println("maxSubArray([1]) = ${maxSubArrayBruteForce(test3)}")

    println("\n=== Method 2: Kadane's Algorithm (Optimal) ===")
    println("maxSubArray([-2,1,-3,4,-1,2,1,-5,4]) = ${maxSubArrayKadane(test1)}")
    println("maxSubArray([5,4,-1,7,8]) = ${maxSubArrayKadane(test2)}")
    println("maxSubArray([1]) = ${maxSubArrayKadane(test3)}")

    println("\n=== Step-by-step trace (Kadane's) ===")
    maxSubArrayTrace(test1)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Try Every Subarray
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each starting index, accumulate sum for all ending indices.
 *
 * Core Idea:
 *   - For each start index i, try all end indices j ≥ i.
 *   - Accumulate the running sum and track the maximum.
 *
 * Time Complexity:  O(N²) — nested loops.
 * Space Complexity: O(1).
 */
fun maxSubArrayBruteForce(nums: IntArray): Int {
    var maxSum = Int.MIN_VALUE

    for (i in nums.indices) {
        var currentSum = 0
        for (j in i until nums.size) {
            currentSum += nums[j]
            maxSum = maxOf(maxSum, currentSum)
        }
    }

    return maxSum
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: KADANE'S ALGORITHM (OPTIMAL)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * KADANE'S ALGORITHM — At each index, decide: extend or start fresh?
 *
 * Core Idea:
 *   - localMax = max(nums[i], localMax + nums[i])
 *   - If extending the previous subarray (localMax + nums[i]) is worse than
 *     starting fresh (nums[i]), then start fresh.
 *   - globalMax tracks the best localMax seen so far.
 *
 * Key Insight:
 *   - If the running sum becomes negative, it can only hurt future subarrays.
 *   - So we "reset" by starting fresh from the current element.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1).
 */
fun maxSubArrayKadane(nums: IntArray): Int {
    // Initialize with the first element.
    var localMax = nums[0]   // Best sum ending at current index.
    var globalMax = nums[0]  // Best sum overall.

    for (i in 1 until nums.size) {
        // Decision: extend previous subarray OR start fresh from here?
        localMax = maxOf(nums[i], localMax + nums[i])
        // Update global max if we found a better sum.
        globalMax = maxOf(globalMax, localMax)
    }

    return globalMax
}

/**
 * Kadane's with step-by-step trace for learning/debugging.
 */
fun maxSubArrayTrace(nums: IntArray) {
    println("Input: ${nums.toList()}")
    var localMax = nums[0]
    var globalMax = nums[0]
    println("  i=0 | nums[i]=${nums[0]} | localMax=$localMax | globalMax=$globalMax (initial)")

    for (i in 1 until nums.size) {
        val extend = localMax + nums[i]
        localMax = maxOf(nums[i], extend)
        globalMax = maxOf(globalMax, localMax)
        val choice = if (nums[i] >= extend) "START FRESH" else "EXTEND"
        println("  i=$i | nums[i]=${nums[i]} | extend=$extend | localMax=$localMax | globalMax=$globalMax | $choice")
    }
    println("  Result: $globalMax")
}
