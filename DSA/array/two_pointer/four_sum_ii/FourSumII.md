# FourSumII — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/4sum-ii/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/4sum-ii/
 * Given four integer arrays nums1, nums2, nums3, and nums4, all of length n,
 * return the number of tuples (i, j, k, l) such that:
 *
 * 0 <= i, j, k, l < n
 * nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0
 *
 *
 * Example 1:
 *
 * Input: nums1 = [1,2], nums2 = [-2,-1], nums3 = [-1,2], nums4 = [0,2]
 * Output: 2
 *
 * Example 2:
 *
 * Input: nums1 = [0], nums2 = [0], nums3 = [0], nums4 = [0]
 * Output: 1

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `FourSumII.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N⁴) |
| **Space** | O(1) |

---

## 🧩 Method 2: Optimal (HashMap)

### Core Idea

See implementation in `FourSumII.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N²) |

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
| FourSum | [https://leetcode.com/problems/4sum/](https://leetcode.com/problems/4sum/) | Medium |
