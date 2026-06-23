package stack

import java.util.Stack
import kotlin.math.max

fun main() {
    println(longestValidParentheses("(()()"))
}

fun longestValidParentheses(s: String): Int {
    val stack = Stack<Int>()
    stack.push(-1) // Base index for length calculation
    var maxLength = 0

    for (i in s.indices) {
        if (s[i] == '(') {
            stack.push(i)
        } else {
            stack.pop()
            if (stack.isEmpty()) {
                // Current ')' is a boundary, no matching '('
                stack.push(i)
            } else {
                // Calculate length: current index - index of last unmatched element
                maxLength = max(maxLength, i - stack.peek())
            }
        }
    }

    return maxLength
}