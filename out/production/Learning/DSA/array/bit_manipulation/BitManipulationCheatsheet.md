# Bit Manipulation — Complete Cheatsheet & Problem Solutions

> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Bit manipulation is a must-know interview topic)
> **Topic:** Bit Manipulation, XOR, Bit Shifting, DP

---

## 📋 Bit Operators Reference

| Operator | Symbol | Description | Example |
|----------|--------|-------------|---------|
| AND | `&` | 1 only if both bits are 1 | `5 & 3 = 1` (101 & 011 = 001) |
| OR | `\|` | 1 if either bit is 1 | `5 \| 3 = 7` (101 \| 011 = 111) |
| XOR | `^` | 1 if bits differ | `5 ^ 3 = 6` (101 ^ 011 = 110) |
| NOT | `~` | Inverts all bits | `~5 = -6` (flip all bits) |
| Left Shift | `<<` | Shift left, fill with 0 | `1 << 3 = 8` (1 → 1000) |
| Right Shift | `>>` | Shift right, sign-extended | `8 >> 2 = 2` (1000 → 10) |
| Unsigned R Shift | `>>>` | Shift right, fill with 0 | `-1 >>> 1 = 2147483647` |

---

## 🔑 Essential Bit Tricks

| Trick | Code | Use Case |
|-------|------|----------|
| Check if bit k is set | `(n shr k) and 1` | Test specific bit |
| Set bit k | `n or (1 shl k)` | Turn on bit |
| Clear bit k | `n and (1 shl k).inv()` | Turn off bit |
| Toggle bit k | `n xor (1 shl k)` | Flip bit |
| Remove lowest set bit | `n and (n - 1)` | Brian Kernighan's |
| Isolate lowest set bit | `n and (-n)` | Get lowest 1-bit |
| Check power of 2 | `n > 0 && (n and (n-1)) == 0` | Single bit check |
| Count set bits | `n.countOneBits()` | Kotlin built-in |

---

## XOR Properties (Critical for Interviews)

```
a ^ a = 0    (self-XOR = 0)
a ^ 0 = a    (XOR with 0 = identity)
a ^ b ^ a = b  (duplicates cancel)
XOR is commutative: a ^ b = b ^ a
XOR is associative: (a ^ b) ^ c = a ^ (b ^ c)
```

---

## Problem 1: Single Number (LeetCode #136)

> Every element appears twice except one. Find it.

```kotlin
fun singleNumber(nums: IntArray): Int {
    var result = 0
    for (num in nums) result = result xor num
    return result
}
```

**Key Insight:** XOR all elements. Duplicates cancel (`a ^ a = 0`), leaving the single number.

| Metric | Value |
|--------|-------|
| Time | O(N) |
| Space | O(1) |

---

## Problem 2: Number of 1 Bits (LeetCode #191)

> Count set bits (Hamming weight).

```kotlin
// Brian Kernighan's Algorithm — O(k) where k = set bits
fun hammingWeight(n: Int): Int {
    var count = 0
    var num = n
    while (num != 0) {
        num = num and (num - 1)  // Removes lowest set bit
        count++
    }
    return count
}
```

**Key Insight:** `n & (n-1)` removes the lowest set bit. Loop runs only k times (k = number of 1 bits), not 32.

| Metric | Value |
|--------|-------|
| Time | O(k) |
| Space | O(1) |

---

## Problem 3: Missing Number (LeetCode #268)

> Array has n distinct numbers from 0 to n. Find the missing one.

```kotlin
// XOR approach — O(N), O(1) space
fun missingNumberXOR(nums: IntArray): Int {
    var result = nums.size
    for (i in nums.indices) {
        result = result xor i xor nums[i]
    }
    return result
}

// Math approach — O(N), O(1) space
fun missingNumberMath(nums: IntArray): Int {
    val n = nums.size
    return n * (n + 1) / 2 - nums.sum()
}
```

**Key Insight:** XOR all indices (0..n) with all array elements. Present numbers cancel, leaving the missing one. Math approach uses Gauss's formula: `sum(0..n) = n*(n+1)/2`.

