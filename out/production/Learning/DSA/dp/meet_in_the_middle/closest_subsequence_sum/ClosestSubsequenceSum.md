# Closest Subsequence Sum — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/closest-subsequence-sum/  
> **Topic:** Meet in the Middle, Binary Search, Bitmask  
> **Difficulty:** Hard

---

## 📋 Problem Statement

You are given an integer array `nums` and an integer `goal`.

You want to choose a **subsequence** of `nums` such that the sum of its elements is **closest** to `goal`. Return the minimum possible absolute value of the difference.

### Constraints

- `1 <= nums.length <= 40`
- `-10^7 <= nums[i] <= 10^7`
- `-10^9 <= goal <= 10^9`

### Examples

**Example 1:**

```
Input:  nums = [5, -7, 3, 5], goal = 6
Output: 0
```

**Example 2:**

```
Input:  nums = [7, -9, 15, -2], goal = -5
Output: 1
```

**Example 3:**

```
Input:  nums = [1, 2, 3], goal = -7
Output: 7
```

---

## 🧩 Why Meet in the Middle?

With `n ≤ 40`, there are up to `2^40 ≈ 10^12` subsequences — far too many to enumerate directly. But `2^20 ≈ 10^6` is very manageable.

**Split the array in half.** Any subsequence sum = (subsequence sum of left half) + (subsequence sum of right half). So:

1. Enumerate all `2^(n/2)` subset sums of the left half → `leftSums`.
2. Enumerate all `2^(n/2)` subset sums of the right half → `rightSums`.
3. Sort `rightSums`. For each `ls ∈ leftSums`, binary search for the value closest to `goal − ls` in `rightSums`.

---

## 🧩 Method 1: Brute Force (Full Enumeration)

### Core Idea

Enumerate all `2^n` subset sums and track the minimum `|sum − goal|`. Only works for `n ≤ ~25`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(2^N) — infeasible for N = 40 |
| **Space** | O(1) |

---

## 🧩 Method 2: Meet in the Middle + Binary Search ✅ Optimal

### Core Idea

Split into halves, enumerate each half's subset sums, sort one side, and binary search for the best complement.

### Step-by-step Walkthrough

```
nums = [5, -7, 3, 5], goal = 6

Left half = [5, -7]  → leftSums  = {0, 5, -7, -2}
Right half = [3, 5]  → rightSums = {0, 3, 5, 8}  (sorted)

For ls = 0:  need 6  → closest in rightSums: 5 → |0+5-6| = 1
For ls = 5:  need 1  → closest: 0 → |5+0-6| = 1
For ls = -7: need 13 → closest: 8 → |-7+8-6| = 5
For ls = -2: need 8  → closest: 8 → |-2+8-6| = 0 ✅

Answer: 0
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(2^(N/2) × N) — enumerate + sort + binary search |
| **Space** | O(2^(N/2)) — storing one half's sums |

---

## 🔑 Key Takeaways

1. **Meet in the Middle** is the go-to technique when `n ≤ 40` and subset enumeration is needed — it converts O(2^N) into O(2^(N/2) · log).
2. The key identity: any subset sum = (left-half sum) + (right-half sum).
3. **Sort + binary search** matches each left sum with its best right complement efficiently.
4. When binary searching, check **both neighbors** (`floor` and `ceiling` of the target) — the exact target may not exist.
5. Compare with DP subset-sum: DP works when values are small; meet-in-the-middle works when values are huge but `n` is small.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Partition Array Into Two Arrays to Minimize Sum Difference | [link](https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/) | Hard |
| Split Array With Same Average | [link](https://leetcode.com/problems/split-array-with-same-average/) | Hard |
| Subsets | [link](https://leetcode.com/problems/subsets/) | Medium |
