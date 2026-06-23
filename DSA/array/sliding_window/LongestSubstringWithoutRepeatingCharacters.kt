package array.sliding_window

/**
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 * Given string s, find length of longest substring without repeating characters.
 * Example: "abcabcbb" → 3 ("abc") | "bbbbb" → 1 | "pwwkew" → 3 ("wke")
 * FAANG Importance: ⭐⭐⭐⭐⭐ (#1 most asked sliding window)
 */

fun main() {
    println(lengthOfLongestSubstringBruteForce("abcabcbb"))
    println(lengthOfLongestSubstringBruteForce("bbbbb"))
    println(lengthOfLongestSubstringBruteForce("pwwkew"))
    println("---")
    println(lengthOfLongestSubstringSlidingWindow("abcabcbb"))
    println(lengthOfLongestSubstringSlidingWindow("bbbbb"))
    println(lengthOfLongestSubstringSlidingWindow("pwwkew"))
}

/**
 * BRUTE FORCE: O(N²) — check every substring
 * For each starting index, extend until duplicate found.
 */
fun lengthOfLongestSubstringBruteForce(s: String): Int {
    var maxLen = 0
    for (i in s.indices) {
        val seen = mutableSetOf<Char>()
        for (j in i until s.length) {
            if (s[j] in seen) break
            seen.add(s[j])
            maxLen = maxOf(maxLen, j - i + 1)
        }
    }
    return maxLen
}

/**
 * OPTIMAL: O(N) Sliding Window with HashMap
 * Jump left directly to lastSeen[char] + 1 instead of shrinking one-by-one.
 */
fun lengthOfLongestSubstringSlidingWindow(s: String): Int {
    val lastSeen = hashMapOf<Char, Int>()
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        if (s[right] in lastSeen && lastSeen[s[right]]!! >= left) {
            left = lastSeen[s[right]]!! + 1
        }
        lastSeen[s[right]] = right
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
