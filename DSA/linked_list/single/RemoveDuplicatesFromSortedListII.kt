package linked_list.single

/**
 * https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/?envType=problem-list-v2&envId=linked-list
 * Input: head = [1,2,3,3,4,4,5]
 * Output: [1,2,5]
 */
fun main() {

}

fun deleteDuplicatesBruteForce(head: ListNode?): ListNode? {
    val counts = mutableMapOf<Int, Int>()
    var curr = head
    while (curr != null) {
        counts[curr.`val`] = counts.getOrDefault(curr.`val`, 0) + 1
        curr = curr.next
    }

    val dummy = ListNode(0, null)
    var tail: ListNode? = dummy
    curr = head
    while (curr != null) {
        if (counts[curr.`val`] == 1) {
            tail?.next = ListNode(curr.`val`, null)
            tail = tail?.next
        }
        curr = curr.next
    }
    return dummy.next
}

fun deleteDuplicates(head: ListNode?): ListNode? {
        // Dummy node helps handle deletions at the start of the list
        val dummy = ListNode(0, null)
        dummy.next = head
        var prev = dummy
        var curr = head

        while (curr != null) {
            // Check if curr is the start of a duplicate sequence
            if (curr.next != null && curr.`val` == curr.next?.`val`) {
                // Move curr to the end of the duplicate sequence
                while (curr?.next != null && curr?.`val` == curr?.next?.`val`) {
                    curr = curr?.next
                }
                // Skip all duplicates by linking prev to the node after duplicates
                prev.next = curr?.next
            } else {
                // No duplicate found, move prev forward
                prev = prev.next!!
            }
            // Move curr to the next potential distinct node
            curr = curr?.next
        }

        return dummy.next
}