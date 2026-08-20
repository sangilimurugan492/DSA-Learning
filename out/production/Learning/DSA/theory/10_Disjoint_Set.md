# Data Structure Theory: Disjoint Set (Union-Find)

> **In-depth theory, diagrams, and implementation details for understanding disjoint sets at a fundamental level.**

---

## 1. What is a Disjoint Set?

A Disjoint Set (also called **Union-Find**) is a data structure that tracks elements partitioned into **disjoint (non-overlapping) sets**. It efficiently answers: "Are these two elements in the same set?" and merges two sets together.

```
Elements: {1, 2, 3, 4, 5, 6, 7, 8}

After some union operations:
Set 1: {1, 2, 3, 4}     → representative: 1
Set 2: {5, 6}           → representative: 5
Set 3: {7, 8}           → representative: 7

find(3) = 1  (3 is in set with representative 1)
find(6) = 5  (6 is in set with representative 5)
find(3) ≠ find(6) → 3 and 6 are in DIFFERENT sets

union(4, 6) → merge Set 1 and Set 2:
Set 1: {1, 2, 3, 4, 5, 6}  → representative: 1 (or 5)
Set 2: {7, 8}               → representative: 7

Now find(3) == find(6) → same set!
```

### Key Properties:
- **Partitioning**: Elements are split into non-overlapping sets
- **Representative**: Each set has one "root" element as its representative
- **Two operations**: `find(x)` and `union(x, y)` — that's it!
- **Near-O(1) operations**: With path compression + union by rank, amortized O(α(N)) ≈ O(1)

---

## 2. Core Operations

### 2.1 Find: "Which set does x belong to?"

Returns the **representative** (root) of x's set. Two elements are in the same set if `find(x) == find(y)`.

```
Set structure (tree representation):
        1               5
       / \             /
      2   3           6
     /
    4

find(4): 4 → 2 → 1 → root (1 is representative)
find(6): 6 → 5 → root (5 is representative)
find(4) ≠ find(6) → different sets
```

### 2.2 Union: "Merge two sets"

Merges the sets containing x and y into one set.

```
BEFORE union(4, 6):

    1           5
   / \         /
  2   3       6
 /
4

AFTER union(4, 6):

    1
   /|\
  2 3  5
 /     |
4      6

Now find(4) = find(6) = 1 → same set!
```

---

## 3. Representing Sets as Trees

Each set is represented as a **tree** where:
- The **root** is the representative
- Each node points to its **parent**
- The root points to itself (or -1)

```
Array representation (parent array):

Index:   1   2   3   4   5   6
Parent: [1,  1,  1,  2,  5,  5]

Tree:
        1               5
       / \             /
      2   3           6
     /
    4

parent[4] = 2  → 4's parent is 2
parent[2] = 1  → 2's parent is 1
parent[1] = 1  → 1 is root (self-loop)

find(4): 4 → parent[4]=2 → parent[2]=1 → parent[1]=1 (root!) → return 1
```

---

## 4. Optimizations (Critical for Performance)

Without optimizations, both find and union are O(N) in the worst case (degenerate tree). Two optimizations make them nearly O(1):

### 4.1 Path Compression (Optimizes `find`)

During `find`, flatten the tree by making every node on the path point **directly to the root**.

```
BEFORE find(4):
        1
       /
      2
     /
    3
   /
  4

find(4) traverses: 4 → 3 → 2 → 1

AFTER path compression:
        1
       /|\
      2 3 4    ← All nodes now point directly to root!

Future find(4): 4 → 1 (one step!)

The tree gets flatter with every find → faster over time.
```

```kotlin
fun find(x: Int): Int {
    if (parent[x] != x) {
        parent[x] = find(parent[x])  // Path compression!
    }
    return parent[x]
}
```

### 4.2 Union by Rank (Optimizes `union`)

When merging two trees, attach the **shorter tree** under the **taller tree**. This keeps the tree shallow.

