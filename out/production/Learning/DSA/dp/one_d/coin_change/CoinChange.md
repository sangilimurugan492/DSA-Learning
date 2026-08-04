# Coin Change — Detailed Explanation

> **LeetCode #322** | [Problem Link](https://leetcode.com/problems/coin-change/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic unbounded knapsack / minimization DP)  
> **Topic:** Dynamic Programming, Unbounded Knapsack

---

## 📋 Problem Statement

You are given coins of different denominations and a total `amount`. Return the **fewest number of coins** needed to make up that amount. If impossible, return `-1`. You may use each coin **unlimited times**.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `coins=[1,2,5], amount=11` | `3` | 5 + 5 + 1 = 11 (3 coins) |
| `coins=[2], amount=3` | `-1` | Can't make 3 with only coin 2 |
| `coins=[1], amount=0` | `0` | 0 coins for amount 0 |

### Key Formula

> **`dp[a] = min(dp[a - coin] + 1)`** for each coin where `coin ≤ a`  
> **Base case:** `dp[0] = 0` (0 coins for amount 0)

---

## 🧩 Method 1: Brute Force — Recursion

### Core Idea

At each amount, try every coin. `f(remaining) = min(1 + f(remaining - coin))` for each valid coin.

### Problem

Massive overlapping subproblems → exponential time. `f(3)` is computed many times.

### Code

```kotlin
fun coinChangeBruteForce(coins: IntArray, amount: Int): Int {
    if (amount == 0) return 0
    val result = coinHelper(coins, amount)
    return if (result == Int.MAX_VALUE) -1 else result
}

private fun coinHelper(coins: IntArray, remaining: Int): Int {
    if (remaining == 0) return 0
    if (remaining < 0) return Int.MAX_VALUE
    var minCoins = Int.MAX_VALUE
    for (coin in coins) {
        val sub = coinHelper(coins, remaining - coin)
        if (sub != Int.MAX_VALUE) minCoins = minOf(minCoins, 1 + sub)
    }
    return minCoins
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(S^N) | Exponential — overlapping subproblems |
| **Space** | O(S) | Recursion depth |

---

## 🧩 Method 2: Bottom-Up DP (Tabulation) — Optimal

### Core Idea

Build `dp[0..amount]` from smallest to largest. For each amount `a`, try every coin: `dp[a] = min(dp[a], 1 + dp[a - coin])`.

### Key Insight

> "To make amount A, what was the LAST coin used?"  
> If last coin was `c`, then before it, we had amount `A - c`.  
> So `dp[A] = 1 + dp[A - c]`. Take the minimum over all coins.

### Algorithm — Step by Step

1. **Initialize** `dp[0..amount]` with `amount + 1` (impossible sentinel). Set `dp[0] = 0`.
2. **For each** `a` from `1` to `amount`:
   - **For each** `coin`:
     - If `coin ≤ a`: `dp[a] = min(dp[a], 1 + dp[a - coin])`.
3. **Return** `dp[amount]` if ≤ `amount`, else `-1`.

### Dry Run — `coins=[1,2,5], amount=11`

| `a` | coin=1 | coin=2 | coin=5 | `dp[a]` |
|:---:|:---:|:---:|:---:|:---:|
| 0 | — | — | — | **0** |
| 1 | 1+dp[0]=1 | — | — | **1** |
| 2 | 1+dp[1]=2 | 1+dp[0]=1 | — | **1** |
| 3 | 1+dp[2]=2 | 1+dp[1]=2 | — | **2** |
| 4 | 1+dp[3]=3 | 1+dp[2]=2 | — | **2** |
| 5 | 1+dp[4]=3 | 1+dp[3]=3 | 1+dp[0]=1 | **1** |
| 6 | 1+dp[5]=2 | 1+dp[4]=3 | 1+dp[1]=2 | **2** |
| 7 | 1+dp[6]=3 | 1+dp[5]=2 | 1+dp[2]=2 | **2** |
| 8 | 1+dp[7]=3 | 1+dp[6]=3 | 1+dp[3]=3 | **3** |
| 9 | 1+dp[8]=4 | 1+dp[7]=3 | 1+dp[4]=3 | **3** |
| 10 | 1+dp[9]=4 | 1+dp[8]=4 | 1+dp[5]=2 | **2** |
| 11 | 1+dp[10]=3 | 1+dp[9]=4 | 1+dp[6]=3 | **3** |

✅ **Result: `3`** (5 + 5 + 1)

### Code

```kotlin
fun coinChangeTabulation(coins: IntArray, amount: Int): Int {
    if (amount == 0) return 0
    val dp = IntArray(amount + 1) { amount + 1 }
    dp[0] = 0
    for (a in 1..amount) {
        for (coin in coins) {
            if (coin <= a) {
                dp[a] = minOf(dp[a], 1 + dp[a - coin])
            }
        }
    }
    return if (dp[amount] > amount) -1 else dp[amount]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(S × N) | S = amount, N = coins |
| **Space** | O(S) | dp array |

---

## 📊 Comparison Table

| Aspect | Brute Force | Bottom-Up DP |
|--------|-------------|-------------|
| **Time** | O(S^N) | O(S × N) |
| **Space** | O(S) | O(S) |
| **Approach** | Recursion, try every coin | Build from 0 to amount |
| **Optimality** | ❌ Exponential | ✅ Optimal |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Unbounded knapsack:** Each coin can be used unlimited times — this is the key distinction from 0/1 knapsack.
2. **Minimization DP:** Unlike Climbing Stairs (count ways) or House Robber (maximize), Coin Change **minimizes** the count.
3. **"Last coin" thinking:** "What was the last coin used?" reduces the problem to `1 + dp[a - coin]`.
4. **Infinity sentinel:** Use `amount + 1` as "impossible" — it's larger than any valid answer (max coins = amount, using all 1s).
5. **Pattern:** This DP pattern extends to Coin Change II (count ways), Perfect Squares, etc.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Coin Change | [#322](https://leetcode.com/problems/coin-change/) | Medium |
| Coin Change II | [#518](https://leetcode.com/problems/coin-change-ii/) | Medium |
| Perfect Squares | [#279](https://leetcode.com/problems/perfect-squares/) | Medium |
| Climbing Stairs | [#70](https://leetcode.com/problems/climbing-stairs/) | Easy |
