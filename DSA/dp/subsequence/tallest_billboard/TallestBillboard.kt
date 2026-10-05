package dp.subsequence.tallest_billboard

/**
 * Tallest Billboard — LeetCode #956
 * https://leetcode.com/problems/tallest-billboard/
 *
 * Problem:
 * -------
 * Build two equal-height steel supports from rods (each rod: left / right / discard).
 * Return the maximum support height, or 0 if impossible.
 *
 * Example:  [1,2,3,6]          → 6   (left=[6], right=[1+2+3])
 *           [1,2,3,4,5,6]     → 10  (left=[1+3+6], right=[2+4+5]... many ways)
 *           [1,2]             → 0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic "3-choice knapsack" DP on differences)
 *
 * Key Insight:
 * ------------
 * Track dp[diff] = max leftHeight with (leftHeight - rightHeight == diff).
 * For each rod, 3 transitions: add to left (diff+rod, left+rod),
 * add to right (diff-rod, left), or discard (diff, left).
 * Answer = dp[0] (equal supports).
 *
 * Two approaches:
 * 1. Brute force recursion: O(3^N)
 * 2. DP over differences: O(N × S)
 */

fun main() {
    val tests = listOf(
        intArrayOf(1, 2, 3, 6),        // 6
        intArrayOf(1, 2, 3, 4, 5, 6), // 10
        intArrayOf(1, 2),             // 0
        intArrayOf(1, 4, 5, 7, 8)     // 12 (left=[4+8], right=[5+7])
    )

    for (rods in tests) {
        println("rods = ${rods.toList()}")
        println("  Brute force : ${tallestBillboardBrute(rods)}")
        println("  DP (optimal): ${tallestBillboard(rods)}")
        println()
    }

    println("=== Step-by-step trace (rods = [1,2,3,6]) ===")
    tallestBillboardTrace(intArrayOf(1, 2, 3, 6))
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: Brute Force — 3-way recursion
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Brute force — each rod goes left, right, or is discarded.
 *
 * Core Idea:
 *   - Recurse over every rod with 3 choices; at the end, if left == right,
 *     that height is a candidate answer.
 *
 * Key Insight:
 *   - Simple to reason about but 3^N explodes for N > ~15.
 *
 * Time Complexity:  O(3^N) — exponential.
 * Space Complexity: O(N) — recursion depth.
 */
fun tallestBillboardBrute(rods: IntArray): Int {
    var best = 0
    fun recurse(i: Int, left: Int, right: Int) {
        if (i == rods.size) {
            if (left == right) best = maxOf(best, left)
            return
        }
        recurse(i + 1, left + rods[i], right)  // rod → left support
        recurse(i + 1, left, right + rods[i])   // rod → right support
        recurse(i + 1, left, right)            // discard rod
    }
    recurse(0, 0, 0)
    return best
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: DP over height differences — optimal
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DP — dp[diff] = max leftHeight such that leftHeight - rightHeight == diff.
 *
 * Core Idea:
 *   - State: only the DIFFERENCE between supports matters, plus the best left
 *     height achieving it. This collapses O(S²) (left, right) states to O(S).
 *   - For each rod and each existing (diff → left):
 *       left support:  new diff = diff + rod, new left = left + rod
 *       right support: new diff = diff - rod, new left = left
 *       discard:       new diff = diff,       new left = left
 *   - Keep the max left per diff; answer is dp[0] (or 0 if absent).
 *
 * Key Insight:
 *   - diff == 0 at the end ⇔ the two supports are equal height.
 *
 * Time Complexity:  O(N × S) — S = sum(rods), number of distinct differences.
 * Space Complexity: O(S) — hash map of differences.
 */
fun tallestBillboard(rods: IntArray): Int {
    // dp[diff] = max leftHeight with (left - right == diff)
    var dp = HashMap<Int, Int>()
    dp[0] = 0

    for (rod in rods) {
        val next = HashMap(dp)  // "discard" transition: carry all states over.
        for ((diff, left) in dp) {
            // Add rod to the LEFT support.
            next.merge(diff + rod, left + rod, ::maxOf)
            // Add rod to the RIGHT support (left height unchanged).
            next.merge(diff - rod, left, ::maxOf)
        }
        dp = next
    }

    return dp[0] ?: 0
}

/**
 * DP with step-by-step trace.
 */
fun tallestBillboardTrace(rods: IntArray) {
    println("Input: ${rods.toList()}")
    var dp = HashMap<Int, Int>()
    dp[0] = 0

    for (rod in rods) {
        val next = HashMap(dp)
        for ((diff, left) in dp) {
            next.merge(diff + rod, left + rod, ::maxOf)
            next.merge(diff - rod, left, ::maxOf)
        }
        dp = next
        println("  after rod=$rod: dp[0]=${dp[0] ?: 0}, states=${dp.size}")
    }

    println("  Result (dp[0]): ${dp[0] ?: 0}")
}
