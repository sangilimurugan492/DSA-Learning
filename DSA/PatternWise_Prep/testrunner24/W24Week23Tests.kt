package testrunner24

import testrunner.Week11Tests

/**
 * W24Week23Tests.kt — 24-Week Plan, Week 23: Monotonic Stack — Complete Mastery
 * 11 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week11Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week23Tests {
    private val src = Week11Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 23 — Monotonic Stack — Complete Mastery ━━━━━━━")
        src.dailyTemps() // 24W #1 Daily Temperatures (old §1)
        src.dailyTemps2()
        src.dailyTemps3()
        src.nextGreater() // 24W #2 Next Greater Element II (old §2)
        src.nextGreater2()
        src.nextGreater3()
        src.stockSpan() // 24W #3 Online Stock Span (old §5)
        src.stockSpan2()
        src.stockSpan3()
        src.subMins() // 24W #4 Sum of Subarray Minimums (old §4)
        src.subMins2()
        src.subMins3()
        src.removeK() // 24W #5 Remove K Digits (old §6)
        src.removeK2()
        src.removeK3()
        src.pattern132() // 24W #6 132 Pattern (old §7)
        src.pattern132b()
        src.pattern132c()
        src.subRanges() // 24W #7 Sum of Subarray Ranges (old §8)
        src.subRanges2()
        src.subRanges3()
        src.maxChunks() // 24W #8 Max Chunks To Make Sorted (old §9)
        src.maxChunks2()
        src.maxChunks3()
        src.removeDup() // 24W #9 Remove All Adjacent Duplicates in String II (old §10)
        src.removeDup2()
        src.removeDup3()
        src.decode() // 24W #10 Decode String (old §20)
        src.decode2()
        src.decode3()
        src.histogram() // 24W #11 Largest Rectangle in Histogram (old §3)
        src.histogram2()
        src.histogram3()
    }
}
