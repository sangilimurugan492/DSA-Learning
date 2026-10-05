# Split Array With Same Average — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/split-array-with-same-average/  
> **Topic:** Dynamic Programming, Math, Subset Sum  
> **Difficulty:** Hard

---

## 📋 Problem Statement

You are given an integer array `nums`.

Move **one element** of `nums` to another array `B` (initially empty). After the move, `A` and `B` are **non-empty**, and `average(A) == average(B)`.

Return `true` if it is possible to achieve that, otherwise return `false`.

**Note:** The average of an array is the sum of its elements divided by its length.

### Constraints

- `1 <= nums.length <= 30`
- `0 <= nums[i] <= 10^4`

### Examples

**Example 1:**

```
Input:  nums = [1, 2, 3, 4, 5, 6, 7, 8]
Output: true
```

**Example 2:**

```
Input:  nums = [3, 1]
Output: false
```

---

## 🧩 Key Mathematical Insight

If we split `nums` into `A` (size `k`) and `B` (size `n - k`) with equal averages:

```
sum(A) / k = sum(B) / (n - k) = total / n
```

So the problem reduces to: **does there exist a subset of size `k` (for some `1 <= k <= n/2`) whose sum equals `k * total / n`?**

Two crucial prunings:

1. `k * total` must be divisible by `n` — otherwise no valid subset of size `k` exists.
2. We only need to check `k` up to `n / 2` — if a subset of size `k` works, its complement (size `n - k`) also works, and one of the two is at most `n / 2`.

---

## 🧩 Method 1: Brute Force (Bitmask Enumeration)

### Core Idea

Enumerate every non-empty proper subset via bitmask and check whether its average equals the global average. Only feasible for small `n` (≤ ~20).

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(2^N × N) — exponential |
| **Space** | O(1) |

---

## 🧩 Method 2: DP over (count → achievable sums) ✅ Optimal

### Core Idea

Build `dp[k]` = set of all subset sums achievable using **exactly `k` elements**.

For each number, update the DP **from high `k` to low `k`** (0/1 knapsack style, so each element is used at most once):

```
dp[k] = { s + num  |  s ∈ dp[k-1] }
```

Finally, for each `k` in `1..n/2` where `k * total % n == 0`, check whether `k * total / n ∈ dp[k]`.

### Step-by-step Walkthrough

```
nums = [1, 2, 3, 4, 5, 6, 7, 8], n = 8, total = 36, half = 4

Check k = 1: 1*36 % 8 = 4 ≠ 0  → skip
Check k = 2: 2*36 % 8 = 0, target = 9. Is 9 in dp[2]? {1+8, 2+7, 3+6, 4+5, ...} → YES ✅

Answer: true  (e.g., A = [1, 8] avg 4.5, B = rest avg 4.5)
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N² × S) where S = number of distinct sums (bounded by N × 10^4) |
| **Space** | O(N × S) for the DP sets |

---

## 🔑 Key Takeaways

1. **Reframe the problem with math**: equal averages ⇔ a subset of size `k` summing to `k·total/n`. This transforms a "split" problem into a **subset-sum** problem.
2. **Divisibility pruning** (`k * total % n == 0`) eliminates most impossible sizes `k` for free.
3. **Symmetry pruning**: only check `k ≤ n/2` — the complement covers the other half.
4. DP keyed by **(element count → sum set)** is a powerful pattern for "subset with exact size and sum" queries.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Partition Equal Subset Sum | [link](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium |
| Closest Subsequence Sum | [link](https://leetcode.com/problems/closest-subsequence-sum/) | Hard |
| Partition Array Into Two Arrays to Minimize Sum Difference | [link](https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/) | Hard |
