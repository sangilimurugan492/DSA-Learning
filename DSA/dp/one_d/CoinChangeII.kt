package dp.one_d

/**
 * https://leetcode.com/problems/coin-change-ii/
 *
 * You are given an integer array coins and an integer amount.
 * Return the number of combinations that make up that amount.
 * You may assume infinite supply of each coin.
 *
 * Example 1: coins = [1,2,5], amount = 5 → Output: 4
 *   (5), (2+2+1), (2+1+1+1), (1+1+1+1+1)
 * Example 2: coins = [2], amount = 3 → Output: 0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Unbounded knapsack — counting combinations)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Coin Change I:  "What's the MINIMUM number of coins?" → min()
 * Coin Change II: "How many COMBINATIONS make the amount?" → count
 *
 * Same unbounded knapsack, but COUNTING instead of MINIMIZING.
 *
 * Recurrence: dp[a] = number of ways to make amount a
 *   For each coin: dp[a] += dp[a - coin]
 *
 * CRITICAL: Process coins in OUTER loop, amounts in INNER loop!
 *   for (coin in coins):
 *     for (a in coin..amount):
 *       dp[a] += dp[a - coin]
 *
 * WHY this order? To count COMBINATIONS (not permutations).
 *   - Outer loop = coins: each coin is processed once, order doesn't matter
 *   - If we swapped loops: we'd count PERMUTATIONS (1+2 and 2+1 counted separately)
 *
 * This is the SAME distinction as Partition Equal Subset Sum vs Coin Change I:
 *   - 0/1 Knapsack: reverse inner loop (each item at most once)
 *   - Unbounded Knapsack: forward inner loop (each item unlimited times)
 *   - Combinations: outer = items, inner = amounts
 *   - Permutations: outer = amounts, inner = items
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Coin Change II ===")
    println("Tabulation [1,2,5],5: ${change(5, intArrayOf(1, 2, 5))}")
    println("Tabulation [2],3:     ${change(3, intArrayOf(2))}")
    println("Tabulation [1],0:    ${change(0, intArrayOf(1))}")
}

/**
 * OPTIMAL — Bottom-Up DP (Tabulation)
 * Time Complexity: O(N × amount) — N = number of coins
 * Space Complexity: O(amount)
 *
 * dp[a] = number of combinations to make amount a
 * dp[0] = 1 (one way to make amount 0: use no coins)
 *
 * Trace for coins=[1,2,5], amount=5:
 * dp = [1, 0, 0, 0, 0, 0]
 *
 * Process coin=1: (forward iteration — unbounded)
 *   a=1: dp[1] += dp[0]=1 → dp[1]=1
 *   a=2: dp[2] += dp[1]=1 → dp[2]=1
 *   a=3: dp[3] += dp[2]=1 → dp[3]=1
 *   a=4: dp[4] += dp[3]=1 → dp[4]=1
 *   a=5: dp[5] += dp[4]=1 → dp[5]=1
 * dp = [1, 1, 1, 1, 1, 1]  ← only using coin 1
 *
 * Process coin=2:
 *   a=2: dp[2] += dp[0]=1 → dp[2]=1+1=2
 *   a=3: dp[3] += dp[1]=1 → dp[3]=1+1=2
 *   a=4: dp[4] += dp[2]=2 → dp[4]=1+2=3
 *   a=5: dp[5] += dp[3]=2 → dp[5]=1+2=3
 * dp = [1, 1, 2, 2, 3, 3]  ← using coins 1 and 2
 *
 * Process coin=5:
 *   a=5: dp[5] += dp[0]=1 → dp[5]=3+1=4
 * dp = [1, 1, 2, 2, 3, 4]  ← using coins 1, 2, and 5
 *
 * dp[5] = 4 ✅
 * Combinations: (1+1+1+1+1), (1+1+1+2), (1+2+2), (5)
 */
fun change(amount: Int, coins: IntArray): Int {
    val dp = IntArray(amount + 1)
    dp[0] = 1  // one way to make amount 0: use no coins

    for (coin in coins) {
        for (a in coin..amount) {
            dp[a] += dp[a - coin]
        }
    }
    return dp[amount]
}
