package linked_list.single

fun main() {
    val  l1 = ListNode(1, null)
    val  l2 = ListNode(4, null)
    val  l3 = ListNode(3, null)
    val  l4 = ListNode(2, null)
    val  l5 = ListNode(5, null)
    val  l6 = ListNode(2, null)
    l1.next = l2
    l2.next = l3
    l3.next = l4
    l4.next = l5
    l5.next = l6
    var temp = partitionBF(l1, 3)
    while (temp != null) {
        println(temp.`val`)
        temp = temp.next
    }
}

/**
 * 1. Brute Force (Extra Space)The simplest way to think about this is to extract values into a list, filter them, and rebuild the linked list.
 * Logic:Traverse the linked list and store all values in a MutableList.
 * Create two new lists: one for values $< x$ and one for values $\ge x$.Combine these two lists.Create a new linked list from the combined values.
 * Complexity:Time Complexity: $O(n)$ — Two passes over the data.
 * Space Complexity: $O(n)$ — To store the values in lists.
 */
fun partitionBF(head: ListNode?, x: Int): ListNode? {
    val beforeList = mutableListOf<Int>()
    val afterList = mutableListOf<Int>()
    var temp = head
    while (temp != null) {
        if (temp.`val` < x) {
            beforeList.add(temp.`val`)
        } else {
            afterList.add(temp.`val`)
        }
        temp = temp.next
    }
    beforeList.addAll(afterList)
    var newHead = ListNode(beforeList[0], null)
    temp = newHead
    for(i in 1 until beforeList.size) {
        temp?.next = ListNode(beforeList[i], next = null)
        temp = temp?.next!!
    }

    return newHead
}

/**
 * Time Complexity O(n)
 * Space Complexity O(1) In-Place Ordering
 */
fun partitionOP(head: ListNode?, x: Int): ListNode? {
    // Dummy nodes to provide a starting point for the two partitions
    val beforeHead = ListNode(0, null)
    val afterHead = ListNode(0, null)

    // Pointers to the current end of the two lists
    var before: ListNode? = beforeHead
    var after: ListNode? = afterHead

    var curr = head

    while (curr != null) {
        if (curr.`val` < x) {
            before?.next = curr
            before = before?.next
        } else {
            after?.next = curr
            after = after?.next
        }
        curr = curr.next
    }

    // Important: Terminate the 'after' list to avoid cycles
    after?.next = null

    // Combine the two lists
    before?.next = afterHead.next

    return beforeHead.next
}