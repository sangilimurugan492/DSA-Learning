package linked_list.single

/**
 * Given the head of a singly linked list and two integers left and right where left <= right,
 * reverse the nodes of the list from position left to position right, and return the reversed list.
 *
 * Input: head = [1,2,3,4,5], left = 2, right = 4
 * Output: [1,4,3,2,5]
 * Example 2:
 *
 * Input: head = [5], left = 1, right = 1
 * Output: [5]
 *
 * Constraints:
 *
 * The number of nodes in the list is n.
 * 1 <= n <= 500
 * -500 <= Node.val <= 500
 * 1 <= left <= right <= n
 *
 *
 * Follow up: Could you do it in one pass?
 */

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
    val result = reverseLinkedListII(l1, 1, 5)
    var prResult = result
    while (prResult != null) {
        print("${prResult.`val`} ->" )
        prResult =prResult.next
    }
}

fun reverseLinkedListII(head : ListNode?, left : Int, right : Int) : ListNode? {
    if (head == null) return head

    var current = head
    var count = 0
    var start : ListNode? = null
    var end : ListNode? = null
    var startPrev : ListNode? = null
    var endPrev : ListNode? = null

    while (current != null) {
        count++

        if (count == left) {
            start = current
        }

        if (count == left -1) {
            startPrev = current
        }

        if (count == right) {
            end = current
            endPrev = current.next
            end.next = null
            break
        }

        current = current.next
    }

    var revList = reverseListII(start)
    if (left > 1) {
        startPrev?.next = revList
    } else {
        startPrev = revList
    }

    while (revList?.next != null) {
        revList = revList.next
    }
    
    revList?.next = endPrev


    return startPrev
}

fun reverseListII(head :ListNode?) : ListNode?{
    if (head == null) {
        return head
    }

    var current = head
    var prev : ListNode? = null

    while (current != null) {
        val nextNode = current.next
        current.next = prev
        prev = current
        current = nextNode
    }

    return prev
}