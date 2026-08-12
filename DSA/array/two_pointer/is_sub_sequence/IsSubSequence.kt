package array.two_pointer.is_sub_sequence

/**
 * https://leetcode.com/problems/is-subsequence/description
 *
 * Given two strings s and t, return true if s is a subsequence of t.
 * A subsequence is formed by deleting some characters of t without
 * changing the order of the remaining characters.
 *
 * Example: s = "abc", t = "ahbgdc" → true
 *          s = "axc", t = "ahbgdc" → false
 *
 * Three approaches:
 * 1. Brute Force (Nested Loops): For each char in s, search through t
 * 2. Two Pointer (Optimal): Single pass through t
 * 3. Recursive: Match or skip
 */
fun main() {
    println("Brute Force:")
    println(isSubsequenceBF("abc", "ahbgdc"))   // true
    println(isSubsequenceBF("axc", "ahbgdc"))   // false
    println("Two Pointer:")
    println(isSubsequence("abc", "ahbgdc"))     // true
    println(isSubsequence("axc", "ahbgdc"))     // false
    println("Recursive:")
    println(isSubsequenceRecursive("abc", "ahbgdc"))  // true
}

/**
 * Brute Force (Nested Loops): For each character in s, scan through t
 * starting from where we left off, looking for a match.
 *
 * Step-by-step:
 * 1. Start with searchPos = 0 (where to start searching in t).
 * 2. For each character s[i]:
 *    a. Scan t from searchPos forward until we find s[i].
 *    b. If found at index j → update searchPos = j + 1 (next search starts after this match).
 *    c. If not found → return false (can't form subsequence).
 * 3. If all characters found → return true.
 *
 * Walkthrough: s = "abc", t = "ahbgdc"
 *
 *   i=0, s[0]='a': scan t from 0 → t[0]='a' match! searchPos=1
 *   i=1, s[1]='b': scan t from 1 → t[1]='h' no, t[2]='b' match! searchPos=3
 *   i=2, s[2]='c': scan t from 3 → t[3]='g' no, t[4]='d' no, t[5]='c' match! searchPos=6
 *   All matched → true ✅
 *
 * Walkthrough: s = "axc", t = "ahbgdc"
 *
 *   i=0, s[0]='a': scan t from 0 → t[0]='a' match! searchPos=1
 *   i=1, s[1]='x': scan t from 1 → t[1]='h','b','g','d','c' → 'x' NOT found
 *   Return false ✅
 *
 * Time Complexity:  O(M × N) — for each char in s (M), scan through t (N)
 * Space Complexity: O(1)     — only index variables
 */
fun isSubsequenceBF(s: String, t: String): Boolean {
    if (s.isEmpty()) return true
    if (t.isEmpty()) return false

    var searchPos = 0 // Where to start searching in t

    for (i in s.indices) {
        var found = false
        // Scan t from searchPos to find s[i]
        for (j in searchPos until t.length) {
            if (s[i] == t[j]) {
                searchPos = j + 1 // Next search starts after this match
                found = true
                break
            }
        }
        if (!found) return false // Character not found → not a subsequence
    }

    return true
}

/**
 * Two Pointer (Optimal): Iterate through t in a single pass, matching
 * characters of s in order. Advance s pointer only on match.
 *
 * Step-by-step:
 * 1. Set sIndex = 0.
 * 2. For each character in t:
 *    a. If s[sIndex] == t[tIndex] → sIndex++ (found a match).
 * 3. After scanning all of t, if sIndex == s.length → all matched → true.
 *
 * Walkthrough: s = "abc", t = "ahbgdc"
 *
 *   t[0]='a' == s[0]='a' → sIndex=1
 *   t[1]='h' != s[1]='b' → skip
 *   t[2]='b' == s[1]='b' → sIndex=2
 *   t[3]='g' != s[2]='c' → skip
 *   t[4]='d' != s[2]='c' → skip
 *   t[5]='c' == s[2]='c' → sIndex=3
 *   sIndex=3 == s.length → true ✅
 *
 * Time Complexity:  O(N) — single pass through t
 * Space Complexity: O(1)
 */
fun isSubsequence(s: String, t: String): Boolean {
    if (s.isEmpty()) return true
    if (t.isEmpty()) return false

    var sIndex = 0

    for (tIndex in t.indices) {
        if (sIndex < s.length && s[sIndex] == t[tIndex]) {
            sIndex++
        }
    }

    return sIndex == s.length
}

/**
 * Recursive approach: For each position, either match and advance both,
 * or skip the current character in t.
 *
 * Step-by-step:
 * 1. Base case 1: i == s.length → all matched → true.
 * 2. Base case 2: j == t.length → t exhausted → false.
 * 3. If s[i] == t[j] → match! Recurse with (i+1, j+1).
 * 4. If s[i] != t[j] → skip t[j]. Recurse with (i, j+1).
 *
 * Time Complexity:  O(N) — where N is length of t
 * Space Complexity: O(N) — recursion stack
 */
fun isSubsequenceRecursive(s: String, t: String): Boolean {
    fun search(i: Int, j: Int): Boolean {
        // Base case: all characters of s matched
        if (i == s.length) return true
        // Base case: exhausted t without matching all of s
        if (j == t.length) return false

        return if (s[i] == t[j]) {
            // Characters match → advance both pointers
            search(i + 1, j + 1)
        } else {
            // Skip current character in t
            search(i, j + 1)
        }
    }

    return search(0, 0)
}
