# Count Number of Nice Subarrays — Detailed Explanation

> **LeetCode #1248** | [Problem Link](https://leetcode.com/problems/count-number-of-nice-subarrays/)  
> **Topic:** Sliding Window / atMost Pattern  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array `nums` and integer `k`, count subarrays with **exactly** `k` odd numbers.

### Example

```
Input: nums = [1,1,2,1,1], k = 3
Output: 2  ([1,1,2,1] and [1,2,1,1])
```

---

## 🧩 Method 1: Brute Force (Nested Loops)

### Core Idea

For each starting index `i`, expand `right` and count odd numbers. If odd count equals `k`, increment the result. If odd count exceeds `k`, stop expanding (odds only increase).

### Walkthrough: `nums = [1,1,2,1,1], k = 3`

```
i=0: expand right → odds: 1,2,2,3 → [1,1,2,1] count++ → odds: 4 > 3 stop
i=1: expand right → odds: 1,1,2,3 → [1,2,1,1] count++ → stop
i=2: expand right → odds: 0,1,2 → never reaches 3
i=3: expand right → odds: 1,2 → never reaches 3
i=4: expand right → odds: 1 → never reaches 3

Result: 2 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — nested loops for each starting index |
| **Space** | O(1) — only a counter |

---

## 🧩 Method 2: atMost(K) − atMost(K−1) (Optimal)

### Core Idea

Counting "exactly k" directly is hard. Instead use:
> **exactly(k) = atMost(k) − atMost(k−1)**

`atMost(k)` counts subarrays with **at most** k odd numbers using a sliding window:
- Expand `right`, count odds.
- When odds > k, shrink `left`.
- Subarrays ending at `right` = `right − left + 1`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — two passes of atMost |
| **Space** | O(1) |

---

## 📊 Comparison

| Method | Time | Space |
|--------|------|-------|
| Brute Force | O(N²) | O(1) |
| atMost Pattern | O(N) | O(1) |

---

## 🔑 Key Takeaways

1. **Brute force is intuitive:** Just expand from each starting index and count odds — simple but O(N²).
2. **atMost pattern:** `exactly(k) = atMost(k) − atMost(k−1)` — converts "exact count" to "at most count" which is easier with sliding window.
3. **Subarrays ending at right:** Each position contributes `right − left + 1` subarrays.
4. **Same pattern** as Subarrays with K Different Integers.


---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Count Number of Nice Subarrays | [#1248](https://leetcode.com/problems/count-number-of-nice-subarrays/) | Medium |
| Subarrays with K Different Integers | [#992](https://leetcode.com/problems/subarrays-with-k-different-integers/) | Hard |
| Binary Subarrays With Sum | [#930](https://leetcode.com/problems/binary-subarrays-with-sum/) | Medium |
