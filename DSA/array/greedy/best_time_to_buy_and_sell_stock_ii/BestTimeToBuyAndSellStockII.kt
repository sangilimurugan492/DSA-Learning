package array.greedy.best_time_to_buy_and_sell_stock_ii

/**
 * Best Time to Buy and Sell Stock II — LeetCode #122
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/
 *
 * Problem:
 * -------
 * You may buy and sell on different days, but hold at most one share at a time.
 * Find the maximum profit with unlimited transactions.
 *
 * Example:  prices = [7,1,5,3,6,4]  →  7  (buy 1, sell 5 → +4; buy 3, sell 6 → +3)
 *           prices = [1,2,3,4,5]  →  4  (buy 1, sell 5 → +4)
 *           prices = [7,6,4,3,1]  →  0  (no profit)
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google, Microsoft)
 *
 * Two approaches:
 * 1. Peak-Valley: O(N) — find local minima to buy, local maxima to sell
 * 2. Sum Positive Differences: O(N) — sum all prices[i+1] - prices[i] when positive
 */

fun main() {
    val prices = intArrayOf(7, 1, 5, 3, 6, 4)

    println("=== Method 1: Peak-Valley ===")
    println("maxProfit(${prices.toList()}) = ${maxProfitPeakValley(prices)}")

    println("\n=== Method 2: Sum Positive Differences ===")
    println("maxProfit(${prices.toList()}) = ${maxProfit(prices)}")

    println("\n=== Step-by-step trace ===")
    maxProfitTrace(prices)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: PEAK-VALLEY — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * PEAK-VALLEY — Find valleys (local minima) to buy, peaks (local maxima) to sell.
 *
 * Core Idea:
 *   - Buy at every local minimum, sell at the next local maximum.
 *   - Profit = sum of (peak - valley) for each cycle.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1).
 */
fun maxProfitPeakValley(prices: IntArray): Int {
    var profit = 0
    var i = 0

    while (i < prices.size - 1) {
        // Find next valley (local minimum).
        while (i < prices.size - 1 && prices[i] >= prices[i + 1]) i++
        val valley = prices[i]

        // Find next peak (local maximum).
        while (i < prices.size - 1 && prices[i] <= prices[i + 1]) i++
        val peak = prices[i]

        profit += peak - valley
    }
    return profit
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SUM POSITIVE DIFFERENCES — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SUM POSITIVE DIFFERENCES — If price goes up tomorrow, capture the gain.
 *
 * Core Idea:
 *   - If prices[i+1] > prices[i], add the difference to profit.
 *   - This is equivalent to buying at every valley and selling at every peak.
 *
 * Key Insight:
 *   - A continuous rise from day i to day j = sum of daily rises from i to j.
 *   - So summing all positive daily differences = total profit from all transactions.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1).
 */
fun maxProfit(prices: IntArray): Int {
    var profit = 0
    for (i in 0 until prices.size - 1) {
        if (prices[i + 1] > prices[i]) {
            profit += prices[i + 1] - prices[i]
        }
    }
    return profit
}

/**
 * Sum positive differences with step-by-step trace.
 */
fun maxProfitTrace(prices: IntArray) {
    println("Input: ${prices.toList()}")
    var profit = 0
    for (i in 0 until prices.size - 1) {
        val diff = prices[i + 1] - prices[i]
        if (diff > 0) {
            println("  day $i→${i+1}: ${prices[i]}→${prices[i+1]}, diff=+$diff → profit=$profit+$diff=${profit+diff}")
            profit += diff
        } else {
            println("  day $i→${i+1}: ${prices[i]}→${prices[i+1]}, diff=$diff → skip")
        }
    }
    println("  Result: $profit")
}
