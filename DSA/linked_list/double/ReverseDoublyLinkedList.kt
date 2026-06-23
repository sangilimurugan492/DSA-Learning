package linked_list.double

/**
 * Reverse a Doubly Linked List.
 * FAANG Importance: ⭐⭐⭐⭐ (Fundamental DLL operation)
 */

fun main() {
    val head = DListNode(1)
    val second = DListNode(2)
    val third = DListNode(3)
    val fourth = DListNode(4)

    head.next = second; second.prev = head
    second.next = third; third.prev = second
    third.next = fourth; fourth.prev = third

    println("Original:")
    printDLL(head)

    val reversed = reverseDLL(head)
    println("Reversed:")
    printDLL(reversed)
}

/**
 * ITERATIVE: O(N) time, O(1) space
 * Swap prev and next for every node.
 */
fun reverseDLL(head: DListNode?): DListNode? {
    if (head == null || head.next == null) return head

    var curr: DListNode? = head
    var temp: DListNode? = null

    while (curr != null) {
        // Swap prev and next
        temp = curr.prev
        curr.prev = curr.next
        curr.next = temp
        curr = curr.prev  // Move to next (which is now prev after swap)
    }

    // temp is now the last node's original prev, which is the new head
    return temp?.prev
}

/**
 * RECURSIVE: O(N) time, O(N) stack space
 */
fun reverseDLLRecursive(head: DListNode?): DListNode? {
    if (head == null || head.next == null) return head

    // Swap prev and next
    val temp = head.prev
    head.prev = head.next
    head.next = temp

    // If prev (originally next) is null, this is the new head
    if (head.prev == null) return head

    return reverseDLLRecursive(head.prev)
}

fun printDLL(head: DListNode?) {
    var curr = head
    while (curr != null) {
        print("${curr.`val`} ↔ ")
        curr = curr.next
    }
    println("null")
}
