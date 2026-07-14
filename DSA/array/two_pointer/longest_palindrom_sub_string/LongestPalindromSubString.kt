package array.two_pointer.longest_palindrom_sub_string

import kotlin.math.max

fun main() {
    println(longestPalindrome("babad"))
}

fun longestPalindrome(s: String?): String {
    if (s.isNullOrEmpty()) return ""

    var start = 0
    var end = 0

    for (i in s.indices) {
        // Case 1: Center is a single character (e.g., "aba")
        val len1 = expandAroundCenter(s, i, i)
        // Case 2: Center is between two characters (e.g., "abba")
        val len2 = expandAroundCenter(s, i, i + 1)

        val maxLen = max(len1.toDouble(), len2.toDouble()).toInt()


        // If we found a longer palindrome, update our start/end pointers
        if (maxLen > end - start) {
            start = i - (maxLen - 1) / 2
            end = i + maxLen / 2
        }
    }

    return s.substring(start, end + 1)
}

private fun expandAroundCenter(s: String, l: Int, r: Int): Int {
    var left = l
    var right = r
    while (left >= 0 && right < s.length && s[left] == s[right]) {
        left--
        right++
    }
    // Returns the length of the palindrome found
    return right - left - 1
}