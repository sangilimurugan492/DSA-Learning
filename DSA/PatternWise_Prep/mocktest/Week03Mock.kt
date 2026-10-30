package mocktest

/**
 * Week03Mock.kt — MOCK TEST Week 3: HashMap + Stack
 * 5 problems · suggested time 105 min. Re-solve from scratch — no peeking at Week03Tests!
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 3 MOCK — HashMap + Stack (5 problems · 105 min) ━━━━━━━")
    mockCheck("M3 #1 Two Sum Unsorted (15 min)", block = { w3mTwoSumUnsorted(intArrayOf(3, 2, 4), 6).sorted() }, verify = { it == listOf(1, 2) })
    mockCheck("M3 #2 Valid Parentheses (15 min)", block = { w3mValidParens("([)]") }, verify = { !it })
    mockCheck("M3 #3 Longest Consecutive (25 min)", block = { w3mLongestConsecutive(intArrayOf(100, 4, 200, 1, 3, 2)) }, verify = { it == 4 })
    mockCheck("M3 #4 Evaluate RPN (20 min)", block = { w3mEvalRPN(arrayOf("2", "1", "+", "3", "*")) }, verify = { it == 9 })
    mockCheck("M3 #5 Largest Rectangle Histogram (30 min)", block = { w3mLargestRect(intArrayOf(2, 1, 5, 6, 2, 3)) }, verify = { it == 10 })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w3mTwoSumUnsorted(nums: IntArray, target: Int): IntArray = TODO()
fun w3mValidParens(s: String): Boolean = TODO()
fun w3mLongestConsecutive(nums: IntArray): Int = TODO()
fun w3mEvalRPN(tokens: Array<String>): Int = TODO()
fun w3mLargestRect(heights: IntArray): Int = TODO()
