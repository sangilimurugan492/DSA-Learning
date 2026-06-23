package array.greedy


/**
 * #455
 * https://leetcode.com/problems/assign-cookies/?envType=problem-list-v2&envId=greedy
 * Assume you are an awesome parent and want to give your children some cookies. But, you should give each child at most one cookie.
 *
 * Each child i has a greed factor g[i], which is the minimum size of a cookie that the child will be content with; and each cookie j has a size s[j]. If s[j] >= g[i], we can assign the cookie j to the child i, and the child i will be content. Your goal is to maximize the number of your content children and output the maximum number.
 *
 * Example 1:
 *
 * Input: g = [1,2,3], s = [1,1]
 * Output: 1
 * Explanation: You have 3 children and 2 cookies. The greed factors of 3 children are 1, 2, 3.
 * And even though you have 2 cookies, since their size is both 1, you could only make the child whose greed factor is 1 content.
 * You need to output 1.
 *
 * Feature     Brute Force   Optimal (Greedy)
 * Technique  Nested Loops  Two Pointers
 * Time         $O(N * M)   O(N log N + M log M)
 */

fun main() {
    println(findContentChildrenBF(intArrayOf(1,2,3), intArrayOf(1,1)))
    println(findContentChildren(intArrayOf(1,2,3), intArrayOf(1,1)))
}

fun findContentChildrenBF(g: IntArray, s: IntArray): Int {
    g.sort()
    s.sort()

    var contentChildren = 0
    val usedCookie = BooleanArray(s.size) { false }

    // For every child, look for the smallest available cookie that fits
    for (i in g.indices) {
        for (j in s.indices) {
            if (!usedCookie[j] && s[j] >= g[i]) {
                contentChildren++
                usedCookie[j] = true // Mark cookie as used
                break // Move to the next child
            }
        }
    }
    return contentChildren
}

fun findContentChildren(g: IntArray, s: IntArray): Int {
    g.sort()
    s.sort()
    var childPointer = 0
    var cookiePointer = 0

    // Iterate until we run out of children or cookies
    while (childPointer < g.size && cookiePointer < s.size) {
        // If the current cookie satisfies the current child
        if (s[cookiePointer] >= g[childPointer]) {
            childPointer++ // Satisfied! Move to the next child
        }
        // Always move to the next cookie, whether it was used or was too small
        cookiePointer++
    }

    return childPointer
}