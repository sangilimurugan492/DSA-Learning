package dp.two_d.longest_common_subsequence

/**
 * Longest Common Subsequence (LCS) — LeetCode #1143
 * https://leetcode.com/problems/longest-common-subsequence/
 *
 * Problem:
 * -------
 * Given two strings text1 and text2, return the length of their longest common subsequence.
 * A subsequence is a sequence that appears in the same relative order but not necessarily contiguous.
 *
 * Example:  text1 = "abcde", text2 = "ace"  →  3  ("ace")
 *           text1 = "abc", text2 = "def"  →  0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic 2D DP)
 *
 * Recurrence:
 *   If text1[i] == text2[j]: dp[i][j] = dp[i-1][j-1] + 1
 *   Else: dp[i][j] = max(dp[i-1][j], dp[i][j-1])
 *
 * Two approaches:
 * 1. 2D DP: O(M × N) time, O(M × N) space
 * 2. Space-Optimized DP: O(M × N) time, O(min(M,N)) space
 */

fun main() {
    val text1 = "abcde"
    val text2 = "ace"

    println("=== Method 1: 2D DP ===")
    println("LCS(\"$text1\", \"$text2\") = ${longestCommonSubsequence2D(text1, text2)}")

    println("\n=== Method 2: Space-Optimized DP ===")
    println("LCS(\"$text1\", \"$text2\") = ${longestCommonSubsequenceOptimized(text1, text2)}")

    println("\n=== Step-by-step trace ===")
    lcsTrace(text1, text2)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: 2D DP — O(M × N) time and space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * 2D DP — dp[i][j] = LCS of text1[0..i) and text2[0..j).
 *
 * Core Idea:
 *   - If chars match: dp[i][j] = dp[i-1][j-1] + 1 (extend LCS).
 *   - If chars differ: dp[i][j] = max(dp[i-1][j], dp[i][j-1]) (skip one char).
 *
 * Key Insight:
 *   - When chars match, we extend the LCS by 1 — look at dp[i-1][j-1].
 *   - When chars differ, we take the best of skipping either char.
 *
 * Time Complexity:  O(M × N) — fill 2D table.
 * Space Complexity: O(M × N) — 2D dp array.
 */
fun longestCommonSubsequence2D(text1: String, text2: String): Int {
    val m = text1.length
    val n = text2.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 1..m) {
        for (j in 1..n) {
            if (text1[i - 1] == text2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1] + 1
            } else {
                dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
            }
        }
    }
    return dp[m][n]
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SPACE-OPTIMIZED DP — O(min(M,N)) space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SPACE-OPTIMIZED — dp[i][j] only depends on current row and previous row. Use two 1D arrays.
 *
 * Core Idea:
 *   - Same recurrence, but only keep prev[] and curr[] arrays.
 *   - Need a temp variable for dp[i-1][j-1] since we overwrite it.
 *
 * Time Complexity:  O(M × N) — fill 1D arrays.
 * Space Complexity: O(min(M,N)) — two 1D arrays.
 */
fun longestCommonSubsequenceOptimized(text1: String, text2: String): Int {
    // Ensure text2 is the shorter string for less space.
    val (short, long) = if (text1.length < text2.length) text1 to text2 else text2 to text1
    val m = long.length
    val n = short.length

    var prev = IntArray(n + 1)
    var curr = IntArray(n + 1)

    for (i in 1..m) {
        for (j in 1..n) {
            if (long[i - 1] == short[j - 1]) {
                curr[j] = prev[j - 1] + 1
            } else {
                curr[j] = maxOf(prev[j], curr[j - 1])
            }
        }
        val temp = prev
        prev = curr
        curr = temp
    }
    return prev[n]
}

/**
 * 2D DP with step-by-step trace.
 */
fun lcsTrace(text1: String, text2: String) {
    val m = text1.length
    val n = text2.length
    val dp = Array(m + 1) { IntArray(n + 1) }
    println("text1=\"$text1\", text2=\"$text2\"")

    for (i in 1..m) {
        for (j in 1..n) {
            if (text1[i - 1] == text2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1] + 1
                println("  [$i][$j]: '${text1[i-1]}'=='${text2[j-1]}' → match → dp=$dp[i][j]")
            } else {
                dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
                println("  [$i][$j]: '${text1[i-1]}'!='${text2[j-1]}' → max(${dp[i-1][j]},${dp[i][j-1]})=$dp[i][j]")
            }
        }
    }
    println("  Result: ${dp[m][n]}")
}
