# Minimum Size Subarray Sum — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/minimum-size-subarray-sum/  
> **Topic:** Array, Sliding Window  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array of **positive** integers `nums` and a positive integer `target`, return the
**minimum length** of a contiguous subarray whose sum is greater than or equal to `target`.
If there is no such subarray, return `0`.

### Constraints

- `1 <= target <= 10^9`
- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^4`

### Examples

**Example 1:**

```
Input:  target = 7, nums = [2, 3, 1, 2, 4, 3]
Output: 2

Subarray [4, 3] has sum = 7 ≥ 7, length = 2. No shorter subarray works.
```

**Example 2:**

```
Input:  target = 4, nums = [1, 4, 4]
Output: 1

Subarray [4] has sum = 4 ≥ 4, length = 1.
```

**Example 3:**

```
Input:  target = 11, nums = [1, 1, 1, 1, 1, 1, 1, 1]
Output: 0

Total sum = 8 < 11. No valid subarray exists.
```

---

## 🧩 Method 1: Brute Force — Check All Subarrays

### Core Idea

For each starting index `i`, extend the subarray to the right while accumulating the sum.
As soon as `sum >= target`, record the length and break (longer subarrays from `i` won't
be shorter).

### Step-by-step Walkthrough (Example 1)

```
target = 7, nums = [2, 3, 1, 2, 4, 3]

i=0: sum=2, 5, 6, 8 → 8 ≥ 7 → len=4, break
i=1: sum=3, 4, 6, 10 → 10 ≥ 7 → len=4, break
i=2: sum=1, 3, 7 → 7 ≥ 7 → len=3, break
i=3: sum=2, 6, 9 → 9 ≥ 7 → len=3, break
i=4: sum=4, 7 → 7 ≥ 7 → len=2, break
i=5: sum=3 (< 7, end of array)

minLen = 2 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — for each start, scan to the right |
| **Space** | O(1) |

---

## 🧩 Method 2: Optimal — Sliding Window (Two Pointers)

### Core Idea

Since all elements are **positive**, the sum is **monotonically increasing** as we expand
the window. This means:
- Expanding right increases the sum.
- Contracting left decreases the sum.

We can use a **sliding window** with two pointers:
1. Expand the right pointer to add elements until `sum >= target`.
2. Once the sum is large enough, try to **shrink** from the left to find a shorter valid
   window. Keep shrinking while `sum >= target`.
3. Track the minimum window length across all valid windows.

### Algorithm Steps

1. Initialize `left = 0`, `sum = 0`, `minLen = ∞`.
2. For `right` from `0` to `n-1`:
   a. Add `nums[right]` to `sum` (expand window).
   b. While `sum >= target`:
      - Update `minLen = min(minLen, right - left + 1)`.
      - Subtract `nums[left]` from `sum`, then `left++` (shrink window).
3. Return `minLen` (or `0` if `minLen` is still `∞`).

### Step-by-step Walkthrough (Example 1)

```
target = 7, nums = [2, 3, 1, 2, 4, 3]
left=0, sum=0, minLen=∞
```

| right | nums[right] | sum (after add) | sum ≥ 7? | Shrink action | minLen | left (after) |
|-------|-------------|------------------|----------|----------------|--------|---------------|
| 0     | 2           | 2                | No       | —              | ∞      | 0             |
| 1     | 3           | 5                | No       | —              | ∞      | 0             |
| 2     | 1           | 6                | No       | —              | ∞      | 0             |
| 3     | 2           | 8                | Yes      | sum=6, left=1  | 4      | 1             |
| 4     | 4           | 10               | Yes      | sum=7, left=2 → minLen=3, sum=6, left=3 | 3 | 3 |
| 5     | 3           | 9                | Yes      | sum=7, left=4 → minLen=2, sum=3, left=5 | 2 | 5 |

```
minLen = 2 ✅  (subarray [4, 3] at indices 4-5)
```

### Why does sliding window work?

- **All elements are positive** → expanding always increases sum, contracting always
  decreases sum. This monotonicity is what makes the sliding window valid.
- If there were negative numbers, shrinking could increase the sum, breaking the logic.
  (For arrays with negatives, a different approach like prefix sums + binary search is needed.)
- Each element is **added once** (by `right`) and **removed at most once** (by `left`),
  giving O(N) total.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — each element visited at most twice (once by right, once by left) |
| **Space** | O(1) — only pointers and a sum variable |

### Why is this better than brute force?

| Aspect | Brute Force (All Subarrays) | Optimal (Sliding Window) |
|--------|------------------------------|--------------------------|
| Time | O(N²) | O(N) |
| Space | O(1) | O(1) |
| Key idea | Check every subarray | Expand and shrink window dynamically |
| Requirement | None | All elements must be positive |

---

## 🔑 Key Takeaways

1. **Sliding window** is the optimal technique for subarray sum problems when all elements
   are positive (monotonic sum property).
2. **Expand right** to grow the sum, **shrink left** to minimize the window — both
   pointers only move forward, giving O(N).
3. The `while (sum >= target)` inner loop doesn't make this O(N²) — the left pointer
   moves at most N times total across all iterations.
4. This pattern (variable-size sliding window) is fundamental for many subarray problems.
5. If elements could be negative, sliding window doesn't work — use prefix sums + monotonic
   deque or binary search instead.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Minimum Size Subarray Sum | [link](https://leetcode.com/problems/minimum-size-subarray-sum/) | Medium |
| Subarray Sum Equals K | [link](https://leetcode.com/problems/subarray-sum-equals-k/) | Medium |
| Maximum Average Subarray I | [link](https://leetcode.com/problems/maximum-average-subarray-i/) | Easy |
| Longest Substring Without Repeating Characters | [link](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium |
