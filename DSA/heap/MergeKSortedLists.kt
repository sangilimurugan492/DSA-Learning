package heap

/**
 * https://leetcode.com/problems/merge-k-sorted-lists/
 *
 * You are given an array of k linked-lists, each sorted in ascending order.
 * Merge all the linked-lists into one sorted linked-list and return it.
 *
 * Example 1: lists = [[1,4,5],[1,3,4],[2,6]] → Output: [1,1,2,3,4,4,5,6]
 * Example 2: lists = [] → Output: []
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard — THE heap/merge problem)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Key question: "How to efficiently find the minimum across K sorted lists?"
 *
 * APPROACH 1: Merge one by one → O(K×N) where N = total nodes
 *   Merge first two, then merge result with third, etc.
 *   Like merge sort but sequential — not optimal.
 *
 * APPROACH 2: Min-Heap of K elements → O(N log K) OPTIMAL
 *   1. Push the head of each list into a min-heap
 *   2. Pop the minimum, add to result, push its next node
 *   3. Repeat until heap is empty
 *
 *   WHY O(N log K)? We process N nodes total, each heap operation is O(log K).
 *   The heap never has more than K elements at once.
 *
 *   WHY is this optimal? We MUST look at all N nodes, and we MUST compare
 *   across K lists. O(N log K) is the best possible for comparison-based merging.
 *
 * APPROACH 3: Divide and Conquer (Merge Sort style) → O(N log K)
 *   Pair up lists, merge each pair, repeat until one list remains.
 *   Same complexity but no heap overhead.
 *
 * Connection to other problems:
 *   - Merge Two Sorted Lists: K=1 special case
 *   - Kth Smallest in Sorted Matrix: same "find min across K sources" pattern
 *   - This is the K-way merge pattern — fundamental for external sorting
 * ─────────────────────────────────────────────────────────────
 */

// Linked List Node definition
class ListNode(var `val`: Int = 0, var next: ListNode? = null)

fun main() {
    println("=== Merge K Sorted Lists ===")

    // Create lists: [[1,4,5],[1,3,4],[2,6]]
    val list1 = ListNode(1).apply { next = ListNode(4).apply { next = ListNode(5) } }
    val list2 = ListNode(1).apply { next = ListNode(3).apply { next = ListNode(4) } }
    val list3 = ListNode(2).apply { next = ListNode(6) }

    val result = mergeKLists(arrayOf(list1, list2, list3))
    print("Merged: ")
    var curr = result
    while (curr != null) {
        print("${curr.`val`} ")
        curr = curr.next
    }
    println()

    println("---")
    val emptyResult = mergeKLists(arrayOf())
    println("Empty: ${emptyResult == null}")
}

/**
 * OPTIMAL — Min-Heap approach
 * Time Complexity: O(N log K) — N total nodes, heap of size K
 * Space Complexity: O(K) — heap stores at most K nodes
 *
 * Trace for [[1,4,5],[1,3,4],[2,6]]:
 *
 * Initial heap: [(1,list1), (1,list2), (2,list3)]  ← heads of all lists
 *
 * Pop 1 (list1): result → 1, push list1.next=4 → heap: [(1,list2), (2,list3), (4,list1)]
 * Pop 1 (list2): result → 1→1, push list2.next=3 → heap: [(2,list3), (3,list2), (4,list1)]
 * Pop 2 (list3): result → 1→1→2, push list3.next=6 → heap: [(3,list2), (4,list1), (6,list3)]
 * Pop 3 (list2): result → 1→1→2→3, push list2.next=4 → heap: [(4,list1), (4,list2), (6,list3)]
 * Pop 4 (list1): result → 1→1→2→3→4, push list1.next=5 → heap: [(4,list2), (5,list1), (6,list3)]
 * Pop 4 (list2): result → 1→1→2→3→4→4, push list2.next=null → heap: [(5,list1), (6,list3)]
 * Pop 5 (list1): result → 1→1→2→3→4→4→5, push list1.next=null → heap: [(6,list3)]
 * Pop 6 (list3): result → 1→1→2→3→4→4→5→6, push list3.next=null → heap: []
 *
 * Result: 1→1→2→3→4→4→5→6 ✅
 */
fun mergeKLists(lists: Array<ListNode?>): ListNode? {
    if (lists.isEmpty()) return null

    // Min-heap ordered by node value
    val minHeap = java.util.PriorityQueue<ListNode>(compareBy { it.`val` })

    // Push all list heads into heap
    for (list in lists) {
        if (list != null) minHeap.offer(list)
    }

    val dummy = ListNode(0)  // dummy head for easy linking
    var tail = dummy

    while (minHeap.isNotEmpty()) {
        val smallest = minHeap.poll()  // get minimum
        tail.next = smallest           // add to result
        tail = tail.next!!             // advance tail

        if (smallest.next != null) {
            minHeap.offer(smallest.next)  // push next node from same list
        }
    }

    return dummy.next
}

/**
 * ALTERNATIVE — Divide and Conquer (Merge Sort style)
 * Time Complexity: O(N log K)
 * Space Complexity: O(log K) — recursion stack
 *
 * Pair up lists and merge. Repeat until one list remains.
 * Round 1: merge(list0, list1), merge(list2, list3), ...
 * Round 2: merge(result0, result1), ...
 * Continue until one list.
 *
 * This avoids heap overhead and has same time complexity.
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
    var p1 = l1
    var p2 = l2

    while (p1 != null && p2 != null) {
        if (p1.`val` <= p2.`val`) {
            tail.next = p1
            p1 = p1.next
        } else {
            tail.next = p2
            p2 = p2.next
        }
        tail = tail.next!!
    }
    tail.next = p1 ?: p2
    return dummy.next
}
