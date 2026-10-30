package mocktest

/**
 * Week02Mock.kt — MOCK TEST Week 2: Sliding Window + Prefix Sum
 * 5 problems · suggested time 130 min. Re-solve from scratch — no peeking at Week02Tests!
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 2 MOCK — Sliding Window + Prefix Sum (5 problems · 130 min) ━━━━━━━")
    mockCheck("M2 #1 Max Average Subarray (15 min)", block = { w2mMaxAverage(intArrayOf(1, 12, -5, -6, 50, 3), 4) }, verify = { kotlin.math.abs(it - 12.75) < 1e-9 })
    mockCheck("M2 #2 Longest Substring No Repeat (25 min)", block = { w2mLongestNoRepeat("abcabcbb") }, verify = { it == 3 })
    mockCheck("M2 #3 Character Replacement (30 min)", block = { w2mCharReplace("AABABBA", 1) }, verify = { it == 4 })
    mockCheck("M2 #4 Circular Max Subarray (25 min)", block = { w2mCircularMax(intArrayOf(5, -3, 5)) }, verify = { it == 10 })
    mockCheck("M2 #5 Min Window Substring (35 min)", block = { w2mMinWindow("ADOBECODEBANC", "ABC") }, verify = { it == "BANC" })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w2mMaxAverage(nums: IntArray, k: Int): Double = TODO()
fun w2mLongestNoRepeat(s: String): Int = TODO()
fun w2mCharReplace(s: String, k: Int): Int = TODO()
fun w2mCircularMax(nums: IntArray): Int = TODO()
fun w2mMinWindow(s: String, t: String): String = TODO()
