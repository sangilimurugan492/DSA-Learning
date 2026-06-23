package graph.topological_sort

/**
 * https://leetcode.com/problems/course-schedule-ii/
 * Return the ordering of courses you should take to finish all courses.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Topological sort — return the actual order)
 */

fun main() {
    val result = findOrder(4, arrayOf(intArrayOf(1, 0), intArrayOf(2, 0), intArrayOf(3, 1), intArrayOf(3, 2)))
    println(result.toList())  // [0, 1, 2, 3] or [0, 2, 1, 3]
}

/**
 * BFS Kahn's Algorithm: O(V+E) time, O(V+E) space
 * Return topological order. If cycle exists, return empty array.
 */
fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
    val adj = List(numCourses) { mutableListOf<Int>() }
    val indegree = IntArray(numCourses)
    for (pre in prerequisites) {
        adj[pre[1]].add(pre[0])
        indegree[pre[0]]++
    }

    val queue = ArrayDeque<Int>()
    for (i in 0 until numCourses) {
        if (indegree[i] == 0) queue.addLast(i)
    }

    val order = mutableListOf<Int>()
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        order.add(node)
        for (neighbor in adj[node]) {
            indegree[neighbor]--
            if (indegree[neighbor] == 0) queue.addLast(neighbor)
        }
    }

    return if (order.size == numCourses) order.toIntArray() else IntArray(0)
}

/**
 * DFS Topological Sort: O(V+E) time, O(V+E) space
 * Add nodes to result in reverse post-order (after processing all descendants).
 */
fun findOrderDFS(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
    val adj = List(numCourses) { mutableListOf<Int>() }
    for (pre in prerequisites) adj[pre[1]].add(pre[0])

    val state = IntArray(numCourses)
    val order = mutableListOf<Int>()
    var hasCycle = false

    fun dfs(node: Int) {
        if (hasCycle) return
        if (state[node] == 1) { hasCycle = true; return }
        if (state[node] == 2) return
        state[node] = 1
        for (neighbor in adj[node]) dfs(neighbor)
        state[node] = 2
        order.add(node)
    }

    for (i in 0 until numCourses) {
        if (state[i] == 0) dfs(i)
    }

    return if (hasCycle) IntArray(0) else order.reversed().toIntArray()
}
