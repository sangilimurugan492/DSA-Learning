package array.kadane_algorithm.maximum_sub_array

/**
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Given an integer array nums, find the subarray with the largest sum.
 *
 * Example 1:
 * Input: nums = [-2,1,-3,4,-1,2,1,-5,4] → Output: 6 (subarray [4,-1,2,1])
 * Example 2:
 * Input: nums = [5,4,-1,7,8] → Output: 23 (entire array)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Kadane's — asked everywhere)
 */

fun main() {
    println(maxSubArrayBruteForce(intArrayOf(-2, 1, -3, 4, -1, 2, 1, -5, 4)))
    println(maxSubArrayBruteForce(intArrayOf(5, 4, -1, 7, 8)))
    println("---")
    println(maxSubArrayKadane(intArrayOf(-2, 1, -3, 4, -1, 2, 1, -5, 4)))
    println(maxSubArrayKadane(intArrayOf(5, 4, -1, 7, 8)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — try every subarray
 * Space Complexity: O(1)
 *
 * For each starting index i, accumulate sum for all ending indices j.
 * Track maximum sum seen.
 *
 * Trace for [-2,1,-3,4,-1,2,1,-5,4]:
 * i=0: -2, -2+1=-1, -1-3=-4, -4+4=0, 0-1=-1, -1+2=1, 1+1=2, 2-5=-3, -3+4=1 → max=2
 * i=3: 4, 4-1=3, 3+2=5, 5+1=6, 6-5=1, 1+4=5 → max=6 ✅
 * Total: max = 6
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

/**
 * OPTIMAL — Kadane's Algorithm
 * Time Complexity: O(N) — single pass
 * Space Complexity: O(1)
 *
 * At each index, decide: extend previous subarray OR start fresh from here?
 * localMax = max(nums[i], localMax + nums[i])
 *
 * Trace for [-2,1,-3,4,-1,2,1,-5,4]:
 * i=0: local=max(-2, -2)= -2, global=-2
 * i=1: local=max(1, -2+1)= 1,  global= 1  ← start fresh!
 * i=2: local=max(-3, 1-3)= -2, global= 1
 * i=3: local=max(4, -2+4)= 4,  global= 4  ← start fresh!
 * i=4: local=max(-1, 4-1)= 3,  global= 4
 * i=5: local=max(2, 3+2)= 5,   global= 5
 * i=6: local=max(1, 5+1)= 6,   global= 6  ← answer!
 * i=7: local=max(-5, 6-5)= 1,  global= 6
 * i=8: local=max(4, 1+4)= 5,   global= 6
 * Result: 6 ✅
 */
fun maxSubArrayKadane(nums: IntArray): Int {
    var localMax = nums[0]
    var globalMax = nums[0]

    for (i in 1 until nums.size) {
        localMax = maxOf(nums[i], localMax + nums[i])
        globalMax = maxOf(globalMax, localMax)
    }

    return globalMax
}
