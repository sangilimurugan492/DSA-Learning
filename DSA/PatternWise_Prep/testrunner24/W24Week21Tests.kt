package testrunner24

import testrunner.Week10Tests

/**
 * W24Week21Tests.kt — 24-Week Plan, Week 21: Advanced DP — 2D, Grids & Strings
 * 10 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week10Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week21Tests {
    private val src = Week10Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 21 — Advanced DP — 2D, Grids & Strings ━━━━━━━")
        src.decodeWays() // 24W #1 Decode Ways (old §13)
        src.decodeWays2()
        src.decodeWays3()
        src.wordBreakCase() // 24W #2 Word Break (old §14)
        src.wordBreakCase2()
        src.wordBreakCase3()
        src.minPathSumCase() // 24W #3 Minimum Path Sum (old §6)
        src.minPathSumCase2()
        src.minPathSumCase3()
        src.uniquePathsII() // 24W #4 Unique Paths II (old §7)
        src.uniquePathsII2()
        src.uniquePathsII3()
        src.lcs() // 24W #5 Longest Common Subsequence (old §2)
        src.lcs2()
        src.lcs3()
        src.palinSubseq() // 24W #6 Longest Palindromic Subsequence (old §3)
        src.palinSubseq2()
        src.palinSubseq3()
        src.interleave() // 24W #7 Interleaving String (old §5)
        src.interleave2()
        src.interleave3()
        src.editDistance() // 24W #8 Edit Distance (old §1)
        src.editDistance2()
        src.editDistance3()
        src.distinctSubseq() // 24W #9 Distinct Subsequences (old §4)
        src.distinctSubseq2()
        src.distinctSubseq3()
        src.maxSquare() // 24W #10 Maximal Square (old §8)
        src.maxSquare2()
        src.maxSquare3()
    }
}
