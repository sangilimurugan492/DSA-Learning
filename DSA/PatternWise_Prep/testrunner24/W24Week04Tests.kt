package testrunner24

import testrunner.Week02Tests

/**
 * W24Week04Tests.kt — 24-Week Plan, Week 4: Sliding Window — Fundamentals → Advanced
 * 15 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week02Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week04Tests {
    private val src = Week02Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 4 — Sliding Window — Fundamentals → Advanced ━━━━━━━")
        src.maxAverageSubarray() // 24W #1 Max Average Subarray (Fixed K) (old §1)
        src.totalFruit() // 24W #2 Fruits into Baskets (old §5)
        src.longestSubarray1s() // 24W #3 Longest Subarray After Deleting One (old §6)
        src.grumpyOwner() // 24W #4 Grumpy Bookstore Owner (old §7)
        src.maxPointsFromCards() // 24W #5 Maximum Points You Can Obtain from Cards (old §23)
        src.maxErasureValue() // 24W #6 Maximum Erasure Value (old §24)
        src.binarySubarraysWithSum() // 24W #7 Binary Subarrays With Sum (old §22)
        src.longestSubstringNoRepeat() // 24W #8 Longest Substring Without Repeating Characters (old §14)
        src.characterReplacement() // 24W #9 Longest Repeating Character Replacement (old §15)
        src.maxOnesIII() // 24W #10 Max Consecutive Ones III (old §19)
        src.permutationInString() // 24W #11 Permutation in String (old §20)
        src.minSubArrayLen() // 24W #12 Min Size Subarray Sum (old §16)
        src.findAnagrams() // 24W #13 Find All Anagrams in String (old §17)
        src.minWindow() // 24W #14 Minimum Window Substring (old §18)
        src.slidingWindowMax() // 24W #15 Sliding Window Maximum (old §25)
    }
}
