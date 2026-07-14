package stack.min_stack

/**
 * Min Stack — LeetCode #155
 * https://leetcode.com/problems/min-stack/
 *
 * Problem:
 * -------
 * Design a stack that supports push, pop, top, and getMin — all in O(1) time.
 *
 * Example:  push(-2), push(0), push(-3) → getMin()=-3, pop(), top()=0, getMin()=-2
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 most asked design)
 *
 * Two approaches:
 * 1. Brute Force: O(N) getMin — scan entire stack
 * 2. Auxiliary Stack: O(1) getMin — store (value, currentMin) pairs
 */

fun main() {
    println("=== Method 1: Brute Force (O(N) getMin) ===")
    val bfStack = MinStackBruteForce()
    bfStack.push(-2)
    bfStack.push(0)
    bfStack.push(-3)
    println("getMin: ${bfStack.getMin()}")  // -3
    bfStack.pop()
    println("top: ${bfStack.top()}")        // 0
    println("getMin: ${bfStack.getMin()}")  // -2

    println("\n=== Method 2: Auxiliary Stack (O(1) getMin) ===")
    val stack = MinStack()
    stack.push(-2)
    stack.push(0)
    stack.push(-3)
    println("getMin: ${stack.getMin()}")  // -3
    stack.pop()
    println("top: ${stack.top()}")        // 0
    println("getMin: ${stack.getMin()}")  // -2
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N) getMin
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — getMin scans entire stack to find minimum.
 *
 * Time Complexity:  push O(1), pop O(1), top O(1), getMin O(N).
 * Space Complexity: O(N).
 */
class MinStackBruteForce {
    private val stack = ArrayDeque<Int>()

    fun push(value: Int) = stack.addLast(value)
    fun pop() = stack.removeLast()
    fun top(): Int = stack.last()

    fun getMin(): Int = stack.minOrNull() ?: throw NoSuchElementException()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: AUXILIARY STACK — O(1) getMin
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * AUXILIARY STACK — Store pairs of (value, currentMin) in the stack.
 *
 * Core Idea:
 *   - Each entry remembers the minimum at the time it was pushed.
 *   - getMin() just reads the min from the top entry.
 *
 * Key Insight:
 *   - When pushing, compare new value with current min (top's min).
 *   - Store min(new value, current min) alongside the value.
 *   - This way, every stack level knows the minimum up to that point.
 *
 * Time Complexity:  O(1) for all operations.
 * Space Complexity: O(N) — pairs take 2× space.
 */
class MinStack {
    private val stack = ArrayDeque<Pair<Int, Int>>()  // (value, minAtThisPoint)

    fun push(value: Int) {
        val min = if (stack.isEmpty()) value else minOf(value, stack.last().second)
        stack.addLast(Pair(value, min))
    }

    fun pop() = stack.removeLast()

    fun top(): Int = stack.last().first

    fun getMin(): Int = stack.last().second
}
