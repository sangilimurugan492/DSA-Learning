package linked_list.single.remove_nth_node_from_last

import linked_list.single.list_node.ListNode

fun main() {
    val l1 = ListNode(1, null)
    val l2 = ListNode(2, null)
    val l3 = ListNode(3, null)
    val l4 = ListNode(4, null)
    val l5 = ListNode(5, null)
    l1.next = l2
    l2.next = l3
    l3.next = l4
    l4.next = l5

    println("Two-Pass result:")
    var temp = removeNthFromEndTwoPass(l1, 2)
    while (temp != null) {
        println(temp.`val`)
        temp = temp.next
    }

    // Rebuild list for second test
    val m1 = ListNode(1, null)
    val m2 = ListNode(2, null)
    val m3 = ListNode(3, null)
    val m4 = ListNode(4, null)
    val m5 = ListNode(5, null)
    m1.next = m2
    m2.next = m3
    m3.next = m4
    m4.next = m5

    println("\nTwo-Pointer result:")
    var temp2 = removeNthFromEnd(m1, 2)
    while (temp2 != null) {
        println(temp2.`val`)
        temp2 = temp2.next
    }
}

/**
 * Method 1: Two-Pass (Count then Remove)
 *
 * First pass: count total nodes.
 * Second pass: traverse to the node just before the target (length - n) and skip it.
 *
 * Time:  O(N) — two passes
 * Space: O(1)
 */
fun removeNthFromEndTwoPass(head: ListNode?, n: Int): ListNode? {
    // 1. Create a dummy node to handle edge cases (like removing the head)
    val dummy = ListNode(0, null)
    dummy.next = head

    // 2. First pass — count total nodes
    var length = 0
    var curr: ListNode? = head
    while (curr != null) {
        length++
        curr = curr.next
    }

    // 3. Compute position from the front (0-indexed from dummy)
    val position = length - n

    // 4. Second pass — move to the node just before the target
    curr = dummy
    var i = 0
    while (i in 0 until position) {
        curr = curr?.next
        i++
    }

    // 5. Skip the target node
    curr?.next = curr.next?.next

    return dummy.next
}

/**
 * Method 2: Optimal — Two Pointers (Fast & Slow)
 *
 * Move fast n+1 steps ahead, then move both until fast reaches null.
 * slow will be just before the target — skip it.
 *
 * Time:  O(N) — single pass
 * Space: O(1)
 */
fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
    // 1. Create a dummy node to handle edge cases (like removing the head)
    val dummy = ListNode(0, null)
    dummy.next = head

    var fast: ListNode? = dummy
    var slow: ListNode? = dummy

    // 2. Move fast pointer so that there is a gap of n+1 nodes between fast and slow
    var i = 0
    while (i in 0..n) {
        fast = fast?.next
        i++
    }

    // 3. Move both until fast reaches the end
    while (fast != null) {
        fast = fast.next
        slow = slow?.next
    }

    // 4. Skip the nth node
    slow?.next = slow.next?.next

    return dummy.next
}
