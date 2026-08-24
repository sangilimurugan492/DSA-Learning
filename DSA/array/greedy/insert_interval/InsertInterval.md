# Insert Interval — Detailed Explanation

> **LeetCode #57** | [Problem Link](https://leetcode.com/problems/insert-interval/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)  
> **Topic:** Greedy, Intervals

---

## 📋 Problem Statement

Given an array of non-overlapping intervals sorted by start time, and a new interval, insert it and merge if necessary.

### Examples

| intervals | newInterval | Output |
|-----------|-------------|--------|
| `[[1,3],[6,9]]` | `[2,5]` | `[[1,5],[6,9]]` |
| `[[1,2],[3,5],[6,7],[8,10],[12,16]]` | `[4,8]` | `[[1,2],[3,10],[12,16]]` |

---

## 🧩 Method 1: Brute Force — O(N log N)

### Core Idea

Add newInterval to the list, sort by start, then single-pass merge overlapping pairs.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sort dominates; merge pass is O(N) |
| **Space** | O(N) | Result |

---

## 🧩 Method 2: Three-Phase — O(N)

### Core Idea

Three phases: (1) Add all intervals before newInterval, (2) Merge all overlapping with newInterval, (3) Add remaining.

### Key Insight

> Since intervals are already sorted, we can process in a single pass. Phase 1 adds non-overlapping intervals before. Phase 2 merges all that overlap. Phase 3 adds the rest.

### Dry Run — `intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]`

| Phase | i | Action | Result |
|:-----:|:-:|:------:|:------:|
| 1 | 0 | [1,2] ends before 4 → add | [[1,2]] |
| 1 | 1 | [3,5] starts ≤ 8 → merge | — |
| 2 | 1 | newInterval = [min(4,3), max(8,5)] = [3,8] | — |
| 2 | 2 | [6,7] starts ≤ 8 → merge → [3,8] | — |
| 2 | 3 | [8,10] starts ≤ 8 → merge → [3,10] | — |
| 2 | 4 | [12,16] starts > 8 → stop | add [3,10] |
| 3 | 4 | add [12,16] | [[1,2],[3,10],[12,16]] |

✅ **Result: [[1,2],[3,10],[12,16]]**

### Code

```kotlin
fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
    val result = mutableListOf<IntArray>()
    var i = 0
    val n = intervals.size

    while (i < n && intervals[i][1] < newInterval[0]) { result.add(intervals[i]); i++ }
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = minOf(newInterval[0], intervals[i][0])
        newInterval[1] = maxOf(newInterval[1], intervals[i][1])
        i++
    }
    result.add(newInterval)
    while (i < n) { result.add(intervals[i]); i++ }
    return result.toTypedArray()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(N) | Result |

---

## 📊 Comparison Table

| Aspect | Brute Force | Three-Phase |
|--------|-------------|-------------|
| **Time** | O(N log N) | O(N) |
| **Space** | O(N) | O(N) |
| **Uses sorted input?** | No (re-sorts) | Yes |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Three phases:** Before (no overlap), merge (overlap), after (no overlap).
2. **Overlap condition:** `intervals[i][0] <= newInterval[1]` means overlap.
3. **Merge:** `newInterval = [min(starts), max(ends)]`.
4. **Pattern:** Interval insertion — extends to Merge Intervals, Non-overlapping Intervals.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Insert Interval | [#57](https://leetcode.com/problems/insert-interval/) | Medium |
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
| Non-overlapping Intervals | [#435](https://leetcode.com/problems/non-overlapping-intervals/) | Medium |
| Meeting Rooms | [#252](https://leetcode.com/problems/meeting-rooms/) | Easy |
