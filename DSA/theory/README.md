# 📖 Data Structure Theory — In-Depth Guides

> **Comprehensive theory documents with ASCII diagrams, internal mechanics, implementations, and trade-offs for every core data structure.**

---

## 📑 Table of Contents

| # | Data Structure | Document | Key Concept |
|---|---------------|----------|-------------|
| 1 | **Arrays** | [01_Arrays.md](./01_Arrays.md) | Contiguous memory, O(1) random access, cache locality |
| 2 | **Linked Lists** | [02_Linked_Lists.md](./02_Linked_Lists.md) | Non-contiguous nodes, O(1) insert/delete at ends |
| 3 | **Stacks** | [03_Stacks.md](./03_Stacks.md) | LIFO, O(1) push/pop, monotonic stack patterns |
| 4 | **Queues** | [04_Queues.md](./04_Queues.md) | FIFO, O(1) enqueue/dequeue, BFS foundation |
| 5 | **Hash Tables** | [05_Hash_Tables.md](./05_Hash_Tables.md) | O(1) avg lookup, hash functions, collision resolution |
| 6 | **Trees** | [06_Trees.md](./06_Trees.md) | Hierarchy, BST O(log N), traversals, balancing |
| 7 | **Graphs** | [07_Graphs.md](./07_Graphs.md) | Vertices + edges, BFS/DFS, shortest path, MST |
| 8 | **Heaps** | [08_Heaps.md](./08_Heaps.md) | Complete binary tree, O(1) min/max, priority queue |
| 9 | **Tries** | [09_Tries.md](./09_Tries.md) | Prefix tree, O(M) operations, autocomplete |
| 10 | **Disjoint Set** | [10_Disjoint_Set.md](./10_Disjoint_Set.md) | Union-Find, O(α(N)) connectivity, cycle detection |

---

## 🧭 How to Use This Guide

### For Interview Preparation:
1. **Read the theory** — understand the "why" behind each structure
2. **Study the diagrams** — visualize how data is stored in memory
3. **Review the time complexity tables** — know operations cold
4. **Read the "When to Use" section** — know trade-offs
5. **Practice problems** in the parent `DSA/` folders

### For Deep Understanding:
- Each document includes **ASCII memory diagrams**
- **Internal mechanics** (how things work under the hood)
- **Kotlin implementations** with comments
- **Comparison tables** between related structures
- **Real-world applications**

---

## ⚡ Quick Reference — Complexity Cheatsheet

| Data Structure | Access | Search | Insert | Delete | Space |
|---------------|--------|--------|--------|--------|-------|
| **Array** | **O(1)** | O(N) | O(N) | O(N) | O(N) |
| **Dynamic Array** | **O(1)** | O(N) | O(1)* | O(N) | O(N) |
| **Linked List** | O(N) | O(N) | **O(1)**† | **O(1)**† | O(N) |
| **Stack** | O(N) | O(N) | **O(1)** | **O(1)** | O(N) |
| **Queue** | O(N) | O(N) | **O(1)** | **O(1)** | O(N) |
| **Hash Table** | N/A | **O(1)** avg | **O(1)** avg | **O(1)** avg | O(N) |
| **BST (balanced)** | O(log N) | **O(log N)** | **O(log N)** | **O(log N)** | O(N) |
| **Heap** | O(N) | O(N) | **O(log N)** | **O(log N)**‡ | O(N) |
| **Trie** | O(M) | **O(M)** | **O(M)** | **O(M)** | O(N×M) |
| **Disjoint Set** | N/A | **O(α(N))** | **O(α(N))** | N/A | O(N) |

> `*` amortized &nbsp; `†` at ends / known node &nbsp; `‡` extract root only &nbsp; `M` = word length &nbsp; `α` = inverse Ackermann

---

## 🔄 Data Structure Selection Guide

```
What do you need?
│
├── Random access by index?
│   └── Array / Dynamic Array
│
├── Fast lookup by key?
│   ├── Don't need order? → Hash Table
│   └── Need sorted order? → Balanced BST
│
├── Frequent insert/delete at ends?
│   ├── LIFO (last in, first out)? → Stack
│   ├── FIFO (first in, first out)? → Queue
│   └── Both ends? → Deque
│
├── Hierarchical data?
│   └── Tree (BST for ordered, Trie for strings)
│
├── Network / relationships?
│   └── Graph (adjacency list for sparse, matrix for dense)
│
├── Always need min/max quickly?
│   └── Heap (Priority Queue)
│
├── Prefix / autocomplete?
│   └── Trie
│
└── Connectivity / merge sets?
    └── Disjoint Set (Union-Find)
```

---

## 📊 Linear vs Non-Linear

### Linear Data Structures (Sequential)
| Structure | Order | Memory |
|-----------|-------|--------|
| Array | Index-based | Contiguous |
| Linked List | Sequential (next pointer) | Scattered |
| Stack | LIFO | Contiguous or scattered |
| Queue | FIFO | Contiguous or scattered |

### Non-Linear Data Structures (Hierarchical/Network)
| Structure | Organization | Use Case |
|-----------|-------------|----------|
| Tree | Parent-child hierarchy | BST, heap, trie |
| Graph | Arbitrary connections | Networks, maps |
| Disjoint Set | Forest of trees | Connectivity |

---

## 🎯 Common Interview Patterns by Structure

| Data Structure | Top Interview Patterns |
|---------------|----------------------|
| **Array** | Two Pointer, Sliding Window, Prefix Sum, Kadane's |
| **Linked List** | Reverse, Cycle Detection (Floyd), Merge, Find Middle |
| **Stack** | Valid Parentheses, Monotonic Stack (Next Greater), RPN |
| **Queue** | BFS, Level-Order Traversal, Sliding Window Max |
| **Hash Table** | Two Sum, Frequency Count, Group Anagrams, LRU Cache |
| **Tree** | DFS Traversals, BFS Level-Order, LCA, Validate BST |
| **Graph** | BFS/DFS, Topological Sort, Dijkstra, Union-Find |
| **Heap** | Top K, Kth Largest, Merge K Sorted, Running Median |
| **Trie** | Autocomplete, Word Search II, Prefix Matching |
| **Disjoint Set** | Connected Components, Cycle Detection, MST (Kruskal) |

---

## 🔗 Related Resources

- [← Back to DSA Main README](../README.md) — Problem solutions organized by pattern
- [DSA Study Guide](../study.md) — Learning roadmap
- [Pattern-Wise Problems](../PatternWiseProblems.md) — Interview problem tracker

---

> 💡 **Pro Tip:** Don't just memorize complexity tables. Understand *why* each structure has those complexities by studying the internal mechanics in each document. The diagrams show exactly how data is stored and manipulated.
