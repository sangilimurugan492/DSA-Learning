# Decode Ways — Detailed Explanation

> **LeetCode #91** | [Problem Link](https://leetcode.com/problems/decode-ways/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic DP — conditional Fibonacci)  
> **Topic:** Dynamic Programming, String

---

## 📋 Problem Statement

A message containing letters A-Z can be encoded into numbers: 'A'→1, 'B'→2, ..., 'Z'→26. Given a string of digits, return the number of ways to decode it.

### Examples

| Input | Output | Decodings |
|-------|--------|-----------|
| `"12"` | 2 | AB (1,2) or L (12) |
| `"226"` | 3 | BBF (2,2,6), BZ (2,26), VF (22,6) |
| `"06"` | 0 | Invalid — leading zero |

### Key Formula

> **`dp[i] = dp[i-1]` (if 1-digit valid) `+ dp[i-2]` (if 2-digit valid)**  
> 1-digit valid: `s[i] != '0'`  
> 2-digit valid: `s[i-1..i]` in `10..26`

---

## 🧩 Method 1: Brute Force — Recursion

### Core Idea

At each position, try decoding 1 digit (if '1'-'9') or 2 digits (if 10-26). Sum both possibilities.

### Code

```kotlin
fun decodeWaysBruteForce(s: String): Int = decode(s, 0)

private fun decode(s: String, i: Int): Int {
    if (i == s.length) return 1
    if (s[i] == '0') return 0

    var ways = decode(s, i + 1)  // 1-digit
    if (i + 1 < s.length) {
        val twoDigit = (s[i] - '0') * 10 + (s[i + 1] - '0')
        if (twoDigit in 10..26) ways += decode(s, i + 2)  // 2-digit
    }
    return ways
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(2^N) | Exponential — overlapping subproblems |
| **Space** | O(N) | Recursion stack |

---

## 🧩 Method 2: Space-Optimized DP (O(1) space)

### Core Idea

Same as Climbing Stairs, but with **conditions** on which steps are valid. `dp[i]` depends on `dp[i-1]` and `dp[i-2]`.

### Key Insight

> '0' blocks the 1-digit path (can't decode '0' alone). Two-digit must be 10-26. This is **conditional Fibonacci**.

### Dry Run — `"226"`

| i | s[i] | 1-digit valid? | 2-digit | 2-digit valid? | curr | prev2 | prev1 |
|:-:|:----:|:--------------:|:-------:|:--------------:|:----:|:-----:|:-----:|
| 1 | 2 | ✅ curr=prev1=1 | 22 | ✅ curr+=prev2=2 | 2 | 1 | 2 |
| 2 | 6 | ✅ curr=prev1=2 | 26 | ✅ curr+=prev2=1 → 3 | 3 | 2 | 3 |

✅ **Result: 3** (BBF, BZ, VF)

### Code

```kotlin
fun decodeWaysOptimal(s: String): Int {
    if (s.isEmpty() || s[0] == '0') return 0
    var prev2 = 1
    var prev1 = 1

    for (i in 1 until s.length) {
        var curr = 0
        if (s[i] != '0') curr = prev1
        val twoDigit = (s[i - 1] - '0') * 10 + (s[i] - '0')
        if (twoDigit in 10..26) curr += prev2
        prev2 = prev1
        prev1 = curr
    }
    return prev1
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Two variables |

---

## 📊 Comparison Table

| Aspect | Brute Force | Space-Optimized DP |
|--------|-------------|---------------------|
| **Time** | O(2^N) | O(N) |
| **Space** | O(N) | O(1) |
| **Approach** | Recursion | Conditional Fibonacci |
| **Key trick** | None | Check '0' and 10-26 conditions |

---

## 🔑 Key Takeaways

1. **Conditional Fibonacci:** Same recurrence as Climbing Stairs, but with validity conditions.
2. **'0' handling:** A '0' can only be decoded as part of a 2-digit number (10, 20). It blocks the 1-digit path.
3. **Two-digit range:** Only 10-26 are valid (J-Z). "07" is invalid, "27" is invalid.
4. **Space optimization:** Since dp[i] depends only on dp[i-1] and dp[i-2], two variables suffice.
5. **Pattern:** Extends to Climbing Stairs, House Robber — all 1D DP with O(1) space.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Decode Ways | [#91](https://leetcode.com/problems/decode-ways/) | Medium |
| Climbing Stairs | [#70](https://leetcode.com/problems/climbing-stairs/) | Easy |
| House Robber | [#198](https://leetcode.com/problems/house-robber/) | Medium |
| Decode Ways II | [#639](https://leetcode.com/problems/decode-ways-ii/) | Hard |
