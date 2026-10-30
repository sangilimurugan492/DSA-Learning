package testrunner24

import testrunner.Week01Tests

/**
 * W24Week02Tests.kt — 24-Week Plan, Week 2: Two Pointer — Advanced + In-Place
 * 14 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week01Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week02Tests {
    private val src = Week01Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 2 — Two Pointer — Advanced + In-Place ━━━━━━━")
        src.backspaceCompare() // 24W #1 Backspace String Compare (old §13)
        src.twoSumLessThanK() // 24W #2 Two Sum Less Than K (old §14)
        src.kSumPairs() // 24W #3 K-Sum Pairs (old §15)
        src.boatsToSavePeople() // 24W #4 Boats to Save People (old §16)
        src.containerWithMostWater() // 24W #5 Container With Most Water (old §11)
        src.removeDuplicatesII() // 24W #6 Remove Duplicates II (Keep Two) (old §19)
        src.replaceGreatestRight() // 24W #7 Replace with Greatest on Right (old §20)
        src.sortedSquares() // 24W #8 Sorted Squares (old §21)
        src.rotateArray() // 24W #9 Rotate Array (old §22)
        src.nextPermutation() // 24W #10 Next Permutation (old §23)
        src.sortColors() // 24W #11 Sort Colors (Dutch National Flag) (old §12)
        src.sortColorsRe() // 24W #12 Sort Colors (old §24)
        src.removeElementRe() // 24W #13 Remove Element (old §25)
        src.trappingRainWater() // 24W #14 Trapping Rain Water (old §17)
    }
}
