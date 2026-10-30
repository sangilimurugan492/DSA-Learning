package testrunner24

import testrunner.Week05Tests

/**
 * W24Week11Tests.kt — 24-Week Plan, Week 11: BST Traversals + Heap + Trie
 * 10 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week05Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week11Tests {
    private val src = Week05Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 11 — BST Traversals + Heap + Trie ━━━━━━━")
        src.kthSmallest() // 24W #1 Kth Smallest Element in BST (old §13)
        src.isValidBST() // 24W #2 Validate BST (old §14)
        src.levelOrder() // 24W #3 Level Order Traversal (old §15)
        src.rightSideView() // 24W #4 Right Side View (old §16)
        src.findKthLargest() // 24W #5 Kth Largest Element (old §17)
        src.w5TopKFrequent() // 24W #6 Top K Frequent Elements (old §18)
        src.w5MergeKLists() // 24W #7 Merge K Sorted Lists (Heap) (old §19)
        src.findMedianStream() // 24W #8 Find Median from Data Stream (old §20)
        src.trieOps() // 24W #9 Implement Trie (Prefix Tree) (old §21)
        src.wordSearchII() // 24W #10 Word Search II (old §22)
    }
}
