# Invert Binary Tree — Detailed Explanation

> **LeetCode #226** | [Problem Link](https://leetcode.com/problems/invert-binary-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic tree recursion — famous Google interview question!)
> **Topic:** Tree, DFS, BFS, Recursion

---

## 📋 Problem Statement

Given the root of a binary tree, invert it — swap the left and right children for every node.

### Example

```
Input:           Output:
    4               4
   / \             / \
  2   7    →     7   2
 / \ / \         / \ / \
1  3 6  9       9  6 3  1
```

---

## 🧩 Method 1: Recursive DFS — O(N)

### Core Idea

For each node: swap its left and right children, then recursively invert both subtrees.

### Key Insight

> The base case is `null → return null`. For non-null nodes, swap first, then recurse. The order doesn't matter — swap before or after recursion both work.

### Dry Run

```
invertTree(4)
  swap: left=7, right=2
  invertTree(7)
    swap: left=9, right=6
    invertTree(9) → leaf, return
    invertTree(6) → leaf, return
  invertTree(2)
    swap: left=3, right=1
    invertTree(3) → leaf, return
    invertTree(1) → leaf, return
  return 4 (inverted) ✅
```

### Code

```kotlin
fun invertTree(root: TreeNode?): TreeNode? {
    if (root == null) return null
    val temp = root.left
    root.left = root.right
    root.right = temp
    invertTree(root.left)
    invertTree(root.right)
    return root
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(H) | Recursion stack |

---

## 🧩 Method 2: Iterative BFS — O(N)

### Core Idea

Use a queue. For each node, swap its children and add them to the queue.

### Code

```kotlin
fun invertTreeBFS(root: TreeNode?): TreeNode? {
    if (root == null) return null
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        val temp = node.left
        node.left = node.right
        node.right = temp
        node.left?.let { queue.addLast(it) }
        node.right?.let { queue.addLast(it) }
    }
    return root
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(W) | Queue width |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Recursive DFS | O(N) | O(H) | Default — cleanest |
| Iterative BFS | O(N) | O(W) | Avoid stack overflow |

> **Fun Fact:** This problem went viral when Max Howell (creator of Homebrew) tweeted that Google asked him to invert a binary tree on a whiteboard and he didn't get the job. It's now the most famous tree problem in tech.

> **Interview Tip:** The recursive solution is 5 lines. Write it from memory. If asked for iterative, use BFS with a queue. This problem tests basic tree recursion — if you can do this, you can do any tree swap/mirror problem.
