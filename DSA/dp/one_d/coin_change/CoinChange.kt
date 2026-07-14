package dp.one_d.coin_change

/**
 * Coin Change — LeetCode #322
 * https://leetcode.com/problems/coin-change/
 *
 * Problem:
 * -------
 * You are given coins of different denominations and a total amount. Return the
 * fewest number of coins needed to make up that amount. If impossible, return -1.
 * You may use each coin unlimited times (unbounded knapsack).
 *
 * Example 1:  coins = [1, 2, 5], amount = 11  →  3  (5 + 5 + 1)
 * Example 2:  coins = [2], amount = 3          →  -1
 * Example 3:  coins = [1], amount = 0          →  0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic unbounded knapsack / minimization DP)
 *
 * Recurrence: dp[a] = min(dp[a - coin] + 1) for each coin where a >= coin
 * Base case:  dp[0] = 0 (0 coins needed for amount 0)
 */

fun main() {
    println("=== Method 1: Brute Force (Recursion) ===")
    println("coinChange([1,2,5], 11) = ${coinChangeBruteForce(intArrayOf(1, 2, 5), 11)}")
    println("coinChange([2], 3) = ${coinChangeBruteForce(intArrayOf(2), 3)}")

    println("\n=== Method 2: Bottom-Up DP (Tabulation) ===")
    println("coinChange([1,2,5], 11) = ${coinChangeTabulation(intArrayOf(1, 2, 5), 11)}")
    println("coinChange([2], 3) = ${coinChangeTabulation(intArrayOf(2), 3)}")
    println("coinChange([1], 0) = ${coinChangeTabulation(intArrayOf(1), 0)}")

    println("\n=== Step-by-step trace ===")
    coinChangeTrace(intArrayOf(1, 2, 5), 11)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Recursion (try every combination)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — At each amount, try every coin. Take the minimum.
 *
 * Core Idea:
 *   - f(remaining) = min(1 + f(remaining - coin)) for each valid coin.
 *   - Base: f(0) = 0, f(negative) = impossible.
 *
 * Problem: Massive overlapping subproblems → exponential time.
 *
 * Time Complexity:  O(S^N) — S = amount, N = coins (exponential!)
 * Space Complexity: O(S) — recursion depth.
 */
fun coinChangeBruteForce(coins: IntArray, amount: Int): Int {
    if (amount == 0) return 0
    val result = coinHelper(coins, amount)
    return if (result == Int.MAX_VALUE) -1 else result
}

private fun coinHelper(coins: IntArray, remaining: Int): Int {
    if (remaining == 0) return 0
    if (remaining < 0) return Int.MAX_VALUE

    var minCoins = Int.MAX_VALUE
    for (coin in coins) {
        val sub = coinHelper(coins, remaining - coin)
        if (sub != Int.MAX_VALUE) {
            minCoins = minOf(minCoins, 1 + sub)
        }
    }
    return minCoins
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BOTTOM-UP DP (TABULATION) — OPTIMAL
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BOTTOM-UP DP — Build dp[0..amount] from smallest to largest.
 *
 * Core Idea:
 *   - dp[a] = minimum coins to make amount a.
 *   - dp[0] = 0 (base case).
 *   - For each amount a, try every coin: dp[a] = min(dp[a], 1 + dp[a - coin]).
 *   - Initialize dp with "infinity" (amount + 1) — impossible sentinel.
 *
 * Key Insight:
 *   - "To make amount A, what was the LAST coin used?"
 *   - If last coin was c, then before it, we had amount (A - c).
 *   - So dp[A] = 1 + dp[A - c]. Take the minimum over all coins.
 *
 * Time Complexity:  O(S × N) — S = amount, N = number of coins.
 * Space Complexity: O(S) — dp array.
 */
fun coinChangeTabulation(coins: IntArray, amount: Int): Int {
    if (amount == 0) return 0

    // dp[a] = min coins to make amount a. Initialize with "infinity".
    val dp = IntArray(amount + 1) { amount + 1 }
    dp[0] = 0  // Base case: 0 coins for amount 0.

    for (a in 1..amount) {
        for (coin in coins) {
            if (coin <= a) {
                dp[a] = minOf(dp[a], 1 + dp[a - coin])
            }
        }
    }

    // If dp[amount] is still "infinity", it's impossible.
    return if (dp[amount] > amount) -1 else dp[amount]
}

/**
 * Tabulation with step-by-step trace for learning/debugging.
 */
fun coinChangeTrace(coins: IntArray, amount: Int) {
    println("Input: coins=${coins.toList()}, amount=$amount")
    val dp = IntArray(amount + 1) { amount + 1 }
    dp[0] = 0
    println("  Initial dp: ${dp.toList()}")

    for (a in 1..amount) {
        for (coin in coins) {
            if (coin <= a) {
                val candidate = 1 + dp[a - coin]
                if (candidate < dp[a]) {
                    dp[a] = candidate
                    println("  a=$a | coin=$coin | dp[$a] = min(dp[$a], 1+dp[${a - coin}]) = $candidate")
                }
            }
        }
    }
    println("  Final dp: ${dp.toList()}")
    println("  Result: ${if (dp[amount] > amount) -1 else dp[amount]}")
}
