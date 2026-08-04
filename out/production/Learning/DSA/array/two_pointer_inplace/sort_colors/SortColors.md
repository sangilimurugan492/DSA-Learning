# Sort Colors (Dutch National Flag) — Detailed Explanation

> **LeetCode #75** | [Problem Link](https://leetcode.com/problems/sort-colors/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic three-pointer problem)  
> **Topic:** Two Pointers, In-place Sorting

---

## 📋 Problem Statement

Given an array with values 0 (red), 1 (white), 2 (blue), sort in-place so that same colors are adjacent, in order 0, 1, 2. Must not use library sort.

### Example

| Input | Output |
|-------|--------|
| `[2,0,2,1,1,0]` | `[0,0,1,1,2,2]` |

---

## 🧩 Method 1: Counting Sort — O(N), two passes

### Core Idea

Count 0s, 1s, 2s. Overwrite array with correct counts.

### Code

```kotlin
fun sortColorsCounting(nums: IntArray) {
    var zeros = 0; var ones = 0; var twos = 0
    for (num in nums) when (num) { 0 -> zeros++; 1 -> ones++; 2 -> twos++ }
    var i = 0
    repeat(zeros) { nums[i++] = 0 }
    repeat(ones) { nums[i++] = 1 }
    repeat(twos) { nums[i++] = 2 }
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Two passes |
| **Space** | O(1) | 3 counters |

---

## 🧩 Method 2: Dutch National Flag — O(N), one pass

### Core Idea

Three pointers: `low`, `mid`, `high`. Maintain invariant: `[0..low)` = 0s, `[low..mid)` = 1s, `[mid..high]` = unknown, `(high..n)` = 2s.

### Key Insight

> When `nums[mid] == 0`: swap with `low`, advance both (swapped-in element is 1).  
> When `nums[mid] == 1`: just advance `mid` (1s belong in the middle).  
> When `nums[mid] == 2`: swap with `high`, only advance `high` (swapped-in element is unknown).

### Dry Run — `[2,0,2,1,1,0]`

| low | mid | high | nums[mid] | Action | Array |
|:---:|:---:|:----:|:---------:|:------:|:-----:|
| 0 | 0 | 5 | 2 | swap(0,5), high-- | [0,0,2,1,1,2] |
| 0 | 0 | 4 | 0 | swap(0,0), low++, mid++ | [0,0,2,1,1,2] |
| 1 | 1 | 4 | 0 | swap(1,1), low++, mid++ | [0,0,2,1,1,2] |
| 2 | 2 | 4 | 2 | swap(2,4), high-- | [0,0,1,1,2,2] |
| 2 | 2 | 3 | 1 | mid++ | [0,0,1,1,2,2] |
| 2 | 3 | 3 | 1 | mid++ | [0,0,1,1,2,2] |

✅ **Result: `[0,0,1,1,2,2]`**

### Code

```kotlin
fun sortColorsDutchFlag(nums: IntArray) {
    var low = 0; var mid = 0; var high = nums.lastIndex
    while (mid <= high) {
        when (nums[mid]) {
            0 -> { swap(nums, low, mid); low++; mid++ }
            1 -> { mid++ }
            2 -> { swap(nums, mid, high); high-- }
        }
    }
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | In-place |

---

## 📊 Comparison Table

| Aspect | Counting Sort | Dutch National Flag |
|--------|---------------|---------------------|
| **Time** | O(N) | O(N) |
| **Space** | O(1) | O(1) |
| **Passes** | Two | One |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Three-way partitioning:** Dutch National Flag partitions into three groups in one pass.
2. **Invariant:** `[0..low)` = 0s, `[low..mid)` = 1s, `[mid..high]` = unknown, `(high..n)` = 2s.
3. **Why advance mid on 0 but not on 2:** When swapping 0 to `low`, the swapped-in element is 1 (since `low..mid` is all 1s). When swapping 2 to `high`, the swapped-in element is unknown.
4. **Named after the Dutch flag:** Red (0), white (1), blue (2) — proposed by Dijkstra.
5. **Pattern:** Three-way partitioning extends to QuickSort 3-way partitioning.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Sort Colors | [#75](https://leetcode.com/problems/sort-colors/) | Medium |
| Move Zeroes | [#283](https://leetcode.com/problems/move-zeroes/) | Easy |
| Sort Array By Parity | [#905](https://leetcode.com/problems/sort-array-by-parity/) | Easy |
| Wiggle Sort II | [#324](https://leetcode.com/problems/wiggle-sort-ii/) | Medium |