```
rank = approximate height of tree

BEFORE union(1, 5):
  rank=2: 1        rank=1: 5
         / \              |
        2   3             6

Attach smaller rank under larger rank:
         1 (rank=2)
        / \
       2   3
            \
             5 (rank=1)
              |
              6

Tree height stays small → find is fast
```

```kotlin
fun union(x: Int, y: Int) {
    val rootX = find(x)
    val rootY = find(y)
    if (rootX == rootY) return  // Already same set

    when {
        rank[rootX] < rank[rootY] -> parent[rootX] = rootY
        rank[rootX] > rank[rootY] -> parent[rootY] = rootX
        else -> {
            parent[rootY] = rootX
            rank[rootX]++  // Equal rank → height increases by 1
        }
    }
}
```

### Combined Complexity:

```
With BOTH optimizations:
  find: O(α(N)) amortized
  union: O(α(N)) amortized

α(N) = Inverse Ackermann function

For any practical N (even N = 10^80):
  α(N) < 5

So effectively O(1) per operation!

Without optimizations:     O(N) per operation
With path compression only: O(log N) amortized
With union by rank only:    O(log N)
With BOTH:                  O(α(N)) ≈ O(1) amortized
```

---

## 5. Implementation (Kotlin)

```kotlin
class DisjointSet(n: Int) {
    private val parent = IntArray(n) { it }  // Each element is its own parent initially
    private val rank = IntArray(n)          // All ranks start at 0

    // Find with path compression
    fun find(x: Int): Int {
        if (parent[x] != x) {
            parent[x] = find(parent[x])  // Compress path
        }
        return parent[x]
    }

    // Union by rank
    fun union(x: Int, y: Int): Boolean {
        val rootX = find(x)
        val rootY = find(y)

        if (rootX == rootY) return false  // Already in same set

        when {
            rank[rootX] < rank[rootY] -> parent[rootX] = rootY
            rank[rootX] > rank[rootY] -> parent[rootY] = rootX
            else -> {
                parent[rootY] = rootX
                rank[rootX]++
            }
        }
        return true  // Successfully merged
    }

    // Check if two elements are in the same set
    fun connected(x: Int, y: Int): Boolean = find(x) == find(y)

    // Count number of distinct sets
    fun countSets(): Int {
        var count = 0
        for (i in parent.indices) {
            if (parent[i] == i) count++  // Root nodes
        }
        return count
    }
}
```

---

## 6. Visual Walkthrough

```
Initial: 8 elements, each in own set
parent: [0, 1, 2, 3, 4, 5, 6, 7]
rank:   [0, 0, 0, 0, 0, 0, 0, 0]

Sets: {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7} (8 sets)

union(0, 1): find(0)=0, find(1)=1. rank[0]==rank[1] → parent[1]=0, rank[0]=1
parent: [0, 0, 2, 3, 4, 5, 6, 7]
Sets: {0,1}, {2}, {3}, {4}, {5}, {6}, {7} (7 sets)

union(2, 3): find(2)=2, find(3)=3. rank[2]==rank[3] → parent[3]=2, rank[2]=1
parent: [0, 0, 2, 2, 4, 5, 6, 7]
Sets: {0,1}, {2,3}, {4}, {5}, {6}, {7} (6 sets)

union(0, 2): find(0)=0, find(2)=2. rank[0]==rank[2] → parent[2]=0, rank[0]=2
parent: [0, 0, 0, 2, 4, 5, 6, 7]
Sets: {0,1,2,3}, {4}, {5}, {6}, {7} (5 sets)

union(4, 5): parent[5]=4, rank[4]=1
parent: [0, 0, 0, 2, 4, 4, 6, 7]

union(6, 7): parent[7]=6, rank[6]=1
parent: [0, 0, 0, 2, 4, 4, 6, 6]

connected(1, 3)? find(1)=0, find(3)=0 → YES (same set)
connected(1, 5)? find(1)=0, find(5)=4 → NO (different sets)

union(0, 4): find(0)=0, find(4)=4. rank[0]=2 > rank[4]=1 → parent[4]=0
parent: [0, 0, 0, 2, 0, 4, 6, 7]

Now: {0,1,2,3,4,5}, {6,7} (2 sets)

find(5) with path compression: 5 → 4 → 0 → 0 (root)
After: parent[5] = 0, parent[4] = 0 (flattened!)
```

