package linked_list.double.least_recently_used_lru

fun main() {
    val lruCache = LRUCache(2)
    lruCache.put(1,1)
    lruCache.put(2,2)
    println(lruCache.get(1))
    lruCache.put(3,3)
    println(lruCache.get(2))
    lruCache.put(4,4)
    println(lruCache.get(1))
    println(lruCache.get(3))
    println(lruCache.get(4))

}

class LRUCache(val capacity: Int) {

    class Node(val key: Int, var value: Int) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val map = HashMap<Int, Node>()
    private val head = Node(0, 0) // Dummy Head
    private val tail = Node(0, 0) // Dummy Tail

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: Int): Int {
        val node = map[key] ?: return -1
        moveToHead(node)
        return node.value
    }

    fun put(key: Int, value: Int) {
        val node = map[key]
        if (node != null) {
            node.value = value
            moveToHead(node)
        } else {
            if (map.size >= capacity) {
                // Evict the least recently used from tail
                val lru = tail.prev!!
                removeNode(lru)
                map.remove(lru.key)
            }
            val newNode = Node(key, value)
            addNode(newNode)
            map[key] = newNode
        }
    }

    // Helper: Add node right after head
    private fun addNode(node: Node) {
        node.prev = head
        node.next = head.next
        head.next?.prev = node
        head.next = node
    }

    // Helper: Remove an existing node from the list
    private fun removeNode(node: Node) {
        val prev = node.prev
        val next = node.next
        prev?.next = next
        next?.prev = prev
    }

    // Helper: Move a node to the front
    private fun moveToHead(node: Node) {
        removeNode(node)
        addNode(node)
    }
}
