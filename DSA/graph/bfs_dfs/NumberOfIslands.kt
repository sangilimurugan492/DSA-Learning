package graph.bfs_dfs

/**
 * https://leetcode.com/problems/number-of-islands/
 * Count the number of islands in a 2D grid ('1' = land, '0' = water).
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 5 most asked graph problem)
 */

fun main() {
    val grid1 = arrayOf(
        charArrayOf('1', '1', '1', '1', '0'),
        charArrayOf('1', '1', '0', '1', '0'),
        charArrayOf('1', '1', '0', '0', '0'),
        charArrayOf('0', '0', '0', '0', '0')
    )
    println(numIslandsDFS(grid1))  // 1

    val grid2 = arrayOf(
        charArrayOf('1', '1', '0', '0', '0'),
        charArrayOf('1', '1', '0', '0', '0'),
        charArrayOf('0', '0', '1', '0', '0'),
        charArrayOf('0', '0', '0', '1', '1')
    )
    println(numIslandsDFS(grid2))  // 3
}

/**
 * DFS: O(M×N) time, O(M×N) space
 * Visit each cell. If it's land, sink the entire island (mark as visited).
 */
fun numIslandsDFS(grid: Array<CharArray>): Int {
    if (grid.isEmpty()) return 0
    val rows = grid.size
    val cols = grid[0].size
    var count = 0

    fun dfs(r: Int, c: Int) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] == '0') return
        grid[r][c] = '0'  // Sink the island (mark visited)
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

/**
 * BFS: O(M×N) time, O(min(M,N)) space
 * Use queue to sink islands level by level.
 */
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
                    for ((nr, nc) in listOf(cr + 1 to cc, cr - 1 to cc, cr to cc + 1, cr to cc - 1)) {
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
