package stack.next_greater_element_ii

/**
 * Next Greater Element II — LeetCode #503
 * https://leetcode.com/problems/next-greater-element-ii/
 *
 * Problem:
 * -------
 * Given a circular array, find the next greater element for every element.
 * If no greater element exists, use -1.
 *
 * Example:  [1,2,1] → [2,-1,2]  (last 1 wraps around to find 2)
 *           [5,4,3,2,1] → [-1,5,5,5,5]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — for each element, scan circular array
 * 2. Monotonic Stack: O(N) — iterate 2×N using modulo
 */

fun main() {
    val nums = intArrayOf(1, 2, 1)

    println("=== Method 1: Brute Force ===")
    println("nextGreaterElements(${nums.toList()}) = ${nextGreaterElementsBruteForce(nums).toList()}")

    println("\n=== Method 2: Monotonic Stack ===")
    println("nextGreaterElements(${nums.toList()}) = ${nextGreaterElementsCircular(nums).toList()}")

    println("\n=== Step-by-step trace ===")
    nextGreaterElementsTrace(nums)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each element, scan the circular array for the next greater.
 *
 * Time Complexity:  O(N²) — nested loops.
 * Space Complexity: O(N) — result array.
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

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MONOTONIC STACK — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MONOTONIC STACK — Iterate 2×N using modulo. Stack stores indices in decreasing order.
 *
 * Core Idea:
 *   - Iterate 2×N times (simulating circular traversal).
 *   - While current element > element at stack top, pop and set result.
 *   - Only push indices during first pass (i < N).
 *
 * Key Insight:
 *   - Circular array = traverse the array twice.
 *   - Monotonic decreasing stack: when a larger element arrives, it's the next greater
 *     for all smaller elements in the stack.
 *
 * Time Complexity:  O(N) — each element pushed/popped once.
 * Space Complexity: O(N) — stack + result.
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

/**
 * Monotonic stack with step-by-step trace.
 */
fun nextGreaterElementsTrace(nums: IntArray) {
    println("Input: ${nums.toList()}")
    val n = nums.size
    val result = IntArray(n) { -1 }
    val stack = ArrayDeque<Int>()

    for (i in 0 until 2 * n) {
        val idx = i % n
        println("  i=$i, idx=$idx, nums[$idx]=${nums[idx]}, stack=${stack.toList()}")
        while (stack.isNotEmpty() && nums[idx] > nums[stack.last()]) {
            val popped = stack.removeLast()
            result[popped] = nums[idx]
            println("    Pop $popped → result[$popped]=${nums[idx]}")
        }
        if (i < n) {
            stack.addLast(idx)
            println("    Push $idx")
        }
    }
    println("  Result: ${result.toList()}")
}
