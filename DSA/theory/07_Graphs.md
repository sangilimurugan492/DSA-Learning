# Data Structure Theory: Graphs

> **In-depth theory, diagrams, and implementation details for understanding graphs at a fundamental level.**

---

## 1. What is a Graph?

A graph is a **non-linear** data structure consisting of **vertices** (nodes) and **edges** (connections). Unlike trees, graphs can have **cycles**, nodes can have **multiple parents**, and not all nodes need to be connected.

```
Undirected Graph:

    A ──── B
    │      │
    │      │
    C ──── D ──── E
           │
           │
           F

Vertices: {A, B, C, D, E, F}
Edges:    {(A,B), (A,C), (B,D), (C,D), (D,E), (D,F)}

Directed Graph:

    A ──→ B ──→ C
    │           ↑
    ↓     ┌─────┘
    D ──→ E

Edges have DIRECTION (one-way)
```

### Key Terminology:

| Term | Definition |
|------|-----------|
| **Vertex (Node)** | A point in the graph |
| **Edge** | Connection between two vertices |
| **Directed** | Edge has direction (A→B ≠ B→A) |
| **Undirected** | Edge has no direction (A-B = B-A) |
| **Weighted** | Edges have costs/weights |
| **Degree** | Number of edges connected to a vertex |
| **In-degree** | Number of incoming edges (directed) |
| **Out-degree** | Number of outgoing edges (directed) |
| **Path** | Sequence of vertices connected by edges |
| **Cycle** | Path that starts and ends at same vertex |
| **Connected** | Path exists between every pair of vertices |
| **Component** | Maximal connected subgraph |
| **DAG** | Directed Acyclic Graph (directed, no cycles) |

---

## 2. Graph Representations

### 2.1 Adjacency Matrix

A 2D array where `matrix[i][j] = 1` (or weight) if edge exists from vertex i to vertex j.

```
Graph:        A ── B
              │    │
              C ── D

          A  B  C  D
       A [0, 1, 1, 0]
       B [1, 0, 0, 1]
       C [1, 0, 0, 1]
       D [0, 1, 1, 0]

Space: O(V²) — always V×V matrix
Edge lookup: O(1) — matrix[i][j]
Finding neighbors: O(V) — scan entire row
Adding edge: O(1)

Best for: DENSE graphs (many edges), frequent edge lookups
```

### 2.2 Adjacency List

An array of lists where `list[i]` contains all vertices adjacent to vertex i.

```
Graph:        A ── B
              │    │
              C ── D

A → [B, C]
B → [A, D]
C → [A, D]
D → [B, C]

Space: O(V + E) — only stores actual edges
Edge lookup: O(degree) — scan adjacency list
Finding neighbors: O(degree) — already have the list
Adding edge: O(1)

Best for: SPARSE graphs (few edges), most real-world graphs
```

### Comparison:

| Aspect | Adjacency Matrix | Adjacency List |
|--------|-----------------|----------------|
| Space | O(V²) | **O(V + E)** |
| Edge lookup | **O(1)** | O(degree) |
| All neighbors | O(V) | **O(degree)** |
| Add edge | **O(1)** | **O(1)** |
| Dense graph | Good | Wasteful (list = V) |
| Sparse graph | Wasteful (matrix mostly 0) | **Efficient** |
| Most interviews | Rarely used | **Preferred** |

---

## 3. Graph Traversal

### 3.1 BFS (Breadth-First Search)

Explore **level by level** using a **queue**. Finds **shortest path** in unweighted graphs.

```
Graph:       A
           / | \
          B  C  D
         / \     |
        E   F    G

BFS from A:
Queue: [A]
→ Visit A, enqueue neighbors: [B, C, D]
→ Visit B, enqueue neighbors: [C, D, E, F]
→ Visit C (already queued? use visited set): [D, E, F]
→ Visit D, enqueue G: [E, F, G]
→ Visit E: [F, G]
→ Visit F: [G]
→ Visit G: []

Visit order: A, B, C, D, E, F, G (level by level)

Level 0: A
Level 1: B, C, D
Level 2: E, F, G
```

```kotlin
fun bfs(graph: Map<Char, List<Char>>, start: Char) {
    val visited = mutableSetOf<Char>()
    val queue = ArrayDeque<Char>()
    queue.add(start)
    visited.add(start)

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        println(node)  // Visit
        for (neighbor in graph[node] ?: emptyList()) {
            if (neighbor !in visited) {
                visited.add(neighbor)
                queue.add(neighbor)
            }
        }
    }
}
```

