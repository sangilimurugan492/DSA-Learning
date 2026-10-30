package testrunner24

import testrunner.Week04Tests

/**
 * W24Week07Tests.kt — 24-Week Plan, Week 7: Linked List — Singly & Doubly Fundamentals
 * 13 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week04Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week07Tests {
    private val src = Week04Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 7 — Linked List — Singly & Doubly Fundamentals ━━━━━━━")
        src.reverseList() // 24W #1 Reverse Linked List (old §1)
        src.middleNode() // 24W #2 Middle of Linked List (old §2)
        src.hasCycle() // 24W #3 Linked List Cycle (old §3)
        src.mergeTwoLists() // 24W #4 Merge Two Sorted Lists (old §4)
        src.deleteDuplicatesLL() // 24W #5 Remove Duplicates from Sorted List (old §5)
        src.getDecimalValue() // 24W #6 Linked List Binary to Integer (old §6)
        src.getIntersectionNode() // 24W #7 Intersection of Two Linked Lists (old §7)
        src.deleteNodeLL() // 24W #8 Delete Node in Linked List (old §8)
        src.reverseDoubly() // 24W #9 Reverse Doubly Linked List (old §9)
        src.deleteNodeDLL() // 24W #10 Delete Node in DLL (old §10)
        src.insertNodeDLL() // 24W #11 Insert Node in DLL (old §11)
        src.removeDuplicatesDLL() // 24W #12 Remove Duplicates from Sorted DLL (old §12)
        src.circularLinkedList() // 24W #13 Circular Linked List (old §13)
    }
}
