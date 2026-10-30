package testrunner24

import testrunner.Week03Tests

/**
 * W24Week06Tests.kt — 24-Week Plan, Week 6: HashMap Wrap-up + Stack
 * 14 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week03Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week06Tests {
    private val src = Week03Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 6 — HashMap Wrap-up + Stack ━━━━━━━")
        src.isValidParens() // 24W #1 Valid Parentheses (old §8)
        src.countKDifference() // 24W #2 Count the Number of Pairs (old §14)
        src.countFairPairs() // 24W #3 Finding Fair Pairs (old §15)
        src.minStack() // 24W #4 Min Stack (old §17)
        src.nextGreaterElementI() // 24W #5 Next Greater Element I (old §18)
        src.nextGreaterElementII() // 24W #6 Next Greater Element II (Circular) (old §19)
        src.dailyTemperatures() // 24W #7 Daily Temperatures (old §20)
        src.stockSpanner() // 24W #8 Online Stock Span (old §21)
        src.evalRPN() // 24W #9 Evaluate Reverse Polish Notation (old §22)
        src.minRemoveToMakeValid() // 24W #10 Minimum Parentheses to Remove (old §23)
        src.carFleet() // 24W #11 Car Fleet (old §24)
        src.longestValidParentheses() // 24W #12 Longest Valid Parentheses (old §25)
        src.largestRectangle() // 24W #13 Largest Rectangle in Histogram (old §26)
        src.getCollisionTimes() // 24W #14 Car Fleet II (old §28)
    }
}
