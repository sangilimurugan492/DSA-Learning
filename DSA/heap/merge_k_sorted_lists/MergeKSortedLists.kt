package heap.merge_k_sorted_lists

/**
 * Merge K Sorted Lists — LeetCode #23
 * https://leetcode.com/problems/merge-k-sorted-lists/
 *
 * Problem:
 * -------
 * You are given an array of k linked-lists, each sorted in ascending order.
 * Merge all into one sorted linked-list and return it.
 *
 * Example:  lists = [[1,4,5],[1,3,4],[2,6]] → [1,1,2,3,4,4,5,6]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard — THE heap/merge problem)
 *
 * Two approaches:
 * 1. Min-Heap: O(N log K) — push heads, pop min, push next
 * 2. Divide & Conquer: O(N log K) — pair up lists, merge each pair
 */

// Linked List Node
class ListNode(var `val`: Int = 0, var next: ListNode? = null)

fun main() {
    println("=== Method 1: Min-Heap ===")
    val list1 = ListNode(1).apply { next = ListNode(4).apply { next = ListNode(5) } }
    val list2 = ListNode(1).apply { next = ListNode(3).apply { next = ListNode(4) } }
    val list3 = ListNode(2).apply { next = ListNode(6) }
    val result = mergeKLists(arrayOf(list1, list2, list3))
    printList("Merged", result)

    println("\n=== Method 2: Divide & Conquer ===")
    val l1 = ListNode(1).apply { next = ListNode(4).apply { next = ListNode(5) } }
    val l2 = ListNode(1).apply { next = ListNode(3).apply { next = ListNode(4) } }
    val l3 = ListNode(2).apply { next = ListNode(6) }
    val result2 = mergeKListsDivideConquer(arrayOf(l1, l2, l3))
    printList("Merged", result2)
}

fun printList(label: String, head: ListNode?) {
    print("$label: ")
    var curr = head
    while (curr != null) {
        print("${curr.`val`} ")
        curr = curr.next
    }
    println()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: MIN-HEAP — O(N log K)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MIN-HEAP — Push all list heads. Pop min, add to result, push its next. Repeat.
 *
 * Core Idea:
 *   - Push head of each list into a min-heap (ordered by value).
 *   - Pop the minimum, add to result, push its next node.
 *   - Repeat until heap is empty.
 *
 * Key Insight:
 *   - We process N nodes total, each heap operation is O(log K).
 *   - The heap never has more than K elements at once.
 *
 * Time Complexity:  O(N log K) — N total nodes, heap of size K.
 * Space Complexity: O(K) — heap stores at most K nodes.
 */
fun mergeKLists(lists: Array<ListNode?>): ListNode? {
    if (lists.isEmpty()) return null

    val minHeap = java.util.PriorityQueue<ListNode>(compareBy { it.`val` })
    for (list in lists) if (list != null) minHeap.offer(list)

    val dummy = ListNode(0)
    var tail = dummy

    while (minHeap.isNotEmpty()) {
        val smallest = minHeap.poll()
        tail.next = smallest
        tail = tail.next!!
        if (smallest.next != null) minHeap.offer(smallest.next)
    }
    return dummy.next
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: DIVIDE & CONQUER — O(N log K)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DIVIDE & CONQUER — Pair up lists, merge each pair. Repeat until one list remains.
 *
 * Core Idea:
 *   - Round 1: merge(list0, list1), merge(list2, list3), ...
 *   - Round 2: merge(result0, result1), ...
 *   - Continue until one list. Same complexity, no heap overhead.
 *
 * Time Complexity:  O(N log K).
 * Space Complexity: O(log K) — recursion stack.
 */
fun mergeKListsDivideConquer(lists: Array<ListNode?>): ListNode? {
    if (lists.isEmpty()) return null
    var currentLists = lists.toList()

    while (currentLists.size > 1) {
        val merged = mutableListOf<ListNode?>()
        for (i in currentLists.indices step 2) {
            val l1 = currentLists[i]
            val l2 = if (i + 1 < currentLists.size) currentLists[i + 1] else null
            merged.add(mergeTwoLists(l1, l2))
        }
        currentLists = merged
    }
    return currentLists[0]
}

private fun mergeTwoLists(l1: ListNode?, l2: ListNode?): ListNode? {
    val dummy = ListNode(0)
    var tail = dummy
    var p1 = l1; var p2 = l2

    while (p1 != null && p2 != null) {
        if (p1.`val` <= p2.`val`) { tail.next = p1; p1 = p1.next }
        else { tail.next = p2; p2 = p2.next }
        tail = tail.next!!
    }
    tail.next = p1 ?: p2
    return dummy.next
}
