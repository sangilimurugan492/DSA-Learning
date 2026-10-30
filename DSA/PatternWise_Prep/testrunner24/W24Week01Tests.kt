package testrunner24

import testrunner.Week01Tests

/**
 * W24Week01Tests.kt — 24-Week Plan, Week 1: Two Pointer — Fundamentals (Easy → Medium)
 * 11 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week01Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week01Tests {
    private val src = Week01Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 1 — Two Pointer — Fundamentals (Easy → Medium) ━━━━━━━")
        src.validPalindrome() // 24W #1 Valid Palindrome (old §2)
        src.isSubsequence() // 24W #2 Is Subsequence (old §3)
        src.moveZeroes() // 24W #3 Move Zeros to End (old §4)
        src.mergeSortedArray() // 24W #4 Merge Sorted Array (old §5)
        src.removeElement() // 24W #5 Remove Element in Array (old §6)
        src.intersectII() // 24W #6 Intersection of Two Arrays II (old §7)
        src.removeDuplicatesI() // 24W #7 Remove Duplicates from Sorted Array (old §18)
        src.twoSumSorted() // 24W #8 Two Sum (Sorted) (old §1)
        src.threeSum() // 24W #9 Three Sum (old §8)
        src.threeSumClosest() // 24W #10 Three Sum Closest (old §9)
        src.fourSum() // 24W #11 Four Sum (old §10)
    }
}
