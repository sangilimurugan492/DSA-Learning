package array.two_pointer.valid_palindrom

/**
 * https://leetcode.com/problems/valid-palindrome/
 *
 * Given a string s, return true if it is a palindrome (considering only alphanumeric).
 *
 * Example: "A man, a plan, a canal: Panama" → true
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Classic warm-up)
 */

fun main() {
    println(isPalindromeBruteForce("A man, a plan, a canal: Panama"))
    println(isPalindromeBruteForce("race a car"))
    println("---")
    println(isPalindromeTwoPointer("A man, a plan, a canal: Panama"))
    println(isPalindromeTwoPointer("race a car"))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N) — clean string + reverse
 * Space Complexity: O(N) — cleaned string + reversed copy
 *
 * Clean the string (lowercase + alphanumeric only), reverse, compare.
 */
fun isPalindromeBruteForce(s: String): Boolean {
    val cleaned = s.lowercase().filter { it.isLetterOrDigit() }
    return cleaned == cleaned.reversed()
}

/**
 * OPTIMAL — Two Pointer
 * Time Complexity: O(N) — single pass
 * Space Complexity: O(1) — no extra string
 *
 * Compare from both ends, skipping non-alphanumeric characters.
 */
fun isPalindromeTwoPointer(s: String): Boolean {
    var left = 0
    var right = s.length - 1

    while (left < right) {
        while (left < right && !s[left].isLetterOrDigit()) left++
        while (left < right && !s[right].isLetterOrDigit()) right--
        if (s[left].lowercaseChar() != s[right].lowercaseChar()) return false
        left++
        right--
    }
    return true
}
