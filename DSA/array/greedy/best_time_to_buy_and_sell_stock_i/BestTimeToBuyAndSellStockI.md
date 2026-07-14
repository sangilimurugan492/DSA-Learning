# Best Time to Buy and Sell Stock I — Detailed Explanation

> **LeetCode #121** | [Problem Link](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic Greedy)  
> **Topic:** Greedy, Array

---

## 📋 Problem Statement

Given `prices[]`, choose one day to buy and a different future day to sell. Return the maximum profit. If no profit possible, return 0.

### Examples

| prices | Output | Explanation |
|--------|--------|-------------|
| `[7,1,5,3,6,4]` | 5 | Buy at 1, sell at 6 |
| `[7,6,4,3,1]` | 0 | No profit possible |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Try every buy/sell pair. Track max profit.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(1) | — |

---

## 🧩 Method 2: Greedy (One Pass) — O(N)

### Core Idea

Track the minimum price seen so far. At each day, compute profit if sold today. Update max profit.

### Key Insight

> The best profit = sell at current price − buy at the **lowest price before it**. We don't need to know which day to buy — just the minimum price so far.

### Dry Run — `prices = [7,1,5,3,6,4]`

| day | price | minPrice | profit | maxProfit |
|:---:|:-----:|:--------:|:------:|:---------:|
| 1 | 1 | 7→1 | 1-7=-6 | 0 |
| 2 | 5 | 1 | 5-1=4 | 4 |
| 3 | 3 | 1 | 3-1=2 | 4 |
| 4 | 6 | 1 | 6-1=5 | 5 |
| 5 | 4 | 1 | 4-1=3 | 5 |

✅ **Result: 5**

### Code

```kotlin
fun maxProfit(prices: IntArray): Int {
    var maxProfit = 0
    var minPrice = prices[0]

    for (i in 1 until prices.size) {
        val profit = prices[i] - minPrice
        if (prices[i] < minPrice) minPrice = prices[i]
        if (profit > maxProfit) maxProfit = profit
    }
    return maxProfit
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Two variables |

---

## 📊 Comparison Table

| Aspect | Brute Force | Greedy |
|--------|-------------|--------|
| **Time** | O(N²) | O(N) |
| **Space** | O(1) | O(1) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Track min price:** The lowest price so far is the best buy day.
2. **Compute profit at each day:** `profit = prices[i] - minPrice`. Update max.
3. **Pattern:** "Track running minimum" — extends to Stock II, Stock III, Maximum Subarray.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Best Time to Buy and Sell Stock | [#121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy |
| Best Time to Buy and Sell Stock II | [#122](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) | Medium |
| Maximum Subarray | [#53](https://leetcode.com/problems/maximum-subarray/) | Medium |
