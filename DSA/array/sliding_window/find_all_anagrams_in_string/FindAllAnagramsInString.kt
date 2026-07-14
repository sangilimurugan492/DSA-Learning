package array.sliding_window.find_all_anagrams_in_string

/**
 * https://leetcode.com/problems/find-all-anagrams-in-a-string/
 * Given strings s and p, return start indices of p's anagrams in s.
 * Example: s = "cbaebabacd", p = "abc" → [0,6]
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(findAnagramsBruteForce("cbaebabacd", "abc"))
    println(findAnagramsBruteForce("abab", "ab"))
    println("---")
    println(findAnagramsSlidingWindow("cbaebabacd", "abc"))
    println(findAnagramsSlidingWindow("abab", "ab"))
}

/**
 * BRUTE FORCE: O(N × M log M) — check every substring of length M
 * Sort each substring and compare with sorted p.
 */
fun findAnagramsBruteForce(s: String, p: String): List<Int> {
    if (s.length < p.length) return emptyList()
    val sortedP = p.toCharArray().sorted().joinToString("")
    val result = mutableListOf<Int>()

    for (i in 0..s.length - p.length) {
        val sub = s.substring(i, i + p.length).toCharArray().sorted().joinToString("")
        if (sub == sortedP) result.add(i)
    }
    return result
}

/**
 * OPTIMAL: O(N) Sliding Window with frequency match
 * Fixed-size window = len(p). Track matching character counts.
 */
fun findAnagramsSlidingWindow(s: String, p: String): List<Int> {
    if (s.length < p.length) return emptyList()
    val result = mutableListOf<Int>()
    val pCount = IntArray(26)
    val sCount = IntArray(26)

    for (c in p) pCount[c - 'a']++

    for (i in s.indices) {
        sCount[s[i] - 'a']++
        if (i >= p.length) sCount[s[i - p.length] - 'a']--
        if (sCount.contentEquals(pCount)) result.add(i - p.length + 1)
    }
    return result
}
