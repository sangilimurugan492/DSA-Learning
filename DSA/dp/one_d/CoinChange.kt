package dp.one_d

/**
 * https://leetcode.com/problems/coin-change/
 *
 * You are given coins of different denominations and a total amount.
 * Return the fewest number of coins needed to make up that amount.
 * Return -1 if it cannot be made up by any combination.
 *
 * Example 1: coins = [1,2,5], amount = 11 → Output: 3 (5+5+1)
 * Example 2: coins = [2], amount = 3 → Output: -1
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic unbounded knapsack / minimization DP)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is an UNBOUNDED knapsack variant — you can use each coin unlimited times.
 *
 * Key question: "To make amount A, what was the LAST coin I used?"
 *   → If last coin was c, then before adding it, I had amount (A - c)
 *   → So: dp[A] = 1 + dp[A - c] for each coin c
 *   → Take the MINIMUM over all valid coins
 *
 * Recurrence: dp[a] = min(dp[a - coin] + 1) for each coin where a >= coin
 *
 * Base case: dp[0] = 0 (0 coins needed to make amount 0)
 *
 * WHY does this work? We're building from smaller amounts to larger amounts.
 * By the time we compute dp[a], all dp[a-coin] values are already known.
 * Each dp[a-coin] is optimal by induction → dp[a] is optimal.
 *
 * This is DIFFERENT from Climbing Stairs/House Robber:
 *   - Climbing Stairs: COUNT ways (sum)
 *   - House Robber: MAXIMIZE value (max)
 *   - Coin Change: MINIMIZE count (min)
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Coin Change ===")
    println("Brute Force [1,2,5],11: ${coinChangeBruteForce(intArrayOf(1, 2, 5), 11)}")
    println("Memoization [1,2,5],11: ${coinChangeMemo(intArrayOf(1, 2, 5), 11)}")
    println("Tabulation  [1,2,5],11: ${coinChangeTabulation(intArrayOf(1, 2, 5), 11)}")
    println("---")
    println("Optimal [2],3:          ${coinChangeTabulation(intArrayOf(2), 3)}")
    println("Optimal [1],0:          ${coinChangeTabulation(intArrayOf(1), 0)}")
}

/**
 * BRUTE FORCE — Recursion (try every combination)
 * Time Complexity: O(S^N) — S = amount, N = number of coins (exponential!)
 * Space Complexity: O(S) — recursion depth
 *
 * At each amount, try every coin. If coin <= remaining amount, recurse.
 * Take the minimum across all valid choices.
 *
 * Recursion tree for coins=[1,2,5], amount=6:
 *                        f(6)
 *                   /      |      \
 *               f(5)     f(4)     f(1)
 *             / | \     / | \      |
 *          f(4) f(3) f(0) ...     f(0)
 *         / | \
 *       f(3) f(2) f(-1)←invalid
 *      / | \
 *    f(2) f(1) f(-3)←invalid
 *   / | \
 * f(1) f(0) f(-3)
 *  |
 * f(0)
 *
 * MASSIVE overlap! f(3) computed many times, f(2) even more.
 * This is why brute force is impractical for amount > ~30.
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

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(S × N) — S = amount, N = number of coins
 * Space Complexity: O(S) — memo + recursion stack
 *
 * Cache result for each remaining amount. Never recompute.
 *
 * Trace for coins=[1,2,5], amount=11:
 * f(11) → try coin 1: 1+f(10), coin 2: 1+f(9), coin 5: 1+f(6)
 *   f(10) → try coin 1: 1+f(9), coin 2: 1+f(8), coin 5: 1+f(5)
 *     ... (builds down, caching each value)
 *   f(5) → try coin 1: 1+f(4)=1+4=5, coin 2: 1+f(3)=1+2=3, coin 5: 1+f(0)=1+0=1
 *   f(5) = min(5, 3, 1) = 1 ✅ (one coin of 5)
 *   f(6) → coin 5: 1+f(1)=1+1=2, coin 2: 1+f(4)=1+4=5, coin 1: 1+f(5)=1+1=2
 *   f(6) = min(2, 5, 2) = 2 ✅
 *   ... continuing up ...
 * f(11) = 3 ✅ (5+5+1)
 */
fun coinChangeMemo(coins: IntArray, amount: Int): Int {
    val memo = IntArray(amount + 1) { -2 }  // -2 = uncomputed, -1 = impossible
    val result = coinMemoHelper(coins, amount, memo)
    return if (result == Int.MAX_VALUE) -1 else result
}

private fun coinMemoHelper(coins: IntArray, remaining: Int, memo: IntArray): Int {
    if (remaining == 0) return 0
    if (remaining < 0) return Int.MAX_VALUE
    if (memo[remaining] != -2) return if (memo[remaining] == -1) Int.MAX_VALUE else memo[remaining]

    var minCoins = Int.MAX_VALUE
    for (coin in coins) {
        val sub = coinMemoHelper(coins, remaining - coin, memo)
        if (sub != Int.MAX_VALUE) {
            minCoins = minOf(minCoins, 1 + sub)
        }
    }
    memo[remaining] = if (minCoins == Int.MAX_VALUE) -1 else minCoins
    return minCoins
}

/**
 * OPTIMAL — Bottom-Up DP (Tabulation)
 * Time Complexity: O(S × N) — S = amount, N = number of coins
 * Space Complexity: O(S)
 *
 * Build dp[0..amount] from bottom up.
 * dp[a] = min coins to make amount a.
 *
 * For each amount a, try every coin: dp[a] = min(dp[a], 1 + dp[a - coin])
 *
 * Trace for coins=[1,2,5], amount=11:
 * dp = [0, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞]
 *
 * a=1: coin=1: dp[1]=min(∞,1+dp[0])=1
 * a=2: coin=1: dp[2]=min(∞,1+dp[1])=2, coin=2: dp[2]=min(2,1+dp[0])=1
 * a=3: coin=1: dp[3]=min(∞,1+dp[2])=2, coin=2: dp[3]=min(2,1+dp[1])=2
 * a=4: coin=1: dp[4]=min(∞,1+dp[3])=3, coin=2: dp[4]=min(3,1+dp[2])=2
 * a=5: coin=1: dp[5]=min(∞,1+dp[4])=3, coin=2: dp[5]=min(3,1+dp[3])=3, coin=5: dp[5]=min(3,1+dp[0])=1
 * a=6: coin=1: dp[6]=min(∞,1+dp[5])=2, coin=2: dp[6]=min(2,1+dp[4])=3, coin=5: dp[6]=min(2,1+dp[1])=2
 * ...
 * a=10: coin=5: dp[10]=min(∞,1+dp[5])=2
 * a=11: coin=1: dp[11]=min(∞,1+dp[10])=3, coin=2: dp[11]=min(3,1+dp[9])=3, coin=5: dp[11]=min(3,1+dp[6])=3
 *
 * dp[11] = 3 ✅ (5+5+1)
 */
fun coinChangeTabulation(coins: IntArray, amount: Int): Int {
    if (amount == 0) return 0
    val dp = IntArray(amount + 1) { amount + 1 }  // amount+1 = "infinity" (impossible)
    dp[0] = 0

    for (a in 1..amount) {
        for (coin in coins) {
            if (coin <= a) {
                dp[a] = minOf(dp[a], 1 + dp[a - coin])
            }
        }
    }

    return if (dp[amount] > amount) -1 else dp[amount]
}
