# Intersection of Two Arrays II — Detailed Explanation

> **LeetCode #350** | [Problem Link](https://leetcode.com/problems/intersection-of-two-arrays-ii/)  
> **Topic:** HashMap / Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given two integer arrays `nums1` and `nums2`, return an array of their **intersection**. Each element in the result must appear as many times as it shows in both arrays.

### Example

```
Input:  nums1 = [1,2,2,1], nums2 = [2,2]
Output: [2,2]
```

---

## 🧩 Method 1: HashMap Frequency Count

### Core Idea

1. Count the frequency of each number in `nums1` using a HashMap.
2. Iterate through `nums2` — if a number exists in the map with count > 0, add it to the result and decrement the count.

### Walkthrough: `nums1 = [1,2,2,1], nums2 = [2,2]`

```
freqMap = {1:2, 2:2}

num=2: count=2 > 0 → add 2, freqMap={1:2, 2:1}
num=2: count=1 > 0 → add 2, freqMap={1:2, 2:0}

Result: [2,2] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N + M) |
| **Space** | O(N) — for the frequency map |

---

## 🔑 Key Takeaways

1. **HashMap tracks remaining count:** Each match decrements the count, preventing over-counting.
2. **Handles duplicates correctly:** Unlike Set-based intersection, this preserves duplicate matches.
3. **Alternative (Sort + Two Pointer):** Sort both arrays, then use two pointers to collect matches — O(N log N) time, O(1) space.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Intersection of Two Arrays II | [#350](https://leetcode.com/problems/intersection-of-two-arrays-ii/) | Easy |
| Intersection of Two Arrays | [#349](https://leetcode.com/problems/intersection-of-two-arrays/) | Easy |
| Merge Sorted Array | [#88](https://leetcode.com/problems/merge-sorted-array/) | Easy |
