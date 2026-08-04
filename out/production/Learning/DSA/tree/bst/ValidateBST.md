# Validate Binary Search Tree — Detailed Explanation

> **LeetCode #98** | [Problem Link](https://leetcode.com/problems/validate-binary-search-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 5 BST problem)
> **Topic:** BST, DFS, Inorder Traversal

---

## 📋 Problem Statement

Given the root of a binary tree, determine if it is a valid BST. A valid BST: left subtree contains only nodes with values < root, right subtree contains only nodes with values > root, and both subtrees are also valid BSTs.

### Examples

```
Valid BST:           Invalid BST:
    5                    5
   / \                  / \
  3   8                1   4
 / \ / \                  / \
1  4 7  9                3   6
Output: true          Output: false (3 < 5 but in right subtree)
```

---

## 🧩 Method 1: Range-Based DFS — O(N)

### Core Idea

Each node must be in a valid range `(min, max)`. Left child: range becomes `(min, node.val)`. Right child: range becomes `(node.val, max)`.

### Key Insight

> Don't just check immediate children — a node deep in the right subtree must still be greater than an ancestor far up. The range approach propagates constraints down the tree.

### Dry Run — Valid BST `[5,3,8,1,4,7,9]`

```
validate(5, min=null, max=null) → 5 in range ✅
  validate(3, min=null, max=5) → 3 < 5 ✅
    validate(1, min=null, max=3) → 1 < 3 ✅
    validate(4, min=3, max=5) → 3 < 4 < 5 ✅
  validate(8, min=5, max=null) → 8 > 5 ✅
    validate(7, min=5, max=8) → 5 < 7 < 8 ✅
    validate(9, min=8, max=null) → 9 > 8 ✅
→ true ✅
```

### Code

```kotlin
fun isValidBSTRange(root: TreeNode?): Boolean {
    return validate(root, null, null)
}

private fun validate(node: TreeNode?, min: Int?, max: Int?): Boolean {
    if (node == null) return true
    if (min != null && node.`val` <= min) return false
    if (max != null && node.`val` >= max) return false
    return validate(node.left, min, node.`val`) && validate(node.right, node.`val`, max)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(H) | Recursion stack |

---

## 🧩 Method 2: Inorder Traversal — O(N)

### Core Idea

Inorder traversal of a BST gives a strictly increasing sequence. Track the previous value and check if current > previous.

### Key Insight

> If inorder traversal is sorted (strictly increasing), the tree is a valid BST. This is a fundamental BST property.

### Code

```kotlin
var prevVal: Int? = null

fun isValidBSTInorder(root: TreeNode?): Boolean {
    prevVal = null
    return inorderCheck(root)
}

private fun inorderCheck(node: TreeNode?): Boolean {
    if (node == null) return true
    if (!inorderCheck(node.left)) return false
    if (prevVal != null && node.`val` <= prevVal!!) return false
    prevVal = node.`val`
    return inorderCheck(node.right)
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
| Range DFS | O(N) | O(H) | No global state | Slightly harder to explain |
| Inorder | O(N) | O(H) | Uses BST property | Global variable |

> **Common Mistake:** Only checking if `left.val < root.val < right.val` is NOT enough. A node in the right subtree might be smaller than an ancestor. Always use range or inorder.

> **Interview Tip:** Start with the range approach — it's the most intuitive. Mention the inorder approach as an alternative that leverages the BST property. Both are O(N) time, O(H) space.
