package array.linear_scan.reverse_a_string

/**
 * https://leetcode.com/problems/reverse-string/
 *
 * Write a function that reverses a string (represented as a char array).
 * The input is given as a character array `s`, and you must modify it **in-place**
 * with O(1) extra space.
 *
 * Constraints:
 *   1 <= s.length <= 10^5
 *   s[i] is a printable ascii character
 *
 * Example 1:
 *   Input:  s = ['h','e','l','l','o']
 *   Output: ['o','l','l','e','h']
 *
 * Example 2:
 *   Input:  s = ['H','a','n','n','a','h']
 *   Output: ['h','a','n','n','a','H']
 */
fun main() {
    val s1 = charArrayOf('h', 'e', 'l', 'l', 'o')
    reverseStringBF(s1)
    println(s1.toList()) // [o, l, l, e, h]

    val s2 = charArrayOf('h', 'e', 'l', 'l', 'o')
    reverseStringOP(s2)
    println(s2.toList()) // [o, l, l, e, h]
}

/**
 * Brute Force — Extra Array
 *
 * Create a new array and copy elements in reverse order.
 * This uses O(N) extra space, which violates the in-place constraint but
 * illustrates the basic idea.
 *
 * Time Complexity:  O(N) — single pass to copy
 * Space Complexity: O(N) — extra array of size N
 */
fun reverseStringBF(s: CharArray) {
    val n = s.size
    val temp = CharArray(n)
    for (i in 0 until n) {
        temp[i] = s[n - 1 - i]
    }
    // Copy back into original array
    for (i in 0 until n) {
        s[i] = temp[i]
    }
}

/**
 * Optimal — Two Pointers (In-Place Swap)
 *
 * Use two pointers: left (start) and right (end). Swap s[left] and s[right],
 * then move both pointers inward until they meet in the middle.
 *
 * Trace for s = ['h','e','l','l','o']:
 *
 *   left=0, right=4: swap 'h' and 'o' → ['o','e','l','l','h']
 *   left=1, right=3: swap 'e' and 'l' → ['o','l','l','e','h']
 *   left=2, right=2: left >= right → stop
 *
 *   Result = ['o','l','l','e','h'] ✅
 *
 * Time Complexity:  O(N) — each element visited once (N/2 swaps)
 * Space Complexity: O(1) — in-place, only a temp variable
 */
fun reverseStringOP(s: CharArray) {
    var left = 0
    var right = s.size - 1

    while (left < right) {
        // Swap s[left] and s[right]
        val temp = s[left]
        s[left] = s[right]
        s[right] = temp

        left++
        right--
    }
}
