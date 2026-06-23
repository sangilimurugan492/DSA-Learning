package dp.one_d

/**
 * https://leetcode.com/problems/word-break/
 *
 * Given a string s and a dictionary of strings wordDict, return true if s can be
 * segmented into a space-separated sequence of one or more dictionary words.
 *
 * Example 1: s = "leetcode", wordDict = ["leet","code"] → Output: true ("leet code")
 * Example 2: s = "applepenapple", wordDict = ["apple","pen"] → Output: true
 * Example 3: s = "catsandog", wordDict = ["cats","dog","sand","and","cat"] → Output: false
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 most asked — DP + string matching pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Key question: "Can I split s[0..i] into valid words?"
 *
 * For each position i, check ALL words in the dictionary:
 *   - If s[i - word.length + 1 .. i] == word AND dp[i - word.length] is true
 *   - Then dp[i] = true (we can reach position i by appending this word)
 *
 * Recurrence: dp[i] = OR of (dp[i - word.length] AND s[i-word.len+1..i] == word)
 *             for all words in wordDict
 *
 * Base case: dp[0] = true (empty string is always "segmentable")
 *
 * WHY does this work? We're building from left to right. If we know that
 * dp[j] = true (we can segment s[0..j-1]), and s[j ... i] is a valid word,
 * then dp[i+1] = true (we can segment s[0..i]).
 *
 * This is like a BFS/DFS on positions — each valid word is an "edge"
 * from position j to position j + word.length.
 *
 * Connection to other problems:
 *   - Climbing Stairs: "reach step n from step n-1 or n-2"
 *   - Word Break: "reach position i from any position j where s[j ... i] is a word"
 *   - Word Break is a GENERALIZATION — instead of fixed step sizes (1,2),
 *     step sizes are word lengths that match the string!
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Word Break ===")
    println("Brute Force 'leetcode': ${wordBreakBruteForce("leetcode", listOf("leet", "code"))}")
    println("Memoization 'leetcode': ${wordBreakMemo("leetcode", listOf("leet", "code"))}")
    println("Tabulation  'leetcode': ${wordBreakTabulation("leetcode", listOf("leet", "code"))}")
    println("---")
    println("Optimal 'applepenapple': ${wordBreakTabulation("applepenapple", listOf("apple", "pen"))}")
    println("Optimal 'catsandog':    ${wordBreakTabulation("catsandog", listOf("cats", "dog", "sand", "and", "cat"))}")
}

/**
 * BRUTE FORCE — Recursion (try all splits)
 * Time Complexity: O(2^N) — at each position, try all words
 * Space Complexity: O(N) — recursion stack
 *
 * For each position, try every word. If it matches, recurse from the new position.
 *
 * Recursion tree for "leetcode", dict=["leet","code"]:
 *                    f(0)
 *                  /      \
 *         "leet"✗  wait, "leet" matches s[0..3]
 *         f(4) ← "leet" matches!
 *           "code" matches s[4..7]!
 *           f(8) → true ✅
 *
 * For "catsandog", dict=["cats","dog","sand","and","cat"]:
 *   f(0) → "cat" matches → f(3) → "sand" matches → f(7) → "og" no match → false
 *                       → "cats" matches → f(4) → "and" matches → f(7) → same
 *   All paths fail → false
 *
 * Exponential because same positions are revisited many times.
 */
fun wordBreakBruteForce(s: String, wordDict: List<String>): Boolean {
    val wordSet = wordDict.toSet()
    return wbRec(s, 0, wordSet)
}

private fun wbRec(s: String, start: Int, wordSet: Set<String>): Boolean {
    if (start == s.length) return true

    for (end in start + 1..s.length) {
        if (s.substring(start, end) in wordSet && wbRec(s, end, wordSet)) {
            return true
        }
    }
    return false
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N × N × W) — N positions, each checks N endpoints × W word match
 * Space Complexity: O(N) — memo + recursion stack
 *
 * Cache result for each starting position.
 *
 * Trace for "leetcode", dict=["leet","code"]:
 * f(0): try end=1..4, "leet" matches s[0..3] → f(4)
 *   f(4): try end=5..8, "code" matches s[4..7] → f(8)
 *     f(8) = true (base case, cache!)
 *   f(4) = true (cache!)
 * f(0) = true ✅
 *
 * Trace for "catsandog", dict=["cats","dog","sand","and","cat"]:
 * f(0): "cat" matches → f(3), "cats" matches → f(4)
 *   f(3): "sand" matches → f(7), "and" no (s[3..6]="sand"≠"and")
 *     f(7): no word matches s[7..] → false (cache!)
 *   f(3) = false (cache!)
 *   f(4): "and" matches → f(7) = false (cached!)
 *   f(4) = false (cache!)
 * f(0) = false ✅
 */
fun wordBreakMemo(s: String, wordDict: List<String>): Boolean {
    val wordSet = wordDict.toSet()
    val memo = BooleanArray(s.length) { false }
    val computed = BooleanArray(s.length) { false }
    return wbMemo(s, 0, wordSet, memo, computed)
}

private fun wbMemo(s: String, start: Int, wordSet: Set<String>, memo: BooleanArray, computed: BooleanArray): Boolean {
    if (start == s.length) return true
    if (computed[start]) return memo[start]

    for (end in start + 1..s.length) {
        if (s.substring(start, end) in wordSet && wbMemo(s, end, wordSet, memo, computed)) {
            memo[start] = true
            computed[start] = true
            return true
        }
    }
    memo[start] = false
    computed[start] = true
    return false
}

/**
 * OPTIMAL — Bottom-Up DP (Tabulation)
 * Time Complexity: O(N × N × W) — N positions, substring check for each word
 * Space Complexity: O(N)
 *
 * dp[i] = true if s[0..i-1] can be segmented
 * dp[0] = true (empty string)
 *
 * For each position i, check all words:
 *   if dp[i - word.length] AND s.substring(i - word.length, i) == word → dp[i] = true
 *
 * Trace for "leetcode", dict=["leet","code"]:
 * dp = [T, F, F, F, F, F, F, F, F]
 *
 * i=4: dp[4-4]=dp[0]=T, s[0..3]="leet"✓ → dp[4]=T
 * i=8: dp[8-4]=dp[4]=T, s[4..7]="code"✓ → dp[8]=T
 *
 * dp[8] = true ✅
 *
 * OPTIMIZATION: Instead of checking all words at each position, only check
 * words whose length ≤ i. This avoids unnecessary substring operations.
 */
fun wordBreakTabulation(s: String, wordDict: List<String>): Boolean {
    val wordSet = wordDict.toSet()
    val n = s.length
    val dp = BooleanArray(n + 1)
    dp[0] = true

    for (i in 1..n) {
        for (word in wordSet) {
            if (word.length <= i && dp[i - word.length] && s.substring(i - word.length, i) == word) {
                dp[i] = true
                break  // found one valid split, no need to check more
            }
        }
    }
    return dp[n]
}
