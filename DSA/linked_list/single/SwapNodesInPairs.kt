package linked_list.single

fun main() {
    var head = swapPairsRe(null)
    while (head != null) {
        println(head.`val`)
        head = head.next
    }
}

/**
 * Recursive Approach
 * Time complexity O(N)
 * Space complexity O(N)
 */
fun swapPairsRe(head: ListNode?): ListNode? {
    // Base case: if list is empty or has 1 node
    if (head?.next == null) {
        return head
    }

    // Nodes to be swapped
    val firstNode = head
    val secondNode = head.next

    // Swapping logic
    firstNode.next = swapPairsRe(secondNode?.next)
    secondNode?.next = firstNode

    // Now secondNode is the new head
    return secondNode
}

/**
 * Recursive Approach
 * Time complexity O(N)
 * Space complexity O(1)
 */
fun swapPairsIt(head: ListNode?): ListNode? {
    val dummy = ListNode(0, null)
    dummy.next = head
    var prev = dummy

    while (prev.next != null && prev.next?.next != null) {
        val first = prev.next
        val second = prev.next?.next

        // Swapping
        first?.next = second?.next
        second?.next = first
        prev.next = second

        // Re-positioning 'prev' for the next pair
        prev = first!!
    }

    return dummy.next
}