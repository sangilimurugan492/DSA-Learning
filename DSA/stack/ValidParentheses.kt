package stack

import java.util.Stack

/**
 * Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.
 *
 * An input string is valid if:
 *
 * Open brackets must be closed by the same type of brackets.
 * Open brackets must be closed in the correct order.
 * Every close bracket has a corresponding open bracket of the same type.
 *
 *
 * Example 1:
 *
 * Input: s = "()"
 *
 * Output: true
 *
 * Example 2:
 *
 * Input: s = "()[]{}"
 *
 * Output: true
 *
 * Example 3:
 *
 * Input: s = "(]"
 *
 * Output: false
 */
fun main() {
 println(isValid("()[]{}[](]"))
 println(isValid("()"))
 println(isValid("["))
}

fun isValid(s: String): Boolean {

    val parenthesesMap = mapOf('(' to ')', '{' to '}', '[' to ']')

    val stack = Stack<Char>()

    for(i in s.indices) {
        if (parenthesesMap.keys.contains(s[i])) {
            stack.push(s[i])
        } else if (stack.isEmpty() || parenthesesMap[stack.pop()] != s[i]) {
            return false
        }
    }
    return stack.isEmpty()
}

fun isValidOP(s: String): Boolean {
    val stack = CharArray(s.length)
    var top = -1

    for (c in s) {
        when (c) {
            '(', '{', '['  -> stack[++top] = c
            ')' -> if (top < 0 || stack[top--] != '(') return false
            '}' -> if (top < 0 || stack[top--] != '{') return false
            ']' -> if (top < 0 || stack[top--] != '[') return false
        }
    }

    return top == -1
}