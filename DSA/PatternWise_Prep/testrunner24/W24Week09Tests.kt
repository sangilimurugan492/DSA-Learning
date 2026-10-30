package testrunner24

import testrunner.Week04Tests

/**
 * W24Week09Tests.kt — 24-Week Plan, Week 9: Linked List — Design, Circular & Hard
 * 12 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week04Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week09Tests {
    private val src = Week04Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 9 — Linked List — Design, Circular & Hard ━━━━━━━")
        src.myHashSet() // 24W #1 Design Hash Set (old §25)
        src.myLinkedList() // 24W #2 My Linked List (Design) (old §26)
        src.flattenMultiLevel() // 24W #3 Flatten Multilevel DLL (old §28)
        src.countPairs() // 24W #4 Find Pairs with Given Sum (Sorted DLL) (old §29)
        src.josephus() // 24W #5 Josephus Problem (old §30)
        src.sortedInsert() // 24W #6 Sorted Insert in Circular LL (old §31)
        src.splitCircularList() // 24W #7 Split Circular LL (old §32)
        src.lruCache() // 24W #8 LRU Cache (old §27)
        src.lfuCache() // 24W #9 LFU Cache (old §34)
        src.mergeKLists() // 24W #10 Merge K Sorted Lists (old §35)
        src.sortList() // 24W #11 Sort List (old §36)
        src.reverseKGroup() // 24W #12 Reverse in K-Group (old §37)
    }
}
