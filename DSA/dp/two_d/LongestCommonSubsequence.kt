package dp.two_d

/**
 * https://leetcode.com/problems/longest-common-subsequence/
 *
 * Given two strings text1 and text2, return the length of their
 * longest common subsequence (LCS). A subsequence is a sequence that
 * appears in the same relative order, but not necessarily contiguous.
 *
 * Example 1: text1 = "abcde", text2 = "ace" → Output: 3 (subsequence "ace")
 * Example 2: text1 = "abc", text2 = "abc" → Output: 3
 * Example 3: text1 = "abc", text2 = "def" → Output: 0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE most important 2D DP — foundation for string DP)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * We compare characters from both strings one at a time.
 * At each position (i, j), we have two cases:
 *
 * CASE 1: text1[i] == text2[j] (characters MATCH)
 *   → This character is part of the LCS!
 *   → Move both pointers forward: 1 + LCS(i+1, j+1)
 *
 * CASE 2: text1[i] != text2[j] (characters DON'T match)
 *   → This character CANNOT be in the LCS (at least not at both positions)
 *   → Try skipping from either string: max(LCS(i+1, j), LCS(i, j+1))
 *   → We try BOTH because we don't know which skip leads to the better answer
 *
 * WHY skip both? Consider "abcde" vs "ace":
 *   At (0,0): 'a'=='a' → match! Move both → 1 + LCS(1,1)
 *   At (1,1): 'b'!='c' → skip 'b' OR skip 'c'
 *     Skip 'b': LCS(2,1) → 'c'=='c' → match! → 1 + LCS(3,2)
 *     Skip 'c': LCS(1,2) → 'b'!='e' → ...
 *   The skip that leads to longer LCS wins.
 *
 * Base case: If either string is exhausted, LCS = 0
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Longest Common Subsequence ===")
    println("Brute Force 'abcde','ace': ${lcsBruteForce("abcde", "ace")}")
    println("Memoization 'abcde','ace':  ${lcsMemo("abcde", "ace")}")
    println("Tabulation  'abcde','ace':  ${lcsTabulation("abcde", "ace")}")
    println("---")
    println("Optimal 'abc','abc':        ${lcsTabulation("abc", "abc")}")
    println("Optimal 'abc','def':        ${lcsTabulation("abc", "def")}")
}

/**
 * BRUTE FORCE — Recursion
 * Time Complexity: O(2^(m+n)) — at each step, up to 2 branches
 * Space Complexity: O(m+n) — recursion depth
 *
 * For each (i, j): if match → 1 branch, if no match → 2 branches.
 * The recursion tree grows exponentially.
 *
 * Recursion tree for "abc" vs "ac":
 *                    (0,0)
 *                  'a'=='a' MATCH!
 *                   1+(1,1)
 *                  'b'!='c' NO MATCH
 *                 /          \
 *             (2,1)         (1,2)
 *           'c'=='c'!     'b'!='c'
 *            1+(3,2)=1    /        \
 *                       (2,2)    (1,3)=0
 *                     'c'!='c' wait...
 *   Actually 'c'!=' ' (end of string2) → 0
 *
 * Result: 1 + 1 = 2 ✅ ("ac")
 */
fun lcsBruteForce(text1: String, text2: String): Int {
    return lcsRec(text1, text2, 0, 0)
}

