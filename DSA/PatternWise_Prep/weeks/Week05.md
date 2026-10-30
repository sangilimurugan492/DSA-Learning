# 📅 Week 5 — Tree + Heap + Trie

> **Dates:** Nov 16 (Mon) – Nov 22 (Sun)
> **Pattern Focus:** Binary Tree (DFS/BFS), BST, Heap/Priority Queue, Trie
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/tree/`, `DSA/heap/`, `DSA/trie/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week05_Practice.md](../practice/Week05_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Nov 16 | #1–#8 (Easy binary tree DFS) | 2 hrs |
| Tue | Nov 17 | #9–#16 (Medium tree + BST) | 2 hrs |
| Wed | Nov 18 | #17–#19 (Heap) | 2 hrs |
| Thu | Nov 19 | #19–#22 (Hard heap + trie) | 2 hrs |
| Sat | Nov 21 | Revision: weakest 4 + Weeks 1–4 mixed | 2 hrs |
| Sun | Nov 22 | 🧪 Mock Test 5 + Review | 3 hrs |

---

## 🟢 MONDAY, Nov 16 — Easy Binary Tree (DFS)

### 1. Maximum Depth of Binary Tree — 🟢
**LeetCode #104** | Solution: `DSA/tree/binary_tree/MaximumDepthOfBinaryTree.kt`

**Problem:** Find the maximum depth of the tree.

<details><summary>💡 Hint 1</summary>
DFS (recursive) — depth = 1 + max(left, right).
</details>

<details><summary>💡 Hint 2</summary>
Base case: null → 0. Else 1 + max(depth(left), depth(right)).
</details>

<details><summary>💡 Hint 3</summary>
fun maxDepth(root: TreeNode?): Int = if (root == null) 0 else 1 + max(maxDepth(root.left), maxDepth(root.right))
</details>

```kotlin
fun maxDepth(root: TreeNode?): Int {
    // TODO: 1 + max of children depths
    return 0
}
```

---

### 2. Same Tree — 🟢
**LeetCode #100** | Solution: `DSA/tree/binary_tree/SameTree.kt`

**Problem:** Are two trees structurally identical with the same values?

<details><summary>💡 Hint 1</summary>
DFS — compare nodes pairwise.
</details>

<details><summary>💡 Hint 2</summary>
Both null → true. One null → false. Values differ → false. Else recurse left && right.
</details>

<details><summary>💡 Hint 3</summary>
if (p == null && q == null) true; if (p == null || q == null) false; p.val == q.val && same(left) && same(right).
</details>

```kotlin
fun isSameTree(p: TreeNode?, q: TreeNode?): Boolean {
    // TODO: Null checks, value check, recurse
    return false
}
```

---

### 3. Balanced Binary Tree — 🟢
**LeetCode #110** | Solution: `DSA/tree/binary_tree/BalancedBinaryTree.kt`

**Problem:** Is the tree height-balanced (|leftH - rightH| <= 1 at every node)?

<details><summary>💡 Hint 1</summary>
DFS height helper that returns -1 to signal "unbalanced".
</details>

<details><summary>💡 Hint 2</summary>
height(node): if either child height is -1 or |l - r| > 1 → return -1. Else 1 + max(l, r).
</details>

<details><summary>💡 Hint 3</summary>
1. height(n): n == null → 0
2. l = height(n.left), r = height(n.right)
3. If l == -1 || r == -1 || abs(l - r) > 1 → -1 else 1 + max(l, r)
4. Return height(root) != -1
</details>

```kotlin
fun isBalanced(root: TreeNode?): Boolean {
    // TODO: Height helper with -1 sentinel
    return false
}
```

---

### 4. Subtree of Another Tree — 🟢
**LeetCode #572** | Solution: `DSA/tree/binary_tree/SubtreeOfAnotherTree.kt`

**Problem:** Is subRoot a subtree of root?

<details><summary>💡 Hint 1</summary>
DFS — at each node of root, check isSameTree with subRoot.
</details>

<details><summary>💡 Hint 2</summary>
isSubtree(root, sub): root == null → false; isSameTree(root, sub) || isSubtree(left) || isSubtree(right).
</details>

<details><summary>💡 Hint 3</summary>
Reuses the Same Tree logic as a helper at every node.
</details>

```kotlin
fun isSubtree(root: TreeNode?, subRoot: TreeNode?): Boolean {
    // TODO: SameTree check at every node
    return false
}
```

---

### 5. Invert Binary Tree — 🟢
**LeetCode #226** | Solution: `DSA/tree/binary_tree/InvertBinaryTree.kt`

**Problem:** Mirror the tree (swap every left/right child).

<details><summary>💡 Hint 1</summary>
DFS — swap children, recurse.
</details>

<details><summary>💡 Hint 2</summary>
null → null. Swap left/right. Recurse both. Return root.
</details>

<details><summary>💡 Hint 3</summary>
root?.let { it.left = invert(it.right).also { it.right = invert(it.left) } }; return root — or the straightforward 4-line version.
</details>

```kotlin
fun invertTree(root: TreeNode?): TreeNode? {
    // TODO: Swap children, recurse
    return null
}
```

---

### 6. Symmetric Tree — 🟢
**LeetCode #101** | Solution: `DSA/tree/binary_tree/SymmetricTree.kt`

**Problem:** Is the tree a mirror of itself?

<details><summary>💡 Hint 1</summary>
DFS with TWO nodes — compare (a.left, b.right) and (a.right, b.left).
</details>

<details><summary>💡 Hint 2</summary>
mirror(a, b): both null → true; one null or values differ → false; mirror(a.left, b.right) && mirror(a.right, b.left).
</details>

<details><summary>💡 Hint 3</summary>
Call mirror(root, root).
</details>

```kotlin
fun isSymmetric(root: TreeNode?): Boolean {
    // TODO: Two-node mirror check
    return false
}
```

---

### 7. Search in BST — 🟢
**LeetCode #700** | Solution: `DSA/tree/bst/SearchInBST.kt`

**Problem:** Find the node with value `val` in a BST.

<details><summary>💡 Hint 1</summary>
BST search — compare and go left/right.
</details>

<details><summary>💡 Hint 2</summary>
While node != null: val == node.val → found; val < node.val → left; else right.
</details>

<details><summary>💡 Hint 3</summary>
Iterative loop comparing val with node.val.
</details>

```kotlin
fun searchBST(root: TreeNode?, `val`: Int): TreeNode? {
    // TODO: Compare, go left or right
    return null
}
```

---

### 8. Tree Traversals (In/Pre/Post) — 🟢
**LeetCode #144/94/145** | Solution: `DSA/tree/traversal/TreeTraversals.kt`

**Problem:** Implement pre-order, in-order, and post-order traversals.

<details><summary>💡 Hint 1</summary>
Position of the node visit determines the order.
</details>

<details><summary>💡 Hint 2</summary>
Pre: node → left → right. In: left → node → right. Post: left → right → node.
</details>

<details><summary>💡 Hint 3</summary>
Recursive helper adding to a list at the right position.
</details>

```kotlin
fun inorderTraversal(root: TreeNode?): List<Int> {
    // TODO: Left → Node → Right
    return listOf()
}
```

---

## 🟡 TUESDAY, Nov 17 — Medium Tree + BST

### 9. Diameter of Binary Tree — 🟡
**LeetCode #543** | Solution: `DSA/tree/binary_tree/DiameterOfBinaryTree.kt`

**Problem:** Longest path (edges) between any two nodes.

<details><summary>💡 Hint 1</summary>
DFS height — track max(leftH + rightH) during the computation.
</details>

<details><summary>💡 Hint 2</summary>
Path through a node = leftHeight + rightHeight. Global max. Return height (1 + max) to parent.
</details>

<details><summary>💡 Hint 3</summary>
1. var diameter = 0
2. height(n): n == null → 0; l = height(left); r = height(right)
3. diameter = max(diameter, l + r); return 1 + max(l, r)
</details>

```kotlin
fun diameterOfBinaryTree(root: TreeNode?): Int {
    // TODO: Track max l + r during height DFS
    return 0
}
```

---

### 10. Lowest Common Ancestor — 🟡
**LeetCode #236** | Solution: `DSA/tree/binary_tree/LowestCommonAncestor.kt`

**Problem:** Find LCA of two nodes in a binary tree.

<details><summary>💡 Hint 1</summary>
DFS — return the node if it's p or q; bubble up non-null results.
</details>

<details><summary>💡 Hint 2</summary>
If both left and right return non-null → current node is the LCA. If only one side → pass it up.
</details>

<details><summary>💡 Hint 3</summary>
1. root == null || root == p || root == q → return root
2. left = LCA(root.left), right = LCA(root.right)
3. left != null && right != null → root; else left ?: right
</details>

```kotlin
fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
    // TODO: Bubble up, both sides non-null = LCA
    return null
}
```

---

### 11. Insert into BST — 🟡
**LeetCode #701** | Solution: `DSA/tree/bst/InsertIntoBST.kt`

**Problem:** Insert a value into a BST (return the root).

<details><summary>💡 Hint 1</summary>
DFS — traverse to the null position, create the node there.
</details>

<details><summary>💡 Hint 2</summary>
root == null → new TreeNode(val). val < root.val → root.left = insert(left) else root.right = insert(right).
</details>

<details><summary>💡 Hint 3</summary>
Recursive assignment to left or right child.
</details>

```kotlin
fun insertIntoBST(root: TreeNode?, `val`: Int): TreeNode? {
    // TODO: Traverse to null, insert
    return null
}
```

---

### 12. Delete Node in BST — 🟡
**LeetCode #450** | Solution: `DSA/tree/bst/DeleteNodeInBST.kt`

**Problem:** Delete a node with the given key from a BST.

<details><summary>💡 Hint 1</summary>
Three cases: leaf, one child, two children.
</details>

<details><summary>💡 Hint 2</summary>
Two children → replace value with in-order successor (min of right subtree), then delete the successor from the right subtree.
</details>

<details><summary>💡 Hint 3</summary>
1. key < val → recurse left; key > val → recurse right
2. Found: leaf → null; one child → that child
3. Two children: minNode = min(right); root.val = minNode.val; root.right = delete(right, minNode.val)
</details>

```kotlin
fun deleteNode(root: TreeNode?, key: Int): TreeNode? {
    // TODO: 3 delete cases
    return null
}
```

---

### 13. Kth Smallest Element in BST — 🟡
**LeetCode #230** | Solution: `DSA/tree/bst/KthSmallestElementInBST.kt`

**Problem:** Find the kth smallest value in a BST.

<details><summary>💡 Hint 1</summary>
In-order traversal gives sorted order — stop at the kth.
</details>

<details><summary>💡 Hint 2</summary>
Counter decrements at each visit; when it hits 0 → record the answer.
</details>

<details><summary>💡 Hint 3</summary>
1. count = k, result = 0
2. In-order: recurse left; if (--count == 0) result = node.val; recurse right
</details>

```kotlin
fun kthSmallest(root: TreeNode?, k: Int): Int {
    // TODO: In-order with counter
    return 0
}
```

---

### 14. Validate BST — 🟡
**LeetCode #98** | Solution: `DSA/tree/bst/ValidateBST.kt`

**Problem:** Is the tree a valid BST (all left < node < right, transitively)?

<details><summary>💡 Hint 1</summary>
Pass down (min, max) bounds.
</details>

<details><summary>💡 Hint 2</summary>
Each node must be strictly within its allowed range. Left child gets (min, node.val), right gets (node.val, max).
</details>

<details><summary>💡 Hint 3</summary>
1. valid(n, min, max): n == null → true
2. n.val <= min || n.val >= max → false
3. valid(left, min, n.val) && valid(right, n.val, max)
4. Start with (Long.MIN_VALUE, Long.MAX_VALUE)
</details>

```kotlin
fun isValidBST(root: TreeNode?): Boolean {
    // TODO: Min/max bounds recursion
    return false
}
```

---

### 15. Level Order Traversal — 🟡
**LeetCode #102** | Solution: `DSA/tree/traversal/BinaryTreeLevelOrderTraversal.kt`

**Problem:** Return level-by-level values (BFS).

<details><summary>💡 Hint 1</summary>
Queue-based BFS, process level by level.
</details>

<details><summary>💡 Hint 2</summary>
Capture levelSize = queue.size at the start of each level; process exactly that many nodes.
</details>

<details><summary>💡 Hint 3</summary>
1. queue = ArrayDeque; offer root
2. While queue not empty: levelSize; repeat levelSize times: poll, add to level list, offer children
3. Add level list to result
</details>

```kotlin
fun levelOrder(root: TreeNode?): List<List<Int>> {
    // TODO: BFS with level size
    return listOf()
}
```

---

### 16. Right Side View — 🟡
**LeetCode #199** | Solution: `DSA/tree/traversal/BinaryTreeRightSideView.kt`

**Problem:** Values visible from the right side (rightmost node of each level).

<details><summary>💡 Hint 1</summary>
Level-order traversal; take the LAST node of each level.
</details>

<details><summary>💡 Hint 2</summary>
BFS: when processing the last node of a level (i == levelSize - 1) → add to result.
</details>

<details><summary>💡 Hint 3</summary>
Same BFS skeleton as level order; record only the final node per level.
</details>

```kotlin
fun rightSideView(root: TreeNode?): List<Int> {
    // TODO: BFS, last node per level
    return listOf()
}
```

---

## 🟡 WEDNESDAY, Nov 18 — Heap

### 17. Kth Largest Element — 🟡
**LeetCode #215** | Solution: `DSA/heap/kth_largest_element/KthLargestElement.kt`

**Problem:** Find the kth largest element.

<details><summary>💡 Hint 1</summary>
Min-heap of size k.
</details>

<details><summary>💡 Hint 2</summary>
Push all; when size > k → poll (smallest evicted). Heap top = kth largest.
</details>

<details><summary>💡 Hint 3</summary>
1. heap = PriorityQueue&lt;Int&gt;()
2. For num: offer(num); if (heap.size > k) poll()
3. Return heap.peek()
</details>

```kotlin
fun findKthLargest(nums: IntArray, k: Int): Int {
    // TODO: Min-heap capped at k
    return 0
}
```

---

### 18. Top K Frequent Elements — 🟡
**LeetCode #347** | Solution: `DSA/heap/top_k_frequent_elements/TopKFrequentElements.kt`

**Problem:** (Re-solve from Week 3 — heap approach.)

<details><summary>💡 Hint 1</summary>
Frequency map + min-heap of size k by frequency.
</details>

<details><summary>💡 Hint 2</summary>
Push (freq, num); evict smallest frequency when over k.
</details>

<details><summary>💡 Hint 3</summary>
Same skeleton as Kth Largest, but the heap compares by frequency.
</details>

```kotlin
fun topKFrequent(nums: IntArray, k: Int): IntArray {
    // TODO: Re-solve from scratch
    return intArrayOf()
}
```

---

### 19. Merge K Sorted Lists (Heap) — 🔴
**LeetCode #23** | Solution: `DSA/heap/merge_k_sorted_lists/MergeKSortedLists.kt`

**Problem:** (Re-solve from Week 4 — this time focus on the heap approach.)

<details><summary>💡 Hint 1</summary>
Min-heap of list heads, compare by node value.
</details>

<details><summary>💡 Hint 2</summary>
Pop smallest → append; push its next node if non-null.
</details>

<details><summary>💡 Hint 3</summary>
PriorityQueue&lt;ListNode&gt;(compareBy { it.`val` }); dummy tail; loop pop/append/push.
</details>

```kotlin
fun mergeKLists(lists: Array<ListNode?>): ListNode? {
    // TODO: Re-solve with heap
    return null
}
```

---

## 🟡 THURSDAY, Nov 19 — Hard Heap + Trie

### 20. Find Median from Data Stream — 🔴
**LeetCode #295** | Solution: `DSA/heap/find_median_from_data_stream/FindMedianFromDataStream.kt`

**Problem:** Design a class: addNum(int) and findMedian() — both efficient.

<details><summary>💡 Hint 1</summary>
Two heaps: max-heap (lower half), min-heap (upper half).
</details>

<details><summary>💡 Hint 2</summary>
Keep sizes balanced (differ by at most 1). Median = top of larger heap, or average of both tops.
</details>

<details><summary>💡 Hint 3</summary>
1. addNum: push to max-heap; move max-heap top to min-heap; if min-heap.size > max-heap.size → move min-heap top back
2. findMedian: sizes equal → avg of tops; else max-heap top
</details>

```kotlin
class MedianFinder() {
    // TODO: Two heaps, balanced
    fun addNum(num: Int) {}
    fun findMedian(): Double = 0.0
}
```

---

### 21. Implement Trie (Prefix Tree) — 🟡
**LeetCode #208** | Solution: `DSA/trie/implement_trie/ImplementTrie.kt`

**Problem:** Implement insert, search, startsWith for a trie.

<details><summary>💡 Hint 1</summary>
Node = HashMap&lt;Char, Node&gt; children + isEnd flag.
</details>

<details><summary>💡 Hint 2</summary>
insert: walk/create nodes, mark isEnd. search: walk, check isEnd. startsWith: walk, path existence is enough.
</details>

<details><summary>💡 Hint 3</summary>
1. class Node { val children = HashMap&lt;Char, Node&gt;(); var isEnd = false }
2. Walk char by char from root for each operation
</details>

```kotlin
class Trie() {
    // TODO: Node with children map + isEnd
    fun insert(word: String) {}
    fun search(word: String): Boolean = false
    fun startsWith(prefix: String): Boolean = false
}
```

---

### 22. Word Search II — 🔴
**LeetCode #212** | Solution: `DSA/trie/word_search_ii/WordSearchII.kt`

**Problem:** Find all words (from a list) in a character grid (words formed by adjacent cells, no reuse).

<details><summary>💡 Hint 1</summary>
Build a Trie from the words; DFS from every cell with a trie node pointer.
</details>

<details><summary>💡 Hint 2</summary>
Mark visited cells (e.g., '#'), recurse 4 directions, restore. When a node isEnd → add word to results.
</details>

<details><summary>💡 Hint 3</summary>
1. Build trie. For each cell: dfs(r, c, rootNode)
2. dfs: char not in node.children → return. node = children[char]; if isEnd → add word
3. Mark board[r][c] = '#'; recurse 4 dirs; restore
</details>

```kotlin
fun findWords(board: Array<CharArray>, words: Array<String>): List<String> {
    // TODO: Trie + DFS from each cell
    return listOf()
}
```

---

## 🟢 SATURDAY, Nov 21 — Revision + Mixed Practice

1. Re-solve the 3–4 hardest problems this week (from scratch, no hints)
2. Mixed practice from Weeks 1–4:
   - Trapping Rain Water (Week 1)
   - Minimum Window Substring (Week 2)
   - Largest Rectangle in Histogram (Week 3)
   - LRU Cache (Week 4)
3. Update [Progress_Tracker.md](../Progress_Tracker.md)

---

## 🧪 SUNDAY, Nov 22 — Mock Test 5 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Pattern | Difficulty | Time Limit |
|---|---------|---------|-----------|-----------|
| 1 | Invert Binary Tree | Tree DFS | 🟢 | 15 min |
| 2 | Validate BST | BST Bounds | 🟡 | 25 min |
| 3 | Level Order Traversal | Tree BFS | 🟡 | 20 min |
| 4 | Kth Largest Element | Heap | 🟡 | 20 min |
| 5 | Word Search II | Trie + DFS | 🔴 | 40 min |

**Part 2 — Review (1 hr):** Score, log in [Mock_Test_Log.md](../Mock_Test_Log.md), re-solve failures, update tracker.

---

## ✅ Week 5 Completion Checklist

- [ ] Mon: 8 easy tree problems
- [ ] Tue: 8 medium tree/BST problems
- [ ] Wed: 3 heap problems
- [ ] Thu: 3 hard heap/trie problems
- [ ] Sat: Revision + Weeks 1–4 mixed practice
- [ ] Sun: Mock Test 5 completed & logged
- [ ] Progress Tracker updated
