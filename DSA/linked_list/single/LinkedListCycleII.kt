package linked_list.single

/**
 * https://leetcode.com/problems/linked-list-cycle-ii/
 * Given head of linked list, return node where cycle begins. Return null if no cycle.
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    // Create list with cycle: 3→2→0→-4→(back to 2)
    val node2 = ListNode(2, null)
    val head = ListNode(3, null).apply { next = node2 }
    node2.next = ListNode(0, null).apply { next = ListNode(-4, null).apply { next = node2 } }
    println(detectCycleHashSet(head)?.`val`)  // 2
    println(detectCycleFloyd(head)?.`val`)    // 2
}

/** BRUTE FORCE: O(N) time, O(N) space — HashSet */
fun detectCycleHashSet(head: ListNode?): ListNode? {
    val visited = mutableSetOf<ListNode>()
    var curr = head
    while (curr != null) {
        if (curr in visited) return curr
        visited.add(curr)
        curr = curr.next
    }
    return null
}

/**
 * OPTIMAL: O(N) time, O(1) space — Floyd's algorithm
 * Phase 1: Find meeting point (fast/slow pointers)
 * Phase 2: Reset one pointer to head, move both 1 step until they meet = cycle start
 */
fun detectCycleFloyd(head: ListNode?): ListNode? {
    var slow = head
    var fast = head

    // Phase 1: Find meeting point
    while (fast != null && fast.next != null) {
        slow = slow!!.next
        fast = fast.next!!.next
        if (slow == fast) {
            // Phase 2: Find cycle start
            var ptr1 = head
            var ptr2 = slow
            while (ptr1 != ptr2) {
                ptr1 = ptr1!!.next
                ptr2 = ptr2!!.next
            }
            return ptr1
        }
    }
    return null
}
