# Number of 1 Bits — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/number-of-1-bits/  
> **Topic:** Bit Manipulation  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Write a function that takes the binary representation of an unsigned integer and returns
the number of '1' bits it has (also known as the **Hamming weight**).

### Constraints

- The input is a 32-bit unsigned integer.

### Examples

```
Input:  n = 11    (binary: 1011)        → Output: 3
Input:  n = 128   (binary: 10000000)    → Output: 1
Input:  n = 2147483645                  → Output: 30
```

---

## 🧩 Method 1: Brian Kernighan's Algorithm (Optimal)

### Core Idea

Use the bit trick `n & (n - 1)` which **removes the lowest set bit**. Repeat until `n == 0`,
counting iterations. Each iteration removes exactly one set bit, so the loop runs `k` times
where `k` = number of 1-bits.

### Step-by-step Walkthrough (n = 11, binary 1011)

```
n = 1011 (11)
n & (n-1) = 1011 & 1010 = 1010 (10)  → count=1
n & (n-1) = 1010 & 1001 = 1000 (8)   → count=2
n & (n-1) = 1000 & 0111 = 0000 (0)   → count=3

Result = 3 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(k) where k = number of 1-bits (at most 32) |
| **Space** | O(1) |

---

## 🧩 Method 2: Bit-by-Bit Check

### Core Idea

Check each of the 32 bits: extract LSB with `n & 1`, add to count, then unsigned right
shift `n` by 1.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(32) = O(1) — always checks all 32 bits |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Brian Kernighan's algorithm** is optimal: O(k) where k = set bits, not O(32).
2. `n & (n - 1)` removes the lowest set bit — a fundamental bit trick.
3. Use **unsigned right shift** (`ushr`) to handle the sign bit correctly.
4. This is the building block for many bit manipulation problems.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Number of 1 Bits | [link](https://leetcode.com/problems/number-of-1-bits/) | Easy |
| Counting Bits | [link](https://leetcode.com/problems/counting-bits/) | Easy |
| Reverse Bits | [link](https://leetcode.com/problems/reverse-bits/) | Easy |
| Power of Two | [link](https://leetcode.com/problems/power-of-two/) | Easy |
