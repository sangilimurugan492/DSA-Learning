# Rank Transform of an Array — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/rank-transform-of-an-array/  
> **Topic:** Array, Sorting, HashMap  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array of integers `arr`, replace each element with its **rank**.

The rank represents how large the element is. The rank has the following rules:

- Rank is an integer **starting from 1**.
- The **larger** the element, the **larger** the rank.
- If two elements are **equal**, their rank must be the **same**.
- Rank should be **as small as possible** (no gaps — ranks are contiguous 1, 2, 3, ...).

### Constraints

- `0 <= arr.length <= 10^5`
- `-10^9 <= arr[i] <= 10^9`

### Examples

**Example 1:**

```
Input:  arr = [40, 10, 20, 30]
Output: [4, 1, 2, 3]

Sorted unique: [10, 20, 30, 40]
Ranks:           1    2    3    4
  40 → rank 4 (largest)
  10 → rank 1 (smallest)
  20 → rank 2
  30 → rank 3
```

**Example 2:**

```
Input:  arr = [100, 100, 100]
Output: [1, 1, 1]

All elements are equal → all get rank 1.
```

**Example 3:**

```
Input:  arr = [37, 12, 28, 9, 100, 56, 80, 5, 12]
Output: [5, 3, 4, 2, 8, 6, 7, 1, 3]

Sorted unique: [5, 9, 12, 28, 37, 56, 80, 100]
Ranks:           1  2   3   4    5    6   7    8
```

---

## 🧩 Method: Optimal — Sort Unique Values + HashMap

### Core Idea

Rank is determined by an element's position among the **unique sorted** values of the array.
The approach is:

1. **Deduplicate + sort**: Get the sorted list of unique values from `arr`.
2. **Build a HashMap**: Map each unique value → its rank (1-indexed position in the sorted
   unique list).
3. **Fill the result**: For each `arr[i]`, look up its rank in O(1).

This is the same **sort + index map → fill result in original order** pattern used in
Relative Ranks, but with a crucial difference: **duplicate values share the same rank**,
so we must deduplicate before assigning ranks.

### Why deduplicate?

If we assigned ranks without deduplication, duplicates would consume multiple rank slots
(e.g., `[100, 100, 100]` would get ranks `[1, 2, 3]` instead of `[1, 1, 1]`). By working
with unique values only, ranks are contiguous with no gaps.

### Algorithm Steps

1. **`arr.toSet().sorted()`** — Convert to a Set to remove duplicates, then sort ascending.
   The smallest value gets rank 1, the next gets rank 2, etc.
2. **Build `rankMap`**: For each value at index `i` in the sorted unique list, assign
   `rank = i + 1` (1-indexed).
3. **Build result**: For each `arr[i]`, set `result[i] = rankMap[arr[i]]`.

### Step-by-step Walkthrough (Example 3)

```
arr = [37, 12, 28, 9, 100, 56, 80, 5, 12]
```

**Step 1 — Deduplicate & sort:**

```
Unique values: {37, 12, 28, 9, 100, 56, 80, 5}  (note: 12 appears twice but is one unique value)
Sorted unique:  [5, 9, 12, 28, 37, 56, 80, 100]
```

**Step 2 — Build HashMap (value → rank):**

| Value | Index in sorted unique | Rank (index + 1) |
|-------|------------------------|-------------------|
| 5     | 0                      | 1                 |
| 9     | 1                      | 2                 |
| 12    | 2                      | 3                 |
| 28    | 3                      | 4                 |
| 37    | 4                      | 5                 |
| 56    | 5                      | 6                 |
| 80    | 6                      | 7                 |
| 100   | 7                      | 8                 |

**Step 3 — Fill result in original order using O(1) lookups:**

| i | arr[i] | rankMap[arr[i]] | Result |
|---|--------|------------------|--------|
| 0 | 37     | 5                | 5 ✅   |
| 1 | 12     | 3                | 3 ✅   |
| 2 | 28     | 4                | 4 ✅   |
| 3 | 9      | 2                | 2 ✅   |
| 4 | 100    | 8                | 8 ✅   |
| 5 | 56     | 6                | 6 ✅   |
| 6 | 80     | 7                | 7 ✅   |
| 7 | 5      | 1                | 1 ✅   |
| 8 | 12     | 3                | 3 ✅   (same as i=1, duplicate) |

**Result = [5, 3, 4, 2, 8, 6, 7, 1, 3]** ✅

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting the unique values; HashMap build + lookups are O(N) |
| **Space** | O(N) — for the sorted unique list + HashMap + result array |

> **Note:** O(N log N) is optimal because values range up to ±10^9, making counting sort
> impractical. We must sort to determine the relative ordering.

---

## 📊 Comparison with Similar Problems

| Problem | Duplicates? | Rank direction | Key difference |
|---------|-------------|----------------|----------------|
| **Relative Ranks** | No (unique scores) | Descending (highest = rank 1) | Top 3 get medals; no dedup needed |
| **Rank Transform** | Yes (duplicates share rank) | Ascending (smallest = rank 1) | Must deduplicate before ranking |
| **How Many Numbers Smaller** | Yes (duplicates counted) | N/A (count, not rank) | Uses prefix sum on frequency array |

---

## 🔑 Key Takeaways

1. **Deduplication is critical**: duplicates share the same rank, so we work with unique
   values only — `toSet()` handles this.
2. Same **sort + HashMap → fill in original order** pattern as Relative Ranks.
3. Ranks are **1-indexed** and **contiguous** (no gaps) because we assign based on position
   in the sorted unique list.
4. The HashMap enables O(1) lookups when filling the result in original order.
5. Time is O(N log N) due to sorting — optimal since values aren't bounded to a small range.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Rank Transform of an Array | [link](https://leetcode.com/problems/rank-transform-of-an-array/) | Easy |
| Relative Ranks | [link](https://leetcode.com/problems/relative-ranks/) | Easy |
| How Many Numbers Are Smaller Than the Current Number | [link](https://leetcode.com/problems/how-many-numbers-are-smaller-than-the-current-number/) | Easy |
| Height Checker | [link](https://leetcode.com/problems/height-checker/) | Easy |
