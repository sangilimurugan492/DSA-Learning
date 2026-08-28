# Top K Frequent Elements — Detailed Explanation

> **LeetCode #347** | https://leetcode.com/problems/top-k-frequent-elements/  
> **Topic:** Array, Hash Map, Bucket Sort  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 15 most asked)

---

## 📋 Problem Statement

Given an integer array `nums` and an integer `k`, return the `k` most frequent elements.

### Examples

| nums | k | Output |
|------|---|--------|
| `[1,1,1,2,2,3]` | 2 | `[1,2]` |
| `[1]` | 1 | `[1]` |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

For each unique element, scan the entire array to count its frequency. Then sort by frequency and take the top k.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | For each of U unique elements, scan N elements |
| **Space** | O(U) | Store unique elements + frequencies |

---

## 🧩 Method 2: HashMap + Sort — O(N + U log U)

### Core Idea

Build a frequency map in a single pass, then sort entries by frequency descending and take the top k.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N + U log U) | N to build map, U log U to sort (U = unique elements) |
| **Space** | O(N) | Frequency map + result |

---

## 🧩 Method 3: Bucket Sort — O(N)

### Core Idea

Key insight: frequency can be at most N. Create buckets where `bucket[i]` = list of numbers with frequency `i`. Traverse from the highest bucket down to collect k elements.

### Trace for `[1,1,1,2,2,3]`, k=2

```
freq = {1:3, 2:2, 3:1}
buckets: [ [], [3], [2], [1], [], [], [] ]
                    freq=1 freq=2 freq=3
Traverse from end: bucket[3]=[1], bucket[2]=[2] → [1,2] ✅
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | No sorting needed |
| **Space** | O(N) | Frequency map + buckets |

---

## 📊 Comparison Table

| Aspect | Brute Force | HashMap + Sort | Bucket Sort |
|--------|-------------|----------------|-------------|
| **Time** | O(N²) | O(N + U log U) | O(N) |
| **Space** | O(U) | O(N) | O(N) |
| **Interview preference** | ⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Bucket sort by frequency** is the optimal approach — O(N) time, no comparison-based sorting.
2. **Frequency is bounded by N**, so bucket indices range from 0 to N.
3. **HashMap is the foundation** for all approaches — count first, then decide how to extract top k.
4. **Pattern:** Frequency counting → extends to Sort Characters by Frequency, Top K Frequent Words.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Top K Frequent Elements | [#347](https://leetcode.com/problems/top-k-frequent-elements/) | Medium |
| Sort Characters by Frequency | [#451](https://leetcode.com/problems/sort-characters-by-frequency/) | Medium |
| Top K Frequent Words | [#692](https://leetcode.com/problems/top-k-frequent-words/) | Medium |
| Find All Duplicates in an Array | [#442](https://leetcode.com/problems/find-all-duplicates-in-an-array/) | Medium |
