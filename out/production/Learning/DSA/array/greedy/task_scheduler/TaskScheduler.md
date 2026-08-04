# Task Scheduler — Detailed Explanation

> **LeetCode #621** | [Problem Link](https://leetcode.com/problems/task-scheduler/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)  
> **Topic:** Greedy, Heap, Math

---

## 📋 Problem Statement

Given tasks and cooldown n, return the least number of units of time to finish all tasks.

### Examples

| tasks | n | Output | Explanation |
|-------|:-:|:------:|-------------|
| `["A","A","A","B","B","B"]` | 2 | 8 | A→B→idle→A→B→idle→A→B |
| `["A","A","A","B","B","B"]` | 0 | 6 | No cooldown |
| `["A","A","A","A","A","A","B","C","D","E","F","G"]` | 2 | 16 | A frame + fillers |

---

## 🧩 Method 1: Simulation — O(N × time)

### Core Idea

Use a max-heap of frequencies. Each cycle of n+1: pick most frequent tasks, decrement, put back after cooldown.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × result) | Simulate each time unit |
| **Space** | O(26) | Frequencies |

---

## 🧩 Method 2: Formula — O(N)

### Core Idea

The most frequent task determines the framework. Formula: `max(total, (maxFreq-1)*(n+1) + countOfMaxFreq)`.

### Key Insight

> The most frequent task creates a "frame": `A _ _ A _ _ A` (for n=2, freq=3). Other tasks fill the gaps. If they overflow the frame, no idle needed → answer is just total tasks.

### Visual — `["A","A","A","B","B","B"], n=2`

```
A B _ | A B _ | A B
  slot1   slot2   slot3
```

- maxFreq = 3 (A and B both appear 3 times)
- countOfMaxFreq = 2
- Frame = (3-1) × (2+1) + 2 = 2×3 + 2 = 8
- total = 6
- Answer = max(6, 8) = **8** ✅

### Code

```kotlin
fun leastInterval(tasks: CharArray, n: Int): Int {
    val freq = IntArray(26)
    for (task in tasks) freq[task - 'A']++
    val maxFreq = freq.max()
    val countOfMaxFreq = freq.count { it == maxFreq }
    return maxOf(tasks.size, (maxFreq - 1) * (n + 1) + countOfMaxFreq)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Count frequencies |
| **Space** | O(1) | 26 letters |

---

## 📊 Comparison Table

| Aspect | Simulation | Formula |
|--------|-----------|---------|
| **Time** | O(N × result) | O(N) |
| **Space** | O(26) | O(1) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Most frequent task frames the schedule:** `(maxFreq-1)` gaps of size `n`.
2. **countOfMaxFreq:** Multiple tasks with max freq share the last slot.
3. **max(total, frame):** If enough tasks to fill all gaps → no idle → answer = total.
4. **Pattern:** Greedy scheduling — extends to Rearrange String, Reorganize String.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Task Scheduler | [#621](https://leetcode.com/problems/task-scheduler/) | Medium |
| Reorganize String | [#767](https://leetcode.com/problems/reorganize-string/) | Medium |
| Rearrange String k Apart | [#358](https://leetcode.com/problems/rearrange-string-k-distance-apart/) | Hard |
| CPU Cache | [#460](https://leetcode.com/problems/lfu-cache/) | Hard |
