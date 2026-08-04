# TrappingRainWaterI — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/trapping-rain-water/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/trapping-rain-water/
 *
 * Given n non-negative integers representing an elevation map, compute how much
 * water it can trap after raining.
 *
 * Example: height = [0,1,0,2,1,0,1,3,2,1,2,1] → Output: 6
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 3 most asked Hard problem)
 */
 * BRUTE FORCE
 * Time Complexity: O(N²) — for each bar, scan left and right for max
 * Space Complexity: O(1)
 *
 * For each index i, water = min(maxLeft, maxRight) - height[i].
 * Scan left/right for each position to find max.

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `TrappingRainWaterI.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `TrappingRainWaterI.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. See the `.kt` file for full implementation and inline comments.
2. Refer to the LeetCode problem for detailed examples.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| TrappingRainWaterI | [https://leetcode.com/problems/trapping-rain-water/](https://leetcode.com/problems/trapping-rain-water/) | Medium |
