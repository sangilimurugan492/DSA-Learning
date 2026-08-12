# Remove Element In Array — Detailed Explanation

> **LeetCode #27** | [Problem Link](https://leetcode.com/problems/remove-element/description/)  
> **Topic:** Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an array `nums` and a value `val`, remove all instances of `val` **in-place**. Return the new length `k`. The first `k` elements should be the non-`val` elements.

### Example

```
Input:  nums = [3,2,2,3], val = 3
Output: k=2, nums = [2,2,_,_]
```

---

## 🧩 Method 1: Brute Force

### Core Idea

For each element equal to `val`, swap it toward the back with a non-val element.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(1) |

---

## 🧩 Method 2: Two Pointer (Optimal)

### Core Idea

Use a "write pointer" `count` that only advances when we keep an element:
- If `nums[i] != val` → swap `nums[i]` to position `count`, then `count++`.
- If `nums[i] == val` → skip it (it gets overwritten or pushed to the back).

### Walkthrough: `[3, 2, 2, 3], val = 3`

```
count=0, i=0: nums[0]=3 == val → skip
count=0, i=1: nums[1]=2 != val → swap(0,1) → [2,3,2,3]  count=1
count=1, i=2: nums[2]=2 != val → swap(1,2) → [2,2,3,3]  count=2
count=2, i=3: nums[3]=3 == val → skip

Result: k=2, nums = [2,2,3,3] (first 2 elements are valid) ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — in-place |

---

## 🔑 Key Takeaways

1. **Write pointer pattern:** `count` only advances for kept elements — same pattern as Move Zeros.
2. **Order doesn't matter:** The problem allows any arrangement of the first `k` elements.
3. **In-place:** No extra array needed.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Remove Element | [#27](https://leetcode.com/problems/remove-element/) | Easy |
| Move Zeroes | [#283](https://leetcode.com/problems/move-zeroes/) | Easy |
| Remove Duplicates | [#26](https://leetcode.com/problems/remove-duplicates-from-sorted-array/) | Easy |
