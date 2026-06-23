package stack

/**
 * https://leetcode.com/problems/next-greater-element-ii/
 * Given circular array, find next greater element for every element.
 * Example: [1,2,1] → [2,-1,2] (circular: last 1 wraps to find 2)
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(nextGreaterElementsBruteForce(intArrayOf(1, 2, 1)).toList())
    println(nextGreaterElementsBruteForce(intArrayOf(5, 4, 3, 2, 1)).toList())
    println("---")
    println(nextGreaterElementsCircular(intArrayOf(1, 2, 1)).toList())
    println(nextGreaterElementsCircular(intArrayOf(5, 4, 3, 2, 1)).toList())
}

/**
 * BRUTE FORCE: O(N²) — for each element, scan circular array for next greater
 */
fun nextGreaterElementsBruteForce(nums: IntArray): IntArray {
    val n = nums.size
    val result = IntArray(n) { -1 }
    for (i in nums.indices) {
        for (j in 1 until n) {
            val idx = (i + j) % n
            if (nums[idx] > nums[i]) {
                result[i] = nums[idx]
                break
            }
        }
    }
    return result
}

/**
 * OPTIMAL: O(N) Monotonic Stack with circular traversal
 * Iterate 2×N using modulo. Stack stores indices in decreasing order.
 */
fun nextGreaterElementsCircular(nums: IntArray): IntArray {
    val n = nums.size
    val result = IntArray(n) { -1 }
    val stack = ArrayDeque<Int>()

    for (i in 0 until 2 * n) {
        val idx = i % n
        while (stack.isNotEmpty() && nums[idx] > nums[stack.last()]) {
            result[stack.removeLast()] = nums[idx]
        }
        if (i < n) stack.addLast(idx)
    }
    return result
}
