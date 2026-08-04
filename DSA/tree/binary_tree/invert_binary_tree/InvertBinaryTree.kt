package patterns.tree_dfs.invert_binary_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/invert-binary-tree/
 * Invert a binary tree (mirror it — swap left and right for every node).
 * Example:
 *      4              4
 *     / \            / \
 *    2   7    →    7   2
 *   / \ / \        / \ / \
 *  1  3 6  9      9  6 3  1
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic tree recursion — Google interview question!)
 */

fun main() {
    val root = TreeNode(4).apply {
        left = TreeNode(2).apply { left = TreeNode(1); right = TreeNode(3) }
        right = TreeNode(7).apply { left = TreeNode(6); right = TreeNode(9) }
    }
    val inverted = invertTree(root)
    println(inverted!!.`val`)  // 4
    println(inverted.left!!.`val`)  // 7
    println(inverted.right!!.`val`)  // 2
}

/**
 * Recursive DFS: O(N) time, O(H) space
 * Swap left and right, then recurse on both subtrees.
 */
fun invertTree(root: TreeNode?): TreeNode? {
    if (root == null) return null
    val temp = root.left
    root.left = root.right
    root.right = temp
    invertTree(root.left)
    invertTree(root.right)
    return root
}

/**
 * Iterative BFS: O(N) time, O(W) space
 * Use a queue, swap children for each node.
 */
fun invertTreeBFS(root: TreeNode?): TreeNode? {
    if (root == null) return null
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        val temp = node.left
        node.left = node.right
        node.right = temp
        node.left?.let { queue.addLast(it) }
        node.right?.let { queue.addLast(it) }
    }
    return root
}
