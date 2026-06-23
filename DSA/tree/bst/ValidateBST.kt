package tree.bst

import tree.TreeNode

/**
 * https://leetcode.com/problems/validate-binary-search-tree/
 * Check if a binary tree is a valid BST (left < root < right for all nodes).
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 5 BST problem, must know inorder + range approach)
 */

fun main() {
    val validBST = TreeNode(5).apply {
        left = TreeNode(3).apply { left = TreeNode(1); right = TreeNode(4) }
        right = TreeNode(8).apply { left = TreeNode(7); right = TreeNode(9) }
    }
    println(isValidBSTRange(validBST))   // true
    println(isValidBSTInorder(validBST)) // true

    val invalidBST = TreeNode(5).apply {
        left = TreeNode(1)
        right = TreeNode(4).apply { left = TreeNode(3); right = TreeNode(6) }
    }
    println(isValidBSTRange(invalidBST))   // false
}

/**
 * APPROACH 1: Range-based DFS — O(N) time, O(H) space
 * Each node must be in range (min, max). Update range as we go down.
 */
fun isValidBSTRange(root: TreeNode?): Boolean {
    return validate(root, null, null)
}

private fun validate(node: TreeNode?, min: Int?, max: Int?): Boolean {
    if (node == null) return true
    if (min != null && node.`val` <= min) return false
    if (max != null && node.`val` >= max) return false
    return validate(node.left, min, node.`val`) && validate(node.right, node.`val`, max)
}

/**
 * APPROACH 2: Inorder traversal — O(N) time, O(H) space
 * Inorder of BST gives sorted sequence. Check if each value > previous.
 */
var prevVal: Int? = null

fun isValidBSTInorder(root: TreeNode?): Boolean {
    prevVal = null
    return inorderCheck(root)
}

private fun inorderCheck(node: TreeNode?): Boolean {
    if (node == null) return true
    if (!inorderCheck(node.left)) return false
    if (prevVal != null && node.`val` <= prevVal!!) return false
    prevVal = node.`val`
    return inorderCheck(node.right)
}
