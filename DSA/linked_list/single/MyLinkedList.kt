package linked_list.single

class MyLinkedList(node: ListNode) {
    private var head : ListNode? = node

    fun addNodeFirst(data : Int) {
        val node = ListNode(data, null)
        if (head == null) {
            head = node
        } else {
            node.next = head
            head = node
        }
    }

    fun addNode(data : Int) {
        val node = ListNode(data, null)
        if (head == null) {
            head = node
        } else {
            head?.next = node
        }
    }

    fun getMiddleNode(head : ListNode?) : Int {
        if(head != null) {
            var slow = head
            var fast = head
            while (fast != null && fast.next != null) {
                slow = slow!!.next!!
                fast = fast.next!!.next!!
            }
            return slow!!.`val`
        }
        return -1
    }

    /**
     * Example:
     * var li = ListNode(5)
     * var v = li.`val`
     * Definition for singly-linked list.
     * class ListNode(var `val`: Int) {
     *     var next: ListNode? = null
     * }
     */


}

fun add(l1: ListNode?, l2: ListNode?, carry: Int): ListNode? {
    if(l1 == null && l2 == null && carry == 0) return null

    val value1 = l1?.`val` ?: 0
    val value2 = l2?.`val` ?: 0

    val sum = value1 + value2 + carry
    val newCarry = sum / 10
    val newNodeValue = sum % 10

    val nextNode = add(l1?.next, l2?.next, newCarry)

    return ListNode(newNodeValue, null).apply {
        next = nextNode
    }
}
fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
    return add(l1, l2, 0)
}

fun main() {
    val firstL = ListNode(9, null)
    val secodeL = ListNode(1, ListNode(9, ListNode(9, ListNode(9,
        ListNode(9, ListNode(9, ListNode(9, ListNode(9, ListNode(9, ListNode(9, null))))))
    ))))

    var result = addTwoNumbers(firstL, secodeL)
    while (result!= null) {
        print("${result.`val`}")
        result = result.next
    }
}