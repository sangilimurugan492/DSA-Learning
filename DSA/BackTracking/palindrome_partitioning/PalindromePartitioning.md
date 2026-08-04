# Palindrome Partitioning — Detailed Explanation

> **LeetCode #131** | [Problem Link](https://leetcode.com/problems/palindrome-partitioning/)
> **FAANG Importance:** ⭐⭐⭐⭐ (Backtracking + palindrome check)
> **Topic:** Backtracking, String, Dynamic Programming

---

## 📋 Problem Statement

Given a string `s`, partition it such that every substring is a palindrome. Return all possible palindrome partitionings.

### Examples

```
Input: "aab" → [["a","a","b"], ["aa","b"]]
Input: "a"   → [["a"]]
```

---

## 🧩 Method 1: Backtracking with Two-Pointer Check — O(N × 2^N)

### Core Idea

Try all possible cut points. If the prefix `s[start..end]` is a palindrome, add it to the current partition and recurse on the remaining suffix.

### Key Insight

> At each position, try all possible end positions. If `s[start..end]` is a palindrome, we can cut here. The decision tree has 2^(N-1) leaves (each gap is a cut or no-cut).

### Dry Run — `s = "aab"`

```
backtrack(0):
  end=1: "a" is palindrome → current=["a"]
    backtrack(1):
      end=2: "a" is palindrome → current=["a","a"]
        backtrack(2):
          end=3: "b" is palindrome → current=["a","a","b"]
            backtrack(3): start==len → add ["a","a","b"] ✅
      end=3: "ab" not palindrome → skip
  end=2: "aa" is palindrome → current=["aa"]
    backtrack(2):
      end=3: "b" is palindrome → current=["aa","b"]
        backtrack(3): start==len → add ["aa","b"] ✅
  end=3: "aab" not palindrome → skip

Result: [["a","a","b"], ["aa","b"]] ✅
```

### Code

```kotlin
fun partition(s: String): List<List<String>> {
    val results = mutableListOf<List<String>>()
    val current = mutableListOf<String>()

    fun backtrack(start: Int) {
        if (start == s.length) {
            results.add(current.toList())
            return
        }
        for (end in start + 1..s.length) {
            if (isPalindrome(s, start, end - 1)) {
                current.add(s.substring(start, end))
                backtrack(end)
                current.removeAt(current.lastIndex)
            }
        }
    }

    backtrack(0)
    return results
}

private fun isPalindrome(s: String, left: Int, right: Int): Boolean {
    var l = left; var r = right
    while (l < r) { if (s[l] != s[r]) return false; l++; r-- }
    return true
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × 2^N) | 2^N partitions × O(N) palindrome check |
| **Space** | O(N) | Current partition + recursion |

---

## 🧩 Method 2: DP + Backtracking — O(N × 2^N)

### Core Idea

Precompute `isPalin[i][j]` table in O(N²). Then check palindromes in O(1) during backtracking.

### Code

```kotlin
fun partitionDP(s: String): List<List<String>> {
    val n = s.length
    val isPalin = Array(n) { BooleanArray(n) }

    for (i in 0 until n) isPalin[i][i] = true
    for (len in 2..n) {
        for (i in 0..n - len) {
            val j = i + len - 1
            isPalin[i][j] = (s[i] == s[j]) && (len == 2 || isPalin[i + 1][j - 1])
        }
    }

    val results = mutableListOf<List<String>>()
    val current = mutableListOf<String>()

    fun backtrack(start: Int) {
        if (start == n) { results.add(current.toList()); return }
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
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N² + N × 2^N) | DP precompute + backtracking |
| **Space** | O(N²) | DP table |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Two-pointer check | O(N × 2^N) | O(N) | Simpler, no DP |
| DP precompute | O(N² + N × 2^N) | O(N²) | Faster palindrome checks |

> **Interview Tip:** Start with the two-pointer approach. If asked to optimize, precompute the palindrome table with DP. The DP recurrence: `isPalin[i][j] = (s[i] == s[j]) && (len <= 2 || isPalin[i+1][j-1])`. This is the same palindrome DP used in Longest Palindromic Substring.
