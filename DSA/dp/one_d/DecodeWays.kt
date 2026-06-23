package dp.one_d

/**
 * https://leetcode.com/problems/decode-ways/
 *
 * A message containing letters A-Z can be encoded into numbers using:
 * 'A' → "1", 'B' → "2", ... 'Z' → "26"
 * Given a string s containing only digits, return the number of ways to decode it.
 *
 * Example 1: s = "12" → Output: 2 ("AB" or "L")
 * Example 2: s = "226" → Output: 3 ("BZ", "VF", "BBF")
 * Example 3: s = "06" → Output: 0 ("06" can't be mapped — leading zero)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic 1D DP — decision at each position)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is Climbing Stairs with CONSTRAINTS!
 *
 * Climbing Stairs: take 1 step or 2 steps → ways(n) = ways(n-1) + ways(n-2)
 * Decode Ways:     take 1 digit or 2 digits → BUT with validity checks!
 *
 * At each position i, we have TWO choices:
 *   1. Take 1 digit: s[i] must be '1'-'9' (can't be '0' — no mapping)
 *      If valid → add dp[i-1] ways
 *   2. Take 2 digits: s[i-1..i] must form a number 10-26
 *      If valid → add dp[i-2] ways
 *
 * Recurrence: dp[i] = (valid1 ? dp[i-1] : 0) + (valid2 ? dp[i-2] : 0)
 *
 * WHY these constraints?
 *   - '0' alone is INVALID (no letter maps to 0)
 *   - '06' is INVALID (leading zero, not "6")
 *   - '27' is INVALID as 2-digit (> 26)
 *   - '10' and '20' are VALID (J and T) — only valid 2-dits starting with 0
 *
 * Base cases: dp[0] = 1 (empty string = 1 way), dp[1] = 1 if s[0] != '0'
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Decode Ways ===")
    println("Brute Force '12':   ${decodeWaysBruteForce("12")}")
    println("Memoization '12':   ${decodeWaysMemo("12")}")
    println("Tabulation  '12':   ${decodeWaysTabulation("12")}")
    println("Optimal     '12':   ${decodeWaysOptimal("12")}")
    println("---")
    println("Optimal '226':      ${decodeWaysOptimal("226")}")
    println("Optimal '06':      ${decodeWaysOptimal("06")}")
    println("Optimal '11106':   ${decodeWaysOptimal("11106")}")
}

/**
 * BRUTE FORCE — Recursion
 * Time Complexity: O(2^N) — at each position, up to 2 choices
 * Space Complexity: O(N) — recursion stack
 *
 * At each index, try taking 1 digit or 2 digits (if valid).
 *
 * Recursion tree for "226":
 *                    f(0)
 *                  /      \
 *           take1:'2'   take2:"22"
 *            f(1)         f(2)
 *          /     \         |
 *     take1:'2' take2:"26" take1:'6'
 *      f(2)     f(3)=1✅    f(3)=1✅
 *    /     \
 * take1:'6' take2:"6✗"
 *  f(3)=1✅
 *
 * f(2) computed TWICE! Overlapping subproblems.
 */
fun decodeWaysBruteForce(s: String): Int {
    return decodeRec(s, 0)
}

private fun decodeRec(s: String, i: Int): Int {
    if (i == s.length) return 1  // reached end = valid decoding

    // Take 1 digit
    var ways = 0
    if (s[i] != '0') {
        ways += decodeRec(s, i + 1)
    }

    // Take 2 digits
    if (i + 1 < s.length) {
        val twoDigit = (s[i] - '0') * 10 + (s[i + 1] - '0')
        if (twoDigit in 10..26) {
            ways += decodeRec(s, i + 2)
        }
    }
    return ways
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N) — each index computed once
 * Space Complexity: O(N) — memo + recursion stack
 *
 * Cache result for each starting index.
 *
 * Trace for "226":
 * f(0): take1='2' → f(1), take2="22" → f(2)
 *   f(1): take1='2' → f(2), take2="26" → f(3)
 *     f(2): take1='6' → f(3)
 *       f(3) = 1 (base case, cache!)
 *     f(2) = 1 (cache!)
 *     f(3) = 1 (cached!)
 *   f(1) = 1 + 1 = 2 (cache!)
 *   f(2) = 1 (cached!)
 * f(0) = 2 + 1 = 3 ✅
 */
