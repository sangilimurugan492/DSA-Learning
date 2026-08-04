# Number of Islands — Detailed Explanation

> **LeetCode #200** | [Problem Link](https://leetcode.com/problems/number-of-islands/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 5 most asked graph problem)
> **Topic:** Graph, DFS, BFS, Matrix

---

## 📋 Problem Statement

Given an `m x n` 2D binary grid where `'1'` represents land and `'0'` represents water, count the number of islands. An island is surrounded by water and formed by connecting adjacent lands horizontally or vertically.

### Examples

```
Grid:                    Grid:
1 1 1 1 0                1 1 0 0 0
1 1 0 1 0                1 1 0 0 0
1 1 0 0 0                0 0 1 0 0
0 0 0 0 0                0 0 0 1 1
Output: 1                Output: 3
```

---

## 🧩 Method 1: DFS (Sink the Island) — O(M×N)

### Core Idea

Scan the grid. When you find `'1'`, increment count and DFS to "sink" the entire island (mark all connected `'1'`s as `'0'`).

### Key Insight

> Marking visited cells by setting them to `'0'` (sinking) avoids needing a separate visited set. Each cell is visited exactly once.

### Dry Run

```
Grid:
1 1 0
1 0 0
0 0 1

Scan (0,0): '1' → count=1, DFS sinks {(0,0),(0,1),(1,0)}
0 0 0
0 0 0
0 0 1

Scan (2,2): '1' → count=2, DFS sinks {(2,2)}
0 0 0
0 0 0
0 0 0

Result: 2 ✅
```

### Code

```kotlin
fun numIslandsDFS(grid: Array<CharArray>): Int {
    if (grid.isEmpty()) return 0
    val rows = grid.size
    val cols = grid[0].size
    var count = 0

    fun dfs(r: Int, c: Int) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] == '0') return
        grid[r][c] = '0'  // Sink (mark visited)
        dfs(r + 1, c)
        dfs(r - 1, c)
        dfs(r, c + 1)
        dfs(r, c - 1)
    }

    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (grid[r][c] == '1') {
                count++
                dfs(r, c)
            }
        }
    }
    return count
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M×N) | Each cell visited once |
| **Space** | O(M×N) | Recursion stack (worst case) |

---

## 🧩 Method 2: BFS — O(M×N)

### Core Idea

Same as DFS but use a queue. For each `'1'`, BFS to sink all connected land.

### Code

```kotlin
fun numIslandsBFS(grid: Array<CharArray>): Int {
    if (grid.isEmpty()) return 0
    val rows = grid.size
    val cols = grid[0].size
    var count = 0

    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (grid[r][c] == '1') {
                count++
                grid[r][c] = '0'
                val queue = ArrayDeque<Pair<Int, Int>>()
                queue.addLast(r to c)
                while (queue.isNotEmpty()) {
                    val (cr, cc) = queue.removeFirst()
                    for ((nr, nc) in listOf(cr+1 to cc, cr-1 to cc, cr to cc+1, cr to cc-1)) {
                        if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == '1') {
                            grid[nr][nc] = '0'
                            queue.addLast(nr to nc)
                        }
                    }
                }
            }
        }
    }
    return count
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M×N) | Each cell visited once |
| **Space** | O(min(M,N)) | Queue width |

---

## 📊 Method Comparison

| Method | Time | Space | Pros | Cons |
|--------|------|-------|------|------|
| DFS | O(M×N) | O(M×N) | Simple | Stack overflow on large grids |
| BFS | O(M×N) | O(min(M,N)) | No stack overflow | Slightly more code |
| Union-Find | O(M×N·α) | O(M×N) | No grid modification | More complex |

> **Interview Tip:** DFS is the go-to solution. Mention BFS as an alternative to avoid stack overflow on large grids. If asked not to modify the grid, use a `visited` boolean array or Union-Find.
