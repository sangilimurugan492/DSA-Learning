# FindTheDifferenceBetweenTwoArray — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/find-the-difference-of-two-arrays/description/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/find-the-difference-of-two-arrays/description/
 *
 * Given two 0-indexed integer arrays nums1 and nums2, return a list answer of size 2 where:
 *
 * answer[0] is a list of all distinct integers in nums1 which are not present in nums2.
 * answer[1] is a list of all distinct integers in nums2 which are not present in nums1.
 * Note that the integers in the lists may be returned in any order.
 *
 * Example 1:
 *
 * Input: nums1 = [1,2,3], nums2 = [2,4,6]
 * Output: [[1,3],[4,6]]
 * Explanation:
 * For nums1, nums1[1] = 2 is present at index 0 of nums2, whereas nums1[0] = 1 and nums1[2] = 3 are not present in nums2. Therefore, answer[0] = [1,3].
 * For nums2, nums2[0] = 2 is present at index 1 of nums1, whereas nums2[1] = 4 and nums2[2] = 6 are not present in nums1. Therefore, answer[1] = [4,6].

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `FindTheDifferenceBetweenTwoArray.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `FindTheDifferenceBetweenTwoArray.kt` for details.

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
| FindTheDifferenceBetweenTwoArray | [https://leetcode.com/problems/find-the-difference-of-two-arrays/description/](https://leetcode.com/problems/find-the-difference-of-two-arrays/description/) | Medium |
