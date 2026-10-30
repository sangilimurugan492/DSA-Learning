package testrunner24

import testrunner.Week02Tests

/**
 * W24Week03Tests.kt — 24-Week Plan, Week 3: Kadane's & Prefix Sum — Fundamentals
 * 15 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week02Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week03Tests {
    private val src = Week02Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 3 — Kadane's & Prefix Sum — Fundamentals ━━━━━━━")
        src.runningSum() // 24W #1 Running Sum of 1D Array (old §2)
        src.prefixSumQuery() // 24W #2 Prefix Sum Query (old §3)
        src.pivotIndex() // 24W #3 Find Pivot Index (old §26)
        src.maxSubArray() // 24W #4 Maximum Subarray (Kadane's) (old §4)
        src.maxProductSubarray() // 24W #5 Maximum Product Subarray (old §8)
        src.maxSubarraySumCircular() // 24W #6 Maximum Sum Circular Subarray (old §9)
        src.productExceptSelf() // 24W #7 Product of Array Except Self (old §10)
        src.bestSightseeingPair() // 24W #8 Best Sightseeing Pair (old §21)
        src.subarraySumK() // 24W #9 Subarray Sum Equals K (old §11)
        src.contiguousArray() // 24W #10 Contiguous Array (Equal 0s & 1s) (old §12)
        src.longestSubarrayWithSumK() // 24W #11 Longest Subarray with Sum K (old §13)
        src.rangeSum2D() // 24W #12 Range Sum Query 2D (old §27)
        src.continuousSubarraySum() // 24W #13 Continuous Subarray Sum (old §28)
        src.maxAbsoluteSum() // 24W #14 Maximum Absolute Sum of Any Subarray (old §29)
        src.maxLenPositiveProduct() // 24W #15 Maximum Length of Subarray With Positive Product (old §30)
    }
}
