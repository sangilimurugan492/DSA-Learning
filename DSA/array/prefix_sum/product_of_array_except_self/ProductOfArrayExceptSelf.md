# Product of Array Except Self — Detailed Explanation

> **LeetCode #238** | [Problem Link](https://leetcode.com/problems/product-of-array-except-self/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (One of the MOST asked FAANG questions)  
> **Topic:** Array, Prefix Sum

---

## 📋 Problem Statement

Given an integer array `nums`, return an array `output` where `output[i]` is the **product of all elements except `nums[i]`**. Must run in **O(N)** **without using division**.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[1, 2, 3, 4]` | `[24, 12, 8, 6]` | 24=2×3×4, 12=1×3×4, 8=1×2×4, 6=1×2×3 |
| `[-1, 1, 0, -3, 3]` | `[0, 0, 9, 0, 0]` | Only index 2 has non-zero product: (-1)×1×(-3)×3=9 |

### Visual Walkthrough — Example 1: `[1, 2, 3, 4]`

```
Index:     0      1      2      3
nums:      1      2      3      4
           ↑      ↑      ↑      ↑
output[0]: -  × 2 × 3 × 4 = 24
output[1]: 1 ×  -  × 3 × 4 = 12
output[2]: 1 × 2 ×  -  × 4 = 8
output[3]: 1 × 2 × 3 ×  -  = 6
```

---

## 🧩 Method 1: Brute Force — For Each Element, Multiply All Others

### Core Idea

For each index `i`, iterate through all elements and multiply those where `j != i`.

### Code

```kotlin
fun productExceptSelfBruteForce(nums: IntArray): IntArray {
    val result = IntArray(nums.size)
    for (i in nums.indices) {
        var product = 1
        for (j in nums.indices) {
            if (i != j) product *= nums[j]
        }
        result[i] = product
    }
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | Output array |

---

## 🧩 Method 2: Prefix + Suffix Products (Optimal)

### Core Idea

> `output[i] = (product of all elements LEFT of i) × (product of all elements RIGHT of i)`

- **Pass 1 (left→right):** Store prefix products in the output array.
- **Pass 2 (right→left):** Multiply each by a running suffix product.

### Key Insight

> `output[i] = prefix[i] × suffix[i]`  
> - `prefix[i]` = product of `nums[0..i-1]`  
> - `suffix[i]` = product of `nums[i+1..n-1]`  
> We compute prefix in the output array, then multiply suffix on the fly — O(1) extra space.

### Algorithm — Step by Step

1. **Pass 1 (prefix, left→right):**
   - `result[0] = 1` (nothing to the left).
   - `result[i] = result[i-1] × nums[i-1]` for `i` from `1` to `n-1`.
2. **Pass 2 (suffix, right→left):**
   - `suffix = 1` (nothing to the right initially).
   - For `i` from `n-1` to `0`:
     - `result[i] *= suffix` (multiply prefix × suffix).
     - `suffix *= nums[i]` (update suffix for next element).
3. **Return** `result`.

### Dry Run — Example 1: `[1, 2, 3, 4]`

**Pass 1 — Prefix (left→right):**
| `i` | `nums[i-1]` | `result[i-1]` | `result[i] = result[i-1] × nums[i-1]` |
|:---:|:---:|:---:|:---:|
| 0 | — | — | 1 (initial) |
| 1 | 1 | 1 | 1 × 1 = **1** |
| 2 | 2 | 1 | 1 × 2 = **2** |
| 3 | 3 | 2 | 2 × 3 = **6** |

After Pass 1: `result = [1, 1, 2, 6]`

**Pass 2 — Suffix (right→left):**
| `i` | `nums[i]` | `suffix` (before) | `result[i] *= suffix` | `suffix *= nums[i]` (after) |
|:---:|:---:|:---:|:---:|:---:|
| 3 | 4 | 1 | 6 × 1 = **6** | 1 × 4 = 4 |
| 2 | 3 | 4 | 2 × 4 = **8** | 4 × 3 = 12 |
| 1 | 2 | 12 | 1 × 12 = **12** | 12 × 2 = 24 |
| 0 | 1 | 24 | 1 × 24 = **24** | 24 × 1 = 24 |

✅ **Result: `[24, 12, 8, 6]`**

### Code

```kotlin
fun productExceptSelfOptimal(nums: IntArray): IntArray {
    val n = nums.size
    val result = IntArray(n)

    // Pass 1: Prefix products
    result[0] = 1
    for (i in 1 until n) {
        result[i] = result[i - 1] * nums[i - 1]
    }

    // Pass 2: Suffix products
    var suffix = 1
    for (i in n - 1 downTo 0) {
        result[i] *= suffix
        suffix *= nums[i]
    }

    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Two passes |
| **Space** | O(1) | Output array doesn't count; only `suffix` variable |

---

## 📊 Comparison Table

| Aspect | Brute Force | Prefix + Suffix |
|--------|-------------|-----------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(1) |
| **Uses division?** | No | No |
| **Handles zeros?** | ✅ | ✅ |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **No division:** The problem explicitly forbids division — prefix/suffix products are the way.
2. **output[i] = prefix × suffix:** Decompose the problem into left and right products.
3. **O(1) extra space:** By reusing the output array for prefix and a running variable for suffix, we avoid extra arrays.
4. **Handles zeros naturally:** Since we never divide, zeros don't cause any issues.
5. **Pattern:** Prefix/suffix products are a common technique (Trapping Rain Water, Candy, etc.).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Product of Array Except Self | [#238](https://leetcode.com/problems/product-of-array-except-self/) | Medium |
| Trapping Rain Water | [#42](https://leetcode.com/problems/trapping-rain-water/) | Hard |
| Candy | [#135](https://leetcode.com/problems/candy/) | Hard |
