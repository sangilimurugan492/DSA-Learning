package linked_list.single

/**
 * https://leetcode.com/problems/delete-node-in-a-linked-list/
 * Delete a node (only given access to that node, not the head).
 * Example: Given node with value 3 in [4,5,1,9] → [4,1,9]
 * FAANG Importance: ⭐⭐⭐ (Trick question: copy next node's value and skip)
 */

fun main() {
    val head = ListNode(4, null).apply {
        next = ListNode(5, null).apply {
            next = ListNode(1, null).apply { next = ListNode(9, null) }
        }
    }
    // Delete node with value 5 (we're given the node itself, not head)
    val nodeToDelete = head.next  // node with value 5
    deleteNode(nodeToDelete)

    var curr = head
    while (curr != null) { print("${curr.`val`} → "); curr = curr.next!! }
    println("null")
}

/**
 * O(1) time, O(1) space
 * Copy next node's value into current node, then skip the next node.
 * Note: Cannot delete the tail node with this approach.
 */
fun deleteNode(node: ListNode?) {
    if (node == null || node.next == null) return
    node.`val` = node.next!!.`val`
    node.next = node.next!!.next
}