---

## 7. Operations and Time Complexity

| Operation | No Optimization | Path Compression Only | Union by Rank Only | Both |
|-----------|----------------|---------------------|--------------------|----|
| **find** | O(N) | O(log N) amortized | O(log N) | **O(α(N))** ≈ O(1) |
| **union** | O(N) | O(log N) amortized | O(log N) | **O(α(N))** ≈ O(1) |
| **connected** | O(N) | O(log N) | O(log N) | **O(α(N))** ≈ O(1) |
| **Space** | O(N) | O(N) | O(N) | O(N) |

---

## 8. Key Patterns (Interview Critical)

### 8.1 Number of Connected Components

```
Problem: Given n nodes and edges, find number of connected components.

Graph: 0-1, 2-3, 4-5 (3 separate components)

union(0,1), union(2,3), union(4,5)
countSets() = n - successfulUnions = 6 - 3 = 3

Or: count roots (parent[i] == i) = 3
```

### 8.2 Cycle Detection in Undirected Graph

```
For each edge (u, v):
  if find(u) == find(v) → CYCLE! (already connected)
  else union(u, v)

Edge (0,1): find(0)≠find(1) → union → {0,1}
Edge (1,2): find(1)≠find(2) → union → {0,1,2}
Edge (0,2): find(0)==find(2) → CYCLE DETECTED!
```

### 8.3 Kruskal's MST Algorithm

```
1. Sort all edges by weight (ascending)
2. For each edge (u, v, w):
   if find(u) ≠ find(v):  // Not in same component
     add edge to MST
     union(u, v)
3. Stop when MST has V-1 edges

Greedy: always pick cheapest edge that doesn't create a cycle.
Union-Find efficiently checks "would this create a cycle?"
```

---

## 9. Advantages and Disadvantages

### Advantages:
- **Near O(1) operations**: With both optimizations, practically constant time
- **Simple**: Only two operations (find, union)
- **Space efficient**: O(N) for parent + rank arrays
- **No graph traversal needed**: Unlike BFS/DFS for connectivity
- **Incremental**: Can add edges dynamically and check connectivity

### Disadvantages:
- **Only connectivity**: Can't find actual paths (use BFS/DFS for that)
- **No edge weights**: Doesn't handle weighted connections directly
- **No deletion**: Can't remove edges (only add)
- **Integer-only typically**: Works best with integer-indexed elements

---

## 10. When to Use Disjoint Set

### Use Disjoint Set When:
- ✅ You need to check **connectivity** between elements
- ✅ **Connected components** in a graph
- ✅ **Cycle detection** in undirected graph
- ✅ **Kruskal's MST** algorithm
- ✅ **Dynamic connectivity** (adding edges over time)
- ✅ **Network connectivity** (are two computers connected?)

### Don't Use Disjoint Set When:
- ❌ You need **actual paths** between nodes (use BFS/DFS)
- ❌ You need to **remove edges** (not supported)
- ❌ You need **shortest path** (use Dijkstra/BFS)
- ❌ Graph is **static** (just use BFS/DFS once)

---

## 11. Real-World Applications

| Application | How Union-Find Is Used |
|-------------|----------------------|
| **Kruskal's MST** | Check if edge creates cycle before adding |
| **Network connectivity** | Are two computers in same network? |
| **Image processing** | Connected component labeling (pixels) |
| **Social networks** | Friend circles / groups |
| **Percolation** | Does material percolate (top-bottom connected)? |
| **Dynamic connectivity** | Add connections, query connectivity |
| **Least Common Ancestor** | Tarjan's offline LCA algorithm |
| **Accounts merge** | Merge accounts with shared emails |
| **Redundant connection** | Find edge that creates a cycle |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Tries →](./09_Tries.md)
