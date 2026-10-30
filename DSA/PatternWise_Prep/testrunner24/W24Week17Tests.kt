package testrunner24

import testrunner.Week08Tests

/**
 * W24Week17Tests.kt — 24-Week Plan, Week 17: Graph — BFS / DFS / Topological Sort
 * 9 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week08Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week17Tests {
    private val src = Week08Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 17 — Graph — BFS / DFS / Topological Sort ━━━━━━━")
        src.numIslands() // 24W #1 Number of Islands (old §5)
        src.cloneGraphCase() // 24W #2 Clone Graph (old §6)
        src.pacificAtlantic() // 24W #3 Pacific Atlantic Water Flow (old §7)
        src.orangesRotting() // 24W #4 Rotting Oranges (old §8)
        src.canFinish() // 24W #5 Course Schedule (old §10)
        src.findOrder() // 24W #6 Course Schedule II (old §11)
        src.findCircleNum() // 24W #7 Number of Provinces (old §12)
        src.validTree() // 24W #8 Graph Valid Tree (old §13)
        src.ladderLength() // 24W #9 Word Ladder (old §22)
    }
}
