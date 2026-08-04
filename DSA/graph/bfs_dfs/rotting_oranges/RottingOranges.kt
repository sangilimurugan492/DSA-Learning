package patterns.bfs.rotting_oranges

/**
 * https://leetcode.com/problems/rotting-oranges/
 * Every minute, fresh oranges adjacent to rotten ones become rotten.
 * Return min minutes until no fresh orange remains, or -1 if impossible.
 * 0=empty, 1=fresh, 2=rotten
 * Example: [[2,1,1],[1,1,0],[0,1,1]] → 4
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic multi-source BFS — must know)
 */

fun main() {
    val grid1 = arrayOf(
        intArrayOf(2, 1, 1),
        intArrayOf(1, 1, 0),
        intArrayOf(0, 1, 1),
    )
    println(orangesRotting(grid1))  // 4

    val grid2 = arrayOf(
        intArrayOf(2, 1, 1),
        intArrayOf(0, 1, 1),
        intArrayOf(1, 0, 1),
    )
    println(orangesRotting(grid2))  // -1 (bottom-left orange can't be reached)
}

/**
 * Multi-source BFS: O(M×N) time, O(M×N) space
 * 1. Add all rotten oranges to queue (minute 0).
 * 2. BFS level by level — each level = 1 minute.
 * 3. Count remaining fresh oranges. If > 0 → return -1.
 */
fun orangesRotting(grid: Array<IntArray>): Int {
    val rows = grid.size
    val cols = grid[0].size
    val queue = ArrayDeque<Triple<Int, Int, Int>>()  // (row, col, minute)
    var freshCount = 0

    // Initialize: add all rotten oranges to queue, count fresh
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            when (grid[r][c]) {
                2 -> queue.addLast(Triple(r, c, 0))
                1 -> freshCount++
            }
        }
    }

    if (freshCount == 0) return 0  // No fresh oranges → 0 minutes

    val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
    var maxMinutes = 0

    while (queue.isNotEmpty()) {
        val (r, c, minute) = queue.removeFirst()

        for ((dr, dc) in directions) {
            val nr = r + dr
            val nc = c + dc
            if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == 1) {
                grid[nr][nc] = 2  // Rot the orange
                freshCount--
                maxMinutes = maxOf(maxMinutes, minute + 1)
                queue.addLast(Triple(nr, nc, minute + 1))
            }
        }
    }

    return if (freshCount == 0) maxMinutes else -1
}
