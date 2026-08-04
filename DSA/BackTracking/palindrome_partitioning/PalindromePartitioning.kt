package patterns.backtracking.palindrome_partitioning

/**
 * https://leetcode.com/problems/palindrome-partitioning/
 * Partition string s such that every substring is a palindrome.
 * Return all possible palindrome partitionings.
 * Example: s = "aab" → [["a","a","b"], ["aa","b"]]
 * FAANG Importance: ⭐⭐⭐⭐ (Backtracking + palindrome check)
 */

fun main() {
    println(partition("aab"))  // [[a, a, b], [aa, b]]
    println(partition("a"))     // [[a]]
}

/**
 * Backtracking: O(N * 2^N) time, O(N) space
 * Try all possible cuts. If prefix is palindrome, recurse on suffix.
 */
fun partition(s: String): List<List<String>> {
    val results = mutableListOf<List<String>>()
    val current = mutableListOf<String>()

    fun backtrack(start: Int) {
        if (start == s.length) {
            results.add(current.toList())
            return
        }
        for (end in start + 1..s.length) {
            val sub = s.substring(start, end)
            if (isPalindrome(s, start, end - 1)) {
                current.add(sub)
                backtrack(end)
                current.removeAt(current.lastIndex)
            }
        }
    }

    backtrack(0)
    return results
}

/**
 * Two-pointer palindrome check: O(N) per call
 */
private fun isPalindrome(s: String, left: Int, right: Int): Boolean {
    var l = left
    var r = right
    while (l < r) {
        if (s[l] != s[r]) return false
        l++; r--
    }
    return true
}

/**
 * Optimized with DP precomputation: O(N * 2^N) time, O(N²) space
 * Precompute isPalin[i][j] to check palindromes in O(1).
 */
fun partitionDP(s: String): List<List<String>> {
    val n = s.length
    val isPalin = Array(n) { BooleanArray(n) }

    // All single chars are palindromes
    for (i in 0 until n) isPalin[i][i] = true
    // Check pairs and longer
    for (len in 2..n) {
        for (i in 0..n - len) {
            val j = i + len - 1
            isPalin[i][j] = (s[i] == s[j]) && (len == 2 || isPalin[i + 1][j - 1])
        }
    }

    val results = mutableListOf<List<String>>()
    val current = mutableListOf<String>()

    fun backtrack(start: Int) {
        if (start == n) {
            results.add(current.toList())
            return
        }
        for (end in start until n) {
            if (isPalin[start][end]) {
                current.add(s.substring(start, end + 1))
                backtrack(end + 1)
                current.removeAt(current.lastIndex)
            }
        }
    }

    backtrack(0)
    return results
}
