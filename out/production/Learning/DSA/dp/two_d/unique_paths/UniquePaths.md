# Unique Paths — Detailed Explanation

> **LeetCode #62** | [Problem Link](https://leetcode.com/problems/unique-paths/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic 2D DP + Combinatorics)  
> **Topic:** Dynamic Programming, Combinatorics

---

## 📋 Problem Statement

A robot is at the top-left corner of an m×n grid. It can only move right or down. How many possible unique paths to reach the bottom-right corner?

### Examples

| m | n | Output |
|---|---|--------|
| 3 | 7 | 28 |
| 3 | 2 | 3 |

### Key Recurrence

> `dp[i][j] = dp[i-1][j] + dp[i][j-1]`  
> Base case: first row and first column = 1

---

## 🧩 Method 1: 2D DP — O(M × N)

### Core Idea

To reach cell (i, j), the robot came from above (i-1, j) or left (i, j-1). Sum both paths.

### Dry Run — m=3, n=2

| | 0 | 1 |
|---|---|---|
| **0** | 1 | 1 |
| **1** | 1 | 2 |
| **2** | 1 | 3 |

✅ **Result: 3**

### Code

```kotlin
fun uniquePathsDP(m: Int, n: Int): Int {
    val dp = Array(m) { IntArray(n) }
    for (i in 0 until m) dp[i][0] = 1
    for (j in 0 until n) dp[0][j] = 1

    for (i in 1 until m) {
        for (j in 1 until n) {
            dp[i][j] = dp[i - 1][j] + dp[i][j - 1]
        }
    }
    return dp[m - 1][n - 1]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M × N) | Fill 2D table |
| **Space** | O(M × N) | 2D dp array |

---

## 🧩 Method 2: Math (Combinatorics) — O(min(M,N))

### Core Idea

The robot makes (m-1) down moves and (n-1) right moves = (m+n-2) total. Choose which (m-1) are down: **C(m+n-2, m-1)**.

### Key Insight

> This is a combinatorics problem. The number of paths = number of ways to arrange (m-1) downs and (n-1) rights = C(m+n-2, m-1).

### Code

```kotlin
fun uniquePathsMath(m: Int, n: Int): Int {
    val total = m + n - 2
    val choose = minOf(m - 1, n - 1)
    var result = 1L
    for (i in 0 until choose) {
        result = result * (total - i) / (i + 1)
    }
    return result.toInt()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(min(M,N)) | Compute combination |
| **Space** | O(1) | No extra space |

---

## 📊 Comparison Table

| Aspect | 2D DP | Math |
|--------|-------|------|
| **Time** | O(M × N) | O(min(M,N)) |
| **Space** | O(M × N) | O(1) |
| **Approach** | Grid DP | Combinatorics C(m+n-2, m-1) |
| **Handles obstacles?** | ✅ (Unique Paths II) | ❌ |
| **Interview preference** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Grid DP:** `dp[i][j] = dp[i-1][j] + dp[i][j-1]` — sum paths from top and left.
2. **Combinatorics insight:** Total moves = m+n-2. Choose m-1 to be down = C(m+n-2, m-1).
3. **First row/col = 1:** Only one way to reach any cell in the first row (all right) or first column (all down).
4. **Math is faster but less flexible:** The combinatorics approach doesn't work with obstacles (Unique Paths II).
5. **Pattern:** Extends to Unique Paths II (with obstacles), Minimum Path Sum, Dungeon Game.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Unique Paths | [#62](https://leetcode.com/problems/unique-paths/) | Medium |
| Unique Paths II | [#63](https://leetcode.com/problems/unique-paths-ii/) | Medium |
| Minimum Path Sum | [#64](https://leetcode.com/problems/minimum-path-sum/) | Medium |
| Dungeon Game | [#174](https://leetcode.com/problems/dungeon-game/) | Hard |
