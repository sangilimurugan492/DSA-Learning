# FindingPairsWithCertainSum — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/finding-pairs-with-a-certain-sum/description/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/finding-pairs-with-a-certain-sum/description/
 *
 * You are given two integer arrays nums1 and nums2. You are tasked to implement a data structure that supports queries of two types:
 *
 * Add a positive integer to an element of a given index in the array nums2.
 * Count the number of pairs (i, j) such that nums1[i] + nums2[j] equals a given value (0 <= i < nums1.length and 0 <= j < nums2.length).
 * Implement the FindSumPairs class:
 *
 * FindSumPairs(int[] nums1, int[] nums2) Initializes the FindSumPairs object with two integer arrays nums1 and nums2.
 * void add(int index, int val) Adds val to nums2[index], i.e., apply nums2[index] += val.
 * int count(int tot) Returns the number of pairs (i, j) such that nums1[i] + nums2[j] == tot.
 *
 * Input
 * ["FindSumPairs", "count", "add", "count", "count", "add", "add", "count"]
 * [[[1, 1, 2, 2, 2, 3], [1, 4, 5, 2, 5, 4]], [7], [3, 2], [8], [4], [0, 1], [1, 1], [7]]

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `FindingPairsWithCertainSum.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `FindingPairsWithCertainSum.kt` for details.

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
| FindingPairsWithCertainSum | [https://leetcode.com/problems/finding-pairs-with-a-certain-sum/description/](https://leetcode.com/problems/finding-pairs-with-a-certain-sum/description/) | Medium |
