package linked_list.single

fun main() {
    val  l1 = ListNode(1, null)
    val  l2 = ListNode(2, null)
    val  l3 = ListNode(3, null)
    val  l4 = ListNode(4, null)
    val  l5 = ListNode(5, null)
    l1.next = l2
    l2.next = l3
    l3.next = l4
    l4.next = l5
    var first = rotateRightBF(l1, 2)
    while (first != null) {
        print("${ first.`val`} ")
        first = first.next
    }

    first = rotateRightOP(l1, 2)
    while (first != null) {
        print("${ first.`val`} ")
        first = first.next
    }
}

fun rotateRightBF(head: ListNode?, k: Int): ListNode? {
    var temp: ListNode?
    var first = head
    var prev = head
    for (i in 0 until k) {
        temp = first
        while (temp?.next !=null) {
            prev = temp
            temp = temp.next
        }
        temp?.next = first
        prev?.next = null
        first = temp

    }
    return first
}

fun rotateRightOP(head: ListNode?, k: Int): ListNode? {
    if (head?.next == null || k == 0) return head

    // 1. Calculate length and find the tail
    var tail = head
    var length = 1
    while (tail?.next != null) {
        tail = tail.next
        length++
    }

    // 2. Adjust k
    val actualK = k % length
    if (actualK == 0) return head

    // 3. Connect tail to head to form a circle
    tail?.next = head

    // 4. Find the node before the new head (the new tail)
    // It is located at (length - actualK - 1) steps from head
    var newTail = head
    for (i in 0 until (length - actualK - 1)) {
        newTail = newTail?.next
    }

    // 5. Set new head and break the circular link
    val newHead = newTail?.next
    newTail?.next = null

    return newHead
}