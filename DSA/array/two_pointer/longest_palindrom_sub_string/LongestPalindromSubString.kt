package array.two_pointer.longest_palindrom_sub_string

import kotlin.math.max

/**
 * https://leetcode.com/problems/longest-palindromic-substring/description/
 *
 * Given a string s, return the longest palindromic substring in s.
 *
 * Example: "babad" → "bab" (or "aba")
 *
 * Two approaches:
 * 1. Brute Force: Check every substring if it's a palindrome
 * 2. Expand Around Center: For each index, expand outward for odd/even palindromes
 */
fun main() {
    println("Brute Force:")
    println(longestPalindromeBF("babad"))   // "bab" or "aba"
    println(longestPalindromeBF("cbbd"))    // "bb"
    println("Expand Around Center:")
    println(longestPalindrome("babad"))     // "bab" or "aba"
    println(longestPalindrome("cbbd"))      // "bb"
}

/**
 * Brute Force: Check every possible substring. For each substring, verify if
 * it's a palindrome using two pointers. Track the longest one.
 *
 * Step-by-step:
 * 1. For each starting index i (0 to n-1):
 *    a. For each ending index j (i to n-1):
 *       - Extract substring s[i.. j].
 *       - Check if it's a palindrome (compare chars from both ends).
 *       - If palindrome and length > current max → update.
 * 2. Return the longest palindrome found.
 *
 * Walkthrough: s = "babad"
 *
 *   i=0: j=0 "b" ✓ len=1, j=1 "ba" ✗, j=2 "bab" ✓ len=3 maxLen=3,
 *         j=3 "baba" ✗, j=4 "babad" ✗
 *   i=1: j=1 "a" ✓, j=2 "ab" ✗, j=3 "aba" ✓ len=3 (tie), j=4 "abad" ✗
 *   i=2: j=2 "b" ✓, j=3 "ba" ✗, j=4 "bad" ✗
 *   i=3: j=3 "a" ✓, j=4 "ad" ✗
 *   i=4: j=4 "d" ✓
 *
 * Longest palindrome: "bab" (or "aba"), length 3 ✅
 *
 * Time Complexity:  O(N³) — N² substrings × O(N) palindrome check each
 * Space Complexity: O(1)
 */
fun longestPalindromeBF(s: String?): String {
    if (s.isNullOrEmpty()) return ""

    var longestStart = 0
    var longestLen = 1

    for (i in s.indices) {
        for (j in i until s.length) {
            // Check if s[i..j] is a palindrome
            if (isPalindromeBF(s, i, j)) {
                val len = j - i + 1
                if (len > longestLen) {
                    longestLen = len
                    longestStart = i
                }
            }
        }
    }

    return s.substring(longestStart, longestStart + longestLen)
}

/**
 * Helper: Check if s[left.. right] is a palindrome using two pointers.
 */
private fun isPalindromeBF(s: String, left: Int, right: Int): Boolean {
    var l = left
    var r = right
    while (l < r) {
        if (s[l] != s[r]) return false
        l++
        r--
    }
    return true
}

/**
 * Expand Around Center (Optimal): A palindrome mirrors around its center.
 * For each index i, try expanding outward for both odd-length (center = i)
 * and even-length (center between i and i+1) palindromes.
 *
 * Step-by-step:
 * 1. For each index i:
 *    a. Expand for odd-length: center = (i, i) → e.g., "aba"
 *    b. Expand for even-length: center = (i, i+1) → e.g., "abba"
 * 2. Track the longest palindrome found.
 *
 * Walkthrough: s = "babad"
 *
 *   i=0: odd expand (0,0) → "b" len=1
 *        even expand (0,1) → s[0]≠s[1] → len=0
 *   i=1: odd expand (1,1) → "a" → "bab" len=3 ← longest!
 *        even expand (1,2) → s[1]≠s[2] → len=0
 *   i=2: odd expand (2,2) → "b" → "aba" len=3 (tie)
 *        even expand (2,3) → s[2]≠s[3] → len=0
 *   i=3: odd expand (3,3) → "a" len=1
 *   i=4: odd expand (4,4) → "d" len=1
 *
 * Longest: "bab" (or "aba"), length 3 ✅
 *
 * Time Complexity:  O(N²) — N centers × O(N) expansion each
 * Space Complexity: O(1)
 */
fun longestPalindrome(s: String?): String {
    if (s.isNullOrEmpty()) return ""

    var start = 0
    var end = 0

    for (i in s.indices) {
        // Case 1: Odd-length palindrome — center is a single character (e.g., "aba")
        val len1 = expandAroundCenter(s, i, i)
        // Case 2: Even-length palindrome — center is between two characters (e.g., "abba")
        val len2 = expandAroundCenter(s, i, i + 1)

        val maxLen = max(len1, len2)

        // If we found a longer palindrome, update start/end pointers
        if (maxLen > end - start) {
            start = i - (maxLen - 1) / 2
            end = i + maxLen / 2
        }
    }

    return s.substring(start, end + 1)
}

/**
 * Expand outward from center (left, right) while characters match.
 * Returns the length of the palindrome found.
 */
private fun expandAroundCenter(s: String, l: Int, r: Int): Int {
    var left = l
    var right = r
    while (left >= 0 && right < s.length && s[left] == s[right]) {
        left--
        right++
    }
    // When the loop exits, left and right have gone one step too far.
    // Palindrome length = (right - 1) - (left + 1) + 1 = right - left - 1
    return right - left - 1
}
