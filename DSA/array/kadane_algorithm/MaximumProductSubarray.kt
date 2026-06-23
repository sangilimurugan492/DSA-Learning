package array.kadane_algorithm

/**
 * https://leetcode.com/problems/maximum-product-subarray/
 *
 * Given an integer array nums, find the subarray that has the largest product, and return the product.
 *
 * Example 1:
 *
 * Input: nums = [2,3,-2,4]
 * Output: 6
 * Explanation: [2,3] has the largest product 6.
 *
 * Example 2:
 *
 * Input: nums = [-2,0,-1]
 * Output: 0
 * Explanation: The result cannot be 2, because [-2,-1] is not a subarray.
 *
 * Example 3:
 *
 * Input: nums = [-2,3,-4]
 * Output: 24
 * Explanation: [-2,3,-4] has product 24 (two negatives make a positive!)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Amazon, Google, Meta)
 *
 * Key Insight: Unlike Kadane's (max sum), we must track BOTH max and min at each index.
 * Why? Because a negative number * negative min = positive max!
 * e.g., min = -3, current = -2 → product = 6 (could be new max!)
 */
fun main() {
    println(maxProductSubarrayBF(intArrayOf(2, 3, -2, 4)))
    println(maxProductSubarrayOP(intArrayOf(2, 3, -2, 4)))
    println(maxProductSubarrayOP(intArrayOf(-2, 3, -4)))
    println(maxProductSubarrayOP(intArrayOf(-2, 0, -1)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun maxProductSubarrayBF(nums: IntArray): Int {
    var maxProduct = Int.MIN_VALUE
    for (i in nums.indices) {
        var product = 1
        for (j in i until nums.size) {
            product *= nums[j]
            maxProduct = maxOf(maxProduct, product)
        }
    }
    return maxProduct
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: Track both max and min at each position.
 * At each step, the new max = max(nums[i], maxSoFar * nums[i], minSoFar * nums[i])
 * At each step, the new min = min(nums[i], maxSoFar * nums[i], minSoFar * nums[i])
 *
 * Why track min? Because a very small negative * another negative = large positive!
 *
 * Trace for [-2, 3, -4]:
 * i=0: max=-2, min=-2, result=-2
 * i=1: max=max(3, -2*3, -2*3)=3, min=min(3, -2*3, -2*3)=-6, result=3
 * i=2: max=max(-4, 3*-4, -6*-4)=24, min=min(-4, 3*-4, -6*-4)=-12, result=24
 */
fun maxProductSubarrayOP(nums: IntArray): Int {
    var maxSoFar = nums[0]
    var minSoFar = nums[0]
    var result = nums[0]

    for (i in 1 until nums.size) {
        val current = nums[i]
        // Calculate both before updating (we need old maxSoFar for min calculation)
        val tempMax = maxOf(current, maxSoFar * current, minSoFar * current)
        minSoFar = minOf(current, maxSoFar * current, minSoFar * current)
        maxSoFar = tempMax

        result = maxOf(result, maxSoFar)
    }

    return result
}
