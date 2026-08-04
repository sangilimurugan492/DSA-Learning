# House Robber — Detailed Explanation

> **LeetCode #198** | [Problem Link](https://leetcode.com/problems/house-robber/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic DP — decision at each step)
> **Topic:** Dynamic Programming, 1D DP

---

## 📋 Problem Statement

You are a robber planning to rob houses along a street. Each house has money. You cannot rob two adjacent houses (alarm triggers). Return the maximum amount you can rob.

### Examples

```
Input: [1,2,3,1]    Output: 4  (rob house 0 + house 3 = 1+3)
Input: [2,7,9,3,1]  Output: 12 (rob house 0 + house 2 + house 4 = 2+9+1)
```

---

## 🧩 Method 1: Brute Force Recursion — O(2^N)

### Core Idea

At each house, two choices: rob it (skip next) or skip it. Try all possibilities.

### Code

```kotlin
fun robBruteForce(nums: IntArray): Int = robFrom(nums, 0)

private fun robFrom(nums: IntArray, i: Int): Int {
    if (i >= nums.size) return 0
    val rob = nums[i] + robFrom(nums, i + 2)   // Rob this, skip next
    val skip = robFrom(nums, i + 1)              // Skip this
    return maxOf(rob, skip)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(2^N) | 2 choices at each house |
| **Space** | O(N) | Recursion stack |

---

## 🧩 Method 2: Memoization (Top-Down) — O(N)

### Core Idea

Cache results for each index. Same recursion, but never recompute.

### Key Insight

> The recurrence: `dp[i] = max(dp[i-1], dp[i-2] + nums[i])`. "Skip" vs "Rob this house." If you rob house i, you can't rob i-1, so add dp[i-2].

### Code

```kotlin
fun robMemo(nums: IntArray): Int {
    val memo = IntArray(nums.size) { -1 }
    return robMemoFrom(nums, 0, memo)
}

private fun robMemoFrom(nums: IntArray, i: Int, memo: IntArray): Int {
    if (i >= nums.size) return 0
    if (memo[i] != -1) return memo[i]
    val rob = nums[i] + robMemoFrom(nums, i + 2, memo)
    val skip = robMemoFrom(nums, i + 1, memo)
    memo[i] = maxOf(rob, skip)
    return memo[i]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each index computed once |
| **Space** | O(N) | Memo + recursion stack |

---

## 🧩 Method 3: Tabulation (Bottom-Up) — O(N)

### Core Idea

Build from the end backwards. `dp[i] = max(nums[i] + dp[i+2], dp[i+1])`.

### Dry Run — `[1,2,3,1]`

```
dp[3] = max(1+0, 0) = 1
dp[2] = max(3+0, 1) = 3
dp[1] = max(2+1, 3) = 3
dp[0] = max(1+3, 3) = 4 ✅
```

### Code

```kotlin
fun robTabulation(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    if (nums.size == 1) return nums[0]
    val n = nums.size
    val dp = IntArray(n + 2)  // dp[n] = 0, dp[n+1] = 0 (base cases)
    for (i in n - 1 downTo 0) {
        dp[i] = maxOf(nums[i] + dp[i + 2], dp[i + 1])
    }
    return dp[0]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(N) | DP array |

---

## 🧩 Method 4: Space-Optimized — O(1) Space

### Core Idea

`dp[i]` only depends on `dp[i+1]` and `dp[i+2]`. Just track two variables!

### Code

```kotlin
fun robOptimal(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    var next2 = 0  // dp[i+2]
    var next1 = 0  // dp[i+1]
    for (i in nums.lastIndex downTo 0) {
        val curr = maxOf(nums[i] + next2, next1)
        next2 = next1
        next1 = curr
    }
    return next1
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Two variables |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Brute Force | O(2^N) | O(N) | Understand the problem |
| Memoization | O(N) | O(N) | Top-down thinking |
| Tabulation | O(N) | O(N) | Bottom-up, clear |
| Space-Optimized | O(N) | O(1) | Interview final answer |

> **Interview Tip:** Start with the recurrence: "At each house, rob or skip?" Then optimize: brute → memo → tabulation → O(1) space. The pattern `dp[i] = max(dp[i-1], dp[i-2] + nums[i])` is the same as Fibonacci but with MAX instead of SUM. House Robber II (circular) uses the same logic but runs twice — once excluding first house, once excluding last.
