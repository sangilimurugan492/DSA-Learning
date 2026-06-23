package tree.bst

import tree.TreeNode

/**
 * https://leetcode.com/problems/insert-into-a-binary-search-tree/
 * Insert a value into BST and return the root.
 * FAANG Importance: ⭐⭐⭐⭐ (BST modification pattern)
 */

fun main() {
    val root = TreeNode(4).apply {
        left = TreeNode(2).apply { left = TreeNode(1); right = TreeNode(3) }
        right = TreeNode(7)
    }
    val result = insertIntoBST(root, 5)
    println(inorderInsert(result))
}

private fun inorderInsert(node: TreeNode?): List<Int> {
    if (node == null) return emptyList()
    return inorderInsert(node.left) + node.`val` + inorderInsert(node.right)
}

/**
 * RECURSIVE: O(H) time, O(H) space
 * Go left if val < root, go right if val > root. Insert at null position.
 */
fun insertIntoBST(root: TreeNode?, `val`: Int): TreeNode? {
    if (root == null) return TreeNode(`val`)
    if (`val` < root.`val`) root.left = insertIntoBST(root.left, `val`)
    else root.right = insertIntoBST(root.right, `val`)
    return root
}

/**
 * ITERATIVE: O(H) time, O(1) space
 */
fun insertIntoBSTIterative(root: TreeNode?, `val`: Int): TreeNode? {
    val newNode = TreeNode(`val`)
    if (root == null) return newNode

    var curr = root
    while (true) {
        if (`val` < curr!!.`val`) {
            if (curr.left == null) { curr.left = newNode; break }
            curr = curr.left
        } else {
            if (curr.right == null) { curr.right = newNode; break }
            curr = curr.right
        }
    }
    return root
}
