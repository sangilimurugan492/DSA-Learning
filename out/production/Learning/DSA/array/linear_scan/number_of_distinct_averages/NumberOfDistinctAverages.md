# Number of Distinct Averages — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/number-of-distinct-averages/  
> **Topic:** Array, Sorting, Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

You are given an integer array `nums` of **even** length. You must perform the following
algorithm on `nums`:

1. Let `min` and `max` be the minimum and maximum values in `nums` respectively.
2. Remove both `min` and `max` from `nums`.
3. Add `(min + max) / 2` to a set of averages.
4. Repeat until `nums` is empty.

Return the number of **distinct** averages computed.

### Constraints

- `1 <= nums.length <= 100` (always even)
- `0 <= nums[i] <= 100`

### Examples

**Example 1:**

```
Input:  nums = [4, 1, 4, 0, 3, 5]
Output: 2

Step 1: min=0, max=5 → avg = (0+5)/2 = 2.5 → set = {2.5}, nums = [4,1,4,3]
Step 2: min=1, max=4 → avg = (1+4)/2 = 2.5 → set = {2.5}, nums = [4,3]
Step 3: min=3, max=4 → avg = (3+4)/2 = 3.5 → set = {2.5, 3.5}

Distinct averages = 2
```

**Example 2:**

```
Input:  nums = [1, 100]
Output: 1

Step 1: min=1, max=100 → avg = (1+100)/2 = 50.5 → set = {50.5}

Distinct averages = 1
```

---

## 🧩 Method 1: Brute Force — Repeatedly Find Min and Max

### Core Idea

Simulate the algorithm literally: for each of N/2 iterations, scan the current array to find
the min and max, compute their average, add it to a set, then remove both elements. Repeat
until the array is empty.

### Step-by-step Walkthrough (Example 1)

```
nums = [4, 1, 4, 0, 3, 5]

Iteration 1:
  Scan → min=0, max=5
  avg = (0+5)/2 = 2.5 → set = {2.5}
  Remove 0 and 5 → list = [4, 1, 4, 3]

Iteration 2:
  Scan → min=1, max=4
  avg = (1+4)/2 = 2.5 → set = {2.5}
  Remove 1 and 4 → list = [4, 3]

Iteration 3:
  Scan → min=3, max=4
  avg = (3+4)/2 = 3.5 → set = {2.5, 3.5}
  Remove 3 and 4 → list = []

Set size = 2
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — N/2 iterations × O(N) scan + O(N) removal per iteration |
| **Space** | O(N) — for the mutable list copy + set of averages |

---

## 🧩 Method 2: Optimal — Sort + Two Pointers

### Core Idea

The key insight is that **after sorting, the min is always at the left end and the max is
always at the right end**. Instead of repeatedly scanning to find min/max and removing
elements, we can:

1. **Sort** the array once.
2. Use **two pointers** (`left` starting at 0, `right` starting at n-1) moving inward.
3. At each step, pair `nums[left]` (current min) with `nums[right]` (current max),
   compute their average, and add to a set.
4. Move both pointers inward (`left++`, `right--`) and repeat.

This eliminates the repeated O(N) scans and element removals.

### Algorithm Steps

1. **Sort** `nums` in ascending order.
2. Initialize `left = 0`, `right = nums.size - 1`, and an empty set `averages`.
3. While `left < right`:
   - Compute `avg = (nums[left] + nums[right]) / 2.0`.
   - Add `avg` to the set.
   - `left++`, `right--`.
4. Return `averages.size`.

### Step-by-step Walkthrough (Example 1)

```
nums = [4, 1, 4, 0, 3, 5]
```

**Step 1 — Sort:**

```
sorted = [0, 1, 3, 4, 4, 5]
          ↑           ↑
        left        right
```

**Step 2 — Two-pointer pairing:**

| Iteration | left | right | nums[left] | nums[right] | Average | Set |
|-----------|------|-------|------------|-------------|---------|-----|
| 1         | 0    | 5     | 0          | 5           | (0+5)/2 = 2.5 | {2.5} |
| 2         | 1    | 4     | 1          | 4           | (1+4)/2 = 2.5 | {2.5} |
| 3         | 2    | 3     | 3          | 4           | (3+4)/2 = 3.5 | {2.5, 3.5} |

**Step 3 — `left=3 >= right=2` → stop.**

**Distinct averages = 2** ✅

### Why does sorting work?

- After sorting, the smallest element is at index 0 and the largest at index n-1.
- Removing them is equivalent to moving the left pointer right and the right pointer left.
- The next smallest is at the new left, and the next largest is at the new right.
- So the two-pointer approach perfectly simulates the remove-min-max algorithm.

### Precision Note

Since values are integers in [0, 100], the average `(a + b) / 2.0` is always either an
integer (e.g., `4.0`) or a half-integer (e.g., `2.5`). Both are **exactly representable**
in `Double`, so there are no floating-point precision issues with set comparison.

Alternatively, to avoid doubles entirely, store `(a + b)` as an integer — two averages are
equal if and only if their sums are equal (since we always divide by 2).

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting; the two-pointer pass is O(N) |
| **Space** | O(N) — for the set of averages (at most N/2 distinct entries) |

### Why is this better than brute force?

| Aspect | Brute Force (Repeated Scan) | Optimal (Sort + Two Pointers) |
|--------|-----------------------------|-------------------------------|
| Time | O(N²) | O(N log N) |
| Space | O(N) | O(N) |
| Key idea | Scan for min/max each iteration | Sort once, use pointers |
| Element removal | O(N) per removal | None — just move pointers |

---

## 🔑 Key Takeaways

1. **Sorting** transforms the problem: after sorting, min and max are at the two ends,
   eliminating the need for repeated scans.
2. **Two pointers** moving inward from both ends naturally pairs min with max at each step.
3. A **HashSet** automatically handles distinctness — duplicates are ignored.
4. The two-pointer pattern is a natural fit for "pair smallest with largest" problems.
5. No floating-point precision issues since all averages are integers or half-integers
   (exactly representable in Double).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Number of Distinct Averages | [link](https://leetcode.com/problems/number-of-distinct-averages/) | Easy |
| Minimum Absolute Difference | [link](https://leetcode.com/problems/minimum-absolute-difference/) | Easy |
| Two Sum II - Input Array Is Sorted | [link](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium |
| Boats to Save People | [link](https://leetcode.com/problems/boats-to-save-people/) | Medium |
