package two_pointer_technique

/**
 * https://leetcode.com/problems/valid-palindrome/description/
 *
 * A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward. Alphanumeric characters include letters and numbers.
 *
 * Given a string s, return true if it is a palindrome, or false otherwise.
 *
 * Example 1:
 *
 * Input: s = "A man, a plan, a canal: Panama"
 * Output: true
 * Explanation: "amanaplanacanalpanama" is a palindrome.
 * Example 2:
 *
 * Input: s = "race a car"
 * Output: false
 * Explanation: "raceacar" is not a palindrome.
 */
fun main() {

    println(isValidPalindromeBF("A man, a plan, a canal: Panama"))
    println(isValidPalindromeOP("A man, a plan, a canal: Panama"))
}

/**
 * Time Complexity: $O(n)$ — We traverse the string a few times (to clean it and to reverse it).
 * Space Complexity: $O(n)$ — We store the cleaned version and the reversed version of the string.
 */
fun isValidPalindromeBF(s: String): Boolean {
    val cleaned = StringBuilder()

    for (char in s) {
        if (char.isLetterOrDigit()) {
            cleaned.append(char.lowercaseChar())
        }
    }

    val filteredString = cleaned.toString()
    val reversedString = filteredString.reversed()

    return filteredString == reversedString
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun isValidPalindromeOP(s: String): Boolean {
    var left = 0
    var right = s.length - 1

    while (left < right) {
        // Skip non-alphanumeric from left
        if (!s[left].isLetterOrDigit()) {
            left++
        }
        // Skip non-alphanumeric from right
        else if (!s[right].isLetterOrDigit()) {
            right--
        }
        // Both are alphanumeric, compare them
        else {
            if (s[left].lowercaseChar() != s[right].lowercaseChar()) {
                return false
            }
            left++
            right--
        }
    }
    return true
}