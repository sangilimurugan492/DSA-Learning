package testrunner24

import testrunner.Week06Tests

/**
 * W24Week14Tests.kt — 24-Week Plan, Week 14: Greedy — Advanced + Hard
 * 7 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week06Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week14Tests {
    private val src = Week06Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 14 — Greedy — Advanced + Hard ━━━━━━━")
        src.leastInterval() // 24W #1 Task Scheduler (old §17)
        src.partitionLabels() // 24W #2 Partition Labels (old §18)
        src.reconstructQueue() // 24W #3 Queue Reconstruction by Height (old §24)
        src.wiggleMaxLength() // 24W #4 Wiggle Subsequence (old §25)
        src.candy() // 24W #5 Candy (old §26)
        src.medianTwoSorted() // 24W #6 Median of Two Sorted Arrays (old §27)
        src.majorityElement() // 24W #7 Majority Element I & II (old §28)
    }
}
