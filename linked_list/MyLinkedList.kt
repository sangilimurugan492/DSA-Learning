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
}