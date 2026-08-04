# StringCompression — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/string-compression/?envType=problem-list-v2&envId=two-pointers  
> **Topic:** Array

---

## 📋 Problem Statement

 * https://leetcode.com/problems/string-compression/?envType=problem-list-v2&envId=two-pointers
 * Given an array of characters chars, compress it using the following algorithm:
 *
 * Begin with an empty string s. For each group of consecutive repeating characters in chars:
 *
 * If the group's length is 1, append the character to s.
 * Otherwise, append the character followed by the group's length.
 * The compressed string s should not be returned separately, but instead, be stored in the input character array chars. Note that group lengths that are 10 or longer will be split into multiple characters in chars.
 *
 * After you are done modifying the input array, return the new length of the array.
 *
 * You must write an algorithm that uses only constant extra space.
 *
 * Note: The characters in the array beyond the returned length do not matter and should be ignored.
 *

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `StringCompression.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `StringCompression.kt` for details.

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
| StringCompression | [https://leetcode.com/problems/string-compression/?envType=problem-list-v2&envId=two-pointers](https://leetcode.com/problems/string-compression/?envType=problem-list-v2&envId=two-pointers) | Medium |
