package tree.binary_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/subtree-of-another-tree/
 * Check if subRoot is a subtree of root.
 * FAANG Importance: ⭐⭐⭐⭐ (Combines SameTree + tree traversal)
 */

fun main() {
    val root = TreeNode(3).apply {
        left = TreeNode(4).apply {
            left = TreeNode(1)
            right = TreeNode(2)
        }
        right = TreeNode(5)
    }
    val subRoot = TreeNode(4).apply {
        left = TreeNode(1)
        right = TreeNode(2)
    }
    println(isSubtree(root, subRoot))  // true
}

/**
 * O(M*N) time worst case — for each node in root, check if subtree matches
 * Uses isSameTree as helper
 */
fun isSubtree(root: TreeNode?, subRoot: TreeNode?): Boolean {
    if (subRoot == null) return true
    if (root == null) return false
    if (isSameTreeSub(root, subRoot)) return true
    return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot)
}

private fun isSameTreeSub(p: TreeNode?, q: TreeNode?): Boolean {
    if (p == null && q == null) return true
    if (p == null || q == null) return false
    if (p.`val` != q.`val`) return false
    return isSameTreeSub(p.left, q.left) && isSameTreeSub(p.right, q.right)
}
