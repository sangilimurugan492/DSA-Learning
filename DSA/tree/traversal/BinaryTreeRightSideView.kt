package tree.traversal

import tree.TreeNode

/**
 * https://leetcode.com/problems/binary-tree-right-side-view/
 * Given the root of a binary tree, return the right side view (values visible from the right).
 * Example: [1,2,3,null,5,null,4] → [1,3,4]
 * FAANG Importance: ⭐⭐⭐⭐ (BFS/DFS level tracking)
 */

fun main() {
    val root = TreeNode(1).apply {
        left = TreeNode(2).apply { right = TreeNode(5) }
        right = TreeNode(3).apply { right = TreeNode(4) }
    }
    println(rightSideViewBFS(root))
    println(rightSideViewDFS(root))
}

/**
 * BFS: O(N) time, O(W) space — take last node of each level
 */
fun rightSideViewBFS(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    if (root == null) return result
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    while (queue.isNotEmpty()) {
        val levelSize = queue.size
        for (i in 0 until levelSize) {
            val node = queue.removeFirst()
            if (i == levelSize - 1) result.add(node.`val`)
            node.left?.let { queue.addLast(it) }
            node.right?.let { queue.addLast(it) }
        }
    }
    return result
}

/**
 * DFS: O(N) time, O(H) space — traverse right first, add first node seen at each depth
 */
fun rightSideViewDFS(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    fun dfs(node: TreeNode?, depth: Int) {
        if (node == null) return
        if (depth == result.size) result.add(node.`val`)
        dfs(node.right, depth + 1)  // Right first!
        dfs(node.left, depth + 1)
    }
    dfs(root, 0)
    return result
}
