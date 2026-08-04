# Merge Intervals — Detailed Explanation

> **LeetCode #56** | [Problem Link](https://leetcode.com/problems/merge-intervals/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic interval problem)  
> **Topic:** Sorting, Intervals

---

## 📋 Problem Statement

Given an array of intervals where `intervals[i] = [start, end]`, merge all overlapping intervals and return the merged result.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[[1,3],[2,6],[8,10],[15,18]]` | `[[1,6],[8,10],[15,18]]` | [1,3] and [2,6] overlap → [1,6] |
| `[[1,4],[4,5]]` | `[[1,5]]` | [1,4] and [4,5] overlap at 4 → [1,5] |

### Overlap Condition

> Two intervals `[a, b]` and `[c, d]` overlap if `c <= b` (when sorted by start). Merge to `[a, max(b, d)]`.

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Compare all pairs of intervals. If they overlap, merge them. Repeat until no more merges.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Compare all pairs, possibly multiple passes |
| **Space** | O(N) | Result list |

---

## 🧩 Method 2: Sort + Merge (Optimal) — O(N log N)

### Core Idea

Sort by start time. Iterate and merge overlapping intervals. Only compare with the last merged interval.

### Key Insight

> After sorting, overlapping intervals are **adjacent**. Only need to compare each interval with the last merged one.

### Dry Run — `[[1,3],[2,6],[8,10],[15,18]]`

| Interval | Last Merged | Overlap? | Action | Result |
|:---------:|:-----------:|:--------:|:------:|:------:|
| [1,3] | — | — | Add | [[1,3]] |
| [2,6] | [1,3] | 2≤3 ✅ | Merge → [1,6] | [[1,6]] |
| [8,10] | [1,6] | 8≤6 ❌ | Add | [[1,6],[8,10]] |
| [15,18] | [8,10] | 15≤10 ❌ | Add | [[1,6],[8,10],[15,18]] |

✅ **Result: `[[1,6],[8,10],[15,18]]`**

### Code

```kotlin
fun mergeOptimal(intervals: Array<IntArray>): Array<IntArray> {
    if (intervals.isEmpty()) return arrayOf()
    val sorted = intervals.sortedBy { it[0] }
    val result = mutableListOf<IntArray>(sorted[0])

    for (i in 1 until sorted.size) {
        val last = result.last()
        val curr = sorted[i]
        if (curr[0] <= last[1]) {
            last[1] = maxOf(last[1], curr[1])  // Merge.
        } else {
            result.add(curr)  // No overlap.
        }
    }
    return result.toTypedArray()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sorting dominates |
| **Space** | O(N) | Result list |

---

## 📊 Comparison Table

| Aspect | Brute Force | Sort + Merge |
|--------|-------------|-------------|
| **Time** | O(N²) | O(N log N) |
| **Space** | O(N) | O(N) |
| **Approach** | Compare all pairs | Sort, then single pass |
| **Key trick** | None | Sort makes overlaps adjacent |
| **Interview preference** | ⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Sort by start:** Sorting makes overlapping intervals adjacent — only one pass needed.
2. **Compare with last merged:** Only need to check the last interval in the result — not all previous.
3. **Merge condition:** `curr[0] <= last[1]` → overlap. Merge to `[last[0], max(last[1], curr[1])]`.
4. **Pattern:** This is the foundation for all interval problems — Insert Interval, Non-overlapping Intervals, Meeting Rooms.
5. **Interval problems checklist:** Sort → iterate → merge/compare with previous.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
| Insert Interval | [#57](https://leetcode.com/problems/insert-interval/) | Medium |
| Non-overlapping Intervals | [#435](https://leetcode.com/problems/non-overlapping-intervals/) | Medium |
| Meeting Rooms II | [#253](https://leetcode.com/problems/meeting-rooms-ii/) | Medium |
