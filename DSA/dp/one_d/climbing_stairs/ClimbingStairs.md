# Climbing Stairs — Detailed Explanation

> **LeetCode #70** | [Problem Link](https://leetcode.com/problems/climbing-stairs/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (THE gateway DP problem — Fibonacci variant)  
> **Topic:** Dynamic Programming, 1D DP

---

## 📋 Problem Statement

You are climbing a staircase. It takes `n` steps to reach the top. Each time you can climb 1 or 2 steps. How many distinct ways can you climb to the top?

### Examples

| n | Output | Ways |
|---|--------|------|
| 2 | 2 | 1+1, 2 |
| 3 | 3 | 1+1+1, 1+2, 2+1 |
| 5 | 8 | Fibonacci sequence |

### Key Formula

> **`ways(n) = ways(n-1) + ways(n-2)`** — This IS Fibonacci!  
> **Base cases:** `ways(0) = 1`, `ways(1) = 1`

### Why?

> "To reach step n, where could I have come from?" → n-1 (1 step) or n-2 (2 steps). Every path MUST end with one of these two moves. So total ways = sum of both.

---

## 🧩 Method 1: Brute Force — Recursion

### Core Idea

`ways(n) = ways(n-1) + ways(n-2)` — recursively compute. But overlapping subproblems cause exponential blowup.

### Recursion Tree for n=5

```
                    f(5)
                  /      \
               f(4)       f(3)
              /    \      /    \
           f(3)   f(2) f(2)   f(1)
          /   \   / \   / \
       f(2) f(1) f(1) f(0) f(1) f(0)
```

`f(3)` computed TWICE, `f(2)` computed THREE times!

### Code

```kotlin
fun climbStairsBruteForce(n: Int): Int {
    if (n <= 1) return 1
    return climbStairsBruteForce(n - 1) + climbStairsBruteForce(n - 2)
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

`dp[i]` only depends on `dp[i-1]` and `dp[i-2]`. Use two variables instead of an array.

### Key Insight

> Same as Fibonacci: `curr = prev1 + prev2`. Only need the last two values — no array needed.

### Algorithm — Step by Step

1. Initialize `prev2 = 1` (ways to step 0), `prev1 = 1` (ways to step 1).
2. For `i` from 2 to n:
   - `curr = prev1 + prev2`
   - `prev2 = prev1`, `prev1 = curr`
3. Return `prev1`.

### Dry Run — n=5

| i | prev2 | prev1 | curr = prev1 + prev2 | New prev2 | New prev1 |
|:-:|:-----:|:-----:|:--------------------:|:---------:|:---------:|
| 2 | 1 | 1 | 2 | 1 | 2 |
| 3 | 1 | 2 | 3 | 2 | 3 |
| 4 | 2 | 3 | 5 | 3 | 5 |
| 5 | 3 | 5 | 8 | 5 | 8 |

✅ **Result: 8**

### Code

```kotlin
fun climbStairsOptimal(n: Int): Int {
    if (n <= 1) return 1
    var prev2 = 1
    var prev1 = 1
    for (i in 2..n) {
        val curr = prev1 + prev2
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
| **Approach** | Recursion | Two variables (Fibonacci) |
| **Optimality** | ❌ Exponential | ✅ Optimal |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Gateway DP problem:** Climbing Stairs is THE first DP problem to learn. The recurrence `ways(n) = ways(n-1) + ways(n-2)` is Fibonacci.
2. **Count ways = SUM:** Unlike House Robber (maximize), Climbing Stairs counts ALL ways → use addition.
3. **Space optimization:** Since `dp[i]` only depends on `dp[i-1]` and `dp[i-2]`, two variables suffice — O(1) space.
4. **"Where could I have come from?"** — this question derives the recurrence for all 1D DP problems.
5. **Pattern:** Extends to Min Cost Climbing Stairs, House Robber, Decode Ways, Fibonacci.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Climbing Stairs | [#70](https://leetcode.com/problems/climbing-stairs/) | Easy |
| Min Cost Climbing Stairs | [#746](https://leetcode.com/problems/min-cost-climbing-stairs/) | Easy |
| House Robber | [#198](https://leetcode.com/problems/house-robber/) | Medium |
| Decode Ways | [#91](https://leetcode.com/problems/decode-ways/) | Medium |
