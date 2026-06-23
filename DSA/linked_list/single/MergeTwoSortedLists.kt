package linked_list.single

/**
 * https://leetcode.com/problems/merge-two-sorted-lists/
 * Merge two sorted linked lists into one sorted list.
 * Example: [1,2,4] + [1,3,4] → [1,1,2,3,4,4]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 5 most asked LL)
 */

fun main() {
    val l1 = ListNode(1).apply { next = ListNode(2).apply { next = ListNode(4) } }
    val l2 = ListNode(1).apply { next = ListNode(3).apply { next = ListNode(4) } }
    var result = mergeTwoListsIterative(l1, l2)
    while (result != null) { print("${result.`val`} → "); result = result.next }
    println("null")
}

/** ITERATIVE: O(N+M) time, O(1) space — use dummy head */
fun mergeTwoListsIterative(l1: ListNode?, l2: ListNode?): ListNode? {
    val dummy = ListNode(0)
    var tail = dummy
    var a = l1
    var b = l2

    while (a != null && b != null) {
        if (a.`val` <= b.`val`) { tail.next = a; a = a.next }
        else { tail.next = b; b = b.next }
        tail = tail.next!!
    }
    tail.next = a ?: b
    return dummy.next
}

/** RECURSIVE: O(N+M) time, O(N+M) stack space */
fun mergeTwoListsRecursive(l1: ListNode?, l2: ListNode?): ListNode? {
    if (l1 == null) return l2
    if (l2 == null) return l1
    if (l1.`val` <= l2.`val`) { l1.next = mergeTwoListsRecursive(l1.next, l2); return l1 }
    else { l2.next = mergeTwoListsRecursive(l1, l2.next); return l2 }
}
