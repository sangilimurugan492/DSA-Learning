# Reverse Bits — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/reverse-bits/  
> **Topic:** Bit Manipulation  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Reverse the 32 bits of a given unsigned integer.

### Constraints

- The input is a 32-bit unsigned integer (binary string of length 32).

### Examples

```
Input:  n = 43261596 (00000010100101000001111010011100)
Output: 964176192  (00111001011110000010100101000000)

Input:  n = 0
Output: 0
```

---

## 🧩 Method: Bit-by-Bit Reversal

### Core Idea

Process each of the 32 bits from LSB to MSB of the input, placing them from MSB to LSB
in the result:

1. Shift `result` left by 1 (make room for the next bit).
2. Add the LSB of `n` to `result` (`result | (n & 1)`).
3. Shift `n` right by 1 (move to next bit, unsigned).

Repeat 32 times.

### Step-by-step Walkthrough (simplified, n = 43261596)

```
n = ...10011100 (binary, 32 bits)
result = 0

For 32 iterations:
  Each iteration: result = (result << 1) | (n & 1); n = n >>> 1

After all 32 bits processed, result has the reversed bit pattern.

n = 43261596 → result = 964176192 ✅
```

### Why unsigned right shift (ushr)?

In Kotlin/Java, integers are **signed**. A regular right shift (`>>`) would fill with the
sign bit (1 for negatives). Using `ushr` (unsigned shift) fills with 0s, treating the number
as unsigned — which is what the problem requires.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(1) — always 32 iterations |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. Process bits from one end and build the reversed result from the other end.
2. Use **unsigned right shift** (`ushr`) to correctly handle the sign bit.
3. Always iterate exactly 32 times to handle leading zeros.
4. This is a foundational bit manipulation technique.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Reverse Bits | [link](https://leetcode.com/problems/reverse-bits/) | Easy |
| Number of 1 Bits | [link](https://leetcode.com/problems/number-of-1-bits/) | Easy |
| Counting Bits | [link](https://leetcode.com/problems/counting-bits/) | Easy |
