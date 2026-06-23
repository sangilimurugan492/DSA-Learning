package dp.two_d

/**
 * https://leetcode.com/problems/longest-palindromic-subsequence/
 *
 * Given a string s, find the length of the longest palindromic subsequence.
 * A subsequence is a sequence that can be derived by deleting some or no
 * elements without changing the order of the remaining elements.
 *
 * Example 1: s = "bbbab" → Output: 4 ("bbbb")
 * Example 2: s = "cbbd" → Output: 2 ("bb")
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (LCS variant — palindrome + subsequence pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * KEY INSIGHT: A palindrome reads the same forward and backward.
 * So the LPS of s is the LCS of s and reverse(s)!
 *
 * WHY? If a subsequence is a palindrome, it reads the same forward and backward.
 * Reading it backward = reading reverse(s) forward. So any palindromic
 * subsequence of s is also a common subsequence of s and reverse(s).
 * And the longest such common subsequence = the longest palindromic subsequence.
 *
 * APPROACH 1: LCS with reverse string
 *   lps(s) = lcs(s, s.reversed())
 *
 * APPROACH 2: Direct 2D DP (more intuitive)
 *   dp[i][j] = length of LPS in s[i ...j]
 *
 *   If s[i] == s[j]: dp[i][j] = 2 + dp[i+1][j-1]
 *     (both ends match, add 2 to inner substring's LPS)
 *   If s[i] != s[j]: dp[i][j] = max(dp[i+1][j], dp[i][j-1])
 *     (skip one end, take the better option)
 *
 *   Base case: dp[i][i] = 1 (single character is a palindrome of length 1)
 *
 * WHY fill diagonally? Because dp[i][j] depends on dp[i+1][j-1], dp[i+1][j], dp[i][j-1]
 * — all values in the "inner" substring. We must compute shorter substrings first.
 *
 * Connection to LCS:
 *   LCS: compare two DIFFERENT strings
 *   LPS: compare a string with ITSELF (reversed) — special case of LCS!
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Longest Palindromic Subsequence ===")
    println("Brute Force 'bbbab': ${lpsBruteForce("bbbab")}")
    println("Memoization 'bbbab': ${lpsMemo("bbbab")}")
    println("Tabulation  'bbbab': ${lpsTabulation("bbbab")}")
    println("---")
    println("Tabulation 'cbbd':   ${lpsTabulation("cbbd")}")
    println("Tabulation 'abcba':  ${lpsTabulation("abcba")}")
}

/**
 * BRUTE FORCE — Recursion
 * Time Complexity: O(2^N) — at each pair (i,j), 2-3 choices
 * Space Complexity: O(N) — recursion stack
 *
 * For each pair (i, j):
 *   - If s[i] == s[j]: include both, recurse on inner substring
 *   - If s[i] != s[j]: skip s[i] OR skip s[j], take max
 *
 * Recursion tree for "bbbab":
 * f(0,4): s[0]='b'==s[4]='b' → 2 + f(1,3)
 *   f(1,3): s[1]='b'!=s[3]='a' → max(f(2,3), f(1,2))
 *     f(2,3): s[2]='b'!=s[3]='a' → max(f(3,3), f(2,2)) = max(1,1) = 1
 *     f(1,2): s[1]='b'==s[2]='b' → 2 + f(2,1) = 2 + 0 = 2
 *   f(1,3) = max(1, 2) = 2
 * f(0,4) = 2 + 2 = 4 ✅
 *
 * But without memoization, many subproblems are recomputed!
 */
fun lpsBruteForce(s: String): Int {
    return lpsRec(s, 0, s.length - 1)
}

