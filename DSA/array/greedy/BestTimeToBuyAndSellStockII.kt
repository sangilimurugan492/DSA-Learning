package array.greedy

/**
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/
 *
 * You are given an integer array prices where prices[i] is the price of a given stock on the ith day.
 * On each day, you may decide to buy and/or sell the stock. You can only hold at most one share
 * of the stock at any time. Find and return the maximum profit you can achieve.
 *
 * Example 1:
 *
 * Input: prices = [7,1,5,3,6,4]
 * Output: 7
 * Explanation: Buy on day 2 (price = 1) and sell on day 3 (price = 5), profit = 5-1 = 4.
 * Then buy on day 4 (price = 3) and sell on day 5 (price = 6), profit = 6-3 = 3.
 * Total profit is 4 + 3 = 7.
 *
 * Example 2:
 *
 * Input: prices = [1,2,3,4,5]
 * Output: 4
 * Explanation: Buy on day 1 (price = 1) and sell on day 5 (price = 5), profit = 5-1 = 4.
 * Total profit is 4.
 *
 * Example 3:
 *
 * Input: prices = [7,6,4,3,1]
 * Output: 0
 * Explanation: No profitable transactions possible.
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google, Microsoft)
 *
 * Key Insight: Capture every upward slope. If price tomorrow > price today, buy today and sell tomorrow.
 * This is equivalent to summing all positive differences between consecutive days.
 * Why? Because a continuous rise from day i to day j = sum of daily rises from i to j.
 */
fun main() {
    println(maxProfitII(intArrayOf(7, 1, 5, 3, 6, 4)))
    println(maxProfitII(intArrayOf(1, 2, 3, 4, 5)))
    println(maxProfitII(intArrayOf(7, 6, 4, 3, 1)))
    println(maxProfitIIPeakValley(intArrayOf(7, 1, 5, 3, 6, 4)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: Sum all positive differences.
 * If prices[i+1] > prices[i], add the difference to profit.
 *
 * Visual for [7,1,5,3,6,4]:
 *   7 ─╮
 *      │ ╭╯
 *   5 ─╯╭╯     Capture: (5-1) + (6-3) = 4 + 3 = 7
 *      │╭╯
 *   3 ─╯╭╯
 *      ╰╯
 *   1 ──╯    4
 *
 * This is equivalent to buying at every valley and selling at every peak.
 */
fun maxProfitII(prices: IntArray): Int {
    var profit = 0

    for (i in 0 until prices.size - 1) {
        if (prices[i + 1] > prices[i]) {
            profit += prices[i + 1] - prices[i]
        }
    }

    return profit
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: Peak-Valley approach (more intuitive).
 * Find valleys (local minima) to buy and peaks (local maxima) to sell.
 *
 * Trace for [7,1,5,3,6,4]:
 * i=0: 7→1, decreasing
 * i=1: 1→5, increasing → valley=1 (buy)
 * i=2: 5→3, decreasing → peak=5 (sell), profit += 5-1 = 4
 * i=3: 3→6, increasing → valley=3 (buy)
 * i=4: 6→4, decreasing → peak=6 (sell), profit += 6-3 = 3
 * Total profit = 7
 */
fun maxProfitIIPeakValley(prices: IntArray): Int {
    var profit = 0
    var i = 0

    while (i < prices.size - 1) {
        // Find next valley (local minimum)
        while (i < prices.size - 1 && prices[i] >= prices[i + 1]) {
            i++
        }
        val valley = prices[i]

        // Find next peak (local maximum)
        while (i < prices.size - 1 && prices[i] <= prices[i + 1]) {
            i++
        }
        val peak = prices[i]

        profit += peak - valley
    }

    return profit
}
