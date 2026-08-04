package patterns.tree_dfs.symmetric_tree

import tree.TreeNode

/**
 * https://leetcode.com/problems/symmetric-tree/
 * Check if a binary tree is a mirror of itself (symmetric around center).
 * Example:
 *     1
 *    / \
 *   2   2     → true (mirror)
 *  / \ / \
 * 3  4 4  3
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic tree comparison + mirror pattern)
 */

fun main() {
    val symmetric = TreeNode(1).apply {
        left = TreeNode(2).apply { left = TreeNode(3); right = TreeNode(4) }
        right = TreeNode(2).apply { left = TreeNode(4); right = TreeNode(3) }
    }
    println(isSymmetric(symmetric))  // true

    val asymmetric = TreeNode(1).apply {
        left = TreeNode(2).apply { right = TreeNode(3) }
        right = TreeNode(2).apply { right = TreeNode(3) }
    }
    println(isSymmetric(asymmetric))  // false
}

/**
 * Recursive: O(N) time, O(H) space
 * Two trees are mirrors if:
 * - Both roots are null → true
 * - One null → false
 * - Values match AND left.left mirrors right.right AND left.right mirrors right.left
 */
fun isSymmetric(root: TreeNode?): Boolean {
    if (root == null) return true
    return isMirror(root.left, root.right)
}

private fun isMirror(left: TreeNode?, right: TreeNode?): Boolean {
    if (left == null && right == null) return true
    if (left == null || right == null) return false
    return left.`val` == right.`val` &&
        isMirror(left.left, right.right) &&
        isMirror(left.right, right.left)
}

/**
 * Iterative BFS: O(N) time, O(N) space
 * Use a queue with pairs — compare outer and inner pairs.
 */
fun isSymmetricIterative(root: TreeNode?): Boolean {
    if (root == null) return true
    val queue = ArrayDeque<Pair<TreeNode?, TreeNode?>>()
    queue.addLast(root.left to root.right)

    while (queue.isNotEmpty()) {
        val (left, right) = queue.removeFirst()
        if (left == null && right == null) continue
        if (left == null || right == null) return false
        if (left.`val` != right.`val`) return false

        queue.addLast(left.left to right.right)   // Outer pair
        queue.addLast(left.right to right.left)    // Inner pair
    }
    return true
}
