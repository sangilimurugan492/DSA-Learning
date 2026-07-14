# Divide Intervals Into Minimum Number of Groups — Detailed Explanation

> **LeetCode #2406** | [Problem Link](https://leetcode.com/problems/divide-intervals-into-minimum-number-of-groups/)  
> **FAANG Importance:** ⭐⭐⭐⭐  
> **Topic:** Intervals, Sweep Line, Heap

---

## 📋 Problem Statement

Given intervals, divide them into minimum number of groups such that no two intervals in the same group overlap.

### Example

`[[5,10],[6,8],[1,5],[2,3],[1,10]]` → 3

---

## 🧩 Method 1: Sweep Line (Counting) — O(N + maxTime)

### Core Idea

Count starts and ends at each time point. Sweep through, track running overlap. Max overlap = min groups.

### Key Insight

> This is equivalent to finding the maximum number of overlapping intervals at any point in time. Same as Meeting Rooms II.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N + maxTime) | Counting + sweep |
| **Space** | O(maxTime) | Counting arrays |

---

## 🧩 Method 2: Min-Heap — O(N log N)

### Core Idea

Sort by start. Min-heap tracks end times. If earliest ended < current start → reuse that group. Heap size = min groups.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sort + heap ops |
| **Space** | O(N) | Heap |

---

## 📊 Comparison Table

| Aspect | Sweep Line | Min-Heap |
|--------|-----------|----------|
| **Time** | O(N + maxTime) | O(N log N) |
| **Space** | O(maxTime) | O(N) |
| **Best when** | maxTime is small | maxTime is large |
| **Interview preference** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Same as Meeting Rooms II:** Min groups = max overlap at any time.
2. **Sweep line:** +1 for start, -1 for end. Max running count = answer.
3. **Min-heap reuse:** If a group is free (end < start), reuse it.
4. **Pattern:** Interval grouping — extends to Meeting Rooms II, Car Pooling.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Divide Intervals Into Groups | [#2406](https://leetcode.com/problems/divide-intervals-into-minimum-number-of-groups/) | Medium |
| Meeting Rooms II | [#253](https://leetcode.com/problems/meeting-rooms-ii/) | Medium |
| Car Pooling | [#1094](https://leetcode.com/problems/car-pooling/) | Medium |
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
