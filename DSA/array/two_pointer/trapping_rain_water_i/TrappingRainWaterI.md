# Trapping Rain Water I — Detailed Explanation

> **LeetCode #42** | [Problem Link](https://leetcode.com/problems/trapping-rain-water/)  
> **Topic:** Two Pointers / Dynamic Programming  
> **Difficulty:** Hard

---

## 📋 Problem Statement

Given `n` non-negative integers representing an elevation map, compute how much water can be **trapped** after raining.

### Key Formula

> **Water at index `i` = `min(maxLeft[i], maxRight[i]) - height[i]`**

### Example

```
Input:  [0,1,0,2,1,0,1,3,2,1,2,1]
Output: 6
```

---

## 🧩 Method 1: Brute Force

### Core Idea

For each index `i`, scan left and right to find `maxLeft` and `maxRight`. Water = `min(maxLeft, maxRight) - height[i]`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(1) |

---

## 🧩 Method 2: DP (Precompute maxLeft/maxRight)

### Core Idea

Precompute `maxLeft[]` and `maxRight[]` arrays in O(N), then calculate water in a single pass.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — three passes |
| **Space** | O(N) — two arrays |

---

## 🧩 Method 3: Two Pointer (Optimal)

### Core Idea

Use two pointers from both ends. Track `maxLeft` and `maxRight` as running variables. Move the pointer with the **smaller** max — that side is the bottleneck.

### Key Insight

> If `maxLeft < maxRight`, the water at `left` is determined by `maxLeft` (since `maxRight` is already taller). So we can safely calculate water at `left` and move it inward.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — only variables |

---

## 📊 Comparison

| Method | Time | Space |
|--------|------|-------|
| Brute Force | O(N²) | O(1) |
| DP | O(N) | O(N) |
| Two Pointer | O(N) | O(1) |

---

## 🔑 Key Takeaways

1. **Water formula:** `min(maxLeft, maxRight) - height[i]` — water level is determined by the shorter of the two tallest bars on each side.
2. **Move the bottleneck:** Always process the side with the smaller max — that's the limiting factor.
3. **Same pattern** as Container With Most Water.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Trapping Rain Water | [#42](https://leetcode.com/problems/trapping-rain-water/) | Hard |
| Container With Most Water | [#11](https://leetcode.com/problems/container-with-most-water/) | Medium |
| Trapping Rain Water II | [#407](https://leetcode.com/problems/trapping-rain-water-ii/) | Hard |
