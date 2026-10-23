package testrunner

/**
 * Week04Tests.kt — Week 4: Linked List
 * Test class + solution stubs. Implement the stubs at the bottom as you solve.
 */

class Week04Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 4 — Linked List ━━━━━━━")
        reverseList(); middleNode(); hasCycle(); mergeTwoLists(); deleteDuplicatesLL()
        removeNthFromEnd(); reorderList(); swapPairs(); rotateRight(); detectCycle()
        isPalindromeLL(); addTwoNumbers(); lruCache(); mergeKLists(); sortList(); reverseKGroup()
        // Complexity validation (Part B):
        reverseListBig(); sortListBig()
    }

    fun reverseList() = TestRunner.check("W4 #1 Reverse Linked List", block = { toValues(reverseList(toList(1, 2, 3, 4, 5))) }, verify = { it == listOf(5, 4, 3, 2, 1) })
    fun middleNode() = TestRunner.check("W4 #2 Middle of Linked List", block = { toValues(middleNode(toList(1, 2, 3, 4, 5))) }, verify = { it == listOf(3, 4, 5) })
    fun hasCycle() = TestRunner.check("W4 #3 Linked List Cycle", block = { val h = toList(3, 2, 0, -4)!!; var t = h; while (t.next != null) t = t.next!!; t.next = h.next; hasCycle(h) }, verify = { it })
    fun mergeTwoLists() = TestRunner.check("W4 #4 Merge Two Sorted Lists", block = { toValues(mergeTwoLists(toList(1, 2, 4), toList(1, 3, 4))) }, verify = { it == listOf(1, 1, 2, 3, 4, 4) })
    fun deleteDuplicatesLL() = TestRunner.check("W4 #5 Remove Duplicates (Sorted LL)", block = { toValues(deleteDuplicatesLL(toList(1, 1, 2))) }, verify = { it == listOf(1, 2) })
    fun removeNthFromEnd() = TestRunner.check("W4 #6 Remove Nth From End", block = { toValues(removeNthFromEnd(toList(1, 2, 3, 4, 5), 2)) }, verify = { it == listOf(1, 2, 3, 5) })
    fun reorderList() = TestRunner.check("W4 #7 Reorder List", block = { val h = toList(1, 2, 3, 4)!!; reorderList(h); toValues(h) }, verify = { it == listOf(1, 4, 2, 3) })
    fun swapPairs() = TestRunner.check("W4 #8 Swap Nodes in Pairs", block = { toValues(swapPairs(toList(1, 2, 3, 4))) }, verify = { it == listOf(2, 1, 4, 3) })
    fun rotateRight() = TestRunner.check("W4 #9 Rotate Linked List", block = { toValues(rotateRight(toList(1, 2, 3, 4, 5), 2)) }, verify = { it == listOf(4, 5, 1, 2, 3) })
    fun detectCycle() = TestRunner.check("W4 #10 Cycle II", block = { val h = toList(3, 2, 0, -4)!!; var t = h; while (t.next != null) t = t.next!!; t.next = h.next; detectCycle(h)?.`val` }, verify = { it == 2 })
    fun isPalindromeLL() = TestRunner.check("W4 #11 Palindrome Linked List", block = { isPalindromeLL(toList(1, 2, 2, 1)) }, verify = { it })
    fun addTwoNumbers() = TestRunner.check("W4 #12 Add Two Numbers", block = { toValues(addTwoNumbers(toList(2, 4, 3), toList(5, 6, 4))) }, verify = { it == listOf(7, 0, 8) })
    fun lruCache() = TestRunner.check("W4 #13 LRU Cache", block = { val c = LRUCache(2); c.put(1, 1); c.put(2, 2); val a = c.get(1); c.put(3, 3); val b = c.get(2); listOf(a, b) }, verify = { it == listOf(1, -1) })
    fun mergeKLists() = TestRunner.check("W4 #14 Merge K Sorted Lists", block = { toValues(mergeKLists(arrayOf(toList(1, 4, 5), toList(1, 3, 4), toList(2, 6)))) }, verify = { it == listOf(1, 1, 2, 3, 4, 4, 5, 6) })
    fun sortList() = TestRunner.check("W4 #15 Sort List", block = { toValues(sortList(toList(4, 2, 1, 3))) }, verify = { it == listOf(1, 2, 3, 4) })
    fun reverseKGroup() = TestRunner.check("W4 #16 Reverse in K-Group", block = { toValues(reverseKGroup(toList(1, 2, 3, 4, 5), 2)) }, verify = { it == listOf(2, 1, 4, 5, 3) })

    // Part B — complexity validation
    fun reverseListBig() = TestRunner.check("W4-B Reverse 1M nodes", budgetMs = 300, block = { var h: ListNode? = null; repeat(1_000_000) { h = ListNode(it + 1, h) }; toValues(reverseList(h)).first() }, verify = { it == 1_000_000 })
    fun sortListBig() = TestRunner.check("W4-B Sort List shuffled 100k", budgetMs = 500, block = { var h: ListNode? = null; shuffledRange(100_000).forEach { v -> h = ListNode(v, h) }; toValues(sortList(h!!)) == (1..100_000).toList() }, verify = { it })
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 4 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════

fun reverseList(head: ListNode?): ListNode? {
    // TODO: prev/curr pointer flip
    return null
}

fun middleNode(head: ListNode?): ListNode? {
    // TODO: fast/slow pointers
    return null
}

fun hasCycle(head: ListNode?): Boolean {
    // TODO: Floyd's tortoise-hare
    return false
}

fun mergeTwoLists(l1: ListNode?, l2: ListNode?): ListNode? {
    // TODO: dummy node, splice smaller
    return null
}

fun deleteDuplicatesLL(head: ListNode?): ListNode? {
    // TODO: skip nodes equal to next
    return null
}

fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
    // TODO: two pointers, n-gap
    return null
}

fun reorderList(head: ListNode?): Unit {
    // TODO: middle + reverse + merge halves
}

fun swapPairs(head: ListNode?): ListNode? {
    // TODO: swap adjacent pairs recursively
    return null
}

fun rotateRight(head: ListNode?, k: Int): ListNode? {
    // TODO: close ring, break at n − k%n
    return null
}

fun detectCycle(head: ListNode?): ListNode? {
    // TODO: Floyd's, then re-walk from head
    return null
}

fun isPalindromeLL(head: ListNode?): Boolean {
    // TODO: middle + reverse half compare
    return false
}

fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
    // TODO: digit-by-digit with carry
    return null
}

class LRUCache(private val capacity: Int) {
    fun get(key: Int): Int {
        // TODO: hashmap + doubly-linked list
        return 0
    }
    fun put(key: Int, value: Int): Unit {
        // TODO: evict from head when over capacity
    }
}

fun mergeKLists(lists: Array<ListNode?>): ListNode? {
    // TODO: min-heap of heads
    return null
}

fun sortList(head: ListNode?): ListNode? {
    // TODO: merge sort with mid split
    return null
}

fun reverseKGroup(head: ListNode?, k: Int): ListNode? {
    // TODO: count k, reverse group, recurse
    return null
}