### 3.2 DFS (Depth-First Search)

Explore **as deep as possible** before backtracking. Uses **stack** (or recursion).

```
Graph:       A
           / | \
          B  C  D
         / \     |
        E   F    G

DFS from A (recursive):
→ Visit A, go to B
  → Visit B, go to E
    → Visit E, no unvisited neighbors, backtrack
  → Go to F
    → Visit F, no unvisited neighbors, backtrack
  → Backtrack to A
→ Go to C
  → Visit C, no unvisited neighbors, backtrack
→ Go to D
  → Visit D, go to G
    → Visit G, backtrack

Visit order: A, B, E, F, C, D, G (depth-first)

Uses recursion (implicit stack) or explicit stack.
```

```kotlin
fun dfs(graph: Map<Char, List<Char>>, node: Char, visited: MutableSet<Char>) {
    visited.add(node)
    println(node)  // Visit
    for (neighbor in graph[node] ?: emptyList()) {
        if (neighbor !in visited) {
            dfs(graph, neighbor, visited)
        }
    }
}
```

### BFS vs DFS:

| Aspect | BFS | DFS |
|--------|-----|-----|
| Data structure | Queue | Stack / Recursion |
| Exploration | Level by level | Deep, then backtrack |
| Shortest path | **Yes (unweighted)** | No |
| Memory | O(V) — queue width | O(h) — recursion depth |
| Best for | Shortest path, level order | Connectivity, cycles, topological sort |
| Path found | Shortest | Not necessarily shortest |

---

## 4. Key Graph Algorithms

### 4.1 Dijkstra's Algorithm (Shortest Path — Weighted, Non-Negative)

```
Find shortest path from source to all vertices.

Graph (weighted):
    A --4-- B
    |       |
    2       3
    |       |
    C --1-- D

From A:
Step 1: dist[A]=0, dist[B,C,D]=∞. Priority Queue: [(A,0)]
Step 2: Pop A(0). Neighbors: B(0+4=4), C(0+2=2). PQ: [(C,2), (B,4)]
Step 3: Pop C(2). Neighbor: D(2+1=3). PQ: [(D,3), (B,4)]
Step 4: Pop D(3). Neighbor: B(3+3=6 > 4, skip). PQ: [(B,4)]
Step 5: Pop B(4). Done.

Result: A=0, C=2, D=3, B=4

Time: O((V+E) log V) with priority queue
Cannot handle negative weights!
```

### 4.2 Topological Sort (DAG Ordering)

```
Linear ordering where for every edge u→v, u comes BEFORE v.

DAG:    5 ──→ 2 ──→ 3
        │           ↑
        ↓     ┌─────┘
        0 ──→ 1

Valid topological order: [5, 0, 2, 3, 1] or [5, 2, 0, 3, 1] etc.

Algorithm: Kahn's (BFS-based)
1. Compute in-degree for each vertex
2. Queue all vertices with in-degree 0
3. Pop vertex, add to result, decrease neighbors' in-degree
4. If neighbor's in-degree becomes 0, add to queue
5. Repeat until queue empty

If result.size < V → cycle exists (not a DAG)

Time: O(V + E)
```

### 4.3 Union-Find / Disjoint Set (Connected Components)

```
Detect cycles, find connected components.

Graph:   A ── B    C ── D
         |         |
         E         F

Components: {A,B,E}, {C,D,F}

Operations:
- find(x): Find root/representative of x's set
- union(x,y): Merge x's and y's sets
- Path compression: Flatten tree on find
- Union by rank: Attach smaller tree under larger

With optimizations: O(α(N)) ≈ O(1) amortized per operation
```

### 4.4 Minimum Spanning Tree (MST)

```
Find tree connecting ALL vertices with MINIMUM total edge weight.

Graph (weighted):
    A --4-- B
    |    / |
    2   3  5
    |  /   |
    C --1-- D

MST: A-C(2), C-D(1), A-B(4) → total = 7

Algorithms:
- Kruskal's: Sort edges by weight, add if no cycle (Union-Find)
- Prim's: Start from vertex, greedily add cheapest edge to new vertex

Time: O(E log E) Kruskal, O(E log V) Prim
```

---

## 5. Operations and Time Complexity

