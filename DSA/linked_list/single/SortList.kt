package linked_list.single

/**
 * https://leetcode.com/problems/sort-list/
 * Sort a linked list in O(N log N) time using constant space.
 * Example: [4,2,1,3] → [1,2,3,4]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Merge sort on LL, combines fast/slow + merge)
 */

fun main() {
    val head = ListNode(4, null).apply {
        next = ListNode(2, null).apply {
            next = ListNode(1, null).apply { next = ListNode(3, null) }
        }
    }
    var result = sortList(head)
    while (result != null) { print("${result.`val`} → "); result = result.next }
    println("null")
}

/**
 * MERGE SORT: O(N log N) time, O(log N) stack space
 * 1. Find middle using fast/slow pointer
 * 2. Recursively sort both halves
 * 3. Merge the two sorted halves
 */
fun sortList(head: ListNode?): ListNode? {
    if (head == null || head.next == null) return head

    // Find middle and split
    val mid = getMid(head)
    val left = sortList(head)
    val right = sortList(mid)

    return mergeList(left, right)
}

/** Find middle node and disconnect the list into two halves */
fun getMid(head: ListNode): ListNode? {
    var slow: ListNode? = head
    var fast: ListNode? = head
    var prev: ListNode? = null

    while (fast != null && fast.next != null) {
        prev = slow
        slow = slow?.next
        fast = fast.next?.next
    }

    // Disconnect the two halves
    prev?.next = null
    return slow
}

/** Merge two sorted lists */
fun mergeList(l1: ListNode?, l2: ListNode?): ListNode? {
    val dummy = ListNode(0, null)
    var tail = dummy
    var a = l1
    var b = l2

    while (a != null && b != null) {
        if (a.`val` <= b.`val`) {
            tail.next = a
            a = a.next
        } else {
            tail.next = b
            b = b.next
        }
        tail = tail.next!!
    }

    tail.next = a ?: b
    return dummy.next
}
