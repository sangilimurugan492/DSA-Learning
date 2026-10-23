package patterns.greedy.lexicographically_largest_string

/**
 * Lexicographically Largest String with at most K character changes
 *
 * Problem:
 *   Given a string `s` (lowercase English letters) and an integer `k`,
 *   you may change at most `k` characters to any lowercase letter.
 *   Return the lexicographically largest string possible.
 *
 * Example:
 *   s = "abcz", k = 2  →  "zzcz"
 *   s = "abcde", k = 3 →  "zzzde"
 *   s = "hello", k = 1 →  "iello"  (wait — 'z' is largest, so "zello")
 *
 * Strategy (Greedy):
 *   - Scan left → right (most significant position first).
 *   - At each position, if the current char is not 'z' and we still have
 *     operations left, replace it with 'z' and decrement k.
 *   - Once k reaches 0, the rest of the string stays unchanged.
 *
 * Why greedy works:
 *   The leftmost character has the highest lexicographic weight.
 *   Replacing an earlier non-'z' char with 'z' always yields a larger
 *   string than replacing a later char, so we should always spend
 *   operations left-to-right.
 *
 * Time:  O(n)  — single pass
 * Space: O(n)  — for the result char array
 */

fun main() {
    println(lexicographicallyLargest("abcz", 2))    // "zzcz"
    println(lexicographicallyLargest("abcde", 3))    // "zzzde"
    println(lexicographicallyLargest("hello", 1))   // "zello"
    println(lexicographicallyLargest("zzzz", 2))    // "zzzz"  (already max)
    println(lexicographicallyLargest("aaaa", 4))    // "zzzz"
    println(lexicographicallyLargest("abc", 0))     // "abc"   (no ops)
}

/**
 * Greedy left-to-right replacement.
 *
 * @param s  input string of lowercase letters
 * @param k  max number of character replacements allowed
 * @return   lexicographically largest string achievable
 */
fun lexicographicallyLargest(s: String, k: Int): String {
    val chars = s.toCharArray()
    var remaining = k

    for (i in chars.indices) {
        if (remaining == 0) break          // no operations left → stop
        if (chars[i] != 'z') {
            chars[i] = 'z'                  // upgrade to 'z'
            remaining--
        }
        // if already 'z', skip (no operation needed)
    }
    return String(chars)
}

/* ================================================================
 *  DRY RUN
 * ================================================================
 *
 *  Example 1:  s = "abcz",  k = 2
 *  ┌─────┬───────┬────────────┬───────────┬──────────┐
 *  │  i  │ char  │ remaining  │ action     │ string   │
 *  ├─────┼───────┼────────────┼───────────┼──────────┤
 *  │  0  │  'a'  │     2      │ → 'z'     │ "zbcz"   │
 *  │  1  │  'b'  │     1      │ → 'z'     │ "zzcz"   │
 *  │  2  │  'c'  │     0      │ stop      │ "zzcz"   │
 *  └─────┴───────┴────────────┴───────────┴──────────┘
 *  Result: "zzcz"
 *
 *  Example 2:  s = "abcde",  k = 3
 *  ┌─────┬───────┬────────────┬───────────┬──────────┐
 *  │  i  │ char  │ remaining  │ action     │ string   │
 *  ├─────┼───────┼────────────┼───────────┼──────────┤
 *  │  0  │  'a'  │     3      │ → 'z'     │ "zbcde"  │
 *  │  1  │  'b'  │     2      │ → 'z'     │ "zzcde"  │
 *  │  2  │  'c'  │     1      │ → 'z'     │ "zzzde"  │
 *  │  3  │  'd'  │     0      │ stop      │ "zzzde"  │
 *  └─────┴───────┴────────────┴───────────┴──────────┘
 *  Result: "zzzde"
 *
 *  Example 3:  s = "hello",  k = 1
 *  ┌─────┬───────┬────────────┬───────────┬──────────┐
 *  │  i  │ char  │ remaining  │ action     │ string   │
 *  ├─────┼───────┼────────────┼───────────┼──────────┤
 *  │  0  │  'h'  │     1      │ → 'z'     │ "zello"  │
 *  │  1  │  'e'  │     0      │ stop      │ "zello"  │
 *  └─────┴───────┴────────────┴───────────┴──────────┘
 *  Result: "zello"
 *
 *  Example 4:  s = "zzzz",  k = 2  (already maximal)
 *  ┌─────┬───────┬────────────┬───────────┬──────────┐
 *  │  i  │ char  │ remaining  │ action     │ string   │
 *  ├─────┼───────┼────────────┼───────────┼──────────┤
 *  │  0  │  'z'  │     2      │ skip       │ "zzzz"   │
 *  │  1  │  'z'  │     2      │ skip       │ "zzzz"   │
 *  │  2  │  'z'  │     2      │ skip       │ "zzzz"   │
 *  │  3  │  'z'  │     2      │ skip       │ "zzzz"   │
 *  └─────┴───────┴────────────┴───────────┴──────────┘
 *  Result: "zzzz"  (no changes needed; k stays 2)
 *
 * ================================================================ */
