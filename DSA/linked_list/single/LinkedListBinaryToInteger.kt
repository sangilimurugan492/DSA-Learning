package linked_list.single

fun main() {
    val  l1 = ListNode(1, null)
    val  l2 = ListNode(0, null)
    val  l3 = ListNode(1, null)
    val  l4 = ListNode(1, null)
    l1.next = l2
    l2.next = l3
    l3.next = l4
    println(getDecimalValue(l1))
}
fun getDecimalValue(head: ListNode?): Int {
    var res = 0
    var curr = head

    while (curr != null) {
        // Left shift the result by 1 (same as res * 2)
        // and add the current bit
        res = (res shl  1) or curr.`val`

        // Move to next node
        curr = curr.next
    }

    return res
}