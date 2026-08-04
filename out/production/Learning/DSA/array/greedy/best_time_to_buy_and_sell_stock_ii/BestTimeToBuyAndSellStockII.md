# Best Time to Buy and Sell Stock II — Detailed Explanation

> **LeetCode #122** | [Problem Link](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Asked at Amazon, Google, Microsoft)  
> **Topic:** Greedy, Array

---

## 📋 Problem Statement

You may buy and sell on different days, but hold at most one share at a time. Find the maximum profit with **unlimited transactions**.

### Examples

| prices | Output | Explanation |
|--------|--------|-------------|
| `[7,1,5,3,6,4]` | 7 | Buy 1, sell 5 (+4); buy 3, sell 6 (+3) |
| `[1,2,3,4,5]` | 4 | Buy 1, sell 5 (+4) |
| `[7,6,4,3,1]` | 0 | No profit |

---

## 🧩 Method 1: Peak-Valley — O(N)

### Core Idea

Find valleys (local minima) to buy, peaks (local maxima) to sell. Profit = sum of (peak - valley) for each cycle.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | — |

---

## 🧩 Method 2: Sum Positive Differences — O(N)

### Core Idea

If `prices[i+1] > prices[i]`, add the difference to profit. This captures every upward slope.

### Key Insight

> A continuous rise from day i to day j = sum of daily rises from i to j. So summing all positive daily differences = total profit from all transactions.

### Dry Run — `prices = [7,1,5,3,6,4]`

| day | prices[i]→prices[i+1] | diff | Action | profit |
|:---:|:---------------------:|:----:|:------:|:------:|
| 0 | 7→1 | -6 | skip | 0 |
| 1 | 1→5 | +4 | add | 4 |
| 2 | 5→3 | -2 | skip | 4 |
| 3 | 3→6 | +3 | add | 7 |
| 4 | 6→4 | -2 | skip | 7 |

✅ **Result: 7**

### Code

```kotlin
fun maxProfit(prices: IntArray): Int {
    var profit = 0
    for (i in 0 until prices.size - 1) {
        if (prices[i + 1] > prices[i])
            profit += prices[i + 1] - prices[i]
    }
    return profit
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | — |

---

## 📊 Comparison Table

| Aspect | Peak-Valley | Sum Positive Diff |
|--------|-------------|-------------------|
| **Time** | O(N) | O(N) |
| **Space** | O(1) | O(1) |
| **Simplicity** | More complex | Simpler |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Capture every upward slope:** If price goes up tomorrow, capture the gain today.
2. **Mathematical equivalence:** Sum of daily rises = total rise over the period.
3. **Unlimited transactions:** Unlike Stock I (one transaction), here we can buy/sell as many times as we want.
4. **Pattern:** Greedy "capture all gains" — extends to Stock III, Stock IV, Stock with Cooldown.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Best Time to Buy and Sell Stock | [#121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy |
| Best Time to Buy and Sell Stock II | [#122](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) | Medium |
| Best Time to Buy and Sell Stock III | [#123](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/) | Hard |
| Best Time to Buy and Sell Stock with Cooldown | [#309](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | Medium |
