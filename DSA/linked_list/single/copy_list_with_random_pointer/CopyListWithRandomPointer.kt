package linked_list.single.copy_list_with_random_pointer

/**
 * https://leetcode.com/problems/copy-list-with-random-pointer/
 * Deep copy a linked list where each node has a random pointer.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Tests understanding of references & hashing)
 */

class NodeRandom(val `val`: Int) {
    var next: NodeRandom? = null
    var random: NodeRandom? = null
}

fun main() {
    val n1 = NodeRandom(7)
    val n2 = NodeRandom(13)
    val n3 = NodeRandom(11)
    val n4 = NodeRandom(10)
    val n5 = NodeRandom(1)

    n1.next = n2
    n2.next = n3
    n2.random = n1
    n3.next = n4
    n3.random = n5
    n4.next = n5
    n4.random = n3
    n5.random = n1

    val copy = copyRandomListHashMap(n1)
    var curr = copy
    while (curr != null) {
        val randVal = curr.random?.`val` ?: "null"
        print("(${curr.`val`}, random=$randVal) → ")
        curr = curr.next
    }
    println("null")
}

/**
 * APPROACH 1: HashMap — O(N) time, O(N) space
 * Create mapping from old node → new node, then wire up next/random.
 */
fun copyRandomListHashMap(head: NodeRandom?): NodeRandom? {
    val map = HashMap<NodeRandom, NodeRandom>()
    var curr = head
    while (curr != null) {
        map[curr] = NodeRandom(curr.`val`)
        curr = curr.next
    }
    curr = head
    while (curr != null) {
        map[curr]!!.next = map[curr.next]
        map[curr]!!.random = map[curr.random]
        curr = curr.next
    }
    return map[head]
}

/**
 * APPROACH 2: Interweave — O(N) time, O(1) space
 * 1. Create copy nodes and insert them right after original nodes
 * 2. Set random pointers for copy nodes
 * 3. Separate the interwoven list
 */
fun copyRandomListOptimal(head: NodeRandom?): NodeRandom? {
    // Step 1: Interweave copy nodes
    var curr = head
    while (curr != null) {
        val copy = NodeRandom(curr.`val`)
        copy.next = curr.next
        curr.next = copy
        curr = copy.next
    }

    // Step 2: Set random pointers
    curr = head
    while (curr != null) {
        curr.next?.random = curr.random?.next
        curr = curr.next?.next
    }

    // Step 3: Separate lists
    val dummy = NodeRandom(0)
    var copyCurr = dummy
    curr = head
    while (curr != null) {
        copyCurr.next = curr.next
        copyCurr = copyCurr.next!!
        curr.next = curr.next?.next
        curr = curr.next
    }

    return dummy.next
}
