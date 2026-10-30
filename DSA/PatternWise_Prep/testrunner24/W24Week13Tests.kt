package testrunner24

import testrunner.Week06Tests

/**
 * W24Week13Tests.kt — 24-Week Plan, Week 13: Intervals + Binary Search + Bit Medium
 * 10 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week06Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week13Tests {
    private val src = Week06Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 13 — Intervals + Binary Search + Bit Medium ━━━━━━━")
        src.mergeIntervals() // 24W #1 Merge Intervals (old §12)
        src.insertInterval() // 24W #2 Insert Interval (old §13)
        src.canAttendMeetings() // 24W #3 Meeting Rooms (old §14)
        src.eraseOverlapIntervals() // 24W #4 Non-Overlapping Intervals (old §15)
        src.findMinArrowShots() // 24W #5 Min Arrows to Burst Balloons (old §16)
        src.searchRotated() // 24W #6 Search in Rotated Sorted Array (old §19)
        src.findMinRotated() // 24W #7 Find Min in Rotated Sorted Array (old §20)
        src.singleNumberII() // 24W #8 Single Number II (old §21)
        src.getSum() // 24W #9 Sum of Two Integers (old §22)
        src.countBits() // 24W #10 Counting Bits (old §23)
    }
}
