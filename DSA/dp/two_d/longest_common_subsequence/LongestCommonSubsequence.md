# Longest Common Subsequence — Detailed Explanation

> **LeetCode #1143** | [Problem Link](https://leetcode.com/problems/longest-common-subsequence/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic 2D DP)  
> **Topic:** Dynamic Programming, 2D DP

---

## 📋 Problem Statement

Given two strings `text1` and `text2`, return the length of their longest common subsequence. A subsequence appears in the same relative order but not necessarily contiguous.

### Examples

| text1 | text2 | Output | LCS |
|-------|-------|--------|-----|
| `"abcde"` | `"ace"` | 3 | "ace" |
| `"abc"` | `"def"` | 0 | none |

### Key Recurrence

> If `text1[i] == text2[j]`: `dp[i][j] = dp[i-1][j-1] + 1`  
> Else: `dp[i][j] = max(dp[i-1][j], dp[i][j-1])`

---

## 🧩 Method 1: 2D DP — O(M × N)

### Core Idea

`dp[i][j]` = LCS of `text1[0..i)` and `text2[0..j)`. When chars match, extend LCS. When they differ, take the best of skipping either char.

### Dry Run — `text1="abcde", text2="ace"`

| | "" | a | c | e |
|---|---|---|---|---|
| **""** | 0 | 0 | 0 | 0 |
| **a** | 0 | 1 | 1 | 1 |
| **b** | 0 | 1 | 1 | 1 |
| **c** | 0 | 1 | 2 | 2 |
| **d** | 0 | 1 | 2 | 2 |
| **e** | 0 | 1 | 2 | 3 |

✅ **Result: 3** ("ace")

### Code

```kotlin
fun longestCommonSubsequence2D(text1: String, text2: String): Int {
    val m = text1.length
    val n = text2.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 1..m) {
        for (j in 1..n) {
            if (text1[i - 1] == text2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1] + 1
            } else {
                dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
            }
        }
    }
    return dp[m][n]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M × N) | Fill 2D table |
| **Space** | O(M × N) | 2D dp array |

---

## 🧩 Method 2: Space-Optimized DP — O(min(M,N))

### Core Idea

`dp[i][j]` only depends on current row and previous row. Use two 1D arrays instead of a 2D table.

### Code

```kotlin
fun longestCommonSubsequenceOptimized(text1: String, text2: String): Int {
    val (short, long) = if (text1.length < text2.length) text1 to text2 else text2 to text1
    val m = long.length
    val n = short.length

    var prev = IntArray(n + 1)
    var curr = IntArray(n + 1)

    for (i in 1..m) {
        for (j in 1..n) {
            if (long[i - 1] == short[j - 1]) {
                curr[j] = prev[j - 1] + 1
            } else {
                curr[j] = maxOf(prev[j], curr[j - 1])
            }
        }
        val temp = prev
        prev = curr
        curr = temp
    }
    return prev[n]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M × N) | Fill 1D arrays |
| **Space** | O(min(M,N)) | Two 1D arrays |

---

## 📊 Comparison Table

| Aspect | 2D DP | Space-Optimized |
|--------|-------|-----------------|
| **Time** | O(M × N) | O(M × N) |
| **Space** | O(M × N) | O(min(M,N)) |
| **Reconstruct LCS?** | ✅ Easy | ❌ Hard |
| **Interview preference** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Match → extend:** When chars match, `dp[i][j] = dp[i-1][j-1] + 1` — extend the LCS.
2. **Differ → skip:** When chars differ, `dp[i][j] = max(dp[i-1][j], dp[i][j-1])` — best of skipping either.
3. **2D → 1D optimization:** Since each row only depends on the previous row, two 1D arrays suffice.
4. **Base case:** `dp[0][j] = dp[i][0] = 0` — empty string has LCS 0.
5. **Pattern:** Extends to Edit Distance, Shortest Common Supersequence, Delete Operation for Two Strings.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| LCS | [#1143](https://leetcode.com/problems/longest-common-subsequence/) | Medium |
| Edit Distance | [#72](https://leetcode.com/problems/edit-distance/) | Hard |
| Shortest Common Supersequence | [#1092](https://leetcode.com/problems/shortest-common-supersequence/) | Hard |
| Delete Operation for Two Strings | [#583](https://leetcode.com/problems/delete-operation-for-two-strings/) | Medium |
