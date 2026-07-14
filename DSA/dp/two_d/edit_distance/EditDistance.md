# Edit Distance — Detailed Explanation

> **LeetCode #72** | [Problem Link](https://leetcode.com/problems/edit-distance/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic 2D DP — Levenshtein Distance)  
> **Topic:** Dynamic Programming, 2D DP

---

## 📋 Problem Statement

Given two strings `word1` and `word2`, return the minimum number of operations (insert, delete, replace) required to convert `word1` to `word2`.

### Example

| word1 | word2 | Output | Operations |
|-------|-------|--------|------------|
| `"horse"` | `"ros"` | 3 | horse→rorse (replace h) → rose (remove r) → ros (remove e) |

### Key Recurrence

> If `word1[i] == word2[j]`: `dp[i][j] = dp[i-1][j-1]` (no operation)  
> Else: `dp[i][j] = 1 + min(dp[i-1][j-1], dp[i-1][j], dp[i][j-1])`  
> (replace, delete, insert)

---

## 🧩 Method 1: 2D DP — O(M × N)

### Core Idea

`dp[i][j]` = min operations to convert `word1[0..i)` to `word2[0..j)`. Three operations: replace (diagonal), delete (up), insert (left).

### Three Operations

| Operation | DP Reference | Meaning |
|-----------|-------------|---------|
| Replace | `dp[i-1][j-1]` | Replace word1[i] with word2[j] |
| Delete | `dp[i-1][j]` | Delete word1[i] |
| Insert | `dp[i][j-1]` | Insert word2[j] into word1 |

### Dry Run — `word1="horse", word2="ros"`

| | "" | r | o | s |
|---|---|---|---|---|
| **""** | 0 | 1 | 2 | 3 |
| **h** | 1 | 1 | 2 | 3 |
| **o** | 2 | 2 | 1 | 2 |
| **r** | 3 | 2 | 2 | 2 |
| **s** | 4 | 3 | 3 | 2 |
| **e** | 5 | 4 | 4 | 3 |

✅ **Result: 3**

### Code

```kotlin
fun editDistance2D(word1: String, word2: String): Int {
    val m = word1.length
    val n = word2.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) dp[i][0] = i
    for (j in 0..n) dp[0][j] = j

    for (i in 1..m) {
        for (j in 1..n) {
            if (word1[i - 1] == word2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1]
            } else {
                dp[i][j] = 1 + minOf(dp[i-1][j-1], dp[i-1][j], dp[i][j-1])
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

## 🧩 Method 2: Space-Optimized DP — O(N)

### Core Idea

`dp[i][j]` only depends on current and previous row. Use two 1D arrays.

### Code

```kotlin
fun editDistanceOptimized(word1: String, word2: String): Int {
    val m = word1.length
    val n = word2.length
    var prev = IntArray(n + 1) { it }
    var curr = IntArray(n + 1)

    for (i in 1..m) {
        curr[0] = i
        for (j in 1..n) {
            if (word1[i - 1] == word2[j - 1]) {
                curr[j] = prev[j - 1]
            } else {
                curr[j] = 1 + minOf(prev[j - 1], prev[j], curr[j - 1])
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
| **Space** | O(N) | Two 1D arrays |

---

## 📊 Comparison Table

| Aspect | 2D DP | Space-Optimized |
|--------|-------|-----------------|
| **Time** | O(M × N) | O(M × N) |
| **Space** | O(M × N) | O(N) |
| **Reconstruct operations?** | ✅ Easy | ❌ Hard |
| **Interview preference** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Three operations:** Replace (diagonal), Delete (up), Insert (left). Take min of all three + 1.
2. **Match = free:** When chars match, no operation needed — copy diagonal value.
3. **Base cases:** `dp[i][0] = i` (delete all), `dp[0][j] = j` (insert all).
4. **Levenshtein distance:** This is the classic string similarity metric used in spell checkers, DNA alignment, etc.
5. **Pattern:** Extends to LCS, Delete Operation for Two Strings, One Edit Distance.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Edit Distance | [#72](https://leetcode.com/problems/edit-distance/) | Hard |
| LCS | [#1143](https://leetcode.com/problems/longest-common-subsequence/) | Medium |
| Delete Operation for Two Strings | [#583](https://leetcode.com/problems/delete-operation-for-two-strings/) | Medium |
| One Edit Distance | [#161](https://leetcode.com/problems/one-edit-distance/) | Medium |
