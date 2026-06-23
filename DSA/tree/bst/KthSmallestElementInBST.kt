package tree.bst

import tree.TreeNode

/**
 * https://leetcode.com/problems/kth-smallest-element-in-a-bst/
 * Find the kth smallest element in a BST.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Inorder traversal on BST gives sorted order)
 */

fun main() {
    val root = TreeNode(5).apply {
        left = TreeNode(3).apply { left = TreeNode(2); right = TreeNode(4) }
        right = TreeNode(6).apply { right = TreeNode(7) }
    }
    println(kthSmallestRecursive(root, 3))  // 4
    println(kthSmallestIterative(root, 3))  // 4
}

/**
 * RECURSIVE: O(H+K) time, O(H) space
 * Inorder traversal, count nodes, return when count == k.
 */
var kthCount = 0
var kthResult = 0

fun kthSmallestRecursive(root: TreeNode?, k: Int): Int {
    kthCount = 0
    kthResult = 0
    inorderKth(root, k)
    return kthResult
}

private fun inorderKth(node: TreeNode?, k: Int) {
    if (node == null) return
    inorderKth(node.left, k)
    kthCount++
    if (kthCount == k) { kthResult = node.`val`; return }
    inorderKth(node.right, k)
}

/**
 * ITERATIVE: O(H+K) time, O(H) space
 * Inorder iterative, stop when we've popped k nodes.
 */
fun kthSmallestIterative(root: TreeNode?, k: Int): Int {
    val stack = ArrayDeque<TreeNode>()
    var curr = root
    var count = 0

    while (curr != null || stack.isNotEmpty()) {
        while (curr != null) {
            stack.addLast(curr)
            curr = curr.left
        }
        curr = stack.removeLast()
        count++
        if (count == k) return curr.`val`
        curr = curr.right
    }
    return -1
}
