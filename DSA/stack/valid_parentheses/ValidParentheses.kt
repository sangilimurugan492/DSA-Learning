package stack.valid_parentheses

/**
 * Valid Parentheses — LeetCode #20
 * https://leetcode.com/problems/valid-parentheses/
 *
 * Problem:
 * -------
 * Given a string s containing '(', ')', '{', '}', '[', ']', determine if it's valid.
 * Valid if: open brackets closed by same type, in correct order, every close has matching open.
 *
 * Example:  "()" → true,  "()[]{}" → true,  "(]" → false,  "[" → false
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Most asked stack problem)
 *
 * Two approaches:
 * 1. Stack with HashMap: O(N) — map open→close, push open, pop on close
 * 2. CharArray as Stack: O(N) — use array with top pointer (no Stack object)
 */

fun main() {
    val test1 = "()[]{}"
    val test2 = "(]"
    val test3 = "["

    println("=== Method 1: Stack with HashMap ===")
    println("isValid(\"$test1\") = ${isValid(test1)}")
    println("isValid(\"$test2\") = ${isValid(test2)}")
    println("isValid(\"$test3\") = ${isValid(test3)}")

    println("\n=== Method 2: CharArray as Stack ===")
    println("isValidOP(\"$test1\") = ${isValidOP(test1)}")
    println("isValidOP(\"$test2\") = ${isValidOP(test2)}")
    println("isValidOP(\"$test3\") = ${isValidOP(test3)}")

    println("\n=== Step-by-step trace ===")
    isValidTrace("()[]{}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: STACK WITH HASHMAP — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * STACK WITH HASHMAP — Push open brackets. On close bracket, pop and check match.
 *
 * Core Idea:
 *   - Map: '(' → ')', '{' → '}', '[' → ']'.
 *   - If char is open → push to stack.
 *   - If char is close → pop stack, check if map[popped] == char. If not → invalid.
 *   - At end, stack must be empty.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(N) — stack.
 */
fun isValid(s: String): Boolean {
    val map = mapOf('(' to ')', '{' to '}', '[' to ']')
    val stack = java.util.Stack<Char>()

    for (c in s) {
        if (map.containsKey(c)) {
            stack.push(c)
        } else if (stack.isEmpty() || map[stack.pop()] != c) {
            return false
        }
    }
    return stack.isEmpty()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: CHARARRAY AS STACK — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * CHARARRAY AS STACK — Use a char array with a top pointer. Avoids Stack object overhead.
 *
 * Core Idea:
 *   - Pre-allocate char array of size s.length.
 *   - top = -1 (empty). Push: stack[++top] = c. Pop: stack[top--].
 *   - On close bracket, check if top matches.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(N) — char array.
 */
fun isValidOP(s: String): Boolean {
    val stack = CharArray(s.length)
    var top = -1

    for (c in s) {
        when (c) {
            '(', '{', '[' -> stack[++top] = c
            ')' -> if (top < 0 || stack[top--] != '(') return false
            '}' -> if (top < 0 || stack[top--] != '{') return false
            ']' -> if (top < 0 || stack[top--] != '[') return false
        }
    }
    return top == -1
}

/**
 * Stack with HashMap trace.
 */
fun isValidTrace(s: String) {
    println("Input: \"$s\"")
    val map = mapOf('(' to ')', '{' to '}', '[' to ']')
    val stack = java.util.Stack<Char>()

    for (c in s) {
        if (map.containsKey(c)) {
            stack.push(c)
            println("  '$c' is open → push. stack=$stack")
        } else {
            val top = if (stack.isEmpty()) "empty" else stack.pop().toString()
            val match = map[top.toCharArray().firstOrNull() ?: ' '] ?: c
            println("  '$c' is close → pop '$top', expected '${if (top != "empty") map[top.toCharArray().first()] else "—"}'")
            if (top == "empty" || map[top.toCharArray().first()] != c) {
                println("  Mismatch! → false")
                return
            }
        }
    }
    println("  Stack empty? ${stack.isEmpty()} → ${stack.isEmpty()}")
}
