package stack.longest_valid_parentheses

/**
 * Longest Valid Parentheses — LeetCode #32
 * https://leetcode.com/problems/longest-valid-parentheses/
 *
 * Problem:
 * -------
 * Given a string of '(' and ')', find the length of the longest valid (well-formed)
 * parentheses substring.
 *
 * Example:  "(()" → 2  ("()")
 *           ")()())" → 4  ("()()")
 *           "" → 0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard stack problem)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — check all substrings
 * 2. Stack with Index: O(N) — push indices, track boundaries
 */

fun main() {
    val s = ")()())"

    println("=== Method 1: Brute Force ===")
    println("longestValidParentheses(\"$s\") = ${longestValidParenthesesBruteForce(s)}")

    println("\n=== Method 2: Stack with Index ===")
    println("longestValidParentheses(\"$s\") = ${longestValidParentheses(s)}")

    println("\n=== Step-by-step trace ===")
    longestValidParenthesesTrace(s)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Check every substring. For each, verify if valid parentheses.
 *
 * Time Complexity:  O(N²) — check all substrings.
 * Space Complexity: O(1).
 */
fun longestValidParenthesesBruteForce(s: String): Int {
    var maxLen = 0
    for (i in s.indices) {
        var count = 0
        for (j in i until s.length) {
            if (s[j] == '(') count++
            else count--
            if (count < 0) break  // More ')' than '(' → invalid.
            if (count == 0) maxLen = maxOf(maxLen, j - i + 1)
        }
    }
    return maxLen
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: STACK WITH INDEX — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * STACK WITH INDEX — Push indices of '(' onto stack. On ')', pop and calculate length.
 *
 * Core Idea:
 *   - Push -1 initially (base for length calculation).
 *   - On '(': push its index.
 *   - On ')': pop. If stack empty → push current index (new boundary).
 *             If stack not empty → length = i - stack.top(). Update max.
 *
 * Key Insight:
 *   - Stack stores the index of the last unmatched character.
 *   - When we pop a '(' and the stack isn't empty, the distance from current index
 *     to the new top gives the length of a valid substring.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(N) — stack.
 */
fun longestValidParentheses(s: String): Int {
    val stack = java.util.Stack<Int>()
    stack.push(-1)  // Base index for length calculation.
    var maxLength = 0

    for (i in s.indices) {
        if (s[i] == '(') {
            stack.push(i)
        } else {
            stack.pop()
            if (stack.isEmpty()) {
                stack.push(i)  // New boundary — no matching '('.
            } else {
                maxLength = maxOf(maxLength, i - stack.peek())
            }
        }
    }
    return maxLength
}

/**
 * Stack with index trace.
 */
fun longestValidParenthesesTrace(s: String) {
    println("Input: \"$s\"")
    val stack = java.util.Stack<Int>()
    stack.push(-1)
    var maxLength = 0

    for (i in s.indices) {
        if (s[i] == '(') {
            stack.push(i)
            println("  i=$i: '(' → push $i. stack=$stack")
        } else {
            stack.pop()
            if (stack.isEmpty()) {
                stack.push(i)
                println("  i=$i: ')' → pop, stack empty → push $i (boundary). stack=$stack")
            } else {
                val len = i - stack.peek()
                maxLength = maxOf(maxLength, len)
                println("  i=$i: ')' → pop, length=$i-${stack.peek()}=$len, maxLen=$maxLength")
            }
        }
    }
    println("  Result: $maxLength")
}
