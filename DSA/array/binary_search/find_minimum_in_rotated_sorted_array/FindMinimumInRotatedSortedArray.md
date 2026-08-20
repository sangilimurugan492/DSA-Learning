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

---

## 🔍 Huge 10-Element Array Walkthrough

To make the binary search crystal clear, let's trace through a **10-element array**.

### Setup

- **Original sorted array:** `[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]`
- **Rotated at pivot 5:** `[6, 7, 8, 9, 10, 1, 2, 3, 4, 5]`
- **Expected minimum:** `1` (at index 5)

```
Index:   0   1   2   3   4   5   6   7   8   9
Array: [ 6,  7,  8,  9, 10,  1,  2,  3,  4,  5 ]
         ←─── sorted (larger) ──→ ←── sorted (smaller) ─→
                              ↑
                         rotation point (minimum)
```

### Step-by-Step Binary Search

#### STEP 1: `left=0, right=9`

- `mid = 0 + (9 - 0) / 2 = 4` → `nums[4] = 10`
- Compare: `nums[mid]=10` vs `nums[right]=5`
- `10 > 5` → Left half `[6,7,8,9,10]` is sorted (all larger values)
- **Decision:** Minimum is in the **right half** → `left = mid + 1 = 5`

```
[ 6,  7,  8,  9, 10,  1,  2,  3,  4,  5 ]
  L              M                    R
 ←── sorted ──→  ←── min is here ──→
                 ↑ new left
```

#### STEP 2: `left=5, right=9`

- `mid = 5 + (9 - 5) / 2 = 7` → `nums[7] = 3`
- Compare: `nums[mid]=3` vs `nums[right]=5`
- `3 ≤ 5` → Right half `[3,4,5]` is sorted
- **Decision:** Minimum is at mid or in **left half** → `right = mid = 7`

```
[ 6,  7,  8,  9, 10,  1,  2,  3,  4,  5 ]
                    L        M        R
              ←── min ──→  ←─ sorted ─→
                          ↑ new right
```

#### STEP 3: `left=5, right=7`

- `mid = 5 + (7 - 5) / 2 = 6` → `nums[6] = 2`
- Compare: `nums[mid]=2` vs `nums[right]=3`
- `2 ≤ 3` → Right half `[2,3]` is sorted
- **Decision:** Minimum is at mid or in **left half** → `right = mid = 6`

```
[ 6,  7,  8,  9, 10,  1,  2,  3,  4,  5 ]
                    L     M     R
                 ←─ min ─→←sorted→
                        ↑ new right
```

#### STEP 4: `left=5, right=6`

- `mid = 5 + (6 - 5) / 2 = 5` → `nums[5] = 1`
- Compare: `nums[mid]=1` vs `nums[right]=2`
- `1 ≤ 2` → Right half `[1,2]` is sorted
- **Decision:** Minimum is at mid or in **left half** → `right = mid = 5`

```
[ 6,  7,  8,  9, 10,  1,  2,  3,  4,  5 ]
                    L/R
                    M
                    ↑ new right (right = mid = 5)
```

#### STEP 5: `left=5, right=5` → Loop ends (`left == right`)

- **Return `nums[5] = 1`** ✅

### Summary Table

| Step | left | right | mid | nums[mid] | nums[right] | Comparison | Action |
|:----:|:----:|:-----:|:---:|:---------:|:-----------:|:----------:|:------:|
| 1 | 0 | 9 | 4 | 10 | 5 | 10 > 5 | left = 5 |
| 2 | 5 | 9 | 7 | 3 | 5 | 3 ≤ 5 | right = 7 |
| 3 | 5 | 7 | 6 | 2 | 3 | 2 ≤ 3 | right = 6 |
| 4 | 5 | 6 | 5 | 1 | 2 | 1 ≤ 2 | right = 5 |
| 5 | 5 | 5 | — | — | — | left == right | return nums[5] = **1** ✅ |

> **Key observation:** Only **4 iterations** to find the minimum in a 10-element array. That's O(log N) — binary search cuts the search space in half each time!


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
