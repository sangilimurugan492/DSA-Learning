package array.prefix_sum.product_of_array_except_self

/**
 * https://leetcode.com/problems/product-of-array-except-self/
 *
 * Given an integer array nums, return an array where output[i] = product of all
 * elements except nums[i]. Must run in O(N) without division.
 *
 * Example 1:
 * Input: nums = [1,2,3,4] → Output: [24,12,8,6]
 * Example 2:
 * Input: nums = [-1,1,0,-3,3] → Output: [0,0,9,0,0]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (One of the MOST asked FAANG questions)
 */

fun main() {
    println(productExceptSelfBruteForce(intArrayOf(1, 2, 3, 4)).toList())
    println(productExceptSelfBruteForce(intArrayOf(-1, 1, 0, -3, 3)).toList())
    println("---")
    println(productExceptSelfWithDivision(intArrayOf(1, 2, 3, 4)).toList())
    println("---")
    println(productExceptSelfOptimal(intArrayOf(1, 2, 3, 4)).toList())
    println(productExceptSelfOptimal(intArrayOf(-1, 1, 0, -3, 3)).toList())
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — for each element, multiply all others
 * Space Complexity: O(N) — output array
 *
 * For each index i, compute product of all elements except nums[i].
 * Simple but too slow for large inputs.
 */
fun productExceptSelfBruteForce(nums: IntArray): IntArray {
    val result = IntArray(nums.size)
    for (i in nums.indices) {
        var product = 1
        for (j in nums.indices) {
            if (i != j) product *= nums[j]
        }
        result[i] = product
    }
    return result
}

/**
 * BETTER — Using Division (but fails with zeros!)
 * Time Complexity: O(N)
 * Space Complexity: O(N)
 *
 * Compute total product, then divide by each element.
 * PROBLEM: Fails when array contains zeros (can't divide by zero).
 */
fun productExceptSelfWithDivision(nums: IntArray): IntArray {
    val totalProduct = nums.reduce { acc, num -> acc * num }
    return nums.map { totalProduct / it }.toIntArray()
}

/**
 * OPTIMAL — Prefix + Suffix Products (O(1) extra space)
 * Time Complexity: O(N) — two passes
 * Space Complexity: O(1) — output array doesn't count as extra space
 *
 * Use output array for prefix products, then multiply with suffix running variable.
 *
 * Trace for [1,2,3,4]:
 * Pass 1 (prefix left→right):
 *   result = [1, 1, 2, 6]  (running prefix product)
 * Pass 2 (suffix right→left, running=1):
 *   i=3: result[3]=6*1=6,  running=4
 *   i=2: result[2]=2*4=8,  running=3*4=12
 *   i=1: result[1]=1*12=12, running=2*12=24
 *   i=0: result[0]=1*24=24
 * Result: [24,12,8,6] ✅
 */
fun productExceptSelfOptimal(nums: IntArray): IntArray {
    val n = nums.size
    val result = IntArray(n)

    // Pass 1: Prefix products
    result[0] = 1
    for (i in 1 until n) {
        result[i] = result[i - 1] * nums[i - 1]
    }

    // Pass 2: Suffix products (running variable)
    var suffix = 1
    for (i in n - 1 downTo 0) {
        result[i] *= suffix
        suffix *= nums[i]
    }

    return result
}
