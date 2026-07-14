package stack.evaluate_reverse_polish_notation

/**
 * Evaluate Reverse Polish Notation — LeetCode #150
 * https://leetcode.com/problems/evaluate-reverse-polish-notation/
 *
 * Problem:
 * -------
 * Evaluate the value of an arithmetic expression in Reverse Polish Notation.
 *
 * Example:  ["2","1","+","3","*"] → 9  ((2+1)*3)
 *           ["4","13","5","/","+"] → 6  (4+(13/5))
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Classic stack evaluation)
 *
 * Two approaches:
 * 1. Recursive: O(N) — parse expression tree recursively
 * 2. Stack: O(N) — push numbers, pop on operator, push result
 */

fun main() {
    println("=== Method: Stack ===")
    println("evalRPN([2,1,+,3,*]) = ${evalRPM(arrayOf("2", "1", "+", "3", "*"))}")
    println("evalRPN([4,13,5,/,+]) = ${evalRPM(arrayOf("4", "13", "5", "/", "+"))}")
    println("evalRPN([10,6,9,3,+,-11,*,/,*,17,+,5,+]) = ${evalRPM(arrayOf("10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"))}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD: STACK — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * STACK — Push numbers. On operator, pop two operands, compute, push result.
 *
 * Core Idea:
 *   - RPN is postfix notation: operands come before operators.
 *   - When we see an operator, the two most recent numbers are the operands.
 *   - Stack naturally gives us the most recent (LIFO).
 *
 * Key Insight:
 *   - RPN eliminates need for parentheses — the order is implicit.
 *   - Stack perfectly models this: push operands, pop on operator.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(N) — stack.
 */
fun evalRPM(tokens: Array<String>): Int {
    val stack = ArrayDeque<Int>()
    for (token in tokens) {
        when (token) {
            "+" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a + b) }
            "-" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a - b) }
            "*" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a * b) }
            "/" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a / b) }
            else -> stack.addLast(token.toInt())
        }
    }
    return stack.last()
}
