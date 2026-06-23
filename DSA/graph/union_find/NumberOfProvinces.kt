package graph.union_find

/**
 * https://leetcode.com/problems/number-of-provinces/
 * Given n cities and isConnected matrix, return number of provinces (connected components).
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Union-Find fundamentals, connected components)
 */

fun main() {
    val isConnected = arrayOf(
        intArrayOf(1, 1, 0),
        intArrayOf(1, 1, 0),
        intArrayOf(0, 0, 1)
    )
    println(findCircleNumDFS(isConnected))  // 2
    println(findCircleNumUnionFind(isConnected))  // 2
}

/**
 * DFS: O(N²) time, O(N) space
 */
fun findCircleNumDFS(isConnected: Array<IntArray>): Int {
    val n = isConnected.size
    val visited = BooleanArray(n)
    var provinces = 0

    fun dfs(city: Int) {
        visited[city] = true
        for (j in 0 until n) {
            if (isConnected[city][j] == 1 && !visited[j]) dfs(j)
        }
    }

    for (i in 0 until n) {
        if (!visited[i]) {
            provinces++
            dfs(i)
        }
    }
    return provinces
}

/**
 * Union-Find: O(N² × α(N)) ≈ O(N²) time, O(N) space
 * α = inverse Ackermann function (nearly constant)
 */
fun findCircleNumUnionFind(isConnected: Array<IntArray>): Int {
    val n = isConnected.size
    val parent = IntArray(n) { it }
    val rank = IntArray(n)

    fun find(x: Int): Int {
        if (parent[x] != x) parent[x] = find(parent[x])  // Path compression
        return parent[x]
    }

    fun union(x: Int, y: Int) {
        val px = find(x)
        val py = find(y)
        if (px == py) return
        if (rank[px] < rank[py]) parent[px] = py
        else if (rank[px] > rank[py]) parent[py] = px
        else { parent[py] = px; rank[px]++ }
    }

    for (i in 0 until n) {
        for (j in i + 1 until n) {
            if (isConnected[i][j] == 1) union(i, j)
        }
    }

    return (0 until n).map { find(it) }.toSet().size
}
