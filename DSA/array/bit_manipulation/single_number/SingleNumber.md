# Single Number — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/single-number/  
> **Topic:** Array, Bit Manipulation  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given a **non-empty** array of integers `nums`, every element appears **twice** except for
one. Find that single one.

You must implement a solution with **O(n) time** and **O(1) space**.

### Constraints

- `1 <= nums.length <= 3 * 10^4`
- `-3 * 10^4 <= nums[i] <= 3 * 10^4`
- Each element appears exactly twice except for one element which appears once.

### Examples

```
Input:  nums = [4, 1, 2, 1, 2]
Output: 4

Input:  nums = [2, 2, 1]
Output: 1
```

---

## 🧩 Method 1: Brute Force — Count Occurrences

### Core Idea

For each element, count how many times it appears in the array. Return the one with count 1.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — for each element, scan the array |
| **Space** | O(1) |

---

## 🧩 Method 2: Better — HashSet

### Core Idea

Use a HashSet: add on first occurrence, remove on second. The remaining element is the
single number.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) — for the HashSet |

---

## 🧩 Method 3: Optimal — XOR

### Core Idea

**XOR properties:**
- `a ^ a = 0` (same bits cancel)
- `a ^ 0 = a` (identity)
- XOR is commutative and associative (order doesn't matter)

XOR all elements together. Pairs cancel out to 0, and the single number XOR'd with 0 gives
itself.

### Step-by-step Walkthrough (nums = [4, 1, 2, 1, 2])

```
result = 0

result = 0 ^ 4 = 4
result = 4 ^ 1 = 5
result = 5 ^ 2 = 7
result = 7 ^ 1 = 6
result = 6 ^ 2 = 4

Result = 4 ✅

Explanation: (4 ^ 1 ^ 2 ^ 1 ^ 2) = 4 ^ (1^1) ^ (2^2) = 4 ^ 0 ^ 0 = 4
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — only one variable |

### Comparison of Methods

| Method | Time | Space | Notes |
|--------|------|-------|-------|
| Brute Force | O(N²) | O(1) | Simple but slow |
| HashSet | O(N) | O(N) | Uses extra space |
| XOR | O(N) | O(1) | Optimal — meets all constraints |

---

## 🔑 Key Takeaways

1. **XOR cancels pairs**: `a ^ a = 0`. All duplicate pairs cancel, leaving the single number.
2. XOR is **commutative and associative** — order doesn't matter.
3. This is O(N) time and O(1) space — the optimal solution.
4. A foundational bit manipulation technique used in many problems.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Single Number | [link](https://leetcode.com/problems/single-number/) | Easy |
| Single Number II | [link](https://leetcode.com/problems/single-number-ii/) | Medium |
| Single Number III | [link](https://leetcode.com/problems/single-number-iii/) | Medium |
| Missing Number | [link](https://leetcode.com/problems/missing-number/) | Easy |
