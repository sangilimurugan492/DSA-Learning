package linked_list.single

/**
 * https://leetcode.com/problems/reorder-list/description/?envType=problem-list-v2&envId=linked-list
 */
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
   reorderListBrute(l1)
   reorderList(l1)

}

fun reorderListBrute(head: ListNode?) {
    if (head == null) return
    val nodes = mutableListOf<ListNode>()
    var curr = head
    while (curr != null) {
        nodes.add(curr)
        curr = curr.next
    }

    var i = 0
    var j = nodes.size - 1
    while (i < j) {
        nodes[i].next = nodes[j]
        i++
        if (i == j) break
        nodes[j].next = nodes[i]
        j--
    }
    nodes[i].next = null

    var temp = head
    while (temp != null) {
        print("${temp.`val`} ")
        temp = temp.next
    }
    println()
}

fun reorderList(head: ListNode?) {
    if (head?.next == null) return

    // 1. Find the middle of the list
    var slow = head
    var fast = head
    while (fast?.next != null && fast.next?.next != null) {
        slow = slow?.next
        fast = fast.next?.next
    }

    // 2. Reverse the second half
    var secondHalf: ListNode? = reverse(slow?.next)
    slow?.next = null // Break the list into two halves

    // 3. Merge the two halves
    var firstHalf = head
    while (secondHalf != null) {
        val temp1 = firstHalf?.next
        val temp2 = secondHalf.next

        firstHalf?.next = secondHalf
        secondHalf.next = temp1

        firstHalf = temp1
        secondHalf = temp2
    }
    var temp = head
    while (temp != null) {
        print("${temp.`val`} ")
        temp = temp.next
    }
}

private fun reverse(head: ListNode?): ListNode? {
    var prev: ListNode? = null
    var curr = head
    while (curr != null) {
        val nextNode = curr.next
        curr.next = prev
        prev = curr
        curr = nextNode
    }
    return prev
}

