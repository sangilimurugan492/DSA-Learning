# Tallest Billboard — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/tallest-billboard/  
> **Topic:** Dynamic Programming, Hash Map  
> **Difficulty:** Hard

---

## 📋 Problem Statement

You are installing a billboard and want it to have maximum height. The billboard will have two steel supports, one on each side. Each steel support must be **equal height**.

You are given an array `rods`, where `rods[i]` is the height of the i-th rod. You may weld rods together to form a taller support.

Return the **maximum possible height** of the billboard installation. If you cannot support the billboard, return `0`.

### Constraints

- `1 <= rods.length <= 20`
- `1 <= rods[i] <= 1000`
- `sum(rods) <= 5000`

### Examples

**Example 1:**

```
Input:  rods = [1, 2, 3, 6]
Output: 6
```

**Example 2:**

```
Input:  rods = [1, 2, 3, 4, 5, 6]
Output: 10
```

**Example 3:**

```
Input:  rods = [1, 2]
Output: 0
```

---

## 🧩 Method 1: Brute Force (Three-way Recursion)

### Core Idea

Each rod has 3 choices: go to the **left** support, go to the **right** support, or be **discarded**. Recursively try all `3^n` combinations and track the best result where `left == right`.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(3^N) — exponential |
| **Space** | O(N) recursion depth |

---

## 🧩 Method 2: DP on Height Difference ✅ Optimal

### Core Idea

Track states as `(leftHeight − rightHeight) → max leftHeight`.

For each rod, three transitions from every existing difference `d`:

| Choice | New difference | New left height |
|--------|---------------|-----------------|
| Add to left | `d + rod` | `left + rod` |
| Add to right | `d − rod` | `left` (unchanged) |
| Discard | `d` | `left` |

At the end, `dp[0]` (difference = 0, i.e., equal supports) holds the answer — the maximum `leftHeight` achievable with balanced supports.

### Step-by-step Walkthrough

```
rods = [1, 2, 3, 6]

Start: {0: 0}   (diff 0, left height 0)

rod=1: {0:0, 1:1, -1:0}
rod=2: {0:0, 1:1, -1:0, 2:2, 3:3, -2:0, -3:1, 1:2→max(1,2)=2, ...}
rod=3: ... diff 0 achievable with left=3 (e.g., left=[3], right=[1+2])
rod=6: ... diff 0 achievable with left=6 (left=[6], right=[1+2+3]) ✅

Answer: dp[0] = 6
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N × S) where S = sum of rods (differences bounded by ±S) |
| **Space** | O(S) hash map of differences |

---

## 🔑 Key Takeaways

1. **Encode the state cleverly**: instead of tracking `(left, right)` pairs (O(S²) states), track only the **difference** `left − right` and store the best `left` for each difference — O(S) states.
2. The answer is `dp[0]` — difference zero means the two supports are equal.
3. This is a **3-choice knapsack** (left / right / skip) — a common pattern for "partition into two equal groups" problems.
4. Hash-map DP over differences generalizes to many "balance" problems (e.g., Target Sum is the 2-choice version).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Target Sum | [link](https://leetcode.com/problems/target-sum/) | Medium |
| Partition Equal Subset Sum | [link](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium |
| Tallest Billboard | [link](https://leetcode.com/problems/tallest-billboard/) | Hard |
