package stack

/**
 * https://leetcode.com/problems/online-stock-span/
 * Design StockSpanner: for each price, return span of consecutive days where
 * price was <= current day's price (including current day).
 * Example: [100,80,60,70,60,75,85] → [1,1,1,2,1,4,6]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Monotonic Stack application)
 */

fun main() {
    val spanner = StockSpanner()
    println(spanner.next(100))  // 1
    println(spanner.next(80))   // 1
    println(spanner.next(60))   // 1
    println(spanner.next(70))   // 2
    println(spanner.next(60))   // 1
    println(spanner.next(75))   // 4
    println(spanner.next(85))   // 6
    println("---")
    // Brute force version
    val prices = intArrayOf(100, 80, 60, 70, 60, 75, 85)
    stockSpanBruteForce(prices).forEach { print("$it ") }
    println()
}

/**
 * BRUTE FORCE: O(N²) — for each day, look back until price > current
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

/**
 * OPTIMAL: O(N) amortized — Monotonic Stack
 * Stack stores (price, span). When new price comes, pop all smaller prices
 * and accumulate their spans.
 *
 * Trace for [100,80,60,70,60,75,85]:
 * 100: stack=[(100,1)] → span=1
 *  80: 80<100 → stack=[(100,1),(80,1)] → span=1
 *  60: 60<80 → stack=[(100,1),(80,1),(60,1)] → span=1
 *  70: 70>60 → pop(60,1), span=1+1=2. stack=[(100,1),(80,1),(70,2)]
 *  60: 60<70 → stack=[(100,1),(80,1),(70,2),(60,1)] → span=1
 *  75: 75>60 → pop(60,1), span=1+1=2. 75>70 → pop(70,2), span=2+2=4.
 *      stack=[(100,1),(80,1),(75,4)] → span=4
 *  85: 85>75 → pop(75,4), span=1+4=5. 85>80 → pop(80,1), span=5+1=6.
 *      stack=[(100,1),(85,6)] → span=6 ✅
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
