package dp.one_d

/**
 * https://leetcode.com/problems/min-cost-climbing-stairs/
 *
 * You are given an integer array cost where cost[i] is the cost of stepping on the i-th step.
 * Once you pay the cost, you can either climb 1 or 2 steps.
 * You can start from step 0 or step 1. Return the minimum cost to reach the top.
 *
 * Example 1: cost = [10,15,20] → Output: 15 (start at step 1, pay 15, climb 2 to top)
 * Example 2: cost = [1,100,1,1,1,100,1,1,100,1] → Output: 6
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Climbing Stairs variant — minimization DP)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is Climbing Stairs' "minimization" cousin.
 *
 * Climbing Stairs: "How many WAYS to reach the top?" → COUNT (sum)
 * Min Cost Stairs: "What's the MIN COST to reach the top?" → MINIMIZE (min)
 *
 * Key question: "To reach step i, where could I have come from?"
 *   → From step (i-1), paying cost[i-1] and climbing 1 step
 *   → From step (i-2), paying cost[i-2] and climbing 2 steps
 * So: dp[i] = min(dp[i-1] + cost[i-1], dp[i-2] + cost[i-2])
 *
 * Base cases: dp[0] = 0, dp[1] = 0 (we can start at either for free!)
 *
 * WHY dp[0]=0 and dp[1]=0? Because the problem says "you can start
 * from step 0 or step 1" — starting is FREE. You only pay when you
 * STEP on a stair and then leave it.
 *
 * The "top" is index n (one past the last step), so answer = dp[n].
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Min Cost Climbing Stairs ===")
    println("Brute Force [10,15,20]:          ${minCostBruteForce(intArrayOf(10, 15, 20))}")
    println("Memoization [10,15,20]:          ${minCostMemo(intArrayOf(10, 15, 20))}")
    println("Tabulation  [10,15,20]:          ${minCostTabulation(intArrayOf(10, 15, 20))}")
    println("Optimal     [10,15,20]:          ${minCostOptimal(intArrayOf(10, 15, 20))}")
    println("---")
    println("Optimal [1,100,1,1,1,100,1,1,100,1]: ${minCostOptimal(intArrayOf(1, 100, 1, 1, 1, 100, 1, 1, 100, 1))}")
}

/**
 * BRUTE FORCE — Recursion without memoization
 * Time Complexity: O(2^N) — each call spawns 2 sub-calls
 * Space Complexity: O(N) — recursion stack depth
 *
 * At each step, try climbing 1 or 2 steps. Take the minimum cost.
 *
 * Recursion tree for [10,15,20]:
 *                    f(0)
 *                  /      \
 *              f(1)       f(2)
 *             /    \      /    \
 *           f(2)  f(3)  f(3)  f(4)
 *          /  \    |     |
 *        f(3) f(4) 0    0
 *         |
 *         0
 *
 * f(2) computed TWICE, f(3) computed THREE times!
 */
fun minCostBruteForce(cost: IntArray): Int {
    return minCostRec(cost, 0)
}

private fun minCostRec(cost: IntArray, i: Int): Int {
    if (i >= cost.size) return 0  // reached the top
    return cost[i] + minOf(minCostRec(cost, i + 1), minCostRec(cost, i + 2))
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N) — each step computed once
 * Space Complexity: O(N) — memo + recursion stack
 *
 * Cache result for each starting step.
 *
 * Trace for [10,15,20]:
 * f(0) = 10 + min(f(1), f(2))
 *   f(1) = 15 + min(f(2), f(3))
 *     f(2) = 20 + min(f(3), f(4)) = 20 + min(0, 0) = 20  (cache!)
 *     f(3) = 0 (base case)
 *   f(1) = 15 + min(20, 0) = 15  (cache!)
 *   f(2) = 20 (cached!)
 * f(0) = 10 + min(15, 20) = 25
 *
 * But we can start at step 0 OR step 1 → answer = min(f(0), f(1)) = min(25, 15) = 15 ✅
 */
fun minCostMemo(cost: IntArray): Int {
    val memo = IntArray(cost.size) { -1 }
    return minOf(minCostMemoHelper(cost, 0, memo), minCostMemoHelper(cost, 1, memo))
}

private fun minCostMemoHelper(cost: IntArray, i: Int, memo: IntArray): Int {
    if (i >= cost.size) return 0
    if (memo[i] != -1) return memo[i]
    memo[i] = cost[i] + minOf(minCostMemoHelper(cost, i + 1, memo), minCostMemoHelper(cost, i + 2, memo))
    return memo[i]
}

/**
 * OPTIMAL-1 — Bottom-Up DP (Tabulation)
 * Time Complexity: O(N)
 * Space Complexity: O(N)
 *
 * dp[i] = minimum cost to reach step i (not yet paying cost[i])
 * dp[0] = 0, dp[1] = 0 (start for free)
 * dp[i] = min(dp[i-1] + cost[i-1], dp[i-2] + cost[i-2])
 *
 * Trace for [10,15,20]:
 * dp[0]=0, dp[1]=0
 * dp[2] = min(dp[1]+cost[1], dp[0]+cost[0]) = min(0+15, 0+10) = 15
 * dp[3] = min(dp[2]+cost[2], dp[1]+cost[1]) = min(15+20, 0+15) = 15
 *
 * dp[3] = 15 ✅
 */
fun minCostTabulation(cost: IntArray): Int {
    val n = cost.size
    val dp = IntArray(n + 1)
    dp[0] = 0
    dp[1] = 0

    for (i in 2..n) {
        dp[i] = minOf(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2])
    }
    return dp[n]
}

/**
 * OPTIMAL-2 — Space-Optimized Bottom-Up DP
 * Time Complexity: O(N)
 * Space Complexity: O(1) ← only 2 variables!
 *
 * dp[i] only depends on dp[i-1] and dp[i-2].
 * Same optimization as Climbing Stairs.
 *
 * Trace for [10,15,20]:
 * prev2=0, prev1=0
 * i=2: curr = min(0+15, 0+10) = 10, prev2=0→0, prev1=0→10  → prev2=0, prev1=10
 * i=3: curr = min(10+20, 0+15) = 15, prev2=0→10, prev1=10→15  → prev2=10, prev1=15
 * Result: 15 ✅
 */
fun minCostOptimal(cost: IntArray): Int {
    var prev2 = 0  // dp[i-2]
    var prev1 = 0  // dp[i-1]

    for (i in 2..cost.size) {
        val curr = minOf(prev1 + cost[i - 1], prev2 + cost[i - 2])
        prev2 = prev1
        prev1 = curr
    }
    return prev1
}
