package linked_list.circular

/**
 * Insert a node in a sorted Circular Linked List.
 * The list is sorted in ascending order and is circular.
 * FAANG Importance: ⭐⭐⭐⭐ (Edge case handling in circular LL)
 */

fun main() {
    val cll = SortedCircularLinkedList()
    cll.insert(1)
    cll.insert(3)
    cll.insert(5)
    cll.insert(7)

    println("Before insertions:")
    cll.display()

    cll.sortedInsert(0)
    println("After inserting 0:")
    cll.display()

    cll.sortedInsert(4)
    println("After inserting 4:")
    cll.display()

    cll.sortedInsert(9)
    println("After inserting 9:")
    cll.display()
}

class CSCNode(val value: Int) {
    var next: CSCNode? = null
}

class SortedCircularLinkedList {
    private var head: CSCNode? = null

    /**
     * Insert in sorted order: O(N) time
     * Cases: 1) Empty list  2) Insert before head  3) Insert in middle  4) Insert at end
     */
    fun sortedInsert(data: Int) {
        val newNode = CSCNode(data)

        // Case 1: Empty list
        if (head == null) {
            head = newNode
            newNode.next = head
            return
        }

        var curr = head

        // Case 2: Insert before head (new node is smallest)
        if (data <= head!!.value) {
            // Find the last node to update its next
            while (curr!!.next != head) {
                curr = curr.next
            }
            curr.next = newNode
            newNode.next = head
            head = newNode
            return
        }

        // Case 3 & 4: Insert in middle or at end
        while (curr!!.next != head && curr.next!!.value < data) {
            curr = curr.next
        }

        newNode.next = curr.next
        curr.next = newNode
    }

    /** Helper to build initial sorted list */
    fun insert(data: Int) {
        sortedInsert(data)
    }

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
