# Gas Station — Detailed Explanation

> **LeetCode #134** | [Problem Link](https://leetcode.com/problems/gas-station/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐  
> **Topic:** Array, Greedy

---

## 📋 Problem Statement

There are `n` gas stations along a **circular route**. `gas[i]` is the amount of gas at station `i`. `cost[i]` is the gas needed to travel from station `i` to station `i+1` (wrapping around). Return the starting station index if you can complete the circuit clockwise once. If impossible, return `-1`. If a solution exists, it is **guaranteed to be unique**.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `gas=[1,2,3,4,5], cost=[3,4,5,1,2]` | `3` | Start at station 3: tank goes 2→6→5→4→3, never negative |
| `gas=[2,3,4], cost=[3,4,3]` | `-1` | Total gas (9) < total cost (10), impossible |

### Visual Walkthrough — Example 1: `gas=[1,2,3,4,5], cost=[3,4,5,1,2]`

```
Station:    0      1      2      3      4
Gas:        1      2      3      4      5
Cost:       3      4      5      1      2
Diff:      -2     -2     -2     +3     +3

Total diff = -2-2-2+3+3 = 0  →  Solution exists!

Start at 3:  diff[3]=+3 → tank=3
             diff[4]=+3 → tank=6
             diff[0]=-2 → tank=4
             diff[1]=-2 → tank=2
             diff[2]=-2 → tank=0  ✅ Circuit complete!
```

---

## 🧩 Method 1: Brute Force — Try Every Starting Station

### Core Idea

For each station `start`, simulate a full circuit. If the tank never goes negative, return `start`.

### Algorithm — Step by Step

1. **For each station** `start` from `0` to `n-1`:
   - Initialize `tank = 0`.
   - **Simulate** `n` steps: at each step, `tank += gas[idx] - cost[idx]` where `idx = (start + i) % n`.
   - If `tank < 0` at any point, this start fails — break.
   - If all `n` steps complete without going negative, return `start`.
2. If no start works, return `-1`.

### Dry Run — Example 1: `gas=[1,2,3,4,5], cost=[3,4,5,1,2]`

| Start | Step 0 (diff) | Step 1 (diff) | Step 2 (diff) | Step 3 (diff) | Step 4 (diff) | Result |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 0 | -2 → tank=-2 ❌ | — | — | — | — | Fail |
| 1 | -2 → tank=-2 ❌ | — | — | — | — | Fail |
| 2 | -2 → tank=-2 ❌ | — | — | — | — | Fail |
| 3 | +3 → tank=3 | +3 → tank=6 | -2 → tank=4 | -2 → tank=2 | -2 → tank=0 | ✅ **Return 3** |

### Code

