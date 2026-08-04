# Subsets — Detailed Explanation

> **LeetCode #78** | [Problem Link](https://leetcode.com/problems/subsets/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (THE gateway backtracking problem)  
> **Topic:** Backtracking, Recursion

---

## 📋 Problem Statement

Given an integer array of UNIQUE elements, return all possible subsets (the power set). No duplicate subsets.

### Example

`nums = [1,2,3]` → `[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]` (2³ = 8)

---

## 🧩 Method 1: Backtracking — O(N × 2^N)

### Core Idea

At each index, choose to include or skip. Every node is a valid subset. Use start index to avoid duplicates.

### Key Insight

> For each element, we have TWO choices: INCLUDE or EXCLUDE. This creates a decision tree with 2^N leaves. Using start index ensures [1,2] and [2,1] don't both appear.

### Code

```kotlin
fun subsets(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun backtrack(start: Int, current: MutableList<Int>) {
        result.add(current.toList())
        for (i in start until nums.size) {
            current.add(nums[i])
            backtrack(i + 1, current)
            current.removeAt(current.lastIndex)
        }
    }
    backtrack(0, mutableListOf())
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × 2^N) | 2^N subsets, each up to N elements |
| **Space** | O(N) | Recursion depth |

---

## 🧩 Method 2: Iterative (Cascading) — O(N × 2^N)

### Core Idea

Start with `[[]]`. For each num, add num to all existing subsets. Doubles result size each iteration.

### Code

```kotlin
fun subsetsIterative(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>(emptyList())
    for (num in nums) {
        val size = result.size
        for (i in 0 until size) result.add(result[i] + num)
    }
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × 2^N) | Same |
| **Space** | O(N × 2^N) | Storing all subsets |

---

## 📊 Comparison Table

| Aspect | Backtracking | Iterative |
|--------|-------------|-----------|
| **Time** | O(N × 2^N) | O(N × 2^N) |
| **Space** | O(N) | O(N × 2^N) |
| **Recursion?** | Yes | No |
| **Interview preference** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Every node is a subset:** Add current to result at every step, not just leaves.
2. **Start index avoids duplicates:** [1,2] and [2,1] can't both appear.
3. **2^N subsets:** For N elements, there are exactly 2^N subsets (include/exclude each).
4. **THE foundation:** All backtracking problems build on this pattern.
5. **Pattern:** Choose→explore→undo — extends to Combination Sum, Permutations, Palindrome Partitioning.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Subsets | [#78](https://leetcode.com/problems/subsets/) | Medium |
| Subsets II | [#90](https://leetcode.com/problems/subsets-ii/) | Medium |
| Combination Sum | [#39](https://leetcode.com/problems/combination-sum/) | Medium |
| Permutations | [#46](https://leetcode.com/problems/permutations/) | Medium |
