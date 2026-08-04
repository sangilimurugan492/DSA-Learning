# Trapping Rain Water — Detailed Explanation

> **LeetCode #42** | [Problem Link](https://leetcode.com/problems/trapping-rain-water/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 3 most asked Hard)  
> **Topic:** Array, Two Pointer, Dynamic Programming

---

## 📋 Problem Statement

Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water can be **trapped** after raining.

### Key Formula

> **Water at index `i` = `min(maxLeft[i], maxRight[i]) - height[i]`**
> - `maxLeft[i]` = tallest bar to the left of `i` (including `i`)
> - `maxRight[i]` = tallest bar to the right of `i` (including `i`)

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `[0,1,0,2,1,0,1,3,2,1,2,1]` | `6` | Water trapped at various positions |
| `[4,2,0,3,2,5]` | `9` | Water fills the gaps between taller bars |

### Visual Walkthrough — Example 1: `[0,1,0,2,1,0,1,3,2,1,2,1]`

```
       █
   █   █ █   █
 █ █ █ █ █ █ █ █
 0 1 0 2 1 0 1 3 2 1 2 1

Water trapped (marked with ~):
       █
   █~~ █~█~~ █
 █ █ █ █ █ █ █ █
 0 1 0 2 1 0 1 3 2 1 2 1
     ↑     ↑ ↑   ↑
     1     1 1   1  → Total = 6
```

---

## 🧩 Method 1: Brute Force — Scan Left/Right for Each Bar

### Core Idea

For each index `i`, scan left to find `maxLeft` and right to find `maxRight`. Water = `min(maxLeft, maxRight) - height[i]`.

### Code

```kotlin
fun trapBruteForce(height: IntArray): Int {
    var totalWater = 0
    for (i in height.indices) {
        var maxLeft = 0
        for (l in 0..i) maxLeft = maxOf(maxLeft, height[l])
        var maxRight = 0
        for (r in i until height.size) maxRight = maxOf(maxRight, height[r])
        totalWater += minOf(maxLeft, maxRight) - height[i]
    }
    return totalWater
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | For each index, scan left and right |
| **Space** | O(1) | Only variables |

---

## 🧩 Method 2: DP — Precompute maxLeft/maxRight Arrays

### Core Idea

Instead of scanning for each index, precompute `maxLeft[]` and `maxRight[]` arrays in O(N).

### Algorithm

1. **maxLeft[i]** = `max(maxLeft[i-1], height[i])` — left to right.
2. **maxRight[i]** = `max(maxRight[i+1], height[i])` — right to left.
3. **Water at i** = `min(maxLeft[i], maxRight[i]) - height[i]`.

### Dry Run — Example 2: `[4, 2, 0, 3, 2, 5]`

| Index | height | maxLeft | maxRight | min(L,R) | water |
|:---:|:---:|:---:|:---:|:---:|:---:|
| 0 | 4 | 4 | 5 | 4 | 0 |
| 1 | 2 | 4 | 5 | 4 | 2 |
| 2 | 0 | 4 | 5 | 4 | 4 |
| 3 | 3 | 4 | 5 | 4 | 1 |
| 4 | 2 | 4 | 5 | 4 | 2 |
| 5 | 5 | 5 | 5 | 5 | 0 |

Total = 0+2+4+1+2+0 = **9** ✅

### Code

```kotlin
fun trapDP(height: IntArray): Int {
    if (height.isEmpty()) return 0
    val n = height.size
    val maxLeft = IntArray(n)
    val maxRight = IntArray(n)
    maxLeft[0] = height[0]
    for (i in 1 until n) maxLeft[i] = maxOf(maxLeft[i - 1], height[i])
    maxRight[n - 1] = height[n - 1]
    for (i in n - 2 downTo 0) maxRight[i] = maxOf(maxRight[i + 1], height[i])
    var totalWater = 0
    for (i in height.indices) totalWater += minOf(maxLeft[i], maxRight[i]) - height[i]
    return totalWater
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Three passes |
| **Space** | O(N) | Two arrays |

---

## 🧩 Method 3: Two Pointer (Optimal)

### Core Idea

Use two pointers from both ends. Track `maxLeft` and `maxRight` as running variables. Move the pointer with the **smaller** max — that side is the bottleneck.

### Key Insight

> If `maxLeft < maxRight`, the water at `left` is determined by `maxLeft` (since `maxRight` is already taller, `min(maxLeft, maxRight) = maxLeft`). So we can safely calculate water at `left` and move it inward.

### Algorithm

1. Initialize `left = 0`, `right = n-1`, `maxLeft = height[0]`, `maxRight = height[n-1]`.
2. While `left < right`:
   - If `maxLeft < maxRight`: `left++`, update `maxLeft`, add `maxLeft - height[left]` to water.
   - Else: `right--`, update `maxRight`, add `maxRight - height[right]` to water.
3. Return total water.

### Code

```kotlin
fun trapTwoPointer(height: IntArray): Int {
    if (height.isEmpty()) return 0
    var left = 0
    var right = height.size - 1
    var maxLeft = height[left]
    var maxRight = height[right]
    var totalWater = 0
    while (left < right) {
        if (maxLeft < maxRight) {
            left++
            maxLeft = maxOf(maxLeft, height[left])
            totalWater += maxLeft - height[left]
        } else {
            right--
            maxRight = maxOf(maxRight, height[right])
            totalWater += maxRight - height[right]
        }
    }
    return totalWater
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Only variables |

---

## 📊 Comparison Table

| Aspect | Brute Force | DP | Two Pointer |
|--------|-------------|-----|------------|
| **Time** | O(N²) | O(N) | O(N) |
| **Space** | O(1) | O(N) | O(1) |
| **Optimality** | ❌ | ✅ | ✅ Best |
| **Interview preference** | ⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Water formula:** `min(maxLeft, maxRight) - height[i]` — the water level is determined by the shorter of the two tallest bars on each side.
2. **Precompute for O(N):** DP precomputes maxLeft/maxRight arrays to avoid repeated scanning.
3. **Two pointer saves space:** By tracking running maxes and moving the smaller one, we achieve O(1) space.
4. **Move the bottleneck:** Always process the side with the smaller max — that's the limiting factor for water level.
5. **Pattern:** This "two-pointer from ends, move the limiting one" pattern is shared with Container With Most Water.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Trapping Rain Water | [#42](https://leetcode.com/problems/trapping-rain-water/) | Hard |
| Container With Most Water | [#11](https://leetcode.com/problems/container-with-most-water/) | Medium |
| Trapping Rain Water II | [#407](https://leetcode.com/problems/trapping-rain-water-ii/) | Hard |
