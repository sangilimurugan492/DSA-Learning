package stack

/**
 * https://leetcode.com/problems/min-stack/
 * Design a stack that supports push, pop, top, and getMin in O(1) time.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 most asked design)
 */

fun main() {
    val stack = MinStack()
    stack.push(-2)
    stack.push(0)
    stack.push(-3)
    println("getMin: ${stack.getMin()}")  // -3
    stack.pop()
    println("top: ${stack.top()}")        // 0
    println("getMin: ${stack.getMin()}")  // -2
}

/**
 * BRUTE FORCE approach (conceptual): getMin would scan entire stack O(N)
 *
 * OPTIMAL: O(1) for all operations
 * Store pairs of (value, currentMin) in the stack.
 * Each entry remembers the minimum at the time it was pushed.
 */
class MinStack {
    private val stack = ArrayDeque<Pair<Int, Int>>()  // (value, minAtThisPoint)

    fun push(value: Int) {
        val min = if (stack.isEmpty()) value else minOf(value, stack.last().second)
        stack.addLast(Pair(value, min))
    }

    fun pop() {
        stack.removeLast()
    }

    fun top(): Int = stack.last().first

    fun getMin(): Int = stack.last().second
}
