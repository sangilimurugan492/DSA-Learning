# Minimum Arrows to Burst Balloons — Detailed Explanation

> **LeetCode #452** | [Problem Link](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Asked at Google, Meta)  
> **Topic:** Greedy, Intervals

---

## 📋 Problem Statement

Balloons are intervals [xstart, xend]. An arrow at x bursts all balloons where xstart ≤ x ≤ xend. Find the minimum number of arrows.

### Examples

| points | Output | Explanation |
|--------|--------|-------------|
| `[[10,16],[2,8],[1,6],[7,12]]` | 2 | Arrow at 6 bursts [2,8],[1,6]. Arrow at 11 bursts [10,16],[7,12] |
| `[[1,2],[3,4],[5,6],[7,8]]` | 4 | No overlaps → 4 arrows |

---

## 🧩 Method 1: Sort by Start — O(N log N)

### Core Idea

Sort by start. Merge overlapping intervals (narrow end to min). Count groups = arrows.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sort |
| **Space** | O(1) | — |

---

## 🧩 Method 2: Sort by End — O(N log N)

### Core Idea

Sort by end. Shoot arrow at end of first balloon. Skip all that start before it. When a balloon starts after → new arrow.

### Key Insight

> Same pattern as Non-overlapping Intervals, but here touching counts as overlap (≤ vs <). Shoot at the earliest end to maximize balloons burst.

### Dry Run — `[[10,16],[2,8],[1,6],[7,12]]`

Sorted by end: `[[1,6],[2,8],[7,12],[10,16]]`

| i | balloon | arrowPos | starts > arrowPos? | Action | arrows |
|:-:|:-------:|:--------:|:------------------:|:------:|:------:|
| 0 | [1,6] | 6 | — | shoot at 6 | 1 |
| 1 | [2,8] | 6 | 2>6? No | bursted | 1 |
| 2 | [7,12] | 6 | 7>6? Yes | new arrow at 12 | 2 |
| 3 | [10,16] | 12 | 10>12? No | bursted | 2 |

✅ **Result: 2**

### Code

```kotlin
fun findMinArrowShots(points: Array<IntArray>): Int {
    if (points.isEmpty()) return 0
    points.sortBy { it[1] }
    var arrows = 1; var arrowPos = points[0][1]
    for (i in 1 until points.size) {
        if (points[i][0] > arrowPos) { arrows++; arrowPos = points[i][1] }
    }
    return arrows
}
```

---

## 📊 Comparison Table

| Aspect | Sort by Start | Sort by End |
|--------|---------------|-------------|
| **Time** | O(N log N) | O(N log N) |
| **Space** | O(1) | O(1) |
| **Simplicity** | Needs min(end) tracking | Cleaner |
| **Interview preference** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Sort by end:** Shoot at earliest end → maximizes balloons burst per arrow.
2. **Touching = overlap:** Unlike Non-overlapping Intervals, `start ≤ arrowPos` means bursted.
3. **Pattern:** Activity selection — extends to Non-overlapping Intervals, Meeting Rooms.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Minimum Arrows | [#452](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/) | Medium |
| Non-overlapping Intervals | [#435](https://leetcode.com/problems/non-overlapping-intervals/) | Medium |
| Meeting Rooms II | [#253](https://leetcode.com/problems/meeting-rooms-ii/) | Medium |
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
