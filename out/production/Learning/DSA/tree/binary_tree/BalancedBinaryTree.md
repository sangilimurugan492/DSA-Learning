# Balanced Binary Tree — Detailed Explanation

> **LeetCode #110** | [Problem Link](https://leetcode.com/problems/balanced-binary-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐ (Bottom-up DFS pattern, early termination)
> **Topic:** Tree, DFS, Recursion

---

## 📋 Problem Statement

Given a binary tree, determine if it is height-balanced — for every node, the depth of left and right subtrees differ by at most 1.

### Examples

```
Balanced:              Unbalanced:
    3                       1
   / \                     /
  9   20                  2
     /  \                 /
    15    7              3
Output: true            Output: false (left depth=3, right depth=0)
```

---

## 🧩 Method 1: Top-Down (Naive) — O(N²)

### Core Idea

At each node, compute left and right heights. If |left - right| > 1, return false. Recurse on children.

### Key Insight

> This recomputes height at every node — O(N) per node, O(N²) total. Works but inefficient.

### Code

```kotlin
fun isBalancedTopDown(root: TreeNode?): Boolean {
    if (root == null) return true
    val leftH = heightBal(root.left)
    val rightH = heightBal(root.right)
    return abs(leftH - rightH) <= 1 &&
        isBalancedTopDown(root.left) && isBalancedTopDown(root.right)
}

private fun heightBal(node: TreeNode?): Int {
    if (node == null) return 0
    return 1 + maxOf(heightBal(node.left), heightBal(node.right))
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Height computed at each node |
| **Space** | O(H) | Recursion stack |

---

## 🧩 Method 2: Bottom-Up (Optimal) — O(N)

### Core Idea

Return height if balanced, -1 if unbalanced. If any subtree returns -1, propagate -1 immediately (early termination).

### Key Insight

> Combine the height computation and balance check into one pass. Return -1 as a sentinel for "unbalanced" — the caller checks for -1 and short-circuits.

### Dry Run — Unbalanced `[1,2,null,3,null,4]`

```
checkHeight(4) → 1
checkHeight(3) → left=1, right=0, |1-0|≤1 → return 2
checkHeight(2) → left=2, right=0, |2-0|>1 → return -1 ⚠️
checkHeight(1) → left=-1 → return -1 ⚠️ (early termination)

Result: -1 → false ✅
```

### Code

```kotlin
fun isBalancedBottomUp(root: TreeNode?): Boolean {
    return checkHeight(root) != -1
}

private fun checkHeight(node: TreeNode?): Int {
    if (node == null) return 0
    val left = checkHeight(node.left)
    if (left == -1) return -1  // Left unbalanced → propagate
    val right = checkHeight(node.right)
    if (right == -1) return -1  // Right unbalanced → propagate
    if (abs(left - right) > 1) return -1  // Current unbalanced
    return 1 + maxOf(left, right)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each node visited once |
| **Space** | O(H) | Recursion stack |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Top-Down | O(N²) | O(H) | Easy to explain, small trees |
| Bottom-Up | O(N) | O(H) | Always prefer — O(N) with early exit |

> **Interview Tip:** Start with top-down to show understanding, then optimize to bottom-up. The -1 sentinel pattern is a common tree technique — it combines computation with validation in a single pass. This pattern appears in diameter, max path sum, and balanced tree problems.
