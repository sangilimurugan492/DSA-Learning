# Backspace String Compare — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/backspace-string-compare/description/  
> **Topic:** Two Pointers / String  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given two strings `s` and `t`, return `true` if they are equal when both are typed into empty text editors. `#` means a backspace character.

> **Note:** After backspacing an empty text, the text will continue to be empty.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `s = "ab#c"`, `t = "ad#c"` | `true` | Both become `"ac"` |
| `s = "ab##"`, `t = "c#d#"` | `true` | Both become `""` |
| `s = "a##c"`, `t = "#a#c"` | `true` | Both become `"c"` |

---

## 🧩 Method 1: Stack-Based Simulation

### Core Idea

Simulate typing into a text editor using a list (acting as a stack):
1. Iterate over each character of the string.
2. If the character is **not** `#`, push it onto the stack.
3. If the character **is** `#`, pop the top of the stack (if non-empty).
4. After processing both strings, compare the results.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(m + n) |
| **Space** | O(m + n) |

---

## 🧩 Method 2: Two-Pointer (Optimal)

### Core Idea

Process both strings **from right to left** so we can determine which characters actually survive backspacing without building intermediate strings:
1. Place a pointer at the last index of each string.
2. For each pointer, skip over characters that would be backspaced:
   - Count `#` characters encountered (increment `skip`).
   - Decrement `skip` for each non-`#` character consumed.
   - Stop when a **valid** (surviving) character is found.
3. Compare the two surviving characters:
   - If both exist and differ → `false`.
   - If one exists but the other doesn't → `false`.
4. Move both pointers left and repeat.
5. If the loop completes without mismatches → `true`.

### Walkthrough (`s = "ab##"`, `t = "c#d#"`)

```
s: a b # #    →  right-to-left: '#'(skip=1) '#'(skip=2) 'b'(skip=1) 'a'(skip=0) → exhausted
t: c # d #    →  right-to-left: '#'(skip=1) 'd'(skip=0) '#'(skip=1) 'c'(skip=0) → exhausted
Both exhausted → true
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(m + n) |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Two pointers from the end** let us resolve backspaces without extra space — a key technique for "process-in-reverse" problems.
2. The stack method is intuitive but uses O(n) space; the two-pointer method is the optimal O(1) space solution.
3. Always handle edge cases: backspacing an empty string, leading `#`, and strings of unequal length.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Backspace String Compare | [Link](https://leetcode.com/problems/backspace-string-compare/description/) | Easy |
| Crawler Log Folder | [Link](https://leetcode.com/problems/crawler-log-folder/) | Easy |
