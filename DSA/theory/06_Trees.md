# Data Structure Theory: Trees

> **In-depth theory, diagrams, and implementation details for understanding trees at a fundamental level.**

---

## 1. What is a Tree?

A tree is a **hierarchical (non-linear)** data structure consisting of **nodes** connected by **edges**. It has a single **root** node, and every node (except root) has exactly **one parent**. Trees represent hierarchical relationships.

```
Binary Tree Example:

            Root
             ↓
           ┌─────┐
           │  1  │          ← Root node (no parent)
           └──┬──┘
          ┌───┴───┐
          ↓       ↓
       ┌─────┐ ┌─────┐
       │  2  │ │  3  │       ← Children of root (level 1)
       └──┬──┘ └─────┘
       ┌──┴──┐
       ↓     ↓
    ┌─────┐ ┌─────┐
    │  4  │ │  5  │           ← Children of node 2 (level 2)
    └─────┘ └─────┘

Terminology:
- Node 1: Root (no parent)
- Node 2, 3: Children of 1; Siblings of each other
- Node 4, 5: Children of 2; Leaf nodes (no children)
- Node 1: Parent of 2 and 3
- Node 2: Parent of 4 and 5; Child of 1
- Depth of node 4: 2 (edges from root)
- Height of tree: 2 (max depth)
```

### Key Terminology:

| Term | Definition |
|------|-----------|
| **Root** | Topmost node (no parent) |
| **Parent** | Node with children |
| **Child** | Node connected below a parent |
| **Leaf** | Node with no children |
| **Sibling** | Nodes with same parent |
| **Depth** | Distance from root (root depth = 0) |
| **Height** | Longest path from node to a leaf |
| **Subtree** | Tree formed by a node and its descendants |
| **Level** | All nodes at same depth |
| **Edge** | Connection between two nodes |
| **Degree** | Number of children a node has |

### Key Properties:
- **N nodes** → **N-1 edges** (always)
- **No cycles**: Tree is a special case of a graph with no cycles
- **Single path**: Exactly one path between any two nodes
- **Hierarchical**: Parent-child relationship

---

## 2. Types of Trees

### 2.1 Binary Tree

Each node has **at most 2 children** (left and right).

```
        1
       / \
      2   3
     / \
    4   5
```

### 2.2 Full Binary Tree (Strict)

Every node has **0 or 2 children** (no node has only 1 child).

```
        1
       / \
      2   3
     / \
    4   5
```

### 2.3 Complete Binary Tree

All levels are **fully filled** except possibly the last level, which is filled **left to right**.

```
        1              1
       / \            / \
      2   3          2   3
     / \ /          / \
    4  5 6         4   5

Both complete: last level filled left-to-right, no gaps before last node
```

### 2.4 Perfect Binary Tree

All **internal nodes** have 2 children, all **leaves** are at the **same level**.

```
        1
       / \
      2   3
     / \ / \
    4  5 6  7

Levels = 3, Nodes = 2³-1 = 7
```

### 2.5 Balanced Binary Tree

Height difference between left and right subtrees is at most 1 for every node.

```
Balanced (AVL):              Unbalanced:
        4                          1
       / \                          \
      2   6                          2
     / \ / \                           \
    1  3 5  7                           3
                                         \
                                          4
```

### 2.6 Binary Search Tree (BST)

Left subtree values **< node < right subtree** values. Enables O(log N) search.

```
BST Property: Left < Node < Right

        8
       / \
      3   10
     / \    \
    1   6    14
       / \   /
      4   7 13

In-order traversal: 1, 3, 4, 6, 7, 8, 10, 13, 14 (SORTED!)
```

---

## 3. Tree Traversals

### 3.1 Depth-First Search (DFS) Traversals

```
Tree:       1
          /   \
         2     3
        / \   / \
       4   5 6   7
```

#### Pre-order (Root → Left → Right):

```
Visit order: 1, 2, 4, 5, 3, 6, 7

         ①
        / \
       ②   ⑥
      / \  / \
     ③  ⑤⑦  (last)

Use: Copy tree, prefix expression, serialize tree
```

```kotlin
fun preorder(node: TreeNode?) {
    if (node == null) return
    visit(node)           // Root
    preorder(node.left)   // Left
    preorder(node.right)  // Right
}
```

#### In-order (Left → Root → Right):

```
Visit order: 4, 2, 5, 1, 6, 3, 7

         ④
        / \
       ②   ⑥
      / \  / \
     ①  ③⑤  ⑦

Use: BST → sorted output, infix expression
```

