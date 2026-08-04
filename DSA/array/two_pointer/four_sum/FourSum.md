# FourSum — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/4sum/description/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/4sum/description/
 * Given an array nums of n integers, return an array of all the unique quadruplets [nums[a], nums[b], nums[c], nums[d]] such that:
 *
 * 0 <= a, b, c, d < n
 * a, b, c, and d are distinct.
 * nums[a] + nums[b] + nums[c] + nums[d] == target
 * You may return the answer in any order.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [1,0,-1,0,-2,2], target = 0
 * Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
 *
 * Example 2:
 *
 * Input: nums = [2,2,2,2,2], target = 8
 * Output: [[2,2,2,2]]

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `FourSum.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N⁴) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `FourSum.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N³) |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. See the `.kt` file for full implementation and inline comments.
2. Refer to the LeetCode problem for detailed examples.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| 3Sum | [https://leetcode.com/problems/3sum/](https://leetcode.com/problems/3sum/) | Medium |
| 3Sum Closest | [https://leetcode.com/problems/3sum-closest/](https://leetcode.com/problems/3sum-closest/) | Medium |
| 4Sum II | [https://leetcode.com/problems/4sum-ii/](https://leetcode.com/problems/4sum-ii/) | Medium |
