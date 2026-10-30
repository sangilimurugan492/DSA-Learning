package testrunner24

import testrunner.Week03Tests

/**
 * W24Week05Tests.kt — 24-Week Plan, Week 5: HashMap & HashSet — Easy → Medium
 * 14 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week03Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week05Tests {
    private val src = Week03Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 5 — HashMap & HashSet — Easy → Medium ━━━━━━━")
        src.containsDuplicate() // 24W #1 Contains Duplicates (old §1)
        src.checkIfExist() // 24W #2 Check if N and 2N Exist (old §2)
        src.countDistinct() // 24W #3 Count Distinct Elements (old §3)
        src.isAnagram() // 24W #4 Valid Anagrams (old §4)
        src.intersection() // 24W #5 Intersection of Two Arrays (old §5)
        src.findDifference() // 24W #6 Find Difference Between Two Arrays (old §6)
        src.twoSumUnsorted() // 24W #7 Two Sum (Unsorted) (old §7)
        src.smallerNumbersThanCurrent() // 24W #8 How Many Numbers Are Smaller Than the Current Number (old §27)
        src.twoSumSortedW3() // 24W #9 Two Sum II (Sorted) (old §9)
        src.findingSumPairs() // 24W #10 Finding Pairs with Certain Sum (old §10)
        src.longestConsecutive() // 24W #11 Longest Consecutive Sequence (old §11)
        src.validSudoku() // 24W #12 Valid Sudoku (old §12)
        src.groupAnagrams() // 24W #13 Group Anagrams (old §13)
        src.topKFrequent() // 24W #14 Top K Frequent Elements (old §16)
    }
}