| Operation | Adjacency List | Adjacency Matrix |
|-----------|---------------|-----------------|
| Add vertex | O(1) | O(V²) (resize matrix) |
| Add edge | **O(1)** | **O(1)** |
| Remove vertex | O(V + E) | O(V²) |
| Remove edge | O(degree) | **O(1)** |
| Query edge (u,v) | O(degree) | **O(1)** |
| Find neighbors | **O(degree)** | O(V) |
| BFS | O(V + E) | O(V²) |
| DFS | O(V + E) | O(V²) |
| Dijkstra | O((V+E) log V) | O(V²) |
| Topological sort | O(V + E) | O(V²) |

---

## 6. Implementation (Kotlin)

### Adjacency List Graph:

```kotlin
class Graph<T> {
    private val adjacencyList = mutableMapOf<T, MutableList<T>>()

    fun addVertex(vertex: T) {
        adjacencyList.putIfAbsent(vertex, mutableListOf())
    }

    fun addEdge(from: T, to: T, directed: Boolean = false) {
        addVertex(from)
        addVertex(to)
        adjacencyList[from]!!.add(to)
        if (!directed) {
            adjacencyList[to]!!.add(from)
        }
    }

    fun bfs(start: T): List<T> {
        val visited = mutableSetOf<T>()
        val result = mutableListOf<T>()
        val queue = ArrayDeque<T>()
        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val vertex = queue.removeFirst()
            result.add(vertex)
            for (neighbor in adjacencyList[vertex] ?: emptyList()) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }
        return result
    }

    fun dfs(start: T): List<T> {
        val visited = mutableSetOf<T>()
        val result = mutableListOf<T>()
        fun dfsRec(node: T) {
            visited.add(node)
            result.add(node)
            for (neighbor in adjacencyList[node] ?: emptyList()) {
                if (neighbor !in visited) dfsRec(neighbor)
            }
        }
        dfsRec(start)
        return result
    }
}
```

### Topological Sort (Kahn's Algorithm):

```kotlin
fun topologicalSort(graph: Map<Int, List<Int>>, numVertices: Int): List<Int> {
    val inDegree = IntArray(numVertices)
    for ((_, neighbors) in graph) {
        for (neighbor in neighbors) inDegree[neighbor]++
    }

    val queue = ArrayDeque<Int>()
    for (i in 0 until numVertices) {
        if (inDegree[i] == 0) queue.add(i)
    }

    val result = mutableListOf<Int>()
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        result.add(node)
        for (neighbor in graph[node] ?: emptyList()) {
            inDegree[neighbor]--
            if (inDegree[neighbor] == 0) queue.add(neighbor)
        }
    }

    return if (result.size == numVertices) result else emptyList()  // Cycle if < V
}
```

---

## 7. Advantages and Disadvantages

### Advantages:
- **Flexible**: Can represent any relationship (not just hierarchical)
- **Versatile**: Models real-world networks, maps, social graphs
- **Rich algorithms**: Shortest path, MST, flow, coloring
- **Directional**: Can be directed or undirected, weighted or unweighted

### Disadvantages:
- **Complex**: More complex than trees or linear structures
- **Memory**: Can require O(V²) (matrix) or O(V+E) (list)
- **Cycle handling**: Must track visited nodes to avoid infinite loops
- **No direct access**: Must traverse to reach a specific node

---

## 8. When to Use Graphs

### Use Graphs When:
- ✅ Modeling **relationships** between entities (social networks)
- ✅ **Routing** / **shortest path** (Google Maps)
- ✅ **Dependency** resolution (build systems, course prerequisites)
- ✅ **Network** analysis (computer networks)
- ✅ **Recommendation** systems (user-item graphs)
- ✅ **State machines** (finite automata)

---

## 9. Real-World Applications

| Application | Graph Type | Algorithm |
|-------------|-----------|-----------|
| **Google Maps** | Weighted directed graph | Dijkstra, A* |
| **Social network** | Undirected graph | BFS, connected components |
| **Course prerequisites** | DAG | Topological sort |
| **Network routing** | Weighted graph | Dijkstra, Bellman-Ford |
| **Recommendation engine** | Bipartite graph | BFS, collaborative filtering |
| **Version control (Git)** | DAG | Topological sort |
| **Circuit design** | Directed graph | Cycle detection |
| **Web crawling** | Directed graph | BFS/DFS |
| **Flight routing** | Weighted graph | Dijkstra, MST |
| **Dependency resolution** | DAG | Topological sort |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Trees →](./06_Trees.md)
- [Next: Heaps →](./08_Heaps.md)
