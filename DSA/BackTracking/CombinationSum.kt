package BackTracking

/**
 * https://leetcode.com/problems/combination-sum/
 *
 * Given an array of DISTINCT integers candidates and a target, return all
 * unique combinations where the chosen numbers sum to target.
 * The SAME number may be chosen UNLIMITED times.
 *
 * Example: candidates = [2,3,6,7], target = 7 → [[2,2,3], [7]]
 * Example: candidates = [2,3,5], target = 8 → [[2,2,2,2],[2,3,3],[3,5]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Subsets + constraint + reuse)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is Subsets with TWO modifications:
 *   1. CONSTRAINT: only add combinations where sum == target
 *   2. REUSE: can pick the same element again (don't increment index after choosing)
 *
 * KEY DIFFERENCE from Subsets:
 *   Subsets:  backtrack(i + 1, ...) → move to next element (no reuse)
 *   CombSum:  backtrack(i, ...)     → stay at same index (reuse allowed!)
 *
 * Pruning: If remaining < 0, stop exploring (no negative numbers).
 *          Sort candidates to enable early termination.
 *
 * Connection to other problems:
 *   Subsets → Combination Sum is Subsets with sum constraint + reuse
 *   Combination Sum II → Same but NO reuse (like 0/1 knapsack vs unbounded)
 *   Coin Change → Combination Sum but we want MIN count, not ALL combinations
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Combination Sum ===")
    println("[2,3,6,7], target=7: ${combinationSum(intArrayOf(2, 3, 6, 7), 7)}")
    println("[2,3,5], target=8:   ${combinationSum(intArrayOf(2, 3, 5), 8)}")
}

/**
 * Backtracking with reuse
 * Time Complexity: O(N^(T/M)) where T=target, M=min candidate
 * Space Complexity: O(T/M) — recursion depth
 *
 * Trace for [2,3,6,7], target=7:
 * dfs(0, 7, [])
 *   i=0: pick 2, remaining=5
 *     i=0: pick 2, remaining=3
 *       i=0: pick 2, remaining=1
 *         i=0: pick 2, remaining=-1 → PRUNE (remaining < 0)
 *         i=1: pick 3, remaining=-2 → PRUNE
 *         ...
 *       i=1: pick 3, remaining=0 → ADD [2,2,3] ✅
 *       i=2: pick 6, remaining=-3 → PRUNE
 *       i=3: pick 7, remaining=-4 → PRUNE
 *     i=1: pick 3, remaining=2
 *       i=1: pick 3, remaining=-1 → PRUNE
 *       ...
 *     i=2: pick 6, remaining=-1 → PRUNE
 *     i=3: pick 7, remaining=-2 → PRUNE
 *   i=1: pick 3, remaining=4
 *     i=1: pick 3, remaining=1
 *       i=1: pick 3, remaining=-2 → PRUNE
 *       ...
 *     i=2: pick 6, remaining=-2 → PRUNE
 *     i=3: pick 7, remaining=-3 → PRUNE
 *   i=2: pick 6, remaining=1 → all fail
 *   i=3: pick 7, remaining=0 → ADD [7] ✅
 *
 * Result: [[2,2,3], [7]] ✅
 */
fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    candidates.sort()  // Sort for pruning

    fun backtrack(start: Int, remaining: Int, current: MutableList<Int>) {
        if (remaining == 0) {
            result.add(current.toList())  // Found valid combination
            return
        }

        for (i in start until candidates.size) {
            if (candidates[i] > remaining) break  // Prune (sorted!)

            current.add(candidates[i])              // CHOOSE
            backtrack(i, remaining - candidates[i], current)  // EXPLORE (i, not i+1 → reuse!)
            current.removeAt(current.lastIndex)     // UNDO
        }
    }

    backtrack(0, target, mutableListOf())
    return result
}
