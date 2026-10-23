# 🧪 Week 05 Practice — Tree + Heap + Trie (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 5 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).
> **Helper:** Use `toTree(vararg values)` (level-order with nulls) from [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Maximum Depth of Binary Tree — LC 104
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,9,20,null,null,15,7] | 3 | Classic |
| 2 | [1] | 1 | Single |
| 3 | [] | 0 | Empty |

### 2. Same Tree — LC 100
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] vs [1,2,3] | true | Identical |
| 2 | [1,2] vs [1,null,2] | false | Structure differs |
| 3 | [] vs [] | true | Both empty |

### 3. Balanced Binary Tree — LC 110
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,9,20,null,null,15,7] | true | Balanced |
| 2 | [1,2,2,3,3,null,null,4,4] | false | Unbalanced |
| 3 | [] | true | Empty |

### 4. Subtree of Another Tree — LC 572
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | root=[3,4,5,1,2], subRoot=[4,1,2] | true | Classic |
| 2 | root=[3,4,5,1,2,null,null,null,null,0], subRoot=[4,1,2] | false | Extra node |
| 3 | root=[1], subRoot=[1] | true | Same single |

### 5. Invert Binary Tree — LC 226
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [4,2,7,1,3,6,9] | [4,7,2,9,6,3,1] | Classic |
| 2 | [2,1,3] | [2,3,1] | Three nodes |
| 3 | [] | [] | Empty |

### 6. Symmetric Tree — LC 101
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,2,3,4,4,3] | true | Symmetric |
| 2 | [1,2,2,null,3,null,3] | false | Asymmetric |
| 3 | [1] | true | Single |

### 7. Search in BST — LC 700
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [4,2,7,1,3], val=2 | subtree [2,1,3] | Found |
| 2 | [4,2,7,1,3], val=5 | null | Not found |
| 3 | [], val=1 | null | Empty |

### 8. Tree Traversals — LC 144/94/145
| # | Input | Expected (in/pre/post) | Tests |
|---|-------|----------|-------|
| 1 | [1,null,2,3] | in=[1,3,2], pre=[1,2,3], post=[3,2,1] | Classic |
| 2 | [] | all [] | Empty |
| 3 | [1] | all [1] | Single |

### 9. Diameter of Binary Tree — LC 543
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5] | 3 | Classic |
| 2 | [1,2] | 1 | Two nodes |
| 3 | [1] | 0 | Single |

### 10. Lowest Common Ancestor — LC 236
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,5,1,6,2,0,8,null,null,7,4], p=5, q=1 | 3 | Classic |
| 2 | same tree, p=5, q=4 | 5 | Ancestor case |
| 3 | [1,2], p=1, q=2 | 1 | Root is LCA |

### 11. Insert into BST — LC 701
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [4,2,7,1,3], insert 5 | [4,2,7,1,3,5] | Right subtree |
| 2 | [], insert 1 | [1] | Empty |
| 3 | [2,1], insert 3 | [2,1,3] | New right child |

### 12. Delete Node in BST — LC 450
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [5,3,6,2,4,null,7], key=3 | [5,4,6,2,null,null,7] (or valid equivalent) | Two children |
| 2 | [5,3,6,2,4,null,7], key=0 | unchanged | Not found |
| 3 | [0], key=0 | [] | Delete root |

### 13. Kth Smallest Element in BST — LC 230
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [5,3,6,2,4,null,null,1], k=3 | 3 | Classic |
| 2 | [5,3,6,2,4,null,null,1], k=1 | 1 | Smallest |
| 3 | [1], k=1 | 1 | Single |

### 14. Validate BST — LC 98
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,1,3] | true | Valid |
| 2 | [5,1,4,null,null,3,6] | false | Right child smaller |
| 3 | [2,2,2] | false | Duplicate values |

### 15. Level Order Traversal — LC 102
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,9,20,null,null,15,7] | [[3],[9,20],[15,7]] | Classic |
| 2 | [1] | [[1]] | Single |
| 3 | [] | [] | Empty |

### 16. Right Side View — LC 199
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,null,5,null,4] | [1,3,4] | Classic |
| 2 | [1,null,3] | [1,3] | Right chain |
| 3 | [] | [] | Empty |

