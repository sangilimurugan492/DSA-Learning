# Meeting Rooms I & II — Detailed Explanation

> **LeetCode #252, #253** | [Meeting Rooms I](https://leetcode.com/problems/meeting-rooms/) | [Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic interval problem — sweep line pattern)  
> **Topic:** Intervals, Heap, Sweep Line

---

## 📋 Problem Statement

- **Meeting Rooms I:** Determine if a person could attend all meetings (no overlaps).
- **Meeting Rooms II:** Find the minimum number of conference rooms required.

### Examples

| intervals | I (can attend?) | II (min rooms) |
|-----------|:---------------:|:--------------:|
| `[[0,30],[5,10],[15,20]]` | false | 2 |
| `[[7,10],[2,4]]` | true | 1 |

---

## 🧩 Meeting Rooms I — O(N log N)

### Core Idea

Sort by start time. If any `start < previous end` → overlap → false.

### Code

```kotlin
fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
    intervals.sortBy { it[0] }
    for (i in 1 until intervals.size) {
        if (intervals[i][0] < intervals[i - 1][1]) return false
    }
    return true
}
```

---

## 🧩 Meeting Rooms II — Method 1: Min-Heap — O(N log N)

### Core Idea

Sort by start. Min-heap tracks end times of ongoing meetings. If earliest ending meeting is done, reuse that room.

### Dry Run — `[[0,30],[5,10],[15,20]]`

| Meeting | heap.peek() | Action | Heap | Rooms |
|:-------:|:-----------:|:------:|:----:|:-----:|
| [0,30] | — | push 30 | [30] | 1 |
| [5,10] | 30 > 5 | can't reuse → push 10 | [10,30] | 2 |
| [15,20] | 10 ≤ 15 | reuse! pop 10, push 20 | [20,30] | 2 |

✅ **Result: 2**

---

## 🧩 Meeting Rooms II — Method 2: Sweep Line — O(N log N)

### Core Idea

Create events: +1 for start, -1 for end. Sort by time (end before start at same time). Sweep through, track max count.

### Dry Run — `[[0,30],[5,10],[15,20]]`

| Time | Event | Count | Max |
|:----:|:-----:|:-----:|:---:|
| 0 | +1 | 1 | 1 |
| 5 | +1 | 2 | 2 |
| 10 | -1 | 1 | 2 |
| 15 | +1 | 2 | 2 |
| 20 | -1 | 1 | 2 |
| 30 | -1 | 0 | 2 |

✅ **Result: 2**

---

## 📊 Comparison Table

| Aspect | Min-Heap | Sweep Line |
|--------|----------|------------|
| **Time** | O(N log N) | O(N log N) |
| **Space** | O(N) | O(N) |
| **Pattern** | Heap reuse | Event counting |
| **Generalizable?** | Specific | Very general |

---

## 🔑 Key Takeaways

1. **Sweep line pattern:** Most general for interval problems. +1 for start, -1 for end.
2. **End before start:** At same time, process end first (meeting ends before next starts).
3. **Min-heap reuse:** If a room is free (end ≤ start), reuse it instead of opening a new room.
4. **Pattern:** Extends to Car Pooling, Employee Free Time, My Calendar.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Meeting Rooms I | [#252](https://leetcode.com/problems/meeting-rooms/) | Easy |
| Meeting Rooms II | [#253](https://leetcode.com/problems/meeting-rooms-ii/) | Medium |
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
| Car Pooling | [#1094](https://leetcode.com/problems/car-pooling/) | Medium |
