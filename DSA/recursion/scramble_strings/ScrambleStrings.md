# Scramble Strings — Detailed Explanation

> **LeetCode #87** | [Problem Link](https://leetcode.com/problems/scramble-string/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Hard recursion + memoization — must know)
> **Topic:** Recursion, Memoization, String, Divide & Conquer

---

## 📋 Problem Statement

We can scramble a string `s` using the following algorithm:
1. If the string length is 1, stop.
2. If the string length > 1, split into two non-empty substrings at a random index.
3. Randomly decide to swap the two substrings or keep them in order.
4. Recursively apply the algorithm to both substrings.

Given two strings `s1` and `s2` of the same length, return `true` if `s2` is a scrambled string of `s1`.

### Examples

```
Input: s1 = "great", s2 = "rgeat"  → Output: true
  great → split "gr" | "eat", swap → "eat" | "gr"
  "eat" → no swap → "eat"
  "gr" → swap → "rg"
  Result: "rgeat" ✅

Input: s1 = "abcde", s2 = "caebd"  → Output: false
Input: s1 = "a", s2 = "a"          → Output: true
```

---

## 🧩 Method 1: Recursive + Memoization — O(N⁴)

### Core Idea

For each possible split point `i` (1 to n-1), check two cases:
1. **No swap**: `s1[0..i]` scrambles to `s2[0..i]` AND `s1[i..n]` scrambles to `s2[i..n]`
2. **Swap**: `s1[0..i]` scrambles to `s2[n-i..n]` AND `s1[i..n]` scrambles to `s2[0..n-i]`

If either case is true for any split point, `s2` is a scramble of `s1`.

### Key Insight

> At each split, we either keep the order (no swap) or reverse the order (swap). The swap case maps `s1`'s left part to `s2`'s right part and vice versa. Memoization on `(s1, s2)` pairs avoids recomputation. Character frequency pruning eliminates impossible branches early.

### Dry Run — `s1 = "great", s2 = "rgeat"`

```
scramble("great", "rgeat"):
  sameChars? g,r,e,a,t vs r,g,e,a,t → same ✅
  n=5, try i=1..4:

  i=2: s1[0..2]="gr", s1[2..5]="eat"
    Case 1 (no swap): scramble("gr", "rg") && scramble("eat", "eat")
      scramble("gr", "rg"):
        sameChars? g,r vs r,g → same ✅
        i=1: Case 2 (swap): scramble("g", "g") && scramble("r", "r") → true ✅
      scramble("eat", "eat"): s1==s2 → true ✅
      → true ✅

  Result: true ✅
```

### Code

```kotlin
fun isScramble(s1: String, s2: String): Boolean {
    val memo = HashMap<String, Boolean>()
    return scramble(s1, s2, memo)
}

private fun scramble(s1: String, s2: String, memo: HashMap<String, Boolean>): Boolean {
    val key = "$s1,$s2"
    if (key in memo) return memo[key]!!

    // Base cases
    if (s1 == s2) { memo[key] = true; return true }
    if (s1.length != s2.length) { memo[key] = false; return false }
    if (s1.length == 1) { memo[key] = (s1 == s2); return s1 == s2 }

    // Pruning: character frequency must match
    if (!sameChars(s1, s2)) { memo[key] = false; return false }

    val n = s1.length
    for (i in 1 until n) {
        // Case 1: No swap
        val noSwap = scramble(s1.substring(0, i), s2.substring(0, i), memo) &&
                     scramble(s1.substring(i), s2.substring(i), memo)
        if (noSwap) { memo[key] = true; return true }

        // Case 2: Swap
        val swap = scramble(s1.substring(0, i), s2.substring(n - i), memo) &&
                   scramble(s1.substring(i), s2.substring(0, n - i), memo)
        if (swap) { memo[key] = true; return true }
    }

    memo[key] = false
    return false
}

private fun sameChars(s1: String, s2: String): Boolean {
    if (s1.length != s2.length) return false
    val count = IntArray(26)
    for (c in s1) count[c - 'a']++
    for (c in s2) count[c - 'a']--
    return count.all { it == 0 }
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N⁴) | O(N²) states × O(N) split × O(N) substring |
| **Space** | O(N³) | Memoization table |

---

## 🧩 Method 2: 3D DP — O(N⁴)

### Core Idea

Use `dp[i][j][len]` = true if `s1[i..i+len]` is a scramble of `s2[j..j+len]`. Build from length 1 up to N.

### Code

```kotlin
fun isScrambleDP(s1: String, s2: String): Boolean {
    val n = s1.length
    if (n != s2.length) return false
    if (s1 == s2) return true

    val dp = Array(n) { Array(n) { BooleanArray(n + 1) } }

    // Length 1: single characters
    for (i in 0 until n) {
        for (j in 0 until n) {
            dp[i][j][1] = s1[i] == s2[j]
        }
    }

    // Length 2 to n
    for (len in 2..n) {
        for (i in 0..n - len) {
            for (j in 0..n - len) {
                for (k in 1 until len) {
                    // No swap: s1[i..i+k] ↔ s2[j..j+k], s1[i+k..i+len] ↔ s2[j+k..j+len]
                    if (dp[i][j][k] && dp[i + k][j + k][len - k]) {
                        dp[i][j][len] = true; break
                    }
                    // Swap: s1[i..i+k] ↔ s2[j+len-k..j+len], s1[i+k..i+len] ↔ s2[j..j+len-k]
                    if (dp[i][j + len - k][k] && dp[i + k][j][len - k]) {
                        dp[i][j][len] = true; break
                    }
                }
            }
        }
    }

    return dp[0][0][n]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N⁴) | 3 nested loops × split loop |
| **Space** | O(N³) | 3D DP table |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Recursion + Memo | O(N⁴) | O(N³) | Easier to code, top-down |
| 3D DP | O(N⁴) | O(N³) | Bottom-up, no stack overflow |

---

## 🔑 Key Concepts

| Concept | Description |
|---------|-------------|
| **No swap case** | `s1[0..i]` ↔ `s2[0..i]` AND `s1[i..n]` ↔ `s2[i..n]` |
| **Swap case** | `s1[0..i]` ↔ `s2[n-i..n]` AND `s1[i..n]` ↔ `s2[0..n-i]` |
| **Pruning** | Character frequency must match — eliminates impossible branches |
| **Memoization** | Key = `"$s1,$s2"` — avoids recomputing same subproblems |
| **Base cases** | Equal strings → true, length 1 → direct comparison |

> **Interview Tip:** This is one of the hardest LeetCode problems. The key insight is the two cases at each split (swap vs no-swap). Always include character frequency pruning — it dramatically reduces the search space. Start with the recursive + memoization approach. If asked for DP, use the 3D table where `dp[i][j][len]` represents whether `s1` starting at `i` and `s2` starting at `j` with length `len` are scrambles. Build from length 1 upward.
