# DeleteNodeInLinkedList — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/delete-node-in-a-linked-list/  
> **Topic:** linked_list — single — delete_node_in_linked_list

---

## 📋 Problem Statement

 * https://leetcode.com/problems/delete-node-in-a-linked-list/
 * Delete a node (only given access to that node, not the head).
 * Example: Given node with value 3 in [4,5,1,9] → [4,1,9]
 * FAANG Importance: ⭐⭐⭐ (Trick question: copy next node's value and skip)
 */
 * O(1) time, O(1) space
 * Copy next node's value into current node, then skip the next node.
 * Note: Cannot delete the tail node with this approach.
 */

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `DeleteNodeInLinkedList.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `DeleteNodeInLinkedList.kt` for details.

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
| DeleteNodeInLinkedList | [https://leetcode.com/problems/delete-node-in-a-linked-list/](https://leetcode.com/problems/delete-node-in-a-linked-list/) | Medium |
