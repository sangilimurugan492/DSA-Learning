package testrunner24

import testrunner.Week07Tests

/**
 * W24Week15Tests.kt — 24-Week Plan, Week 15: Backtracking + DP Fundamentals
 * 11 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week07Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week15Tests {
    private val src = Week07Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 15 — Backtracking + DP Fundamentals ━━━━━━━")
        src.climbStairs() // 24W #1 Climbing Stairs (old §1)
        src.minCostClimbing() // 24W #2 Min Cost Climbing Stairs (old §2)
        src.subsets() // 24W #3 Subsets (old §3)
        src.combinationSum() // 24W #4 Combination Sum (old §4)
        src.permute() // 24W #5 Permutations (old §5)
        src.wordSearch() // 24W #6 Word Search (old §6)
        src.palindromePartition() // 24W #7 Palindrome Partitioning (old §21)
        src.nQueens() // 24W #8 N-Queens (old §22)
        src.sudoku() // 24W #9 Sudoku Solver (old §23)
        src.houseRobber() // 24W #10 House Robber (old §7)
        src.houseRobberII() // 24W #11 House Robber II (Circular) (old §8)
    }
}
