# Lowest Common Ancestor of a Binary Tree — Detailed Explanation

> **LeetCode #236** | [Problem Link](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 10 tree problem, DFS pattern)
> **Topic:** Tree, DFS, Recursion

---

## 📋 Problem Statement

Given a binary tree, find the lowest common ancestor (LCA) of two nodes `p` and `q`. The LCA is the lowest node that has both `p` and `q` as descendants (a node can be a descendant of itself).

### Examples

```
        3
       / \
      5   1
     / \ / \
    6  2 0  8
      / \
     7   4

LCA(5, 1) = 3  (root is ancestor of both)
LCA(5, 4) = 5  (5 is ancestor of 4 and itself)
LCA(7, 4) = 2  (2 is lowest common ancestor)
```

---

## 🧩 Method 1: Recursive DFS — O(N)

### Core Idea

1. If current node is null → return null
2. If current node is `p` or `q` → return current node
3. Recurse on left and right subtrees
4. If both return non-null → current node is LCA
5. If only one returns non-null → propagate it up

### Key Insight

> The LCA is the node where `p` and `q` "split" — one is in the left subtree, the other in the right. If both are on the same side, the LCA is deeper. If the current node IS one of p/q, it could be the LCA (since a node is its own ancestor).

### Dry Run — LCA(5, 1) in tree above

```
LCA(3, p=5, q=1)
  left = LCA(5, ...) → 5 == p → return 5
  right = LCA(1, ...) → 1 == q → return 1
  left != null && right != null → return 3 ✅ (LCA found!)
```

### Dry Run — LCA(7, 4)

```
LCA(3, p=7, q=4)
  left = LCA(5, ...)
    left = LCA(6, ...) → null
    right = LCA(2, ...)
      left = LCA(7, ...) → 7 == p → return 7
      right = LCA(4, ...) → 4 == q → return 4
      left != null && right != null → return 2
    left=null, right=2 → return 2
  right = LCA(1, ...) → null (neither 7 nor 4 here)
  left=2, right=null → return 2 ✅
```

### Code

```kotlin
fun lowestCommonAncestor(root: TreeNode?, p: TreeNode, q: TreeNode): TreeNode? {
    if (root == null || root.`val` == p.`val` || root.`val` == q.`val`) return root

    val left = lowestCommonAncestor(root.left, p, q)
    val right = lowestCommonAncestor(root.right, p, q)

    if (left != null && right != null) return root  // Found LCA
    return left ?: right  // Propagate non-null result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(H) | Recursion stack |

---

## 🧩 Method 2: Parent Pointer (Iterative) — O(N)

### Core Idea

1. DFS to build a parent map for all nodes
2. Trace ancestors of `p` into a set
3. Trace ancestors of `q` — first one in the set is the LCA

### Code

```kotlin
fun lowestCommonAncestorIterative(
    root: TreeNode?, p: TreeNode, q: TreeNode
): TreeNode? {
    val parent = mutableMapOf<Int, TreeNode>()
    val stack = ArrayDeque<TreeNode>()
    stack.addLast(root!!)
    parent[root!!.`val`] = root!!

    // Build parent map until both p and q found
    while (!parent.containsKey(p.`val`) || !parent.containsKey(q.`val`)) {
        val node = stack.removeLast()
        node.left?.let {
            parent[it.`val`] = node
            stack.addLast(it)
        }
        node.right?.let {
            parent[it.`val`] = node
            stack.addLast(it)
        }
    }

    // Collect ancestors of p
    val ancestors = mutableSetOf<Int>()
    var curr: TreeNode = p
    while (curr.`val` != parent[curr.`val`]!!.`val`) {
        ancestors.add(curr.`val`)
        curr = parent[curr.`val`]!!
    }
    ancestors.add(root!!.`val`)

    // Find first ancestor of q that's in p's ancestors
    curr = q
    while (curr.`val` !in ancestors) {
        curr = parent[curr.`val`]!!
    }
    return curr
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Visit each node once |
| **Space** | O(N) | Parent map + ancestor set |

---

## 📊 Method Comparison

| Method | Time | Space | Pros | Cons |
|--------|------|-------|------|------|
| Recursive DFS | O(N) | O(H) | Elegant, minimal code | Recursion depth |
| Parent pointer | O(N) | O(N) | No recursion | More code, more space |

> **Interview Tip:** The recursive DFS is the go-to solution. Explain the 4 cases clearly: (1) null → null, (2) found p or q → return it, (3) both sides non-null → LCA found, (4) one side non-null → propagate. For BST (sorted), there's an O(H) solution using BST property — go left if both < root, go right if both > root, else root is LCA.
