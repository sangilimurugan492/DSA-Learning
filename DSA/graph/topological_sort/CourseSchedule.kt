package graph.topological_sort

/**
 * https://leetcode.com/problems/course-schedule/
 * Determine if you can finish all courses given prerequisites (detect cycle in DAG).
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Topological sort / Cycle detection — must know)
 */

fun main() {
    // Course 0 requires course 1, course 1 requires course 2 → valid
    println(canFinishDFS(3, arrayOf(intArrayOf(0, 1), intArrayOf(1, 2))))  // true

    // Cycle: 0→1→0 → invalid
    println(canFinishDFS(2, arrayOf(intArrayOf(0, 1), intArrayOf(1, 0))))  // false
}

/**
 * DFS Cycle Detection: O(V+E) time, O(V+E) space
 * 3 states: 0=unvisited, 1=visiting (in current path), 2=visited (done)
 * If we encounter a "visiting" node during DFS → cycle detected.
 */
fun canFinishDFS(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
    val adj = List(numCourses) { mutableListOf<Int>() }
    for (pre in prerequisites) adj[pre[1]].add(pre[0])

    val state = IntArray(numCourses)  // 0=unvisited, 1=visiting, 2=visited

    fun hasCycle(node: Int): Boolean {
        if (state[node] == 1) return true   // Cycle!
        if (state[node] == 2) return false   // Already processed
        state[node] = 1  // Mark as visiting
        for (neighbor in adj[node]) {
            if (hasCycle(neighbor)) return true
        }
        state[node] = 2  // Mark as visited
        return false
    }

    for (i in 0 until numCourses) {
        if (state[i] == 0 && hasCycle(i)) return false
    }
    return true
}

/**
 * BFS Kahn's Algorithm (Topological Sort): O(V+E) time, O(V+E) space
 * Process nodes with indegree 0. If all nodes processed → no cycle.
 */
fun canFinishBFS(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
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

    var processed = 0
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        processed++
        for (neighbor in adj[node]) {
            indegree[neighbor]--
            if (indegree[neighbor] == 0) queue.addLast(neighbor)
        }
    }
    return processed == numCourses
}
