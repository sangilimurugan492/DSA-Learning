# Diameter of Binary Tree — Detailed Explanation

> **LeetCode #543** | [Problem Link](https://leetcode.com/problems/diameter-of-binary-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic tree DFS, global variable pattern)
> **Topic:** Tree, DFS, Recursion

---

## 📋 Problem Statement

Given the root of a binary tree, return the diameter — the length of the longest path between any two nodes. The path may or may not pass through the root. The length is measured by the number of edges.

### Examples

```
        1
       / \
      2   3
     / \
    4   5

Input: root = [1,2,3,4,5]
Output: 3  (path: 4→2→1→3 or 5→2→1→3, 3 edges)
```

---

## 🧩 Method 1: DFS with Global Max — O(N)

### Core Idea

For each node, the longest path **through** that node = `leftHeight + rightHeight` (in edges). Track the global maximum while computing heights.

### Key Insight

> The diameter doesn't have to pass through the root. At each node, compute the path through it (left depth + right depth). The answer is the maximum over all nodes.

### Dry Run — `[1,2,3,4,5]`

```
height(4) → 1,  maxDiameter = max(0, 0+0) = 0
height(5) → 1,  maxDiameter = max(0, 0+0) = 0
height(2) → 1 + max(1,1) = 2,  maxDiameter = max(0, 1+1) = 2
height(3) → 1,  maxDiameter = max(2, 0+0) = 2
height(1) → 1 + max(2,1) = 3,  maxDiameter = max(2, 2+1) = 3 ✅
```

### Code

```kotlin
var maxDiameter = 0

fun diameterOfBinaryTree(root: TreeNode?): Int {
    maxDiameter = 0
    height(root)
    return maxDiameter
}

fun height(node: TreeNode?): Int {
    if (node == null) return 0
    val left = height(node.left)
    val right = height(node.right)
    maxDiameter = maxOf(maxDiameter, left + right)  // Path through this node
    return 1 + maxOf(left, right)  // Height for parent
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(H) | Recursion stack = tree height |

---

## 🧩 Method 2: DFS Returning Pair — O(N)

### Core Idea

Instead of a global variable, return a `Pair<height, diameter>` from each call. More functional, avoids mutable state.

### Code

```kotlin
fun diameterOfBinaryTreePair(root: TreeNode?): Int {
    return dfs(root).second
}

private fun dfs(node: TreeNode?): Pair<Int, Int> {
    // Pair(height, diameter)
    if (node == null) return 0 to 0
    val (leftHeight, leftDiam) = dfs(node.left)
    val (rightHeight, rightDiam) = dfs(node.right)
    val height = 1 + maxOf(leftHeight, rightHeight)
    val diameter = maxOf(leftHeight + rightHeight, leftDiam, rightDiam)
    return height to diameter
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(H) | Recursion stack |

---

## 📊 Method Comparison

| Method | Time | Space | Pros | Cons |
|--------|------|-------|------|------|
| Global max | O(N) | O(H) | Simple | Mutable state |
| Pair return | O(N) | O(H) | Pure function | Slightly more complex |

> **Interview Tip:** The global variable pattern is common in tree problems (diameter, max path sum, longest univalue path). Explain that the diameter at each node = left height + right height, and we track the max across all nodes. Reset the global variable before each call to avoid stale state.
