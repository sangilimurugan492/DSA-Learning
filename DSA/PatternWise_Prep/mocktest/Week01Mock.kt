package DSA.PatternWise_Prep.mocktest

/**
 * Week01Mock.kt — MOCK TEST Week 1: Two Pointer
 * 5 problems · suggested time 120 min total. Re-solve from scratch — no peeking at Week01Tests!
 * Run:  kotlinc MockTestRunner.kt Week01Mock.kt -d out && kotlin -cp out Week01MockKt
 * Stub names are w1m-prefixed so all 12 mock files can compile together.
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 1 MOCK — Two Pointer (5 problems · 120 min) ━━━━━━━")
    mockCheck("M1 #1 Two Sum II (25 min)", block = { w1mTwoSumSorted(intArrayOf(2, 7, 11, 15), 9).sorted() }, verify = { it == listOf(0, 1) })
    mockCheck("M1 #2 Valid Palindrome (20 min)", block = { w1mIsPalindrome("A man, a plan, a canal: Panama") }, verify = { it })
    mockCheck("M1 #3 Container With Most Water (25 min)", block = { w1mMaxArea(intArrayOf(1, 8, 6, 2, 5, 4, 8, 3, 7)) }, verify = { it == 49 })
    mockCheck("M1 #4 3Sum (30 min)", block = { w1mThreeSum(intArrayOf(-1, 0, 1, 2, -1, -4)).size }, verify = { it == 2 })
    mockCheck("M1 #5 Next Permutation (20 min)", block = { val a = intArrayOf(1, 2, 3); w1mNextPermutation(a); a.toList() }, verify = { it == listOf(1, 3, 2) })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w1mTwoSumSorted(numbers: IntArray, target: Int): IntArray = TODO()
fun w1mIsPalindrome(s: String): Boolean = TODO()
fun w1mMaxArea(heights: IntArray): Int = TODO()
fun w1mThreeSum(nums: IntArray): List<List<Int>> = TODO()
fun w1mNextPermutation(nums: IntArray): Unit = TODO()
