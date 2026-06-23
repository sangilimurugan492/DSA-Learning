package graph.union_find

/**
 * https://leetcode.com/problems/graph-valid-tree/
 * Given n nodes and edges, check if they form a valid tree.
 * Valid tree = connected + no cycles = exactly n-1 edges and 1 connected component.
 * FAANG Importance: ⭐⭐⭐⭐ (Union-Find cycle detection)
 */

fun main() {
    // Valid tree: 0-1, 0-2, 0-3, 1-4
    println(validTree(5, arrayOf(intArrayOf(0, 1), intArrayOf(0, 2), intArrayOf(0, 3), intArrayOf(1, 4))))  // true

    // Has cycle: 0-1, 1-2, 2-0
    println(validTree(3, arrayOf(intArrayOf(0, 1), intArrayOf(1, 2), intArrayOf(2, 0))))  // false

    // Not connected
    println(validTree(4, arrayOf(intArrayOf(0, 1), intArrayOf(2, 3))))  // false
}

/**
 * Union-Find: O(E × α(N)) time, O(N) space
 * Tree = n-1 edges AND no cycles (all unions succeed).
 */
fun validTree(n: Int, edges: Array<IntArray>): Boolean {
    if (edges.size != n - 1) return false  // Tree must have exactly n-1 edges

    val parent = IntArray(n) { it }
    val rank = IntArray(n)

    fun find(x: Int): Int {
        if (parent[x] != x) parent[x] = find(parent[x])
        return parent[x]
    }

    fun union(x: Int, y: Int): Boolean {
        val px = find(x)
        val py = find(y)
        if (px == py) return false  // Cycle detected!
        if (rank[px] < rank[py]) parent[px] = py
        else if (rank[px] > rank[py]) parent[py] = px
        else { parent[py] = px; rank[px]++ }
        return true
    }

    for (edge in edges) {
        if (!union(edge[0], edge[1])) return false  // Cycle!
    }
    return true
}
