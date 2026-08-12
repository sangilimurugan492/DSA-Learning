# Sum of Two Integers — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/sum-of-two-integers/  
> **Topic:** Bit Manipulation  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given two integers `a` and `b`, return the sum of the two integers **without using** the
operators `+` and `-`.

### Constraints

- `-1000 <= a, b <= 1000`

### Examples

```
a = 1, b = 2 → 3
a = 2, b = 3 → 5
a = -1, b = 1 → 0
a = -14, b = 16 → 2
```

---

## 🧩 Method: Bit Manipulation (XOR + Carry)

### Core Idea

Addition can be decomposed into two parts:
1. **Sum without carry**: XOR (`a ^ b`) — gives the sum bits where there's no carry.
2. **Carry**: AND shifted left (`(a & b) << 1`) — gives the carry bits.

Repeat: set `a = sum_without_carry`, `b = carry`, until `b == 0` (no more carry). Then `a`
is the final sum.

### Step-by-step Walkthrough (a = 2, b = 3)

```
a = 2 (010), b = 3 (011)

Iteration 1:
  carry = (010 & 011) << 1 = 010 << 1 = 100 (4)
  a = 010 ^ 011 = 001 (1)
  b = 100 (4)

Iteration 2:
  carry = (001 & 100) << 1 = 000 << 1 = 000 (0)
  a = 001 ^ 100 = 101 (5)
  b = 000 (0)

b = 0 → stop. Result = 5 ✅
```

### Handling Negatives

Kotlin/Java use two's complement, so XOR and AND work correctly for negative numbers too.
The loop terminates because carries eventually become 0 (within at most 32 iterations for
32-bit integers).

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(1) — at most 32 iterations |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **XOR** gives sum without carry; **AND shifted left** gives carry.
2. Repeat until no carry remains — this is how hardware adders work.
3. Two's complement ensures correctness for negative numbers.
4. This demonstrates how arithmetic can be built from pure bit operations.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Sum of Two Integers | [link](https://leetcode.com/problems/sum-of-two-integers/) | Medium |
| Missing Number | [link](https://leetcode.com/problems/missing-number/) | Easy |
| Single Number | [link](https://leetcode.com/problems/single-number/) | Easy |
