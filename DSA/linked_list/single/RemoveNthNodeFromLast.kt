package linked_list.single

fun main() {
    val  l1 = ListNode(1, null)
    val  l2 = ListNode(2, null)
//    val  l3 = ListNode(3, null)
//    val  l4 = ListNode(4, null)
//    val  l5 = ListNode(5, null)
    l1.next = l2
//    l2.next = l3
//    l3.next = l4
//    l4.next = l5
    var temp = removeNthFromEnd(l1, 2)
    while (temp != null) {
        println(temp.`val`)
        temp = temp.next
    }

}


fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
    // 1. Create a dummy node to handle edge cases (like removing the head)
    val dummy = ListNode(0, null)
    dummy.next = head

    var fast: ListNode? = dummy
    var slow: ListNode? = dummy

    // 2. Move fast pointer so that there is a gap of n nodes between fast and slow
    for (i in 0..n) {
        fast = fast?.next
    }

    // 3. Move both until fast reaches the end
    while (fast != null) {
        fast = fast.next
        slow = slow?.next
    }

    // 4. Skip the nth node
    slow?.next = slow?.next?.next

    return dummy.next
}