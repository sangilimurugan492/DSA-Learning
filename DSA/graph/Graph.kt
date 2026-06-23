package graph

/**
 * Graph representation utilities used across graph problems.
 * Adjacency List is the most common representation for DSA problems.
 */

/**
 * Build adjacency list from edge list (undirected graph).
 * n = number of nodes (0-indexed), edges = list of [u, v] pairs
 */
fun buildUndirectedGraph(n: Int, edges: List<List<Int>>): List<MutableList<Int>> {
    val adj = List(n) { mutableListOf<Int>() }
    for (edge in edges) {
        adj[edge[0]].add(edge[1])
        adj[edge[1]].add(edge[0])
    }
    return adj
}

/**
 * Build adjacency list from edge list (directed graph).
 */
fun buildDirectedGraph(n: Int, edges: List<List<Int>>): List<MutableList<Int>> {
    val adj = List(n) { mutableListOf<Int>() }
    for (edge in edges) {
        adj[edge[0]].add(edge[1])
    }
    return adj
}

/**
 * Build weighted adjacency list from edge list (directed, weighted).
 * edges = list of [u, v, weight]
 */
fun buildWeightedGraph(n: Int, edges: List<List<Int>>): List<MutableList<Pair<Int, Int>>> {
    val adj = List(n) { mutableListOf<Pair<Int, Int>>() }
    for (edge in edges) {
        adj[edge[0]].add(Pair(edge[1], edge[2]))
    }
    return adj
}
