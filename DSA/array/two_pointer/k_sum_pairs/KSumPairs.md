# K-Sum Pairs — Detailed Explanation

> **LeetCode #1679** | [Problem Link](https://leetcode.com/problems/k-sum-pairs/)  
> **Topic:** Two Pointers / Sorting  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array `nums` and integer `k`, return the maximum number of operations where each operation picks two numbers whose sum equals `k` and removes them.

### Example

```
Input: nums = [1,2,3,4], k = 5
Output: 2  ((1,4) and (2,3))
```

---

## 🧩 Method 1: Brute Force (Nested Loops)

### Core Idea

For each unpaired element, scan the rest of the array forward for a partner that sums to `k`. Mark both as used when a pair is found.

### Step-by-Step

1. Create a `used` boolean array to track which elements are already paired.
2. For each element `nums[i]` (if not already used):
   - Scan forward through `nums[j]` (j > i, not used).
   - If `nums[i] + nums[j] == k` → pair found! Mark both as used, increment count, break.
   - If no partner found, skip (nums[i] goes unpaired).
3. Return total pairs found.

### Walkthrough: `nums = [1,2,3,4], k = 5`

```
i=0, nums[0]=1: scan j=1(2)→3, j=2(3)→4, j=3(4)→1+4=5 ✅ pair! used=[T,F,F,T], count=1
i=1, nums[1]=2: not used, scan j=2(3)→2+3=5 ✅ pair! used=[T,T,T,T], count=2
i=2: already used → skip
i=3: already used → skip

Result: 2 ✅
```

### Walkthrough: `nums = [3,1,3,4,3], k = 6`

```
i=0, nums[0]=3: scan j=1(1)→4, j=2(3)→6 ✅ pair! used=[T,F,T,F,F], count=1
i=1, nums[1]=1: not used, scan j=3(4)→5, j=4(3)→4 → no pair found
i=2: already used → skip
i=3, nums[3]=4: not used, scan j=4(3)→7 → no pair found
i=4: no more elements to scan

Result: 1 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — nested loops |
| **Space** | O(N) — used array |

---

## 🧩 Method 2: Sort + Two Pointer (Optimal)

### Core Idea

Sort the array, then use two pointers from both ends:
- `sum == k` → count++, move both pointers
- `sum < k` → `left++` (need bigger)
- `sum > k` → `right--` (need smaller)

### Step-by-Step

1. Sort the array.
2. Set `left = 0`, `right = last index`.
3. While `left < right`:
   - `sum = nums[left] + nums[right]`
   - If `sum == k` → pair found! `operations++`, `left++`, `right--`
   - If `sum < k` → need bigger → `left++`
   - If `sum > k` → need smaller → `right--`
4. Return operations.

### Walkthrough: `nums = [3,1,3,4,3], k = 6`

```
Sorted: [1,3,3,3,4]
left=0, right=4: 1+4=5 < 6 → left++
left=1, right=4: 3+4=7 > 6 → right--
left=1, right=3: 3+3=6 == 6 → pair! ops=1, left=2, right=2
left=2, right=2: left >= right → stop

Result: 1 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting |
| **Space** | O(1) |

---

## 📊 Comparison

| Method | Time | Space |
|--------|------|-------|
| Brute Force | O(N²) | O(N) |
| Sort + Two Pointer | O(N log N) | O(1) |

---

## 🔑 Key Takeaways

1. **Brute force:** For each element, scan forward for a partner — intuitive but O(N²).
2. **Sort enables two-pointer:** After sorting, pair smallest with largest.
3. **Greedy pairing:** If sum < k, we need a bigger number (move left). If sum > k, we need smaller (move right).
4. **Alternative:** HashMap approach also works in O(N) time, O(N) space.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| K-Sum Pairs | [#1679](https://leetcode.com/problems/k-sum-pairs/) | Medium |
| Two Sum | [#1](https://leetcode.com/problems/two-sum/) | Easy |
| Two Sum II | [#167](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium |
