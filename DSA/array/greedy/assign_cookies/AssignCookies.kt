package array.greedy.assign_cookies

/**
 * Assign Cookies — LeetCode #455
 * https://leetcode.com/problems/assign-cookies/
 *
 * Problem:
 * -------
 * Each child i has a greed factor g[i] (minimum cookie size to be content).
 * Each cookie j has a size s[j]. If s[j] >= g[i], child i is content.
 * Maximize the number of content children. Each child gets at most one cookie.
 *
 * Example:  g = [1,2,3], s = [1,1]  →  1
 *           g = [1,2], s = [1,2,3]  →  2
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Classic Greedy)
 *
 * Two approaches:
 * 1. Brute Force: O(N × M) — nested loops with used array
 * 2. Two Pointers: O(N log N + M log M) — sort both, assign greedily
 */

fun main() {
    val g = intArrayOf(1, 2, 3)
    val s = intArrayOf(1, 1)

    println("=== Method 1: Brute Force ===")
    println("findContentChildren(${g.toList()}, ${s.toList()}) = ${findContentChildrenBF(g, s)}")

    println("\n=== Method 2: Two Pointers ===")
    println("findContentChildren(${g.toList()}, ${s.toList()}) = ${findContentChildren(g, s)}")

    println("\n=== Step-by-step trace ===")
    findContentChildrenTrace(g, s)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N × M)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Sort both. For each child, find the smallest available cookie that fits.
 *
 * Core Idea:
 *   - Sort g and s.
 *   - For each child, scan all cookies for the smallest unused one that satisfies greed.
 *   - Mark cookie as used.
 *
 * Time Complexity:  O(N × M) — nested loops.
 * Space Complexity: O(M) — used array.
 */
fun findContentChildrenBF(g: IntArray, s: IntArray): Int {
    g.sort()
    s.sort()

    var contentChildren = 0
    val usedCookie = BooleanArray(s.size) { false }

    for (i in g.indices) {
        for (j in s.indices) {
            if (!usedCookie[j] && s[j] >= g[i]) {
                contentChildren++
                usedCookie[j] = true
                break
            }
        }
    }
    return contentChildren
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: TWO POINTERS — O(N log N + M log M)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * TWO POINTERS — Sort both. Use two pointers: one for children, one for cookies.
 *
 * Core Idea:
 *   - Sort g and s.
 *   - If current cookie satisfies current child → move both pointers.
 *   - If cookie too small → move to next cookie.
 *   - Answer = number of children satisfied (childPointer).
 *
 * Key Insight:
 *   - Assign the smallest sufficient cookie to the least greedy child first.
 *   - This greedy choice maximizes the number of content children.
 *
 * Time Complexity:  O(N log N + M log M) — sorting dominates.
 * Space Complexity: O(1) — two pointers.
 */
fun findContentChildren(g: IntArray, s: IntArray): Int {
    g.sort()
    s.sort()
    var childPointer = 0
    var cookiePointer = 0

    while (childPointer < g.size && cookiePointer < s.size) {
        if (s[cookiePointer] >= g[childPointer]) {
            childPointer++  // Satisfied! Move to next child.
        }
        cookiePointer++  // Always move to next cookie.
    }
    return childPointer
}

/**
 * Two pointers with step-by-step trace.
 */
fun findContentChildrenTrace(g: IntArray, s: IntArray) {
    g.sort(); s.sort()
    println("Sorted g: ${g.toList()}, s: ${s.toList()}")
    var childPointer = 0
    var cookiePointer = 0

    while (childPointer < g.size && cookiePointer < s.size) {
        if (s[cookiePointer] >= g[childPointer]) {
            println("  cookie[$cookiePointer]=${s[cookiePointer]} >= g[$childPointer]=${g[childPointer]} → content! child++")
            childPointer++
        } else {
            println("  cookie[$cookiePointer]=${s[cookiePointer]} < g[$childPointer]=${g[childPointer]} → too small, cookie++")
        }
        cookiePointer++
    }
    println("  Result: $childPointer")
}
