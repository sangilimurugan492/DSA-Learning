package array.two_pointer.longest_repeating_charcter_replacement

/**
 * https://leetcode.com/problems/longest-repeating-character-replacement/description/
 *
 * You are given a string s and an integer k. You can choose any character of the string and change it to any other uppercase English character. You can perform this operation at most k times.
 *
 * Return the length of the longest substring containing the same letter you can get after performing the above operations.
 *
 *
 *
 * Example 1:
 *
 * Input: s = "ABAB", k = 2
 * Output: 4
 * Explanation: Replace the two 'A's with two 'B's or vice versa.
 * Example 2:
 *
 * Input: s = "AABABBA", k = 1
 * Output: 4
 * Explanation: Replace the one 'A' in the middle with 'B' and form "AABBBBA".
 * The substring "BBBB" has the longest repeating letters, which is 4.
 * There may exists other ways to achieve this answer too.
 */
fun main() {
    println(characterReplacementBruteForce("ABAB", 2))
    println(characterReplacement("ABAB", 2))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 * LeetCode Result - Time Limit Exceeded TLE
 */
fun characterReplacementBruteForce(s: String, k: Int): Int {
    var maxLen = 0
    val n = s.length

    for (i in 0 until n) {
        val counts = IntArray(26)
        var maxFreq = 0
        for (j in i until n) {
            val charIndex = s[j] - 'A'
            counts[charIndex]++
            maxFreq = maxOf(maxFreq, counts[charIndex])

            // Substring length is (j - i + 1)
            val replacementsNeeded = (j - i + 1) - maxFreq

            if (replacementsNeeded <= k) {
                maxLen = maxOf(maxLen, j - i + 1)
            } else {
                break // Too many replacements needed for this start point
            }
        }
    }
    return maxLen
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun characterReplacement(s: String, k: Int): Int {
    val counts = IntArray(26)
    var left = 0
    var maxFreq = 0
    var maxLength = 0

    for (right in s.indices) {
        // Update frequency of current character
        val charIndex = s[right] - 'A'
        counts[charIndex]++

        // maxFreq is the count of the most frequent character in current window
        maxFreq = maxOf(maxFreq, counts[charIndex])

        // If replacements needed > k, shrink window from left
        val windowSize = right - left + 1
        if (windowSize - maxFreq > k) {
            counts[s[left] - 'A']--
            left++
        }

        // Update max length (the window only grows or stays same size in this version)
        maxLength = maxOf(maxLength, right - left + 1)
    }

    return maxLength
}