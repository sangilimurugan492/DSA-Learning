package array.two_pointer.permutation_in_string

/**
 * https://leetcode.com/problems/permutation-in-string/
 *
 * Given two strings s1 and s2, return true if s2 contains a permutation of s1, or false otherwise.
 *
 * In other words, return true if one of s1's permutations is the substring of s2.
 *
 *
 *
 * Example 1:
 *
 * Input: s1 = "ab", s2 = "eidbaooo"
 * Output: true
 * Explanation: s2 contains one permutation of s1 ("ba").
 * Example 2:
 *
 * Input: s1 = "ab", s2 = "eidboaoo"
 * Output: false
 */
fun main() {
    println(permutationInStringBF("ab", "eidbaooo"))
    println(permutationInStringOP("ab", "eidbaooo"))
}

/**
 * Time Complexity O(m . n log n)
 * Space Complexity O(n)
 */
fun permutationInStringBF(s1: String, s2: String): Boolean {
    val n = s1.length
    val m = s2.length
    if (n > m) return false

    val sortedS1 = s1.toCharArray().sortedArray()

    for (i in 0..m - n) {
        val sub = s2.substring(i, i + n).toCharArray().sortedArray()
        if (sortedS1.contentEquals(sub)) return true
    }
    return false
}

/**
 * Time Complexity O(m) (where m is length of s2)
 * Space Complexity O(1)
 */
fun permutationInStringOP(s1: String, s2: String): Boolean {
    val n = s1.length
    val m = s2.length
    if (n > m) return false

    val s1Count = IntArray(26)
    val s2Count = IntArray(26)

    // Initialize the first window
    for (i in 0 until n) {
        s1Count[s1[i] - 'a']++
        s2Count[s2[i] - 'a']++
    }

    // Slide the window
    for (i in 0 until m - n) {
        if (s1Count.contentEquals(s2Count)) return true

        // Move window: remove leftmost, add next rightmost
        s2Count[s2[i] - 'a']--
        s2Count[s2[i + n] - 'a']++
    }

    // Check the last window
    return s1Count.contentEquals(s2Count)
}