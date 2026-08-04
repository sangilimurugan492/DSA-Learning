package linked_list.double.find_pairs_with_given_sum

import linked_list.double.d_list_node.DListNode

/**
 * https://www.geeksforgeeks.org/find-pairs-given-sum-doubly-linked-list/
 * Find all pairs of nodes in a sorted DLL that sum to a given value.
 * Example: DLL = [1,2,3,4,5,6], sum = 5 → (1,4), (2,3)
 * FAANG Importance: ⭐⭐⭐⭐ (Two pointer on DLL, leverages bidirectional traversal)
 */

fun main() {
    val head = DListNode(1)
    val second = DListNode(2)
    val third = DListNode(3)
    val fourth = DListNode(4)
    val fifth = DListNode(5)
    val sixth = DListNode(6)

    head.next = second; second.prev = head
    second.next = third; third.prev = second
    third.next = fourth; fourth.prev = third
    fourth.next = fifth; fifth.prev = fourth
    fifth.next = sixth; sixth.prev = fifth

    println("Pairs with sum 5:")
    findPairsWithSum(head, 5)

    println("Pairs with sum 7:")
    findPairsWithSum(head, 7)
}

/**
 * TWO POINTER: O(N) time, O(1) space
 * Since DLL is sorted, use left (head) and right (tail) pointers.
 * Move pointers based on current sum vs target.
 */
fun findPairsWithSum(head: DListNode?, target: Int) {
    if (head == null || head.next == null) return

    // Find tail
    var right = head
    while (right?.next != null) {
        right = right.next
    }

    var left: DListNode? = head

    while (left != null && right != null && left != right && left?.prev != right) {
        val sum = left!!.`val` + right!!.`val`
        when {
            sum == target -> {
                println("(${left!!.`val`}, ${right!!.`val`})")
                left = left!!.next
                right = right!!.prev
            }
            sum < target -> left = left!!.next
            else -> right = right!!.prev
        }
    }
}
