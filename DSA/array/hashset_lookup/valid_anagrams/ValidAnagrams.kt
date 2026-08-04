package patterns.hashset_lookup.valid_anagrams

/**
 * https://leetcode.com/problems/valid-anagram/
 * Check if two strings are anagrams (same characters, same frequency).
 * Example: s = "anagram", t = "nagaram" → true
 *          s = "rat", t = "car" → false
 * FAANG Importance: ⭐⭐⭐⭐ (Classic hash map frequency problem)
 */

fun main() {
    println(isAnagramSort("anagram", "nagaram"))  // true
    println(isAnagramSort("rat", "car"))           // false
    println(isAnagramCount("listen", "silent"))     // true
    println(isAnagramCount("hello", "world"))       // false
}

/**
 * APPROACH 1: Sort & Compare — O(N log N) time, O(N) space
 * Sort both strings and compare. If equal → anagrams.
 */
fun isAnagramSort(s: String, t: String): Boolean {
    if (s.length != t.length) return false
    return s.toCharArray().sorted() == t.toCharArray().sorted()
}

/**
 * APPROACH 2: Character Frequency Count — O(N) time, O(1) space
 * Count character frequencies. If all match → anagrams.
 */
fun isAnagramCount(s: String, t: String): Boolean {
    if (s.length != t.length) return false
    val count = IntArray(26)
    for (i in s.indices) {
        count[s[i] - 'a']++
        count[t[i] - 'a']--
    }
    return count.all { it == 0 }
}

/**
 * APPROACH 3: HashMap (Unicode-safe) — O(N) time, O(K) space
 * Works for any characters, not just a-z.
 */
fun isAnagramMap(s: String, t: String): Boolean {
    if (s.length != t.length) return false
    val map = HashMap<Char, Int>()
    for (c in s) map[c] = map.getOrDefault(c, 0) + 1
    for (c in t) {
        val count = map.getOrDefault(c, 0) - 1
        if (count < 0) return false
        map[c] = count
    }
    return true
}
