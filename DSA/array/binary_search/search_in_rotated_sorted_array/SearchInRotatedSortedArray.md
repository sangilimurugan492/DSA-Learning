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

---

## 🔍 Huge 10-Element Array Walkthrough

To make the binary search crystal clear, let's trace through a **10-element array**.

### Setup

- **Original sorted array:** `[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]`
- **Rotated at pivot 6:** `[7, 8, 9, 10, 1, 2, 3, 4, 5, 6]`
- **Target = 3** → found at index 6
- **Target = 11** → not found (-1)

```
Index:   0   1   2   3   4   5   6   7   8   9
Array: [ 7,  8,  9, 10,  1,  2,  3,  4,  5,  6 ]
         ←─── sorted (larger) ──→ ←── sorted (smaller) ─→
                              ↑
                         rotation point
```

### Dry Run — `target=3` (found at index 6)

#### STEP 1: `left=0, right=9, mid=4` → `nums[4]=1`

- `nums[0]=7 <= nums[4]=1`? **No** → right half is sorted
- Is 3 in `(1, 6]`? **Yes** → search right → `left = 5`

```
[ 7,  8,  9, 10,  1,  2,  3,  4,  5,  6 ]
  L              M                    R
 ←── unsorted ──→  ←── sorted (1-6) ─→
                   ↑ new left
```

#### STEP 2: `left=5, right=9, mid=7` → `nums[7]=4`

- `nums[5]=2 <= nums[7]=4`? **Yes** → left half is sorted
- Is 3 in `[2, 4)`? **Yes** → search left → `right = 6`

```
[ 7,  8,  9, 10,  1,  2,  3,  4,  5,  6 ]
                         L        M     R
                      ←─ sorted ─→
                               ↑ new right
```

#### STEP 3: `left=5, right=6, mid=5` → `nums[5]=2`

- `nums[5]=2 <= nums[5]=2`? **Yes** → left half is sorted
- Is 3 in `[2, 2)`? **No** → search right → `left = 6`

```
[ 7,  8,  9, 10,  1,  2,  3,  4,  5,  6 ]
                         L  M     R
                      (single element, sorted)
                            ↑ new left
```

#### STEP 4: `left=6, right=6, mid=6` → `nums[6]=3 == 3` → **FOUND! return 6** ✅

### Dry Run — `target=11` (not found)

#### STEP 1: `left=0, right=9, mid=4` → `nums[4]=1`

- `nums[0]=7 <= 1`? **No** → right half is sorted
- Is 11 in `(1, 6]`? **No** → search left → `right = 3`

#### STEP 2: `left=0, right=3, mid=1` → `nums[1]=8`

- `nums[0]=7 <= 8`? **Yes** → left half is sorted
- Is 11 in `[7, 8)`? **No** → search right → `left = 2`

#### STEP 3: `left=2, right=3, mid=2` → `nums[2]=9`

- `nums[2]=9 <= 9`? **Yes** → left half is sorted
- Is 11 in `[9, 9)`? **No** → search right → `left = 3`

#### STEP 4: `left=3, right=3, mid=3` → `nums[3]=10`

- `nums[3]=10 <= 10`? **Yes** → left half is sorted
- Is 11 in `[10, 10)`? **No** → search right → `left = 4`

#### STEP 5: `left=4, right=3` → `left > right` → **NOT FOUND → return -1** ✅

### Summary Table — `target=3` (found)

| Step | left | right | mid | nums[mid] | Sorted half? | Target in sorted? | Action |
|:----:|:----:|:-----:|:---:|:---------:|:------------:|:-----------------:|:------:|
| 1 | 0 | 9 | 4 | 1 | Right (7>1) | 3 in (1,6]? Yes | left = 5 |
| 2 | 5 | 9 | 7 | 4 | Left (2≤4) | 3 in [2,4)? Yes | right = 6 |
| 3 | 5 | 6 | 5 | 2 | Left (2≤2) | 3 in [2,2)? No | left = 6 |
| 4 | 6 | 6 | 6 | 3 | — | nums[6]==3 == target | return **6** ✅ |

### Summary Table — `target=11` (not found)

| Step | left | right | mid | nums[mid] | Sorted half? | Target in sorted? | Action |
|:----:|:----:|:-----:|:---:|:---------:|:------------:|:-----------------:|:------:|
| 1 | 0 | 9 | 4 | 1 | Right (7>1) | 11 in (1,6]? No | right = 3 |
| 2 | 0 | 3 | 1 | 8 | Left (7≤8) | 11 in [7,8)? No | left = 2 |
| 3 | 2 | 3 | 2 | 9 | Left (9≤9) | 11 in [9,9)? No | left = 3 |
| 4 | 3 | 3 | 3 | 10 | Left (10≤10) | 11 in [10,10)? No | left = 4 |
| 5 | 4 | 3 | — | — | left > right | — | return **-1** ✅ |

> **Key observation:** Only **3-4 iterations** for a 10-element array. That's O(log N) — binary search cuts the search space in half each time by identifying the sorted half!


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
