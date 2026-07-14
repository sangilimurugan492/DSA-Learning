# MaximumAverageSubArray — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/maximum-average-subarray-i/  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/maximum-average-subarray-i/
 * Given integer array nums and integer k, find max average of any contiguous subarray of length k.
 * Example: nums = [1,12,-5,-6,50,3], k = 4 → Output: 12.75 (avg of [12,-5,-6,50])
 * FAANG Importance: ⭐⭐⭐ (Classic fixed-window warm-up)
 */
 * BRUTE FORCE: O(N × K) — calculate sum for every window of size k
 */
 * OPTIMAL: O(N) Fixed Sliding Window
 * Maintain window sum. Add new element, remove old element.
 */

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `MaximumAverageSubArray.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `MaximumAverageSubArray.kt` for details.

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
| MaximumAverageSubArray | [https://leetcode.com/problems/maximum-average-subarray-i/](https://leetcode.com/problems/maximum-average-subarray-i/) | Medium |
