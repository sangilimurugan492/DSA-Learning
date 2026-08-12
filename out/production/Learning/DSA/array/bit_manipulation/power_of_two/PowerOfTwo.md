# Power of Two — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/power-of-two/  
> **Topic:** Bit Manipulation  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an integer `n`, return `true` if it is a power of two. Otherwise, return `false`.

An integer `n` is a power of two if there exists an integer `x` such that `n == 2^x`.

### Constraints

- `-2^31 <= n <= 2^31 - 1`

### Examples

```
n = 1  → true   (2^0 = 1, binary: 1)
n = 16 → true   (2^4 = 16, binary: 10000)
n = 3  → false  (binary: 11, two set bits)
n = 0  → false
n = -16 → false (negative)
```

---

## 🧩 Method: Bit Trick — Single Set Bit Check

### Core Idea

A power of two has **exactly one '1' bit** in its binary representation. For example:
- 1 = `1`, 2 = `10`, 4 = `100`, 8 = `1000`, ...

The trick `n & (n - 1)` removes the lowest set bit. If the result is 0, there was only one
set bit.

**Condition:** `n > 0 && (n & (n - 1)) == 0`

- `n > 0` — powers of two are positive (excludes 0 and negatives)
- `(n & (n - 1)) == 0` — exactly one set bit

### Step-by-step Walkthrough

```
n = 16 (10000):
  n - 1 = 15 (01111)
  n & (n-1) = 10000 & 01111 = 00000 = 0 → true ✅

n = 3 (11):
  n - 1 = 2 (10)
  n & (n-1) = 11 & 10 = 10 = 2 ≠ 0 → false ✅

n = 0:
  n > 0 fails → false ✅

n = -16:
  n > 0 fails → false ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(1) — single bit operation |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. Powers of two have **exactly one set bit**.
2. `n & (n - 1)` removes the lowest set bit — if result is 0, only one bit was set.
3. Always check `n > 0` first — 0 and negatives are not powers of two.
4. This is one of the most common bit manipulation interview questions.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Power of Two | [link](https://leetcode.com/problems/power-of-two/) | Easy |
| Power of Three | [link](https://leetcode.com/problems/power-of-three/) | Easy |
| Power of Four | [link](https://leetcode.com/problems/power-of-four/) | Easy |
| Number of 1 Bits | [link](https://leetcode.com/problems/number-of-1-bits/) | Easy |
