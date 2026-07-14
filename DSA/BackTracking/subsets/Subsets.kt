package backtracking.subsets

/**
 * Subsets — LeetCode #78
 * https://leetcode.com/problems/subsets/
 *
 * Problem:
 * -------
 * Given an integer array nums of UNIQUE elements, return all possible subsets (the power set).
 * The solution set must NOT contain duplicate subsets.
 *
 * Example:  nums = [1,2,3] → [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 *           Output: 2^3 = 8 subsets
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE gateway backtracking problem)
 *
 * Two approaches:
 * 1. Backtracking: O(N × 2^N) — at each index, choose to include or skip
 * 2. Iterative (Cascading): O(N × 2^N) — start with [[]], add num to all existing subsets
 */

fun main() {
    val nums = intArrayOf(1, 2, 3)

    println("=== Method 1: Backtracking ===")
    println("subsets(${nums.toList()}) = ${subsets(nums)}")

    println("\n=== Method 2: Iterative ===")
    println("subsetsIterative(${nums.toList()}) = ${subsetsIterative(nums)}")

    println("\n=== Step-by-step trace ===")
    subsetsTrace(nums)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BACKTRACKING — O(N × 2^N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BACKTRACKING — At each index, choose to include or skip. Every node is a valid subset.
 *
 * Core Idea:
 *   - Add current subset to result at every node (not just leaves).
 *   - For each remaining element, include it, recurse, then undo (backtrack).
 *   - Use start index to avoid duplicates (order doesn't matter).
 *
 * Key Insight:
 *   - For each element, we have TWO choices: INCLUDE or EXCLUDE.
 *   - This creates a decision tree with 2^N leaves.
 *   - Using start index ensures [1,2] and [2,1] don't both appear.
 *
 * Time Complexity:  O(N × 2^N) — 2^N subsets, each up to N elements.
 * Space Complexity: O(N) — recursion depth.
 */
fun subsets(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun backtrack(start: Int, current: MutableList<Int>) {
        result.add(current.toList())  // Every node is a valid subset.

        for (i in start until nums.size) {
            current.add(nums[i])
            backtrack(i + 1, current)
            current.removeAt(current.lastIndex)
        }
    }

    backtrack(0, mutableListOf())
    return result
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: ITERATIVE (CASCADING) — O(N × 2^N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * ITERATIVE — Start with [[]]. For each num, add num to all existing subsets.
 *
 * Core Idea:
 *   - Start with result = [[]].
 *   - For each num, take all existing subsets and create new ones by appending num.
 *   - This doubles the result size each iteration → 2^N total.
 *
 * Time Complexity:  O(N × 2^N).
 * Space Complexity: O(N × 2^N) — storing all subsets.
 */
fun subsetsIterative(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>(emptyList())

    for (num in nums) {
        val size = result.size
        for (i in 0 until size) {
            result.add(result[i] + num)
        }
    }
    return result
}

/**
 * Backtracking with step-by-step trace.
 */
fun subsetsTrace(nums: IntArray) {
    println("Input: ${nums.toList()}")
    val result = mutableListOf<List<Int>>()

    fun backtrack(start: Int, current: MutableList<Int>, depth: Int) {
        val indent = "  ".repeat(depth)
        println("${indent}Add subset: ${current.toList()}")
        result.add(current.toList())

        for (i in start until nums.size) {
            println("${indent}Include ${nums[i]} → ${current + nums[i]}")
            current.add(nums[i])
            backtrack(i + 1, current, depth + 1)
            current.removeAt(current.lastIndex)
        }
    }

    backtrack(0, mutableListOf(), 0)
    println("  Total: ${result.size} subsets")
}
