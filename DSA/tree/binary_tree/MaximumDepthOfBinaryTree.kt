package tree.binary_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/maximum-depth-of-binary-tree/
 * Find the maximum depth (height) of a binary tree.
 * Example: [3,9,20,null,null,15,7] → depth = 3
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Most basic tree recursion pattern)
 */

fun main() {
    val root = TreeNode(3).apply {
        left = TreeNode(9)
        right = TreeNode(20).apply {
            left = TreeNode(15)
            right = TreeNode(7)
        }
    }
    println(maxDepthRecursive(root))
    println(maxDepthBFS(root))
    println(maxDepthDFS(root))
}

/**
 * RECURSIVE: O(N) time, O(H) space
 * Height = 1 + max(height(left), height(right))
 */
fun maxDepthRecursive(root: TreeNode?): Int {
    if (root == null) return 0
    return 1 + maxOf(maxDepthRecursive(root.left), maxDepthRecursive(root.right))
}

/**
 * BFS: O(N) time, O(W) space — count levels
 */
fun maxDepthBFS(root: TreeNode?): Int {
    if (root == null) return 0
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    var depth = 0
    while (queue.isNotEmpty()) {
        depth++
        repeat(queue.size) {
            val node = queue.removeFirst()
            node.left?.let { queue.addLast(it) }
            node.right?.let { queue.addLast(it) }
        }
    }
    return depth
}

/**
 * ITERATIVE DFS: O(N) time, O(H) space — track depth with each node
 */
fun maxDepthDFS(root: TreeNode?): Int {
    if (root == null) return 0
    val stack = ArrayDeque<Pair<TreeNode, Int>>()
    stack.addLast(root to 1)
    var maxDepth = 0
    while (stack.isNotEmpty()) {
        val (node, depth) = stack.removeLast()
        maxDepth = maxOf(maxDepth, depth)
        node.left?.let { stack.addLast(it to depth + 1) }
        node.right?.let { stack.addLast(it to depth + 1) }
    }
    return maxDepth
}
