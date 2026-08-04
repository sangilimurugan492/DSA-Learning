# Same Tree — Detailed Explanation

> **LeetCode #100** | [Problem Link](https://leetcode.com/problems/same-tree/)
> **FAANG Importance:** ⭐⭐⭐⭐ (Basic tree comparison pattern)
> **Topic:** Tree, DFS, Recursion

---

## 📋 Problem Statement

Given the roots of two binary trees `p` and `q`, check if they are the same tree — identical in structure and node values.

### Examples

```
Tree p:     1         Tree q:     1
           / \                  / \
          2   3                2   3
Output: true

Tree p:     1         Tree q:     1
           /                      \
          2                        2
Output: false (different structure)
```

---

## 🧩 Method 1: Recursive DFS — O(N)

### Core Idea

Two trees are the same if:
1. Both roots are null → true
2. One is null, other isn't → false
3. Values match AND left subtrees match AND right subtrees match

### Key Insight

> Check the current nodes first (base cases), then recurse on children. The AND of left and right subtree comparisons gives the answer.

### Dry Run

```
p: [1,2,3]  q: [1,2,3]

isSameTree(1, 1)
  ├─ values match (1 == 1)
  ├─ isSameTree(2, 2) → true (both leaf, values match)
  └─ isSameTree(3, 3) → true
  → true && true = true ✅
```

### Code

```kotlin
fun isSameTree(p: TreeNode?, q: TreeNode?): Boolean {
    if (p == null && q == null) return true
    if (p == null || q == null) return false
    if (p.`val` != q.`val`) return false
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(min(N, M)) | Visit nodes until mismatch |
| **Space** | O(min(H1, H2)) | Recursion stack |

---

## 🧩 Method 2: BFS (Iterative) — O(N)

### Core Idea

Use two queues. Dequeue pairs and compare. Both null → skip. One null → false. Values differ → false.

### Code

```kotlin
fun isSameTreeBFS(p: TreeNode?, q: TreeNode?): Boolean {
    val queue = ArrayDeque<Pair<TreeNode?, TreeNode?>>()
    queue.addLast(p to q)
    while (queue.isNotEmpty()) {
        val (n1, n2) = queue.removeFirst()
        if (n1 == null && n2 == null) continue
        if (n1 == null || n2 == null) return false
        if (n1.`val` != n2.`val`) return false
        queue.addLast(n1.left to n2.left)
        queue.addLast(n1.right to n2.right)
    }
    return true
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(min(N, M)) | Visit nodes until mismatch |
| **Space** | O(W) | Queue width |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Recursive DFS | O(N) | O(H) | Default — cleanest |
| BFS | O(N) | O(W) | Avoid recursion |

> **Interview Tip:** This is the foundation for subtree checking, mirror tree, and symmetric tree problems. Master the base case pattern: both null → true, one null → false, values differ → false.
