package array.two_pointer.backspace_string_compare

/**
 * https://leetcode.com/problems/backspace-string-compare/description/
 * Given two strings s and t, return true if they are equal when both are typed into empty text editors. '#' means a backspace character.
 *
 * Note that after backspacing an empty text, the text will continue empty.
 *
 * Example 1:
 *
 * Input: s = "ab#c", t = "ad#c"
 * Output: true
 * Explanation: Both s and t become "ac".
 * Example 2:
 *
 * Input: s = "ab##", t = "c#d#"
 * Output: true
 * Explanation: Both s and t become "".
 */
fun main() {
    println(backspaceStringCompareStack("a##c", "#a#c"))      // true
    println(backspaceStringCompareTwoPointer("a##c", "#a#c")) // true
}

/**
 * Stack-based approach: Build the final string by simulating backspaces using a list.
 *
 * Time complexity:  O(m + n)  — each character is processed once
 * Space Complexity: O(m + n) — extra storage for the processed strings
 */
fun backspaceStringCompareStack(s: String, t: String): Boolean {
    val s1 = helperToRemoveHash(s)
    val t1 = helperToRemoveHash(t)
    return s1 == t1
}

fun helperToRemoveHash(s: String): String {
    val charArray = mutableListOf<Char>()
    for (i in s.indices) {
        if (s[i] != '#') {
            charArray.add(s[i])
        } else {
            if (charArray.isNotEmpty())
                charArray.removeAt(charArray.size - 1)
        }
    }
    return String(charArray.toCharArray())
}

/**
 * Two-pointer approach: Traverse both strings from the end, skipping backspaced characters.
 * This achieves O(1) space by not building any intermediate strings.
 *
 * Time Complexity:  O(m + n) — each character is visited at most once
 * Space Complexity: O(1)    — only a few integer variables are used
 */
fun backspaceStringCompareTwoPointer(s: String, t: String): Boolean {
    var i = s.length - 1
    var j = t.length - 1
    var skipS = 0
    var skipT = 0

    while (i >= 0 || j >= 0) {
        // Find next valid character in S
        while (i >= 0) {
            if (s[i] == '#') { skipS++; i-- }
            else if (skipS > 0) { skipS--; i-- }
            else break
        }

        // Find next valid character in T
        while (j >= 0) {
            if (t[j] == '#') { skipT++; j-- }
            else if (skipT > 0) { skipT--; j-- }
            else break
        }

        // Compare the surviving characters
        if (i >= 0 && j >= 0 && s[i] != t[j]) return false

        // If one string is exhausted but the other isn't
        if ((i >= 0) != (j >= 0)) return false

        i--
        j--
    }
    return true
}
