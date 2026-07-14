package dp.one_d.decode_ways

/**
 * Decode Ways — LeetCode #91
 * https://leetcode.com/problems/decode-ways/
 *
 * Problem:
 * -------
 * A message containing letters A-Z can be encoded into numbers: 'A'→1, 'B'→2, ..., 'Z'→26.
 * Given a string of digits, return the number of ways to decode it.
 *
 * Example:  "12" → 2  (AB or L)
 *           "226" → 3  (BBF, BZ, VF)
 *           "06" → 0   (invalid — leading zero)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic DP — conditional Fibonacci)
 *
 * Recurrence: dp[i] = dp[i-1] (if s[i-1] valid) + dp[i-2] (if s[i-2..i-1) valid)
 * Base case: dp[0] = 1 (empty string)
 *
 * Two approaches:
 * 1. Brute Force Recursion: O(2^N) — try 1-digit and 2-digit decodings
 * 2. Space-Optimized DP: O(N) time, O(1) space
 */

fun main() {
    println("=== Method 1: Brute Force Recursion ===")
    println("decode(\"12\") = ${decodeWaysBruteForce("12")}")
    println("decode(\"226\") = ${decodeWaysBruteForce("226")}")

    println("\n=== Method 2: Space-Optimized DP ===")
    println("decode(\"12\") = ${decodeWaysOptimal("12")}")
    println("decode(\"226\") = ${decodeWaysOptimal("226")}")

    println("\n=== Step-by-step trace ===")
    decodeWaysTrace("226")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Recursion (try 1-digit and 2-digit)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — At each position, try decoding 1 digit (if valid) or 2 digits (if valid).
 *
 * Core Idea:
 *   - Take 1 digit: valid if '1'-'9' (not '0').
 *   - Take 2 digits: valid if 10-26.
 *   - Sum both possibilities.
 *
 * Time Complexity:  O(2^N) — exponential.
 * Space Complexity: O(N) — recursion stack.
 */
fun decodeWaysBruteForce(s: String): Int {
    return decode(s, 0)
}

private fun decode(s: String, i: Int): Int {
    if (i == s.length) return 1  // Reached end — valid decoding.
    if (s[i] == '0') return 0    // Leading zero — invalid.

    // Take 1 digit.
    var ways = decode(s, i + 1)

    // Take 2 digits (if valid: 10-26).
    if (i + 1 < s.length) {
        val twoDigit = (s[i] - '0') * 10 + (s[i + 1] - '0')
        if (twoDigit in 10..26) {
            ways += decode(s, i + 2)
        }
    }
    return ways
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SPACE-OPTIMIZED DP (O(1) space)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SPACE-OPTIMIZED DP — dp[i] only depends on dp[i-1] and dp[i-2]. Use two variables.
 *
 * Core Idea:
 *   - dp[i] = dp[i-1] (if 1-digit valid) + dp[i-2] (if 2-digit valid).
 *   - 1-digit valid: s[i] != '0'.
 *   - 2-digit valid: s[i-1..i] in 10..26.
 *
 * Key Insight:
 *   - Same as Climbing Stairs, but with CONDITIONS on which steps are valid.
 *   - '0' blocks the 1-digit path. Two-digit must be 10-26.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1) — two variables.
 */
fun decodeWaysOptimal(s: String): Int {
    if (s.isEmpty() || s[0] == '0') return 0
    var prev2 = 1  // dp[i-2]
    var prev1 = 1  // dp[i-1] — dp[0] = 1 (empty string)

    for (i in 1 until s.length) {
        var curr = 0
        // 1-digit: valid if current digit is not '0'.
        if (s[i] != '0') curr = prev1
        // 2-digit: valid if s[i-1..i] is in 10..26.
        val twoDigit = (s[i - 1] - '0') * 10 + (s[i] - '0')
        if (twoDigit in 10..26) curr += prev2

        prev2 = prev1
        prev1 = curr
    }
    return prev1
}

/**
 * Space-optimized DP with step-by-step trace.
 */
fun decodeWaysTrace(s: String) {
    println("Input: \"$s\"")
    if (s.isEmpty() || s[0] == '0') {
        println("  Result: 0 (invalid start)")
        return
    }
    var prev2 = 1
    var prev1 = 1
    println("  Base: prev2=1 (dp[0]), prev1=1 (dp[1])")

    for (i in 1 until s.length) {
        var curr = 0
        if (s[i] != '0') {
            curr = prev1
            println("  i=$i: 1-digit '${s[i]}' valid → curr=$curr")
        } else {
            println("  i=$i: 1-digit '${s[i]}' invalid (zero)")
        }
        val twoDigit = (s[i - 1] - '0') * 10 + (s[i] - '0')
        if (twoDigit in 10..26) {
            curr += prev2
            println("       2-digit '$twoDigit' valid → curr=$curr")
        }
        prev2 = prev1
        prev1 = curr
    }
    println("  Result: $prev1")
}
