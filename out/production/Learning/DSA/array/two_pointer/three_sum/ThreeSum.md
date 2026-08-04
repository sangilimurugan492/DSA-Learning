# 3Sum — Detailed Explanation

> **LeetCode #15** | [Problem Link](https://leetcode.com/problems/3sum/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 5 most asked)  
> **Topic:** Array, Two Pointer, Sorting

---

## 📋 Problem Statement

Given an integer array `nums`, return all **unique triplets** `[nums[i], nums[j], nums[k]]` such that `i != j != k` and `nums[i] + nums[j] + nums[k] == 0`. The solution set must not contain duplicate triplets.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[-1, 0, 1, 2, -1, -4]` | `[[-1, -1, 2], [-1, 0, 1]]` | -1+0+1=0, -1+(-1)+2=0 |
| `[0, 1, 1]` | `[]` | No triplet sums to 0 |
| `[0, 0, 0]` | `[[0, 0, 0]]` | 0+0+0=0 |

### Visual Walkthrough — Example 1: `[-1, 0, 1, 2, -1, -4]`

```
Sorted: [-4, -1, -1, 0, 1, 2]

i=0: nums[i]=-4 → need two numbers that sum to 4
     left=-1, right=2 → sum=-3 < 0 → left++
     left=-1, right=2 → sum=-3 < 0 → left++
     ... no triplet found

i=1: nums[i]=-1 → need two numbers that sum to 1
     left=-1, right=2 → sum=0 ✅ → [-1, -1, 2]
     left=0, right=1 → sum=0 ✅ → [-1, 0, 1]

Result: [[-1, -1, 2], [-1, 0, 1]] ✅
```

---

## 🧩 Method 1: Brute Force — Three Nested Loops

### Core Idea

Try every triplet `(i, j, k)` and check if they sum to 0. Use a Set to avoid duplicates.

### Algorithm — Step by Step

1. **For each** `i` from `0` to `n-1`:
   **For each** `j` from `i+1` to `n-1`:
      **For each** `k` from `j+1` to `n-1`:
         - If `nums[i] + nums[j] + nums[k] == 0`, add sorted triplet to Set.
2. **Return** the Set as a list.

### Code

```kotlin
fun threeSumBruteForce(nums: IntArray): List<List<Int>> {
    val result = mutableSetOf<List<Int>>()
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            for (k in j + 1 until nums.size) {
                if (nums[i] + nums[j] + nums[k] == 0) {
                    result.add(listOf(nums[i], nums[j], nums[k]).sorted())
                }
            }
        }
    }
    return result.toList()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N³) | Three nested loops |
| **Space** | O(N) | Set for unique triplets |

---

## 🧩 Method 2: Sort + Two Pointer (Optimal)

### Core Idea

Sort the array. Fix the first element `nums[i]`, then use two pointers to find pairs that sum to `-nums[i]`. Skip duplicates at each level.

### Key Insight

> After sorting, if we fix `nums[i]`, the problem reduces to "find two numbers that sum to `-nums[i]`" — a classic two-pointer problem on a sorted array.

### Algorithm — Step by Step

1. **Sort** the array.
2. **For each** `i` from `0` to `n-1`:
   - **Skip duplicates:** If `nums[i] == nums[i-1]`, skip (already processed).
   - **Early exit:** If `nums[i] > 0`, break (no triplet can sum to 0).
   - **Two-pointer:** Set `left = i+1`, `right = n-1`.
   - **While** `left < right`:
     - `sum = nums[i] + nums[left] + nums[right]`
     - If `sum < 0`: `left++` (need larger sum).
     - If `sum > 0`: `right--` (need smaller sum).
     - If `sum == 0`: add triplet, then skip duplicates on both sides.
3. **Return** result.

### Dry Run — Example 1: `[-1, 0, 1, 2, -1, -4]`

**Sorted:** `[-4, -1, -1, 0, 1, 2]`

