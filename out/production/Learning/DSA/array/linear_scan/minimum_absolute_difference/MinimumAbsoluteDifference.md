# Minimum Absolute Difference — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/minimum-absolute-difference/  
> **Topic:** Array, Sorting  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array of **distinct** integers `arr`, find all pairs of elements with the **minimum
absolute difference** of any two elements.

Return a list of pairs in **ascending order** (with respect to pairs), where each pair
`[a, b]` follows:

- `a`, `b` are from `arr`
- `a < b`
- `b - a` equals the minimum absolute difference of any two elements in `arr`

### Constraints

- `2 <= arr.length <= 10^5`
- `-10^6 <= arr[i] <= 10^6`
- All integers in `arr` are distinct.

### Examples

**Example 1:**

```
Input:  arr = [4, 2, 1, 3]
Output: [[1, 2], [2, 3], [3, 4]]

Sorted: [1, 2, 3, 4]
Adjacent diffs: |2-1|=1, |3-2|=1, |4-3|=1
Min diff = 1
Pairs with diff 1: [1,2], [2,3], [3,4]
```

**Example 2:**

```
Input:  arr = [1, 3, 6, 10, 15]
Output: [[1, 3]]

Sorted: [1, 3, 6, 10, 15]
Adjacent diffs: 2, 3, 4, 5
Min diff = 2
Pairs with diff 2: [1, 3]
```

**Example 3:**

```
Input:  arr = [3, 8, -10, 4, 11]
Output: [[3, 4]]

Sorted: [-10, 3, 4, 8, 11]
Adjacent diffs: 13, 1, 4, 3
Min diff = 1
Pairs with diff 1: [3, 4]
```

---

## 🧩 Method 1: Brute Force — Check All Pairs

### Core Idea

Examine every possible pair `(i, j)` where `i < j`, compute `|arr[i] - arr[j]|`, and track
the minimum difference. Then collect all pairs whose difference equals this minimum.
Finally, sort the pairs in ascending order.

### Step-by-step Walkthrough (Example 1)

```
arr = [4, 2, 1, 3]

Pass 1 — Find min diff among all pairs:
  (4,2): |4-2|=2    (4,1): |4-1|=3    (4,3): |4-3|=1
  (2,1): |2-1|=1    (2,3): |2-3|=1
  (1,3): |1-3|=2
  minDiff = 1

Pass 2 — Collect pairs with diff == 1:
  (4,3): diff=1 → add [3, 4]
  (2,1): diff=1 → add [1, 2]
  (2,3): diff=1 → add [2, 3]
  (1,3): diff=2 → skip

Pass 3 — Sort pairs:
  [[3,4], [1,2], [2,3]] → sorted → [[1,2], [2,3], [3,4]]

Result = [[1, 2], [2, 3], [3, 4]]
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — examine all N*(N-1)/2 pairs + O(P log P) to sort results |
| **Space** | O(N²) — worst case store all pairs before filtering |

---

## 🧩 Method 2: Optimal — Sort + Single Pass

### Core Idea

The key insight is that **after sorting, the minimum absolute difference between any two
elements must occur between adjacent elements**. This is because in a sorted array, for any
pair `(arr[i], arr[j])` where `i < j`, the difference `arr[j] - arr[i]` is at least as large
as the difference between consecutive elements `arr[i+1] - arr[i]` (since all elements
between them only add to the gap).

This reduces the problem from O(N²) pair comparisons to O(N) adjacent comparisons.

### Algorithm Steps

1. **Sort** the array in ascending order.
2. **Find the minimum difference** among all adjacent pairs `(arr[i-1], arr[i])`.
3. **Collect all adjacent pairs** whose difference equals the minimum.
   - Since the array is sorted, pairs are naturally in ascending order — no extra sort needed.

### Step-by-step Walkthrough (Example 1)

```
arr = [4, 2, 1, 3]
```

**Step 1 — Sort:**

```
sorted = [1, 2, 3, 4]
```

**Step 2 — Find minimum adjacent difference:**

| i | arr[i-1] | arr[i] | arr[i] - arr[i-1] | minDiff |
|---|----------|--------|---------------------|---------|
| 1 | 1        | 2      | 1                   | 1       |
| 2 | 2        | 3      | 1                   | 1       |
| 3 | 3        | 4      | 1                   | 1       |

```
minDiff = 1
```

**Step 3 — Collect pairs with diff == minDiff:**

| i | arr[i-1] | arr[i] | diff == minDiff? | Pair added |
|---|----------|--------|-------------------|------------|
| 1 | 1        | 2      | 1 == 1 ✅          | [1, 2]     |
| 2 | 2        | 3      | 1 == 1 ✅          | [2, 3]     |
| 3 | 3        | 4      | 1 == 1 ✅          | [3, 4]     |

**Result = [[1, 2], [2, 3], [3, 4]]** ✅

### Why does sorting work?

In a sorted array, for any pair `(arr[i], arr[j])` with `i < j`:

```
arr[j] - arr[i] = (arr[j] - arr[j-1]) + (arr[j-1] - arr[j-2]) + ... + (arr[i+1] - arr[i])
```

Each term is non-negative (array is sorted), so `arr[j] - arr[i] >= arr[i+1] - arr[i]`.
This means the minimum difference is always found between **adjacent** elements — we never
need to check non-adjacent pairs.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting; two O(N) passes after |
| **Space** | O(N) — for the result list (sorting is in-place) |

### Why is this better than brute force?

| Aspect | Brute Force (All Pairs) | Optimal (Sort + Adjacent) |
|--------|-------------------------|---------------------------|
| Time | O(N²) | O(N log N) |
| Space | O(N²) | O(N) |
| Key idea | Check every pair | Only check adjacent pairs after sorting |
| Extra sort needed? | Yes (sort results) | No (sorted array gives sorted pairs) |

---

## 🔑 Key Takeaways

1. **Sorting transforms** the problem: the minimum difference is guaranteed to be between
   adjacent elements in a sorted array.
2. This is a classic example of how sorting can reduce O(N²) to O(N log N).
3. Since the array is sorted, collected pairs are **automatically in ascending order** —
   no additional sorting of results is needed.
4. Two passes after sorting: one to find `minDiff`, one to collect pairs. Can also be done
   in a single pass with a growing list that resets when a smaller diff is found.
5. Elements are **distinct**, so the minimum difference is always > 0.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Minimum Absolute Difference | [link](https://leetcode.com/problems/minimum-absolute-difference/) | Easy |
| Minimum Absolute Difference in an Array | [HackerRank](https://www.hackerrank.com/challenges/minimum-absolute-difference-in-an-array) | Easy |
| Find All Pairs With a Given Difference | [GeeksforGeeks](https://www.geeksforgeeks.org/find-all-pairs-with-a-given-difference/) | Medium |
| K-diff Pairs in an Array | [link](https://leetcode.com/problems/k-diff-pairs-in-an-array/) | Medium |
