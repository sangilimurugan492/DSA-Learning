# Reverse an Array — Detailed Explanation

> **Topic:** Array, Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array of integers, reverse the order of its elements **in-place**.

### Examples

**Example 1:**

```
Input:  arr = [1, 4, 3, 2, 6, 5, 7]
Output: [7, 5, 6, 2, 3, 4, 1]
```

**Example 2:**

```
Input:  arr = [1, 2, 3, 4, 5]
Output: [5, 4, 3, 2, 1]
```

---

## 🧩 Method 1: Brute Force — Extra Array

### Core Idea

Create a new array and copy elements from the original in reverse order, then copy back
into the original array.

### Step-by-step Walkthrough (Example 1)

```
arr = [1, 4, 3, 2, 6, 5, 7]

Copy in reverse:
  temp[0] = arr[6] = 7
  temp[1] = arr[5] = 5
  temp[2] = arr[4] = 6
  temp[3] = arr[3] = 2
  temp[4] = arr[2] = 3
  temp[5] = arr[1] = 4
  temp[6] = arr[0] = 1

temp = [7, 5, 6, 2, 3, 4, 1] → copy back to arr
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass to copy |
| **Space** | O(N) — extra array of size N |

---

## 🧩 Method 2: Optimal — Two Pointers (In-Place Swap)

### Core Idea

Use two pointers: `left` at the start and `right` at the end. Swap `arr[left]` and
`arr[right]`, then move both inward until they meet. This reverses the array in-place
with O(1) space.

### Step-by-step Walkthrough (Example 1)

```
arr = [1, 4, 3, 2, 6, 5, 7]
       ↑                 ↑
     left              right

Step 1: left=0, right=6 → swap 1 and 7 → [7, 4, 3, 2, 6, 5, 1]
Step 2: left=1, right=5 → swap 4 and 5 → [7, 5, 3, 2, 6, 4, 1]
Step 3: left=2, right=4 → swap 3 and 6 → [7, 5, 6, 2, 3, 4, 1]
Step 4: left=3, right=3 → left >= right → stop

Result = [7, 5, 6, 2, 3, 4, 1] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — N/2 swaps, each O(1) |
| **Space** | O(1) — only a temp variable |

---

## 🔑 Key Takeaways

1. **Two-pointer swap** is the standard in-place reversal technique — O(N) time, O(1) space.
2. Swap from both ends moving inward until the pointers meet in the middle.
3. Only N/2 swaps are needed — each swap fixes two positions.
4. The same pattern applies to strings (char arrays), linked lists, etc.

---

## 📚 Related Problems

| Problem | Link | Difficulty |
|---------|------|------------|
| Reverse String | [LeetCode](https://leetcode.com/problems/reverse-string/) | Easy |
| Reverse Vowels of a String | [LeetCode](https://leetcode.com/problems/reverse-vowels-of-a-string/) | Easy |
| Reverse Linked List | [LeetCode](https://leetcode.com/problems/reverse-linked-list/) | Easy |
