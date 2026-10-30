package mocktest

/**
 * Week09Mock.kt — MOCK TEST Week 9: Advanced Graphs + Intervals
 * 5 problems · suggested time 135 min. Re-solve from scratch — no peeking at Week09Tests!
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 9 MOCK — Advanced Graphs + Intervals (5 problems · 135 min) ━━━━━━━")
    mockCheck("M9 #1 Network Delay Time (25 min)", block = { w9mNetworkDelay(arrayOf(intArrayOf(2, 1, 1), intArrayOf(2, 3, 1), intArrayOf(3, 4, 1)), 4, 2) }, verify = { it == 2 })
    mockCheck("M9 #2 Cheapest Flights K Stops (30 min)", block = { w9mCheapestFlights(4, arrayOf(intArrayOf(0, 1, 100), intArrayOf(1, 2, 100), intArrayOf(0, 2, 500)), 0, 2, 1) }, verify = { it == 200 })
    mockCheck("M9 #3 Most Stones Removed (25 min)", block = { w9mRemoveStones(arrayOf(intArrayOf(0, 0), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(1, 2), intArrayOf(2, 1), intArrayOf(2, 2))) }, verify = { it == 5 })
    mockCheck("M9 #4 Non-overlapping Intervals (20 min)", block = { w9mEraseOverlap(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(1, 3))) }, verify = { it == 1 })
    mockCheck("M9 #5 Alien Dictionary (35 min)", block = { w9mAlienOrder(listOf("z", "x")) }, verify = { it == "zx" })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w9mNetworkDelay(times: Array<IntArray>, n: Int, k: Int): Int = TODO()
fun w9mCheapestFlights(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int = TODO()
fun w9mRemoveStones(stones: Array<IntArray>): Int = TODO()
fun w9mEraseOverlap(intervals: Array<IntArray>): Int = TODO()
fun w9mAlienOrder(words: List<String>): String = TODO()
