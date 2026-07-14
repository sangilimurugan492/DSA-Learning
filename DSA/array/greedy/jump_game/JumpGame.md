# Jump Game — Detailed Explanation

> **LeetCode #55** | [Problem Link](https://leetcode.com/problems/jump-game/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)  
> **Topic:** Greedy, DP, Array

---

## 📋 Problem Statement

You are at index 0. Each element represents the **max jump length** from that position. Return `true` if you can reach the last index.

### Examples

| nums | Output | Explanation |
|------|--------|-------------|
| `[2,3,1,1,4]` | true | 0→1→4 (jump 1 from index 0, then jump 3 from index 1) |
| `[3,2,1,0,4]` | false | Stuck at index 3 (nums[3]=0, can't jump further) |

---

## 🧩 Method 1: DP — O(N²)

### Core Idea

`dp[i]` = true if we can reach index i. For each i, check all j < i: if `dp[j]` and `j + nums[j] >= i` → `dp[i] = true`.

### Step-by-Step

1. **Initialize:** `dp[0] = true` (we start at index 0).
2. **Fill dp:** For each index `i` from 1 to n-1:
   - Check all previous indices `j` from 0 to i-1.
   - If `dp[j]` is true (j is reachable) AND `j + nums[j] >= i` (can jump from j to i):
     - Set `dp[i] = true` and break.
3. **Answer:** `dp[n-1]`.

### Dry Run — `[2,3,1,1,4]`

| i | Check j | dp[j] && j+nums[j]>=i? | dp[i] |
|:-:|:-------:|:----------------------:|:-----:|
| 0 | — | — | true |
| 1 | j=0 | true && 0+2>=1 ✅ | true |
| 2 | j=0 | true && 0+2>=2 ✅ | true |
| 3 | j=0 | true && 0+2>=3 ❌ |
|   | j=1 | true && 1+3>=3 ✅ | true |
| 4 | j=0 | true && 0+2>=4 ❌ |
|   | j=1 | true && 1+3>=4 ✅ | true |

✅ **Result: dp[4] = true**

### Dry Run — `[3,2,1,0,4]`

| i | Check j | dp[j] && j+nums[j]>=i? | dp[i] |
|:-:|:-------:|:----------------------:|:-----:|
| 0 | — | — | true |
| 1 | j=0 | true && 0+3>=1 ✅ | true |
| 2 | j=0 | true && 0+3>=2 ✅ | true |
| 3 | j=0 | true && 0+3>=3 ✅ | true |
| 4 | j=0 | true && 0+3>=4 ❌ |
|   | j=1 | true && 1+2>=4 ❌ |
|   | j=2 | true && 2+1>=4 ❌ |
|   | j=3 | true && 3+0>=4 ❌ | false |

❌ **Result: dp[4] = false** (stuck at index 3 where nums[3]=0)

### Code

```kotlin
fun canJumpDP(nums: IntArray): Boolean {
    val n = nums.size
    val dp = BooleanArray(n)
    dp[0] = true

    for (i in 1 until n) {
        for (j in 0 until i) {
            if (dp[j] && j + nums[j] >= i) {
                dp[i] = true
                break
            }
        }
    }
    return dp[n - 1]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | dp array |

---

## 🧩 Method 2: Greedy — O(N)

### Core Idea

Track the **farthest** index reachable so far. Iterate through each index:
- If current index > farthest → can't reach here → return false.
- Update farthest = max(farthest, i + nums[i]).
- If farthest >= last index → return true.

### Key Insight

> We don't need to track the exact path — just whether we can reach each index. "farthest" = the maximum index reachable from any position seen so far. If we ever encounter an index beyond farthest, it's unreachable.

### Step-by-Step

1. **Initialize:** `farthest = 0` (we can only reach index 0 initially).
2. **Iterate:** For each index `i`:
   - If `i > farthest` → return **false** (index i is unreachable).
   - Update `farthest = max(farthest, i + nums[i])`.
   - If `farthest >= n-1` → return **true** (can reach the end).
3. If loop completes → return **true**.

### Dry Run — `[2,3,1,1,4]`

| i | nums[i] | i > farthest? | farthest = max(farthest, i+nums[i]) | farthest >= 4? |
|:-:|:-------:|:-------------:|:------------------------------------:|:--------------:|
| 0 | 2 | 0>0? No | max(0, 0+2) = 2 | No |
| 1 | 3 | 1>2? No | max(2, 1+3) = 4 | Yes ✅ → return true |

✅ **Result: true**

### Dry Run — `[3,2,1,0,4]`

| i | nums[i] | i > farthest? | farthest = max(farthest, i+nums[i]) | farthest >= 4? |
|:-:|:-------:|:-------------:|:------------------------------------:|:--------------:|
| 0 | 3 | 0>0? No | max(0, 0+3) = 3 | No |
| 1 | 2 | 1>3? No | max(3, 1+2) = 3 | No |
| 2 | 1 | 2>3? No | max(3, 2+1) = 3 | No |
| 3 | 0 | 3>3? No | max(3, 3+0) = 3 | No |
| 4 | 4 | 4>3? **Yes** ❌ | — | return **false** |

❌ **Result: false** (index 4 is beyond farthest=3, unreachable)

### Code

```kotlin
fun canJump(nums: IntArray): Boolean {
    var farthest = 0

    for (i in nums.indices) {
        if (i > farthest) return false  // Can't reach index i
        farthest = maxOf(farthest, i + nums[i])
        if (farthest >= nums.size - 1) return true  // Can reach the end
    }

    return true
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | One variable |

---

## 📊 Comparison Table

| Aspect | DP | Greedy |
|--------|-----|--------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(1) |
| **Approach** | Bottom-up reachability | Track farthest |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Greedy "farthest" tracking:** The single variable `farthest` replaces the entire dp array. At each step, we only care about the maximum reachable index.
2. **Early termination:** If `i > farthest` → immediately return false. If `farthest >= n-1` → immediately return true.
3. **Why greedy works:** We don't need to know HOW we reach each index — just WHETHER we can. The farthest variable captures all reachable indices in one value.
4. **Pattern:** Reachability with greedy — extends to Jump Game II, Jump Game III, Gas Station.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Jump Game | [#55](https://leetcode.com/problems/jump-game/) | Medium |
| Jump Game II | [#45](https://leetcode.com/problems/jump-game-ii/) | Medium |
| Jump Game III | [#1306](https://leetcode.com/problems/jump-game-iii/) | Medium |
| Gas Station | [#134](https://leetcode.com/problems/gas-station/) | Medium |
| Candy | [#135](https://leetcode.com/problems/candy/) | Hard |
