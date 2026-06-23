package dp.two_d

/**
 * https://leetcode.com/problems/edit-distance/
 *
 * Given two strings word1 and word2, return the minimum number of
 * operations required to convert word1 to word2.
 * Allowed operations: Insert, Delete, Replace (each costs 1).
 *
 * Example 1: word1 = "horse", word2 = "ros" → Output: 3
 *   horse → rorse (replace 'h' with 'r')
 *   rorse → rose  (delete 'r')
 *   rose  → ros   (delete 'e')
 *
 * Example 2: word1 = "intention", word2 = "execution" → Output: 5
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard — THE classic edit distance problem)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is LCS's more powerful cousin. LCS only counts matches;
 * Edit Distance counts the COST of mismatches.
 *
 * At each position (i, j), we have 3 choices when chars DON'T match:
 *   1. INSERT a char into word1 → dp[i][j-1] + 1
 *      (We "insert" word2[j], so j moves forward, i stays)
 *   2. DELETE a char from word1 → dp[i-1][j] + 1
 *      (We "delete" word1[i], so i moves forward, j stays)
 *   3. REPLACE word1[i] with word2[j] → dp[i-1][j-1] + 1
 *      (Both chars are now "handled", both pointers move forward)
 *
 * When chars MATCH: dp[i][j] = dp[i-1][j-1] (no cost, just move forward)
 *
 * WHY these 3 operations? They cover ALL possible transformations:
 *   - Insert: "I need this character that word2 has but word1 doesn't"
 *   - Delete: "word1 has an extra character that word2 doesn't"
 *   - Replace: "Both have a character here, but they're different"
 *
 * Connection to LCS: If LCS length = L, then edit distance ≥ m+n-2L
 *   (You must delete m-L from word1 and insert n-L into word2)
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Edit Distance ===")
    println("Brute Force 'horse','ros': ${editDistanceBruteForce("horse", "ros")}")
    println("Memoization 'horse','ros':  ${editDistanceMemo("horse", "ros")}")
    println("Tabulation  'horse','ros':  ${editDistanceTabulation("horse", "ros")}")
    println("---")
    println("Optimal 'intention','execution': ${editDistanceTabulation("intention", "execution")}")
    println("Optimal '','abc':                 ${editDistanceTabulation("", "abc")}")
}

/**
 * BRUTE FORCE — Recursion
 * Time Complexity: O(3^(m+n)) — 3 choices at each mismatch
 * Space Complexity: O(m+n) — recursion depth
 *
 * At each (i, j): if match → 1 path, if no match → 3 paths.
 * Exponential explosion!
 *
 * Recursion tree for "ab" vs "a":
 *              (0,0)
 *            'a'=='a' MATCH
 *              (1,1)
 *            'b'!=' ' NO MATCH (j exhausted)
 *            → delete 'b': 1 + (2,1) = 1 + 0 = 1
 * Result: 1 ✅
 */
fun editDistanceBruteForce(word1: String, word2: String): Int {
    return edRec(word1, word2, word1.length, word2.length)
}

