package dp.one_d.word_break

/**
 * Word Break — LeetCode #139
 * https://leetcode.com/problems/word-break/
 *
 * Problem:
 * -------
 * Given a string s and a dictionary of words, determine if s can be segmented
 * into a space-separated sequence of one or more dictionary words.
 *
 * Example:  s = "leetcode", wordDict = ["leet","code"]  →  true
 *           s = "applepenapple", wordDict = ["apple","pen"]  →  true
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic DP — string segmentation)
 *
 * Recurrence: dp[i] = true if any dp[j] && s[j..i) is in wordDict
 * Base case: dp[0] = true (empty string)
 *
 * Two approaches:
 * 1. Brute Force Recursion: O(2^N) — try all segmentations
 * 2. Bottom-Up DP: O(N² × M) — build dp array
 */

fun main() {
    val s1 = "leetcode"
    val dict1 = listOf("leet", "code")
    val s2 = "applepenapple"
    val dict2 = listOf("apple", "pen")

    println("=== Method 1: Brute Force Recursion ===")
    println("wordBreak(\"$s1\") = ${wordBreakBruteForce(s1, dict1)}")
    println("wordBreak(\"$s2\") = ${wordBreakBruteForce(s2, dict2)}")

    println("\n=== Method 2: Bottom-Up DP ===")
    println("wordBreak(\"$s1\") = ${wordBreakDP(s1, dict1)}")
    println("wordBreak(\"$s2\") = ${wordBreakDP(s2, dict2)}")

    println("\n=== Step-by-step trace ===")
    wordBreakTrace(s1, dict1)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Recursion (try all segmentations)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Try every possible prefix. If prefix is in dict, recurse on suffix.
 *
 * Core Idea:
 *   - For each position i, check if s[0..i) is in the dictionary.
 *   - If yes, recursively check if s[i..end) can be segmented.
 *
 * Problem: Exponential time — same substrings checked repeatedly.
 *
 * Time Complexity:  O(2^N) — exponential.
 * Space Complexity: O(N) — recursion stack.
 */
fun wordBreakBruteForce(s: String, wordDict: List<String>): Boolean {
    val wordSet = wordDict.toSet()
    return canBreak(s, 0, wordSet)
}

private fun canBreak(s: String, start: Int, wordSet: Set<String>): Boolean {
    if (start == s.length) return true  // Entire string consumed.
    for (end in start + 1..s.length) {
        if (s.substring(start, end) in wordSet && canBreak(s, end, wordSet)) {
            return true
        }
    }
    return false
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BOTTOM-UP DP (OPTIMAL)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BOTTOM-UP DP — dp[i] = true if s[0..i) can be segmented.
 *
 * Core Idea:
 *   - dp[0] = true (empty string).
 *   - For each i from 1 to n, for each j from 0 to i-1:
 *     If dp[j] && s[j..i) is in dict → dp[i] = true.
 *
 * Key Insight:
 *   - "Can the prefix s[0..j) be segmented AND is s[j..i) a word?"
 *   - If both are true, then s[0..i) can be segmented.
 *
 * Time Complexity:  O(N² × M) — N = string length, M = max word length for substring.
 * Space Complexity: O(N) — dp array.
 */
fun wordBreakDP(s: String, wordDict: List<String>): Boolean {
    val wordSet = wordDict.toSet()
    val dp = BooleanArray(s.length + 1)
    dp[0] = true  // Empty string can always be segmented.

    for (i in 1..s.length) {
        for (j in 0 until i) {
            if (dp[j] && s.substring(j, i) in wordSet) {
                dp[i] = true
                break  // Found a valid segmentation, no need to check more.
            }
        }
    }
    return dp[s.length]
}

/**
 * Bottom-up DP with step-by-step trace.
 */
fun wordBreakTrace(s: String, wordDict: List<String>) {
    val wordSet = wordDict.toSet()
    val dp = BooleanArray(s.length + 1)
    dp[0] = true
    println("Input: s=\"$s\", dict=$wordDict")

    for (i in 1..s.length) {
        for (j in 0 until i) {
            val sub = s.substring(j, i)
            if (dp[j] && sub in wordSet) {
                dp[i] = true
                println("  dp[$i]=true (dp[$j]=true && \"$sub\" in dict)")
                break
            }
        }
        if (!dp[i]) println("  dp[$i]=false")
    }
    println("  Result: ${dp[s.length]}")
}
