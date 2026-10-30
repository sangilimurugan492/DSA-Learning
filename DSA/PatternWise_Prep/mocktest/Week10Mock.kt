package mocktest

/**
 * Week10Mock.kt — MOCK TEST Week 10: Advanced DP (2D / Strings / Grids)
 * 5 problems · suggested time 120 min. Re-solve from scratch — no peeking at Week10Tests!
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 10 MOCK — Advanced DP (5 problems · 120 min) ━━━━━━━")
    mockCheck("M10 #1 Min Path Sum (20 min)", block = { w10mMinPathSum(arrayOf(intArrayOf(1, 3, 1), intArrayOf(1, 5, 1), intArrayOf(4, 2, 1))) }, verify = { it == 7 })
    mockCheck("M10 #2 Longest Common Subsequence (25 min)", block = { w10mLCS("abcde", "ace") }, verify = { it == 3 })
    mockCheck("M10 #3 Edit Distance (30 min)", block = { w10mEditDistance("horse", "ros") }, verify = { it == 3 })
    mockCheck("M10 #4 Decode Ways (25 min)", block = { w10mDecode("226") }, verify = { it == 3 })
    mockCheck("M10 #5 Word Break (25 min)", block = { w10mWordBreak("leetcode", listOf("leet", "code")) }, verify = { it })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w10mMinPathSum(grid: Array<IntArray>): Int = TODO()
fun w10mLCS(text1: String, text2: String): Int = TODO()
fun w10mEditDistance(word1: String, word2: String): Int = TODO()
fun w10mDecode(s: String): Int = TODO()
fun w10mWordBreak(s: String, wordDict: List<String>): Boolean = TODO()
