package two_pointer_technique

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
    println(backspaceStringCompareBF("a##c", "#a#c"))
    println(backspaceStringCompareOP("a##c", "#a#c"))
}

/**
 * Time complexity O(m+n)
 * Space Complexity O(1)
 */
fun backspaceStringCompareBF(s : String,t : String) : Boolean {
    val s1 = helperToRemoveHash(s)
    val t1 = helperToRemoveHash(t)
    return s1 == t1
}

fun helperToRemoveHash(s : String) : String {
    val charArray = mutableListOf<Char>()
    for (i in s.indices) {
        if (s[i] != '#') {
            charArray.add(s[i])
        } else {
            if (charArray.size >= 1)
                charArray.removeAt(charArray.size - 1)
        }
    }

    return charArray.toString()
}

/**
 * Time Complexity O(N+M)
 * Space Complexity O(N)
 */
fun backspaceStringCompareOP(s : String,t : String) : Boolean {
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