| `i` | `nums[i]` | `left` | `right` | `sum` | Action | Triplet Found |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 0 | -4 | 1 | 5 | -4+(-1)+2 = -3 | sum < 0 → left++ | — |
| 0 | -4 | 2 | 5 | -4+(-1)+2 = -3 | sum < 0 → left++ | — |
| 0 | -4 | 3 | 5 | -4+0+2 = -2 | sum < 0 → left++ | — |
| 0 | -4 | 4 | 5 | -4+1+2 = -1 | sum < 0 → left++ | — |
| 0 | -4 | 5 | 5 | left ≥ right → stop | — | — |
| 1 | -1 | 2 | 5 | -1+(-1)+2 = 0 | **FOUND!** | **[-1, -1, 2]** |
| 1 | -1 | 3 | 4 | -1+0+1 = 0 | **FOUND!** | **[-1, 0, 1]** |
| 1 | -1 | 4 | 3 | left ≥ right → stop | — | — |
| 2 | -1 | — | — | — | SKIP (duplicate of i=1) | — |
| 3 | 0 | — | — | — | nums[i] > 0 → BREAK | — |

✅ **Result: `[[-1, -1, 2], [-1, 0, 1]]`**

### Code

```kotlin
fun threeSumTwoPointer(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    nums.sort()

    for (i in nums.indices) {
        if (i > 0 && nums[i] == nums[i - 1]) continue  // skip duplicate first element
        if (nums[i] > 0) break  // optimization

        var left = i + 1
        var right = nums.size - 1

        while (left < right) {
            val sum = nums[i] + nums[left] + nums[right]
            when {
                sum < 0 -> left++
                sum > 0 -> right--
                else -> {
                    result.add(listOf(nums[i], nums[left], nums[right]))
                    left++
                    right--
                    while (left < right && nums[left] == nums[left - 1]) left++
                    while (left < right && nums[right] == nums[right + 1]) right--
                }
            }
        }
    }
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Sort O(N log N) + two-pointer scan O(N²) |
| **Space** | O(1) | Ignoring output space |

### Why It Works

- **Sorting** enables the two-pointer technique: if sum is too small, move left pointer right (larger); if too large, move right pointer left (smaller).
- **Duplicate skipping** at three levels ensures unique triplets:
  1. Skip duplicate `nums[i]` (first element).
  2. Skip duplicate `nums[left]` after finding a triplet.
  3. Skip duplicate `nums[right]` after finding a triplet.
- **Early exit** when `nums[i] > 0`: since the array is sorted, all subsequent elements are positive, so no triplet can sum to 0.

---

## 📊 Comparison Table

| Aspect | Brute Force | Sort + Two Pointer |
|--------|-------------|-------------------|
| **Time Complexity** | O(N³) | O(N²) |
| **Space Complexity** | O(N) | O(1) |
| **Approach** | Try all triplets, use Set for dedup | Sort, fix one, two-pointer for other two |
| **Optimality** | ❌ TLE on LeetCode | ✅ Optimal |
| **Duplicate handling** | Set (expensive) | Skip in-place (efficient) |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Reduce 3Sum to 2Sum:** Fix one element, then the problem becomes "find two numbers that sum to a target" — classic two-pointer.
2. **Sort first:** Sorting enables the two-pointer technique and makes duplicate skipping trivial.
3. **Skip duplicates at all levels:** This is the most common bug source — forgetting to skip duplicates leads to duplicate triplets in the output.
4. **Early exit:** If `nums[i] > 0` (sorted array), no valid triplet can exist — break early.
5. **Pattern:** This "fix one, two-pointer the rest" pattern extends to 4Sum and beyond.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Two Sum | [#1](https://leetcode.com/problems/two-sum/) | Easy |
| 3Sum | [#15](https://leetcode.com/problems/3sum/) | Medium |
| 3Sum Closest | [#16](https://leetcode.com/problems/3sum-closest/) | Medium |
| 4Sum | [#18](https://leetcode.com/problems/4sum/) | Medium |
