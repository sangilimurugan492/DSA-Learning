# Permutations — Detailed Explanation

> **LeetCode #46** | [Problem Link](https://leetcode.com/problems/permutations/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (ORDER matters — unlike Subsets)  
> **Topic:** Backtracking, Recursion

---

## 📋 Problem Statement

Given an array of DISTINCT integers, return ALL possible permutations.

### Example

`nums = [1,2,3]` → `[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]` (3! = 6)

---

## 🧩 Method 1: Used Array — O(N × N!)

### Core Idea

Track which elements are picked with a `used` array. At each step, try ALL unused elements.

### Key Difference from Subsets

> Subsets: Order doesn't matter → use start index.  
> Permutations: Order matters → try ALL positions.

### Code

```kotlin
fun permute(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    val used = BooleanArray(nums.size)

    fun backtrack(current: MutableList<Int>) {
        if (current.size == nums.size) { result.add(current.toList()); return }
        for (i in nums.indices) {
            if (used[i]) continue
            used[i] = true; current.add(nums[i])
            backtrack(current)
            current.removeAt(current.lastIndex); used[i] = false
        }
    }
    backtrack(mutableListOf())
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × N!) | N! permutations, O(N) to copy each |
| **Space** | O(N) | Recursion + used array |

---

## 🧩 Method 2: Swap-based — O(N × N!)

### Core Idea

Swap elements in-place. Position `first` is fixed, recurse on remaining. No used array needed.

### Code

```kotlin
fun permuteSwap(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    fun backtrack(first: Int) {
        if (first == nums.size) { result.add(nums.toList()); return }
        for (i in first until nums.size) {
            nums.swap(first, i); backtrack(first + 1); nums.swap(first, i)
        }
    }
    backtrack(0)
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × N!) | Same as used array |
| **Space** | O(N) | Recursion only (no used array) |

---

## 📊 Comparison Table

| Aspect | Used Array | Swap-based |
|--------|-----------|------------|
| **Time** | O(N × N!) | O(N × N!) |
| **Space** | O(N) + used array | O(N) only |
| **Modifies input?** | No | Yes (in-place) |
| **Interview preference** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Order matters:** Unlike subsets, [1,2] ≠ [2,1]. Try ALL unused elements at each step.
2. **Used array:** Tracks which elements are already in the current permutation.
3. **Swap approach:** More elegant — no extra array. Swap element to position, recurse, swap back.
4. **N! permutations:** For N distinct elements, there are exactly N! permutations.
5. **Pattern:** Backtracking with choose→explore→undo — extends to Permutations II, Combinations.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Permutations | [#46](https://leetcode.com/problems/permutations/) | Medium |
| Permutations II | [#47](https://leetcode.com/problems/permutations-ii/) | Medium |
| Subsets | [#78](https://leetcode.com/problems/subsets/) | Medium |
| Combinations | [#77](https://leetcode.com/problems/combinations/) | Medium |
