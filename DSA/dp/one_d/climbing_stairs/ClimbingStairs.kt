package dp.one_d.climbing_stairs

/**
 * Climbing Stairs — LeetCode #70
 * https://leetcode.com/problems/climbing-stairs/
 *
 * Problem:
 * -------
 * You are climbing a staircase. It takes n steps to reach the top.
 * Each time you can either climb 1 or 2 steps.
 * In how many distinct ways can you climb to the top?
 *
 * Example 1:  n = 2  →  2  (1+1, 2)
 * Example 2:  n = 3  →  3  (1+1+1, 1+2, 2+1)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE gateway DP problem — Fibonacci variant)
 *
 * Recurrence: ways(n) = ways(n-1) + ways(n-2)  ← This IS Fibonacci!
 * Base cases: ways(0) = 1, ways(1) = 1
 *
 * Two approaches:
 * 1. Brute Force Recursion: O(2^N) — exponential
 * 2. Space-Optimized DP: O(N) time, O(1) space
 */

fun main() {
    println("=== Method 1: Brute Force Recursion ===")
    println("climbStairs(5) = ${climbStairsBruteForce(5)}")
    println("climbStairs(3) = ${climbStairsBruteForce(3)}")

    println("\n=== Method 2: Space-Optimized DP ===")
    println("climbStairs(5) = ${climbStairsOptimal(5)}")
    println("climbStairs(3) = ${climbStairsOptimal(3)}")

    println("\n=== Step-by-step trace ===")
    climbStairsTrace(5)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Recursion without memoization
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — To reach step n, you came from n-1 (1 step) or n-2 (2 steps).
 *
 * Core Idea:
 *   - ways(n) = ways(n-1) + ways(n-2) — this IS Fibonacci!
 *   - Every path to step n MUST end with 1 step (from n-1) or 2 steps (from n-2).
 *
 * Problem: Overlapping subproblems → exponential time. f(3) computed multiple times.
 *
 * Time Complexity:  O(2^N) — exponential tree.
 * Space Complexity: O(N) — recursion stack.
 */
fun climbStairsBruteForce(n: Int): Int {
    if (n <= 1) return 1
    return climbStairsBruteForce(n - 1) + climbStairsBruteForce(n - 2)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SPACE-OPTIMIZED DP (O(1) space)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SPACE-OPTIMIZED DP — dp[i] only depends on dp[i-1] and dp[i-2]. Use two variables.
 *
 * Core Idea:
 *   - Same recurrence as Fibonacci: curr = prev1 + prev2.
 *   - Only need the last two values — no array needed.
 *
 * Key Insight:
 *   - "To reach step n, where could I have come from?" → n-1 or n-2.
 *   - ways(n) = ways(n-1) + ways(n-2) — count ALL ways (sum, not max).
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — two variables.
 */
fun climbStairsOptimal(n: Int): Int {
    if (n <= 1) return 1
    var prev2 = 1  // dp[i-2] — ways to reach step i-2.
    var prev1 = 1  // dp[i-1] — ways to reach step i-1.

    for (i in 2..n) {
        val curr = prev1 + prev2
        prev2 = prev1
        prev1 = curr
    }
    return prev1
}

/**
 * Space-optimized DP with step-by-step trace.
 */
fun climbStairsTrace(n: Int) {
    println("n = $n")
    if (n <= 1) {
        println("  Result: 1 (base case)")
        return
    }
    var prev2 = 1
    var prev1 = 1
    println("  Base: prev2=1 (ways to step 0), prev1=1 (ways to step 1)")

    for (i in 2..n) {
        val curr = prev1 + prev2
        println("  i=$i: curr = prev1($prev1) + prev2($prev2) = $curr")
        prev2 = prev1
        prev1 = curr
    }
    println("  Result: $prev1")
}
