# Move Zeros to End — Detailed Explanation

> **LeetCode #283** | [Problem Link](https://leetcode.com/problems/move-zeroes/description/)  
> **Topic:** Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array `nums`, move all `0`s to the end while maintaining the relative order of non-zero elements. Must be done **in-place**.

### Example

```
Input:  [0,1,0,3,12]
Output: [1,3,12,0,0]
```

---

## 🧩 Method 1: Brute Force

### Core Idea

For each `0` found, swap it forward with the next non-zero element. Uses nested loops.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(1) |

---

## 🧩 Method 2: Two Pointer (Optimal)

### Core Idea

Use a slow pointer `k` that tracks where the next non-zero element should go. Iterate with `i`:
- If `nums[i] != 0`, swap it to position `k`, then `k++`.
- After the loop, all non-zeros are at the front (in original order), zeros are at the back.

### Walkthrough: `[0, 1, 0, 3, 12]`

```
k=0, i=0: nums[0]=0 → skip
k=0, i=1: nums[1]=1 → swap(k=0, i=1) → [1,0,0,3,12]  k=1
k=1, i=2: nums[2]=0 → skip
k=1, i=3: nums[3]=3 → swap(k=1, i=3) → [1,3,0,0,12]  k=2
k=2, i=4: nums[4]=12 → swap(k=2, i=4) → [1,3,12,0,0]  k=3

Result: [1,3,12,0,0] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — in-place |

---

## 🔑 Key Takeaways

1. **Slow/fast pointer pattern:** `k` (slow) marks the boundary; `i` (fast) scans ahead.
2. **Order preserved:** Swapping non-zeros to the front maintains their relative order.
3. **Same pattern as Remove Element:** Both use a "write pointer" that advances only for kept elements.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Move Zeroes | [#283](https://leetcode.com/problems/move-zeroes/) | Easy |
| Remove Element | [#27](https://leetcode.com/problems/remove-element/) | Easy |
| Sort Colors | [#75](https://leetcode.com/problems/sort-colors/) | Medium |
