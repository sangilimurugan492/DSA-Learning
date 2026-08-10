# Remove Element — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/remove-element/  
> **Topic:** Array, Two Pointers (In-Place)  
> **Difficulty:** Easy

---

## 📋 Problem Statement

Given an integer array `nums` and an integer `val`, remove **all occurrences** of `val`
in `nums` **in-place**. The order of elements may be changed. Return the number of elements
in `nums` which are not equal to `val`.

The first `k` elements of `nums` should hold the elements not equal to `val`. The remaining
elements beyond `k` are ignored.

### Constraints

- `0 <= nums.length <= 100`
- `0 <= nums[i] <= 50`
- `0 <= val <= 100`

### Examples

**Example 1:**

```
Input:  nums = [3, 2, 2, 3], val = 3
Output: 2, nums = [2, 2, _, _]

Remove both 3s → first 2 elements are [2, 2], k = 2
```

**Example 2:**

```
Input:  nums = [0, 1, 2, 2, 3, 0, 4, 2], val = 2
Output: 5, nums = [0, 1, 4, 0, 3, _, _, _]

Remove all 2s → first 5 elements are [0, 1, 4, 0, 3], k = 5
```

---

## 🧩 Method 1: Brute Force — Shift on Every Match

### Core Idea

When we find `nums[i] == val`, shift all elements to the right of `i` one position left,
then decrease the logical size. Don't increment `i` (the new element at position `i` needs
checking). Repeat until the end.

### Step-by-step Walkthrough (Example 1)

```
nums = [3, 2, 2, 3], val = 3, size = 4

i=0: nums[0]=3 == val → shift left → [2, 2, 3, 3], size=3
i=0: nums[0]=2 != val → i=1
i=1: nums[1]=2 != val → i=2
i=2: nums[2]=3 == val → shift left → [2, 2, 3, 3], size=2
i=2: i >= size → stop

Result: k = 2, nums = [2, 2, _, _]
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — each removal shifts up to N elements |
| **Space** | O(1) — in-place |

---

## 🧩 Method 2: Optimal — Two Pointers (Write Pointer)

### Core Idea

Instead of shifting on every match, use a **write pointer** `k` that tracks where the next
non-`val` element should go. Iterate with a **read pointer** `i`:
- If `nums[i] != val`, copy `nums[i]` to `nums[k]` and increment `k`.
- If `nums[i] == val`, skip it.

After one pass, the first `k` elements are all non-`val` elements.

### Algorithm Steps

1. Initialize `k = 0` (write pointer).
2. For each `i` from `0` to `n-1`:
   - If `nums[i] != val`: `nums[k] = nums[i]`, then `k++`.
3. Return `k`.

### Step-by-step Walkthrough (Example 1)

```
nums = [3, 2, 2, 3], val = 3
k = 0

i=0: nums[0]=3 == val → skip
i=1: nums[1]=2 != val → nums[0]=2, k=1  → [2, 2, 2, 3]
i=2: nums[2]=2 != val → nums[1]=2, k=2  → [2, 2, 2, 3]
i=3: nums[3]=3 == val → skip

Result: k = 2, nums = [2, 2, 2, 3] (first 2 elements valid) ✅
```

### Why does this work?

- `k` always points to the next position where a valid (non-`val`) element should be written.
- We never lose data because `k <= i` always — the write pointer never overtakes the read
  pointer.
- Elements equal to `val` are simply overwritten by subsequent non-`val` elements.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass through the array |
| **Space** | O(1) — in-place |

### Why is this better than brute force?

| Aspect | Brute Force (Shift) | Optimal (Write Pointer) |
|--------|---------------------|-------------------------|
| Time | O(N²) | O(N) |
| Space | O(1) | O(1) |
| Key idea | Shift elements on every match | Overwrite in single pass |

---

## 🔑 Key Takeaways

1. **Write pointer pattern**: use a slow pointer (`k`) to track where to write valid
   elements, and a fast pointer (`i`) to scan all elements.
2. This is a foundational in-place array technique used in many problems.
3. No shifting needed — just overwrite. `k <= i` guarantees no data loss.
4. The order of non-`val` elements is preserved (stable).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Remove Element | [link](https://leetcode.com/problems/remove-element/) | Easy |
| Move Zeroes | [link](https://leetcode.com/problems/move-zeroes/) | Easy |
| Remove Duplicates from Sorted Array | [link](https://leetcode.com/problems/remove-duplicates-from-sorted-array/) | Easy |
| Remove Duplicates from Sorted Array II | [link](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/) | Medium |
