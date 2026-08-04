# Maximum Depth of Binary Tree — Detailed Explanation

> **LeetCode #104** | [Problem Link](https://leetcode.com/problems/maximum-depth-of-binary-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Most basic tree recursion pattern)
> **Topic:** Tree, DFS, BFS, Recursion

---

## 📋 Problem Statement

Given the root of a binary tree, return its maximum depth. The maximum depth is the number of nodes along the longest path from the root to the farthest leaf node.

### Examples

```
    3
   / \
  9   20
     /  \
    15    7

Input: root = [3,9,20,null,null,15,7]
Output: 3
```

---

## 🧩 Method 1: Recursive DFS — O(N)

### Core Idea

The height of a tree = 1 + max(height(left), height(right)). Recurse down to null (base case returns 0).

### Key Insight

> Think bottom-up: the depth of a leaf is 1, null is 0. Each node asks "what's the max depth of my children?" and adds 1 for itself.

### Dry Run — `[3,9,20,null,null,15,7]`

```
maxDepth(3)
  ├─ maxDepth(9) → 1 + max(0, 0) = 1
  └─ maxDepth(20)
       ├─ maxDepth(15) → 1
       └─ maxDepth(7)  → 1
       → 1 + max(1, 1) = 2
  → 1 + max(1, 2) = 3 ✅
```

### Code

```kotlin
fun maxDepthRecursive(root: TreeNode?): Int {
    if (root == null) return 0
    return 1 + maxOf(maxDepthRecursive(root.left), maxDepthRecursive(root.right))
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(H) | Recursion stack = tree height |

---

## 🧩 Method 2: BFS (Level Order) — O(N)

### Core Idea

Count the number of levels. Each BFS iteration = one level.

### Code

```kotlin
fun maxDepthBFS(root: TreeNode?): Int {
    if (root == null) return 0
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    var depth = 0
    while (queue.isNotEmpty()) {
        depth++
        repeat(queue.size) {
            val node = queue.removeFirst()
            node.left?.let { queue.addLast(it) }
            node.right?.let { queue.addLast(it) }
        }
    }
    return depth
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(W) | Max queue width = max level width |

---

## 🧩 Method 3: Iterative DFS — O(N)

### Core Idea

Use a stack with (node, depth) pairs. Track the maximum depth seen.

### Code

```kotlin
fun maxDepthDFS(root: TreeNode?): Int {
    if (root == null) return 0
    val stack = ArrayDeque<Pair<TreeNode, Int>>()
    stack.addLast(root to 1)
    var maxDepth = 0
    while (stack.isNotEmpty()) {
        val (node, depth) = stack.removeLast()
        maxDepth = maxOf(maxDepth, depth)
        node.left?.let { stack.addLast(it to depth + 1) }
        node.right?.let { stack.addLast(it to depth + 1) }
    }
    return maxDepth
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(H) | Stack depth = tree height |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Recursive DFS | O(N) | O(H) | Default — cleanest code |
| BFS | O(N) | O(W) | When level info needed |
| Iterative DFS | O(N) | O(H) | Avoid stack overflow |

> **Interview Tip:** Start with recursive DFS. If asked for iterative, use BFS (level counting is intuitive). Mention that recursion depth = tree height — for skewed trees, this can cause stack overflow.
