package linked_list.single.middle_of_linked_list

import linked_list.single.list_node.ListNode

/**
 * https://leetcode.com/problems/middle-of-the-linked-list/
 * Given head, return the middle node. If even length, return second middle.
 * Example: [1,2,3,4,5] → node 3 | [1,2,3,4,5,6] → node 4
 * FAANG Importance: ⭐⭐⭐⭐ (Fast/Slow pointer pattern)
 */

fun main() {
    val head = ListNode(1).apply { next = ListNode(2).apply { next = ListNode(3).apply { next = ListNode(4).apply { next = ListNode(5) } } } }
    println(middleNodeArray(head)?.`val`)
    println("---")
    val head2 = ListNode(1).apply { next = ListNode(2).apply { next = ListNode(3).apply { next = ListNode(4).apply { next = ListNode(5).apply { next = ListNode(6) } } } } }
    println(middleNodeFastSlow(head2)?.`val`)
}

/** BRUTE FORCE: O(N) time, O(N) space — store all nodes in array, return middle */
fun middleNodeArray(head: ListNode?): ListNode? {
    val nodes = mutableListOf<ListNode>()
    var curr = head
    while (curr != null) { nodes.add(curr); curr = curr.next }
    return nodes[nodes.size / 2]
}

/** OPTIMAL: O(N) time, O(1) space — Fast/Slow pointer. Fast moves 2x, when fast reaches end, slow is at middle */
fun middleNodeFastSlow(head: ListNode?): ListNode? {
    var slow = head
    var fast = head
    while (fast != null && fast.next != null) {
        slow = slow!!.next
        fast = fast.next!!.next
    }
    return slow
}
