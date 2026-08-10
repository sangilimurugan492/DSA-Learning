# Missing Number — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/missing-number/  
> **Topic:** Array, Math (Gauss' Formula)  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array `nums` containing `n` **distinct** numbers in the range `[0, n]`, return
the only number in the range that is missing from the array.

### Constraints

- `n == nums.length`
- `1 <= n <= 10^4`
- `0 <= nums[i] <= n`
- All numbers in `nums` are unique.

### Examples

**Example 1:**

```
Input:  nums = [3, 0, 1]
Output: 2

n = 3, range is [0, 1, 2, 3]. The array has {0, 1, 3}. Missing: 2.
```

**Example 2:**

```
Input:  nums = [0, 1]
Output: 2

n = 2, range is [0, 1, 2]. The array has {0, 1}. Missing: 2.
```

**Example 3:**

```
Input:  nums = [9, 6, 4, 2, 3, 5, 7, 0, 1]
Output: 8

n = 9, range is [0..9]. Missing: 8.
```

---

## 🧩 Method 1: Brute Force — Check Each Number in Range

### Core Idea

For each number `i` from `0` to `n`, check if it exists in the array. If not, it's the
missing number.

### Step-by-step Walkthrough (Example 1)

```
nums = [3, 0, 1], n = 3

i=0: found in nums? Yes (nums[1]=0)  → continue
i=1: found in nums? Yes (nums[2]=1)  → continue
i=2: found in nums? No              → return 2 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — for each of N+1 numbers, scan the array |
| **Space** | O(1) |

---

## 🧩 Method 2: Optimal — Gauss' Formula (Math)

### Core Idea

The sum of numbers from `0` to `n` is given by **Gauss' formula**: `n * (n + 1) / 2`.

If we compute the **expected sum** (all numbers 0..n) and subtract the **actual sum** of
the array, the difference is the missing number.

```
missing = expectedSum - actualSum
        = n*(n+1)/2 - sum(nums)
```

### Algorithm Steps

1. Compute `expectedSum = n * (n + 1) / 2` using Gauss' formula.
2. Compute `actualSum = sum of all elements in nums`.
3. Return `expectedSum - actualSum`.

### Step-by-step Walkthrough (Example 1)

```
nums = [3, 0, 1], n = 3

Step 1 — Expected sum:
  expectedSum = 3 * 4 / 2 = 6
  (This is 0 + 1 + 2 + 3 = 6)

Step 2 — Actual sum:
  actualSum = 3 + 0 + 1 = 4

Step 3 — Missing number:
  missing = 6 - 4 = 2 ✅
```

### Why does this work?

- The range `[0, n]` has exactly `n + 1` numbers, and the array has `n` numbers.
- Exactly one number is missing, so the difference between the expected sum and actual
  sum equals that missing number.
- This is a direct consequence of the fact that all numbers are unique and in `[0, n]`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass to compute the sum |
| **Space** | O(1) — only a few variables |

### Overflow Note

For `n` up to 10^4, the maximum sum is `10^4 * 10^4 / 2 ≈ 5 * 10^7`, which fits in `Int`.
However, using `Long` is safer for larger inputs and avoids potential overflow during the
`n * (n + 1)` multiplication.

### Alternative: XOR Approach

Another O(N) / O(1) approach uses XOR: XOR all indices `0..n` and all array elements.
Duplicates cancel out (x ^ x = 0), leaving only the missing number. This avoids any overflow
concerns entirely. (See `DSA/array/bit_manipulation/missing_number/` for the XOR implementation.)

### Comparison of Methods

| Method | Time | Space | Notes |
|--------|------|-------|-------|
| Brute Force | O(N²) | O(1) | Simple but slow |
| Gauss' Formula | O(N) | O(1) | Elegant math approach |
| XOR | O(N) | O(1) | No overflow risk, bit manipulation |

---

## 🔑 Key Takeaways

1. **Gauss' formula** `n*(n+1)/2` gives the sum of 0..n in O(1) — a powerful tool for
   range-sum problems.
2. The missing number = expected sum − actual sum — a single subtraction.
3. All numbers must be **unique** and in `[0, n]` for this to work.
4. XOR is an alternative that avoids overflow entirely (x ^ x = 0).
5. Time is O(N) with O(1) space — optimal for this problem.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Missing Number | [link](https://leetcode.com/problems/missing-number/) | Easy |
| Find the Duplicate Number | [link](https://leetcode.com/problems/find-the-duplicate-number/) | Medium |
| Single Number | [link](https://leetcode.com/problems/single-number/) | Easy |
| First Missing Positive | [link](https://leetcode.com/problems/first-missing-positive/) | Hard |