```kotlin
fun inorder(node: TreeNode?) {
    if (node == null) return
    inorder(node.left)    // Left
    visit(node)           // Root
    inorder(node.right)   // Right
}
```

#### Post-order (Left → Right → Root):

```
Visit order: 4, 5, 2, 6, 7, 3, 1

         ⑦ (last)
        / \
       ③   ⑥
      / \  / \
     ①  ②④  ⑤

Use: Delete tree, postfix expression, calculate directory size
```

```kotlin
fun postorder(node: TreeNode?) {
    if (node == null) return
    postorder(node.left)   // Left
    postorder(node.right)  // Right
    visit(node)           // Root
}
```

### 3.2 Breadth-First Search (BFS) / Level-Order

```
Visit order: 1, 2, 3, 4, 5, 6, 7

         ①
        / \
       ②   ③
      / \  / \
     ④  ⑤⑥  ⑦

Level by level, left to right. Uses QUEUE.
```

```kotlin
fun levelOrder(root: TreeNode?) {
    if (root == null) return
    val queue = ArrayDeque<TreeNode>()
    queue.add(root)
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        visit(node)
        node.left?.let { queue.add(it) }
        node.right?.let { queue.add(it) }
    }
}
```

---

## 4. Binary Search Tree (BST) Operations

### Search: O(log N) average, O(N) worst

```
Search for 6 in BST:

        8
       / \
      3   10        6 < 8 → go LEFT
     / \    \
    1   6    14      6 > 3 → go RIGHT
       / \   /
      4   7 13        FOUND! Return node 6

At each step, eliminate half the tree.
```

### Insert: O(log N) average

```
Insert 5 into BST:

        8                      8
       / \                    / \
      3   10                 3   10
     / \    \      →        / \    \
    1   6    14            1   6    14
       / \   /                 / \   /
      4   7 13                4   7 13
                                 \
                                  5

5 > 3 → right, 5 < 6 → left, 5 > 4 → right (empty) → insert here
```

### Delete: O(log N) average

```
3 cases for deletion:

Case 1: Leaf node (no children) → just remove
        8                 8
       / \               / \
      3   10   →        3   10    (deleted 14, leaf)
          / \               / 
        13  14            13

Case 2: One child → replace with child
        8                 8
       / \               / \
      3   10   →        3   13    (deleted 10, had one child 13)
          / \               / 
        13  14            14

Case 3: Two children → find in-order successor (or predecessor), 
        replace value, delete successor
        8                 8
       / \               / \
      3   10   →        3   13    (deleted 10, replaced with
         / \               / \     successor = 13)
       13  14            14
```

---

## 5. Self-Balancing Trees

### 5.1 AVL Tree

Every node maintains a **balance factor** = height(left) - height(right) ∈ {-1, 0, 1}. Rotations rebalance after insert/delete.

```
Unbalanced → Rotate:

  Left-Left case (right rotate):
       3                     2
      /                     / \
     2          →          1   3
    /
   1

  Left-Right case (left rotate, then right rotate):
     3                     3                   2
    /                     /                   / \
   1          →          2         →        1   3
    \                   /
     2                 1

  Balance factor = height(left) - height(right)
  Allowed: -1, 0, +1
  If |BF| > 1 → rotate to rebalance
```

### 5.2 Red-Black Tree

Each node is colored **red or black**. Balanced via color flips and rotations. Used in Java's TreeMap, C++'s std::map.

```
Rules:
1. Root is BLACK
2. Every RED node has BLACK children (no two consecutive reds)
3. Every path from root to leaf has the SAME number of black nodes
4. New nodes are RED

These rules guarantee height ≤ 2·log(N+1)

Example:
        8B
       /    \
      4R     12R
     / \     / \
    2B 6B  10B 14B
```

### AVL vs Red-Black Tree:

| Aspect | AVL | Red-Black |
|--------|-----|-----------|
| Balance | Stricter (BF ≤ 1) | Looser |
| Lookup | **Faster** (shorter tree) | Slightly slower |
| Insert/Delete | Slower (more rotations) | **Faster** (fewer rotations) |
| Use case | Read-heavy | Write-heavy |

---

## 6. Operations and Time Complexity

| Operation | BST (avg) | BST (worst) | Balanced BST | Notes |
|-----------|-----------|-------------|-------------|-------|
| **Search** | O(log N) | O(N) | **O(log N)** | Worst: skewed tree |
| **Insert** | O(log N) | O(N) | **O(log N)** | Worst: skewed tree |
| **Delete** | O(log N) | O(N) | **O(log N)** | Worst: skewed tree |
| **Traversal** | O(N) | O(N) | O(N) | Visit all nodes |
| **Min/Max** | O(log N) | O(N) | **O(log N)** | Leftmost / rightmost |
| **Successor** | O(log N) | O(N) | **O(log N)** | Next larger value |
| **Height** | O(N) | O(N) | **O(1)** | Stored as metadata |

