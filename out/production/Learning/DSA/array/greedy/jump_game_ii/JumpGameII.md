# Jump Game II — Detailed Explanation

> **LeetCode #45** | [Problem Link](https://leetcode.com/problems/jump-game-ii/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)  
> **Topic:** Greedy, DP, Array

---

## 📋 Problem Statement

Return the minimum number of jumps to reach the last index.

### Examples

| nums | Output | Explanation |
|------|--------|-------------|
| `[2,3,1,1,4]` | 2 | 0→1→4 |
| `[2,3,0,1,4]` | 2 | 0→1→4 |

---

## 🧩 Method 1: DP — O(N²)

### Core Idea

`dp[i]` = min jumps to reach index i. For each i, check all j < i: if `j + nums[j] >= i`, `dp[i] = min(dp[i], dp[j] + 1)`.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | dp array |

---

## 🧩 Method 2: BFS-like Greedy — O(N)

### Core Idea

Think of it as BFS levels. Each jump = one level. Track `currentEnd` (end of current level) and `farthest` (farthest reachable in next level). When `i == currentEnd` → jump to next level.

### Key Insight

> Level 0: index 0 (reach 1..2). Level 1: indices 1..2 (reach 2..4). Level 2: reached the end! Each level = one jump.

### Dry Run — `[2,3,1,1,4]`

| i | nums[i] | farthest | i==currentEnd? | jumps | currentEnd |
|:-:|:-------:|:--------:|:--------------:|:-----:|:----------:|
| 0 | 2 | 2 | 0==0 ✅ | 1 | 2 |
| 1 | 3 | 4 | 1==2 ❌ | 1 | 2 |
| 2 | 1 | 4 | 2==2 ✅ | 2 | 4 (≥4, break) |

✅ **Result: 2**

### Code

```kotlin
fun jumpGameII(nums: IntArray): Int {
    if (nums.size <= 1) return 0
    var jumps = 0; var currentEnd = 0; var farthest = 0
    for (i in 0 until nums.size - 1) {
        farthest = maxOf(farthest, i + nums[i])
        if (i == currentEnd) {
            jumps++; currentEnd = farthest
            if (currentEnd >= nums.size - 1) break
        }
    }
    return jumps
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Two variables |

---

## 📊 Comparison Table

| Aspect | DP | BFS-like Greedy |
|--------|-----|-----------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(1) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **BFS levels:** Each jump = one BFS level. `currentEnd` = boundary of current level.
2. **farthest:** Track the farthest reachable index across all positions in the current level.
3. **Jump trigger:** When `i == currentEnd`, we've exhausted the current level → jump.
4. **Pattern:** BFS on implicit graph — extends to Jump Game I, Jump Game III.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Jump Game | [#55](https://leetcode.com/problems/jump-game/) | Medium |
| Jump Game II | [#45](https://leetcode.com/problems/jump-game-ii/) | Medium |
| Jump Game III | [#1306](https://leetcode.com/problems/jump-game-iii/) | Medium |
| Video Stitching | [#1024](https://leetcode.com/problems/video-stitching/) | Medium |
