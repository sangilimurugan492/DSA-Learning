# Partition Equal Subset Sum — Detailed Explanation

> **LeetCode #416** | [Problem Link](https://leetcode.com/problems/partition-equal-subset-sum/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic 0/1 Knapsack DP)  
> **Topic:** Dynamic Programming, 0/1 Knapsack

---

## 📋 Problem Statement

Given a non-empty array, determine if it can be partitioned into two subsets with equal sum.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[1,5,11,5]` | true | [1,5,5] and [11] — both sum to 11 |
| `[1,2,3,5]` | false | Total=11 (odd) — can't split evenly |

### Key Insight

> If total sum is **odd** → impossible. If **even** → find a subset summing to `total/2`. This is the **0/1 Knapsack** problem!

---

## 🧩 Method 1: 2D DP — O(N × target)

### Core Idea

`dp[i][j]` = true if a subset of the first `i` elements can sum to `j`. For each element, include it or not.

### Recurrence

> `dp[i][j] = dp[i-1][j]` (exclude) `|| dp[i-1][j-nums[i]]` (include)

### Code

```kotlin
fun canPartition2D(nums: IntArray): Boolean {
    val total = nums.sum()
    if (total % 2 != 0) return false
    val target = total / 2

    val dp = Array(nums.size + 1) { BooleanArray(target + 1) }
    for (i in 0..nums.size) dp[i][0] = true

    for (i in 1..nums.size) {
        for (j in 1..target) {
            dp[i][j] = dp[i - 1][j]
            if (j >= nums[i - 1]) {
                dp[i][j] = dp[i][j] || dp[i - 1][j - nums[i - 1]]
            }
        }
    }
    return dp[nums.size][target]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × target) | Fill 2D table |
| **Space** | O(N × target) | 2D dp array |

---

## 🧩 Method 2: 1D DP (Space-Optimized) — O(target)

### Core Idea

`dp[j]` = true if sum `j` is achievable. Process `j` from **right to left** to avoid reusing the same element (0/1 Knapsack constraint).

### Key Insight

> Right-to-left processing ensures each element is used **at most once**. If we went left-to-right, we might reuse the same element (unbounded knapsack).

### Dry Run — `[1,5,11,5]`, target=11

| num | dp before | dp after |
|:---:|:---------:|:--------:|
| 1 | {0} | {0,1} |
| 5 | {0,1} | {0,1,5,6} |
| 11 | {0,1,5,6} | {0,1,5,6,11} |
| 5 | {0,1,5,6,11} | {0,1,5,6,10,11,16} |

✅ **Result: true** (dp[11] = true)

### Code

```kotlin
fun canPartition1D(nums: IntArray): Boolean {
    val total = nums.sum()
    if (total % 2 != 0) return false
    val target = total / 2

    val dp = BooleanArray(target + 1)
    dp[0] = true

    for (num in nums) {
        for (j in target downTo num) {
            dp[j] = dp[j] || dp[j - num]
        }
    }
    return dp[target]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × target) | Fill 1D array |
| **Space** | O(target) | 1D dp array |

---

## 📊 Comparison Table

| Aspect | 2D DP | 1D DP |
|--------|-------|-------|
| **Time** | O(N × target) | O(N × target) |
| **Space** | O(N × target) | O(target) |
| **Direction** | Any | Right-to-left (critical!) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Odd sum → false:** If total is odd, equal partition is impossible.
2. **Reduce to subset sum:** Find a subset summing to `total/2` — this is 0/1 Knapsack.
3. **Right-to-left is critical:** Processing `j` from target down to `num` ensures each element is used at most once. Left-to-right would allow reuse (unbounded knapsack).
4. **dp[0] = true:** Sum of 0 is always achievable (empty subset).
5. **Pattern:** 0/1 Knapsack pattern — extends to Last Stone Weight II, Target Sum, Coin Change (unbounded).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Partition Equal Subset Sum | [#416](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium |
| Last Stone Weight II | [#1049](https://leetcode.com/problems/last-stone-weight-ii/) | Medium |
| Target Sum | [#494](https://leetcode.com/problems/target-sum/) | Medium |
| Coin Change | [#322](https://leetcode.com/problems/coin-change/) | Medium |
