# Height Checker — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/height-checker/description/  
> **Topic:** Array, Counting Sort  
> **Difficulty:** Easy

---

## 📋 Problem Statement

A school is trying to take an annual photo of all the students. The students are asked to
stand in a single file line in **non-decreasing order by height**. Let this ordering be
represented by the integer array `expected` where `expected[i]` is the expected height of
the ith student in line.

You are given an integer array `heights` representing the **current order** that the students
are standing in. Each `heights[i]` is the height of the ith student in line (0-indexed).

**Return the number of indices where `heights[i] != expected[i]`.**

### Constraints

- `1 <= heights.length <= 100`
- `1 <= heights[i] <= 100`

### Examples

**Example 1:**

```
Input:  heights = [1, 1, 4, 2, 1, 3]
Output: 3

heights:  [1, 1, 4, 2, 1, 3]
expected: [1, 1, 1, 2, 3, 4]
          ✓  ✓  ✗  ✓  ✗  ✗   → 3 mismatches (indices 2, 4, 5)
```

**Example 2:**

```
Input:  heights = [5, 1, 2, 3, 4]
Output: 5

heights:  [5, 1, 2, 3, 4]
expected: [1, 2, 3, 4, 5]
          ✗  ✗  ✗  ✗  ✗   → 5 mismatches (all indices)
```

---

## 🧩 Method 1: Brute Force — Sort and Compare

### Core Idea

1. Create a **sorted copy** of `heights` — this is the `expected` array (what the line *should*
   look like).
2. Walk through both arrays element-by-element and **count how many positions differ**.

### Step-by-step Walkthrough (Example 1)

```
heights = [1, 1, 4, 2, 1, 3]

Step 1 — Copy & sort:
  expected = [1, 1, 1, 2, 3, 4]

Step 2 — Compare:
  Index 0: heights[0]=1  vs expected[0]=1  ✓
  Index 1: heights[1]=1  vs expected[1]=1  ✓
  Index 2: heights[2]=4  vs expected[2]=1  ✗  mismatch
  Index 3: heights[3]=2  vs expected[3]=2  ✓
  Index 4: heights[4]=1  vs expected[4]=3  ✗  mismatch
  Index 5: heights[5]=3  vs expected[5]=4  ✗  mismatch

Result = 3
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting |
| **Space** | O(N) — for the sorted copy |

---

## 🧩 Method 2: Optimal — Counting Sort

### Core Idea

The key insight is that **heights are constrained to the range [1, 100]**. This is a small,
fixed range, which makes **counting sort** ideal.

Instead of sorting the array explicitly, we:

1. Build a **frequency array** (`counts`) where `counts[h]` = number of students with height `h`.
2. Walk through the original `heights` array again. We maintain a pointer `currentHeight` that
   tells us **what the next height in the sorted order should be**.
3. At each position, if the actual height doesn't match `currentHeight`, we have a mismatch.
4. We "consume" one occurrence of `currentHeight` by decrementing its count.

This avoids creating the sorted array entirely — we simulate reading from it on the fly.

### Step-by-step Walkthrough (Example 1)

```
heights = [1, 1, 4, 2, 1, 3]
```

**Step 1 — Build frequency array (size 101, indices 0–100)**

```
counts[1] = 3   (three students of height 1)
counts[2] = 1   (one student of height 2)
counts[3] = 1   (one student of height 3)
counts[4] = 1   (one student of height 4)
all others = 0
```

**Step 2 — Iterate through heights and compare against `currentHeight`**

`currentHeight` starts at 0. We skip any height with `count == 0`.

| i | heights[i] | currentHeight (expected) | Match? | Action | mismatches |
|---|------------|--------------------------|--------|--------|------------|
| 0 | 1          | 1 (skip 0, count[0]=0)   | ✓      | counts[1]-- → 2 | 0 |
| 1 | 1          | 1                        | ✓      | counts[1]-- → 1 | 0 |
| 2 | 4          | 1                        | ✗      | counts[1]-- → 0 | 1 |
| 3 | 2          | 1 → skip (count=0) → 2   | ✓      | counts[2]-- → 0 | 1 |
| 4 | 1          | 2 → skip (count=0) → 3   | ✗      | counts[3]-- → 0 | 2 |
| 5 | 3          | 3 → skip (count=0) → 4   | ✗      | counts[4]-- → 0 | 3 |

**Result = 3** ✅

> **Why it works:** The `currentHeight` pointer always points to the next smallest height that
> still has students remaining. This is exactly what the sorted array would produce at each
> position. By comparing `heights[i]` against `currentHeight`, we're effectively comparing
> against `expected[i]` without ever building `expected`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N + K) where K = 100 (max height). Since K is constant, this is effectively **O(N)**. |
| **Space** | O(K) = O(101) = **O(1)** — the frequency array is fixed-size regardless of input. |

### Why is this better than brute force?

| Aspect | Brute Force (Sort) | Optimal (Counting Sort) |
|--------|--------------------|-------------------------|
| Time | O(N log N) | O(N) |
| Space | O(N) | O(1) |
| When to use | General-purpose, any range | When value range is small & known |

---

## 🔑 Key Takeaways

1. **Counting sort** is the optimal approach when the value range is small and known (here, 1–100).
2. We don't need to build the sorted array — a frequency array + pointer simulates reading from it.
3. The `currentHeight` pointer advances only forward, so the total pointer movement across the
   entire loop is at most K (100), keeping the overall time O(N + K) = O(N).
4. Space is O(1) because the frequency array size (101) is constant regardless of input size.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Height Checker | [height-checker](https://leetcode.com/problems/height-checker/description/) | Easy |
| How Many Numbers Are Smaller Than the Current Number | [how-many-numbers-are-smaller-than-the-current-number](https://leetcode.com/problems/how-many-numbers-are-smaller-than-the-current-number/) | Easy |
| Relative Ranks | [relative-ranks](https://leetcode.com/problems/relative-ranks/) | Easy |
