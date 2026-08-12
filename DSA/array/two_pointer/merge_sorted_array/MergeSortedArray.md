# Merge Sorted Array — Detailed Explanation

> **LeetCode #88** | [Problem Link](https://leetcode.com/problems/merge-sorted-array/)  
> **Topic:** Two Pointers  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given two sorted arrays `nums1` (with `m` elements + extra space) and `nums2` (with `n` elements), merge them in-place into `nums1` as one sorted array.

### Example

```
Input:  nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
Output: [1,2,2,3,5,6]
```

---

## 🧩 Method: Two Pointers from the End

### Core Idea

Merge from the **back** of `nums1` to avoid overwriting elements. Use three pointers:
- `i` = last real element in `nums1` (m-1)
- `j` = last element in `nums2` (n-1)
- `k` = last position in `nums1` (m+n-1)

Compare `nums1[i]` and `nums2[j]`, place the larger one at `nums1[k]`, and move pointers.

### Walkthrough: `nums1 = [1,2,3,0,0,0], nums2 = [2,5,6]`

```
i=2 (3), j=2 (6): 6 > 3 → nums1[5]=6, j=1, k=4
i=2 (3), j=1 (5): 5 > 3 → nums1[4]=5, j=0, k=3
i=2 (3), j=0 (2): 3 > 2 → nums1[3]=3, i=1, k=2
i=1 (2), j=0 (2): 2 == 2 → nums1[2]=2, j=-1, k=1
j < 0 → stop (nums1 already has remaining elements in place)

Result: [1,2,2,3,5,6] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(M + N) — single pass |
| **Space** | O(1) — in-place |

---

## 🔑 Key Takeaways

1. **Merge from the back:** The extra space is at the end of `nums1`, so filling from the back avoids overwriting.
2. **Three pointers:** `i` for nums1, `j` for nums2, `k` for the write position.
3. **Handle remaining:** If `j` reaches -1 first, remaining nums1 elements are already in place. If `i` reaches -1 first, copy remaining nums2 elements.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Merge Sorted Array | [#88](https://leetcode.com/problems/merge-sorted-array/) | Easy |
| Merge Two Sorted Lists | [#21](https://leetcode.com/problems/merge-two-sorted-lists/) | Easy |
| Intersection of Two Arrays II | [#350](https://leetcode.com/problems/intersection-of-two-arrays-ii/) | Easy |
