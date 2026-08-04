# LinkedListCycleII — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/linked-list-cycle-ii/  
> **Topic:** linked_list — single — linked_list_cycle_ii

---

## 📋 Problem Statement

 * https://leetcode.com/problems/linked-list-cycle-ii/
 * Given head of linked list, return node where cycle begins. Return null if no cycle.
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */
 * OPTIMAL: O(N) time, O(1) space — Floyd's algorithm
 * Phase 1: Find meeting point (fast/slow pointers)
 * Phase 2: Reset one pointer to head, move both 1 step until they meet = cycle start
 */

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `LinkedListCycleII.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `LinkedListCycleII.kt` for details.

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
| LinkedListCycleII | [https://leetcode.com/problems/linked-list-cycle-ii/](https://leetcode.com/problems/linked-list-cycle-ii/) | Medium |
