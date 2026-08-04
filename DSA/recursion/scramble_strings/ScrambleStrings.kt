package patterns.recursion.scramble_strings

/**
 * https://leetcode.com/problems/scramble-string/
 * Check if string s2 is a scrambled version of s1.
 * Scramble: split string into two non-empty parts, optionally swap them, recurse.
 * Example: s1 = "great", s2 = "rgeat" → true
 *          s1 = "abcde", s2 = "caebd" → false
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard recursion + memoization — must know)
 */

fun main() {
    println(isScramble("great", "rgeat"))  // true
    println(isScramble("abcde", "caebd"))  // false
    println(isScramble("a", "a"))           // true
}

/**
 * Recursive + Memoization: O(N^4) time, O(N^3) space
 * For each split point i, check two cases:
 * 1. No swap: s1[0..i] scrambles s2[0..i] AND s1[i ...n] scrambles s2[i ...n]
 * 2. Swap: s1[0..i] scrambles s2[n-i..n] AND s1[i ...n] scrambles s2[0 ..n-i]
 */
fun isScramble(s1: String, s2: String): Boolean {
    val memo = HashMap<String, Boolean>()
    return scramble(s1, s2, memo)
}

private fun scramble(s1: String, s2: String, memo: HashMap<String, Boolean>): Boolean {
    val key = "$s1,$s2"
    if (key in memo) return memo[key]!!

    // Base cases
    if (s1 == s2) { memo[key] = true; return true }
    if (s1.length != s2.length) { memo[key] = false; return false }
    if (s1.length == 1) { memo[key] = (s1 == s2); return s1 == s2 }

    // Pruning: if character counts differ, can't be scramble
    if (!sameChars(s1, s2)) { memo[key] = false; return false }

    val n = s1.length
    for (i in 1 until n) {
        // Case 1: No swap — s1[0..i] ↔ s2[0..i], s1[i..n] ↔ s2[i..n]
        val noSwap = scramble(s1.substring(0, i), s2.substring(0, i), memo) &&
                      scramble(s1.substring(i), s2.substring(i), memo)
        if (noSwap) { memo[key] = true; return true }

        // Case 2: Swap — s1[0..i] ↔ s2[n-i..n], s1[i..n] ↔ s2[0..n-i]
        val swap = scramble(s1.substring(0, i), s2.substring(n - i), memo) &&
                   scramble(s1.substring(i), s2.substring(0, n - i), memo)
        if (swap) { memo[key] = true; return true }
    }

    memo[key] = false
    return false
}

/**
 * Quick check: both strings must have same character frequency.
 * If not, they can't be scrambles — prune early.
 */
private fun sameChars(s1: String, s2: String): Boolean {
    if (s1.length != s2.length) return false
    val count = IntArray(26)
    for (c in s1) count[c - 'a']++
    for (c in s2) count[c - 'a']--
    return count.all { it == 0 }
}