private fun edRec(s1: String, s2: String, i: Int, j: Int): Int {
    // Base cases: if one string is exhausted, insert/delete remaining chars
    if (i == 0) return j  // insert all j chars of s2
    if (j == 0) return i  // delete all i chars of s1

    return if (s1[i - 1] == s2[j - 1]) {
        edRec(s1, s2, i - 1, j - 1)  // match, no cost
    } else {
        1 + minOf(
            edRec(s1, s2, i, j - 1),      // insert
            minOf(
                edRec(s1, s2, i - 1, j),   // delete
                edRec(s1, s2, i - 1, j - 1) // replace
            )
        )
    }
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(m × n)
 * Space Complexity: O(m × n) — memo + recursion stack
 *
 * Cache each (i, j) result. Never recompute.
 *
 * Trace for "horse" vs "ros":
 * ed(5,3): 'e'!='s' → 1 + min(ed(5,2), ed(4,3), ed(4,2))
 *   ed(4,2): 's'=='s' → ed(3,1)
 *     ed(3,1): 'r'!='r' wait... 'r'=='r' → ed(2,0) = 2
 *     ed(3,1) = 2
 *   ed(4,2) = 2
 *   ... (continuing with cached values)
 * ed(5,3) = 3 ✅
 */
fun editDistanceMemo(word1: String, word2: String): Int {
    val m = word1.length
    val n = word2.length
    val memo = Array(m + 1) { IntArray(n + 1) { -1 } }
    return edMemoHelper(word1, word2, m, n, memo)
}

private fun edMemoHelper(s1: String, s2: String, i: Int, j: Int, memo: Array<IntArray>): Int {
    if (i == 0) return j
    if (j == 0) return i
    if (memo[i][j] != -1) return memo[i][j]

    memo[i][j] = if (s1[i - 1] == s2[j - 1]) {
        edMemoHelper(s1, s2, i - 1, j - 1, memo)
    } else {
        1 + minOf(
            edMemoHelper(s1, s2, i, j - 1, memo),
            minOf(
                edMemoHelper(s1, s2, i - 1, j, memo),
                edMemoHelper(s1, s2, i - 1, j - 1, memo)
            )
        )
    }
    return memo[i][j]
}

/**
 * OPTIMAL — Bottom-Up DP (Tabulation)
 * Time Complexity: O(m × n)
 * Space Complexity: O(m × n)
 *
 * dp[i][j] = min operations to convert word1[0..i-1] to word2[0..j-1]
 *
 * Base cases:
 *   dp[0][j] = j (insert j characters)
 *   dp[i][0] = i (delete i characters)
 *
 * Recurrence:
 *   if word1[i-1] == word2[j-1]: dp[i][j] = dp[i-1][j-1]
 *   else: dp[i][j] = 1 + min(dp[i][j-1], dp[i-1][j], dp[i-1][j-1])
 *                                     insert    delete   replace
 *
 * Trace for "horse" vs "ros":
 *       ""  r  o  s
 *  ""    0  1  2  3
 *  h     1  1  2  3    ← 'h'!='r': 1+min(dp[0][1],dp[1][0],dp[0][0])=1+min(1,1,0)=1
 *  o     2  2  1  2    ← 'o'!='r': 1+min(2,1,1)=2; 'o'=='o': dp[1][1]=1
 *  r     3  2  2  2    ← 'r'=='r': dp[2][1]=2; 'r'!='o': 1+min(1,2,1)=2
 *  s     4  3  3  2    ← 's'!='r': 1+min(2,3,2)=3; 's'=='s': dp[3][2]=2
 *  e     5  4  4  3    ← 'e'!='s': 1+min(3,4,3)=4 wait...
 *         Actually: 'e'!='r': 1+min(dp[4][1],dp[5][0],dp[4][0])=1+min(2,4,3)=3
 *         'e'!='o': 1+min(dp[4][2],dp[5][1],dp[4][1])=1+min(2,4,2)=3
 *         'e'!='s': 1+min(dp[4][3],dp[5][2],dp[4][2])=1+min(2,4,2)=3
 *
 * dp[5][3] = 3 ✅
 *
 * HOW TO READ THE TABLE:
 * - dp[0][j] = j: converting "" to word2[0..j-1] needs j insertions
 * - dp[i][0] = i: converting word1[0..i-1] to "" needs i deletions
 * - Match: copy diagonal (free!)
 * - No match: 1 + min(left=insert, up=delete, diagonal=replace)
 */
fun editDistanceTabulation(word1: String, word2: String): Int {
    val m = word1.length
    val n = word2.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    // Base cases
    for (i in 0..m) dp[i][0] = i
    for (j in 0..n) dp[0][j] = j

    for (i in 1..m) {
        for (j in 1..n) {
            if (word1[i - 1] == word2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1]  // match, no cost
            } else {
                dp[i][j] = 1 + minOf(
                    dp[i][j - 1],      // insert
                    minOf(
                        dp[i - 1][j],   // delete
                        dp[i - 1][j - 1] // replace
                    )
                )
            }
        }
    }
    return dp[m][n]
}
