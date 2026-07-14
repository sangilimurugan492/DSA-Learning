package array.sliding_window.longest_repeating_character_replacement

/**
 * https://leetcode.com/problems/longest-repeating-character-replacement/
 * Given string s and integer k, find longest substring after at most k replacements.
 * Example: s = "AABABBA", k = 1 → Output: 4 ("AABA" or "ABBA")
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(characterReplacementBruteForce("AABABBA", 1))
    println(characterReplacementBruteForce("ABAB", 2))
    println("---")
    println(characterReplacementSlidingWindow("AABABBA", 1))
    println(characterReplacementSlidingWindow("ABAB", 2))
}

/**
 * BRUTE FORCE: O(N² × 26) — check every substring
 * For each substring, check if (length - maxFreq) <= k
 */
fun characterReplacementBruteForce(s: String, k: Int): Int {
    var maxLen = 0
    for (i in s.indices) {
        val count = IntArray(26)
        var maxFreq = 0
        for (j in i until s.length) {
            count[s[j] - 'A']++
            maxFreq = maxOf(maxFreq, count[s[j] - 'A'])
            val windowLen = j - i + 1
            if (windowLen - maxFreq <= k) {
                maxLen = maxOf(maxLen, windowLen)
            }
        }
    }
    return maxLen
}

/**
 * OPTIMAL: O(N) Sliding Window
 * Key formula: windowLen - maxFreq <= k
 * No need to update maxFreq when shrinking — smaller maxFreq only makes validity stricter.
 */
fun characterReplacementSlidingWindow(s: String, k: Int): Int {
    val count = IntArray(26)
    var maxFreq = 0
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        count[s[right] - 'A']++
        maxFreq = maxOf(maxFreq, count[s[right] - 'A'])

        while ((right - left + 1) - maxFreq > k) {
            count[s[left] - 'A']--
            left++
        }
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