> **BST worst case O(N)** happens when the tree becomes a linked list (insert sorted data). Self-balancing trees (AVL, Red-Black) guarantee O(log N) always.

---

## 7. Implementation (Kotlin)

### Binary Tree Node:

```kotlin
class TreeNode<T>(val data: T) {
    var left: TreeNode<T>? = null
    var right: TreeNode<T>? = null
}
```

### Binary Search Tree:

```kotlin
class BST<T : Comparable<T>> {
    private var root: TreeNode<T>? = null

    fun insert(value: T) {
        root = insertRec(root, value)
    }

    private fun insertRec(node: TreeNode<T>?, value: T): TreeNode<T> {
        if (node == null) return TreeNode(value)
        when {
            value < node.data -> node.left = insertRec(node.left, value)
            value > node.data -> node.right = insertRec(node.right, value)
            // value == node.data → duplicate, do nothing or handle
        }
        return node
    }

    fun search(value: T): Boolean {
        var current = root
        while (current != null) {
            when {
                value == current.data -> return true
                value < current.data -> current = current.left
                else -> current = current.right
            }
        }
        return false
    }

    fun delete(value: T) {
        root = deleteRec(root, value)
    }

    private fun deleteRec(node: TreeNode<T>?, value: T): TreeNode<T>? {
        if (node == null) return null
        when {
            value < node.data -> node.left = deleteRec(node.left, value)
            value > node.data -> node.right = deleteRec(node.right, value)
            else -> {
                // Found node to delete
                if (node.left == null) return node.right
                if (node.right == null) return node.left
                // Two children: get in-order successor
                node.data = minValue(node.right!!)
                node.right = deleteRec(node.right, node.data)
            }
        }
        return node
    }

    private fun minValue(node: TreeNode<T>): T {
        var current = node
        while (current.left != null) current = current.left!!
        return current.data
    }

    fun inorder(): List<T> {
        val result = mutableListOf<T>()
        fun dfs(node: TreeNode<T>?) {
            if (node == null) return
            dfs(node.left)
            result.add(node.data)
            dfs(node.right)
        }
        dfs(root)
        return result  // Sorted order!
    }
}
```

---

## 8. Common Tree Interview Patterns

| Pattern | Description | Example Problem |
|---------|-------------|-----------------|
| **DFS Traversal** | Pre/in/post-order recursion | Validate BST, Max Depth |
| **BFS Level-Order** | Queue-based, level by level | Level order traversal, Right side view |
| **Recursive divide** | Process left + right, combine | Diameter, Balanced tree |
| **BST search** | Eliminate half at each step | Search in BST, kth smallest |
| **Path tracking** | Track path from root | Path sum, all root-to-leaf paths |
| **LCA (Lowest Common Ancestor)** | Find common ancestor | LCA of two nodes |
| **Serialization** | Tree → string → tree | Serialize/Deserialize BST |

---

## 9. Advantages and Disadvantages

### Advantages:
- **Hierarchical**: Natural representation of parent-child relationships
- **Ordered** (BST): In-order traversal gives sorted output
- **O(log N) operations** (balanced BST): Fast search, insert, delete
- **Range queries**: Easy in BST (find all values in [a, b])
- **Flexible**: Many variants (BST, AVL, Red-Black, Trie, B-Tree)

### Disadvantages:
- **O(N) worst case** (unbalanced BST): Degrades to linked list
- **Not cache-friendly**: Nodes scattered in memory (unlike arrays)
- **Pointer overhead**: Each node stores child pointers
- **Complex balancing**: Self-balancing trees have complex insert/delete

---

## 10. Real-World Applications

| Application | How Trees Are Used |
|-------------|------------------|
| **File system** | Directory structure (folders and files) |
| **DOM (HTML)** | Document Object Model is a tree |
| **Database indexing** | B-Tree / B+ Tree for range queries |
| **Compiler** | AST (Abstract Syntax Tree) for parsing |
| **Decision making** | Decision trees in ML |
| **Auto-complete** | Trie (prefix tree) for suggestions |
| **Routing** | Trie for IP routing tables |
| **Priority scheduling** | Heap (complete binary tree) |
| **XML/JSON parsing** | Parse trees |
| ** Huffman coding** | Optimal prefix code tree for compression |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Hash Tables →](./05_Hash_Tables.md)
- [Next: Graphs →](./07_Graphs.md)
