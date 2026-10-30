package testrunner24

import testrunner.Week04Tests

/**
 * W24Week08Tests.kt — 24-Week Plan, Week 8: Linked List — Medium I (fast/slow, reorder, cycles)
 * 12 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week04Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week08Tests {
    private val src = Week04Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 8 — Linked List — Medium I (fast/slow, reorder, cycles) ━━━━━━━")
        src.removeNthFromEnd() // 24W #1 Remove Nth from End (old §14)
        src.deleteDuplicatesII() // 24W #2 Remove Duplicates II (old §15)
        src.reorderList() // 24W #3 Reorder List (old §16)
        src.swapPairs() // 24W #4 Swap Nodes in Pairs (old §17)
        src.rotateRight() // 24W #5 Rotate Linked List (old §18)
        src.partition() // 24W #6 Partition List (old §19)
        src.detectCycle() // 24W #7 Linked List Cycle II (old §20)
        src.isPalindromeLL() // 24W #8 Palindrome Linked List (old §21)
        src.addTwoNumbers() // 24W #9 Add Two Numbers (old §22)
        src.copyRandomList() // 24W #10 Copy List with Random Pointer (old §23)
        src.sortedListToBST() // 24W #11 Convert Sorted List to BST (old §24)
        src.reverseBetween() // 24W #12 Reverse Linked List II (old §33)
    }
}
