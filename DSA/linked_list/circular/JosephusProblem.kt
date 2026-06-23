package linked_list.circular

/**
 * https://en.wikipedia.org/wiki/Josephus_problem
 * Josephus Problem: N people stand in a circle, every Kth person is eliminated.
 * Find the last person standing.
 * Example: N=5, K=2 → Position 3 survives
 * FAANG Importance: ⭐⭐⭐⭐ (Classic circular LL application, math + simulation)
 */

fun main() {
    println("Josephus N=5, K=2: ${josephusMath(5, 2)}")   // 3
    println("Josephus N=7, K=3: ${josephusMath(7, 3)}")   // 4
    println("Josephus N=40, K=2: ${josephusMath(40, 2)}") // 17

    println("\nSimulation approach:")
    println("Josephus N=5, K=2: ${josephusSimulation(5, 2)}")
}

/**
 * MATHEMATICAL: O(N) time, O(1) space
 * Recurrence: J(1,k) = 0, J(n,k) = (J(n-1,k) + k) % n
 * Result is 0-indexed, add 1 for 1-indexed position.
 */
fun josephusMath(n: Int, k: Int): Int {
    var result = 0
    for (i in 2..n) {
        result = (result + k) % i
    }
    return result + 1  // 1-indexed
}

/**
 * SIMULATION using Circular Linked List: O(N*K) time, O(N) space
 * Build a circular LL, eliminate every Kth node until one remains.
 */
fun josephusSimulation(n: Int, k: Int): Int {
    if (n == 1) return 1

    // Build circular linked list
    class JNode(val pos: Int) { var next: JNode? = null }

    val head = JNode(1)
    var prev: JNode = head
    for (i in 2..n) {
        val node = JNode(i)
        prev.next = node
        prev = node
    }
    prev.next = head  // Make it circular

    // Eliminate every Kth person
    var curr: JNode = head
    while (curr.next != curr) {
        // Move K-1 steps (we count current as 1)
        for (i in 1 until k - 1) {
            curr = curr.next!!
        }
        // Eliminate the next person
        curr.next = curr.next!!.next
        curr = curr.next!!
    }

    return curr.pos
}
