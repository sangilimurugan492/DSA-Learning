package dp.one_d

/**
 * https://leetcode.com/problems/climbing-stairs/
 *
 * You are climbing a staircase. It takes n steps to reach the top.
 * Each time you can either climb 1 or 2 steps.
 * In how many distinct ways can you climb to the top?
 *
 * Example 1: n = 2 → Output: 2 (1+1, 2)
 * Example 2: n = 3 → Output: 3 (1+1+1, 1+2, 2+1)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE gateway DP problem — Fibonacci variant)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Think of it as: "To reach step n, where could I have come from?"
 *   → I could have been at step (n-1) and taken 1 step
 *   → I could have been at step (n-2) and taken 2 steps
 * So: ways(n) = ways(n-1) + ways(n-2)  ← This IS Fibonacci!
 *
 * Why? Because these are the ONLY two last moves possible.
 * Every path to step n MUST end with one of these two moves.
 * No other possibilities exist → the recurrence is complete.
 *
 * Base cases: ways(1) = 1, ways(2) = 2
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Climbing Stairs ===")
    println("Brute Force (n=5):  ${climbStairsBruteForce(5)}")
    println("Memoization (n=5):   ${climbStairsMemo(5)}")
    println("Tabulation  (n=5):   ${climbStairsTabulation(5)}")
    println("Optimal     (n=5):   ${climbStairsOptimal(5)}")
    println("---")
    println("Brute Force (n=3):  ${climbStairsBruteForce(3)}")
    println("Optimal     (n=3):  ${climbStairsOptimal(3)}")
}

/**
 * BRUTE FORCE — Recursion without memoization
 * Time Complexity: O(2^N) — each call spawns 2 sub-calls (exponential tree)
 * Space Complexity: O(N) — recursion stack depth
 *
 * We recompute the same subproblems over and over.
 *
 * Recursion tree for n=5:
 *                          f(5)
 *                       /        \
 *                    f(4)         f(3)
 *                   /    \       /    \
 *                f(3)   f(2)  f(2)   f(1)
 *               /   \   / \   / \
 *             f(2) f(1) f(1) f(0) f(1) f(0)
 *             / \
 *           f(1) f(0)
 *
 * f(3) is computed TWICE, f(2) is computed THREE times!
 * This exponential blowup is why brute force fails for large n.
 */
fun climbStairsBruteForce(n: Int): Int {
    if (n <= 1) return 1
    return climbStairsBruteForce(n - 1) + climbStairsBruteForce(n - 2)
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N) — each subproblem computed exactly once
 * Space Complexity: O(N) — memo array + recursion stack
 *
 * Key insight: Cache results! Before computing f(n), check if we already know it.
 * This transforms O(2^N) → O(N) because each of the N values is computed once.
 *
 * Trace for n=5:
 * f(5) → need f(4), f(3)
 *   f(4) → need f(3), f(2)
 *     f(3) → need f(2), f(1)
 *       f(2) → need f(1), f(0) → 1+1 = 2 ✓ (cache f(2)=2)
 *       f(1) = 1 (base case, cache f(1)=1)
 *     f(3) = f(2)+f(1) = 2+1 = 3 ✓ (cache f(3)=3)
 *   f(4) = f(3)+f(2) = 3+2 = 5 ✓ (cache f(4)=5)
 *   f(3) → already cached! Return 3 directly (no recomputation!)
 * f(5) = f(4)+f(3) = 5+3 = 8 ✓
 */
fun climbStairsMemo(n: Int): Int {
    val memo = IntArray(n + 2) { -1 }
    return climbMemo(n, memo)
}

private fun climbMemo(n: Int, memo: IntArray): Int {
    if (n <= 1) return 1
    if (memo[n] != -1) return memo[n]
    memo[n] = climbMemo(n - 1, memo) + climbMemo(n - 2, memo)
    return memo[n]
}

/**
 * OPTIMAL-1 — Bottom-Up DP (Tabulation)
 * Time Complexity: O(N)
 * Space Complexity: O(N)
 *
 * Instead of top-down recursion, build from the bottom up.
 * Start from base cases, fill dp[] iteratively.
 *
 * Why bottom-up? No recursion overhead, no stack overflow risk.
 *
 * Trace for n=5:
 * dp[0]=1, dp[1]=1  (base cases)
 * dp[2] = dp[1] + dp[0] = 1 + 1 = 2
 * dp[3] = dp[2] + dp[1] = 2 + 1 = 3
 * dp[4] = dp[3] + dp[2] = 3 + 2 = 5
 * dp[5] = dp[4] + dp[3] = 5 + 3 = 8 ✅
 */
fun climbStairsTabulation(n: Int): Int {
    if (n <= 1) return 1
    val dp = IntArray(n + 1)
    dp[0] = 1
    dp[1] = 1
    for (i in 2..n) {
        dp[i] = dp[i - 1] + dp[i - 2]
    }
    return dp[n]
}

/**
 * OPTIMAL-2 — Space-Optimized Bottom-Up DP
 * Time Complexity: O(N)
 * Space Complexity: O(1) ← only 2 variables instead of entire array!
 *
 * Key insight: dp[i] only depends on dp[i-1] and dp[i-2].
 * We don't need the whole array — just the last two values!
 *
 * This is the SAME optimization as computing Fibonacci with O(1) space.
 *
 * Trace for n=5:
 * prev2=1, prev1=1
 * i=2: curr = 1+1 = 2, prev2=1→prev1=1, prev1=1→curr=2  → prev2=1, prev1=2
 * i=3: curr = 2+1 = 3, prev2=1→2, prev1=2→3              → prev2=2, prev1=3
 * i=4: curr = 3+2 = 5, prev2=2→3, prev1=3→5              → prev2=3, prev1=5
 * i=5: curr = 5+3 = 8, prev2=3→5, prev1=5→8              → prev2=5, prev1=8
 * Result: 8 ✅
 */
fun climbStairsOptimal(n: Int): Int {
    if (n <= 1) return 1
    var prev2 = 1  // dp[i-2]
    var prev1 = 1  // dp[i-1]

    for (i in 2..n) {
        val curr = prev1 + prev2
        prev2 = prev1
        prev1 = curr
    }
    return prev1
}
