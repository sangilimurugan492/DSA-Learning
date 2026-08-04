# 3Sum Closest — Detailed Explanation

> **LeetCode #16** | [Problem Link](https://leetcode.com/problems/3sum-closest/)
> **FAANG Importance:** ⭐⭐⭐⭐ (Two-pointer + sorting pattern)
> **Topic:** Array, Two Pointers, Sorting

---

## 📋 Problem Statement

Given an integer array `nums` and an integer `target`, return the sum of three integers in `nums` that is closest to `target`. Assume exactly one solution exists.

### Examples

```
Input: nums = [-1,2,1,-4], target = 1  → Output: 2  (-1+2+1=2)
Input: nums = [0,0,0], target = 1       → Output: 0  (0+0+0=0)
```

---

## 🧩 Method 1: Two-Pointer — O(N²)

### Core Idea

Sort the array. Fix one element, use two pointers for the other two. Track the closest sum to target.

### Key Insight

> After sorting, if the current sum < target, move left pointer right (increase sum). If sum > target, move right pointer left (decrease sum). This efficiently narrows down the closest sum.

### Dry Run — `nums = [-1,2,1,-4], target = 1`

```
Sorted: [-4, -1, 1, 2]
closest = -4 + -1 + 1 = -4

i=0 (nums[0]=-4):
  left=1(-1), right=3(2): sum = -4-1+2 = -3, |−3−1|=4 > |−4−1|=5? No → closest=-3
    sum(-3) < target(1) → left++
  left=2(1), right=3(2): sum = -4+1+2 = -1, |−1−1|=2 < 4 → closest=-1
    sum(-1) < target(1) → left++
  left=3, right=3 → stop

i=1 (nums[1]=-1):
  left=2(1), right=3(2): sum = -1+1+2 = 2, |2−1|=1 < 2 → closest=2
    sum(2) > target(1) → right--
  left=2, right=2 → stop

Result: 2 ✅
```

### Code

```kotlin
fun threeSumClosest(nums: IntArray, target: Int): Int {
    nums.sort()
    var closest = nums[0] + nums[1] + nums[2]

    for (i in 0 until nums.size - 2) {
        var left = i + 1
        var right = nums.size - 1

        while (left < right) {
            val sum = nums[i] + nums[left] + nums[right]
            if (kotlin.math.abs(sum - target) < kotlin.math.abs(closest - target)) {
                closest = sum
            }
            when {
                sum < target -> left++
                sum > target -> right--
                else -> return sum  // Exact match
            }
        }
    }
    return closest
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | N iterations × two-pointer scan |
| **Space** | O(1) | In-place (ignoring sort) |

---

## 🧩 Method 2: Brute Force — O(N³)

### Core Idea

Try all triplets, track the closest sum.

### Code

```kotlin
fun threeSumClosestBrute(nums: IntArray, target: Int): Int {
    var closest = nums[0] + nums[1] + nums[2]
    for (i in 0 until nums.size - 2) {
        for (j in i + 1 until nums.size - 1) {
            for (k in j + 1 until nums.size) {
                val sum = nums[i] + nums[j] + nums[k]
                if (kotlin.math.abs(sum - target) < kotlin.math.abs(closest - target)) {
                    closest = sum
                }
            }
        }
    }
    return closest
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N³) | All triplets |
| **Space** | O(1) | No extra space |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Two-Pointer | O(N²) | O(1) | Always prefer |
| Brute Force | O(N³) | O(1) | Baseline only |

> **Interview Tip:** Sort first, then fix one element and use two pointers. Early exit on exact match (sum == target). This is the same pattern as 3Sum — the only difference is tracking the closest instead of collecting all triplets. Skip duplicates for optimization if needed.
