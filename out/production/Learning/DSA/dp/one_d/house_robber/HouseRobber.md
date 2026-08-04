# House Robber — Detailed Explanation

> **LeetCode #198** | [Problem Link](https://leetcode.com/problems/house-robber/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic DP — decision at each step)  
> **Topic:** Dynamic Programming, 1D DP

---

## 📋 Problem Statement

You are a robber planning to rob houses along a street. Each house has a certain amount of money. You **cannot rob two adjacent houses** (alarm will trigger). Return the **maximum amount** you can rob.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[1, 2, 3, 1]` | `4` | Rob house 0 (1) + house 3 (3) = 4 |
| `[2, 7, 9, 3, 1]` | `12` | Rob house 0 (2) + house 2 (9) + house 4 (1) = 12 |

### Key Formula

> **`dp[i] = max(dp[i-1], dp[i-2] + nums[i])`**  
> "skip house i" vs "rob house i"  
> **Base cases:** `dp[0] = nums[0]`, `dp[1] = max(nums[0], nums[1])`

---

## 🧩 Method 1: Brute Force — Recursion

### Core Idea

At each house `i`, two choices:
1. **Rob** house `i` → get `nums[i] + rob(i+2)` (must skip `i+1`).
2. **Skip** house `i` → get `rob(i+1)`.

Return `max(rob, skip)`.

### Problem

Overlapping subproblems → exponential time. Same index computed multiple times.

### Code

```kotlin
fun robBruteForce(nums: IntArray): Int {
    return robFrom(nums, 0)
}

private fun robFrom(nums: IntArray, i: Int): Int {
    if (i >= nums.size) return 0
    val rob = nums[i] + robFrom(nums, i + 2)  // Rob this, skip next
    val skip = robFrom(nums, i + 1)            // Skip this
    return maxOf(rob, skip)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(2^N) | Two choices at each house |
| **Space** | O(N) | Recursion stack |

---

## 🧩 Method 2: Space-Optimized DP (O(1) space)

### Core Idea

`dp[i]` only depends on `dp[i+1]` and `dp[i+2]`. Replace the dp array with two variables.

### Key Insight

> If you **rob** house `i`, you CANNOT rob house `i-1`, so add `dp[i-2]`.  
> If you **skip** house `i`, you get `dp[i-1]`.  
> Take the **max** of these two choices.

### Algorithm — Step by Step

1. **Initialize** `next2 = 0` (dp[i+2]), `next1 = 0` (dp[i+1]).
2. **For each** `i` from `n-1` down to `0`:
   - `curr = max(nums[i] + next2, next1)` — rob vs skip.
   - `next2 = next1`, `next1 = curr` — shift forward.
3. **Return** `next1`.

### Dry Run — `[1, 2, 3, 1]`

| `i` | `nums[i]` | `rob = nums[i] + next2` | `skip = next1` | `curr` | Choice | `next2` | `next1` |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 3 | 1 | 1+0=1 | 0 | **1** | ROB | 0 | 1 |
| 2 | 3 | 3+0=3 | 1 | **3** | ROB | 1 | 3 |
| 1 | 2 | 2+1=3 | 3 | **3** | SKIP | 3 | 3 |
| 0 | 1 | 1+3=4 | 3 | **4** | ROB | 3 | 4 |

✅ **Result: `4`** (rob house 0 + house 3 = 1 + 3)

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

## 📊 Comparison Table

| Aspect | Brute Force | Space-Optimized DP |
|--------|-------------|---------------------|
| **Time** | O(2^N) | O(N) |
| **Space** | O(N) | O(1) |
| **Approach** | Recursion, try all | Two variables, rob vs skip |
| **Optimality** | ❌ Exponential | ✅ Optimal |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Decision DP:** At each house, make a binary decision: rob or skip. This is the core of 1D DP.
2. **Rob → skip previous:** If you rob house `i`, you must skip `i-1`, so add `dp[i-2]`.
3. **Space optimization:** Since `dp[i]` only depends on `dp[i-1]` and `dp[i-2]`, two variables suffice — O(1) space.
4. **Max, not sum:** Unlike Climbing Stairs (count ways), House Robber **maximizes** value.
5. **Pattern:** This extends to House Robber II (circular), Delete and Earn, Paint House, etc.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| House Robber | [#198](https://leetcode.com/problems/house-robber/) | Medium |
| House Robber II | [#213](https://leetcode.com/problems/house-robber-ii/) | Medium |
| Climbing Stairs | [#70](https://leetcode.com/problems/climbing-stairs/) | Easy |
| Delete and Earn | [#740](https://leetcode.com/problems/delete-and-earn/) | Medium |