### 17. Kth Largest Element — LC 215
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,2,1,5,6,4], k=2 | 5 | Classic |
| 2 | [3,2,3,1,2,4,5,5,6], k=4 | 4 | Duplicates |
| 3 | [1], k=1 | 1 | Single |

### 18. Top K Frequent Elements — LC 347
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,1,1,2,2,3], k=2 | [1,2] | Classic |
| 2 | [1], k=1 | [1] | Single |
| 3 | [3,0,1,0], k=1 | [0] | Freq winner |

### 19. Merge K Sorted Lists (Heap) — LC 23
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,4,5],[1,3,4],[2,6]] | [1,1,2,3,4,4,5,6] | Classic |
| 2 | [] | [] | Empty |
| 3 | [[], [1]] | [1] | Some empty |

### 20. Find Median from Data Stream — LC 295
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | addNum(1), addNum(2), findMedian | 1.5 | Even |
| 2 | addNum(3), findMedian | 2.0 | Odd |
| 3 | addNum(5) after [1,2,3], findMedian | 2.5 | Rebalance |

### 21. Implement Trie — LC 208
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | insert("apple"), search("apple") | true | Exact |
| 2 | search("app") | false | Prefix ≠ word |
| 3 | startsWith("app") | true | Prefix |

### 22. Word Search II — LC 212
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | board=[["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]], words=["oath","pea","eat","rain"] | ["eat","oath"] | Classic |
| 2 | board=[["a","b"],["c","d"]], words=["abcb"] | [] | No reuse |
| 3 | board=[["a"]], words=["a"] | ["a"] | Single |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Max Depth | Degenerate tree (linked list) of 100,000 nodes | **100,000** | < 200 ms | Recursion → StackOverflow |
| 2 | Validate BST | Degenerate tree of 100,000 nodes: values 100,000..1 (decreasing) | true | < 200 ms | StackOverflow / missing bounds |
| 3 | Validate BST | Tree with values near Int.MAX_VALUE: root=Int.MAX_VALUE, left=Int.MIN_VALUE | false | instant | Int overflow in min/max bounds |
| 4 | Level Order | Perfect binary tree of depth 17 (131,071 nodes) | 17 levels, last has 65,536 nodes | < 500 ms | DFS-based level tracking |
| 5 | Kth Largest Element | N=1,000,000 shuffled 1..1,000,000, k=500,000 | **500,000** | < 500 ms | Full sort O(N log N) ok; O(N²) selection sort hangs |
| 6 | Top K Frequent | N=500,000: values 1..1000 cycling, k=10 | 10 most frequent (991..1000 by count) | < 500 ms | Sorting all unique pairs |
| 7 | Merge K Sorted Lists | K=1,000 lists × 1,000 nodes (interleaved values) | 1..1,000,000 sorted | < 1 s | Sequential merge O(NK) |
| 8 | Find Median from Data Stream | 500,000 addNum calls (shuffled 1..500,000), findMedian after each | Correct median each time | < 2 s | Re-sorting per query |
| 9 | Implement Trie | Insert 100,000 random 10-char words; search all + 100,000 absent words | All inserted found, absent not | < 2 s | Re-scanning all words per search |
| 10 | Word Search II | 12×12 board of 'a'-'z' random, words = 1,000 random 5-char strings | Found words match brute-force check | < 2 s | Per-word DFS (W × M×N×4^L) |

### 📝 Notes
- **Case 1/2:** Build degenerate trees via loop (`root = TreeNode(i, root)` style) — recursion depth 10⁵ will crash default JVM stack; iterative or increased stack (`-Xss512m`) needed.
- **Case 3:** Classic overflow trap: using `Int.MAX_VALUE`/`Int.MIN_VALUE` as "no bound" sentinel fails when node values equal them — use `Long` bounds or nullable bounds.
- **Case 6:** With values cycling 1..1000 over 500,000 elements, each value appears 500 times — all tie; verify k=10 returns any 10 (or adjust to values 1..1000 with distinct counts).
- **Case 8:** Verify medians against a maintained sorted list on a smaller run (N=1,000) first, then trust the structure for the large run.
