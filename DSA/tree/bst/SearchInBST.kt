package tree.bst

import tree.TreeNode

/**
 * https://leetcode.com/problems/search-in-a-binary-search-tree/
 * Search for a value in BST. Return the subtree rooted at that node.
 * FAANG Importance: ⭐⭐⭐ (Basic BST operation)
 */

fun main() {
    val root = TreeNode(4).apply {
        left = TreeNode(2).apply { left = TreeNode(1); right = TreeNode(3) }
        right = TreeNode(7)
    }
    println(searchBST(root, 2)?.`val`)  // 2
    println(searchBST(root, 5)?.`val`)  // null
}

/**
 * RECURSIVE: O(H) time, O(H) space
 */
fun searchBST(root: TreeNode?, `val`: Int): TreeNode? {
    if (root == null || root.`val` == `val`) return root
    return if (`val` < root.`val`) searchBST(root.left, `val`) else searchBST(root.right, `val`)
}

/**
 * ITERATIVE: O(H) time, O(1) space
 */
fun searchBSTIterative(root: TreeNode?, `val`: Int): TreeNode? {
    var curr = root
    while (curr != null && curr.`val` != `val`) {
        curr = if (`val` < curr.`val`) curr.left else curr.right
    }
    return curr
}
