package stack.online_stock_span

/**
 * Online Stock Span — LeetCode #901
 * https://leetcode.com/problems/online-stock-span/
 *
 * Problem:
 * -------
 * Design StockSpanner: for each price, return span of consecutive days where
 * price was ≤ current day's price (including current day).
 *
 * Example:  [100,80,60,70,60,75,85] → [1,1,1,2,1,4,6]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Monotonic Stack application)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — for each day, look back until price > current
 * 2. Monotonic Stack: O(N) amortized — stack stores (price, span), pop and accumulate
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    val prices = intArrayOf(100, 80, 60, 70, 60, 75, 85)
    stockSpanBruteForce(prices).forEach { print("$it ") }
    println()

    println("\n=== Method 2: Monotonic Stack ===")
    val spanner = StockSpanner()
    println(spanner.next(100))  // 1
    println(spanner.next(80))   // 1
    println(spanner.next(60))   // 1
    println(spanner.next(70))   // 2
    println(spanner.next(60))   // 1
    println(spanner.next(75))   // 4
    println(spanner.next(85))   // 6
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each day, look back until price > current.
 *
 * Time Complexity:  O(N²).
 * Space Complexity: O(N) — result.
 */
fun stockSpanBruteForce(prices: IntArray): IntArray {
    val span = IntArray(prices.size)
    for (i in prices.indices) {
        var count = 1
        var j = i - 1
        while (j >= 0 && prices[j] <= prices[i]) {
            count++
            j--
        }
        span[i] = count
    }
    return span
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MONOTONIC STACK — O(N) amortized
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MONOTONIC STACK — Stack stores (price, span). When new price comes, pop all
 * smaller prices and accumulate their spans.
 *
 * Core Idea:
 *   - Stack maintains decreasing prices.
 *   - When new price ≥ stack top's price → pop and accumulate span.
 *   - The accumulated span represents consecutive days with price ≤ current.
 *
 * Key Insight:
 *   - We don't need to re-examine popped prices — their spans are absorbed.
 *   - Each element pushed/popped at most once → O(N) amortized.
 *
 * Time Complexity:  O(N) amortized — each element pushed/popped once.
 * Space Complexity: O(N) — stack.
 */
class StockSpanner {
    private val stack = ArrayDeque<Pair<Int, Int>>()  // (price, span)

    fun next(price: Int): Int {
        var span = 1
        while (stack.isNotEmpty() && stack.last().first <= price) {
            span += stack.removeLast().second
        }
        stack.addLast(Pair(price, span))
        return span
    }
}
