package tree.traversal

import tree.TreeNode

/**
 * Tree Traversals — Foundation of all tree problems.
 * Inorder: Left → Root → Right (gives sorted order for BST)
 * Preorder: Root → Left → Right (used for serialization, copying)
 * Postorder: Left → Right → Root (used for deletion, bottom-up calculations)
 * Level Order: BFS level by level (used for width-based problems)
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Must-know for any tree problem)
 */

fun main() {
    /**
     *       1
     *      / \
     *     2   3
     *    / \
     *   4   5
     */
    val root = TreeNode(1).apply {
        left = TreeNode(2).apply {
            left = TreeNode(4)
            right = TreeNode(5)
        }
        right = TreeNode(3)
    }

    println("Inorder (iterative): ${inorderIterative(root)}")
    println("Inorder (recursive): ${inorderRecursive(root)}")
    println("Preorder (iterative): ${preorderIterative(root)}")
    println("Preorder (recursive): ${preorderRecursive(root)}")
    println("Postorder (iterative): ${postorderIterative(root)}")
    println("Postorder (recursive): ${postorderRecursive(root)}")
    println("Level order: ${levelOrder(root)}")
}

// ==================== INORDER ====================

fun inorderRecursive(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    fun dfs(node: TreeNode?) {
        if (node == null) return
        dfs(node.left)
        result.add(node.`val`)
        dfs(node.right)
    }
    dfs(root)
    return result
}

fun inorderIterative(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    val stack = ArrayDeque<TreeNode>()
    var curr = root
    while (curr != null || stack.isNotEmpty()) {
        while (curr != null) {
            stack.addLast(curr)
            curr = curr.left
        }
        curr = stack.removeLast()
        result.add(curr!!.`val`)
        curr = curr.right
    }
    return result
}

// ==================== PREORDER ====================

fun preorderRecursive(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    fun dfs(node: TreeNode?) {
        if (node == null) return
        result.add(node.`val`)
        dfs(node.left)
        dfs(node.right)
    }
    dfs(root)
    return result
}

fun preorderIterative(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    if (root == null) return result
    val stack = ArrayDeque<TreeNode>()
    stack.addLast(root)
    while (stack.isNotEmpty()) {
        val node = stack.removeLast()
        result.add(node.`val`)
        node.right?.let { stack.addLast(it) }
        node.left?.let { stack.addLast(it) }
    }
    return result
}

// ==================== POSTORDER ====================

fun postorderRecursive(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    fun dfs(node: TreeNode?) {
        if (node == null) return
        dfs(node.left)
        dfs(node.right)
        result.add(node.`val`)
    }
    dfs(root)
    return result
}

fun postorderIterative(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    if (root == null) return result
    val stack = ArrayDeque<TreeNode>()
    stack.addLast(root)
    while (stack.isNotEmpty()) {
        val node = stack.removeLast()
        result.add(node.`val`)
        node.left?.let { stack.addLast(it) }
        node.right?.let { stack.addLast(it) }
    }
    return result.reversed()
}

// ==================== LEVEL ORDER (BFS) ====================

fun levelOrder(root: TreeNode?): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    if (root == null) return result
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    while (queue.isNotEmpty()) {
        val levelSize = queue.size
        val level = mutableListOf<Int>()
        repeat(levelSize) {
            val node = queue.removeFirst()
            level.add(node.`val`)
            node.left?.let { queue.addLast(it) }
            node.right?.let { queue.addLast(it) }
        }
        result.add(level)
    }
    return result
}
