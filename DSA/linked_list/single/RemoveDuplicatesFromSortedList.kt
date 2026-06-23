package linked_list.single

/**
 * https://leetcode.com/problems/remove-duplicates-from-sorted-list/
 * Delete all duplicates such that each element appears only once.
 * Example: [1,1,2,3,3] → [1,2,3]
 * FAANG Importance: ⭐⭐⭐⭐ (Basic LL manipulation)
 */

fun main() {
    val head = ListNode(1).apply {
        next = ListNode(1).apply {
            next = ListNode(2).apply {
                next = ListNode(3).apply { next = ListNode(3) }
            }
        }
    }
    var result = deleteDuplicatesIterative(head)
    while (result != null) { print("${result.`val`} → "); result = result.next }
    println("null")
}

/**
 * ITERATIVE: O(N) time, O(1) space
 * If current val equals next val, skip the next node.
 */
fun deleteDuplicatesIterative(head: ListNode?): ListNode? {
    var curr = head
    while (curr != null && curr.next != null) {
        if (curr.`val` == curr.next!!.`val`) {
            curr.next = curr.next!!.next
        } else {
            curr = curr.next
        }
    }
    return head
}

/**
 * RECURSIVE: O(N) time, O(N) stack space
 */
fun deleteDuplicatesRecursive(head: ListNode?): ListNode? {
    if (head == null || head.next == null) return head
    head.next = deleteDuplicatesRecursive(head.next)
    return if (head.`val` == head.next!!.`val`) head.next else head
}
