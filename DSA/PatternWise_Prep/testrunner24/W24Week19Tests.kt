package testrunner24

import testrunner.Week09Tests

/**
 * W24Week19Tests.kt — 24-Week Plan, Week 19: Advanced Graphs — Dijkstra + Union-Find
 * 12 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week09Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week19Tests {
    private val src = Week09Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 19 — Advanced Graphs — Dijkstra + Union-Find ━━━━━━━")
        src.networkDelay() // 24W #1 Network Delay Time (old §1)
        src.cheapestFlights() // 24W #2 Cheapest Flights Within K Stops (old §2)
        src.minEffort() // 24W #3 Path With Minimum Effort (old §3)
        src.maxProb() // 24W #4 Path With Maximum Probability (old §4)
        src.redundantConn() // 24W #5 Redundant Connection (old §6)
        src.redundantConnII() // 24W #6 Redundant Connection II (old §7)
        src.networkConnected() // 24W #7 Number of Operations to Make Network Connected (old §8)
        src.removeStones() // 24W #8 Most Stones Removed with Same Row or Column (old §9)
        src.accountsMerge() // 24W #9 Accounts Merge (old §10)
        src.regionsSlashes() // 24W #10 Regions Cut By Slashes (old §11)
        src.connectPoints() // 24W #11 Min Cost to Connect All Points (old §12)
        src.alienDict() // 24W #12 Alien Dictionary (old §14)
    }
}
