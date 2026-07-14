# Candy — Detailed Explanation

> **LeetCode #135** | [Problem Link](https://leetcode.com/problems/candy/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Amazon, Google, Meta — hard greedy)  
> **Topic:** Greedy, Array

---

## 📋 Problem Statement

There are n children standing in a line. Each child has a rating. Give candies such that:
- Each child gets at least 1 candy.
- Children with a **higher rating** get **more candies** than their neighbors.

Return the minimum number of candies needed.

### Examples

| ratings | Output | Candies | Explanation |
|---------|--------|---------|-------------|
| `[1,0,2]` | 5 | [2,1,2] | 2+1+2=5 |
| `[1,2,2]` | 4 | [1,2,1] | 1+2+1=4 (equal ratings don't need more) |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Give each child 1 candy. Repeatedly scan and fix violations until stable.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | May need N passes |
| **Space** | O(N) | candies array |

---

## 🧩 Method 2: Two-Pass Greedy — O(N)

### Core Idea

- **Pass 1 (Left→Right):** If `ratings[i] > ratings[i-1]`, `candies[i] = candies[i-1] + 1`.
- **Pass 2 (Right→Left):** If `ratings[i] > ratings[i+1]`, `candies[i] = max(candies[i], candies[i+1] + 1)`.

### Key Insight

> One pass can only satisfy **one neighbor constraint**. Left→Right handles "higher than left neighbor". Right→Left handles "higher than right neighbor". Taking `max` ensures both constraints are satisfied.

### Dry Run — `ratings = [1,0,2]`

| Step | candies |
|------|---------|
| Initial | [1,1,1] |
| After L→R | [1,1,2] (2>0 → candies[2]=candies[1]+1=2) |
| After R→L | [2,1,2] (1>0 → candies[0]=max(1,1+1)=2) |

✅ **Result: 2+1+2 = 5**

### Code

```kotlin
fun candy(ratings: IntArray): Int {
    val n = ratings.size
    val candies = IntArray(n) { 1 }

    for (i in 1 until n)
        if (ratings[i] > ratings[i - 1])
            candies[i] = candies[i - 1] + 1

    for (i in n - 2 downTo 0)
        if (ratings[i] > ratings[i + 1])
            candies[i] = maxOf(candies[i], candies[i + 1] + 1)

    return candies.sum()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Two linear passes |
| **Space** | O(N) | candies array |

---

## 📊 Comparison Table

| Aspect | Brute Force | Two-Pass Greedy |
|--------|-------------|-----------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(N) |
| **Approach** | Fix until stable | L→R then R→L |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Two constraints:** "Higher than left neighbor" and "higher than right neighbor" — each pass handles one.
2. **Take max:** In pass 2, `max(candies[i], candies[i+1]+1)` ensures we don't break pass 1's result.
3. **Equal ratings:** Equal ratings don't require more candies — only strictly higher does.
4. **Pattern:** Two-pass greedy — extends to Trapping Rain Water, Product of Array Except Self.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Candy | [#135](https://leetcode.com/problems/candy/) | Hard |
| Trapping Rain Water | [#42](https://leetcode.com/problems/trapping-rain-water/) | Hard |
| Product of Array Except Self | [#238](https://leetcode.com/problems/product-of-array-except-self/) | Medium |
