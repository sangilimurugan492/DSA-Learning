package tree.binary_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/diameter-of-binary-tree/
 * Find the length of the longest path between any two nodes (may or may not pass through root).
 * Example: [1,2,3,4,5] → diameter = 3 (path 4→2→1→3 or 5→2→1→3)
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic tree DFS, global variable pattern)
 */

fun main() {
    val root = TreeNode(1).apply {
        left = TreeNode(2).apply {
            left = TreeNode(4)
            right = TreeNode(5)
        }
        right = TreeNode(3)
    }
    println(diameterOfBinaryTree(root))
}

/**
 * DFS: O(N) time, O(H) space
 * For each node, the longest path through it = leftHeight + rightHeight.
 * Track the global maximum while computing heights.
 */
var maxDiameter = 0

fun diameterOfBinaryTree(root: TreeNode?): Int {
    maxDiameter = 0
    height(root)
    return maxDiameter
}

fun height(node: TreeNode?): Int {
    if (node == null) return 0
    val left = height(node.left)
    val right = height(node.right)
    maxDiameter = maxOf(maxDiameter, left + right)
    return 1 + maxOf(left, right)
}
