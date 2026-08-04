package linked_list.double.delete_node_in_doubly_linked_list

import linked_list.double.d_list_node.DListNode

/**
 * Delete a node at a given position in a Doubly Linked List.
 * FAANG Importance: ⭐⭐⭐ (Basic DLL manipulation)
 */

fun main() {
    val head = DListNode(1)
    val second = DListNode(2)
    val third = DListNode(3)
    val fourth = DListNode(4)

    head.next = second; second.prev = head
    second.next = third; third.prev = second
    third.next = fourth; fourth.prev = third

    println("Before deletion:")
    printDLLDelete(head)

    val newHead = deleteAtPosition(head, 2)  // Delete node at position 2 (value=3)
    println("After deleting position 2:")
    printDLLDelete(newHead)
}

/**
 * O(N) time, O(1) space
 * Handle edge cases: delete head, delete tail, delete middle.
 */
fun deleteAtPosition(head: DListNode?, position: Int): DListNode? {
    if (head == null || position < 1) return head

    var curr: DListNode? = head
    var count = 1

    while (curr != null && count < position) {
        curr = curr.next
        count++
    }

    if (curr == null) return head  // Position out of bounds

    // Update previous node's next
    curr.prev?.next = curr.next

    // Update next node's prev
    curr.next?.prev = curr.prev

    // If deleting head, return new head
    return if (curr == head) head.next else head
}

/**
 * Delete a specific node (given reference) — O(1) time
 */
fun deleteNode(node: DListNode?) {
    if (node == null) return
    node.prev?.next = node.next
    node.next?.prev = node.prev
}

fun printDLLDelete(head: DListNode?) {
    var curr = head
    while (curr != null) {
        print("${curr.`val`} ↔ ")
        curr = curr.next
    }
    println("null")
}
