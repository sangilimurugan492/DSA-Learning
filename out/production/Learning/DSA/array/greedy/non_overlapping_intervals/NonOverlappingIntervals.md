# Non-overlapping Intervals — Detailed Explanation

> **LeetCode #435** | [Problem Link](https://leetcode.com/problems/non-overlapping-intervals/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐  
> **Topic:** Greedy, Intervals

---

## 📋 Problem Statement

Given an array of intervals, return the minimum number of intervals to remove to make the rest non-overlapping.

### Examples

| intervals | Output | Explanation |
|-----------|--------|-------------|
| `[[1,2],[2,3],[3,4],[1,3]]` | 1 | Remove [1,3] |
| `[[1,2],[1,2],[1,2]]` | 2 | Remove two [1,2] |
| `[[1,2],[2,3]]` | 0 | Already non-overlapping |

---

## 🧩 Method 1: Brute Force — O(2^N)

Try all subsets, find the largest non-overlapping subset. Exponential — not practical.

---

## 🧩 Method 2: Greedy (Sort by End) — O(N log N)

### Core Idea

Sort by end time. Greedily keep intervals that don't overlap. Count removals.

### Key Insight

> Sorting by end time ensures we keep the interval that ends earliest, leaving maximum room for remaining intervals. This is the classic "activity selection" greedy.

### Dry Run — `[[1,2],[2,3],[3,4],[1,3]]`

Sorted by end: `[[1,2],[2,3],[1,3],[3,4]]`

| i | interval | lastEnd | Overlap? | Action | removals |
|:-:|:--------:|:-------:|:--------:|:------:|:--------:|
| 0 | [1,2] | 2 | — | keep | 0 |
| 1 | [2,3] | 2 | 2<2? No | keep, lastEnd=3 | 0 |
| 2 | [1,3] | 3 | 1<3? Yes | remove | 1 |
| 3 | [3,4] | 3 | 3<3? No | keep, lastEnd=4 | 1 |

✅ **Result: 1**

### Code

```kotlin
fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
    if (intervals.isEmpty()) return 0
    intervals.sortBy { it[1] }
    var removals = 0
    var lastEnd = intervals[0][1]
    for (i in 1 until intervals.size) {
        if (intervals[i][0] < lastEnd) removals++
        else lastEnd = intervals[i][1]
    }
    return removals
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sort |
| **Space** | O(1) | — |

---

## 🔑 Key Takeaways

1. **Sort by end:** Keep earliest-ending intervals → maximum room for others.
2. **Overlap condition:** `start < lastEnd` → overlap (touching at a point is NOT overlap).
3. **Greedy choice:** Remove the interval that ends later (it blocks more).
4. **Pattern:** Activity selection — extends to Minimum Arrows, Meeting Rooms.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Non-overlapping Intervals | [#435](https://leetcode.com/problems/non-overlapping-intervals/) | Medium |
| Minimum Arrows to Burst Balloons | [#452](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/) | Medium |
| Meeting Rooms | [#252](https://leetcode.com/problems/meeting-rooms/) | Easy |
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
