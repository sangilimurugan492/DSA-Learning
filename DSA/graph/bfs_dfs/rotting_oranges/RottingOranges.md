# Rotting Oranges — Detailed Explanation

> **LeetCode #994** | [Problem Link](https://leetcode.com/problems/rotting-oranges/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic multi-source BFS — must know)
> **Topic:** BFS, Matrix, Graph

---

## 📋 Problem Statement

Every minute, any fresh orange adjacent (4-directional) to a rotten orange becomes rotten. Return the minimum number of minutes until no fresh orange remains. If impossible, return -1.

- `0` = empty cell
- `1` = fresh orange
- `2` = rotten orange

### Examples

```
Grid:                    Result: 4
2 1 1
1 1 0
0 1 1

Grid:                    Result: -1 (bottom-left can't be reached)
2 1 1
0 1 1
1 0 1
```

---

## 🧩 Method 1: Multi-Source BFS — O(M×N)

### Core Idea

1. Find all initially rotten oranges — add them to the queue (minute 0).
2. BFS level by level: each level = 1 minute of rotting.
3. Count fresh oranges. If any remain after BFS → return -1.

### Key Insight

> This is **multi-source BFS** — all rotten oranges start spreading simultaneously. BFS guarantees minimum time because it processes in order of distance. The minute when the last fresh orange rots is the answer.

### Dry Run — `[[2,1,1],[1,1,0],[0,1,1]]`

```
Initial: rotten=[(0,0)], fresh=6

Minute 0: Process (0,0)
  → rot (0,1) and (1,0), fresh=4

Minute 1: Process (0,1)
  → rot (0,2) and (1,1), fresh=2

Minute 1: Process (1,0)
  → (0,0) already rotten, (1,1) already rotten, (2,0) is empty

Minute 2: Process (0,2)
  → no fresh neighbors

Minute 2: Process (1,1)
  → rot (2,1), fresh=1

Minute 3: Process (2,1)
  → rot (2,2), fresh=0

Minute 4: Process (2,2)
  → no fresh neighbors

fresh=0 → return 4 ✅
```

### Code

```kotlin
fun orangesRotting(grid: Array<IntArray>): Int {
    val rows = grid.size
    val cols = grid[0].size
    val queue = ArrayDeque<Triple<Int, Int, Int>>()  // (row, col, minute)
    var freshCount = 0

    // Initialize: add all rotten oranges, count fresh
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            when (grid[r][c]) {
                2 -> queue.addLast(Triple(r, c, 0))
                1 -> freshCount++
            }
        }
    }

    if (freshCount == 0) return 0  // No fresh oranges

    val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
    var maxMinutes = 0

    while (queue.isNotEmpty()) {
        val (r, c, minute) = queue.removeFirst()
        for ((dr, dc) in directions) {
            val nr = r + dr
            val nc = c + dc
            if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == 1) {
                grid[nr][nc] = 2  // Rot it
                freshCount--
                maxMinutes = maxOf(maxMinutes, minute + 1)
                queue.addLast(Triple(nr, nc, minute + 1))
            }
        }
    }

    return if (freshCount == 0) maxMinutes else -1
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M×N) | Each cell visited at most once |
| **Space** | O(M×N) | Queue (worst case all rotten) |

---

## 📊 Key Patterns

| Concept | Implementation |
|---------|---------------|
| Multi-source BFS | All rotten oranges start in queue |
| Level = time | Each BFS level = 1 minute |
| Track fresh count | If > 0 at end → impossible (-1) |
| 4-directional | Up, down, left, right |
| In-place marking | Set grid[r][c] = 2 (rotten) to mark visited |

> **Interview Tip:** This is the **multi-source BFS** pattern — all sources start simultaneously. The key insight: BFS processes nodes in order of distance, so the last fresh orange to rot is at the maximum distance from any rotten orange. Compare with Number of Islands (DFS) — here BFS is better because we need the minimum time (distance). Other multi-source BFS problems: Walls and Gates, 01 Matrix.
