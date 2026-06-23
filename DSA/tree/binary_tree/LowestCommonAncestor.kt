package tree.binary_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/
 * Find the lowest common ancestor (LCA) of two nodes in a binary tree.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 tree problem, DFS pattern)
 */

fun main() {
    val root = TreeNode(3).apply {
        left = TreeNode(5).apply {
            left = TreeNode(6)
            right = TreeNode(2).apply {
                left = TreeNode(7)
                right = TreeNode(4)
            }
        }
        right = TreeNode(1).apply {
            left = TreeNode(0)
            right = TreeNode(8)
        }
    }
    val p = root.left!!   // node 5
    val q = root.right!!  // node 1
    println(lowestCommonAncestor(root, p, q)?.`val`)  // 3

    val p2 = root.left!!.right!!.left!!   // node 7
    val q2 = root.left!!.right!!.right!!   // node 4
    println(lowestCommonAncestor(root, p2, q2)?.`val`)  // 2
}

/**
 * DFS: O(N) time, O(H) space
 * If current node is p or q → return it.
 * Recurse left and right. If both return non-null → current is LCA.
 * If only one side returns non-null → propagate it up.
 */
fun lowestCommonAncestor(root: TreeNode?, p: TreeNode, q: TreeNode): TreeNode? {
    if (root == null || root.`val` == p.`val` || root.`val` == q.`val`) return root

    val left = lowestCommonAncestor(root.left, p, q)
    val right = lowestCommonAncestor(root.right, p, q)

    if (left != null && right != null) return root  // Found LCA
    return left ?: right
}
