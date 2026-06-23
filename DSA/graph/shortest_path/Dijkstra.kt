package graph.shortest_path

import java.util.PriorityQueue

/**
 * Dijkstra's Algorithm — Shortest path from source to all nodes in weighted graph.
 * Works only with non-negative weights.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Must-know algorithm, used in network routing, maps)
 */

fun main() {
    // Graph: 5 nodes, edges = [u, v, weight]
    val n = 5
    val edges = listOf(
        listOf(0, 1, 4), listOf(0, 2, 1),
        listOf(2, 1, 2), listOf(1, 3, 1),
        listOf(2, 3, 5), listOf(3, 4, 3)
    )

    val dist = dijkstra(n, edges, 0)
    println("Shortest distances from node 0: ${dist.toList()}")
    // Expected: [0, 3, 1, 4, 7]
}

/**
 * Dijkstra using Min-Heap: O((V+E) log V) time, O(V+E) space
 */
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
