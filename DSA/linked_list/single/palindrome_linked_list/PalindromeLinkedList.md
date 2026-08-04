# PalindromeLinkedList — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/palindrome-linked-list/  
> **Topic:** linked_list — single — palindrome_linked_list

---

## 📋 Problem Statement

 * https://leetcode.com/problems/palindrome-linked-list/
 * Given head of singly linked list, determine if it is a palindrome.
 * Example: [1,2,2,1] → true | [1,2] → false
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Combines Fast/Slow + Reverse)
 */
 * OPTIMAL: O(N) time, O(1) space — Find middle, reverse second half, compare
 * 1. Fast/slow to find middle
 * 2. Reverse from middle to end
 * 3. Compare first half with reversed second half

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `PalindromeLinkedList.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `PalindromeLinkedList.kt` for details.

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
| PalindromeLinkedList | [https://leetcode.com/problems/palindrome-linked-list/](https://leetcode.com/problems/palindrome-linked-list/) | Medium |
