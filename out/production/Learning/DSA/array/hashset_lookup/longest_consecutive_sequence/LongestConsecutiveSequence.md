# Longest Consecutive Sequence — Detailed Explanation

> **LeetCode #128** | [Problem Link](https://leetcode.com/problems/longest-consecutive-sequence/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic HashSet problem)  
> **Topic:** HashSet, Array

---

## 📋 Problem Statement

Given an unsorted array, find the length of the longest consecutive elements sequence. Must run in O(N) time.

### Examples

| Input | Output | Sequence |
|-------|--------|----------|
| `[100,4,200,1,3,2]` | 4 | [1,2,3,4] |
| `[0,3,7,2,5,8,4,6,0,1]` | 9 | [0,1,2,3,4,5,6,7,8] |

---

## 🧩 Method 1: Sort — O(N log N)

### Core Idea

Sort the array, then count consecutive elements. Skip duplicates.

### Code

```kotlin
fun longestConsecutiveSort(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    val sorted = nums.sorted()
    var maxLen = 1
    var currLen = 1

    for (i in 1 until sorted.size) {
        if (sorted[i] == sorted[i - 1]) continue
        else if (sorted[i] == sorted[i - 1] + 1) currLen++
        else currLen = 1
        maxLen = maxOf(maxLen, currLen)
    }
    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sorting dominates |
| **Space** | O(N) | Sorted copy |

---

## 🧩 Method 2: HashSet — O(N)

### Core Idea

Add all numbers to a HashSet. Only start counting from **sequence starts** (where `num-1` is NOT in the set). Count upward while `num+1`, `num+2`, ... are in the set.

### Key Insight

> Only start counting from the **beginning** of a sequence. If `num-1` exists, `num` is not a start — skip it. This ensures each element is visited at most twice → O(N).

### Dry Run — `[100,4,200,1,3,2]`

Set = {1, 2, 3, 4, 100, 200}

| num | num-1 in set? | Action | Streak |
|:---:|:-------------:|:------:|:------:|
| 1 | 0? No | Start counting: 1→2→3→4→5? No | 4 |
| 2 | 1? Yes | Skip | — |
| 3 | 2? Yes | Skip | — |
| 4 | 3? Yes | Skip | — |
| 100 | 99? No | Start counting: 100→101? No | 1 |
| 200 | 199? No | Start counting: 200→201? No | 1 |

✅ **Result: 4** ([1,2,3,4])

### Code

```kotlin
fun longestConsecutiveHashSet(nums: IntArray): Int {
    val set = nums.toSet()
    var maxLen = 0

    for (num in set) {
        if (num - 1 !in set) {  // Only start from sequence begins.
            var currNum = num
            var currLen = 1
            while (currNum + 1 in set) {
                currNum++
                currLen++
            }
            maxLen = maxOf(maxLen, currLen)
        }
    }
    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each element visited at most twice |
| **Space** | O(N) | HashSet |

---

## 📊 Comparison Table

| Aspect | Sort | HashSet |
|--------|------|---------|
| **Time** | O(N log N) | O(N) |
| **Space** | O(N) | O(N) |
| **Approach** | Sort + count | Only count from sequence starts |
| **Key trick** | None | Check `num-1 not in set` |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Sequence start detection:** A number is a sequence start if `num-1` is NOT in the set. Only start counting from starts.
2. **O(N) despite while loop:** Each element is part of at most one counting sequence. Total work across all while loops = O(N).
3. **HashSet for O(1) lookup:** The key to O(N) — checking `num+1 in set` is O(1).
4. **Skip non-starts:** If `num-1` exists, `num` is in the middle of a sequence — it will be counted when we reach the start.
5. **Pattern:** HashSet + "only process starts" is a common optimization for sequence problems.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Consecutive Sequence | [#128](https://leetcode.com/problems/longest-consecutive-sequence/) | Medium |
| Contains Duplicate | [#217](https://leetcode.com/problems/contains-duplicate/) | Easy |
| Contains Duplicate II | [#219](https://leetcode.com/problems/contains-duplicate-ii/) | Easy |
| First Missing Positive | [#41](https://leetcode.com/problems/first-missing-positive/) | Hard |
