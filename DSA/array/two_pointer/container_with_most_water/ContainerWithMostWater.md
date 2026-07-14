# Container With Most Water — Detailed Explanation

> **LeetCode #11** | [Problem Link](https://leetcode.com/problems/container-with-most-water/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 10 most asked)  
> **Topic:** Array, Two Pointer

---

## 📋 Problem Statement

Given `n` non-negative integers `a₁, a₂, ..., aₙ`, where each represents a vertical line at point `i` with height `height[i]`, find two lines that together with the x-axis form a container that holds the **most water**.

**Area formula:** `min(height[i], height[j]) × (j - i)`

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[1, 8, 6, 2, 5, 4, 8, 3, 7]` | `49` | Lines at index 1 (h=8) and index 8 (h=7): area = min(8,7) × 7 = 49 |
| `[1, 1]` | `1` | Lines at index 0 and 1: area = min(1,1) × 1 = 1 |

### Visual Walkthrough — Example 1: `[1, 8, 6, 2, 5, 4, 8, 3, 7]`

```
Index:  0   1   2   3   4   5   6   7   8
        |   |   |   |   |   |   |   |   |
  8     |   |               |       |
  7     |   |               |       |       ← height[8]=7
  6     |   |   |           |       |
  5     |   |   |   |   |   |       |
  4     |   |   |   |   |   |   |   |
  3     |   |   |   |   |   |   |   |
  2     |   |   |   |   |   |   |   |
  1 |   |   |   |   |   |   |   |   |
    0   1   2   3   4   5   6   7   8

Best: index 1 (h=8) and index 8 (h=7)
Area = min(8, 7) × (8-1) = 7 × 7 = 49 ✅
```

---

## 🧩 Method 1: Brute Force — Check Every Pair

### Core Idea

For every pair of lines `(i, j)`, calculate the area and track the maximum.

### Algorithm

1. **For each** `i` from `0` to `n-1`:
   **For each** `j` from `i+1` to `n-1`:
   - `area = min(height[i], height[j]) × (j - i)`
   - `maxArea = max(maxArea, area)`
2. **Return** `maxArea`.

### Code

```kotlin
fun maxAreaBruteForce(height: IntArray): Int {
    var maxArea = 0
    for (i in height.indices) {
        for (j in i + 1 until height.size) {
            val area = minOf(height[i], height[j]) * (j - i)
            maxArea = maxOf(maxArea, area)
        }
    }
    return maxArea
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops over all pairs |
| **Space** | O(1) | Only one variable |

---

## 🧩 Method 2: Two Pointer (Optimal)

### Core Idea

Start from the **widest** container (left=0, right=n-1). Move the **shorter** line inward. This works because moving the longer line can only decrease the area.

### Key Insight

> Area = min(h[left], h[right]) × (right - left)  
> - If we move the **longer** line inward: width decreases, height stays ≤ shorter line → area can only decrease.  
> - If we move the **shorter** line inward: width decreases, but we might find a taller line → area might increase.  
> So we always move the shorter line — we never miss the optimal solution.

### Algorithm — Step by Step

1. **Initialize** `left = 0`, `right = n-1`, `maxArea = 0`.
2. **While** `left < right`:
   - `area = min(height[left], height[right]) × (right - left)`
   - `maxArea = max(maxArea, area)`
   - If `height[left] < height[right]`: `left++` (move shorter line)
   - Else: `right--`
3. **Return** `maxArea`.

### Dry Run — Example 1: `[1, 8, 6, 2, 5, 4, 8, 3, 7]`

| Step | `left` | `right` | `h[left]` | `h[right]` | `min_h` | `width` | `area` | `maxArea` | Move |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 1 | 0 | 8 | 1 | 7 | 1 | 8 | 8 | 8 | left++ (1<7) |
| 2 | 1 | 8 | 8 | 7 | 7 | 7 | **49** | **49** | right-- (8≥7) |
| 3 | 1 | 7 | 8 | 3 | 3 | 6 | 18 | 49 | right-- (8≥3) |
| 4 | 1 | 6 | 8 | 8 | 8 | 5 | 40 | 49 | right-- (8≥8) |
| 5 | 1 | 5 | 8 | 4 | 4 | 4 | 16 | 49 | right-- (8≥4) |
| 6 | 1 | 4 | 8 | 5 | 5 | 3 | 15 | 49 | right-- (8≥5) |
| 7 | 1 | 3 | 8 | 2 | 2 | 2 | 4 | 49 | right-- (8≥2) |
| 8 | 1 | 2 | 8 | 6 | 6 | 1 | 6 | 49 | right-- (8≥6) |
| 9 | 1 | 1 | — | — | — | — | — | 49 | left ≥ right → stop |

✅ **Result: `49`** — Found at step 2 (lines at index 1 and 8).

### Code

```kotlin
fun maxAreaTwoPointer(height: IntArray): Int {
    var left = 0
    var right = height.size - 1
    var maxArea = 0

    while (left < right) {
        val area = minOf(height[left], height[right]) * (right - left)
        maxArea = maxOf(maxArea, area)
        if (height[left] < height[right]) left++ else right--
    }

    return maxArea
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass, each step moves one pointer |
| **Space** | O(1) | Only three variables |

### Why It Works

- We start with the **maximum width** — any narrower container must be taller to beat the current max.
- Moving the **shorter** line is the only way to potentially find a larger area (the longer line limits nothing).
- Moving the **longer** line would shrink width without any chance of increasing height — guaranteed to not improve.
- This greedy elimination ensures we check all potentially optimal pairs in O(N).

---

## 📊 Comparison Table

| Aspect | Brute Force | Two Pointer |
|--------|-------------|------------|
| **Time Complexity** | O(N²) | O(N) |
| **Space Complexity** | O(1) | O(1) |
| **Approach** | Check every pair | Start widest, move shorter line |
| **Optimality** | ❌ TLE on large inputs | ✅ Optimal |
| **Ease of understanding** | Easy | Medium (requires insight) |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Area = min(h) × width:** The shorter line determines the height; the distance determines the width.
2. **Move the shorter line:** This is the key insight — moving the longer line can never improve the area.
3. **Start widest:** Beginning with maximum width ensures we explore the most promising containers first.
4. **Greedy elimination:** At each step, we eliminate one line from consideration — the shorter one can't be part of a better solution with any line between the current pair.
5. **Pattern:** This "two-pointer from ends, move the limiting one" pattern appears in many problems (Trapping Rain Water, etc.).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Container With Most Water | [#11](https://leetcode.com/problems/container-with-most-water/) | Medium |
| Trapping Rain Water | [#42](https://leetcode.com/problems/trapping-rain-water/) | Hard |
| Two Sum II | [#167](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium |
