package array.two_pointer

import java.util.*

/**
 * https://leetcode.com/problems/find-all-anagrams-in-a-string/description/
 *
 * Given two strings s and p, return an array of all the start indices of p's anagrams in s. You may return the answer in any order.
 *
 * Example 1:
 *
 * Input: s = "cbaebabacd", p = "abc"
 * Output: [0,6]
 * Explanation:
 * The substring with start index = 0 is "cba", which is an anagram of "abc".
 * The substring with start index = 6 is "bac", which is an anagram of "abc".
 * Example 2:
 *
 * Input: s = "abab", p = "ab"
 * Output: [0,1,2]
 * Explanation:
 * The substring with start index = 0 is "ab", which is an anagram of "ab".
 * The substring with start index = 1 is "ba", which is an anagram of "ab".
 * The substring with start index = 2 is "ab", which is an anagram of "ab".
 */
fun main() {
    findAnagramsBF("abab", "ab").forEach {
        println(it)
    }
    findAnagramsOP("abab", "ab").forEach {
        println(it)
    }
}

/**
 * Time Complexity O(M.N Log N)
 * Space Complexity O(N)
 * Leetcode result - Time Limit Exceeded
 */
fun findAnagramsBF(s: String, p: String): List<Int> {
    val result = hashSetOf<Int>()
    if(p.length > s.length) {
        return arrayListOf()
    } else {

        for (i in 0 until (s.length - (p.length -1))) {
                val current = s.substring(i, i+p.length).toCharArray().sortedArray()
                val fixed = p.toCharArray().sortedArray()
                if (current.contentEquals(fixed)) {
                    result.add(i)
                }
        }
    }

    return result.toList()
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 * Leetcode result - Time Limit Exceeded
 */
fun findAnagramsOP(s: String, p: String): List<Int> {
    val ns = s.length
    val np = p.length
    if (ns < np) return emptyList()

    val pCount = IntArray(26)
    val sCount = IntArray(26)
    val result = mutableListOf<Int>()

    // Initialize the counts for p and the first window of s
    for (i in 0 until np) {
        pCount[p[i] - 'a']++
        sCount[s[i] - 'a']++
    }

    // Check first window
    if (pCount.contentEquals(sCount)) result.add(0)

    // Slide the window
    for (i in np until ns) {
        sCount[s[i] - 'a']++          // Add new character from right
        sCount[s[i - np] - 'a']--     // Remove old character from left

        if (pCount.contentEquals(sCount)) {
            result.add(i - np + 1)
        }
    }

    return result
}