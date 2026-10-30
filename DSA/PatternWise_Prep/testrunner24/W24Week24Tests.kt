package testrunner24

import testrunner.Week11Tests

/**
 * W24Week24Tests.kt — 24-Week Plan, Week 24: Design Problems + Trie/XOR + 🏁 Final Grand Mock
 * 9 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week11Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week24Tests {
    private val src = Week11Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 24 — Design Problems + Trie/XOR + 🏁 Final Grand Mock ━━━━━━━")
        src.minStack() // 24W #1 Min Stack (old §13)
        src.minStack2()
        src.minStack3()
        src.randomSet() // 24W #2 Insert Delete GetRandom O(1) (old §14)
        src.randomSet2()
        src.randomSet3()
        src.timeMap() // 24W #3 Time Based Key-Value Store (old §15)
        src.timeMap2()
        src.timeMap3()
        src.lru() // 24W #4 LRU Cache (old §11)
        src.lru2()
        src.lru3()
        src.lfu() // 24W #5 LFU Cache (old §12)
        src.lfu2()
        src.lfu3()
        src.addSearch() // 24W #6 Design Add and Search Words Data Structure (old §16)
        src.addSearch2()
        src.addSearch3()
        src.maxXor() // 24W #7 Maximum XOR of Two Numbers in an Array (old §18)
        src.maxXor2()
        src.maxXor3()
        src.wordSearch() // 24W #8 Word Search II (old §17)
        src.wordSearch2()
        src.wordSearch3()
        src.calculator() // 24W #9 Basic Calculator (old §19)
        src.calculator2()
        src.calculator3()
    }
}
