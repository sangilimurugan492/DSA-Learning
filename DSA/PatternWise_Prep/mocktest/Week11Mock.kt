package mocktest

/**
 * Week11Mock.kt — MOCK TEST Week 11: Monotonic Stack + Design
 * 5 problems · suggested time 135 min. Includes two design-class stubs.
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 11 MOCK — Monotonic Stack + Design (5 problems · 135 min) ━━━━━━━")
    mockCheck("M11 #1 Daily Temperatures (20 min)", block = { w11mDailyTemps(intArrayOf(73, 74, 75, 71, 69, 72, 76, 73)).toList() }, verify = { it == listOf(1, 1, 4, 2, 1, 1, 0, 0) })
    mockCheck("M11 #2 Largest Rectangle Histogram (30 min)", block = { w11mLargestRect(intArrayOf(2, 1, 5, 6, 2, 3)) }, verify = { it == 10 })
    mockCheck("M11 #3 Trapping Rain Water (30 min)", block = { w11mTrapRain(intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)) }, verify = { it == 6 })
    mockCheck("M11 #4 LRU Cache design (35 min)", block = { val c = W11mLRUCache(2); c.put(1, 1); c.put(2, 2); val a = c.get(1); c.put(3, 3); val b = c.get(2); listOf(a, b) }, verify = { it == listOf(1, -1) })
    mockCheck("M11 #5 Min Stack design (20 min)", block = { val s = W11mMinStack(); s.push(5); s.push(3); s.push(7); val a = s.getMin(); s.pop(); val b = s.top(); listOf(a, b) }, verify = { it == listOf(3, 3) })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w11mDailyTemps(temperatures: IntArray): IntArray = TODO()
fun w11mLargestRect(heights: IntArray): Int = TODO()
fun w11mTrapRain(heights: IntArray): Int = TODO()

class W11mLRUCache(private val capacity: Int) {
    fun get(key: Int): Int = TODO()
    fun put(key: Int, value: Int): Unit = TODO()
}

class W11mMinStack {
    fun push(x: Int): Unit = TODO()
    fun pop(): Unit = TODO()
    fun top(): Int = TODO()
    fun getMin(): Int = TODO()
}
