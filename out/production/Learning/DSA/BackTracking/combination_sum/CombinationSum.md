# Combination Sum — Detailed Explanation

> **LeetCode #39** | [Problem Link](https://leetcode.com/problems/combination-sum/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic Backtracking)  
> **Topic:** Backtracking, Recursion

---

## 📋 Problem Statement

Given an array of distinct integers (candidates) and a target, find all unique combinations that sum to target. Each number can be used **unlimited times**.

### Examples

| candidates | target | Output |
|------------|--------|--------|
| `[2,3,6,7]` | 7 | `[[2,2,3],[7]]` |
| `[2,3,5]` | 8 | `[[2,2,2,2],[2,3,3],[3,5]]` |

---

## 🧩 Method 1: Brute Force Recursion — O(2^T)

### Core Idea

At each step, try adding each candidate. Recurse with reduced target. No pruning — explores all branches including ones that overshoot.

### Code

```kotlin
fun combinationSumBruteForce(candidates: IntArray, target: Int): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun bruteForce(start: Int, remaining: Int, path: MutableList<Int>) {
        if (remaining == 0) { result.add(path.toList()); return }
        if (remaining < 0) return  // Overshot.

        for (i in start until candidates.size) {
            path.add(candidates[i])
            bruteForce(i, remaining - candidates[i], path)
            path.removeAt(path.lastIndex)  // Backtrack.
        }
    }

    bruteForce(0, target, mutableListOf())
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(2^T) | Exponential, no pruning |
| **Space** | O(T) | Recursion depth |

---

## 🧩 Method 2: Backtracking with Pruning — O(N^(T/M))

### Core Idea

Sort candidates. At each step, only try candidates ≤ remaining (prune early). Use start index to avoid duplicates.

### Key Insight

> **Sorting + pruning:** If `sorted[i] > remaining`, break — all subsequent candidates are also too large.  
> **Start index:** Using `i` (not 0) as start avoids duplicate combinations like [2,3] and [3,2].

### Dry Run — `candidates=[2,3,6,7], target=7`

```
Try 2, remaining=7 → path=[2]
  Try 2, remaining=5 → path=[2,2]
    Try 2, remaining=3 → path=[2,2,2]
      Try 2, remaining=1 → path=[2,2,2,2]
        ✂️ Prune: 2 > 1
      Try 3, remaining=1 → path=[2,2,2,3]
        ✂️ Prune: 3 > 1
    Try 3, remaining=3 → path=[2,2,3]
      ✅ Found: [2,2,3]
  Try 3, remaining=5 → path=[2,3]
    Try 3, remaining=2 → path=[2,3,3]
      ✂️ Prune: 3 > 2
Try 3, remaining=7 → path=[3]
  Try 3, remaining=4 → path=[3,3]
    Try 3, remaining=1 → path=[3,3,3]
      ✂️ Prune: 3 > 1
Try 6, remaining=7 → path=[6]
  Try 6, remaining=1 → path=[6,6]
    ✂️ Prune: 6 > 1
Try 7, remaining=7 → path=[7]
  ✅ Found: [7]
```

✅ **Result: `[[2,2,3],[7]]`**

### Code

```kotlin
fun combinationSumBacktrack(candidates: IntArray, target: Int): List<List<Int>> {
    val sorted = candidates.sorted()
    val result = mutableListOf<List<Int>>()

    fun backtrack(start: Int, remaining: Int, path: MutableList<Int>) {
        if (remaining == 0) { result.add(path.toList()); return }

        for (i in start until sorted.size) {
            if (sorted[i] > remaining) break  // Prune.
            path.add(sorted[i])
            backtrack(i, remaining - sorted[i], path)
            path.removeAt(path.lastIndex)  // Backtrack.
        }
    }

    backtrack(0, target, mutableListOf())
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N^(T/M)) | N candidates, T/M max depth |
| **Space** | O(T/M) | Recursion depth |

---

## 📊 Comparison Table

| Aspect | Brute Force | Backtracking + Pruning |
|--------|-------------|------------------------|
| **Time** | O(2^T) | O(N^(T/M)) |
| **Space** | O(T) | O(T/M) |
| **Sorting?** | No | Yes (enables pruning) |
| **Pruning?** | No | Yes (`break` when > remaining) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Sort first:** Sorting enables early pruning — `break` when candidate > remaining.
2. **Start index avoids duplicates:** Using `i` (not 0) as start ensures combinations are in non-decreasing order, avoiding [2,3] and [3,2].
3. **Unlimited use:** Pass `i` (not `i+1`) to allow reusing the same candidate.
4. **Backtrack pattern:** Add → recurse → remove. This is the universal backtracking template.
5. **Pattern:** Extends to Combination Sum II (each number once), Subsets, Permutations, Palindrome Partitioning.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Combination Sum | [#39](https://leetcode.com/problems/combination-sum/) | Medium |
| Combination Sum II | [#40](https://leetcode.com/problems/combination-sum-ii/) | Medium |
| Combination Sum III | [#216](https://leetcode.com/problems/combination-sum-iii/) | Medium |
| Subsets | [#78](https://leetcode.com/problems/subsets/) | Medium |
