# Subarrays with K Different Integers — Detailed Explanation

> **LeetCode #992** | [Problem Link](https://leetcode.com/problems/subarrays-with-k-different-integers/description/)  
> **Topic:** Sliding Window / atMost Pattern  
> **Difficulty:** Hard

---

## 📋 Problem Statement

Given an array `nums` and integer `k`, return the number of subarrays with **exactly** `k` different integers.

### Example

```
Input: nums = [1,2,1,2,3], k = 2
Output: 7
```

---

## 🧩 Method 1: Brute Force

### Core Idea

For each starting index, expand right and track distinct count in a Set. Count when distinct == k, break when > k.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(k) |

---

## 🧩 Method 2: atMost(K) − atMost(K−1) (Optimal)

### Core Idea

Counting "exactly k" directly is hard. Instead use:
> **exactly(k) = atMost(k) − atMost(k−1)**

`atMost(k)` counts subarrays with **at most** k distinct integers using a sliding window:
- Expand `right`, track frequency in an array.
- When distinct count > k, shrink `left`.
- Subarrays ending at `right` = `right − left + 1`.

### Key Insight

> `atMost(k)` is easy with sliding window. The difference gives exactly k.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — two passes of atMost |
| **Space** | O(N) — frequency array |

---

## 🔑 Key Takeaways

1. **atMost pattern:** `exactly(k) = atMost(k) − atMost(k−1)` — converts "exact count" to "at most count."
2. **Subarrays ending at right:** Each position contributes `right − left + 1` subarrays.
3. **Same pattern** as Count Number of Nice Subarrays.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Subarrays with K Different Integers | [#992](https://leetcode.com/problems/subarrays-with-k-different-integers/) | Hard |
| Count Number of Nice Subarrays | [#1248](https://leetcode.com/problems/count-number-of-nice-subarrays/) | Medium |
| Longest Substring with At Most K Distinct Characters | [#340](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/) | Medium |
