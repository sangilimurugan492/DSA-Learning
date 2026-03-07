package array_traversals.linked_list

import linked_list.ListNode

class TreeNode (data : Int) {
    var left: TreeNode? = null
    var right : TreeNode? = null

}

fun main() {

}

fun sortedListToBSTBF(head: ListNode?): TreeNode? {
    val values = mutableListOf<Int>()
    var curr = head
    while (curr != null) {
        values.add(curr.`val`)
        curr = curr.next
    }

    fun buildBST(left: Int, right: Int): TreeNode? {
        if (left > right) return null
        val mid = left + (right - left) / 2
        val node = TreeNode(values[mid])
        node.left = buildBST(left, mid - 1)
        node.right = buildBST(mid + 1, right)
        return node
    }

    return buildBST(0, values.size - 1)
}

fun sortedListToBSTOP1(head: ListNode?): TreeNode? {
    if (head == null) return null
    if (head.next == null) return TreeNode(head.`val`)

    // Find the middle element and the node before it
    var prev: ListNode? = null
    var slow = head
    var fast = head

    while (fast != null && fast.next != null) {
        prev = slow
        slow = slow?.next
        fast = fast.next?.next
    }

    // 'slow' is now the middle node. Disconnect the left half.
    prev?.next = null

    val root = TreeNode(slow!!.`val`)

    // Recursively build left (start to prev) and right (slow.next to end)
    root.left = sortedListToBSTOP1(head)
    root.right = sortedListToBSTOP1(slow.next)

    return root
}

class Solution {
    private var currentHead: ListNode? = null

    fun sortedListToBST(head: ListNode?): TreeNode? {
        var n = 0
        var temp = head
        while (temp != null) {
            temp = temp.next
            n++
        }
        currentHead = head
        return convert(0, n - 1)
    }

    private fun convert(left: Int, right: Int): TreeNode? {
        if (left > right) return null

        val mid = left + (right - left) / 2

        // 1. Build left subtree
        val leftChild = convert(left, mid - 1)

        // 2. Build root from current head
        val root = TreeNode(currentHead!!.`val`)
        root.left = leftChild
        currentHead = currentHead?.next // Move pointer forward

        // 3. Build right subtree
        root.right = convert(mid + 1, right)

        return root
    }
}