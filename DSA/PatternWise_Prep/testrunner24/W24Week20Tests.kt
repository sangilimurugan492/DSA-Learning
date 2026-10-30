package testrunner24

import testrunner.Week09Tests

/**
 * W24Week20Tests.kt — 24-Week Plan, Week 20: Advanced Graphs — Topo Sort + Intervals II
 * 8 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week09Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week20Tests {
    private val src = Week09Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 20 — Advanced Graphs — Topo Sort + Intervals II ━━━━━━━")
        src.courseScheduleIV() // 24W #1 Course Schedule IV (old §13)
        src.minHeightTrees() // 24W #2 Minimum Height Trees (old §15)
        src.swimWater() // 24W #3 Swim in Rising Water (old §5)
        src.mergeIntervals() // 24W #4 Merge Intervals (old §16)
        src.insertInterval() // 24W #5 Insert Interval (old §17)
        src.eraseOverlap() // 24W #6 Non-overlapping Intervals (old §18)
        src.meetingRoomsII() // 24W #7 Meeting Rooms II (old §19)
        src.intervalIntersect() // 24W #8 Interval List Intersections (old §20)
    }
}
