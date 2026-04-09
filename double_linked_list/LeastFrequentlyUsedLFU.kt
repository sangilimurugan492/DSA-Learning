package double_linked_list

import double_linked_list.LRUCache.Node

fun main() {
    val lfuCache = LFUCache(2)
    lfuCache.put(1,1)
    lfuCache.put(2,2)
    println(lfuCache.get(1))
    lfuCache.put(3,3)
    println(lfuCache.get(2))
    lfuCache.put(4,4)
    println(lfuCache.get(1))
    println(lfuCache.get(3))
    println(lfuCache.get(4))
}


class LFUCache2(capacity: Int) {

    class Node(val key: Int, var value: Int, var counter : Int) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val map = HashMap<Int, double_linked_list.LRUCache.Node>()
    private val head = Node(0, 0) // Dummy Head
    private val tail = Node(0, 0) // Dummy Tail

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: Int): Int {

        return 0
    }

    fun put(key: Int, value: Int) {

    }

}

class LFUCache(val capacity: Int) {
    class Node(val key: Int, var value: Int, var freq: Int = 1) {
        var prev: Node? = null
        var next: Node? = null
    }

    class DLList {
        private val head = Node(0, 0)
        private val tail = Node(0, 0)
        var size = 0

        init {
            head.next = tail
            tail.prev = head
        }

        fun addNode(node: Node) {
            node.next = head.next
            node.prev = head
            head.next?.prev = node
            head.next = node
            size++
        }

        fun removeNode(node: Node) {
            node.prev?.next = node.next
            node.next?.prev = node.prev
            size--
        }

        fun removeTail(): Node? {
            if (size == 0) return null
            val lastNode = tail.prev!!
            removeNode(lastNode)
            return lastNode
        }
    }

    private val nodeMap = HashMap<Int, Node>()
    private val freqMap = HashMap<Int, DLList>()
    private var minFreq = 0

    fun get(key: Int): Int {
        val node = nodeMap[key] ?: return -1
        updateNode(node)
        return node.value
    }

    fun put(key: Int, value: Int) {
        if (capacity == 0) return

        if (nodeMap.containsKey(key)) {
            val node = nodeMap[key]!!
            node.value = value
            updateNode(node)
        } else {
            if (nodeMap.size >= capacity) {
                val minList = freqMap[minFreq]!!
                val evicted = minList.removeTail()!!
                nodeMap.remove(evicted.key)
            }

            val newNode = Node(key, value)
            nodeMap[key] = newNode
            minFreq = 1
            freqMap.getOrPut(1) { DLList() }.addNode(newNode)
        }
    }

    private fun updateNode(node: Node) {
        val oldFreq = node.freq
        val list = freqMap[oldFreq]!!
        list.removeNode(node)

        if (oldFreq == minFreq && list.size == 0) {
            minFreq++
        }

        node.freq++
        freqMap.getOrPut(node.freq) { DLList() }.addNode(node)
    }
}