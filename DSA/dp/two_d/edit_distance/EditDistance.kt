package dp.two_d.edit_distance

/**
 * Edit Distance — LeetCode #72
 * https://leetcode.com/problems/edit-distance/
 *
 * Problem:
 * -------
 * Given two strings word1 and word2, return the minimum number of operations
 * (insert, delete, replace) required to convert word1 to word2.
 *
 * Example:  word1 = "horse", word2 = "ros"  →  3
 *           horse → rorse (replace h) → rose (remove r) → ros (remove e)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic 2D DP — Levenshtein Distance)
 *
 * Recurrence:
 *   If word1[i] == word2[j]: dp[i][j] = dp[i-1][j-1] (no operation needed)
 *   Else: dp[i][j] = 1 + min(dp[i-1][j-1], dp[i-1][j], dp[i][j-1])
 *         (replace, delete, insert)
 *
 * Two approaches:
 * 1. 2D DP: O(M × N) time, O(M × N) space
 * 2. Space-Optimized DP: O(M × N) time, O(N) space
 */

fun main() {
    val word1 = "horse"
    val word2 = "ros"

    println("=== Method 1: 2D DP ===")
    println("editDistance(\"$word1\", \"$word2\") = ${editDistance2D(word1, word2)}")

    println("\n=== Method 2: Space-Optimized DP ===")
    println("editDistance(\"$word1\", \"$word2\") = ${editDistanceOptimized(word1, word2)}")

    println("\n=== Step-by-step trace ===")
    editDistanceTrace(word1, word2)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: 2D DP — O(M × N) time and space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * 2D DP — dp[i][j] = min operations to convert word1[0..i) to word2[0..j).
 *
 * Core Idea:
 *   - If chars match: dp[i][j] = dp[i-1][j-1] (free — no operation).
 *   - If chars differ: dp[i][j] = 1 + min of:
 *     - dp[i-1][j-1] (replace)
 *     - dp[i-1][j]   (delete from word1)
 *     - dp[i][j-1]   (insert into word1)
 *
 * Key Insight:
 *   - Three operations: replace (diagonal), delete (up), insert (left).
 *   - Base case: dp[i][0] = i (delete all), dp[0][j] = j (insert all).
 *
 * Time Complexity:  O(M × N) — fill 2D table.
 * Space Complexity: O(M × N) — 2D dp array.
 */
fun editDistance2D(word1: String, word2: String): Int {
    val m = word1.length
    val n = word2.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    // Base cases: converting to/from empty string.
    for (i in 0..m) dp[i][0] = i  // Delete all chars.
    for (j in 0..n) dp[0][j] = j  // Insert all chars.

    for (i in 1..m) {
        for (j in 1..n) {
            if (word1[i - 1] == word2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1]  // No operation needed.
            } else {
                dp[i][j] = 1 + minOf(
                    dp[i - 1][j - 1],  // Replace
                    dp[i - 1][j],      // Delete
                    dp[i][j - 1]       // Insert
                )
            }
        }
    }
    return dp[m][n]
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SPACE-OPTIMIZED DP — O(N) space
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SPACE-OPTIMIZED — dp[i][j] only depends on current and previous row. Use two 1D arrays.
 *
 * Core Idea:
 *   - Same recurrence, but only keep prev[] and curr[] arrays.
 *   - Need a temp variable for dp[i-1][j-1] (diagonal) since we overwrite it.
 *
 * Time Complexity:  O(M × N) — fill 1D arrays.
 * Space Complexity: O(N) — two 1D arrays.
 */
fun editDistanceOptimized(word1: String, word2: String): Int {
    val m = word1.length
    val n = word2.length
    var prev = IntArray(n + 1) { it }  // Base case: dp[0][j] = j.
    var curr = IntArray(n + 1)

    for (i in 1..m) {
        curr[0] = i  // Base case: dp[i][0] = i.
        for (j in 1..n) {
            if (word1[i - 1] == word2[j - 1]) {
                curr[j] = prev[j - 1]
            } else {
                curr[j] = 1 + minOf(prev[j - 1], prev[j], curr[j - 1])
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
fun editDistanceTrace(word1: String, word2: String) {
    val m = word1.length
    val n = word2.length
    val dp = Array(m + 1) { IntArray(n + 1) }
    for (i in 0..m) dp[i][0] = i
    for (j in 0..n) dp[0][j] = j
    println("word1=\"$word1\", word2=\"$word2\"")

    for (i in 1..m) {
        for (j in 1..n) {
            if (word1[i - 1] == word2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1]
                println("  [$i][$j]: '${word1[i-1]}'=='${word2[j-1]}' → match → dp=$dp[i][j]")
            } else {
                dp[i][j] = 1 + minOf(dp[i-1][j-1], dp[i-1][j], dp[i][j-1])
                println("  [$i][$j]: '${word1[i-1]}'!='${word2[j-1]}' → 1+min(${dp[i-1][j-1]},${dp[i-1][j]},${dp[i][j-1]})=$dp[i][j]")
            }
        }
    }
    println("  Result: ${dp[m][n]}")
}
