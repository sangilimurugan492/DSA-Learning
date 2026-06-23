package tree.traversal

import tree.TreeNode

/**
 * https://leetcode.com/problems/binary-tree-level-order-traversal/
 * Return the level order traversal of a binary tree (left to right, level by level).
 * FAANG Importance: ⭐⭐⭐⭐⭐ (BFS on trees — fundamental pattern)
 */

fun main() {
    val root = TreeNode(3).apply {
        left = TreeNode(9)
        right = TreeNode(20).apply {
            left = TreeNode(15)
            right = TreeNode(7)
        }
    }
    println(levelOrderBottomUp(root))
}

/**
 * Bottom-Up Level Order: Return levels from bottom to top.
 * Same as level order, then reverse the result.
 * O(N) time, O(N) space
 */
fun levelOrderBottomUp(root: TreeNode?): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    if (root == null) return result
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    while (queue.isNotEmpty()) {
        val levelSize = queue.size
        val level = mutableListOf<Int>()
        repeat(levelSize) {
            val node = queue.removeFirst()
            level.add(node.`val`)
            node.left?.let { queue.addLast(it) }
            node.right?.let { queue.addLast(it) }
        }
        result.add(level)
    }
    return result.reversed()
}
