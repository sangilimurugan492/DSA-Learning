# Merge Intervals — Detailed Explanation

> **LeetCode #56** | [Problem Link](https://leetcode.com/problems/merge-intervals/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 5 most asked)  
> **Topic:** Greedy, Intervals, Sorting

---

## 📋 Problem Statement

Given an array of intervals, merge all overlapping intervals.

### Example

`[[1,3],[2,6],[8,10],[15,18]]` → `[[1,6],[8,10],[15,18]]`

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Compare every pair of intervals and merge until no more merges possible.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Repeated merging passes |
| **Space** | O(N) | Result |

---

## 🧩 Method 2: Sort + Single Pass — O(N log N)

### Core Idea

Sort by start time. If current start ≤ previous end → merge. Otherwise, start new interval.

### Key Insight

> After sorting by start, overlapping intervals are adjacent. A single pass suffices — if `intervals[i][0] <= last[1]`, they overlap.

### Dry Run — `[[1,3],[2,6],[8,10],[15,18]]`

| i | interval | last | Overlap? | Action | Result |
|:-:|:--------:|:----:|:--------:|:------:|:------:|
| 0 | [1,3] | — | — | add | [[1,3]] |
| 1 | [2,6] | [1,3] | 2≤3 ✅ | merge → [1,6] | [[1,6]] |
| 2 | [8,10] | [1,6] | 8≤6 ❌ | add | [[1,6],[8,10]] |
| 3 | [15,18] | [8,10] | 15≤10 ❌ | add | [[1,6],[8,10],[15,18]] |

✅ **Result: [[1,6],[8,10],[15,18]]**

### Code

```kotlin
fun mergeOptimal(intervals: Array<IntArray>): Array<IntArray> {
    if (intervals.isEmpty()) return intervals
    intervals.sortBy { it[0] }
    val result = mutableListOf(intervals[0])
    for (i in 1 until intervals.size) {
        val last = result.last()
        if (intervals[i][0] <= last[1]) last[1] = maxOf(last[1], intervals[i][1])
        else result.add(intervals[i])
    }
    return result.toTypedArray()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sort + one pass |
| **Space** | O(N) | Result |

---

## 📊 Comparison Table

| Aspect | Brute Force | Sort + Single Pass |
|--------|-------------|---------------------|
| **Time** | O(N²) | O(N log N) |
| **Space** | O(N) | O(N) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Sort by start:** After sorting, overlapping intervals are adjacent.
2. **Merge condition:** `intervals[i][0] <= last[1]` → overlap → merge.
3. **Merge operation:** `last[1] = max(last[1], intervals[i][1])`.
4. **Pattern:** Interval merging — extends to Insert Interval, Meeting Rooms, Non-overlapping Intervals.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
| Insert Interval | [#57](https://leetcode.com/problems/insert-interval/) | Medium |
| Non-overlapping Intervals | [#435](https://leetcode.com/problems/non-overlapping-intervals/) | Medium |
| Meeting Rooms II | [#253](https://leetcode.com/problems/meeting-rooms-ii/) | Medium |
