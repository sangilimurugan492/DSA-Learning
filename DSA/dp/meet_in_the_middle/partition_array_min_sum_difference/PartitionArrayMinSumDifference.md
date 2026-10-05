# Partition Array Into Two Arrays to Minimize Sum Difference — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/  
> **Topic:** Meet in the Middle, Binary Search, Two Pointers  
> **Difficulty:** Hard

---

## 📋 Problem Statement

You are given an array `nums` of `2n` integers. You need to partition `nums` into two arrays `A` and `B`, each of length `n`.

- The **score** is `|sum(A) − sum(B)|`.
- Each element of `nums` must go to **exactly one** of `A` or `B`.

Return the **minimum score** possible.

### Constraints

- `2 <= n <= 30` (array length `2n` is even, `4 <= 2n <= 60`)
- `nums.length == 2 * n`
- `-10^7 <= nums[i] <= 10^7`

### Examples

**Example 1:**

```
Input:  nums = [3, 9, 7, 3]
Output: 2
```

**Example 2:**

```
Input:  nums = [-36, 36]
Output: 72
```

**Example 3:**

```
Input:  nums = [2, -1, 0, 4, -2, -9]
Output: 0
```

---

## 🧩 Why Meet in the Middle?

The array has `2n ≤ 60` elements, so there are up to `C(60, 30) ≈ 10^17` ways to choose which elements go to `A` — astronomically many. But `2^30 ≈ 10^9` per half is still too much... unless we split into halves of `n ≤ 30` elements each and enumerate `2^15 ≈ 3×10^4` states per half. That's the trick.

---

## 🧩 Method 1: Brute Force (Try All Splits)

### Core Idea

Recursively assign each element to `A` or `B` (respecting the size-n constraint) and track the minimum difference. Only feasible for tiny inputs.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(C(2N, N)) — combinatorial explosion |
| **Space** | O(N) recursion depth |

---

## 🧩 Method 2: Meet in the Middle on (sum, count) Pairs ✅ Optimal

### Core Idea

1. Split `nums` into `left = nums[0..n)` and `right = nums[n..2n)`.
2. For each half, enumerate all subsets, recording `(sum, count)` — the subset's sum and its size.
3. Group left subsets by count: `leftByCount[c]` = sorted list of sums of left subsets with `c` elements.
4. If `A` takes `c` elements from the left half, it must take `n − c` from the right half. For each right subset `(rSum, rCount)`:
   - We need a left subset with `count = n − rCount` and sum as close as possible to `target = total/2 − rSum` (since `sum(A) = lSum + rSum`, and the difference is minimized when `sum(A)` is closest to `total/2`).
   - Binary search `leftByCount[n − rCount]` for the value closest to `target`.

### Why "closest to total/2"?

```
diff = |sum(A) − sum(B)| = |sum(A) − (total − sum(A))| = |2·sum(A) − total|
```

This is minimized when `sum(A)` is as close as possible to `total / 2`.

### Step-by-step Walkthrough

```
nums = [3, 9, 7, 3], n = 2, total = 22, half = 11

left = [3, 9]  → subsets: (0,0), (3,1), (9,1), (12,2)
right = [7, 3] → subsets: (0,0), (7,1), (3,1), (10,2)

For right subset (7,1): need left count = 1, target = 11 − 7 = 4.
  leftByCount[1] = [3, 9] → closest to 4 is 3 → sum(A) = 3+7 = 10, diff = |20−22| = 2 ✅

Answer: 2
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(2^N × N) — N = n (half length); enumeration + sort + binary search |
| **Space** | O(2^N) — subset (sum, count) lists |

---

## 🔑 Key Takeaways

1. **Meet in the middle with counts**: when subsets must have an exact size, carry the **count** alongside the sum and group by it.
2. The **complement trick**: if `A` takes `c` from the left half, it takes `n − c` from the right half — this pairs left and right subsets of complementary sizes.
3. **Reduce to "closest to total/2"**: minimizing `|sum(A) − sum(B)|` is equivalent to making `sum(A)` as close as possible to `total/2`.
4. This problem is the "hardest variant" of the meet-in-the-middle family: it combines subset-sum enumeration, size constraints, and binary search — master 1755 first, then this.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Closest Subsequence Sum | [link](https://leetcode.com/problems/closest-subsequence-sum/) | Hard |
| Partition Equal Subset Sum | [link](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium |
| Split Array With Same Average | [link](https://leetcode.com/problems/split-array-with-same-average/) | Hard |
