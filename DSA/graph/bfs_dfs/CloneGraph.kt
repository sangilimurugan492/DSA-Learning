package graph.bfs_dfs

/**
 * https://leetcode.com/problems/clone-graph/
 * Deep copy a connected undirected graph (each node has neighbors list).
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Graph traversal + HashMap pattern)
 */

fun main() {
    val node1 = GNode(1)
    val node2 = GNode(2)
    val node3 = GNode(3)
    val node4 = GNode(4)
    node1.neighbors = listOf(node2, node4)
    node2.neighbors = listOf(node1, node3)
    node3.neighbors = listOf(node2, node4)
    node4.neighbors = listOf(node1, node3)

    val cloned = cloneGraphDFS(node1)
    println("Original: ${node1.`val`}, Cloned: ${cloned?.`val`}")
}

class GNode(val `val`: Int) {
    var neighbors: List<GNode?> = emptyList()
}

/**
 * DFS: O(N) time, O(N) space
 * Use HashMap to map original → clone. Recursively clone neighbors.
 */
fun cloneGraphDFS(node: GNode?): GNode? {
    if (node == null) return null
    val visited = HashMap<GNode, GNode>()

    fun dfs(curr: GNode): GNode {
        if (curr in visited) return visited[curr]!!
        val clone = GNode(curr.`val`)
        visited[curr] = clone
        for (neighbor in curr.neighbors) {
            if (neighbor != null) {
                clone.neighbors = clone.neighbors + dfs(neighbor)
            }
        }
        return clone
    }

    return dfs(node)
}

/**
 * BFS: O(N) time, O(N) space
 */
fun cloneGraphBFS(node: GNode?): GNode? {
    if (node == null) return null
    val visited = HashMap<GNode, GNode>()
    val queue = ArrayDeque<GNode>()
    queue.addLast(node)
    visited[node] = GNode(node.`val`)

    while (queue.isNotEmpty()) {
        val curr = queue.removeFirst()
        for (neighbor in curr.neighbors) {
            if (neighbor != null && neighbor !in visited) {
                visited[neighbor] = GNode(neighbor.`val`)
                queue.addLast(neighbor)
            }
            if (neighbor != null) {
                visited[curr]!!.neighbors = visited[curr]!!.neighbors + visited[neighbor]!!
            }
        }
    }
    return visited[node]
}
