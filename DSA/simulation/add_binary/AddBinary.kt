package simulation.add_binary

/**
 * Add Binary — LeetCode #67
 * https://leetcode.com/problems/add-binary/
 *
 * Problem:
 * -------
 * Given two binary strings a and b, return their sum as a binary string.
 *
 * Example:  a = "11", b = "1" → "100"
 *           a = "1010", b = "1011" → "10101"
 *
 * FAANG Importance: ⭐⭐⭐⭐
 *
 * Two approaches:
 * 1. Built-in Conversion: O(N) — parse to BigInteger, add, convert back to binary
 * 2. Digit-by-Digit with Carry: O(N) — traverse right to left, add bits + carry
 */

fun main() {
    val a = "1010"
    val b = "1011"

    println("=== Method 1: Built-in Conversion ===")
    println("addBinary(\"$a\", \"$b\") = \"${addBinaryBuiltin(a, b)}\"")

    println("\n=== Method 2: Digit-by-Digit with Carry ===")
    println("addBinary(\"$a\", \"$b\") = \"${addBinary(a, b)}\"")

    println("\n=== Step-by-step trace ===")
    addBinaryTrace("11", "1")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BUILT-IN CONVERSION — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BUILT-IN CONVERSION — Parse to BigInteger, add, convert back to binary string.
 *
 * Note: Simple but not always accepted in interviews (they want manual addition).
 *
 * Time Complexity:  O(N).
 * Space Complexity: O(N).
 */
fun addBinaryBuiltin(a: String, b: String): String {
    val numA = a.toBigInteger(2)
    val numB = b.toBigInteger(2)
    return (numA + numB).toString(2)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: DIGIT-BY-DIGIT WITH CARRY — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DIGIT-BY-DIGIT — Traverse both strings right to left. Add bits + carry. Append result.
 *
 * Core Idea:
 *   - Start from the rightmost bit of both strings.
 *   - sum = bit_a + bit_b + carry.
 *   - result bit = sum % 2, carry = sum / 2.
 *   - Reverse at the end (we built it backwards).
 *
 * Key Insight:
 *   - Binary addition: 0+0=0, 0+1=1, 1+1=10 (carry 1), 1+1+1=11 (carry 1).
 *   - sum % 2 gives the result bit, sum / 2 gives the carry.
 *
 * Time Complexity:  O(max(N, M)) — traverse the longer string.
 * Space Complexity: O(max(N, M)) — result string.
 */
fun addBinary(a: String, b: String): String {
    val result = StringBuilder()
    var i = a.length - 1
    var j = b.length - 1
    var carry = 0

    while (i >= 0 || j >= 0 || carry == 1) {
        var sum = carry
        if (i >= 0) sum += a[i--] - '0'
        if (j >= 0) sum += b[j--] - '0'
        result.append(sum % 2)
        carry = sum / 2
    }

    return result.reverse().toString()
}

/**
 * Digit-by-digit with step-by-step trace.
 */
fun addBinaryTrace(a: String, b: String) {
    println("Input: a=\"$a\", b=\"$b\"")
    val result = StringBuilder()
    var i = a.length - 1
    var j = b.length - 1
    var carry = 0

    while (i >= 0 || j >= 0 || carry == 1) {
        var sum = carry
        if (i >= 0) { sum += a[i] - '0'; println("  a[$i]=${a[i]}"); i-- }
        if (j >= 0) { sum += b[j] - '0'; println("  b[$j]=${b[j]}"); j-- }
        result.append(sum % 2)
        println("  sum=$sum, bit=${sum % 2}, carry=${sum / 2}")
        carry = sum / 2
    }
    println("  Result (reversed): ${result.reverse()}")
}
