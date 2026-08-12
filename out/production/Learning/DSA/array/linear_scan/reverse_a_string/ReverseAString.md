# Reverse a String — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/reverse-string/  
> **Topic:** Array, Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Write a function that reverses a string. The input is given as a character array `s`, and
you must modify it **in-place** with O(1) extra space.

### Constraints

- `1 <= s.length <= 10^5`
- `s[i]` is a printable ascii character

### Examples

**Example 1:**

```
Input:  s = ['h','e','l','l','o']
Output: ['o','l','l','e','h']
```

**Example 2:**

```
Input:  s = ['H','a','n','n','a','h']
Output: ['h','a','n','n','a','H']
```

---

## 🧩 Method 1: Brute Force — Extra Array

### Core Idea

Create a new array and copy elements from the original in reverse order, then copy back.

### Step-by-step Walkthrough (Example 1)

```
s = ['h','e','l','l','o']

Copy in reverse:
  temp[0] = s[4] = 'o'
  temp[1] = s[3] = 'l'
  temp[2] = s[2] = 'l'
  temp[3] = s[1] = 'e'
  temp[4] = s[0] = 'h'

temp = ['o','l','l','e','h'] → copy back to s
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass to copy |
| **Space** | O(N) — extra array of size N (violates in-place constraint) |

---

## 🧩 Method 2: Optimal — Two Pointers (In-Place Swap)

### Core Idea

Use two pointers: `left` at the start and `right` at the end. Swap `s[left]` and `s[right]`,
then move both inward until they meet. This reverses the array in-place with O(1) space.

### Step-by-step Walkthrough (Example 1)

```
s = ['h','e','l','l','o']
      ↑           ↑
    left        right

Step 1: left=0, right=4 → swap 'h' and 'o' → ['o','e','l','l','h']
Step 2: left=1, right=3 → swap 'e' and 'l' → ['o','l','l','e','h']
Step 3: left=2, right=2 → left >= right → stop

Result = ['o','l','l','e','h'] ✅
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
4. The same technique works for arrays of any type (int, char, etc.).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Reverse String | [link](https://leetcode.com/problems/reverse-string/) | Easy |
| Reverse Vowels of a String | [link](https://leetcode.com/problems/reverse-vowels-of-a-string/) | Easy |
| Reverse Words in a String | [link](https://leetcode.com/problems/reverse-words-in-a-string/) | Medium |
| Reverse Array | — | Easy |
