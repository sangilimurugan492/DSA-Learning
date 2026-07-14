# Add Binary — Detailed Explanation

> **LeetCode #67** | [Problem Link](https://leetcode.com/problems/add-binary/)  
> **FAANG Importance:** ⭐⭐⭐⭐  
> **Topic:** String, Math, Simulation

---

## 📋 Problem Statement

Given two binary strings a and b, return their sum as a binary string.

### Examples

| a | b | Output | Explanation |
|---|---|--------|-------------|
| `"11"` | `"1"` | `"100"` | 3 + 1 = 4 |
| `"1010"` | `"1011"` | `"10101"` | 10 + 11 = 21 |

---

## 🧩 Method 1: Built-in Conversion — O(N)

### Core Idea

Parse both strings to BigInteger (base 2), add, convert back to binary string.

### Code

```kotlin
fun addBinaryBuiltin(a: String, b: String): String {
    val numA = a.toBigInteger(2)
    val numB = b.toBigInteger(2)
    return (numA + numB).toString(2)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Conversion + add |
| **Space** | O(N) | Result string |

---

## 🧩 Method 2: Digit-by-Digit with Carry — O(N)

### Core Idea

Traverse both strings right to left. Add bits + carry. `sum % 2` = result bit, `sum / 2` = carry. Reverse at end.

### Key Insight

> Binary addition: 0+0=0, 0+1=1, 1+1=10 (carry 1), 1+1+1=11 (carry 1). `sum % 2` gives the result bit, `sum / 2` gives the carry.

### Dry Run — `a = "11", b = "1"`

| i | j | a[i] | b[j] | carry | sum | bit | carry (out) | result |
|:-:|:-:|:----:|:----:|:-----:|:---:|:---:|:-----------:|:------:|
| 1 | 0 | 1 | 1 | 0 | 2 | 0 | 1 | "0" |
| 0 | — | 1 | — | 1 | 2 | 0 | 1 | "00" |
| — | — | — | — | 1 | 1 | 1 | 0 | "001" |

Reverse: `"100"` ✅

### Code

```kotlin
fun addBinary(a: String, b: String): String {
    val result = StringBuilder()
    var i = a.length - 1
    var j = b.length - 1
    var carry = 0

    while (i >= 0 || j >= 0 || carry == 1) {
        var sum = carry
        if (i >= 0) sum += a[i--] - '0'
        if (j >= 0) sum += b[j--] - '0'
        result.append(sum % 2)
        carry = sum / 2
    }
    return result.reverse().toString()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(max(N, M)) | Traverse the longer string |
| **Space** | O(max(N, M)) | Result string |

---

## 📊 Comparison Table

| Aspect | Built-in Conversion | Digit-by-Digit |
|--------|---------------------|----------------|
| **Time** | O(N) | O(N) |
| **Space** | O(N) | O(N) |
| **Interview accepted?** | Sometimes rejected | Always accepted |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Right to left:** Like manual addition — start from the least significant bit.
2. **Carry propagation:** `sum = bit_a + bit_b + carry`, `result = sum % 2`, `carry = sum / 2`.
3. **Reverse at end:** We build the result backwards (least significant first).
4. **Loop condition:** `i >= 0 || j >= 0 || carry == 1` — handles remaining carry.
5. **Pattern:** Digit manipulation with carry — extends to Plus One, Add Strings, Multiply Strings.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Add Binary | [#67](https://leetcode.com/problems/add-binary/) | Easy |
| Plus One | [#66](https://leetcode.com/problems/plus-one/) | Easy |
| Add Strings | [#415](https://leetcode.com/problems/add-strings/) | Easy |
| Multiply Strings | [#43](https://leetcode.com/problems/multiply-strings/) | Medium |
