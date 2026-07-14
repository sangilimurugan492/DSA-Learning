package array.greedy.best_time_to_buy_and_sell_stock_i

/**
 * Best Time to Buy and Sell Stock I — LeetCode #121
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 *
 * Problem:
 * -------
 * Given prices[], choose one day to buy and a different future day to sell.
 * Return the maximum profit. If no profit possible, return 0.
 *
 * Example:  prices = [7,1,5,3,6,4]  →  5  (buy at 1, sell at 6)
 *           prices = [7,6,4,3,1]  →  0  (no profit)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Greedy)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — try every buy/sell pair
 * 2. Greedy (One Pass): O(N) — track min price, compute max profit
 */

fun main() {
    val prices = intArrayOf(7, 1, 5, 3, 6, 4)

    println("=== Method 1: Brute Force ===")
    println("maxProfit(${prices.toList()}) = ${maxProfitBF(prices)}")

    println("\n=== Method 2: Greedy (One Pass) ===")
    println("maxProfit(${prices.toList()}) = ${maxProfit(prices)}")

    println("\n=== Step-by-step trace ===")
    maxProfitTrace(prices)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Try every buy/sell pair. Track max profit.
 *
 * Time Complexity:  O(N²) — nested loops.
 * Space Complexity: O(1).
 */
fun maxProfitBF(prices: IntArray): Int {
    var maxProfit = 0
    for (i in prices.indices) {
        for (j in i + 1 until prices.size) {
            val profit = prices[j] - prices[i]
            if (profit > maxProfit) maxProfit = profit
        }
    }
    return maxProfit
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: GREEDY (ONE PASS) — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY — Track the minimum price seen so far. At each day, compute profit if sold today.
 *
 * Core Idea:
 *   - minPrice = lowest price seen so far (best day to buy).
 *   - At each day i: profit = prices[i] - minPrice. Update maxProfit.
 *   - Update minPrice if prices[i] is lower.
 *
 * Key Insight:
 *   - The best profit = sell at current price - buy at the lowest price before it.
 *   - We don't need to know which day to buy — just the minimum price so far.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1).
 */
fun maxProfit(prices: IntArray): Int {
    var maxProfit = 0
    var minPrice = prices[0]

    for (i in 1 until prices.size) {
        val profit = prices[i] - minPrice
        if (prices[i] < minPrice) minPrice = prices[i]
        if (profit > maxProfit) maxProfit = profit
    }
    return maxProfit
}

/**
 * Greedy with step-by-step trace.
 */
fun maxProfitTrace(prices: IntArray) {
    println("Input: ${prices.toList()}")
    var maxProfit = 0
    var minPrice = prices[0]

    for (i in 1 until prices.size) {
        val profit = prices[i] - minPrice
        println("  day=$i: price=${prices[i]}, minPrice=$minPrice, profit=$profit, maxProfit=$maxProfit")
        if (prices[i] < minPrice) minPrice = prices[i]
        if (profit > maxProfit) maxProfit = profit
    }
    println("  Result: $maxProfit")
}
