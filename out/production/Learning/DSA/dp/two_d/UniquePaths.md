# Unique Paths — Detailed Explanation

> **LeetCode #62** | [Problem Link](https://leetcode.com/problems/unique-paths/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic 2D grid DP — foundation for all grid problems)
> **Topic:** Dynamic Programming, 2D DP, Combinatorics

---

## 📋 Problem Statement

A robot is at the top-left corner of an `m×n` grid and wants to reach the bottom-right corner. It can only move RIGHT or DOWN. How many unique paths are there?

### Examples

```
Input: m=3, n=7  Output: 28
Input: m=3, n=2  Output: 3  (R→D→D, D→R→D, D→D→R)
```

---

## 🧩 Method 1: Brute Force Recursion — O(2^(m+n))

### Core Idea

From each cell, try going right and going down. Count paths that reach the destination.

### Code

```kotlin
fun uniquePathsBruteForce(m: Int, n: Int): Int =
    pathFrom(0, 0, m - 1, n - 1)

private fun pathFrom(r: Int, c: Int, destR: Int, destC: Int): Int {
    if (r > destR || c > destC) return 0
    if (r == destR && c == destC) return 1
    return pathFrom(r + 1, c, destR, destC) + pathFrom(r, c + 1, destR, destC)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(2^(m+n)) | Exponential — 2 choices per cell |
| **Space** | O(m+n) | Recursion depth |

---

## 🧩 Method 2: Memoization (Top-Down) — O(m×n)

### Core Idea

Cache result for each cell `(r, c)`. Same recursion, never recompute.

### Key Insight

> `paths(r, c) = paths(r-1, c) + paths(r, c-1)`. You can reach a cell from above (moved down) or from the left (moved right). Only two possible predecessors.

### Code

```kotlin
fun uniquePathsMemo(m: Int, n: Int): Int {
    val memo = Array(m) { IntArray(n) { -1 } }
    return pathMemo(0, 0, m - 1, n - 1, memo)
}

private fun pathMemo(r: Int, c: Int, destR: Int, destC: Int, memo: Array<IntArray>): Int {
    if (r > destR || c > destC) return 0
    if (r == destR && c == destC) return 1
    if (memo[r][c] != -1) return memo[r][c]
    memo[r][c] = pathMemo(r + 1, c, destR, destC, memo) + pathMemo(r, c + 1, destR, destC, memo)
    return memo[r][c]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(m×n) | Each cell computed once |
| **Space** | O(m×n) | Memo + recursion stack |

---

## 🧩 Method 3: Tabulation (Bottom-Up) — O(m×n)

### Core Idea

Fill the grid from top-left to bottom-right. First row = 1, first column = 1. `dp[r][c] = dp[r-1][c] + dp[r][c-1]`.

### Dry Run — 3×3 grid

```
  1  1  1
  1  2  3
  1  3  6

dp[2][2] = dp[1][2] + dp[2][1] = 3 + 3 = 6 ✅
```

### Code

```kotlin
fun uniquePathsTabulation(m: Int, n: Int): Int {
    val dp = Array(m) { IntArray(n) { 1 } }  // first row & col = 1
    for (r in 1 until m) {
        for (c in 1 until n) {
            dp[r][c] = dp[r - 1][c] + dp[r][c - 1]
        }
    }
    return dp[m - 1][n - 1]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(m×n) | Fill each cell once |
| **Space** | O(m×n) | Full grid |

---

## 🧩 Method 4: Space-Optimized — O(n) Space

### Core Idea

`dp[r][c]` only depends on the row above (`dp[r-1][c]`) and the cell to the left (`dp[r][c-1]`). Use a single row and update in-place.

### Code

```kotlin
fun uniquePathsOptimal(m: Int, n: Int): Int {
    val dp = IntArray(n) { 1 }  // first row: all 1s
    for (r in 1 until m) {
        for (c in 1 until n) {
            dp[c] = dp[c] + dp[c - 1]  // above + left
        }
    }
    return dp[n - 1]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(m×n) | Fill each cell once |
| **Space** | O(n) | Single row |

---

## 🧩 Method 5: Combinatorics — O(min(m,n)) Time

### Core Idea

Total moves = `(m-1)` down + `(n-1)` right = `m+n-2` moves. Choose which `m-1` are down: `C(m+n-2, m-1)`.

### Code

```kotlin
fun uniquePathsMath(m: Int, n: Int): Int {
    val total = m + n - 2
    val down = m - 1
    var result = 1L
    for (i in 0 until down) {
        result = result * (total - i) / (i + 1)
    }
    return result.toInt()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(min(m,n)) | Compute combination |
| **Space** | O(1) | No grid |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Brute Force | O(2^(m+n)) | O(m+n) | Understand the problem |
| Memoization | O(m×n) | O(m×n) | Top-down thinking |
| Tabulation | O(m×n) | O(m×n) | Bottom-up, clear |
| Space-Optimized | O(m×n) | O(n) | Interview final answer |
| Combinatorics | O(min(m,n)) | O(1) | Mathematical, fastest |

> **Interview Tip:** Start with the recurrence: "How can I reach cell (r,c)? From above or from the left." Then optimize: brute → memo → tabulation → O(n) space. Mention the combinatorics formula as a bonus — it shows mathematical maturity. This is the foundation for all grid DP problems (Minimum Path Sum, Unique Paths II with obstacles, Dungeon Game).
