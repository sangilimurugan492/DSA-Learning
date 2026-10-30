package testrunner

/**
 * Week09Tests.kt — Week 9: Advanced Graphs (Dijkstra + Union-Find + Topo Sort) + Intervals
 *
 * Part A — 20 problems × 3 cases from practice/Week09_Practice.md (runAll).
 * Part B — 10 timed stress cases (complexityAll, not called by the default main()).
 * Solution stubs sit at the bottom of the file; they throw TODO() so each case is
 * reported as [SKIP ⏭️] until you implement it, then it runs for real.
 * The interval trio keeps the w9 prefix so it does not collide with Week 6's
 * merge / insert / eraseOverlapIntervals when both weeks are compiled together.
 */

class Week09Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 9 — Advanced Graphs + Intervals ━━━━━━━")
        networkDelay(); cheapestFlights(); minEffort(); maxProb(); swimWater()
        redundantConn(); redundantConnII(); networkConnected(); removeStones(); accountsMerge()
        regionsSlashes(); connectPoints(); courseScheduleIV(); alienDict(); minHeightTrees()
        mergeIntervals(); insertInterval(); eraseOverlap(); meetingRoomsII(); intervalIntersect()
        // Complexity validation (Part B) — run with: complexityAll()
        // networkDelayStarBig(); networkDelayChainBig(); redundantBig(); accountsMergeBig()
        // connectPointsBig(); prerequisiteChainBig(); alienChainBig(); mergeIntervalsBig()
        // meetingRoomsBig(); intervalIntersectionBig()
    }

    // ── Part A — practice-sheet cases ───────────────────────────────────────

    // 1) Network Delay Time: Dijkstra, answer = max distance, -1 if any node is lost.
    fun networkDelay() {
        TestRunner.check("W9 #1a Delay classic",
            block = { networkDelayTime(arrayOf(intArrayOf(2, 1, 1), intArrayOf(2, 3, 1), intArrayOf(3, 4, 1)), 4, 2) },
            verify = { it == 2 })
        TestRunner.check("W9 #1b Delay unreachable",
            block = { networkDelayTime(arrayOf(intArrayOf(1, 2, 1)), 2, 2) }, verify = { it == -1 })
        TestRunner.check("W9 #1c Delay two nodes",
            block = { networkDelayTime(arrayOf(intArrayOf(1, 2, 1), intArrayOf(2, 1, 1)), 2, 1) }, verify = { it == 1 })
    }

    // 2) Cheapest Flights Within K Stops: Bellman-Ford for k + 1 rounds (or BFS layers).
    fun cheapestFlights() {
        val flights = arrayOf(intArrayOf(0, 1, 100), intArrayOf(1, 2, 100), intArrayOf(0, 2, 500))
        TestRunner.check("W9 #2a Flights one stop", block = { findCheapestPrice(4, flights, 0, 2, 1) }, verify = { it == 200 })
        TestRunner.check("W9 #2b Flights no stops", block = { findCheapestPrice(4, flights, 0, 2, 0) }, verify = { it == 500 })
        TestRunner.check("W9 #2c Flights from middle",
            block = { findCheapestPrice(3, arrayOf(intArrayOf(0, 1, 2), intArrayOf(1, 2, 1)), 1, 2, 1) }, verify = { it == 1 })
    }

    // 3) Path With Minimum Effort: Dijkstra where an edge costs the height difference.
    fun minEffort() {
        TestRunner.check("W9 #3a Effort classic",
            block = { minimumEffortPath(arrayOf(intArrayOf(1, 2, 2), intArrayOf(3, 8, 2), intArrayOf(5, 3, 5))) }, verify = { it == 2 })
        TestRunner.check("W9 #3b Effort detour wins",
            block = { minimumEffortPath(arrayOf(intArrayOf(1, 2, 3), intArrayOf(3, 8, 4), intArrayOf(5, 3, 5))) }, verify = { it == 1 })
        TestRunner.check("W9 #3c Effort flat column",
            block = { minimumEffortPath(arrayOf(intArrayOf(1, 2, 1, 1, 1), intArrayOf(1, 2, 1, 2, 1), intArrayOf(1, 2, 1, 2, 1), intArrayOf(1, 2, 1, 2, 1), intArrayOf(1, 1, 1, 2, 1))) },
            verify = { it == 0 })
    }

    // 4) Path With Maximum Probability: max-heap Dijkstra over products.
    fun maxProb() {
        val edges = arrayOf(intArrayOf(0, 1), intArrayOf(1, 2), intArrayOf(0, 2))
        TestRunner.check("W9 #4a Probability classic",
            block = { kotlin.math.abs(maxProbability(3, edges, doubleArrayOf(0.5, 0.5, 0.2), 0, 2) - 0.25) < 1e-9 }, verify = { it })
        TestRunner.check("W9 #4b Probability direct edge",
            block = { kotlin.math.abs(maxProbability(3, edges, doubleArrayOf(0.5, 0.5, 0.3), 0, 2) - 0.3) < 1e-9 }, verify = { it })
        TestRunner.check("W9 #4c Probability start equals end",
            block = { maxProbability(1, arrayOf(), doubleArrayOf(), 0, 0) }, verify = { it == 1.0 })
    }

    // 5) Swim in Rising Water: Dijkstra where the cost is the tallest cell on the path.
    fun swimWater() {
        TestRunner.check("W9 #5a Swim classic", block = { swimInWater(arrayOf(intArrayOf(0, 2), intArrayOf(1, 3))) }, verify = { it == 3 })
        TestRunner.check("W9 #5b Swim spiral",
            block = { swimInWater(arrayOf(intArrayOf(0, 1, 2, 3, 4), intArrayOf(24, 23, 22, 21, 5), intArrayOf(12, 13, 14, 15, 16), intArrayOf(11, 17, 18, 19, 20), intArrayOf(10, 9, 8, 7, 6))) },
            verify = { it == 16 })
        TestRunner.check("W9 #5c Swim single cell", block = { swimInWater(arrayOf(intArrayOf(0))) }, verify = { it == 0 })
    }

    // 6) Redundant Connection: union as you go; the first edge that fails is the answer.
    fun redundantConn() {
        TestRunner.check("W9 #6a Redundant classic",
            block = { findRedundantConnection(arrayOf(intArrayOf(1, 2), intArrayOf(1, 3), intArrayOf(2, 3))).toList() }, verify = { it == listOf(2, 3) })
        TestRunner.check("W9 #6b Redundant middle edge",
            block = { findRedundantConnection(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(1, 4), intArrayOf(1, 5))).toList() }, verify = { it == listOf(1, 4) })
        TestRunner.check("W9 #6c Redundant last edge",
            block = { findRedundantConnection(arrayOf(intArrayOf(3, 4), intArrayOf(1, 2), intArrayOf(2, 4), intArrayOf(3, 5), intArrayOf(2, 5))).toList() }, verify = { it == listOf(2, 5) })
    }

    // 7) Redundant Connection II: two parents or a cycle — one edge fixes both.
    fun redundantConnII() {
        TestRunner.check("W9 #7a Directed redundant classic",
            block = { findRedundantDirectedConnection(arrayOf(intArrayOf(1, 2), intArrayOf(1, 3), intArrayOf(2, 3))).toList() }, verify = { it == listOf(2, 3) })
        TestRunner.check("W9 #7b Directed redundant cycle",
            block = { findRedundantDirectedConnection(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(4, 1), intArrayOf(1, 5))).toList() }, verify = { it == listOf(4, 1) })
        TestRunner.check("W9 #7c Directed redundant two parents",
            block = { findRedundantDirectedConnection(arrayOf(intArrayOf(2, 1), intArrayOf(3, 1), intArrayOf(4, 1))).toList() }, verify = { it == listOf(4, 1) })
    }

    // 8) Make Network Connected: components - 1, or -1 when spare cables run out.
    fun networkConnected() {
        TestRunner.check("W9 #8a Cables classic", block = { makeConnected(4, arrayOf(intArrayOf(1, 2), intArrayOf(1, 3), intArrayOf(2, 3))) }, verify = { it == 1 })
        TestRunner.check("W9 #8b Cables too few", block = { makeConnected(6, arrayOf(intArrayOf(1, 2), intArrayOf(1, 3), intArrayOf(2, 3), intArrayOf(4, 5), intArrayOf(4, 5))) }, verify = { it == -1 })
        TestRunner.check("W9 #8c Cables already linked", block = { makeConnected(4, arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4))) }, verify = { it == 0 })
    }

    // 9) Most Stones Removed: one removal per stone except one per connected component.
    fun removeStones() {
        TestRunner.check("W9 #9a Stones classic",
            block = { removeStones(arrayOf(intArrayOf(0, 0), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(1, 2), intArrayOf(2, 1), intArrayOf(2, 2))) },
            verify = { it == 5 })
        TestRunner.check("W9 #9b Stones cross",
            block = { removeStones(arrayOf(intArrayOf(0, 0), intArrayOf(0, 2), intArrayOf(1, 1), intArrayOf(2, 0), intArrayOf(2, 2))) },
            verify = { it == 3 })
        TestRunner.check("W9 #9c Stones single", block = { removeStones(arrayOf(intArrayOf(0, 0))) }, verify = { it == 0 })
    }

    // 10) Accounts Merge: union emails that appear together, then group by root.
    fun accountsMerge() {
        TestRunner.check("W9 #10a Merge shared email", block = {
            accountsMerge(listOf(
                listOf("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                listOf("John", "johnsmith@mail.com", "john00@mail.com")))
        }, verify = { it.size == 1 && it[0][0] == "John" && it[0].drop(1).sorted() == listOf("john00@mail.com", "john_newyork@mail.com", "johnsmith@mail.com") })
        TestRunner.check("W9 #10b Merge no overlap", block = {
            accountsMerge(listOf(listOf("Mary", "mary@mail.com"), listOf("Mary", "marym@mail.com")))
        }, verify = { it.size == 2 && it.all { a -> a.size == 2 } })
        TestRunner.check("W9 #10c Merge one bridge", block = {
            accountsMerge(listOf(
                listOf("Gabe", "gabe0@mail.com", "gabe1@mail.com"),
                listOf("Gabe", "gabe3@mail.com", "gabe1@mail.com")))
        }, verify = { it.size == 1 && it[0].drop(1).sorted() == listOf("gabe0@mail.com", "gabe1@mail.com", "gabe3@mail.com") })
    }

    // 11) Regions Cut By Slashes: split every cell into 4 triangles, union them.
    fun regionsSlashes() {
        TestRunner.check("W9 #11a Slashes classic", block = { regionsBySlashes(arrayOf(" /", "/ ")) }, verify = { it == 2 })
        TestRunner.check("W9 #11b Slashes one region", block = { regionsBySlashes(arrayOf(" /", "  ")) }, verify = { it == 1 })
        TestRunner.check("W9 #11c Slashes diamond", block = { regionsBySlashes(arrayOf("/\\", "\\/")) }, verify = { it == 5 })
    }

    // 12) Min Cost to Connect All Points: Prim over Manhattan distances, O(n^2).
    fun connectPoints() {
        TestRunner.check("W9 #12a MST classic",
            block = { minCostConnectPoints(arrayOf(intArrayOf(1, 1), intArrayOf(3, 4), intArrayOf(-1, 0))) }, verify = { it == 20 })
        TestRunner.check("W9 #12b MST three points",
            block = { minCostConnectPoints(arrayOf(intArrayOf(3, 12), intArrayOf(-2, 5), intArrayOf(-4, 1))) }, verify = { it == 18 })
        TestRunner.check("W9 #12c MST single point", block = { minCostConnectPoints(arrayOf(intArrayOf(0, 0))) }, verify = { it == 0 })
    }

    // 13) Course Schedule IV: transitive closure over the prerequisite relation.
    fun courseScheduleIV() {
        TestRunner.check("W9 #13a Prereq closure",
            block = { checkIfPrerequisite(2, arrayOf(intArrayOf(1, 0)), arrayOf(intArrayOf(0, 1), intArrayOf(1, 0))) },
            verify = { it == listOf(false, true) })
        TestRunner.check("W9 #13b Prereq none",
            block = { checkIfPrerequisite(2, arrayOf(), arrayOf(intArrayOf(1, 0), intArrayOf(0, 1))) },
            verify = { it == listOf(false, false) })
        TestRunner.check("W9 #13c Prereq transitive",
            block = { checkIfPrerequisite(3, arrayOf(intArrayOf(1, 2), intArrayOf(2, 0)), arrayOf(intArrayOf(1, 0))) },
            verify = { it == listOf(true) })
    }

    // 14) Alien Dictionary: first differing letter of neighbours gives the edges, then Kahn.
    fun alienDict() {
        TestRunner.check("W9 #14a Alien classic",
            block = { alienOrder(listOf("wrt", "wrf", "er", "ett", "rftt")) }, verify = { it == "wertf" })
        TestRunner.check("W9 #14b Alien two words", block = { alienOrder(listOf("z", "x")) }, verify = { it == "zx" })
        TestRunner.check("W9 #14c Alien contradictory", block = { alienOrder(listOf("z", "x", "z")) }, verify = { it == "" })
    }

    // 15) Minimum Height Trees: peel leaves until 1 or 2 nodes remain.
    fun minHeightTrees() {
        TestRunner.check("W9 #15a MHT star", block = { findMinHeightTrees(4, arrayOf(intArrayOf(1, 0), intArrayOf(1, 2), intArrayOf(1, 3))).sorted() }, verify = { it == listOf(1) })
        TestRunner.check("W9 #15b MHT two centres",
            block = { findMinHeightTrees(6, arrayOf(intArrayOf(3, 0), intArrayOf(3, 1), intArrayOf(3, 2), intArrayOf(3, 4), intArrayOf(5, 4))).sorted() },
            verify = { it == listOf(3, 4) })
        TestRunner.check("W9 #15c MHT single node", block = { findMinHeightTrees(1, arrayOf()).sorted() }, verify = { it == listOf(0) })
    }

    // 16) Merge Intervals (w9 prefix so Week 6's merge keeps its own file).
    fun mergeIntervals() {
        TestRunner.check("W9 #16a Merge classic",
            block = { w9Merge(arrayOf(intArrayOf(1, 3), intArrayOf(2, 6), intArrayOf(8, 10), intArrayOf(15, 18))).map { it.toList() } },
            verify = { it == listOf(listOf(1, 6), listOf(8, 10), listOf(15, 18)) })
        TestRunner.check("W9 #16b Merge touching", block = { w9Merge(arrayOf(intArrayOf(1, 4), intArrayOf(4, 5))).map { it.toList() } }, verify = { it == listOf(listOf(1, 5)) })
        TestRunner.check("W9 #16c Merge unsorted starts", block = { w9Merge(arrayOf(intArrayOf(1, 4), intArrayOf(0, 4))).map { it.toList() } }, verify = { it == listOf(listOf(0, 4)) })
    }

    // 17) Insert Interval: emit the left part, merge the overlap run, emit the tail.
    fun insertInterval() {
        TestRunner.check("W9 #17a Insert overlap",
            block = { w9Insert(arrayOf(intArrayOf(1, 3), intArrayOf(6, 9)), intArrayOf(2, 5)).map { it.toList() } },
            verify = { it == listOf(listOf(1, 5), listOf(6, 9)) })
        TestRunner.check("W9 #17b Insert multi-merge",
            block = { w9Insert(arrayOf(intArrayOf(1, 2), intArrayOf(3, 5), intArrayOf(6, 7), intArrayOf(8, 10), intArrayOf(12, 16)), intArrayOf(4, 8)).map { it.toList() } },
            verify = { it == listOf(listOf(1, 2), listOf(3, 10), listOf(12, 16)) })
        TestRunner.check("W9 #17c Insert into empty", block = { w9Insert(arrayOf(), intArrayOf(5, 7)).map { it.toList() } }, verify = { it == listOf(listOf(5, 7)) })
    }

    // 18) Non-overlapping Intervals: keep the earliest finishing interval.
    fun eraseOverlap() {
        TestRunner.check("W9 #18a Erase classic",
            block = { w9EraseOverlapIntervals(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(1, 3))) }, verify = { it == 1 })
        TestRunner.check("W9 #18b Erase duplicates",
            block = { w9EraseOverlapIntervals(arrayOf(intArrayOf(1, 2), intArrayOf(1, 2), intArrayOf(1, 2))) }, verify = { it == 2 })
        TestRunner.check("W9 #18c Erase touching", block = { w9EraseOverlapIntervals(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3))) }, verify = { it == 0 })
    }

    // 19) Meeting Rooms II: sweep starts and ends, or a min-heap of end times.
    fun meetingRoomsII() {
        TestRunner.check("W9 #19a Rooms classic",
            block = { minMeetingRooms(arrayOf(intArrayOf(0, 30), intArrayOf(5, 10), intArrayOf(15, 20))) }, verify = { it == 2 })
        TestRunner.check("W9 #19b Rooms disjoint", block = { minMeetingRooms(arrayOf(intArrayOf(7, 10), intArrayOf(2, 4))) }, verify = { it == 1 })
        TestRunner.check("W9 #19c Rooms stacked",
            block = { minMeetingRooms(arrayOf(intArrayOf(1, 5), intArrayOf(2, 4), intArrayOf(3, 6))) }, verify = { it == 3 })
    }

    // 20) Interval List Intersections: two pointers, clip with max(start)/min(end).
    fun intervalIntersect() {
        TestRunner.check("W9 #20a Intersections classic",
            block = {
                intervalIntersection(
                    arrayOf(intArrayOf(0, 2), intArrayOf(5, 10), intArrayOf(13, 23), intArrayOf(24, 25)),
                    arrayOf(intArrayOf(1, 5), intArrayOf(8, 12), intArrayOf(15, 24), intArrayOf(25, 26))).map { it.toList() }
            },
            verify = { it == listOf(listOf(1, 2), listOf(5, 5), listOf(8, 10), listOf(15, 23), listOf(24, 24), listOf(25, 25)) })
        TestRunner.check("W9 #20b Intersections none",
            block = { intervalIntersection(arrayOf(intArrayOf(1, 3)), arrayOf(intArrayOf(4, 6))).map { it.toList() } },
            verify = { it == emptyList<List<Int>>() })
        TestRunner.check("W9 #20c Intersections inside",
            block = { intervalIntersection(arrayOf(intArrayOf(1, 10)), arrayOf(intArrayOf(2, 3), intArrayOf(5, 7))).map { it.toList() } },
            verify = { it == listOf(listOf(2, 3), listOf(5, 7)) })
    }

    // ── Part B — complexity validation (complexityAll) ──────────────────────
    // 1) Dijkstra on a 5,000-node star: every node is one hop away, max delay 1.
    fun networkDelayStarBig() = TestRunner.check("W9-B1 Network Delay 5k star", budgetMs = 200,
        block = { networkDelayTime(Array(4_999) { intArrayOf(0, it + 1, 1) }, 5_000, 0) }, verify = { it == 1 })

    // 2) 50,000-node chain: the last node is reached after 49,999 hops. (The old
    //    single-shot case expected 49,998, which is one hop short.)
    fun networkDelayChainBig() = TestRunner.check("W9-B2 Network Delay 50k chain", budgetMs = 300,
        block = { networkDelayTime(Array(49_999) { intArrayOf(it, it + 1, 1) }, 50_000, 0) }, verify = { it == 49_999 })

    // 3) A 20,000-node chain plus one closing edge: only the closing edge is redundant.
    fun redundantBig() = TestRunner.check("W9-B3 Redundant Connection 20k", budgetMs = 300,
        block = { findRedundantConnection(Array(20_000) { if (it < 19_999) intArrayOf(it + 1, it + 2) else intArrayOf(1, 20_000) }).toList() },
        verify = { it == listOf(1, 20_000) })

    // 4) 10,000 accounts, each sharing an email with the next, collapse into one.
    fun accountsMergeBig() = TestRunner.check("W9-B4 Accounts Merge 10k chain", budgetMs = 500,
        block = { accountsMerge(List(10_000) { listOf("User$it", "e${it}@x.com", "e${it + 1}@x.com") }) },
        verify = { it.size == 1 && it[0].size == 10_002 })

    // 5) 3,000 points on a line: the MST is the chain between neighbours, so its cost
    //    is maxX - minX = 999,887. Prim's O(n^2) fits; listing every edge does not.
    fun connectPointsBig() = TestRunner.check("W9-B5 Min Cost Connect 3000 points", budgetMs = 500,
        block = { minCostConnectPoints(Array(3_000) { intArrayOf((it * 7919) % 1_000_000, 0) }) }, verify = { it == 999_887 })

    // 6) Chain of 100 courses, 10,000 queries against the transitive closure.
    fun prerequisiteChainBig() = TestRunner.check("W9-B6 Course Schedule IV 10k queries", budgetMs = 500,
        block = { checkIfPrerequisite(100, Array(99) { intArrayOf(it + 1, it) }, Array(10_000) { intArrayOf(it % 99, it % 99 + 1) }) },
        verify = { it.size == 10_000 && it.all { answer -> answer } })

    // 7) 5,000 words whose first-difference edges form a..b..c..: one legal order.
    fun alienChainBig() = TestRunner.check("W9-B7 Alien Dictionary 5k words", budgetMs = 300,
        block = { alienOrder(List(5_000) { val i = it % 25; "${'a' + i}${'a' + i + 1}" }) },
        verify = { it == "abcdefghijklmnopqrstuvwxyz" })

    // 8) 100,000 chained intervals collapse into a single [0, 100000] interval — the
    //    old expectation of 100,000 kept intervals was wrong.
    fun mergeIntervalsBig() = TestRunner.check("W9-B8 Merge 100k chained intervals", budgetMs = 500,
        block = { w9Merge(Array(100_000) { intArrayOf(it, it + 1) }) },
        verify = { it.size == 1 && it[0][0] == 0 && it[0][1] == 100_000 })

    // 9) Every meeting covers the middle instant, so 50,000 rooms are needed.
    fun meetingRoomsBig() = TestRunner.check("W9-B9 Meeting Rooms II 50k", budgetMs = 500,
        block = { minMeetingRooms(Array(50_000) { intArrayOf(it, it + 50_000) }) }, verify = { it == 50_000 })

    // 10) Two identical 50k lists intersect into themselves: 50,000 clipped pieces.
    fun intervalIntersectionBig() = TestRunner.check("W9-B10 Interval Intersections 50k", budgetMs = 500,
        block = {
            val left = Array(50_000) { intArrayOf(it * 2, it * 2 + 1) }
            val right = Array(50_000) { intArrayOf(it * 2, it * 2 + 1) }
            val out = intervalIntersection(left, right)
            out.size == 50_000 && out[0][0] == 0 && out[0][1] == 1 && out[49_999][0] == 99_998 && out[49_999][1] == 99_999
        }, verify = { it })

    fun complexityAll() {
        println("\n━━━━━━━ WEEK 9 Part B — complexity validation ━━━━━━━")
        networkDelayStarBig(); networkDelayChainBig(); redundantBig(); accountsMergeBig(); connectPointsBig()
        prerequisiteChainBig(); alienChainBig(); mergeIntervalsBig(); meetingRoomsBig(); intervalIntersectionBig()
    }
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 9 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════

