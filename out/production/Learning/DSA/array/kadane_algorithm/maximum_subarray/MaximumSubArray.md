# Maximum Subarray — Detailed Explanation

> **LeetCode #53** | [Problem Link](https://leetcode.com/problems/maximum-subarray/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic Kadane's — asked everywhere)  
> **Topic:** Array, Dynamic Programming, Kadane's Algorithm

---

## 📋 Problem Statement

Given an integer array `nums`, find the **contiguous subarray** (containing at least one element) which has the **largest sum**, and return its sum.

### Examples

| Input | Output | Subarray |
|-------|--------|----------|
| `[-2, 1, -3, 4, -1, 2, 1, -5, 4]` | `6` | `[4, -1, 2, 1]` |
| `[5, 4, -1, 7, 8]` | `23` | Entire array |
| `[1]` | `1` | `[1]` |

### Visual Walkthrough — Example 1: `[-2, 1, -3, 4, -1, 2, 1, -5, 4]`

```
Index:  0   1   2   3   4   5   6   7   8
Array: -2   1  -3   4  -1   2   1  -5   4
                                ↑
                    Best subarray: [4, -1, 2, 1] = 6 ✅
```

---

## 🧩 Method 1: Brute Force — Try Every Subarray

### Core Idea

For each starting index `i`, accumulate the sum for all ending indices `j ≥ i`. Track the maximum.

### Code

```kotlin
fun maxSubArrayBruteForce(nums: IntArray): Int {
    var maxSum = Int.MIN_VALUE
    for (i in nums.indices) {
        var currentSum = 0
        for (j in i until nums.size) {
            currentSum += nums[j]
            maxSum = maxOf(maxSum, currentSum)
        }
    }
    return maxSum
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops over all subarrays |
| **Space** | O(1) | Only variables |

---

## 🧩 Method 2: Kadane's Algorithm (Optimal)

### Core Idea

At each index, decide: **extend** the previous subarray or **start fresh** from here?

> `localMax = max(nums[i], localMax + nums[i])`

If the running sum is negative, it can only hurt future subarrays — so we reset.

### Key Insight

> A negative running sum is a liability. If `localMax + nums[i] < nums[i]`, it means the previous subarray is dragging us down. Better to start fresh from `nums[i]`.

### Algorithm — Step by Step

1. **Initialize** `localMax = nums[0]`, `globalMax = nums[0]`.
2. **For each** `i` from `1` to `n-1`:
   - `localMax = max(nums[i], localMax + nums[i])` — extend or start fresh.
   - `globalMax = max(globalMax, localMax)` — track best overall.
3. **Return** `globalMax`.

### Dry Run — Example 1: `[-2, 1, -3, 4, -1, 2, 1, -5, 4]`

| `i` | `nums[i]` | `extend = localMax + nums[i]` | `localMax` | Decision | `globalMax` |
|:---:|:---:|:---:|:---:|:---:|:---:|
| 0 | -2 | — | -2 | (initial) | -2 |
| 1 | 1 | -2+1 = -1 | **1** | START FRESH (1 > -1) | 1 |
| 2 | -3 | 1+(-3) = -2 | **-2** | EXTEND (-2 > -3) | 1 |
| 3 | 4 | -2+4 = 2 | **4** | START FRESH (4 > 2) | 4 |
| 4 | -1 | 4+(-1) = 3 | **3** | EXTEND (3 > -1) | 4 |
| 5 | 2 | 3+2 = 5 | **5** | EXTEND (5 > 2) | 5 |
| 6 | 1 | 5+1 = 6 | **6** | EXTEND (6 > 1) | **6** ← answer! |
| 7 | -5 | 6+(-5) = 1 | **1** | EXTEND (1 > -5) | 6 |
| 8 | 4 | 1+4 = 5 | **5** | EXTEND (5 > 4) | 6 |

✅ **Result: `6`** — subarray `[4, -1, 2, 1]`

### Code

```kotlin
fun maxSubArrayKadane(nums: IntArray): Int {
    var localMax = nums[0]
    var globalMax = nums[0]
    for (i in 1 until nums.size) {
        localMax = maxOf(nums[i], localMax + nums[i])
        globalMax = maxOf(globalMax, localMax)
    }
    return globalMax
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Two variables |

---

## 📊 Comparison Table

| Aspect | Brute Force | Kadane's |
|--------|-------------|----------|
| **Time** | O(N²) | O(N) |
| **Space** | O(1) | O(1) |
| **Optimality** | ❌ | ✅ Optimal |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Extend or start fresh:** At each index, the best subarray ending here is either the element alone or the element + previous best.
2. **Negative sums are liabilities:** If the running sum is negative, it can only decrease future sums — reset.
3. **Two variables:** `localMax` (best ending here) and `globalMax` (best overall) — that's all you need.
4. **Kadane's is a DP:** `localMax[i] = max(nums[i], localMax[i-1] + nums[i])` — space-optimized to O(1).
5. **Pattern:** Kadane's extends to Maximum Circular Subarray, Maximum Product Subarray, etc.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Maximum Subarray | [#53](https://leetcode.com/problems/maximum-subarray/) | Medium |
| Maximum Product Subarray | [#152](https://leetcode.com/problems/maximum-product-subarray/) | Medium |
| Maximum Circular Subarray | [#918](https://leetcode.com/problems/maximum-sum-circular-subarray/) | Medium |
