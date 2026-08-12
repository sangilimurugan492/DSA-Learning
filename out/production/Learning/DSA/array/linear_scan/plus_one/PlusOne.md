# Plus One — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/plus-one/  
> **Topic:** Array, Math  
> **Difficulty:** Easy

---

## 📋 Problem Statement

You are given a large integer represented as an integer array `digits`, where each
`digits[i]` is the `i`th digit of the integer. The digits are ordered from most significant
to least significant in left-to-right order. The large integer does not contain any leading
0's.

Increment the large integer by one and return the resulting array of digits.

### Constraints

- `1 <= digits.length <= 100`
- `0 <= digits[i] <= 9`
- `digits` does not contain any leading 0's.

### Examples

**Example 1:**

```
Input:  digits = [1, 2, 3]
Output: [1, 2, 4]

Explanation: 123 + 1 = 124
```

**Example 2:**

```
Input:  digits = [4, 3, 2, 1]
Output: [4, 3, 2, 2]

Explanation: 4321 + 1 = 4322
```

**Example 3:**

```
Input:  digits = [9]
Output: [1, 0]

Explanation: 9 + 1 = 10
```

---

## 🧩 Method 1: String Conversion

### Core Idea

Convert the array to a string, parse it as a BigInteger, add 1, then convert back to an
integer array. This works for arbitrarily large inputs but uses extra space.

### Algorithm Steps

1. Join the digits into a string.
2. Parse the string as a BigInteger.
3. Add 1 to the BigInteger.
4. Convert the result back to a string and map each character to an integer.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — conversion + addition |
| **Space** | O(N) — string + result array |

---

## 🧩 Method 2: Digit-by-Digit with Carry (Optimal)

### Core Idea

Simulate manual addition: start from the least significant digit (rightmost), add 1 (the
initial carry), and propagate the carry leftward. If the carry becomes 0, we can return
early. If a carry remains after processing all digits (all 9s case), create a new array
with the carry prepended.

### Algorithm Steps

1. Initialize `carry = 1` (we're adding 1).
2. Traverse from the last digit to the first:
   - `sum = digits[i] + carry`
   - `digits[i] = sum % 10`
   - `carry = sum / 10`
   - If `carry == 0`, return immediately (no further digits change).
3. If carry remains after the loop, create a new array of size `N+1` with `carry` at index 0
   (rest are 0s).

### Step-by-step Walkthrough (Example 1: [1, 2, 3])

```
carry = 1

i=2: sum = 3 + 1 = 4 → digits[2] = 4, carry = 0 → early return!

Result = [1, 2, 4] ✅
```

### Step-by-step Walkthrough ([3, 9, 9, 9])

```
carry = 1

i=3: sum = 9 + 1 = 10 → digits[3] = 0, carry = 1
i=2: sum = 9 + 1 = 10 → digits[2] = 0, carry = 1
i=1: sum = 9 + 1 = 10 → digits[1] = 0, carry = 1
i=0: sum = 3 + 1 = 4  → digits[0] = 4, carry = 0 → early return!

Result = [4, 0, 0, 0] ✅
```

### Step-by-step Walkthrough ([9, 9, 9] — All 9s Edge Case)

```
carry = 1

i=2: sum = 9 + 1 = 10 → digits[2] = 0, carry = 1
i=1: sum = 9 + 1 = 10 → digits[1] = 0, carry = 1
i=0: sum = 9 + 1 = 10 → digits[0] = 0, carry = 1

carry remains = 1 → create new array [1, 0, 0, 0]

Result = [1, 0, 0, 0] ✅
```

### Key Edge Cases

| Input | Output | Notes |
|-------|--------|-------|
| `[1, 2, 3]` | `[1, 2, 4]` | No carry propagation needed |
| `[3, 9, 9, 9]` | `[4, 0, 0, 0]` | Carry propagates but stops early |
| `[9, 9, 9]` | `[1, 0, 0, 0]` | All 9s — new array needed |
| `[9]` | `[1, 0]` | Single digit 9 — new array needed |
| `[0]` | `[1]` | Single digit 0 → 1 |

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — traverse once from right to left |
| **Space** | O(N) worst case (all 9s → new array of size N+1), O(1) best case (in-place) |

### Comparison of Methods

| Aspect | String Conversion | Digit-by-Digit |
|--------|-------------------|----------------|
| **Time** | O(N) | O(N) |
| **Space** | O(N) | O(N) worst, O(1) best |
| **Handles overflow?** | Yes (BigInteger) | Yes (in-place carry) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Carry propagation** is the core technique — just like manual addition from right to left.
2. **Early return** when carry becomes 0 — no need to process remaining digits.
3. **All 9s edge case** requires a new array with one extra leading digit `[1, 0, ..., 0]`.
4. `IntArray(N)` initializes all elements to 0, so we only need to set `result[0] = carry`.
5. This pattern extends to Add Binary, Add Strings, and Multiply Strings.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Plus One | [link](https://leetcode.com/problems/plus-one/) | Easy |
| Add Binary | [link](https://leetcode.com/problems/add-binary/) | Easy |
| Add Strings | [link](https://leetcode.com/problems/add-strings/) | Easy |
| Multiply Strings | [link](https://leetcode.com/problems/multiply-strings/) | Medium |
