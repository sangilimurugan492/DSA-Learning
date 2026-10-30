package testrunner24

import testrunner.Week07Tests

/**
 * W24Week16Tests.kt — 24-Week Plan, Week 16: DP — 1-D & Knapsack
 * 8 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week07Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week16Tests {
    private val src = Week07Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 16 — DP — 1-D & Knapsack ━━━━━━━")
        src.coinChange() // 24W #1 Coin Change (Min Coins) (old §9)
        src.coinChangeII() // 24W #2 Coin Change II (Combinations) (old §10)
        src.decodeWays() // 24W #3 Decode Ways (old §11)
        src.wordBreak() // 24W #4 Word Break (old §12)
        src.minPathSum() // 24W #5 Minimum Path Sum (old §14)
        src.lis() // 24W #6 Longest Increasing Subsequence (old §18)
        src.canPartition() // 24W #7 Partition Equal Subset Sum (old §19)
        src.targetSum() // 24W #8 Target Sum (old §20)
    }
}
