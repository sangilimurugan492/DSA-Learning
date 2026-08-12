# How Many Numbers Are Smaller Than the Current Number — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/how-many-numbers-are-smaller-than-the-current-number/  
> **Topic:** Array, Counting Sort, Prefix Sum  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given the array `nums`, for each `nums[i]` find out how many numbers in the array are
**smaller than it**. That is, for each `nums[i]` you have to count the number of valid `j`'s
such that `j != i` and `nums[j] < nums[i]`.

Return the answer in an array.

### Constraints

- `2 <= nums.length <= 500`
- `0 <= nums[i] <= 100`

### Examples

**Example 1:**

```
Input:  nums = [8, 1, 2, 2, 3]
Output: [4, 0, 1, 1, 3]

For nums[0]=8 → four smaller numbers (1, 2, 2, 3)
For nums[1]=1 → no smaller number
For nums[2]=2 → one smaller number (1)
For nums[3]=2 → one smaller number (1)
For nums[4]=3 → three smaller numbers (1, 2, 2)
```

**Example 2:**

```
Input:  nums = [6, 5, 4, 8]
Output: [2, 1, 0, 3]
```

**Example 3:**

```
Input:  nums = [7, 7, 7, 7]
Output: [0, 0, 0, 0]
```

---

## 🧩 Method 1: Brute Force — Nested Loops

### Core Idea

For each element `nums[i]`, scan the entire array and count how many elements `nums[j]`
satisfy `nums[j] < nums[i]`.

### Step-by-step Walkthrough (Example 1)

```
nums = [8, 1, 2, 2, 3]

i=0 (8): scan [8,1,2,2,3] → 1,2,2,3 are smaller → count=4
i=1 (1): scan [8,1,2,2,3] → nothing is smaller    → count=0
i=2 (2): scan [8,1,2,2,3] → only 1 is smaller      → count=1
i=3 (2): scan [8,1,2,2,3] → only 1 is smaller      → count=1
i=4 (3): scan [8,1,2,2,3] → 1,2,2 are smaller      → count=3

Result = [4, 0, 1, 1, 3]
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — for each of N elements, scan all N elements |
| **Space** | O(N) — for the result array |

---

## 🧩 Method 2: Optimal — Counting Sort + Prefix Sum

### Core Idea

The key insight is that **`nums[i]` is constrained to the range [0, 100]** — a small, fixed
range. This means:

1. The **number of elements smaller than value `v`** = sum of frequencies of all values
   from `0` to `v-1`. This is a **prefix sum** of the frequency array.
2. We can precompute the answer for every possible value (0–100) in O(K) time, then answer
   each `nums[i]` with a simple O(1) lookup.

### Algorithm Steps

1. **Build a frequency array** `counts` of size 101, where `counts[v]` = number of times
   value `v` appears in `nums`.
2. **Convert to a prefix-sum ("smaller count") array**: Iterate from `v=0` to `v=100`.
   Maintain a running `prefix` variable that tracks how many elements have value `< v`.
   At each `v`, set `counts[v] = prefix` (this becomes the answer for value `v`), then
   update `prefix += freq` (to include elements equal to `v` for the next iteration).
3. **Build the result**: For each `nums[i]`, look up `counts[nums[i]]` in O(1).

### Step-by-step Walkthrough (Example 1)

```
nums = [8, 1, 2, 2, 3]
```

**Step 1 — Build frequency array (size 101, showing indices 0–8):**

```
counts = [0, 1, 2, 1, 0, 0, 0, 0, 1, ...]
          ↑  ↑  ↑  ↑              ↑
          0s 1s 2s 3s             8s
```

**Step 2 — Convert to "smaller count" array using running prefix:**

At each index `v`, we store the current `prefix` (count of all elements < `v`), then add
`freq` to `prefix`.

| v | freq (counts[v] before) | prefix (before) | counts[v] = (answer for v) | prefix (after) |
|---|--------------------------|-------------------|-----------------------------|-----------------|
| 0 | 0                        | 0                 | 0                           | 0               |
| 1 | 1                        | 0                 | 0                           | 1               |
| 2 | 2                        | 1                 | 1                           | 3               |
| 3 | 1                        | 3                 | 3                           | 4               |
| 4 | 0                        | 4                 | 4                           | 4               |
| 5 | 0                        | 4                 | 4                           | 4               |
| 6 | 0                        | 4                 | 4                           | 4               |
| 7 | 0                        | 4                 | 4                           | 4               |
| 8 | 1                        | 4                 | 4                           | 5               |

After this step: `counts = [0, 0, 1, 3, 4, 4, 4, 4, 4, ...]`

**Step 3 — Look up answer for each `nums[i]`:**

| i | nums[i] | counts[nums[i]] | Result |
|---|---------|------------------|--------|
| 0 | 8       | counts[8] = 4    | 4 ✅   |
| 1 | 1       | counts[1] = 0    | 0 ✅   |
| 2 | 2       | counts[2] = 1    | 1 ✅   |
| 3 | 2       | counts[2] = 1    | 1 ✅   |
| 4 | 3       | counts[3] = 3    | 3 ✅   |

**Result = [4, 0, 1, 1, 3]** ✅

### Why does the prefix trick work?

- `prefix` before processing index `v` = total count of all elements with value in `[0, v-1]`.
- That's **exactly** the number of elements **strictly smaller than** `v`.
- By storing `prefix` into `counts[v]` *before* adding `freq`, we overwrite the frequency
  with the answer, avoiding a separate array.
- Duplicate values (like the two `2`s) naturally get the same answer since they map to the
  same index.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N + K) where K = 101 (max value + 1). Since K is constant, effectively **O(N)**. |
| **Space** | O(K) = O(101) = **O(1)** — fixed-size frequency array regardless of input size. |

### Why is this better than brute force?

| Aspect | Brute Force (Nested Loops) | Optimal (Counting Sort + Prefix Sum) |
|--------|----------------------------|--------------------------------------|
| Time | O(N²) | O(N) |
| Space | O(N) | O(1) |
| When to use | General-purpose, any value range | When value range is small & known |

---

## 🔑 Key Takeaways

1. **Small, bounded value ranges** unlock counting sort — no comparison-based sorting needed.
2. A **prefix sum** over the frequency array gives the "count of elements smaller than v"
   for all values in a single O(K) pass.
3. The in-place prefix trick (store `prefix` before adding `freq`) avoids allocating a
   separate answer array for values.
4. Duplicate values are handled naturally — they map to the same index and get the same answer.
5. Time is O(N + K) ≈ O(N) and space is O(K) ≈ O(1) since K=101 is constant.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| How Many Numbers Are Smaller Than the Current Number | [link](https://leetcode.com/problems/how-many-numbers-are-smaller-than-the-current-number/) | Easy |
| Height Checker | [link](https://leetcode.com/problems/height-checker/) | Easy |
| Relative Ranks | [link](https://leetcode.com/problems/relative-ranks/) | Easy |
| Rank Transform of an Array | [link](https://leetcode.com/problems/rank-transform-of-an-array/) | Easy |
