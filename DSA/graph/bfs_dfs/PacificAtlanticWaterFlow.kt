package graph.bfs_dfs

/**
 * https://leetcode.com/problems/pacific-atlantic-water-flow/
 * Find cells where water can flow to both Pacific (top/left) and Atlantic (bottom/right) oceans.
 * Water flows from a cell to adjacent cells with equal or lower height.
 * FAANG Importance: ⭐⭐⭐⭐ (Reverse BFS/DFS from borders pattern)
 */

fun main() {
    val heights = arrayOf(
        intArrayOf(1, 2, 2, 3, 5),
        intArrayOf(3, 2, 3, 4, 4),
        intArrayOf(2, 4, 5, 3, 1),
        intArrayOf(6, 7, 1, 4, 5),
        intArrayOf(5, 1, 1, 2, 4)
    )
    val result = pacificAtlantic(heights)
    println(result.map { it.toList() })
}

/**
 * REVERSE DFS: O(M×N) time, O(M×N) space
 * Instead of checking each cell, start from ocean borders and go inward.
 * Cells reachable from both oceans are the answer.
 */
fun pacificAtlantic(heights: Array<IntArray>): List<List<Int>> {
    val rows = heights.size
    val cols = heights[0].size
    val pacific = Array(rows) { BooleanArray(cols) }
    val atlantic = Array(rows) { BooleanArray(cols) }

    fun dfs(r: Int, c: Int, visited: Array<BooleanArray>, prevHeight: Int) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return
        if (visited[r][c]) return
        if (heights[r][c] < prevHeight) return  // Water can't flow uphill
        visited[r][c] = true
        dfs(r + 1, c, visited, heights[r][c])
        dfs(r - 1, c, visited, heights[r][c])
        dfs(r, c + 1, visited, heights[r][c])
        dfs(r, c - 1, visited, heights[r][c])
    }

    // Start from Pacific border (top row, left column)
    for (c in 0 until cols) dfs(0, c, pacific, heights[0][c])
    for (r in 0 until rows) dfs(r, 0, pacific, heights[r][0])

    // Start from Atlantic border (bottom row, right column)
    for (c in 0 until cols) dfs(rows - 1, c, atlantic, heights[rows - 1][c])
    for (r in 0 until rows) dfs(r, cols - 1, atlantic, heights[r][cols - 1])

    // Find cells reachable from both oceans
    val result = mutableListOf<List<Int>>()
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (pacific[r][c] && atlantic[r][c]) result.add(listOf(r, c))
        }
    }
    return result
}
