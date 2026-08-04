package linked_list.circular.circular_linked_list

/**
 * Circular Linked List implementation and operations.
 * Last node's next points back to head, forming a circle.
 * FAANG Importance: ⭐⭐⭐ (Used in round-robin scheduling, Josephus problem)
 */

fun main() {
    val cll = CircularLinkedList()
    cll.insert(10)
    cll.insert(20)
    cll.insert(30)
    cll.insert(40)
    cll.display()
    println("Has cycle: ${cll.hasCycle()}")
    println("Remove 30:")
    cll.delete(30)
    cll.display()
}

class CListNode(val value: Int) {
    var next: CListNode? = null
}

class CircularLinkedList {
    private var head: CListNode? = null
    private var tail: CListNode? = null

    fun insert(value: Int) {
        val newNode = CListNode(value)
        if (head == null) {
            head = newNode
            tail = newNode
            newNode.next = head  // point to itself
        } else {
            tail!!.next = newNode
            tail = newNode
            tail!!.next = head  // maintain circular link
        }
    }

    fun delete(value: Int) {
        if (head == null) return
        var curr = head
        var prev: CListNode? = tail
        do {
            if (curr!!.value == value) {
                if (curr == head && curr == tail) { head = null; tail = null }
                else {
                    prev!!.next = curr.next
                    if (curr == head) head = curr.next
                    if (curr == tail) tail = prev
                }
                return
            }
            prev = curr
            curr = curr.next
        } while (curr != head)
    }

    fun hasCycle(): Boolean = head != null  // Circular LL always has cycle by definition

    fun display() {
        if (head == null) { println("Empty"); return }
        var curr = head
        do {
            print("${curr!!.value} → ")
            curr = curr.next
        } while (curr != head)
        println("(back to ${head!!.value})")
    }
}
