# Find Minimum in Rotated Sorted Array — Detailed Explanation

> **LeetCode #153** | [Problem Link](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)  
> **Topic:** Binary Search

---

## 📋 Problem Statement

A sorted array is rotated at an unknown pivot. Find the minimum element in O(log N) time.

### Examples

| nums | Output | Explanation |
|------|--------|-------------|
| `[3,4,5,1,2]` | 1 | Rotated at pivot 3 |
| `[4,5,6,7,0,1,2]` | 0 | Rotated at pivot 4 |
| `[11,13,15,17]` | 11 | Not rotated |

---

## 🧩 Method 1: Linear Scan — O(N)

### Core Idea

Scan entire array, track the minimum value seen.

### Step-by-Step

1. Initialize `min = nums[0]`.
2. Iterate through each element.
3. If current element < min → update min.
4. Return min.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | One variable |

---

## 🧩 Method 2: Binary Search — O(log N)

### Core Idea

Compare `nums[mid]` with `nums[right]` to determine which half is unsorted. The minimum is always in the unsorted half.

### Key Insight

> In a rotated sorted array, the minimum is in the unsorted half. If `nums[mid] > nums[right]`, the left half is sorted → minimum is in right half. If `nums[mid] <= nums[right]`, the right half is sorted → minimum is in left half (including mid).

### Step-by-Step

1. Initialize `left = 0`, `right = n-1`.
2. While `left < right`:
   - Compute `mid = left + (right - left) / 2`.
   - If `nums[mid] > nums[right]` → left half is sorted → `left = mid + 1`.
   - Else → right half is sorted → `right = mid` (mid could be the minimum).
3. Return `nums[left]`.

### Dry Run — `[4,5,6,7,0,1,2]`

| left | right | mid | nums[mid] | nums[right] | Comparison | Action |
|:----:|:-----:|:---:|:---------:|:-----------:|:----------:|:------:|
| 0 | 6 | 3 | 7 | 2 | 7 > 2 | left = 4 |
| 4 | 6 | 5 | 1 | 2 | 1 ≤ 2 | right = 5 |
| 4 | 5 | 4 | 0 | 1 | 0 ≤ 1 | right = 4 |
| 4 | 4 | — | — | — | left == right | return nums[4] = **0** ✅ |

### Code

```kotlin
fun findMin(nums: IntArray): Int {
    var left = 0
    var right = nums.size - 1
    while (left < right) {
        val mid = left + (right - left) / 2
        if (nums[mid] > nums[right]) {
            left = mid + 1  // Min is in right half
        } else {
            right = mid     // Min is at mid or left half
        }
    }
    return nums[left]
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

1. **Compare mid with right:** This tells us which half is sorted and which contains the minimum.
2. **Why not compare with left?** Comparing with right is cleaner — if `nums[mid] <= nums[right]`, the right half is sorted, so the minimum is at mid or to its left.
3. **`right = mid` not `mid - 1`:** When right half is sorted, mid could be the minimum, so we include it.
4. **Pattern:** Rotated sorted array binary search — extends to Search in Rotated Sorted Array, Find Minimum II.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Find Minimum in Rotated Sorted Array | [#153](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | Medium |
| Find Minimum II (with duplicates) | [#154](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array-ii/) | Hard |
| Search in Rotated Sorted Array | [#33](https://leetcode.com/problems/search-in-rotated-sorted-array/) | Medium |