fun decodeWaysMemo(s: String): Int {
    val memo = IntArray(s.length) { -1 }
    return decodeMemo(s, 0, memo)
}

private fun decodeMemo(s: String, i: Int, memo: IntArray): Int {
    if (i == s.length) return 1
    if (memo[i] != -1) return memo[i]

    var ways = 0
    if (s[i] != '0') {
        ways += decodeMemo(s, i + 1, memo)
    }
    if (i + 1 < s.length) {
        val twoDigit = (s[i] - '0') * 10 + (s[i + 1] - '0')
        if (twoDigit in 10..26) {
            ways += decodeMemo(s, i + 2, memo)
        }
    }
    memo[i] = ways
    return ways
}

/**
 * OPTIMAL-1 — Bottom-Up DP (Tabulation)
 * Time Complexity: O(N)
 * Space Complexity: O(N)
 *
 * dp[i] = number of ways to decode s[0..i-1]
 * dp[0] = 1 (empty string)
 * dp[1] = 1 if s[0] != '0', else 0
 *
 * For i from 2 to n:
 *   if s[i-1] != '0': dp[i] += dp[i-1]     (take 1 digit)
 *   if s[i-2..i-1] in 10..26: dp[i] += dp[i-2]  (take 2 digits)
 *
 * Trace for "226":
 * dp[0]=1, dp[1]=1 (s[0]='2' ≠ '0')
 * i=2: s[1]='2'≠'0' → dp[2]+=dp[1]=1; "22" in 10..26 → dp[2]+=dp[0]=2
 * i=3: s[2]='6'≠'0' → dp[3]+=dp[2]=2; "26" in 10..26 → dp[3]+=dp[1]=3
 *
 * dp[3] = 3 ✅
 */
fun decodeWaysTabulation(s: String): Int {
    if (s.isEmpty() || s[0] == '0') return 0
    val n = s.length
    val dp = IntArray(n + 1)
    dp[0] = 1
    dp[1] = 1

    for (i in 2..n) {
        // Take 1 digit
        if (s[i - 1] != '0') {
            dp[i] += dp[i - 1]
        }
        // Take 2 digits
        val twoDigit = (s[i - 2] - '0') * 10 + (s[i - 1] - '0')
        if (twoDigit in 10..26) {
            dp[i] += dp[i - 2]
        }
    }
    return dp[n]
}

/**
 * OPTIMAL-2 — Space-Optimized Bottom-Up DP
 * Time Complexity: O(N)
 * Space Complexity: O(1) ← only 2 variables!
 *
 * dp[i] only depends on dp[i-1] and dp[i-2].
 * Same as Climbing Stairs space optimization.
 *
 * Trace for "226":
 * prev2=1, prev1=1
 * i=2: s[1]='2'≠'0' → curr+=1; "22"∈10..26 → curr+=1=2; prev2=1, prev1=2
 * i=3: s[2]='6'≠'0' → curr+=2; "26"∈10..26 → curr+=1=3; prev2=2, prev1=3
 * Result: 3 ✅
 */
fun decodeWaysOptimal(s: String): Int {
    if (s.isEmpty() || s[0] == '0') return 0
    var prev2 = 1  // dp[i-2]
    var prev1 = 1  // dp[i-1]

    for (i in 2..s.length) {
        var curr = 0
        if (s[i - 1] != '0') {
            curr += prev1
        }
        val twoDigit = (s[i - 2] - '0') * 10 + (s[i - 1] - '0')
        if (twoDigit in 10..26) {
            curr += prev2
        }
        prev2 = prev1
        prev1 = curr
    }
    return prev1
}
