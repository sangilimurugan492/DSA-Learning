# Car Fleet — Detailed Explanation

> **LeetCode #853** | [Problem Link](https://leetcode.com/problems/car-fleet/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Top 10 hardest stack)  
> **Topic:** Stack, Sorting, Greedy

---

## 📋 Problem Statement

Given target position and cars' position/speed, return number of car fleets. A fleet forms when a faster car catches a slower car before target.

### Example

`target=12, position=[10,8,0,5,3], speed=[2,4,1,1,3]` → 3

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Sort by position. If car behind reaches target ≤ car ahead, they merge.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Check merges for each car |
| **Space** | O(N) | Times + merged array |

---

## 🧩 Method 2: Monotonic Stack — O(N log N)

### Core Idea

Sort by position DESC. Calculate time to target. If current time ≤ top of stack → merges. If > top → new fleet.

### Key Insight

> Time to target = (target - position) / speed. A car behind that takes ≤ time as the car ahead will catch up and merge. Stack stores arrival times in increasing order.

### Dry Run — `target=12, position=[10,8,0,5,3], speed=[2,4,1,1,3]`

Sorted by position DESC: `[(10,2), (8,4), (5,1), (3,3), (0,1)]`

| Car | pos | spd | time | Stack | Action |
|:---:|:---:|:---:|:----:|:-----:|:------:|
| 1 | 10 | 2 | 1.0 | [1.0] | push (new fleet) |
| 2 | 8 | 4 | 1.0 | [1.0] | 1.0 ≤ 1.0 → merge |
| 3 | 5 | 1 | 7.0 | [1.0, 7.0] | 7.0 > 1.0 → push (new fleet) |
| 4 | 3 | 3 | 3.0 | [1.0, 7.0] | 3.0 ≤ 7.0 → merge |
| 5 | 0 | 1 | 12.0 | [1.0, 7.0, 12.0] | 12.0 > 7.0 → push (new fleet) |

✅ **Result: 3 fleets**

### Code

```kotlin
fun carFleetStack(target: Int, position: IntArray, speed: IntArray): Int {
    val cars = position.indices.map { Pair(position[it], speed[it]) }.sortedByDescending { it.first }
    val stack = ArrayDeque<Double>()
    for ((pos, spd) in cars) {
        val time = (target - pos).toDouble() / spd
        if (stack.isEmpty() || time > stack.last()) stack.addLast(time)
    }
    return stack.size
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sort |
| **Space** | O(N) | Stack |

---

## 📊 Comparison Table

| Aspect | Brute Force | Monotonic Stack |
|--------|-------------|-----------------|
| **Time** | O(N²) | O(N log N) |
| **Space** | O(N) | O(N) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Time to target:** `(target - position) / speed` — the key metric.
2. **Sort by position DESC:** Process cars closest to target first.
3. **Merge condition:** If car behind takes ≤ time → it catches up → merges.
4. **Stack stores fleet times:** Increasing order. New time > top → new fleet.
5. **Pattern:** Monotonic stack — extends to Daily Temperatures, Next Greater Element.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Car Fleet | [#853](https://leetcode.com/problems/car-fleet/) | Medium |
| Car Fleet II | [#1776](https://leetcode.com/problems/car-fleet-ii/) | Hard |
| Daily Temperatures | [#739](https://leetcode.com/problems/daily-temperatures/) | Medium |
| Next Greater Element | [#496](https://leetcode.com/problems/next-greater-element-i/) | Easy |
