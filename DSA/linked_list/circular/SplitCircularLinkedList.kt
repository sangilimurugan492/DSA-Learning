package linked_list.circular

/**
 * Split a Circular Linked List into two halves.
 * If the list has odd number of nodes, first half should have one more node.
 * Example: [1→2→3→4→5→(back to 1)] → [1→2→3→(back to 1)] and [4→5→(back to 4)]
 * FAANG Importance: ⭐⭐⭐⭐ (Fast/slow pointer on circular LL)
 */

fun main() {
    val cll = CircularLinkedListSplit()
    cll.insert(1)
    cll.insert(2)
    cll.insert(3)
    cll.insert(4)
    cll.insert(5)

    println("Original circular list:")
    cll.display()

    val (head1, head2) = cll.splitList()
    println("First half:")
    displaySplitList(head1)
    println("Second half:")
    displaySplitList(head2)
}

class CSplitNode(val value: Int) {
    var next: CSplitNode? = null
}

class CircularLinkedListSplit {
    private var head: CSplitNode? = null
    private var tail: CSplitNode? = null

    fun insert(value: Int) {
        val newNode = CSplitNode(value)
        if (head == null) {
            head = newNode
            tail = newNode
            newNode.next = head
        } else {
            tail!!.next = newNode
            tail = newNode
            tail!!.next = head
        }
    }

    /**
     * SPLIT: O(N) time, O(1) space
     * Use fast/slow pointer to find middle, then break into two circular lists.
     */
    fun splitList(): Pair<CSplitNode?, CSplitNode?> {
        if (head == null) return Pair(null, null)
        if (head!!.next == head) return Pair(head, null)

        // Find middle using fast/slow pointer
        var slow = head
        var fast = head

        while (fast!!.next != head && fast.next!!.next != head) {
            slow = slow!!.next
            fast = fast.next!!.next
        }

        // If even number of nodes, move fast to last node
        if (fast.next!!.next == head) {
            fast = fast.next
        }

        // Set head of second half
        val head2 = slow!!.next

        // Make first half circular
        slow.next = head

        // Make second half circular
        fast!!.next = head2

        return Pair(head, head2)
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

fun displaySplitList(head: CSplitNode?) {
    if (head == null) { println("Empty"); return }
    var curr = head
    do {
        print("${curr!!.value} → ")
        curr = curr.next
    } while (curr != head)
    println("(back to ${head.value})")
}
