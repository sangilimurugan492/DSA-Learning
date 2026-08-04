# Plus One — Detailed Explanation

> **LeetCode #66** | [Problem Link](https://leetcode.com/problems/plus-one/)  
> **FAANG Importance:** ⭐⭐⭐⭐  
> **Topic:** Array, Math

---

## 📋 Problem Statement

Given a large integer represented as an integer array digits, increment by one and return the resulting array.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[1,2,3]` | `[1,2,4]` | 123 + 1 = 124 |
| `[3,9,9,9]` | `[4,0,0,0]` | 3999 + 1 = 4000 |
| `[9]` | `[1,0]` | 9 + 1 = 10 |

---

## 🧩 Method 1: String Conversion — O(N)

### Core Idea

Convert array to string → BigInteger → add 1 → convert back to array.

### Code

```kotlin
fun plusOneString(digits: IntArray): IntArray {
    val num = digits.joinToString("").toBigInteger() + 1.toBigInteger()
    return num.toString().map { it - '0' }.toIntArray()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Conversion + add |
| **Space** | O(N) | String + result |

---

## 🧩 Method 2: Digit-by-Digit with Carry — O(N)

### Core Idea

Traverse from least significant digit. Add carry, propagate. If carry remains after all digits, create new array.

### Key Insight

> Start with carry = 1 (we're adding 1). For each digit: `sum = digit + carry`, `digit = sum % 10`, `carry = sum / 10`. If carry becomes 0, return early — no further digits are affected.

### Dry Run — `[3,9,9,9]`

| i | digit | carry (in) | sum | digit (out) | carry (out) |
|:-:|:-----:|:----------:|:---:|:-----------:|:-----------:|
| 3 | 9 | 1 | 10 | 0 | 1 |
| 2 | 9 | 1 | 10 | 0 | 1 |
| 1 | 9 | 1 | 10 | 0 | 1 |
| 0 | 3 | 1 | 4 | 4 | 0 |

✅ **Result: [4,0,0,0]** (carry = 0, early return)

### Code

```kotlin
fun plusOne(digits: IntArray): IntArray {
    var carry = 1
    for (i in digits.lastIndex downTo 0) {
        val sum = digits[i] + carry
        digits[i] = sum % 10
        carry = sum / 10
        if (carry == 0) return digits
    }
    val result = IntArray(digits.size + 1)
    result[0] = carry
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Traverse once |
| **Space** | O(N) | Worst case: new array for all 9s |

---

## 📊 Comparison Table

| Aspect | String Conversion | Digit-by-Digit |
|--------|-------------------|----------------|
| **Time** | O(N) | O(N) |
| **Space** | O(N) | O(N) |
| **Handles overflow?** | Yes (BigInteger) | Yes (in-place) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Carry propagation:** Like manual addition — add from right to left, propagate carry.
2. **Early return:** If carry becomes 0, no further digits change — return immediately.
3. **All 9s edge case:** [9,9,9] → need a new array with one extra digit [1,0,0,0].
4. **Pattern:** Digit manipulation — extends to Add Binary, Add Strings, Multiply Strings.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Plus One | [#66](https://leetcode.com/problems/plus-one/) | Easy |
| Add Binary | [#67](https://leetcode.com/problems/add-binary/) | Easy |
| Add Strings | [#415](https://leetcode.com/problems/add-strings/) | Easy |
| Multiply Strings | [#43](https://leetcode.com/problems/multiply-strings/) | Medium |
