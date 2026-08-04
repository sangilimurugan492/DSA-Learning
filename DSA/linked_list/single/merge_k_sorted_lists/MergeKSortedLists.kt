package linked_list.single.merge_k_sorted_lists

import linked_list.single.list_node.ListNode

import java.util.PriorityQueue

/**
 * https://leetcode.com/problems/merge-k-sorted-lists/
 * Merge k sorted linked lists into one sorted list.
 * Example: [[1,4,5],[1,3,4],[2,6]] → [1,1,2,3,4,4,5,6]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 5 hardest LL, uses Heap/Divide&Conquer)
 */

fun main() {
    val l1 = ListNode(1).apply { next = ListNode(4).apply { next = ListNode(5) } }
    val l2 = ListNode(1).apply { next = ListNode(3).apply { next = ListNode(4) } }
    val l3 = ListNode(2).apply { next = ListNode(6) }
    val result = mergeKListsBruteForce(arrayOf(l1, l2, l3))
    printList(result)
    val r2 = mergeKListsHeap(arrayOf(l1, l2, l3))
    printList(r2)
}

fun printList(head: ListNode?) {
    var curr = head
    while (curr != null) { print("${curr.`val`} → "); curr = curr.next }
    println("null")
}

/**
 * BRUTE FORCE: O(N log N) — collect all values, sort, rebuild list
 * N = total number of nodes across all lists
 */
fun mergeKListsBruteForce(lists: Array<ListNode?>): ListNode? {
    val values = mutableListOf<Int>()
    for (list in lists) {
        var curr = list
        while (curr != null) { values.add(curr.`val`); curr = curr.next }
    }
    values.sort()
    val dummy = ListNode(
        0,
        next = null
    )
    var tail = dummy
    for (v in values) { tail.next = ListNode(v); tail = tail.next!! }
    return dummy.next
}

/**
 * OPTIMAL: O(N log K) — Min Heap (Priority Queue)
 * Put head of each list into heap. Pop smallest, add its next to heap.
 * K = number of lists, N = total nodes
 */
fun mergeKListsHeap(lists: Array<ListNode?>): ListNode? {
    val heap = PriorityQueue<ListNode>(compareBy { it.`val` })
    for (list in lists) if (list != null) heap.add(list)

    val dummy = ListNode(
        0,
        next = null
    )
    var tail = dummy

    while (heap.isNotEmpty()) {
        val min = heap.poll()
        tail.next = min
        tail = tail.next!!
        if (min.next != null) heap.add(min.next)
    }
    return dummy.next
}
