package tree.binary_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/same-tree/
 * Check if two binary trees are identical (same structure and values).
 * FAANG Importance: ⭐⭐⭐⭐ (Basic tree comparison pattern)
 */

fun main() {
    val p = TreeNode(1).apply { left = TreeNode(2); right = TreeNode(3) }
    val q = TreeNode(1).apply { left = TreeNode(2); right = TreeNode(3) }
    println(isSameTree(p, q))  // true

    val r = TreeNode(1).apply { left = TreeNode(2) }
    val s = TreeNode(1).apply { right = TreeNode(2) }
    println(isSameTree(r, s))  // false
}

/**
 * RECURSIVE: O(N) time, O(H) space
 * Both null → true. One null → false. Values differ → false.
 * Recurse on left and right subtrees.
 */
fun isSameTree(p: TreeNode?, q: TreeNode?): Boolean {
    if (p == null && q == null) return true
    if (p == null || q == null) return false
    if (p.`val` != q.`val`) return false
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right)
}