| Metric | Value |
|--------|-------|
| Time | O(N) |
| Space | O(1) |

---

## Problem 4: Sum of Two Integers (LeetCode #371)

> Add two integers without + or - operators.

```kotlin
fun getSum(a: Int, b: Int): Int {
    var x = a; var y = b
    while (y != 0) {
        val carry = (x and y) shl 1  // Carry bits
        x = x xor y                  // Sum without carry
        y = carry                    // Repeat with carry
    }
    return x
}
```

**Key Insight:** XOR gives sum without carry. AND + left shift gives carry. Repeat until no carry.

| Metric | Value |
|--------|-------|
| Time | O(1) (max 32 iterations) |
| Space | O(1) |

---

## Problem 5: Power of Two (LeetCode #231)

> Check if n is a power of 2.

```kotlin
fun isPowerOfTwo(n: Int): Boolean {
    return n > 0 && (n and (n - 1)) == 0
}
```

**Key Insight:** Powers of 2 have exactly one set bit. `n & (n-1)` removes it → result is 0. Must check `n > 0` (0 is not a power of 2, and the trick gives 0 for n=0).

| Metric | Value |
|--------|-------|
| Time | O(1) |
| Space | O(1) |

---

## Problem 6: Counting Bits (LeetCode #338)

> For each i from 0 to n, count set bits.

```kotlin
fun countBits(n: Int): IntArray {
    val ans = IntArray(n + 1)
    for (i in 1..n) {
        ans[i] = ans[i shr 1] + (i and 1)
    }
    return ans
}
```

**Key Insight:** `ans[i] = ans[i/2] + (i % 2)`. The bit count of `i` equals the bit count of `i/2` (same bits shifted right) plus the LSB of `i`. This is DP — reuse the answer for `i/2`.

| Metric | Value |
|--------|-------|
| Time | O(N) |
| Space | O(N) |

---

## Problem 7: Reverse Bits (LeetCode #190)

> Reverse all 32 bits of an unsigned integer.

```kotlin
fun reverseBits(n: Int): Int {
    var result = 0
    var num = n
    for (i in 0 until 32) {
        result = result shl 1          // Shift left to make room
        result = result or (num and 1)  // Add LSB of n
        num = num ushr 1                // Next bit
    }
    return result
}
```

**Key Insight:** For each of 32 bits, take the LSB of `n`, append it to `result` (shift left + OR), then shift `n` right. Use `ushr` (unsigned shift) to avoid sign extension.

| Metric | Value |
|--------|-------|
| Time | O(1) (32 iterations) |
| Space | O(1) |

---

## 📊 All Problems Summary

| # | Problem | Pattern | Time | Space |
|---|---------|---------|------|-------|
| 1 | Single Number | XOR all | O(N) | O(1) |
| 2 | Number of 1 Bits | `n & (n-1)` | O(k) | O(1) |
| 3 | Missing Number | XOR / Math | O(N) | O(1) |
| 4 | Sum of Two Integers | XOR + AND carry | O(1) | O(1) |
| 5 | Power of Two | `n & (n-1) == 0` | O(1) | O(1) |
| 6 | Counting Bits | DP + `ans[i>>1]` | O(N) | O(N) |
| 7 | Reverse Bits | Shift + OR | O(1) | O(1) |

---

## 🎯 Interview Tips

1. **Always consider XOR** when a problem involves pairs, duplicates, or finding a unique element
2. **`n & (n-1)`** is the most important trick — removes the lowest set bit. Used in: count bits, power of 2, AND of all numbers
3. **Check `n > 0`** for power of 2 — 0 and negative numbers are not powers of 2
4. **Use `ushr`** (unsigned right shift) when dealing with unsigned semantics to avoid sign extension
5. **Kotlin built-ins:** `n.countOneBits()`, `n.countTrailingZeroBits()`, `n.countLeadingZeroBits()` — know these exist but be able to implement manually
6. **DP + bits:** Counting Bits uses `ans[i] = ans[i >> 1] + (i & 1)` — the bit count of `i` is the bit count of `i/2` plus the last bit
7. **Subtraction without `-`:** `a - b = a + (~b + 1)` (two's complement)
