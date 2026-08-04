package linked_list.single.reverse_linked_list_k_group

import linked_list.single.list_node.ListNode

/**
 * Input: head = [1,2,3,4,5], k = 2
 * Output: [2,1,4,3,5]
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
    val result = reverseKGroup(l1, 3)
    var prResult = result
    while (prResult != null) {
        print("${prResult.`val`} ->" )
        prResult =prResult.next
    }
}

/**
 * Brute Force Approach
 */
fun reverseKGroup(head: ListNode? , k : Int) : ListNode? {
    if (head == null) {
        return head
    }

    var current = head
    var curHead = head
    var count = 0
    var dummy = ListNode(0, null)
    var res: ListNode? = dummy

    while (current != null) {
        if (count == 0) {
            curHead = current
        }
        count++

        if (count % k == 0) {
            val next = current.next
            current.next = null
            while (res?.next != null) {
                res = res.next
            }
            res?.next = reverseList(curHead)
            current = next
            count = 0
            curHead = null
        } else {
            current = current.next
        }

    }
    if (curHead != null) {
        while (res?.next != null) {
            res = res.next
        }
        res?.next = curHead
    }


    return dummy.next
}

fun reverseList(head : ListNode?) : ListNode? {
    if (head == null) return head
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