```kotlin
fun canCompleteCircuitBruteForce(gas: IntArray, cost: IntArray): Int {
    val n = gas.size
    for (start in 0 until n) {
        var tank = 0
        var canComplete = true
        for (i in 0 until n) {
            val idx = (start + i) % n
            tank += gas[idx] - cost[idx]
            if (tank < 0) { canComplete = false; break }
        }
        if (canComplete) return start
    }
    return -1
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | N starts × N steps each |
| **Space** | O(1) | Only a few variables |

---

## 🧩 Method 2: Greedy (Optimal)

### Core Idea

Two key insights:
1. **If total gas ≥ total cost**, a solution **must exist** (guaranteed unique).
2. **If we can't reach station `j` from start `i`**, then no station between `i` and `j` can be a valid start either — they'd have even less gas. So skip to `j+1`.

### Algorithm — Step by Step

1. **Initialize** `totalTank = 0`, `currentTank = 0`, `startStation = 0`.
2. **Iterate** through each station `i`:
   - `totalTank += gas[i] - cost[i]` (track overall surplus/deficit).
   - `currentTank += gas[i] - cost[i]` (track running tank from current start).
   - If `currentTank < 0`: reset `startStation = i + 1`, `currentTank = 0`.
3. **Return** `startStation` if `totalTank >= 0`, else `-1`.

### Dry Run — Example 1: `gas=[1,2,3,4,5], cost=[3,4,5,1,2]`

| Station `i` | `gas[i]` | `cost[i]` | `diff` | `currentTank` | `totalTank` | `startStation` | Notes |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|-------|
| 0 | 1 | 3 | -2 | -2 | -2 | 0→**1** | currentTank < 0 → reset start to 1 |
| 1 | 2 | 4 | -2 | -2 | -4 | 1→**2** | currentTank < 0 → reset start to 2 |
| 2 | 3 | 5 | -2 | -2 | -6 | 2→**3** | currentTank < 0 → reset start to 3 |
| 3 | 4 | 1 | +3 | +3 | -3 | 3 | currentTank ≥ 0 → keep going |
| 4 | 5 | 2 | +3 | +6 | 0 | 3 | currentTank ≥ 0 → keep going |

`totalTank = 0 ≥ 0` → Solution exists! Return `startStation = 3`.

✅ **Result: `3`**

### Dry Run — Example 2: `gas=[2,3,4], cost=[3,4,3]`

| Station `i` | `gas[i]` | `cost[i]` | `diff` | `currentTank` | `totalTank` | `startStation` | Notes |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|-------|
| 0 | 2 | 3 | -1 | -1 | -1 | 0→**1** | currentTank < 0 → reset |
| 1 | 3 | 4 | -1 | -1 | -2 | 1→**2** | currentTank < 0 → reset |
| 2 | 4 | 3 | +1 | +1 | -1 | 2 | currentTank ≥ 0 |

`totalTank = -1 < 0` → No solution exists.

❌ **Result: `-1`**

### Code

```kotlin
fun canCompleteCircuitGreedy(gas: IntArray, cost: IntArray): Int {
    var totalTank = 0
    var currentTank = 0
    var startStation = 0

    for (i in gas.indices) {
        totalTank += gas[i] - cost[i]
        currentTank += gas[i] - cost[i]
        if (currentTank < 0) {
            startStation = i + 1
            currentTank = 0
        }
    }

    return if (totalTank >= 0) startStation else -1
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass through the array |
| **Space** | O(1) | Only three variables |

### Why It Works

- **Insight 1:** If total gas ≥ total cost, the circuit is completable from some station. The gas surplus from some stations compensates for the deficit at others.
- **Insight 2:** If we start at station `i` and run out of gas at station `j`, then starting from any station between `i` and `j` would also fail (they'd have even less gas by the time they reach `j`). So we can safely skip all of them and try `j+1`.
- This eliminates the need to re-check stations, giving us O(N) instead of O(N²).

---

## 📊 Comparison Table

| Aspect | Brute Force | Greedy |
|--------|-------------|--------|
| **Time Complexity** | O(N²) | O(N) |
| **Space Complexity** | O(1) | O(1) |
| **Approach** | Try every start, simulate circuit | Track total + current tank, skip invalid starts |
| **Optimality** | ❌ TLE on large inputs | ✅ Optimal |
| **Ease of understanding** | Easy | Medium (requires insight) |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Total gas ≥ total cost = solution exists:** This is the first check — if total gas is less than total cost, return `-1` immediately.
2. **Skip invalid starts:** If we can't reach station `j` from start `i`, all stations between `i` and `j` are also invalid. Skip to `j+1`.
3. **Single pass:** The greedy approach completes in one pass — no need to simulate from every station.
4. **Unique solution:** The problem guarantees uniqueness, so we don't need to handle multiple valid starts.
5. **Circular array:** Use `(start + i) % n` for circular indexing in the brute force approach.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Gas Station | [#134](https://leetcode.com/problems/gas-station/) | Medium |
| Gas Station II | (LintCode) | Medium |
| Minimum Number of Refueling Stops | [#871](https://leetcode.com/problems/minimum-number-of-refueling-stops/) | Hard |
