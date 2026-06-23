package tree.bst

import tree.TreeNode

/**
 * https://leetcode.com/problems/delete-node-in-a-bst/
 * Delete a node with given key in BST. Return the root.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hardest BST operation, 3 cases to handle)
 */

fun main() {
    val root = TreeNode(5).apply {
        left = TreeNode(3).apply { left = TreeNode(2); right = TreeNode(4) }
        right = TreeNode(6).apply { right = TreeNode(7) }
    }
    val result = deleteNode(root, 3)
    println(inorderDelete(result))
}

private fun inorderDelete(node: TreeNode?): List<Int> {
    if (node == null) return emptyList()
    return inorderDelete(node.left) + node.`val` + inorderDelete(node.right)
}

/**
 * O(H) time, O(H) space
 * 3 Cases:
 * 1. Node is leaf → just remove it
 * 2. Node has one child → replace with child
 * 3. Node has two children → replace with inorder successor (min of right subtree), then delete successor
 */
fun deleteNode(root: TreeNode?, key: Int): TreeNode? {
    if (root == null) return null

    when {
        key < root.`val` -> root.left = deleteNode(root.left, key)
        key > root.`val` -> root.right = deleteNode(root.right, key)
        else -> {
            // Found the node to delete
            // Case 1 & 2: No left child or no right child
            if (root.left == null) return root.right
            if (root.right == null) return root.left

            // Case 3: Two children — find inorder successor (min in right subtree)
            val successor = findMin(root.right!!)
            root.`val` = successor.`val`
            root.right = deleteNode(root.right, successor.`val`)
        }
    }
    return root
}

/** Find the minimum value node in a BST (leftmost node) */
private fun findMin(node: TreeNode): TreeNode {
    var curr = node
    while (curr.left != null) curr = curr.left!!
    return curr
}
