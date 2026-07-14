package array.sliding_window.minimum_window_substring

/**
 * https://leetcode.com/problems/minimum-window-substring/
 * Given strings s and t, return minimum window substring of s containing all chars of t.
 * Example: s = "ADOBECODEBANC", t = "ABC" → "BANC"
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE hardest sliding window)
 */

fun main() {
    println(minWindowBruteForce("ADOBECODEBANC", "ABC"))
    println(minWindowBruteForce("a", "a"))
    println("---")
    println(minWindowSlidingWindow("ADOBECODEBANC", "ABC"))
    println(minWindowSlidingWindow("a", "a"))
}

/**
 * BRUTE FORCE: O(N² × M) — check every substring
 * Time: O(N²) substrings × O(M) to check if contains all of t
 * Space: O(N) for substring copies
 */
fun minWindowBruteForce(s: String, t: String): String {
    if (s.length < t.length) return ""
    val tCount = t.groupingBy { it }.eachCount()
    var minLen = Int.MAX_VALUE
    var result = ""

    for (i in s.indices) {
        var windowCount = mutableMapOf<Char, Int>()
        for (j in i until s.length) {
            windowCount[s[j]] = windowCount.getOrDefault(s[j], 0) + 1
            // Check if window contains all chars of t
            var valid = true
            for ((ch, cnt) in tCount) {
                if (windowCount.getOrDefault(ch, 0) < cnt) { valid = false; break }
            }
            if (valid && (j - i + 1) < minLen) {
                minLen = j - i + 1
                result = s.substring(i, j + 1)
            }
        }
    }
    return result
}

/**
 * OPTIMAL: O(N) Sliding Window
 * Time: O(N) — each character visited at most twice
 * Space: O(K) — frequency maps
 *
 * Expand right until window is valid, then shrink left to minimize.
 */
fun minWindowSlidingWindow(s: String, t: String): String {
    if (s.length < t.length) return ""
    val need = t.groupingBy { it }.eachCount()
    val have = mutableMapOf<Char, Int>()
    var formed = 0
    val required = need.size
    var left = 0
    var minLen = Int.MAX_VALUE
    var start = 0

    for (right in s.indices) {
        val ch = s[right]
        have[ch] = have.getOrDefault(ch, 0) + 1
        if (ch in need && have[ch] == need[ch]) formed++

        while (formed == required) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1
                start = left
            }
            val leftChar = s[left]
            have[leftChar] = have[leftChar]!! - 1
            if (leftChar in need && have[leftChar]!! < need[leftChar]!!) formed--
            left++
        }
    }
    return if (minLen == Int.MAX_VALUE) "" else s.substring(start, start + minLen)
}
