package stack.minimum_parentheses_to_remove

/**
 * Minimum Remove to Make Valid Parentheses — LeetCode #1249
 * https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/
 *
 * Problem:
 * -------
 * Given a string s of '(' , ')' and lowercase English characters, remove the minimum
 * number of parentheses to make the input string valid. Return any valid string.
 *
 * Example:  "lee(t(c)o)de)" → "lee(t(c)o)de"  (remove last ')')
 *           "a)b(c)d" → "ab(c)d"  (remove first ')')
 *           "))((" → ""  (remove all)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — repeatedly check and remove invalid
 * 2. Stack with Marking: O(N) — mark invalid indices, build result
 */

fun main() {
    val s1 = "lee(t(c)o)de)"
    val s2 = "a)b(c)d"
    val s3 = "))(("

    println("=== Method 1: Brute Force ===")
    println("minRemoveToMakeValid(\"$s1\") = \"${minRemoveToMakeValidBruteForce(s1)}\"")
    println("minRemoveToMakeValid(\"$s2\") = \"${minRemoveToMakeValidBruteForce(s2)}\"")

    println("\n=== Method 2: Stack with Marking ===")
    println("minRemoveToMakeValid(\"$s1\") = \"${minRemoveToMakeValid(s1)}\"")
    println("minRemoveToMakeValid(\"$s2\") = \"${minRemoveToMakeValid(s2)}\"")
    println("minRemoveToMakeValid(\"$s3\") = \"${minRemoveToMakeValid(s3)}\"")

    println("\n=== Step-by-step trace ===")
    minRemoveToMakeValidTrace(s1)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Count open/close. Remove extra ')' from left, extra '(' from right.
 *
 * Time Complexity:  O(N²) — string building.
 * Space Complexity: O(N).
 */
fun minRemoveToMakeValidBruteForce(s: String): String {
    // First pass: remove extra ')'.
    val sb = StringBuilder()
    var open = 0
    for (c in s) {
        if (c == '(') {
            open++
            sb.append(c)
        } else if (c == ')') {
            if (open > 0) {
                open--
                sb.append(c)
            }
            // Skip extra ')'.
        } else {
            sb.append(c)
        }
    }

    // Second pass: remove extra '(' from the right.
    var result = sb.toString()
    while (open > 0) {
        val lastOpen = result.lastIndexOf('(')
        result = result.removeRange(lastOpen, lastOpen + 1)
        open--
    }
    return result
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: STACK WITH MARKING — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * STACK WITH MARKING — Use stack to track '(' indices. Mark unmatched for removal.
 *
 * Core Idea:
 *   - Push index of '(' onto stack.
 *   - On ')': if stack not empty → pop (matched). If empty → mark this ')' for removal.
 *   - After processing: any remaining '(' in stack are unmatched → mark for removal.
 *   - Build result excluding marked indices.
 *
 * Key Insight:
 *   - Unmatched ')' are found during the pass (stack empty when ')' arrives).
 *   - Unmatched '(' are found after the pass (still in stack).
 *
 * Time Complexity:  O(N) — single pass + build.
 * Space Complexity: O(N) — stack + set.
 */
fun minRemoveToMakeValid(s: String): String {
    val stack = ArrayDeque<Int>()  // indices of '('
    val toRemove = mutableSetOf<Int>()

    for (i in s.indices) {
        when (s[i]) {
            '(' -> stack.addLast(i)
            ')' -> {
                if (stack.isNotEmpty()) {
                    stack.removeLast()  // Matched.
                } else {
                    toRemove.add(i)  // Extra ')'.
                }
            }
        }
    }

    // Remaining '(' in stack are unmatched.
    toRemove.addAll(stack)

    return s.filterIndexed { i, _ -> i !in toRemove }
}

/**
 * Stack with marking trace.
 */
fun minRemoveToMakeValidTrace(s: String) {
    println("Input: \"$s\"")
    val stack = ArrayDeque<Int>()
    val toRemove = mutableSetOf<Int>()

    for (i in s.indices) {
        when (s[i]) {
            '(' -> {
                stack.addLast(i)
                println("  i=$i: '(' → push $i. stack=$stack")
            }
            ')' -> {
                if (stack.isNotEmpty()) {
                    stack.removeLast()
                    println("  i=$i: ')' → pop (matched). stack=$stack")
                } else {
                    toRemove.add(i)
                    println("  i=$i: ')' → extra! mark for removal. toRemove=$toRemove")
                }
            }
            else -> {}
        }
    }
    toRemove.addAll(stack)
    println("  Unmatched '(' in stack: $stack → add to toRemove=$toRemove")
    val result = s.filterIndexed { i, _ -> i !in toRemove }
    println("  Result: \"$result\"")
}
