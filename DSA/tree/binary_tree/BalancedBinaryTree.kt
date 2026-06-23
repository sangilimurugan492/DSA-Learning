package tree.binary_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/balanced-binary-tree/
 * Check if a binary tree is height-balanced (left & right subtree heights differ by at most 1).
 * FAANG Importance: ⭐⭐⭐⭐ (Bottom-up DFS pattern, early termination)
 */

fun main() {
    val balanced = TreeNode(3).apply {
        left = TreeNode(9)
        right = TreeNode(20).apply {
            left = TreeNode(15)
            right = TreeNode(7)
        }
    }
    println(isBalancedTopDown(balanced))

    val unbalanced = TreeNode(1).apply {
        left = TreeNode(2).apply {
            left = TreeNode(3).apply {
                left = TreeNode(4)
                right = TreeNode(4)
            }
            right = TreeNode(3)
        }
        right = TreeNode(2)
    }
    println(isBalancedTopDown(unbalanced))
}

/**
 * TOP-DOWN: O(N²) time — check height at every node
 */
fun isBalancedTopDown(root: TreeNode?): Boolean {
    if (root == null) return true
    val leftH = heightBal(root.left)
    val rightH = heightBal(root.right)
    return kotlin.math.abs(leftH - rightH) <= 1 &&
            isBalancedTopDown(root.left) && isBalancedTopDown(root.right)
}

private fun heightBal(node: TreeNode?): Int {
    if (node == null) return 0
    return 1 + maxOf(heightBal(node.left), heightBal(node.right))
}

/**
 * BOTTOM-UP: O(N) time — return -1 if unbalanced (early termination)
 * If any subtree is unbalanced, propagate -1 up immediately.
 */
fun isBalancedBottomUp(root: TreeNode?): Boolean {
    return checkHeight(root) != -1
}

private fun checkHeight(node: TreeNode?): Int {
    if (node == null) return 0
    val left = checkHeight(node.left)
    if (left == -1) return -1
    val right = checkHeight(node.right)
    if (right == -1) return -1
    if (kotlin.math.abs(left - right) > 1) return -1
    return 1 + maxOf(left, right)
}
