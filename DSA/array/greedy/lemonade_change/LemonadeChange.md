# Lemonade Change — Detailed Explanation

> **LeetCode #860** | [Problem Link](https://leetcode.com/problems/lemonade-change/)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Asked at Amazon, Google)  
> **Topic:** Greedy, Simulation

---

## 📋 Problem Statement

Each lemonade costs $5. Customers pay with $5, $10, or $20 bills. Return true if you can provide correct change to every customer.

### Examples

| bills | Output | Explanation |
|-------|--------|-------------|
| `[5,5,5,10,20]` | true | $5→keep, $5→keep, $5→keep, $10→give $5, $20→give $10+$5 |
| `[5,5,10,10,20]` | false | $20 needs $15 change, only one $5 left |

---

## 🧩 Method 1: Simulation with HashMap — O(N)

### Core Idea

Track bill counts in a HashMap. For each customer, give change using largest bills first.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Only 3 bill types |

---

## 🧩 Method 2: Greedy with Counters — O(N)

### Core Idea

Track $5 and $10 counts. For $20, prefer $10+$5 (conserve $5s). Fallback: three $5s.

### Key Insight

> $5 bills are more valuable — they're needed for $10 change. So when giving change for $20, prefer $10+$5 over three $5s. This is the greedy choice.

### Dry Run — `[5,5,5,10,20]`

| bill | five | ten | Action | Result |
|:----:|:----:|:---:|:------:|:------:|
| 5 | 1 | 0 | collect | true |
| 5 | 2 | 0 | collect | true |
| 5 | 3 | 0 | collect | true |
| 10 | 2 | 1 | give $5 change | true |
| 20 | 1 | 0 | give $10+$5 | true |

✅ **Result: true**

### Code

```kotlin
fun lemonadeChange(bills: IntArray): Boolean {
    var five = 0; var ten = 0
    for (bill in bills) {
        when (bill) {
            5 -> five++
            10 -> { if (five == 0) return false; five--; ten++ }
            20 -> {
                if (ten > 0 && five > 0) { ten--; five-- }
                else if (five >= 3) five -= 3
                else return false
            }
        }
    }
    return true
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Two counters |

---

## 📊 Comparison Table

| Aspect | HashMap Simulation | Greedy Counters |
|--------|---------------------|-----------------|
| **Time** | O(N) | O(N) |
| **Space** | O(1) | O(1) |
| **Clarity** | More general | More efficient |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Greedy choice:** For $20, prefer $10+$5 over three $5s — conserve $5s.
2. **Why conserve $5s:** $5s are needed for $10 change. Running out of $5s = failure.
3. **Pattern:** Greedy change-making — extends to Coin Change, Gas Station.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Lemonade Change | [#860](https://leetcode.com/problems/lemonade-change/) | Easy |
| Gas Station | [#134](https://leetcode.com/problems/gas-station/) | Medium |
| Coin Change | [#322](https://leetcode.com/problems/coin-change/) | Medium |
