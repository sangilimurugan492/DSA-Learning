# SortList — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/sort-list/  
> **Topic:** linked_list — single — sort_list

---

## 📋 Problem Statement

 * https://leetcode.com/problems/sort-list/
 * Sort a linked list in O(N log N) time using constant space.
 * Example: [4,2,1,3] → [1,2,3,4]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Merge sort on LL, combines fast/slow + merge)
 */
 * MERGE SORT: O(N log N) time, O(log N) stack space
 * 1. Find middle using fast/slow pointer
 * 2. Recursively sort both halves
 * 3. Merge the two sorted halves
 */

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `SortList.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `SortList.kt` for details.

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
| SortList | [https://leetcode.com/problems/sort-list/](https://leetcode.com/problems/sort-list/) | Medium |
