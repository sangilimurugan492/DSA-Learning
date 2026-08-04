# Dijkstra's Algorithm — Detailed Explanation

> **LeetCode #743** (Network Delay Time) | [Problem Link](https://leetcode.com/problems/network-delay-time/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Must-know algorithm, used in network routing, maps)
> **Topic:** Graph, Shortest Path, Greedy, Min-Heap

---

## 📋 Problem Statement

Given a weighted directed graph with non-negative edge weights, find the shortest distance from a source node to all other nodes.

### Example

```
Graph: 5 nodes, edges = [u, v, weight]
0→1 (4), 0→2 (1), 2→1 (2), 1→3 (1), 2→3 (5), 3→4 (3)

Shortest distances from node 0: [0, 3, 1, 4, 7]
  - 0→0: 0
  - 0→2→1: 1+2=3
  - 0→2: 1
  - 0→2→1→3: 1+2+1=4
  - 0→2→1→3→4: 1+2+1+3=7
```

---

## 🧩 Method 1: Min-Heap (Priority Queue) — O((V+E) log V)

### Core Idea

Use a min-heap to always process the closest unvisited node. For each neighbor, relax the edge: if `dist[node] + weight < dist[neighbor]`, update.

### Key Insight

> Greedy: always expand the nearest node first. Once a node is popped from the heap, its shortest distance is finalized. The min-heap ensures we process nodes in order of increasing distance.

### Dry Run

```
Source=0, dist=[0, ∞, ∞, ∞, ∞]

Heap: [(0,0)]
Pop (0,0) → relax neighbors: dist[1]=4, dist[2]=1
Heap: [(1,2), (4,1)]

Pop (1,2) → relax: dist[1]=min(4, 1+2)=3, dist[3]=1+5=6
Heap: [(3,1), (4,1), (6,3)]

Pop (3,1) → relax: dist[3]=min(6, 3+1)=4
Heap: [(4,1), (4,3), (6,3)]

Pop (4,1) → skip (4 > dist[1]=3, outdated)
Pop (4,3) → relax: dist[4]=4+3=7
Heap: [(6,3), (7,4)]

Pop (6,3) → skip (6 > dist[3]=4)
Pop (7,4) → no neighbors

Result: [0, 3, 1, 4, 7] ✅
```

### Code

```kotlin
fun dijkstra(n: Int, edges: List<List<Int>>, source: Int): IntArray {
    // Build adjacency list
    val adj = List(n) { mutableListOf<Pair<Int, Int>>() }
    for (edge in edges) {
        adj[edge[0]].add(Pair(edge[1], edge[2]))
    }

    val dist = IntArray(n) { Int.MAX_VALUE }
    dist[source] = 0

    // Min-heap: (distance, node)
    val heap = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })
    heap.add(Pair(0, source))

    while (heap.isNotEmpty()) {
        val (currentDist, node) = heap.poll()
        if (currentDist > dist[node]) continue  // Skip outdated entries

        for ((neighbor, weight) in adj[node]) {
            val newDist = dist[node] + weight
            if (newDist < dist[neighbor]) {
                dist[neighbor] = newDist
                heap.add(Pair(newDist, neighbor))
            }
        }
    }

    return dist
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O((V+E) log V) | Each edge relaxed once, heap push O(log V) |
| **Space** | O(V+E) | Adjacency list + dist array + heap |

---

## 🧩 Method 2: Array (No Heap) — O(V²)

### Core Idea

Instead of a heap, scan all unvisited nodes to find the minimum distance. Better for dense graphs.

### Code

```kotlin
fun dijkstraArray(n: Int, edges: List<List<Int>>, source: Int): IntArray {
    val adj = Array(n) { IntArray(n) { Int.MAX_VALUE } }
    for (edge in edges) adj[edge[0]][edge[1]] = edge[2]

    val dist = IntArray(n) { Int.MAX_VALUE }
    val visited = BooleanArray(n)
    dist[source] = 0

    repeat(n) {
        // Find unvisited node with min distance
        var u = -1
        for (i in 0 until n) {
            if (!visited[i] && (u == -1 || dist[i] < dist[u])) u = i
        }
        visited[u] = true

        // Relax neighbors
        for (v in 0 until n) {
            if (!visited[v] && adj[u][v] != Int.MAX_VALUE && dist[u] != Int.MAX_VALUE) {
                dist[v] = minOf(dist[v], dist[u] + adj[u][v])
            }
        }
    }
    return dist
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(V²) | V iterations, each scans V nodes |
| **Space** | O(V²) | Adjacency matrix |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Min-Heap | O((V+E) log V) | O(V+E) | Sparse graphs (E << V²) |
| Array | O(V²) | O(V²) | Dense graphs (E ≈ V²) |

> **Important:** Dijkstra does NOT work with negative weights. For negative weights, use Bellman-Ford (O(V·E)). If asked about this limitation, mention Bellman-Ford as the alternative.

> **Interview Tip:** Always skip outdated heap entries (`if currentDist > dist[node] continue`). This is called "lazy deletion" — instead of decreasing a key in the heap (which requires a indexed priority queue), we push a new entry and skip the old one when popped.
