package backtracking.combination_sum

/**
 * Combination Sum — LeetCode #39
 * https://leetcode.com/problems/combination-sum/
 *
 * Problem:
 * -------
 * Given an array of distinct integers (candidates) and a target, find all unique
 * combinations that sum to target. Each number can be used unlimited times.
 *
 * Example:  candidates = [2,3,6,7], target = 7  →  [[2,2,3],[7]]
 *           candidates = [2,3,5], target = 8  →  [[2,2,2,2],[2,3,3],[3,5]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Backtracking)
 *
 * Two approaches:
 * 1. Brute Force Recursion: O(2^T) — try include/exclude each candidate
 * 2. Backtracking with Pruning: O(N^(T/M)) — skip duplicates, prune early
 */

fun main() {
    val candidates = intArrayOf(2, 3, 6, 7)
    val target = 7

    println("=== Method 1: Brute Force Recursion ===")
    println("combinationSum(${candidates.toList()}, $target) = ${combinationSumBruteForce(candidates, target)}")

    println("\n=== Method 2: Backtracking with Pruning ===")
    println("combinationSum(${candidates.toList()}, $target) = ${combinationSumBacktrack(candidates, target)}")

    println("\n=== Step-by-step trace ===")
    combinationSumTrace(candidates, target)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE RECURSION — O(2^T)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — At each step, try adding each candidate. No pruning.
 *
 * Core Idea:
 *   - For each candidate, try adding it to the current combination.
 *   - Recurse with reduced target.
 *   - No pruning — explores all possibilities including overshooting.
 *
 * Time Complexity:  O(2^T) — exponential, no pruning.
 * Space Complexity: O(T) — recursion depth.
 */
fun combinationSumBruteForce(candidates: IntArray, target: Int): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun bruteForce(start: Int, remaining: Int, path: MutableList<Int>) {
        if (remaining == 0) {
            result.add(path.toList())
            return
        }
        if (remaining < 0) return  // Overshot — no pruning before this.

        for (i in start until candidates.size) {
            path.add(candidates[i])
            bruteForce(i, remaining - candidates[i], path)  // Same i — unlimited use.
            path.removeAt(path.lastIndex)  // Backtrack.
        }
    }

    bruteForce(0, target, mutableListOf())
    return result
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BACKTRACKING WITH PRUNING — O(N^(T/M))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BACKTRACKING WITH PRUNING — Sort candidates. Skip if candidate > remaining.
 *
 * Core Idea:
 *   - Sort candidates first.
 *   - At each step, only try candidates <= remaining (prune early).
 *   - Use start index to avoid duplicate combinations.
 *
 * Key Insight:
 *   - Sorting + early pruning avoids exploring branches that will overshoot.
 *   - Using start index (not restarting from 0) avoids duplicates like [2,3] and [3,2].
 *
 * Time Complexity:  O(N^(T/M)) — N candidates, T/M max depth (M = smallest candidate).
 * Space Complexity: O(T/M) — recursion depth.
 */
fun combinationSumBacktrack(candidates: IntArray, target: Int): List<List<Int>> {
    val sorted = candidates.sorted()
    val result = mutableListOf<List<Int>>()

    fun backtrack(start: Int, remaining: Int, path: MutableList<Int>) {
        if (remaining == 0) {
            result.add(path.toList())
            return
        }

        for (i in start until sorted.size) {
            if (sorted[i] > remaining) break  // Prune — no point continuing.
            path.add(sorted[i])
            backtrack(i, remaining - sorted[i], path)  // Same i — unlimited use.
            path.removeAt(path.lastIndex)  // Backtrack.
        }
    }

    backtrack(0, target, mutableListOf())
    return result
}

/**
 * Backtracking with step-by-step trace.
 */
fun combinationSumTrace(candidates: IntArray, target: Int) {
    val sorted = candidates.sorted()
    val result = mutableListOf<List<Int>>()
    println("Sorted candidates: ${sorted.toList()}, target=$target")

    fun backtrack(start: Int, remaining: Int, path: MutableList<Int>, depth: Int) {
        val indent = "  ".repeat(depth)
        if (remaining == 0) {
            println("${indent}✅ Found: ${path.toList()}")
            result.add(path.toList())
            return
        }
        for (i in start until sorted.size) {
            if (sorted[i] > remaining) {
                println("${indent}✂️ Prune: ${sorted[i]} > $remaining")
                break
            }
            println("${indent}Try ${sorted[i]}, remaining=$remaining → path=${path + sorted[i]}")
            path.add(sorted[i])
            backtrack(i, remaining - sorted[i], path, depth + 1)
            path.removeAt(path.lastIndex)
        }
    }

    backtrack(0, target, mutableListOf(), 0)
    println("  Result: ${result.toList()}")
}
