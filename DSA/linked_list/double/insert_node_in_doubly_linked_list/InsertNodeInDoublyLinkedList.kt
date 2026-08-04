package linked_list.double.insert_node_in_doubly_linked_list

import linked_list.double.d_list_node.DListNode

/**
 * Insert a node in a Doubly Linked List at various positions.
 * FAANG Importance: ⭐⭐⭐ (Fundamental DLL operation)
 */

fun main() {
    val head = DListNode(2)
    val second = DListNode(3)
    val third = DListNode(4)

    head.next = second; second.prev = head
    second.next = third; third.prev = second

    println("Original list:")
    printDLLInsert(head)

    // Insert at beginning
    val newHead = insertAtHead(head, 1)
    println("After inserting 1 at head:")
    printDLLInsert(newHead)

    // Insert at end
    val afterTail = insertAtTail(newHead, 5)
    println("After inserting 5 at tail:")
    printDLLInsert(afterTail)

    // Insert at position
    val afterPos = insertAtPosition(afterTail, 10, 3)
    println("After inserting 10 at position 3:")
    printDLLInsert(afterPos)
}

/**
 * Insert at head: O(1) time
 */
fun insertAtHead(head: DListNode?, data: Int): DListNode {
    val newNode = DListNode(data)
    newNode.next = head
    head?.prev = newNode
    return newNode
}

/**
 * Insert at tail: O(N) time
 */
fun insertAtTail(head: DListNode?, data: Int): DListNode? {
    val newNode = DListNode(data)
    if (head == null) return newNode

    var curr = head
    while (curr!!.next != null) {
        curr = curr.next
    }
    curr.next = newNode
    newNode.prev = curr
    return head
}

/**
 * Insert at position (1-indexed): O(N) time
 */
fun insertAtPosition(head: DListNode?, data: Int, position: Int): DListNode? {
    if (position <= 1) return insertAtHead(head, data)

    val newNode = DListNode(data)
    var curr = head
    var count = 1

    while (curr != null && count < position - 1) {
        curr = curr.next
        count++
    }

    if (curr == null) return head  // Position out of bounds

    newNode.next = curr.next
    newNode.prev = curr
    curr.next?.prev = newNode
    curr.next = newNode

    return head
}

fun printDLLInsert(head: DListNode?) {
    var curr = head
    while (curr != null) {
        print("${curr.`val`} ↔ ")
        curr = curr.next
    }
    println("null")
}