private fun lpsRec(s: String, i: Int, j: Int): Int {
    if (i > j) return 0
    if (i == j) return 1  // single character
    return if (s[i] == s[j]) {
        2 + lpsRec(s, i + 1, j - 1)
    } else {
        maxOf(lpsRec(s, i + 1, j), lpsRec(s, i, j - 1))
    }
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N²) — each (i,j) pair computed once
 * Space Complexity: O(N²) — memo + recursion stack
 *
 * Cache result for each (i, j) pair.
 *
 * Trace for "bbbab":
 * f(0,4): 'b'=='b' → 2 + f(1,3)
 *   f(1,3): 'b'!='a' → max(f(2,3), f(1,2))
 *     f(2,3): 'b'!='a' → max(f(3,3), f(2,2))
 *       f(3,3) = 1 (cache!)
 *       f(2,2) = 1 (cache!)
 *     f(2,3) = 1 (cache!)
 *     f(1,2): 'b'=='b' → 2 + f(2,1)
 *       f(2,1) = 0 (i > j, cache!)
 *     f(1,2) = 2 (cache!)
 *   f(1,3) = max(1, 2) = 2 (cache!)
 * f(0,4) = 2 + 2 = 4 ✅
 */
fun lpsMemo(s: String): Int {
    val n = s.length
    val memo = Array(n) { IntArray(n) { -1 } }
    return lpsMemoHelper(s, 0, n - 1, memo)
}

private fun lpsMemoHelper(s: String, i: Int, j: Int, memo: Array<IntArray>): Int {
    if (i > j) return 0
    if (i == j) return 1
    if (memo[i][j] != -1) return memo[i][j]

    memo[i][j] = if (s[i] == s[j]) {
        2 + lpsMemoHelper(s, i + 1, j - 1, memo)
    } else {
        maxOf(lpsMemoHelper(s, i + 1, j, memo), lpsMemoHelper(s, i, j - 1, memo))
    }
    return memo[i][j]
}

/**
 * OPTIMAL — Bottom-Up DP (Tabulation)
 * Time Complexity: O(N²)
 * Space Complexity: O(N²)
 *
 * dp[i][j] = length of LPS in s[i ...j]
 *
 * Fill diagonally: first all substrings of length 1, then length 2, etc.
 * WHY? Because dp[i][j] depends on dp[i+1][j-1], dp[i+1][j], dp[i][j-1]
 * which are all "shorter" substrings (computed in earlier iterations).
 *
 * Trace for "bbbab":
 * Length 1 (diagonal): dp[i][i] = 1 for all i
 *   b  0  0  0  0       1  0  0  0
 *   0  b  0  0  0  →    0  1  0  0
 *   0  0  b  0  0       0  0  1  0
 *   0  0  0  a  0       0  0  0  1
 *
 * Length 2:
 *   (0,1): 'b'=='b' → 2+dp[1][0]=2+0=2
 *   (1,2): 'b'=='b' → 2+dp[2][1]=2+0=2
 *   (2,3): 'b'!='a' → max(dp[3][3],dp[2][2])=1
 *
 * Length 3:
 *   (0,2): 'b'=='b' → 2+dp[1][1]=2+1=3
 *   (1,3): 'b'!='a' → max(dp[2][3],dp[1][2])=max(1,2)=2
 *
 * Length 4:
 *   (0,3): 'b'!='a' → max(dp[1][3],dp[0][2])=max(2,3)=3
 *
 * Length 5:
 *   (0,4): 'b'=='b' → 2+dp[1][3]=2+2=4
 *
 * dp[0][4] = 4 ✅
 */
fun lpsTabulation(s: String): Int {
    val n = s.length
    val dp = Array(n) { IntArray(n) }

    // Base case: single characters
    for (i in 0 until n) dp[i][i] = 1

    // Fill by substring length
    for (len in 2..n) {
        for (i in 0..n - len) {
            val j = i + len - 1
            if (s[i] == s[j]) {
                dp[i][j] = 2 + if (i + 1 <= j - 1) dp[i + 1][j - 1] else 0
            } else {
                dp[i][j] = maxOf(dp[i + 1][j], dp[i][j - 1])
            }
        }
    }
    return dp[0][n - 1]
}
