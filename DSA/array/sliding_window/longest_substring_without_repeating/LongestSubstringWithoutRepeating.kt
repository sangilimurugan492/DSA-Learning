package patterns.sliding_window.longest_substring_without_repeating

/**
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 * Find the length of the longest substring without repeating characters.
 * Example: "abcabcbb" → 3 ("abc"), "bbbbb" → 1 ("b"), "pwwkew" → 3 ("wke")
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic sliding window — must know)
 */

fun main() {
    println(lengthOfLongestSubstring("abcabcbb"))  // 3
    println(lengthOfLongestSubstring("bbbbb"))       // 1
    println(lengthOfLongestSubstring("pwwkew"))      // 3
}

/**
 * Sliding Window + HashSet: O(N) time, O(min(N, charset)) space
 * Expand right, shrink left when duplicate found.
 */
fun lengthOfLongestSubstring(s: String): Int {
    val seen = HashSet<Char>()
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        while (s[right] in seen) {
            seen.remove(s[left])
            left++
        }
        seen.add(s[right])
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}

/**
 * Optimized with HashMap (jump left): O(N) time, O(min(N, charset)) space
 * Store last index of each char. Jump left to lastSeen[char] + 1 on duplicate.
 */
fun lengthOfLongestSubstringOptimized(s: String): Int {
    val lastIndex = HashMap<Char, Int>()
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        val ch = s[right]
        if (ch in lastIndex && lastIndex[ch]!! >= left) {
            left = lastIndex[ch]!! + 1  // Jump past the duplicate
        }
        lastIndex[ch] = right
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
