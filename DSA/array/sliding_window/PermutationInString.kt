package array.sliding_window

/**
 * https://leetcode.com/problems/permutation-in-string/
 * Given strings s1 and s2, return true if s2 contains a permutation of s1.
 * Example: s1 = "ab", s2 = "eidbaooo" → true ("ba" at index 3)
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(checkInclusionBruteForce("ab", "eidbaooo"))
    println(checkInclusionBruteForce("ab", "eidboaoo"))
    println("---")
    println(checkInclusionSlidingWindow("ab", "eidbaooo"))
    println(checkInclusionSlidingWindow("ab", "eidboaoo"))
}

/**
 * BRUTE FORCE: O(N × M log M) — check every substring of length M
 * Sort each substring and compare with sorted s1.
 */
fun checkInclusionBruteForce(s1: String, s2: String): Boolean {
    if (s2.length < s1.length) return false
    val sortedS1 = s1.toCharArray().sorted().joinToString("")

    for (i in 0..s2.length - s1.length) {
        val sub = s2.substring(i, i + s1.length).toCharArray().sorted().joinToString("")
        if (sub == sortedS1) return true
    }
    return false
}

/**
 * OPTIMAL: O(N) Sliding Window with frequency match
 * Same pattern as Find All Anagrams. Fixed window = len(s1).
 */
fun checkInclusionSlidingWindow(s1: String, s2: String): Boolean {
    if (s2.length < s1.length) return false
    val s1Count = IntArray(26)
    val s2Count = IntArray(26)

    for (c in s1) s1Count[c - 'a']++

    for (i in s2.indices) {
        s2Count[s2[i] - 'a']++
        if (i >= s1.length) s2Count[s2[i - s1.length] - 'a']--
        if (s2Count.contentEquals(s1Count)) return true
    }
    return false
}
