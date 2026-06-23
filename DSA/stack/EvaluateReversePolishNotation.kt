package stack

/**
 * https://leetcode.com/problems/evaluate-reverse-polish-notation/
 * Evaluate the value of an arithmetic expression in Reverse Polish Notation.
 * Example: ["2","1","+","3","*"] → 9 ((2+1)*3)
 *          ["4","13","5","/","+"] → 6 (4+(13/5))
 * FAANG Importance: ⭐⭐⭐⭐ (Classic stack evaluation)
 */

fun main() {
    println(evalRPM(arrayOf("2", "1", "+", "3", "*")))
    println(evalRPM(arrayOf("4", "13", "5", "/", "+")))
    println(evalRPM(arrayOf("10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+")))
}

/**
 * OPTIMAL: O(N) Stack
 * Push numbers. On operator, pop two operands, compute, push result.
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
