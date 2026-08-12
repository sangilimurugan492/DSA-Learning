# Sort Colors (Dutch National Flag) — Detailed Explanation

> **LeetCode #75** | [Problem Link](https://leetcode.com/problems/sort-colors/description/)  
> **Topic:** Two Pointers / 3-Way Partition  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array `nums` with values `0` (red), `1` (white), `2` (blue), sort them **in-place** so that same colors are adjacent.

### Example

```
Input:  [2,0,2,1,1,0]
Output: [0,0,1,1,2,2]
```

---

## 🧩 Method 1: Brute Force — Bubble Sort Style

### Core Idea

Use nested loops to swap elements into sorted order. Simple but O(N²).

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(1) |

---

## 🧩 Method 2: Dutch National Flag (Optimal)

### Core Idea

Use **3 pointers** to partition the array into 4 zones in a single pass:

```
[0 .. low-1] → all 0s (sorted)
[low .. mid-1] → all 1s (sorted)
[mid .. high] → unknown (being processed)
[high+1 .. end] → all 2s (sorted)
```

### Algorithm — Step by Step

1. Initialize `low = 0`, `mid = 0`, `high = n-1`.
2. While `mid <= high`:
   - If `nums[mid] == 0` → swap with `low`, then `low++`, `mid++`
   - If `nums[mid] == 1` → already in place, just `mid++`
   - If `nums[mid] == 2` → swap with `high`, then `high--` (don't move `mid` — check swapped value)

### Walkthrough: `[2, 0, 1]`

```
Initial:  [2, 0, 1]  low=0 mid=0 high=2

mid=0: nums[0]=2 → swap with high → [1, 0, 2]  high=1
mid=0: nums[0]=1 → mid++           → [1, 0, 2]  mid=1
mid=1: nums[1]=0 → swap with low  → [0, 1, 2]  low=1 mid=2
mid=2 > high=1 → stop

Result: [0, 1, 2] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — in-place |

---

## 🔑 Key Takeaways

1. **3-way partition:** The Dutch National Flag algorithm partitions into 3 groups using 3 pointers.
2. **Don't advance `mid` after swapping with `high`:** The swapped-in value is unknown and must be checked.
3. **In-place:** No extra array needed — all swaps happen within the input.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Sort Colors | [#75](https://leetcode.com/problems/sort-colors/) | Medium |
| Move Zeroes | [#283](https://leetcode.com/problems/move-zeroes/) | Easy |
