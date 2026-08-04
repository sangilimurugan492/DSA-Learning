# Course Schedule — Detailed Explanation

> **LeetCode #207** | [Problem Link](https://leetcode.com/problems/course-schedule/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Topological sort / Cycle detection — must know)
> **Topic:** Graph, Topological Sort, Cycle Detection, DFS, BFS

---

## 📋 Problem Statement

You must take `numCourses` courses labeled `0` to `numCourses-1`. Given `prerequisites[i] = [a, b]` meaning you must take `b` before `a`, determine if you can finish all courses.

### Examples

```
Input: numCourses=2, prerequisites=[[1,0]]
Output: true  (take 0, then 1)

Input: numCourses=2, prerequisites=[[1,0],[0,1]]
Output: false  (cycle: 0→1→0)
```

---

## 🧩 Method 1: DFS Cycle Detection — O(V+E)

### Core Idea

Build adjacency list. Use 3 states: `0=unvisited`, `1=visiting` (in current DFS path), `2=visited` (done). If we encounter a "visiting" node during DFS → cycle detected.

### Key Insight

> A "visiting" node in the current DFS path means we found a back edge → cycle. A "visited" node is fully processed and safe — skip it.

### Dry Run — `numCourses=3, prereq=[[0,1],[1,2]]`

```
Graph: 2→1→0

DFS from 0: state[0]=1 (visiting)
  → neighbor 1: state[1]=1 (visiting)
    → neighbor 2: state[2]=1 (visiting)
      → no neighbors → state[2]=2 (visited)
    → state[1]=2 (visited)
  → state[0]=2 (visited)
No cycle → true ✅
```

### Code

```kotlin
fun canFinishDFS(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
    val adj = List(numCourses) { mutableListOf<Int>() }
    for (pre in prerequisites) adj[pre[1]].add(pre[0])

    val state = IntArray(numCourses)  // 0=unvisited, 1=visiting, 2=visited

    fun hasCycle(node: Int): Boolean {
        if (state[node] == 1) return true   // Cycle!
        if (state[node] == 2) return false  // Already processed
        state[node] = 1  // Mark as visiting
        for (neighbor in adj[node]) {
            if (hasCycle(neighbor)) return true
        }
        state[node] = 2  // Mark as visited
        return false
    }

    for (i in 0 until numCourses) {
        if (state[i] == 0 && hasCycle(i)) return false
    }
    return true
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(V+E) | Visit each node and edge once |
| **Space** | O(V+E) | Adjacency list + state array |

---

## 🧩 Method 2: BFS (Kahn's Algorithm) — O(V+E)

### Core Idea

Process nodes with indegree 0 first. Remove them, decrement neighbors' indegree. If all nodes processed → no cycle. If some remain → cycle.

### Key Insight

> Nodes with indegree 0 have no dependencies — safe to take. Removing them may unlock new indegree-0 nodes. If the count of processed nodes < total → cycle exists.

### Dry Run — `numCourses=3, prereq=[[0,1],[1,2]]`

```
Indegrees: [0→1, 1→1, 2→0]
Queue: [2]

Process 2 → processed=1, neighbor 1 indegree→0, queue: [1]
Process 1 → processed=2, neighbor 0 indegree→0, queue: [0]
Process 0 → processed=3, queue empty

processed(3) == numCourses(3) → true ✅
```

### Code

```kotlin
fun canFinishBFS(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
    val adj = List(numCourses) { mutableListOf<Int>() }
    val indegree = IntArray(numCourses)
    for (pre in prerequisites) {
        adj[pre[1]].add(pre[0])
        indegree[pre[0]]++
    }

    val queue = ArrayDeque<Int>()
    for (i in 0 until numCourses) {
        if (indegree[i] == 0) queue.addLast(i)
    }

    var processed = 0
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        processed++
        for (neighbor in adj[node]) {
            indegree[neighbor]--
            if (indegree[neighbor] == 0) queue.addLast(neighbor)
        }
    }
    return processed == numCourses
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(V+E) | Visit each node and edge once |
| **Space** | O(V+E) | Adjacency list + indegree array |

---

## 📊 Method Comparison

| Method | Time | Space | Pros | Cons |
|--------|------|-------|------|------|
| DFS (3-state) | O(V+E) | O(V+E) | Intuitive cycle detection | Recursion depth |
| BFS (Kahn's) | O(V+E) | O(V+E) | Also gives topological order | Slightly more setup |

> **Interview Tip:** Both methods are O(V+E). DFS is more intuitive for cycle detection. Kahn's algorithm (BFS) is better when you also need the topological ordering (Course Schedule II). Explain the 3-state DFS clearly — "visiting" = in current path (cycle), "visited" = fully done (skip).
