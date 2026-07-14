package backtracking.permutations

/**
 * Permutations — LeetCode #46
 * https://leetcode.com/problems/permutations/
 *
 * Problem:
 * -------
 * Given an array of DISTINCT integers, return ALL possible permutations.
 *
 * Example:  nums = [1,2,3] → [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 *           Output: 3! = 6 permutations
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (ORDER matters — unlike Subsets)
 *
 * Two approaches:
 * 1. Used Array: O(N × N!) — track which elements are already picked
 * 2. Swap-based: O(N × N!) — swap elements in-place, recurse on remaining
 */

fun main() {
    val nums = intArrayOf(1, 2, 3)

    println("=== Method 1: Used Array ===")
    println("permute(${nums.toList()}) = ${permute(nums)}")

    println("\n=== Method 2: Swap-based ===")
    println("permuteSwap(${nums.toList()}) = ${permuteSwap(nums.copyOf())}")

    println("\n=== Step-by-step trace ===")
    permuteTrace(nums)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: USED ARRAY — O(N × N!)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * USED ARRAY — Track which elements are already picked. Try ALL unused elements at each step.
 *
 * Core Idea:
 *   - Use a `used` boolean array to track picked elements.
 *   - At each step, try every unused element (NOT starting from 'start' — try ALL).
 *   - Base case: current.size == nums.size → add to result.
 *
 * Key Difference from Subsets:
 *   - Subsets: Order doesn't matter → use start index.
 *   - Permutations: Order matters → try ALL positions.
 *
 * Time Complexity:  O(N × N!) — N! permutations, each takes O(N) to copy.
 * Space Complexity: O(N) — recursion depth + used array.
 */
fun permute(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    val used = BooleanArray(nums.size)

    fun backtrack(current: MutableList<Int>) {
        if (current.size == nums.size) {
            result.add(current.toList())
            return
        }
        for (i in nums.indices) {
            if (used[i]) continue
            used[i] = true
            current.add(nums[i])
            backtrack(current)
            current.removeAt(current.lastIndex)
            used[i] = false
        }
    }

    backtrack(mutableListOf())
    return result
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SWAP-BASED — O(N × N!)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SWAP-BASED — Swap elements in-place. Position `first` is fixed, recurse on remaining.
 *
 * Core Idea:
 *   - For each position `first`, try every element from `first` to end.
 *   - Swap element to position `first`, recurse, swap back.
 *   - No used array needed — swapping handles it.
 *
 * Time Complexity:  O(N × N!).
 * Space Complexity: O(N) — recursion depth only (no used array).
 */
fun permuteSwap(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun backtrack(first: Int) {
        if (first == nums.size) {
            result.add(nums.toList())
            return
        }
        for (i in first until nums.size) {
            nums.swap(first, i)
            backtrack(first + 1)
            nums.swap(first, i)
        }
    }

    backtrack(0)
    return result
}

private fun IntArray.swap(i: Int, j: Int) {
    val temp = this[i]; this[i] = this[j]; this[j] = temp
}

/**
 * Used array with step-by-step trace.
 */
fun permuteTrace(nums: IntArray) {
    println("Input: ${nums.toList()}")
    val result = mutableListOf<List<Int>>()
    val used = BooleanArray(nums.size)

    fun backtrack(current: MutableList<Int>, depth: Int) {
        val indent = "  ".repeat(depth)
        if (current.size == nums.size) {
            println("${indent}✅ Found: ${current.toList()}")
            result.add(current.toList())
            return
        }
        for (i in nums.indices) {
            if (used[i]) continue
            println("${indent}Pick ${nums[i]}, current=${current + nums[i]}")
            used[i] = true
            current.add(nums[i])
            backtrack(current, depth + 1)
            current.removeAt(current.lastIndex)
            used[i] = false
        }
    }

    backtrack(mutableListOf(), 0)
    println("  Total: ${result.size} permutations")
}
