package linked_list.double

/**
 * Remove duplicates from a sorted Doubly Linked List.
 * Example: [1,1,2,3,3,4] → [1,2,3,4]
 * FAANG Importance: ⭐⭐⭐⭐ (DLL manipulation, common interview question)
 */

fun main() {
    val head = DListNode(1)
    val second = DListNode(1)
    val third = DListNode(2)
    val fourth = DListNode(3)
    val fifth = DListNode(3)
    val sixth = DListNode(4)

    head.next = second; second.prev = head
    second.next = third; third.prev = second
    third.next = fourth; fourth.prev = third
    fourth.next = fifth; fifth.prev = fourth
    fifth.next = sixth; sixth.prev = fifth

    println("Before removing duplicates:")
    printDLLDup(head)

    val newHead = removeDuplicatesSortedDLL(head)
    println("After removing duplicates:")
    printDLLDup(newHead)
}

/**
 * O(N) time, O(1) space
 * Since DLL is sorted, duplicates are adjacent. Skip duplicate nodes.
 */
fun removeDuplicatesSortedDLL(head: DListNode?): DListNode? {
    if (head == null || head.next == null) return head

    var curr: DListNode? = head
    while (curr != null && curr.next != null) {
        if (curr.`val` == curr.next!!.`val`) {
            val duplicate = curr.next
            curr.next = duplicate!!.next
            duplicate.next?.prev = curr
        } else {
            curr = curr.next
        }
    }

    return head
}

fun printDLLDup(head: DListNode?) {
    var curr = head
    while (curr != null) {
        print("${curr.`val`} ↔ ")
        curr = curr.next
    }
    println("null")
}
