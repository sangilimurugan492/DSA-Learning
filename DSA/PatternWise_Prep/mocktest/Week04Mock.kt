package mocktest

/**
 * Week04Mock.kt — MOCK TEST Week 4: Linked List
 * 5 problems · suggested time 110 min. Uses bundled MockListNode/mockList helpers.
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 4 MOCK — Linked List (5 problems · 110 min) ━━━━━━━")
    mockCheck("M4 #1 Middle of List (10 min)", block = { mockListValues(w4mMiddle(mockList(1, 2, 3, 4, 5))) }, verify = { it == listOf(3, 4, 5) })
    mockCheck("M4 #2 Reverse List (15 min)", block = { mockListValues(w4mReverse(mockList(1, 2, 3))) }, verify = { it == listOf(3, 2, 1) })
    mockCheck("M4 #3 Add Two Numbers (20 min)", block = { mockListValues(w4mAddTwo(mockList(2, 4, 3), mockList(5, 6, 4))) }, verify = { it == listOf(7, 0, 8) })
    mockCheck("M4 #4 Remove Nth From End (20 min)", block = { mockListValues(w4mRemoveNth(mockList(1, 2, 3, 4, 5), 2)) }, verify = { it == listOf(1, 2, 3, 5) })
    mockCheck("M4 #5 Detect Cycle (15 min)", block = { val h = mockList(3, 2, 0, -4)!!; var t = h; while (t.next != null) t = t.next!!; t.next = h.next; w4mHasCycle(h) }, verify = { it })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w4mMiddle(head: MockListNode?): MockListNode? = TODO()
fun w4mReverse(head: MockListNode?): MockListNode? = TODO()
fun w4mAddTwo(l1: MockListNode?, l2: MockListNode?): MockListNode? = TODO()
fun w4mRemoveNth(head: MockListNode?, n: Int): MockListNode? = TODO()
fun w4mHasCycle(head: MockListNode?): Boolean = TODO()