//
// Each stub throws TODO() so every case above reports [SKIP ⏭️] instead of a wrong
// answer. Replace the TODO() with your implementation and the case runs for real.

// ── Weighted graphs (Dijkstra / Bellman-Ford) ───────────────────────────────
fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int = TODO("W9 #1 — Dijkstra, answer is the max distance")
fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int = TODO("W9 #2 — Bellman-Ford, k+1 rounds")
fun minimumEffortPath(heights: Array<IntArray>): Int = TODO("W9 #3 — Dijkstra with max-diff edge cost")
fun maxProbability(n: Int, edges: Array<IntArray>, succProb: DoubleArray, start: Int, end: Int): Double = TODO("W9 #4 — max-heap Dijkstra")
fun swimInWater(grid: Array<IntArray>): Int = TODO("W9 #5 — Dijkstra with path-max cost")

// ── Union-Find ──────────────────────────────────────────────────────────────
fun findRedundantConnection(edges: Array<IntArray>): IntArray = TODO("W9 #6 — first edge whose union fails")
fun findRedundantDirectedConnection(edges: Array<IntArray>): IntArray = TODO("W9 #7 — two-parent and cycle cases")
fun makeConnected(n: Int, connections: Array<IntArray>): Int = TODO("W9 #8 — components - 1, needs spare cables")
fun removeStones(stones: Array<IntArray>): Int = TODO("W9 #9 — union rows with columns")
fun accountsMerge(accounts: List<List<String>>): List<List<String>> = TODO("W9 #10 — union emails, group by root")
fun regionsBySlashes(grid: Array<String>): Int = TODO("W9 #11 — DSU over 4 triangles per cell")
fun minCostConnectPoints(points: Array<IntArray>): Int = TODO("W9 #12 — Prim's O(n^2) MST")

// ── Topological sort ────────────────────────────────────────────────────────
fun checkIfPrerequisite(numCourses: Int, prerequisites: Array<IntArray>, queries: Array<IntArray>): List<Boolean> = TODO("W9 #13 — Boolean transitive closure")
fun alienOrder(words: List<String>): String = TODO("W9 #14 — first-difference edges, then Kahn")
fun findMinHeightTrees(n: Int, edges: Array<IntArray>): List<Int> = TODO("W9 #15 — peel leaves layer by layer")

// ── Intervals (w9 prefix so they can coexist with Week 6) ───────────────────
fun w9Merge(intervals: Array<IntArray>): Array<IntArray> = TODO("W9 #16 — sort by start, extend the last")
fun w9Insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> = TODO("W9 #17 — before / merge / after")
fun w9EraseOverlapIntervals(intervals: Array<IntArray>): Int = TODO("W9 #18 — sort by end, keep earliest finish")
fun minMeetingRooms(intervals: Array<IntArray>): Int = TODO("W9 #19 — min-heap of end times, or two sweeps")
fun intervalIntersection(firstList: Array<IntArray>, secondList: Array<IntArray>): Array<IntArray> = TODO("W9 #20 — two pointers, clip the overlap")

