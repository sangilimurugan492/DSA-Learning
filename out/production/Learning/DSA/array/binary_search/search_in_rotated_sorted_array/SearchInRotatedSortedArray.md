# Search in Rotated Sorted Array — Detailed Explanation

> **LeetCode #33** | [Problem Link](https://leetcode.com/problems/search-in-rotated-sorted-array/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta, Apple — must-know)  
> **Topic:** Binary Search

---

## 📋 Problem Statement

A sorted array (distinct values) is rotated at an unknown pivot. Given a target, return its index, or -1. Must run in O(log N) time.

### Examples

| nums | target | Output |
|------|--------|--------|
| `[4,5,6,7,0,1,2]` | 0 | 4 |
| `[4,5,6,7,0,1,2]` | 3 | -1 |
| `[1]` | 0 | -1 |

---

## 🧩 Method 1: Linear Scan — O(N)

### Core Idea

Iterate through array, return index when target found.

### Step-by-Step

1. Loop through each index i.
2. If `nums[i] == target` → return i.
3. If loop ends → return -1.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | No extra space |

---

## 🧩 Method 2: Binary Search — O(log N)

### Core Idea

At any mid point, one half (left or right) is always sorted. Identify the sorted half, check if target lies in it. If yes, search that half; otherwise search the other half.

### Key Insight

> In a rotated sorted array, one half is ALWAYS sorted. By identifying the sorted half, we can check if target lies in it. This eliminates half the search space each iteration → O(log N).

### Step-by-Step

1. Initialize `left = 0`, `right = n-1`.
2. While `left <= right`:
   - Compute `mid = left + (right - left) / 2`.
   - If `nums[mid] == target` → return mid.
   - **If left half is sorted** (`nums[left] <= nums[mid]`):
     - If `target >= nums[left] && target < nums[mid]` → search left: `right = mid - 1`.
     - Else → search right: `left = mid + 1`.
   - **Else right half is sorted**:
     - If `target > nums[mid] && target <= nums[right]` → search right: `left = mid + 1`.
     - Else → search left: `right = mid - 1`.
3. Return -1.

### Dry Run — `nums=[4,5,6,7,0,1,2], target=0`

| left | right | mid | nums[mid] | Sorted half? | Target in sorted? | Action |
|:----:|:-----:|:---:|:---------:|:------------:|:-----------------:|:------:|
| 0 | 6 | 3 | 7 | Left (4≤7) | 0 in [4,7)? No | left = 4 |
| 4 | 6 | 5 | 1 | Left (0≤1) | 0 in [0,1)? Yes | right = 4 |
| 4 | 4 | 4 | 0 | — | nums[4]==0 == target | return **4** ✅ |

### Dry Run — `nums=[4,5,6,7,0,1,2], target=3`

| left | right | mid | nums[mid] | Sorted half? | Target in sorted? | Action |
|:----:|:-----:|:---:|:---------:|:------------:|:-----------------:|:------:|
| 0 | 6 | 3 | 7 | Left (4≤7) | 3 in [4,7)? No | left = 4 |
| 4 | 6 | 5 | 1 | Left (0≤1) | 3 in [0,1)? No | left = 6 |
| 6 | 6 | 6 | 2 | Left (2≤2) | 3 in [2,2)? No | left = 7 |
| 7 | 6 | — | — | left > right | — | return **-1** ✅ |

### Code

```kotlin
fun searchRotated(nums: IntArray, target: Int): Int {
    var left = 0
    var right = nums.size - 1
    while (left <= right) {
        val mid = left + (right - left) / 2
        if (nums[mid] == target) return mid
        if (nums[left] <= nums[mid]) {  // Left half sorted
            if (target >= nums[left] && target < nums[mid]) right = mid - 1
            else left = mid + 1
        } else {  // Right half sorted
            if (target > nums[mid] && target <= nums[right]) left = mid + 1
            else right = mid - 1
        }
    }
    return -1
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

1. **One half is always sorted:** In a rotated sorted array, at any mid, either left or right half is sorted.
2. **Check sorted half first:** Always check if target is in the sorted half — it's the only half where we can do a range check.
3. **`nums[left] <= nums[mid]` with `<=`:** Use `<=` (not `<`) to handle the case where left == mid (2 elements).
4. **Pattern:** Rotated sorted array binary search — extends to Search II (with duplicates), Find Minimum.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Search in Rotated Sorted Array | [#33](https://leetcode.com/problems/search-in-rotated-sorted-array/) | Medium |
| Search in Rotated Sorted Array II | [#81](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/) | Medium |
| Find Minimum in Rotated Sorted Array | [#153](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | Medium |
