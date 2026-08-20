# Search Insert Position — Detailed Explanation

> **LeetCode #35** | [Problem Link](https://leetcode.com/problems/search-insert-position/)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Classic binary search — asked at Amazon, Apple)  
> **Topic:** Binary Search

---

## 📋 Problem Statement

Given a sorted array of distinct integers and a target value, return the index of target if found, or the index where it would be inserted to maintain sorted order. Must run in O(log N) time.

### Examples

| nums | target | Output | Explanation |
|------|--------|--------|-------------|
| `[1,3,5,6]` | 5 | 2 | Found at index 2 |
| `[1,3,5,6]` | 2 | 1 | Insert at index 1 |
| `[1,3,5,6]` | 7 | 4 | Insert at end |
| `[1,3,5,6]` | 0 | 0 | Insert at start |

---

## 🧩 Method 1: Linear Scan — O(N)

### Core Idea

Walk through the array left to right. Return the index when we find target or a value greater than target.

### Step-by-Step

1. Loop through each index `i`.
2. If `nums[i] >= target` → return `i` (found or insertion point).
3. If loop ends → return `n` (insert at end).

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | No extra space |

---

## 🧩 Method 2: Binary Search — O(log N)

### Core Idea

Standard binary search. If target is found, return its index. If not found, the `left` pointer when the loop exits is exactly the insertion position.

### Key Insight

> When the loop exits without finding target, `left` points to the first element greater than target — exactly where target should be inserted. This works because `left` only moves past elements smaller than target, and `right` only moves past elements greater than target.

### Step-by-Step

1. Initialize `left = 0`, `right = n-1`.
2. While `left <= right`:
   - Compute `mid = left + (right - left) / 2`.
   - If `nums[mid] == target` → return `mid`.
   - If `nums[mid] < target` → `left = mid + 1`.
   - Else → `right = mid - 1`.
3. Return `left` (insertion point).

---

## 🔍 Huge 10-Element Array Walkthrough

### Setup

- **Array:** `[1, 3, 5, 7, 9, 11, 13, 15, 17, 19]` (10 elements)
- **Target = 11** → found at index 5
- **Target = 8** → not found, insert at index 4

### Dry Run — `target=11` (found)

| Step | left | right | mid | nums[mid] | Comparison | Action |
|:----:|:----:|:-----:|:---:|:---------:|:----------:|:------:|
| 1 | 0 | 9 | 4 | 9 | 9 < 11 | left = 5 |
| 2 | 5 | 9 | 7 | 15 | 15 > 11 | right = 6 |
| 3 | 5 | 6 | 5 | 11 | 11 == 11 | return **5** ✅ |

```
[ 1,  3,  5,  7,  9, 11, 13, 15, 17, 19]
  L              M                    R        → 9 < 11 → left = 5
                 L        M           R        → 15 > 11 → right = 6
                 L  M              (5==11)     → FOUND! return 5 ✅
```

### Dry Run — `target=8` (not found)

| Step | left | right | mid | nums[mid] | Comparison | Action |
|:----:|:----:|:-----:|:---:|:---------:|:----------:|:------:|
| 1 | 0 | 9 | 4 | 9 | 9 > 8 | right = 3 |
| 2 | 0 | 3 | 1 | 3 | 3 < 8 | left = 2 |
| 3 | 2 | 3 | 2 | 5 | 5 < 8 | left = 3 |
| 4 | 3 | 3 | 3 | 7 | 7 < 8 | left = 4 |
| 5 | 4 | 3 | — | — | left > right | return **4** ✅ |

```
[ 1,  3,  5,  7,  9, 11, 13, 15, 17, 19]
  L              M                    R        → 9 > 8 → right = 3
  L     M        R                             → 3 < 8 → left = 2
        L  M     R                             → 5 < 8 → left = 3
           L=M   R                             → 7 < 8 → left = 4
           R     L                              → left > right → return 4 ✅
```

> **Key observation:** Only **3-4 iterations** for a 10-element array. When target isn't found, `left` naturally lands on the insertion point.

### Code

```kotlin
fun searchInsert(nums: IntArray, target: Int): Int {
    var left = 0
    var right = nums.size - 1
    while (left <= right) {
        val mid = left + (right - left) / 2
        if (nums[mid] == target) return mid
        if (nums[mid] < target) left = mid + 1
        else right = mid - 1
    }
    return left
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(log N) | Binary search |
| **Space** | O(1) | Two pointers |

---

## 📊 Comparison Table

| Aspect | Linear Scan | Binary Search |
|--------|-------------|---------------|
| **Time** | O(N) | O(log N) |
| **Space** | O(1) | O(1) |
| **Meets O(log N) requirement?** | ❌ | ✅ |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **`left` is the insertion point:** When the loop exits, `left` is exactly where target should be inserted — no extra logic needed.
2. **Standard binary search:** This is the most fundamental binary search pattern — master it before moving to rotated arrays or answer-space binary search.
3. **`left <= right` (not `<`):** Using `<=` ensures we check every element, including when `left == right`.
4. **Pattern:** This is the foundation for all binary search problems — understand this before tackling harder variants.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Search Insert Position | [#35](https://leetcode.com/problems/search-insert-position/) | Easy |
| Binary Search | [#704](https://leetcode.com/problems/binary-search/) | Easy |
| Search in Rotated Sorted Array | [#33](https://leetcode.com/problems/search-in-rotated-sorted-array/) | Medium |
| Find First and Last Position | [#34](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) | Medium |
