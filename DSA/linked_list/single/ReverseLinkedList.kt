package linked_list.single

fun main() {
    val  l1 = ListNode(1, null)
    val  l2 = ListNode(2, null)
    val  l3 = ListNode(3, null)
    val  l4 = ListNode(4, null)
    val  l5 = ListNode(5, null)
    val  l6 = ListNode(6, null)
    l1.next = l2
    l2.next = l3
    l3.next = l4
    l4.next = l5
    l5.next = l6
    var result = reverseLinkedList(l1)
    var prResult = result
    while (prResult != null) {
        print("${prResult.`val`} ->" )
        prResult =prResult.next
    }
}

fun reverseLinkedList(head :ListNode?) : ListNode?{
    if (head == null) {
        return head
    }

    var current = head
    var prev : ListNode? = null

    while (current != null) {
        val nextNode = current.next
        current.next = prev
        prev = current
        current = nextNode
    }

    return prev
}