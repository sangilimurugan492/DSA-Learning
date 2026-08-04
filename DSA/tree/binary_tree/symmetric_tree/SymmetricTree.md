# Symmetric Tree — Detailed Explanation

> **LeetCode #101** | [Problem Link](https://leetcode.com/problems/symmetric-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic tree comparison + mirror pattern)
> **Topic:** Tree, DFS, BFS, Recursion

---

## 📋 Problem Statement

Given the root of a binary tree, check whether it is a mirror of itself (symmetric around its center).

### Examples

```
Symmetric:              Not Symmetric:
    1                       1
   / \                     / \
  2   2                   2   2
 / \ / \                   \   \
3  4 4  3                   3    3
Output: true              Output: false
```

---

## 🧩 Method 1: Recursive DFS — O(N)

### Core Idea

Two trees are mirrors if:
1. Both roots null → true
2. One null → false
3. Values match AND `left.left` mirrors `right.right` (outer) AND `left.right` mirrors `right.left` (inner)

### Key Insight

> Symmetry means the left subtree is a **mirror** of the right subtree. Mirror = outer children match (left.left ↔ right.right) and inner children match (left.right ↔ right.left).

### Dry Run

```
    1
   / \
  2   2
 / \ / \
3  4 4  3

isMirror(2, 2):
  values match (2==2)
  isMirror(3, 3) → 3==3, both null children → true ✅
  isMirror(4, 4) → 4==4, both null children → true ✅
  → true ✅
```

### Code

```kotlin
fun isSymmetric(root: TreeNode?): Boolean {
    if (root == null) return true
    return isMirror(root.left, root.right)
}

private fun isMirror(left: TreeNode?, right: TreeNode?): Boolean {
    if (left == null && right == null) return true
    if (left == null || right == null) return false
    return left.`val` == right.`val` &&
        isMirror(left.left, right.right) &&   // Outer
        isMirror(left.right, right.left)      // Inner
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

Use a queue with pairs. Enqueue outer and inner pairs. Compare each pair.

### Code

```kotlin
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
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(N) | Queue (worst case) |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Recursive DFS | O(N) | O(H) | Default — cleanest |
| Iterative BFS | O(N) | O(N) | Avoid stack overflow |

> **Interview Tip:** This is the same base case pattern as Same Tree (both null → true, one null → false, values differ → false). The difference: instead of comparing `left↔left` and `right↔right`, we compare `left.left↔right.right` and `left.right↔right.left` (mirror). Master this pattern — it applies to all tree comparison problems.
