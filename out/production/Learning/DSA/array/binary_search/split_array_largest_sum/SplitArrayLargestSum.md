# Split Array Largest Sum — Detailed Explanation

> **LeetCode #410** | [Problem Link](https://leetcode.com/problems/split-array-largest-sum/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Hard — Binary search on answer — asked at Google, Meta)  
> **Topic:** Binary Search on Answer Space

---

## 📋 Problem Statement

Given an array of non-negative integers and an integer `m`, split the array into `m` non-empty contiguous subarrays. Minimize the largest sum among these `m` subarrays. Return the minimized largest sum.

### Examples

| nums | m | Output | Best Split |
|------|---|--------|------------|
| `[7,2,5,10,8]` | 2 | 18 | `[7,2,5] + [10,8]` → max(14,18) |
| `[1,2,3,4,5]` | 2 | 9 | `[1,2,3] + [4,5]` → max(6,9) |
| `[1,4,4]` | 3 | 4 | `[1]+[4]+[4]` → max(1,4,4) |

---

## 🧩 Method 1: Brute Force DP — O(n² × m)

### Core Idea

Use dynamic programming: `dp[i][j]` = min largest sum for first `i` elements split into `j` parts. For each split point `k < i`, `dp[i][j] = min(max(dp[k][j-1], sum(k..i-1)))`.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(n² × m) | For each (i, j), try all split points k |
| **Space** | O(n × m) | DP table |

---

## 🧩 Method 2: Binary Search on Answer — O(n × log(sum(nums)))

### Core Idea

The answer lies in `[max(nums), sum(nums)]`. Binary search this range: for a candidate `maxSum`, greedily check if we can split into ≤ `m` subarrays where each subarray sum ≤ `maxSum`.

### Key Insight

> The relationship is **monotonic**: larger `maxSum` → fewer subarrays needed. If `maxSum` works (≤ m subarrays), all larger values also work. We want the **minimum** working `maxSum`. The greedy check is: add elements to the current subarray until adding the next would exceed `maxSum`, then start a new subarray.

### Step-by-Step

1. Set `left = max(nums)`, `right = sum(nums)`.
2. While `left < right`:
   - `mid = left + (right - left) / 2`.
   - Greedily count subarrays needed with maxSum = `mid`.
   - If subarrays ≤ `m` → `mid` works → `right = mid` (try smaller).
   - Else → `mid` too small → `left = mid + 1` (try larger).
3. Return `left`.

---

## 🔍 Huge 10-Element Array Walkthrough

### Setup

- **Array:** `[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]` (10 elements)
- **m = 3** (split into 3 contiguous subarrays)
- **sum = 55**, **max = 10**
- **Search range:** `[10, 55]`
- **Expected answer:** `21`

### Step-by-Step Binary Search

#### STEP 1: `left=10, right=55, mid=32`

- Greedy split at maxSum=32: `[1,2,3,4,5,6,7]=28`, `[8,9,10]=27` → **2 subarrays**
- `2 ≤ 3` → works! → try smaller → `right = 32`

#### STEP 2: `left=10, right=32, mid=21`

- Greedy split at maxSum=21: `[1,2,3,4,5,6]=21`, `[7,8]=15`, `[9,10]=19` → **3 subarrays**
- `3 ≤ 3` → works! → try smaller → `right = 21`

#### STEP 3: `left=10, right=21, mid=15`

- Greedy split at maxSum=15: `[1,2,3,4,5]=15`, `[6,7]=13`, `[8]=8`, `[9]=9`, `[10]=10` → **5 subarrays**
- `5 > 3` → too many! → need larger maxSum → `left = 16`

#### STEP 4: `left=16, right=21, mid=18`

- Greedy split at maxSum=18: `[1,2,3,4,5]=15`, `[6,7]=13`, `[8,9]=17`, `[10]=10` → **4 subarrays**
- `4 > 3` → too many! → need larger maxSum → `left = 19`

#### STEP 5: `left=19, right=21, mid=20`

- Greedy split at maxSum=20: `[1,2,3,4,5]=15`, `[6,7]=13`, `[8,9]=17`, `[10]=10` → **4 subarrays**
- `4 > 3` → too many! → need larger maxSum → `left = 21`

#### STEP 6: `left=21, right=21` → loop ends → return **21** ✅

### Verification

| maxSum | Greedy Split | Subarrays | ≤ 3? |
|:------:|:------------|:---------:|:----:|
| 21 | `[1,2,3,4,5,6]=21, [7,8]=15, [9,10]=19` | 3 | ✅ |
| 20 | `[1,2,3,4,5]=15, [6,7]=13, [8,9]=17, [10]=10` | 4 | ❌ |

### Summary Table

| Step | left | right | mid (maxSum) | Subarrays | ≤ m? | Action |
|:----:|:----:|:-----:|:------------:|:---------:|:----:|:------:|
| 1 | 10 | 55 | 32 | 2 | ✅ | right = 32 |
| 2 | 10 | 32 | 21 | 3 | ✅ | right = 21 |
| 3 | 10 | 21 | 15 | 5 | ❌ | left = 16 |
| 4 | 16 | 21 | 18 | 4 | ❌ | left = 19 |
| 5 | 19 | 21 | 20 | 4 | ❌ | left = 21 |
| 6 | 21 | 21 | — | — | left == right | return **21** ✅ |

> **Key observation:** Only **5 iterations** to search a range of [10, 55]. That's O(log(sum(nums))) — binary search on the answer space!

### Code

```kotlin
fun splitArray(nums: IntArray, m: Int): Int {
    var left = nums.max()
    var right = nums.sum()
    while (left < right) {
        val mid = left + (right - left) / 2
        if (canSplit(nums, mid, m)) right = mid
        else left = mid + 1
    }
    return left
}

fun canSplit(nums: IntArray, maxSum: Int, m: Int): Boolean {
    var subarrays = 1
    var currentSum = 0
    for (num in nums) {
        if (currentSum + num > maxSum) { subarrays++; currentSum = num }
        else currentSum += num
    }
    return subarrays <= m
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(n × log(sum(nums))) | Binary search with O(n) greedy check per step |
| **Space** | O(1) | Constant variables |

---

## 📊 Comparison Table

| Aspect | Brute Force DP | Binary Search |
|--------|----------------|---------------|
| **Time** | O(n² × m) | O(n × log(sum(nums))) |
| **Space** | O(n × m) | O(1) |
| **Efficient for large arrays?** | ❌ | ✅ |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Binary search on answer space:** The answer is in [max(nums), sum(nums)] and the feasibility function (can split into ≤ m subarrays) is monotonic.
2. **Greedy check:** To check if maxSum works, greedily pack elements into subarrays — add until exceeding maxSum, then start a new subarray.
3. **Same pattern as Koko Eating Bananas:** Both binary search on the answer space with a monotonic feasibility function.
4. **Pattern:** This extends to Capacity To Ship Packages, Divide Chocolate, Min Days for Bouquets.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Split Array Largest Sum | [#410](https://leetcode.com/problems/split-array-largest-sum/) | Hard |
| Capacity To Ship Packages | [#1011](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/) | Medium |
| Divide Chocolate | [#1231](https://leetcode.com/problems/divide-chocolate/) | Hard |
| Koko Eating Bananas | [#875](https://leetcode.com/problems/koko-eating-bananas/) | Medium |
