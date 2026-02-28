package linked_list

class MyLinkedList {
    var head : Node? = null

    fun addNodeFirst(data : Int) {
        val node = Node(data, null)
        if (head == null) {
            head = node
        } else {
            node.next = head
            head = node
        }
    }

    fun getMiddleNode(head : Node?) : Int {
        if(head != null) {
            var slow = head
            var fast = head
            while (fast != null && fast.next != null) {
                slow = slow!!.next!!
                fast = fast.next!!.next!!
            }
            return slow!!.data
        }
        return -1
    }
}