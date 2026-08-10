# Missing Number (Bit Manipulation) — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/missing-number/  
> **Topic:** Bit Manipulation, Math  
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
```

**Example 2:**

```
Input:  nums = [0, 1]
Output: 2
```

**Example 3:**

```
Input:  nums = [9, 6, 4, 2, 3, 5, 7, 0, 1]
Output: 8
```

---

## 🧩 Method 1: XOR Approach

### Core Idea

XOR all indices `0..n` and all array elements. Since `x ^ x = 0` and `x ^ 0 = x`, all paired
numbers cancel out, leaving only the missing number.

### Step-by-step Walkthrough (Example 1)

```
nums = [3, 0, 1], n = 3
result = 3 (start with n, since index n is never in the array)

i=0: result = 3 ^ 0 ^ 3 = 0
i=1: result = 0 ^ 1 ^ 0 = 1
i=2: result = 1 ^ 2 ^ 1 = 2

Result = 2 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) |

---

## 🧩 Method 2: Math (Gauss' Formula)

### Core Idea

`missing = n*(n+1)/2 - sum(nums)` — the expected sum minus the actual sum.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass to compute sum |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **XOR** cancels pairs: `x ^ x = 0`. XOR all indices + all values → the missing one remains.
2. Start with `n` (not 0) because index `n` exists in the range but never in the array.
3. **Gauss' formula** is a simpler alternative — no overflow risk for small n.
4. Both approaches are O(N) time, O(1) space — optimal.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Missing Number | [link](https://leetcode.com/problems/missing-number/) | Easy |
| Single Number | [link](https://leetcode.com/problems/single-number/) | Easy |
| Find the Duplicate Number | [link](https://leetcode.com/problems/find-the-duplicate-number/) | Medium |
