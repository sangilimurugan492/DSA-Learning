# Relative Ranks — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/relative-ranks/  
> **Topic:** Array, Sorting, HashMap  
> **Difficulty:** Easy

---

## 📋 Problem Statement

You are given an integer array `score` of size `n`, where `score[i]` is the score of the
ith athlete in a competition. All scores are guaranteed to be **unique**.

Athletes are placed based on their scores — the 1st place athlete has the highest score,
the 2nd place has the 2nd highest, and so on. The placement determines the rank:

- **1st place** → `"Gold Medal"`
- **2nd place** → `"Silver Medal"`
- **3rd place** → `"Bronze Medal"`
- **4th–nth place** → placement number as a string (e.g., `"4"`, `"5"`, ...)

Return an array `answer` where `answer[i]` is the rank of the ith athlete.

### Constraints

- `n == score.length`
- `1 <= n <= 10^4`
- `0 <= score[i] <= 10^6`
- All values in `score` are unique.

### Examples

**Example 1:**

```
Input:  score = [5, 4, 3, 2, 1]
Output: ["Gold Medal", "Silver Medal", "Bronze Medal", "4", "5"]

Scores are already in descending order, so placements are [1st, 2nd, 3rd, 4th, 5th].
```

**Example 2:**

```
Input:  score = [10, 3, 8, 9, 4]
Output: ["Gold Medal", "5", "Bronze Medal", "Silver Medal", "4"]

Sorted descending: [10, 9, 8, 4, 3]
  score[0]=10 → 1st → "Gold Medal"
  score[1]=3  → 5th → "5"
  score[2]=8  → 3rd → "Bronze Medal"
  score[3]=9  → 2nd → "Silver Medal"
  score[4]=4  → 4th → "4"
```

---

## 🧩 Method 1: Brute Force — Sort + Linear Search

### Core Idea

1. Sort a copy of `score` in **descending** order — the index in this sorted array is the
   athlete's rank (0-indexed).
2. For each element in the **original** array, find its index in the sorted array using
   linear search (`indexOf`).
3. Convert the rank to a medal string.

### Step-by-step Walkthrough (Example 2)

```
score = [10, 3, 8, 9, 4]

Step 1 — Sort descending:
  sorted = [10, 9, 8, 4, 3]

Step 2 — For each original score, find its rank via linear search:
  score[0]=10 → indexOf(10) in sorted = 0 → "Gold Medal"
  score[1]=3  → indexOf(3)  in sorted = 4 → "5"
  score[2]=8  → indexOf(8)  in sorted = 2 → "Bronze Medal"
  score[3]=9  → indexOf(9)  in sorted = 1 → "Silver Medal"
  score[4]=4  → indexOf(4)  in sorted = 3 → "4"

Result = ["Gold Medal", "5", "Bronze Medal", "Silver Medal", "4"]
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — O(N log N) for sort + O(N) linear search per element |
| **Space** | O(N) — for the sorted copy and result array |

---

## 🧩 Method 2: Optimal — HashMap + Sort

### Core Idea

The brute-force bottleneck is the **O(N) linear search** for each score. We can eliminate
this by building a **HashMap** that maps each score → its rank (position in sorted array),
giving O(1) lookups.

### Algorithm Steps

1. **Sort** a copy of `score` in descending order.
2. **Build a HashMap**: For each index `rank` in the sorted array, map `sorted[rank] → rank`.
   Since all scores are unique, this is a one-to-one mapping.
3. **Build the result** in original order: For each `score[i]`, look up its rank from the
   HashMap in O(1), then convert to a medal string.

### Step-by-step Walkthrough (Example 2)

```
score = [10, 3, 8, 9, 4]
```

**Step 1 — Sort descending:**

```
sorted = [10, 9, 8, 4, 3]
          ↑   ↑  ↑  ↑  ↑
         1st 2nd 3rd 4th 5th   (rank 0–4, 0-indexed)
```

**Step 2 — Build HashMap (score → rank):**

| Score | Rank (0-indexed) | Placement |
|-------|-------------------|-----------|
| 10    | 0                 | 1st       |
| 9     | 1                 | 2nd       |
| 8     | 2                 | 3rd       |
| 4     | 3                 | 4th       |
| 3     | 4                 | 5th       |

**Step 3 — Fill result in original order using O(1) lookups:**

| i | score[i] | rankMap[score[i]] | Medal String |
|---|----------|--------------------|--------------|
| 0 | 10       | 0                  | "Gold Medal" ✅ |
| 1 | 3        | 4                  | "5" ✅ |
| 2 | 8        | 2                  | "Bronze Medal" ✅ |
| 3 | 9        | 1                  | "Silver Medal" ✅ |
| 4 | 4        | 3                  | "4" ✅ |

**Result = ["Gold Medal", "5", "Bronze Medal", "Silver Medal", "4"]** ✅

### Medal Mapping Logic

| Rank (0-indexed) | Placement | Output String |
|-------------------|-----------|---------------|
| 0                 | 1st       | "Gold Medal"  |
| 1                 | 2nd       | "Silver Medal"|
| 2                 | 3rd       | "Bronze Medal"|
| 3+                | 4th+      | (rank + 1).toString() |

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting; HashMap build + lookups are O(N) |
| **Space** | O(N) — for sorted copy + HashMap + result array |

### Why is this better than brute force?

| Aspect | Brute Force (Sort + Linear Search) | Optimal (Sort + HashMap) |
|--------|-------------------------------------|--------------------------|
| Time | O(N²) | O(N log N) |
| Space | O(N) | O(N) |
| Key difference | Linear search per element | O(1) HashMap lookup per element |

> **Note:** O(N log N) is optimal for this problem because we must sort to determine rankings.
> The scores are not bounded to a small range (up to 10^6), so counting sort would require
> a very large array and is not practical here.

---

## 🔑 Key Takeaways

1. **Sorting** is the natural first step when we need to rank elements by value.
2. **HashMap** bridges the gap between sorted order and original order — store the mapping
   after sorting, then look up in O(1) when filling results in original order.
3. This is a common pattern: **sort + index map → fill result in original order**.
4. Scores must be **unique** for the HashMap approach to work (guaranteed by constraints).
5. Time is O(N log N) due to sorting — this is optimal since values aren't bounded to a
   small range.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Relative Ranks | [link](https://leetcode.com/problems/relative-ranks/) | Easy |
| How Many Numbers Are Smaller Than the Current Number | [link](https://leetcode.com/problems/how-many-numbers-are-smaller-than-the-current-number/) | Easy |
| Height Checker | [link](https://leetcode.com/problems/height-checker/) | Easy |
| Rank Transform of an Array | [link](https://leetcode.com/problems/rank-transform-of-an-array/) | Easy |