private fun lcsRec(s1: String, s2: String, i: Int, j: Int): Int {
    if (i == s1.length || j == s2.length) return 0
    return if (s1[i] == s2[j]) {
        1 + lcsRec(s1, s2, i + 1, j + 1)
    } else {
        maxOf(lcsRec(s1, s2, i + 1, j), lcsRec(s1, s2, i, j + 1))
    }
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(m × n) — each (i,j) pair computed once
 * Space Complexity: O(m × n) — memo + recursion stack
 *
 * Cache result for each (i, j) pair. Never recompute.
 *
 * Trace for "abcde" vs "ace":
 * (0,0): 'a'=='a' → 1 + (1,1)
 *   (1,1): 'b'!='c' → max((2,1), (1,2))
 *     (2,1): 'c'=='c' → 1 + (3,2)
 *       (3,2): 'd'!='e' → max((4,2), (3,3))
 *         (4,2): 'e'=='e' → 1 + (5,3) = 1+0 = 1  (cache!)
 *         (3,3): 'd'!=' ' → 0  (cache!)
 *       (3,2) = max(1, 0) = 1  (cache!)
 *     (2,1) = 1 + 1 = 2  (cache!)
 *     (1,2): 'b'!='e' → max((2,2), (1,3))
 *       (2,2): 'c'!='e' → max((3,2), (2,3))
 *         (3,2) = 1 (cached!) ← no recomputation!
 *         (2,3): 'c'!=' ' → 0
 *       (2,2) = max(1, 0) = 1
 *       (1,3): 'b'!=' ' → 0
 *     (1,2) = max(1, 0) = 1
 *   (1,1) = max(2, 1) = 2
 * (0,0) = 1 + 2 = 3 ✅ ("ace")
 */
fun lcsMemo(text1: String, text2: String): Int {
    val m = text1.length
    val n = text2.length
    val memo = Array(m + 1) { IntArray(n + 1) { -1 } }
    return lcsMemoHelper(text1, text2, 0, 0, memo)
}

private fun lcsMemoHelper(s1: String, s2: String, i: Int, j: Int, memo: Array<IntArray>): Int {
    if (i == s1.length || j == s2.length) return 0
    if (memo[i][j] != -1) return memo[i][j]
    memo[i][j] = if (s1[i] == s2[j]) {
        1 + lcsMemoHelper(s1, s2, i + 1, j + 1, memo)
    } else {
        maxOf(lcsMemoHelper(s1, s2, i + 1, j, memo), lcsMemoHelper(s1, s2, i, j + 1, memo))
    }
    return memo[i][j]
}

/**
 * OPTIMAL — Bottom-Up DP (Tabulation)
 * Time Complexity: O(m × n)
 * Space Complexity: O(m × n)
 *
 * dp[i][j] = LCS of text1[0..i-1] and text2[0..j-1]
 * Build from smaller prefixes to larger ones.
 *
 * Recurrence:
 *   if text1[i-1] == text2[j-1]: dp[i][j] = 1 + dp[i-1][j-1]
 *   else: dp[i][j] = max(dp[i-1][j], dp[i][j-1])
 *
 * Trace for "abcde" vs "ace":
 *       ""  a  c  e
 *   ""   0  0  0  0
 *   a    0  1  1  1     ← 'a'=='a': 1+dp[0][0]=1, then carry forward
 *   b    0  1  1  1     ← 'b'!='c': max(dp[1][1],dp[2][0])=1
 *   c    0  1  2  2     ← 'c'=='c': 1+dp[1][1]=2
 *   d    0  1  2  2     ← 'd'!='e': max(dp[3][2],dp[4][1])=2
 *   e    0  1  2  3     ← 'e'=='e': 1+dp[4][2]=3 ✅
 *
 * Result: 3 ✅
 *
 * HOW TO READ THE TABLE:
 * - Row i represents considering text1[0..i-1]
 * - Column j represents considering text2[0..j-1]
 * - dp[i][j] = best LCS using those prefixes
 * - When characters match: diagonal (both advance)
 * - When they don't: take max of left (skip text2 char) or up (skip text1 char)
 */
fun lcsTabulation(text1: String, text2: String): Int {
    val m = text1.length
    val n = text2.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 1..m) {
        for (j in 1..n) {
            if (text1[i - 1] == text2[j - 1]) {
                dp[i][j] = 1 + dp[i - 1][j - 1]
            } else {
                dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
            }
        }
    }
    return dp[m][n]
}
