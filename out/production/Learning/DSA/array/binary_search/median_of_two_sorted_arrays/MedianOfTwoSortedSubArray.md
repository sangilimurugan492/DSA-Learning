# Median of Two Sorted Arrays — Detailed Explanation

> **LeetCode #4** | [Problem Link](https://leetcode.com/problems/median-of-two-sorted-arrays/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Hard — Asked at Google, Meta, Amazon)  
> **Topic:** Binary Search

---

## 📋 Problem Statement

Given two sorted arrays nums1 and nums2, find the median in O(log(m+n)) time.

### Examples

| nums1 | nums2 | Output | Merged |
|-------|-------|--------|--------|
| `[1,2]` | `[3,4]` | 2.5 | `[1,2,3,4]` → (2+3)/2 |
| `[1,3]` | `[2]` | 2.0 | `[1,2,3]` → 2 |

---

## 🧩 Method 1: Merge + Sort — O((m+n) log(m+n))

### Core Idea

Merge both arrays into one, sort, find the middle element(s).

### Step-by-Step

1. Create merged array of size m+n.
2. Copy nums1 and nums2 into merged.
3. Sort merged array.
4. If length is odd → return middle element.
5. If length is even → return average of two middle elements.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O((m+n) log(m+n)) | Sorting dominates |
| **Space** | O(m+n) | Merged array |

---

## 🧩 Method 2: Binary Search Partition — O(log(min(m,n)))

### Core Idea

Binary search on the smaller array to find a partition point where left halves of both arrays combined equal right halves. All elements in left partition ≤ all elements in right partition.

### Key Insight

> If we partition both arrays at the right point: `max(left side) ≤ min(right side)`. For even total: median = (max(left) + min(right)) / 2. For odd total: median = max(left).

### Step-by-Step

1. Ensure nums1 is the smaller array (swap if needed).
2. Binary search on nums1: `low = 0`, `high = m`.
3. At each step:
   - `partitionX = (low + high) / 2` (elements from nums1 in left half)
   - `partitionY = (m + n + 1) / 2 - partitionX` (elements from nums2 in left half)
   - `maxLeftX` = last element of nums1's left partition (or MIN_VALUE if empty)
   - `minRightX` = first element of nums1's right partition (or MAX_VALUE if empty)
   - `maxLeftY` = last element of nums2's left partition (or MIN_VALUE if empty)
   - `minRightY` = first element of nums2's right partition (or MAX_VALUE if empty)
4. Check valid partition: `maxLeftX <= minRightY && maxLeftY <= minRightX`
   - If valid → compute median.
   - If `maxLeftX > minRightY` → too many from nums1 → `high = partitionX - 1`
   - Else → too few from nums1 → `low = partitionX + 1`

### Dry Run — `nums1=[1,2], nums2=[3,4]` (m=2, n=2, total=4, even)

| low | high | partX | partY | maxLeftX | minRightX | maxLeftY | minRightY | Valid? |
|:---:|:-----:|:-----:|:-----:|:---------:|:---------:|:---------:|:---------:|:------:|
| 0 | 2 | 1 | 1 | 1 | 2 | 3 | 4 | 1≤4 ✅ but 3≤2 ❌ |
| | | | | | | | | maxLeftY > minRightX → low = 2 |
| 2 | 2 | 2 | 0 | 2 | MAX | MIN | 3 | 2≤3 ✅ && MIN≤MAX ✅ |

Valid partition! Total is even → median = (max(2, MIN) + min(MAX, 3)) / 2 = (2+3)/2 = **2.5** ✅

### Code

```kotlin
fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
    val (a, b) = if (nums1.size <= nums2.size) nums1 to nums2 else nums2 to nums1
    val m = a.size; val n = b.size
    var low = 0; var high = m
    while (low <= high) {
        val partitionX = (low + high) / 2
        val partitionY = (m + n + 1) / 2 - partitionX
        val maxLeftX = if (partitionX == 0) Int.MIN_VALUE else a[partitionX - 1]
        val minRightX = if (partitionX == m) Int.MAX_VALUE else a[partitionX]
        val maxLeftY = if (partitionY == 0) Int.MIN_VALUE else b[partitionY - 1]
        val minRightY = if (partitionY == n) Int.MAX_VALUE else b[partitionY]
        if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
            return if ((m + n) % 2 == 0)
                (maxOf(maxLeftX, maxLeftY) + minOf(minRightX, minRightY)) / 2.0
            else maxOf(maxLeftX, maxLeftY).toDouble()
        } else if (maxLeftX > minRightY) high = partitionX - 1
        else low = partitionX + 1
    }
    throw IllegalArgumentException()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(log(min(m,n))) | Binary search on smaller array |
| **Space** | O(1) | Constant variables |

---

## 📊 Comparison Table

| Aspect | Merge + Sort | Binary Search Partition |
|--------|-------------|--------------------------|
| **Time** | O((m+n) log(m+n)) | O(log(min(m,n))) |
| **Space** | O(m+n) | O(1) |
| **Meets O(log(m+n))?** | ❌ | ✅ |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Binary search on smaller array:** Always search the smaller array for efficiency → O(log(min(m,n))).
2. **Partition logic:** Left half has (m+n+1)/2 elements total. partitionY = total_left - partitionX.
3. **Valid partition:** maxLeftX ≤ minRightY AND maxLeftY ≤ minRightX.
4. **Edge cases:** Use MIN_VALUE/MAX_VALUE for empty partitions (partition at 0 or at end).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Median of Two Sorted Arrays | [#4](https://leetcode.com/problems/median-of-two-sorted-arrays/) | Hard |
| Kth Element of Two Sorted Arrays | — | Medium |
| Find Minimum in Rotated Sorted Array | [#153](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | Medium |
