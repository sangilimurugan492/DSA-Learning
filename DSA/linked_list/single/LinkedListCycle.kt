package linked_list.single

/**
 * https://leetcode.com/problems/linked-list-cycle/
 * Given head of linked list, determine if it has a cycle.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Fast/Slow pointer)
 */

fun main() {
    val head = ListNode(3, null).apply {
        next = ListNode(2, null).apply {
            next = ListNode(0, null).apply {
                next = ListNode(-4, null).apply { next = this@apply.next }  // cycle to node 2
            }
        }
    }
    println(hasCycleHashSet(head))
    println(hasCycleFastSlow(head))
}

/**
 * BRUTE FORCE: O(N) time, O(N) space — HashSet
 * Store visited nodes. If we see a node again → cycle.
 */
fun hasCycleHashSet(head: ListNode?): Boolean {
    val visited = mutableSetOf<ListNode>()
    var curr = head
    while (curr != null) {
        if (curr in visited) return true
        visited.add(curr)
        curr = curr.next
    }
    return false
}

/**
 * OPTIMAL: O(N) time, O(1) space — Floyd's Tortoise & Hare
 * Slow moves 1 step, fast moves 2 steps. If they meet → cycle.
 */
fun hasCycleFastSlow(head: ListNode?): Boolean {
    var slow = head
    var fast = head
    while (fast != null && fast.next != null) {
        slow = slow!!.next
        fast = fast.next!!.next
        if (slow == fast) return true
    }
    return false
}
