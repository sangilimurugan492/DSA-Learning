package BackTracking

/**
 * https://leetcode.com/problems/subsets/
 *
 * Given an integer array nums of UNIQUE elements, return all possible subsets (the power set).
 * The solution set must NOT contain duplicate subsets.
 *
 * Example: nums = [1,2,3] → [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 * Output: 2^3 = 8 subsets
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE gateway backtracking problem)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * For each element, we have TWO choices: INCLUDE it or EXCLUDE it.
 * This creates a decision tree with 2^N leaves (= number of subsets).
 *
 * Two approaches:
 * 1. BACKTRACKING: At each index, choose to include or skip, recurse, undo
 * 2. ITERATIVE: Start with [[]], for each num, add num to all existing subsets
 *
 * Backtracking tree for [1,2,3]:
 *                        []
 *                   /          \
 *                 [1]           []
 *                /   \         /   \
 *           [1,2]   [1]    [2]    []
 *           / \     / \    / \    / \
 *      [1,2,3][1,2][1,3][1][2,3][2][3][]
 *
 * Connection to other problems:
 *   Subsets → THE foundation. All other backtracking problems build on this.
 *   Combination Sum → Subsets with constraint (sum == target) + reuse allowed
 *   Permutations → Subsets where ORDER matters (all elements used)
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Subsets ===")
    println("Backtracking [1,2,3]: ${subsets(intArrayOf(1, 2, 3))}")
    println("Iterative [1,2,3]:   ${subsetsIterative(intArrayOf(1, 2, 3))}")
}

/**
 * APPROACH 1: Backtracking
 * Time Complexity: O(N × 2^N) — 2^N subsets, each up to N elements
 * Space Complexity: O(N) — recursion depth
 *
 * Trace for [1,2,3]:
 * dfs(0, [])
 *   include 1: dfs(1, [1])
 *     include 2: dfs(2, [1,2])
 *       include 3: dfs(3, [1,2,3]) → ADD [1,2,3]
 *       exclude 3: dfs(3, [1,2]) → ADD [1,2]
 *     exclude 2: dfs(2, [1])
 *       include 3: dfs(3, [1,3]) → ADD [1,3]
 *       exclude 3: dfs(3, [1]) → ADD [1]
 *   exclude 1: dfs(1, [])
 *     include 2: dfs(2, [2])
 *       include 3: dfs(3, [2,3]) → ADD [2,3]
 *       exclude 3: dfs(3, [2]) → ADD [2]
 *     exclude 2: dfs(2, [])
 *       include 3: dfs(3, [3]) → ADD [3]
 *       exclude 3: dfs(3, []) → ADD []
 *
 * Total: 8 subsets ✅
 */
fun subsets(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun backtrack(start: Int, current: MutableList<Int>) {
        // ADD current subset to result (every node is a valid subset)
        result.add(current.toList())

        // Try including each remaining element
        for (i in start until nums.size) {
            current.add(nums[i])          // CHOOSE
            backtrack(i + 1, current)      // EXPLORE
            current.removeAt(current.lastIndex)  // UNDO (backtrack)
        }
    }

    backtrack(0, mutableListOf())
    return result
}

/**
 * APPROACH 2: Iterative (Cascading)
 * Time Complexity: O(N × 2^N)
 * Space Complexity: O(N × 2^N) — storing all subsets
 *
 * Trace for [1,2,3]:
 * Start: [[]]
 * Add 1: [[], [1]]
 * Add 2: [[], [1], [2], [1,2]]
 * Add 3: [[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]
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
