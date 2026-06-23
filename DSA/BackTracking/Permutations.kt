package BackTracking

/**
 * https://leetcode.com/problems/permutations/
 *
 * Given an array nums of DISTINCT integers, return ALL possible permutations.
 *
 * Example: nums = [1,2,3] → [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 * Output: 3! = 6 permutations
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (ORDER matters — unlike Subsets)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * KEY DIFFERENCE from Subsets:
 *   Subsets:      Order DOESN'T matter → [1,2] == [2,1] → use start index
 *   Permutations: Order MATTERS       → [1,2] ≠ [2,1] → try ALL positions
 *
 * We must use EVERY element exactly once. So we need a `used` array to
 * track which elements are already in the current permutation.
 *
 * Two approaches:
 * 1. USED ARRAY: Track which elements are already picked
 * 2. SWAP: Swap elements in-place, recurse on remaining positions
 *
 * Connection to other problems:
 *   Subsets → Choose some elements, order doesn't matter
 *   Permutations → Use ALL elements, order matters
 *   Combination Sum → Choose elements with constraint, reuse allowed
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Permutations ===")
    println("[1,2,3]: ${permute(intArrayOf(1, 2, 3))}")
}

/**
 * APPROACH 1: Backtracking with used array
 * Time Complexity: O(N × N!) — N! permutations, each takes O(N) to copy
 * Space Complexity: O(N) — recursion depth + used array
 *
 * Trace for [1,2,3]:
 * dfs([])
 *   pick 1: dfs([1])
 *     pick 2: dfs([1,2])
 *       pick 3: dfs([1,2,3]) → ADD [1,2,3] ✅
 *     pick 3: dfs([1,3])
 *       pick 2: dfs([1,3,2]) → ADD [1,3,2] ✅
 *   pick 2: dfs([2])
 *     pick 1: dfs([2,1])
 *       pick 3: dfs([2,1,3]) → ADD [2,1,3] ✅
 *     pick 3: dfs([2,3])
 *       pick 1: dfs([2,3,1]) → ADD [2,3,1] ✅
 *   pick 3: dfs([3])
 *     pick 1: dfs([3,1])
 *       pick 2: dfs([3,1,2]) → ADD [3,1,2] ✅
 *     pick 2: dfs([3,2])
 *       pick 1: dfs([3,2,1]) → ADD [3,2,1] ✅
 *
 * Total: 6 permutations ✅
 */
fun permute(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    val used = BooleanArray(nums.size)

    fun backtrack(current: MutableList<Int>) {
        // Base case: all elements used
        if (current.size == nums.size) {
            result.add(current.toList())
            return
        }

        // Try each unused element (NOT starting from 'start' — try ALL!)
        for (i in nums.indices) {
            if (used[i]) continue  // Skip already used elements

            used[i] = true
            current.add(nums[i])          // CHOOSE
            backtrack(current)              // EXPLORE
            current.removeAt(current.lastIndex)  // UNDO
            used[i] = false
        }
    }

    backtrack(mutableListOf())
    return result
}

/**
 * APPROACH 2: Swap-based (in-place)
 * Time Complexity: O(N × N!)
 * Space Complexity: O(N) — recursion depth only (no used array)
 *
 * Instead of tracking used elements, swap elements in-place.
 * Position `first` is fixed, recurse on remaining positions.
 */
fun permuteSwap(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun backtrack(first: Int) {
        if (first == nums.size) {
            result.add(nums.toList())
            return
        }

        for (i in first until nums.size) {
            nums.swap(first, i)       // SWAP: put nums[i] at position first
            backtrack(first + 1)       // EXPLORE: fix position first, recurse
            nums.swap(first, i)       // UNDO: swap back
        }
    }

    backtrack(0)
    return result
}

private fun IntArray.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
}
