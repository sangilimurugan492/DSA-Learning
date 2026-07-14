# Longest Increasing Subsequence — Detailed Explanation

> **LeetCode #300** | [Problem Link](https://leetcode.com/problems/longest-increasing-subsequence/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic DP + Binary Search)  
> **Topic:** Dynamic Programming, Binary Search

---

## 📋 Problem Statement

Given an integer array, return the length of the longest strictly increasing subsequence.

### Examples

| Input | Output | LIS |
|-------|--------|-----|
| `[10,9,2,5,3,7,101,18]` | 4 | [2,3,7,101] |
| `[0,1,0,3,2,3]` | 4 | [0,1,2,3] |

---

## 🧩 Method 1: DP — O(N²)

### Core Idea

`dp[i]` = length of LIS ending at index `i`. For each `i`, check all `j < i`: if `nums[j] < nums[i]`, `dp[i] = max(dp[i], dp[j] + 1)`.

### Code

```kotlin
fun lengthOfLISDP(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    val dp = IntArray(nums.size) { 1 }
    for (i in nums.indices) {
        for (j in 0 until i) {
            if (nums[j] < nums[i]) {
                dp[i] = maxOf(dp[i], dp[j] + 1)
            }
        }
    }
    return dp.max()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | dp array |

---

## 🧩 Method 2: Binary Search — O(N log N)

### Core Idea

Maintain a `tails` array where `tails[i]` = smallest tail of all LIS of length `i+1`. For each num, binary search its position. If larger than all → extend. Else → replace.

### Key Insight

> We don't build the actual LIS — we maintain the **smallest possible tails**. Smaller tails allow more future elements to extend the sequence. This is "patience sorting."

### Dry Run — `[10,9,2,5,3,7,101,18]`

| num | Action | tails |
|:---:|:------:|:-----:|
| 10 | extend | [10] |
| 9 | replace 10 | [9] |
| 2 | replace 9 | [2] |
| 5 | extend | [2,5] |
| 3 | replace 5 | [2,3] |
| 7 | extend | [2,3,7] |
| 101 | extend | [2,3,7,101] |
| 18 | replace 101 | [2,3,7,18] |

✅ **Result: 4** (tails.size = 4)

### Code

```kotlin
fun lengthOfLISBinarySearch(nums: IntArray): Int {
    val tails = mutableListOf<Int>()
    for (num in nums) {
        var lo = 0
        var hi = tails.size
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (tails[mid] < num) lo = mid + 1
            else hi = mid
        }
        if (lo == tails.size) tails.add(num)
        else tails[lo] = num
    }
    return tails.size
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Binary search per element |
| **Space** | O(N) | tails array |

---

## 📊 Comparison Table

| Aspect | DP O(N²) | Binary Search O(N log N) |
|--------|----------|--------------------------|
| **Time** | O(N²) | O(N log N) |
| **Space** | O(N) | O(N) |
| **Approach** | dp[i] = max LIS ending at i | Patience sorting |
| **Reconstruct LIS?** | ✅ Easy | ❌ Hard |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **DP approach:** `dp[i]` = LIS ending at index i. Check all previous elements. Simple but O(N²).
2. **Binary search approach:** Maintain smallest tails. Replace to keep tails minimal. O(N log N).
3. **tails array is NOT the actual LIS:** It only gives the correct **length**. The actual LIS may differ.
4. **Patience sorting:** The binary search approach is based on the card game "Patience" — place each card on the leftmost pile where it fits.
5. **Pattern:** Extends to Russian Doll Envelopes, Longest Bitonic Subsequence, Minimum Number of Removals.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| LIS | [#300](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium |
| Russian Doll Envelopes | [#354](https://leetcode.com/problems/russian-doll-envelopes/) | Hard |
| Longest Common Subsequence | [#1143](https://leetcode.com/problems/longest-common-subsequence/) | Medium |
| Number of LIS | [#673](https://leetcode.com/problems/number-of-longest-increasing-subsequence/) | Medium |
