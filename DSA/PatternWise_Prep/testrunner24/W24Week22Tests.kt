package testrunner24

import testrunner.Week10Tests

/**
 * W24Week22Tests.kt — 24-Week Plan, Week 22: Advanced DP — Interval DP & Hard Grids
 * 10 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week10Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week22Tests {
    private val src = Week10Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 22 — Advanced DP — Interval DP & Hard Grids ━━━━━━━")
        src.palinSubstrings() // 24W #1 Palindromic Substrings (old §12)
        src.palinSubstrings2()
        src.palinSubstrings3()
        src.perfectSquares() // 24W #2 Perfect Squares (old §15)
        src.perfectSquares2()
        src.perfectSquares3()
        src.coinChangeII() // 24W #3 Coin Change II (old §16)
        src.coinChangeII2()
        src.coinChangeII3()
        src.divisibleSubset() // 24W #4 Largest Divisible Subset (old §19)
        src.divisibleSubset2()
        src.divisibleSubset3()
        src.countLIS() // 24W #5 Number of Longest Increasing Subsequence (old §20)
        src.countLIS2()
        src.countLIS3()
        src.maxRectangle() // 24W #6 Maximal Rectangle (old §9)
        src.maxRectangle2()
        src.maxRectangle3()
        src.cherryPickupCase() // 24W #7 Cherry Pickup II (old §10)
        src.cherryPickupCase2()
        src.cherryPickupCase3()
        src.burstBalloons() // 24W #8 Burst Balloons (old §11)
        src.burstBalloons2()
        src.burstBalloons3()
        src.longestIncPath() // 24W #9 Longest Increasing Path in a Matrix (old §17)
        src.longestIncPath2()
        src.longestIncPath3()
        src.dungeon() // 24W #10 Dungeon Game (old §18)
        src.dungeon2()
        src.dungeon3()
    }
}
